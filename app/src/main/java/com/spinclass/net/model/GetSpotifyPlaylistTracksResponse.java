package com.spinclass.net.model;

import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;
import com.spinclass.model.SpotifyPlaylistTrack;

import java.util.ArrayList;

public class GetSpotifyPlaylistTracksResponse {

	@SerializedName(FieldNames.ITEMS)
	private ArrayList<SpotifyPlaylistTrack> mSpotifyTracks;

	@SerializedName(FieldNames.NEXT)
	private String mNextPageUrl;

	public String getNextPageUrl() {
		return mNextPageUrl;
	}

	public ArrayList<SpotifyPlaylistTrack> getSpotifyTracks() {
		return mSpotifyTracks;
	}
}
