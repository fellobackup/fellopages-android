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

package com.fellopages.mobileapp.classes.modules.store.fragments;

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
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.adapters.StoreViewAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageStoreFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private AppConstant mAppConst;
    private View rootView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mBrowseAdapter;
    private List<Object> mStoreItemList;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private String mManageStoreUrl;
    private StoreInfoModel mStoreInfo;
    private boolean isVisibleToUser = false, isLoading = false, isAdLoaded = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private int mLoadingPageNo = 1,pageNumber = 1;

    public ManageStoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageStoreFragment newInstance(String param1, String param2) {
        ManageStoreFragment fragment = new ManageStoreFragment();
        return fragment;
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
        rootView = inflater.inflate(R.layout.recycler_view_layout, null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mStoreItemList = new ArrayList<>();
        mContext = getContext();
        mStoreInfo = new StoreInfoModel();
        mAppConst = new AppConstant(mContext);
        if(PreferencesUtils.getCurrentSelectedModule(mContext) != null
                && !PreferencesUtils.getCurrentSelectedModule(mContext).equals("core_main_sitestore")) {
            PreferencesUtils.updateCurrentModule(mContext,"core_main_sitestore");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(mContext, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBrowseAdapter = new StoreViewAdapter(mContext,mStoreItemList,this);
        mRecyclerView.setAdapter(mBrowseAdapter);
        mManageStoreUrl = UrlUtil.MANAGE_STORE_URL + "page=1";
        addListeners();
        return rootView;
    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            getStoreResult();
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public void addListeners(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();
                int lastVisibleCount = gridLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mStoreInfo.getTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = UrlUtil.MANAGE_STORE_URL + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        loadMoreData(url);
                    }

                }
            }
        });

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();

    }
    public void getStoreResult() {
        try {

            mAppConst.getJsonResponseFromUrl(mManageStoreUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                    mStoreItemList.clear();
                    setItemInTheList(jsonObject);
                    isVisibleToUser = true;
                    mBrowseAdapter.notifyDataSetChanged();
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
                    try {
                        if (isRetryOption) {
                            snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, rootView, message,
                                    new SnackbarUtils.OnSnackbarActionClickListener() {
                                        @Override
                                        public void onSnackbarActionClick() {
                                            rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                            getStoreResult();
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        mStoreItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mStoreItemList.size() - 1);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mStoreItemList.remove(mStoreItemList.size() - 1);
                mBrowseAdapter.notifyItemRemoved(mStoreItemList.size());
                setItemInTheList(jsonObject);
                mBrowseAdapter.notifyItemInserted(mStoreItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }
    public void setItemInTheList(JSONObject jsonObject){
        mBody = jsonObject;
        mStoreInfo.setTotalItemCount(mBody.optInt("totalItemCount"));
        mDataResponse = mBody.optJSONArray("response");
        if(mDataResponse!= null && mDataResponse.length() > 0){
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                String store_id = jsonDataObject.optString("store_id");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                String owner_image = jsonDataObject.optString("owner_image");
                int like_count = jsonDataObject.optInt("like_count");
                int comment_count = jsonDataObject.optInt("comment_count");
                int owner_id = jsonDataObject.optInt("owner_id");
                int featured = jsonDataObject.optInt("featured");
                int sponsored = jsonDataObject.optInt("sponsored");
                int closed = jsonDataObject.optInt("closed");
                String category =  jsonDataObject.optString("category_title");
                JSONArray menuArray = jsonDataObject.optJSONArray("menu");
                mStoreItemList.add(new StoreInfoModel(store_id,title,image,category,
                        owner_image,owner_id,featured,sponsored,like_count,comment_count,closed,menuArray, jsonDataObject));
            }
            // Show End of Result Message when there are less results
            if(mStoreInfo.getTotalItemCount() <= AppConstant.LIMIT * mLoadingPageNo){
                mStoreItemList.add(ConstantVariables.FOOTER_TYPE);
            }

        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf187");
            errorMessage.setText(mContext.getResources().getString(R.string.no_store_available));
        }
        mBrowseAdapter.notifyDataSetChanged();


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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(View view, int position) {
        StoreInfoModel storeInfoModel = (StoreInfoModel) mStoreItemList.get(position);
        if(view.getTag() != null && view.getTag().equals("mainView")) {
            startActivityForResult(StoreUtil.getStoreViewPageIntent(mContext,storeInfoModel.getStoreId(), storeInfoModel.storeDetails),
                    ConstantVariables.VIEW_PAGE_CODE);
        }else {
            Intent intent = new Intent(mContext, userProfile.class);
            intent.putExtra(ConstantVariables.USER_ID,storeInfoModel.getOwnerId());
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
         /* Showing Swipe Refresh animation*/
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getStoreResult();
            }
        });
    }
}
