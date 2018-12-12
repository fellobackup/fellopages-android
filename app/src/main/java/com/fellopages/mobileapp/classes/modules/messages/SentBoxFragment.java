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

package com.fellopages.mobileapp.classes.modules.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SentBoxFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private AppConstant mAppConst;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private HashMap<String, String> postParams;

    private JSONObject mBody, mSenderDataResponse, mReceiverDataResponse, mMessageData;

    private int pageNumber = 1, mLoadingPageNo = 1, mTotalMessageCount;
    private String mInboxRequestUrl;
    private boolean isLoading = false, isVisibleToUser = false;
    private String mUserDisplayName, mUserProfileImage;
    private String mMessageTitle, mMessageUpdatedDate, mMessageBody;
    private int mInboxRead, mInboxDeleted, mMessageId, mConversationId;
    private Snackbar snackbar;

    public SentBoxFragment() {
        // Required empty public constructor
    }

    public static SentBoxFragment newInstance(Bundle bundle){
        SentBoxFragment fragment = new SentBoxFragment();
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
        // Inflate the layout for this fragment
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        postParams = new HashMap<>();
        mAppConst = new AppConstant(getActivity());

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mInboxRequestUrl = UrlUtil.MESSAGE_SENTBOX_URL + "&page=" + pageNumber;

        //getting the reference of BrowseDataAdapter class
        mRecyclerViewAdapter = new MessageViewAdapter(getActivity(), mBrowseItemList,
                new MessageViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                        if (listItems.getmReceiverObject() != null && listItems.getConversationId() != 0) {
                            /*
                        Send request to server if unread message is being read to update read/unread flag
                         */
                            if(listItems.getInboxRead() == 0){
                                view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                                postParams.put("message_id", String.valueOf(listItems.getConversationId()));
                                postParams.put("is_read",String.valueOf(1));
                                mAppConst.markAllMessageRead(postParams);
                            }

                            Intent mainIntent = new Intent(getActivity(), MessageViewActivity.class);
                            String viewUrl = UrlUtil.MESSAGE_VIEW_URL + listItems.getConversationId() +
                                    "?gutter_menu=1";
                            mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewUrl);
                            mainIntent.putExtra("UserName", listItems.getmBrowseListOwnerTitle());
                            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager layoutManager = (LinearLayoutManager)mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleCount = layoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem+visibleItemCount;

                if(limit == totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)  <
                            mBrowseList.getmTotalItemCount()){

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = UrlUtil.MESSAGE_SENTBOX_URL + "&page=" + mLoadingPageNo;
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

    public void makeRequest(){

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mInboxRequestUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addMessagesToList(jsonObject);
                isVisibleToUser = true;

                mRecyclerViewAdapter.notifyDataSetChanged();
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

    public void addMessagesToList(JSONObject jsonObject){

        mBody = jsonObject;
        mTotalMessageCount = mBody.optInt("getTotalItemCount");
        mBrowseList.setmTotalItemCount(mTotalMessageCount);
        JSONArray bodyJsonArray = mBody.optJSONArray("response");
        if(bodyJsonArray != null && bodyJsonArray.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < bodyJsonArray.length(); i++) {
                JSONObject jsonDataObject = bodyJsonArray.optJSONObject(i);
                mMessageData = jsonDataObject.optJSONObject("message");
                mReceiverDataResponse = jsonDataObject.optJSONObject("recipient");
                mSenderDataResponse = jsonDataObject.optJSONObject("sender");

                mUserDisplayName = mSenderDataResponse.optString("displayname");
                mUserProfileImage = mSenderDataResponse.optString("image_profile");

                if (mMessageData != null) {
                    mConversationId = mMessageData.optInt("conversation_id");
                    mMessageId = mMessageData.optInt("message_id");
                    mMessageUpdatedDate = mMessageData.optString("date");
                    mMessageTitle = mMessageData.optString("title");
                    mMessageBody = mMessageData.optString("body");
                }

                if (mReceiverDataResponse != null) {
                    mInboxRead = mReceiverDataResponse.optInt("inbox_read");
                    mInboxDeleted = mReceiverDataResponse.optInt("inbox_deleted");
                }

                mBrowseItemList.add(new BrowseListItems(mUserDisplayName, mUserProfileImage,
                        mMessageTitle, mMessageBody, mConversationId, mMessageId,
                        mMessageUpdatedDate, mReceiverDataResponse,  mInboxRead, mInboxDeleted));
            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf0e0");
            errorMessage.setText(mContext.getResources().getString(R.string.no_message));
        }

    }

    /**
     * Load More data on Scrolling
     * @param url Url to send Request on server
     */
    private  void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add("progress");
        mRecyclerViewAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mRecyclerViewAdapter.notifyItemRemoved(mBrowseItemList.size());
                addMessagesToList(jsonObject);

                mRecyclerViewAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            switch (resultCode) {
                case ConstantVariables.MESSAGE_CREATED:
                    SnackbarUtils.displaySnackbar(rootView,
                            getResources().getString(R.string.message_sent_successfully));
                    break;
                case ConstantVariables.MESSAGE_DELETED:
                    SnackbarUtils.displaySnackbar(rootView,
                            getResources().getString(R.string.conversation_delete_message));
                    break;
            }

        }
        makeRequest();

    }

}
