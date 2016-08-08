package com.spinclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.spinclass.R;
import com.spinclass.util.Print;

public class PlayerProgressSectionView extends RelativeLayout {

	private SeekBar mSeekBar;
	private TextView mTimeProgress;
	private TextView mDuration;
	private FrameLayout mNotesSection;

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
		mNotesSection = (FrameLayout) findViewById(R.id.wpps_notes_section);

		mSeekBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				resizeNotesContainer();
				mSeekBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}

		});

	}

	private void resizeNotesContainer() {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mNotesSection.getLayoutParams();
		params.bottomMargin = mSeekBar.getHeight() / 2;

		mNotesSection.requestLayout();
	}

	public void addClassNoteViewToFrameLayout(float progressAsPercentage, OnClickListener classNoteClickedListener) {
		ImageView iv = new ImageView(getContext());
		iv.setImageResource(R.drawable.ic_edit_location_white_24dp);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT);
		iv.setLayoutParams(params);

		mNotesSection.addView(iv);

		iv.setX(mSeekBar.getPaddingLeft() + mNotesSection.getWidth() * progressAsPercentage
				- ((mNotesSection.getPaddingLeft() + mNotesSection.getPaddingRight()) * progressAsPercentage)
				- iv.getLayoutParams().width / 2);

		Print.log("added view @", iv.getX());


		iv.setOnClickListener(classNoteClickedListener);
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

	public FrameLayout getNotesSection() {
		return mNotesSection;
	}

}
