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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.adapters.StoreViewAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseStoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnItemClickListener, AdapterView.OnItemSelectedListener {

    private AppConstant mAppConst;
    private View rootView, mFilterView, mHeaderView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mBrowseAdapter;
    private List<Object> mStoreItemList;
    private JSONObject mBody;
    private JSONArray mDataResponse,mSubCategoryResponse = null,mSubSubCategoryResponse = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mBrowseStoreUrl,mCurrentSelectedModule;
    private StoreInfoModel mStoreInfo;
    private boolean isLoading = false, isSearchTextSubmitted = false,isMemberStore = false,isCategoryBased = false,isLoadSubCategory = true,isFirstRequest = true,isLoadSubSubcategory = true;
    private Snackbar snackbar;
    private int mLoadingPageNo = 1,pageNumber = 1,mUserId,mCategoryId;
    private Bundle mViewExtras;
    private HashMap<String, String> searchParams,postParams;
    private CardView subCategoryLayout, subSubCategoryLayout;
    private Spinner subCategorySpinner, subSubCategorySpinner;
    private SpinnerAdapter subCategoryAdapter, subSubCategoryAdapter;
    private int mSelectedItem = -1, mSubsubcategorySelectedItem = -1,mTotalItemCount = 0;
    private String mSubCategoryId, mSubSubCategoryId;

    public BrowseStoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseStoreFragment newInstance(String param1, String param2) {
        BrowseStoreFragment fragment = new BrowseStoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, container,false);
        mHeaderView = inflater.inflate(R.layout.layout_category_block, null, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(PreferencesUtils.getCurrentSelectedModule(mContext) != null
                && !PreferencesUtils.getCurrentSelectedModule(mContext).equals("core_main_sitestore")) {
            PreferencesUtils.updateCurrentModule(mContext,"core_main_sitestore");
        }
        mRecyclerView.setHasFixedSize(true);
        mStoreItemList = new ArrayList<>();
        mStoreInfo = new StoreInfoModel();
        mContext = getContext();
        mAppConst = new AppConstant(mContext);

        if (mAppConst.isLoggedOutUser())
            setHasOptionsMenu(true);

        mBrowseStoreUrl = UrlUtil.BROWSE_STORE_URL + "page="+pageNumber;
        if(getArguments() != null) {
            mViewExtras = getArguments();
            mUserId = mViewExtras.getInt("user_id");

            if(mUserId != 0){
                mBrowseStoreUrl += "&user_id=" + mUserId;
            }
            isMemberStore = mViewExtras.getBoolean("isMemberStore");
            isCategoryBased = mViewExtras.getBoolean(ConstantVariables.IS_CATEGORY_BASED_RESULTS,false);
            mCategoryId = mViewExtras.getInt(ConstantVariables.VIEW_PAGE_ID,0);
            if(!isMemberStore && !isCategoryBased) {
                Set<String> searchArgumentSet = getArguments().keySet();
                searchParams = new HashMap<>();
                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key);
                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
                    }
                }
                if (searchParams != null && searchParams.size() != 0) {
                    isSearchTextSubmitted = true;
                    mBrowseStoreUrl = mAppConst.buildQueryString(mBrowseStoreUrl, searchParams);
                }
            }

        }
        // getting header views, when it is laoded for category page.
        if(isCategoryBased){
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
        // The number of Columns
        mLayoutManager = new GridLayoutManager(mContext, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBrowseAdapter = new StoreViewAdapter(mContext,mStoreItemList,this);
        mRecyclerView.setAdapter(mBrowseAdapter);

        addListeners();
        getStoreResult();
        return rootView;
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
                            < mStoreInfo.getTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url;
                        if(isMemberStore){
                            url = UrlUtil.BROWSE_STORE_URL + "&page=" + mLoadingPageNo+ "&user_id=" + mUserId;
                        }else {
                            url = UrlUtil.BROWSE_STORE_URL + "&page=" + mLoadingPageNo;
                        }
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
    public void getStoreResult() {
        try {
            mLoadingPageNo = 1;
            if (!isSearchTextSubmitted && !isMemberStore && !isCategoryBased) {
                // Don't show data in case of searching and User Profile Tabs.
                mBrowseStoreUrl = UrlUtil.BROWSE_STORE_URL + "&page=" + pageNumber;
                mStoreItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext,DataStorage.SITE_STORE_FILE);
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
                    mBrowseAdapter.notifyDataSetChanged();
                }

            } else if (isMemberStore) {
                mBrowseStoreUrl = UrlUtil.BROWSE_STORE_URL + "&page=" + pageNumber + "&user_id=" + mUserId;
            } else if(isCategoryBased){
                mBrowseStoreUrl = UrlUtil.STORE_CATEGORY_URL + "&category_id=" + mCategoryId;
                mBrowseStoreUrl = mAppConst.buildQueryString(mBrowseStoreUrl, postParams);
            }

            mAppConst.getJsonResponseFromUrl(mBrowseStoreUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                    mStoreItemList.clear();
                    addDataToList(jsonObject);
                    // Don't save data in case of searching and User Profile Tabs.
                    if(!isSearchTextSubmitted && !isMemberStore) {
                        DataStorage.createTempFile(mContext,DataStorage.SITE_STORE_FILE, jsonObject.toString());
                    }
                    mBrowseAdapter.notifyDataSetChanged();
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
                                            getStoreResult();
                                        }
                                    });
                        } else {
                            SnackbarUtils.displaySnackbar(rootView, message);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mStoreItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mStoreItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mStoreItemList.remove(mStoreItemList.size() - 1);
                mBrowseAdapter.notifyItemRemoved(mStoreItemList.size());
                addDataToList(jsonObject);
                mBrowseAdapter.notifyItemInserted(mStoreItemList.size());
                isLoading = false;
                isFirstRequest = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }
    public void addDataToList(JSONObject jsonObject) {
        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");
        mStoreInfo.setTotalItemCount(mBody.optInt("totalItemCount"));
        if (isCategoryBased && mBody.optJSONArray("subCategories") != null) {
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
            } else if(!mBody.has("subsubCategories")){
                subSubCategoryLayout.setVisibility(View.GONE);
            }

            if(mTotalItemCount == 0 && isFirstRequest){
                subCategoryLayout.setVisibility(View.GONE);
                subSubCategoryLayout.setVisibility(View.GONE);
            }

        }
            if (isCategoryBased && mBody.optJSONObject("stores") != null) {
                mBody = mBody.optJSONObject("stores");
            }

            mDataResponse = mBody.optJSONArray("response");
            if (mDataResponse != null && mDataResponse.length() > 0) {
                for (int i = 0; i < mDataResponse.length(); i++) {
                    JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                    String store_id = jsonDataObject.optString("store_id");
                    String title = jsonDataObject.optString("title");
                    String image = jsonDataObject.optString("image");
                    String owner_image = jsonDataObject.optString("owner_image");
                    int owner_id = jsonDataObject.optInt("owner_id");
                    int featured = jsonDataObject.optInt("featured");
                    int sponsored = jsonDataObject.optInt("sponsored");
                    int like_count = jsonDataObject.optInt("like_count");
                    int comment_count = jsonDataObject.optInt("comment_count");
                    String category = jsonDataObject.optString("category_title");
                    mStoreItemList.add(new StoreInfoModel(store_id, title, image, category, owner_image,
                            owner_id, featured, sponsored, like_count, comment_count, jsonDataObject));
                }
                // Show End of Result Message when there are less results
                if (mStoreInfo.getTotalItemCount() <= AppConstant.LIMIT * mLoadingPageNo) {
                    mStoreItemList.add(ConstantVariables.FOOTER_TYPE);
                }

            } else {
                rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                TextView errorIcon = rootView.findViewById(R.id.error_icon);
                SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
                errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                errorIcon.setText("\uf187");
                errorMessage.setText(mContext.getResources().getString(R.string.no_store_available));
            }
            mBrowseAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
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
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getStoreResult();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        StoreInfoModel storeInfoModel = (StoreInfoModel) mStoreItemList.get(position);
        if(view.getTag() != null && view.getTag().equals("mainView")) {
            startActivityForResult(StoreUtil.getStoreViewPageIntent(mContext,storeInfoModel.getStoreId(), storeInfoModel.storeDetails),
                    ConstantVariables.VIEW_PAGE_CODE);
        }else {
            Intent intent = new Intent(mContext, userProfile.class);
            intent.putExtra(ConstantVariables.USER_ID,storeInfoModel.getOwnerId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
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
                    postParams.put("subcategory_id", mSubCategoryId);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getStoreResult();

                } else {
                    subCategoryLayout.setVisibility(View.VISIBLE);
                    subSubCategoryLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getStoreResult();
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
                    postParams.put("subsubcategory_id", mSubSubCategoryId);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getStoreResult();
                } else {
                    postParams.put("subcategory_id", mSubCategoryId);
                    mSwipeRefreshLayout.setRefreshing(true);
                    getStoreResult();
                }
                break;


        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
