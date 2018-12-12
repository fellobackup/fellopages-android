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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.fellopages.mobileapp.R;

import java.util.List;

/**
 * A class that creates, holds, and populates the view assets for a {@link NativeContentAd}.
 */
public class ContentAdViewHolder extends RecyclerView.ViewHolder{
    public NativeContentAdView mAdView;
    public NativeContentAd mContentAd;
    /**
     * Stores the View for a {@link NativeContentAd} and locates specific {@link View}s used
     * to display its assets.
     *
     * @param adView the {@link View} used to display assets for a native content ad.
     */
    public ContentAdViewHolder(NativeContentAdView adView) {
        super(adView);
        mAdView = adView;

        mAdView.setHeadlineView(mAdView.findViewById(R.id.contentad_headline));
        mAdView.setImageView(mAdView.findViewById(R.id.contentad_image));
        mAdView.setBodyView(mAdView.findViewById(R.id.contentad_body));
        if(mAdView.findViewById(R.id.contentad_call_to_action) != null) {
            mAdView.setCallToActionView(mAdView.findViewById(R.id.contentad_call_to_action));
        }
        mAdView.setLogoView(mAdView.findViewById(R.id.contentad_logo));
        if(mAdView.findViewById(R.id.contentad_advertiser) != null) {
            mAdView.setAdvertiserView(mAdView.findViewById(R.id.contentad_advertiser));
        }
    }

    /**
     * Populates the asset {@link View}s contained it the {@link NativeContentAdView} with data
     * from the {@link NativeContentAd} object. This method is invoked when an
     * {@link AdFetcher} has successfully loaded a {@link NativeContentAd}.
     *
     * @param contentAd the ad that is to be displayed
     */
    public void populateView(NativeContentAd contentAd) {
        ((TextView) mAdView.getHeadlineView()).setText(contentAd.getHeadline());
        ((TextView) mAdView.getBodyView()).setText(contentAd.getBody());
        if(mAdView.findViewById(R.id.contentad_call_to_action) != null) {
            ((TextView) mAdView.getCallToActionView()).setText(contentAd.getCallToAction());
        }
        if(mAdView.findViewById(R.id.contentad_advertiser) != null) {
            ((TextView) mAdView.getAdvertiserView()).setText(contentAd.getAdvertiser());
        }

        List<NativeAd.Image> images = contentAd.getImages();

        if (images != null && images.size() > 0) {
            ((ImageView) mAdView.getImageView())
                    .setImageDrawable(images.get(0).getDrawable());
        }

        NativeAd.Image logoImage = contentAd.getLogo();

        if (logoImage != null) {
            ((ImageView) mAdView.getLogoView())
                    .setImageDrawable(logoImage.getDrawable());
        }

        // assign native ad object to the native view and make visible
        mAdView.setNativeAd(contentAd);
        mAdView.setVisibility(View.VISIBLE);
    }
    public void hideView() {
        mAdView.setVisibility(View.GONE);
    }
}
