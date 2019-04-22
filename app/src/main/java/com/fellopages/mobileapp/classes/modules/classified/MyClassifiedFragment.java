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

package com.fellopages.mobileapp.classes.modules.classified;

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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyClassifiedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private View rootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mManageDataAdapter;
    private GridLayoutManager mLayoutManager;
    private int pageNumber = 1, mTotalItemCount = 0 ;
    private List<Object> mBrowseItemList;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private BrowseListItems mBrowseList;
    private String mClassifiedUrl;
    private JSONObject mBody;
    private JSONArray mDataResponse, mMenuArray, mAdvertisementsArray;
    private boolean isVisibleToUser = false, isLoading = false, isAdLoaded = false, isCommunityAds = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private int mLoadingPageNo = 1;
    private NativeAdsManager listNativeAdsManager;
    private String mRedirectUrl;
    private AlertDialogWithAction mAlertDialogWithAction;

    public static MyClassifiedFragment newInstance(Bundle bundle) {
        // Required public constructor
        MyClassifiedFragment fragment = new MyClassifiedFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(MyClassifiedFragment.this);

        rootView = inflater.inflate(R.layout.recycler_view_layout,container,false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mManageDataAdapter = new RecyclerViewAdapter(mContext, mBrowseItemList, false, 0,
                ConstantVariables.CLASSIFIED_MENU_TITLE, MyClassifiedFragment.this,
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                        Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                PreferencesUtils.getCurrentSelectedModule(mContext), null);
                        startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

        mRecyclerView.setAdapter(mManageDataAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mManageDataAdapter.getItemViewType(position)) {
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

        if(ConstantVariables.ENABLE_CLASSIFIED_ADS == 1){
            switch (ConstantVariables.CLASSIFIED_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS :
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

        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        mClassifiedUrl = UrlUtil.MANAGE_CLASSIFIED_URL + "&page=" + pageNumber;

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        addListeners();
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(ConstantVariables.CLASSIFIED_ADS_TYPE == ConstantVariables.TYPE_GOOGLE_ADS){
                mAdFetcher = new AdFetcher(mContext);
                mAdFetcher.loadAds(mBrowseItemList,mManageDataAdapter,ConstantVariables.CLASSIFIED_ADS_POSITION);
            }
            makeRequest();
        } else {
            if(snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }

    public void addListeners(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();
                int lastVisibleCount = gridLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = UrlUtil.MANAGE_CLASSIFIED_URL + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        loadMoreData(url);
                    }

                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();

    }

    public void makeRequest() {

        mLoadingPageNo = 1;
        mAppConst.getJsonResponseFromUrl(mClassifiedUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseItemList.clear();
                setItemInTheList(jsonObject);
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.CLASSIFIED_ADS_POSITION,
                            ConstantVariables.CLASSIFIED_ADS_TYPE);
                }
                isVisibleToUser = true;
                mManageDataAdapter.notifyDataSetChanged();
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

    private void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mManageDataAdapter.notifyItemInserted(mBrowseItemList.size() - 1);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.CLASSIFIED_ADS_POSITION,
                            ConstantVariables.CLASSIFIED_ADS_TYPE);
                } else{
                    mBrowseItemList.remove(mBrowseItemList.size() - 1);
                    mManageDataAdapter.notifyItemRemoved(mBrowseItemList.size());
                    setItemInTheList(jsonObject);
                }
                mManageDataAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }
    int j = 0;
    public void setItemInTheList(JSONObject jsonObject){
        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("response");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                        && mBrowseItemList.size() % ConstantVariables.CLASSIFIED_ADS_POSITION == 0) {
                    switch (ConstantVariables.CLASSIFIED_ADS_TYPE){
                        case ConstantVariables.TYPE_FACEBOOK_ADS :
                            NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                            mBrowseItemList.add(ad);
                            break;
                        case ConstantVariables.TYPE_GOOGLE_ADS:
                            if(j < mAdFetcher.getAdList().size()) {
                                NativeAppInstallAd nativeAppInstallAd = (NativeAppInstallAd) mAdFetcher.getAdList().get(j);
                                j++;
                                mBrowseItemList.add(nativeAppInstallAd);
                            }else {
                                j = 0;
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
                int classified_id = jsonDataObject.optInt("classified_id");
                String title = jsonDataObject.optString("title");
                String image_normal = jsonDataObject.optString("image");
                String owner_title = jsonDataObject.optString("owner_title");
                String creation_date = jsonDataObject.optString("creation_date");
                mMenuArray = jsonDataObject.optJSONArray("menu");
                mBrowseItemList.add(new BrowseListItems(image_normal, title, owner_title,
                        creation_date, classified_id, mMenuArray));

            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf022");
            errorMessage.setText(mContext.getResources().getString(R.string.no_classified));
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mRecyclerView != null){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onRefresh() {
        /* Showing Swipe Refresh animation*/
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case ConstantVariables.VIEW_PAGE_CODE:
            case ConstantVariables.PAGE_EDIT_CODE:
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
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.CLASSIFIED_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mManageDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

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

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, rootView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

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

        int j = 0;
        if(!isAdLoaded && mAdvertisementsArray != null){

            isAdLoaded = true;
            for (int i = 0; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.CLASSIFIED_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mManageDataAdapter.notifyDataSetChanged();
                }
            }
        } else if (mBrowseItemList.size() > 0) {
            mBrowseItemList.remove(mBrowseItemList.size() - 1);
            mManageDataAdapter.notifyItemRemoved(mBrowseItemList.size());
            setItemInTheList(mBody);
        }
    }
}
