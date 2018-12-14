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
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The inbox tab item.
 */
public class NewMessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private Context mContext;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private AppConstant mAppConst;
    private List<Object> mBrowseItemList;
    private HashMap<String, String> postParams;
    private JSONObject mBody,mSenderDataResponse,mReceiverDataResponse,mMessageData;
    private int pageNumber = 1;
    private String mInboxRequestUrl;
    private boolean isVisibleToUser = false;
    private String mUserDisplayName, mUserProfileImage, mMessageTitle, mMessageUpdatedDate, mMessageBody;
    private int mInboxRead, mInboxDeleted, mMessageId, mConversationId;
    Snackbar snackbar;
    private int mTotalItemCount;

    public NewMessagesFragment() {
        // Required empty public constructor
    }

    public static NewMessagesFragment newInstance(Bundle bundle) {
        // Required  public constructor
        NewMessagesFragment fragment = new NewMessagesFragment();
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
        postParams = new HashMap<>();

        mAppConst = new AppConstant(mContext);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mInboxRequestUrl = UrlUtil.MESSAGE_INBOX_URL + "&page=" + pageNumber;

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
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        postParams.put("message_id", String.valueOf(listItems.getConversationId()));
                        postParams.put("is_read",String.valueOf(1));
                        mAppConst.markAllMessageRead(postParams);
                    }

                    Intent mainIntent = new Intent(getActivity(), MessageViewActivity.class);
                    String viewUrl = UrlUtil.MESSAGE_VIEW_URL + listItems.getConversationId() + "?gutter_menu=1";
                    mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewUrl);
                    mainIntent.putExtra("UserName", listItems.getmBrowseListOwnerTitle());
                    startActivityForResult(mainIntent, ConstantVariables.MESSAGE_VIEW_PAGE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }});

        mRecyclerView.setAdapter(mRecyclerViewAdapter);


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

    public void makeRequest(){

        mAppConst.getJsonResponseFromUrl(mInboxRequestUrl, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();

                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBody = jsonObject;
                mTotalItemCount = mBody.optInt("getTotalItemCount");
                JSONArray bodyJsonArray = mBody.optJSONArray("response");

                if (bodyJsonArray != null && bodyJsonArray.length() > 0) {
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);

                    for (int i = 0; i < bodyJsonArray.length(); i++) {
                        JSONObject jsonDataObject = bodyJsonArray.optJSONObject(i);

                        mMessageData = jsonDataObject.optJSONObject("message");
                        mReceiverDataResponse = jsonDataObject.optJSONObject("recipient");
                        mSenderDataResponse = jsonDataObject.optJSONObject("sender");

                        if (mSenderDataResponse.has("displayname"))
                            mUserDisplayName = mSenderDataResponse.optString("displayname");
                        else
                            mUserDisplayName = mSenderDataResponse.optString("title");

                        mUserProfileImage = mSenderDataResponse.optString("image_profile");

                        mConversationId = mMessageData.optInt("conversation_id");
                        mMessageId = mMessageData.optInt("message_id");
                        mMessageUpdatedDate = mMessageData.optString("date");
                        mMessageTitle = mMessageData.optString("title");
                        mMessageBody = mMessageData.optString("body");

                        if (mReceiverDataResponse != null) {
                            mInboxRead = mReceiverDataResponse.optInt("inbox_read");
                            mInboxDeleted = mReceiverDataResponse.optInt("inbox_deleted");
                        }

                        mBrowseItemList.add(new BrowseListItems(mUserDisplayName,
                                                                mUserProfileImage,
                                                                mMessageTitle,
                                                                mMessageBody,
                                                                mConversationId,
                                                                mMessageId,
                                                                mMessageUpdatedDate,
                                                                mReceiverDataResponse,
                                                                mInboxRead,
                                                                mInboxDeleted));
                    }

                    if(mTotalItemCount > AppConstant.LIMIT){
                        mBrowseItemList.add(ConstantVariables.FOOTER_TYPE);
                        mRecyclerViewAdapter.notifyItemInserted(mBrowseItemList.size() - 1);
                    }
                } else {
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                    SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf0e0");
                    errorMessage.setText(mContext.getResources().getString(R.string.no_message));
                }

                isVisibleToUser = true;
                mRecyclerViewAdapter.notifyDataSetChanged();

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

                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message, new SnackbarUtils.OnSnackbarActionClickListener() {
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
