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

package com.fellopages.mobileapp.classes.modules.store.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.Tasks;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;

import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GridSpacingItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.modules.store.ProductViewPage;
import com.fellopages.mobileapp.classes.modules.store.StoreViewPage;
import com.fellopages.mobileapp.classes.modules.store.adapters.ProductViewAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;


public class BrowseProductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, OnItemClickListener, AdapterView.OnItemSelectedListener {

    private View rootView, mFilterView, mHeaderView, bottomSheet;
    private AppConstant mAppConst;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private JSONObject mBody,mSortingFieldObject;
    private JSONArray mDataResponse, mSubCategoryResponse = null, mSubSubCategoryResponse = null;
    private List<Object> mProductList;
    private ProductInfoModel mProductInfo;
    private ProductViewAdapter mProductViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CardView subCategoryLayout, subSubCategoryLayout;
    private Spinner subCategorySpinner, subSubCategorySpinner;
    private SpinnerAdapter subCategoryAdapter, subSubCategoryAdapter;
    private int mSelectedItem = -1, mSubsubcategorySelectedItem = -1;
    private String mBrowseProductUrl,mDefaultCurrency;
    private Bundle mViewExtras;
    private Snackbar snackbar;
    private LinearLayout mSortingView,mApplyFilterView;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private boolean isLoading = false, isSearchTextSubmitted = false,isMemberListing = false,isFilterLoaded = false;
    private boolean isVisibleToUser = false,isSortingApplied = false;
    private HashMap<String, String> searchParams;
    private Map<String,View> mRadioButtonFields;
    private Map<String,Boolean> mRadioButtonValueMap;
    private BottomSheetBehavior<View> behavior, sortViewBehavior;
    private TextView mApplyFilterButton;
    private NestedScrollView filterView;

    FloatingActionButton mFilterButton;
    private int mPrevScrollY = 0;
    private int mFooterDiffTotal = 0;
    private int mMinFooterTranslation;
    // For the categories.
    private boolean isCategoryResults = false, isLoadSubCategory = true, isLoadSubSubcategory = true,
            isFirstRequest = true, isSelectProduct = false;
    private int mCategoryId;
    private String mSubCategoryId, mSubSubCategoryId;
    private Map<String, String> postParams;
    public  ArrayList<String> selectedProducts = new ArrayList<>();
    private BottomSheetDialog sheetDialog;
    private boolean isManageView;
    private View footerView;

    public BrowseProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BrowseStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseProductFragment newInstance(Bundle args) {
        BrowseProductFragment fragment = new BrowseProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout,container, false);
        mHeaderView = inflater.inflate(R.layout.layout_category_block, null, false);
        mFilterView = getActivity().findViewById(R.id.quick_return_footer_ll);
        mFilterButton = getActivity().findViewById(R.id.filter_fab);
        mAppConst = new AppConstant(mContext);
        mProductInfo = new ProductInfoModel();
        mProductList = new ArrayList<>();
        mRadioButtonValueMap = new HashMap<>();
        mContext = getContext();

        if(mFilterView!= null || mFilterButton != null) {
            getActivity().findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
            bottomSheet = getActivity().findViewById(R.id.bottom_sheet);

            behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setHideable(true);
            behavior.setSkipCollapsed(true);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            getActivity().findViewById(R.id.close_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAppConst.hideKeyboard();
                    behavior.setSkipCollapsed(true);
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            });
            if(mFilterButton != null) mFilterButton.setOnClickListener(this);

            sortViewBehavior = BottomSheetBehavior.from(bottomSheet);
            sortViewBehavior.setHideable(true);
            sortViewBehavior.setSkipCollapsed(true);
            sortViewBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mBrowseProductUrl = UrlUtil.BROWSE_PRODUCTS_URL +"?limit=" + AppConstant.LIMIT;
        if(getArguments() != null) {
            mViewExtras = getArguments();
            isSelectProduct = mViewExtras.getBoolean(ConstantVariables.SELECT_PRODUCT,false);
            isCategoryResults = mViewExtras.getBoolean(ConstantVariables.IS_CATEGORY_BASED_RESULTS, false);
            isManageView = mViewExtras.getBoolean(ConstantVariables.IS_MANAGE_VIEW, false);
            if(mViewExtras.containsKey("store_id")) {
                isMemberListing = true;
                mBrowseProductUrl = UrlUtil.BROWSE_PRODUCTS_URL + "?store_id=" + mViewExtras.getInt("store_id")
                        + "&limit=" + AppConstant.LIMIT;

            } else if (isCategoryResults) {
                mCategoryId = mViewExtras.getInt(ConstantVariables.VIEW_PAGE_ID);
                mBrowseProductUrl = UrlUtil.BROWSE_PRODUCT_CATEGORIES + "?category_id=" + mCategoryId;
            } else {

                Set<String> searchArgumentSet = getArguments().keySet();
                if (searchArgumentSet != null && !searchArgumentSet.isEmpty()) {
                    searchParams = new HashMap<>();
                    for (String key : searchArgumentSet) {
                        String value = getArguments().get(key) instanceof String
                                ? getArguments().getString(key) : null;
                        if (value != null && !value.isEmpty()) {
                            searchParams.put(key, value);
                        }
                    }
                }
                if (searchParams != null && searchParams.size() != 0) {
                    isSearchTextSubmitted = true;
                    mBrowseProductUrl = mAppConst.buildQueryString(mBrowseProductUrl, searchParams);
                }
            }

        }
        if (isManageView) {
            mBrowseProductUrl = UrlUtil.MANAGE_PRODUCTS_URL +"?limit=" + AppConstant.LIMIT;
        } else if (!isCategoryResults && !isSelectProduct && !isSearchTextSubmitted){
            mApplyFilterButton = getActivity().findViewById(R.id.update_cart);
            mApplyFilterButton.setText(mContext.getResources().getString(R.string.filter_apply_btn));
            mApplyFilterButton.setOnClickListener(this);
        }
        if(!isMemberListing){
            if(PreferencesUtils.getCurrentSelectedModule(mContext) != null
                    && !PreferencesUtils.getCurrentSelectedModule(mContext).equals("core_main_sitestoreproduct")) {
                PreferencesUtils.updateCurrentModule(mContext,"core_main_sitestoreproduct");
            }
        }

        // getting header views, when it is laoded for category page.
        if(isCategoryResults){
            postParams = new HashMap<>();
            subCategoryLayout = mHeaderView.findViewById(R.id.categoryFilterLayout);
            subSubCategoryLayout = mHeaderView.findViewById(R.id.subCategoryFilterLayout);
            subCategorySpinner = subCategoryLayout.findViewById(R.id.filter_view);
            subSubCategorySpinner = subSubCategoryLayout.findViewById(R.id.filter_view);
            mHeaderView.findViewById(R.id.mlt_category_block).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.toolbar).setVisibility(View.GONE);
            // Adding header view to main view.
            RelativeLayout mainView = rootView.findViewById(R.id.main_view_recycler);
            mainView.addView(mHeaderView);
            CustomViews.addHeaderView(R.id.mlt_category_block, mSwipeRefreshLayout);
            mHeaderView.findViewById(R.id.mlt_category_block).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

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
            subCategorySpinner.setOnItemSelectedListener(this);
            subSubCategorySpinner.setOnItemSelectedListener(this);
        }

        mLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                R.dimen.padding_1dp, mRecyclerView, true));
        mProductViewAdapter = new ProductViewAdapter(mContext,mProductList,false,this, BrowseProductFragment.this);
        mRecyclerView.setAdapter(mProductViewAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mProductViewAdapter.getItemViewType(position)) {
                    case ProductViewAdapter.VIEW_ITEM:
                        return 1;
                    case ProductViewAdapter.VIEW_PROG:
                        return 2;//number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        isFilterLoaded = false;
        getProductItems();
        if(mFilterView != null && !isManageView) {
            mFilterView.setVisibility(View.VISIBLE);
            mSortingView = mFilterView.findViewById(R.id.sorting_view);
            mApplyFilterView = mFilterView.findViewById(R.id.filter_apply_view);
            mSortingView.setOnClickListener(this);
            mApplyFilterView.setOnClickListener(this);
            mMinFooterTranslation = getActivity().getResources().getDimensionPixelSize(R.dimen.home_icon_tab_height);
            ViewCompat.setElevation(mFilterView, 12);
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrollY = getScrollY(recyclerView, 2);
                int diff = mPrevScrollY - scrollY;
                if (diff != 0 && mFilterView != null) {
                    if (diff < 0) { // scrolling down
                        mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
                    } else { // scrolling up
                        mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
                    }

                    mFilterView.setTranslationY(-mFooterDiffTotal);
                }

                final GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();
                int lastVisibleCount = gridLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit=firstVisibleItem+visibleItemCount;

                if(limit==totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)  <
                            mProductInfo.getTotalProductCount()){

                        mLoadingPageNo = mLoadingPageNo + 1;
                        isLoading = true;
                        loadMoreData(mBrowseProductUrl + "&page=" + mLoadingPageNo);
                    }

                }
                mPrevScrollY = scrollY;
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mFilterView != null){
                    int midFooter = mMinFooterTranslation / 2;
                    if (-mFooterDiffTotal > 0 && -mFooterDiffTotal < midFooter) { // slide up
                        ObjectAnimator anim = ObjectAnimator.ofFloat(mFilterView, "translationY",
                                mFilterView.getTranslationY(), 0);
                        anim.setDuration(100);
                        anim.start();
                        mFooterDiffTotal = 0;
                    } else if (-mFooterDiffTotal < mMinFooterTranslation && -mFooterDiffTotal >= midFooter) { // slide down
                        ObjectAnimator anim = ObjectAnimator.ofFloat(mFilterView, "translationY",
                                mFilterView.getTranslationY(), mMinFooterTranslation);
                        anim.setDuration(100);
                        anim.start();
                        mFooterDiffTotal = -mMinFooterTranslation;
                    }
                }
            }
        });

        return rootView;
    }
    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isManageView && isVisible && mFilterView != null) {
            mFilterView.setVisibility(View.GONE);
        } else if (isVisible && mFilterView != null){
            mFilterView.setVisibility(View.VISIBLE);
        }
    }
    public void getProductItems() {
        try {
            mLoadingPageNo = 1;
            if (!isSearchTextSubmitted && !isMemberListing && !isSortingApplied && !isCategoryResults && !isManageView) {
                // Don't show data in case of searching and User Profile Tabs.
                mBrowseProductUrl = UrlUtil.BROWSE_PRODUCTS_URL + "?limit=" + AppConstant.LIMIT;
                mProductList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.SITE_STORE_PRODUCT_FILE);
                if (tempData != null) {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(tempData);
                    addDataToList(jsonObject);
                    mProductViewAdapter.notifyDataSetChanged();
                }
            }

            if (isCategoryResults) {
                mBrowseProductUrl = UrlUtil.BROWSE_PRODUCT_CATEGORIES + "?category_id=" + mCategoryId;
                mBrowseProductUrl = mAppConst.buildQueryString(mBrowseProductUrl, postParams);
            }

            mAppConst.getJsonResponseFromUrl(mBrowseProductUrl + "&page=" + pageNumber, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProductList.clear();
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                    isVisibleToUser = true;
                    addDataToList(jsonObject);
                    // Don't save data in case of searching and User Profile Tabs.
                    if(!isSearchTextSubmitted && !isMemberListing && !isSortingApplied && !isCategoryResults && !isManageView) {
                        DataStorage.createTempFile(mContext,DataStorage.SITE_STORE_PRODUCT_FILE, jsonObject.toString());
                    }
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    mSwipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, ConstantVariables.REFRESH_DELAY_TIME);
                    try {
                        if (isRetryOption) {
                            snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, rootView, message,
                                    new SnackbarUtils.OnSnackbarActionClickListener() {
                                        @Override
                                        public void onSnackbarActionClick() {
                                            rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                            getProductItems();
                                        }
                                    });
                        } else {
                            SnackbarUtils.displaySnackbar(rootView, message);
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDataToList(JSONObject jsonObject){
        mBody = jsonObject;
        mDefaultCurrency = mBody.optString("currency");
        mSortingFieldObject = mBody.optJSONObject("orderby");
        if(isCategoryResults){
            mTotalItemCount = mBody.optInt("totalProductCount");
            mProductInfo.setTotalProductCount(mTotalItemCount);
            /**
             * Show Sub Categories of the selected category
             */
            if (isLoadSubCategory) {
                mSubCategoryResponse = mBody.optJSONArray("subCategories");

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
            if (mBody.has("subsubCategories") && isLoadSubSubcategory) {
                mSubSubCategoryResponse = mBody.optJSONArray("subsubCategories");
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

            if(mTotalItemCount == 0 && isFirstRequest){
                subCategoryLayout.setVisibility(View.GONE);
                subSubCategoryLayout.setVisibility(View.GONE);
            }
        } else {
            mTotalItemCount = mBody.optInt("totalItemCount");
            mProductInfo.setTotalProductCount(mTotalItemCount);
        }

        mDataResponse = mBody.optJSONArray("response");
        if(mDataResponse != null && mDataResponse.length() > 0 ) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int product_id = jsonDataObject.optInt("product_id");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                int store_id = jsonDataObject.optInt("store_id");
                int featured = jsonDataObject.optInt("featured");
                int sponsored = jsonDataObject.optInt("sponsored");
                int newLabel = jsonDataObject.optInt("newlabel");
                double ratingAvg = jsonDataObject.optDouble("rating_avg");
                int user_type = jsonDataObject.optInt("user_type");
                JSONObject priceObject = jsonDataObject.optJSONObject("information").optJSONObject("price");
                double discount_value = priceObject.optDouble("discount_amount");
                double discount_amount = priceObject.optDouble("discounted_amount");
                int discount = priceObject.optInt("discount");
                double price = priceObject.optDouble("price");
                JSONArray wishListArray = jsonDataObject.optJSONArray("wishlist");
                JSONArray menu = jsonDataObject.optJSONArray("menu");
                boolean isChecked = false;
                if(FormActivity.selectedProducts != null && FormActivity.selectedProducts.contains(String.valueOf(product_id))){
                    isChecked = true;
                }
                mProductList.add(new ProductInfoModel(store_id, product_id, title, image, ratingAvg, price,
                        discount_amount, discount, user_type, discount_value, featured, sponsored,
                        newLabel, mDefaultCurrency, wishListArray!= null, isSelectProduct, isChecked, menu));
            }
            // Show End of Result Message when there are less results
            if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                mProductList.add(ConstantVariables.FOOTER_TYPE);
            }

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById
                    (R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf291");
            errorMessage.setText(mContext.getResources().getString(R.string.no_products_available));

        }
        mProductViewAdapter.notifyDataSetChanged();
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
    public void onRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getProductItems();
            }
        });
    }


    /* Load More Data On Scroll */

    public void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        Tasks.call(new Callable<Void>() {
            @Override
            public Void call() {
                mProductList.add(null);
                mProductViewAdapter.notifyItemInserted(mProductList.size() - 1);
                return null;
            }
        });
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mProductList.remove(mProductList.size() - 1);
                mProductViewAdapter.notifyItemRemoved(mProductList.size());
                addDataToList(jsonObject);
                mProductViewAdapter.notifyItemInserted(mProductList.size());
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sorting_view:
                if(mSortingFieldObject != null) {
                    showSortingDialog();
                }
                break;
            case R.id.filter_fab:
            case R.id.filter_apply_view:
                itemFilter();
                break;

            case R.id.update_cart:
                mAppConst.hideKeyboard();
                behavior.setHideable(true);
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                isSearchTextSubmitted = true;
                if(mFilterButton != null){
                    searchParams = ((StoreViewPage) getActivity()).save();
                }else {
                    searchParams = ((MainActivity) getActivity()).save();
                }
                mBrowseProductUrl = mAppConst.buildQueryString(mBrowseProductUrl, searchParams);
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        getProductItems();
                    }
                });
                break;
        }

    }

    private void itemFilter() {
        filterView = getActivity().findViewById(R.id.product_filter_view);
        if(!isFilterLoaded) {
            mAppConst.getJsonResponseFromUrl(UrlUtil.PRODUCT_FILTER_URL, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    isFilterLoaded = true;
                    filterView.removeAllViews();
                    if(mFilterButton != null){
                        filterView.addView(((StoreViewPage) getActivity()).generateForm(jsonObject, true,
                                "core_main_sitestoreproduct"));
                    }else {
                        filterView.addView(((MainActivity) getActivity()).generateForm(jsonObject, true,
                                "core_main_sitestoreproduct"));
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    getActivity().findViewById(R.id.filterProgress).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(filterView, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                }
                            });
                }
            });
        }
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getProductItems();
    }

    public void showSortingDialog() {
        if (mSortingFieldObject == null) return;
        mRadioButtonFields =new HashMap<>();
        View bottomSheetView = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_view, null);
        LinearLayout customFieldBlock = bottomSheetView.findViewById(R.id.custom_fields_block);
        Iterator<?> keys = mSortingFieldObject.keys();
        final RadioGroup radioGroup = new RadioGroup(mContext);
        radioGroup.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        radioGroup.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp));
        while( keys.hasNext() ) {
            LinearLayout subView = new LinearLayout(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            subView.setLayoutParams(params);
            subView.setOrientation(LinearLayout.HORIZONTAL);
            AppCompatTextView checkIcon = new AppCompatTextView(mContext);
            checkIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_check_circle), null);
            setTextViewDrawableColor(checkIcon, ContextCompat.getColor(mContext, R.color.themeButtonColor));
            checkIcon.setLayoutParams(params);
            checkIcon.setGravity(Gravity.RIGHT);
            checkIcon.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_15dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp));
            AppCompatRadioButton radioButton = new AppCompatRadioButton(mContext);
            radioButton.setButtonDrawable(android.R.color.transparent);
            String key = (String) keys.next();
            radioButton.setText(mSortingFieldObject.optString(key));
            radioButton.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_large_font_size));
            radioButton.setTag(key);
            radioButton.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if(isChecked) {
                        isSortingApplied = true;
                        mBrowseProductUrl = UrlUtil.BROWSE_PRODUCTS_URL + "?limit=" + AppConstant.LIMIT
                                + "&orderby=" + compoundButton.getTag();
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(true);
                                getProductItems();
                            }
                        });
                    }
                    if(!mRadioButtonValueMap.isEmpty() && !mRadioButtonFields.isEmpty() ){
                        for (Map.Entry<String,View> entry : mRadioButtonFields.entrySet()) {
                            RadioButton value = (RadioButton) entry.getValue();
                            value.setChecked(false);
                            mRadioButtonFields.clear();
                        }
                    }
                    mRadioButtonValueMap.clear();
                    mRadioButtonValueMap.put(compoundButton.getTag().toString(),true);
                    sheetDialog.cancel();
                }
            });
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.title_font_size));
            subView.addView(radioButton);
            if(mRadioButtonValueMap.get(key) != null && mRadioButtonValueMap.get(key)) {
                mRadioButtonFields.put(key,radioButton);
                radioButton.setChecked(true);
                subView.addView(checkIcon);
            }

            radioGroup.addView(subView);
            radioButton.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);

        }
        customFieldBlock.addView(radioGroup);
        bottomSheetView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        customFieldBlock.setVisibility(View.VISIBLE);
        sheetDialog = new BottomSheetDialog(mContext);
        sheetDialog.setContentView(bottomSheetView);
        sheetDialog.show();

    }
    @Override
    public void onItemClick(View view, int position) {
        ProductInfoModel productInfo = (ProductInfoModel) mProductList.get(position);
        if(isSelectProduct && view.getTag().equals("mainview")) {
            manageSelected(productInfo.getProductId(),!productInfo.isSelectProductChecked());
            productInfo.setSelectProductCheckedValue(!productInfo.isSelectProductChecked());
            mProductViewAdapter.notifyItemChanged(position);
        } else if(view.getTag().equals("mainview")) {
            Intent intent = new Intent(mContext, ProductViewPage.class);
            intent.putExtra("store_id", productInfo.getStoreId());
            intent.putExtra("product_id", productInfo.getProductId());
            startActivityForResult(intent,ConstantVariables.VIEW_PAGE_CODE);
        }else {
            Intent intent = new Intent(mContext, CreateNewEntry.class);
            intent.putExtra(ConstantVariables.CREATE_URL, UrlUtil.ADD_TO_WISHLIST_STORE
                    +productInfo.getProductId());
            intent.putExtra(ConstantVariables.FORM_TYPE, "add_wishlist");
            intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PRODUCT_MENU_TITLE);
            startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
        }

    }
    private static Dictionary<Integer, Integer> sRecyclerViewItemHeights = new Hashtable<Integer, Integer>();
    public static int getScrollY(RecyclerView rv, int columnCount) {
        View c = rv.getChildAt(0);
        if (c == null) {
            return 0;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int scrollY = -(c.getTop());
        if (columnCount > 1) {
            sRecyclerViewItemHeights.put(firstVisiblePosition, c.getHeight() + dp2px(rv.getContext(), 8) / columnCount);
        } else {
            sRecyclerViewItemHeights.put(firstVisiblePosition, c.getHeight());
        }

        if (scrollY < 0)
            scrollY = 0;

        for (int i = 0; i < firstVisiblePosition; ++i) {
            if (sRecyclerViewItemHeights.get(i) != null) // (this is a sanity check)
                scrollY += sRecyclerViewItemHeights.get(i); //add all heights of the views that are gone
        }

        return scrollY;
    }

    public static int dp2px(Context context, int dp) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);

        return (int) (dp * displaymetrics.density + 0.5f);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        postParams.clear();
        switch (parent.getTag().toString()){

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
                    mSwipeRefreshLayout.setRefreshing(true);
                    getProductItems();

                } else {
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    subSubCategoryLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getProductItems();
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
                    postParams.put("subsubcategory_id", mSubSubCategoryId);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getProductItems();

                } else {
                    postParams.put("subCategory_id", mSubCategoryId);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getProductItems();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public ArrayList<String> getSelectedProducts(){
        return selectedProducts;
    }
    public void manageSelected(int productId, boolean isSelected){
        if (isSelected) {
            selectedProducts.add(String.valueOf(productId));
        } else {
            selectedProducts.remove(String.valueOf(productId));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                itemFilter();
                break;
        }
        return false;
    }
    private void setTextViewDrawableColor(AppCompatTextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }
}
