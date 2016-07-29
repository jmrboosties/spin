package com.spinclass.interfaces;

public interface ClassNote {

	enum Type {
		MOVE, FADEOUT
	}

	long getTimestamp();

	Type getType();

}
