package com.spinclass.net.model;

import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;
import com.spinclass.model.SpotifyPlaylist;

import java.util.ArrayList;

public class GetSpotifyPlaylistsResponse {

	@SerializedName(FieldNames.ITEMS)
	private ArrayList<SpotifyPlaylist> mSpotifyPlaylists = new ArrayList<>();

	@SerializedName(FieldNames.LIMIT)
	private int mLimit;

	@SerializedName(FieldNames.NEXT)
	private String mNextPageUrl;

	public ArrayList<SpotifyPlaylist> getSpotifyPlaylists() {
		return mSpotifyPlaylists;
	}

	public void setSpotifyPlaylists(ArrayList<SpotifyPlaylist> spotifyPlaylists) {
		mSpotifyPlaylists = spotifyPlaylists;
	}

	public int getLimit() {
		return mLimit;
	}

	public String getNextPageUrl() {
		return mNextPageUrl;
	}
}
