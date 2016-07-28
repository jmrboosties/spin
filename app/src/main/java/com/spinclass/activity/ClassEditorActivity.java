package com.spinclass.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.spinclass.R;
import com.spinclass.adapter.SpotifyTracksAdapter;
import com.spinclass.base.BaseActivity;
import com.spinclass.constant.Constants;
import com.spinclass.model.SpotifyPlaylistTrack;
import com.spinclass.net.SpotifyApiHelper;
import com.spinclass.net.VolleyRequestListener;
import com.spinclass.net.model.GetSpotifyPlaylistTracksResponse;
import com.spinclass.preference.Preferences;
import com.spinclass.util.Helpbot;
import com.spinclass.util.Print;
import com.spotify.sdk.android.player.*;

import java.util.ArrayList;


public class ClassEditorActivity extends BaseActivity implements ConnectionStateCallback, PlayerNotificationCallback {

	private SeekBar mSeekBar;
	private TextView mTimeProgress;
	private TextView mDuration;

	private Player mPlayer;
	private String mPlaylistTracksUrl;
	private SpotifyTracksAdapter mTracksAdapter;

	private ArrayList<SpotifyPlaylistTrack> mPlaylistTracks = new ArrayList<>();

	private PlayerProgressHandler mPlayerProgressHandler;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		mPlaylistTracksUrl = getIntent().getStringExtra(Constants.PLAYLIST_TRACKS_URL);
		if(mPlaylistTracksUrl == null)
			throw new NullPointerException("missing playlist tracks url");

		setContentView(R.layout.activity_class_editor);

		Config playerConfig = new Config(this, Preferences.getInstance().getSpotifyAccessToken(), Constants.CLIENT_ID);
		Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

			@Override
			public void onInitialized(Player player) {
				mPlayer = player;
				mPlayer.addConnectionStateCallback(ClassEditorActivity.this);
				mPlayer.addPlayerNotificationCallback(ClassEditorActivity.this);
			}

			@Override
			public void onError(Throwable throwable) {

			}

		});

		initLayout();
	}

	private void initLayout() {
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tracks_list);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		mTracksAdapter = new SpotifyTracksAdapter(this);
		mTracksAdapter.setTrackClickListener(new SpotifyTracksAdapter.OnSpotifyPlaylistTrackClickListener() {

			@Override
			public void onSpotifyPlaylistTrackClick(SpotifyPlaylistTrack track) {
				playTrack(track);
			}

		});

		recyclerView.setAdapter(mTracksAdapter);

		getPlaylistTracks();

		mSeekBar = (SeekBar) findViewById(R.id.progress);
		mTimeProgress = (TextView) findViewById(R.id.time_progress);
		mDuration = (TextView) findViewById(R.id.time_remaining);
	}

	private void getPlaylistTracks() {
		getPlaylistTracks(mPlaylistTracksUrl);
	}

	private void getPlaylistTracks(String url) {
		SpotifyApiHelper helper = new SpotifyApiHelper(this);
		helper.getPlaylistTracks(url, new VolleyRequestListener<GetSpotifyPlaylistTracksResponse>() {

			@Override
			public void onResponse(GetSpotifyPlaylistTracksResponse response) {
				mPlaylistTracks.addAll(response.getSpotifyTracks());

				if(response.getNextPageUrl() != null)
					getPlaylistTracks(response.getNextPageUrl());

				//TODO have some progress spinner which dismisses when next page url is not null?

				loadTracksIntoAdapter();
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				Print.log("error getting playlist tracks");
				Print.exception(error);
			}

		});
	}

	private void loadTracksIntoAdapter() {
		mTracksAdapter.setSpotifyTracks(mPlaylistTracks);
	}

	private void playTrack(SpotifyPlaylistTrack track) {
		mPlayer.play(track.getUri());


	}

	@Override
	public void onLoggedIn() {

	}

	@Override
	public void onLoggedOut() {

	}

	@Override
	public void onLoginFailed(Throwable throwable) {

	}

	@Override
	public void onTemporaryError() {

	}

	@Override
	public void onConnectionMessage(String s) {

	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
		Print.log("event type", eventType.toString());
		Print.log("player playing", playerState.playing);
		Print.log("player duration", playerState.durationInMs);
		Print.log("player position", playerState.positionInMs);

		mSeekBar.setProgress(playerState.positionInMs);
		mSeekBar.setMax(playerState.durationInMs);

		mTimeProgress.setText(Helpbot.getDurationTimestampFromMillis(playerState.positionInMs));
		mDuration.setText(Helpbot.getDurationTimestampFromMillis(playerState.durationInMs));

		if(eventType == EventType.PLAY) {
			generateNewPlayerProgressHandler();
			mPlayerProgressHandler.execute();
		}
	}

	private void generateNewPlayerProgressHandler() {
		cancelPlayerProgressHandler();

		mPlayerProgressHandler = new PlayerProgressHandler();
	}

	private void cancelPlayerProgressHandler() {
		if(mPlayerProgressHandler != null)
			mPlayerProgressHandler.cancel(true);

		mPlayerProgressHandler = null;
	}

	private class PlayerProgressHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while(!isCancelled()) {
				try {
					Thread.sleep(200L);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}

				publishProgress();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... v) {
			if(!mPlayer.isShutdown()) {
				mPlayer.getPlayerState(new PlayerStateCallback() {

					@Override
					public void onPlayerState(PlayerState playerState) {
						if(playerState.playing) {
							mSeekBar.setProgress(playerState.positionInMs);
							mTimeProgress.setText(Helpbot.getDurationTimestampFromMillis(playerState.positionInMs));
						}
						else
							cancelPlayerProgressHandler();
					}

				});
			}
			else
				cancelPlayerProgressHandler();
		}

	}

	private void onPlayerStoppedPlaying() {
		cancelPlayerProgressHandler();
	}

	@Override
	public void onPlaybackError(ErrorType errorType, String s) {

	}

	@Override
	public void onDestroy() {
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}

}
