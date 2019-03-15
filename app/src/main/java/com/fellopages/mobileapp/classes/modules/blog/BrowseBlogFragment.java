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

package com.fellopages.mobileapp.classes.modules.blog;


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
import android.widget.ListView;
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
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

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
public class BrowseBlogFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener, NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private View rootView;
    private Context mContext;
    private ListView mListView;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private View footerView;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private BrowseDataAdapter mBrowseDataAdapter;
    private String mBrowseBlogUrl, mCurrentSelectedModule;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading=false, isSearchTextSubmitted = false, isAdLoaded = false, isCommunityAds = false;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private HashMap<String, String> searchParams = new HashMap<>();
    private boolean isMemberBlog = false, isVisibleToUser = false, isFirstTab = false;
    private int mUserId;
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;


    public static BrowseBlogFragment newInstance(Bundle bundle){
        BrowseBlogFragment fragment = new BrowseBlogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(BrowseBlogFragment.this);

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);

        // Updating current selected module
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_blog")){
            PreferencesUtils.updateCurrentModule(mContext, "core_main_blog");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.list_view_layout, container, false);
        mListView = rootView.findViewById(R.id.list_item_view);
        footerView = CustomViews.getFooterView(inflater);

        ViewCompat.setNestedScrollingEnabled(mListView, true);

        mBrowseBlogUrl = UrlUtil.BROWSE_BLOG_URL;

        mBrowseDataAdapter = new BrowseDataAdapter(mContext, R.layout.list_row, mBrowseItemList);
        mListView.setAdapter(mBrowseDataAdapter);


        if(getArguments() != null) {

            Bundle bundle = getArguments();

            // If The Fragment Being called from User profile page.
            isMemberBlog = bundle.getBoolean("isMemberBlogs");
            mUserId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

            if(!isMemberBlog){

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

        if(ConstantVariables.ENABLE_BLOG_ADS == 1) {
            switch (ConstantVariables.BLOG_ADS_TYPE){

                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseDataAdapter,ConstantVariables.BLOG_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        if (!isMemberBlog || isFirstTab) {
            makeRequest();
        }

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    public void makeRequest() {

        mLoadingPageNo = 1;
        CustomViews.hideEndOfResults(footerView);

        String url = getBrowseBlogUrl() + "&page=" + pageNumber;

        // Load Data from Caching in if Fragment is not being called from user profile and search page.
        if (!isSearchTextSubmitted && !isMemberBlog) {

            try {

                // Don't show data in case of searching and User Profile Tabs.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.BLOG_FILE);

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
                    mAppConst.getCommunityAds(ConstantVariables.BLOG_ADS_POSITION, ConstantVariables.BLOG_ADS_TYPE);
                }

                // Don't save data in caching in case of searching and user profile tabs.

                if (!isSearchTextSubmitted && !isMemberBlog) {
                    DataStorage.createTempFile(mContext, DataStorage.BLOG_FILE, jsonObject.toString());
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

    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.BLOG_ADS_POSITION, ConstantVariables.BLOG_ADS_TYPE);
                }else{
                    CustomViews.removeFooterView(mListView, footerView);
                    addDataToList(mBody);
                }
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
        mListView.setOnItemClickListener(this);
    }


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
                        && mBrowseItemList.size() % ConstantVariables.BLOG_ADS_POSITION == 0) {
                    switch (ConstantVariables.BLOG_ADS_TYPE){
                        case ConstantVariables.TYPE_FACEBOOK_ADS:
                            NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                            mBrowseItemList.add(ad);
                            break;
                        case ConstantVariables.TYPE_GOOGLE_ADS:
                            if(mAdFetcher.getAdList() != null && !mAdFetcher.getAdList().isEmpty()) {
                                if (j < mAdFetcher.getAdList().size()) {
                                    NativeAppInstallAd nativeAppInstallAd = (NativeAppInstallAd) mAdFetcher.getAdList().get(j);
                                    j++;
                                    mBrowseItemList.add(nativeAppInstallAd);
                                } else {
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
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int blog_id = jsonDataObject.optInt("blog_id");
                String title = jsonDataObject.optString("title");
                String image_icon = jsonDataObject.optString("owner_image");
                String owner_title = jsonDataObject.optString("owner_title");
                String creation_date = jsonDataObject.optString("creation_date");
                int allow_to_view = jsonDataObject.optInt("allow_to_view");
                int searchable = jsonDataObject.optInt("search");
                if (searchable == 1) {
                    if (allow_to_view == 1)
                        mBrowseItemList.add(new BrowseListItems(image_icon, title,
                                owner_title, creation_date, blog_id, true));
                    else
                        mBrowseItemList.add(new BrowseListItems(image_icon, title,
                                owner_title, creation_date, blog_id, false));
                }

            }

            // Show End of Result Message when there are less results
            if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                CustomViews.addFooterView(mListView, footerView);
                CustomViews.showEndOfResults(mContext, footerView);
            }

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf143");
            errorMessage.setText(mContext.getResources().getString(R.string.no_blogs));
        }
        mBrowseDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(mBrowseItemList.size() > 0) {
            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
            if (listItems != null) {
                boolean isAllowedToView = listItems.isAllowToView();
            /*  Show message if user do not have view privacy * */
                if (!isAllowedToView) {
                    SnackbarUtils.displaySnackbar(rootView,
                            getResources().getString(R.string.unauthenticated_view_message));
                } else {
                    Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                            mCurrentSelectedModule, null);
                    startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mListView != null){
            mListView.smoothScrollToPosition(0);
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
                if (listNativeAdsManager != null)
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if (limit == totalItemCount && !isLoading) {

            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {

                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;


                String url = getBrowseBlogUrl() + "&page=" + mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }

        }
    }

    private String getBrowseBlogUrl(){

        String url = mBrowseBlogUrl;

        // If The Fragment Being called from User profile page.
        if(mUserId != 0){
            url += "&user_id=" + mUserId;
        }

        // If Fragment being called from search page
        if(searchParams != null && searchParams.size() != 0){
            isSearchTextSubmitted = true;
            url = mAppConst.buildQueryString(url, searchParams);
        }
        return url;
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.BLOG_ADS_POSITION == 0) {
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
                if (i != 0 && i % ConstantVariables.BLOG_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mBrowseDataAdapter.notifyDataSetChanged();
                }
            }
        } else{
            CustomViews.removeFooterView(mListView, footerView);
            addDataToList(mBody);
        }
    }
}
