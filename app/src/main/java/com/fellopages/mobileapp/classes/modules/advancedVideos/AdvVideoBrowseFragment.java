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

package com.fellopages.mobileapp.classes.modules.advancedVideos;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.adapters.BrowseDataAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SlideShowAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SlideShowListItems;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.ui.CircleIndicator;

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
public class AdvVideoBrowseFragment extends Fragment implements  AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener, AdapterView.OnItemSelectedListener {

    // Member variables.
    private Context mContext;
    private View mRootView, mFooterView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridViewWithHeaderAndFooter mGridView;
    private Snackbar mSnackbar;
    private TextView tvErrorIcon;
    private SelectableTextView tvErrorMessage;
    private LinearLayout llErrorMessage, categoryFilterBlock, mSlideShowLayout;
    private ViewPager mSlideShowPager;
    private CircleIndicator mCircleIndicator;
    private CardView subCategoryLayout, subSubCategoryLayout;
    private List<Object> mBrowseItemList;
    private List<SlideShowListItems> mSlideShowItemList;
    private HashMap<String, String> mSearchParams, mUrlParams, postParams;
    private JSONObject mBody;
    private JSONArray mDataResponse, mAdvertisementsArray, mSubCategoryResponse = null, mSubSubCategoryResponse = null;
    private String mBrowseVideoUrl, mCurrentSelectedModule, mOrderBy = "", mSubCategoryId, mSubSubCategoryId;
    private int mLoadingPageNo = 1, mTotalItemCount = 0, mCategoryId,
            mFeaturedCount = 0, mSelectedItem = -1, mSubsubcategorySelectedItem = -1;
    private boolean isLoading = false, isSearchTextSubmitted = false, isProfilePageVideos = false,
            isAdLoaded = false, isCommunityAds = false, isVisibleToUser = false, isCategoryVideos = false,
            isFirstRequest = true, isLoadSubCategory = true, isLoadSubSubcategory = true, isFirstTab = false;
    private AppConstant mAppConst;
    private BrowseListItems mBrowseList;
    private SlideShowAdapter mSlideShowAdapter;
    private BrowseDataAdapter mBrowseDataAdapter;
    private SpinnerAdapter adapter, subCategoryAdapter, subSubCategoryAdapter;
    private NativeAdsManager listNativeAdsManager;
    private AdFetcher mAdFetcher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser && mContext != null) {
            makeRequest();
        }
        if (!isVisible() && mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    public static AdvVideoBrowseFragment newInstance(Bundle bundle) {
        // Required public constructor
        AdvVideoBrowseFragment fragment = new AdvVideoBrowseFragment();
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

        mSearchParams = new HashMap<>();
        mUrlParams = new HashMap<>();
        postParams = new HashMap<>();
        mBrowseItemList = new ArrayList<>();
        mSlideShowItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.grid_view_layout, null);
        mFooterView = CustomViews.getFooterView(inflater);
        mSlideShowLayout = (LinearLayout) inflater.inflate(R.layout.slide_show_header, container, false);
        getViews();

        PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.ADV_VIDEO_MENU_TITLE);
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);

        //Getting the reference of BrowseDataAdapter class reschedule my
        mBrowseDataAdapter = new BrowseDataAdapter(mContext, R.layout.album_item_view, mBrowseItemList);

        //Setting the adapter for the grid view.
        mGridView.setAdapter(mBrowseDataAdapter);

        //Editing the url for browse Vidoes
        mBrowseVideoUrl = UrlUtil.ADV_VIDEO_BROWSE_URL;
        mUrlParams.put("limit", String.valueOf(AppConstant.LIMIT));

        /**
         * Getting Search Arguments and them in url
         */
        if (getArguments() != null) {
            mSlideShowLayout.setVisibility(View.GONE);

            Bundle bundle = getArguments();

            // If The Fragment Being called from profile page.
            isProfilePageVideos = bundle.getBoolean("isProfilePageRequest");
            isCategoryVideos = bundle.getBoolean(ConstantVariables.IS_CATEGORY_BASED_RESULTS, false);
            mCategoryId = bundle.getInt(ConstantVariables.VIEW_PAGE_ID, 0);
            postParams.put("category_id", String.valueOf(mCategoryId));
            String contentBrowseUrl = bundle.getString(ConstantVariables.URL_STRING);
            int userId = bundle.getInt("user_id");
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

            if (isProfilePageVideos && contentBrowseUrl != null && !contentBrowseUrl.isEmpty()) {
                mBrowseVideoUrl = contentBrowseUrl;
            }

            if (userId != 0) {
                mUrlParams.put("user_id", String.valueOf(userId));
            }

            if (!isProfilePageVideos && !isCategoryVideos) {

                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if (value != null && !value.isEmpty()) {
                        mSearchParams.put(key, value);
                    }
                }
            }

        } else {
            mSlideShowAdapter = new SlideShowAdapter(mContext, R.layout.list_item_slide_show,
                    mSlideShowItemList, new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    SlideShowListItems listItems = mSlideShowItemList.get(position);
                    Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                            mCurrentSelectedModule, null);
                    startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            mSlideShowPager.setAdapter(mSlideShowAdapter);
            sendRequestForFeaturedContent();
        }

        /**
         * Show Order by Spinner when fragment is loaded from dashboard.
         */
        if (!isCategoryVideos) {
            Spinner orderBySpinner = mRootView.findViewById(R.id.filter_view);
            mRootView.findViewById(R.id.eventFilterBlock).setVisibility(View.VISIBLE);

            adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);

            /* Add Videos filter type to spinner using adapter */
            adapter.add(mContext.getResources().getString(R.string.browse_event_filter_sell_all));
            adapter.add(mContext.getResources().getString(R.string.adv_group_filter_most_recent));
            adapter.add(mContext.getResources().getString(R.string.adv_group_filter_most_liked));
            adapter.add(mContext.getResources().getString(R.string.adv_group_filter_most_viewed));
            adapter.add(mContext.getResources().getString(R.string.adv_group_filter_most_commented));
            adapter.add(mContext.getResources().getString(R.string.adv_group_filter_most_rated));
            adapter.add(mContext.getResources().getString(R.string.adv_video_browse_filter_most_favoured));
            adapter.add(mContext.getResources().getString(R.string.featured));
            adapter.add(mContext.getResources().getString(R.string.sponsored));
            adapter.add(mContext.getResources().getString(R.string.adv_video_browse_filter_best_video));

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            orderBySpinner.setAdapter(adapter);
            orderBySpinner.setSelection(0, false);
            orderBySpinner.setOnItemSelectedListener(this);
            orderBySpinner.setTag("orderby");
        } else {

            mRootView.findViewById(R.id.eventFilterBlock).setVisibility(View.GONE);

            categoryFilterBlock = mRootView.findViewById(R.id.category_filter_block);
            categoryFilterBlock.setVisibility(View.VISIBLE);
            categoryFilterBlock.findViewById(R.id.toolbar).setVisibility(View.GONE);

            subCategoryLayout = mRootView.findViewById(R.id.categoryFilterLayout);
            subSubCategoryLayout = mRootView.findViewById(R.id.subCategoryFilterLayout);
            Spinner subCategorySpinner = subCategoryLayout.findViewById(R.id.filter_view);
            Spinner subSubCategorySpinner = subSubCategoryLayout.findViewById(R.id.filter_view);

            /*
            Add swipeRefreshLayout layout below category filter
             */
            RelativeLayout.LayoutParams layoutParams = CustomViews.getFullWidthRelativeLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, R.id.category_filter_block);
            mSwipeRefreshLayout.setLayoutParams(layoutParams);

            subCategoryAdapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);
            subCategoryAdapter.add(getResources().getString(R.string.select_sub_category_text));
            subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategorySpinner.setAdapter(subCategoryAdapter);
            subCategorySpinner.setSelection(0, false);
            subCategorySpinner.setOnItemSelectedListener(this);
            subCategorySpinner.setTag("subCategory");

            subSubCategoryAdapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSubsubcategorySelectedItem);
            subSubCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subSubCategorySpinner.setAdapter(subSubCategoryAdapter);
            subSubCategorySpinner.setSelection(0, false);
            subSubCategorySpinner.setOnItemSelectedListener(this);
            subSubCategorySpinner.setTag("subSubCategory");
        }

        // Hide Filter View if Fragment is being called from user profile page or search page
        if ((mSearchParams != null && mSearchParams.size() != 0) || isProfilePageVideos) {
            mRootView.findViewById(R.id.eventFilterBlock).setVisibility(View.GONE);
            if(categoryFilterBlock != null){
                categoryFilterBlock.setVisibility(View.GONE);
            }
        }

        if (ConstantVariables.ENABLE_ADV_VIDEO_ADS == 1) {
            switch (ConstantVariables.ADV_VIDEO_ADS_TYPE) {

                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList, mBrowseDataAdapter, ConstantVariables.ADV_VIDEO_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        if (!isProfilePageVideos || mBrowseVideoUrl.contains("channel") || isFirstTab) {
            makeRequest();
        }

        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(this);
        return mRootView;
    }

    /**
     * Method to get the views.
     */
    private void getViews() {
        mGridView = mRootView.findViewById(R.id.gridView);
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSlideShowPager = mSlideShowLayout.findViewById(R.id.slide_show_pager);
        mCircleIndicator = mSlideShowLayout.findViewById(R.id.circle_indicator);
        mRootView.findViewById(R.id.eventFilterBlock).setVisibility(View.VISIBLE);

        // No data message views
        llErrorMessage = mRootView.findViewById(R.id.message_layout);
        tvErrorIcon = mRootView.findViewById(R.id.error_icon);
        tvErrorMessage = mRootView.findViewById(R.id.error_message);
        tvErrorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        // Adding header and footer view.
        mGridView.addHeaderView(mSlideShowLayout);
        mGridView.addFooterView(mFooterView);
        mFooterView.setVisibility(View.GONE);

        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);
        ViewCompat.setNestedScrollingEnabled(mGridView, true);
    }

    /**
     * Method to build the browse video page url with the required params.
     *
     * @return Returns the updated browse video page url.
     */
    private String getBrowseVideosUrl() {

        String url = mAppConst.buildQueryString(mBrowseVideoUrl, mUrlParams);

        // Add search params in url if fragment is being called from search page.
        if (mSearchParams != null && mSearchParams.size() != 0) {
            isSearchTextSubmitted = true;
            url = mAppConst.buildQueryString(url, mSearchParams);
        }
        if (mOrderBy != null && !mOrderBy.isEmpty()) {
            url += "&orderby=" + mOrderBy;
        }
        if (isCategoryVideos) {
            url = UrlUtil.BROWSE_ADV_VIDEO_CATEGORIES + "?category_id=" + mCategoryId;
            url = mAppConst.buildQueryString(url, mUrlParams);
        }
        return url;
    }

    /**
     * Method to make server call to get the featured content.
     */
    private void sendRequestForFeaturedContent() {
        String url = UrlUtil.ADV_VIDEO_BROWSE_URL + "?limit=" + AppConstant.FEATURED_CONTENT_LIMIT +
                "&orderby=featured&page=1";

        mSlideShowItemList.clear();
        String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.ADV_VIDEO_FEATURED_CONTENT);
        if (tempData != null) {
            try {
                JSONObject jsonObject = new JSONObject(tempData);
                addDataToFeaturedList(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mSlideShowItemList.clear();
                addDataToFeaturedList(jsonObject);
                DataStorage.createTempFile(mContext, DataStorage.ADV_VIDEO_FEATURED_CONTENT, jsonObject.toString());
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    /**
     * Method to add the featured content into list.
     *
     * @param featuredObject Object which contains the featured content.
     */
    private void addDataToFeaturedList(JSONObject featuredObject) {

        if (featuredObject != null) {
            mDataResponse = featuredObject.optJSONArray("response");
            mFeaturedCount = featuredObject.optInt("totalItemCount");
            if (mDataResponse != null && mDataResponse.length() > 0) {
                mSlideShowLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < 5 && i < mDataResponse.length(); i++) {
                    JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                    int videoId = jsonDataObject.optInt("video_id");
                    String title = jsonDataObject.optString("title");
                    String image_icon = jsonDataObject.optString("image");

                    //Add data to slide show adapter
                    mSlideShowItemList.add(new SlideShowListItems(image_icon, title, videoId));

                }
                if (mDataResponse.length() > 1) {
                    mCircleIndicator.setViewPager(mSlideShowPager);
                }
            } else {
                mSlideShowLayout.setVisibility(View.GONE);
            }
            mSlideShowAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method to make request to server to get the browse page response.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;
        mUrlParams.put("page", String.valueOf(mLoadingPageNo));

        final String url = getBrowseVideosUrl();
        // Don't show data in case of searching and User Profile Tabs.
        try {
            if (!isSearchTextSubmitted && !isProfilePageVideos && !isCategoryVideos
                    && (mOrderBy.isEmpty() || mOrderBy.equals("all"))) {
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.ADV_VIDEO_FILE);
                if (tempData != null) {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(tempData);
                    addDataToList(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
                CustomViews.removeFooterView(mFooterView);
                CustomViews.hideEndOfResults(mFooterView);

                addDataToList(jsonObject);
                if (isCommunityAds) {
                    mAppConst.getCommunityAds(ConstantVariables.ADV_VIDEO_ADS_POSITION,
                            ConstantVariables.ADV_VIDEO_ADS_TYPE);
                }
                // Don't save data in case of searching and User Profile Tabs.
                if (!isSearchTextSubmitted && !isCategoryVideos && !isProfilePageVideos
                        && !url.contains("&orderby")) {
                    DataStorage.createTempFile(mContext, DataStorage.ADV_VIDEO_FILE, jsonObject.toString());
                }
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
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);

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

    /**
     * Function to load more data on scrolling.
     *
     * @param url Url to send request on server.
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
    public void addDataToList(JSONObject jsonObject) {
        mBody = jsonObject;
        if (mBody != null) {

            // Checking if the page is loaded for category result of browse page data result.
            if (isCategoryVideos) {
                /**
                 * Show Sub Categories of the selected category
                 */
                if (isLoadSubCategory) {
                    mSubCategoryResponse = mBody.optJSONArray("subCategories");
                    if (mSubCategoryResponse != null && mSubCategoryResponse.length() != 0) {
                        subCategoryLayout.setVisibility(View.VISIBLE);
                        for (int j = 0; j < mSubCategoryResponse.length(); j++) {
                            JSONObject object = mSubCategoryResponse.optJSONObject(j);
                            String sub_cat_name = object.optString("sub_cat_name");
                            subCategoryAdapter.add(sub_cat_name);
                        }
                    } else {
                        subCategoryLayout.setVisibility(View.GONE);
                    }

                    isLoadSubCategory = false;
                }

                /**
                 * Show 3rd level categories when sub category will be selected
                 */
                if (mBody.has("subsubCategories") && isLoadSubSubcategory) {

                    mSubSubCategoryResponse = mBody.optJSONArray("subsubCategories");
                    if (mSubSubCategoryResponse != null && mSubSubCategoryResponse.length() != 0) {
                        subSubCategoryLayout.setVisibility(View.VISIBLE);

                        for (int j = 0; j < mSubSubCategoryResponse.length(); j++) {
                            JSONObject object = mSubSubCategoryResponse.optJSONObject(j);
                            String sub_sub_cat_name = object.optString("tree_sub_cat_name");
                            subSubCategoryAdapter.add(sub_sub_cat_name);
                        }
                    } else {
                        subSubCategoryLayout.setVisibility(View.GONE);
                    }

                    isLoadSubSubcategory = false;
                }

                JSONObject videosObject = mBody.optJSONObject("video");
                mTotalItemCount = videosObject.optInt("totalItemCount");
                mDataResponse = videosObject.optJSONArray("response");
                if(mTotalItemCount == 0 && isFirstRequest) {
                    subCategoryLayout.setVisibility(View.GONE);
                    subSubCategoryLayout.setVisibility(View.GONE);
                }
            } else {
                mTotalItemCount = mBody.optInt("totalItemCount");
                mBrowseList.setmTotalItemCount(mTotalItemCount);
                mDataResponse = mBody.optJSONArray("response");
            }
            mBrowseList.setmTotalItemCount(mTotalItemCount);

            if (mDataResponse != null && mDataResponse.length() > 0) {
                llErrorMessage.setVisibility(View.GONE);
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
                    int video_id = jsonDataObject.optInt("video_id");
                    int comment_count = jsonDataObject.optInt("comment_count");
                    int like_count = jsonDataObject.optInt("like_count");
                    int view_count = jsonDataObject.optInt("view_count");
                    int duration = jsonDataObject.optInt("duration");
                    int rating = jsonDataObject.optInt("rating");
                    int rating_count = jsonDataObject.optInt("rating_count");
                    String createdDate = jsonDataObject.optString("creation_date");
                    String title = jsonDataObject.optString("title");
                    String image = jsonDataObject.optString("image");
                    int allow_to_view = jsonDataObject.optInt("allow_to_view");
                    String video_url = jsonDataObject.optString("video_url");
                    String contentUrl = jsonDataObject.optString("content_url");
                    int type = jsonDataObject.optInt("type");
                    mBrowseItemList.add(new BrowseListItems(image, title, createdDate,
                            like_count, comment_count, view_count, rating, rating_count,
                            duration, video_id, video_url, contentUrl, type, allow_to_view == 1));

                }
                // Show End of Result Message when there are less results
                if (mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo) {
                    CustomViews.showEndOfResults(mContext, mFooterView);
                }

            } else {
                llErrorMessage.setVisibility(View.VISIBLE);
                tvErrorIcon.setText("\uf03d");
                tvErrorMessage.setText(mContext.getResources().getString(R.string.no_videos));
            }
            mBrowseDataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBrowseDataAdapter.notifyDataSetChanged();
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

        PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.ADV_VIDEO_MENU_TITLE);
        switch (resultCode) {
            case ConstantVariables.VIEW_PAGE_CODE:
                //TODO recheck for this.(When video is uploading inside a channel then refreshing the browse page)
            case ConstantVariables.CREATE_REQUEST_CODE:
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
            boolean isAllowedToView = listItems.isAllowToView();

            if (!isAllowedToView) {
                SnackbarUtils.displaySnackbar(mRootView, mContext.getResources().
                        getString(R.string.unauthenticated_view_message));
            } else {
                Intent mainIntent;
                if (listItems.getmVideoType() >=1 && listItems.getmVideoType() <= 5) {
                    mainIntent = new Intent(mContext, AdvVideoView.class);
                    mainIntent.putExtra(ConstantVariables.VIEW_ID, listItems.getmListItemId());
                    mainIntent.putExtra(ConstantVariables.VIDEO_TYPE, listItems.getmVideoType());
                    mainIntent.putExtra(ConstantVariables.VIDEO_URL, listItems.getmVideoUrl());
                    mainIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                    startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    mainIntent = new Intent(mContext, WebViewActivity.class);
                    mainIntent.putExtra("headerText", listItems.getmBrowseListTitle());
                    mainIntent.putExtra("url", listItems.getmContentUrl());
                    getActivity().startActivityForResult(mainIntent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
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
                // Refreshing featured content carousel.
                if (mSlideShowAdapter != null) {
                    sendRequestForFeaturedContent();
                }
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

                mUrlParams.put("page", String.valueOf(mLoadingPageNo));
                isLoading = true;
                loadMoreData(getBrowseVideosUrl());
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

        if (!isAdLoaded && mAdvertisementsArray != null) {
            isAdLoaded = true;
            int j = 0;
            for (int i = 0; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.ADV_VIDEO_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mBrowseDataAdapter.notifyDataSetChanged();
                }
            }
        } else if (!(mBrowseItemList.size() < AppConstant.LIMIT)) {
            CustomViews.removeFooterView(mFooterView);
            addDataToList(mBody);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        postParams.clear();

        switch (parent.getTag().toString()) {

            case "subCategory":
                isFirstRequest = false;
                mSelectedItem = position;
                subCategoryAdapter.getCustomView(position, view, parent, mSelectedItem);
                if (position != 0) {
                    isLoadSubSubcategory = true;
                    subSubCategoryAdapter.clear();
                    subSubCategoryAdapter.add(getResources().getString(R.string.select_3rd_level_category_text));
                    JSONObject object = mSubCategoryResponse.optJSONObject(position - 1);
                    mSubCategoryId = object.optString("sub_cat_id");
                    postParams.put("subcategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    mSwipeRefreshLayout.setRefreshing(true);
                    makeRequest();

                } else {
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    subSubCategoryLayout.setVisibility(View.GONE);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    mSwipeRefreshLayout.setRefreshing(true);
                    makeRequest();
                }
                break;

            case "subSubCategory":
                isFirstRequest = false;
                mSubsubcategorySelectedItem = position;
                subSubCategoryAdapter.getCustomView(position, view, parent, mSubsubcategorySelectedItem);
                if (position != 0) {
                    JSONObject object = mSubSubCategoryResponse.optJSONObject(position - 1);
                    mSubSubCategoryId = object.optString("tree_sub_cat_id");
                    postParams.put("subcategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    postParams.put("subsubcategory_id", mSubSubCategoryId);
                    mSwipeRefreshLayout.setRefreshing(true);
                    makeRequest();

                } else {
                    postParams.put("subcategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    mSwipeRefreshLayout.setRefreshing(true);
                    makeRequest();
                }
                break;

            case "orderby":
                mSelectedItem = position;
                adapter.getCustomView(position, view, parent, mSelectedItem);
                switch (position) {
                    case 0:
                        mOrderBy = "all";
                        break;
                    case 1:
                        mOrderBy = "creation_date";
                        break;
                    case 2:
                        mOrderBy = "like_count";
                        break;
                    case 3:
                        mOrderBy = "view_count";
                        break;
                    case 4:
                        mOrderBy = "comment_count";
                        break;
                    case 5:
                        mOrderBy = "rating";
                        break;
                    case 6:
                        mOrderBy = "favourite_count";
                        break;
                    case 7:
                        mOrderBy = "featured";
                        break;
                    case 8:
                        mOrderBy = "sponsored";
                        break;
                    case 9:
                        mOrderBy = "best_video";
                        break;
                }
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                makeRequest();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
