package com.uniprogy.outquiz.helpers;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;

public class RoundedCornersListener implements OnLayoutChangeListener {
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(v.getBackground() instanceof ColorDrawable) {
            int radius = (int) Math.round(v.getHeight() / 2.0);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(radius);
            int color = ((ColorDrawable) v.getBackground()).getColor();
            shape.setColor(color);
            v.setBackground(shape);
        }
    }
}
