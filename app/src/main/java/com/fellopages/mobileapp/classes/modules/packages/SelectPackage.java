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

package com.fellopages.mobileapp.classes.modules.packages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesManageDataAdapter;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectPackage extends AppCompatActivity implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener{

    private AppConstant mAppConst;
    private Context mContext;
    private ListView mListView;
    private View footerView;
    private List<Object> mBrowseItemList;
    private AdvModulesManageDataAdapter mManageDataAdapter;
    private String mPackageListUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private int pageNumber = 1, mTotalItemCount = 0, mLoadingPageNo = 1;
    private BrowseListItems mBrowseList;
    private Toolbar mToolbar;
    private boolean isLoading = false;
    private String mCurrentSelectedOption;
    private int mListingTypeId;
    private TextView mHeaderView;
    private JSONObject currentPackage;
    private boolean isPackageUpgrade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_package);

        mContext = this;
        mAppConst = new AppConstant(mContext);

        mListView = (ListView) findViewById(R.id.list_item_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CustomViews.createMarqueeTitle(this, mToolbar);
        footerView = CustomViews.getFooterView(getLayoutInflater());

        mHeaderView = (TextView) findViewById(R.id.header_view_text);

        mCurrentSelectedOption = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        if (mCurrentSelectedOption == null || mCurrentSelectedOption.isEmpty()) {
            mCurrentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        mPackageListUrl = getIntent().getStringExtra(ConstantVariables.CREATE_URL);
        mPackageListUrl += "&page=" + pageNumber;

        mListingTypeId = getIntent().getIntExtra(ConstantVariables.LISTING_TYPE_ID, 0);

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        mManageDataAdapter = new AdvModulesManageDataAdapter(mContext, R.layout.list_row, mBrowseItemList,
                "site_package", mCurrentSelectedOption, mListingTypeId);

        mListView.setAdapter(mManageDataAdapter);

        mManageDataAdapter.setOnPackageChangeListener(new AdvModulesManageDataAdapter.OnPackageChangeListener() {
            @Override
            public void onPackageChanged() {
                onBackPressed();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        makeRequest();

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

    }

    public void makeRequest(){

        mLoadingPageNo = 1;
        String packageUrl = mPackageListUrl + "&page=" + pageNumber;

        mAppConst.getJsonResponseFromUrl(packageUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(mContext,
                            findViewById(R.id.fragment_item_view), message, new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(findViewById(R.id.fragment_item_view), message);
                }
            }
        });

    }

    private void addDataToList(JSONObject jsonObject){

        mBody = jsonObject;
        mTotalItemCount = mBody.optInt("getTotalItemCount");
        mBrowseList.setmTotalItemCount(mTotalItemCount);
        mDataResponse = mBody.optJSONArray("response");

        currentPackage = mBody.optJSONObject("currentPackage");
        if (currentPackage != null && mTotalItemCount > 0) {
            isPackageUpgrade = true;
            findViewById(R.id.header_view).setVisibility(View.VISIBLE);
            mHeaderView.setText(currentPackage.optString("title"));
        }

        if(mDataResponse != null && mDataResponse.length() > 0) {
            findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponse.length(); i++) {

                JSONObject jsonDataObject = mDataResponse.optJSONObject(i);
                JSONObject singlePackageObject = jsonDataObject.optJSONObject("package");
                int package_id = singlePackageObject.optInt("package_id");
                String title = singlePackageObject.optJSONObject("title").optString("value");
                int isPaid = singlePackageObject.optJSONObject("price").optInt("isPaid");
                JSONObject descriptionObject = singlePackageObject.optJSONObject("description");
                JSONArray packageMenuArray = jsonDataObject.optJSONArray("menu");

                mBrowseItemList.add(new BrowseListItems(package_id, singlePackageObject,
                        descriptionObject,  packageMenuArray, title, isPackageUpgrade, isPaid)) ;
            }

        } else {
            findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf143");
            errorMessage.setText(mContext.getResources().getString(R.string.no_packages));
        }
        mManageDataAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mBrowseItemList.size() > 0) {
            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
            if (listItems != null) {

                JSONArray menuArray = listItems.getMenuArray();
                JSONObject menuObject = menuArray.optJSONObject(0);
                JSONObject urlParams = menuObject.optJSONObject("urlParams");

                Intent packageViewIntent = new Intent(mContext, PackageView.class);
                packageViewIntent.putExtra("packageObject", listItems.getmPackageObject().toString());
                packageViewIntent.putExtra("packageTitle", listItems.getmBrowseListTitle());
                packageViewIntent.putExtra("isPackageUpgrade", isPackageUpgrade);
                packageViewIntent.putExtra("isPackagePaid", listItems.getmIsPaid());
                packageViewIntent.putExtra("upgrade_url", mPackageListUrl);
                packageViewIntent.putExtra("urlParams", urlParams.toString());
                packageViewIntent.putExtra("isAdvEventPayment", false);
                packageViewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,mCurrentSelectedOption);
                packageViewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, mListingTypeId);
                startActivityForResult(packageViewIntent, ConstantVariables.PACKAGE_VIEW_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != ConstantVariables.PACKAGE_VIEW_CODE || resultCode == ConstantVariables.VIEW_PAGE_CODE) {
            Intent intent = new Intent();
            setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            finish();
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
        String url = null;
        if (limit == totalItemCount && !isLoading) {

            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {

                CustomViews.addFooterView(mListView, footerView);
                mLoadingPageNo = mLoadingPageNo + 1;


                switch(mCurrentSelectedOption){
                    case "sitereview_listing":
                        url = UrlUtil.MLT_PACKAGE_LIST_URL + "&page=" + mLoadingPageNo;
                        break;
                    case "core_main_siteevent":
                        url = UrlUtil.ADV_EVENTS_PACKAGE_LIST_URL + "&page=" + mLoadingPageNo;
                        break;
                    case "core_main_sitepage":
                        url = UrlUtil.SITE_PAGE_PACKAGE_LIST_URL + "&page=" + mLoadingPageNo;
                        break;
                    case ConstantVariables.STORE_MENU_TITLE:
                        url = UrlUtil.STORE_PACKAGE_LIST_URL + "&page=" + mLoadingPageNo;
                        break;
                }

                isLoading = true;
                loadMoreData(url);
            }

        }

    }

    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(mListView, footerView);
                addDataToList(jsonObject);
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(findViewById(R.id.fragment_item_view), message);

            }
        });
        mListView.setOnItemClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
