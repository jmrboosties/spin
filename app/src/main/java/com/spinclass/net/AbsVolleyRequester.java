package com.spinclass.net;

import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.spinclass.constant.Constants;
import com.spinclass.util.Print;

public abstract class AbsVolleyRequester {

	protected VolleyContext mVolleyContext;

	public AbsVolleyRequester(VolleyContext context) {
		mVolleyContext = context;
	}

	public void makeGetRequest(String url, final VolleyRequestListener listener) {
		makeGetRequest(url, listener, android.os.Bundle.EMPTY);
	}

	public void makeGetRequest(String url, final VolleyRequestListener listener, Bundle bundle) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.GET, url, null, listener, Constants.DEFAULT_TIMEOUT_MILLIS, true, bundle);
		queue.add(request);
	}

	public void makeGetRequest(String url, final VolleyRequestListener listener, boolean handleResponse, Bundle bundle) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.GET, url, null, listener, Constants.DEFAULT_TIMEOUT_MILLIS, handleResponse, bundle);
		queue.add(request);
	}

	public void makeLargeGetRequest(String url, final VolleyRequestListener listener) {
		makeLargeGetRequest(url, listener, Bundle.EMPTY);
	}

	public void makeLargeGetRequest(String url, final VolleyRequestListener listener, Bundle bundle) {
		RequestQueue queue = Volley.newRequestQueue(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.GET, url, null, listener, 10000, true, bundle);
		queue.add(request);
	}

	public void makePostRequest(String url, JsonObject requestBody, VolleyRequestListener listener) {
		makePostRequest(url, requestBody, listener, true, Bundle.EMPTY);
	}

	public void makePostRequest(String url, JsonObject requestBody, VolleyRequestListener listener, boolean handleResponse, Bundle bundle) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.POST, url, requestBody, listener, Constants.DEFAULT_TIMEOUT_MILLIS, handleResponse, bundle);
		queue.add(request);
	}

	public void makeLargePostRequest(String url, JsonObject requestBody, VolleyRequestListener listener) {
		makeLargePostRequest(url, requestBody, listener, true, Bundle.EMPTY);
	}

	public void makeLargePostRequest(String url, JsonObject requestBody, VolleyRequestListener listener, boolean handleResponse, Bundle bundle) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.POST, url, requestBody, listener, 10000, handleResponse, bundle);
		queue.add(request);
	}

	public void makePatchRequest(String url, JsonObject requestBody, VolleyRequestListener listener, boolean handleResponse) {
		makePatchRequest(url, requestBody, listener, handleResponse, Bundle.EMPTY);
	}

	public void makePatchRequest(String url, JsonObject requestBody, VolleyRequestListener listener, boolean handleResponse, Bundle extras) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.PATCH, url, requestBody, listener, Constants.DEFAULT_TIMEOUT_MILLIS, handleResponse, extras);
		queue.add(request);
	}

	public void makePutRequest(String url, JsonObject requestBody, VolleyRequestListener listener) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.PUT, url, requestBody, listener, Constants.DEFAULT_TIMEOUT_MILLIS, true, Bundle.EMPTY);
		queue.add(request);
	}

	public void makeLargePatchRequest(String url, JsonObject requestBody, VolleyRequestListener listener, boolean handleResponse) {
		makeLargePatchRequest(url, requestBody, listener, handleResponse, Bundle.EMPTY);
	}

	public void makeLargePatchRequest(String url, JsonObject requestBody, VolleyRequestListener listener, boolean handleResponse, Bundle extras) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.PATCH, url, requestBody, listener, 10000, handleResponse, extras);
		queue.add(request);
	}

	public void makeDeleteRequest(String url, final VolleyRequestListener listener) {
		makeDeleteRequest(url, listener, Bundle.EMPTY);
	}

	public void makeDeleteRequest(String url, final VolleyRequestListener listener, Bundle bundle) {
		MyRequestQueue queue = MyRequestQueue.getInstance(mVolleyContext.getContext().getApplicationContext());
		Request request = buildRequest(mVolleyContext, Request.Method.DELETE, url, null, listener, Constants.DEFAULT_TIMEOUT_MILLIS, true, bundle);
		queue.add(request);
	}

	protected abstract Request buildRequest(VolleyContext activity, int requestType, String url, JsonObject requestBody,
											VolleyRequestListener listener, int timeout, boolean handleResponse, Bundle extras);

	protected void onVolleyError(VolleyError error, String url, VolleyRequestListener listener) {
		try {
			if(error instanceof TimeoutError) {
				Print.log("request timed out", url);
				Print.exception(new Exception("time out on: " + url));
			}
			else if(error.networkResponse != null) {
				int statusCode = error.networkResponse.statusCode;
				String message = new String(error.networkResponse.data, "UTF-8");

				Print.exception(new Exception("url: " + url + "; status code: " + statusCode + "; message: " + message));
			}
			else
				Print.exception(error);
		} catch(Exception e) {
			Print.exception(e);
		}

		if(listener != null)
			listener.onErrorResponse(error);
	}

}
