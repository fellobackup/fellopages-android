
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

package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.fellopages.mobileapp.classes.common.adapters.SlideShowAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SlideShowListItems;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.store.ui.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class AdvEventsBrowseEventsFragment  extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,  AdapterView.OnItemSelectedListener,
        NativeAdsManager.Listener, OnCommunityAdsLoadedListener {

    private String mBrowseAdvancedEventsUrl, mCurrentSelectedModule;
    private View rootView, footerView;
    private int mTotalItemCount = 0, mLoadingPageNo = 1;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private BrowseListItems mBrowseList;
    private GridViewWithHeaderAndFooter mGridView;
    private JSONObject mBody;
    private List<Object> mBrowseItemList;
    private AdvEventsBrowseDataAdapter mBrowseDataAdapter;
    private SlideShowAdapter mSlideShowAdapter;
    private List<SlideShowListItems> mSlideShowItemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isSearchByDate = false, isSiteGroupEvents = false;
    private boolean isLoading = false, isSearchTextSubmitted = false, isAdLoaded = false,
            isCategoryEvents = false, isLoadSubCategory = true, isLoadSubSubcategory = true;
    private Context mContext;
    private Snackbar snackbar;
    private HashMap<String, String> searchParams = new HashMap<>();
    private String mEventTime = "";
    private JSONArray mDataResponse, mSubCategoryResponse = null, mSubSubCategoryResponse = null, mAdvertisementsArray;
    private NativeAdsManager listNativeAdsManager;
    private String mParentType;
    private int mParentId, mCategoryId;
    Spinner orderBySpinner, subCategorySpinner, subSubCategorySpinner;
    SpinnerAdapter adapter, subCategoryAdapter, subSubCategoryAdapter;
    private String mSubCategoryId, mSubSubCategoryId;
    private CardView subCategoryLayout, subSubCategoryLayout;
    private LinearLayout categoryFilterBlock;
    private int mSelectedItem = -1, mSubsubcategorySelectedItem = -1;
    private HashMap<String, String> postParams = new HashMap<>();
    private boolean isFirstRequest = true;
    private ViewPager mSlideShowPager;
    private CircleIndicator mCircleIndicator;
    private LinearLayout mSlideShowLayout;
    private int mFeaturedCount = 0;
    private boolean isCommunityAds = false;


    public static AdvEventsBrowseEventsFragment newInstance(Bundle bundle) {
        // Required public constructor
        AdvEventsBrowseEventsFragment fragment = new AdvEventsBrowseEventsFragment();
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
        mSlideShowItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        Log.d("LogEvent1Viewed ", "AdvEventsBrowseEventsFragment");
        mAppConst = new AppConstant(mContext);
        mAppConst.setOnCommunityAdsLoadedListener(this);

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.grid_view_layout, container, false);
        footerView = CustomViews.getFooterView(inflater);
        mSlideShowLayout = (LinearLayout) inflater.inflate(R.layout.slide_show_header, container, false);

        mSlideShowPager = (ViewPager) mSlideShowLayout.findViewById(R.id.slide_show_pager);
        mCircleIndicator = (CircleIndicator) mSlideShowLayout.findViewById(R.id.circle_indicator);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        rootView.findViewById(R.id.eventFilterBlock).setVisibility(View.VISIBLE);

        mGridView = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(this);
        mGridView.addHeaderView(mSlideShowLayout);
        mGridView.addFooterView(footerView);

        footerView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);
        ViewCompat.setNestedScrollingEnabled(mGridView, true);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);

        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_siteevent")) {
            PreferencesUtils.updateCurrentModule(mContext,"core_main_siteevent");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        mBrowseAdvancedEventsUrl = UrlUtil.BROWSE_ADV_EVENTS_URL;

        mBrowseDataAdapter = new AdvEventsBrowseDataAdapter(getActivity(),
                R.layout.list_advanced_event_info, mBrowseItemList,"browse_siteevent");
        mGridView.setAdapter(mBrowseDataAdapter);


         /*
        GET SEARCH ARGUMENTS AND ADD THESE PARAMETERS TO THE URL
         */

        if(getArguments() != null) {
            mSlideShowLayout.setVisibility(View.GONE);

            Bundle bundle = getArguments();

            // If The Fragment Being called from User profile page.
            isSearchByDate = bundle.getBoolean("isSearchByDate");
            isSiteGroupEvents = bundle.getBoolean("isSiteGroupEvents");
            isCategoryEvents = bundle.getBoolean(ConstantVariables.IS_CATEGORY_BASED_RESULTS, false);
            mCategoryId = bundle.getInt(ConstantVariables.VIEW_PAGE_ID, 0);
            postParams.put("category_id", String.valueOf(mCategoryId));

            if (isSearchByDate) {
                searchParams.clear();
                String date_current = bundle.getString("date_current");
                String viewType = bundle.getString("viewtype");
                searchParams.put("date_current", date_current);
                searchParams.put("viewtype", viewType);
            } else if (isSiteGroupEvents) {
                mParentType = bundle.getString("parent_type");
                mParentId = bundle.getInt("parent_id");
            }

            if(!isSearchByDate && !isSiteGroupEvents && !isCategoryEvents) {
                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key, null);

                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
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
                            PreferencesUtils.getCurrentSelectedModule(mContext), null);
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
        if(!isCategoryEvents){
            orderBySpinner = (Spinner) rootView.findViewById(R.id.filter_view);
            rootView.findViewById(R.id.eventFilterBlock).setVisibility(View.VISIBLE);

            adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);

            /* Add events filter type to spinner using adpter */
            adapter.add(getResources().getString(R.string.browse_event_filter_sell_all));
            adapter.add(getResources().getString(R.string.browse_event_filter_today));
            adapter.add(getResources().getString(R.string.browse_event_filter_tomorrow));
            adapter.add(getResources().getString(R.string.browse_event_filter_this_weekend));
            adapter.add(getResources().getString(R.string.browse_event_filter_this_week));
            adapter.add(getResources().getString(R.string.browse_event_filter_this_month));
            adapter.add(mContext.getResources().getString(R.string.featured));
            adapter.add(mContext.getResources().getString(R.string.sponsored));

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            orderBySpinner.setAdapter(adapter);
            orderBySpinner.setSelection(0, false);
            orderBySpinner.setOnItemSelectedListener(this);
            orderBySpinner.setTag("orderby");
        }else{

            rootView.findViewById(R.id.eventFilterBlock).setVisibility(View.GONE);

            categoryFilterBlock = (LinearLayout) rootView.findViewById(R.id.category_filter_block);
            categoryFilterBlock.setVisibility(View.VISIBLE);
            categoryFilterBlock.findViewById(R.id.toolbar).setVisibility(View.GONE);

            subCategoryLayout = (CardView) rootView.findViewById(R.id.categoryFilterLayout);
            subSubCategoryLayout = (CardView) rootView.findViewById(R.id.subCategoryFilterLayout);
            subCategorySpinner = (Spinner) subCategoryLayout.findViewById(R.id.filter_view);
            subSubCategorySpinner = (Spinner) subSubCategoryLayout.findViewById(R.id.filter_view);


            /*
            Add swipeRefreshLayout layout below category filter
             */
            RelativeLayout.LayoutParams layoutParams = CustomViews.getFullWidthRelativeLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, R.id.category_filter_block);
            swipeRefreshLayout.setLayoutParams(layoutParams);

            subCategoryAdapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);
            subCategoryAdapter.add(getResources().getString(R.string.select_sub_category_text));

            //    ArrayAdapter adapter = new ArrayAdapter(this,R.layout.simple_spinner_item,list);
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

        if(ConstantVariables.ENABLE_ADV_EVENT_ADS == 1) {
            switch (ConstantVariables.ADV_EVENT_ADS_TYPE){
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList,mBrowseDataAdapter,ConstantVariables.ADV_EVENT_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        // Hide Filter View if Fragment is being called from user profile page or search page
        if((searchParams != null && searchParams.size() != 0) || isSiteGroupEvents){
            rootView.findViewById(R.id.eventFilterBlock).setVisibility(View.GONE);
        }

        sendRequestToServer();

        return rootView;
    }

    private void sendRequestForFeaturedContent() {
        String url = UrlUtil.BROWSE_ADV_EVENTS_URL + "limit=" + AppConstant.FEATURED_CONTENT_LIMIT +
                "&event_time=featured&page=" + 1;

        mSlideShowItemList.clear();
        String tempData = DataStorage.getResponseFromLocalStorage(mContext,DataStorage.ADV_EVENT_FEATURED_CONTENT);
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
                DataStorage.createTempFile(mContext,DataStorage.ADV_EVENT_FEATURED_CONTENT, jsonObject.toString());

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });

    }

    private void addDataToFeaturedList(JSONObject mFeaturedObject) {

        if (mFeaturedObject != null) {
            mDataResponse = mFeaturedObject.optJSONArray("response");
            mFeaturedCount = mFeaturedObject.optInt("getTotalItemCount");
            if (mDataResponse != null && mDataResponse.length() > 0){
                mSlideShowLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < 5 && i < mDataResponse.length(); i++) {
                    JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                    int event_id = jsonDataObject.optInt("event_id");
                    String title = jsonDataObject.optString("title");
                    String image_icon = jsonDataObject.optString("image");

                    //Add data to slide show adapter
                    mSlideShowItemList.add(new SlideShowListItems(image_icon, title, event_id));

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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        boolean isAllowedToView = listItems.isAllowToView();

        // If View is not allowed, show Snackbar
        if(!isAllowedToView) {
            SnackbarUtils.displaySnackbar(rootView,
                    mContext.getResources().getString(R.string.unauthenticated_view_message));
        }else {

            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                    mCurrentSelectedModule, null);
            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void setMenuVisibility(boolean isVisibleToUser) {
        super.setMenuVisibility(isVisibleToUser);

        // Make sure that currently visible
        if (!isVisible() && snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public void sendRequestToServer() {

        try {
            mLoadingPageNo = 1;

            String browseAdvancedEventsUrl = getBrowseEventsUrl() + "&page=" + mLoadingPageNo;

            if (!isSearchTextSubmitted && !isSearchByDate && (mEventTime.isEmpty() || mEventTime.equals("all"))
                    && !isSiteGroupEvents && !isCategoryEvents) {

                // Don't show data in case of searching and User Profile Tabs.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.ADVANCED_EVENT_FILE);
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
            }

            mAppConst.getJsonResponseFromUrl(browseAdvancedEventsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBrowseItemList.clear();
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }

                    addDataToList(jsonObject);
                    if(isCommunityAds){
                        mAppConst.getCommunityAds(ConstantVariables.ADV_EVENT_ADS_POSITION,
                                ConstantVariables.ADV_EVENT_ADS_TYPE);
                    }

                    // Don't save data in case of searching and User Profile Tabs.
                    if (!isSearchTextSubmitted && !isSearchByDate &&
                            (mEventTime.isEmpty() || mEventTime.equals("all")) && !isSiteGroupEvents
                            && !isCategoryEvents) {
                        DataStorage.createTempFile(mContext, DataStorage.ADVANCED_EVENT_FILE, jsonObject.toString());
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
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        mGridView.setOnItemClickListener(this);
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

    int j=0;
    public void addDataToList(JSONObject jsonObject) {
        mBody = jsonObject;
        if (mBody != null) {
            try {

                if(isCategoryEvents){
                    /**
                     * Show Sub Categories of the selected category
                     */
                    if (isLoadSubCategory) {
                        mSubCategoryResponse = mBody.optJSONArray("subCategories");

                        if (mSubCategoryResponse != null && mSubCategoryResponse.length() != 0) {
                            subCategoryLayout.setVisibility(View.VISIBLE);
                            for (int k = 0; k < mSubCategoryResponse.length(); k++) {
                                JSONObject object = mSubCategoryResponse.getJSONObject(k);
                                String sub_cat_name = object.getString("sub_cat_name");
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

                            for (int k = 0; k < mSubSubCategoryResponse.length(); k++) {
                                JSONObject object = mSubSubCategoryResponse.getJSONObject(k);
                                String sub_sub_cat_name = object.getString("tree_sub_cat_name");
                                subSubCategoryAdapter.add(sub_sub_cat_name);
                            }
                        } else {
                            subSubCategoryLayout.setVisibility(View.GONE);
                        }

                        isLoadSubSubcategory = false;
                    }

                    mTotalItemCount = mBody.optInt("totalEventCount");
                    mDataResponse = mBody.optJSONArray("events");
                    if(mTotalItemCount == 0 && isFirstRequest){
                        subCategoryLayout.setVisibility(View.GONE);
                        subSubCategoryLayout.setVisibility(View.GONE);
                    }
                }else{
                    mTotalItemCount = mBody.optInt("getTotalItemCount");

                    if (mTotalItemCount == 0) {
                        mTotalItemCount = mBody.optInt("totalItemCount");
                    }
                    mDataResponse = mBody.optJSONArray("response");
                }

                mBrowseList.setmTotalItemCount(mTotalItemCount);

                if (mDataResponse != null && mDataResponse.length() > 0) {
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);

                    for (int i = 0; i < mDataResponse.length(); i++) {
                        if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                                && mBrowseItemList.size() % ConstantVariables.ADV_EVENT_ADS_POSITION == 0) {
                            switch (ConstantVariables.ADV_EVENT_ADS_TYPE){
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
                                            j=0;
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
                        int event_id = jsonDataObject.optInt("event_id");
                        String title = jsonDataObject.optString("title");
                        String location = jsonDataObject.optString("location");
                        String starttime = jsonDataObject.optString("starttime");
                        int featured = isCategoryEvents ? 0 : jsonDataObject.optInt("featured");
                        int sponsored = isCategoryEvents ? 0 : jsonDataObject.optInt("sponsored");
                        JSONObject hostObject = jsonDataObject.optJSONObject("hosted_by");
                        String hostTitle = null, hostType = null, hostImage = null;
                        int hostId = 0;
                        if(hostObject != null){
                            hostTitle = hostObject.optString("host_title");
                            hostId = hostObject.optInt("host_id");
                            hostType = hostObject.optString("host_type");
                            hostImage = hostObject.optString("image");
                        }
                        String image_icon = jsonDataObject.optString("image");
                        int member_count = jsonDataObject.optInt("member_count");
                        int allow_to_view = jsonDataObject.optInt("allow_to_view");
                        int hasMultipleDates = jsonDataObject.optInt("hasMultipleDates");

                        if (allow_to_view == 1) {
                            mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
                                    hostTitle, member_count, event_id, true, location,
                                    starttime, hostId, hostType, hasMultipleDates, featured,sponsored));
                        } else {
                            mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
                                    hostTitle, member_count, event_id, false, location,
                                    starttime, hostId, hostType, hasMultipleDates, featured,sponsored));
                        }

                        mBrowseDataAdapter.notifyDataSetChanged();

                        // Show End of Result Message when there are less results
                        if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                            CustomViews.showEndOfResults(mContext, footerView);
                        }
                    }
                } else {
                    CustomViews.removeFooterView(footerView);
                    LinearLayout error_view = (LinearLayout) rootView.findViewById(R.id.message_layout);
                    String message = mContext.getResources().getString(R.string.no_events);
                    int padding10 = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);

                    if (mFeaturedCount > 0) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        error_view.setLayoutParams(layoutParams);
                        error_view.setPadding(padding10, (int) mContext.getResources().getDimension(R.dimen.slider_view_height), padding10 , padding10);

                        if (PreferencesUtils.getDefaultLocation(mContext) != null && !PreferencesUtils.getDefaultLocation(mContext).isEmpty()) {
                            message = message + " " + mContext.getResources().getString(R.string.for_this_location_text);
                        }
                    }

                    error_view.setVisibility(View.VISIBLE);
                    TextView errorIcon = (TextView) error_view.findViewById(R.id.error_icon);
                    TextView errorMessage = (TextView) error_view.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf073");
                    errorMessage.setPadding(0, padding10, 0, 0);
                    errorMessage.setText(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onRefresh() {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                isAdLoaded = false;
                sendRequestToServer();
                if (listNativeAdsManager != null) {
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

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
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;

        if(limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = getBrowseEventsUrl() + "&page=" + mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }
        }

    }

    /**
     * Load more data on scrolling
     * @param url Url to send request on server
     */
    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if(isCommunityAds){
                    mAppConst.getCommunityAds(ConstantVariables.ADV_EVENT_ADS_POSITION,
                            ConstantVariables.ADV_EVENT_ADS_TYPE);
                } else {
                    CustomViews.removeFooterView(footerView);
                    addDataToList(jsonObject);
                }
                isLoading = false;
                isFirstRequest = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
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
                            sendRequestToServer();
                        }
                    });
                }
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        postParams.clear();

        switch (parent.getTag().toString()){
            case "orderby":
                mSelectedItem = position;
                adapter.getCustomView(position, view, parent, mSelectedItem);
                switch (position) {
                    case 0:
                        mEventTime = "all";
                        break;
                    case 1:
                        mEventTime = "today";
                        break;
                    case 2:
                        mEventTime = "tomorrow";
                        break;
                    case 3:
                        mEventTime = "this_weekend";
                        break;
                    case 4:
                        mEventTime = "this_week";
                        break;
                    case 5:
                        mEventTime = "this_month";
                        break;
                    case 6:
                        mEventTime = "featured";
                        break;
                    case 7:
                        mEventTime = "sponsored";
                        break;
                }
                swipeRefreshLayout.setRefreshing(true);
                sendRequestToServer();
                break;

            case "subCategory":
                isFirstRequest = false;
                mSelectedItem = position;
                subCategoryAdapter.getCustomView(position, view, parent, mSelectedItem);
                if (position != 0){
                    isLoadSubSubcategory = true;
                    subSubCategoryAdapter.clear();
                    subSubCategoryAdapter.add(getResources().getString(R.string.select_3rd_level_category_text));
                    JSONObject object = mSubCategoryResponse.optJSONObject(position - 1);
                    mSubCategoryId = object.optString("sub_cat_id");
                    postParams.put("subCategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    swipeRefreshLayout.setRefreshing(true);
                    sendRequestToServer();

                } else {
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    subSubCategoryLayout.setVisibility(View.GONE);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    swipeRefreshLayout.setRefreshing(true);
                    sendRequestToServer();
                }
                break;

            case "subSubCategory":
                isFirstRequest = false;
                mSubsubcategorySelectedItem = position;
                subSubCategoryAdapter.getCustomView(position, view, parent, mSubsubcategorySelectedItem);
                if (position != 0) {
                    JSONObject object = mSubSubCategoryResponse.optJSONObject(position - 1);
                    mSubSubCategoryId = object.optString("tree_sub_cat_id");
                    postParams.put("subCategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    postParams.put("subsubcategory_id", mSubSubCategoryId);
                    swipeRefreshLayout.setRefreshing(true);
                    sendRequestToServer();

                } else {
                    postParams.put("subCategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    swipeRefreshLayout.setRefreshing(true);
                    sendRequestToServer();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.ADV_EVENT_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mBrowseDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    private String getBrowseEventsUrl(){

        String url = mBrowseAdvancedEventsUrl + "limit=" + AppConstant.LIMIT;

        if (isSiteGroupEvents) {
            url += "&parent_type=" + mParentType + "&parent_id=" + mParentId;
        } else if (isCategoryEvents) {
            url = UrlUtil.BROWSE_CATEGORIES_ADV_EVENTS_URL;
            url = mAppConst.buildQueryString(url, postParams);
        } else if (!mEventTime.isEmpty()) {
            url += "&event_time=" + mEventTime;
        } else if (isSearchByDate){
            url =  UrlUtil.BROWSE_CALENDAR_ADV_EVENTS_URL;
        }


        // Adding search params in url
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
                if (i != 0 && i % ConstantVariables.ADV_EVENT_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mBrowseDataAdapter.notifyDataSetChanged();
                }
            }
        } else if (!(mBrowseItemList.size() < AppConstant.LIMIT)) {
            CustomViews.removeFooterView(footerView);
            addDataToList(mBody);
        }
    }
}
