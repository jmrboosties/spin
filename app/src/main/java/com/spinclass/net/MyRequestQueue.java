package com.spinclass.net;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.spinclass.util.Print;

public class MyRequestQueue {

	private static MyRequestQueue instance;
	private RequestQueue mRequestQueue;
	private Context mContext;

	public static final String CANCELLED = "cancelled";

	private MyRequestQueue(Context context) {
		mContext = context;
		mRequestQueue = getRequestQueue();
	}

	private RequestQueue getRequestQueue() {
		if(mRequestQueue == null)
			mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext()); //Want it to live through the entire application, no leaks

		return mRequestQueue;
	}

	public static synchronized MyRequestQueue getInstance(Context context) {
		if(instance == null)
			instance = new MyRequestQueue(context);

		return instance;
	}

	public <T> void add(Request<T> request) {
		getRequestQueue().add(request);
	}

	public void cancelAll(@Nullable final String filter) {
		Print.log("cancelling all requests for " + filter);

		Intent intent = new Intent(filter);
		mContext.sendBroadcast(intent);

		getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {

			@Override
			public boolean apply(Request<?> request) {
				//noinspection SimplifiableIfStatement
				if(filter != null)
					return request.getTag().equals(filter);
				else
					return true;
			}

		});
	}

}
