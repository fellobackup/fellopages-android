/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.SearchView;

public class CustomSearchView extends SearchView {


    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    //
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {

        // Clear focus when back button is pressed in searchview
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus();
            return false;
        }
        return super.dispatchKeyEventPreIme(event);
    }
}