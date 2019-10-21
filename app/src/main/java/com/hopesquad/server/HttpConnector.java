package com.hopesquad.server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.hopesquad.server.content.FileBody;
import com.hopesquad.server.content.StringBody;
import com.hopesquad.server.mime.HttpMultipartMode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressLint("DefaultLocale")
public class HttpConnector {
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    private static final String ACCEPT = "application/json, api_version=1";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String USER_AGENT = "Xplorer-android";
    private static final int TYPE_BLUETOOTH = 3;
    protected static int i = 1;
    private static int TYPE_ETHERNET = 4;
    private static int TYPE_VPN = 5;
    private Context context;
    private HttpResponseListener listener;
    private Dialog dialog = null;
    private HttpClient httpClient = null;
    private HttpResponse httpResponse = null;
    private String accessToken = null;
    private Runnable runnable = null;
    private String error;
    private View loaderView;


    public HttpConnector(Context context, HttpResponseListener listener,
                         Dialog dialog) {
        // set various timeouts by ramanands@viscus.com
        HttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, 30000);
        // timeout until a connection is established
        HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
        // timeout for waiting for data
        HttpConnectionParams.setSoTimeout(httpParams, 60000);
        HttpHost proxy = getProxy(context);
        httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        this.context = context;
        this.listener = listener;
        this.dialog = dialog;
        this.httpClient = new DefaultHttpClient(httpParams);

        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        Log.d("Helper", "getConnectivityStatus status: "
                + (null == activeNetwork ? "Unknown or not connected"
                : activeNetwork.getState()));
        if (null != activeNetwork && activeNetwork.isConnected()) {
            Log.d("NetworkChangeReceiver", "getConnectivityStatus type: "
                    + activeNetwork.getType());
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_BLUETOOTH) {
                return TYPE_BLUETOOTH;
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return TYPE_ETHERNET;
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                return TYPE_VPN;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public HttpHost getProxy(Context context) {
        HttpHost proxy = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null)
                proxy = new HttpHost(proxyHost, port);
        }

        return proxy;
    }

    public void setHttpResponseListener(HttpResponseListener listener) {
        this.listener = listener;

    }

    public void setLoader(View v) {
        this.loaderView = v;
    }


    public void executeAsync(final String remainUrl, final int reqCode,
                             final String method, final boolean runInBg, final String jsonData,
                             final ArrayList<BasicNameValuePair> extraFormData, int urlType,
                             final boolean isAddHeader) {
        if (null == context) {
            return;
        }

        logFullResponse("executeAsync - url=" + remainUrl + ", reqCode="
                + reqCode + ", method=" + method + ", runInBg=" + runInBg
                + ", jsonData=" + jsonData + ", extraFormData=" + extraFormData
                + ":", "Request");

        if (TYPE_NOT_CONNECTED == getConnectivityStatus(context)) {


            return;
        }

        String url = "";

        switch (urlType) {
            case HTTPRequest.URLTYPE_SERVICE:
                url = HTTPRequest.SERVICE_URL + remainUrl;
                break;

            case HTTPRequest.URLTYPE_IMAGE:
                url = HTTPRequest.IMAGE_URL + remainUrl;

                break;

            case HTTPRequest.URLTYPE_UPLOAD:
                url = HTTPRequest.UPLOAD_URL + remainUrl;

                break;
            case HTTPRequest.URLTYPE_EXTERNAL:
                url = remainUrl;
                break;
            default:
                url = HTTPRequest.SERVICE_URL + remainUrl;
                break;
        }

        displayLoader(runInBg, true);

        final String finalUrl = url.replace(" ", "%20");

        runnable = new Runnable() {
            private JSONObject mJSObject;

            @Override
            public void run() {
                error = null;
                try {
                    Log.i("INFO", "Line :546");
                    // Request body
                    if (method.toLowerCase().contains("get")) {

                        HttpGet httpGet = new HttpGet(finalUrl);
                        httpGet.addHeader("Accept", ACCEPT);
                        httpGet.addHeader("Content-Type", CONTENT_TYPE);
                        httpGet.addHeader("User-Agent", USER_AGENT);

                        httpResponse = httpClient.execute(httpGet);
                    } else if (method.toLowerCase().equalsIgnoreCase("post")) {
                        HttpPost httpPost = new HttpPost(finalUrl);
                        if (jsonData != null) {
                            httpPost.setEntity(new StringEntity(jsonData));
                        } else if (extraFormData != null) {
                            httpPost.setEntity(new UrlEncodedFormEntity(extraFormData));
                        }

                        httpResponse = httpClient.execute(httpPost);
                    } else if (method.toLowerCase().equalsIgnoreCase("multipost")) {
                        HttpPost httpPost = new HttpPost(finalUrl);
                        // Used for posting an image to AWS
                        httpPost.addHeader("Content-Type", "multipart/form-data");
                        if (isAddHeader) {
                            //TODO
                        }
                        if (extraFormData != null) {
                            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                            for (int i = 0; i < extraFormData.size(); i++) {
                                BasicNameValuePair pair = extraFormData.get(i);
                                if (pair.getName().startsWith("files")) {
                                    String path = pair.getValue();
                                    int lastSlash = path.lastIndexOf("/");
                                    String filename = path.substring(lastSlash + 1);

                                    FileBody fb = new FileBody(new File(pair.getValue()), "multipart/form-data", filename);
                                    entity.addPart(pair.getName(), fb);
                                } else
                                    entity.addPart(pair.getName(), new StringBody(pair.getValue()));
                            }
                            httpPost.setEntity(entity);
                        } else {
                            // Don't add headers for multi-form post or else it
                            // breaks!
                            httpPost.addHeader("Accept", ACCEPT);
                            httpPost.addHeader("Content-Type", CONTENT_TYPE);
                            httpPost.addHeader("User-Agent", USER_AGENT);
                        }

                        if (jsonData != null) {
                            httpPost.setEntity(new StringEntity(jsonData));
                        }

                        httpResponse = httpClient.execute(httpPost);
                    }
                    Log.i("INFO", "Line :594");//ed07431569cab3ca604e0862d78015ba
                    // Response
                    HttpEntity entity = httpResponse.getEntity();
                    StatusLine sl = httpResponse.getStatusLine();
                    final int statusCode = sl.getStatusCode();
                    InputStream contentStream = null;
                    try {
                        contentStream = entity.getContent();
                    } catch (Exception e) {
                    }
                    // Read Response
                    try {
                        String tempresult = readStreamFully(contentStream);
                        Log.i("INFO", "Line :607");
                        logFullResponse(tempresult, "Response");
                        tempresult = tempresult.replace("\\n", "").replace("\\r", "").replace("\"[", "[").replace("]\"", "]").replace("\\", "").trim();
                        final String response = tempresult;
                        if (response != null) {

                            // mJSObject = new JSONObject(response);
//                            if (statusCode == 403) {
////                                displayLoader(runInBg, false);
//                                Intent intent = new Intent(context,
//                                        LogoutBroadcastReceiver.class);
//                                intent.putExtra("logout", true);
//                                intent.putExtra("reason",
//                                        "May be logged in with other device");
//                                context.sendBroadcast(intent);
//                                return;
//                            }
                        }
                        if (null != listener) {

                            ((Activity) context).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    displayLoader(runInBg, false);

                                    listener.onResponse(reqCode, statusCode,
                                            response);
                                }
                            });
                        }
                        Log.i("INFO", "Line :640");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("INFO", "Line :643");
                        error = e.getLocalizedMessage();
                    }

                } catch (UnknownHostException e) {
                    // DialogUtil.showOkDialog(context,
                    // "Unable to connect the server");
                    error = e.getLocalizedMessage();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    error = e.getLocalizedMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    error = e.getLocalizedMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    error = e.getLocalizedMessage();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayLoader(runInBg, false);
                        if (null != error && null != listener) {
                            listener.onResponse(reqCode, 0, error);
                        }
                    }
                });
            }
        };

        new Thread(runnable).start();
    }

    private String readStreamFully(InputStream inputStream) {
        if (inputStream == null) {
            System.out.println("inputStream null, returning blank");
            return "";
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String strCurrentLine = null;
        try {
            /* Read until all response is read */
            while ((strCurrentLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(strCurrentLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public void logFullResponse(String response, String tag) {
        final int chunkSize = 4000;
        if (null != response && response.length() > chunkSize) {
            int chunks = (int) Math.ceil((double) response.length()
                    / (double) chunkSize);
            for (int i = 1; i <= chunks; i++) {
                if (i != chunks) {
                    Log.i(tag,
                            "logResponse - response="
                                    + response.substring((i - 1) * chunkSize, i
                                    * chunkSize));
                } else {
                    Log.i(tag,
                            "logResponse - response="
                                    + response.substring((i - 1) * chunkSize,
                                    response.length()));
                }
            }
        } else {
            Log.i(tag, "logResponse - response=" + response);
        }
    }

    public void displayLoader(boolean runInBg, final boolean show) {
        if (runInBg || null == context) {
            return;
        }
        if (dialog != null) {
            Activity activity = ((Activity) context);
            if (activity.isFinishing()) {
                return;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != loaderView) {
                        loaderView.setVisibility(show ? View.VISIBLE
                                : View.GONE);
                    } else if (null != dialog) {
                        if (show && !dialog.isShowing()) {
                            dialog.show();
                        } else if (!show) {
                            dialog.dismiss();
                        }
                    }
                }
            });
        }
    }

    public interface HttpResponseListener {
        void onResponse(int reqCode, int statusCode, String json);

        void onCancel(boolean canceled);
    }

}
