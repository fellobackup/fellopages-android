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

package com.fellopages.mobileapp.classes.modules.music;

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
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseMusicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private View rootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mBrowseAdapter;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray;
    private int mTotalItemCount = 0, mLoadingPageNo = 1, mUserId;
    private String mBrowseMusicUrl, mCurrentSelectedModule;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false, isSearchTextSubmitted = false, isMemberMusics = false,
            isAdLoaded = false, isVisibleToUser = false, isCommunityAds = false, isFirstTab = false;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private HashMap<String, String> searchParams = new HashMap<>();
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;

    public static BrowseMusicFragment newInstance(Bundle bundle) {
        // Required public constructor
        BrowseMusicFragment fragment = new BrowseMusicFragment();
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
        searchParams = new HashMap<>();
        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(BrowseMusicFragment.this);

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_music")){
            PreferencesUtils.updateCurrentModule(mContext, "core_main_music");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBrowseMusicUrl = UrlUtil.BROWSE_MUSIC_URL;


        mBrowseAdapter = new RecyclerViewAdapter(getActivity(), mBrowseItemList, true, false, 0,
                ConstantVariables.MUSIC_MENU_TITLE,
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                        boolean isAllowedToView = listItems.isAllowToView();

                        if (!isAllowedToView) {
                            SnackbarUtils.displaySnackbar(rootView,
                                    mContext.getResources().getString(R.string.unauthenticated_view_message));

                        } else {

                            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                    mCurrentSelectedModule, null);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });

        mRecyclerView.setAdapter(mBrowseAdapter);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mBrowseAdapter.getItemViewType(position)) {
                    case RecyclerViewAdapter.TYPE_ADMOB:
                    case RecyclerViewAdapter.TYPE_FB_AD:
                    case RecyclerViewAdapter.VIEW_ITEM:
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

        /*
            Get the search arguments and add them in Urls.
         */
        if(getArguments() != null) {

            Bundle bundle = getArguments();

            // If The Fragment Being called from User profile page.
            isMemberMusics = bundle.getBoolean("isMemberMusics");
            mUserId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

            if(!isMemberMusics){

                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if(value != null && !value.isEmpty()){
                        searchParams.put(key, value);
                    }
                }

                if(searchParams != null && searchParams.size() != 0){
                    isSearchTextSubmitted = true;
                    mBrowseMusicUrl = mAppConst.buildQueryString(mBrowseMusicUrl, searchParams);
                }
            }
        }

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_MUSIC_ADS == 1) {
            switch (ConstantVariables.MUSIC_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseAdapter,ConstantVariables.MUSIC_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        if (!isMemberMusics || isFirstTab) {
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

                int limit = firstVisibleItem + visibleItemCount;
                if(limit == totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                            mBrowseList.getmTotalItemCount()){

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String browseMusicUrl = getBrowseMusicUrl() + "&page=" + mLoadingPageNo;
                        isLoading = true;

                        loadMoreData(browseMusicUrl);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mRecyclerView != null){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void makeRequest() {

        mLoadingPageNo = 1;

        String browseMusicUrl = getBrowseMusicUrl() + "&page=" + mLoadingPageNo;

        if (!isSearchTextSubmitted && !isMemberMusics) {

            try {
                // Don't show data in case of searching and User Profile Tabs.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.MUSIC_FILE);
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

        mAppConst.getJsonResponseFromUrl(browseMusicUrl, new OnResponseListener() {
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
                    mAppConst.getCommunityAds(ConstantVariables.MUSIC_ADS_POSITION, ConstantVariables.MUSIC_ADS_TYPE);
                }
                mBrowseAdapter.notifyDataSetChanged();

                // Don't save data in case of searching and User Profile Tabs.
                if (!isSearchTextSubmitted && !isMemberMusics) {
                    DataStorage.createTempFile(mContext, DataStorage.MUSIC_FILE, jsonObject.toString());
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
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mBrowseItemList.size() - 1);
            mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    //   remove progress item
                    if(isCommunityAds){
                        mAppConst.getCommunityAds(ConstantVariables.MUSIC_ADS_POSITION,
                                ConstantVariables.MUSIC_ADS_TYPE);
                    } else {
                        mBrowseItemList.remove(mBrowseItemList.size() - 1);
                        mBrowseAdapter.notifyItemRemoved(mBrowseItemList.size());
                        addDataToList(jsonObject);
                    }
                    mBrowseAdapter.notifyItemInserted(mBrowseItemList.size());
                    isLoading = false;

                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                }
            });

    }

    int j = 0;
    public void addDataToList(JSONObject jsonObject){
        try {
            mBody = jsonObject;
            mTotalItemCount = mBody.optInt("totalItemCount");
            mBrowseList.setmTotalItemCount(mTotalItemCount);
            mDataResponse = mBody.optJSONArray("response");
            if (mDataResponse != null && mDataResponse.length() > 0) {
                rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                for (int i = 0; i < mDataResponse.length(); i++) {
                    if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                            && mBrowseItemList.size() % ConstantVariables.MUSIC_ADS_POSITION == 0) {
                        switch (ConstantVariables.MUSIC_ADS_TYPE){
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
                    int item_id = jsonDataObject.optInt("playlist_id");
                    int album_play_count = jsonDataObject.optInt("play_count");
                    String album_title = jsonDataObject.optString("title");
                    String description = jsonDataObject.optString("description");
                    String image_icon = jsonDataObject.optString("owner_image");
                    String image_profile = jsonDataObject.optString("image");
                    String owner_title = jsonDataObject.optString("owner_title");
                    String creation_date = jsonDataObject.optString("creation_date");
                    JSONArray playlist_songs = jsonDataObject.optJSONArray("playlist_songs");
                    int size = 0;
                    if(playlist_songs != null)
                       size = playlist_songs.length();
                    int searchable = jsonDataObject.optInt("search");
                    if (searchable == 1) {
                        mBrowseItemList.add(new BrowseListItems(image_icon,
                                image_profile, owner_title, album_title, size, album_play_count,
                                creation_date, item_id, description, true));
                    }

                }
                // Show End of Result Message when there are less results
                if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                    mBrowseItemList.add(ConstantVariables.FOOTER_TYPE);
                }
            } else {
                rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
                errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                errorIcon.setText("\uf001");
                errorMessage.setText(mContext.getResources().getString(R.string.no_music));
            }
        }catch (Exception e){
            e.printStackTrace();
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
        }});
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
                            makeRequest();
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
            if (i != 0 && i % ConstantVariables.MUSIC_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mBrowseAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    private String getBrowseMusicUrl(){

        String url = mBrowseMusicUrl;

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
                if (i != 0 && i % ConstantVariables.MUSIC_ADS_POSITION == 0 &&
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


