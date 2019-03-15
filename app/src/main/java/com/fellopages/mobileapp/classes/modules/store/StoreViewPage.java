package com.fellopages.mobileapp.classes.modules.store;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.InviteGuest;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.activities.ReportEntry;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BadgeView;
import com.fellopages.mobileapp.classes.common.ui.SlidingTabLayout;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;
import com.fellopages.mobileapp.classes.modules.offers.BrowseOffersFragment;
import com.fellopages.mobileapp.classes.modules.packages.SelectPackage;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.store.fragments.BrowseProductFragment;
import com.fellopages.mobileapp.classes.modules.store.fragments.ReviewFragment;
import com.fellopages.mobileapp.classes.modules.store.fragments.StoreInfoFragment;
import com.fellopages.mobileapp.classes.modules.video.VideoUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreViewPage extends FormActivity implements ViewPager.OnPageChangeListener, View.OnClickListener,
        AppBarLayout.OnOffsetChangedListener,OnOptionItemClickResponseListener, OnUploadResponseListener {
    private AppConstant mAppConst;
    private SocialShareUtil mSocialShareUtil;
    private ViewPager mPager;
    private FragmentAdapter mPagerAdapter;
    private ImageView mStoreCover,mProfileImage;
    private BadgeView mCartCountBadge;
    private TextView mStoreTitleView, mOwnerTitle, mLikeCountView,mProfileImageMenus,mCoverImageMenus;
    private JSONObject mStoreDetails,mInfoTabObject,mProfileInfoObject;
    private JSONArray mProfileTabsArray,mGutterMenusArray;
    private SlidingTabLayout slidingTabLayout;
    private ProgressBar mProgressBar;
    private String mMenuFieldName,mMenuFieldValue, mStoreUrl,mOverViewData,mStoreTitle;
    private Map<String, String> postParams;
    private ArrayList<Object> mCoverImageDetails,mProfileImageDetails;
    private int isLiked,isStoreClosed,userId,storeId,mLikeCount;
    boolean isProductTab = false;
    private String mStoreViewPageUrl, redirectUrl, successMessage, profileImageUrl, coverImageUrl;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView mToolBarTitle;
    private Toolbar mToolbar;
    FloatingActionButton mFilterButton;
    private int siteVideoPluginEnabled, mAdvVideosCount;
    private JSONArray coverPhotoMenuArray, profilePhotoMenuArray;
    private int defaultCover, cover_photo = 0, profile_photo = 0;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private boolean isCoverRequest;
    private CoordinatorLayout mMainContent;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_view_page);
        mToolbar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mAppConst = new AppConstant(this);
        mSocialShareUtil = new SocialShareUtil(this);
        postParams = new HashMap<>();
        mCoverImageDetails = new ArrayList<>();
        mProfileImageDetails = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        mFilterButton = findViewById(R.id.filter_fab);
        mProgressBar = findViewById(R.id.progressBar);
        mOwnerTitle = findViewById(R.id.owner_title);
        mStoreCover = findViewById(R.id.cover_image);
        mProfileImage = findViewById(R.id.profile_image);
        mStoreTitleView = findViewById(R.id.store_title);
        mLikeCountView = findViewById(R.id.like_count);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mPager = findViewById(R.id.pager);

        //Header view
        mMainContent = findViewById(R.id.main_content);
        mProfileImageMenus = findViewById(R.id.profile_image_menus);
        mProfileImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mCoverImageMenus = findViewById(R.id.cover_image_menus);
        mCoverImageMenus.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        mFilterButton.setVisibility(View.GONE);
        mPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        slidingTabLayout = findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.textColorPrimary));

        // When the page is selected, change the page details
        slidingTabLayout.setOnPageChangeListener(this);
        ((AppBarLayout) findViewById(R.id.appbar)).addOnOffsetChangedListener(this);

        mStoreViewPageUrl = UrlUtil.STORE_VIEW_URL + getIntent().getStringExtra("store_id")+ "?cart=1";
        if (getIntent().hasExtra("store_details")){
            try {
                mStoreDetails = new JSONObject(getIntent().getStringExtra("store_details"));
                setStoreDetails();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getStoreDetails();

    }

    public void getStoreDetails(){
        mAppConst.getJsonResponseFromUrl(mStoreViewPageUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mStoreDetails = jsonObject;
                checkSiteVideoPluginEnabled();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                SnackbarUtils.displaySnackbar(slidingTabLayout, message);
            }
        });
    }

    /**
     *  This calling will return sitevideoPluginEnabled to 1 if
     *  1. Adv Video plugin is integrated with Directory/Pages plugin
     *  2. And if there is any video uploaded in this page using Avd video
     *  else it will return sitevideoPluginEnabled to 0
     */
    public void checkSiteVideoPluginEnabled(){

        String url = UrlUtil.IS_SITEVIDEO_ENABLED + "?subject_id=" + getIntent().getStringExtra("store_id") +
                "&subject_type=sitestore_store";
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mProgressBar.setVisibility(View.GONE);
                siteVideoPluginEnabled = jsonObject.optInt("sitevideoPluginEnabled");
                mAdvVideosCount = jsonObject.optInt("totalItemCount");
                setStoreDetails();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                setStoreDetails();
            }
        });

    }

    private void setStoreDetails(){
        userId = mStoreDetails.optInt("owner_id");
        storeId = mStoreDetails.optInt("store_id");
        mStoreTitle = mStoreDetails.optString("title");
        mStoreTitleView.setText(mStoreTitle);
        mToolBarTitle.setText(mStoreTitle);
        mOwnerTitle.setText(getResources().getString(R.string.by_text)+ " "+mStoreDetails.optString("owner_title"));
        mGutterMenusArray = mStoreDetails.optJSONArray("gutterMenu");
        mStoreUrl = mStoreDetails.optString("content_url");
        isStoreClosed = mStoreDetails.optInt("closed");
        profileImageUrl = mStoreDetails.optString("image");
        coverImageUrl = mStoreDetails.optString("cover_image");
        invalidateOptionsMenu();


        // If default_cover image is coming then showing user cover image plugin views.
        if (mStoreDetails.has("default_cover")) {
            mProfileImage.setVisibility(View.VISIBLE);
            defaultCover = mStoreDetails.optInt("default_cover");

            //Showing profile image.
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                mProfileImage.setVisibility(View.VISIBLE);
                mImageLoader.setImageForUserProfile(profileImageUrl, mProfileImage);
            }

            //Showing Cover image.
            if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                cover_photo = defaultCover == 1 ? 0 : 1;
                mImageLoader.setCoverImageUrl(coverImageUrl, mStoreCover);
            }
            profile_photo = mStoreDetails.has("photo_id") ? 1 : 0;
            getCoverMenuRequest();

            mProfileImageDetails.clear();
            mCoverImageDetails.clear();

            mProfileImageDetails.add(new PhotoListDetails(profileImageUrl));
            mCoverImageDetails.add(new PhotoListDetails(coverImageUrl));

        } else {
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                mImageLoader.setImageForUserProfile(profileImageUrl, mProfileImage);
            }
            mProfileImageDetails.add(new PhotoListDetails(profileImageUrl));
        }

        mBrowseList = new BrowseListItems(storeId, mStoreTitle, isStoreClosed, mStoreDetails.optString("cover_image"), mStoreUrl);
        mStoreCover.setOnClickListener(this);
        mProfileImage.setOnClickListener(this);
        mInfoTabObject = mStoreDetails.optJSONObject("basic_information");
        mProfileInfoObject = mStoreDetails.optJSONObject("profile_information");
        mOverViewData = mStoreDetails.optString("overview");
        mProfileTabsArray =  mStoreDetails.optJSONArray("profileTabs");
        
        mPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        if (mProfileTabsArray != null){
            for(int i = 0; i < mProfileTabsArray.length(); i++){
                JSONObject tabsObject =  mProfileTabsArray.optJSONObject(i);
                String tabName =  tabsObject.optString("name");
                Fragment fragment = getTabFragment(tabName,tabsObject.optString("url"),tabsObject.optJSONObject("urlParams"));
                if(fragment != null){
                    LogUtils.LOGD("StoreView","Tab Object - "+tabsObject.toString());
                    if(tabsObject.has("count")) {
                        mPagerAdapter.addFragment(fragment, tabsObject.optString("label")
                                + " ("+tabsObject.optInt("count")+") ");
                    }else {
                        mPagerAdapter.addFragment(fragment, tabsObject.optString("label"));
                    }
                }
                mPagerAdapter.notifyDataSetChanged();
                slidingTabLayout.setViewPager(mPager);
            }
        }
        mPager.setOffscreenPageLimit(mPagerAdapter.getCount() + 1);
        isLiked =  mStoreDetails.optInt("is_liked");
        mLikeCount = mStoreDetails.optInt("like_count");
        mLikeCountView.setVisibility(View.VISIBLE);
        mLikeCountView.setText(String.valueOf(mLikeCount));
        if(!mAppConst.isLoggedOutUser()){
            mLikeCountView.setOnClickListener(this);
            if(isLiked == 1){
                mLikeCountView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_thumb_up_blue_18dp,0);
                setTextViewDrawableColor(mLikeCountView, getResources().getColor(R.color.colorAccent));
            }
        }
    }

    private Fragment getTabFragment(String tabName, String tabUrl, JSONObject urlParams){
        Bundle bundle = new Bundle();
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE,ConstantVariables.STORE_MENU_TITLE);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID,storeId);
        Fragment fragment = null;
        switch (tabName){
            case "products":
                bundle.putInt("store_id",storeId);
                fragment = BrowseProductFragment.newInstance(bundle);
                //TODO
//                mFilterButton.setVisibility(View.VISIBLE);
                isProductTab = true;
                break;
            case "update":
                // TODO - Will release in store plugin second version
                break;
            case "information":
                fragment = new StoreInfoFragment();
                bundle.putString(ConstantVariables.RESPONSE_OBJECT,mInfoTabObject.toString());
                if(mProfileInfoObject != null && mProfileInfoObject.length() > 0) {
                    bundle.putString("profileInfo",mProfileInfoObject.toString());
                }
                bundle.putString(ConstantVariables.OVERVIEW,mOverViewData);
                break;
            case "photos":
                fragment = AlbumUtil.getBrowsePageInstance();
                break;
            case "reviews":
                fragment = new ReviewFragment();
                fragment.setArguments(bundle);
                break;
            case "coupons":
                fragment = new BrowseOffersFragment();
                fragment.setArguments(bundle);
                break;
            case "video":
            case "videos":
                if (siteVideoPluginEnabled == 0) {
                    String viewPageUrl = AppConstant.DEFAULT_URL
                            + "videogeneral/view?subject_type=sitestorevideo_video&subject_id=";
                    bundle.putString(ConstantVariables.VIEW_PAGE_URL, viewPageUrl);
                    bundle.putString(ConstantVariables.VIDEO_SUBJECT_TYPE, ConstantVariables.SITE_STORE_VIDEO_MENU_TITLE);
                }
                tabUrl += "?subject_type="+urlParams.optString("subject_type")
                        +"&subject_id="+ urlParams.optInt("subject_id");
                fragment = VideoUtil.getBrowsePageInstance();
                bundle.putInt(ConstantVariables.ADV_VIDEO_INTEGRATED, siteVideoPluginEnabled);
                bundle.putBoolean("isProfilePageRequest", true);
                break;

        }
        if(fragment != null){
            bundle.putString(ConstantVariables.URL_STRING, AppConstant.DEFAULT_URL + tabUrl);
            fragment.setArguments(bundle);
        }
        return fragment;
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if(verticalOffset == -collapsingToolbarLayout.getHeight() + mToolbar.getHeight()){
            mToolBarTitle.setVisibility(View.VISIBLE);
        }else{
            mToolBarTitle.setVisibility(View.GONE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        if(mGutterMenusArray != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenusArray, ConstantVariables.STORE_MENU_TITLE, mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        JSONObject menuJsonObject, urlParams;
        String menuName;
        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(this)) {
                SoundUtil.playSoundEffectOnBackPressed(this);
            }
        } else if(mGutterMenusArray != null) {
            mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()),
                    id, mGutterMenusArray);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //TODO
//        if(position == 0 && isProductTab){
//            mFilterButton.show();
//        }else {
//            mFilterButton.hide();
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.like_count:
                likeButtonAction();
                break;
            case R.id.profile_image:
                if (profilePhotoMenuArray != null && profilePhotoMenuArray.length() > 0) {
                    isCoverRequest = false;
                    mGutterMenuUtils.showPopup(mProfileImageMenus, profilePhotoMenuArray,
                            mBrowseList, ConstantVariables.STORE_MENU_TITLE);
                } else {
                    openLightBox(false);
                }
                break;
            case R.id.cover_image:
                if (PreferencesUtils.getSiteContentCoverPhotoEnabled(mContext) == 1 &&
                        coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                    isCoverRequest = true;
                    mGutterMenuUtils.showPopup(mCoverImageMenus, coverPhotoMenuArray,
                            mBrowseList, ConstantVariables.STORE_MENU_TITLE);
                } else {
                    openLightBox(true);
                }
                break;
        }

    }
    String mLikeUnlikeUrl;
    public void likeButtonAction(){

        mAppConst.showProgressDialog();
        Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, "sitestore_store");
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(storeId));

        mLikeUnlikeUrl = AppConstant.DEFAULT_URL + (isLiked == 0 ? "like":"unlike");

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mAppConst.hideProgressDialog();
                if(isLiked == 0) {
                    mLikeCount+=1;
                    isLiked = 1;
                    mLikeCountView.setText(String.valueOf(mLikeCount));
                    mLikeCountView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_thumb_up_blue_18dp,0);
                    setTextViewDrawableColor(mLikeCountView, getResources().getColor(R.color.colorAccent));
                }else {
                    mLikeCount-=1;
                    isLiked = 0;
                    mLikeCountView.setText(String.valueOf(mLikeCount));
                    mLikeCountView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_thumb_up_white_18dp,0);
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(slidingTabLayout, message);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Updating the cart count
        if(mCartCountBadge != null) {
            if (!PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("0") &&
                    !PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("")
                    && !PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("null")) {
                mCartCountBadge.setVisibility(View.VISIBLE);
                mCartCountBadge.setText(PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT));
            } else{
                mCartCountBadge.setVisibility(View.GONE);
            }
        }
        switch (requestCode){
            case ConstantVariables.EDIT_ENTRY_RETURN_CODE:
            case ConstantVariables.UPGRADE_PACKAGE_CODE:
                getStoreDetails();
                break;
            case ConstantVariables.PAGE_EDIT_CODE:
            case ConstantVariables.CREATE_REQUEST_CODE:

                if (requestCode == ConstantVariables.PAGE_EDIT_CODE
                        && resultCode == ConstantVariables.PAGE_EDIT_CODE && data != null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(ConstantVariables.PHOTO_LIST);
                    String  postUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL + "subject_type=sitestore_store" +
                            "&subject_id=" + storeId;
                    if (isCoverRequest) {
                        successMessage = mContext.getResources().getString(R.string.cover_photo_updated);
                    } else {
                        postUrl = postUrl + "&special=profile";
                        successMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                    }
                    new UploadFileToServerUtils(mContext, postUrl, resultList, this).execute();
                }
                break;
        }

    }
    private void setCoverMenu(JSONObject mResponseObject) {

        if (mResponseObject != null) {

            coverPhotoMenuArray = mResponseObject.optJSONArray("coverPhotoMenu");
            profilePhotoMenuArray = mResponseObject.optJSONArray("profilePhotoMenu");

            if (coverPhotoMenuArray != null && coverPhotoMenuArray.length() > 0) {
                mCoverImageMenus.setVisibility(View.VISIBLE);
                mCoverImageMenus.setText("\uf030");
            }

            if (profilePhotoMenuArray != null && profilePhotoMenuArray.length() > 0) {
                mProfileImageMenus.setVisibility(View.VISIBLE);
                mProfileImageMenus.setText("\uf030");
            }
        }

    }
    private void getCoverMenuRequest() {
        String menuUrl = UrlUtil.GET_COVER_MENU_URL +"subject_id=" + storeId +
                "&subject_type=sitestore_store&special=both&cover_photo=" + cover_photo + "&profile_photo=" + profile_photo;

        mAppConst.getJsonResponseFromUrl(menuUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                JSONObject mResponseObject = jsonObject.optJSONObject("response");

                setCoverMenu(mResponseObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });

    }
    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;
        switch (menuName) {
            case "upload_cover_photo":
            case "upload_photo":
                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    startImageUploading();
                }
                break;

            case "choose_from_album":
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "album");
                bundle.putString(ConstantVariables.CONTENT_TITLE, mBrowseList.getmBrowseListTitle());
                bundle.putBoolean(ConstantVariables.IS_WAITING, false);
                bundle.putBoolean("isCoverRequest", isCoverRequest);
                bundle.putBoolean("isSiteStoreAlbums", true);
                bundle.putString(ConstantVariables.URL_STRING, AppConstant.DEFAULT_URL + "sitestore/photos/browse-album/" + storeId);
                bundle.putString(ConstantVariables.SUBJECT_TYPE, ConstantVariables.SITE_STORE_MENU_TITLE);
                bundle.putInt(ConstantVariables.SUBJECT_ID, storeId);
                bundle.putInt(ConstantVariables.VIEW_PAGE_ID, storeId);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, ConstantVariables.PAGE_EDIT_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "view_profile_photo":
            case "view_cover_photo":
                openLightBox(isCoverRequest);
                break;

            case "remove_cover_photo":
            case "remove_photo":
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.bringToFront();
                getStoreDetails();
                break;
        }
    }
    @Override
    public void onItemDelete(String successMessage) {
        // Show Message
        SnackbarUtils.displaySnackbarShortWithListener(mMainContent, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        Intent intent = new Intent();
                        setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
                        finish();
                    }
                });
    }
    /**
     * Method to open photolightbox when user click on view image
     * @param isCoverRequest parameter to decide whether it is user profile or user cover.
     */
    public void openLightBox(boolean isCoverRequest) {

        Bundle bundle = new Bundle();
        if (isCoverRequest) {
            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverImageDetails);
        } else {
            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mProfileImageDetails);
        }
        Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
        i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
        i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
        i.putExtras(bundle);
        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);

    }
    private void startImageUploading() {
        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        intent.putExtra("selection_mode", true);
        intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
        startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
    }
    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        if (isRequestSuccessful) {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, successMessage);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.bringToFront();
            getStoreDetails();

        } else {
            SnackbarUtils.displaySnackbarLongTime(mMainContent, jsonObject.optString("message"));
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }
}
