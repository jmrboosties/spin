package com.spinclass.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CountdownHelper {

//	private HashMap<TextView, Boolean> mTextViews;

	private ViewGroup mRoot;
	private Context mContext;
	private String[] mMessages;
	private String mStartMsg;
	private String mEndMsg;

	private Handler mHandler;

	private long mDuration;

	private CountdownCallback mCallback;

	public CountdownHelper(Context context, ViewGroup root, String[] messages, String startMsg, String endMsg, long duration) {
		mContext = context;
		mMessages = messages;
		mStartMsg = startMsg;
		mEndMsg = endMsg;
		mRoot = root;
		mDuration = duration;

		mHandler = new Handler();

//		buildTextViewMap();
	}

	public void setCallback(CountdownCallback callback) {
		mCallback = callback;
	}

//	private void buildTextViewMap() {
//		mTextViews = new HashMap<>();
//
//		mTextViews.put(buildTextView(), true);
//	}

	private TextView buildTextView() {
		TextView textView = new TextView(mContext);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);

		textView.setLayoutParams(params);

		textView.setVisibility(View.GONE);

		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);

		mRoot.addView(textView);

		return textView;
	}

//	private TextView getAvailableTextView() {
//		for(Map.Entry<TextView, Boolean> entry : mTextViews.entrySet()) {
//			if(entry.getValue()) {
//				entry.setValue(false);
//				return entry.getKey();
//			}
//		}
//
//		//No textviews available, build a new one
//		TextView textView = buildTextView();
//
//		//Set as false because its being used
//		mTextViews.put(textView, false);
//
//		return textView;
//	}

	public void start() {
		//TODO different rules, hangs for a little bit and doesnt fade. work on this
		if(mStartMsg != null) {
			animateCountdownExpandAndFade(mStartMsg, buildTextView(), -1, false);
		}

		for(int i = 0; i < mMessages.length; i++) {
			long baseDelay = mDuration * i + (mStartMsg != null ? 1 : 0);
			long fullDelay = (long) (baseDelay + mDuration * .75f);

			final int finalI = i;
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					animateCountdownExpandAndFade(mMessages[finalI], buildTextView(), finalI, mEndMsg == null && finalI == mMessages.length - 1);
				}

			}, fullDelay);
		}

		//TODO end message doesnt fade out, it sticks for a second then goes away
//		if(mEndMsg != null)
	}

   private void animateCountdownExpandAndFade(String text, final TextView tv, final int i, final boolean last) {
		tv.setText(text);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(
				ObjectAnimator.ofFloat(tv, "alpha", 1f, 0f),
				ObjectAnimator.ofFloat(tv, "scaleX", 1f, 2f),
				ObjectAnimator.ofFloat(tv, "scaleY", 1f, 2f)
		);

		set.setDuration(mDuration);

		set.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				tv.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				tv.setVisibility(View.GONE);

				mRoot.removeView(tv);

				if(mCallback != null) {
//					if(i >= 0)
//						mCallback.onCountdownAnimationFinished(i);

					if(last)
						mCallback.onFinished();
				}

				//TODO not using this can remove once sure
				//It's available again
//				mTextViews.put(tv, true);
			}

		});

		set.start();
	}

	public interface CountdownCallback {

//		void onCountdownAnimationFinished(int i);

		void onFinished();

	}

}
