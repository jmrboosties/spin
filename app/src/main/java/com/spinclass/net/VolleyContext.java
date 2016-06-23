package com.spinclass.net;

import android.content.Context;

public interface VolleyContext {

	/**
	 * A request filter for request cancellation
	 *
	 * @return the filter
	 */
	String getRequestFilter();

	/**
	 * Get an app context
	 *
	 * @return app context
	 */
	Context getContext();

}
