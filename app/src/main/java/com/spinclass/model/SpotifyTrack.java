package com.spinclass.model;

import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;

public class SpotifyTrack {

	@SerializedName(FieldNames.NAME)
	private String mName;

	private String mArtist;

	@SerializedName(FieldNames.DURATION_MS)
	private long mDuration;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getArtist() {
		return mArtist;
	}

	public void setArtist(String artist) {
		mArtist = artist;
	}

	public long getDuration() {
		return mDuration;
	}

	public void setDuration(long duration) {
		mDuration = duration;
	}
}
