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

package com.fellopages.mobileapp.classes.modules.album;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUserLayoutClickListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.adapters.CustomImageAdapter;
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
import java.util.List;
import java.util.Map;

public class AlbumView extends AppCompatActivity implements View.OnClickListener,
        AppBarLayout.OnOffsetChangedListener, OnOptionItemClickResponseListener,
        OnUploadResponseListener, View.OnLongClickListener {

    private Context mContext;
    private Activity mActivity;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private String mItemViewUrl,mRequestMoreDataUrl,normalImgUrl, redirectUrl;
    private String mLoadingImageUrl;
    private JSONArray mDataResponseArray,mGutterMenus;
    private JSONObject mDataResponse,mBody,mAlbumResponseObject;

    private int columnWidth,mTotalItemCount;
    private String  actionUrl, successMessage;
    private String title,body,owner_image,owner_title,creation_date,convertedDate,albumCover;
    private int view_count,mCommentCount, mLikeCount;
    private boolean isLoadingFromCreate = false, isLoading = false, isContentEdited = false, isContentDeleted = false;
    private int mTotalPhotoCount, canEdit = 0, mLoadingPageNo = 1;
    private int mContent_id, mUserId;
    private String mSubjectType, mModuleName, mPhotoSubjectType;
    private int mSubjectId;
    private ActionIconThemedTextView mLikeCountTextView, mCommentCountTextView, mLikeUnlikeText;
    private SelectableTextView mDateView, mViewCount, mContentTitle;
    private ImageView coverImageView;
    private List<ImageViewList> mPhotoUrls;
    private ArrayList<PhotoListDetails> mPhotoDetails, mCoverImageDetails;
    private Map<String, String> postParams;
    private ImageViewList mGridViewImages;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private CustomImageAdapter recyclerViewAdapter;
    private LinearLayout mLikeCommentContent;
    private FrameLayout mCoverImageLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout mMainLayout;
    private Typeface fontIcon;
    private boolean isLike, mIsCoverRequest;
    private String mLikeUnlikeUrl, mContentUrl;
    private ArrayList<String> mSelectPath;
    private Toolbar mToolbar;
    private SplitToolbar mBottomToolBar;
    private Bundle bundle;
    private TextView mToolBarTitle;
    private AppBarLayout appBar;
    private JSONObject mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private int mReactionsEnabled;
    private ImageView mReactionIcon;
    private List<ImageViewList> reactionsImages;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ArrayList<JSONObject> mReactionsArray;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);


        /* Create Back Button On Action Bar **/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mActivity = this;
        mContext = this;

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mBrowseList = new BrowseListItems();
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(getApplicationContext());

        postParams = new HashMap<>();
        //setting up font icons
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);

        mContent_id = getIntent().getIntExtra(ConstantVariables.VIEW_PAGE_ID, 0);
        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mPhotoSubjectType= getIntent().getStringExtra(ConstantVariables.SUBJECT_TYPE);

        if(mModuleName != null){
            switch (mModuleName){
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                    mSubjectType = "sitepage_album";
                    mPhotoSubjectType = "sitepage_photo";
                    break;
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    mSubjectType = "sitegroup_album";
                    mPhotoSubjectType = "sitegroup_photo";
                    break;
                case ConstantVariables.STORE_MENU_TITLE:
                    mSubjectType = "sitestore_album";
                    mPhotoSubjectType = "sitestore_photo";
                    break;
                default:
                    mSubjectType = "album";
                    break;
            }
        }else if (mPhotoSubjectType != null && !mPhotoSubjectType.isEmpty()) {
            mSubjectType = mPhotoSubjectType.replace("_photo", "_album");
        } else {
            mSubjectType = "album";
        }

        mSubjectId = mContent_id;

        // For the user profile and cover photo update.
        bundle = getIntent().getExtras().getBundle(ConstantVariables.USER_PROFILE_COVER_BUNDLE);
        if (bundle != null) {
            mIsCoverRequest = bundle.getBoolean("isCoverRequest", false);
        }

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        /*
        Like and Unlike Fields...
         */

        mLikeCountTextView = (ActionIconThemedTextView) findViewById(R.id.likeCount);
        mCommentCountTextView = (ActionIconThemedTextView) findViewById(R.id.commentCount);
        mReactionIcon = (ImageView) findViewById(R.id.reactionIcon);
        mLikeUnlikeText = (ActionIconThemedTextView) findViewById(R.id.likeUnlikeText);

        LinearLayout mLikeBlock = (LinearLayout) findViewById(R.id.likeBlock);
        LinearLayout mCommentBlock = (LinearLayout) findViewById(R.id.commentBlock);
        LinearLayout mLikeCommentBlock = (LinearLayout) findViewById(R.id.likeCommentBlock);

        mLikeBlock.setOnClickListener(this);
        mLikeBlock.setOnLongClickListener(this);
        mCommentBlock.setOnClickListener(this);
        mLikeCommentBlock.setOnClickListener(this);

        mLikeCommentContent = (LinearLayout) findViewById(R.id.likeCommentContent);

        //Getting all the views
        mMainLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mCoverImageLayout = (FrameLayout) findViewById(R.id.cover_image_layout);
        coverImageView = (ImageView) findViewById(R.id.backdropImage);
        mContentTitle = (SelectableTextView) findViewById(R.id.content_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBottomToolBar = (SplitToolbar) findViewById(R.id.toolbarBottom);
        mDateView = (SelectableTextView) findViewById(R.id.dateView);
        mViewCount = (SelectableTextView) findViewById(R.id.viewDetail);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Hiding cover image layout.
        if ((bundle != null && !bundle.isEmpty()) || mSubjectType.equals("sitereview_album")) {
            mCoverImageLayout.setVisibility(View.GONE);
        }

        InitializeGridLayout();

        mPhotoUrls = new ArrayList<>();
        mPhotoDetails = new ArrayList<>();
        mCoverImageDetails = new ArrayList<>();
        mGridViewImages = new ImageViewList();
        recyclerViewAdapter = new CustomImageAdapter(this, mGridViewImages, mPhotoUrls, columnWidth, bundle,
                mSubjectType, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // Uploading selected image as cover/profile photo.
                if (bundle != null && !bundle.isEmpty()) {
                    PhotoListDetails photoListDetails = mPhotoDetails.get(position + 1);
                    if (mIsCoverRequest) {
                        if (bundle.containsKey(ConstantVariables.SUBJECT_TYPE)) {
                            actionUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL +  "subject_id="
                                    + bundle.getInt(ConstantVariables.SUBJECT_ID) + "&subject_type="
                                    + bundle.getString(ConstantVariables.SUBJECT_TYPE)
                                    + "&photo_id=" + photoListDetails.getPhotoId();
                        } else {
                            actionUrl =  UrlUtil.UPLOAD_USER_COVER_PHOTO_URL + "user_id/"
                                    + bundle.getInt("user_id") + "/photo_id/"
                                    + photoListDetails.getPhotoId() + "/special/cover";
                        }
                        successMessage = mContext.getResources().getString(R.string.cover_photo_updated);

                    } else {
                        if (bundle.containsKey(ConstantVariables.SUBJECT_TYPE)) {
                            actionUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL + "special=profile&subject_id="
                                    + bundle.getInt(ConstantVariables.SUBJECT_ID) + "&subject_type="
                                    + bundle.getString(ConstantVariables.SUBJECT_TYPE) + "&photo_id="
                                    + photoListDetails.getPhotoId();
                        } else {
                            actionUrl = UrlUtil.UPLOAD_USER_COVER_PHOTO_URL + "user_id/"
                                    + bundle.getInt("user_id") + "/photo_id/"
                                    + photoListDetails.getPhotoId() + "/special/profile";
                        }
                        successMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                    }

                    mAppConst.showProgressDialog();
                    mAppConst.postJsonResponseForUrl(actionUrl, postParams, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                            launchUserProfileOnSuccessOrError(successMessage);
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            launchUserProfileOnSuccessOrError(message);
                        }
                    });

                } else {
                    // on selecting grid view image
                    // launch full screen activity

                    // increasing position for the subject type = sitereview_album.
                    // because 1st position item i.e. header is hiding in this case.
                    if (mSubjectType.equals("sitereview_album")) {
                        position++;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
                    Intent i = new Intent(mActivity, PhotoLightBoxActivity.class);
                    i.putExtra(ConstantVariables.ITEM_POSITION, position);
                    i.putExtra(ConstantVariables.CAN_EDIT, canEdit);
                    i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, mTotalItemCount);
                    i.putExtra(ConstantVariables.PHOTO_REQUEST_URL, mRequestMoreDataUrl);
                    i.putExtra(ConstantVariables.IS_ALBUM_PHOTO_REQUEST, true);
                    i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mModuleName);
                    if(mPhotoSubjectType != null && !mPhotoSubjectType.isEmpty()){
                        i.putExtra(ConstantVariables.SUBJECT_TYPE, mPhotoSubjectType);
                    }
                    i.putExtras(bundle);
                    //TODO getting Parcelable encountered IOException writing serializable object
                    mActivity.startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
                }
            }

        }, new OnUserLayoutClickListener() {

            @Override
            public void onUserLayoutClick() {

                Intent intent = new Intent(AlbumView.this, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID,mUserId);
                startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        mRecyclerView.setAdapter(recyclerViewAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleCount = mLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;
                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mGridViewImages.getTotalPhotoCount()) {
                        mPhotoUrls.add(null);
                        recyclerViewAdapter.notifyItemInserted(mPhotoUrls.size() - 1);
                        mLoadingPageNo = mLoadingPageNo + 1;
                        mLoadingImageUrl = mItemViewUrl + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        mAppConst.getJsonResponseFromUrl(mLoadingImageUrl, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                mPhotoUrls.remove(mPhotoUrls.size() - 1);
                                recyclerViewAdapter.notifyItemRemoved(mPhotoUrls.size());
                                mBody = jsonObject;
                                mDataResponseArray = mBody.optJSONArray("albumPhotos");
                                mTotalPhotoCount = mBody.optInt("totalPhotoCount");
                                canEdit = mBody.optInt("canEdit");
                                mGridViewImages.setTotalPhotoCount(mTotalPhotoCount);
                                loadMorePhotos();
                                recyclerViewAdapter.notifyItemInserted(mPhotoUrls.size());
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                            }
                        });

                    }

                }


            }
        });


        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (recyclerViewAdapter.getItemViewType(position)) {
                    case CustomImageAdapter.TYPE_HEADER:
                        return mLayoutManager.getSpanCount();
                    case CustomImageAdapter.TYPE_PROGRESS:
                        return 3;//number of columns of the grid
                    case CustomImageAdapter.TYPE_ITEM:
                        return 1;
                    default:
                        return -1;
                }

            }
        });

        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);



        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(mContext);

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
                        PreferencesUtils.storeReactions(mContext, mAllReactionObject);
                        if(mAllReactionObject != null){
                            mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mAllReactionObject);
                        }
                    }

                    // Send Request to load View page data after fetching Reactions on the content.
                    if(mBody != null) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        makePhotoRequest();
                        setValuesInView();
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
                        makePhotoRequest();
                        setValuesInView();
                    }else {
                        makeRequest();
                    }
                }

            });
        } else{
            if(mBody != null) {
                isLoadingFromCreate = true;
                isContentEdited = true;
                makePhotoRequest();
                setValuesInView();
            }else {
                makeRequest();
            }
        }

    }

    /**
     * Method to launch user profile activity when photo uploaded successfully or there is an error.
     * @param successMessage success or error message
     */
    public void launchUserProfileOnSuccessOrError(String successMessage) {
        mAppConst.hideProgressDialog();
        SnackbarUtils.displaySnackbarLongWithListener(mMainLayout, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        Intent intent;
                        if (bundle.containsKey(ConstantVariables.SUBJECT_TYPE)) {
                            intent = GlobalFunctions.getIntentForModule(mContext,
                                    bundle.getInt(ConstantVariables.SUBJECT_ID),
                                    bundle.getString(ConstantVariables.SUBJECT_TYPE), null);

                        } else {
                            intent = new Intent(mContext, userProfile.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtras(bundle);
                        intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
                        startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     * */
    private void InitializeGridLayout() {

        // Column width
        columnWidth = (mAppConst.getScreenWidth()/ AppConstant.NUM_OF_COLUMNS_FOR_VIEW_PAGE);


        mLayoutManager = new GridLayoutManager(this, AppConstant.NUM_OF_COLUMNS_FOR_VIEW_PAGE);


        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void makeRequest(){
        mLoadingPageNo = 1;
        if(!isLoadingFromCreate) {

            mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    makePhotoRequest();
                    setValuesInView();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(mMainLayout, message,
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

    public void makePhotoRequest(){

        String url = UrlUtil.VIEW_PHOTOS_URL + "?subject_id=" + mSubjectId + "&subject_type=" + mSubjectType;
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(jsonObject != null){
                    mDataResponseArray = jsonObject.optJSONArray("albumPhotos");
                    mPhotoDetails.clear();
                    mPhotoUrls.clear();
                    loadMorePhotos();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(mMainLayout, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }

        });
    }


    public void setValuesInView(){

        mRecyclerView.setVisibility(View.VISIBLE);

        try {

            // Parsing json object response
            // response will be a json object
            mDataResponse = mBody.optJSONObject("response");
            mGutterMenus = mBody.optJSONArray("gutterMenu");

            if (mDataResponse == null) {
                mDataResponseArray = mBody.optJSONArray("response");
                mDataResponse = mAppConst.convertToJsonObject(mDataResponseArray);
            }

            if (mGutterMenus != null) {
                invalidateOptionsMenu();
            }

            mTotalPhotoCount = mBody.optInt("totalPhotoCount");
            canEdit = mBody.optInt("canEdit");
            mGridViewImages.setTotalPhotoCount(mTotalPhotoCount);
            mAlbumResponseObject = mBody.optJSONObject("album");

            // Checking if album is coming or not.
            if (mAlbumResponseObject != null) {
                mSubjectId = mAlbumResponseObject.optInt("album_id");
                mTotalItemCount = mBody.optInt("totalPhotoCount");
                view_count = mAlbumResponseObject.optInt("view_count");
                title = mAlbumResponseObject.optString("title");
                body = mAlbumResponseObject.optString("description");
                mCommentCount = mAlbumResponseObject.optInt("comment_count");
                mLikeCount = mAlbumResponseObject.optInt("like_count");
                isLike = mAlbumResponseObject.optBoolean("is_like");
                albumCover = mAlbumResponseObject.optString("image");
                owner_image = mAlbumResponseObject.optString("owner_image_icon");
                owner_title = mAlbumResponseObject.optString("owner_title");
                mUserId = mAlbumResponseObject.optInt("owner_id");
                creation_date = mAlbumResponseObject.optString("creation_date");
                convertedDate = AppConstant.convertDateFormat(getResources(), creation_date);
                mContentUrl = mAlbumResponseObject.optString("content_url");
            } else {
                mTotalItemCount = mBody.optInt("totalItemCount");
            }
            mBrowseList = new BrowseListItems(mSubjectId, title, albumCover, mContentUrl, redirectUrl);

//            loadMorePhotos();

            // Setting up Like and Comment Count
            setLikeAndCommentCount();


            mCoverImageDetails.add(new PhotoListDetails(albumCover));
            mImageLoader.setImageUrl(albumCover, coverImageView);

            coverImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverImageDetails);
                    Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
                    i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                    i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });

            mContentTitle.setText(title);
            collapsingToolbarLayout.setTitle(title);
            mToolBarTitle.setText(title);
            String dateString = getResources().getString(R.string.date_salutation);
            String modifiedDate = String.format(dateString, convertedDate);
            mDateView.setText(Html.fromHtml(modifiedDate));

            mViewCount.setTypeface(fontIcon);
            String viewCountString = getResources().getString(R.string.view_salutation);
            String modifiedViewString = String.format(viewCountString,view_count);
            mViewCount.setText(String.format("%s \uF06E", Html.fromHtml(modifiedViewString)));

        } catch (Exception e) {
            e.printStackTrace();
            SnackbarUtils.displaySnackbar(mMainLayout,
                    getResources().getString(R.string.no_data_available));
        }

    }

    public void loadMorePhotos(){

        if (mDataResponseArray != null) {
            for (int i = 0; i < mDataResponseArray.length(); i++) {
                JSONObject imageUrlsObj = mDataResponseArray.optJSONObject(i);
                String menuArray = imageUrlsObj.optString("menu");

                normalImgUrl = imageUrlsObj.optString("image");
                String image_title = imageUrlsObj.optString("title");
                String image_desc = imageUrlsObj.optString("description");
                int photo_id = imageUrlsObj.optInt("photo_id");
                int album_id = imageUrlsObj.optInt("album_id");
                int likeCount = imageUrlsObj.optInt("like_count");
                int commentCount = imageUrlsObj.optInt("comment_count");
                boolean likeStatus = imageUrlsObj.optBoolean("is_like");
                String contentUrl = imageUrlsObj.optString("content_url");
                if (imageUrlsObj.has("isLike")) {
                    likeStatus = imageUrlsObj.optBoolean("isLike");
                }
                String reactions = imageUrlsObj.optString("reactions");
                String mUserTagArray = imageUrlsObj.optString("tags");

                mRequestMoreDataUrl = AppConstant.DEFAULT_URL + "albums/photo/list?" +
                        "album_id="+album_id+"&limit=" + AppConstant.LIMIT;

                mPhotoDetails.add(new PhotoListDetails(title, owner_title,
                        image_title, image_desc, photo_id, normalImgUrl, likeCount,
                        commentCount, likeStatus, menuArray, reactions, mUserTagArray, contentUrl));
                mPhotoUrls.add(new ImageViewList(normalImgUrl));

                mGridViewImages.setAlbumDescription(body);
                mGridViewImages.setOwnerImageUrl(owner_image);
                mGridViewImages.setOwnerTitle(owner_title);

                isLoading = false;
            }
            recyclerViewAdapter.notifyDataSetChanged();
        }

    }

    public void setLikeAndCommentCount(){

        // Hiding like/comment bar when user is logged out or the request is for choose picture for profile/cover.
        if(mAppConst.isLoggedOutUser() || bundle != null || mSubjectType.equals("sitereview_album")){
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mRecyclerView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            mRecyclerView.setLayoutParams(params);
            mBottomToolBar.setVisibility(View.GONE);
        }else{
            mBottomToolBar.setVisibility(View.VISIBLE);
            // setting up the values in views
            mLikeCommentContent.setVisibility(View.VISIBLE);

            mLikeUnlikeText.setActivated(isLike);
            if(!isLike){
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            }else{
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }

            /*
            Set Like and Comment Count
             */

            // Check if Reactions is enabled, show that content reaction and it's icon here.
            if(mReactionsEnabled == 1 && mReactionsObject != null && mReactionsObject.length() != 0){


                myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                if(myReaction != null && myReaction.length() != 0){
                    mLikeUnlikeText.setText(myReaction.optString("caption"));
                    String reactionImage = myReaction.optString("reaction_image_icon");
                    mImageLoader.setImageUrl(reactionImage, mReactionIcon);
                    mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                    mReactionIcon.setVisibility(View.VISIBLE);
                } else {
                    mReactionIcon.setVisibility(View.GONE);
                    mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    mLikeUnlikeText.setTextColor(
                            ContextCompat.getColor(mContext, R.color.grey_dark));
                    mLikeUnlikeText.setText(getString(R.string.like_text));
                }
            } else{
                mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                mLikeUnlikeText.setTextColor(
                        ContextCompat.getColor(mContext, R.color.grey_dark));
                mLikeUnlikeText.setText(getString(R.string.like_text));
            }

            mLikeCountTextView.setText(String.valueOf(mLikeCount));
            mCommentCountTextView.setText(String.valueOf(mCommentCount));
            mCommentCountTextView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.grey_dark));
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
        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        } else {

            if(mGutterMenus != null) {
                mGutterMenuUtils.onMenuOptionItemSelected(mMainLayout, findViewById(item.getItemId()),
                        id, mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        // Not showing the option menu when the request is for choose picture for profile/cover
        // or if the gutter menus null.
        if(mGutterMenus != null && bundle == null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.ALBUM_MENU_TITLE,
                    mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
        /*
        Set Result to Manage page to refresh the page if any changes made in the content.
         */
            if (isContentEdited || isLoadingFromCreate || isContentDeleted) {
                Intent intent = new Intent();
                setResult(ConstantVariables.ALBUM_VIEW_PAGE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
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

                    // failed to capture image
                    SnackbarUtils.displaySnackbar(mBottomToolBar,
                            getResources().getString(R.string.image_capturing_failed));

                }
                break;

            case ConstantVariables.CREATE_REQUEST_CODE:
            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                isLoadingFromCreate = false;
                isContentEdited = true;
                makeRequest();
                break;

            case ConstantVariables.VIEW_LIGHT_BOX:

                switch (resultCode) {
                    case ConstantVariables.LIGHT_BOX_DELETE:
                        isLoadingFromCreate = false;
                        makeRequest();
                        break;

                    case ConstantVariables.LIGHT_BOX_EDIT:
                        if (data.getBooleanExtra(ConstantVariables.IS_CONTENT_EDITED, false)) {
                            isLoadingFromCreate = false;
                            makeRequest();
                        } else {
                            mPhotoDetails = (ArrayList<PhotoListDetails>) data.getSerializableExtra(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST);
                        }
                        break;
                }
                break;

            case ConstantVariables.USER_PROFILE_CODE:
            case ConstantVariables.CONTENT_COVER_EDIT_CODE:
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
    }


    public void onClick(View view) {
        int id = view.getId();

        switch (id){

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

                String likeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type="
                        + mSubjectType + "&subject_id=" + mSubjectId +
                        "&viewAllComments=1&page=1&limit=" + AppConstant.LIMIT;
                Intent commentIntent = new Intent(this, Comment.class);
                commentIntent.putExtra("LikeCommentUrl", likeCommentsUrl);
                commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                commentIntent.putExtra(ConstantVariables.SUBJECT_ID, mSubjectId);
                commentIntent.putExtra("commentCount", mCommentCount);
                commentIntent.putExtra("reactionsEnabled", mReactionsEnabled);
                if(mContentReactions != null){
                    commentIntent.putExtra("popularReactions", mContentReactions.toString());
                }
                startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if(verticalOffset == -collapsingToolbarLayout.getHeight() + mToolbar.getHeight()){
            mToolBarTitle.setVisibility(View.VISIBLE);
            collapsingToolbarLayout.setTitle("");
        }else{
            mToolBarTitle.setVisibility(View.GONE);
            collapsingToolbarLayout.setTitle(title);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int[] location = new int[2];
        mBottomToolBar.getLocationOnScreen(location);
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
        SnackbarUtils.displaySnackbarShortWithListener(mMainLayout, successMessage,
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
            case "addphoto":
                this.redirectUrl = mBrowseList.getmRedirectUrl();
                /* Check if permission is granted or not */
                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
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

            isContentEdited = true;
            isLoadingFromCreate = false;
            makeRequest();
        } else {
            message = jsonObject.optString("message");
        }
        SnackbarUtils.displaySnackbar(mMainLayout, message);

    }

    private void startImageUploading(){

        Intent uploadPhoto = new Intent(AlbumView.this, MultiMediaSelectorActivity.class);
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

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    startImageUploading();
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.
                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mMainLayout,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

        }
    }

    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){
        if (PreferencesUtils.isNestedCommentEnabled(mContext)) {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        } else {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "send-notification";
        }

        mLikeUnlikeText.setTypeface(fontIcon);
        mLikeUnlikeText.setText("\uf110");
        mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null);
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
                if( mReactionsEnabled == 1){
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
}
