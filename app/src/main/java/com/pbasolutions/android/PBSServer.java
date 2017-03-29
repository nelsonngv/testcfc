package com.pbasolutions.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pbasolutions.android.json.PBSJson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by pbadell on 8/21/15.
 */
public class PBSServer {
    /**
     * Class name tag.
     */
    private static final String TAG = "PBSServer";

    /**
     * This method for sending request to server and returning json result object.
     * @param url url of server to send request.
     * @param classname
     * @return
     */
    protected PBSJson callServer(final String url, final String classname) {
        try {
//            // HttpClient.set
//            HttpGet httpGet = new HttpGet(url);
//            // Create local HTTP context
//            HttpContext localContext = new BasicHttpContext();
//            // Bind custom cookie store to the local context
//            localContext.setAttribute(ClientContext.COOKIE_STORE, PBSServerConst.cookieStore);
//            String responseString = "";
//            Class cls = Class.forName(classname);
//            final HttpParams httpParams = new BasicHttpParams();
//            //set time out to 30 seconds. any attempt to request on server >30 sec will timed out.
//            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
//            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
//            HttpResponse response = httpClient.execute(httpGet, localContext);
//            responseString = EntityUtils.toString(response.getEntity());
//            return (PBSJson) new Gson().fromJson(responseString, cls);

            PBSHttpsTrustManager.allowAllSSL();
            Class cls = Class.forName(classname);
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringReq = new StringRequest(Request.Method.POST, url, future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(ClientContext.COOKIE_STORE, PBSServerConst.cookieStore.toString());
                    return headers;
                }
            };
            stringReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding string request to request queue
            AppSingleton appSingleton = AppSingleton.getInstance(PandoraMain.instance.getApplicationContext());
            appSingleton.getRequestQueue().add(stringReq);
            try {
                String response = future.get(30, TimeUnit.SECONDS); // Block thread, waiting for response, timeout after 30 seconds
                return (PBSJson) new Gson().fromJson(response.toString(), cls);
            } catch (InterruptedException e) {
                // Continue waiting for response (unless you specifically intend to use the interrupt to cancel your request)
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            } catch (TimeoutException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        return null;
    }

    /**
     * This method for sending request to server and returning json result object.
     * @param url url of server to send request.
     * @return
     */
    protected String postServer(final String url, final String json) {
        try {
//            HttpPost httpPost= new HttpPost(url);
//            HttpContext localContext = new BasicHttpContext();
//            localContext.setAttribute(ClientContext.COOKIE_STORE, PBSServerConst.cookieStore);
//
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//            nameValuePairs.add(new BasicNameValuePair("json", json));
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//            final HttpParams httpParams = new BasicHttpParams();
//            //set time out to 30 seconds. any attempt to request on server >30 sec will timed out.
//            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
//            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
//            HttpResponse response = httpClient.execute(httpPost, localContext);
//            return EntityUtils.toString(response.getEntity());

            PBSHttpsTrustManager.allowAllSSL();
            RequestFuture<String> future = PandoraMain.instance.future;
            StringRequest stringReq = new StringRequest(Request.Method.POST, url, future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(ClientContext.COOKIE_STORE, PBSServerConst.cookieStore.toString());
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("json", json);
                    return params;
                }
            };
            stringReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding string request to request queue
            AppSingleton appSingleton = AppSingleton.getInstance(PandoraMain.instance.getApplicationContext());
            appSingleton.getRequestQueue().add(stringReq);
            try {
                String response = future.get(30, TimeUnit.SECONDS); // Block thread, waiting for response, timeout after 30 seconds
                return response.toString();
            } catch (InterruptedException e) {
                // Continue waiting for response (unless you specifically intend to use the interrupt to cancel your request)
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            } catch (TimeoutException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return main url path without action and variable parameters.
     * @param url
     * @param path
     * @param jsp
     * @return
     */
    protected String getURL(String url, String path, String jsp){
        return url + path + jsp;
    }

}
