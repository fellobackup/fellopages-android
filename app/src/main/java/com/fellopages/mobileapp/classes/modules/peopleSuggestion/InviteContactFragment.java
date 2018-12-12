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

package com.fellopages.mobileapp.classes.modules.peopleSuggestion;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesRecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.ui.BaseButton;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InviteContactFragment extends Fragment implements GetContactsAsync.OnContactLoadResponseListener {

    //Member variables.
    private Context mContext;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private BaseButton btnInviteAll;
    private int mLoadingPageNo = 1;
    private boolean isVisibleToUser = false, isLoading = false;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private AdvModulesRecyclerViewAdapter mAdapter;

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            makeRequest(mLoadingPageNo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.recycler_view_layout, null);

        // Getting views.
        getViews();

        mAdapter = new AdvModulesRecyclerViewAdapter(mContext, mBrowseItemList, "invite");
        mRecyclerView.setAdapter(mAdapter);
        addScrollListener();
        return mRootView;
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
    public void onContactLoaded(JSONObject jsonObject, boolean isRequestSuccessful) {
        try {
            isVisibleToUser = true;
            mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

            // Hiding progress bar from footer when loading more members.
            if (mLoadingPageNo > 1) {
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mAdapter.notifyItemRemoved(mBrowseItemList.size());
                mAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            // Checking if json object is null (No contacts) then showing the error else add member to list.
            if (jsonObject != null) {

                // If request is successful then load the data else showing the error.
                if (isRequestSuccessful) {

                    // Clear the list when it is not a load more member request.
                    if (mLoadingPageNo <= 1) {
                        mBrowseItemList.clear();
                    }
                    JSONObject users = jsonObject.optJSONObject("users");
                    mBrowseList.setmTotalItemCount(jsonObject.optInt("totalItemCount"));
                    if (users != null && users.length() > 0) {
                        mRootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                        Iterator iterator = users.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            String value = users.optString(key);
                            mBrowseItemList.add(new BrowseListItems(key, value, false, false));
                        }
                        mAdapter.notifyDataSetChanged();
//                        btnInviteAll.setVisibility(View.VISIBLE);
                    } else {
                        showError();
                    }
                } else {
                    SnackbarUtils.displaySnackbar(mRootView, jsonObject.optString("message"));
                }
            } else {
                showError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Method to get views.
     */
    public void getViews() {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        //TODO do it on future when we will use our own messaging service, so that api calling is used.
        // Generate invite all button.
//        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        rlParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.margin_2dp),
//                0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_2dp),
//                mContext.getResources().getDimensionPixelSize(R.dimen.margin_2dp));
//
//        RelativeLayout mainView = (RelativeLayout) mRootView.findViewById(R.id.main_view_recycler);
//        btnInviteAll = new BaseButton(mContext);
//        btnInviteAll.setVisibility(View.GONE);
//        btnInviteAll.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//        btnInviteAll.setText(mContext.getResources().getString(R.string.invite_all));
//        btnInviteAll.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//        btnInviteAll.setBackgroundResource(R.drawable.background_app_theme_color);
//        btnInviteAll.setId(R.id.add_description);
//        btnInviteAll.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_15dp),
//                0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_15dp));
//
//        // Adding invite all button at the bottom of the view.
//        rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        mainView.addView(btnInviteAll , rlParams);
//
//        // Align the swiperefresh layout above the invite all button.
//        rlParams = (RelativeLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
//        rlParams.addRule(RelativeLayout.ABOVE, btnInviteAll.getId());
//        swipeRefreshLayout.setLayoutParams(rlParams);
//
//        btnInviteAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAdapter.performInvite(v, 0, false);
//            }
//        });

    }

    /**
     * Method to send request on server and show the contacts which are not the site members.
     */
    public void makeRequest(int pageNumber) {
        isVisibleToUser = true;
        GetContactsAsync getContactsAsync = new GetContactsAsync(mContext, "invite", pageNumber);
        getContactsAsync.setOnResponseListener(this);
        getContactsAsync.execute();
    }

    /**
     * Method to show Error.
     */
    public void showError() {
        mRootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
        TextView errorIcon = (TextView) mRootView.findViewById(R.id.error_icon);
        SelectableTextView errorMessage = (SelectableTextView) mRootView.findViewById(R.id.error_message);
        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        errorIcon.setText("\uf007");
        errorMessage.setText(mContext.getResources().getString(R.string.no_member_to_display));
    }

    private void addScrollListener() {
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

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;

                        isLoading = true;
                        //add null , so the adapter will check view_type and show progress bar at bottom
                        mBrowseItemList.add(null);
                        mAdapter.notifyItemInserted(mBrowseItemList.size() - 1);
                        makeRequest(mLoadingPageNo);
                    }

                }
            }
        });
    }

}
