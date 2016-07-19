package com.spinclass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.spinclass.R;
import com.spinclass.model.SpotifyPlaylist;

import java.util.ArrayList;

public class SpotifyPlaylistAdapter extends RecyclerView.Adapter {

	private Context mContext;
	private ArrayList<SpotifyPlaylist> mSpotifyPlaylists = new ArrayList<>();

	public SpotifyPlaylistAdapter(Context context) {
		mContext = context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new SpotifyPlaylistViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_spotify_playlist, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof SpotifyPlaylistViewHolder)
			((SpotifyPlaylistViewHolder) holder).buildItem();
	}

	@Override
	public int getItemCount() {
		return mSpotifyPlaylists.size();
	}

	public void setSpotifyPlaylists(ArrayList<SpotifyPlaylist> spotifyPlaylists) {
		mSpotifyPlaylists = spotifyPlaylists;
		notifyDataSetChanged();
	}

	public class SpotifyPlaylistViewHolder extends RecyclerView.ViewHolder {

		private TextView mTitle;
		private TextView mSubtitle;

		public SpotifyPlaylistViewHolder(View itemView) {
			super(itemView);

			mTitle = (TextView) itemView.findViewById(R.id.lsp_title);
			mSubtitle = (TextView) itemView.findViewById(R.id.lsp_subtitle);
		}

		public void buildItem() {
			mTitle.setText(mSpotifyPlaylists.get(getAdapterPosition()).getName());
			mSubtitle.setText(mContext.getString(R.string.track_count, mSpotifyPlaylists.get(getAdapterPosition()).getTrackCount()));
		}

	}

}
