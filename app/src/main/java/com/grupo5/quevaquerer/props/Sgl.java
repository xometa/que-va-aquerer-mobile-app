package com.grupo5.quevaquerer.props;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Sgl {
    private AsyncHttpClient client;
    private RequestParams params;
    private Context context;
    private CallbackResponse response;

    public Sgl(Context context, CallbackResponse response) {
        this.client = new AsyncHttpClient();
        this.params = new RequestParams();
        this.context = context;
        this.response = response;
    }

    public void get(String url, RequestParams params, String option) {
        callback("get", url, params, option);
    }

    public void post(String url, RequestParams params, String option) {
        callback("post", url, params, option);
    }

    public void delete(String url, RequestParams params, String option) {
        callback("delete", url, params, option);
    }

    private void callback(String action, String url, RequestParams data, final String option) {
        if (data != null) {
            params = data;
        } else {
            params = new RequestParams();
        }
        if (action == "post") {
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        response.success(new String(responseBody), option);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    response.fail(new String(responseBody));
                }
            });
        } else if (action == "get") {
            client.get(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        response.success(new String(responseBody), option);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    response.fail(new String(responseBody));
                }
            });
        } else {
            client.delete(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        response.success(new String(responseBody), option);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    response.fail(new String(responseBody));
                }
            });
        }
    }
}
