package com.spinclass.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.spinclass.util.Print;

import java.util.ArrayList;

public class DatabaseProvider extends ContentProvider {

	public static final String AUTHORITY = "com.bandsintown.provider.internalDB";
	public static final String DATABASE_NAME = "BIT5.db";
	public static final int DATABASE_VERSION = 6; //ver 6 as of 5.3.0.1

	private OpenDatabaseHelper mDatabaseHelper;

	private static final int RAW = 0;

	private static final UriMatcher mUriMatcher;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, Tables.RAW, RAW);
	}

	public static class OpenDatabaseHelper extends SQLiteOpenHelper {

		private Context mContext;

		public OpenDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + Tables.SpotifyPlaylists.TABLE_NAME + "("
					+ Tables.SpotifyPlaylists.ID


			);

//			db.execSQL("CREATE TABLE " + Posts.TABLE_NAME + "("
//					+ Posts.ID + " INTEGER PRIMARY KEY,"
//					+ Posts.ACTIVITY_ID + " INTEGER UNIQUE,"
//					+ Posts.MESSAGE + " TEXT,"
//					+ Posts.RATING + " REAL,"
//					+ Posts.MEDIA_ID + " INTEGER"
//					+ ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new OpenDatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(@NonNull Uri uri) {
		switch(mUriMatcher.match(uri)) {
//			case ARTISTS :
//				return Artists.CONTENT_ITEM_TYPE;
//			case EVENTS :
//				return Events.CONTENT_ITEM_TYPE;
//			case VENUES :
//				return Venues.CONTENT_ITEM_TYPE;
//			case LINEUPS :
//				return Lineups.CONTENT_ITEM_TYPE;
//			case TICKETS :
//				return Tickets.CONTENT_ITEM_TYPE;
//			case SIMILAR_ARTISTS :
//				return SimilarArtists.CONTENT_ITEM_TYPE;
//			case STATES :
//				return States.CONTENT_ITEM_TYPE;
//			case COUNTRIES :
//				return Countries.CONTENT_ITEM_TYPE;
//			case TRACKERS:
//				return Trackers.CONTENT_ITEM_TYPE;
//			case ATTENDEES:
//				return Attendees.CONTENT_ITEM_TYPE;
//			case USERS :
//				return Users.CONTENT_ITEM_TYPE;
//			case FRIENDS :
//				return Friends.CONTENT_ITEM_TYPE;
//			case ARTIST_STUBS :
//				return ArtistStubs.CONTENT_ITEM_TYPE;
//			case EVENT_STUBS :
//				return EventStubs.CONTENT_ITEM_TYPE;
//			case VENUE_STUBS :
//				return VenueStubs.CONTENT_ITEM_TYPE;
//			case ACTIVITY_FEED_ITEMS :
//				return ActivityFeedItems.CONTENT_ITEM_TYPE;
//			case ACTIVITY_FEED_OBJECTS :
//				return ActivityFeedObjects.CONTENT_ITEM_TYPE;
//			case ACTIVITY_FEED_ACTORS :
//				return ActivityFeedActors.CONTENT_ITEM_TYPE;
//			case EVENT_MAP :
//				return EventMap.CONTENT_ITEM_TYPE;
//			case ACTIVITY_MAP :
//				return ActivityMap.CONTENT_ITEM_TYPE;
//			case RECOMMENDED_ARTISTS :
//				return RecommendedArtists.CONTENT_ITEM_TYPE;
//			case USER_POSTS :
//				return Posts.CONTENT_ITEM_TYPE;
//			case PAYMENT_METHODS :
//				return PaymentMethods.CONTENT_ITEM_TYPE;
//			case PURCHASES :
//				return Purchases.CONTENT_ITEM_TYPE;
//			case ACTIVITY_FEED_GROUPS :
//				return ActivityFeedGroups.CONTENT_ITEM_TYPE;
//			case ACTIVITY_FEED_GROUP_ITEMS :
//				return ActivityFeedGroupItems.CONTENT_ITEM_TYPE;
			default :
				throw new IllegalArgumentException("uri not matched");
		}
	}

	/**
	 * Get the table that the FILE_PATH represents
	 *
	 * @param uri content uri
	 * @return string table name
	 */
	public static String getTable(Uri uri) {
		switch(mUriMatcher.match(uri)) {
			default :
				throw new IllegalArgumentException("uri not matched");
		}
	}
	private ArrayList<String> getOnConflictConstraints(Uri uri) {
		ArrayList<String> constraints = new ArrayList<>();

		switch(mUriMatcher.match(uri)) {
		}

		return constraints;
	}

	@Override
	public synchronized Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		if(uri.equals(Tables.RAW_QUERY))
			return db.rawQuery(selection, selectionArgs);
		else
			return db.query(getTable(uri), projection, selection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public synchronized int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] initialValues) {
		String table = getTable(uri);
		int rows = 0;

		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		db.beginTransaction();

		for(ContentValues values : initialValues) {
			if(handleInsert(db, table, uri, values) >= 0)
				rows++;
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		return rows;
	}

	@Override
	public synchronized Uri insert(@NonNull Uri uri, ContentValues values) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String table = getTable(uri);

		long insertedId = handleInsert(db, table, uri, values);

		if(insertedId < 0)
			Print.exception(new Exception("inserted id: " + insertedId + " and was not caught"));

		if(insertedId > 0)
			return ContentUris.withAppendedId(uri, insertedId);
		else
			return null;
	}

	private long handleInsert(SQLiteDatabase db, String table, Uri uri, ContentValues values) {
		long insertedId = -1;
		switch(mUriMatcher.match(uri)) {
			default :
				try {
					insertedId = db.insertOrThrow(table, null, values);
				} catch(SQLiteConstraintException e) {
					//Try to update
					ArrayList<String> constraints = getOnConflictConstraints(uri);
					if(constraints.size() > 0) {
						StringBuilder selectionBuilder = new StringBuilder();
						for(int i = 0; i < constraints.size(); i++) {
							selectionBuilder.append(constraints.get(i));
							selectionBuilder.append(" = ");
							selectionBuilder.append(values.get(constraints.get(i)));

							if(i < constraints.size() - 1)
								selectionBuilder.append(" AND ");
						}

						String selection = selectionBuilder.toString();
						int updatedRows = db.update(table, values, selection, null);

						//Set the inserted id to 0 if it just updated, if there was an error it will be -1
						if(updatedRows == 1)
							insertedId = 0;
						else
							Print.exception(new Exception("update on conflict yielded result != 1. result: " + updatedRows));
					}
					else
						Print.log("missing sql constraints for uri: " + uri.toString());
				}
				break;
		}

		return insertedId;
	}

	@Override
	public synchronized int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String table = getTable(uri);

		int count = db.delete(table, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public synchronized int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String table = getTable(uri);

		int count = db.update(table, values, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

}
