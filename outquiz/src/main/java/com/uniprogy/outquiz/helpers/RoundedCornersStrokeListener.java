package com.uniprogy.outquiz.helpers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

public class RoundedCornersStrokeListener implements OnLayoutChangeListener {

    private int borderColor;

    public RoundedCornersStrokeListener(int borderColor)
    {
        this.borderColor = borderColor;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(v.getBackground() instanceof ColorDrawable) {
            int radius = (int) Math.round(v.getHeight() / 2.0);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(radius);
            int color = ((ColorDrawable) v.getBackground()).getColor();
            shape.setColor(color);
            shape.setStroke(2, this.borderColor);
            v.setBackground(shape);
        }
    }
}
