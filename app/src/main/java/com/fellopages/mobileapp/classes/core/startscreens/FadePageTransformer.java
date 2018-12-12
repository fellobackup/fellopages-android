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

package com.fellopages.mobileapp.classes.core.startscreens;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.fellopages.mobileapp.R;


class FadePageTransformer implements ViewPager.PageTransformer {
    TextView title,subtitle;
    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        title = (TextView)view.findViewById(R.id.slideTitle);
        subtitle = (TextView)view.findViewById(R.id.slideSubtitle);
        //view.findViewById(R.id.backgroundImage).setTranslationX(view.getWidth() * -position);

        if (position <= -1.0F || position >= 1.0F) {
            view.setAlpha(0.0F);
            //view.findViewById(R.id.backgroundImage).setAlpha(0.0F);
        } else if (position == 0.0F) {
            view.setAlpha(1.0F);
            //view.findViewById(R.id.backgroundImage).setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setAlpha(1.0F - Math.abs(position));
            //view.findViewById(R.id.backgroundImage).setAlpha(1.0F - Math.abs(position));
        }
        if(title != null)
            title.setTranslationX(view.getWidth() * position);

        if(subtitle != null)
            subtitle.setTranslationX(view.getWidth() * position);
    }
}
