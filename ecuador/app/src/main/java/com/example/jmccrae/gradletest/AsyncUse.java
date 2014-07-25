package com.example.jmccrae.gradletest;

import com.loopj.android.http.*;

/**
 * Created by jmccrae on 7/1/13.
 */
public class AsyncUse {
    private String BASE_URL;

    public AsyncUse (String base_url){
        BASE_URL = base_url;
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
