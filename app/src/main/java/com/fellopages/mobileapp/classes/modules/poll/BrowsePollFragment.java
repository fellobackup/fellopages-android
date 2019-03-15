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

package com.fellopages.mobileapp.classes.modules.poll;


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
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.adapters.BrowseDataAdapter;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BrowsePollFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private Context mContext;
    private View rootView, footerView;
    private ListView mListView;
    private String mBrowsePollUrl, mCurrentSelectedModule;
    private int mUserId, mLoadingPageNo = 1, mTotalItemCount;
    private BrowseDataAdapter mBrowseDataAdapter;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private boolean isLoading=false, isSearchTextSubmitted = false, isAdLoaded = false,
            isVisibleToUser = false, isCommunityAds = false, isFirstTab = false;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HashMap<String, String> searchParams;
    private boolean isMemberPoll = false;
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;


    public BrowsePollFragment() {
        // Required empty public constructor
    }

    public static BrowsePollFragment newInstance(Bundle bundle){
        BrowsePollFragment fragment = new BrowsePollFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
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
            getData();
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

        mAppConst = new AppConstant(getActivity());
        mAppConst.setOnCommunityAdsLoadedListener(BrowsePollFragment.this);
        searchParams = new HashMap<>();

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);

        // Updating current selected module
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_poll")){
            PreferencesUtils.updateCurrentModule(mContext, "core_main_poll");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        rootView = inflater.inflate(R.layout.list_view_layout,container,false);
        mListView = rootView.findViewById(R.id.list_item_view);
        footerView = CustomViews.getFooterView(inflater);

        ViewCompat.setNestedScrollingEnabled(mListView, true);

        mBrowsePollUrl = UrlUtil.BROWSE_POLL_URL;

        mBrowseDataAdapter = new BrowseDataAdapter(getActivity(), R.layout.list_row, mBrowseItemList);
        mListView.setAdapter(mBrowseDataAdapter);

        /**
         * Get Search Arguments and add in url
         */
        if(getArguments() != null) {

            Bundle bundle = getArguments();

            // If The Fragment Being called from User profile page.
            isMemberPoll = bundle.getBoolean("isMemberPolls");
            mUserId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

            if (!isMemberPoll) {

                Set<String> searchArgumentSet = getArguments().keySet();
                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if (value != null) {
                        searchParams.put(key, value);
                    }
                }
            }
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_POLL_ADS == 1) {
            switch (ConstantVariables.POLL_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseDataAdapter,ConstantVariables.POLL_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        if (!isMemberPoll || isFirstTab) {
            getData();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Update currentSelected Option on back press*/
        switch (resultCode){

            case ConstantVariables.VIEW_POLL_REQUEST_CODE:
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        getData();
                    }
                });
                break;
        }

    }


    private void getData() {

        mLoadingPageNo = 1;
        CustomViews.hideEndOfResults(footerView);

        String browsePollsUrl = getBrowsePollUrl() + "&page=" + mLoadingPageNo;

        if (!isSearchTextSubmitted && !isMemberPoll) {
            try {

                // Don't show data in case of searching and User Profile Tabs.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.POLL_FILE);
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

        mAppConst.getJsonResponseFromUrl(browsePollsUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                addDataToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.POLL_ADS_POSITION, ConstantVariables.POLL_ADS_TYPE);
                }
                // Don't save data in case of searching and User Profile Tabs.
                if (!isSearchTextSubmitted && !isMemberPoll) {
                    DataStorage.createTempFile(mContext, DataStorage.POLL_FILE, jsonObject.toString());
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
                                    getData();
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
    private void addDataToList(JSONObject jsonObject) {

        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");

        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("response");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                if ((isAdLoaded || AdFetcher.isAdLoaded())  && mBrowseItemList.size() != 0
                        && mBrowseItemList.size() % ConstantVariables.POLL_ADS_POSITION == 0) {
                    switch (ConstantVariables.POLL_ADS_TYPE){
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
                JSONObject jsonObj = mDataResponse.optJSONObject(i);
                 // Getting data from individual JSONObject
                int pollId = jsonObj.optInt("poll_id");
                String pollTitle = jsonObj.optString("title");
                String creationDate = jsonObj.optString("creation_date");
                String viewCount = jsonObj.optString("view_count");
                String voteCount = jsonObj.optString("vote_count");
                int closed = jsonObj.optInt("closed");
                String ownerImage = jsonObj.optString("owner_image");
                String ownerTitle = jsonObj.optString("owner_title");
                int allowToView = jsonObj.optInt("allow_to_view");

                //Adding data to list
                if (allowToView == 1)
                    mBrowseItemList.add(new BrowseListItems(ownerImage, pollTitle,
                            ownerTitle, creationDate, voteCount, viewCount, closed, pollId, true));
                else
                    mBrowseItemList.add(new BrowseListItems(ownerImage, pollTitle,
                            ownerTitle, creationDate, voteCount, viewCount, closed, pollId, false));

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
            errorIcon.setText("\uf080");
            errorMessage.setText(mContext.getResources().getString(R.string.no_polls));
        }
        mBrowseDataAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if(mBrowseItemList.size() > 0) {
            BrowseListItems browseListItems = (BrowseListItems) mBrowseItemList.get(position);
            if (browseListItems != null) {
                boolean isAllowToView = browseListItems.isAllowToView();
                if (!isAllowToView) {
                    SnackbarUtils.displaySnackbar(rootView,
                            getResources().getString(R.string.unauthenticated_view_message));

                } else {
                    Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, browseListItems.getmListItemId(),
                            mCurrentSelectedModule, null);
                    getActivity().startActivityForResult(mainIntent, ConstantVariables.VIEW_POLL_REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }

            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mListView != null) {
            mListView.smoothScrollToPosition(0);
        }
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                isAdLoaded = false;
                getData();
                if (listNativeAdsManager != null) {
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                }
            }
        });

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if (limit == totalItemCount && !isLoading)
        {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String browsePollsUrl = getBrowsePollUrl() + "&page=" + mLoadingPageNo;

                isLoading = true;
                loadMoreData(browsePollsUrl);
            }
        }


    }

    /**
     * Function to load more data
     * @param url
     */
    private void loadMoreData(String url) {
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.POLL_ADS_POSITION, ConstantVariables.POLL_ADS_TYPE);
                } else{
                    CustomViews.removeFooterView(mListView, footerView);
                    addDataToList(jsonObject);
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

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.POLL_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mBrowseDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    private String getBrowsePollUrl(){

        String url = mBrowsePollUrl;

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
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {

        mAdvertisementsArray = advertisementsArray;

        if(!isAdLoaded && mAdvertisementsArray != null){
            isAdLoaded = true;
            int j = 0;
            for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.POLL_ADS_POSITION == 0 &&
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
