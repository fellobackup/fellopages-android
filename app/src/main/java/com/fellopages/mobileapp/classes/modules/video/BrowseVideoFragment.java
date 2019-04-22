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
import android.view.Menu;
import android.view.MenuItem;
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
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.adapters.BrowseDataAdapter;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseVideoFragment extends Fragment implements  AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private String mBrowseVideoUrl, mCurrentSelectedModule, mContentViewUrl, mVideoSubjectType;
    private View rootView,footerView;
    private Context mContext;
    private List<Object> mBrowseItemList;
    private int mLoadingPageNo = 1, mTotalItemCount = 0, isSiteVideoEnabled;
    private BrowseDataAdapter mBrowseDataAdapter;
    private GridViewWithHeaderAndFooter mGridView;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private BrowseListItems mBrowseList;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private boolean isLoading = false, isSearchTextSubmitted = false, isAdvEventProfileVideos = false,
            isAdLoaded = false, isVisibleToUser = false, isProfilePageVideos = false, isStoreProductVideos = false, isFirstTab = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HashMap<String, String> searchParams = new HashMap<>();
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;
    HashMap<String, String> urlParams = new HashMap<>();
    private boolean isCommunityAds = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggle = menu.findItem(R.id.viewToggle);
        if (toggle != null) {
            toggle.setVisible(false);
        }
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser && mContext != null) {
            makeRequest();
        }
        if (!isVisible() && snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public static BrowseVideoFragment newInstance(Bundle bundle) {
        // Required public constructor
        BrowseVideoFragment fragment = new BrowseVideoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(this);

        searchParams = new HashMap<>();
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);
        
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.grid_view_layout, null);
        footerView = CustomViews.getFooterView(inflater);

        mGridView = rootView.findViewById(R.id.gridView);
        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);

        ViewCompat.setNestedScrollingEnabled(mGridView,true);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_video")){
            PreferencesUtils.updateCurrentModule(mContext, "core_main_video");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        //getting the reference of BrowseDataAdapter class
        mBrowseDataAdapter = new BrowseDataAdapter(getActivity(), R.layout.album_item_view, mBrowseItemList);

        //setting the adapter for the list view
        mGridView.setAdapter(mBrowseDataAdapter);

        //Editing the url for browse Vidoes
        mBrowseVideoUrl = UrlUtil.BROWSE_VIDEO_URL;
        urlParams.put("limit", String.valueOf(AppConstant.LIMIT));
        urlParams.put("page", String.valueOf(mLoadingPageNo));

        /**
         * Getting Search Arguments and them in url
         */
        if(getArguments() != null) {

            Bundle bundle = getArguments();

            isSiteVideoEnabled = bundle.getInt(ConstantVariables.ADV_VIDEO_INTEGRATED, 0);

            isProfilePageVideos = bundle.getBoolean("isProfilePageRequest");
            isStoreProductVideos = bundle.getBoolean("isStoreProductVideos");
            isAdvEventProfileVideos = bundle.getBoolean("isAdvEventProfileVideos");
            String contentBrowseUrl = bundle.getString(ConstantVariables.URL_STRING);
            mContentViewUrl = bundle.getString(ConstantVariables.VIEW_PAGE_URL);
            mVideoSubjectType = bundle.getString(ConstantVariables.VIDEO_SUBJECT_TYPE);
            int userId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

            if(userId != 0){
                urlParams.put("user_id", String.valueOf(userId));
            }

            if (isProfilePageVideos && contentBrowseUrl != null && !contentBrowseUrl.isEmpty()) {
                mBrowseVideoUrl = contentBrowseUrl;
            }

            if (!isProfilePageVideos) {
                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
                    }
                }
            }
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_VIDEO_ADS == 1) {
            switch (ConstantVariables.VIDEO_ADS_TYPE){

                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseDataAdapter,ConstantVariables.VIDEO_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        if (!isProfilePageVideos || isStoreProductVideos || isFirstTab) {
            makeRequest();
        }

        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(this);
        return rootView;

    }

    public void makeRequest() {

        mLoadingPageNo = 1;

        String url = getBrowseVideosUrl();
        // Don't show data in case of searching and User Profile Tabs.
        try {
            if (!isSearchTextSubmitted && !isProfilePageVideos) {
                // Don't show data in case of searching and User Profile Tabs.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.VIDEO_FILE);
                if (tempData != null) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(tempData);
                    addDataToList(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.VIDEO_ADS_POSITION,
                            ConstantVariables.VIDEO_ADS_TYPE);
                }
                // Don't save data in case of searching and User Profile Tabs.
                if (!isSearchTextSubmitted && !isProfilePageVideos) {
                    DataStorage.createTempFile(mContext, DataStorage.VIDEO_FILE, jsonObject.toString());
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){

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
     * Function to load more data on scrolling.
     * @param url Url to send request on server.
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


    int j=0;

    public void addDataToList(JSONObject jsonObject){
        mBody = jsonObject;

        if (isAdvEventProfileVideos) {
            mDataResponse = mBody.optJSONArray("response");
        } else {
            mTotalItemCount = mBody.optInt("totalItemCount");
            mBrowseList.setmTotalItemCount(mTotalItemCount);
            mDataResponse = mBody.optJSONArray("response");
        }

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
                int comment_count = jsonDataObject.optInt("comment_count");
                int like_count = jsonDataObject.optInt("like_count");
                int view_count = jsonDataObject.optInt("view_count");
                int duration = jsonDataObject.optInt("duration");
                int rating = jsonDataObject.optInt("rating");
                int rating_count = jsonDataObject.optInt("rating_count");
                String createdDate = jsonDataObject.optString("creation_date");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                int allow_to_view = jsonDataObject.optInt("allow_to_view");
                String video_url = jsonDataObject.optString("video_url");
                String contentUrl = jsonDataObject.optString("content_url");
                int type = jsonDataObject.optInt("type");
                int searchable = jsonDataObject.optInt("search");
                if (searchable == 1) {
                    if (allow_to_view == 1) {
                        mBrowseItemList.add(new BrowseListItems(image, title, createdDate,
                                like_count, comment_count, view_count, rating, rating_count,
                                duration, video_id, video_url, contentUrl, type, true));
                    } else {
                        mBrowseItemList.add(new BrowseListItems(image, title, createdDate,
                                like_count, comment_count, view_count, rating, rating_count,
                                duration, video_id, video_url, contentUrl, type, false));
                    }
                }

            }
            // Show End of Result Message when there are less results
            if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                CustomViews.showEndOfResults(mContext, footerView);
            }

        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf03d");
            errorMessage.setText(mContext.getResources().getString(R.string.no_videos));
        }

        mBrowseDataAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart(){
        super.onStart();
        mBrowseDataAdapter.notifyDataSetChanged();
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
            boolean isAllowedToView = listItems.isAllowToView();

            if (!isAllowedToView) {
                SnackbarUtils.displaySnackbar(rootView, mContext.getResources().
                        getString(R.string.unauthenticated_view_message));
            } else {
                Intent mainIntent;
                if (listItems.getmVideoType() == 1 || listItems.getmVideoType() == 2 ||
                        listItems.getmVideoType() == 3) {
                    mainIntent = new Intent(getActivity(), AdvVideoView.class);
                    mainIntent.putExtra(ConstantVariables.VIEW_ID, listItems.getmListItemId());
                    mainIntent.putExtra(ConstantVariables.VIDEO_TYPE, listItems.getmVideoType());
                    mainIntent.putExtra(ConstantVariables.VIDEO_URL, listItems.getmVideoUrl());
                    mainIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                    mainIntent.putExtra(ConstantVariables.ADV_VIDEO_INTEGRATED, isSiteVideoEnabled);

                    String videoViewUrl;
                    if (mContentViewUrl != null && !mContentViewUrl.isEmpty()) {
                        videoViewUrl = mContentViewUrl + listItems.getmListItemId();
                        if (!videoViewUrl.contains("gutter_menu")) {
                            videoViewUrl = videoViewUrl + "?gutter_menu=1";
                        }
                        mainIntent.putExtra(ConstantVariables.VIDEO_SUBJECT_TYPE, mVideoSubjectType);
                    } else {
                        videoViewUrl = UrlUtil.VIDEO_VIEW_URL + listItems.getmListItemId() + "?gutter_menu=1";
                    }

                    mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, videoViewUrl);
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

                urlParams.put("page", String.valueOf(mLoadingPageNo));
                String url = getBrowseVideosUrl();
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
                mBrowseDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    private String getBrowseVideosUrl(){

        String url = mAppConst.buildQueryString(mBrowseVideoUrl, urlParams);

        // Add search params in url if fragment is being called from search page.
        if(searchParams != null && searchParams.size() != 0){
            isSearchTextSubmitted = true;
            url = mAppConst.buildQueryString(url, searchParams);
        }

        return url;
    }

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
                    mBrowseDataAdapter.notifyDataSetChanged();
                }
            }
        } else {
            CustomViews.removeFooterView(footerView);
            addDataToList(mBody);
        }
    }
}
