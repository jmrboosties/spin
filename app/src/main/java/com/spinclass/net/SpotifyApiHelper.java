package com.spinclass.net;

import com.spinclass.model.SpotifyTrack;
import com.spinclass.net.model.GetSpotifyPlaylistsResponse;
import com.spinclass.net.model.SpotifyMe;
import com.spinclass.preference.Preferences;
import com.spinclass.util.Print;

public class SpotifyApiHelper {

	private VolleyContext mVolleyContext;
	private String mAccessToken;
	private String mUserId;

	public static final String BASE_URL = "https://api.spotify.com/v1/";

	public SpotifyApiHelper(VolleyContext volleyContext) {
		mVolleyContext = volleyContext;

		mAccessToken = Preferences.getInstance().getSpotifyAccessToken();
		if(mAccessToken == null)
			throw new NullPointerException("missing access token");

		//Won't necessarily have this yet, so don't throw if it's missing.
		mUserId = Preferences.getInstance().getSpotifyUserId();
	}

	public void getTrackInformation(String trackUri, VolleyRequestListener<SpotifyTrack> listener) {
		String[] split = trackUri.split(":");
		String trackId = split[split.length - 1];

		String url = BASE_URL + "tracks/" + trackId;

		Print.log("get track information url", url);

		SpotifyVolleyRequester requester = new SpotifyVolleyRequester(mVolleyContext, mAccessToken, SpotifyTrack.class);
		requester.makeGetRequest(url, listener);
	}

	public void getAllPlaylists(VolleyRequestListener<GetSpotifyPlaylistsResponse> listener) {
		if(mUserId == null)
			throw new NullPointerException("missing user id");

		String url = BASE_URL + "users/" + mUserId + "/playlists?limit=50";

		getAllPlaylists(url, listener);
	}

	public void getAllPlaylists(String url, VolleyRequestListener<GetSpotifyPlaylistsResponse> listener) {
		Print.log("get playlists url", url);

		SpotifyVolleyRequester requester = new SpotifyVolleyRequester(mVolleyContext, mAccessToken, GetSpotifyPlaylistsResponse.class);
		requester.makeGetRequest(url, listener);
	}

	public void getSpotifyMe(VolleyRequestListener<SpotifyMe> listener) {
		String url = BASE_URL + "me";

		Print.log("get me url", url);

		SpotifyVolleyRequester requester = new SpotifyVolleyRequester(mVolleyContext, mAccessToken, SpotifyMe.class);
		requester.makeGetRequest(url, listener);
	}

}
