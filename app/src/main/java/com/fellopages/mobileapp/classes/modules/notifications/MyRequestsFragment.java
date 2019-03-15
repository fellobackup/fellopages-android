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

package com.fellopages.mobileapp.classes.modules.notifications;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequestsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        PopupMenu.OnMenuItemClickListener {

    private View rootView;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mNotificationViewAdapter;
    private AppConstant mAppConst;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private HashMap<String, String> postParams;
    private int pageNumber = 1, mNotificationId, mCurrentUserId, mObjectId, mSubjectId, mCurrentItemPosition;
    private String mNotificationRequestUrl;
    private boolean isLoading = false, isVisibleToUser = false;
    private String mSubjectType, mObjectType;
    private JSONObject mBody, mSubjectResponse, mObjectResponse, mNotificationObject;
    private JSONArray mRecentUpdatedItemArray, mActionBodyParamsArray;
    private String mNotificationType, mActionTypeBody, mFeedTitle, mNotificationUrl;
    private int mTotalUpdatedItemCount, isRead;
    private Snackbar snackbar;

    public MyRequestsFragment() {
        // Required empty public constructor
    }

    public static MyRequestsFragment newInstance(Bundle bundle) {
        // Required  public constructor
        MyRequestsFragment fragment = new MyRequestsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        postParams = new HashMap<>();
        mAppConst = new AppConstant(mContext);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, null);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mNotificationRequestUrl = UrlUtil.MY_REQUEST_URL + "&page=" + pageNumber;

        mNotificationViewAdapter = new NotificationViewAdapter(getActivity(), mBrowseItemList,true,
                new NotificationViewAdapter.OnItemClickListener() {
                    BrowseListItems listItems;
                    PopupMenu popupMenu;
                    int id;
                    @Override
                    public void onItemClick(View view, int position) {
                        listItems = (BrowseListItems) mBrowseItemList.get(position);
                        /*
                        Send request to server if unread notification is being read to update read/unread flag
                         */
                        if(listItems.getIsRead() == 0){
                            view.setBackground(ContextCompat.getDrawable(mContext,
                                    R.drawable.selectable_background_white));
                            String messageReadUrl = UrlUtil.NOTIFICATION_READ_URL;
                            postParams.put("action_id", String.valueOf(listItems.getNotificationId()));
                            mAppConst.postJsonResponseForUrl(messageReadUrl, postParams,
                                    new OnResponseListener() {
                                        @Override
                                        public void onTaskCompleted(JSONObject jsonObject) {
                                            listItems.setIsRead(1);
                                            mNotificationViewAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                            SnackbarUtils.displaySnackbar(rootView, message);
                                        }
                                    });
                        }

                        JSONObject jsonObject;
                        String type;
                        if (listItems.getNotificationType().equals("friend_accepted") || listItems.getNotificationType().equals("friend_request")) {
                            jsonObject = listItems.getSubjectResponse();
                            type = listItems.getNotificationSubjectType();
                        } else {
                            jsonObject = listItems.getObjectResponse();
                            type = listItems.getNotificationObjectType();
                        }

                        id = GlobalFunctions.getIdOfModule(jsonObject, type);
                        startNewActivity(type, id, listItems, jsonObject);

                    }

                    @Override
                    public void onProfilePictureClicked(View view, int position) {
                        listItems = (BrowseListItems) mBrowseItemList.get(position);
                        id = GlobalFunctions.getIdOfModule(listItems.getSubjectResponse(),
                                listItems.getNotificationSubjectType());
                        startNewActivity(listItems.getNotificationSubjectType(),id,listItems, null);
                    }


                    @Override
                    public void onOptionSelected(View v,BrowseListItems listItems, int position) {
                        mCurrentItemPosition = position;
                        mCurrentUserId = listItems.getmUserId();
                        mObjectId = listItems.getNotificationObjectId();
                        popupMenu = new PopupMenu(mContext, v);
                        popupMenu.setOnMenuItemClickListener(MyRequestsFragment.this);
                        switch (listItems.getNotificationObjectType()){
                            case "group":
                                popupMenu.inflate(R.menu.group_popup_menu);
                                popupMenu.show();
                                break;
                            case "event":
                                popupMenu.inflate(R.menu.event_popup_menu);
                                popupMenu.show();
                                break;

                        }
                    }
                });

        mRecyclerView.setAdapter(mNotificationViewAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleCount = linearLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * pageNumber)
                            < mBrowseList.getmTotalItemCount()) {

                        pageNumber = pageNumber + 1;
                        String url = UrlUtil.MY_REQUEST_URL + "&page=" + pageNumber;
                        isLoading = true;
                        loadMoreData(url);
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
            makeRequest();
        } else {
            if(snackbar != null && snackbar.isShown())
                snackbar.dismiss();
        }
    }


    public void startNewActivity(String type, int id, BrowseListItems customList, JSONObject objectResponse){
        Intent viewIntent;

        switch (type) {
            case "user":
                viewIntent = new Intent(mContext, userProfile.class);
                viewIntent.putExtra("user_id", id);
                getActivity().startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            default:
                viewIntent = GlobalFunctions.getIntentForModule(mContext, id, type, null);
                if (viewIntent != null && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains(type)) {

                    if (type.equals("sitereview_listing") || type.equals("sitereview_review")) {
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, objectResponse.optInt("listingtype_id"));
                    } else if (type.equals("sitereview_wishlist")) {
                        viewIntent.putExtra(ConstantVariables.CONTENT_TITLE, objectResponse.optString("title"));
                    }

                    startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (customList.getmNotificationUrl() != null && !customList.getmNotificationUrl().isEmpty()) {
                    if (ConstantVariables.WEBVIEW_ENABLE == 1) {
                        Intent webViewActivity = new Intent(mContext, WebViewActivity.class);
                        webViewActivity.putExtra("headerText", customList.getNotificationObject().
                                optString("title"));
                        webViewActivity.putExtra("url", customList.getmNotificationUrl());
                        startActivity(webViewActivity);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        CustomTabUtil.launchCustomTab((Activity) mContext, GlobalFunctions.
                                getWebViewUrl(customList.getmNotificationUrl(), mContext));
                    }

                }
                break;
        }

    }

    public void makeRequest() {

        pageNumber = 1;
        mAppConst.getJsonResponseFromUrl(mNotificationRequestUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addNotificationToTheList(jsonObject);
                isVisibleToUser = true;
                mNotificationViewAdapter.notifyDataSetChanged();
                if(mBrowseItemList.size() == 0){
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = rootView.findViewById(R.id.error_icon);
                    SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf0f3");
                    errorMessage.setText(mContext.getResources().getString(R.string.no_notifications));
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
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
        });
    }

    private void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add("progress");
        mNotificationViewAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mNotificationViewAdapter.notifyItemRemoved(mBrowseItemList.size());

                addNotificationToTheList(jsonObject);
                mNotificationViewAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    public void addNotificationToTheList(JSONObject jsonObject){
        mBody = jsonObject;

        mTotalUpdatedItemCount = mBody.optInt("requestTotalItemCount");
        mBrowseList.setmTotalItemCount(mTotalUpdatedItemCount);
        if(mTotalUpdatedItemCount != 0){
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            mRecentUpdatedItemArray = mBody.optJSONArray("myRequests");
            for(int i = 0;i<mRecentUpdatedItemArray.length();i++){
                mNotificationObject = mRecentUpdatedItemArray.optJSONObject(i);
                mNotificationId = mNotificationObject.optInt("notification_id");
                mCurrentUserId = mNotificationObject.optInt("user_id");
                mSubjectId = mNotificationObject.optInt(ConstantVariables.SUBJECT_ID);
                mObjectId = mNotificationObject.optInt("object_id");
                isRead = mNotificationObject.optInt("read");
                mSubjectType = mNotificationObject.optString(ConstantVariables.SUBJECT_TYPE);
                mObjectType = mNotificationObject.optString("object_type");
                mActionTypeBody = mNotificationObject.optString("action_type_body");
                mFeedTitle = mNotificationObject.optString("feed_title");
                mNotificationType = mNotificationObject.optString("type");
                mNotificationUrl = mNotificationObject.optString("url");
                mSubjectResponse = mNotificationObject.optJSONObject("subject");
                mObjectResponse = mNotificationObject.optJSONObject("object");
                mActionBodyParamsArray = mNotificationObject.optJSONArray("action_type_body_params");
                mBrowseItemList.add(new BrowseListItems(mNotificationId, mCurrentUserId, mSubjectId,
                        mObjectId,isRead, mSubjectType, mObjectType, mNotificationObject, mActionTypeBody,
                        mFeedTitle,mNotificationType,mNotificationUrl, mSubjectResponse, mObjectResponse,
                        mActionBodyParamsArray));

            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            TextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf0f3");
            errorMessage.setText(mContext.getResources().getString(R.string.no_notifications));
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mRecyclerView != null){
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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String urlForRequest = null;
        postParams.put("user_id", String.valueOf(mCurrentUserId));
        switch (item.getItemId()){
            case R.id.group_accept:
                urlForRequest =  UrlUtil.GROUP_REQUEST_ACCEPT_URL + mObjectId;
                break;
            case R.id.group_ignore:
                urlForRequest = UrlUtil.GROUP_REQUEST_IGNORE_URL + mObjectId;
                break;
            case R.id.event_accept:
                urlForRequest = UrlUtil.EVENT_REQUEST_ACCEPT_URL + mObjectId;
                break;
            case R.id.event_ignore:
                urlForRequest = UrlUtil.EVENT_REQUEST_IGNORE_URL + mObjectId;
                break;
        }
        mAppConst.postJsonResponseForUrl(urlForRequest, postParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.remove(mCurrentItemPosition);
                mNotificationViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
        return true;
    }

}