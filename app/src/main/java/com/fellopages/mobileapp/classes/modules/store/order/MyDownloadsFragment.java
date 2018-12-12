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

package com.fellopages.mobileapp.classes.modules.store.order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.store.utils.OrderInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyDownloadsFragment extends Fragment implements OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private AppConstant mAppConst;
    private View rootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mBrowseOrderAdapter;
    private List<OrderInfoModel> mOrderList;
    private OrderInfoModel mOrderInfo;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mBrowseOrderUrl;
    private int mDefaultPageNumber = 1,mLoadingPageNo = 1;
    private boolean isLoading = false,isVisibleToUser = false;
    private Snackbar snackbar;

    public MyDownloadsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        mOrderList = new ArrayList<>();
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        mOrderInfo = new OrderInfoModel();
        // The number of Columns
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBrowseOrderAdapter = new OrderViewAdapter(mContext,mOrderList,this,true);
        mRecyclerView.setAdapter(mBrowseOrderAdapter);
        mBrowseOrderUrl = UrlUtil.BROWSE_DOWNLOADABLE_URL + "page="+mDefaultPageNumber;

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

                int limit=firstVisibleItem+visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                            mOrderInfo.getTotalOrderCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = UrlUtil.BROWSE_DOWNLOADABLE_URL + "&page=" + mLoadingPageNo ;
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
            getOrders();
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();

    }
    public void getOrders() {
        try {
            mLoadingPageNo = 1;
            mAppConst.getJsonResponseFromUrl(mBrowseOrderUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    mOrderList.clear();
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if(snackbar != null && snackbar.isShown())
                        snackbar.dismiss();
                    addDataToList(jsonObject);
                    mBrowseOrderAdapter.notifyDataSetChanged();
                    isVisibleToUser = true;
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    try {
                        if (isRetryOption) {
                            snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                                    new SnackbarUtils.OnSnackbarActionClickListener() {
                                        @Override
                                        public void onSnackbarActionClick() {
                                            rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                            getOrders();
                                        }
                                    });
                        } else {
                            SnackbarUtils.displaySnackbar(rootView, message);
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private  void loadMoreData(String url){
        //add null , so the adapter will check view_type and show progress bar at bottom
        mOrderList.add(null);
        mBrowseOrderAdapter.notifyItemInserted(mOrderList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mOrderList.remove(mOrderList.size() - 1);
                mBrowseOrderAdapter.notifyItemRemoved(mOrderList.size());
                addDataToList(jsonObject);
                mBrowseOrderAdapter.notifyItemInserted(mOrderList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }
    public void addDataToList(JSONObject jsonObject){
        mBody = jsonObject;
        mOrderInfo.setTotalOrderCount(mBody.optInt("totalItemCount"));
        mDataResponse = mBody.optJSONArray("downloadablefiles");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int order_id = jsonDataObject.optInt("order_id");
                int downloads = jsonDataObject.optInt("downloads");
                int remainingDownloads = jsonDataObject.optInt("remainingDownloads");
                String file_title = jsonDataObject.optString("title");
                JSONArray urlParamArray = jsonDataObject.optJSONArray("option");
                String finalUrl;
                if(urlParamArray != null) {
                    JSONObject urlParamObject = urlParamArray.optJSONObject(0);
                    String defaultUrl = urlParamObject.optString("url");
                    JSONObject urlParams = urlParamObject.optJSONObject("urlparams");
                    finalUrl = AppConstant.DEFAULT_URL + defaultUrl + "?product_id=" + urlParams.optString("product_id") +
                            "&downloadablefile_id=" + urlParams.optString("downloadablefile_id") +
                            "&download_id=" + urlParams.optString("download_id");
                    finalUrl = mAppConst.buildQueryString(finalUrl,mAppConst.getAuthenticationParams());
                }else {
                    finalUrl =  jsonDataObject.optString("option");
                }
                mOrderList.add(new OrderInfoModel(order_id,file_title,finalUrl,downloads,remainingDownloads));
            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf0ed");
            errorMessage.setText(mContext.getResources().getString(R.string.no_downloads));
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onItemClick(View view, int position) {
        OrderInfoModel orderInfoModel = mOrderList.get(position);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(orderInfoModel.getFileOption()));
        browserIntent.setPackage("com.android.chrome");
        startActivity(browserIntent);

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
                getOrders();
            }
        });
    }
}
