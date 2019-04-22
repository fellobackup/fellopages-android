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
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumView extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener, OnOptionItemClickResponseListener{

    public static Context contextOfParent;
    public static int mViewForumTopicCounter;
    public static int editForumPosition;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseListItem;
    private Toolbar mToolbar;
    private View footerView;
    private ListView mListView;
    private TextView textViewTopicCloseInfo;
    private String mTitle, viewUrl, viewUrl1;
    private ForumAdapter mForumAdapter;
    private List<Object> mBrowseForumList;
    private BrowseListItems mBrowseList;
    private ProgressBar progressBar;
    private Snackbar snackbar;
    private boolean isLoading = false;
    private int pageNumber = 1, postTotalItemCount = 0 , mLoadingPageNo = 1;
    private int watch = 0, close = 0, sticky = 0;
    private JSONObject mBody;
    private JSONArray mGutterMenus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);
        textViewTopicCloseInfo = findViewById(R.id.topic_close_info);
        mListView = findViewById(R.id.listView_forumProfile);
        progressBar = findViewById(R.id.progressBar);
        footerView = CustomViews.getFooterView(getLayoutInflater());

        mBrowseList = new BrowseListItems();
        mAppConst = new AppConstant(ForumView.this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        contextOfParent = ForumView.this;

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
        viewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);

        viewUrl1 =viewUrl+ "?page="+pageNumber + "&limit=" + AppConstant.LIMIT;


        if(mTitle != null)
            setTitle(mTitle);

        CustomViews.createMarqueeTitle(this, mToolbar);

        mBrowseForumList = new ArrayList<>();
        mForumAdapter = new ForumAdapter(ForumView.this,
                R.layout.view_forum_topic_item, mBrowseForumList,"ForumView");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mListView.setAdapter(mForumAdapter);
        mListView.setOnScrollListener(this);

        makeRequest();
    }

    public void makeRequest() {

        mLoadingPageNo = 1;
        mAppConst.getJsonResponseFromUrl(viewUrl1, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                progressBar.setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseForumList.clear();
                addDataToListView(jsonObject);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(ForumView.this,
                            findViewById(R.id.forum_main_content), message, new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(findViewById(R.id.forum_main_content), message);
                }
            }
        });

    }

    /**
     *Method to load more data(if exists) when user scrolling
     *
     * @param url Url to load next page data
    */
    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                CustomViews.removeFooterView(mListView, footerView);
                addDataToListView(jsonObject);
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(findViewById(R.id.forum_main_content), message);
            }
        });
    }

    /**
     *Method to add data to the list view
     *
     * @param jsonObject JsonObject by which getting the response
    */
    public void addDataToListView(JSONObject jsonObject) {
        mBody = jsonObject;

        try {
            postTotalItemCount = mBody.optInt("totalItemCount");
            mBrowseList.setPostTotalItemCount(postTotalItemCount);
            int isTopicClosed = mBody.optInt("isClosed");
            if (isTopicClosed == 1) {
                close = 0;
                textViewTopicCloseInfo.setTypeface(GlobalFunctions.getFontIconTypeFace(ForumView.this));
                textViewTopicCloseInfo.setVisibility(View.VISIBLE);
                textViewTopicCloseInfo.setText(String.format("\uF023 %s",
                        getResources().getString(R.string.topic_close_info) + "."));
            } else {
                close = 1;
                textViewTopicCloseInfo.setVisibility(View.GONE);
            }

            mGutterMenus = mBody.optJSONArray("gutterMenu");
            if (mGutterMenus != null) {
                for (int i = 0; i < mGutterMenus.length(); i++) {

                    JSONObject menuJsonObject = mGutterMenus.optJSONObject(i);
                    JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                    if (menuJsonObject.optString("name").equals("watch_topic")) {
                        watch = urlParams.optInt("watch");
                    } else if (menuJsonObject.optString("name").equals("make_sticky")) {
                        sticky = urlParams.optInt("sticky");
                    }

                }
                invalidateOptionsMenu();
            }
            mBrowseListItem = new BrowseListItems(close, sticky, watch, viewUrl, mTitle);

            JSONArray response = mBody.optJSONArray("response");

            for (int i = 0;i<response.length();i++) {

                JSONObject postJsonObject = response.optJSONObject(i);
                int postId = postJsonObject.optInt("post_id");
                String postCreationDate = postJsonObject.optString("creation_date");
                String postModifiedDate = postJsonObject.optString("modified_date");
                int postEditId = postJsonObject.optInt("edit_id");
                JSONObject postEditByJSONObject = postJsonObject.optJSONObject("editBy");
                int postEditByUserId = 0;
                String postEditByDisplayName = null;
                if (postEditByJSONObject != null) {
                    postEditByUserId = postEditByJSONObject.optInt("user_id");
                    postEditByDisplayName = postEditByJSONObject.optString("displayname");
                }
                String postBody = postJsonObject.optString("body");
                JSONArray forumMenuArray = postJsonObject.optJSONArray("menu");

                JSONObject userJsonObject = postJsonObject.optJSONObject("posted_by");
                String postByName = userJsonObject.optString("displayname");
                int postedByUserId = userJsonObject.optInt("user_id");
                String postByImage = userJsonObject.optString("image");
                int ownerPostCount = userJsonObject.optInt("post_count");

                if (postEditByJSONObject != null) {
                    mBrowseForumList.add(new BrowseListItems(postByImage, postByName, postCreationDate, postEditId,
                            postBody, postId, ownerPostCount, forumMenuArray, postModifiedDate, postedByUserId,
                            postEditByUserId, postEditByDisplayName));

                } else {
                    mBrowseForumList.add(new BrowseListItems(postByImage, postByName, postCreationDate, postEditId,
                            postBody, postId, ownerPostCount, forumMenuArray, postModifiedDate, postedByUserId,
                            0, null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mForumAdapter.notifyDataSetChanged();
        // When user edit a post then scroll to edit item position.
        if (mViewForumTopicCounter != 0 && editForumPosition != 0) {
            mListView.smoothScrollToPosition(editForumPosition);
            editForumPosition = 0;
            mViewForumTopicCounter = 0;
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;

        if (limit == totalItemCount && !isLoading) {

            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getPostTotalItemCount()) {

                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = viewUrl+ "?page=" + mLoadingPageNo + "&limit="+ AppConstant.LIMIT;
                isLoading = true;
                loadMoreData(url);
            }

        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        // Calling Restart only when there is any modification.
        if (mViewForumTopicCounter != 0) {
            finish();
            startActivity(getIntent());
            if (editForumPosition == 0)
                mViewForumTopicCounter = 0;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(ForumView.this)) {
                SoundUtil.playSoundEffectOnBackPressed(ForumView.this);
            }
        } else {

            if (mGutterMenus != null) {

                mGutterMenuUtils.onMenuOptionItemSelected(findViewById(R.id.forum_main_content),
                        findViewById(item.getItemId()), id, mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        JSONObject menuJsonObject;
        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.FORUM_MENU_TITLE,
                    mBrowseListItem);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.forum_main_content),
                successMessage, new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        ForumProfile.counter++;
                        finish();
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseListItem = (BrowseListItems) itemList;
        switch (menuName) {
            case "open":
                onRefresh();
            case "close":
                if (mBrowseListItem.getmClosed() == 1) {
                    textViewTopicCloseInfo.setVisibility(View.GONE);
                } else {
                    //When topic is close showing info to the user
                    textViewTopicCloseInfo.setTypeface(
                            GlobalFunctions.getFontIconTypeFace(ForumView.this));
                    textViewTopicCloseInfo.setVisibility(View.VISIBLE);
                    textViewTopicCloseInfo.setText("\uf023 " + getResources().
                            getString(R.string.topic_close_info));
                }
                break;
        }
    }
}
