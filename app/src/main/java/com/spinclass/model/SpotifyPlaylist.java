package com.spinclass.model;

import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;

import java.util.ArrayList;

public class SpotifyPlaylist {

	@SerializedName(FieldNames.NAME)
	private String mName;

	@SerializedName(FieldNames.URI)
	private String mUri;

	@SerializedName(FieldNames.SNAPSHOT_ID)
	private String mSnapshotId;

	private int mTrackCount;

	private String mTracksUrl;

	private ArrayList<SpotifyPlaylistTrack> mSpotifyTracks = new ArrayList<>();

	public ArrayList<SpotifyPlaylistTrack> getSpotifyTracks() {
		return mSpotifyTracks;
	}

	public void setSpotifyTracks(ArrayList<SpotifyPlaylistTrack> spotifyTracks) {
		mSpotifyTracks = spotifyTracks;
	}

	public String getName() {
		return mName;
	}

	public String getUri() {
		return mUri;
	}

	public int getTrackCount() {
		return mTrackCount;
	}

	public void setTrackCount(int trackCount) {
		mTrackCount = trackCount;
	}

	public String getTracksUrl() {
		return mTracksUrl;
	}

	public void setTracksUrl(String tracksUrl) {
		mTracksUrl = tracksUrl;
	}

	public String getSnapshotId() {
		return mSnapshotId;
	}
}
