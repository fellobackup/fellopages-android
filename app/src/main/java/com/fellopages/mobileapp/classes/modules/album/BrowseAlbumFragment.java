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
 *
 */

package com.fellopages.mobileapp.classes.modules.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
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
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BrowseAlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseAlbumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private View rootView;
    private Context mContext;
    private List<Object> mBrowseItemList;
    private int  pageNumber = 1, mLoadingPageNo = 1, mTotalItemCount = 0, mUserId;
    private String mBrowseAlbumUrl, mExtraModuleType, mCurrentSelectedModule;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private BrowseListItems mBrowseList;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private boolean isLoading = false, isSearchTextSubmitted = false, isVisibleToUser = false, isFirstTab = false;
    private boolean isMemberAlbums = false, isAdLoaded = false, isSitePageAlbums = false, isSiteGroupAlbums = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseAdapter;
    private HashMap<String, String> searchParams = new HashMap<>();
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;
    private int mContentId;
    private Bundle mBundle;
    private boolean isCommunityAds = false;


    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggle = menu.findItem(R.id.viewToggle);
        if (toggle != null) {
            toggle.setVisible(false);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment BrowseAlbumFragment.
     */
    public static BrowseAlbumFragment newInstance(Bundle bundle) {
        BrowseAlbumFragment fragment = new BrowseAlbumFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public BrowseAlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser && mContext != null) {
            if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_album")){
                PreferencesUtils.updateCurrentModule(mContext, "core_main_album");
                mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
            }
            makeRequest();
        }
        if (!isVisible() && snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(this);

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);

        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.recycler_view_layout, null);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, AppConstant.getNumOfColumns(mContext));
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Updating current selected module
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);

        //Editing the url for browse album

        mBrowseAlbumUrl = UrlUtil.BROWSE_ALBUM_URL;

        if(getArguments() != null) {

            mBundle = getArguments();

            // If The Fragment Being called from User profile page.
            isMemberAlbums = mBundle.getBoolean("isMemberAlbums");
            isSitePageAlbums = mBundle.getBoolean("isSitePageAlbums", false);
            isSiteGroupAlbums = mBundle.getBoolean("isSiteGroupAlbums", false);
            mBrowseAlbumUrl = mBundle.getString(ConstantVariables.URL_STRING, mBrowseAlbumUrl);
            mContentId = mBundle.getInt(ConstantVariables.VIEW_PAGE_ID, 0);
            mExtraModuleType = mBundle.getString(ConstantVariables.EXTRA_MODULE_TYPE);
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
            if (isSitePageAlbums) {
                mExtraModuleType = "sitepage_album";
            } else if (isSiteGroupAlbums) {
                mExtraModuleType = "sitegroup_album";
            }
            mUserId = mBundle.getInt("user_id");

            /**
             * Add Search Params in url when the fragment is not being called from
             * user, page and group profile pages.
             */
            if (!isMemberAlbums && !isSitePageAlbums && !isSiteGroupAlbums && mExtraModuleType == null) {

                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
                    }
                }
            }
        }

        //getting the reference of BrowseDataAdapter class

        mBrowseAdapter = new RecyclerViewAdapter(mContext, mBrowseItemList, true, false, 0,
                ConstantVariables.ALBUM_MENU_TITLE,
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                        boolean isAllowedToView = listItems.isAllowToView();

                        if (!isAllowedToView) {
                            SnackbarUtils.displaySnackbar(rootView,
                                    mContext.getResources().getString(R.string.unauthenticated_view_message));

                        } else {
                            Intent mainIntent;

                            if (mExtraModuleType == null
                                    || (mExtraModuleType != null && mExtraModuleType.equals("userProfile"))) {
                                mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                        mCurrentSelectedModule, null);
                            } else {
                                mainIntent = GlobalFunctions.getIntentForSubModule(mContext, mContentId,
                                        listItems.getmListItemId(), mExtraModuleType);
                            }
                            // For the user profile/cover update.
                            if (mBundle != null && !mBundle.isEmpty() && mBundle.containsKey("isCoverRequest")) {
                                Bundle bundle = mainIntent.getExtras();
                                bundle.putBundle(ConstantVariables.USER_PROFILE_COVER_BUNDLE, mBundle);
                                mainIntent.putExtras(bundle);
                            }
                            if(mainIntent != null) {
                                startActivityForResult(mainIntent, ConstantVariables.ALBUM_VIEW_PAGE);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }

                    }
                });

        mRecyclerView.setAdapter(mBrowseAdapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if(ConstantVariables.ENABLE_ALBUM_ADS == 1) {
            switch (ConstantVariables.ALBUM_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseAdapter,ConstantVariables.ALBUM_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        // Making server call when the album fragment is launched from FragmentLoadActivity.
        if (((!isMemberAlbums && !isSiteGroupAlbums && !isSitePageAlbums)
                || mBundle != null && !mBundle.isEmpty() && mBundle.containsKey("isCoverRequest")) || isFirstTab){
            makeRequest();
        }


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

                int limit=firstVisibleItem+visibleItemCount;

                if(limit==totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)  <
                            mBrowseList.getmTotalItemCount()){

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String albumsListingUrl = getBrowseAlbumUrl() + "&page=" + mLoadingPageNo;

                        isLoading = true;
                        loadMoreData(albumsListingUrl);
                    }

                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    public void makeRequest() {

        mLoadingPageNo = 1;
        String albumsListingUrl = getBrowseAlbumUrl() + "&page=" + pageNumber;

        if (!isSearchTextSubmitted && !isMemberAlbums && !isSitePageAlbums && !isSiteGroupAlbums
                && mExtraModuleType == null) {

            try {

                // Don't show data in case of searching and User Profile Tabs.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.ALBUM_FILE);
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
                    mBrowseAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mAppConst.getJsonResponseFromUrl(albumsListingUrl, new OnResponseListener() {
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
                    mAppConst.getCommunityAds(ConstantVariables.ALBUM_ADS_POSITION,
                            ConstantVariables.ALBUM_ADS_TYPE);
                }
                // Don't save data in case of searching and User Profile Tabs.
                if(!isSearchTextSubmitted && !isMemberAlbums && !isSitePageAlbums && !isSiteGroupAlbums
                        && mExtraModuleType == null) {
                    DataStorage.createTempFile(mContext,DataStorage.ALBUM_FILE, jsonObject.toString());
                }
                mBrowseAdapter.notifyDataSetChanged();
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

    private  void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.ALBUM_ADS_POSITION,
                            ConstantVariables.ALBUM_ADS_TYPE);
                } else {
                    // remove progress item
                    mBrowseItemList.remove(mBrowseItemList.size() - 1);
                    mBrowseAdapter.notifyItemRemoved(mBrowseItemList.size());
                    addDataToList(jsonObject);
                }
                mBrowseAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
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
                        && mBrowseItemList.size() % ConstantVariables.ALBUM_ADS_POSITION == 0) {
                    switch (ConstantVariables.ALBUM_ADS_TYPE){
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
                int album_id = jsonDataObject.optInt("album_id");
                int comment_count = jsonDataObject.optInt("comment_count");
                int photo_count = jsonDataObject.optInt("photo_count");
                int view_count = jsonDataObject.optInt("view_count");
                int like_count = jsonDataObject.optInt("like_count");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                String owner_title = jsonDataObject.optString("owner_title");
                int allow_to_view = jsonDataObject.optInt("allow_to_view");
                int searchable = jsonDataObject.optInt("search");
                if (searchable == 1) {
                    if (allow_to_view == 1) {
                        mBrowseItemList.add(new BrowseListItems(image, title, owner_title,
                                photo_count, comment_count, view_count, like_count, album_id, true));
                    } else {
                        mBrowseItemList.add(new BrowseListItems(image, title, owner_title,
                                photo_count, comment_count, view_count, like_count, album_id, false));
                    }
                }
            }
            // Show End of Result Message when there are less results
            if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                 mBrowseItemList.add(ConstantVariables.FOOTER_TYPE);
            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf1c5");
            errorMessage.setText(mContext.getResources().getString(R.string.no_albums));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case ConstantVariables.ALBUM_VIEW_PAGE:
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

    private String getBrowseAlbumUrl(){

        String albumsListingUrl = mBrowseAlbumUrl + "?limit=" + AppConstant.LIMIT;

        // Add user_id in the url if the fragment is being called from user profile page.
        if(mUserId != 0){
            albumsListingUrl += "&user_id=" + mUserId;
        }
        // Adding Search Params in the url if the fragment is being called from search page.
        if(searchParams != null && searchParams.size() != 0){
            isSearchTextSubmitted = true;
            albumsListingUrl = mAppConst.buildQueryString(albumsListingUrl, searchParams);
        }

        return albumsListingUrl;
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.ALBUM_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mBrowseAdapter.notifyDataSetChanged();
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
                if (i != 0 && i % ConstantVariables.POLL_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mBrowseAdapter.notifyDataSetChanged();
                }
            }
        } else if (mBrowseItemList.size() > 0) {
            mBrowseItemList.remove(mBrowseItemList.size() - 1);
            mBrowseAdapter.notifyItemRemoved(mBrowseItemList.size());
            addDataToList(mBody);
        }

    }
}
