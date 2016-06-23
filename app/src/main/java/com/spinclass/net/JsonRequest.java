package com.spinclass.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;

public class JsonRequest extends com.android.volley.toolbox.JsonRequest<JsonObject> {

	public JsonRequest(int method, String url, JsonObject requestBody, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
		super(method, url, requestBody != null ? requestBody.toString() : null, listener, errorListener);
	}

	public JsonRequest(int method, String url, String requestBody, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
		super(method, url, requestBody, listener, errorListener);
	}

	@Override
	protected Response<JsonObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

			return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
		} catch(UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

}
