package com.spinclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.spinclass.R;

public class PlayerProgressSectionView extends RelativeLayout {

	private SeekBar mSeekBar;
	private TextView mTimeProgress;
	private TextView mDuration;

	public PlayerProgressSectionView(Context context) {
		this(context, null);
	}

	public PlayerProgressSectionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PlayerProgressSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.widget_player_progress_section, this, true);

		mSeekBar = (SeekBar) findViewById(R.id.wpps_seekbar);
		mTimeProgress = (TextView) findViewById(R.id.wpps_time_progress);
		mDuration = (TextView) findViewById(R.id.wpps_duration);
	}

	public SeekBar getSeekBar() {
		return mSeekBar;
	}

	public TextView getTimeProgress() {
		return mTimeProgress;
	}

	public TextView getDuration() {
		return mDuration;
	}
}
