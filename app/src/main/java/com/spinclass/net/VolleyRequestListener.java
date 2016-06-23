package com.spinclass.net;

import com.android.volley.VolleyError;

public interface VolleyRequestListener<T> {

	void onResponse(T response);

	void onErrorResponse(VolleyError error);

}
