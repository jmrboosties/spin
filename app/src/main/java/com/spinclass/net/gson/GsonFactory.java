package com.spinclass.net.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spinclass.model.SpotifyPlaylist;
import com.spinclass.model.SpotifyPlaylistTrack;

public class GsonFactory {

	public static Gson createGson() {
		return createGson(null);
	}

	public static Gson createGson(Class classToIgnore) {
		GsonBuilder builder = new GsonBuilder();

		if(classToIgnore != SpotifyPlaylistTrack.class)
			builder.registerTypeAdapter(SpotifyPlaylistTrack.class, new SpotifyPlaylistTrackDeserializer());

		if(classToIgnore != SpotifyPlaylist.class)
			builder.registerTypeAdapter(SpotifyPlaylist.class, new SpotifyPlaylistDeserializer());

		return builder.create();
	}

}
