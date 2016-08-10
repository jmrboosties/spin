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

		mSeekBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				resizeNotesContainer();

				mSeekBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				float progressAsPercentage = (float) mSeekBar.getProgress() / (float) mSeekBar.getMax();

				Print.log("progress, max", mSeekBar.getProgress(), mSeekBar.getMax());
				Print.log("progress as percentage", progressAsPercentage);

				addViewToFrameLayout(progressAsPercentage);
			}

		});
	}

	private void resizeNotesContainer() {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFrameLayout.getLayoutParams();
		params.bottomMargin = mSeekBar.getHeight() / 2;

//		mFrameLayout.setPadding(mSeekBar.getPaddingLeft(), 0, mSeekBar.getPaddingRight(), 0);

		mFrameLayout.requestLayout();
	}

	private void addViewToFrameLayout(float progressAsPercentage) {
		View view = getMiddleStripeView();

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		mFrameLayout.addView(view);

		view.setX(mSeekBar.getPaddingLeft() + mFrameLayout.getWidth() * progressAsPercentage - ((mSeekBar.getPaddingLeft() + mSeekBar.getPaddingRight()) * progressAsPercentage) - view.getLayoutParams().width / 2);

		Print.log("added view", view.getX());
	}

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
