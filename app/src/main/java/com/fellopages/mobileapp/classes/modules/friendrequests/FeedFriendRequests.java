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

package com.fellopages.mobileapp.classes.modules.friendrequests;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 * Friend request feed tab item.
 */
public class FeedFriendRequests extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mFriendRequestViewAdapter;
    private AppConstant mAppConst;
    private List<BrowseListItems> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private HashMap<String, String> postParams;
    private JSONObject mBody, mFRequestObject, mSubjectResponse;
    private JSONArray mDataResponseArray;
    private String mRequestSenderImage, mRequestSenderName;
    private int mRequestSenderId;
    private String mFriendRequestUrl;
    private int pageNumber = 1, mTotalRequestCount, mLoadingPageNo = 1;
    private boolean isLoading = false ,isVisibleToUser = false;
    private Snackbar snackbar;

    public FeedFriendRequests() {
        // Required empty public constructor
    }

    public static FeedFriendRequests newInstance(Bundle bundle) {
        // Required  public constructor
        FeedFriendRequests fragment = new FeedFriendRequests();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        postParams = new HashMap<>();

        mAppConst = new AppConstant(mContext);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mFriendRequestUrl = UrlUtil.FRIEND_REQUEST_URL + "&page=" + pageNumber;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final LinearLayoutManager layoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleCount = layoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem+visibleItemCount;

                if(limit == totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)  < mBrowseList.getmTotalItemCount()){
                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = UrlUtil.FRIEND_REQUEST_URL + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        loadMoreData(url);
                    }
                }
            }
        });

        mFriendRequestViewAdapter = new FriendRequestViewAdapter(getActivity(), mBrowseItemList,
                new FriendRequestViewAdapter.OnItemClickListener() {

                    @Override
                    public void onAcceptButtonClick(View view, int position) {
                        mAppConst.showProgressDialog();
                        BrowseListItems listItems = mBrowseItemList.get(position);
                        postParams.put("user_id",String.valueOf(listItems.getmUserId()));
                        String acceptRequestUrl = UrlUtil.USER_CONFIRM_URL;

                        mAppConst.postJsonResponseForUrl(acceptRequestUrl, postParams,
                                new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                        mAppConst.hideProgressDialog();
                                        makeRequest();

                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        mAppConst.hideProgressDialog();
                                        SnackbarUtils.displaySnackbar(rootView, message);
                                    }
                                });

                    }

                    @Override
                    public void onIgnoreButtonClick(View view, int position) {
                        mAppConst.showProgressDialog();
                        BrowseListItems listItems = mBrowseItemList.get(position);
                        postParams.put("user_id", String.valueOf(listItems.getmUserId()));
                        String rejectRequestUrl = UrlUtil.USER_REJECT_URL;

                        mAppConst.postJsonResponseForUrl(rejectRequestUrl, postParams,
                                new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                        mAppConst.hideProgressDialog();
                                        makeRequest();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        mAppConst.hideProgressDialog();
                                        SnackbarUtils.displaySnackbar(rootView, message);
                                    }
                                });
                    }

                    @Override
                    public void onProfilePictureClicked(View view, int position) {
                        BrowseListItems listItems = mBrowseItemList.get(position);
                        Intent viewIntent;
                        viewIntent = new Intent(mContext, userProfile.class);
                        viewIntent.putExtra("user_id", listItems.getmUserId());
                        getActivity().startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                });
        mRecyclerView.setAdapter(mFriendRequestViewAdapter);
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible && !isVisibleToUser) {
            makeRequest();
        } else {
            if(snackbar != null && snackbar.isShown())
                snackbar.dismiss();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    public void makeRequest() {
        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mFriendRequestUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToBrowseList(jsonObject);

                isVisibleToUser = true;
                mFriendRequestViewAdapter.notifyDataSetChanged();

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

                if (message != null) {
                    if (isRetryOption) {
                        snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                                new SnackbarUtils.OnSnackbarActionClickListener() {
                                    @Override
                                    public void onSnackbarActionClick() {
                                        rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                        makeRequest();
                                    }
                                });
                    } else {
                        SnackbarUtils.displaySnackbar(rootView, message);
                    }
                }
            }
        });
    }

    private  void loadMoreData(String url) {

        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mFriendRequestViewAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mFriendRequestViewAdapter.notifyItemRemoved(mBrowseItemList.size());

                addDataToBrowseList(jsonObject);

                mFriendRequestViewAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    public void addDataToBrowseList(JSONObject jsonObject){
        mBody = jsonObject;
        mTotalRequestCount = mBody.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalRequestCount);
        mDataResponseArray = mBody.optJSONArray("response");

        if(mDataResponseArray != null && mDataResponseArray.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);

            for (int i = 0; i < mDataResponseArray.length(); i++) {
                mFRequestObject = mDataResponseArray.optJSONObject(i);
                mSubjectResponse = mFRequestObject.optJSONObject("subject");
                mRequestSenderId = mSubjectResponse.optInt("user_id");
                mRequestSenderName = mSubjectResponse.optString("displayname");
                mRequestSenderImage = mSubjectResponse.optString("image_profile");
                mBrowseItemList.add(new BrowseListItems(mRequestSenderId, mRequestSenderName, mRequestSenderImage));
            }
        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf235");
            errorMessage.setText(mContext.getResources().getString(R.string.no_friend_requests));
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
