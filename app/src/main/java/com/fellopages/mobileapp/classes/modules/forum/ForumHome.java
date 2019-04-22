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

package com.fellopages.mobileapp.classes.modules.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ForumHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{

    private View rootView;
    private AppConstant mAppConst;
    private Context mContext;
    private ListView mListView;
    private List<Object> mBrowseForumList;
    private String mForumListUrl;
    private JSONArray mBody;
    private ForumAdapter mBrowseDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;

    public ForumHome() {
        // Required empty public constructor
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

        mAppConst = new AppConstant(getActivity());
        mContext = getContext();
        mBrowseForumList = new ArrayList<>();

        rootView = inflater.inflate(R.layout.list_view_layout, container,false);
        mListView = rootView.findViewById(R.id.list_item_view);
        mForumListUrl = AppConstant.DEFAULT_URL+"forums";

        if( PreferencesUtils.getCurrentSelectedModule(mContext) != null &&
                !PreferencesUtils.getCurrentSelectedModule(mContext).equals("core_main_forum")){
            PreferencesUtils.updateCurrentModule(mContext,"core_main_forum");
        }

        mBrowseDataAdapter = new ForumAdapter(getActivity(),
                R.layout.forum_home_item, mBrowseForumList,"ForumHome");
        mListView.setAdapter(mBrowseDataAdapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        setHasOptionsMenu(true);

        makeRequest();
        mListView.setOnItemClickListener(this);
        return rootView;
    }

    public void makeRequest() {

        mBrowseForumList.clear();
        String tempData = DataStorage.getResponseFromLocalStorage(mContext, DataStorage.FORUM_FILE);

        try {
            if (tempData != null) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject(tempData);
                addDataInList(jsonObject);
                mBrowseDataAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAppConst.getJsonResponseFromUrl(mForumListUrl, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseForumList.clear();
                addDataInList(jsonObject);
                mBrowseDataAdapter.notifyDataSetChanged();
                DataStorage.createTempFile(mContext, DataStorage.FORUM_FILE, jsonObject.toString());
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

    public void addDataInList(JSONObject jsonObject){

        try {
            mBody = jsonObject.optJSONArray("response");
            if (mBody != null && mBody.length() > 0) {
                rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);

                for (int i = 0; i < mBody.length(); i++) {

                    JSONObject categoryJsonObject = mBody.optJSONObject(i);
                    int categoryId = categoryJsonObject.optInt("category_id");
                    String categoryTitle = categoryJsonObject.optString("title");
                    int forumId = categoryJsonObject.optInt("forum_id");
                    mBrowseForumList.add(new BrowseListItems(categoryId, categoryTitle, forumId));

                    JSONArray innerJsonArray = new JSONArray(mBody.optJSONObject(i).optString("forums"));
                    for (int j = 0; j < innerJsonArray.length(); j++) {
                        JSONObject innerJsonObject = innerJsonArray.optJSONObject(j);
                        forumId = innerJsonObject.optInt("forum_id");
                        String forumTitle = innerJsonObject.optString("title");
                        int topicCount = innerJsonObject.optInt("topic_count");
                        int postCount = innerJsonObject.optInt("post_count");
                        String slug = innerJsonObject.optString("slug");
                        int forumAllowToView = innerJsonObject.optInt("allow_to_view");

                        mBrowseForumList.add(new BrowseListItems(forumId, topicCount, postCount,
                                forumTitle, slug, (forumAllowToView == 1)));
                    }
                }
            } else {
                rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                TextView errorIcon = rootView.findViewById(R.id.error_icon);
                SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
                errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                errorIcon.setText("\uf086");
                errorMessage.setText(getActivity().getResources().getString(R.string.no_forum));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BrowseListItems listItems = (BrowseListItems) mBrowseForumList.get(position);
        int forumId = listItems.getForumId();
        String slug = listItems.getSlug();
        String forumTitle = listItems.getForumTitle();
        boolean forumAllowToView = listItems.isForumAllowToView();
        if (forumId != 0) {
            if (!forumAllowToView) {
                SnackbarUtils.displaySnackbar(rootView,
                        getResources().getString(R.string.unauthenticated_view_message));

            } else {
                Intent mainIntent = new Intent(getActivity(), ForumProfile.class);
                String viewUrl = AppConstant.DEFAULT_URL + "forums/" + forumId + "/" + slug;
                mainIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,
                        PreferencesUtils.getCurrentSelectedModule(mContext));
                mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, forumTitle);
                mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewUrl);
                mainIntent.putExtra("forumId", forumId);
                mainIntent.putExtra("forumSlug", slug);
                getActivity().startActivity(mainIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferencesUtils.updateCurrentModule(getContext(), "home");
    }
}
