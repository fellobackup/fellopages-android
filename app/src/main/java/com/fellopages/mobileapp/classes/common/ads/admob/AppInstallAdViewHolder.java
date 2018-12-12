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

package com.fellopages.mobileapp.classes.common.ads.admob;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;

import java.util.List;

public class AppInstallAdViewHolder extends RecyclerView.ViewHolder {
    public NativeAppInstallAdView mAdView;
    public NativeAppInstallAd mAppInstallAd;
    /**
     * Stores the View for a {@link NativeAppInstallAd} and locates specific {@link View}s used
     * to display its assets.
     *
     * @param adView the {@link View} used to display assets for a native app install ad.
     */
    public AppInstallAdViewHolder(NativeAppInstallAdView adView) {
        super(adView);
        mAdView = adView;

        mAdView.setHeadlineView(mAdView.findViewById(R.id.appinstall_headline));
        if(mAdView.findViewById(R.id.appinstall_image) != null) {
            mAdView.setImageView(mAdView.findViewById(R.id.appinstall_image));
        }
        if(mAdView.findViewById(R.id.appinstall_body)!= null) {
            mAdView.setBodyView(mAdView.findViewById(R.id.appinstall_body));
        }
        if(mAdView.findViewById(R.id.appinstall_call_to_action)!= null) {
            mAdView.setCallToActionView(mAdView.findViewById(R.id.appinstall_call_to_action));
        }

        mAdView.setIconView(mAdView.findViewById(R.id.appinstall_app_icon));
        if(mAdView.findViewById(R.id.appinstall_stars)!= null) {
            mAdView.setStarRatingView(mAdView.findViewById(R.id.appinstall_stars));
        }
    }

    /**
     * Populates the asset {@link View}s contained it the {@link NativeAppInstallAdView} with data
     * from the {@link NativeAppInstallAd} object. This method is invoked when an
     * {@link AdFetcher} has successfully loaded a {@link NativeAppInstallAd}.
     *
     * @param appInstallAd the ad that is to be displayed
     */
    public void populateView(Context context,NativeAppInstallAd appInstallAd) {

        ((TextView) mAdView.getHeadlineView()).setText(appInstallAd.getHeadline());
        if(mAdView.findViewById(R.id.appinstall_body)!= null) {
            ((TextView) mAdView.getBodyView()).setText(appInstallAd.getBody());
        }
        if(mAdView.findViewById(R.id.appinstall_call_to_action)!= null) {
            if(mAdView.findViewById(R.id.appinstall_call_to_action) instanceof Button){
                ((Button) mAdView.getCallToActionView()).setText(appInstallAd.getCallToAction());
                mAdView.getCallToActionView().setVisibility(View.VISIBLE);
            }else{
                ((TextView) mAdView.getCallToActionView()).setTypeface(GlobalFunctions.getFontIconTypeFace(context));
                ((TextView) mAdView.getCallToActionView()).setText("\uf019");
            }
        }
        ((ImageView) mAdView.getIconView()).setImageDrawable(appInstallAd.getIcon().getDrawable());
        if(mAdView.findViewById(R.id.appinstall_stars)!= null) {
            ((RatingBar) mAdView.getStarRatingView())
                    .setRating(appInstallAd.getStarRating().floatValue());
            mAdView.findViewById(R.id.appinstall_stars).setVisibility(View.VISIBLE);
        }

        List<NativeAd.Image> images = appInstallAd.getImages();

        if (images.size() > 0 && mAdView.findViewById(R.id.appinstall_image) != null) {
            ((ImageView) mAdView.getImageView())
                    .setImageDrawable(images.get(0).getDrawable());
        }

        // assign native ad object to the native view and make visible
        mAdView.setNativeAd(appInstallAd);
        mAdView.setVisibility(View.VISIBLE);
    }

}
