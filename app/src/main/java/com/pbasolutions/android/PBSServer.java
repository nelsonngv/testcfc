package com.pbasolutions.android;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by pbadell on 8/21/15.
 */
public class PBSServer {
    /**
     * Class name tag.
     */
    private static final String TAG = "PBSServer";
    private RequestQueue queue;

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
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }
            };
            int timeout = 30;
//            if (!PandoraMain.instance.getGlobalVariable().isFirstBatchSynced())
//                timeout = 180;
            stringReq.setRetryPolicy(new DefaultRetryPolicy(timeout * 1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if (queue == null) {
                // Adding string request to request queue
                queue = Volley.newRequestQueue(PandoraMain.instance.getBaseContext());
            }
            queue.add(stringReq);
            try {
                String response = future.get(timeout, TimeUnit.SECONDS); // Block thread, waiting for response, timeout after n seconds
                return (PBSJson) new Gson().fromJson(response.toString(), cls);
            } catch (InterruptedException e) {
                // Continue waiting for response (unless you specifically intend to use the interrupt to cancel your request)
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error: " + e.toString());
            } catch (TimeoutException e) {
                Log.e(TAG, "Error: " + e.toString());
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.toString());
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


//            //Here a logging interceptor is created
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//            //The logging interceptor will be added to the http client
//            OkHttpClient okhttp = getUnsafeOkHttpClient(logging, new JavaNetCookieJar(CookieHandler.getDefault()));
//
//            //The Retrofit builder will have the client attached, in order to get connection logs
//            Retrofit retrofit = new Retrofit.Builder()
//                    .client(okhttp)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .baseUrl(url.substring(0, url.lastIndexOf("?")) + "/")
//                    .build();
//            ApiCall service = retrofit.create(ApiCall.class);
//
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/x-www-form-urlencoded");
//            headers.put("Accept", "application/json");
//            Call<JsonObject> call = service.post(url, headers, json);
//
//            final BlockingQueue<JsonObject> blockingQueue = new ArrayBlockingQueue<>(1);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//                    JsonObject result = response.body();
//                    blockingQueue.offer(result);
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    // handle execution failures like no internet connectivity
//                    Log.d(TAG, "Failed");
//                }
//            });
//            String a = blockingQueue.poll(5, TimeUnit.SECONDS).toString();
//            return a;

            PBSHttpsTrustManager.allowAllSSL();
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringReq = new StringRequest(Request.Method.POST, url, future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    headers.put("Accept", "application/x-www-form-urlencoded");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("json", json);
                    return params;
                }
            };
            stringReq.setRetryPolicy(new DefaultRetryPolicy(30000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if (queue == null) {
                // Adding string request to request queue
                queue = Volley.newRequestQueue(PandoraMain.instance.getBaseContext());
            }
            queue.add(stringReq);
            try {
                String response = future.get(30, TimeUnit.SECONDS); // Block thread, waiting for response, timeout after n seconds
                return response.toString();
            } catch (InterruptedException e) {
                // Continue waiting for response (unless you specifically intend to use the interrupt to cancel your request)
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error: " + e.toString());
            } catch (TimeoutException e) {
                Log.e(TAG, "Error: " + e.toString());
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.toString());
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

//    public interface ApiCall {
//
//        //This method is used for "POST"
//        @FormUrlEncoded
//        @POST
//        Call<JsonObject> post(
//                @Url String url,
//                @HeaderMap Map<String, String> headers,
//                @Field("json") String json
//        );
//    }
}
