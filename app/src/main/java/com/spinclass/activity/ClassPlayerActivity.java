package com.spinclass.activity;

import android.os.Bundle;
import com.spinclass.R;
import com.spinclass.base.BaseActivity;
import com.spinclass.constant.Constants;
import com.spinclass.model.SpotifyPlaylist;
import com.spinclass.preference.Preferences;
import com.spotify.sdk.android.player.*;

public class ClassPlayerActivity extends BaseActivity implements ConnectionStateCallback, PlayerNotificationCallback {

	private SpotifyPlaylist mSpotifyPlaylist;
	private Player mPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_class_player);

		//TODO get spotify playlist and all its components from the DB

		Config playerConfig = new Config(this, Preferences.getInstance().getSpotifyAccessToken(), Constants.CLIENT_ID);
		Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

			@Override
			public void onInitialized(Player player) {
				mPlayer = player;
				mPlayer.addConnectionStateCallback(ClassPlayerActivity.this);
				mPlayer.addPlayerNotificationCallback(ClassPlayerActivity.this);
			}

			@Override
			public void onError(Throwable throwable) {

			}

		});

		initLayout();
	}

	private void initLayout() {

	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

	}

	@Override
	public void onPlaybackError(ErrorType errorType, String s) {

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

}
