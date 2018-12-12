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

package com.fellopages.mobileapp.classes.common.utils.support;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class TextViewSupport {
    @Nullable
    public static Drawable[] getCompoundDrawablesRelative(TextView view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return null;
        return TextViewSupportJBMR1.getCompoundDrawablesRelative(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static class TextViewSupportJBMR1 {
        public static Drawable[] getCompoundDrawablesRelative(TextView view) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return null;
            return view.getCompoundDrawablesRelative();
        }
    }
}
