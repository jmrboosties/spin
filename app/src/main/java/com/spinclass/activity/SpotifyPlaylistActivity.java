package com.spinclass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.spinclass.R;
import com.spinclass.adapter.SpotifyPlaylistAdapter;
import com.spinclass.constant.Constants;
import com.spinclass.model.SpotifyPlaylist;
import com.spinclass.net.SpotifyApiHelper;
import com.spinclass.net.VolleyContext;
import com.spinclass.net.VolleyRequestListener;
import com.spinclass.net.model.GetSpotifyPlaylistsResponse;
import com.spinclass.net.model.SpotifyMe;
import com.spinclass.preference.Preferences;
import com.spinclass.util.Print;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;

public class SpotifyPlaylistActivity extends AppCompatActivity implements VolleyContext {

	private ArrayList<SpotifyPlaylist> mPlaylists = new ArrayList<>();
	private SpotifyPlaylistAdapter mAdapter;

	private static final String REDIRECT_URI = "spinapptest://callback";
	private static final int REQUEST_CODE = 1112;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.basic_swipe_refresh_recyclerview);

		initLayout();

		connectToSpotifyIfNecessary();
	}

	private void connectToSpotifyIfNecessary() {
		if(Preferences.getInstance().getSpotifyAccessToken() == null || Preferences.getInstance().getSpotifyUserId() == null) {
			AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(Constants.CLIENT_ID,
					AuthenticationResponse.Type.TOKEN,
					REDIRECT_URI);

			builder.setScopes(new String[]{"user-read-private", "streaming"});
			AuthenticationRequest request = builder.build();

			AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
		}
		else {
			//They're connected, trigger the fetch here
			fetchPlaylistsFromSpotify();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CODE) {
			AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
			if(response.getType() == AuthenticationResponse.Type.TOKEN) {
				Preferences.getInstance().setSpotifyAccessToken(response.getAccessToken());

				//Get user id
				new SpotifyApiHelper(this).getSpotifyMe(new VolleyRequestListener<SpotifyMe>() {

					@Override
					public void onResponse(SpotifyMe response) {
						Preferences.getInstance().setSpotifyUserId(response.getId());
						//TODO store other things?

						fetchPlaylistsFromSpotify();
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						Print.log("error getting spotify me");
					}

				});
			}
		}
	}

	private void initLayout() {
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		mAdapter = new SpotifyPlaylistAdapter(this);
		mAdapter.setOnPlaylistClickedListener(new SpotifyPlaylistAdapter.OnPlaylistClickedListener() {

			@Override
			public void onPlaylistClicked(SpotifyPlaylist playlist) {
				handlePlaylistClick(playlist);
			}

			@Override
			public void onPlaylistLongClicked(SpotifyPlaylist playlist) {
				handlePlaylistLongClick(playlist);
			}

		});

		recyclerView.setAdapter(mAdapter);
	}

	private void handlePlaylistLongClick(SpotifyPlaylist playlist) {
		Intent intent = new Intent(this, ClassEditorActivity.class);
		intent.putExtra(Constants.PLAYLIST_TRACKS_URL, playlist.getTracksUrl());

		startActivity(intent);
	}

	private void handlePlaylistClick(SpotifyPlaylist playlist) {
		Intent intent = new Intent(this, ClassPlayerActivity.class);
		intent.putExtra(Constants.SPOTIFY_PLAYLIST_EXTRA, playlist);

		startActivity(intent);
	}

	public void fetchPlaylistsFromSpotify() {
		fetchPlaylistsFromSpotify(null);
	}

	public void fetchPlaylistsFromSpotify(String url) {
		VolleyRequestListener<GetSpotifyPlaylistsResponse> listener = new VolleyRequestListener<GetSpotifyPlaylistsResponse>() {

			@Override
			public void onResponse(GetSpotifyPlaylistsResponse response) {
				mPlaylists.addAll(response.getSpotifyPlaylists());

				String nextPageUrl = response.getNextPageUrl();
				if(nextPageUrl != null)
					fetchPlaylistsFromSpotify(nextPageUrl);

				loadPlaylistsIntoAdapter();
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				Print.log("error fetching playlists");
				if(error.networkResponse.statusCode == 401) {
					Preferences.getInstance().clearSpotifyPrefs();
					connectToSpotifyIfNecessary();
				}
			}

		};

		if(url == null)
			new SpotifyApiHelper(this).getAllPlaylists(listener);
		else
			new SpotifyApiHelper(this).getAllPlaylists(url, listener);
	}

	private void loadPlaylistsIntoAdapter() {
		mAdapter.setSpotifyPlaylists(mPlaylists);
	}

	@Override
	public String getRequestFilter() {
		return SpotifyPlaylistActivity.class.getName();
	}

	@Override
	public Context getContext() {
		return this;
	}
}
