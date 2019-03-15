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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesRecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseCategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {



    private Context mContext;
    private View rootView;
    private TextView errorIcon;
    private SelectableTextView errorMessage;
    private LinearLayout messageLayout;
    private RecyclerView mRecyclerView;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private RecyclerView.Adapter mBrowseAdapter;
    private AppConstant mAppConst;
    private String mCategoriesUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isVisibleToUser = false;
    private int mListingTypeId;
    private Snackbar snackbar;
    private String mCurrentSelectedModule, mFragmentName;

    public static BrowseCategoriesFragment newInstance(Bundle bundle) {
        // Required  public constructor
        BrowseCategoriesFragment fragment = new BrowseCategoriesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            makeRequest();
        } else if(snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // No data message views
        messageLayout = rootView.findViewById(R.id.message_layout);
        errorIcon = rootView.findViewById(R.id.error_icon);
        errorMessage = rootView.findViewById(R.id.error_message);
        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCategoriesUrl = getCategoriesUrl();

        mBrowseAdapter = new AdvModulesRecyclerViewAdapter(mContext, mBrowseItemList, "category", mListingTypeId,
                BrowseCategoriesFragment.this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, mFragmentName);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE,
                        PreferencesUtils.getCurrentSelectedModule(mContext));
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
                    case AdvModulesRecyclerViewAdapter.VIEW_ITEM:
                        return 1;
                    case AdvModulesRecyclerViewAdapter.VIEW_PROG:
                        return 2; //number of columns of the grid
                    case AdvModulesRecyclerViewAdapter.TYPE_FB_AD:
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    /**
     * Method to send request to server to get browse category page data.
     */
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

    /**
     *Method to add data to the list.
     *
     * @param jsonObject JsonObject by which getting the response
     */
    public void addItemsToList(JSONObject jsonObject){

        int mTotalItemCount = jsonObject.optInt("totalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);

        JSONArray mDataResponse = jsonObject.optJSONArray("categories");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            messageLayout.setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                int category_id = jsonDataObject.optInt("category_id");
                String category_name = jsonDataObject.optString("category_name");
                String image = jsonDataObject.optString("image_icon");
                JSONObject imagesObject = jsonDataObject.optJSONObject("images");
                if (imagesObject != null && imagesObject.length() > 0 && imagesObject.has("image_icon")) {
                    image = imagesObject.optString("image_icon");
                }
                String order = jsonDataObject.optString("order");
                int count = jsonDataObject.optInt("count");
                mBrowseItemList.add(new BrowseListItems(category_id, category_name, image, order, count));
            }
        }else {
            messageLayout.setVisibility(View.VISIBLE);

            errorIcon.setText("\uf15c");
            errorMessage.setText(mContext.getResources().getString(R.string.no_category));
        }

        isVisibleToUser = true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantVariables.VIEW_PAGE_CODE) {
            switch (mCurrentSelectedModule){
                case "sitereview_listing":
                    PreferencesUtils.updateCurrentModule(mContext, "sitereview_listing");
                    break;
                case "core_main_sitegroup":
                    PreferencesUtils.updateCurrentModule(mContext, "core_main_sitegroup");
                    break;
                case "core_main_sitepage":
                case "sitepage":
                    PreferencesUtils.updateCurrentModule(mContext, "sitepage");
                    break;
                case ConstantVariables.STORE_MENU_TITLE:
                    PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.STORE_MENU_TITLE);
                    break;
                default:
                    break;
            }
        }
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
    public void onDetach() {
        super.onDetach();

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

    private String getCategoriesUrl(){
        String url = null;
        switch (mCurrentSelectedModule){

            case "sitereview_listing":
                mFragmentName = "mlt_categories";
                // Getting current Listing variables from preferences.
                mListingTypeId = PreferencesUtils.getCurrentSelectedListingId(mContext);
                url = UrlUtil.BROWSE_CATEGORIES_MLT_URL + "&listingtype_id=" + mListingTypeId;
                break;
            case "core_main_sitegroup":
                url = UrlUtil.ADV_GROUP_CATEGORY_HOME_PAGE_URL;
                mFragmentName = "group_categories";
                break;
            case "core_main_sitepage":
            case "sitepage":
                url = UrlUtil.CATEGORY_SITE_PAGE_URL ;
                mFragmentName = "site_page_categories";
                break;
            case ConstantVariables.STORE_MENU_TITLE:
                url = UrlUtil.STORE_CATEGORY_URL + "&showCount=1" ;
                mFragmentName = "store_categories";
                break;
            default:
                break;
        }
        return url;
    }
}

