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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.PeopleSuggestionAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PeopleSuggestionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    // Member Variables.
    private Context mContext;
    private AppConstant mAppConst;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout llErrorMessage;
    private TextView tvErrorIcon, tvErrorMessage;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private PeopleSuggestionAdapter mPeopleSuggestionAdapter;
    private int mLoadingPageNo = 1;
    private String mBrowseSuggestionUrl;
    private boolean isLoading = false;
    private static final int PEOPLE_SUGGESTION_LIMIT = 10;

    public static PeopleSuggestionFragment newInstance(Bundle bundle) {
        PeopleSuggestionFragment fragment = new PeopleSuggestionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mAppConst = new AppConstant(getActivity());
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        mBrowseSuggestionUrl = UrlUtil.PEOPLE_SUGGESTION_URL + PEOPLE_SUGGESTION_LIMIT;

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.recycler_view_layout, null);

        // Getting Views.
        getViews();

        // Set Adapter.
        mPeopleSuggestionAdapter = new PeopleSuggestionAdapter(mContext, mBrowseItemList, "list_view",
                new PeopleSuggestionAdapter.OnItemDeleteListener() {
                    @Override
                    public void onItemDelete() {
                        // When removed all the suggestions then resend the request.
                        mBrowseItemList.clear();
                        mPeopleSuggestionAdapter.notifyDataSetChanged();
                        onRefresh();
                    }
                });
        mRecyclerView.setAdapter(mPeopleSuggestionAdapter);
        setRecyclerScrollListener();
        makeRequest();

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

    /**
     * Method to get views.
     */
    public void getViews() {

        // Recycler view.
        mRootView.findViewById(R.id.main_view_recycler).setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));

        // Swipe refresh view.
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        // No data message views
        llErrorMessage = mRootView.findViewById(R.id.message_layout);
        tvErrorIcon = mRootView.findViewById(R.id.error_icon);
        tvErrorMessage = (SelectableTextView) mRootView.findViewById(R.id.error_message);
        tvErrorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
    }

    public void setRecyclerScrollListener() {
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

                    if (limit >= PEOPLE_SUGGESTION_LIMIT
                            && (PEOPLE_SUGGESTION_LIMIT * mLoadingPageNo) < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = mBrowseSuggestionUrl + "&page=" + mLoadingPageNo;

                        isLoading = true;
                        loadMoreData(url);
                    }

                }
            }
        });
    }

    /**
     * Method to send request to server to get suggestion.
     */
    public void makeRequest() {
        mLoadingPageNo = 1;
        mAppConst.getJsonResponseFromUrl(mBrowseSuggestionUrl + "&page=1", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mBrowseItemList.clear();

                addDataToList(jsonObject);
                mPeopleSuggestionAdapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    /**
     * Method to load more data(if exists) on scrolling.
     *
     * @param url Url to load next page data
     */
    public void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mPeopleSuggestionAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mPeopleSuggestionAdapter.notifyItemRemoved(mBrowseItemList.size());

                addDataToList(jsonObject);
                mPeopleSuggestionAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                try {
                    SnackbarUtils.displaySnackbar(mRootView, message);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response.
     */
    public void addDataToList(JSONObject jsonObject) {
        int totalItemCount = jsonObject.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(jsonObject.optInt("totalItemCount"));
        JSONArray userArray = jsonObject.optJSONArray("users");
        if (userArray != null && userArray.length() > 0) {
            llErrorMessage.setVisibility(View.GONE);
            for (int i = 0; i < userArray.length(); i++) {
                JSONObject userObject = userArray.optJSONObject(i);
                int userId = userObject.optInt("user_id");
                int mutualFriendCount = userObject.optInt("mutualFriendCount");
                String userName = userObject.optString("displayname");
                String userImage = userObject.optString("image");
                JSONArray userMenuArray = userObject.optJSONArray("menus");
                mBrowseItemList.add(new BrowseListItems(userId, mutualFriendCount,
                        userName, userImage, false, userMenuArray));
            }

            // Show End of Result Message when there are less results
            if (totalItemCount <= PEOPLE_SUGGESTION_LIMIT * mLoadingPageNo) {
                mBrowseItemList.add(ConstantVariables.FOOTER_TYPE);
            }
        } else {
            llErrorMessage.setVisibility(View.VISIBLE);
            tvErrorIcon.setText("\uf007");
            tvErrorMessage.setText(mContext.getResources().getString(R.string.no_member_to_display));
        }
    }

}
