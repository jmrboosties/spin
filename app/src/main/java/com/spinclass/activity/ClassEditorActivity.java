package com.spinclass.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.android.volley.VolleyError;
import com.spinclass.R;
import com.spinclass.adapter.SpotifyTracksAdapter;
import com.spinclass.base.BaseActivity;
import com.spinclass.constant.Constants;
import com.spinclass.dialog.NewMoveDialogBuilder;
import com.spinclass.interfaces.ClassNote;
import com.spinclass.model.Move;
import com.spinclass.model.SpotifyAudioFeatures;
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
	private RelativeLayout mPlayerSection;
	private RelativeLayout mSeekSection;
	private FrameLayout mNotesContainer;

	private Player mPlayer;
	private String mPlaylistTracksUrl;
	private SpotifyTracksAdapter mTracksAdapter;

	private SpotifyPlaylistTrack mCurrentTrack;

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
		mPlayerSection = (RelativeLayout) findViewById(R.id.player_section);
		mSeekSection = (RelativeLayout) findViewById(R.id.seek_container);

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tracks_list);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

			@Override
			public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
			}

			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				mTracksAdapter.swapItems(viewHolder.getAdapterPosition(), target.getAdapterPosition());
				return true;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

			}

			@Override
			public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				super.clearView(recyclerView, viewHolder);
				mTracksAdapter.onSwapComplete();
			}

		});

		itemTouchHelper.attachToRecyclerView(recyclerView);

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

		ImageView newButton = (ImageView) findViewById(R.id.add);

		final PopupMenu popupMenu = new PopupMenu(this, newButton);
		popupMenu.inflate(R.menu.new_class_note);

		newButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Print.log("new button clicked");
				popupMenu.show();
			}

		});

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mPlayer.pause();

				switch(item.getItemId()) {
					case R.id.new_move :
						openNewMoveDialog();
						return true;
					case R.id.new_fadeout :

						return true;
					default :
						return false;
				}
			}
		});

		//Set up seekbar to respond to moving it around
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser)
					mTimeProgress.setText(Helpbot.getDurationTimestampFromMillis(progress));
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

//				Start progress handler again
//				generateNewPlayerProgressHandler();
//				mPlayerProgressHandler.execute();
			}

		});

		//Create container view for note bubbles
		mNotesContainer = new FrameLayout(this);
		mSeekSection.addView(mNotesContainer);

		//TODO remove
//		mNotesContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.alpha_red));

		mNotesContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				resizeNotesContainer();

				mNotesContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}

		});
	}

	private void resizeNotesContainer() {
		int sliderBoundsLeft = mSeekBar.getLeft();
		int sliderBoundsRight = mSeekBar.getRight();

		Print.log("slider left right bounds", sliderBoundsLeft, sliderBoundsRight);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sliderBoundsRight - sliderBoundsLeft, mSeekBar.getHeight() * 2);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, mSeekBar.getId());
		params.addRule(RelativeLayout.ALIGN_LEFT, mSeekBar.getId());
		params.bottomMargin = mSeekBar.getHeight() / 2;

		mNotesContainer.setPadding(mSeekBar.getPaddingLeft(), 0, mSeekBar.getPaddingRight(), 0);

		mNotesContainer.setLayoutParams(params);
		mNotesContainer.requestLayout();
	}

	private void openNewMoveDialog() {
		mPlayer.getPlayerState(new PlayerStateCallback() {

			@Override
			public void onPlayerState(PlayerState playerState) {
				String currentTime = Helpbot.getDurationTimestampFromMillis(playerState.positionInMs);

				NewMoveDialogBuilder builder = new NewMoveDialogBuilder(ClassEditorActivity.this, currentTime, new NewMoveDialogBuilder.OnConfirmClickListener() {

					@Override
					public void onConfirmClicked(String time, String description) {
						Move move = new Move();
						move.setDescription(description);
						move.setTimeStamp(Helpbot.getMillisFromTimestamp(time));

						mCurrentTrack.addClassNote(move);
						mTracksAdapter.notifyItemChanged(mPlaylistTracks.indexOf(mCurrentTrack));

						addClassNoteToPlayer(move);

						//Resume
						mPlayer.resume();
					}

				});

				mAlertDialog = builder.build();
				mAlertDialog.show();
			}

		});
	}

	private void addClassNoteToPlayer(ClassNote classNote) {
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.ic_edit_location_white_24dp);
//		iv.setBackgroundColor(ContextCompat.getColor(this, R.color.alpha_white));

		int classNoteIconSize = (int) getResources().getDimension(R.dimen.class_note_icon_size);

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(classNoteIconSize, classNoteIconSize);
		iv.setLayoutParams(params);

		mNotesContainer.addView(iv);

		float topSeekBarY = mSeekBar.getY() + mSeekSection.getY();

		float seekBarStartX = mSeekBar.getLeft();
		float seekBarLength = mSeekBar.getWidth();

		float progressAsPercentage = seekBarLength * modifier;//classNote.getTimestamp() / mSeekBar.getMax();

		//Remove just testing
		modifier += .25f;
		modifier = Math.min(modifier, 1f);

		//Set progress to match classnote timestamp so it doesn't look off
		mSeekBar.setProgress((int) classNote.getTimestamp());

		float xMinusCenterIcon = progressAsPercentage - (classNoteIconSize / 2);

		Print.log("values", progressAsPercentage, xMinusCenterIcon);

		iv.setX(progressAsPercentage);
		iv.setY(0);
	}

	private static float modifier = 0f;

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

				getAudioFeaturesForTracks();
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
		for(final SpotifyPlaylistTrack track : mPlaylistTracks) {
			helper.getTrackAudioFeatures(track.getUri().split(":")[2], new VolleyRequestListener<SpotifyAudioFeatures>() {

				@Override
				public void onResponse(SpotifyAudioFeatures response) {
					track.setAudioFeatures(response);
					mTracksAdapter.itemChanged(track);
				}

				@Override
				public void onErrorResponse(VolleyError error) {
					Print.log("error getting audio information for track", track.getName());
				}

			});
		}
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

		//Resize notes container
		resizeNotesContainer();

		if(mCurrentTrack == null || eventType == EventType.TRACK_CHANGED)
			mCurrentTrack = getTrackFromUri(playerState.trackUri);

		if(mCurrentTrack == null) {
			//TODO FUCK
		}

		if(eventType == EventType.PLAY || playerState.playing) {
			Print.log("starting progress handler");

			generateNewPlayerProgressHandler();
			mPlayerProgressHandler.execute();
		}
		else if(eventType == EventType.LOST_PERMISSION) {
			//TODO toast saying they lost a permission probably due to playing on another device
		}
	}

	private SpotifyPlaylistTrack getTrackFromUri(String uri) {
		for(SpotifyPlaylistTrack track : mPlaylistTracks) {
			if(track.getUri().equals(uri))
				return track;
		}

		return null;
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

	@Override
	public void onPlaybackError(ErrorType errorType, String s) {
		Print.log("playback error", s);
		Print.log("playback error type", errorType.toString());
	}

	@Override
	public void onDestroy() {
		cancelPlayerProgressHandler();
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}

}
