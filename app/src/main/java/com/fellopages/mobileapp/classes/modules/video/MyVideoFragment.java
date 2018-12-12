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

package com.fellopages.mobileapp.classes.modules.video;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.adapters.ManageDataAdapter;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyVideoFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private String mManageVideoUrl, TAG = "MyVideoFragment";
    private View rootView, footerView;
    private Context mContext;
    private List<Object> mBrowseItemList;
    private int pageNumber = 1, mLoadingPageNo = 1, mTotalItemCount = 0;
    private ManageDataAdapter mManageDataAdapter;
    private GridViewWithHeaderAndFooter mGridView;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private BrowseListItems mBrowseList;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private boolean isVisibleToUser = false,isLoading = false, isAdLoaded = false, isCommunityAds = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public static MyVideoFragment newInstance(Bundle bundle) {
        // Required public constructor
        MyVideoFragment fragment = new MyVideoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(this);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        //Editing the url for browse album
        mManageVideoUrl = UrlUtil.MANAGE_VIDEO_URL + "&page=" + pageNumber;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.grid_view_layout, container,false);
        footerView = CustomViews.getFooterView(inflater);

        mGridView = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.gridView);
        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);

        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);

        ViewCompat.setNestedScrollingEnabled(mGridView,true);

        //getting the reference of BrowseDataAdapter class
        mManageDataAdapter = new ManageDataAdapter(getActivity(), R.layout.album_item_view, mBrowseItemList,
                null, MyVideoFragment.this);
        //setting the adapter for the list view
        mGridView.setAdapter(mManageDataAdapter);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_VIDEO_ADS == 1){

            switch (ConstantVariables.VIDEO_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_COMMUNITY_ADS:
                    isCommunityAds = true;
                    break;
            }

        }

        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(this);

        return rootView;
    }

    /**
     * Show Request only when the fragment os visible
     * @param visible
     */
    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(ConstantVariables.VIDEO_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS){
                mAdFetcher = new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList,mManageDataAdapter,ConstantVariables.VIDEO_ADS_POSITION);
            }
            makeRequest();
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    /**
     * Send request on server to load data
     */
    public void makeRequest(){

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mManageVideoUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.VIDEO_ADS_POSITION,
                            ConstantVariables.VIDEO_ADS_TYPE);
                }
                isVisibleToUser = true;

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }
            }
        });

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){

            case ConstantVariables.PAGE_EDIT_CODE:
            case ConstantVariables.VIEW_PAGE_CODE:
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        makeRequest();
                    }
                });
                break;
        }
    }

    /**
     * Function to load more data to scrolling
     * @param url Url to send request on server
     */
    private  void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.VIDEO_ADS_POSITION,
                            ConstantVariables.VIDEO_ADS_TYPE);
                } else {
                    CustomViews.removeFooterView(footerView);
                    addDataToList(jsonObject);
                }
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    /**
     * Add Data to List and notify adapter
     * @param jsonObject
     */
    int j = 0;
    public void addDataToList(JSONObject jsonObject){
        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("response");

        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                        && mBrowseItemList.size() % ConstantVariables.VIDEO_ADS_POSITION == 0) {
                    switch (ConstantVariables.VIDEO_ADS_TYPE){
                        case ConstantVariables.TYPE_FACEBOOK_ADS:
                            NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                            mBrowseItemList.add(ad);
                            break;
                        case ConstantVariables.TYPE_GOOGLE_ADS:
                            if(mAdFetcher.getAdList() != null && !mAdFetcher.getAdList().isEmpty()){
                                if(j < mAdFetcher.getAdList().size()) {
                                    NativeAppInstallAd nativeAppInstallAd = (NativeAppInstallAd) mAdFetcher.getAdList().get(j);
                                    j++;
                                    mBrowseItemList.add(nativeAppInstallAd);
                                }else {
                                    j=0;
                                }
                            }
                            break;
                        default:
                            if(mAdvertisementsArray != null){
                                if(j < mAdvertisementsArray.length()){
                                    mBrowseItemList.add(addCommunityAddsToList(j));
                                    j++;
                                } else {
                                    j = 0;
                                }
                            }
                            break;
                    }
                }
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int video_id = jsonDataObject.optInt("video_id");
                int event_id = jsonDataObject.optInt("event_id");
                int comment_count = jsonDataObject.optInt("comment_count");
                int like_count = jsonDataObject.optInt("like_count");
                int view_count = jsonDataObject.optInt("view_count");
                int duration = jsonDataObject.optInt("duration");
                int rating = jsonDataObject.optInt("rating");
                int rating_count = jsonDataObject.optInt("rating_count");
                String createdDate = jsonDataObject.optString("creation_date");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                int type = jsonDataObject.optInt("type");
                String video_url = jsonDataObject.optString("video_url");
                String contentUrl = jsonDataObject.optString("content_url");
                JSONArray mMenuArray = jsonDataObject.optJSONArray("menu");
                mBrowseItemList.add(new BrowseListItems(image, title, createdDate,
                        like_count, comment_count, view_count, rating, rating_count,
                        duration, video_id, event_id, video_url, contentUrl, type, mMenuArray));

            }

        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf03d");
            errorMessage.setText(mContext.getResources().getString(R.string.no_videos));
        }

        mManageDataAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart(){
        super.onStart();
        mManageDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mGridView != null) {
            mGridView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        if(listItems != null) {
            Intent mainIntent;
            if (listItems.getmVideoType() == 1 || listItems.getmVideoType() == 2 ||
                    listItems.getmVideoType() == 3) {
                mainIntent = new Intent(mContext, AdvVideoView.class);
                mainIntent.putExtra(ConstantVariables.VIEW_ID, listItems.getmListItemId());
                mainIntent.putExtra(ConstantVariables.VIDEO_TYPE, listItems.getmVideoType());
                mainIntent.putExtra(ConstantVariables.VIDEO_URL, listItems.getmVideoUrl());
                mainIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, PreferencesUtils.
                        getCurrentSelectedModule(mContext));
                startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                mainIntent = new Intent(mContext, WebViewActivity.class);
                mainIntent.putExtra("headerText", listItems.getmBrowseListTitle());
                mainIntent.putExtra("url", listItems.getmContentUrl());
                ((Activity)mContext).startActivityForResult(mainIntent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    @Override
    public void onRefresh() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                isAdLoaded = false;
                makeRequest();
                if (listNativeAdsManager != null) {
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem+visibleItemCount;

        if(limit == totalItemCount && !isLoading) {

            if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)  < mBrowseList.getmTotalItemCount()){

                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;

                String url = UrlUtil.MANAGE_VIDEO_URL + "&page=" + mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }

        }
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.VIDEO_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mManageDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    private CommunityAdsList addCommunityAddsToList(int j) {

        JSONObject singleAdObject = mAdvertisementsArray.optJSONObject(j);
        int adId = singleAdObject.optInt("userad_id");
        String ad_type = singleAdObject.optString("ad_type");
        String cads_title = singleAdObject.optString("cads_title");
        String cads_body = singleAdObject.optString("cads_body");
        String cads_url = singleAdObject.optString("cads_url");
        String image = singleAdObject.optString("image");
        return new CommunityAdsList(adId, ad_type, cads_title, cads_body,
                cads_url, image);
    }

    // todo need to optimize
    @Override
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {
        mAdvertisementsArray = advertisementsArray;

        if(!isAdLoaded && mAdvertisementsArray != null){
            isAdLoaded = true;
            int j = 0;
            for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.VIDEO_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mManageDataAdapter.notifyDataSetChanged();
                }
            }
        } else {
            CustomViews.removeFooterView(footerView);
            addDataToList(mBody);
        }
    }
}
