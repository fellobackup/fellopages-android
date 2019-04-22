package com.fellopages.mobileapp.classes.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScrollableViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public ScrollableViewPager(Context context) {
        super(context);
    }

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        try {
            return this.isPagingEnabled && super.onInterceptTouchEvent(event);
        }
        catch(IllegalArgumentException exception){
            exception.printStackTrace();
        }
        return false;

    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return this.isPagingEnabled && super.canScroll(v, checkV, dx, x, y);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
    }


    public Boolean getPagingStatus() {
        return isPagingEnabled;
    }
}
