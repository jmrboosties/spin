package com.spinclass.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.spinclass.model.SpotifyPlaylist;

import java.util.ArrayList;

@SuppressWarnings({"TryFinallyCanBeTryWithResources", "ConstantConditions"})
public class DatabaseHelper {

	private static DatabaseHelper helper;
	private ContentResolver mContentResolver;
	private Context mContext;

	private DatabaseHelper(Context context) {
		//Use application context to avoid any leaks
		mContext = context.getApplicationContext();
		mContentResolver = mContext.getContentResolver();
	}

	public static DatabaseHelper getInstance(Context context) {
		if(helper == null)
			helper = new DatabaseHelper(context);

		return helper;
	}

	public synchronized boolean insertValues(Uri uri, ContentValues values) {
		return mContentResolver.insert(uri, values) != null;
	}

	public synchronized int bulkInsertValues(Uri uri, ArrayList<ContentValues> allValues) {
		return mContentResolver.bulkInsert(uri, allValues.toArray(new ContentValues[allValues.size()]));
	}

	public synchronized void saveSpotifyPlaylists(ArrayList<SpotifyPlaylist> playlists) {

	}

}