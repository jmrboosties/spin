package com.spinclass.database;

import android.net.Uri;

public class Tables {

	public static final String RAW = "raw";
	public static final Uri RAW_QUERY = Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/" + RAW);

	public static final class SpotifyPlaylists {

		public static final String TABLE_NAME = "spotify_playlists";

		//uri and MIME type
		public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.BIT." + TABLE_NAME;

		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String URI = "uri";
		public static final String SNAPSHOT_ID = "snapshot_id";

	}

	public static final class SpotifyTracks {

		public static final String TABLE_NAME = "spotify_tracks";

		//uri and MIME type
		public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.BIT." + TABLE_NAME;

		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String URI = "uri";
		public static final String IMAGE_URL = "image_url";
		public static final String ARTIST = "artist";
		public static final String DURATION = "duration";

	}

	public static final class Classes {

		public static final String TABLE_NAME = "classes";

		//uri and MIME type
		public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.BIT." + TABLE_NAME;

		public static final String ID = "id";
		public static final String CREATED_AT = "created_at";
		public static final String TITLE = "title";

	}

	public static final class ClassTracks {

		public static final String TABLE_NAME = "classes";

		//uri and MIME type
		public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.BIT." + TABLE_NAME;

		public static final String CLASS_ID = "class_id";
		public static final String TRACK_ID = "track_id";
		public static final String ORDER = "order";

	}

}
