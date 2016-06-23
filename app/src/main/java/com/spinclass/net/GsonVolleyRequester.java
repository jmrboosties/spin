package com.spinclass.net;

import android.os.Bundle;
import com.android.volley.*;
import com.google.gson.JsonObject;
import com.spinclass.constant.Constants;
import com.spinclass.net.gson.GsonRequest;

import java.util.HashMap;
import java.util.Map;

public class GsonVolleyRequester<T> extends AbsVolleyRequester {

	protected Class<T> mClass;

	public GsonVolleyRequester(VolleyContext context, Class<T> theClass) {
		super(context);
		mClass = theClass;
	}

	@Override
	protected Request buildRequest(VolleyContext volleyContext, int requestType, final String url, JsonObject requestBody, final VolleyRequestListener listener,
								   int timeout, boolean handleResponse, Bundle bundle) {

		GsonRequest<T> request = new GsonRequest<T>(requestType, url, requestBody, mClass, new Response.Listener<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(T response) {
				listener.onResponse(response);
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				onVolleyError(error, url, listener);
			}

		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> map = new HashMap<>();

				addHeaders(map);

				return map;
			}

		};

		if(timeout == 0)
			timeout = Constants.DEFAULT_TIMEOUT_MILLIS;

		request.setRetryPolicy(new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		request.setTag(volleyContext.getRequestFilter());

		return request;
	}

	protected void addHeaders(HashMap<String, String> map) { }

}
