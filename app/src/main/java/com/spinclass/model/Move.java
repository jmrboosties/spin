package com.spinclass.model;

import com.spinclass.interfaces.ClassNote;

public class Move implements ClassNote {

	private long mTimeStamp;
	private String mDescription;

	@Override
	public long getTimestamp() {
		return mTimeStamp;
	}

	@Override
	public Type getType() {
		return Type.MOVE;
	}

	@Override
	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public void setTimeStamp(long timeStamp) {
		mTimeStamp = timeStamp;
	}
}
