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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.activities.ReportEntry;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.adapters.ReviewAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.MyRatingsInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.ReviewInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnItemClickListener {


    private AppConstant mAppConst;
    private View rootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mReviewAdapter;
    private List<Object> mReviewList;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String reviewPageUrl;
    private Bundle mReviewPageExtras;
    private int mDefaultPageNumber = 1;
    private Snackbar snackbar;
    private boolean isLoading = false,isVisibleToUser = false;

    public ReviewFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recycler_view_layout, container,false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mRecyclerView.setHasFixedSize(true);
        mReviewList = new ArrayList<>();
        mContext = getContext();
        mAppConst = new AppConstant(mContext);

        mReviewPageExtras = getArguments();

        // The number of Columns
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mReviewAdapter = new ReviewAdapter(mContext,mReviewList,this);
        mRecyclerView.setAdapter(mReviewAdapter);

        reviewPageUrl = mReviewPageExtras.getString(ConstantVariables.URL_STRING) + "?page=1&limit="
                + AppConstant.LIMIT + "&getRating=1";


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

                int limit=firstVisibleItem+visibleItemCount;

                if(limit==totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mDefaultPageNumber)  <
                            totalReviewCount){

                        mDefaultPageNumber += 1;
                        isLoading = true;
                        loadMoreData(mReviewPageExtras.getString(ConstantVariables.URL_STRING) +
                                "?limit=" + AppConstant.LIMIT + "&page=" + mDefaultPageNumber);
                    }

                }
            }
        });
        return rootView;

    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            getReviews();
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }
    public void getReviews(){
        try {

            mAppConst.getJsonResponseFromUrl(reviewPageUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    mReviewList.clear();

                    if (snackbar != null && snackbar.isShown())
                        snackbar.dismiss();
                    addDataToList(jsonObject);
                    isVisibleToUser = true;
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    try {
                        if (isRetryOption) {
                            snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, rootView, message,
                                    new SnackbarUtils.OnSnackbarActionClickListener() {
                                        @Override
                                        public void onSnackbarActionClick() {
                                            rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                            getReviews();
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

    /* Load More Data On Scroll */

    public void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mReviewList.add(null);
        mReviewAdapter.notifyItemInserted(mReviewList.size() - 1);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mReviewList.remove(mReviewList.size() - 1);
                mReviewAdapter.notifyItemRemoved(mReviewList.size());
                addDataToList(jsonObject);
                mReviewAdapter.notifyItemInserted(mReviewList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }
    int totalReviewCount;
    JSONObject myRatingObject;
    public void addDataToList(JSONObject jsonObject){
        mBody = jsonObject;
        myRatingObject = mBody.optJSONObject("ratings");
        totalReviewCount = mBody.optInt("total_reviews");
        if (totalReviewCount != 0) {
            if (myRatingObject != null) {
                int review_id = myRatingObject.optInt("review_id");
                int avgRating = myRatingObject.optInt("rating_avg");
                int userCount = myRatingObject.optInt("rating_users");
                String recommendedBy = myRatingObject.optString("recomended");
                JSONArray breakDownParams = myRatingObject.optJSONArray("breakdown_ratings_params");
                JSONArray myRatingParams = myRatingObject.optJSONArray("myRatings");
                mReviewList.add(new MyRatingsInfoModel(review_id, avgRating, userCount, recommendedBy,
                        breakDownParams, myRatingParams));
            }
            mDataResponse = mBody.optJSONArray("reviews");
            if (mDataResponse != null && mDataResponse.length() > 0) {
                for (int i = 0; i < mDataResponse.length(); i++) {
                    JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                    int review_id = jsonDataObject.optInt("review_id");
                    String title = jsonDataObject.optString("title");
                    String body = jsonDataObject.optString("body");
                    String owner_title = jsonDataObject.optString("owner_title");
                    String like_count = jsonDataObject.optString("like_count");
                    String view_count = jsonDataObject.optString("view_count");
                    String comment_count = jsonDataObject.optString("comment_count");
                    String pros = jsonDataObject.optString("pros");
                    String cons = jsonDataObject.optString("cons");
                    String date = jsonDataObject.optString("creation_date");
                    String recommendation = jsonDataObject.optString("recommend");
                    int overall_rating = jsonDataObject.optInt("overall_rating");
                    JSONArray options = jsonDataObject.optJSONArray("guttermenu");
                    boolean isLiked = jsonDataObject.optBoolean("is_liked");

                    mReviewList.add(new ReviewInfoModel(review_id, title, body, owner_title, like_count,
                            view_count, comment_count, pros, cons, overall_rating, isLiked, date,
                            recommendation, options));
                }
            }

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            TextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf007");
            errorMessage.setText(getResources().getString(R.string.no_review_text));
        }
        mReviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        getReviews();
    }
    ReviewInfoModel reviewInfo;
    @Override
    public void onItemClick(View view, int position) {
        reviewInfo = (ReviewInfoModel) mReviewList.get(position);
        showPopup(view,reviewInfo.getReviewOptions());
    }
    private JSONObject menuJsonObject;
    private HashMap<String,String> postParams;
    String menuName,redirectUrl;
    private void showPopup(final View v, final JSONArray menuArray) {

        postParams = new HashMap<>();
        PopupMenu popup = new PopupMenu(mContext, v);

        if (menuArray.length() != 0) {

            for (int i = 0; i < menuArray.length(); i++) {
                try {
                    menuJsonObject = menuArray.optJSONObject(i);
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, menuJsonObject.getString("label").trim());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                try {
                    menuJsonObject = menuArray.optJSONObject(id);
                    menuName = menuJsonObject.optString("name");
                    JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                    redirectUrl = AppConstant.DEFAULT_URL + menuJsonObject.optString("url");

                    if (urlParams != null && urlParams.length() != 0) {
                        JSONArray urlParamsNames = urlParams.names();
                        for (int j = 0; j < urlParams.length(); j++) {
                            String name = urlParamsNames.getString(j);
                            String value = urlParams.getString(name);
                            postParams.put(name, value);
                        }
                    }
                    redirectUrl = mAppConst.buildQueryString(redirectUrl, postParams);

                    switch (menuName) {

                        case "delete":
                            performAction(redirectUrl,
                                    mContext.getResources().getString(R.string.delete_dialogue_message),
                                    mContext.getResources().getString(R.string.delete_dialogue_title),
                                    mContext.getResources().getString(R.string.delete_dialogue_button),
                                    mContext.getResources().getString(R.string.delete_dialogue_success_message),
                                    postParams, menuName);
                            break;

                        case "report":
                            Intent reportIntent = new Intent(mContext, ReportEntry.class);
                            reportIntent.putExtra(ConstantVariables.URL_STRING, redirectUrl);
                            mContext.startActivity(reportIntent);
                            break;

                        case "edit_review":
                        case "update":
                            Intent intent = new Intent(mContext, EditEntry.class);
                            intent.putExtra(ConstantVariables.URL_STRING, redirectUrl);
                            intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                            intent.putExtra(ConstantVariables.FORM_TYPE, "update_review");
                            ((Activity) mContext).startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        popup.show();

    }

    public void performAction(final String url, final String message, String title, String buttonTitle,
                              final String showSuccessMessage, final Map<String, String> params,
                              final String selectedMenuName){

        try {
            final View mRootView = ((AppCompatActivity) mContext).getCurrentFocus();

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
            alertBuilder.setMessage(message);
            alertBuilder.setTitle(title);

            alertBuilder.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    mAppConst.showProgressDialog();
                    if (selectedMenuName.equals("delete")) {
                        mAppConst.deleteResponseForUrl(url, params, new OnResponseListener() {

                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {
                                mAppConst.hideProgressDialog();
                                /* Notify Adapter After Deleting the Entry */
                                mReviewList.remove(reviewInfo);
                                mReviewAdapter.notifyDataSetChanged();
                                SnackbarUtils.displaySnackbar(mRootView, showSuccessMessage);
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                                SnackbarUtils.displaySnackbar(mRootView, message);
                            }
                        });
                    }
                }
            });
            alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.delete_account_cancel_button_text),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertBuilder.create().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
