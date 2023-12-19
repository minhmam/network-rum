package com.example.networkdemoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Make some simple HTTP Requests when it is clicked on specific Button.
 * HttpURLConnection and Apache HttpClient is used for demonstration.
 */
public class HttpRequestsActivity extends Activity {
    final String TAG = HttpRequestsActivity.class.getSimpleName();

    private TextView tvDisplayOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        setContentView(R.layout.httprequests);

        tvDisplayOutput = (TextView) findViewById(R.id.display_output);
    }

    /**
     * Retrieves a Website via Apache HttpClient and displays it as String.
     *
     * @param view
     */
    @SuppressLint("StaticFieldLeak")
    public void onClickGetHttpClient (View view) {
        if (!isOnline()) {
            tvDisplayOutput.setText(R.string.no_connection);
            return;
        }

        String url = "http://www.mi.hs-rm.de/~pbart001/wsgi/highscore.wsgi/";
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                InputStream is = null;

                try {
                    HttpParams httpParameters = new BasicHttpParams();
                    // Default == 0
                    HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
                    HttpConnectionParams.setSoTimeout(httpParameters, 15000);

                    HttpClient httpClient = new DefaultHttpClient(httpParameters);
                    HttpGet httpGet = new HttpGet(strings[0]);
                    
                    // make request
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    int response = httpResponse.getStatusLine().getStatusCode();

                    if (response == HttpStatus.SC_OK) {
                        // receive as InputStream
                        is = httpResponse.getEntity().getContent();

                        // Convert InputStream into a string
                        String contentAsString = convertInputStreamToString(is);
                        return contentAsString;
                    } else {
                        return getResources().getString(R.string.unable_to_fetch_data);
                    }
                } catch (IOException e) {
                    return getResources().getString(R.string.unable_to_fetch_data);
                } finally {
                    // close input stream
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }

            // display result of Task
            @Override
            protected void onPostExecute(String s) {
                tvDisplayOutput.setText(s);
            }
        }.execute(url);
    }

    /**
     * Retrieves a Website via HttpURLConnection and displays it as String.
     *
     * @param view
     */
    public void onClickGetHttpURLConnection(View view) {
        if (!isOnline()) {
            tvDisplayOutput.setText(R.string.no_connection);
            return;
        }


        String url = "https://64e8747599cf45b15fdf960f.mockapi.io/api/vi/app/app";
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                InputStream is = null;
                HttpURLConnection conn = null;

                try {
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    // Default == 0
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");

                    // make request
                    conn.connect();
                    int response = conn.getResponseCode();

                    if (response == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();

                        // Convert InputStream into a string
                        String contentAsString = convertInputStreamToString(is);
                        return contentAsString;
                    } else {
                        return getResources().getString(R.string.unable_to_fetch_data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return getResources().getString(R.string.unable_to_fetch_data);
                } finally {
                    // close input stream
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

            // display result of Task
            @Override
            protected void onPostExecute(String s) {
                tvDisplayOutput.setText(s);
            }
        }.execute(url);
    }

    /**
     * Tries to retrieve a Website via HttpURLConnection, but there is a Timeout.
     *
     * @param view
     */
    public void onClickGetTimeout(View view) {
        if (!isOnline()) {
            tvDisplayOutput.setText(R.string.no_connection);
            return;
        }


//        String url = "http://jonastheis.de/timeout.php";
//        String url = "https://64e8747599cf45b15fdf960f.mockapi.io/api/vi/app/app";
        String url = "https://minhmam.free.beeceptor.com/";
        new AsyncTask<String, Void, String>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected String doInBackground(String... strings) {
                InputStream is = null;
                HttpURLConnection conn = null;

                try {
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    // Default == 0
                    conn.setReadTimeout(10000); // throws SocketTimeoutException
                    conn.setConnectTimeout(15000); // throws SocketTimeoutException
                    conn.setRequestMethod("GET");

                    // make request
                    conn.connect();
                    int response = conn.getResponseCode();

                    if (response == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();

                        // Convert InputStream into a string
                        String contentAsString = convertInputStreamToString(is);
                        return contentAsString;
                    } else {
                        return getResources().getString(R.string.unable_to_fetch_data);
                    }
                } catch (SocketTimeoutException e1) {
                    return getResources().getString(R.string.timeout);
                } catch (IOException e) {
                    return getResources().getString(R.string.unable_to_fetch_data);
                } finally {
                    // close input stream
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

            // display result of Task
            @Override
            protected void onPostExecute(String s) {
                tvDisplayOutput.setText(s);
            }
        }.execute(url);
    }

    /**
     * Sends a Post Request via HttpURLConnection.
     *
     * @param view
     */
    @SuppressLint("StaticFieldLeak")
    public void onClickPost(View view) {
        if (!isOnline()) {
            tvDisplayOutput.setText(R.string.no_connection);
            return;
        }


        String url = "http://www.mi.hs-rm.de/~jthei001/wsgi/blatt2/aufg3/blog.wsgi/blog/Kafka";
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                InputStream is = null;
                HttpURLConnection conn = null;

                try {
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    // Default == 0
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    //String param="comment_text=" + URLEncoder.encode("Fancy Android!", "UTF-8");
                    List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
                    params.add(new Pair<String, String>("comment_text", "Fancy Android!"));
                    String paramsString = createQuery(params);

                    // set length of data, but use not header (conn.setRequestProperty(“Content-Length”, length))
                    // not sure about length to send? -> setChunkedStreamingMode(int)
                    conn.setFixedLengthStreamingMode(paramsString.getBytes().length);

                    PrintWriter out = new PrintWriter(conn.getOutputStream());
                    out.print(paramsString);
                    out.close();

                    // make request
                    conn.connect();
                    int response = conn.getResponseCode();

                    if (response == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();

                        // Convert InputStream into a string
                        String contentAsString = convertInputStreamToString(is);
                        return contentAsString;
                    } else {
                        return getResources().getString(R.string.unable_to_fetch_data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return getResources().getString(R.string.unable_to_fetch_data);
                } finally {
                    // close input stream
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

            // display result of Task
            @Override
            protected void onPostExecute(String s) {
                tvDisplayOutput.setText(s);
            }
        }.execute(url);
    }



    /**
     * Converts a List of Pairs (key, value) to String for POST Request.
     *
     * @param params List of Pairs (key, value) which should be sent
     * @return String of key, value Pairs for request
     * @throws UnsupportedEncodingException
     */
    private String createQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode((String) pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.second, "UTF-8"));
        }

        return result.toString();
    }

    /**
     * Converts an InputStream into a String.
     *
     * @param inputStream
     * @return String of InputStream
     * @throws IOException
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        return result;
    }

    /**
     * Checks whether a Network interface is available and a connection is possible.
     * @return boolean
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
