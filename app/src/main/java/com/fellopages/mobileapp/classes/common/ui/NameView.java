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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.fellopages.mobileapp.R;

public class NameView extends ThemedTextView {

    private boolean mNameFirst;

    private String mName, mScreenName;

    private ForegroundColorSpan mPrimaryTextColor, mSecondaryTextColor;
    private StyleSpan mPrimaryTextStyle, mSecondaryTextStyle;
    private AbsoluteSizeSpan mPrimaryTextSize, mSecondaryTextSize;

    public NameView(final Context context) {
        this(context, null);
    }

    public NameView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NameView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.END);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NameView, defStyleAttr, 0);
        setPrimaryTextColor(a.getColor(R.styleable.NameView_nv_primaryTextColor, 0));
        setSecondaryTextColor(a.getColor(R.styleable.NameView_nv_secondaryTextColor, 0));
        mPrimaryTextStyle = new StyleSpan(a.getInt(R.styleable.NameView_nv_primaryTextStyle, 0));
        mSecondaryTextStyle = new StyleSpan(a.getInt(R.styleable.NameView_nv_secondaryTextStyle, 0));
        a.recycle();
        setNameFirst(true);
    }

    public void setPrimaryTextColor(final int color) {
        mPrimaryTextColor = new ForegroundColorSpan(color);
    }

    public void setSecondaryTextColor(final int color) {
        mSecondaryTextColor = new ForegroundColorSpan(color);
    }

    public void setName(String name) {
        mName = name;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }

    public void setNameFirst(final boolean nameFirst) {
        mNameFirst = nameFirst;
    }

    public void updateText() {
        updateText(null);
    }

    public void updateText(@Nullable BidiFormatter formatter) {
        if (isInEditMode()) return;
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        final String primaryText = mNameFirst ? mName : mScreenName;
        final String secondaryText = mNameFirst ? mScreenName : mName;
        if (primaryText != null) {
            int start = sb.length();
            if (formatter != null) {
                sb.append(formatter.unicodeWrap(primaryText));
            } else {
                sb.append(primaryText);
            }
            int end = sb.length();
            sb.setSpan(mPrimaryTextColor, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(mPrimaryTextStyle, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(mPrimaryTextSize, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sb.append(" ");
        if (secondaryText != null) {
            int start = sb.length();
            if (formatter != null) {
                sb.append(formatter.unicodeWrap(secondaryText));
            } else {
                sb.append(secondaryText);
            }
            int end = sb.length();
            sb.setSpan(mSecondaryTextColor, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(mSecondaryTextStyle, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(mSecondaryTextSize, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(sb);
    }

    public void setPrimaryTextSize(final float textSize) {
        mPrimaryTextSize = new AbsoluteSizeSpan((int) calculateTextSize(TypedValue.COMPLEX_UNIT_SP, textSize));
    }

    private float calculateTextSize(final int unit, final float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public void setSecondaryTextSize(final float textSize) {
        mSecondaryTextSize = new AbsoluteSizeSpan((int) calculateTextSize(TypedValue.COMPLEX_UNIT_SP, textSize));
    }

}
