package com.spinclass.net;

import android.os.Bundle;
import com.android.volley.*;
import com.google.gson.JsonObject;
import com.spinclass.constant.Constants;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectVolleyRequester extends AbsVolleyRequester {

	public JsonObjectVolleyRequester(VolleyContext context) {
		super(context);
	}

	@Override
	protected Request buildRequest(VolleyContext activity, int requestType, final String url, JsonObject requestBody, final VolleyRequestListener listener,
								   int timeout, final boolean handleResponse, Bundle extras) {

		JsonRequest jsonRequest = new JsonRequest(requestType, url, requestBody, new Response.Listener<JsonObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(JsonObject response) {
				if(handleResponse)
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

		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonRequest.setTag(mVolleyContext.getRequestFilter());

		return jsonRequest;
	}

	protected void addHeaders(HashMap<String, String> map) { }

}
