package com.spinclass;

import android.app.Application;
import com.spinclass.preference.Preferences;

public class SpinClassApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Preferences.initialize(this);

//		Kahlo.initialize(new UILImgProvider());
	}
}
