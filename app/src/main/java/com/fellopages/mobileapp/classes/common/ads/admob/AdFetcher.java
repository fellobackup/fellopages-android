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
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class AdFetcher {
    List<Object> adList =  new ArrayList<>();
    int adCount = 0,j=0,adPosition;
    static boolean adsLoaded = false;
    Context mContext;
    List<Object> mBrowseItemList;
    RecyclerView.Adapter mAdapter;
    ArrayAdapter<Object> mArrayAdapter;

    public AdFetcher(Context context){
        mContext = context;
    }
    public void loadAds(List<Object> browseItemList, RecyclerView.Adapter adapter,int adPosition){
        mBrowseItemList = browseItemList;
        mAdapter = adapter;
        this.adPosition = adPosition;
        new LoadGoogleAds().execute();
    }

    public void loadAds(List<Object> browseItemList, ArrayAdapter<Object> adapter, int adPosition){
        mBrowseItemList = browseItemList;
        mArrayAdapter = adapter;
        this.adPosition = adPosition;
        new LoadGoogleAds().execute();
    }

    private void loadNewAd() {
        /* Uncomment the code for testing ads */
        //        AdRequest request = new AdRequest.Builder()
        //                .addTestDevice("FE46332D98712FCF6EB4118088F7467E")  // An example device ID
        //                .build();
        //        request.isTestDevice(mContext);

        AdLoader adLoader = new AdLoader.Builder(mContext, mContext.getResources().getString(R.string.ad_unit_id))
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {

                        LogUtils.LOGD("AdFetcher","Ad Loaded "+appInstallAd.getHeadline());
                        adList.add(appInstallAd);
                        adCount++;
                        loadNewAd();
                    }

                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, etc.
                        loadNewAd();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .setReturnUrlsForImageAssets(true)
                        .setImageOrientation(NativeAdOptions.ORIENTATION_PORTRAIT).build())
                .build();

        if(adCount < 10) {
            adLoader.loadAd(new AdRequest.Builder()
                    .build());
        }else {
            for(int i = 0; i< mBrowseItemList.size();i++){
                if (i != 0 && i % this.adPosition == 0) {
                    if(j < 10) {
                        NativeAppInstallAd nativeAppInstallAd = (NativeAppInstallAd) adList.get(j++);
                        mBrowseItemList.add(i, nativeAppInstallAd);
                    }else {
                        j = 0;
                    }
                    if(mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    if(mArrayAdapter != null){
                        mArrayAdapter.notifyDataSetChanged();
                    }
                    adsLoaded = true;
                }
            }
        }
    }

    private class LoadGoogleAds extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            loadNewAd();
            return null;
        }
    }

    public List<Object> getAdList(){
        return adList;
    }
    public static boolean isAdLoaded(){
        return adsLoaded;
    }

}
