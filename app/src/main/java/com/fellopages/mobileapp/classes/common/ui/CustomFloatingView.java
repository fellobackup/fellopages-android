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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;


public class CustomFloatingView extends LinearLayout {

    // Member variables.
    // Views.
    private Context mContext;
    private ImageView ivIcon;
    private TextView tvTitle;


    public CustomFloatingView(Context context) {
        this(context, null);
    }

    public CustomFloatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        mContext = context;
        initiateView();
        initStyle(attrs, defStyleAttr);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomFloatingView, defStyleAttr, 0);

        if (a != null) {

            if (a.hasValue(R.styleable.CustomFloatingView_cfv_fabTextColor)) {
                setTextColor(a.getColor(R.styleable.CustomFloatingView_cfv_fabTextColor,
                        ContextCompat.getColor(mContext, R.color.black)));
            }

            Drawable fabIcon = null;
            if (a.hasValue(R.styleable.CustomFloatingView_cfv_fabIcon)) {
                fabIcon = a.getDrawable(R.styleable.CustomFloatingView_cfv_fabIcon);
            }
            if (fabIcon == null) {
                fabIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_message);
            }
            fabIcon.mutate();
            setFabIcon(fabIcon);

            if (a.hasValue(R.styleable.CustomFloatingView_cfv_fabText)) {
                setText(a.getString(R.styleable.CustomFloatingView_cfv_fabText));
            }

            a.recycle();
        }
    }

    private void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.custom_floating_view, this, true);
        ivIcon = findViewById(R.id.icon);
        tvTitle = findViewById(R.id.title);
    }

    private void setText(String text) {
        tvTitle.setText(text);
    }

    public void setTextColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setFabIcon(Drawable drawable) {
        ivIcon.setImageDrawable(drawable);
    }
}
