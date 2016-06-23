package com.spinclass.net.gson;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.spinclass.util.Print;

import java.io.UnsupportedEncodingException;

public abstract class AbsGsonRequest<T> extends JsonRequest<T> {

	protected final Gson mGson;
	protected final Class<T> mClazz;
	protected final Response.Listener<T> mListener;
	protected final String mUrl;

	public AbsGsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
		this(method, url, null, clazz, listener, errorListener);
	}

	public AbsGsonRequest(int method, String url, JsonObject requestBody, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, requestBody != null ? requestBody.toString() : null, listener, errorListener);
		this.mClazz = clazz;
		this.mListener = listener;
		mUrl = url;
		mGson = buildGson();
	}

	protected abstract Gson buildGson();

	@Override
	protected void deliverResponse(T response) {
		if(!isCanceled())
			mListener.onResponse(response);
	}

	@Override
	public void cancel() {
		super.cancel();
		Print.log("Request " + mUrl + " cancelled");
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(mGson.fromJson(json, mClazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			Print.exception(new Exception("json syntax error on url: " + mUrl));
			return Response.error(new ParseError(e));
		}
	}

}
