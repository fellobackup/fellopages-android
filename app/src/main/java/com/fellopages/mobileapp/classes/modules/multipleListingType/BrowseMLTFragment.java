/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.modules.multipleListingType;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Tasks;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.MapActivity;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommunityAdsLoadedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GridSpacingItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.user.MemberClusterItems;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class BrowseMLTFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        NativeAdsManager.Listener, AdapterView.OnItemSelectedListener, OnCommunityAdsLoadedListener, View.OnClickListener {

    Spinner spinner, subCategorySpinner, subSubCategorySpinner;
    SpinnerAdapter adapter, subCategoryAdapter, subSubCategoryAdapter;
    /**
     * Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response
     */
    int j = 0;
    private View mRootView, mHeaderView;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private Context mContext;
    private TextView errorIcon, tvViewMore;
    private SelectableTextView errorMessage;
    private ProgressBar mProgressBar;
    private LinearLayout messageLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseMLTAdapter;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList, mFeaturedBrowseList;
    private boolean isLoading = false, isSearchTextSubmitted = false, isMemberMLT = false,
            isAdLoaded = false, isAdvGroupsMLT = false, isVisibleToUser = false, isBringToFront = false;
    private int mLoadingPageNo = 1, NUM_OF_COLUMNS;
    private String mBrowseMLTUrl, mListingLabel, mListingIcon, mGroupMLTUrl, mCurrentSelectedModule;
    private int mUserId, mListingTypeId, mMLTBrowseType;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HashMap<String, String> searchParams = new HashMap<>();
    private NativeAdsManager listNativeAdsManager;
    private String mListingFilter = "";
    private int mCategoryId;
    private String mSubCategoryId, mSubSubCategoryId;
    private CardView subCategoryLayout, subSubCategoryLayout;
    private int mSelectedItem = -1, mSubsubcategorySelectedItem = -1;
    private HashMap<String, String> postParams = new HashMap<>();
    private JSONArray mDataResponse, mSubCategoryResponse = null, mSubSubCategoryResponse = null;
    private boolean isCategoryResults = false, isLoadSubCategory = true, isLoadSubSubcategory = true, isFirstRequest = true;
    private LayoutInflater layoutInflater;
    private JSONObject sliderDataObject, mBody;
    private boolean isShowHeader = true, isCommunityAds = false, isFirstTab = false;
    private int mFeaturedCount = 0;
    private JSONArray mAdvertisementsArray;
    private boolean isMakeFirstCall = true;
    private FloatingActionButton myLocationButton;
    private View mapMyLocationButton;
    private GoogleMap mMap;
    private ClusterManager<MemberClusterItems> mClusterManager;
    private float mCurrentZoom;
    private String isViewTypeLoaded = "isLoaded";
    private LatLng currentLatLng = null;
    private BitmapDescriptor bitmapDescriptor = null;
    private int i = 0, itemPosition = -1, mSecondaryViewType, mViewType, mLoadingMapPageNo = 1, mapItemCount;
    private Dialog quickInfoDialog;
    private View mMapView;
    private List<BrowseListItems> mMapItemList;
    private boolean isRequestProcessing = false, isLocationSet = false;
    private Menu actionMenu;
    public static int selectedViewType;
    private String locationTitle = "";
    private static boolean isGpsRequestDisplayed = false;

    public static BrowseMLTFragment newInstance(Bundle bundle) {
        BrowseMLTFragment fragment = new BrowseMLTFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible && !isVisibleToUser && mContext != null) {
            updateListingParams();
            makeRequest();
        }
        isBringToFront = visible;
        if (this.actionMenu != null && !visible) {
            this.actionMenu.findItem(R.id.viewToggle).setVisible(false);
            onPrepareOptionsMenu(this.actionMenu);
        } else if (this.actionMenu != null) {
            this.actionMenu.findItem(R.id.viewToggle).setVisible(true);
            onPrepareOptionsMenu(this.actionMenu);
        }
        if (!isVisible() && snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
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
        layoutInflater = inflater;
        mMapItemList = new ArrayList<>();

        // Updating current selected module
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if (mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("sitereview_listing")) {
            PreferencesUtils.updateCurrentModule(mContext, "sitereview_listing");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }


        if (getArguments() != null && getArguments().containsKey(ConstantVariables.LISTING_TYPE_ID)) {
            PreferencesUtils.setCurrentSelectedListingId(mContext, getArguments().getInt(ConstantVariables.
                    LISTING_TYPE_ID));
            getArguments().remove(ConstantVariables.LISTING_TYPE_ID);
        }

        // Inflating recycler layout.
        mRootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mHeaderView = inflater.inflate(R.layout.spinner_view, null, false);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mMapView = mRootView.findViewById(R.id.map_layout);
        myLocationButton = mRootView.findViewById(R.id.my_location_button);
        setDrawableColor(myLocationButton);
        LocationManager manager = (LocationManager) mContext.getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            myLocationButton.setVisibility(View.GONE);
        }
        mapMyLocationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        mapMyLocationButton.setVisibility(View.GONE);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapMyLocationButton.callOnClick();
            }
        });

        // Getting arguments from search query
        if (getArguments() != null) {

            Bundle bundle = getArguments();
            mRecyclerView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            // If The Fragment Being called from User profile page.
            isMemberMLT = bundle.getBoolean("isMemberMLT");
            isAdvGroupsMLT = bundle.getBoolean("isAdvGroupsMLT");
            mUserId = bundle.getInt("user_id");
            mViewType = BrowseMLTFragment.selectedViewType;
            isCategoryResults = bundle.getBoolean(ConstantVariables.IS_CATEGORY_BASED_RESULTS, false);
            getViews();
            mCategoryId = bundle.getInt(ConstantVariables.VIEW_PAGE_ID, 0);
            postParams.put("category_id", String.valueOf(mCategoryId));
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
            if(mUserId != 0){
                mBrowseMLTUrl += "&user_id=" + mUserId;
            }
            if (isMemberMLT || isAdvGroupsMLT) {
                mGroupMLTUrl = bundle.getString(ConstantVariables.URL_STRING);
                mHeaderView.findViewById(R.id.spinnerCardView).setVisibility(View.GONE);
            }

            if (!isMemberMLT && !isAdvGroupsMLT && !isCategoryResults) {
                updateListingParams();
                Set<String> searchArgumentSet = getArguments().keySet();
                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
                    }
                }
            }
            isShowHeader = false;
        } else {
            getViews();
            updateListingParams();
            sendRequestForFeaturedContent();
        }

        // Hide Filter View if Fragment is being called from user profile, group profile or search page
        if ((searchParams != null && searchParams.size() != 0) || isMemberMLT || isAdvGroupsMLT) {
            isSearchTextSubmitted = true;
            mHeaderView.setVisibility(View.GONE);
            mBrowseMLTUrl = mAppConst.buildQueryString(mBrowseMLTUrl, searchParams);
            mBrowseItemList.clear();
        }

        if (!isSearchTextSubmitted) {
            setHasOptionsMenu(true);
        }

        /**
         * Show Order by Spinner when fragment is loaded from dashboard.
         */
        if (!isCategoryResults) {

            adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);

            /* Add events filter type to spinner using adpter */
            adapter.add(mContext.getResources().getString(R.string.browse_event_filter_sell_all));
            adapter.add(mContext.getResources().getString(R.string.featured));
            adapter.add(mContext.getResources().getString(R.string.sponsored));

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0, false);
            spinner.setTag("listingFilter");
        } else {

            subCategoryAdapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);
            subCategoryAdapter.add(getResources().getString(R.string.select_sub_category_text));

            //    ArrayAdapter adapter = new ArrayAdapter(this,R.layout.simple_spinner_item,list);
            subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategorySpinner.setAdapter(subCategoryAdapter);
            subCategorySpinner.setSelection(0, false);
            subCategorySpinner.setTag("subCategory");

            subSubCategoryAdapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSubsubcategorySelectedItem);
            subSubCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subSubCategorySpinner.setAdapter(subSubCategoryAdapter);
            subSubCategorySpinner.setSelection(0, false);
            subSubCategorySpinner.setTag("subSubCategory");
        }


        // Showing adds if ADS enabled for MLT.
        if (ConstantVariables.ENABLE_MLT_ADS == 1) {
            switch (ConstantVariables.MLT_ADS_TYPE) {
                case ConstantVariables.TYPE_FACEBOOK_ADS:
                    listNativeAdsManager = new NativeAdsManager(mContext,
                            mContext.getResources().getString(R.string.facebook_placement_id),
                            ConstantVariables.DEFAULT_AD_COUNT);
                    listNativeAdsManager.setListener(this);
                    listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
                    break;
                case ConstantVariables.TYPE_GOOGLE_ADS:
                    mAdFetcher = new AdFetcher(mContext);
                    mAdFetcher.loadAds(mBrowseItemList, mBrowseMLTAdapter, ConstantVariables.MLT_ADS_POSITION);
                    break;
                default:
                    isCommunityAds = true;
                    break;
            }
        }

        // When its loading for the Browse fragment from the dashboard or from the category selection
        // then making server call.
        if ((!isMemberMLT && !isAdvGroupsMLT && searchParams != null && searchParams.size() != 0)
                || isCategoryResults || isFirstTab) {
            updateListingParams();
            makeRequest();
        }
        return mRootView;
    }

    private void sendRequestForFeaturedContent() {
        String featuredContentUrl = UrlUtil.BROWSE_MLT_URL + "limit=" + AppConstant.FEATURED_CONTENT_LIMIT +
                "&listingtype_id=" + mListingTypeId + "&page=1&listing_filter=featured";

        mBrowseItemList.clear();
        String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.MLT_FEATURED_CONTENT + mListingTypeId);
        if (tempData != null) {
            try {
                JSONObject jsonObject = new JSONObject(tempData);
                addDataToFeaturedList(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mAppConst.getJsonResponseFromUrl(featuredContentUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                addDataToFeaturedList(jsonObject);
                DataStorage.createTempFile(mContext, DataStorage.MLT_FEATURED_CONTENT + mListingTypeId, jsonObject.toString());
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    private void addDataToFeaturedList(JSONObject jsonObject) {
        JSONArray mDataResponse = jsonObject.optJSONArray("response");
        if (mDataResponse != null && mDataResponse.length() > 0) {
            sliderDataObject = jsonObject;
            mFeaturedCount = jsonObject.optInt("totalItemCount");
            mFeaturedBrowseList = new BrowseListItems(sliderDataObject, mListingTypeId);
            mBrowseItemList.add(0, mFeaturedBrowseList);
            isShowHeader = true;
        } else {
            isShowHeader = false;
        }

        // Initializing layout manager and adapter according to the browse view type.
        if (mMLTBrowseType != 0)
            setLayoutManager(mMLTBrowseType);
        else
            setLayoutManager(2);

        if (mViewType != 4) {
            mBrowseMLTAdapter.notifyDataSetChanged();
        }

        makeRequest();
    }

    public void getViews() {
        swipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mProgressBar = mRootView.findViewById(R.id.progressBar);

        // No data message views
        messageLayout = mRootView.findViewById(R.id.message_layout);
        errorIcon = mRootView.findViewById(R.id.error_icon);
        errorMessage = mRootView.findViewById(R.id.error_message);
        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        // getting header views.

        if (isCategoryResults) {
            mHeaderView = layoutInflater.inflate(R.layout.layout_category_block, null, false);
            subCategoryLayout = mHeaderView.findViewById(R.id.categoryFilterLayout);
            subSubCategoryLayout = mHeaderView.findViewById(R.id.subCategoryFilterLayout);
            subCategorySpinner = subCategoryLayout.findViewById(R.id.filter_view);
            subSubCategorySpinner = subSubCategoryLayout.findViewById(R.id.filter_view);
            mHeaderView.findViewById(R.id.mlt_category_block).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.toolbar).setVisibility(View.GONE);
            // Adding header view to main view.
            RelativeLayout mainView = mRootView.findViewById(R.id.main_view_recycler);
            mainView.addView(mHeaderView);
            CustomViews.addHeaderView(R.id.mlt_category_block, swipeRefreshLayout);
            mHeaderView.findViewById(R.id.mlt_category_block).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            mHeaderView = layoutInflater.inflate(R.layout.spinner_view, null, false);
            spinner = mHeaderView.findViewById(R.id.filter_view);
            // Adding header view to main view.
            RelativeLayout mainView = mRootView.findViewById(R.id.main_view_recycler);
            mainView.addView(mHeaderView);
            CustomViews.addHeaderView(R.id.spinnerCardView, swipeRefreshLayout);
            mHeaderView.findViewById(R.id.spinnerCardView).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        tvViewMore = mRootView.findViewById(R.id.view_more);
        tvViewMore.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    /**
     * Method to initialize listing variables.
     */
    public void updateListingParams() {

        if (spinner != null)
            spinner.setOnItemSelectedListener(this);

        if (subCategorySpinner != null)
            subCategorySpinner.setOnItemSelectedListener(this);

        if (subSubCategorySpinner != null)
            subSubCategorySpinner.setOnItemSelectedListener(this);

        // Getting current Listing variables from preferences.
        mListingTypeId = PreferencesUtils.getCurrentSelectedListingId(mContext);
        mListingLabel = PreferencesUtils.getCurrentSelectedListingSingularLabel(mContext, mListingTypeId);

        // Don't display map view in case of user profile page
        if (BrowseMLTFragment.selectedViewType == 0 || isMemberMLT || isCategoryResults) {
            BrowseMLTFragment.selectedViewType = mViewType = mMLTBrowseType = PreferencesUtils.getCurrentSelectedListingBrowseType(mContext, mListingTypeId);
        } else {
            mViewType = mMLTBrowseType = BrowseMLTFragment.selectedViewType;
        }
        mSecondaryViewType = PreferencesUtils.getCurrentSelectedListingSecondaryBrowseType(mContext, mListingTypeId);
        mListingIcon = PreferencesUtils.getCurrentSelectedListingIcon(mContext, mListingTypeId);

        mBrowseMLTUrl = UrlUtil.BROWSE_MLT_URL + "limit=" + AppConstant.LIMIT + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo +
                "&listing_filter=" + mListingFilter;

        // setting up the listing icon.
        if (mListingIcon != null && !mListingIcon.isEmpty()) {
            try {
                mListingIcon = new String(Character.toChars(Integer.parseInt(mListingIcon, 16)));
            } catch (NumberFormatException e) {
                mListingIcon = GlobalFunctions.getItemIcon(mCurrentSelectedModule);
            }
        } else {
            mListingIcon = GlobalFunctions.getItemIcon(mCurrentSelectedModule);
        }

        // Initializing layout manager and adapter according to the browse view type.
        if (mMLTBrowseType != 0) {
            setLayoutManager(mMLTBrowseType);
        } else {
            setLayoutManager(2);
        }
    }

    /**
     * Method to send request to server to get browse page data.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;
        if (isRequestProcessing) {
            return;
        }
        isRequestProcessing = true;
        if (!isSearchTextSubmitted && !isMemberMLT && (mListingFilter.isEmpty() ||
                mListingFilter.equals("all")) && !isAdvGroupsMLT && !isCategoryResults) {
            mProgressBar.setVisibility(View.VISIBLE);


                mBrowseItemList.clear();
                if (mFeaturedBrowseList != null && !mBrowseItemList.contains(mFeaturedBrowseList)) {
                    mBrowseItemList.add(0, mFeaturedBrowseList);
                }
                mBrowseMLTUrl = UrlUtil.BROWSE_MLT_URL + "limit=" + AppConstant.LIMIT + "&listingtype_id=" + mListingTypeId + "&page=" +
                        mLoadingPageNo + "&listing_filter=" + mListingFilter;
                _showViewFromLocal();

        } else if (isMemberMLT) {
            mBrowseMLTUrl = UrlUtil.BROWSE_MLT_URL + "limit=" + AppConstant.LIMIT + "&listingtype_id=" + mListingTypeId +
                    "&page=" + mLoadingPageNo + "&user_id=" + mUserId;

        } else if (isAdvGroupsMLT) {
            mBrowseMLTUrl = mGroupMLTUrl + "&limit=" + AppConstant.LIMIT + "&page=" + mLoadingPageNo;

        } else if (!mListingFilter.isEmpty()) {
            swipeRefreshLayout.setRefreshing(true);
            mBrowseMLTUrl = UrlUtil.BROWSE_MLT_URL + "limit=" + AppConstant.LIMIT + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo
                    + "&listing_filter=" + mListingFilter;
        } else if (isCategoryResults) {
            mBrowseMLTUrl = UrlUtil.BROWSE_CATEGORIES_MLT_URL + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo
                    + "&showListings=1";
            mBrowseMLTUrl = mAppConst.buildQueryString(mBrowseMLTUrl, postParams);
        }

        if (mViewType == 4) mBrowseMLTUrl += "&viewType=1";

        if (searchParams != null && searchParams.size() != 0) {
            mBrowseMLTUrl = mAppConst.buildQueryString(mBrowseMLTUrl, searchParams);
        }

        if (!swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
        }
        mAppConst.getJsonResponseFromUrl(mBrowseMLTUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mProgressBar.setVisibility(View.GONE);
                isRequestProcessing = false;
                mBody = jsonObject;
                isVisibleToUser = true;
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                mBrowseItemList.clear();
                if (sliderDataObject != null) {
                    mBrowseItemList.add(0, new BrowseListItems(sliderDataObject, mListingTypeId));
                }


                if (isCommunityAds) {
                    mAppConst.getCommunityAds(ConstantVariables.MLT_ADS_POSITION,
                            ConstantVariables.MLT_ADS_TYPE);
                }
                if (mViewType != 4) {
                    addItemsToList(jsonObject);
                    mBrowseMLTAdapter.notifyDataSetChanged();
                } else {
                    addDataToMap(jsonObject);
                }

                // Don't save data in cashing in case of searching and user profile tabs.
                if (!isSearchTextSubmitted && !isMemberMLT && (mListingFilter.isEmpty() ||
                        mListingFilter.equals("all")) && !isAdvGroupsMLT && !isCategoryResults && mViewType != 4) {
                    DataStorage.createTempFile(mContext, DataStorage.MLT_FILE + mListingLabel, jsonObject.toString());
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (!isViewTypeLoaded.contains(String.valueOf(mViewType)) && !isSearchTextSubmitted) {
                    getActivity().invalidateOptionsMenu();
                }
                isViewTypeLoaded += mViewType;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), ((Activity)mContext).findViewById(android.R.id.content), message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(((Activity)mContext).findViewById(android.R.id.content), message);
                }
                isRequestProcessing = false;
            }
        });

    }

    private void _showViewFromLocal() {
        try {
            // Don't show data in case of searching and User Profile Tabs.
            String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.MLT_FILE +
                    mListingLabel);

            if (tempData != null && mViewType != 4) {
                JSONObject jsonObject = new JSONObject(tempData);
                addItemsToList(jsonObject);
                mBrowseMLTAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    /**
     * Method to load more data(if exists) on scrolling.
     *
     * @param url Url to load next page data
     */
    private void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        Tasks.call(new Callable<Void>() {
            @Override
            public Void call() {
                mBrowseItemList.add(null);
                mBrowseMLTAdapter.notifyItemInserted(mBrowseItemList.size() - 1);
                return null;
            }
        });

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                if (isCommunityAds) {
                    mAppConst.getCommunityAds(ConstantVariables.MLT_ADS_POSITION,
                            ConstantVariables.MLT_ADS_TYPE);
                } else {
                    //   remove progress item
                    mBrowseItemList.remove(mBrowseItemList.size() - 1);
                    mBrowseMLTAdapter.notifyItemRemoved(mBrowseItemList.size());
                    addItemsToList(jsonObject);
                }

                mBrowseMLTAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
                isFirstRequest = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(((Activity)mContext).findViewById(android.R.id.content), message);
            }
        });
    }

    public void addItemsToList(JSONObject jsonObject) {


        int mTotalItemCount;
        if (isCategoryResults) {
            mDataResponse = jsonObject.optJSONArray("listings");
            mTotalItemCount = jsonObject.optInt("totalListingCount");

            mBrowseList.setmTotalItemCount(mTotalItemCount);

            /**
             * Show Sub Categories of the selected category
             */
            if (isLoadSubCategory) {
                mSubCategoryResponse = jsonObject.optJSONArray("subCategories");

                if (mSubCategoryResponse != null && mSubCategoryResponse.length() != 0) {
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    for (int k = 0; k < mSubCategoryResponse.length(); k++) {
                        JSONObject object = mSubCategoryResponse.optJSONObject(k);
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
            if (jsonObject.has("subsubCategories") && isLoadSubSubcategory) {

                mSubSubCategoryResponse = jsonObject.optJSONArray("subsubCategories");
                if (mSubSubCategoryResponse != null && mSubSubCategoryResponse.length() != 0) {
                    subSubCategoryLayout.setVisibility(View.VISIBLE);

                    for (int k = 0; k < mSubSubCategoryResponse.length(); k++) {
                        JSONObject object = mSubSubCategoryResponse.optJSONObject(k);
                        String sub_sub_cat_name = object.optString("tree_sub_cat_name");
                        subSubCategoryAdapter.add(sub_sub_cat_name);
                    }
                } else {
                    subSubCategoryLayout.setVisibility(View.GONE);
                }

                isLoadSubSubcategory = false;
            }

            if (mTotalItemCount == 0 && isFirstRequest) {
                subCategoryLayout.setVisibility(View.GONE);
                subSubCategoryLayout.setVisibility(View.GONE);
            }
        } else {
            mDataResponse = jsonObject.optJSONArray("response");
            mTotalItemCount = jsonObject.optInt("totalItemCount");
            mBrowseList.setmTotalItemCount(mTotalItemCount);
        }

        if (mDataResponse != null && mDataResponse.length() > 0) {
            messageLayout.setVisibility(View.GONE);

            for (int i = 0; i < mDataResponse.length(); i++) {
                if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseItemList.size() != 0
                        && mBrowseItemList.size() % ConstantVariables.MLT_ADS_POSITION == 0) {
                    switch (ConstantVariables.MLT_ADS_TYPE) {
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
                int listingId = jsonDataObject.optInt("listing_id");
                int listingTypeId = jsonDataObject.optInt("listingtype_id");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                String ownerTitle = jsonDataObject.optString("owner_title");
                int featured = isCategoryResults ? 0 : jsonDataObject.optInt("featured");
                int sponsored = isCategoryResults ? 0 : jsonDataObject.optInt("sponsored");
                String creationDate = jsonDataObject.optString("creation_date");
                String location = jsonDataObject.optString("location");
                String price = jsonDataObject.optString("price");
                String currency = jsonDataObject.optString("currency");
                int allowToView = jsonDataObject.optInt("allow_to_view");
                int isClosed = jsonDataObject.optInt("closed");
                double longitude = jsonDataObject.optDouble("longitude");
                double latitude = jsonDataObject.optDouble("latitude");
                String categoryTitle = jsonDataObject.optString("categoryTitle");
                mBrowseItemList.add(new BrowseListItems(listingId, listingTypeId, title, image, ownerTitle,
                        creationDate, location, price, currency, allowToView == 1, isClosed, featured, sponsored, latitude, longitude, categoryTitle));
            }

            // Show End of Result Message when there are less results
            if (mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo) {
                mBrowseItemList.add(ConstantVariables.FOOTER_TYPE);
            }
        } else {
            String message = mContext.getResources().getString(R.string.no_text) + " " + mListingLabel.toLowerCase() + " " +
                    mContext.getResources().getString(R.string.available_text);

            if (mFeaturedCount > 0) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                messageLayout.setLayoutParams(layoutParams);
                int padding10 = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);

                messageLayout.setPadding(padding10, (int) mContext.getResources().getDimension(R.dimen.slider_view_height), padding10, padding10);

                if (PreferencesUtils.getDefaultLocation(mContext) != null && !PreferencesUtils.getDefaultLocation(mContext).isEmpty()) {
                    message = message + " " + mContext.getResources().getString(R.string.for_this_location_text);
                }
            }

            messageLayout.setVisibility(View.VISIBLE);
            messageLayout.bringToFront();
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText(mListingIcon);
            errorMessage.setText(message);
        }
    }

    public void addDataToMap(JSONObject response) {
        mDataResponse = response.optJSONArray("response");
        mapItemCount = response.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mapItemCount);

        if (mDataResponse != null && mDataResponse.length() > 0) {
            messageLayout.setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int listingId = jsonDataObject.optInt("listing_id");
                int listingTypeId = jsonDataObject.optInt("listingtype_id");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                String ownerTitle = jsonDataObject.optString("owner_title");
                int featured = isCategoryResults ? 0 : jsonDataObject.optInt("featured");
                int sponsored = isCategoryResults ? 0 : jsonDataObject.optInt("sponsored");
                String creationDate = jsonDataObject.optString("creation_date");
                String location = jsonDataObject.optString("location");
                String price = jsonDataObject.optString("price");
                String currency = jsonDataObject.optString("currency");
                int allowToView = jsonDataObject.optInt("allow_to_view");
                int isClosed = jsonDataObject.optInt("closed");
                double longitude = jsonDataObject.optDouble("longitude");
                double latitude = jsonDataObject.optDouble("latitude");
                String categoryTitle = jsonDataObject.optString("categoryTitle");

                mMapItemList.add(new BrowseListItems(listingId, listingTypeId, title, image, ownerTitle,
                        creationDate, location, price, currency, allowToView == 1, isClosed, featured, sponsored, latitude, longitude, categoryTitle));

            }
        } else {
            String message = mContext.getResources().getString(R.string.no_text) + " " + mListingLabel.toLowerCase() + " " +
                    mContext.getResources().getString(R.string.available_text);

            if (PreferencesUtils.getDefaultLocation(mContext) != null && !PreferencesUtils.getDefaultLocation(mContext).isEmpty()) {
                message = message + " " + mContext.getResources().getString(R.string.for_this_location_text);
            }

            messageLayout.setVisibility(View.VISIBLE);
            messageLayout.bringToFront();
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText(mListingIcon);
            errorMessage.setText(message);
            mMapView.setAlpha(0.1f);
        }
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            // Check if successful obtained the map.
                            if (mMap != null) {
                                setUpMap();
                            }
                        }
                    });

        } else {
            setUpMap();
        }
    }

    /**
     * Method to set layout manager according to type.
     *
     * @param mMLTBrowseType type of view to show MLT.
     */
    public void setLayoutManager(final int mMLTBrowseType) {
        try {
            LinearLayoutManager mLinearLayoutManager;
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
                    mMapView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    break;
                case ConstantVariables.MATRIX_VIEW:
                    mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (mBrowseMLTAdapter.getItemViewType(position)) {
                                case RecyclerViewAdapter.VIEW_ITEM:
                                case RecyclerViewAdapter.TYPE_FB_AD:
                                case RecyclerViewAdapter.TYPE_COMMUNITY_ADS:
                                case RecyclerViewAdapter.REMOVE_COMMUNITY_ADS:
                                    return 1;
                                case RecyclerViewAdapter.VIEW_PROG:
                                case RecyclerViewAdapter.HEADER_TYPE:
                                    return 2; //number of columns of the grid
                                default:
                                    return -1;
                            }
                        }
                    });
                    mMapView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    break;
                case ConstantVariables.LIST_VIEW:
                    mLinearLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
                    mMapView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    break;
                case ConstantVariables.MAP_VIEW:
                    mMapView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    if( getActivity().findViewById(R.id.create_fab) != null ) getActivity().findViewById(R.id.create_fab).setVisibility(View.GONE);
                    break;
            }

            if (mViewType != 4) {
                if (!mAppConst.isLoggedOutUser() &&
                        PreferencesUtils.getMLTCanCreate(mContext, mListingTypeId) == 1) {
                    if( getActivity().findViewById(R.id.create_fab) != null ) getActivity().findViewById(R.id.create_fab).setVisibility(View.VISIBLE);
                }
                mBrowseMLTAdapter = new RecyclerViewAdapter(getActivity(), mBrowseItemList, true, isShowHeader, mMLTBrowseType,
                        ConstantVariables.MLT_MENU_TITLE,
                        new OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {

                                BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                                boolean isAllowedToView = listItems.isAllowToView();

                                if (!isAllowedToView) {
                                    SnackbarUtils.displaySnackbar(((Activity)mContext).findViewById(android.R.id.content),
                                            mContext.getResources().getString(R.string.unauthenticated_view_message));
                                } else {
                                    Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                            mCurrentSelectedModule, null);
                                    mainIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, mListingTypeId);
                                    startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }

                            }
                        });
                mRecyclerView.setAdapter(mBrowseMLTAdapter);
                addScrollListener(mMLTBrowseType);
                if (spinner != null) spinner.setVisibility(View.VISIBLE);
            } else if (spinner != null) {
                spinner.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to add Scroll listener according to view type.
     *
     * @param mMLTViewType type of view to show MLT.
     */
    public void addScrollListener(final int mMLTViewType) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = 0, totalItemCount = 0, lastVisibleCount, visibleItemCount = 0;

                switch (mViewType) {
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
                        String url = getListUrl(mLoadingPageNo);
                        loadMoreData(url);
                    }
                }
            }
        });
    }

    private String getListUrl(int mLoadingPageNo) {
        String url;
        if (isAdvGroupsMLT) {
            url = mGroupMLTUrl + "&limit=" + AppConstant.LIMIT + "&page=" + mLoadingPageNo;
        } else if (isCategoryResults) {
            url = UrlUtil.BROWSE_CATEGORIES_MLT_URL + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo
                    + "&showListings=1";
            url = mAppConst.buildQueryString(url, postParams);
        } else {
            url = UrlUtil.BROWSE_MLT_URL + "limit=" + AppConstant.LIMIT + "&listingtype_id=" + mListingTypeId + "&page=" + mLoadingPageNo;
        }

        if (isMemberMLT) {
            url += "&user_id=" + mUserId;
        }

        if (!mListingFilter.isEmpty()) {
            url += "&listing_filter=" + mListingFilter;
        }
        isLoading = true;
        // Adding Search Params in the scrolling url
        if (searchParams != null && searchParams.size() != 0) {
            url = mAppConst.buildQueryString(url, searchParams);
        }
        return url;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantVariables.VIEW_PAGE_CODE) {
            PreferencesUtils.updateCurrentModule(mContext, "sitereview_listing");
            if (resultCode == ConstantVariables.VIEW_PAGE_CODE) {
                makeRequest();
            }
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0; i <= mBrowseItemList.size(); i++) {
            if (i != 0 && i % ConstantVariables.MLT_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseItemList.add(i, ad);
                mBrowseMLTAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        postParams.clear();


        switch (parent.getTag().toString()) {
            case "listingFilter":
                mSelectedItem = position;
                adapter.getCustomView(position, view, parent, mSelectedItem);
                switch (position) {
                    case 0:
                        mListingFilter = "all";
                        break;
                    case 1:
                        mListingFilter = "featured";
                        isMakeFirstCall = false;
                        break;
                    case 2:
                        mListingFilter = "sponsored";
                        isMakeFirstCall = false;
                        break;
                }
                if (!isMakeFirstCall) {
                    swipeRefreshLayout.setRefreshing(true);
                    makeRequest();
                }
                break;

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
                    postParams.put("subCategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    swipeRefreshLayout.setRefreshing(true);
                    makeRequest();

                } else {
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    subSubCategoryLayout.setVisibility(View.GONE);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    swipeRefreshLayout.setRefreshing(true);
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
                    postParams.put("subCategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    postParams.put("subsubcategory_id", mSubSubCategoryId);
                    swipeRefreshLayout.setRefreshing(true);
                    makeRequest();

                } else {
                    postParams.put("subCategory_id", mSubCategoryId);
                    postParams.put("category_id", String.valueOf(mCategoryId));
                    swipeRefreshLayout.setRefreshing(true);
                    makeRequest();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCommunityAdsLoaded(JSONArray advertisementsArray) {

        mAdvertisementsArray = advertisementsArray;

        if (!isAdLoaded && mAdvertisementsArray != null) {
            isAdLoaded = true;
            int j = 0;
            for (int i = 0; i <= mBrowseItemList.size(); i++) {
                if (i != 0 && i % ConstantVariables.MLT_ADS_POSITION == 0 &&
                        j < mAdvertisementsArray.length()) {
                    mBrowseItemList.add(i, addCommunityAddsToList(j));
                    j++;
                    mBrowseMLTAdapter.notifyDataSetChanged();
                }
            }
        } else if (mAdvertisementsArray != null && mBrowseItemList.size() > 0) {
            mBrowseItemList.remove(mBrowseItemList.size() - 1);
            mBrowseMLTAdapter.notifyItemRemoved(mBrowseItemList.size());
            addItemsToList(mBody);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewToggle:
                isLocationSet = false;
                BrowseMLTFragment.selectedViewType = mViewType = (mViewType == mSecondaryViewType) ? mMLTBrowseType : mSecondaryViewType;
                setLayoutManager(mViewType);
                getActivity().invalidateOptionsMenu();
                if (!isViewTypeLoaded.contains(String.valueOf(mViewType))) {
                    makeRequest();
                } else {
                    _showViewFromLocal();
                }
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu != null && menu.findItem(R.id.viewToggle) != null) {
            if (mSecondaryViewType > 0 && isBringToFront && isVisibleToUser) {
                menu.findItem(R.id.viewToggle).setVisible(true);
            } else {
                menu.findItem(R.id.viewToggle).setVisible(false);
            }

            int toggleType = (mViewType == mSecondaryViewType) ? mMLTBrowseType : mSecondaryViewType;
            switch (toggleType) {
                case ConstantVariables.GRID_VIEW:
                    Drawable gridDrawable = mContext.getResources().getDrawable(R.drawable.ic_grid_menu);
                    gridDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
                    menu.findItem(R.id.viewToggle).setIcon(gridDrawable);
                    break;
                case ConstantVariables.MATRIX_VIEW:
                    Drawable matrix = mContext.getResources().getDrawable(R.drawable.ic_two_columns_layout);
                    matrix.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
                    menu.findItem(R.id.viewToggle).setIcon(matrix);
                    break;
                case ConstantVariables.LIST_VIEW:
                    Drawable list = mContext.getResources().getDrawable(R.drawable.ic_list_dot);
                    list.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
                    menu.findItem(R.id.viewToggle).setIcon(list);
                    break;
                case ConstantVariables.MAP_VIEW:
                    Drawable map = mContext.getResources().getDrawable(R.drawable.ic_three_column);
                    map.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
                    menu.findItem(R.id.viewToggle).setIcon(map);
            }
            this.actionMenu = menu;
        }
        super.onPrepareOptionsMenu(menu);
    }

    private Bitmap getCircularBitmap(@NonNull Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = mContext.getResources().getColor(R.color.grey_dark);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void showQuickInfo(BrowseListItems listItem) {
        TextView txtclose, userTitle, userLocation, categoryTitle, bulletPoint;
        ImageView userThumb;
        quickInfoDialog = new Dialog(mContext);
        quickInfoDialog.setContentView(R.layout.mlt_quick_info_view);
        itemPosition = mMapItemList.indexOf(listItem);
        txtclose = quickInfoDialog.findViewById(R.id.txtclose);
        txtclose.setText("x");
        categoryTitle = quickInfoDialog.findViewById(R.id.mlt_category);
        categoryTitle.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        categoryTitle.setText("\uf105 " + listItem.getCategoryTitle());
        userTitle = quickInfoDialog.findViewById(R.id.mlt_title);
        userTitle.setTag(listItem.getmUserId());
        userTitle.setOnClickListener(this);
        bulletPoint = quickInfoDialog.findViewById(R.id.bullet_point);
        bulletPoint.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        bulletPoint.setText("\uf105");
        userLocation = quickInfoDialog.findViewById(R.id.mlt_location);
        userThumb = quickInfoDialog.findViewById(R.id.profile_image);
        userThumb.setTag(listItem.getmBrowseImgUrl());
        userTitle.setText(listItem.getmBrowseListTitle());
        userLocation.setOnClickListener(this);
        userLocation.setText(listItem.getmLocation());
        userThumb.setOnClickListener(this);
        Picasso.with(mContext)
                .load(listItem.getmBrowseImgUrl())
                .into(userThumb);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickInfoDialog.dismiss();
            }
        });

        Window window = quickInfoDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        quickInfoDialog.show();
    }

    /**
     * Method to set up the map by checking the required permission.
     */
    private void setUpMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<>(mContext, mMap);

        // Setting up the ClusterManager as onMarkerClickListener to make the the cluster item clickable.
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mClusterManager.onCameraIdle();
                mCurrentZoom = mMap.getCameraPosition().zoom;
            }
        });

        // Setting up the renderer for the cluster manager.
        mClusterManager.setRenderer(new DefaultClusterRenderer<MemberClusterItems>(mContext, mMap, mClusterManager) {
            @Override
            protected boolean shouldRenderAsCluster(Cluster cluster) {
                return cluster.getSize() > 1 && mCurrentZoom < 19;
            }

            @Override
            protected void onBeforeClusterItemRendered(MemberClusterItems item, MarkerOptions markerOptions) {
                markerOptions.icon(item.getBitmapDescriptor());
            }
        });

        // Applying the click listener on the each item of the Cluster. (Each marker clickable)
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MemberClusterItems>() {
            @Override
            public boolean onClusterItemClick(MemberClusterItems memberClusterItems) {
                showQuickInfo(memberClusterItems.getBrowseListItem());
                return true;
            }
        });

        setMarkersAtMemberLocations();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mapMyLocationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        mapMyLocationButton.setVisibility(View.GONE);
        setZoomControlUI();
    }

    /**
     * Method to set members as marker at the specified locations.
     */
    private void setMarkersAtMemberLocations() {
        mMap.clear();
        mClusterManager.clearItems();
        try {
            JSONObject userDetail = (PreferencesUtils.getUserDetail(mContext) != null) ? new JSONObject(PreferencesUtils.getUserDetail(mContext)) : null;

            /* Check the double value is valid or not before generating Lat Lng */
            if (userDetail != null && !Double.isNaN(userDetail.optDouble(PreferencesUtils.USER_LOCATION_LATITUDE))
                    && !Double.isNaN(userDetail.optDouble(PreferencesUtils.USER_LOCATION_LONGITUDE))
                    && userDetail.optDouble(PreferencesUtils.USER_LOCATION_LATITUDE) != 0
                    && userDetail.optDouble(PreferencesUtils.USER_LOCATION_LONGITUDE) != 0) {
                currentLatLng = new LatLng(userDetail.optDouble(PreferencesUtils.USER_LOCATION_LATITUDE),
                        userDetail.optDouble(PreferencesUtils.USER_LOCATION_LONGITUDE));

                locationTitle = PreferencesUtils.getDefaultLocation(mContext);

            }

            /* Display my location button if user already set device location
            or Enabled device gps*/
            if (GlobalFunctions.isLocationEnabled(mContext)) {
                myLocationButton.setVisibility(View.VISIBLE);
            } else if (locationTitle != null && locationTitle.isEmpty()){
                if (!isGpsRequestDisplayed && AppConstant.mLocationType != null && AppConstant.mLocationType.equals("notspecific")
                        && AppConstant.isDeviceLocationEnable == 1) {
                    if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (!PreferencesUtils.isLocationPermissionDisplayedOnMap(mContext)) {
                            PreferencesUtils.setLocationPermissionDisplayedOnMap(mContext);
                            mAppConst.requestForManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                    ConstantVariables.ACCESS_FINE_LOCATION);
                        }
                    } else {
                        isGpsRequestDisplayed = true;
                        GlobalFunctions.requestForDeviceLocation(mContext);
                    }
                }
                myLocationButton.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mMapItemList != null && mMapItemList.size() > 0) {
            for (i = 0; i < mMapItemList.size(); i++) {
                final BrowseListItems browseListItems = mMapItemList.get(i);

                if (browseListItems.mLatitude != 0 && browseListItems.mLongitude != 0) {
                    double lat = browseListItems.mLatitude;
                    double lng = browseListItems.mLongitude;
                    double offset = i / 60000d;
                    lat = lat + offset;
                    lng = lng + offset;

                    LatLng itemLatLng = new LatLng(lat, lng);
                    itemLatLng = (currentLatLng != null) ? currentLatLng : itemLatLng;

                    // Showing the current location in Google Map
                    if (itemLatLng != null && !isLocationSet) {
                        if (currentLatLng != null) {
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title(locationTitle));
                        }
                        isLocationSet = true;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(itemLatLng));
                    }

                    // Showing color of the marker as it is coming from the api response.
                    try {

                        final ImageView img = new ImageView(mContext);
                        final double finalLat = lat;
                        final double finalLng = lng;
                        Picasso.with(mContext).load(browseListItems.getmBrowseImgUrl()).resize(90, 90).
                                into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                        img.setDrawingCacheEnabled(false);
                                        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                                        Bitmap bitmap = drawable.getBitmap();
                                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCircularBitmap(bitmap));

                                        // Adding the moments position into Cluster item.
                                        MemberClusterItems memberClusterItems = new MemberClusterItems(finalLat, finalLng, bitmapDescriptor, null, browseListItems);
                                        mClusterManager.addItem(memberClusterItems);
                                        mClusterManager.cluster();
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        if (!isLocationSet && currentLatLng != null) {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title(locationTitle));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            isLocationSet = true;
        }
        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        mClusterManager.cluster();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mlt_location:
                openInMap(itemPosition);
                break;
            case R.id.mlt_title:
            case R.id.profile_image:
                BrowseListItems listItem = mMapItemList.get(itemPosition);
                Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItem.getmListItemId(),
                        mCurrentSelectedModule, null);
                mainIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listItem.getmListingTypeId());
                startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.view_more:
                readyToViewMore();
                break;
        }
    }

    private void openInMap(int itemPosition) {
        BrowseListItems listItem = mMapItemList.get(itemPosition);
        if (GlobalFunctions.isMapAppEnabled(mContext)) {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(listItem.getmLocation()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            Intent mapIntent = new Intent(mContext, MapActivity.class);
            mapIntent.putExtra("location", listItem.getmLocation());
            startActivity(mapIntent);
            ((Activity)mContext).overridePendingTransition(R.anim.slide_up_in, R.anim.push_up_out);

        }
    }

    public void setZoomControlUI(){
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Find map fragment
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        // Find ZoomControl view
        View zoomControls = mapFragment.getView().findViewById(0x1);

        if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

            // Align it to - parent top|left
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);
        }
    }
    private void readyToViewMore() {
        if ((AppConstant.LIMIT * mLoadingMapPageNo) < mapItemCount) {

            mLoadingMapPageNo += 1;
            String url = getListUrl(mLoadingMapPageNo) + "viewType=1";
            isLoading = true;
            // Adding Search Params in the scrolling url
            if (searchParams != null && searchParams.size() != 0) {
                url = mAppConst.buildQueryString(url, searchParams);
            }
            mAppConst.showProgressDialog();
            loadMoreData(url);
        } else {
            SnackbarUtils.displaySnackbar(((Activity)mContext).findViewById(android.R.id.content), mContext.getResources().getString(R.string.no_more_item_display));
        }
    }
    private void setDrawableColor(FloatingActionButton actionButton) {
        //get the drawable
        Drawable buttonSrc = mContext.getResources().getDrawable(R.drawable.ic_gps_fixed_24dp);
        //copy it in a new one
        Drawable drawable = buttonSrc.getConstantState().newDrawable();
        //set the color filter, you can use also Mode.SRC_ATOP
        drawable.mutate().setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        //set it to your fab button initialized before
        actionButton.setImageDrawable(drawable);
    }
}
