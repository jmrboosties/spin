package com.spinclass.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyPlaylistTrack implements Parcelable {

	private String mName;
	private String mArtist;
	private long mDuration;
	private String mUri;

	protected SpotifyPlaylistTrack(Parcel in) {
		mName = in.readString();
		mArtist = in.readString();
		mDuration = in.readLong();
		mUri = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mArtist);
		dest.writeLong(mDuration);
		dest.writeString(mUri);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SpotifyPlaylistTrack> CREATOR = new Creator<SpotifyPlaylistTrack>() {

		@Override
		public SpotifyPlaylistTrack createFromParcel(Parcel in) {
			return new SpotifyPlaylistTrack(in);
		}

		@Override
		public SpotifyPlaylistTrack[] newArray(int size) {
			return new SpotifyPlaylistTrack[size];
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

	public String getUri() {
		return mUri;
	}

	public void setUri(String uri) {
		mUri = uri;
	}
}
