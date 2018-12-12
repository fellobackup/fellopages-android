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

package com.fellopages.mobileapp.classes.common.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.likeNComment.CommentAdapter;
import com.fellopages.mobileapp.classes.modules.likeNComment.CommentList;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReactionsFragment extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {


    private View mRootView;
    private Context mContext;
    private AppConstant mAppConst;
    private String mReactionName;
    private JSONArray mReactionResponse;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<CommentList> mLikeListItems;
    private CommentList mCommentList;
    private CommentAdapter mLikeAdapter;
    private boolean isVisibleToUser = false, isLoading = false;
    private String mViewAllLikesUrl, mViewLikeBaseUrl;
    private int mTotalLikes, pageNumber = 1;
    private View footerView;



    public ReactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.list_view_layout, container, false);
        mContext = getContext();
        mAppConst = new AppConstant(mContext);

        mReactionName = getArguments().getString(ConstantVariables.REACTION_NAME);
        mViewAllLikesUrl = mViewLikeBaseUrl = getArguments().getString(ConstantVariables.URL_STRING);
        mViewAllLikesUrl += "&page=" + pageNumber + "&limit=" + AppConstant.LIMIT;
        mTotalLikes = getArguments().getInt(ConstantVariables.TOTAL_ITEM_COUNT, 0);

        try {
            mReactionResponse = new JSONArray(getArguments().getString(ConstantVariables.REACTION_RESPONSE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        mListView = (ListView) mRootView.findViewById(R.id.list_item_view);
        mListView.setDividerHeight(0);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);

        mLikeListItems = new ArrayList<>();
        mCommentList = new CommentList();

        mLikeAdapter = new CommentAdapter(mContext, R.layout.list_comment, mLikeListItems, mCommentList, false);
        footerView = CustomViews.getFooterView(inflater);

        mListView.setAdapter(mLikeAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        if(mReactionName.equals("all")){
            mProgressBar.setVisibility(View.GONE);
            addDataToList();
        }

        return mRootView;
    }

    private void addDataToList(){

        if(mReactionResponse != null){
            for (int i = 0; i < mReactionResponse.length(); i++){
                JSONObject likeInfoObject = mReactionResponse.optJSONObject(i);
                int user_id = likeInfoObject.optInt("user_id");
                String displayName = likeInfoObject.optString("displayname");
                String photoUrl = likeInfoObject.optString("image_profile");
                String friendshipType = likeInfoObject.optString("friendship_type");
                String reactionImageIcon = likeInfoObject.optString("reaction_image_icon");
                int isVerified = likeInfoObject.optInt("isVerified");
                mLikeListItems.add(new CommentList(user_id, displayName, photoUrl, friendshipType,
                        reactionImageIcon, isVerified));
            }
        }
        mLikeAdapter.notifyDataSetChanged();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            if(mReactionName != null && !mReactionName.equals("all")){
                mViewAllLikesUrl += "&reaction=" + mReactionName;
                makeRequest();
            }
        }
    }

    public void makeRequest() {

        mProgressBar.setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(mViewAllLikesUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mLikeListItems.clear();
                isVisibleToUser = true;
                mProgressBar.setVisibility(View.GONE);
                mReactionResponse = jsonObject.optJSONArray("viewAllLikesBy");
                addDataToList();
                isVisibleToUser = true;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    public void loadMoreLikes(String url){
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(mListView, footerView);
                if (jsonObject != null) {
                    mReactionResponse = jsonObject.optJSONArray("viewAllLikesBy");
                    mTotalLikes = jsonObject.optInt("getTotalLikes");
                    addDataToList();
                    isLoading = false;
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;

        if(limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * pageNumber) < mTotalLikes) {
                CustomViews.addFooterView(mListView, footerView);
                pageNumber += 1;
                String likeCommentsUrl = mViewLikeBaseUrl + "&page=" + pageNumber + "&limit=" + AppConstant.LIMIT;
                isLoading = true;
                loadMoreLikes(likeCommentsUrl);

            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommentList clickedLikedList = mLikeListItems.get(position);
        int userId = clickedLikedList.getmUserId();

        Intent userProfileIntent = new Intent(mContext, userProfile.class);
        userProfileIntent.putExtra("user_id", userId);
        startActivityForResult(userProfileIntent, ConstantVariables.USER_PROFILE_CODE);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
