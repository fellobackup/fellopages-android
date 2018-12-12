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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.CartView;
import com.fellopages.mobileapp.classes.modules.store.utils.OrderInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BrowseOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnItemClickListener, AdapterView.OnItemSelectedListener {
    private AppConstant mAppConst;
    private View rootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mBrowseOrderAdapter;
    private OrderInfoModel mOrderInfo;
    private List<OrderInfoModel> mOrderList;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mBrowseOrderUrl, mDefaultCurrency;
    private Snackbar snackbar;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1, mSelectedItem = -1;
    private boolean isLoading = false, isSearchTextSubmitted = false,isAdLoaded = false;
    private boolean isVisibleToUser = false, isStoreOrders = false,isContentSelected = false;
    private HashMap<String, String> searchParams;
    Spinner contentSpinner;
    private CardView contentSpinnerLayout;
    private SpinnerAdapter contentListAdapter;
    public BrowseOrderFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
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

        // Updating current selected module
        if (PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                !PreferencesUtils.getCurrentSelectedModule(mContext).equals("sitestore_orders")) {
            PreferencesUtils.updateCurrentModule(mContext, "sitestore_orders");
        }

        mOrderInfo = new OrderInfoModel();
        mOrderList = new ArrayList<>();
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        // The number of Columns
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBrowseOrderAdapter = new OrderViewAdapter(mContext,mOrderList,this,false);
        mRecyclerView.setAdapter(mBrowseOrderAdapter);
        mBrowseOrderUrl = UrlUtil.MY_ORDERS_URL + "page="+pageNumber;
        if(getArguments() != null) {
            if(getArguments().containsKey("isStoreOrders")){
                mBrowseOrderUrl = UrlUtil.MY_ORDERS_URL + "/my-store-order?page="+pageNumber;
                isStoreOrders = true;
                contentSpinnerLayout = (CardView)rootView.findViewById(R.id.filterLayout);
                contentSpinner = (Spinner) rootView.findViewById(R.id.filter_view);
            }
            Set<String> searchArgumentSet = getArguments().keySet();
            searchParams = new HashMap<>();
            for (String key : searchArgumentSet) {
                String value = getArguments().getString(key);
                if (key.equals("query")) {
                    key = "order_id";
                }
                if (value != null && !value.isEmpty()) {
                    searchParams.put(key, value);
                }
            }
            if (searchParams != null && searchParams.size() != 0) {
                isSearchTextSubmitted = true;
                mBrowseOrderUrl = mAppConst.buildQueryString(mBrowseOrderUrl, searchParams);
            }

        }
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

                if(limit==totalItemCount && !isLoading) {

                    if(limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)  <
                            mOrderInfo.getTotalOrderCount()){

                        mLoadingPageNo = mLoadingPageNo + 1;
                        isLoading = true;
                        loadMoreData(UrlUtil.MY_ORDERS_URL + "?limit=" + AppConstant.LIMIT
                                + "&page=" + mLoadingPageNo);
                    }

                }
            }
        });

        getOrders();
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();

    }
    public void getOrders() {
        try {
            mLoadingPageNo = 1;
            if (!isSearchTextSubmitted) {
                // Don't show data in case of searching and User Profile Tabs.
                mBrowseOrderUrl = UrlUtil.MY_ORDERS_URL+ "?limit=" + AppConstant.LIMIT + "&page=" + pageNumber;

            }
            mAppConst.getJsonResponseFromUrl(mBrowseOrderUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    mOrderList.clear();
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    addDataToList(jsonObject);
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mSwipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, ConstantVariables.REFRESH_DELAY_TIME);
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
        mDataResponse = mBody.optJSONArray("orders");
        mDefaultCurrency = mBody.optString("currency");
        mTotalItemCount = mBody.optInt("totalItemCount");
        mOrderInfo.setTotalOrderCount(mTotalItemCount);
        if(isStoreOrders) mOrderList.removeAll(mOrderList);
        if(mDataResponse != null && mDataResponse.length() > 0) {
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int order_id = jsonDataObject.optInt("order_id");
                String order_date = jsonDataObject.optString("creation_date");
                double order_amount = jsonDataObject.optDouble("order_total");
                String order_status = jsonDataObject.optString("order_status");
                JSONArray order_options = jsonDataObject.optJSONArray("options");
                JSONObject item_details = jsonDataObject.optJSONObject("item_details");
                mOrderList.add(new OrderInfoModel(order_id,order_date,order_status,order_amount,
                        order_options,mDefaultCurrency,item_details));
            }
            mBrowseOrderAdapter.notifyDataSetChanged();
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
        }else {
            mBrowseOrderAdapter.notifyDataSetChanged();
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf291");
            errorMessage.setText(mContext.getResources().getString(R.string.no_orders));
        }
        mDataResponse = mBody.optJSONArray("stores");
        if(!isContentSelected && isStoreOrders && mDataResponse != null && mDataResponse.length() > 0) {
            contentListAdapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);
            contentListAdapter.add(mContext.getResources().getString(R.string.select_store));
            contentListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contentSpinner.setAdapter(contentListAdapter);
            for (int i = 0; i < mDataResponse.length(); i++) {
                contentListAdapter.add(mDataResponse.optJSONObject(i).optString("title"));
            }
            mRecyclerView.setPadding(0,mContext.getResources().getDimensionPixelSize(R.dimen.dimen_100dp),0,0);
        contentSpinnerLayout.setVisibility(View.VISIBLE);
        contentSpinner.setOnItemSelectedListener(this);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onItemClick(View view, int position) {

        showPopup(view, mOrderList.get(position).getOrderOptions());
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



    JSONObject menuJsonObject;
    String menuName,redirectUrl;
    HashMap<String,String> postParams;
    public void showPopup(View v, final JSONArray menuArray) {

        PopupMenu popup = new PopupMenu(mContext, v, Gravity.END);

        if (menuArray.length() != 0) {
            for (int i = 0; i < menuArray.length(); i++) {
                try {
                    menuJsonObject = menuArray.getJSONObject(i);
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, menuJsonObject.getString("label").trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                try {
                    menuJsonObject = menuArray.getJSONObject(id);
                    menuName = menuJsonObject.getString("name");
                    JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                    if(menuName.equals("make-payment")) {
                        redirectUrl = menuJsonObject.getString("url");
                    }else {
                        redirectUrl = AppConstant.DEFAULT_URL + menuJsonObject.getString("url");
                    }
                    if (urlParams != null && urlParams.length() != 0) {
                        JSONArray urlParamsNames = urlParams.names();
                        for (int j = 0; j < urlParams.length(); j++) {
                            String name = urlParamsNames.getString(j);
                            String value = urlParams.getString(name);
                            postParams.put(name, value);
                        }
                        redirectUrl = mAppConst.buildQueryString(redirectUrl, postParams);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (menuName) {
                    case "view":
                        Intent intent = new Intent(mContext,OrderViewActivity.class);
                        intent.putExtra(ConstantVariables.VIEW_PAGE_URL,redirectUrl);
                        startActivityForResult(intent,ConstantVariables.ORDER_PAGE_CODE);
                        break;
                    case "reorder":
                        Intent orderIntent = new Intent(mContext,CartView.class);
                        orderIntent.putExtra("order_url",redirectUrl);
                        startActivityForResult(orderIntent,ConstantVariables.ORDER_PAGE_CODE);
                        break;
                    case "shipping":
                        Intent shipIntent = new Intent(mContext,OrderViewActivity.class);
                        shipIntent.putExtra("shipping_url",redirectUrl);
                        startActivityForResult(shipIntent,ConstantVariables.ORDER_PAGE_CODE);
                        break;
                    case "cancel":
                        cancelOrder();
                        break;
                    case "make-payment":
                        Intent payment = new Intent(mContext, WebViewActivity.class);
                        payment.putExtra("url",redirectUrl);
                        payment.putExtra("cartorder",true);
                        startActivityForResult(payment, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                        break;

                }
                return true;
            }
        });
        popup.show();
    }

    public void cancelOrder(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(mContext.getResources().getString(R.string.cancel_order_warning));
        alertBuilder.setTitle(mContext.getResources().getString(R.string.cancel_order_label));
        alertBuilder.setPositiveButton(mContext.getResources().

                getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mAppConst.putResponseForUrl(redirectUrl, null, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {
                                mAppConst.hideProgressDialog();
                                SnackbarUtils.displaySnackbar(rootView,
                                        mContext.getResources().getString(R.string.order_cancel_msg));
                            }

                            @Override
                            public void onErrorInExecutingTask(String message,
                                                               boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                                SnackbarUtils.displaySnackbar(rootView, message);
                            }
                        }

                );
            }
        });

        alertBuilder.setNegativeButton(mContext.getResources().

                        getString(R.string.cancel),

                new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }

        );
        alertBuilder.create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        mDataResponse = mBody.optJSONArray("stores");
        String content_id = "0";
        isContentSelected = true;
        if(mDataResponse.optJSONObject(position - 1) != null){
            content_id = mDataResponse.optJSONObject(position - 1).optString("store_id");
        }
        mBrowseOrderUrl = UrlUtil.MY_ORDERS_URL + "/my-store-order?page="+pageNumber+"&store_id="+content_id;
        getOrders();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
