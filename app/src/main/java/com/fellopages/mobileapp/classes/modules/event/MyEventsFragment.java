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

package com.fellopages.mobileapp.classes.modules.event;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.adapters.ManageDataAdapter;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private String mMyEventsUrl, mCurrentSelectedModule;
    private View rootView, footerView;
    private Context mContext;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private GridViewWithHeaderAndFooter mGridView;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private ManageDataAdapter mManageDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NativeAdsManager listNativeAdsManager;
    private boolean isLoading = false, isVisibleToUser = false, isAdLoaded = false, isCommunityAds = false;
    Snackbar snackbar;

    public static MyEventsFragment newInstance(Bundle bundle) {
        // Required public constructor
        MyEventsFragment fragment = new MyEventsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(MyEventsFragment.this);

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.grid_view_layout, null);
        Log.d("ThisWasLogged ", "MyEventsFragment");
        footerView = CustomViews.getFooterView(inflater);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mGridView = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(this);
        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);

        ViewCompat.setNestedScrollingEnabled(mGridView,true);

        pageNumber = 1;

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        mMyEventsUrl = UrlUtil.MANAGE_EVENT_URL + "&page=" + pageNumber;
        mManageDataAdapter = new ManageDataAdapter(getActivity(), R.layout.list_event_info, mBrowseItemList,
                null, MyEventsFragment.this);
        mGridView.setAdapter(mManageDataAdapter);
        if(ConstantVariables.ENABLE_EVENT_ADS == 1){
            switch (ConstantVariables.EVENT_ADS_TYPE){
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

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(ConstantVariables.EVENT_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS){
                mAdFetcher = new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList,mManageDataAdapter,ConstantVariables.EVENT_ADS_POSITION);
            }
            sendRequestToServer();
        } else {
            if(snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
        String eventTitle = listItems.getmBrowseListTitle();

        Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                PreferencesUtils.getCurrentSelectedModule(mContext), null);
        mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, eventTitle);
        startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void sendRequestToServer() {

        mLoadingPageNo = 1;
        mMyEventsUrl = UrlUtil.MANAGE_EVENT_URL + "&page=" + pageNumber;

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */

        mAppConst.getJsonResponseFromUrl(mMyEventsUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.EVENT_ADS_POSITION, ConstantVariables.EVENT_ADS_TYPE);
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

        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        isAdLoaded = false;
        sendRequestToServer();
        if (listNativeAdsManager != null) {
            listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
        }
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
                String url = mMyEventsUrl = UrlUtil.MANAGE_EVENT_URL + "&page=" + mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }

        }

    }

    /**
     * Load more content on scrolling
     * @param url Url to send request on server
     */

    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.EVENT_ADS_POSITION, ConstantVariables.EVENT_ADS_TYPE);
                }else{
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
     * Add data in List and notify adapter
     * @param jsonObject
     */
    int j=0;
    public void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;
        if (mBody != null) {
            try {
                mTotalItemCount = mBody.getInt("getTotalItemCount");

                mBrowseList.setmTotalItemCount(mTotalItemCount);
                if (mTotalItemCount != 0) {
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                    mDataResponse = mBody.optJSONArray("response");
                    for (int i = 0; i < mDataResponse.length(); i++) {
                        if ((isAdLoaded || AdFetcher.isAdLoaded())  && mBrowseItemList.size() != 0
                                && mBrowseItemList.size() % ConstantVariables.EVENT_ADS_POSITION == 0) {
                            switch (ConstantVariables.EVENT_ADS_TYPE){
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
                        int event_id = jsonDataObject.getInt("event_id");
                        String title = jsonDataObject.getString("title");
                        String location = jsonDataObject.getString("location");
                        String starttime = jsonDataObject.getString("starttime");
                        String image_icon = jsonDataObject.getString("image");
                        String owner_title = jsonDataObject.getString("owner_title");
                        int member_count = jsonDataObject.getInt("member_count");
                        int isJoined = jsonDataObject.optInt("isJoined");
                        mBrowseItemList.add(new BrowseListItems(image_icon, title,
                                owner_title, member_count, event_id,
                                jsonDataObject.getJSONArray("menu"), location,
                                starttime, isJoined));

                    }

                } else {
                    mGridView.setVisibility(View.INVISIBLE);
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                    SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById
                            (R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf073");
                    errorMessage.setText(getActivity().getResources().getString(R.string.no_events));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mGridView.setVisibility(View.INVISIBLE);
            }
            isVisibleToUser = true;
            mManageDataAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Update currentSelected Option on back press*/

        if(requestCode == ConstantVariables.VIEW_PAGE_CODE){
            PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
        }

        switch (resultCode){
            case ConstantVariables.VIEW_PAGE_CODE:
            case ConstantVariables.PAGE_EDIT_CODE:
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        sendRequestToServer();
                    }
                });
                break;
        }

    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.EVENT_ADS_POSITION == 0) {
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

    @Override
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {
        mAdvertisementsArray = advertisementsArray;

        if(!isAdLoaded && mAdvertisementsArray != null){
            isAdLoaded = true;
            int j = 0;
            for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.EVENT_ADS_POSITION == 0 &&
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


