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

package com.fellopages.mobileapp.classes.common.fragments;

import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUserReviewDeleteListener;

public class FragmentUtils {

    public static OnFragmentDataChangeListener mOnFragmentDataChangeListener;
    public static OnUserReviewDeleteListener mOnUserReviewDeleteListener;

    public static void setFragmentDataChangeListener(OnFragmentDataChangeListener fragmentDataChangeListener) {
        mOnFragmentDataChangeListener = fragmentDataChangeListener;
    }

    public static OnFragmentDataChangeListener getOnFragmentDataChangeListener() {
        return mOnFragmentDataChangeListener;
    }

    public static OnUserReviewDeleteListener getOnUserReviewDeleteListener() {
        return mOnUserReviewDeleteListener;
    }

    public static void setOnUserReviewDeleteListener(OnUserReviewDeleteListener onUserReviewDeleteListener) {
        mOnUserReviewDeleteListener = onUserReviewDeleteListener;
    }
}
