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

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ads.admob.AdFetcher;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumProfile extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener,AbsListView.OnScrollListener, NativeAdsManager.Listener {

    private SwipeRefreshLayout swipeRefreshLayout;
    public static int counter;
    private AppConstant mAppConst;
    private AdFetcher mAdFetcher;
    private Toolbar mToolbar;
    private View footerView;
    private ListView mListView;
    private String mTitle, forumSlug, viewUrl, viewUrl1;
    private int forumId;
    private ForumAdapter mForumAdapter;
    private List<Object> mBrowseForumList;
    private BrowseListItems mBrowseList;
    private boolean isLoading = false, isAdLoaded = false;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private int canPost;
    private JSONObject mBody;
    private Snackbar snackbar;
    private NativeAdsManager listNativeAdsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);
        mListView = (ListView)findViewById(R.id.listView_forumProfile);
        footerView = CustomViews.getFooterView(getLayoutInflater());

        mBrowseList = new BrowseListItems();
        mAppConst = new AppConstant(ForumProfile.this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTitle = extras.getString(ConstantVariables.CONTENT_TITLE);
            viewUrl = extras.getString(ConstantVariables.VIEW_PAGE_URL);

            //These variables are required to create topics
            forumId = extras.getInt("forumId");
            forumSlug = extras.getString("forumSlug");
        }

        //Adding limit to the url.
        viewUrl1 =viewUrl+ "?page="+pageNumber + "&limit=" + AppConstant.LIMIT;

        if(mTitle != null && !mTitle.isEmpty())
            setTitle(mTitle);

        CustomViews.createMarqueeTitle(this, mToolbar);

        mBrowseForumList = new ArrayList<>();
        mForumAdapter = new ForumAdapter(ForumProfile.this,
                R.layout.forum_profile_item, mBrowseForumList,"ForumProfile");
        mListView.setAdapter(mForumAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(ConstantVariables.ENABLE_FORUM_ADS == 1) {
            if(ConstantVariables.ENABLE_ADMOB == 0) {
                listNativeAdsManager = new NativeAdsManager(this,
                        this.getResources().getString(R.string.facebook_placement_id),
                        ConstantVariables.DEFAULT_AD_COUNT);
                listNativeAdsManager.setListener(this);
                listNativeAdsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
            }else {
                mAdFetcher = new AdFetcher(this);
                mAdFetcher.loadAds(mBrowseForumList,mForumAdapter,ConstantVariables.FORUM_ADS_POSITION);
            }
        }
        makeRequest();
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
    }

    public void makeRequest(){

        mLoadingPageNo = 1;
        CustomViews.hideEndOfResults(footerView);

        mAppConst.getJsonResponseFromUrl(viewUrl1,new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                mBrowseForumList.clear();
                addDataToListView(jsonObject);

                if(swipeRefreshLayout.isRefreshing()) {
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
                    snackbar = SnackbarUtils.displaySnackbarWithAction(ForumProfile.this,
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
        mListView.setOnItemClickListener(this);
    }

    /**
     *Method to add data to the list view
     *
     * @param jsonObject JsonObject by which getting the response
     */
    int j=0;
    public void addDataToListView(JSONObject jsonObject) {
        mBody = jsonObject;
        try {
            mTotalItemCount = mBody.optInt("totalItemCount");
            canPost = mBody.optInt("can_post");
            invalidateOptionsMenu();
            mBrowseList.setTopicTotalItemCount(mTotalItemCount);
            JSONArray response = mBody.optJSONArray("response");

            if (response != null && response.length() > 0) {
                findViewById(R.id.message_layout).setVisibility(View.GONE);

                for (int i = 0; i < response.length(); i++) {
                    if ((isAdLoaded || AdFetcher.isAdLoaded()) && mBrowseForumList.size() != 0
                            && mBrowseForumList.size() % ConstantVariables.FORUM_ADS_POSITION == 0) {
                        if(ConstantVariables.ENABLE_ADMOB == 0) {
                            NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                            mBrowseForumList.add(ad);
                        }else if(mAdFetcher.getAdList() != null && !mAdFetcher.getAdList().isEmpty()){
                            if(j < mAdFetcher.getAdList().size()) {
                                NativeAppInstallAd nativeAppInstallAd = (NativeAppInstallAd) mAdFetcher.getAdList().get(j);
                                j++;
                                mBrowseForumList.add(nativeAppInstallAd);
                            }else {
                                j = 0;
                            }
                        }
                    }
                    JSONObject responseJSONObject = response.optJSONObject(i);
                    int topicId = responseJSONObject.optInt("topic_id");
                    int forumTopicId = responseJSONObject.optInt("forum_id");
                    String topicTitle = responseJSONObject.optString("title");
                    String topicDescription = responseJSONObject.optString("description");
                    int totalReplies = responseJSONObject.optInt("post_count");
                    String topicModifiedDate = responseJSONObject.optString("modified_date");
                    int isSticky = responseJSONObject.optInt("sticky");
                    int isTopicClose = responseJSONObject.optInt("closed");
                    String topicSlug = responseJSONObject.optString("slug");
                    int isTopicAllowToView = responseJSONObject.optInt("allow_to_view");

                    String displayName = null, topicOwnerImage = null;
                    int topicLastPostByUserId = 0;
                    JSONObject lastPostedByJsonObject = responseJSONObject.optJSONObject("last_posted_by");
                    if (lastPostedByJsonObject != null) {
                        displayName = lastPostedByJsonObject.optString("displayname");
                        topicOwnerImage = lastPostedByJsonObject.optString("image");
                        topicLastPostByUserId = lastPostedByJsonObject.optInt("user_id");
                    }
                    mBrowseForumList.add(new BrowseListItems(isTopicClose, isSticky, forumTopicId, totalReplies,
                            topicLastPostByUserId, topicId, displayName, topicOwnerImage, topicDescription,
                            topicModifiedDate, topicSlug, topicTitle, (isTopicAllowToView == 1)));
                    mForumAdapter.notifyDataSetChanged();
                }

                // Show End of Result Message when there are less results
                if(mTotalItemCount <= AppConstant.LIMIT * mLoadingPageNo){
                    CustomViews.addFooterView(mListView, footerView);
                    CustomViews.showEndOfResults(this, footerView);
                }

            } else {
                findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                TextView errorIcon = (TextView) findViewById(R.id.error_icon);
                SelectableTextView errorMessage = (SelectableTextView) findViewById(R.id.error_message);
                errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(ForumProfile.this));
                errorIcon.setText("\uf086");
                errorMessage.setText(getResources().getString(R.string.no_forum_topic));
            }
        } catch (Exception e) {
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;

        if (limit == totalItemCount && !isLoading) {

            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getTopicTotalItemCount()) {

                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = viewUrl+ "?page=" + mLoadingPageNo + "&limit="+ AppConstant.LIMIT;
                isLoading = true;
                loadMoreData(url);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.title_create_topic))
                .setIcon(ContextCompat.getDrawable(ForumProfile.this, R.drawable.ic_action_new))
                .setVisible(false);

        menu.findItem(R.id.submit).getIcon().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(
                this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        // Check to determine whether user have permission to create topic or user is logged out
        //Then not showing add topic option
        if (canPost != 0 && !mAppConst.isLoggedOutUser()) {
            menu.findItem(R.id.submit).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(ForumProfile.this)) {
                SoundUtil.playSoundEffectOnBackPressed(ForumProfile.this);
            }
        } else if (id == R.id.submit) {

            String url = AppConstant.DEFAULT_URL+"forums/"+forumId+"/"+forumSlug+"/topic-create";
            Intent createEntry = new Intent(ForumProfile.this, CreateNewEntry.class);
            createEntry.putExtra(ConstantVariables.CREATE_URL, url);
            createEntry.putExtra(ConstantVariables.FORM_TYPE, "create_topic");
            createEntry.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
            startActivity(createEntry);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        // Calling Restart only when there is any modification.
        if (counter != 0) {
            finish();
            startActivity(getIntent());
            counter = 0;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BrowseListItems listItems = (BrowseListItems) mBrowseForumList.get(position);
        if(listItems != null) {
            int topicId = listItems.getTopicId();
            String topicSlug = listItems.getTopicSlug();
            String topicTitle = listItems.getTopicTitle();
            boolean topicAllowToView = listItems.isTopicAllowToView();

            if (!topicAllowToView) {
                SnackbarUtils.displaySnackbar(findViewById(R.id.forum_main_content),
                        getResources().getString(R.string.unauthenticated_view_message));
            } else {

                Intent mainIntent = new Intent(ForumProfile.this, ForumView.class);
                String viewUrl = AppConstant.DEFAULT_URL + "forums/topic/" + topicId + "/" + topicSlug;
                mainIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,
                        PreferencesUtils.getCurrentSelectedModule(ForumProfile.this));
                mainIntent.putExtra(ConstantVariables.CONTENT_TITLE, topicTitle);
                mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewUrl);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    @Override
    public void onAdsLoaded() {
        isAdLoaded = true;
        for (int i = 0 ; i <= mBrowseForumList.size(); i++) {
            if (i != 0 && i % ConstantVariables.FORUM_ADS_POSITION == 0) {
                NativeAd ad = this.listNativeAdsManager.nextNativeAd();
                mBrowseForumList.add(i, ad);
                mForumAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAdError(AdError adError) {

    }
}
