package com.spinclass.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import com.spinclass.net.VolleyContext;

public abstract class BaseActivity extends AppCompatActivity implements VolleyContext {

	@Override
	public String getRequestFilter() {
		return getClass().getName();
	}

	@Override
	public Context getContext() {
		return this;
	}

}
