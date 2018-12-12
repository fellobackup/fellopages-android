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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GridSpacingItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.store.adapters.ProductViewAdapter;
import com.fellopages.mobileapp.classes.modules.store.fragments.BrowseProductFragment;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WishlistView extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnOptionItemClickResponseListener, OnItemClickListener {

    private Context mContext;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseListItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseAdapter;
    private RelativeLayout mRootView;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private String mWishlistViewUrl;
    private String mShareImageUrl, mContentUrl, title, currentModule;
    private boolean isLoading = false;
    private int mLoadingPageNo = 1, mWishListId;
    private JSONObject mBody;
    private JSONArray mGutterMenus;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);

        mContext = WishlistView.this;
        mAppConst = new AppConstant(mContext);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        View headerView = getLayoutInflater().inflate(R.layout.toolbar, null, false);

        /* Create Back Button On Action Bar **/
        mToolbar = (Toolbar) headerView.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE));
        }

        CustomViews.createMarqueeTitle(mContext, mToolbar);

        /** Getting Intent Key's. **/
        mWishlistViewUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        currentModule = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        // Getting Views
        mRootView = (RelativeLayout) findViewById(R.id.main_view_recycler);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        // Adding header view to main view.
        mRootView.addView(headerView);
        CustomViews.addHeaderView(R.id.toolbar, swipeRefreshLayout);
        headerView.findViewById(R.id.toolbar).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Setting Layout Manager, adapter and scroll listener.
        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, AppConstant.getNumOfColumns(mContext));
        if (AppConstant.getNumOfColumns(mContext) > 1) {
            mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                    R.dimen.margin_2dp, mRecyclerView, true));
        } else {
            mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                    R.dimen.margin_2dp, mRecyclerView, false));
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mBrowseAdapter);
        if (currentModule.equals("sitestoreproduct_wishlist")) {
            mWishlistViewUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING) + "?page=1";
            mRecyclerView.addItemDecoration(new GridSpacingItemDecorationUtil(mContext,
                    R.dimen.margin_1dp, mRecyclerView, true));
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mBrowseAdapter = new ProductViewAdapter(mContext, mBrowseItemList, false, this, null);
            mRecyclerView.setAdapter(mBrowseAdapter);

        } else {
            mBrowseAdapter = new RecyclerViewAdapter(mContext, mBrowseItemList, false, 2, mBrowseList,
                    ConstantVariables.MLT_WISHLIST_MENU_TITLE,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                            boolean isAllowedToView = listItems.isAllowToView();

                            if (!isAllowedToView) {
                                SnackbarUtils.displaySnackbar(mRootView,
                                        mContext.getResources().getString(R.string.unauthenticated_view_message));
                            } else {
                                Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                                        ConstantVariables.MLT_MENU_TITLE, null);
                                mainIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listItems.getmListingTypeId());
                                startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }
                    },
                    new OnItemDeleteListener() {
                        @Override
                        public void onItemDelete() {
                            onRefresh();
                        }
                    });
            mRecyclerView.setAdapter(mBrowseAdapter);
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleCount = layoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = mWishlistViewUrl + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        loadMoreData(url);
                    }
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (mBody != null) {
            addItemsToList(mBody);
        } else {
            makeRequest();
        }
    }

    /**
     * Method to send request to server to get browse page data.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;
        mAppConst.getJsonResponseFromUrl(mWishlistViewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseItemList.clear();

                addItemsToList(jsonObject);
                mBrowseAdapter.notifyDataSetChanged();

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);

                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, mRootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
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
     * Method to load more data(if exists) on scrolling.
     *
     * @param url Url to load next page data
     */
    private void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mBrowseAdapter.notifyItemRemoved(mBrowseItemList.size());

                addItemsToList(jsonObject);
                mBrowseAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });
    }

    /**
     * Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response
     */
    public void addItemsToList(JSONObject jsonObject) {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        mGutterMenus = jsonObject.optJSONArray("gutterMenus");
        if (mGutterMenus != null)
            invalidateOptionsMenu();

        JSONObject responseObject = jsonObject.optJSONObject("response");


        int mTotalItemCount;
        mWishListId = responseObject.optInt("wishlist_id");
        mContentUrl = responseObject.optString("content_url");
        mShareImageUrl = responseObject.optString("image");
        mBrowseListItems = new BrowseListItems(responseObject.optInt("wishlist_id"), title,
                mShareImageUrl, mContentUrl);
        String body = responseObject.optString("body");

        JSONArray mDataResponse;
        if (currentModule.equals("sitestoreproduct_wishlist")) {
            mTotalItemCount = responseObject.optInt("totalproducts");
            mDataResponse = responseObject.optJSONArray("products");
        } else {
            mTotalItemCount = responseObject.optInt("totallistings");
            if (body != null && !body.isEmpty() && mLoadingPageNo == 1) {
                mBrowseItemList.add(body);
            }
            mDataResponse = responseObject.optJSONArray("listing");
        }
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        title = responseObject.optString("title");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title + ": " + "(" + mTotalItemCount + ")");
        }
        if (mDataResponse != null && mDataResponse.length() > 0) {
            findViewById(R.id.message_layout).setVisibility(View.GONE);
            if (!currentModule.equals("sitestoreproduct_wishlist")) {
                for (int i = 0; i < mDataResponse.length(); i++) {
                    JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                    int listingId = jsonDataObject.optInt("listing_id");
                    int listingTypeId = jsonDataObject.optInt("listingtype_id");
                    String title = jsonDataObject.optString("title");
                    String image = jsonDataObject.optString("image");
                    String ownerTitle = jsonDataObject.optString("owner_title");
                    String creationDate = jsonDataObject.optString("creation_date");
                    String location = jsonDataObject.optString("location");
                    String price = jsonDataObject.optString("price");
                    String currency = jsonDataObject.optString("currency");
                    int allowToView = jsonDataObject.optInt("allow_to_view");
                    int isClosed = jsonDataObject.optInt("closed");
                    JSONArray menuArray = jsonDataObject.optJSONArray("gutter_menu");
                    if (allowToView == 1) {
                        mBrowseItemList.add(new BrowseListItems(listingId, listingTypeId, this.title, title, body, image, ownerTitle,
                                creationDate, location, price, currency, true, isClosed, menuArray));
                    } else {
                        mBrowseItemList.add(new BrowseListItems(listingId, listingTypeId, this.title, title, body, image, ownerTitle,
                                creationDate, location, price, currency, false, isClosed, menuArray));
                    }

                }
            } else {
                String currency = jsonObject.optString("currency");
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
                    mBrowseItemList.add(new ProductInfoModel(store_id, product_id, title, image, ratingAvg, price,
                            discount_amount, discount, user_type, discount_value,
                            featured, sponsored, newLabel, currency, true,false,false,null));
                }
            }
        } else {
            findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf046");
            errorMessage.setText(mContext.getResources().getString(R.string.no_classified));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mGutterMenus != null) {
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, currentModule, mBrowseListItems);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        } else {
            if (mGutterMenus != null) {

                mGutterMenuUtils.onMenuOptionItemSelected(mRootView, findViewById(item.getItemId()),
                        id, mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantVariables.VIEW_PAGE_EDIT_CODE &&
                resultCode == ConstantVariables.PAGE_EDIT_CODE) {
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


    @Override
    public void onItemClick(View view, final int position) {
        ProductInfoModel productInfo = (ProductInfoModel) mBrowseItemList.get(position);
        if (view.getTag().equals("mainview")) {
            Intent intent = StoreUtil.getProductViewPageIntent(mContext, productInfo.getProductId());
            startActivity(intent);
        } else {
            mAppConst.postJsonResponseForUrl(UrlUtil.REMOVE_FROM_WISHLIT +
                            "?wishlist_id=" + mWishListId + "&product_id=" + productInfo.getProductId(),
                    null, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {
                            mBrowseItemList.remove(position);
                            mBrowseAdapter.notifyDataSetChanged();
                            SnackbarUtils.displaySnackbar(mRootView,
                                    getResources().getString(R.string.remove_item_from_wishlist));
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            SnackbarUtils.displaySnackbar(mRootView, message);
                        }
                    });
        }
    }

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mRootView, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        finish();
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseListItems = (BrowseListItems) itemList;
    }
}
