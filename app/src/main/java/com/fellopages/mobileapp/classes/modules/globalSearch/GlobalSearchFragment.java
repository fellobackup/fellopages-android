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

package com.fellopages.mobileapp.classes.modules.globalSearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;

import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.adapters.GlobalSearchAdapter;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalSearchFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private View mRootView, footerView;
    private ListView mListView;
    private List<BrowseListItems> mBrowseItemList;
    private AppConstant mAppConst;
    private Context mContext;
    private String mSearchUrl;
    private GlobalSearchAdapter mGlobalSearchAdapter;
    private JSONObject mBody;
    private JSONArray mResultJsonArray;
    private int pageNumber = 1;
    private boolean isLoading = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mCounter = 0;
    private Map<String, String> postParams;
    private int mTotalItemCount;

    public GlobalSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView =  inflater.inflate(R.layout.list_view_layout, container, false);
        mContext = getContext();
        mListView = mRootView.findViewById(R.id.list_item_view);

        postParams = new HashMap<>();
        mBrowseItemList = new ArrayList<>();
        mAppConst = new AppConstant(mContext);
        footerView = CustomViews.getFooterView(inflater);

        mGlobalSearchAdapter = new GlobalSearchAdapter(getActivity(), R.layout.list_row, mBrowseItemList);
        mListView.setAdapter(mGlobalSearchAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        swipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mSearchUrl = AppConstant.DEFAULT_URL + "search?page=" + pageNumber + "&limit=" + AppConstant.LIMIT;

        if(getArguments() != null){
            Set<String> searchArgumentSet = getArguments().keySet();

            for (String key : searchArgumentSet) {
                String value = getArguments().getString(key);
                if(value != null && !value.isEmpty()) {
                    postParams.put(key, value);
                }
            }
        }

        postSearchRequest(mSearchUrl, false);
        return mRootView;

    }

    public void postSearchRequest(String searchUrl, final boolean isScrolling){
        Log.d("PostParams ", String.valueOf(postParams));
        mCounter = 0;
        CustomViews.hideEndOfResults(footerView);
        Log.d("GlobalSearch ", searchUrl+" "+postParams);

//        searchUrl = "\n" +
//                "https://www.fellopages.com/beta1/api/rest/search?oauth_consumer_key=tqrqueo5pxnae436nmrgeqhzs6jiud1n&oauth_secret=a01objk8tgm35mvtqrxxhqkle87z0fy3&oauth_consumer_secret=dlixjfdviokbfk48mv1x0ir2u8v7o9xj&oauth_token=xpc6cnig4a8f8a62v6wlwv00yvv7tnv1&_ANDROID_VERSION=3.1.10page=1&limit=20&query=Vict";
        mAppConst.postJsonResponseForUrl(searchUrl, postParams, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mBrowseItemList.clear();

                addDataToList(jsonObject);

          }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        SnackbarUtils.displaySnackbar(getActivity().findViewById(android.R.id.content), message);
//                    }
//                },1500);

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        BrowseListItems selectedListItem = mBrowseItemList.get(i);

        String contentUrl = selectedListItem.getmContentUrl();
        String title = selectedListItem.getmBrowseListTitle();

        String type = selectedListItem.getmModuleType().toLowerCase();
        int id = selectedListItem.getmContentId();

        Intent viewIntent;

        if(type.equals("user")){
            viewIntent = new Intent(mContext, userProfile.class);
            viewIntent.putExtra("user_id", id);
            getActivity().startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if(GlobalFunctions.isModuleEnabled(type)
                && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains(type)) {
            viewIntent = GlobalFunctions.getIntentForModule(mContext, id, type, selectedListItem.getTopicSlug());
            if(viewIntent != null){
                switch (type) {
                    case "sitereview_listing":
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, selectedListItem.getmListingTypeId());
                        break;

                    case "sitereview_video":
                        viewIntent.putExtra("isMLTVideo", true);
                        viewIntent.putExtra(ConstantVariables.LISTING_ID, selectedListItem.getmListItemId());
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, selectedListItem.getmListingTypeId());
                        break;

                    case "sitereview_review":
                        viewIntent.putExtra(ConstantVariables.LISTING_ID, selectedListItem.getmListItemId());
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, selectedListItem.getmListingTypeId());
                        break;

                    case "forum_topic":
                    case "forum_post":
                        viewIntent.putExtra(ConstantVariables.CONTENT_TITLE, selectedListItem.getmBrowseListTitle());
                        break;
                }
                startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }else{
            if(contentUrl != null && !contentUrl.isEmpty()) {
                if (ConstantVariables.WEBVIEW_ENABLE == 0) {
                    CustomTabUtil.launchCustomTab((Activity) mContext, GlobalFunctions.getWebViewUrl(contentUrl, mContext));
                } else {
                    Intent webViewActivity = new Intent(mContext, WebViewActivity.class);
                    webViewActivity.putExtra("headerText", title);
                    webViewActivity.putExtra("url", contentUrl);
                    mContext.startActivity(webViewActivity);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            }
        }


    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;


        if (limit == totalItemCount && !isLoading) {

            if (mTotalItemCount > AppConstant.LIMIT * pageNumber) {

                CustomViews.addFooterView(mListView, footerView);
                pageNumber = pageNumber + 1;

                String url = AppConstant.DEFAULT_URL + "search?page=" + pageNumber + "&limit="
                        + AppConstant.LIMIT;

                isLoading = true;
                loadMoreData(url);
            }

        }
    }


    public void addDataToList(JSONObject jsonObject) {
        if(jsonObject != null){
            mBody = jsonObject;
            mTotalItemCount = mBody.optInt("totalItemCount");
            mResultJsonArray = mBody.optJSONArray("result");
            String body;

            if(mResultJsonArray != null && mResultJsonArray.length() != 0){

                for(int i = 0; i < mResultJsonArray.length(); i++) {
                    JSONObject searchResultObject = mResultJsonArray.optJSONObject(i);
                    String type = searchResultObject.optString("type");
                    if (type.equals("Sitereview") || type.equals("sitereview") || type.equals("forum")
                            || type.equals("Forum") || type.equals("Siteevent") || type.equals("Sitepage")
                            || type.equals("Sitepagereview") || type.equals("Sitegroup") || type.equals("Sitevideo")) {
                        type = searchResultObject.optString("item_type");
                    }
                    String content_url = searchResultObject.optString("content_url");
                    String module_title = searchResultObject.optString("module_title");
                    int listingTypeId = searchResultObject.optInt("listingtype_id");
                    int listingId = searchResultObject.optInt("listing_id");
                    String title;
                    switch (type) {
                        case "User":
                            title = searchResultObject.optString("displayname");
                            break;
                        case "sitereview_review":
                            listingId = searchResultObject.optInt("resource_id");
                            title = searchResultObject.optString("listing_title");
                            break;
                        default:
                            title = searchResultObject.optString("title");
                            break;
                    }
                    body = searchResultObject.optString("body");
                    String topicSlug = searchResultObject.optString("slug");

                    if ((title != null && !title.isEmpty()) || ((body != null) && !body.isEmpty())) {
                        mCounter += 1;
                        int id = GlobalFunctions.getIdOfModule(searchResultObject, type.toLowerCase());
                        if (searchResultObject.has("body"))
                            body = searchResultObject.optString("body");
                        else if (searchResultObject.has("description"))
                            body = searchResultObject.optString("description");
                        String imageIcon = searchResultObject.optString("image_profile");
                        mBrowseItemList.add(new BrowseListItems(imageIcon, title, body, type, id,
                                content_url, module_title, listingTypeId, listingId, topicSlug));
                    }
                }

                // Show End of Result Message when there are less results
                if(mTotalItemCount <= AppConstant.LIMIT * pageNumber){
                    CustomViews.addFooterView(mListView, footerView);
                    CustomViews.showEndOfResults(mContext, footerView);
                }

            }else {
                mRootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                TextView errorIcon = mRootView.findViewById(R.id.error_icon);
                SelectableTextView errorMessage = mRootView.findViewById(R.id.error_message);
                errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                errorIcon.setText("\uf119");
                errorMessage.setText(mContext.getResources().getString(R.string.no_data_available));
            }
            mGlobalSearchAdapter.notifyDataSetChanged();
            isLoading = false;
            if(swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }



    private void loadMoreData(String url) {
        CustomViews.hideEndOfResults(footerView);

        mAppConst.postJsonResponseForUrl(url, postParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(mListView, footerView);
                addDataToList(jsonObject);

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }

        });
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                pageNumber = 1;
                mSearchUrl = AppConstant.DEFAULT_URL + "search?page=" + pageNumber + "&limit=" + AppConstant.LIMIT;
                postSearchRequest(mSearchUrl, false);
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
