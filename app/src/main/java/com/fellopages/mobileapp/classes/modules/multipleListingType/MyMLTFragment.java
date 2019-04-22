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

package com.fellopages.mobileapp.classes.modules.multipleListingType;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GridSpacingItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyMLTFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private View mRootView;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private Context mContext;
    private TextView errorIcon;
    private SelectableTextView errorMessage;
    private LinearLayout messageLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mManageMLTAdapter;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private boolean isLoading = false , isVisibleToUser = false, isAdLoaded = false, isCommunityAds = false;
    private int mLoadingPageNo = 1, NUM_OF_COLUMNS;
    private String mManageMLTUrl, mListingLabel, mListingIcon;
    private int mListingTypeId, mMLTBrowseType;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NativeAdsManager listNativeAdsManager;
    private String mRedirectUrl;
    private AlertDialogWithAction mAlertDialogWithAction;
    private JSONArray mAdvertisementsArray;
    private JSONObject mBody;

    public static MyMLTFragment newInstance(Bundle bundle){
        MyMLTFragment fragment = new MyMLTFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(ConstantVariables.MLT_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS){
                mAdFetcher =  new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList,mManageMLTAdapter,ConstantVariables.MLT_ADS_POSITION);
            }
            makeRequest();
        } else {
            if(snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAppConst = new AppConstant(getActivity());
        mAppConst.setOnCommunityAdsLoadedListener(this);
        mContext = getContext();
        NUM_OF_COLUMNS = AppConstant.getNumOfColumns(mContext);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        // Updating current selected module
        if(PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                !PreferencesUtils.getCurrentSelectedModule(mContext).equals("sitereview_listing")){
            PreferencesUtils.updateCurrentModule(mContext,"sitereview_listing");
        }

        // Getting current Listing variables from preferences.
        mListingTypeId = PreferencesUtils.getCurrentSelectedListingId(mContext);
        mListingLabel = PreferencesUtils.getCurrentSelectedListingSingularLabel(mContext, mListingTypeId);
        mMLTBrowseType = PreferencesUtils.getCurrentSelectedListingBrowseType(mContext, mListingTypeId);
        mListingIcon = PreferencesUtils.getCurrentSelectedListingIcon(mContext, mListingTypeId);

        // setting up the listing icon.
        if (mListingIcon != null && !mListingIcon.isEmpty()) {
            try {
                mListingIcon = new String(Character.toChars(Integer.parseInt(mListingIcon, 16)));
            } catch (NumberFormatException e) {
                mListingIcon = GlobalFunctions.getItemIcon(PreferencesUtils.getCurrentSelectedModule(mContext));
            }
        } else {
            mListingIcon = GlobalFunctions.getItemIcon(PreferencesUtils.getCurrentSelectedModule(mContext));
        }

        mManageMLTUrl = UrlUtil.MANAGE_MLT_URL + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo;

        // Inflating recycler layout.
        mRootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // No data message views
        messageLayout = mRootView.findViewById(R.id.message_layout);
        errorIcon = mRootView.findViewById(R.id.error_icon);
        errorMessage = mRootView.findViewById(R.id.error_message);
        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));


        // Initializing layout manager and adapter according to the browse view type.
        if (mMLTBrowseType != 0)
            setLayoutManager(mMLTBrowseType);
        else
            setLayoutManager(2);

        swipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        // Showing adds if ADS enabled for MLT.
        if(ConstantVariables.ENABLE_MLT_ADS == 1) {
            switch (ConstantVariables.MLT_ADS_TYPE){
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
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    /**
     * Method to send request to server to get manage page data.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mManageMLTUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseItemList.clear();
                addItemsToList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.MLT_ADS_POSITION,
                            ConstantVariables.MLT_ADS_TYPE);
                }
                isVisibleToUser = true;
                mManageMLTAdapter.notifyDataSetChanged();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), mRootView, message,
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
     *Method to load more data(if exists) on scrolling.
     *
     * @param url Url to load next page data
     */
    private void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mManageMLTAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.MLT_ADS_POSITION,
                            ConstantVariables.MLT_ADS_TYPE);
                } else{
                    //   remove progress item
                    mBrowseItemList.remove(mBrowseItemList.size() - 1);
                    mManageMLTAdapter.notifyItemRemoved(mBrowseItemList.size());
                    addItemsToList(jsonObject);
                }
                mManageMLTAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });
    }

    /**
     *Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response
     */
    int j = 0;
    public void addItemsToList(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        int mTotalItemCount = jsonObject.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        JSONArray mDataResponse = jsonObject.optJSONArray("listings");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            messageLayout.setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                        && mBrowseItemList.size() % ConstantVariables.MLT_ADS_POSITION == 0) {
                    switch (ConstantVariables.MLT_ADS_TYPE){
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
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int listingId = jsonDataObject.optInt("listing_id");
                int listingTypeId = jsonDataObject.optInt("listingtype_id");
                String title = jsonDataObject.optString("title");
                String body = jsonDataObject.optString("body");
                String image = jsonDataObject.optString("image");
                String ownerTitle = jsonDataObject.optString("owner_title");
                String creationDate = jsonDataObject.optString("creation_date");
                String location = jsonDataObject.optString("location");
                String price = jsonDataObject.optString("price");
                String currency = jsonDataObject.optString("currency");
                int viewCount = jsonDataObject.optInt("view_count");
                int reviewCount = jsonDataObject.optInt("review_count");
                int commentCount = jsonDataObject.optInt("comment_count");
                int likeCount = jsonDataObject.optInt("like_count");
                int allowToView = jsonDataObject.optInt("allow_to_view");
                int isClosed = jsonDataObject.optInt("closed");
                JSONArray menuArray = jsonDataObject.optJSONArray("gutterMenus");

                mBrowseItemList.add(new BrowseListItems(listingId, listingTypeId, title, body, image, ownerTitle, creationDate,
                        location, price, currency, viewCount, reviewCount, commentCount, likeCount, allowToView == 1, isClosed, menuArray));
            }
        }else {
            messageLayout.setVisibility(View.VISIBLE);
            errorIcon.setText(mListingIcon);
            errorMessage.setText(mContext.getResources().getString(R.string.no_text) + " " + mListingLabel.toLowerCase() + " " +
                    mContext.getResources().getString(R.string.available_text));
        }
    }

    /**
     * Method to set layout manager according to type.
     * @param mMLTBrowseType type of view to show MLT.
     */
    public void setLayoutManager(final int mMLTBrowseType) {
        try {
            switch (mMLTBrowseType) {
                case ConstantVariables.GRID_VIEW:
                    GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), NUM_OF_COLUMNS);
                    if (NUM_OF_COLUMNS > 1) {
                        mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                                R.dimen.loading_bar_height, mRecyclerView, true));
                    } else {
                        mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                                R.dimen.margin_2dp, mRecyclerView, false));
                    }
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    break;

                case ConstantVariables.MATRIX_VIEW:
                    mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (mManageMLTAdapter.getItemViewType(position)) {
                                case RecyclerViewAdapter.VIEW_ITEM:
                                case RecyclerViewAdapter.TYPE_FB_AD:
                                case RecyclerViewAdapter.TYPE_COMMUNITY_ADS:
                                case RecyclerViewAdapter.REMOVE_COMMUNITY_ADS:
                                    return 1;
                                case RecyclerViewAdapter.VIEW_PROG:
                                    return 2; //number of columns of the grid
                                default:
                                    return -1;
                            }
                        }
                    });
                    break;

                case ConstantVariables.LIST_VIEW:
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
                    break;
            }

            mManageMLTAdapter = new RecyclerViewAdapter(getActivity(), mBrowseItemList, false, mMLTBrowseType,
                    ConstantVariables.MLT_MENU_TITLE, MyMLTFragment.this,
                    new OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                    PreferencesUtils.getCurrentSelectedModule(mContext), null);
                            mainIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, mListingTypeId);
                            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
            mRecyclerView.setAdapter(mManageMLTAdapter);
            addScrollListener(mMLTBrowseType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to add Scroll listener according to view type.
     * @param mMLTViewType type of view to show MLT.
     */
    public void addScrollListener(final int mMLTViewType) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = 0, totalItemCount = 0, lastVisibleCount, visibleItemCount = 0;

                switch (mMLTViewType) {
                    case ConstantVariables.LIST_VIEW:
                        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView
                                .getLayoutManager();
                        firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleCount = linearLayoutManager.findLastVisibleItemPosition() + 1;
                        visibleItemCount = lastVisibleCount - firstVisibleItem;
                        break;
                    case ConstantVariables.GRID_VIEW:
                    case ConstantVariables.MATRIX_VIEW:
                        final GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView
                                .getLayoutManager();
                        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        totalItemCount = layoutManager.getItemCount();
                        lastVisibleCount = layoutManager.findLastVisibleItemPosition() + 1;
                        visibleItemCount = lastVisibleCount - firstVisibleItem;
                        break;
                }

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = UrlUtil.MANAGE_MLT_URL + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo;

                        isLoading = true;
                        loadMoreData(url);
                    }
                }
            }
        });
    }

    public void checkStoragePermission(String redirectUrl){
        mRedirectUrl = redirectUrl;
        if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
        }else{
            startImageUploading();
        }
    }

    private void startImageUploading(){

        Intent uploadPhoto = new Intent(mContext, MultiMediaSelectorActivity.class);
        // Selection type photo to display items in grid
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
        // Whether photoshoot
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // The maximum number of selectable image
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT,
                ConstantVariables.FILE_UPLOAD_LIMIT);
        // Select mode
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE,
                MultiMediaSelectorActivity.MODE_MULTI);
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_URL, mRedirectUrl);

        ((Activity) mContext).startActivityForResult(uploadPhoto,
                ConstantVariables.REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantVariables.VIEW_PAGE_CODE:
                PreferencesUtils.updateCurrentModule(mContext,"sitereview_listing");
                if(resultCode == ConstantVariables.VIEW_PAGE_CODE){
                    makeRequest();
                }
                break;

            case ConstantVariables.PAGE_EDIT_CODE:
                if(resultCode == ConstantVariables.PAGE_EDIT_CODE){
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            makeRequest();
                        }
                    });
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    startImageUploading();
                } else {
                    // If user deny the permission popup
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mRootView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;
        }
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.MLT_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mManageMLTAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mRecyclerView != null){
            mRecyclerView.smoothScrollToPosition(0);
        }
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
                if (i != 0 && i % ConstantVariables.MLT_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mManageMLTAdapter.notifyDataSetChanged();
                }
            }
        } else if (!(mBrowseItemList.size() < AppConstant.LIMIT)) {
            addItemsToList(mBody);
        }
    }
}
