package com.spinclass.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class FakeFabView extends ImageButton {

    private boolean mHasShown = true;
    private boolean mIsReady;
    private boolean mStartHidden;
    private int mBottomMargin;
    private boolean mIsAnimating;
    private OnAnimationCompleteListener mListener;

    public FakeFabView(Context context) {
        super(context);

        getBottomMargin();
    }

    private void getBottomMargin() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) getLayoutParams();
                    mBottomMargin = params.bottomMargin;
                } else if (getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                    mBottomMargin = params.bottomMargin;
                } else
                    throw new ClassCastException("Must be a child of either a frame or coordinator layout");

                mIsReady = true;
                if(mStartHidden)
                    hide();

                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

        });
    }

    public FakeFabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getBottomMargin();
    }

    public FakeFabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getBottomMargin();
    }

    public void startHidden() {
        mStartHidden = true;
    }

    public void show() {
        if(mIsReady) {
            if (getVisibility() != VISIBLE)
                setVisibility(VISIBLE);

            animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mIsAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsAnimating = false;

                    if (mListener != null)
                        mListener.onAnimationComplete(animation);
                }

            }).start();

            mHasShown = true;
        }
    }

    public boolean hasShown() {
        return mHasShown;
    }

    public void hide() {
        hide(mBottomMargin);
    }

    public void hide(int bottomMargin) {
        if(mIsReady) {
            animate().translationY(getHeight() + bottomMargin).setInterpolator(new AccelerateInterpolator(2)).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mIsAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsAnimating = false;

                    if (mListener != null)
                        mListener.onAnimationComplete(animation);
                }

            }).start();

            mHasShown = false;
        }
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public void addOnAnimationCompleteListener(OnAnimationCompleteListener onAnimationCompleteListener) {
        mListener = onAnimationCompleteListener;
    }

    public void removeOnAnimationCompleteListener() {
        mListener = null;
    }

    public interface OnAnimationCompleteListener {

        void onAnimationComplete(Animator animation);

    }

}
