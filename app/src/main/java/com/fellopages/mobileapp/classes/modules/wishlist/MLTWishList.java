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

package com.fellopages.mobileapp.classes.modules.wishlist;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesRecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GridSpacingItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MLTWishList extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private View mRootView;
    private AppConstant mAppConst;
    private Context mContext;
    private TextView errorIcon;
    private SelectableTextView errorMessage;
    private LinearLayout messageLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseWishlistAdapter;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private boolean isLoading = false , isSearchTextSubmitted = false,isVisibleToUser = false;
    private int mLoadingPageNo = 1;
    private String mBrowseWishlistUrl;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HashMap<String, String> searchParams = new HashMap<>();

    public static MLTWishList newInstance(Bundle bundle){
        MLTWishList fragment = new MLTWishList();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mAppConst = new AppConstant(getActivity());
        int NUM_OF_COLUMNS = AppConstant.getNumOfColumns(mContext);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mBrowseWishlistUrl = UrlUtil.BROWSE_WISHLIST_URL + "&page=" + mLoadingPageNo;

        // Inflating recycler layout.
        mRootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // No data message views
        messageLayout = mRootView.findViewById(R.id.message_layout);
        errorIcon = mRootView.findViewById(R.id.error_icon);
        errorMessage = mRootView.findViewById(R.id.error_message);
        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        if (NUM_OF_COLUMNS > 1)
            mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                R.dimen.loading_bar_height, mRecyclerView, true));
        else
            mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                    R.dimen.margin_2dp, mRecyclerView, false));

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, NUM_OF_COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBrowseWishlistAdapter = new AdvModulesRecyclerViewAdapter(mContext, mBrowseItemList, "wishlist", 0,
                MLTWishList.this, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                        Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                PreferencesUtils.getCurrentSelectedModule(mContext), null);
                        mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, listItems.getmBrowseListTitle());
                        mainIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,"sitereview_wishlist");
                        startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
        mRecyclerView.setAdapter(mBrowseWishlistAdapter);

        addScrollListener();

        // Getting arguments from search query
        if(getArguments() != null) {

            Set<String> searchArgumentSet = getArguments().keySet();
            for (String key : searchArgumentSet) {
                String value = getArguments().getString(key);
                if (value != null && !value.isEmpty()) {
                    searchParams.put(key, value);
                }
            }
            if(searchParams != null && searchParams.size() != 0) {
                isSearchTextSubmitted = true;
                mBrowseWishlistUrl = mAppConst.buildQueryString(mBrowseWishlistUrl, searchParams);
            }

        }

        swipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(getArguments() != null) {
            if (PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                    !PreferencesUtils.getCurrentSelectedModule(mContext).equals("sitereview_wishlist")) {
                PreferencesUtils.updateCurrentModule(mContext, "sitereview_wishlist");
            }
            makeRequest();
        }
        return mRootView;
    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            makeRequest();
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible() && isVisible && isVisibleToUser) {
            // Updating current selected module
            if (PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                    !PreferencesUtils.getCurrentSelectedModule(mContext).equals("sitereview_wishlist")) {
                PreferencesUtils.updateCurrentModule(mContext, "sitereview_wishlist");
            }

        }
    }
    public void addScrollListener() {
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
                        String url = UrlUtil.BROWSE_WISHLIST_URL + "&page=" + mLoadingPageNo;
                        if (!isSearchTextSubmitted)
                            url += "&orderby=wishlist_id";
                        isLoading = true;
                        // Adding Search Params in the scrolling url
                        if(searchParams != null && searchParams.size() != 0){
                            url = mAppConst.buildQueryString(url, searchParams);
                        }
                        loadMoreData(url);
                    }
                }
            }
        });
    }

    /**
     * Method to send request to server to get browse page data.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;
        if (!isSearchTextSubmitted) {

            try {
                mBrowseWishlistUrl = UrlUtil.BROWSE_WISHLIST_URL + "&page=" + mLoadingPageNo + "&orderby=wishlist_id";

                // Don't show data in case of searching.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.MLT_WISHLIST_FILE);
                if (tempData != null) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(tempData);
                    addItemsToList(jsonObject);
                    mBrowseWishlistAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

            mAppConst.getJsonResponseFromUrl(mBrowseWishlistUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (snackbar != null && snackbar.isShown())
                        snackbar.dismiss();
                    isVisibleToUser = true;
                    mBrowseItemList.clear();
                    addItemsToList(jsonObject);
                    mBrowseWishlistAdapter.notifyDataSetChanged();

                    // Don't save data in cashing in case of searching.
                    if (!isSearchTextSubmitted) {
                        DataStorage.createTempFile(mContext, DataStorage.MLT_WISHLIST_FILE, jsonObject.toString());
                    }
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);

                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), mRootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    mRecyclerView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
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
        mBrowseWishlistAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mBrowseWishlistAdapter.notifyItemRemoved(mBrowseItemList.size());

                addItemsToList(jsonObject);
                mBrowseWishlistAdapter.notifyItemInserted(mBrowseItemList.size());
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
    int j=0;
    public void addItemsToList(JSONObject jsonObject){

        String firstListingImage = null, secondListingImage = null, thirdListingImage = null;

        int mTotalItemCount = jsonObject.optInt("totalItemCount");

        mBrowseList.setmTotalItemCount(mTotalItemCount);
        JSONArray mDataResponse = jsonObject.optJSONArray("response");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            messageLayout.setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int wishlistId = jsonDataObject.optInt("wishlist_id");
                String title = jsonDataObject.optString("title");
                String creationDate = jsonDataObject.optString("creation_date");
                int totalItem = jsonDataObject.optInt("total_item");
                int liked = jsonDataObject.optInt("isLike");
                int followed = jsonDataObject.optInt("followed");
                JSONArray menuArray = jsonDataObject.optJSONArray("gutterMenu");

                // Getting listing images.
                if (jsonDataObject.has("images_1")) {
                    JSONObject firstListingImageObject = jsonDataObject.optJSONObject("images_1");
                    firstListingImage = firstListingImageObject.optString("image");
                }
                if (jsonDataObject.has("images_0")) {
                    JSONObject firstListingImageObject = jsonDataObject.optJSONObject("images_0");
                    firstListingImage = firstListingImageObject.optString("image");
                }
                if (jsonDataObject.has("images_2")) {
                    JSONObject secondListingImageObject = jsonDataObject.optJSONObject("images_2");
                    secondListingImage = secondListingImageObject.optString("image");
                }
                if (jsonDataObject.has("images_3")) {
                    JSONObject thirdListingImageObject = jsonDataObject.optJSONObject("images_3");
                    thirdListingImage = thirdListingImageObject.optString("image");
                }
                mBrowseItemList.add(new BrowseListItems(wishlistId, liked, followed, title, creationDate, totalItem,
                        firstListingImage, secondListingImage, thirdListingImage, menuArray));
            }
        }else {
            messageLayout.setVisibility(View.VISIBLE);
            errorIcon.setText("\uf046");
            errorMessage.setText(mContext.getResources().getString(R.string.no_wishlist));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantVariables.VIEW_PAGE_CODE) {
            PreferencesUtils.updateCurrentModule(mContext, "sitereview_wishlist");
            makeRequest();
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
                makeRequest();
            }
        });
    }

}
