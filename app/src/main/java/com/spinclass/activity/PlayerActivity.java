package com.spinclass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import com.spinclass.R;
import com.spinclass.constant.Constants;
import com.spotify.sdk.android.player.*;

public class PlayerActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

	private String mTrackUri;
	private String mAccessToken;

	private Player mPlayer;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_track);

		mTrackUri = getIntent().getStringExtra("track_uri");
		mAccessToken = getIntent().getStringExtra("access_token");

		Config playerConfig = new Config(this, mAccessToken, Constants.CLIENT_ID);
		Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

			@Override
			public void onInitialized(Player player) {
				mPlayer = player;
				mPlayer.addConnectionStateCallback(PlayerActivity.this);
				mPlayer.addPlayerNotificationCallback(PlayerActivity.this);

				mPlayer.play(mTrackUri);
			}

			@Override
			public void onError(Throwable throwable) {

			}

		});

		initLayout();
	}

	private void initLayout() {
		SeekBar seekBar = (SeekBar) findViewById(R.id.progress);
		TextView timeRemaining = (TextView) findViewById(R.id.time_remaining);
		TextView timeProgress = (TextView) findViewById(R.id.time_progress);
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

	@Override
	protected void onDestroy() {
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}

}
