package com.spinclass.net.model;

import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;

public class SpotifyMe {

	@SerializedName(FieldNames.NAME)
	private String mName;

	@SerializedName(FieldNames.ID)
	private String mId;

	private String mImageUrl;

	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}

}
