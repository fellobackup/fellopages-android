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
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ActionIconButton extends ImageButton {

    private final int mDefaultColor;

    public ActionIconButton(Context context) {
        this(context, null);
    }

    public ActionIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
    }
    @SuppressWarnings("ResourceType")
    public ActionIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.color,
                android.R.attr.colorForeground});
        if (a.hasValue(0)) {
            mDefaultColor = a.getColor(0, 0);
        } else {
            mDefaultColor = a.getColor(1, 0);
        }
        setColorFilter(mDefaultColor, Mode.SRC_ATOP);
        a.recycle();
    }

    public int getDefaultColor() {
        return mDefaultColor;
    }
}
