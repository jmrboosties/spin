package com.spinclass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.spinclass.R;
import com.spinclass.model.SpotifyPlaylistTrack;
import com.spinclass.util.Helpbot;
import com.spinclass.util.Print;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class SpotifyTracksAdapter extends RecyclerView.Adapter {

	private Context mContext;
	private ArrayList<SpotifyPlaylistTrack> mSpotifyTracks = new ArrayList<>();

	private HashSet<Integer> mChangedPositions = new HashSet<>();

	private OnSpotifyPlaylistTrackClickListener mTrackClickListener;

	public SpotifyTracksAdapter(Context context) {
		mContext = context;
	}

	public void setSpotifyTracks(ArrayList<SpotifyPlaylistTrack> spotifyTracks) {
		mSpotifyTracks.clear();
		mSpotifyTracks.addAll(spotifyTracks);

		notifyDataSetChanged();
	}

	public void swapItems(int position1, int position2) {
		Print.log("swapping positions", position1, position2);

		Collections.swap(mSpotifyTracks, position1, position2);

		notifyItemMoved(position1, position2);

		mChangedPositions.add(position1);
		mChangedPositions.add(position2);
	}

	public void onSwapComplete() {
		for(Integer i : mChangedPositions)
			notifyItemChanged(i);

		mChangedPositions.clear();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new TrackViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_spotify_track, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof TrackViewHolder)
			((TrackViewHolder) holder).buildItem();
	}

	@Override
	public int getItemCount() {
		return mSpotifyTracks.size();
	}

	public void setTrackClickListener(OnSpotifyPlaylistTrackClickListener trackClickListener) {
		mTrackClickListener = trackClickListener;
	}

	public class TrackViewHolder extends RecyclerView.ViewHolder {

		private TextView mTitle;
		private TextView mArtist;
		private TextView mNotesCount;
		private TextView mOrder;
		private TextView mDuration;

		public TrackViewHolder(View itemView) {
			super(itemView);

			mTitle = (TextView) itemView.findViewById(R.id.lst_title);
			mArtist = (TextView) itemView.findViewById(R.id.lst_subtitle);
			mNotesCount = (TextView) itemView.findViewById(R.id.lst_notes_count);
			mOrder = (TextView) itemView.findViewById(R.id.lst_order);
			mDuration = (TextView) itemView.findViewById(R.id.lst_duration);

			itemView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mTrackClickListener != null)
						mTrackClickListener.onSpotifyPlaylistTrackClick(mSpotifyTracks.get(getAdapterPosition()));
				}

			});
		}

		public void buildItem() {
			mTitle.setText(mSpotifyTracks.get(getAdapterPosition()).getName());
			mArtist.setText(mSpotifyTracks.get(getAdapterPosition()).getArtist());
			mNotesCount.setText(String.valueOf(mSpotifyTracks.get(getAdapterPosition()).getClassNotes().size()));
			mOrder.setText(String.valueOf(getAdapterPosition() + 1));
			mDuration.setText(Helpbot.getDurationTimestampFromMillis(mSpotifyTracks.get(getAdapterPosition()).getDuration()));

//			Kahlo.with(mContext).matrixCenterTop().source(mSpotifyTracks.get(getAdapterPosition()).getImageUrl());
		}

	}

	public interface OnSpotifyPlaylistTrackClickListener {

		void onSpotifyPlaylistTrackClick(SpotifyPlaylistTrack track);

	}

}
