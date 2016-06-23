package com.spinclass.net;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class SpotifyVolleyRequester extends GsonVolleyRequester {

	private String mAccessToken;

	protected static final String SPOTIFY_AUTHORIZATION_VALUE_FORMAT = "Bearer %s";

	public SpotifyVolleyRequester(VolleyContext context, String accessToken, Class clazz) {
		super(context, clazz);
		mAccessToken = accessToken;
	}

	@Override
	protected void addHeaders(HashMap map) {
		super.addHeaders(map);
		map.put("Authorization", String.format(SPOTIFY_AUTHORIZATION_VALUE_FORMAT, mAccessToken));
	}

}
