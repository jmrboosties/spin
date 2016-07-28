package com.spinclass.util;

import java.util.concurrent.TimeUnit;

public class Helpbot {

	public static String getDurationTimestampFromMillis(long millis) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

}
