package com.spinclass.net;

import com.spinclass.model.SpotifyTrack;
import com.spinclass.util.Print;

public class SpotifyApiHelper {

	private VolleyContext mVolleyContext;
	private String mAccessToken;

	public static final String BASE_URL = "https://api.spotify.com/v1/";

	public SpotifyApiHelper(VolleyContext volleyContext, String accessToken) {
		mVolleyContext = volleyContext;
		mAccessToken = accessToken;
	}

	public void getTrackInformation(String trackUri, VolleyRequestListener<SpotifyTrack> listener) {
		String[] split = trackUri.split(":");
		String trackId = split[split.length - 1];

		String url = BASE_URL + "tracks/" + trackId;

		Print.log("get track information url");

		SpotifyVolleyRequester requester = new SpotifyVolleyRequester(mVolleyContext, mAccessToken, SpotifyTrack.class);
		requester.makeGetRequest(url, listener);
	}

}
