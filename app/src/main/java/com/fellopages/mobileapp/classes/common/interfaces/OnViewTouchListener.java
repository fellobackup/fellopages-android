/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.common.interfaces;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.fellopages.mobileapp.classes.core.AppConstant;


public class OnViewTouchListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;
    private int mHalfScreenWidth, mHalfScreenHeight;
    private boolean isLongPressed = false;
    private long longClickDuration = 200;
    private Context mContext;


    public OnViewTouchListener(Context context) {
        mContext = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
        mHalfScreenWidth = AppConstant.getDisplayMetricsWidth(context) / 3;
        mHalfScreenHeight = AppConstant.getDisplayMetricsHeight(context) / 8;
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            isLongPressed = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isLongPressed) {
                        onLongPressedCustom();
                    }
                }
            }, longClickDuration);

        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP && isLongPressed) {
            // We're only interested in anything if long pressed.
            isLongPressed = false;
            onTouchRelease();
        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            isLongPressed = true;
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (e.getX() >= AppConstant.getDisplayMetricsWidth(mContext) - mHalfScreenWidth) {
                onRightClick();
            } else if (e.getX() < mHalfScreenWidth){
                onLeftClick();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > mHalfScreenHeight
                        && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onTopToBottomSwipe();
                    } else {
                        onBottomToTopSwipe();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onLongPressed() {

    }

    public void onLongPressedCustom() {
        isLongPressed = true;
    }

    public void onTouchRelease() {

    }

    public void onLeftClick() {

    }

    public void onRightClick() {

    }

    public void onTopToBottomSwipe() {

    }

    public void onBottomToTopSwipe() {

    }
}
