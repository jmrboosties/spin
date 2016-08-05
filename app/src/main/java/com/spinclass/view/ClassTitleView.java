package com.spinclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.spinclass.R;
import com.spinclass.util.Helpbot;

public class ClassTitleView extends RelativeLayout {

	private TextView mTitle;
	private TextView mAuthor;
	private TextView mDuration;
	private TextView mTrackCount;
	private TextView mAverageBpm;
	private ImageView mPlayButton;

	public ClassTitleView(Context context) {
		this(context, null);
	}

	public ClassTitleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClassTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.widget_class_title_card, this, true);

		mTitle = (TextView) findViewById(R.id.wctc_title);
		mAuthor = (TextView) findViewById(R.id.wctc_author);
		mDuration = (TextView) findViewById(R.id.wctc_duration);
		mAverageBpm = (TextView) findViewById(R.id.wctc_track_count);
		mTrackCount = (TextView) findViewById(R.id.wctc_avg_bpm);
		mPlayButton = (ImageView) findViewById(R.id.wctc_play_button);

		int padding = (int) getContext().getResources().getDimension(R.dimen.class_title_padding);

		setPadding(padding, padding, padding, padding);
	}

	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public void setAuthor(String author) {
		mAuthor.setText(getContext().getString(R.string.by_author, author));
	}

	public void setDuration(long duration) {
		mDuration.setText(Helpbot.getDurationTimestampFromMillis(duration));
	}

	public void setTrackCount(int count) {
		mTrackCount.setText(getContext().getString(R.string.track_count, count));
	}

	public void setAverageBpm(int bpm) {
		mAverageBpm.setText(getContext().getString(R.string.average_bpm, bpm));
	}

	public void setPlayButtonListener(OnClickListener clickListener) {
		mPlayButton.setOnClickListener(clickListener);
	}

}
