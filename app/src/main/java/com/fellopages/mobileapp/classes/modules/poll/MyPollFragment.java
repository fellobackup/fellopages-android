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
import com.fellopages.mobileapp.classes.common.adapters.ManageDataAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyPollFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private Boolean isVisibleToUser = false, isLoading = false, isCommunityAds = false;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private View footerView, rootView;
    private ListView mListView;
    private String mManagePollUrl;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1, listSize = 0;
    private ManageDataAdapter mManageDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private Context mContext;
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;
    private boolean isAdLoaded = false;

    public MyPollFragment() {
        // Required empty public constructor
    }

    public static MyPollFragment newInstance(Bundle bundle){
        MyPollFragment fragment = new MyPollFragment();
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
        mAppConst = new AppConstant(getActivity());
        mAppConst.setOnCommunityAdsLoadedListener(MyPollFragment.this);

        rootView = inflater.inflate(R.layout.list_view_layout, container,false);
        mListView = (ListView) rootView.findViewById(R.id.list_item_view);
        footerView = CustomViews.getFooterView(inflater);

        ViewCompat.setNestedScrollingEnabled(mListView,true);

        mManagePollUrl = UrlUtil.MANAGE_POLL_URL + "&page=" + pageNumber;

        mManageDataAdapter = new ManageDataAdapter(getActivity(), R.layout.list_row, mBrowseItemList,
                null, MyPollFragment.this);
        mListView.setAdapter(mManageDataAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_POLL_ADS == 1){
            switch (ConstantVariables.POLL_ADS_TYPE){
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

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(ConstantVariables.POLL_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS){
                mAdFetcher = new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList,mManageDataAdapter,ConstantVariables.POLL_ADS_POSITION);
            }
            getData();
        } else {
            if(snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
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

            case ConstantVariables.PAGE_EDIT_CODE:
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

        mAppConst.getJsonResponseFromUrl(mManagePollUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.POLL_ADS_POSITION, ConstantVariables.POLL_ADS_TYPE);
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
                                    getData();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }

            }
        });


    }

    int j =0;
    private void addDataToList(JSONObject jsonObject) {
        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("response");

        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                listSize = mDataResponse.length();
                for (int i = 0; i < listSize; i++) {
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
                    int pollId = jsonObj.optInt("poll_id");
                    String pollTitle = jsonObj.optString("title");
                    String creationDate = jsonObj.optString("creation_date");
                    String viewCount = jsonObj.optString("view_count");
                    String voteCount = jsonObj.optString("vote_count");
                    int closed = jsonObj.optInt("closed");
                    String ownerImage = jsonObj.optString("owner_image");
                    String ownerTitle = jsonObj.optString("owner_title");
                    //Adding data to list
                        mBrowseItemList.add(new BrowseListItems(ownerImage, pollTitle,
                                ownerTitle, creationDate, voteCount, viewCount, closed, pollId,
                                jsonObj.optJSONArray("menu"), listSize));
                }

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf080");
            errorMessage.setText(mContext.getResources().getString(R.string.no_polls));

        }
        mManageDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

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
        /*** Showing Swipe Refresh animation on activity create
             * As animation won't start on onCreate, post runnable is used
             */
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
        if(listItems != null) {
            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                    ConstantVariables.POLL_MENU_TITLE, null);
            getActivity().startActivityForResult(mainIntent, ConstantVariables.VIEW_POLL_REQUEST_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {

        int limit = firstVisibleItem+visibleItemCount;

        if(limit == totalItemCount && !isLoading) {

            if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()){

                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = UrlUtil.MANAGE_POLL_URL + "&page= " + mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }

        }
    }

    /**
     * Load More data on scrolling
     * @param url Url to send request on server
     */
    private void loadMoreData(String url) {

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mBody = jsonObject;
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
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.POLL_ADS_POSITION == 0) {
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
                if (i != 0 && i % ConstantVariables.POLL_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mManageDataAdapter.notifyDataSetChanged();
                }
            }
        } else{
            CustomViews.removeFooterView(mListView, footerView);
            addDataToList(mBody);
        }
    }
}


