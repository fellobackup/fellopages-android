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

package com.fellopages.mobileapp.classes.common.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ViewPageFragmentAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewGroupEvent extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        OnOptionItemClickResponseListener {

    private Context mContext;
    private String mModuleName, mContentIdString, mSubjectType, mInviteGuestUrl, mContentUrl, title,
            image, mItemViewUrl;
    private Bundle bundle;
    private AppConstant mAppConst;
    private BrowseListItems mBrowseList;
    private GutterMenuUtils mGutterMenuUtils;
    private ViewPager mViewPager;
    private TabLayout mSlidingTabs;
    private JSONObject mBody, mDataResponse;
    private JSONArray mGutterMenus, mProfileTabs, mProfileRsvpForm;
    private int mCanUpload, mContentId, mProfileRsvpValue, mMembershipRequestCode = 0, mProfileTabSize;
    private boolean isLoadingFromCreate = false, isAdapterSet = false;
    private TextView mContentTitle, mToolBarTitle;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private ImageView mCoverImage;
    private CoordinatorLayout mMainContent;
    private ViewPageFragmentAdapter mViewPageFragmentAdapter;
    private ArrayList<PhotoListDetails> mCoverImageDetails;
    private boolean isContentEdited = false, isContentDeleted = false;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfileTabs = new JSONArray();
        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mContentId = getIntent().getExtras().getInt(ConstantVariables.VIEW_PAGE_ID);

        setContentView(R.layout.activity_profile_pages);

        mCoverImageDetails = new ArrayList<>();

        if (mModuleName.equals("core_main_group")) {
            mContentIdString = "group_id";
            mSubjectType = "group";
        } else if (mModuleName.equals("core_main_event")) {
            mContentIdString = "event_id";
            mSubjectType = "event";
        }

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        mContext = this;
        mImageLoader = new ImageLoader(getApplicationContext());

        // View Page Url
        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);

        mItemViewUrl += "&profile_rsvp=1";

         /* Create Back Button On Action Bar **/
        mToolbar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*
        Getting Fields to show content
         */
        mMainContent = findViewById(R.id.main_content);
        appBar = findViewById(R.id.appbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mCoverImage = findViewById(R.id.coverImage);
        mContentTitle = findViewById(R.id.content_title);

        mViewPager = findViewById(R.id.pager);
        mSlidingTabs = findViewById(R.id.slidingTabs);
        mSlidingTabs.setTabTextColors(ContextCompat.getColor(mContext, R.color.light_gray),
                ContextCompat.getColor(mContext, R.color.textColorPrimary));
        mSlidingTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        /*
        Load Data Directly if Coming from Create Page.
         */
        if (mBody != null && mBody.length() != 0) {
            isLoadingFromCreate = true;
            isContentEdited = true;
            loadViewPageData(mBody);
        }
        makeRequest();
    }

    public void makeRequest(){

        // Do not send request if coming from create page
        if(!isLoadingFromCreate){
            mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    mBody = jsonObject;
                    loadViewPageData(mBody);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(mMainContent, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    finish();
                                }
                            });
                }
            });
        }
    }

    private void loadViewPageData(JSONObject bodyJsonObject){

        findViewById(R.id.progressBar).setVisibility(View.GONE);

        if(bodyJsonObject != null && bodyJsonObject.length() != 0) {
            try {

                mDataResponse = bodyJsonObject.getJSONObject("response");
                if(mDataResponse != null){
                    mCanUpload = mDataResponse.optInt("canUpload");
                }
                mGutterMenus = bodyJsonObject.getJSONArray("gutterMenu");

                /*
                Check Invite Option exist in GutterMenus or not to add + icon on Members Page
                 */
                if (mGutterMenus != null) {
                    for (int i = 0; i < mGutterMenus.length(); i++) {
                        JSONObject menuJsonObject = mGutterMenus.getJSONObject(i);
                        String menuUrl = menuJsonObject.optString("url");

                        if (menuUrl.contains("events/member/invite/") || menuUrl.contains("groups/member/invite/"))
                            mInviteGuestUrl = AppConstant.DEFAULT_URL + menuUrl;

                        if (menuUrl.contains("groups/member/request/") ||
                                menuUrl.contains("events/member/request/")) {
                            mMembershipRequestCode = 0;
                        } else if (menuUrl.contains("groups/member/request-cancel/") ||
                                menuUrl.contains("events/member/cancel/")) {
                            mMembershipRequestCode = 1;
                        }
                    }
                    invalidateOptionsMenu();
                }

                 /*
                Fetch Data from Response
                 */

                mProfileRsvpForm = bodyJsonObject.optJSONArray("profile_rsvp_form");
                mProfileRsvpValue = bodyJsonObject.optInt("profile_rsvp_value");
                title = mDataResponse.getString("title");
                image = mDataResponse.getString("image");
                mContentUrl = mDataResponse.getString("content_url");
                int isMember = mDataResponse.optInt("isMember");
                mBrowseList = new BrowseListItems(title, image, mContentUrl, mContentId,
                        mMembershipRequestCode, isMember, mProfileRsvpValue);

                JSONArray profileTabs = bodyJsonObject.getJSONArray("profile_tabs");

                // Add Info Tab in array on 1st position
                if (profileTabs != null) {
                    JSONObject InfoTabJsonObject = new JSONObject();
                    InfoTabJsonObject.put("totalItemCount", 0);
                    InfoTabJsonObject.put("label", getResources().getString(R.string.action_bar_title_info));
                    InfoTabJsonObject.put("name", "info");
                    mProfileTabs.put(1, InfoTabJsonObject);

                    // Add All the tabs in mProfileTabs
                    for (int i = 0; i < profileTabs.length(); i++) {
                        JSONObject tabObject = profileTabs.getJSONObject(i);
                        if (i == 0) {
                            mProfileTabs.put(i, tabObject);
                        } else {
                            mProfileTabs.put(i + 1, tabObject);
                        }
                    }

                    mContentId = mDataResponse.getInt(mContentIdString);

                    bundle = new Bundle();
                    bundle.putString(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                    bundle.putInt(ConstantVariables.SUBJECT_ID, mContentId);
                    bundle.putString(ConstantVariables.MODULE_NAME, "groupEventProfile");
                    bundle.putString(ConstantVariables.RESPONSE_OBJECT, mDataResponse.toString());
                    bundle.putInt(ConstantVariables.CAN_UPLOAD, mCanUpload);
                    bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                    bundle.putInt(mContentIdString, mContentId);
                    bundle.putInt(ConstantVariables.CONTENT_ID, mContentId);
                    bundle.putString(ConstantVariables.INVITE_GUEST, mInviteGuestUrl);
                    bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, mModuleName);
                    bundle.putString(ConstantVariables.VIEW_PAGE_URL, mItemViewUrl);
                    bundle.putString(ConstantVariables.CONTENT_TITLE, title);

                    if (mModuleName.equals("core_main_event") && (mProfileRsvpForm != null
                            && mProfileRsvpForm.length() != 0)) {
                        bundle.putInt(ConstantVariables.PROFILE_RSVP_VALUE, mProfileRsvpValue);
                        bundle.putBoolean("showRsvp", true);
                    }

                    if (!isAdapterSet) {
                        mProfileTabSize = mProfileTabs.length();
                        mViewPageFragmentAdapter = new ViewPageFragmentAdapter(mContext,
                                getSupportFragmentManager(), mProfileTabs, bundle);
                        mViewPager.setAdapter(mViewPageFragmentAdapter);
                        mViewPager.setOffscreenPageLimit(mViewPageFragmentAdapter.getCount() + 1);
                        mSlidingTabs.setupWithViewPager(mViewPager);
                        isAdapterSet = true;

                    } else {

                        mViewPageFragmentAdapter.updateData(bundle, mProfileTabs,
                                (mProfileTabSize == mProfileTabs.length()), false);
                        // If any tab is added/removed then again set the adapter.
                        if (mProfileTabSize != mProfileTabs.length()) {
                            mProfileTabSize = mProfileTabs.length();
                            mViewPager.setAdapter(mViewPageFragmentAdapter);
                        }
                    }

                }

                mCoverImageDetails.clear();
                mCoverImageDetails.add(new PhotoListDetails(image));
                mImageLoader.setImageUrl(image, mCoverImage);

                mCoverImage.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverImageDetails);
                    Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
                    i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                    i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
                    i.putExtras(bundle);
                    startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);

                });

                mContentTitle.setText(title);
                collapsingToolbar.setTitle(title);
                mToolBarTitle.setText(title);
                CustomViews.setCollapsingToolBarTitle(collapsingToolbar);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        }else{
            if(mGutterMenus != null) {
                mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()),
                        id, mGutterMenus);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, mModuleName, mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        /* Reset the view pager adapter if any status is posted using AAF */
        if(requestCode == ConstantVariables.FEED_REQUEST_CODE && resultCode == ConstantVariables.FEED_REQUEST_CODE
                && mViewPager != null) {
            mViewPager.setAdapter(mViewPageFragmentAdapter);
        }

        /* Send Request Again if content is edited */
        if(resultCode == ConstantVariables.VIEW_PAGE_EDIT_CODE){
            isContentEdited = true;
            isLoadingFromCreate = false;
        }
    }

    @Override
    public void onBackPressed() {

        if (!isFinishing()) {
             // Set Result to Manage page to refresh the page if any changes made in the content.
            if(isContentEdited || isContentDeleted){
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        CustomViews.showMarqueeTitle(i, collapsingToolbar, mToolbar, mToolBarTitle, title);
    }

    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mMainContent, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        isContentDeleted = true;
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        switch (menuName) {
            case "request_member":
            case "cancel_request":
                mBrowseList = (BrowseListItems) itemList;
                break;

            case "join":
            case "leave":
                mBrowseList = (BrowseListItems) itemList;
                if (mModuleName.equals(ConstantVariables.EVENT_MENU_TITLE) &&
                        mViewPageFragmentAdapter != null) {
                    bundle.putBoolean("showRsvp", mBrowseList.getmJoined() == 1);
                    bundle.putInt(ConstantVariables.PROFILE_RSVP_VALUE, mBrowseList.getmProfileRsvpValue());
                    mViewPageFragmentAdapter.updateData(bundle, mProfileTabs, true, false);
                }
                break;

            default:
                finish();
                startActivity(getIntent());
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        appBar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop() {
        appBar.removeOnOffsetChangedListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
