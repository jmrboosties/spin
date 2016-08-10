package com.spinclass.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.android.volley.VolleyError;
import com.spinclass.R;
import com.spinclass.base.BaseActivity;
import com.spinclass.constant.Constants;
import com.spinclass.model.Move;
import com.spinclass.model.SpotifyAudioFeatures;
import com.spinclass.model.SpotifyPlaylist;
import com.spinclass.model.SpotifyPlaylistTrack;
import com.spinclass.net.SpotifyApiHelper;
import com.spinclass.net.VolleyRequestListener;
import com.spinclass.net.model.GetSpotifyPlaylistTracksResponse;
import com.spinclass.preference.Preferences;
import com.spinclass.util.CountdownHelper;
import com.spinclass.util.PlayerHelper;
import com.spinclass.util.Print;
import com.spinclass.view.ClassTitleView;
import com.spinclass.view.PlayerControlsView;
import com.spinclass.view.PlayerProgressSectionView;
import com.spotify.sdk.android.player.*;

public class ClassPlayerActivity extends BaseActivity implements PlayerHelper.PlayerHelperCallback {

	private RelativeLayout mRoot;
	private PlayerProgressSectionView mPlayerProgressSectionView;
	private PlayerControlsView mPlayerControlsView;
	private LinearLayout mPlayerSection;
	private ClassTitleView mClassTitleView;

	private SpotifyPlaylist mSpotifyPlaylist;

	private PlayerHelper mPlayerHelper;
	private CountdownHelper mCountdownHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_class_player);

		//TODO temporary, we store this stuff in the DB prior to hitting the player activity
		mSpotifyPlaylist = getIntent().getParcelableExtra(Constants.SPOTIFY_PLAYLIST_EXTRA);

		//Init layout first because get player callback requires UI
		initLayout();

		Config playerConfig = new Config(this, Preferences.getInstance().getSpotifyAccessToken(), Constants.CLIENT_ID);
		Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

			@Override
			public void onInitialized(Player player) {
				mPlayerHelper = new PlayerHelper.Builder(player)
						.setPlayerProgressSectionView(mPlayerProgressSectionView)
						.setPlayerControlsView(mPlayerControlsView)
						.setCallback(ClassPlayerActivity.this)
						.build();
			}

			@Override
			public void onError(Throwable throwable) {

			}

		});
	}

	private void initLayout() {
		mRoot = (RelativeLayout) findViewById(R.id.root);
		mPlayerProgressSectionView = (PlayerProgressSectionView) findViewById(R.id.acp_progress_view);
		mPlayerControlsView = (PlayerControlsView) findViewById(R.id.acp_controls_view);
		mClassTitleView = (ClassTitleView) findViewById(R.id.class_title_view);

		mPlayerSection = (LinearLayout) findViewById(R.id.player_section);
		mPlayerSection.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				int height = mPlayerSection.getHeight();
				mPlayerSection.setTranslationY(height);

				mPlayerSection.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}

		});

		mClassTitleView.setTitle(mSpotifyPlaylist.getName());
		mClassTitleView.setAuthor("brett");
		mClassTitleView.setTrackCount(mSpotifyPlaylist.getTrackCount());
		mClassTitleView.setAverageBpm(mSpotifyPlaylist.getAverageBpm());
		mClassTitleView.setDuration(mSpotifyPlaylist.getDuration());

		mClassTitleView.setPlayButtonListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onPlayButtonClicked();
			}

		});

		getPlaylistTracks();
	}

	private void onPlayButtonClicked() {
		Print.log("on play clicked");

		ObjectAnimator animator = ObjectAnimator.ofFloat(mClassTitleView, "alpha", 1f, 0f)
				.setDuration(400L);

		animator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				mClassTitleView.setVisibility(View.GONE);
				startCountdown();
			}

		});

		animator.start();
	}

	private void startCountdown() {
		String[] messages = {"5", "4", "3", "2", "1"};

		//Get how long each animation should be, simulating the beat of the music
		float animationBeatDuration = (60f / mSpotifyPlaylist.getSpotifyTracks().get(0).getAudioFeatures().getBpm()) * 1000;

		Print.log("calculated duration", animationBeatDuration);

		//Start the player, but pause it immediately
//		mPlayerHelper.bufferTrack(mSpotifyPlaylist.getSpotifyTracks().get(0).getUri());

		mCountdownHelper = new CountdownHelper(this, mRoot, messages, "Get Ready!", null, (long) animationBeatDuration);
		mCountdownHelper.setCallback(new CountdownHelper.CountdownCallback() {

			@Override
			public void onCountdownAnimationFinished(int i) {
//				//TODO Give it 4 secs to buffer? wish there was a better way
//				if(i == 4)
//					mPlayerHelper.playBufferedTrack(mSpotifyPlaylist.getSpotifyTracks().get(0).getUri());
			}

			@Override
			public void onFinished() {
				Print.log("on finished");

				//TODO Because of the delay look into queueing this and maybe even firing it off a bit earlier
//				mPlayerHelper.getPlayer().play(mSpotifyPlaylist.getSpotifyTracks().get(0).getUri());
				mPlayerHelper.playTrack(mSpotifyPlaylist.getSpotifyTracks().get(0)); //TODO play playlist here instead of single song

				animatePlayerSectionUp();
			}

		});

		mCountdownHelper.start();
	}

	private void animatePlayerSectionUp() {
		ObjectAnimator.ofFloat(mPlayerSection, "translationY", mPlayerSection.getTranslationY(), 0f)
				.setDuration(400L)
				.start();
	}

	//TODO this is all temporary, we shouldnt let them into this screen until all this data is loaded up and verified, will require DB
	private void getPlaylistTracks() {
		getPlaylistTracks(mSpotifyPlaylist.getTracksUrl());
	}

	private void getPlaylistTracks(String url) {
		SpotifyApiHelper helper = new SpotifyApiHelper(this);
		helper.getPlaylistTracks(url, new VolleyRequestListener<GetSpotifyPlaylistTracksResponse>() {

			@Override
			public void onResponse(GetSpotifyPlaylistTracksResponse response) {
				mSpotifyPlaylist.addTracks(response.getSpotifyTracks());

				//TODO temp data
				for(SpotifyPlaylistTrack track : response.getSpotifyTracks()) {
					Move move = new Move();

					move.setTimeStamp(track.getDuration() * (response.getSpotifyTracks().indexOf(track) + 1) / response.getSpotifyTracks().size());
					move.setDescription("Sample Note for " + track.getName());

					track.addClassNote(move);
				}

				if(response.getNextPageUrl() != null)
					getPlaylistTracks(response.getNextPageUrl());

				getAudioFeaturesForTracks();

				mClassTitleView.setDuration(mSpotifyPlaylist.getDuration());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				Print.log("error getting playlist tracks");
				Print.exception(error);
			}

		});
	}

	private void getAudioFeaturesForTracks() {
		SpotifyApiHelper helper = new SpotifyApiHelper(this);
		for(final SpotifyPlaylistTrack track : mSpotifyPlaylist.getSpotifyTracks()) {
			helper.getTrackAudioFeatures(track.getUri().split(":")[2], new VolleyRequestListener<SpotifyAudioFeatures>() {

				@Override
				public void onResponse(SpotifyAudioFeatures response) {
					track.setAudioFeatures(response);
					mClassTitleView.setAverageBpm(mSpotifyPlaylist.getAverageBpm());
				}

				@Override
				public void onErrorResponse(VolleyError error) {
					Print.log("error getting audio information for track", track.getName());
				}

			});
		}
	}

	@Override
	public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPlayerHelper.onDestroy();
		Spotify.destroyPlayer(this);
	}

}
