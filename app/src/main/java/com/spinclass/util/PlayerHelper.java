package com.spinclass.util;

import android.os.AsyncTask;
import android.widget.SeekBar;
import com.spinclass.model.SpotifyPlaylist;
import com.spinclass.view.PlayerControlsView;
import com.spinclass.view.PlayerProgressSectionView;
import com.spotify.sdk.android.player.*;

/**
 * Does a bunch of shit related to the player so you only have to deal with it once.
 */
public class PlayerHelper implements ConnectionStateCallback, PlayerNotificationCallback, PlayerControlsView.ControlsCallback {

	private Player mPlayer;
	private PlayerProgressSectionView mPlayerProgressSectionView;
	private PlayerHelperCallback mPlayerHelperCallback;
	private PlayerProgressHandler mPlayerProgressHandler;
	private PlayerControlsView mPlayerControlsView;

	private boolean mBufferOnly;

	private SpotifyPlaylist mSpotifyPlaylist;

	private void setPlayer(Player player) {
		mPlayer = player;

		mPlayer.addConnectionStateCallback(this);
		mPlayer.addPlayerNotificationCallback(this);
	}

	private void setPlayerProgressSectionView(PlayerProgressSectionView playerProgressSectionView) {
		mPlayerProgressSectionView = playerProgressSectionView;
	}

	private void setPlayerHelperCallback(PlayerHelperCallback playerHelperCallback) {
		mPlayerHelperCallback = playerHelperCallback;
	}

	private void setPlayerControlsView(PlayerControlsView playerControlsView) {
		mPlayerControlsView = playerControlsView;

		mPlayerControlsView.setControlsCallback(this);
	}

	public void setSpotifyPlaylist(SpotifyPlaylist playlist) {
		mSpotifyPlaylist = playlist;
	}

	private void build() {
		//Set up seekbar to respond to moving it around
		mPlayerProgressSectionView.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser)
					mPlayerProgressSectionView.getTimeProgress().setText(Helpbot.getDurationTimestampFromMillis(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Print.log("on start tracking touch");
				//Stop the seekbar from updating from player
				cancelPlayerProgressHandler();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Print.log("on stop tracking touch");
				int progress = seekBar.getProgress();

				//Set player to start from here
				mPlayer.seekToPosition(progress);
			}

		});
	}

	public Player getPlayer() {
		return mPlayer;
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

	public void playBufferedTrack(String uri) {
		playTrack(uri);
		mBufferOnly = false;
	}

	public void playTrack(final String uri) {
		mPlayer.getPlayerState(new PlayerStateCallback() {

			@Override
			public void onPlayerState(PlayerState playerState) {
				if(uri.equals(playerState.trackUri)) {
					Print.log("track has same uri");

					if(!playerState.playing) {
						Print.log("resuming track");
						mPlayer.resume();
					}
					else
						Print.log("track already playing");
				}
				else {
					Print.log("existing uri differs, starting over");
					mPlayer.play(uri);
				}
			}
		});
	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
		Print.log("playerstate", playerState);
		Print.log("eventtype", eventType);

		mPlayerProgressSectionView.getSeekBar().setProgress(playerState.positionInMs);
		mPlayerProgressSectionView.getSeekBar().setMax(playerState.durationInMs);

		mPlayerProgressSectionView.getTimeProgress().setText(Helpbot.getDurationTimestampFromMillis(playerState.positionInMs));
		mPlayerProgressSectionView.getDuration().setText(Helpbot.getDurationTimestampFromMillis(playerState.durationInMs));

		//TODO says deprecated but still getting track start, use it for now but watch out when updating sdk
		if(eventType == EventType.TRACK_START && mBufferOnly) {
			Print.log("pausing track, hopefully it buffers");
			mPlayer.pause();
			mPlayer.seekToPosition(0);
		}

		if(eventType == EventType.PLAY || playerState.playing) {
			Print.log("starting progress handler");

			generateNewPlayerProgressHandler();
			mPlayerProgressHandler.execute();
		}
		else if(eventType == EventType.LOST_PERMISSION) {
			//TODO toast saying they lost a permission probably due to playing on another device
		}

		if(mPlayerHelperCallback != null)
			mPlayerHelperCallback.onPlaybackEvent(eventType, playerState);
	}

	@Override
	public void onPlayPauseClicked() {
		mPlayerControlsView.togglePlayPauseIcon();

		mPlayer.getPlayerState(new PlayerStateCallback() {

			@Override
			public void onPlayerState(PlayerState playerState) {
				if(playerState.trackUri != null) {
					if(playerState.playing)
						mPlayer.pause();
					else
						mPlayer.resume();
				}
				else {
					//TODO pick first off list and beign playing it
				}
			}

		});
	}

	@Override
	public void onBack() {

	}

	@Override
	public void onNext() {

	}

	public void onDestroy() {
		cancelPlayerProgressHandler();
	}

	public void bufferTrack(String uri) {
		mPlayer.play(uri);
		mBufferOnly = true;
	}

	private class PlayerProgressHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while(!isCancelled()) {
				try {
					Thread.sleep(200L);
				} catch(InterruptedException e) {
//					Don't report, no need
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
							mPlayerProgressSectionView.getSeekBar().setProgress(playerState.positionInMs);
							mPlayerProgressSectionView.getTimeProgress().setText(Helpbot.getDurationTimestampFromMillis(playerState.positionInMs));
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

	private void generateNewPlayerProgressHandler() {
		cancelPlayerProgressHandler();

		mPlayerProgressHandler = new PlayerProgressHandler();
	}

	private void cancelPlayerProgressHandler() {
		if(mPlayerProgressHandler != null)
			mPlayerProgressHandler.cancel(true);

		mPlayerProgressHandler = null;
	}

	@Override
	public void onPlaybackError(ErrorType errorType, String s) {
		Print.log("playback error", s);
	}

	public interface PlayerHelperCallback {

		void onPlaybackEvent(EventType eventType, PlayerState playerState);

	}

	public static class Builder {

		private PlayerHelper mPlayerHelper;

		public Builder(Player player) {
			mPlayerHelper = new PlayerHelper();
			mPlayerHelper.setPlayer(player);
		}

		public Builder setPlayerProgressSectionView(PlayerProgressSectionView playerProgressSectionView) {
			mPlayerHelper.setPlayerProgressSectionView(playerProgressSectionView);
			return this;
		}

		public Builder setCallback(PlayerHelperCallback callback) {
			mPlayerHelper.setPlayerHelperCallback(callback);
			return this;
		}

		public Builder setPlayerControlsView(PlayerControlsView playerControlsView) {
			mPlayerHelper.setPlayerControlsView(playerControlsView);
			return this;
		}

		public PlayerHelper build() {
			//TODO check to make sure all necessary components are present

			return mPlayerHelper;
		}

	}

}
