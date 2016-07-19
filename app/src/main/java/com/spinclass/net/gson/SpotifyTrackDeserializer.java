package com.spinclass.net.gson;

import com.google.gson.*;
import com.spinclass.constant.FieldNames;
import com.spinclass.model.SpotifyTrack;

import java.lang.reflect.Type;

public class SpotifyTrackDeserializer implements JsonDeserializer<SpotifyTrack> {

	@Override
	public SpotifyTrack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Gson gson = GsonFactory.createGson(SpotifyTrack.class);

		SpotifyTrack track = gson.fromJson(json, SpotifyTrack.class);

		JsonArray artists = json.getAsJsonObject().getAsJsonArray(FieldNames.ARTISTS);
		track.setArtist(artists.get(0).getAsJsonObject().getAsJsonPrimitive(FieldNames.NAME).getAsString());

		JsonArray images = json.getAsJsonObject().getAsJsonArray(FieldNames.IMAGES);
		track.setImageUrl(images.get(0).getAsJsonObject().getAsJsonPrimitive(FieldNames.URL).getAsString());

		return track;
	}

}
