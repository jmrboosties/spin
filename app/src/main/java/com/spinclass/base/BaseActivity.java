package com.spinclass.base;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.spinclass.net.VolleyContext;

public abstract class BaseActivity extends AppCompatActivity implements VolleyContext {

	protected AlertDialog mAlertDialog;

	@Override
	public String getRequestFilter() {
		return getClass().getName();
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void onStop() {
		super.onStop();

		if(mAlertDialog != null)
			mAlertDialog.dismiss();
	}

}
