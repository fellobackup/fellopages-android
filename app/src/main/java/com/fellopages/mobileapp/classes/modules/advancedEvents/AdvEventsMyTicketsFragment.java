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

package com.fellopages.mobileapp.classes.modules.advancedEvents;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.modules.advancedEvents.ticketsSelling.AdvEventsOrderViewPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvEventsMyTicketsFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    private AppConstant mAppConst;
    private Context mContext;
    private View rootView, footerView;
    private ListView mListView;
    String mMyTicketsUrl;
    private int pageNumber = 1, mLoadingPageNo = 1,mTotalItemCount, mSelectedItem = -1;
    private AdvEventsBrowseDataAdapter mBrowseDataAdapter;
    private String orderType = "current";
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private boolean isLoading=false, isVisibleToUser = false;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private Spinner spinner;
    private SpinnerAdapter adapter;


    public AdvEventsMyTicketsFragment() {
        // Required empty public constructor
    }

    public static AdvEventsMyTicketsFragment newInstance(Bundle bundle){
        AdvEventsMyTicketsFragment fragment = new AdvEventsMyTicketsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(getActivity());

        rootView = inflater.inflate(R.layout.list_view_layout,container,false);
        mListView = rootView.findViewById(R.id.list_item_view);
        footerView = CustomViews.getFooterView(inflater);

        spinner = rootView.findViewById(R.id.filter_view);
        rootView.findViewById(R.id.eventTicketsFilter).setVisibility(View.VISIBLE);

        ViewCompat.setNestedScrollingEnabled(mListView,true);

        mMyTicketsUrl = UrlUtil.MY_TICKETS_ADV_EVENTS_URL + "&page=" + pageNumber;

        mBrowseDataAdapter = new AdvEventsBrowseDataAdapter(getActivity(), R.layout.list_row, mBrowseItemList,
                "my_tickets_siteevent");
        mListView.setAdapter(mBrowseDataAdapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));

        adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);

        /* Add events filter type to spinner using adpter */
        adapter.add(getResources().getString(R.string.tickets_filter_current_order));
        adapter.add(getResources().getString(R.string.tickets_filter_past_order));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setTag("orderFilter");

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        return rootView;
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(isVisibleToUser);
        if (visible && !isVisibleToUser && spinner != null) {
            spinner.setAdapter(adapter);
            isVisibleToUser = true;
        } else if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    private void makeRequest() {
        try {

            mLoadingPageNo = 1;
            mMyTicketsUrl = UrlUtil.MY_TICKETS_ADV_EVENTS_URL + "&viewType=" + orderType + "&page=" + pageNumber;

            mAppConst.getJsonResponseFromUrl(mMyTicketsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBrowseItemList.clear();
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if(snackbar != null && snackbar.isShown())
                        snackbar.dismiss();

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    addDataToList(jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (swipeRefreshLayout.isRefreshing()) {
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void addDataToList(JSONObject jsonObject) {

        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");

        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("response");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {

                JSONObject jsonObj = mDataResponse.optJSONObject(i);

                // Getting data from individual JSONObject
                int orderId = jsonObj.optInt("order_id");
                int eventId = jsonObj.optInt("event_id");
                String eventTitle = jsonObj.optString("event_title");
                String eventTime = jsonObj.optString("creation_date");
                String image = jsonObj.optString("image");
                JSONObject orderInfo = jsonObj.optJSONObject("order_info");

                mBrowseItemList.add(new BrowseListItems(orderId, eventId, eventTitle, eventTime, image, orderInfo));

            }
            // Show End of Result Message when there are less results
            if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                CustomViews.showEndOfResults(mContext, footerView);
            }

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf080");
            errorMessage.setText(mContext.getResources().getString(R.string.no_tickets));
        }
        mBrowseDataAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if(mBrowseItemList.size() > 0) {
            BrowseListItems browseListItems = (BrowseListItems) mBrowseItemList.get(position);
            if (browseListItems != null) {
                String url = UrlUtil.ORDER_VIEW_PAGE_ADV_EVENTS_URL + "order_id=" + browseListItems.getmOrderId();
                Intent mainIntent = new Intent(mContext, AdvEventsOrderViewPage.class);
                mainIntent.putExtra(ConstantVariables.URL_STRING, url);
                mainIntent.putExtra("order_id", String.valueOf(browseListItems.getmOrderId()));
                getActivity().startActivity(mainIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mListView != null){
            mListView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                makeRequest();
            }
        });

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if (limit == totalItemCount && !isLoading)
        {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount())
            {
                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = UrlUtil.MY_TICKETS_ADV_EVENTS_URL + "&page=" + mLoadingPageNo;

                isLoading = true;
                loadMoreData(url);
            }
        }


    }

    /**
     * Function to load more data
     * @param url
     */
    private void loadMoreData(String url) {
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(footerView);
                addDataToList(jsonObject);
                isLoading=false;

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                try {
                    SnackbarUtils.displaySnackbar(rootView, message);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

        });

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedItem = position;
        adapter.getCustomView(position, view, parent, mSelectedItem);
        switch (position) {
            case 0:
                orderType = "current";
                break;
            case 1:
                orderType = "past";
                break;
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        makeRequest();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
