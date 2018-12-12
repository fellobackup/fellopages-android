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

package com.fellopages.mobileapp.classes.common.ads.communityAds;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommunityAdsHolder extends RecyclerView.ViewHolder {
    public CommunityAdsList mCommunityAd;
    public View adView;
    private static int mPlacementCount, mAdType;
    private static RecyclerView.Adapter mRecyclerViewAdapter;
    private static ArrayAdapter<Object> mArrayAdapter;
    private static ArrayList<Integer> mRemoveAdsPosition;

    public CommunityAdsHolder(RecyclerView.Adapter recyclerViewAdapter, View itemView,
                              int placementCount, int adType, ArrayList<Integer> removeAdsPosition) {
        super(itemView);
        mRecyclerViewAdapter = recyclerViewAdapter;
        adView = itemView;
        mPlacementCount = placementCount;
        mAdType = adType;
        mRemoveAdsPosition = removeAdsPosition;
    }

    public CommunityAdsHolder(ArrayAdapter<Object> arrayAdapter, View itemView,
                              int placementCount, int adType, ArrayList<Integer> removeAdsPosition) {
        super(itemView);
        mArrayAdapter = arrayAdapter;
        adView = itemView;
        mPlacementCount = placementCount;
        mAdType = adType;
        mRemoveAdsPosition = removeAdsPosition;
    }

    public static void inflateAd(final CommunityAdsList mCommunityAd,
                                 final View adView, final Context context, final int adPosition) {
        // Create native UI using the ad metadata.
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        nativeAdTitle.setText(mCommunityAd.getmCommunityAdTitle());

        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
        if(mCommunityAd.getmCommunityAdBody() != null && !mCommunityAd.getmCommunityAdBody().isEmpty()){
            nativeAdBody.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                nativeAdBody.setText(Html.fromHtml(mCommunityAd.getmCommunityAdBody(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                nativeAdBody.setText(Html.fromHtml(mCommunityAd.getmCommunityAdBody()));
            }
        } else {
            nativeAdBody.setVisibility(View.GONE);
        }

        ImageLoader imageLoader = new ImageLoader(context);

        if(adView.findViewById(R.id.native_ad_cover) != null){
            ImageView nativeAdCover = (ImageView) adView.findViewById(R.id.native_ad_cover);
            imageLoader.setImageUrl(mCommunityAd.getmCommunityAdImage(), nativeAdCover);
            nativeAdCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if(adView.findViewById(R.id.advertiser_layout) != null)
                adView.findViewById(R.id.advertiser_layout).setVisibility(View.GONE);
        }

        // Downloading and setting the ad icon.
        if(adView.findViewById(R.id.native_ad_icon) != null ) {
            ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
            nativeAdIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageLoader.setImageUrl(mCommunityAd.getmCommunityAdImage(), nativeAdIcon);
        }

        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConstant appConstant = new AppConstant(context);
                appConstant.postJsonRequest(AppConstant.DEFAULT_URL + "communityads/update-click-count/"
                        + mCommunityAd.getmCommunityAdsId());
                if(ConstantVariables.WEBVIEW_ENABLE == 1) {
                    Intent webViewActivity = new Intent(context, WebViewActivity.class);
                    webViewActivity.putExtra("url", mCommunityAd.getmCommunityAdUrl());
                    ((Activity)context).startActivityForResult(webViewActivity, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }else {
                    CustomTabUtil.launchCustomTab((Activity) context, GlobalFunctions.
                            getWebViewUrl(mCommunityAd.getmCommunityAdUrl(), context));
                }
            }
        });

        // Apply Click Listener on Remove Ads Image
        if(adView.findViewById(R.id.remove_ads_button) != null){
            adView.findViewById(R.id.remove_ads_button).setVisibility(View.VISIBLE);
                adView.findViewById(R.id.remove_ads_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(PreferencesUtils.getReportAdsArray(context) != null){
                            try {
                                mCommunityAd.setRemoveAdsOptions(new JSONArray(PreferencesUtils.getReportAdsArray(context)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mRemoveAdsPosition.add(adPosition);
                            if(mRecyclerViewAdapter != null){
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            } else if(mArrayAdapter != null){
                                mArrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            String removeAdsUrl = UrlUtil.REMOVE_COMMUNITY_ADS_URL + "?adsId=" +
                                    mCommunityAd.getmCommunityAdsId() + "&placementCount=" +
                                    mPlacementCount + "&type=" + mAdType;

                            AppConstant appConstant = new AppConstant(context);
                            appConstant.getJsonResponseFromUrl(removeAdsUrl, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

                                    PreferencesUtils.updateReportAdsFormArray(context, jsonObject.optJSONArray("form"));

                                    mCommunityAd.setRemoveAdsOptions(jsonObject.optJSONArray("form"));
                                    mRemoveAdsPosition.add(adPosition);

                                    if(mRecyclerViewAdapter != null){
                                        mRecyclerViewAdapter.notifyDataSetChanged();
                                    } else if(mArrayAdapter != null){
                                        mArrayAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                                }
                            });
                        }

                    }
                });
        }

    }

}
