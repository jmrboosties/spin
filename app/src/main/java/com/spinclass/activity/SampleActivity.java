package com.spinclass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.spinclass.R;
import com.spinclass.constant.Constants;
import com.spinclass.model.SpotifyPlaylistTrack;
import com.spinclass.net.SpotifyApiHelper;
import com.spinclass.net.VolleyContext;
import com.spinclass.net.VolleyRequestListener;
import com.spinclass.util.Print;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.*;

public class SampleActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback, VolleyContext {

	private TextView mTextView;

	private Player mPlayer;
	private String mAccessToken;

	private String mCurrentTrack;
	private boolean mHasTrackInformation;
	private boolean mGettingTrackInformation;

	private static final String REDIRECT_URI = "spinapptest://callback";
	private static final int REQUEST_CODE = 1112;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sample);

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mPlayer != null) {
					mPlayer.getPlayerState(new PlayerStateCallback() {

						@Override
						public void onPlayerState(PlayerState playerState) {
							if(playerState.playing)
								mPlayer.pause();
							else
								mPlayer.resume();
						}

					});
				}

			}

		});

		mTextView = (TextView) findViewById(R.id.text);

		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(Constants.CLIENT_ID,
						AuthenticationResponse.Type.TOKEN,
						REDIRECT_URI);

				builder.setScopes(new String[]{"user-read-private", "streaming"});
				AuthenticationRequest request = builder.build();

				AuthenticationClient.openLoginActivity(SampleActivity.this, REQUEST_CODE, request);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CODE) {
			AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
			if(response.getType() == AuthenticationResponse.Type.TOKEN) {
				mAccessToken = response.getAccessToken();


				Config playerConfig = new Config(this, response.getAccessToken(), Constants.CLIENT_ID);
				Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

					@Override
					public void onInitialized(Player player) {
						mPlayer = player;
						mPlayer.addConnectionStateCallback(SampleActivity.this);
						mPlayer.addPlayerNotificationCallback(SampleActivity.this);

						mCurrentTrack = "spotify:track:2TpxZ7JUBn3uw46aR7qd6V";

						mPlayer.play(mCurrentTrack);
					}

					@Override
					public void onError(Throwable throwable) {
						Print.log("could not connect", throwable.getMessage());
						throwable.printStackTrace();
					}

				});
			}
		}
	}

	@Override
	public void onLoggedIn() {
		Print.log("on logged in");
	}

	@Override
	public void onLoggedOut() {
		Print.log("on logged out");
	}

	@Override
	public void onLoginFailed(Throwable throwable) {
		Print.log("on logged failed");
		throwable.printStackTrace();
	}

	@Override
	public void onTemporaryError() {
		Print.log("on temporary error");
	}

	@Override
	public void onConnectionMessage(String s) {
		Print.log("connection message", s);
	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
		Print.log("playback event", eventType.toString(), playerState.toString());

		if(eventType == EventType.PLAY || eventType == EventType.TRACK_CHANGED) {
			Print.log("notable event", eventType.toString());

			if(!mCurrentTrack.equals(playerState.trackUri)) {
				mCurrentTrack = playerState.trackUri;
				mHasTrackInformation = false;

				Print.log("updating current track");
			}

			if(!mHasTrackInformation && !mGettingTrackInformation) {
				Print.log("getting track information");
				mGettingTrackInformation = true;

				SpotifyApiHelper helper = new SpotifyApiHelper(this);
				helper.getTrackInformation(mCurrentTrack, new VolleyRequestListener<SpotifyPlaylistTrack>() {

					@Override
					public void onResponse(SpotifyPlaylistTrack response) {
						mHasTrackInformation = true;
						mTextView.setText(response.getName() + " by " + response.getArtist());

						mGettingTrackInformation = false;
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						Print.log("volley error", error);

						mGettingTrackInformation = false;
					}

				});
			}
		}

	}

	@Override
	public void onPlaybackError(ErrorType errorType, String s) {
		Print.log("playback error: " + errorType.toString(),  s);
	}

	@Override
	protected void onDestroy() {
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}

	@Override
	public String getRequestFilter() {
		return SampleActivity.this.getClass().getName();
	}

	@Override
	public Context getContext() {
		return this;
	}

}
