package com.spinclass.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.spinclass.R;
import com.spinclass.base.BaseActivity;
import com.spinclass.util.Print;

public class SeekbarTest extends BaseActivity {

	private SeekBar mSeekBar;
	private FrameLayout mFrameLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_seekbar);

		initLayout();
	}

	private void initLayout() {
		RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);

		mSeekBar.setMax(200000);
//		mSeekBar.setThumbOffset(18);

		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Print.log("thumb center", mSeekBar.getThumb().getBounds().centerX());
				Print.log("exact center", mSeekBar.getThumb().getBounds().exactCenterX());
			}

		});

		mFrameLayout = (FrameLayout) findViewById(R.id.container);
		mFrameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.alpha_red));

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFrameLayout.getLayoutParams();

//		mFrameLayout = new FrameLayout(this);
//		root.addView(mFrameLayout);
//
//		mFrameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.alpha_red));
//
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFrameLayout.getLayoutParams();
//
//		params.width = FrameLayout.LayoutParams.MATCH_PARENT;
//		params.height = mSeekBar.getHeight() * 2;
//
//		params.addRule(RelativeLayout.ALIGN_BOTTOM, mSeekBar.getId());
//		params.bottomMargin = mSeekBar.getHeight() / 2 + 6; //TODO this is plus 6 making it a little higher
//
//		mFrameLayout.requestLayout();

//		mSeekBar.getLayoutParams().width = (int) (getResources().getDisplayMetrics().widthPixels * .75f);
//		mSeekBar.requestLayout();

		mSeekBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
//				int frameLayoutHeight = seekBar.getHeight() * 2;
//				Print.log("seekbar height times 2", frameLayoutHeight);
//
//				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(seekBar.getWidth(), frameLayoutHeight);
//				frameLayout.setLayoutParams(params);
//
//				Print.log("frame layout y", seekBar.getTop() - frameLayoutHeight);
//				Print.log("seekbar top", seekBar.getTop());
//				Print.log("seekbar y", seekBar.getY());
//
//				frameLayout.setY(seekBar.getTop() - frameLayoutHeight);
//				frameLayout.setX(seekBar.getX());

				resizeNotesContainer();

				mSeekBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				float progressAsPercentage = (float) mSeekBar.getProgress() / (float) mSeekBar.getMax();

				Print.log("progress as percentage", progressAsPercentage);

				addViewToFrameLayout(progressAsPercentage);
			}

		});
	}

	private void resizeNotesContainer() {
//		int sliderBoundsLeft = mSeekBar.getLeft();
//		int sliderBoundsRight = mSeekBar.getRight();
//
//		Print.log("slider left right bounds", sliderBoundsLeft, sliderBoundsRight);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFrameLayout.getLayoutParams();
//		params.addRule(RelativeLayout.ALIGN_BOTTOM, mSeekBar.getId());
//		params.addRule(RelativeLayout.ALIGN_LEFT, mSeekBar.getId());
		params.bottomMargin = mSeekBar.getHeight() / 2 + 6; //TODO this is plus 6 making it a little higher

		mFrameLayout.setPadding(mSeekBar.getPaddingLeft(), 0, mSeekBar.getPaddingRight(), 0);

		mFrameLayout.requestLayout();
	}

	private void addViewToFrameLayout(float progressAsPercentage) {
		View view = getMiddleStripeView();

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		mFrameLayout.addView(view);

//		int progressPositionX = getXOnSeekbarFromProgress(progress);

		int x = mSeekBar.getWidth();
		Print.log("seekbar width", mSeekBar.getWidth());
		Print.log("frame layout width", mFrameLayout.getWidth());

		view.setX(mFrameLayout.getWidth() * progressAsPercentage - ((mFrameLayout.getPaddingLeft() + mFrameLayout.getPaddingRight()) * progressAsPercentage) - view.getLayoutParams().width / 2);

		Print.log("added view", view.getX(), view.getY());
		Print.log("thumb offset?", mSeekBar.getThumbOffset());

//		TextView textView = new TextView(this);
//		textView.setText("Test");
//		mFrameLayout.addView(textView);
	}

//	private int getXOnSeekbarFromProgress(int progress) {
//
//
//
//	}

	private View getMiddleStripeView() {
		FrameLayout frameLayout = new FrameLayout(this);
		frameLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));

		View stripe = new View(this);
		stripe.setBackgroundColor(ContextCompat.getColor(this, R.color.big_green_ui_element_shader));

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(8, ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;

		stripe.setLayoutParams(params);

		frameLayout.addView(stripe);

		return frameLayout;
	}

}
