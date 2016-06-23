package com.spinclass.net.gson;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GsonRequest<T> extends AbsGsonRequest<T> {

	public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, clazz, listener, errorListener);
	}

	public GsonRequest(int method, String url, JsonObject requestBody, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, requestBody, clazz, listener, errorListener);
	}

	@Override
	protected Gson buildGson() {
		return GsonFactory.createGson();
	}

}
