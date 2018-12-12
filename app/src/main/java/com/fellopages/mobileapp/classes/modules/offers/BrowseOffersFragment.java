/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.modules.offers;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.BrowseDataAdapter;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseOffersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private AppConstant mAppConst;
    private Context mContext;
    private View rootView, footerView;
    private ListView mListView;
    private String mBrowseOffersUrl;
    private int mLoadingPageNo = 1, mTotalItemCount;
    private BrowseDataAdapter mBrowseDataAdapter;

    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private boolean isLoading = false, isVisibleToUser = false, isFirstTab = false;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private String idName, mCurrentSelectedModule, mExtraModuleType;
    private ImageLoader mImageLoader;


    public BrowseOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        // Make sure that currently visible
        if (visible && !isVisibleToUser && mContext != null) {
            makeRequest();
        }
    }

    public static BrowseOffersFragment newInstance() {
        BrowseOffersFragment fragment = new BrowseOffersFragment();
        Bundle bundle =  new Bundle();
        bundle.putString(ConstantVariables.URL_STRING, UrlUtil.STORE_OFFERS_URL);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(getActivity());
        mImageLoader = new ImageLoader(getActivity());

        rootView = inflater.inflate(R.layout.list_view_layout, container,false);
        mListView = (ListView) rootView.findViewById(R.id.list_item_view);
        footerView = CustomViews.getFooterView(inflater);

        ViewCompat.setNestedScrollingEnabled(mListView,true);

        if(getArguments()!= null) {
            mBrowseOffersUrl = getArguments().getString(ConstantVariables.URL_STRING);
            mExtraModuleType = getArguments().getString(ConstantVariables.EXTRA_MODULE_TYPE);
            isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
        }


        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);

        if (mCurrentSelectedModule != null && mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
            idName = "coupon_id";
            if (!mBrowseOffersUrl.contains("?limit")) {
                mBrowseOffersUrl += "&limit=" + AppConstant.LIMIT;
            }
        } else {
            idName = "offer_id";
            mBrowseOffersUrl +="?limit=" + AppConstant.LIMIT;
        }

        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_offers")) {
            PreferencesUtils.updateCurrentModule(mContext, "core_main_offers");
        }

        mBrowseDataAdapter = new BrowseDataAdapter(getActivity(), R.layout.offer_view, mBrowseItemList);
        mListView.setAdapter(mBrowseDataAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        if(mExtraModuleType == null || isFirstTab) {
            makeRequest();
        }
        return rootView;
    }

    private void makeRequest() {

        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mBrowseOffersUrl + "&page=" + mLoadingPageNo, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                addDataToList(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);

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

    private void addDataToList(JSONObject jsonObject) {

        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("offers");

        if (mDataResponse == null) {
            mDataResponse = mBody.optJSONArray("response");
        }

        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {

                JSONObject jsonObj = mDataResponse.optJSONObject(i);
                // Getting data from individual JSONObject

                int offerId = jsonObj.optInt(idName);

                String title = jsonObj.optString("title");
                int claimCount = jsonObj.optInt("claimed");
                int totalClaims = jsonObj.optInt("claim_count");
                String couponCode = jsonObj.optString("coupon_code");
                String ownerImage = jsonObj.optString("image_profile");
                String startTime = jsonObj.optString("start_time");
                String endTime = jsonObj.optString("end_time");
                int endSettings = jsonObj.optInt("end_settings");
                String offerDescription = jsonObj.optString("description");

                //Adding data to list
                mBrowseItemList.add(new BrowseListItems(ownerImage, title,
                        couponCode, claimCount,totalClaims, endSettings, endTime,startTime, offerId,
                        offerDescription));

            }
        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf080");
            errorMessage.setText(mContext.getResources().getString(R.string.no_offers));
        }
        mBrowseDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mListView != null) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        View offerInfoView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.offer_view, null, false);

        ImageView offerImageView = (ImageView) offerInfoView.findViewById(R.id.contentImage);
        TextView offerTitle = (TextView) offerInfoView.findViewById(R.id.contentTitle);
        TextView offerEndDate = (TextView) offerInfoView.findViewById(R.id.contentDetail);
        TextView offerStartDate = (TextView) offerInfoView.findViewById(R.id.coupon_start_date);
        TextView offerClaimCount = (TextView) offerInfoView.findViewById(R.id.claimCount);
        SelectableTextView couponCodeButton = (SelectableTextView) offerInfoView.findViewById(R.id.couponCodeButton);
        TextView offerDescription = (TextView) offerInfoView.findViewById(R.id.offerDescription);

        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), offerImageView);

        offerTitle.setText(listItems.getmBrowseListTitle());
        if(listItems.getmEndSetting() == 0){
            offerEndDate.setText(mContext.getResources().
                    getString(R.string.end_date_text) + ": " + mContext.getResources().
                    getString(R.string.offer_never_expires));
        }else{
            String convertedDate = AppConstant.getMonthFromDate(listItems.getmEndTime(), "MMM") +
                    " " + AppConstant.getDayFromDate(listItems.getmEndTime()) +
                    ", "  + AppConstant.getYearFormat(listItems.getmEndTime()) ;
            offerEndDate.setText(mContext.getResources().
                    getString(R.string.end_date_text) + ": " + convertedDate);
        }
        offerStartDate.setText(mContext.getResources().
                getString(R.string.start_date_label) +": "+
                AppConstant.getMonthFromDate(listItems.getStartTime(), "MMM") +
                " " + AppConstant.getDayFromDate(listItems.getStartTime()) +
                ", "  + AppConstant.getYearFormat(listItems.getStartTime()) );


        if(listItems.getTotalClaims() == -1){
            offerClaimCount.setText(mContext.getResources().
                    getString(R.string.claim_count_text, listItems.getmClaimCount()));
        }else {
            offerClaimCount.setText(mContext.getResources().
                    getString(R.string.claim_count_text, listItems.getmClaimCount()) + " - "
                    + mContext.getResources().getQuantityString(R.plurals.coupon_left,
                    listItems.getTotalClaims() - listItems.getmClaimCount(),
                    listItems.getTotalClaims() - listItems.getmClaimCount()));
        }
        offerClaimCount.setVisibility(View.VISIBLE);
        couponCodeButton.setText(listItems.getmCouponCode());
        couponCodeButton.setVisibility(View.VISIBLE);

        offerDescription.setText(listItems.getmOfferDescription());
        offerDescription.setVisibility(View.VISIBLE);

        alertBuilder.setView(offerInfoView);
        Dialog dialog = alertBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if (limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                isLoading = true;
                loadMoreData(mBrowseOffersUrl + "&page=" + mLoadingPageNo);
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
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                CustomViews.removeFooterView(mListView, footerView);
                addDataToList(jsonObject);
                isLoading = false;

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }

        });

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggle = menu.findItem(R.id.viewToggle);
        if (toggle != null) {
            toggle.setVisible(false);
        }
    }
}
