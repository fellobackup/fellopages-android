/*
 *
 * Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 * You may not use this file except in compliance with the
 * SocialEngineAddOns License Agreement.
 * You may obtain a copy of the License at:
 * https://www.socialengineaddons.com/android-app-license
 * The full copyright and license information is also mentioned
 * in the LICENSE file that was distributed with this
 * source code.
 *
 */

package com.fellopages.mobileapp.classes.common.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.support.TextViewSupport;


public class ActionIconThemedTextView extends AppCompatTextView {

    private final int mIconWidth, mIconHeight;
    @ColorInt
    private int mColor;
    @ColorInt
    private int mDisabledColor;
    @ColorInt
    private int mActivatedColor;

    public ActionIconThemedTextView(Context context) {
        this(context, null);
    }

    public ActionIconThemedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionIconThemedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconActionButton);
        mColor = a.getColor(R.styleable.IconActionButton_iabColor, 0);
        mDisabledColor = a.getColor(R.styleable.IconActionButton_iabDisabledColor, 0);
        mActivatedColor = a.getColor(R.styleable.IconActionButton_iabActivatedColor, 0);
        mIconWidth = a.getDimensionPixelSize(R.styleable.IconActionButton_iabIconWidth, 0);
        mIconHeight = a.getDimensionPixelSize(R.styleable.IconActionButton_iabIconHeight, 0);
        a.recycle();
        updateCompoundDrawables();
    }

    @ColorInt
    public int getActivatedColor() {
        if (mActivatedColor != 0) return mActivatedColor;
        final ColorStateList colors = getLinkTextColors();
        if (colors != null) return colors.getDefaultColor();
        return getCurrentTextColor();
    }

    public void setActivatedColor(@ColorInt int color) {
        this.mActivatedColor = color;
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        updateCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        updateCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        updateCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        updateCompoundDrawables();
    }

    @ColorInt
    public int getColor() {
        if (mColor != 0) return mColor;
        final ColorStateList colors = getTextColors();
        if (colors != null) return colors.getDefaultColor();
        return getCurrentTextColor();
    }

    public void setColor(@ColorInt int color) {
        this.mColor = color;
    }

    @ColorInt
    public int getDisabledColor() {
        if (mDisabledColor != 0) return mDisabledColor;
        final ColorStateList colors = getTextColors();
        if (colors != null) return colors.getColorForState(new int[0], colors.getDefaultColor());
        return getCurrentTextColor();
    }

    public void setDisabledColor(@ColorInt int color) {
        this.mDisabledColor = color;
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateCompoundDrawables();
    }

    private void updateCompoundDrawables() {
        updateCompoundDrawables(getCompoundDrawables());
        updateCompoundDrawables(TextViewSupport.getCompoundDrawablesRelative(this));
    }

    private void updateCompoundDrawables(Drawable[] drawables) {
        if (drawables == null) return;
        for (Drawable d : drawables) {
            if (d == null) continue;
            d.mutate();
            final int color;
            if (isActivated()) {
                color = getActivatedColor();
            } else if (isEnabled()) {
                color = getColor();
            } else {
                color = getDisabledColor();
            }
            if (mIconWidth > 0 && mIconHeight > 0) {
                d.setBounds(0, 0, mIconWidth, mIconHeight);
            }
            d.setColorFilter(color, Mode.SRC_ATOP);
        }
    }

}
