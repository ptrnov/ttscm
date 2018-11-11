package com.cudocomm.troubleticket.util;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.cache.CacheConfig;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class ApiClient {

    protected static Context applicationContext;
    public static List<Cookie> c;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context ac) {
        applicationContext = ac;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setResponseTimeout(75000);
        client.setConnectTimeout(10000);
        client.get(CommonsUtil.getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setResponseTimeout(75000);
        client.setConnectTimeout(10000);
        client.setMaxRetriesAndTimeout(1, CacheConfig.DEFAULT_MAX_CACHE_ENTRIES);
        client.post(CommonsUtil.getAbsoluteUrl(url), params, responseHandler);
    }

    public static void cancel() {
        Log.i(Constants.LOG_APP, "Canceling all request............");
        client.cancelAllRequests(true);
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, RequestBody body) throws IOException {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().post(body).url(url).build();
//        String result = "";
        try {
            return client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error, Can't reach server / No Internet Connection";
        }
    }

    public static String post2(String url, MultipartBody.Builder builder) throws IOException {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();

        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        Log.d("REQbuilder", buffer.readUtf8());
        try {
            return client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error, Can't reach server / No Internet Connection";
        }
    }

    public String post(String url) throws IOException {
        return new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute().body().string();
    }

    public static JSONObject uploadImages(List<File> files) {
        try {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if(files.size() > 0) {
                for(File file : files) {
                    builder.addFormDataPart("tag", "filename", RequestBody.create(MEDIA_TYPE_PNG, file));
                }
            }

            RequestBody req = builder.build();

            Request request = new Request.Builder()
                    .url("url")
                    .post(req)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            Log.d("response", "uploadImage:"+response.body().string());

            return new JSONObject(response.body().string());

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Logcat.e("Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Logcat.e("Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }

}