package com.spinclass.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.spinclass.constant.FieldNames;

public class SpotifyTrack implements Parcelable {

	@SerializedName(FieldNames.NAME)
	private String mName;

	private String mArtist;

	private String mImageUrl;

	@SerializedName(FieldNames.DURATION_MS)
	private long mDuration;

	protected SpotifyTrack(Parcel in) {
		mName = in.readString();
		mArtist = in.readString();
		mDuration = in.readLong();
		mImageUrl = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mArtist);
		dest.writeLong(mDuration);
		dest.writeString(mImageUrl);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SpotifyTrack> CREATOR = new Creator<SpotifyTrack>() {

		@Override
		public SpotifyTrack createFromParcel(Parcel in) {
			return new SpotifyTrack(in);
		}

		@Override
		public SpotifyTrack[] newArray(int size) {
			return new SpotifyTrack[size];
		}
	};

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

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}
}
