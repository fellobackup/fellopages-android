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

package com.fellopages.mobileapp.classes.common.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesRecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUserReviewDeleteListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserReviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    // Member variables
    private Context mContext;
    private AppConstant mAppConst;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private boolean isLoading = false, isVisibleToUser = false, isProfilePageRequest = false, isFirstTab = false;
    private String mUserReviewUrl, mCurrentSelectedModule, mTabTitle;
    private int mLoadingPageNo = 1, mListingId, mListingTypeId, mContentId, mTotalReviewCount, mRatingAvg,
            mRecommendedAvg, mReviewId, mMyRating;
    private JSONArray mMyRatingJsonArray;
    private View mRootView, mHeaderView;
    private RecyclerView mRecyclerView;
    private TextView tvUserBasedText, tvRecommendedText, tvUpdateReview, tvErrorIcon;
    private SelectableTextView stvErrorMessage;
    private LinearLayout llMessage;
    private RatingBar rbAverage, rbMy;
    private Snackbar mSnackbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter mBrowseAdapter;
    private OnFragmentDataChangeListener mOnFragmentDataChangeListener;
    private OnUserReviewDeleteListener mOnUserReviewDeleteListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAppConst = new AppConstant(getActivity());
        mContext = getContext();
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        // Inflating recycler layout.
        mRootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mHeaderView = inflater.inflate(R.layout.layout_rating_block, null, false);

        // Getting Views
        getViews();

        // Showing the average rating bar and my rating bar with the color filter.
        LayerDrawable avgRatingStar = (LayerDrawable) rbAverage.getProgressDrawable();
        avgRatingStar.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                PorterDuff.Mode.SRC_ATOP);
        avgRatingStar.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                PorterDuff.Mode.SRC_ATOP);

        LayerDrawable myRatingStar = (LayerDrawable) rbMy.getProgressDrawable();
        myRatingStar.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                PorterDuff.Mode.SRC_ATOP);
        myRatingStar.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                PorterDuff.Mode.SRC_ATOP);

        // Getting arguments.
        if(getArguments() != null) {

            Bundle bundle = getArguments();

            isProfilePageRequest = bundle.getBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST);
            mContentId = bundle.getInt(ConstantVariables.VIEW_PAGE_ID);
            mCurrentSelectedModule = bundle.getString(ConstantVariables.EXTRA_MODULE_TYPE);
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
            if (mCurrentSelectedModule == null || mCurrentSelectedModule.isEmpty()) {
                mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
            }

            // Getting url's according to current selected module.
            switch (mCurrentSelectedModule) {
                case ConstantVariables.MLT_MENU_TITLE:

                    // Getting current Listing variables from arguments.
                    mListingId = bundle.getInt(ConstantVariables.LISTING_ID, 0);
                    mListingTypeId = bundle.getInt(ConstantVariables.LISTING_TYPE_ID, 0);
                    mUserReviewUrl = bundle.getString(ConstantVariables.URL_STRING) + "&listingtype_id="
                            + mListingTypeId + "&page=1";

                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    mUserReviewUrl = AppConstant.DEFAULT_URL + "advancedevents/reviews/browse/event_id/"
                            + mContentId + "?getRating=1&page=1";
                    break;

                default:
                    mUserReviewUrl = bundle.getString(ConstantVariables.URL_STRING);
                    break;
            }

            mTabTitle = bundle.getString(ConstantVariables.TAB_LABEL);

            if (isProfilePageRequest) {
                mOnFragmentDataChangeListener = FragmentUtils.getOnFragmentDataChangeListener();
                mOnUserReviewDeleteListener = FragmentUtils.getOnUserReviewDeleteListener();
            }
        }

        // Updating current selected module
        if(PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                !PreferencesUtils.getCurrentSelectedModule(mContext).equals(mCurrentSelectedModule)) {
            PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
        }

        // Setting Layout Manager, adapter and scroll listener.
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBrowseAdapter = new AdvModulesRecyclerViewAdapter(mContext, mBrowseItemList, "user_review",
                mListingTypeId, UserReviewFragment.this, new OnItemDeleteResponseListener() {

            @Override
            public void onItemDelete(int itemCount, boolean isUserReviewDelete) {

                // IF user delete self review then refresh the fragment.
                // And update the view page if loading from view page
                if (isUserReviewDelete) {
                    if (mOnUserReviewDeleteListener != null) {
                        mOnUserReviewDeleteListener.onUserReviewDelete();
                    }
                    tvUpdateReview.setVisibility(View.GONE);
                }

                // IF any review is deleted then update the item count in tab title and refresh when there is no review.
                if (itemCount != 0 && mOnFragmentDataChangeListener != null) {
                    mOnFragmentDataChangeListener.onFragmentTitleUpdated(UserReviewFragment.this, itemCount);

                } else if (itemCount == 0) {
                    onRefresh();
                }
            }
        });
        mRecyclerView.setAdapter(mBrowseAdapter);

        // Set-up scroll listener on recycler view.
        setScrollListener();

        if (!isProfilePageRequest || isFirstTab) {
            makeRequest();
        }

        return mRootView;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.updateReviewText) {
            String reviewUpdateUrl;
            switch (mCurrentSelectedModule) {
                case ConstantVariables.MLT_MENU_TITLE:
                    reviewUpdateUrl = AppConstant.DEFAULT_URL + "listings/review/update/" + mListingId +
                            "?review_id=" + mReviewId + "&listing_id=" + mListingId + "&listingtype_id=" + mListingTypeId;
                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    reviewUpdateUrl = AppConstant.DEFAULT_URL + "advancedevents/review/update/" + mContentId +
                            "?review_id=" + mReviewId;
                    break;

                default:
                    reviewUpdateUrl = AppConstant.DEFAULT_URL + "sitestore/product/review/edit/" + mContentId +
                            "/" + mReviewId;
                    break;
            }
            Intent updateReviewIntent = new Intent(mContext, EditEntry.class);
            updateReviewIntent.putExtra(ConstantVariables.URL_STRING, reviewUpdateUrl);
            updateReviewIntent.putExtra(ConstantVariables.FORM_TYPE, "update_review");
            updateReviewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
            startActivityForResult(updateReviewIntent, ConstantVariables.PAGE_EDIT_CODE);
            ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                makeRequest();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ConstantVariables.PAGE_EDIT_CODE:
                if (!isProfilePageRequest && resultCode == ConstantVariables.PAGE_EDIT_CODE) {
                    onRefresh();
                }
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {

                    Bundle bundle = data.getExtras();
                    int position = bundle.getInt(ConstantVariables.ITEM_POSITION);
                    if (mBrowseItemList != null && mBrowseItemList.size() != 0) {
                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                        listItems.setmCommentCount(bundle.getInt(ConstantVariables.PHOTO_COMMENT_COUNT));
                        mBrowseAdapter.notifyItemChanged(position);
                    }
                }
                break;
        }
    }

    // Getting all views.
    public void getViews() {

        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // No data message views
        llMessage = mRootView.findViewById(R.id.message_layout);
        tvErrorIcon = mRootView.findViewById(R.id.error_icon);
        stvErrorMessage = mRootView.findViewById(R.id.error_message);
        tvErrorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        //Swipe refresh layout view.
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        // Getting header views
        tvUserBasedText = mHeaderView.findViewById(R.id.userbaseText);
        tvRecommendedText = mHeaderView.findViewById(R.id.recommendedText);
        tvUpdateReview = mHeaderView.findViewById(R.id.updateReviewText);
        rbAverage = mHeaderView.findViewById(R.id.avgRatingBar);
        rbMy = mHeaderView.findViewById(R.id.myRatingBar);
        tvUpdateReview.setOnClickListener(this);
        RelativeLayout mainView = mRootView.findViewById(R.id.main_view_recycler);
        mainView.addView(mHeaderView);
        CustomViews.addHeaderView(R.id.ratingInfo, mSwipeRefreshLayout);
        mHeaderView.findViewById(R.id.ratingInfo).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    // Set ScrollListener on recycler view.
    public void setScrollListener() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
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
                        String url = mUserReviewUrl + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        loadMoreData(url);
                    }
                }
            }
        });
    }

    /**
     * Method to send request to server to get user review page data.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;
        mAppConst.getJsonResponseFromUrl(mUserReviewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                isVisibleToUser = true;
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }

                mBrowseItemList.clear();
                if (isAdded()) {
                    addItemsToList(jsonObject);
                }
                mBrowseAdapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
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

    /**
     *Method to load more data(if exists) on scrolling.
     *
     * @param url Url to load next page data
     */
    private void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mBrowseAdapter.notifyItemRemoved(mBrowseItemList.size());

                if (isAdded()) {
                    addItemsToList(jsonObject);
                }
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
     *Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response
     */
    public void addItemsToList(JSONObject jsonObject) {

        mTotalReviewCount = jsonObject.optInt("total_reviews");
        if (mOnFragmentDataChangeListener != null) {
            mOnFragmentDataChangeListener.onFragmentTitleUpdated(UserReviewFragment.this, mTotalReviewCount);
        }

        // Setting tool bar title.
        String contentTitle = jsonObject.optString("content_title");

        if (((AppCompatActivity) mContext).getSupportActionBar() != null) {
            ((AppCompatActivity) mContext).getSupportActionBar()
                    .setTitle(mTabTitle + " (" + mTotalReviewCount + "): " + contentTitle);
        }

        mBrowseList.setmTotalItemCount(mTotalReviewCount);

        JSONObject ratingObj = jsonObject.optJSONObject("ratings");
        if (ratingObj != null) {
            mRatingAvg = ratingObj.optInt("rating_avg");
            mRecommendedAvg = ratingObj.optInt("recomended");
            mReviewId = ratingObj.optInt("review_id");
            mMyRatingJsonArray = ratingObj.optJSONArray("myRatings");
            if (mMyRatingJsonArray != null && mMyRatingJsonArray.length() > 0) {
                JSONObject myRatingObj = mMyRatingJsonArray.optJSONObject(0);
                mMyRating = myRatingObj.optInt("rating");
            }
        }

        JSONArray mDataResponse = jsonObject.optJSONArray("reviews");
        if (mDataResponse != null && mDataResponse.length() > 0) {
            llMessage.setVisibility(View.GONE);
            // call method to show rating statistics.
            showRatingStatistics();

            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);

                int reviewId = jsonDataObject.optInt("review_id");
                int resourceId = jsonDataObject.optInt("resource_id");
                int ownerId = jsonDataObject.optInt("owner_id");
                int rating;
                if(jsonDataObject.has("rating")){
                    rating = jsonDataObject.optInt("rating");
                }else {
                    rating = jsonDataObject.optInt("overall_rating");
                }
                String ownerImage = jsonDataObject.optString("owner_image");
                String contentUrl = jsonDataObject.optString("content_url");
                String title = jsonDataObject.optString("title");
                String body = jsonDataObject.optString("body");
                String pros = jsonDataObject.optString("pros");
                String cons = jsonDataObject.optString("cons");
                String creationDate = jsonDataObject.optString("creation_date");
                int store_id = jsonDataObject.optInt("store_id");
                int likeCount = jsonDataObject.optInt("like_count");
                int commentCount = jsonDataObject.optInt("comment_count");
                int helpfulCount = jsonDataObject.optInt("helpful_count");
                int notHelpfulCount = jsonDataObject.optInt("nothelpful_count");
                Boolean isHelpful = jsonDataObject.optBoolean("is_helful");
                Boolean isNotHelpful = jsonDataObject.optBoolean("is_not_helful");
                int recommend = jsonDataObject.optInt("recommend");
                String ownerTitle = jsonDataObject.optString("owner_title");
                JSONArray updatedReviewArray = jsonDataObject.optJSONArray("updatedReviewArray");
                JSONArray menuArray;
                if(jsonDataObject.has("gutterMenus")){
                    menuArray = jsonDataObject.optJSONArray("gutterMenus");
                }else {
                    menuArray = jsonDataObject.optJSONArray("guttermenu");
                }

                mBrowseItemList.add(new BrowseListItems(reviewId, resourceId, store_id, ownerId, rating, ownerImage, contentUrl,
                        title, body, pros, cons, creationDate, likeCount, commentCount, helpfulCount,
                        notHelpfulCount, isHelpful, isNotHelpful, recommend, ownerTitle, menuArray, updatedReviewArray));
            }
        } else {
            llMessage.setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.ratingInfo).setVisibility(View.GONE);
            tvErrorIcon.setText("\uf007");
            stvErrorMessage.setText(mContext.getResources().getString(R.string.no_user_review));
        }
    }

    /**
     * Method to show rating statistics i.e. Avg. User rating and user rating.
     */
    public void showRatingStatistics() {

        mHeaderView.findViewById(R.id.ratingInfo).setVisibility(View.VISIBLE);
        mHeaderView.findViewById(R.id.avgRatingBlock).setVisibility(View.VISIBLE);
        mHeaderView.findViewById(R.id.ratingBlock).setVisibility(View.VISIBLE);

        // if not logged out user then show both avg. rating block and my rating block otherwise show only avg. rating block,
        // And align it at center.
        if (!mAppConst.isLoggedOutUser()) {
            mHeaderView.findViewById(R.id.myRatingBlock).setVisibility(View.VISIBLE);

        } else {
            mHeaderView.findViewById(R.id.myRatingBlock).setVisibility(View.GONE);
            mHeaderView.findViewById(R.id.ratingBlock).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

            LinearLayout.LayoutParams avgRatingBlockParams = (LinearLayout.LayoutParams) mHeaderView.findViewById(R.id.avgRatingBlock).
                    getLayoutParams();
            avgRatingBlockParams.gravity = Gravity.CENTER;
            avgRatingBlockParams.weight = 1.0f;
            mHeaderView.findViewById(R.id.avgRatingBlock).setLayoutParams(avgRatingBlockParams);

            ((LinearLayout.LayoutParams) mHeaderView.findViewById(R.id.avgRatingTitle).getLayoutParams()).gravity = Gravity.CENTER;
            ((LinearLayout.LayoutParams)rbAverage.getLayoutParams()).gravity = Gravity.CENTER;
            ((LinearLayout.LayoutParams)tvUserBasedText.getLayoutParams()).gravity = Gravity.CENTER;
            ((LinearLayout.LayoutParams)tvRecommendedText.getLayoutParams()).gravity = Gravity.CENTER;
        }

        if (mMyRatingJsonArray != null && mMyRatingJsonArray.length() > 0 && mReviewId != 0) {
            tvUpdateReview.setVisibility(View.VISIBLE);
        } else {
            tvUpdateReview.setVisibility(View.GONE);
        }

        float rating = Float.parseFloat(String.valueOf(mRatingAvg));

        rbAverage.setRating(rating);
        rbAverage.setIsIndicator(true);

        float myRating = Float.parseFloat(String.valueOf(mMyRating));

        rbMy.setRating(myRating);
        rbMy.setIsIndicator(true);

        int recommendedBy = (mRecommendedAvg / mTotalReviewCount) * 100;

        tvUserBasedText.setText(getResources().getString(R.string.based_on_text) + " " + mTotalReviewCount + " " +
                getResources().getString(R.string.review_text));
        tvRecommendedText.setText(getResources().getString(R.string.recommended_by) + " " +
                recommendedBy + "% " + getResources().getString(R.string.user_text));
    }

}
