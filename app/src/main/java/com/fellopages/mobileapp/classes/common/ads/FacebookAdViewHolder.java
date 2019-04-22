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

package com.fellopages.mobileapp.classes.common.ads;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

public class FacebookAdViewHolder extends RecyclerView.ViewHolder {
    public NativeAd mNativeAd;
    public View adView;

    public FacebookAdViewHolder(View itemView) {
        super(itemView);
        adView = itemView;
    }


    public static void inflateAd(NativeAd nativeAd, View adView, Context context,boolean isVideoModule) {
        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        nativeAdTitle.setText(nativeAd.getAdTitle());

        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        nativeAdBody.setText(nativeAd.getAdBody());

        ImageLoader imageLoader = new ImageLoader(context);
        if (adView.findViewById(R.id.appinstall_store) != null) {
            ImageView adIconImageView = adView.findViewById(R.id.appinstall_store);
            imageLoader.setImageUrl(nativeAd.getAdChoicesIcon().getUrl(), adIconImageView);
        }

        // Setting the Text
        if(adView.findViewById(R.id.native_ad_call_to_action) != null) {
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        }

        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        if(adView.findViewById(R.id.native_ad_cover) != null){
            ImageView nativeAdCover = adView.findViewById(R.id.native_ad_cover);
            NativeAd.downloadAndDisplayImage(adCoverImage, nativeAdCover);
        }
        // Downloading and setting the ad icon.
        if(adView.findViewById(R.id.native_ad_icon) != null ) {
            ImageView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            NativeAd.Image adIcon = nativeAd.getAdIcon();
            if(isVideoModule) {
                NativeAd.downloadAndDisplayImage(adCoverImage, nativeAdIcon);
            }else {
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
            }
        }

        // Downloading and setting the ad choice icon.
        if(adView.findViewById(R.id.native_ad_choice_icon) != null ) {
            ImageView nativeAdChoicesIcon = adView.findViewById(R.id.native_ad_choice_icon);
            nativeAdChoicesIcon.setVisibility(View.VISIBLE);
            NativeAd.Image adChoicesIcon = nativeAd.getAdChoicesIcon();
            NativeAd.downloadAndDisplayImage(adChoicesIcon, nativeAdChoicesIcon);
        }

        // Downloading and setting the cover image.
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        if(adView.findViewById(R.id.native_ad_media) != null) {
            MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
            nativeAdMedia.setAutoplay(AdSettings.isVideoAutoplay());
            nativeAdMedia.setSoundEffectsEnabled(true);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int mediaWidth = adView.getWidth() > 0 ? adView.getWidth() : metrics.widthPixels;
            nativeAdMedia.setLayoutParams(new LinearLayout.LayoutParams(
                    mediaWidth,
                    Math.min(
                            (int) (((double) mediaWidth / (double) bannerWidth) * bannerHeight),
                            metrics.heightPixels / 3)));
            nativeAdMedia.setNativeAd(nativeAd);
        }

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable.
        nativeAd.registerViewForInteraction(adView);

    }

}
