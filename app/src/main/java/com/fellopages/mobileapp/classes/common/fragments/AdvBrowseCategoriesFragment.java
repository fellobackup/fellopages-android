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

package com.fellopages.mobileapp.classes.common.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvBrowseCategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private Context mContext;
    private View rootView;
    private RecyclerView mRecyclerView;
    private int mTotalItemCount = 0;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private GridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mBrowseAdapter;
    private AppConstant mAppConst;
    private String mCategoriesUrl, extraModuleName, mFragmentName;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isVisibleToUser = false;
    private Snackbar snackbar;

    public static AdvBrowseCategoriesFragment newInstance(Bundle bundle) {
        // Required  public constructor
        AdvBrowseCategoriesFragment fragment = new AdvBrowseCategoriesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (getActivity() != null && getActivity().findViewById(R.id.quick_return_footer_ll) != null)
            getActivity().findViewById(R.id.quick_return_footer_ll).setVisibility(View.GONE);
        if (visible && !isVisibleToUser) {
            makeRequest();
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAppConst = new AppConstant(getActivity());
        extraModuleName = PreferencesUtils.getCurrentSelectedModule(mContext);

        if (extraModuleName != null) {
            switch (extraModuleName) {
                case ConstantVariables.PRODUCT_MENU_TITLE:
                    mCategoriesUrl = UrlUtil.BROWSE_PRODUCT_CATEGORIES + "?showCount=1";
                    mFragmentName = "store_product_categories";
                    break;

                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    mCategoriesUrl = UrlUtil.BROWSE_ADV_VIDEO_CATEGORIES + "?showCount=1";
                    mFragmentName = "adv_videos_categories";
                    break;

                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    mCategoriesUrl = UrlUtil.BROWSE_ADV_VIDEO_CHANNEL_CATEGORIES + "?showCount=1";
                    mFragmentName = "adv_videos_channel_categories";
                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    mFragmentName = "adv_events_categories";
                    mCategoriesUrl = UrlUtil.BROWSE_CATEGORIES_ADV_EVENTS_URL;
                    break;
            }
        }


        // Inflate the layout for this fragment
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        rootView = inflater.inflate(R.layout.recycler_view_layout,container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBrowseAdapter = new RecyclerViewAdapter(getActivity(), mBrowseItemList, true, 0,
                extraModuleName, AdvBrowseCategoriesFragment.this,
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantVariables.FRAGMENT_NAME, mFragmentName);
                        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, extraModuleName);
                        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, listItems.getmListItemId());
                        bundle.putString(ConstantVariables.CONTENT_TITLE, listItems.getmBrowseListTitle());
                        bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, listItems.getmTotalItemCount());

                        Intent intent = new Intent(mContext, FragmentLoadActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ConstantVariables.VIEW_PAGE_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
        mRecyclerView.setAdapter(mBrowseAdapter);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mBrowseAdapter.getItemViewType(position)) {
                    case RecyclerViewAdapter.VIEW_ITEM:
                        return 1;
                    case RecyclerViewAdapter.VIEW_PROG:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    public void makeRequest() {
        mAppConst.getJsonResponseFromUrl(mCategoriesUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseItemList.clear();

                addItemsToList(jsonObject);
                mBrowseAdapter.notifyDataSetChanged();

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
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

    public void addItemsToList(JSONObject jsonObject){
        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        mBody = jsonObject;

        mTotalItemCount = mBody.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("categories");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int category_id = jsonDataObject.optInt("category_id");
                String category_name = jsonDataObject.optString("category_name");
                JSONObject images= jsonDataObject.optJSONObject("images");
                String image = images.optString("image");
                String order = jsonDataObject.optString("order");
                int count = jsonDataObject.optInt("count");
                mBrowseItemList.add(new BrowseListItems(category_id, category_name, image, order, count));

            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf022");
            errorMessage.setText(mContext.getResources().getString(R.string.no_categories_available));
        }

        isVisibleToUser = true;

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


}
