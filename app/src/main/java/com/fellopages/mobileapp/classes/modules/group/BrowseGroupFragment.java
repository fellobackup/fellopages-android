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

package com.fellopages.mobileapp.classes.modules.group;


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
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.BrowseDataAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.blog.BrowseBlogFragment;

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
public class BrowseGroupFragment extends Fragment implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    View rootView, footerView;
    private int mUserId, pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private String  mBrowseGroupUrl, mCurrentSelectedModule;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private GridViewWithHeaderAndFooter mGridView;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private BrowseDataAdapter mBrowseDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false, isSearchTextSubmitted = false, isMemberGroups = false,
            isAdLoaded = false, isVisibleToUser = false, isCommunityAds = false, isFirstTab = false;
    private Context mContext;
    private HashMap<String, String> searchParams;
    Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;

    public static BrowseGroupFragment newInstance(Bundle bundle) {
        // Required public constructor
        BrowseGroupFragment fragment = new BrowseGroupFragment();
        fragment.setArguments(bundle);
        return fragment;
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
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        // Make sure that currently visible
        if (visible && !isVisibleToUser && mContext != null) {
            sendRequestToServer();
        }
        if (!isVisible() && snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mGridView != null){
            mGridView.smoothScrollToPosition(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        searchParams = new HashMap<>();
        mAppConst = new AppConstant(getActivity());
        mAppConst.setOnCommunityAdsLoadedListener(BrowseGroupFragment.this);

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.grid_view_layout, container, false);
        footerView = CustomViews.getFooterView(inflater);

        mGridView = rootView.findViewById(R.id.gridView);

        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);

        ViewCompat.setNestedScrollingEnabled(mGridView,true);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_group")){
            PreferencesUtils.updateCurrentModule(mContext, "core_main_group");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        mBrowseGroupUrl = UrlUtil.BROWSE_GROUP_URL + "&page=" + pageNumber;

        mBrowseDataAdapter = new BrowseDataAdapter(getActivity(), R.layout.group_item_view, mBrowseItemList);
        mGridView.setAdapter(mBrowseDataAdapter);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);

        /*
        GET SEARCH ARGUMENTS AND ADD THESE PARAMETERS TO THE URL
         */

        if(getArguments() != null) {

            Bundle bundle = getArguments();

            // If The Fragment Being called from User profile page.
            isMemberGroups = bundle.getBoolean("isMemberGroups");
            mUserId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
            if(mUserId != 0){
                mBrowseGroupUrl += "&user_id=" + mUserId;
            }

            if(!isMemberGroups) {

                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key, null);
                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
                    }
                }

                if(searchParams != null && searchParams.size() != 0){
                    isSearchTextSubmitted = true;
                    mBrowseGroupUrl = mAppConst.buildQueryString(mBrowseGroupUrl, searchParams);
                }

            }

        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_GROUPS_ADS == 1) {
            switch (ConstantVariables.GROUPS_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseDataAdapter,ConstantVariables.GROUPS_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        if (!isMemberGroups || isFirstTab) {
            sendRequestToServer();
        }

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        boolean isAllowedToView = listItems.isAllowToView();
        String groupTitle = listItems.getmBrowseListTitle();

        if (!isAllowedToView) {
            SnackbarUtils.displaySnackbar(rootView,
                    mContext.getResources().getString(R.string.unauthenticated_view_message));
        }else {
            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                    mCurrentSelectedModule, null);
            mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, groupTitle);
            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    /**
     * Function to send request to server
     */
    public void sendRequestToServer() {

        mLoadingPageNo = 1;

        // Don't show data in case of searching and User Profile Tabs.
        if (!isSearchTextSubmitted && !isMemberGroups) {

            try {
                mBrowseGroupUrl = UrlUtil.BROWSE_GROUP_URL + "&page=" + pageNumber;
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.GROUP_FILE);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (isMemberGroups) {
            mBrowseGroupUrl = UrlUtil.BROWSE_GROUP_URL + "&page=" + pageNumber + "&user_id=" + mUserId;
        }

        mAppConst.getJsonResponseFromUrl(mBrowseGroupUrl, new OnResponseListener() {
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
                    mAppConst.getCommunityAds(ConstantVariables.GROUPS_ADS_POSITION, ConstantVariables.GROUPS_ADS_TYPE);
                }

                // Don't save data in case of searching and User Profile Tabs.
                if (!isSearchTextSubmitted && !isMemberGroups) {
                    DataStorage.createTempFile(mContext, DataStorage.GROUP_FILE, jsonObject.toString());
                }

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
                                    sendRequestToServer();
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

    int j =0;
    public void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;
        if (mBody != null) {
            try {
                mTotalItemCount = mBody.getInt("totalItemCount");

                mBrowseList.setmTotalItemCount(mTotalItemCount);
                mDataResponse = mBody.optJSONArray("response");

                if(mDataResponse != null && mDataResponse.length() > 0) {
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                    for (int i = 0; i < mDataResponse.length(); i++) {
                        if ((isAdLoaded || AdFetcher.isAdLoaded())  && mBrowseItemList.size() != 0
                                && mBrowseItemList.size() % ConstantVariables.GROUPS_ADS_POSITION == 0) {
                            switch (ConstantVariables.GROUPS_ADS_TYPE){
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
                                            j = 0;
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
                        JSONObject jsonDataObject = mDataResponse.getJSONObject(i);
                        int group_id = jsonDataObject.getInt("group_id");
                        String title = jsonDataObject.getString("title");
                        String image_icon = jsonDataObject.getString("image");
                        String owner_title = jsonDataObject.getString("owner_title");
                        int member_count = jsonDataObject.getInt("member_count");
                        int allow_to_view = jsonDataObject.getInt("allow_to_view");
                        if(allow_to_view == 1)
                            mBrowseItemList.add(new BrowseListItems(image_icon, title,
                                    owner_title, member_count, group_id, true));
                        else
                            mBrowseItemList.add(new BrowseListItems(image_icon, title,
                                    owner_title, member_count, group_id, false));
                    }

                    // Show End of Result Message when there are less results
                    if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                        CustomViews.showEndOfResults(mContext, footerView);
                    }
                }else{
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = rootView.findViewById(R.id.error_icon);
                    SelectableTextView errorMessage = rootView.findViewById
                            (R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf0c0");
                    errorMessage.setText(mContext.getResources().getString(R.string.no_groups));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                mGridView.setVisibility(View.INVISIBLE);
            }
            mBrowseDataAdapter.notifyDataSetChanged();
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
                sendRequestToServer();
                if (listNativeAdsManager != null) {
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !isLoading) {

            if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()){

                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;

                String url = mBrowseGroupUrl = UrlUtil.BROWSE_GROUP_URL + "&page=" + mLoadingPageNo;

                if(isMemberGroups){
                    url += "&user_id=" + mUserId;
                }
                isLoading = true;
                // Adding Search Params in the scrolling url
                if(searchParams != null && searchParams.size() != 0){
                    url = mAppConst.buildQueryString(url, searchParams);
                }
                loadMoreData(url);
            }

        }
    }

    /* Load More Groups On Scroll */

    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.GROUPS_ADS_POSITION, ConstantVariables.GROUPS_ADS_TYPE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Update currentSelected Option on back press*/
        switch (requestCode){
            case ConstantVariables.VIEW_PAGE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
                if(resultCode == ConstantVariables.VIEW_PAGE_CODE){
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            sendRequestToServer();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.GROUPS_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mBrowseDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    @Override
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {
        mAdvertisementsArray = advertisementsArray;

        if(!isAdLoaded && mAdvertisementsArray != null){
            isAdLoaded = true;
            int j = 0;
            for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.GROUPS_ADS_POSITION == 0 &&
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
