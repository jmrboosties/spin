package com.spinclass.model;

import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;

public class SpotifyAudioFeatures {

	@SerializedName(FieldNames.BPM)
	private float mBpm;

	public float getBpm() {
		return mBpm;
	}
}
