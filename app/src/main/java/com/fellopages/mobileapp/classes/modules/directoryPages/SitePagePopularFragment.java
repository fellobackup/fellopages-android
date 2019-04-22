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
 *
 */

package com.fellopages.mobileapp.classes.modules.directoryPages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.BrowseDataAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * A simple {@link Fragment} subclass.
 */
public class SitePagePopularFragment extends Fragment implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,
        AdapterView.OnItemSelectedListener {

    View rootView, footerView;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private String  mBrowsePageUrl, mCurrentSelectedModule;
    AppConstant mAppConst;
    private GridViewWithHeaderAndFooter mGridView;
    private JSONObject mBody;
    private JSONArray mDataResponse, mOrderByFilterResponse;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private BrowseDataAdapter mBrowseDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false,isLoadCategory = true, isVisibleToUser = false;
    private Context mContext;
    Snackbar snackbar;
    Spinner spinner;
    SpinnerAdapter adapter;
    private Map<String, String> postParams;
    private CardView spinnerLayout;
    int mSelectedItem = -1;


    public static SitePagePopularFragment newInstance(Bundle bundle) {
        // Required public constructor
        SitePagePopularFragment fragment = new SitePagePopularFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mGridView != null){
            mGridView.smoothScrollToPosition(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        postParams = new HashMap<>();

        mAppConst = new AppConstant(getActivity());

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.grid_view_layout, container, false);
        footerView = CustomViews.getFooterView(inflater);

        spinnerLayout = rootView.findViewById(R.id.eventFilterBlock);
        spinner = rootView.findViewById(R.id.filter_view);

        mGridView = rootView.findViewById(R.id.gridView);
        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);

        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);
        ViewCompat.setNestedScrollingEnabled(mGridView, true);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("sitepage")){
            PreferencesUtils.updateCurrentModule(mContext, "sitepage");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        mBrowsePageUrl = UrlUtil.POPULAR_SITE_PAGE_URL + "&showOrderBy=1" + "&page=" + pageNumber;

        mBrowseDataAdapter = new BrowseDataAdapter(getActivity(), R.layout.group_item_view, mBrowseItemList);
        mGridView.setAdapter(mBrowseDataAdapter);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);


        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        isLoadCategory = true;
        isVisibleToUser = false;
        adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);
        adapter.add(getResources().getString(R.string.popular_page_filter_text));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setTag("subCategory");

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        boolean isAllowedToView = listItems.isAllowToView();
        String groupTitle = listItems.getmBrowseListTitle();

        if (!isAllowedToView) {
            SnackbarUtils.displaySnackbar(rootView,
                    mContext.getResources().getString(R.string.unauthenticated_view_message));
        } else {
            Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, listItems.getmListItemId(),
                    PreferencesUtils.getCurrentSelectedModule(mContext), null);
            mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, groupTitle);
            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(isVisibleToUser);
        // Make sure that currently visible
        if (visible && !isVisibleToUser) {
            sendRequestToServer(mBrowsePageUrl);
        } else {
            if(snackbar != null && snackbar.isShown())
                snackbar.dismiss();
        }
    }

    /**
     * Function to send request to server
     */
    public void sendRequestToServer(final String mBrowsePageUrl) {

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mBrowsePageUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                addDataToList(jsonObject);

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
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    sendRequestToServer(mBrowsePageUrl);
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }

            }
        });
    }

    public void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;
        if (mBody != null) {
            try {
                mTotalItemCount = mBody.getInt("totalItemCount");

                if (isLoadCategory) {
                    mOrderByFilterResponse = mBody.optJSONArray("orderby");
                    if (mOrderByFilterResponse != null && mOrderByFilterResponse.length() != 0) {
                        spinnerLayout.setVisibility(View.VISIBLE);

                        for (int j = 0; j < mOrderByFilterResponse.length(); j++) {
                            JSONObject object = mOrderByFilterResponse.getJSONObject(j);
                            String title = object.optString("title");
                            String name = object.optString("name");
                            if(name.equals("view_count")) {
                                mSelectedItem = j + 1;
                            }
                            adapter.add(title);
                        }
                    } else {
                        spinnerLayout.setVisibility(View.GONE);
                    }

                    if (mSelectedItem != -1) {
                        spinner.setSelection(mSelectedItem);
                    }
                    isLoadCategory = false;
                }

                mBrowseList.setmTotalItemCount(mTotalItemCount);
                mDataResponse = mBody.optJSONArray("response");

                if(mDataResponse != null && mDataResponse.length() > 0) {
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                    for (int i = 0; i < mDataResponse.length(); i++) {
                        JSONObject jsonDataObject = mDataResponse.getJSONObject(i);
                        int page_id = jsonDataObject.getInt("page_id");
                        String title = jsonDataObject.getString("title");
                        String image_icon = jsonDataObject.getString("image");
                        int allow_to_view = jsonDataObject.getInt("allow_to_view");
                        int like_count = jsonDataObject.getInt("like_count");
                        int follow_count = jsonDataObject.getInt("follow_count");
                        int closed = jsonDataObject.optInt("closed");
                        if(allow_to_view == 1)
                            mBrowseItemList.add(new BrowseListItems(image_icon, title, page_id, true,
                                    like_count, follow_count, closed));
                        else
                            mBrowseItemList.add(new BrowseListItems(image_icon, title, page_id, false,
                                    like_count, follow_count, closed));
                    }
                }else{
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = rootView.findViewById(R.id.error_icon);
                    SelectableTextView errorMessage = rootView.findViewById
                            (R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf15c");
                    errorMessage.setText(mContext.getResources().getString(R.string.no_pages));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                mGridView.setVisibility(View.INVISIBLE);
            }
            isVisibleToUser = true;
            mBrowseDataAdapter.notifyDataSetChanged();
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
                if (postParams != null && !postParams.isEmpty()) {
                    mBrowsePageUrl = mAppConst.buildQueryString(mBrowsePageUrl, postParams);
                }
                sendRequestToServer(mBrowsePageUrl);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !isLoading) {

            if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()){

                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;

                String url = mBrowsePageUrl = UrlUtil.POPULAR_SITE_PAGE_URL + "&showCategory=1" + "&page=" + mLoadingPageNo;

                isLoading = true;
                loadMoreData(url);
            }

        }
    }

    /* Load More Pages On Scroll */

    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(footerView);
                addDataToList(jsonObject);
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Update currentSelected Option on back press*/
        switch (requestCode){
            case ConstantVariables.VIEW_PAGE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (isVisibleToUser) {
            mSelectedItem = position;
            adapter.getCustomView(position, view, parent, mSelectedItem);
            swipeRefreshLayout.setRefreshing(true);
            postParams.clear();
            if (position != 0) {
                JSONObject object = mOrderByFilterResponse.optJSONObject(position - 1);
                String name = object.optString("name");
                postParams.put("orderby", name);
                String url = mAppConst.buildQueryString(mBrowsePageUrl, postParams);
                sendRequestToServer(url);

            } else {
                sendRequestToServer(mBrowsePageUrl);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
