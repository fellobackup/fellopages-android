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

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import com.fellopages.mobileapp.classes.common.adapters.SliderAdapter;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.SplitToolbar;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ViewItem extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, AppBarLayout.OnOffsetChangedListener,
        OnOptionItemClickResponseListener, OnUploadResponseListener, View.OnLongClickListener  {

    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private String mItemViewUrl, mModuleName, normalImgUrl, redirectUrl;
    private String mContentUrl;
    private JSONArray mDataResponseArray, mGutterMenus, mTagNamesArray;
    private JSONObject mDataResponse, mBody, mProfileFiledsObject, mTagObject;
    private String tags = null;
    private View mMainContent;
    private WebView mViewDescription;
    private ViewPager coverImagePager;
    private List<ImageViewList> mPhotoUrls;
    private ArrayList<PhotoListDetails> mPhotoDetails;
    private SliderAdapter sliderAdapter;
    private Activity mActivity;
    private Context mContext;
    private int mCommentCount, mLikeCount, mContent_id, mSubjectId;
    private String mSubjectType,mShareImageUrl;
    private TextView mLikeCountTextView, mCommentCountTextView;
    private String title, body, category_title, owner_image, owner_title, creation_date, convertedDate;
    private int isListingClosed = 0, mCategoryId, mUserId, isSubscribe = 0;
    private SelectableTextView mOwnerTitle, mCategoryName, mContentTitle, mCategoryView, mCreatorView,
            mDateView, mViewTitle;
    private TextView categoryIcon, mPhotoCountIcon;
    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout detailView, mOwnerDetailView, blogTitleView;
    private RelativeLayout carouselView;
    private BezelImageView mOwnerImage;
    private ImageView leftArrow, rightArrow;
    private GridLayout profileFieldLayout;
    private NestedScrollView mNestedScrollView;
    private SplitToolbar mSplitToolBar;
    private LinearLayout mLikeCommentContent, mBlogLinearLayout;
    private boolean isLike, isLoadingFromCreate = false;
    private String mLikeUnlikeUrl, mLikeCommentsUrl;
    private Typeface fontIcon;
    private ArrayList<String> mSelectPath;
    private Toolbar mToolbar;
    private AppBarLayout appBar;
    public Map<String, String> mProfileFieldMap;
    public static int position = 0;
    private int totalItemCount;
    private boolean isContentEdited = false, isContentDeleted = false;
    private TextView mToolBarTitle;
    private JSONObject mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private int mReactionsEnabled;
    private ImageView mReactionIcon;
    private List<ImageViewList> reactionsImages;
    private ActionIconThemedTextView mLikeUnlikeText;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ArrayList<JSONObject> mReactionsArray;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(this);
        mProfileFieldMap = new LinkedHashMap<>();

        /* Create Back Button On Action Bar **/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mActivity = this;
        mContext = this;

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(getApplicationContext());

        //setting up font icons
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);

        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mContent_id = getIntent().getIntExtra(ConstantVariables.VIEW_PAGE_ID, 0);

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        /* Like and Unlike Fields...*/

        mLikeCountTextView = (TextView) findViewById(R.id.likeCount);
        mCommentCountTextView = (TextView) findViewById(R.id.commentCount);
        mReactionIcon = (ImageView) findViewById(R.id.reactionIcon);
        mLikeUnlikeText = (ActionIconThemedTextView) findViewById(R.id.likeUnlikeText);
        mCommentCountTextView.setTypeface(fontIcon);
        mLikeCountTextView.setTypeface(fontIcon);
        mLikeUnlikeText.setTypeface(fontIcon);

        LinearLayout mLikeBlock = (LinearLayout) findViewById(R.id.likeBlock);
        LinearLayout mCommentBlock = (LinearLayout) findViewById(R.id.commentBlock);
        LinearLayout mLikeCommentBlock = (LinearLayout) findViewById(R.id.likeCommentBlock);

        mLikeBlock.setOnClickListener(this);
        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(mContext);
        if(mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)){
            mLikeBlock.setOnLongClickListener(this);
        }
        mCommentBlock.setOnClickListener(this);
        mLikeCommentBlock.setOnClickListener(this);

        mLikeCommentContent = (LinearLayout) findViewById(R.id.likeCommentContent);

        mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        mSplitToolBar = (SplitToolbar) findViewById(R.id.toolbarBottom);

        mBlogLinearLayout = (LinearLayout) findViewById(R.id.blog_linear_layout);

        //Getting all the views
        mMainContent = findViewById(R.id.main_content);
        mViewTitle = (SelectableTextView) findViewById(R.id.view_title);
        mViewDescription = (WebView) findViewById(R.id.view_description);
        mViewDescription.setVisibility(View.VISIBLE);
//        mViewDescription.setPadding(1,1,1,mContext.getResources().getDimensionPixelSize(R.dimen.ticket_fab_bottom_margin));
        coverImagePager = (ViewPager) findViewById(R.id.backdrop);
        mCategoryView = (SelectableTextView) findViewById(R.id.category_view);
        mCreatorView = (SelectableTextView) findViewById(R.id.creator_view);
        mPhotoCountIcon = (TextView) findViewById(R.id.image_count);
        mDateView = (SelectableTextView) findViewById(R.id.date_view);
        mCategoryView.setOnClickListener(this);
        mCreatorView.setOnClickListener(this);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mContentTitle = (SelectableTextView) findViewById(R.id.content_title);
        detailView = (LinearLayout) findViewById(R.id.detailLayout);
        carouselView = (RelativeLayout) findViewById(R.id.carouselLayout);
        blogTitleView = (LinearLayout) findViewById(R.id.blog_titleView);
        profileFieldLayout = (GridLayout) findViewById(R.id.profileFieldLayout);

        mOwnerDetailView = (LinearLayout) findViewById(R.id.ownerDetailView);
        mOwnerDetailView.setVisibility(View.GONE);
        mOwnerImage = (BezelImageView) findViewById(R.id.owner_image);
        mOwnerTitle = (SelectableTextView) findViewById(R.id.owner_title);

        mCategoryName = (SelectableTextView) findViewById(R.id.categoryTitle);
        categoryIcon = (TextView) findViewById(R.id.categoryIcon);

        mOwnerTitle.setOnClickListener(this);
        mCategoryName.setOnClickListener(this);
        mOwnerDetailView.setOnClickListener(this);


        leftArrow = (ImageView) findViewById(R.id.left_arrow);
        rightArrow = (ImageView) findViewById(R.id.right_arrow);

        categoryIcon.setTypeface(fontIcon);
        categoryIcon.setText("\uf097");


        switch (mModuleName) {

            case "classified":
            case "Classified":
            case "core_main_classified":
                mSubjectType = "classified";
                mSubjectId = mContent_id;

                appBar.getLayoutParams().height = ((int) getResources().getDimension(R.dimen.album_view_height));

                mPhotoUrls = new ArrayList<>();
                mPhotoDetails = new ArrayList<>();
                carouselView.setVisibility(View.VISIBLE);

                sliderAdapter = new SliderAdapter(this, mPhotoUrls, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // launch full screen activity
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
                        Intent i = new Intent(mActivity, PhotoLightBoxActivity.class);
                        i.putExtra(ConstantVariables.ITEM_POSITION, position);
                        i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
                        i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mModuleName);
                        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, totalItemCount);
                        i.putExtras(bundle);
                        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
                    }
                });
                coverImagePager.setAdapter(sliderAdapter);
                coverImagePager.addOnPageChangeListener(this);

                leftArrow.setOnClickListener(this);
                rightArrow.setOnClickListener(this);

                blogTitleView.setVisibility(View.GONE);
                mPhotoCountIcon.setTypeface(fontIcon);
                break;

            case "Blog":
            case "blog":
            case "core_main_blog":
                mSubjectType = "blog";
                mSubjectId = mContent_id;
                mOwnerDetailView.setVisibility(View.GONE);
                break;

            default:
                mOwnerDetailView.setVisibility(View.GONE);

        }

        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.
                OnScrollChangedListener() {

            // Keeps track of the overall vertical offset in the list
            int verticalOffset;

            // Determines the scroll UP/DOWN direction
            boolean scrollingUp;

            @Override
            public void onScrollChanged() {
                int scrollY = mNestedScrollView.getScrollY();         //Uncomment for hide and show like & comment bar when scrolling up and down
                verticalOffset += scrollY;

                scrollingUp = scrollY > 0;
                int toolbarYOffset = (int) (scrollY + mSplitToolBar.getTranslationY());

                /*if (scrollingUp) {
                    if (toolbarYOffset < mSplitToolBar.getHeight()) {
                        mSplitToolBar.setTranslationY(toolbarYOffset);
                    } else {
                        mSplitToolBar.setTranslationY(mSplitToolBar.getHeight());
                    }

                    if (verticalOffset > mSplitToolBar.getHeight()) {
                        toolbarAnimateHide();
                    } else {
                        toolbarAnimateShow(verticalOffset);
                    }

                } else {
                    if (toolbarYOffset < 0) {
                        mSplitToolBar.setTranslationY(0);
                    } else {
                        mSplitToolBar.setTranslationY(toolbarYOffset);

                        if (mSplitToolBar.getTranslationY() < mSplitToolBar.getHeight() * -0.6
                                && verticalOffset > mSplitToolBar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    }
                }*/
            }
        });

        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);

        /*
            Check if Reactions and nested comment plugin is enabled on the site
            send request to get the reactions on a particular content
            send this request only if the reactions Enabled is not saved yet in Preferences
             or if it is set to 1
         */
        if(mReactionsEnabled == 1 || mReactionsEnabled == -1){
            String getContentReactionsUrl = UrlUtil.CONTENT_REACTIONS_URL + "&subject_type=" + mSubjectType +
                    "&subject_id=" + mSubjectId;
            mAppConst.getJsonResponseFromUrl(getContentReactionsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                    mReactionsObject = jsonObject;
                    JSONObject reactionsData = mReactionsObject.optJSONObject("reactions");
                    mContentReactions = mReactionsObject.optJSONObject("feed_reactions");
                    if(reactionsData != null){
                        mReactionsEnabled = reactionsData.optInt("reactionsEnabled");
                        PreferencesUtils.updateReactionsEnabledPref(mContext, mReactionsEnabled);
                        mAllReactionObject = reactionsData.optJSONObject("reactions");
                        if(mAllReactionObject != null){
                            mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mAllReactionObject);
                        }
                    }

                    // Send Request to load View page data after fetching Reactions on the content.
                    if(mBody != null) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        loadDataInView();
                    }else {
                        makeRequest();

                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    // Send Request to load View page data after fetching Reactions on the content.
                    if(mBody != null) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        loadDataInView();
                    }else {
                        makeRequest();

                    }
                }
            });
        } else{
            if(mBody != null) {
                isLoadingFromCreate = true;
                isContentEdited = true;
                loadDataInView();
            }else {
                makeRequest();

            }
        }

    }


    private void toolbarAnimateShow(final int verticalOffset) {
        mSplitToolBar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        //toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                    }
                });
    }

    private void toolbarAnimateHide() {
        mSplitToolBar.animate()
                .translationY(mSplitToolBar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //toolbarSetElevation(0);
                    }
                });
    }


    public void makeRequest() {

        if (!isLoadingFromCreate) {
            mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    loadDataInView();

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

    public void loadDataInView() {

        mProfileFieldMap.clear();
        if (mPhotoDetails != null && mPhotoUrls != null) {
            mPhotoUrls.clear();
            mPhotoDetails.clear();
        }
        if (profileFieldLayout != null) {
            profileFieldLayout.removeAllViews();
        }

        findViewById(R.id.progressBar).setVisibility(View.GONE);

        mDataResponse = mBody.optJSONObject("response");
        mGutterMenus = mBody.optJSONArray("gutterMenu");

        if (mDataResponse == null) {
            mDataResponseArray = mBody.optJSONArray("response");
            mDataResponse = mAppConst.convertToJsonObject(mDataResponseArray);
        }

        if (mDataResponse != null) {
            switch (mModuleName) {

                case "Blog":
                case "blog":
                case "core_main_blog":
                    mSubjectType = "blog";
                    mSubjectId = mDataResponse.optInt("blog_id");
                    break;

                case "classified":
                case "Classified":
                case "core_main_classified":
                    mSubjectType = "classified";
                    mSubjectId = mDataResponse.optInt("classified_id");
                    break;
            }
            if (mGutterMenus != null) {
                for (int i = 0; i < mGutterMenus.length(); i++) {
                    JSONObject menuJsonObject = mGutterMenus.optJSONObject(i);
                    JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                    if (menuJsonObject.optString("name").equals("close")) {
                        isListingClosed = urlParams.optInt("closed");
                        isListingClosed = (isListingClosed == 0) ? 1 : 0;
                    }
                    if (menuJsonObject.optString("name").equals("subscribe")) {
                        isSubscribe = 0;
                    }
                    if (menuJsonObject.optString("name").equals("unsubscribe")) {
                        isSubscribe = 1;
                    }
                }
                invalidateOptionsMenu();
            }

            //Fetch Data from Response
            category_title = mDataResponse.optString("category_title");
            body = mDataResponse.optString("body");
            title = mDataResponse.optString("title");
            owner_image = mDataResponse.optString("owner_image");
            if (mModuleName.equals("core_main_blog") || mModuleName.equals("blog") ||
                    mModuleName.equals("Blog")) {
                mShareImageUrl = owner_image;
            }
            owner_title = mDataResponse.optString("owner_title");
            creation_date = mDataResponse.optString("creation_date");
            mCommentCount = mDataResponse.optInt("comment_count");
            mLikeCount = mDataResponse.optInt("like_count");
            isLike = mDataResponse.optBoolean("is_like");
            convertedDate = AppConstant.convertDateFormat(getResources(), creation_date);
            mCategoryId = mDataResponse.optInt("category_id");
            mUserId = mDataResponse.optInt("owner_id");
            mContentUrl = mDataResponse.optString("content_url");

            //getting related tags
            mTagObject = mDataResponse.optJSONObject("tags");

            if (mTagObject != null) {
                mTagNamesArray = mTagObject.names();
                mTagObject = mDataResponse.optJSONObject("tags");
                mTagNamesArray = mTagObject.names();
                for (int i = 0; i < mTagNamesArray.length(); i++) {
                    String key = mTagNamesArray.optString(i);
                    if (tags == null) {
                        tags = mTagObject.optString(key);
                    } else {
                        tags += "," + mTagObject.optString(key);
                    }
                }
            }

            if (!mModuleName.equals("core_main_blog") || !mModuleName.equals("blog") ||
                    !mModuleName.equals("Blog")) {

                JSONArray imageArray = mDataResponse.optJSONArray("images");
                if (imageArray != null) {
                    for (int i = 0; i < imageArray.length(); i++) {

                        JSONObject imageUrlsObj = imageArray.optJSONObject(i);

                        normalImgUrl = imageUrlsObj.optString("image");
                        if (i == 0) {
                            mShareImageUrl = normalImgUrl;
                        }
                        String image_title = imageUrlsObj.optString("title");
                        String image_desc = imageUrlsObj.optString("description");
                        int photo_id = imageUrlsObj.optInt("photo_id");
                        int likeCount = imageUrlsObj.optInt("like_count");
                        int isLiked = imageUrlsObj.optInt("is_like");
                        totalItemCount = imageArray.length();
                        boolean likeStatus = false;
                        if (isLiked == 1) {
                            likeStatus = true;
                        }
                        mPhotoDetails.add(new PhotoListDetails(title, owner_title,
                                image_title, image_desc, photo_id, normalImgUrl, likeCount,
                                likeStatus, null));

                        mPhotoUrls.add(new ImageViewList(normalImgUrl));
                        sliderAdapter.notifyDataSetChanged();

                    }
                }

                mBrowseList = new BrowseListItems(mSubjectId, title, mShareImageUrl, mContentUrl, isListingClosed,
                        isSubscribe, redirectUrl);

                mProfileFiledsObject = mDataResponse.optJSONObject("profile_fields");

                if (mProfileFiledsObject != null) {
                    Iterator iter = mProfileFiledsObject.keys();
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        String value = mProfileFiledsObject.optString(key);

                        mProfileFieldMap.put(key, value);
                    }
                    CustomViews.generateProfileFieldsView(mContext,
                            mProfileFieldMap, profileFieldLayout);
                } else {
                    profileFieldLayout.setVisibility(View.GONE);
                    findViewById(R.id.field_above_horizontal_line).setVisibility(View.GONE);
                    findViewById(R.id.field_below_horizontal_line).setVisibility(View.GONE);
                }
            }

            // setting up the values in views
            setValuesInViews();

            // Setting up Like and Comment Count
            setLikeAndCommentCount();

        }else {
            SnackbarUtils.displaySnackbar(findViewById(R.id.main_content),
                    this.getResources().getString(R.string.no_data_available));
        }

    }

    public void setValuesInViews() {

        switch (mModuleName) {
            case "Blog":
            case "blog":
            case "core_main_blog":

                mViewTitle.setText(title);
                String categoryTextFormat = getResources().getString(R.string.category_salutation_format);
                String categoryText = String.format(categoryTextFormat,
                        getResources().getString(R.string.category_salutation), category_title);

                if (mAppConst.isRtlSupported()) {
                    mCategoryView.setText(Html.fromHtml(String.format(" - %s", categoryText)));
                } else {
                    mCategoryView.setText(Html.fromHtml(String.format("%s - ", categoryText)));
                }

                String creatorTextFormat = getResources().getString(R.string.creator_salutation_format);
                String creatorText = String.format(creatorTextFormat,
                        getResources().getString(R.string.album_owner_salutation), owner_title);

                mCreatorView.setText(Html.fromHtml(creatorText));

                if (mAppConst.isRtlSupported()) {
                    mDateView.setText(String.format("%s - ", convertedDate));
                } else {
                    mDateView.setText(String.format(" - %s", convertedDate));
                }

                break;

            case "classified":
            case "Classified":
            case "core_main_classified":
                mOwnerDetailView.setVisibility(View.VISIBLE);
                detailView.setVisibility(View.VISIBLE);
                mImageLoader.setImageForUserProfile(owner_image, mOwnerImage);

                mOwnerTitle.setText(owner_title);

                mCategoryName.setText(category_title);

                mContentTitle.setText(title);
                collapsingToolbar.setTitle(title);
                mToolBarTitle.setText(title);


                if (mPhotoUrls.size() > 1 && coverImagePager != null &&
                        coverImagePager.getCurrentItem() == 0) {
                    rightArrow.setVisibility(View.VISIBLE);
                }
                mPhotoCountIcon.setText(String.format("\uF030 %d", mPhotoUrls.size()));
                break;

        }


        /* Setting Body in TextView */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mViewDescription.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mViewDescription.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        mViewDescription.loadDataWithBaseURL("file:///android_asset/",
                GlobalFunctions.getHtmlData(this, body, true), "text/html", "utf-8", null);

    }

    public void setLikeAndCommentCount() {

        if (mAppConst.isLoggedOutUser()) {
            mSplitToolBar.setVisibility(View.GONE);
        } else {
            mSplitToolBar.setVisibility(View.VISIBLE);
            mLikeCommentContent.setVisibility(View.VISIBLE);

            /*
             Set Like and Comment Count
            */

            mLikeUnlikeText.setActivated(isLike);

            if (!isLike) {
                mLikeUnlikeText.setText(getString(R.string.like_text));
                mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            } else {
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

                // Check if Reactions is enabled, show that content reaction and it's icon here.
                if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext) &&
                        mReactionsObject != null && mReactionsObject.length() != 0) {


                    myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                    if (myReaction != null && myReaction.length() != 0) {
                        mLikeUnlikeText.setText(myReaction.optString("caption"));
                        mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        String reactionImage = myReaction.optString("reaction_image_icon");
                        mImageLoader.setImageUrl(reactionImage, mReactionIcon);
                        mReactionIcon.setVisibility(View.VISIBLE);
                    } else {
                        mReactionIcon.setVisibility(View.GONE);
                        mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp),
                                null, null, null);
                        mLikeUnlikeText.setText(getString(R.string.like_text));
                    }

                } else {
                    mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    mLikeUnlikeText.setText(getString(R.string.like_text));
                }
            }
                mLikeCountTextView.setText(String.valueOf(mLikeCount));
                mCommentCountTextView.setText(String.valueOf(mCommentCount));
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
        } else {

            if (mGutterMenus != null) {

                mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()),
                        id, mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mGutterMenus != null) {
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, mModuleName, mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            /*
            Set Result to Manage page to refresh the page if any changes made in the content.
             */
            if (isContentEdited || isContentDeleted) {
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case ConstantVariables.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                    StringBuilder sb = new StringBuilder();
                    for (String p : mSelectPath) {
                        sb.append(p);
                        sb.append("\n");
                    }

                    // uploading the photos to server
                    new UploadFileToServerUtils(mContext, redirectUrl, mSelectPath, this).execute();

                } else if (resultCode != RESULT_CANCELED) {

                    SnackbarUtils.displaySnackbar(mSplitToolBar,
                            getResources().getString(R.string.image_capturing_failed));

                }
                break;

            case ConstantVariables.VIEW_LIGHT_BOX:

                switch (resultCode) {
                    case ConstantVariables.LIGHT_BOX_DELETE:
                        int position = data.getIntExtra(PhotoLightBoxActivity.DELETED_POSITION, 0);
                        mPhotoUrls.remove(position);
                        break;

                    default:
                        isLoadingFromCreate = false;
                        makeRequest();
                        break;
                }
                break;
            case ConstantVariables.USER_PROFILE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mModuleName);
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {
                    mCommentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mCommentCount);
                    mCommentCountTextView.setText(String.valueOf(mCommentCount));
                }
                break;

            default:
                break;

        }

        if (resultCode == ConstantVariables.VIEW_PAGE_EDIT_CODE) {
            isContentEdited = true;
            isLoadingFromCreate = false;
            makeRequest();
        }
    }


    public void onClick(View view) {
        int id = view.getId();
        Intent intent;

        switch (id) {

            case R.id.likeBlock:
                int reactionId = 0;
                String reactionIcon = null, caption = null;

                if(mReactionsEnabled ==  1 && mAllReactionObject != null ){
                    reactionId = mAllReactionObject.optJSONObject("like").optInt("reactionicon_id");
                    reactionIcon = mAllReactionObject.optJSONObject("like").optJSONObject("icon").
                            optString("reaction_image_icon");
                    caption = mContext.getResources().getString(R.string.like_text);
                }
                doLikeUnlike(null, false, reactionId, reactionIcon, caption);
                break;

            case R.id.commentBlock:
            case R.id.likeCommentBlock:

                mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=" +
                        mSubjectType + "&subject_id=" + mSubjectId +
                        "&viewAllComments=1&page=1&limit=20";
                intent = new Intent(this, Comment.class);
                intent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                intent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                intent.putExtra(ConstantVariables.SUBJECT_ID, mSubjectId);
                intent.putExtra("commentCount", mCommentCount);
                intent.putExtra("reactionsEnabled", mReactionsEnabled);
                if(mContentReactions != null){
                    intent.putExtra("popularReactions", mContentReactions.toString());
                }
                startActivityForResult(intent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.left_arrow:
                coverImagePager.setCurrentItem(coverImagePager.getCurrentItem() - 1, true);
                break;

            case R.id.right_arrow:
                coverImagePager.setCurrentItem(coverImagePager.getCurrentItem() + 1, true);
                break;
            case R.id.ownerDetailView:
            case R.id.ownerTitle:
            case R.id.creator_view:
                intent = new Intent(ViewItem.this, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID, mUserId);
                startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.categoryTitle:
            case R.id.category_view:
                intent = new Intent(ViewItem.this, SearchActivity.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mModuleName);
                intent.putExtra(ConstantVariables.CATEGORY_ID, String.valueOf(mCategoryId));
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mModuleName);
                intent.putExtra(ConstantVariables.CATEGORY_VALUE, category_title);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }

    }

    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){

        mLikeUnlikeText.setTypeface(fontIcon);
        mLikeUnlikeText.setText("\uf110");
        mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null);

        if (PreferencesUtils.isNestedCommentEnabled(mContext)) {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        } else {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "send-notification";
        }

        mReactionIcon.setVisibility(View.GONE);
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, mSubjectType);
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mSubjectId));

        if(reaction != null){
            likeParams.put("reaction", reaction);
        }

        if(!isLike || isReactionChanged){
            if(mReactionsEnabled ==  1 && PreferencesUtils.isNestedCommentEnabled(mContext)){
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "advancedcomments/like?sendNotification=0";
            } else{
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "like";
            }
        } else{
            mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "unlike";
        }

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mLikeUnlikeText.setTypeface(null);

                // Set My FeedReaction Changed
                if( mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)){
                    /* If a Reaction is posted or a reaction is changed on content
                       put the updated reactions in my feed reactions array
                     */
                    updateContentReactions(reactionId, reactionIcon, caption, isLike, isReactionChanged);

                    /* Calling to send notifications after like action */
                    if (!isLike) {
                        mAppConst.postJsonResponseForUrl(sendLikeNotificationUrl, likeParams, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                            }
                        });
                    }
                }

                /*
                 Increase Like Count if content is liked else
                 decrease like count if the content is unliked
                 Do not need to increase/decrease the like count when it is already liked and only reaction is changed.
                  */
                if(!isLike){
                    mLikeCount += 1;
                } else if( !isReactionChanged){
                    mLikeCount -= 1;
                }

                // Toggle isLike Variable if reaction is not changed
                if( !isReactionChanged)
                    isLike = !isLike;

                setLikeAndCommentCount();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mLikeUnlikeText.setTypeface(null);
                setLikeAndCommentCount();
                SnackbarUtils.displaySnackbar(findViewById(R.id.main_content), message);
            }

        });
    }

    private void updateContentReactions(int reactionId, String reactionIcon, String caption,
                                        boolean isLiked, boolean isReactionChanged){

        try{

            // Update the count of previous reaction in reactions object and remove the my_feed_reactions
            if(isLiked){
                if(myReaction != null && mContentReactions != null){
                    int myReactionId = myReaction.optInt("reactionicon_id");
                    if(mContentReactions.optJSONObject(String.valueOf(myReactionId)) != null){
                        int myReactionCount = mContentReactions.optJSONObject(String.valueOf(myReactionId)).
                                optInt("reaction_count");
                        if((myReactionCount - 1) <= 0){
                            mContentReactions.remove(String.valueOf(myReactionId));
                        } else {
                            mContentReactions.optJSONObject(String.valueOf(myReactionId)).put("reaction_count",
                                    myReactionCount - 1);
                        }
                        mReactionsObject.put("feed_reactions", mContentReactions);
                    }
                }
                mReactionsObject.put("my_feed_reaction", null);
            }

            // Update the count of current reaction in reactions object and set new my_feed_reactions object.
            if(!isLiked || isReactionChanged){
                // Set the updated my Reactions

                JSONObject jsonObject = new JSONObject();
                jsonObject.putOpt("reactionicon_id", reactionId);
                jsonObject.putOpt("reaction_image_icon", reactionIcon);
                jsonObject.putOpt("caption", caption);
                mReactionsObject.put("my_feed_reaction", jsonObject);

                if (mContentReactions != null) {
                    if (mContentReactions.optJSONObject(String.valueOf(reactionId)) != null) {
                        int reactionCount = mContentReactions.optJSONObject(String.valueOf(reactionId)).optInt("reaction_count");
                        mContentReactions.optJSONObject(String.valueOf(reactionId)).putOpt("reaction_count", reactionCount + 1);
                    } else {
                        jsonObject.put("reaction_count", 1);
                        mContentReactions.put(String.valueOf(reactionId), jsonObject);
                    }
                } else {
                    mContentReactions = new JSONObject();
                    jsonObject.put("reaction_count", 1);
                    mContentReactions.put(String.valueOf(reactionId), jsonObject);
                }
                mReactionsObject.put("feed_reactions", mContentReactions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == (mPhotoUrls.size() - 1)) {
            rightArrow.setVisibility(View.INVISIBLE);
        } else {
            rightArrow.setVisibility(View.VISIBLE);
        }
        if (position == 0) {
            leftArrow.setVisibility(View.INVISIBLE);
        } else {
            leftArrow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        CustomViews.showMarqueeTitle(verticalOffset, collapsingToolbar, mToolbar, mToolBarTitle, title);
    }

    @Override
    public boolean onLongClick(View v) {

        int[] location = new int[2];
        mSplitToolBar.getLocationOnScreen(location);
        RecyclerView reactionsRecyclerView = new RecyclerView(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reactionsRecyclerView.setHasFixedSize(true);
        reactionsRecyclerView.setLayoutManager(linearLayoutManager);
        reactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final PopupWindow popUp = new PopupWindow(reactionsRecyclerView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape));
        popUp.setTouchable(true);
        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.setAnimationStyle(R.style.customDialogAnimation);

        // Playing popup effect when user long presses on like button of a feed.
        if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
            SoundUtil.playSoundEffectOnReactionsPopup(mContext);
        }
        popUp.showAtLocation(reactionsRecyclerView, Gravity.TOP, location[0], location[1] - 80);

        if(mAllReactionObject != null && mReactionsArray != null) {

            reactionsImages = new ArrayList<>();

            for(int i = 0; i< mReactionsArray.size(); i++){
                JSONObject reactionObject = mReactionsArray.get(i);
                String reaction_image_url = reactionObject.optJSONObject("icon").
                        optString("reaction_image_icon");
                String caption = reactionObject.optString("caption");
                String reaction = reactionObject.optString("reaction");
                int reactionId = reactionObject.optInt("reactionicon_id");
                String reactionIconUrl = reactionObject.optJSONObject("icon").
                        optString("reaction_image_icon");
                reactionsImages.add(new ImageViewList(reaction_image_url, caption,
                        reaction, reactionId, reactionIconUrl));
            }

            ImageAdapter reactionsAdapter = new ImageAdapter((Activity) mContext, reactionsImages, true,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {


                            ImageViewList imageViewList = reactionsImages.get(position);
                            String reaction = imageViewList.getmReaction();
                            String caption = imageViewList.getmCaption();
                            String reactionIcon = imageViewList.getmReactionIcon();
                            int reactionId = imageViewList.getmReactionId();
                            popUp.dismiss();

                            /**
                             * If the user Presses the same reaction again then don't do anything
                             */
                            if(myReaction != null){
                                if(myReaction.optInt("reactionicon_id") != reactionId){
                                    doLikeUnlike(reaction, true, reactionId, reactionIcon, caption);
                                }
                            } else{
                                doLikeUnlike(reaction, false, reactionId, reactionIcon, caption);
                            }
                        }
                    });

            reactionsRecyclerView.setAdapter(reactionsAdapter);
        }
        return true;
    }

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
        mBrowseList = (BrowseListItems) itemList;
        switch (menuName) {
            case "add":
            case "photo":
            case "add_photo":
                this.redirectUrl = mBrowseList.getmRedirectUrl();
                /* Check if permission is granted or not */
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    startImageUploading();
                }
                break;
        }
    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        String message;
        if (isRequestSuccessful) {
            message = getResources().getQuantityString(R.plurals.photo_upload_msg,
                    mSelectPath.size(),
                    mSelectPath.size());
            isLoadingFromCreate = false;
            makeRequest();
        } else {
            message = jsonObject.optString("message");
        }
        SnackbarUtils.displaySnackbar(mMainContent, message);

    }

    @Override
    protected void onResume() {

        SharedPreferences sharedPreferences = getSharedPreferences("CommentCount", MODE_PRIVATE);

        if (sharedPreferences.contains("totalCommentCount")) {
            int totalCommentCount = sharedPreferences.getInt("totalCommentCount", 0);
            mCommentCountTextView.setText(String.valueOf(totalCommentCount));
        }else{
            mCommentCountTextView.setText(String.valueOf(mCommentCount));
        }
        super.onResume();
    }

    private void startImageUploading() {

        Intent uploadPhoto = new Intent(ViewItem.this, MultiMediaSelectorActivity.class);
        // Selection type photo to display items in grid
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
        // Whether photo shoot
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // The maximum number of selectable image
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT,
                ConstantVariables.FILE_UPLOAD_LIMIT);
        // Select mode
        uploadPhoto.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE,
                MultiMediaSelectorActivity.MODE_MULTI);

        startActivityForResult(uploadPhoto, ConstantVariables.REQUEST_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow
                    startImageUploading();
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, findViewById(R.id.main_content),
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

        }
    }

}
