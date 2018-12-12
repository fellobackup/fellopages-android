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

package com.fellopages.mobileapp.classes.modules.advancedVideos;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ManageDataAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageChannelFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener, AdapterView.OnItemSelectedListener {

    // Member variables.
    private Context mContext;
    private View mRootView, mFooterView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Snackbar mSnackbar;
    private GridViewWithHeaderAndFooter mGridView;
    private List<Object> mBrowseItemList;
    private HashMap<String, String> mPostParams;
    private String mManageChannelUrl, mOrderBy = "";
    private int mLoadingPageNo = 1, mSelectedItem = -1;
    private boolean isVisibleToUser = false, isLoading = false, isAdLoaded = false, isCommunityAds = false;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private SpinnerAdapter adapter;
    private ManageDataAdapter mManageDataAdapter;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private BrowseListItems mBrowseList;
    private NativeAdsManager listNativeAdsManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if (ConstantVariables.ADV_VIDEO_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS) {
                mAdFetcher = new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList, mManageDataAdapter, ConstantVariables.ADV_VIDEO_ADS_POSITION);
            }
            makeRequest();
        } else if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    public static ManageChannelFragment newInstance(Bundle bundle) {
        // Required public constructor
        ManageChannelFragment fragment = new ManageChannelFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initializing member variables.
        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(this);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        mPostParams = new HashMap<>();

        //Editing the url for browse album
        mManageChannelUrl = UrlUtil.ADV_VIDEO_CHANNEL_MANAGE_URL;

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.grid_view_layout, container, false);
        mFooterView = CustomViews.getFooterView(inflater);
        getViews();

        //getting the reference of BrowseDataAdapter class
        mManageDataAdapter = new ManageDataAdapter(mContext, R.layout.album_item_view, mBrowseItemList,
                null, ManageChannelFragment.this);
        //setting the adapter for the list view
        mGridView.setAdapter(mManageDataAdapter);

        if (ConstantVariables.ENABLE_ADV_VIDEO_ADS == 1) {

            switch (ConstantVariables.ADV_VIDEO_ADS_TYPE) {
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

        return mRootView;
    }

    /**
     * Method to get the views.
     */
    private void getViews() {
        mGridView = (GridViewWithHeaderAndFooter) mRootView.findViewById(R.id.gridView);
        mGridView.addFooterView(mFooterView);
        mFooterView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);
        ViewCompat.setNestedScrollingEnabled(mGridView, true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        Spinner orderBySpinner = (Spinner) mRootView.findViewById(R.id.filter_view);
        mRootView.findViewById(R.id.eventFilterBlock).setVisibility(View.VISIBLE);

        adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);

        /* Add Videos filter type to spinner using adapter */
        adapter.add(mContext.getResources().getString(R.string.browse_event_filter_sell_all));
        adapter.add(mContext.getResources().getString(R.string.my_event_filter_liked));
        adapter.add(mContext.getResources().getString(R.string.favorites_channel_filter));
        adapter.add(mContext.getResources().getString(R.string.subscribed_menu_label));
        adapter.add(mContext.getResources().getString(R.string.rated_videos_filter));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderBySpinner.setAdapter(adapter);
        orderBySpinner.setSelection(0, false);
        orderBySpinner.setOnItemSelectedListener(this);
        orderBySpinner.setTag("orderby");
    }

    /**
     * Method to get manage page url.
     * @return Returns the manage url by adding the required params.
     */
    private String getChannelManageUrl() {
        String url = mManageChannelUrl;
        if (mPostParams != null && !mPostParams.isEmpty()) {
            url = mAppConst.buildQueryString(url, mPostParams);
        }
        url = url + "&page=" + mLoadingPageNo;
        return  url;
    }

    /**
     * Send request on server to load data
     */
    private void makeRequest() {

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(getChannelManageUrl(), new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }

                addDataToList(jsonObject);
                if (isCommunityAds) {
                    mAppConst.getCommunityAds(ConstantVariables.ADV_VIDEO_ADS_POSITION,
                            ConstantVariables.ADV_VIDEO_ADS_TYPE);
                }
                isVisibleToUser = true;

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                // Scrolling to top when changing the filter.
                if (mOrderBy != null && !mOrderBy.isEmpty()) {
                    mGridView.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (isRetryOption) {
                    mSnackbar = SnackbarUtils.displaySnackbarWithAction(mContext, mRootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }
            }
        });

    }

    /**
     * Method to load more data to scrolling
     *
     * @param url Url to send request on server
     */
    private void loadMoreData(String url) {

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if (isCommunityAds) {
                    mAppConst.getCommunityAds(ConstantVariables.ADV_VIDEO_ADS_POSITION,
                            ConstantVariables.ADV_VIDEO_ADS_TYPE);
                } else {
                    CustomViews.removeFooterView(mFooterView);
                    addDataToList(jsonObject);
                }
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });
    }

    int j = 0;
    /**
     * Method to add browse page data into list.
     *
     * @param jsonObject Object which contains the browse page data.
     */
    private void addDataToList(JSONObject jsonObject) {
        mBody = jsonObject;
        mBrowseList.setmTotalItemCount(mBody.optInt("totalItemCount"));
        mDataResponse = mBody.optJSONArray("response");

        if (mDataResponse != null && mDataResponse.length() > 0) {
            mRootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                        && mBrowseItemList.size() % ConstantVariables.ADV_VIDEO_ADS_POSITION == 0) {
                    switch (ConstantVariables.ADV_VIDEO_ADS_TYPE) {
                        case ConstantVariables.TYPE_FACEBOOK_ADS:
                            NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                            mBrowseItemList.add(ad);
                            break;
                        case ConstantVariables.TYPE_GOOGLE_ADS:
                            if (mAdFetcher.getAdList() != null && !mAdFetcher.getAdList().isEmpty()) {
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
                            if (mAdvertisementsArray != null) {
                                if (j < mAdvertisementsArray.length()) {
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
                int channelId = jsonDataObject.optInt("channel_id");
                int subscribeCount = jsonDataObject.optInt("subscribe_count");
                int likeCount = jsonDataObject.optInt("like_count");
                int viewCount = jsonDataObject.optInt("view_count");
                int videosCount = jsonDataObject.optInt("videos_count");
                int rating_count = jsonDataObject.optInt("rating_count");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                String contentUrl = jsonDataObject.optString("content_url");
                JSONArray mMenuArray = jsonDataObject.optJSONArray("menu");
                mBrowseItemList.add(new BrowseListItems(image, title, likeCount, subscribeCount,
                        viewCount, rating_count, videosCount, channelId, contentUrl, mMenuArray));

            }

        } else {
            mRootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) mRootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) mRootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            //TODO change this.
            errorIcon.setText("\uf233");
            errorMessage.setText(mContext.getResources().getString(R.string.no_channels));
        }

        mManageDataAdapter.notifyDataSetChanged();

    }

    /***
     * Method to add community ads into the list.
     *
     * @param j Position, where ads will be added.
     * @return Returns the Community ads list at the desired position.
     */
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
    public void onStart() {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE);
        switch (resultCode) {
            case ConstantVariables.PAGE_EDIT_CODE:
            case ConstantVariables.VIEW_PAGE_CODE:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        makeRequest();
                    }
                });
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        if (listItems != null) {
            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                    ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE, null);
            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onRefresh() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                makeRequest();
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

            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) < mBrowseList.getmTotalItemCount()) {

                CustomViews.addFooterView(mFooterView);
                mLoadingPageNo = mLoadingPageNo + 1;
                isLoading = true;
                loadMoreData(getChannelManageUrl());
            }

        }
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.ADV_VIDEO_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mManageDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    @Override
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {
        mAdvertisementsArray = advertisementsArray;

        if (!isAdLoaded && mAdvertisementsArray != null) {
            isAdLoaded = true;
            int j = 0;
            for (int i = 0; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.ADV_VIDEO_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mManageDataAdapter.notifyDataSetChanged();
                }
            }
        } else {
            CustomViews.removeFooterView(mFooterView);
            addDataToList(mBody);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPostParams.clear();
        mSelectedItem = position;
        adapter.getCustomView(position, view, parent, mSelectedItem);
        switch (position) {
            case 0:
                mOrderBy = "all";
                break;
            case 1:
                mOrderBy = "like_count";
                break;
            case 2:
                mOrderBy = "favourite_count";
                break;
            case 3:
                mOrderBy = "subscribe_count";
                break;
            case 4:
                mOrderBy = "rating";
                break;
        }
        if (mOrderBy != null && !mOrderBy.isEmpty()) {
            mPostParams.put("orderby", mOrderBy);
        }
        mSwipeRefreshLayout.setRefreshing(true);
        makeRequest();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
