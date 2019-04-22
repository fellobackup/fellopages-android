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

package com.socialengineaddons.messenger.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

import com.socialengineaddons.messenger.R;


public class BaseButton extends Button {
    private final float defaultRadius = 0.0f;

    private int defaultPrimaryColor;

    public BaseButton(Context context) {
        this(context, null);
    }

    public BaseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultPrimaryColor = ContextCompat.getColor(context, R.color.colorPrimary);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseButton);
        int primaryColor = typedArray.getColor(R.styleable.BaseButton_normalStateColor, defaultPrimaryColor);
        float radius = typedArray.getDimension(R.styleable.BaseButton_cornerRadius, defaultRadius);

        int pressedStateColor = primaryColor & 0x00ffffff | 0xdf000000;
        ShapeDrawable shapeSelected = new ShapeDrawable(new RectShape());
        shapeSelected.getPaint().setColor(pressedStateColor);
        shapeSelected.getPaint().setPathEffect(new CornerPathEffect(radius));
        shapeSelected.getPaint().setAntiAlias(true);
        shapeSelected.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeSelected.getPaint().setStrokeWidth(1);


        ShapeDrawable darkenSelected = new ShapeDrawable(new RectShape());
        darkenSelected.getPaint().setColor(Color.BLACK);
        darkenSelected.getPaint().setPathEffect(new CornerPathEffect(radius));
        darkenSelected.getPaint().setAntiAlias(true);
        darkenSelected.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        darkenSelected.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        darkenSelected.getPaint().setStrokeWidth(1);


        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{darkenSelected, shapeSelected});

        ShapeDrawable shapeNormal = new ShapeDrawable(new RectShape());
        shapeNormal.getPaint().setAntiAlias(true);
        shapeNormal.getPaint().setColor(primaryColor);
        shapeNormal.getPaint().setPathEffect(new CornerPathEffect(radius));
        shapeNormal.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeNormal.getPaint().setStrokeWidth(1);

        StateListDrawable states = new StateListDrawable();
        Resources res = getResources();
        states.addState(new int[]{android.R.attr.state_pressed}, layerDrawable);
        states.addState(new int[]{android.R.attr.state_focused}, layerDrawable);
        states.addState(new int[]{}, shapeNormal);

        setBackground(states);
        typedArray.recycle();
    }
}
