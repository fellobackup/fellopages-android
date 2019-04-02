package com.fellopages.mobileapp.classes.common.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollableViewPager extends ViewPager {

    private boolean shouldScroll = true;

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setShouldScroll(boolean shouldScroll) {
        this.shouldScroll = shouldScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return shouldScroll && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return shouldScroll && super.onInterceptTouchEvent(ev);
    }
}
