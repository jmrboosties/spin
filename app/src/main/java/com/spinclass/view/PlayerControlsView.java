package com.spinclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.spinclass.R;

public class PlayerControlsView extends RelativeLayout {

	private ImageView mPlayPauseButton;
	private ImageView mBack;
	private ImageView mNext;

	public PlayerControlsView(Context context) {
		this(context, null);
	}

	public PlayerControlsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PlayerControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.widget_player_controls_section, this, true);

		mPlayPauseButton = (ImageView) findViewById(R.id.pcs_play_pause);
		mBack = (ImageView) findViewById(R.id.pcs_back);
		mNext = (ImageView) findViewById(R.id.pcs_next);
	}

}
