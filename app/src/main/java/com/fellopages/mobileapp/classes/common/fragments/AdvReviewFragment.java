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

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesManageDataAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUserReviewDeleteListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvReviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener {

    // Member variables
    private Context mContext;
    private AppConstant mAppConst;
    private BrowseListItems mBrowseList;
    private AdvModulesManageDataAdapter mManageDataAdapter;
    private String mUserReviewUrl, recommended_avg, mCurrentSelectedModule, mContentIdString, mSubjectType;
    private boolean mIsLoading = false, isVisibleToUser = false, isProfilePageRequest = false,
            isHeaderViewAdded = false, isFirstTab = false;
    private int mLoadingPageNo = 1, mRatingAvg, mTotalReviewCount;
    private JSONArray mRatingParams;
    private List<Object> mBrowseItemList;
    private View mRootView, mHeaderView;
    private Snackbar mSnackbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvRecommendedText;
    private LinearLayout llRating;
    private RelativeLayout rlMainContent;
    private RatingBar rbAverage;
    private ListView lvReviews;
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

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        mAppConst = new AppConstant(getContext());
        mContext = getContext();

        // Inflating layout.
        mRootView = inflater.inflate(R.layout.list_view_layout, container, false);
        mHeaderView = inflater.inflate(R.layout.layout_review_header, null, false);

        // Getting arguments.
        if(getArguments() != null) {

            Bundle bundle = getArguments();
            isProfilePageRequest = bundle.getBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST);
            mUserReviewUrl = bundle.getString(ConstantVariables.URL_STRING);
            mTotalReviewCount = bundle.getInt(ConstantVariables.TOTAL_ITEM_COUNT);
            mCurrentSelectedModule = bundle.getString(ConstantVariables.EXTRA_MODULE_TYPE);
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
            if (mCurrentSelectedModule == null || mCurrentSelectedModule.isEmpty()) {
                mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
            }

            if(mCurrentSelectedModule.equals(ConstantVariables.SITE_PAGE_MENU_TITLE)) {
                mContentIdString = "page_id";
                mSubjectType = "sitepage_review";
            } else {
                mContentIdString = "group_id";
                mSubjectType = "sitegroup_review";
            }

        }
        // Updating current selected module
        if(PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                !PreferencesUtils.getCurrentSelectedModule(mContext).equals(mCurrentSelectedModule)) {
            PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
        }

        // Getting Views
        getViews();

        // Setting adapter and scroll listener.
        mManageDataAdapter = new AdvModulesManageDataAdapter(mContext, R.layout.rating_info_list_layout,
                mBrowseItemList, mSubjectType, AdvReviewFragment.this, new OnItemDeleteResponseListener() {
            @Override
            public void onItemDelete(int itemCount, boolean isUserReviewDelete) {

                // IF user delete self review then update the view page if loading from view page.
                if (isUserReviewDelete && mOnUserReviewDeleteListener != null) {
                    mOnUserReviewDeleteListener.onUserReviewDelete();
                }

                // IF any review is deleted then update the item count in tab title and refresh when there is no review.
                if (itemCount != 0 && mOnFragmentDataChangeListener != null) {
                    mOnFragmentDataChangeListener.onFragmentTitleUpdated(AdvReviewFragment.this, itemCount);

                } else if (itemCount == 0) {
                    removeAddHeaderView(false);
                    mHeaderView.findViewById(R.id.review_header_main).setVisibility(View.GONE);
                    onRefresh();
                }
            }
        });

        lvReviews.setAdapter(mManageDataAdapter);
        lvReviews.setOnScrollListener(this);
        if (isProfilePageRequest) {
            mOnFragmentDataChangeListener = FragmentUtils.getOnFragmentDataChangeListener();
            mOnUserReviewDeleteListener = FragmentUtils.getOnUserReviewDeleteListener();
        }
        ViewCompat.setNestedScrollingEnabled(lvReviews, true);

        if (!isProfilePageRequest || isFirstTab) {
            makeRequest();
        }

        return mRootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtils.LOGD(AdvReviewFragment.class.getSimpleName(), "requestCode: "+requestCode+", resultCode: "+resultCode);
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
                        mManageDataAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !mIsLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                mLoadingPageNo = mLoadingPageNo + 1;
                mUserReviewUrl = mUserReviewUrl + "&page=" + mLoadingPageNo;
                mIsLoading = true;
                loadMoreData(mUserReviewUrl);
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
                makeRequest();
            }
        });
    }

    // Getting views.
    public void getViews() {

        rlMainContent = mRootView.findViewById(R.id.fragment_item_view);
        lvReviews = mRootView.findViewById(R.id.list_item_view);

        tvRecommendedText = mHeaderView.findViewById(R.id.recommendedText);
        rbAverage = mHeaderView.findViewById(R.id.mainRatingBar);
        llRating  = mHeaderView.findViewById(R.id.linearLayoutRating);

        // Set rating bar stars color yellow for selected and gray for unselected
        LayerDrawable avgRatingStar = (LayerDrawable) rbAverage.getProgressDrawable();
        avgRatingStar.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                PorterDuff.Mode.SRC_ATOP);
        avgRatingStar.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray),
                PorterDuff.Mode.SRC_ATOP);

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (!isProfilePageRequest) {
            rlMainContent.addView(mHeaderView);
            CustomViews.addHeaderView(R.id.review_header_main, mSwipeRefreshLayout);
        }
    }

    /**
     * Remove and add header view in case of review fragment is loaded in profile page.
     * @param addHeaderView true if add request else false.
     */
    public void removeAddHeaderView(boolean addHeaderView) {

        if (isProfilePageRequest) {
            if (!isHeaderViewAdded && addHeaderView) {
                lvReviews.addHeaderView(mHeaderView);
                isHeaderViewAdded = true;
            } else if (!addHeaderView) {
                lvReviews.removeHeaderView(mHeaderView);
                isHeaderViewAdded = false;
            }
        }
    }

    /**
     * Method to send request to server to get user review page data.
     */
    public void makeRequest() {

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mUserReviewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }

                if (isAdded()) {
                    addDataToList(jsonObject);
                }
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
                    mSnackbar = SnackbarUtils.displaySnackbarWithAction(mContext, rlMainContent, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rlMainContent, message);
                }

            }
        });
    }

    /**
     *Method to load more data(if exists) on scrolling.
     *
     * @param url Url to load next page data
     */
    public void loadMoreData(String url) {

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                addDataToList(jsonObject);
                mIsLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rlMainContent, message);
            }
        });
    }

    /**
     *Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response
     */
    public void addDataToList(JSONObject jsonObject){

        if (jsonObject != null) {
            try {
                mTotalReviewCount = jsonObject.optInt("total_reviews");
                mBrowseList.setmTotalItemCount(mTotalReviewCount);
                if (mOnFragmentDataChangeListener != null) {
                    mOnFragmentDataChangeListener.onFragmentTitleUpdated(AdvReviewFragment.this, mTotalReviewCount);
                }
                String contentTitle = jsonObject.optString("content_title");
                String tabTitle;
                if (contentTitle != null && !contentTitle.isEmpty()) {
                    tabTitle = getResources().getString(R.string.user_review) + "(" + mTotalReviewCount + "): "
                            + contentTitle;
                } else {
                    tabTitle = getResources().getString(R.string.user_review) + " : (" + mTotalReviewCount + ")";
                }

                // Setting tool bar title.
                if (((AppCompatActivity) mContext).getSupportActionBar() != null
                        && contentTitle != null && !contentTitle.isEmpty()) {
                    ((AppCompatActivity) mContext).getSupportActionBar().setTitle(tabTitle);
                }

                if (mTotalReviewCount != 0){
                    mRootView.findViewById(R.id.message_layout).setVisibility(View.GONE);

                    JSONObject ratingJsonObject = jsonObject.optJSONObject("ratings");
                    mRatingAvg = ratingJsonObject.optInt("rating_avg");
                    recommended_avg = ratingJsonObject.optString("recomended");
                    mRatingParams = ratingJsonObject.optJSONArray("breakdown_ratings_params");

                    showRatingStatistics();

                    JSONArray reviewsJsonArray = jsonObject.optJSONArray("reviews");
                    if (reviewsJsonArray != null) {
                        for (int i = 0; i < reviewsJsonArray.length(); i++) {
                            JSONObject jsonDataObject = reviewsJsonArray.getJSONObject(i);

                            String hostType = jsonDataObject.optString("type");
                            int review_id = jsonDataObject.getInt("review_id");
                            int content_id = jsonDataObject.getInt(mContentIdString);
                            String title = jsonDataObject.getString("title");
                            int owner_id = jsonDataObject.optInt("owner_id");
                            String body = jsonDataObject.getString("body");
                            String pros = jsonDataObject.getString("pros");
                            String cons = jsonDataObject.getString("cons");
                            String cDate = jsonDataObject.getString("creation_date");
                            int likeCount = jsonDataObject.optInt("like_count");
                            Boolean is_liked = jsonDataObject.optBoolean("is_liked");
                            int commentCount = jsonDataObject.optInt("comment_count");
                            String recommend = jsonDataObject.optString("recommend");
                            String ownerTitle = jsonDataObject.getString("owner_title");
                            int overallRating = jsonDataObject.optInt("overall_rating");
                            int isShowProsCons = jsonDataObject.optInt("isShowProsCons");

                            JSONArray gutterMenus = jsonDataObject.optJSONArray("guttermenu");
                            JSONArray rating_params = jsonDataObject.optJSONArray("breakdown_ratings_params");

                            mBrowseItemList.add(new BrowseListItems(review_id, content_id, owner_id, hostType, title, body,
                                    pros, cons, cDate, likeCount, is_liked, commentCount, recommend, ownerTitle, overallRating, gutterMenus,
                                    rating_params, isShowProsCons));

                        }
                    }
                } else {
                    removeAddHeaderView(false);
                    mRootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = mRootView.findViewById(R.id.error_icon);
                    TextView errorMessage = mRootView.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf007");
                    errorMessage.setText(getResources().getString(R.string.no_review_text));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mManageDataAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method to show rating statistics.
     */
    public void showRatingStatistics() {

        // Showing header view when data is loaded.
        removeAddHeaderView(true);
        mHeaderView.findViewById(R.id.review_header_main).setVisibility(View.VISIBLE);

        float rating = Float.parseFloat(String.valueOf(mRatingAvg));
        rbAverage.setRating(rating);
        rbAverage.setIsIndicator(true);

        tvRecommendedText.setText(mContext.getResources().getString(R.string.recommended_by_user_prefix) + " " +
                recommended_avg + " " + mContext.getResources().getString(R.string.recommended_by_user_suffix));

        /* Inflating rating parameter in table structure  */

        if(mRatingParams != null){
            llRating = CustomViews.generateRatingView(mContext, "top_view", mRatingParams, llRating);
        }

    }

}
