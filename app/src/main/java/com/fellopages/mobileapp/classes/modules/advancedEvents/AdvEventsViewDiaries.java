/*
 *   Copyright (c) 2015 BigStep Technologies Private Limited.
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

package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdvEventsViewDiaries extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener,
        OnOptionItemClickResponseListener {

    private String mViewDiariesUrl, mCurrentSelectedModule;
    private int mTotalItemCount = 0, mLoadingPageNo = 1;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseListItems;
    private BrowseListItems mBrowseList;
    private JSONObject mBody;
    private JSONArray mDataResponse = null;
    private List<Object> mBrowseItemList;
    private AdvEventsBrowseDataAdapter mBrowseDataAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridViewWithHeaderAndFooter mGridView;
    private boolean isLoading = false, isLoadingFromCreate = false;
    private Snackbar snackbar;
    private int diaryId, eventCount;
    private String diaryName, contentUrl;
    private Toolbar mToolbar;
    private TextView mDiaryDescription;
    JSONArray mGutterMenus;
    RelativeLayout mainContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view_layout);

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        mGridView = findViewById(R.id.gridView);
        mGridView.setOnScrollListener(this);
        CustomViews.initializeGridLayout(this, AppConstant.getNumOfColumns(this), mGridView);

        mDiaryDescription = new TextView(this);
        mDiaryDescription.setLayoutParams(CustomViews.getFullWidthLayoutParams());
        mDiaryDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.body_default_font_size));

        int padding = (int) getResources().getDimension(R.dimen.padding_10dp);
        mDiaryDescription.setTextColor(ContextCompat.getColor(this, R.color.body_text_2));
        mDiaryDescription.setPadding(padding, padding, padding, padding);


        if (mDiaryDescription != null) {
            mGridView.addHeaderView(mDiaryDescription);
        }

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        findViewById(R.id.category_filter_block).setVisibility(View.VISIBLE);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        // Add swipeRefreshLayout layout below category filter

        RelativeLayout.LayoutParams layoutParams = CustomViews.getFullWidthRelativeLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.category_filter_block);
        swipeRefreshLayout.setLayoutParams(layoutParams);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(this);

        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_siteevent")){
            PreferencesUtils.updateCurrentModule(this,"core_main_siteevent");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(this);
        }

        PreferencesUtils.updateCurrentList(this,"browse_siteevent");

        mBrowseDataAdapter = new AdvEventsBrowseDataAdapter(this, R.layout.list_advanced_event_info,
                mBrowseItemList,null);
        mGridView.setAdapter(mBrowseDataAdapter);

        diaryId = getIntent().getExtras().getInt(ConstantVariables.VIEW_PAGE_ID);
        if (diaryId != 0) {
            mViewDiariesUrl = AppConstant.DEFAULT_URL +"advancedevents/diary/"+
                    diaryId +"?limit=" + AppConstant.LIMIT;
        }
        eventCount = getIntent().getExtras().getInt(ConstantVariables.TOTAL_ITEM_COUNT);
        diaryName = getIntent().getExtras().getString(ConstantVariables.CONTENT_TITLE);
        getSupportActionBar().setTitle(diaryName + ": " + String.valueOf(eventCount));

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));
        if (mBody != null && mBody.length() > 0) {
            isLoadingFromCreate = true;
            isLoading = false;
            addDataToList(mBody);
        }

        mainContent = findViewById(R.id.main_content);

        sendRequestToServer();

    }

    public void sendRequestToServer() {

        if (!isLoadingFromCreate) {

            mViewDiariesUrl = AppConstant.DEFAULT_URL + "advancedevents/diary/" +
                    diaryId + "?limit=" + AppConstant.LIMIT;

            mAppConst.getJsonResponseFromUrl(mViewDiariesUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBrowseItemList.clear();
                    findViewById(R.id.progressBar).setVisibility(View.GONE);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (snackbar != null && snackbar.isShown())
                        snackbar.dismiss();
                    addDataToList(jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (isRetryOption) {
                        snackbar = SnackbarUtils.displaySnackbarWithAction(AdvEventsViewDiaries.this,
                                mainContent, message, new SnackbarUtils.OnSnackbarActionClickListener() {
                                    @Override
                                    public void onSnackbarActionClick() {
                                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                        sendRequestToServer();
                                    }
                                });
                    } else {
                        SnackbarUtils.displaySnackbarShortWithListener(mainContent, message,
                                new SnackbarUtils.OnSnackbarDismissListener() {
                                    @Override
                                    public void onSnackbarDismissed() {
                                        finish();
                                    }
                                });
                    }

                }
            });
        }

        mGridView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

        boolean isAllowedToView = listItems.isAllowToView();

        // If View is not allowed, show Snackbar
        if (!isAllowedToView) {
            SnackbarUtils.displaySnackbar(mainContent,
                    getResources().getString(R.string.unauthenticated_view_message));
        } else {

            Intent mainIntent = GlobalFunctions.getIntentForModule(getApplicationContext(), listItems.getmListItemId(),
                    PreferencesUtils.getCurrentSelectedModule(getApplicationContext()), null);
            startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    private void loadMoreData(String mViewDiariesUrl) {
        mAppConst.getJsonResponseFromUrl(mViewDiariesUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                addDataToList(jsonObject);
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mainContent, message);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.DIARY_MENU_TITLE,
                    mBrowseListItems);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(AdvEventsViewDiaries.this)) {
                SoundUtil.playSoundEffectOnBackPressed(AdvEventsViewDiaries.this);
            }
        } else {

            if(mGutterMenus != null) {

                mGutterMenuUtils.onMenuOptionItemSelected(mainContent, findViewById(item.getItemId()),
                        id, mGutterMenus, "diaries_siteevent");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;

        String location = null;
        if (mBody != null) {
            try {
                JSONObject resObj = mBody.getJSONObject("response");
                mGutterMenus = mBody.getJSONArray("gutterMenus");
                mTotalItemCount = resObj.getInt("total_events");
                String body = resObj.optString("body");

                String diaryTitle = resObj.optString("title");
                getSupportActionBar().setTitle(diaryTitle + ": " + String.valueOf(mTotalItemCount));
                if (body != null) {
                    mDiaryDescription.setVisibility(View.VISIBLE);
                    mDiaryDescription.setText(Html.fromHtml(body).toString().trim());
                } else {
                    mDiaryDescription.setVisibility(View.GONE);
                }

                mDataResponse = resObj.optJSONArray("event");
                contentUrl = resObj.optString("content_url");
                mBrowseListItems = new BrowseListItems(resObj.optInt("diary_id"), diaryTitle,
                        resObj.optString("image"), contentUrl);

                if (!mAppConst.isLoggedOutUser())
                    (this).invalidateOptionsMenu();

                if (mTotalItemCount != 0){
                    findViewById(R.id.message_layout).setVisibility(View.GONE);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (mDataResponse == null) {
                        mDataResponse = mBody.optJSONArray("response");
                    }

                    for (int i = 0; i < mDataResponse.length(); i++) {
                        JSONObject jsonDataObject = mDataResponse.getJSONObject(i);
                        int event_id = jsonDataObject.optInt("event_id");
                        String title = jsonDataObject.optString("title");
                        JSONObject locationObj = jsonDataObject.optJSONObject("location");
                        if (locationObj != null) {
                            location = locationObj.optString("location");
                        }
                        String starttime = jsonDataObject.optString("starttime");
                        String image_icon = jsonDataObject.optString("image");

                        JSONObject hostObject = jsonDataObject.getJSONObject("hosted_by");
                        String hostTitle = hostObject.getString("host_title");
                        int hostId = hostObject.getInt("host_id");
                        String hostType = hostObject.getString("host_type");
                        String hostImage = hostObject.getString("image");

                        int member_count = jsonDataObject.optInt("member_count");
                        int allow_to_view = jsonDataObject.optInt("allow_to_view");
                        int hasMultipleDates = jsonDataObject.optInt("hasMultipleDates");
                        if (allow_to_view == 1)
                            mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
                                    hostTitle, member_count, event_id, true, location,
                                    starttime, hostId, hostType, hasMultipleDates,0,0));
                        else
                            mBrowseItemList.add(new BrowseListItems(image_icon, hostImage, title,
                                    hostTitle, member_count, event_id, true, location,
                                    starttime, hostId, hostType, hasMultipleDates,0,0));


                    }
                }else{
                    mGridView.setVisibility(View.INVISIBLE);
                    findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    TextView errorIcon = findViewById(R.id.error_icon);
                    TextView errorMessage = findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
                    errorIcon.setText("\uf073");
                    errorMessage.setText(getResources().getString(R.string.no_events));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mGridView.setVisibility(View.INVISIBLE);
            }
            mBrowseDataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            PreferencesUtils.updateCurrentList(this, "browse_diaries_siteevent");
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        sendRequestToServer();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                mLoadingPageNo = mLoadingPageNo + 1;
                mViewDiariesUrl = mViewDiariesUrl + "&page=" + mLoadingPageNo;
                isLoading = true;
                loadMoreData(mViewDiariesUrl);
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ConstantVariables.VIEW_PAGE_EDIT_CODE && resultCode ==
                ConstantVariables.PAGE_EDIT_CODE){
            isLoadingFromCreate = false;
            sendRequestToServer();
        }
    }

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mainContent, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        finish();
        startActivity(getIntent());
    }

}
