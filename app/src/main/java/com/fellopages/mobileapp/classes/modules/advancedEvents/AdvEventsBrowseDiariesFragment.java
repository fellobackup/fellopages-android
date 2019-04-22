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
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
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
import java.util.Set;


public class AdvEventsBrowseDiariesFragment  extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    private String mBrowseDiariesUrl, mCurrentSelectedModule;
    private View rootView, footerView;
    private int mTotalItemCount = 0, mLoadingPageNo = 1;
    private AppConstant mAppConst;
    private BrowseListItems mBrowseList;
    private GridViewWithHeaderAndFooter mGridView;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private List<Object> mBrowseItemList;
    private AdvEventsBrowseDataAdapter mBrowseDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false, isSearchTextSubmitted = false, isMemberDiaries = false;
    private Context mContext;
    private Snackbar snackbar;
    private HashMap<String, String> searchParams = new HashMap<>();

    public static AdvEventsBrowseDiariesFragment newInstance(Bundle bundle) {
        // Required public constructor
        AdvEventsBrowseDiariesFragment fragment = new AdvEventsBrowseDiariesFragment();
        fragment.setArguments(bundle);
        return fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(mContext);

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.grid_view_layout, null);
        footerView = CustomViews.getFooterView(inflater);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mGridView = rootView.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(this);
        mGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);
        CustomViews.initializeGridLayout(mContext, AppConstant.getNumOfColumns(mContext), mGridView);
        ViewCompat.setNestedScrollingEnabled(mGridView,true);
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);

        mBrowseDiariesUrl = UrlUtil.BROWSE_DIARIES_ADV_EVENTS_URL;

        mBrowseDataAdapter = new AdvEventsBrowseDataAdapter(getActivity(),
                R.layout.list_advanced_event_info, mBrowseItemList,"browse_diaries_siteevent");
        mGridView.setAdapter(mBrowseDataAdapter);

         /*
        GET SEARCH ARGUMENTS AND ADD THESE PARAMETERS TO THE URL
         */

        if(getArguments() != null) {

            Bundle bundle = getArguments();
            isMemberDiaries = bundle.getBoolean("isMemberDiaries");
            if(isMemberDiaries){
                mBrowseDiariesUrl = bundle.getString(ConstantVariables.URL_STRING);
            }

            if(!isMemberDiaries){
                Set<String> searchArgumentSet = getArguments().keySet();

                for (String key : searchArgumentSet) {
                    String value = getArguments().getString(key, null);

                    if (value != null && !value.isEmpty()) {
                        searchParams.put(key, value);
                    }
                }

                if(searchParams != null && searchParams.size() != 0){
                    isSearchTextSubmitted = true;
                    mBrowseDiariesUrl = mAppConst.buildQueryString(mBrowseDiariesUrl, searchParams);
                }
            }
        }
        sendRequestToServer();

        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        boolean isAllowedToView = listItems.isAllowToView();
        String eventTitle = listItems.getmBrowseListTitle();

        // If View is not allowed, show Snackbar
        if(!isAllowedToView) {
            SnackbarUtils.displaySnackbar(rootView,
                    mContext.getResources().getString(R.string.unauthenticated_view_message));
        }else {
            //           PreferencesUtils.updateCurrentList(mContext,"core_main_siteevent");
            Bundle bundle = new Bundle();
            bundle.putInt(ConstantVariables.VIEW_PAGE_ID, listItems.getmListItemId());
            bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, listItems.getmEventCount());
            bundle.putString(ConstantVariables.CONTENT_TITLE, listItems.getmBrowseListTitle());
            Intent intent = new Intent(mContext, AdvEventsViewDiaries.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantVariables.VIEW_PAGE_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

    }

    public void sendRequestToServer() {

        mLoadingPageNo = 1;

        if (!isSearchTextSubmitted && !isMemberDiaries) {

            try {
                // Don't show data in case of searching.
                mBrowseItemList.clear();
                String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.DIARIES_ADV_EVENT_FILE);
                if (tempData != null) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(tempData);
                    addDataToList(jsonObject);
                    mBrowseDataAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mAppConst.getJsonResponseFromUrl(mBrowseDiariesUrl + "&page=" + mLoadingPageNo, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                addDataToList(jsonObject);

                // Don't save data in cashing in case of searching.
                if (!isSearchTextSubmitted && !isMemberDiaries) {
                    DataStorage.createTempFile(mContext, DataStorage.DIARIES_ADV_EVENT_FILE, jsonObject.toString());
                }
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
                                    sendRequestToServer();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }

            }
        });

        mGridView.setOnItemClickListener(this);
    }

    public void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;
        String image1= null, image2= null, image3= null;
        if (mBody != null) {
            try {

                mTotalItemCount = mBody.optInt("totalItemCount");

                mBrowseList.setmTotalItemCount(mTotalItemCount);
                if (mTotalItemCount != 0){
                    rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                    mDataResponse = mBody.optJSONArray("response");

                    for (int i = 0; i < mDataResponse.length(); i++) {
                        JSONObject jsonDataObject = mDataResponse.getJSONObject(i);
                        int diaryId = jsonDataObject.getInt("diary_id");
                        String title = jsonDataObject.getString("title");
                        String body = jsonDataObject.getString("body");
                        int ownerId = jsonDataObject.getInt("owner_id");
                        int eventId = jsonDataObject.getInt("event_id");
                        int viewCount = jsonDataObject.getInt("view_count");
                        int totalItem= jsonDataObject.getInt("total_item");

                        if (jsonDataObject.has("images_1")) {
                            JSONObject imageObject1 = jsonDataObject.optJSONObject("images_1");
                            image1 = imageObject1.optString("image");
                        } else {
                            JSONObject imageObject1 = jsonDataObject.optJSONObject("images_0");
                            image1 = imageObject1.optString("image");
                        }

                        if (jsonDataObject.has("images_2")) {
                            JSONObject imageObject1 = jsonDataObject.optJSONObject("images_2");
                            image2 = imageObject1.optString("image");
                        }

                        if (jsonDataObject.has("images_3")) {
                            JSONObject imageObject1 = jsonDataObject.optJSONObject("images_3");
                            image3 = imageObject1.optString("image");
                        }

                        mBrowseItemList.add(new BrowseListItems(diaryId, image1, image2, image3, title,
                                body, ownerId, eventId, viewCount, totalItem, true ));


                    }
                }else{
                    mGridView.setVisibility(View.INVISIBLE);
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = rootView.findViewById(R.id.error_icon);
                    TextView errorMessage = rootView.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    //TODO, check if its approved by QA.
                    errorIcon.setText("\uf02d");
                    errorMessage.setText(mContext.getResources().getString(R.string.no_diaries));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mGridView.setVisibility(View.INVISIBLE);
            }

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
                sendRequestToServer();
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

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
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                isLoading = true;
                loadMoreData(mBrowseDiariesUrl + "&page=" + mLoadingPageNo);
            }
        }
    }

    /**
     * Load more data on scrolling
     * @param url Url to send request on server
     */
    public void loadMoreData(String url) {

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
        if (requestCode == ConstantVariables.VIEW_PAGE_CODE ) {
            PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
            swipeRefreshLayout.setRefreshing(true);
            sendRequestToServer();
        }

    }

}
