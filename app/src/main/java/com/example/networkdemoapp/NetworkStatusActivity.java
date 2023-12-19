package com.example.networkdemoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;


/**
 * MainActivity of NetworkDemo.
 * checks connectivity and displays information about the first connected network interface.
 * Registers a BroadcastReceiver to track connection changes.
 */
public class NetworkStatusActivity extends Activity {
    final String TAG = NetworkStatusActivity.class.getSimpleName();
    private TableLayout tDetailsTable;
    private TextView tvNetworkStatus;
    private TextView tvType;
    private TextView tvSubtype;
    private TextView tvExtra;
    private TextView tvRoaming;
    private TextView tvReason;

    private NetworkReceiver receiver;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        setContentView(R.layout.networkstatus);

        // get views
        tDetailsTable = (TableLayout) findViewById(R.id.details_table);
        tvNetworkStatus = (TextView) findViewById(R.id.network_status);
        tvType = (TextView) findViewById(R.id.type);
        tvSubtype = (TextView) findViewById(R.id.subtype);
        tvExtra = (TextView) findViewById(R.id.extra);
        tvRoaming = (TextView) findViewById(R.id.roaming);
        tvReason = (TextView) findViewById(R.id.reason);


        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister Receiver
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private void setCurrentStatus() {
        if (isOnline()) {
            tvNetworkStatus.setText(R.string.connected);
            tvNetworkStatus.setBackgroundColor(0xFF8BC34A);

            tDetailsTable.setVisibility(View.VISIBLE);
            tvType.setText(networkInfo.getTypeName());
            tvSubtype.setText(networkInfo.getSubtypeName());
            tvExtra.setText(networkInfo.getExtraInfo());
            tvRoaming.setText("" + networkInfo.isRoaming());
            tvReason.setText(networkInfo.getReason());

            Log.v(TAG, networkInfo.toString());
        } else {
            tvNetworkStatus.setText(R.string.disconnected);
            tvNetworkStatus.setBackgroundColor(0xFFF44336);
            tvNetworkStatus.setTextColor(0xFFFFFFFF);

            tDetailsTable.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Checks whether a Network interface is available and a connection is possible.
     * @return boolean
     */
    private boolean isOnline() {
        // getActiveNetworkInfo() -> first connected network interface or null
        // getNetworkInfo(ConnectivityManager.TYPE_WIFI | TYPE_MOBILE) -> for wifi | mobile
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Go to other Activity where some Requests are shown.
     * @param view
     */
    public void onClickToHttp(View view) {
        Intent intent = new Intent (this, HttpRequestsActivity.class);
        startActivity(intent);
    }


    /**
     * NetworkReceiver receives updates for connection
     * changes (ConnectivityManager.CONNECTIVITY_ACTION) and updates view.
     */
    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setCurrentStatus(); // update GUI and networkInfo
        }
    }
}
