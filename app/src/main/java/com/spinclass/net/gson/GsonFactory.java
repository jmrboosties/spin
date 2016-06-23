package com.spinclass.net.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spinclass.model.SpotifyTrack;

public class GsonFactory {

	public static Gson createGson() {
		return createGson(null);
	}

	public static Gson createGson(Class classToIgnore) {
		GsonBuilder builder = new GsonBuilder();

		if(classToIgnore != SpotifyTrack.class)
			builder.registerTypeAdapter(SpotifyTrack.class, new SpotifyTrackDeserializer());

		return builder.create();
	}

}
