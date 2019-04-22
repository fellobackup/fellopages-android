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

package com.fellopages.mobileapp.classes.modules.multipleListingType;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.WhereToBuyAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhereToBuyFragment extends Fragment {

    private View rootView;
    private Context mContext;
    private AppConstant mAppConst;
    private String mListUrl;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    private Snackbar snackbar;
    private List<BrowseListItems> mBrowseItemList;
    private WhereToBuyAdapter mAdapter;
    private String mCurrency;
    private boolean isVisibleToUser = false;


    public WhereToBuyFragment() {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        mListUrl = getArguments().getString(ConstantVariables.URL_STRING);
        boolean isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

        mBrowseItemList = new ArrayList<>();
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mAdapter = new WhereToBuyAdapter(mContext, mBrowseItemList);
        mRecyclerView.setAdapter(mAdapter);

        if (isFirstTab) {
            makeRequest();
        }

        return rootView;
    }

    private void makeRequest() {

        mAppConst.getJsonResponseFromUrl(mListUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(snackbar != null && snackbar.isShown())
                    snackbar.dismiss();

                addItemsToList(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                try {
                    if (isRetryOption) {
                        snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, rootView, message,
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
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addItemsToList(JSONObject jsonObject){

        if(jsonObject != null){

            mCurrency = jsonObject.optString("currency");
            JSONArray priceInfoArray = jsonObject.optJSONArray("priceInfo");
            if(priceInfoArray != null && priceInfoArray.length() != 0){

                for(int i = 0; i < priceInfoArray.length(); i++){

                    String price;
                    JSONObject priceObject = priceInfoArray.optJSONObject(i);
                    String title = priceObject.optString("title");
                    String url = priceObject.optString("url");
                    if(priceObject.optDouble("price") != 0){
                        price = GlobalFunctions.getFormattedCurrencyString(mCurrency, priceObject.optDouble("price"));
                    } else {
                        price = mContext.getResources().getString(R.string.not_available_text);
                    }
                    int minPriceOption = priceObject.optInt("minPriceOption");
                    String image = priceObject.optString("image");
                    String tagImage = priceObject.optString("tag_image");
                    mBrowseItemList.add(new BrowseListItems(title, price, url, image, minPriceOption, tagImage));
                }
            } else{
                rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                TextView errorIcon = rootView.findViewById(R.id.error_icon);
                SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
                errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                errorIcon.setText("\uf143");
                errorMessage.setText(mContext.getResources().getString(R.string.no_blogs));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

}
