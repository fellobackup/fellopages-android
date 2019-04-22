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

package com.fellopages.mobileapp.classes.modules.advancedActivityFeeds;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.adapters.CustomImageAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.dialogs.CheckInLocationDialog;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncFacebookResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnCheckInLocationResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnViewTouchListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.PredicateLayout;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.BitMapCreatorUtil;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CacheUtils;
import com.fellopages.mobileapp.classes.common.utils.EmojiUtil;
import com.fellopages.mobileapp.classes.common.utils.FeedList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadAttachmentUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.LoginActivity;
import com.fellopages.mobileapp.classes.core.NewLoginActivity;
import com.fellopages.mobileapp.classes.modules.stickers.StickersClickListener;
import com.fellopages.mobileapp.classes.modules.stickers.StickersGridView;
import com.fellopages.mobileapp.classes.modules.stickers.StickersPopup;
import com.fellopages.mobileapp.classes.modules.stickers.StickersUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.TextContextMenuListener;

import static android.app.Activity.RESULT_OK;
import static com.fellopages.mobileapp.classes.core.ConstantVariables.MAP_VIEW;
import static com.fellopages.mobileapp.classes.core.ConstantVariables.STATUS_POST_OPTIONS;


public class Status extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        OnAsyncResponseListener, OnCheckInLocationResponseListener, OnCancelClickListener,
        OnAsyncFacebookResponseListener, AdapterView.OnItemClickListener {

    public static final int BANNER_TEXT_SIZE = 30;
    public static JSONObject FORM_OBJECT = new JSONObject();
    public static JSONArray USER_LIST_ARRAY = new JSONArray();
    public static JSONArray NETWORK_LIST_ARRAY = new JSONArray();
    String fb_uid, fbAccessToken;
    public String mStatusPostUrl;
    public HashMap<String, String> mPostAttachmentParams;
    String videoAttachedUrl;
    Toolbar mToolbar;
    boolean isPosted = false, mIsStickerEnabled = false, isBannerSelected = false, isBannerSet = true, isFullTextShowing = false,
            isEditRequest;
    int mStart = 0;
    CharSequence mCharSequence;
    private Context mContext;
    private String mAttachPostUrl, mAttachType = "", mStickerGuid, mStickerImage, mSchedulePostTime;
    private PredicateLayout mTaggedFriendsLayout;
    private LinearLayout mLocationsLayout, mAddLinkLayout, mAddVideoLayout, mLinkAttachmentBlock, llSticker;
    private Map<String, String> selectedFriends, mMultiSelectUserPrivacy;
    private Map<String, String> selectedLocation;
    private View persistentBottomSheet, bannerView;
    private NestedScrollView mNestedScrollView;
    private RelativeLayout.LayoutParams nestedViewParams;
    private BottomSheetBehavior mBottomSheetBehavior;
    private StatusPostRecyclerViewAdapter postAdapter;
    private CoordinatorLayout clBottom;
    private TextView tvUserTitle, tvPrivacy, tvPostSchedule, tvTargetAudience, mLocationPrefix, mLocationLabel,
            mAddLinkText, mAddVideoText, musicAddedMessage;
    private EditText mEnterLinkText;
    private EmojiconEditText mStatusBodyField;
    private Button mAttachLinkButton;
    private ArrayList<String> popupMenuList = new ArrayList<>();
    public  ArrayList<String> mSelectPath = new ArrayList<>();
    private JSONObject mFeedPostMenus;
    private AppConstant mAppConst;
    private JSONObject mSelectedLocationObject, mUserPrivacyObject, mUserListObject, feedDecoration;
    private JSONArray mPrivacyKeys, bannerArray;
    private String mSelectedPrivacyKey = "everyone", mSubjectType, mUploadingOption;
    private boolean mShowPhotoBlock, mOpenCheckIn, mOpenVideoUpload, mCanShowBanner = true;
    private int mSubjectId, width;
    private boolean isAttachmentAttached = false, isExternalShare = false, isFacebookPost = false, isTwitterPost = false;
    private ImageView ivSticker, ivBannerIndicator, mVideoAttachmentIcon, mLinkAttachmentImage,
            mCancelSelectedLocation, ivCancelSticker;
    private TextView mVideoAttachmentTitle, mVideoAttachmentBody, mLinkAttachmentTitle, mLinkAttachmentDescription,
            mLinkAttachmentUrl, tvPrice, tvLocation, tvVideoAddedMsg, tvEdit, tvCancel;
    private TreeMap<String, String> statusBodyFieldUrlList;
    private Bundle mSelectedLocationBundle, mFeelingActivityBundle;
    private CallbackManager callbackManager;
    private String mSelectedMusicFile, uriText = null, mSelectedVideoThumbnail;
    private int mVideoId, mSongId;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;
    private String searchText;
    private AddPeopleAdapter mAddPeopleAdapter;
    private List<AddPeopleList> mAddPeopleList;
    private List<BrowseListItems> mBrowseList;
    private String tagString, fontColor, fontStyle, bannerFontColor, userName, mEditBannerUrl;
    private int decorationCharacterLength, fontSize, bannerTextSize, userId;
    private ListView mUserListView;
    private JSONObject tagObject, bannerObject, schedulePostFormValues, targetAudienceFormValues,
            sellSomethingFormValues;
    private PopupWindow popupWindow;
    private RecyclerView mPhotosRecyclerView, mSellSomethingPhotosRecyclerView, mBannerRecyclerView;
    private CustomImageAdapter mCustomImageAdapter;
    private StickersPopup mStickersPopup;
    private RelativeLayout mStickersParentView, mBodyView, rlTitleView;
    private LinearLayout.LayoutParams titleViewParam;
    private BannerPhotoRecyclerAdapter mBannerAdapter;
    private Drawable mBannerDrawable;
    private FeedList mFeedList;
    private GradientDrawable bannerIndicatorShape;
    public static JSONObject mBody;
    private View mVideoPreviewLayout;
    private CheckInLocationDialog checkInLocationDialog;
    private String mSelectedVideoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mContext = this;
        mAppConst = new AppConstant(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(getApplicationContext());
        statusBodyFieldUrlList = new TreeMap<>(Collections.reverseOrder());
        mAddPeopleList = new ArrayList<>();
        mBrowseList = new ArrayList<>();
        mSelectPath = new ArrayList<>();
        Status.mBody = null;
        //Setting up the action bar
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.title_activity_status));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mSelectedPrivacyKey = PreferencesUtils.getStatusPrivacyKey(mContext);
        mIsStickerEnabled = PreferencesUtils.getStickersEnabled(mContext) == 1;
        mStickersParentView = findViewById(R.id.stickersMainLayout);
        llSticker = findViewById(R.id.sticker_popup);

        mNestedScrollView = findViewById(R.id.nestedScrollView);
        nestedViewParams = (RelativeLayout.LayoutParams) mNestedScrollView.getLayoutParams();
        mBodyView = findViewById(R.id.body_view);
        rlTitleView = findViewById(R.id.title_view);
        titleViewParam = (LinearLayout.LayoutParams) rlTitleView.getLayoutParams();
        bannerView = findViewById(R.id.banner_view);
        ivBannerIndicator = findViewById(R.id.banner_indicator);
        ivBannerIndicator.setOnClickListener(this);
        bannerIndicatorShape = new GradientDrawable();
        bannerIndicatorShape.setShape(GradientDrawable.RECTANGLE);
        bannerIndicatorShape.setCornerRadius(3.0f);
        bannerIndicatorShape.setColor(ContextCompat.getColor(mContext, R.color.white));
        bannerIndicatorShape.setStroke(1, ContextCompat.getColor(mContext, R.color.gray_stroke_color));
        ivBannerIndicator.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                PorterDuff.Mode.SRC_ATOP);
        ivBannerIndicator.setImageResource(R.drawable.ic_chevron_left);
        ivBannerIndicator.setBackground(bannerIndicatorShape);

        ivSticker = findViewById(R.id.iv_sticker);
        ivCancelSticker = findViewById(R.id.iv_cancel_sticker);
        ivCancelSticker.setColorFilter(ContextCompat.getColor(mContext, R.color.gray_stroke_color),
                PorterDuff.Mode.SRC_ATOP);

        mStatusBodyField = findViewById(R.id.statusBody);
        mStatusBodyField.setHint(mContext.getResources().getString(R.string.status_box_default_text) + "...");
        mStatusBodyField.addListener(() -> detectUri(mCharSequence, mStart, true));
        musicAddedMessage = findViewById(R.id.music_added_msg);

        selectedFriends = new HashMap<>();
        selectedLocation = new HashMap<>();

        clBottom = findViewById(R.id.bottom_sheet_layout);
        mAddVideoText = findViewById(R.id.addVideoText);
        mAddVideoLayout = findViewById(R.id.addVideoLayout);
        mTaggedFriendsLayout = findViewById(R.id.taggedFriends);
        mLocationsLayout = findViewById(R.id.Locations);

        mLocationPrefix = findViewById(R.id.locationPrefix);
        mLocationLabel = findViewById(R.id.name);
        mCancelSelectedLocation = findViewById(R.id.cancel);
        mCancelSelectedLocation.setOnClickListener(this);

        mAddLinkLayout = findViewById(R.id.addLinkBlock);
        mEnterLinkText = findViewById(R.id.enterLinkText);
        mAttachLinkButton = findViewById(R.id.attachLinkButton);
        mAddLinkText = findViewById(R.id.addLinkText);

        //Link Attachment Info
        mLinkAttachmentBlock = findViewById(R.id.linkAttachment);
        mLinkAttachmentImage = findViewById(R.id.linkAttachmentImage);
        mLinkAttachmentTitle = findViewById(R.id.linkAttachmentTitle);
        mLinkAttachmentDescription = findViewById(R.id.linkAttachmentDescription);
        mLinkAttachmentUrl = findViewById(R.id.linkAttachmentUrl);

        //Video attachment info.
        mVideoAttachmentIcon = findViewById(R.id.attachmentIcon);
        mVideoAttachmentTitle = findViewById(R.id.attachmentTitle);
        mVideoAttachmentBody = findViewById(R.id.attachmentBody);
        tvVideoAddedMsg = findViewById(R.id.video_added_msg);
        tvPrice = findViewById(R.id.price_text);
        tvLocation = findViewById(R.id.location);
        tvEdit = findViewById(R.id.edit);
        tvCancel = findViewById(R.id.cancel_attachment);

        mVideoPreviewLayout = findViewById(R.id.attachment_preview_layout);
        // Photos preview recycler view.
        mPhotosRecyclerView = findViewById(R.id.recycler_view_list);
        mPhotosRecyclerView.setNestedScrollingEnabled(false);
        mPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mPhotosRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));

        mSellSomethingPhotosRecyclerView = findViewById(R.id.sell_something_recycler_view_list);
        mSellSomethingPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        mSellSomethingPhotosRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));

        mBannerRecyclerView = findViewById(R.id.banner_recycler_view);
        mBannerRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));

        // Getting edit feed's value.
        mFeedList = getIntent().getParcelableExtra(ConstantVariables.FEED_LIST);
        if (mFeedList != null) {
            mIsStickerEnabled = false;
            isEditRequest = true;
            if (mFeedList.getmIsShareFeed() && mFeedList.getmShareBody() != null && !mFeedList.getmShareBody().isEmpty()) {
                mStatusBodyField.setText(Smileys.getEmojiFromString(mFeedList.getmShareBody()));
                mStatusBodyField.setSelection(mStatusBodyField != null ? mStatusBodyField.getText().length() : 0);
            } else if (mFeedList.getmActionTypeBody() != null && !mFeedList.getmActionTypeBody().isEmpty()) {
                mStatusBodyField.setText(Smileys.getEmojiFromString(mFeedList.getmActionTypeBody()));
                mStatusBodyField.setSelection(mStatusBodyField != null ? mStatusBodyField.getText().length() : 0);
            }
            mStatusBodyField.addTextChangedListener(this);

            JSONObject userTagObject = mFeedList.getUserTagObject();

            // If there is any user tag object then creating tag string.
            if (userTagObject != null && userTagObject.length() > 0) {
                userTagObject.keys();
                Iterator<String> iter = userTagObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = userTagObject.optString(key);
                    String userTagString = key + "=" + value;

                    if (tagString != null && tagString.length() > 0) {
                        tagString = tagString + "&" + userTagString;
                    } else {
                        tagString = userTagString;
                    }
                }
                tagObject = new JSONObject();
                try {
                    tagObject.put("tag", tagString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mEditBannerUrl = mFeedList.getBannerObject() != null ? mFeedList.getBannerObject().optString("image")
                    : null;
            mSchedulePostTime = mFeedList.getSchedulePostTime() != null && !mFeedList.getSchedulePostTime().isEmpty()
                    && !mFeedList.getSchedulePostTime().equals("null") ? mFeedList.getSchedulePostTime() : null;

            if ((mFeedList.getmFeedType() != null && mFeedList.getmFeedType().equals("share"))
                    || (mFeedList.getmAttachmentCount() > 0)) {
                mCanShowBanner = false;
            }

            String privacy = mFeedList.getPostPrivacy();
            if (privacy != null && !privacy.isEmpty() && mFeedList.getPrivacyIcon() != null) {
                if (!mFeedList.getPrivacyIcon().equals("network_list_custom")
                        && !mFeedList.getPrivacyIcon().equals("friend_list_custom")) {
                    mSelectedPrivacyKey = privacy;
                } else {
                    mSelectedPrivacyKey = null;
                    mMultiSelectUserPrivacy = new HashMap<>();
                    List<String> multiOptionList = Arrays.asList(privacy.split("\\s*,\\s*"));
                    if (!multiOptionList.isEmpty()) {
                        for (int i = 0; i < multiOptionList.size(); i++) {
                            mMultiSelectUserPrivacy.put(multiOptionList.get(i), "1");
                        }
                    }
                }
            }
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (STATUS_POST_OPTIONS.isEmpty()) {
            // calling for status post options.
            mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/feeds/feed-decoration",
                    new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {
                            STATUS_POST_OPTIONS.put(ConstantVariables.FEED_DECORATION,
                                    jsonObject.optJSONObject("feed_docoration_setting"));
                            STATUS_POST_OPTIONS.put(ConstantVariables.WORD_STYLING,
                                    jsonObject.optJSONArray("word_styling"));
                            STATUS_POST_OPTIONS.put(ConstantVariables.ON_THIS_DAY,
                                    jsonObject.optJSONObject("on_thisDay"));
                            setFeedDecorationValues();
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                        }
                    });
        } else {
            setFeedDecorationValues();
        }

        if (mCanShowBanner && !STATUS_POST_OPTIONS.isEmpty() && STATUS_POST_OPTIONS.get(ConstantVariables.BANNER_DECORATION) != null) {
            showBanner();
        } else if (mCanShowBanner) {
            mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/feelings/banner",
                    new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {
                            STATUS_POST_OPTIONS.put(ConstantVariables.BANNER_DECORATION,
                                    jsonObject.optJSONArray("response"));
                            showBanner();
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                        }
                    });
        }

        /**
         * Check if user is logged-out in case of External share
         * Redirect to login first and send the intent data to Login Activity
         * After successful login load the intent data here
         */
        if (PreferencesUtils.getAuthToken(this) == null || PreferencesUtils.getAuthToken(this).isEmpty()) {

            Intent loginIntent;
            if (ConstantVariables.INTRO_SLIDE_SHOW_ENABLED == 1) {
                loginIntent = new Intent(mContext, NewLoginActivity.class);
            } else {
                loginIntent = new Intent(mContext, LoginActivity.class);
            }

            loginIntent.putExtras(intent.getExtras());
            loginIntent.setAction(action);
            loginIntent.setType(type);
            finish();
            startActivity(loginIntent);
        }

        if (action != null && type != null) {
            switch (action) {
                case Intent.ACTION_SEND:
                    isExternalShare = true;
                    if (type.equals("text/plain")) {
                        handleSendText(intent); // Handle text being sent
                    } else if (type.startsWith("image/") || type.startsWith("video/")) {
                        if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
                        } else if (type.startsWith("image/")){
                            handleSendImage(intent); // Handle single image being sent
                        } else if (type.startsWith("video/")) {
                            Uri mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                            mSelectedVideoPath = GlobalFunctions.getRealPathFromURI(mContext, mVideoUri);
                            attachVideoPreview();
                        }
                    }
                    break;
                case Intent.ACTION_SEND_MULTIPLE:
                    isExternalShare = true;
                    if (type.startsWith("image/")) {
                        if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
                        } else {
                            handleSendMultipleImages(intent); // Handle multiple images being sent
                        }
                    }
                    break;
            }
        } else {
            try {
                if (intent.getStringExtra("feedPostMenus") != null) {
                    mFeedPostMenus = new JSONObject(intent.getStringExtra("feedPostMenus"));
                }
                mShowPhotoBlock = intent.getBooleanExtra("showPhotoBlock", true);
                mOpenCheckIn = intent.getBooleanExtra("openCheckIn", false);
                mOpenVideoUpload = intent.getBooleanExtra("openVideo", false);
                mSubjectType = intent.getStringExtra(ConstantVariables.SUBJECT_TYPE);
                mSubjectId = intent.getIntExtra(ConstantVariables.SUBJECT_ID, 0);

                if (mOpenCheckIn) {
                    if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        mAppConst.requestForManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                ConstantVariables.ACCESS_FINE_LOCATION);
                    } else {
                        addLocation(true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Showing user image and name.
        ImageView ivUserProfile = findViewById(R.id.user_profile_image);
        tvUserTitle = findViewById(R.id.user_name);
        tvPrivacy = findViewById(R.id.post_privacy);
        tvPrivacy.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvPostSchedule = findViewById(R.id.post_schedule);
        tvPostSchedule.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvTargetAudience = findViewById(R.id.target_audience);
        tvTargetAudience.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        if (PreferencesUtils.getUserDetail(mContext) != null) {
            try {
                JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                String userImage = userDetail.optString("image");
                userName = userDetail.optString("displayname");
                userId = userDetail.optInt("user_id");
                mImageLoader.setImageForUserProfile(userImage, ivUserProfile);
                tvUserTitle.setText(userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Show attachment Options in bottom
        if (mFeedPostMenus != null && mFeedPostMenus.length() != 0) {

            mUserPrivacyObject = mFeedPostMenus.optJSONObject("userprivacy");
            mUserListObject = mFeedPostMenus.optJSONObject("userlists");

            initializeBottomSheet();

            if (mUserPrivacyObject != null && mUserPrivacyObject.length() != 0) {
                tvPrivacy.setVisibility(View.VISIBLE);
                tvPrivacy.setOnClickListener(this);
                StateListDrawable drawable = (StateListDrawable) tvPrivacy.getBackground();
                DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
                if (dcs != null) {
                    Drawable[] drawableItems = dcs.getChildren();
                    GradientDrawable gradientDrawableChecked = (GradientDrawable) drawableItems[0];
                    GradientDrawable gradientDrawableUnChecked = (GradientDrawable) drawableItems[1];
                    gradientDrawableChecked.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height),
                            ContextCompat.getColor(mContext, R.color.gray_text_color));
                    gradientDrawableUnChecked.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height),
                            ContextCompat.getColor(mContext, R.color.gray_text_color));
                }
                USER_LIST_ARRAY = mFeedPostMenus.optJSONArray("userlist");
                NETWORK_LIST_ARRAY = mFeedPostMenus.optJSONArray("multiple_networklist");

                if (mMultiSelectUserPrivacy == null && mSelectedPrivacyKey != null
                        && !mSelectedPrivacyKey.equals("null")) {
                    mMultiSelectUserPrivacy = new HashMap<>();

                    // When the user selected custom network or friend list then putting the all options into map.
                    if ((mSelectedPrivacyKey.equals("network_list_custom")
                            || mSelectedPrivacyKey.equals("friend_list_custom"))
                            && PreferencesUtils.getStatusPrivacyMultiOptions(mContext) != null) {
                        setUserPrivacy("\uf013", mSelectedPrivacyKey);
                        List<String> multiOptionList = Arrays.asList(PreferencesUtils.
                                getStatusPrivacyMultiOptions(mContext).split("\\s*,\\s*"));
                        if (!multiOptionList.isEmpty()) {
                            for (int i = 0; i < multiOptionList.size(); i++) {
                                mMultiSelectUserPrivacy.put(multiOptionList.get(i), "1");
                            }
                        }
                    }

                    setPrivacyOption(false);
                } else if (mFeedList != null && mFeedList.getPrivacyIcon() != null
                        && !mFeedList.getPrivacyIcon().equals("null")) {
                    setUserPrivacy("\uf013", mFeedList.getPrivacyIcon());
                } else {
                    tvPrivacy.setVisibility(View.GONE);
                }

            } else {
                tvPrivacy.setVisibility(View.GONE);
            }

            if (mFeedPostMenus.optInt("allowSchedulePost") == 1) {
                schedulePostFormValues = new JSONObject();
                tvPostSchedule.setVisibility(View.VISIBLE);
                tvPostSchedule.setOnClickListener(this);
                tvPostSchedule.setText("\uf073");
                if (mSchedulePostTime != null) {
                    try {
                        schedulePostFormValues.put("schedule_time", mSchedulePostTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setScheduleSelected();
            } else {
                tvPostSchedule.setVisibility(View.GONE);
            }

            if (mFeedPostMenus.optInt("allowTargetPost") == 1) {
                targetAudienceFormValues = new JSONObject();
                tvTargetAudience.setVisibility(View.VISIBLE);
                tvTargetAudience.setOnClickListener(this);
                tvTargetAudience.setText("\uf05b");
                setTargetAudienceSelected();
            } else {
                tvTargetAudience.setVisibility(View.GONE);
            }
        } else {
            nestedViewParams.bottomMargin = 0;
            mNestedScrollView.setLayoutParams(nestedViewParams);
            clBottom.setVisibility(View.GONE);
        }

        /*
         Open Photo Gallery If Photos Clicked From Status Menu
         */
        if (mShowPhotoBlock) {
            if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        ConstantVariables.WRITE_EXTERNAL_STORAGE);
            } else {
                addPhotoBlock(true);
            }
        }

        if (mOpenVideoUpload) {
            mUploadingOption = "video";
            if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        ConstantVariables.WRITE_EXTERNAL_STORAGE);
            } else {
                attachVideo();
            }
        }

        /* To create a Emoji popup with all emoticons of keyboard height */
        EmojiUtil.createEmojiPopup(this, findViewById(R.id.rootView), mStatusBodyField);
        width = AppConstant.getDisplayMetricsWidth(mContext);

    }

    /**
     * Method to show banner's list in status box.
     */
    private void showBanner() {
        bannerView.setVisibility(View.VISIBLE);
        bannerArray = (JSONArray) STATUS_POST_OPTIONS.get(ConstantVariables.BANNER_DECORATION);

        if (bannerArray != null && bannerArray.length() > 0) {
            bannerTextSize = GlobalFunctions.getFontSizeFromPixel(mContext, BANNER_TEXT_SIZE);
            int selectedBanner = -1;
            for (int i = 0; i < bannerArray.length(); i++) {
                JSONObject bannerObject = bannerArray.optJSONObject(i);
                if (mEditBannerUrl != null && mEditBannerUrl.equals(bannerObject.optString("image"))) {
                    selectedBanner = i;
                }
                mBrowseList.add(new BrowseListItems(bannerObject.optString("feed_banner_url"), bannerObject));
            }

            if (mFeedList == null || mEditBannerUrl == null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("background-color", "#ffffff");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mBrowseList.add(0, new BrowseListItems(null, jsonObject));
            }

            // When editing feed's banner is not list down in banner's list then adding it into the list.
            if (selectedBanner < 0 && mEditBannerUrl != null && mFeedList != null
                    && mFeedList.getBannerObject() != null) {
                mBrowseList.add(new BrowseListItems(mFeedList.getBannerObject().optString("feed_banner_url"),
                        mFeedList.getBannerObject()));
                selectedBanner = mBrowseList.size() - 1;
            }
            mBannerAdapter = new BannerPhotoRecyclerAdapter(mContext, mBrowseList, (view, position) -> {

                scrollToBottom();

                if (position == 0 && (mFeedList == null || mEditBannerUrl == null)) {
                    isBannerSelected = false;
                    isAttachmentAttached = false;
                    mStatusBodyField.setGravity(Gravity.START);
                    mBodyView.setMinimumHeight(0);
                    mBodyView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

                    if (mStatusBodyField.getText() != null
                            && mStatusBodyField.getText().length() > decorationCharacterLength) {
                        setDecorationOnStatusBody(mContext.getResources().getDimension(R.dimen.title_medium_font_size), true);
                    } else {
                        setDecorationOnStatusBody(fontSize, false);
                    }

                } else {
                    BrowseListItems browseListItems = mBrowseList.get(position);
                    bannerObject = browseListItems.getBannerObject();
                    bannerFontColor = bannerObject.optString("color");

                    isBannerSelected = true;
                    isAttachmentAttached = true;
                    mStatusBodyField.setGravity(Gravity.CENTER);
                    mStatusBodyField.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
                    mBodyView.setMinimumHeight(mContext.getResources().getDimensionPixelSize(R.dimen.app_bar_height));
                    setDecorationOnStatusBody(bannerTextSize,
                            (mStatusBodyField.getText().length() > feedDecoration.optInt("banner_feed_length")));

                    if (browseListItems.getmBrowseImgUrl() != null
                            && CacheUtils.getInstance(mContext).getLru().get(browseListItems.getmBrowseImgUrl()) != null) {
                        mBannerDrawable = new BitmapDrawable(mContext.getResources(),
                                CacheUtils.getInstance(mContext).getLru().get(browseListItems.getmBrowseImgUrl()));
                        mBodyView.setBackground(mBannerDrawable);

                    } else if (browseListItems.getmBrowseImgUrl() != null && !browseListItems.getmBrowseImgUrl().isEmpty()) {
                        new BitMapCreatorUtil(mContext, browseListItems.getmBrowseImgUrl(),
                                bitmap -> {
                                    mBannerDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                    mBodyView.setBackground(mBannerDrawable);
                                }).execute();

                    } else if (bannerObject.optString("background-color") != null
                            && CacheUtils.getInstance(mContext).getLru()
                            .get(bannerObject.optString("background-color")) != null) {
                        mBannerDrawable = null;
                        try {
                            mBodyView.setBackgroundColor(Color.parseColor(bannerObject.optString("background-color")));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                    scrollToBottom();
                }

                mStatusBodyField.setText(mStatusBodyField.getText());
                mStatusBodyField.setSelection(mStatusBodyField != null ? mStatusBodyField.getText().length() : 0);


            });
            mBannerRecyclerView.setAdapter(mBannerAdapter);
            mBannerAdapter.notifyDataSetChanged();

            // When the page is called for feed editing and there is a banner in feed.
            if (mEditBannerUrl != null && selectedBanner >= 0) {
                mBannerAdapter.setEditingBanner(selectedBanner);
                mBannerRecyclerView.smoothScrollToPosition(selectedBanner);
            }

            nestedViewParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.padding_100dp);
            mNestedScrollView.setLayoutParams(nestedViewParams);
        }
    }

    /**
     * Method to scroll the nested scroll view to bottom of the page.
     */
    private void scrollToBottom() {
        findViewById(R.id.rootView).post(() -> {
            //call smooth scroll
            mNestedScrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    /**
     * Method to display/hide banner view when anyother attachment is selected.
     *
     * @param isNeedToShow True, if need to display banner view.
     */
    private void showHideBanner(boolean isNeedToShow) {
        bannerView.setVisibility(bannerArray != null && bannerArray.length() > 0 && isNeedToShow ? View.VISIBLE : View.GONE);
        if (bannerView.getVisibility() == View.GONE) {
            nestedViewParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.home_icon_tab_height);
            mNestedScrollView.setLayoutParams(nestedViewParams);
        } else {
            nestedViewParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.padding_100dp);
            mNestedScrollView.setLayoutParams(nestedViewParams);
        }
    }

    /**
     * Method to set decoration values on status body field.
     */
    private void setFeedDecorationValues() {
        feedDecoration = (JSONObject) STATUS_POST_OPTIONS.get(ConstantVariables.FEED_DECORATION);
        if (feedDecoration != null && feedDecoration.length() > 0) {
            decorationCharacterLength = feedDecoration.optInt("char_length");
            fontSize = GlobalFunctions.getFontSizeFromPixel(mContext, feedDecoration.optInt("font_size"));
            fontColor = feedDecoration.optString("font_color");
            fontStyle = feedDecoration.optString("font_style");

            if (mStatusBodyField.getText() != null
                    && mStatusBodyField.getText().length() > decorationCharacterLength) {
                setDecorationOnStatusBody(mContext.getResources().getDimension(R.dimen.title_medium_font_size), true);
            } else {
                setDecorationOnStatusBody(fontSize, false);
            }
        }
    }

    /**
     * Method to set status edit text's font style according to character length.
     *
     * @param fontSize     Font size which needs to be set on text.
     * @param isNormalText True, if no styling need to be shown on text.
     */
    private void setDecorationOnStatusBody(float fontSize, boolean isNormalText) {

        mStatusBodyField.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        mStatusBodyField.setEmojiconSize((int) mStatusBodyField.getTextSize());

        if (!isNormalText && !isBannerSelected && fontStyle != null && fontStyle.equals("italic")) {
            mStatusBodyField.setTypeface(null, Typeface.ITALIC);
        }

        int color;
        if (isNormalText) {
            mStatusBodyField.setTypeface(null, Typeface.NORMAL);
            mStatusBodyField.setTextColor(ContextCompat.getColor(mContext, R.color.gray_text_color));

            // Checking if the normal text is shown when the banner is selected.
            if (isBannerSelected) {
                mBodyView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                mBodyView.setMinimumHeight(0);
                mStatusBodyField.setGravity(Gravity.START);
                mStatusBodyField.setHintTextColor(ContextCompat.getColor(mContext, R.color.gray_text_color));
                isBannerSet = false;
                bannerView.setVisibility(View.GONE);
                isAttachmentAttached = false;
            }
        } else {
            try {
                /*Getting the color from decoration setting.*/
                try {
                    color = Color.parseColor(isBannerSelected && bannerFontColor != null ? bannerFontColor : fontColor);
                } catch (IllegalArgumentException e) {
                    color = ContextCompat.getColor(mContext, R.color.gray_text_color);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                //Reset the EditText field to default
                color = ContextCompat.getColor(mContext, R.color.gray_text_color);
            }
            mStatusBodyField.setTextColor(color);
            mStatusBodyField.setHintTextColor(color);

            // Checking if banner is selected and text length falls under its length limit then showing it again.
            if (isBannerSelected && !isBannerSet) {
                isBannerSet = true;
                isAttachmentAttached = true;
                if (mBannerDrawable != null) {
                    mBodyView.setBackground(mBannerDrawable);
                } else if (bannerObject.optString("background-color") != null
                        && CacheUtils.getInstance(mContext).getLru()
                        .get(bannerObject.optString("background-color")) != null) {
                    try {
                        mBodyView.setBackgroundColor(Color.parseColor(bannerObject.optString("background-color")));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
                mStatusBodyField.setGravity(Gravity.CENTER);
                mStatusBodyField.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
                mBodyView.setMinimumHeight(mContext.getResources().getDimensionPixelSize(R.dimen.app_bar_height));
                scrollToBottom();

                bannerView.setVisibility(View.VISIBLE);
            }
        }

        if (mAttachType != null && mAttachType.equals("sticker")) {
            mStatusBodyField.setGravity(Gravity.CENTER);
        }

    }

    /**
     * Method to set default privacy option when status page opened up.
     */
    private void setPrivacyOption(boolean isOptionChanged) {
        switch (mSelectedPrivacyKey) {
            case "everyone":
                setUserPrivacy("\uf0ac", mSelectedPrivacyKey);
                break;
            case "networks":
                setUserPrivacy("\uf0c0", mSelectedPrivacyKey);
                break;
            case "friends":
                setUserPrivacy("\uf007", mSelectedPrivacyKey);
                break;
            case "onlyme":
                setUserPrivacy("\uf023", mSelectedPrivacyKey);
                break;
            case "network_list_custom":
            case "friend_list_custom":
                if (isOptionChanged) {
                    getPrivacyForm(mSelectedPrivacyKey.equals("friend_list_custom"), mSelectedPrivacyKey);
                    mSelectedPrivacyKey = !PreferencesUtils.getStatusPrivacyKey(mContext).equals("network_list_custom")
                            && !PreferencesUtils.getStatusPrivacyKey(mContext).equals("friend_list_custom")
                            ? PreferencesUtils.getStatusPrivacyKey(mContext) : null;
                } else {
                    setUserPrivacy("\uf013", mSelectedPrivacyKey);
                    PreferencesUtils.setStatusPrivacyKey(mContext, mSelectedPrivacyKey);
                    mSelectedPrivacyKey = null;
                }
                break;
            default:
                if (mSelectedPrivacyKey.contains("network")) {
                    setUserPrivacy("\uf0c0", mSelectedPrivacyKey);
                } else if (mSelectedPrivacyKey.matches("-?\\d+(\\.\\d+)?")) {
                    setUserPrivacy("\uf03a", mSelectedPrivacyKey);
                } else {
                    setUserPrivacy("\uf007", mSelectedPrivacyKey);
                }
                break;
        }

        if (mSelectedPrivacyKey != null && !mSelectedPrivacyKey.equals("network_list_custom")
                && !mSelectedPrivacyKey.equals("friend_list_custom")) {
            PreferencesUtils.setStatusPrivacyKey(mContext, mSelectedPrivacyKey);
            if (mMultiSelectUserPrivacy != null) {
                mMultiSelectUserPrivacy.clear();
            }
        }
    }

    /**
     * Method to set privacy option when user changes the privacy.
     *
     * @param icon Icon of the privacy.
     * @param key  Key, from which label needs to be fetched.
     */
    private void setUserPrivacy(String icon, String key) {
        tvPrivacy.setText(Html.fromHtml(icon + " " + mUserPrivacyObject.optString(key)));
    }

    /**
     * Method to set default privacy key.
     */
    private void setDefaultPrivacyKey() {
        mSelectedPrivacyKey = !PreferencesUtils.getStatusPrivacyKey(mContext).equals("network_list_custom")
                && !PreferencesUtils.getStatusPrivacyKey(mContext).equals("friend_list_custom")
                ? PreferencesUtils.getStatusPrivacyKey(mContext) : "everyone";
        setUserPrivacy("\uf0ac", mSelectedPrivacyKey);
    }

    /**
     * Method to initialize Bottom Sheet for attachment options.
     */
    private void initializeBottomSheet() {
        persistentBottomSheet = clBottom.findViewById(R.id.bottomsheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(persistentBottomSheet);
        if (mBottomSheetBehavior != null) {
            if (mShowPhotoBlock || mOpenVideoUpload) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                persistentBottomSheet.findViewById(R.id.attachment).setVisibility(View.VISIBLE);
            } else {
                persistentBottomSheet.findViewById(R.id.attachment).setVisibility(View.GONE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

        TextView tvPhoto = persistentBottomSheet.findViewById(R.id.photo);
        TextView tvCheckIn = persistentBottomSheet.findViewById(R.id.checkin);
        TextView tvEmoticons = persistentBottomSheet.findViewById(R.id.emoticons);
        TextView tvTagFriends = persistentBottomSheet.findViewById(R.id.tag_friends);
        tvPhoto.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvCheckIn.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvEmoticons.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvTagFriends.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        tvPhoto.setText("\uF030");
        tvCheckIn.setText("\uF041");
        tvEmoticons.setText("\uF118");
        tvTagFriends.setText("\uF234");

        persistentBottomSheet.findViewById(R.id.attachment).setOnClickListener(view -> {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                persistentBottomSheet.findViewById(R.id.attachment).setVisibility(View.GONE);
                persistentBottomSheet.bringToFront();
                mAppConst.hideKeyboard();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final RecyclerView recyclerView = persistentBottomSheet.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        final List<BrowseListItems> mBrowseList = new ArrayList<>();
        postAdapter = new StatusPostRecyclerViewAdapter(mContext, mBrowseList, (view, position) -> {
            BrowseListItems listItem = mBrowseList.get(position);

            switch (listItem.getKey()) {
                case "facebook":
                    if (!isFacebookPost) {
                        callbackManager = CallbackManager.Factory.create();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
                    }
                    isFacebookPost = !isFacebookPost;
                    listItem.setAlreadyAdded(isFacebookPost);
                    postAdapter.notifyItemChanged(position);
                    break;

                case "twitter":
                    isTwitterPost = !isTwitterPost;
                    listItem.setAlreadyAdded(isTwitterPost);
                    postAdapter.notifyItemChanged(position);
                    break;

                case "tag":
                    Intent addPeopleIntent = new Intent(Status.this, AddPeople.class);

                    Set<String> keySet = selectedFriends.keySet();
                    Bundle bundle = new Bundle();

                    for (String key : keySet) {
                        String value = selectedFriends.get(key);
                        bundle.putString(key, value);
                    }

                    bundle.putInt(ConstantVariables.SUBJECT_ID, mSubjectId);
                    bundle.putString(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                    addPeopleIntent.putExtras(bundle);
                    startActivityForResult(addPeopleIntent, ConstantVariables.ADD_PEOPLE_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case "emoticons":
                    /* To show Emoji Keyboard */
                    EmojiUtil.showEmojiKeyboard(mContext, mStatusBodyField);
                    break;

                case "checkin":
                    if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        mAppConst.requestForManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                ConstantVariables.ACCESS_FINE_LOCATION);
                    } else {
                        addLocation(false);
                    }
                    break;

                case "feeling":
                    launchFeelingActivity();
                    break;

                case "photo":
                case "video":
                case "music":
                case "link":
                case "sticker":
                case "sell_something":
                    checkForAttachmentOptions(listItem.getKey());
                    break;
            }

            if (!listItem.getKey().equals("facebook") && !listItem.getKey().equals("twitter")
                    && !listItem.getKey().equals("sticker")
                    && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

        });

        // Adding attachment options.
        if (getResources().getString(R.string.facebook_post).equals("1")) {
            mBrowseList.add(new BrowseListItems("f082",
                    ContextCompat.getColor(mContext, R.color.facebook_color), "facebook",
                    mContext.getResources().getString(R.string.com_facebook_loginview_log_in_button)));
        }
        if (getResources().getString(R.string.twitter_post).equals("1")) {
            mBrowseList.add(new BrowseListItems("f081",
                    ContextCompat.getColor(mContext, R.color.twitter_color), "twitter",
                    mContext.getResources().getString(R.string.twitter)));
        }

        if (mFeedPostMenus.optInt("withtags") == 1) {
            mBrowseList.add(new BrowseListItems("f234",
                    ContextCompat.getColor(mContext, R.color.tag_color), "tag",
                    mContext.getResources().getString(R.string.tag_friends)));
        }

        if (mFeedPostMenus.optInt("emotions") == 1) {
            mBrowseList.add(new BrowseListItems("f118",
                    ContextCompat.getColor(mContext, R.color.emoticons_color), "emoticons",
                    mContext.getResources().getString(R.string.emoticons)));
        }
        if (mFeedPostMenus.optInt("emotions") != 1) {
            mStatusBodyField.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }

        if (mIsStickerEnabled) {
            mBrowseList.add(new BrowseListItems("f113",
                    ContextCompat.getColor(mContext, R.color.sticker_color), "sticker",
                    mContext.getResources().getString(R.string.sticker_post)));
            findViewById(R.id.iv_cancel_sticker).setOnClickListener(this);

            mAppConst.getJsonResponseFromUrl(UrlUtil.AAF_VIEW_STICKERS_URL, new OnResponseListener() {

                @Override
                public void onTaskCompleted(JSONObject jsonObject) {

                    if (jsonObject != null) {
                        mStickersPopup = StickersUtil.createStickerPopup(mContext,
                                findViewById(R.id.rootView),
                                mStickersParentView, mStatusBodyField, persistentBottomSheet,
                                mBottomSheetBehavior, llSticker, jsonObject);
                        mStickersPopup.setOnStickerClickedListener(stickerItem -> {
                            persistentBottomSheet.setVisibility(View.VISIBLE);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            mAttachType = "sticker";
                            showHideBanner(false);
                            mStickerGuid = stickerItem.getmStickerGuid();
                            mStickerImage = stickerItem.getmGridViewImageUrl();
                            if (stickerItem.getmGridViewImageUrl() != null
                                    && !stickerItem.getmGridViewImageUrl().isEmpty()) {
                                findViewById(R.id.sticker_view).setVisibility(View.VISIBLE);
                                mStatusBodyField.setGravity(Gravity.CENTER);
                                mImageLoader.setAlbumPhoto(stickerItem.getmGridViewImageUrl(), ivSticker);
                                StickersUtil.showStickerView();
                            } else {
                                findViewById(R.id.sticker_view).setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                }
            });
        }

        if (mFeedPostMenus.optInt("allowfeelingActivity") == 1) {
            mBrowseList.add(new BrowseListItems("f0f5",
                    ContextCompat.getColor(mContext, R.color.feeling_color), "feeling",
                    mContext.getResources().getString(R.string.feeling_activity)));
        }


        if (mFeedPostMenus.optInt("checkin") == 1 && !getResources().getString(R.string.places_api_key).isEmpty()) {
            mBrowseList.add(new BrowseListItems("f041",
                    ContextCompat.getColor(mContext, R.color.checkin_color), "checkin",
                    mContext.getResources().getString(R.string.checkIn)));
        }

        if (mFeedPostMenus.optInt("photo") == 1) {
            mBrowseList.add(new BrowseListItems("f030",
                    ContextCompat.getColor(mContext, R.color.photo_color), "photo",
                    mContext.getResources().getString(R.string.photos)));
        }

        if (mFeedPostMenus.optInt("video") == 1) {
            mBrowseList.add(new BrowseListItems("f03d",
                    ContextCompat.getColor(mContext, R.color.video_color), "video",
                    mContext.getResources().getString(R.string.video)));
        }

        if (mFeedPostMenus.optInt("music") == 1) {
            mBrowseList.add(new BrowseListItems("f001",
                    ContextCompat.getColor(mContext, R.color.music_color), "music",
                    mContext.getResources().getString(R.string.action_bar_title_music)));
        }

        if (mFeedPostMenus.optInt("link") == 1) {
            mStatusBodyField.addTextChangedListener(this);
            mBrowseList.add(new BrowseListItems("f0c1",
                    ContextCompat.getColor(mContext, R.color.link_color), "link",
                    mContext.getResources().getString(R.string.add_link_text)));
        }
        
        if (mFeedPostMenus.optInt("allowAdvertize") == 1) {
            sellSomethingFormValues = new JSONObject();
            mStatusBodyField.addTextChangedListener(this);
            mBrowseList.add(new BrowseListItems("f02c",
                    ContextCompat.getColor(mContext, R.color.sell_something_color), "sell_something",
                    mContext.getResources().getString(R.string.sell_something)));
        }

        recyclerView.setAdapter(postAdapter);
        recyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
        postAdapter.notifyDataSetChanged();

        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    //showing the different states
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            mAppConst.hideKeyboard();
                            persistentBottomSheet.findViewById(R.id.attachment).setVisibility(View.GONE);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            persistentBottomSheet.findViewById(R.id.attachment).setVisibility(View.VISIBLE);
                            recyclerView.smoothScrollToPosition(0);
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // React to dragging events
                    if (slideOffset > 0) {
                        persistentBottomSheet.findViewById(R.id.attachment).setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    /**
     * Method to check attachement options.
     *
     * @param key Name of the attached opiton.
     */
    private void checkForAttachmentOptions(String key) {
        if (!isAttachmentAttached && mAttachType.isEmpty()
                || ((mAttachType.equals("photo") || mAttachType.equals("sticker")) && key.equals(mAttachType))) {
            switch (key) {
                case "photo":
                    mUploadingOption = "photo";
                    if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    } else {
                        addPhotoBlock(false);
                    }
                    break;

                case "video":
                    mUploadingOption = "video";
                    if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    } else {
                        attachVideo();
                    }
                    break;

                case "music":
                    mUploadingOption = "music";
                    /* Check if permission is granted or not */
                    if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    } else {
                        //Visible music block and disable other attachments until music option is not canceled.
                        GlobalFunctions.addMusicBlock(Status.this);
                    }
                    break;

                case "link":
                    addLinkBlock();
                    mEnterLinkText.setVisibility(View.VISIBLE);
                    mEnterLinkText.setText("");
                    mAttachLinkButton.setVisibility(View.VISIBLE);
                    mEnterLinkText.setOnTouchListener((view, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            EmojiUtil.dismissEmojiPopup();
                        }
                        return false;
                    });

                    mAttachLinkButton.setOnClickListener(view -> {

                        String text = mEnterLinkText.getText().toString().trim();
                        attachLink(text);
                    });
                    break;

                case "sticker":
                    if (mStickersPopup != null) {
                        StickersUtil.showStickerView();
                        persistentBottomSheet.setVisibility(View.GONE);
                    }
                    break;

                case "sell_something":
                    launchFormForStatusPostOption("sellingForm",
                            mContext.getResources().getString(R.string.sell_something), sellSomethingFormValues);
                    break;
            }
        } else if (key.equals("sticker")
                && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    /**
     * Method to launch Feeling activity to show feelings.
     */
    private void launchFeelingActivity() {
        Intent intent = new Intent(mContext, FeelingActivity.class);
        startActivityForResult(intent, ConstantVariables.FEELING_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Method to set feeling info with user name.
     *
     * @param feelingBitmap Feeling icon's bitmap which will be shown with text.
     */
    private void setFeeling(Bitmap feelingBitmap) {

        if (feelingBitmap != null) {
            tvUserTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_text_color));
            tvUserTitle.setMovementMethod(new LinkMovementMethod());
            final int start = userName.length() + 3;
            String title = userName + " -   " + mFeelingActivityBundle.getString("parenttitle") + " ";
            final SpannableStringBuilder ssb = new SpannableStringBuilder(title + mFeelingActivityBundle.getString("childtitle") + "  ");

            // User title's part.
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {

                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                }
            }, 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Feeling icon.
            Drawable feelingDrawable = new BitmapDrawable(mContext.getResources(), feelingBitmap);
            feelingDrawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp));
            ssb.setSpan(new ImageSpan(feelingDrawable), start, start + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    launchFeelingActivity();
                }
            }, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Making parent title clickable.
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    launchFeelingActivity();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.gray_text_color));
                }
            }, start + 1, start + 1 + mFeelingActivityBundle.getString("parenttitle").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Making child label highlighted.
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    launchFeelingActivity();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                }
            }, title.length(), ssb.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Cancel icon.
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_grey);
            if (drawable != null) {
                drawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp), mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp));
                ssb.setSpan(new ImageSpan(drawable), ssb.length() - 1, ssb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    tvUserTitle.setText(userName);
                    tvUserTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    mFeelingActivityBundle = null;
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.white));
                }
            }, ssb.length() - 2, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvUserTitle.setText(ssb, TextView.BufferType.SPANNABLE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && mBottomSheetBehavior != null
                && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            Rect outRect = new Rect();
            persistentBottomSheet.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mAppConst.showKeyboard();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        menu.findItem(R.id.submit).setTitle(mContext.getResources().getString(R.string.post_status_button_text));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                return true;

            case R.id.submit:
                String statusBodyText = mStatusBodyField.getText().toString();

                //Checking and allow to post only if post is not empty when there is no attachment.
                //If there is any attachment then allow to post.
                if (((statusBodyText.length() > 0 && !statusBodyText.trim().isEmpty()) || isEditRequest) || !mAttachType.isEmpty()
                        || !selectedLocation.isEmpty()
                        || (mFeelingActivityBundle != null && mFeelingActivityBundle.size() > 0)) {
                    mAppConst.hideKeyboard();
                    String url;
                    List<String> enabledModuleList = null;
                    if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
                        enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
                    }
                    if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                            && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains("sitevideo")) {
                        url = AppConstant.DEFAULT_URL + "advancedvideos/create?post_attach=1";
                    } else {
                        url = AppConstant.DEFAULT_URL + "videos/create?post_attach=1";
                    }
                    mStatusPostUrl = ( mSelectedVideoPath != null ) ? url : AppConstant.DEFAULT_URL + "advancedactivity/feeds/post";
                    Log.d("LoggedmStatusPostUrl ", mStatusPostUrl);
                    if (mSubjectType != null && !mSubjectType.isEmpty() && mSubjectId != 0) {
                        mStatusPostUrl += "?subject_id=" + mSubjectId + "&subject_type=" + mSubjectType;
                    }

                    if (mFeedList != null && mFeedList.getmMenuUrl() != null) {
                        mStatusPostUrl = mFeedList.getmMenuUrl();
                    }

                    if (isFacebookPost) {
                        UpdateStatusOnFacebook();
                    } else {

                        uploadFilesAndData();
                    }


                } else {
                    SnackbarUtils.displaySnackbar(findViewById(R.id.rootView),
                            getResources().getString(R.string.status_empty_msg));
                }

        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadFilesAndData() {
        Log.d("UploadingHere ", "true");
        // Uploading files in background with the status post.
        mPostAttachmentParams = getAttachmentPostParams(new HashMap<>());
        if (isExternalShare || (mPostAttachmentParams != null && mPostAttachmentParams.get("schedule_time") != null) || mFeedList != null) {
            Log.d("UploadingHere 2 ", "true");
           new UploadAttachmentUtil(Status.this, mStatusPostUrl,
                   mPostAttachmentParams,
                   mSelectPath).execute();
       } else {
            Log.d("UploadingHere 4 ", "true");
           Intent intent = new Intent();
           intent.putExtra("mSelectPath", mSelectPath);
           intent.putExtra("mStatusPostUrl", mStatusPostUrl);
           intent.putExtra("postParam", getAttachmentPostParams(new HashMap<>()));
           setResult(ConstantVariables.POSTED_NEW_FEED, intent);
           finish();
       }



    }

    private void UpdateStatusOnFacebook() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(Status.this, Arrays.asList("publish_actions"));

        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        fbAccessToken = loginResult.getAccessToken().getToken();
                        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                fb_uid = object.optString("id");

                                String updateStatusUrl;
                                HashMap<String, String> fbPostParams = new HashMap<>();

                                // TODO
                                if (mAttachType.equals("video")) {
                                    updateStatusUrl = "https://graph.facebook.com/" + fb_uid + "/feed?access_token=" + fbAccessToken;
                                    fbPostParams.put("message", videoAttachedUrl);
                                } else if (mSelectPath != null && !mSelectPath.isEmpty()) {
                                    fbPostParams.put("photo", mSelectPath.get(0));
                                    fbPostParams.put("caption", mStatusBodyField.getText().toString().trim());
                                    updateStatusUrl = "https://graph.facebook.com/" + fb_uid + "/photos?access_token=" + fbAccessToken;
                                } else {
                                    updateStatusUrl = "https://graph.facebook.com/" + fb_uid + "/feed?access_token=" + fbAccessToken;
                                    fbPostParams.put("message", mStatusBodyField.getText().toString().trim());
                                    if (selectedLocation != null) {
                                        fbPostParams.put("place", selectedLocation.toString());
                                        if (selectedFriends != null && selectedFriends.size() != 0) {
                                            fbPostParams.put("tags", selectedFriends.toString());
                                        }
                                    }
                                    if (uriText != null) {
                                        fbPostParams.put("link", uriText);
                                    }
                                }

                                new UploadAttachmentUtil(Status.this, updateStatusUrl, fbPostParams, mSelectPath, isFacebookPost).execute();

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        uploadFilesAndData();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        uploadFilesAndData();
                    }
                });

    }

    @Override
    public void onClick(final View view) {

        int id = view.getId();

        switch (id) {

            case R.id.post_privacy:
                showPopup(view);
                break;

            // Remove selected check-in location.
            case R.id.cancel:
                selectedLocation.clear();
                mLocationsLayout.setVisibility(View.GONE);
                break;

            case R.id.iv_cancel_sticker:
                findViewById(R.id.sticker_view).setVisibility(View.GONE);
                mAttachType = "";
                mStatusBodyField.setGravity(Gravity.START);
                showHideBanner(true);
                break;

            case R.id.post_schedule:
                launchFormForStatusPostOption("scheduleForm",
                        mContext.getResources().getString(R.string.post_schedule), schedulePostFormValues);
                break;

            case R.id.target_audience:
                launchFormForStatusPostOption("targetForm",
                        mContext.getResources().getString(R.string.target_audience), targetAudienceFormValues);
                break;

            case R.id.edit:
                launchFormForStatusPostOption("sellingForm",
                        mContext.getResources().getString(R.string.sell_something), sellSomethingFormValues);
                break;

            case R.id.cancel_attachment:
                // When cancel the attachment option then setting the default text color.
                // and setting the attach type to null value & enable other attachment options.
                mAddVideoLayout.setVisibility(View.GONE);
                mAttachType = "";
                isAttachmentAttached = false;
                mSelectedVideoPath = null;
                mVideoId = 0;
                mSelectedVideoThumbnail = null;
                mVideoPreviewLayout.setVisibility(View.GONE);

                mStatusBodyField.setVisibility(View.VISIBLE);
                mSellSomethingPhotosRecyclerView.setVisibility(View.GONE);
                sellSomethingFormValues = new JSONObject();
                if (mSelectPath != null) {
                    mSelectPath.clear();
                }
                showHideBanner(true);
                break;

            case R.id.banner_indicator:
                ivBannerIndicator.setVisibility(View.INVISIBLE);
                showAnimationOnBannerViewHideShow(mBannerRecyclerView.getVisibility() == View.VISIBLE);
                break;
        }
    }

    /**
     * Method to show animation when banner show/hide button clicked.
     *
     * @param isBannerShown True if banner is already shown.
     */
    private void showAnimationOnBannerViewHideShow(final boolean isBannerShown) {
        float startX, endX;
        if (isBannerShown) {
            startX = 0;
            endX = -mBannerRecyclerView.getWidth();
            ivBannerIndicator.setImageResource(R.drawable.ic_banner_selected);
            ivBannerIndicator.setColorFilter(null);
            ivBannerIndicator.setBackground(null);
        } else {
            startX = -mBannerRecyclerView.getWidth();
            endX = 0;
            ivBannerIndicator.setImageResource(R.drawable.ic_chevron_left);
            ivBannerIndicator.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                    PorterDuff.Mode.SRC_ATOP);
            ivBannerIndicator.setBackground(bannerIndicatorShape);
        }
        TranslateAnimation animate = new TranslateAnimation(startX, endX, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(!isBannerShown);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBannerRecyclerView.setVisibility(isBannerShown ? View.GONE : View.VISIBLE);
                ivBannerIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBannerRecyclerView.startAnimation(animate);

    }

    /**
     * Method to launch EditEntry (Form) to display the the form view.
     *
     * @param formModule Form Module which needs to be displayed.
     * @param title      Title of the page.
     * @param formValue  FormValue object if values are selected already.
     */
    private void launchFormForStatusPostOption(String formModule, String title, JSONObject formValue) {
        Intent intent = new Intent(mContext, EditEntry.class);
        intent.putExtra("isStatusPage", true);
        intent.putExtra(ConstantVariables.FORM_TYPE, formModule);
        intent.putExtra(ConstantVariables.CONTENT_TITLE, title);
        intent.putExtra(ConstantVariables.URL_STRING, AppConstant.DEFAULT_URL + "advancedactivity/feelings/get-status-form");
        if (FORM_OBJECT != null && FORM_OBJECT.length() > 0) {
            try {
                if (formValue != null && formValue.length() > 0) {
                    FORM_OBJECT.put("formValues", formValue);
                } else {
                    FORM_OBJECT.remove("formValues");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mFeedList != null && formModule.equals("scheduleForm")
                && schedulePostFormValues != null && schedulePostFormValues.length() > 0) {
            intent.putExtra("form_value", schedulePostFormValues.toString());
        }
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT, mSelectPath);
        }
        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.HOME_MENU_TITLE);
        startActivityForResult(intent, ConstantVariables.STATUS_FORM_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Method to show Schedule post icon as selected/unselected according to selected value.
     */
    private void setScheduleSelected() {
        if (schedulePostFormValues != null) {
            int color;
            if (schedulePostFormValues.length() > 0) {
                color = R.color.themeButtonColor;
            } else {
                color = R.color.gray_text_color;
            }
            tvPostSchedule.setTextColor(ContextCompat.getColor(mContext, color));
            StateListDrawable drawable = (StateListDrawable) tvPostSchedule.getBackground();
            DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
            if (dcs != null) {
                Drawable[] drawableItems = dcs.getChildren();
                GradientDrawable gradientDrawableChecked = (GradientDrawable) drawableItems[0];
                GradientDrawable gradientDrawableUnChecked = (GradientDrawable) drawableItems[1];
                gradientDrawableChecked.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height),
                        ContextCompat.getColor(mContext, color));
                gradientDrawableUnChecked.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height),
                        ContextCompat.getColor(mContext, color));
            }
        }
    }

    /**
     * Method to show Target Audience icon as selected/unselected according to selected value.
     */
    private void setTargetAudienceSelected() {
        if (targetAudienceFormValues != null) {
            int color;
            if (targetAudienceFormValues.length() > 0) {
                color = R.color.themeButtonColor;
            } else {
                color = R.color.gray_text_color;
            }
            tvTargetAudience.setTextColor(ContextCompat.getColor(mContext, color));
            StateListDrawable drawable = (StateListDrawable) tvTargetAudience.getBackground();
            DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
            if (dcs != null) {
                Drawable[] drawableItems = dcs.getChildren();
                GradientDrawable gradientDrawableChecked = (GradientDrawable) drawableItems[0];
                GradientDrawable gradientDrawableUnChecked = (GradientDrawable) drawableItems[1];
                gradientDrawableChecked.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height),
                        ContextCompat.getColor(mContext, color));
                gradientDrawableUnChecked.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height),
                        ContextCompat.getColor(mContext, color));
            }
        }
    }

    /**
     * Method to show preview of sell something.
     */
    private void setSellSomethingData() {
        if (sellSomethingFormValues != null && sellSomethingFormValues.length() > 0) {
            isAttachmentAttached = true;
            mAttachType = "sell";
            showHideBanner(false);

            // Making view's visible accordingly.
            mStatusBodyField.setVisibility(View.GONE);
            mStatusBodyField.setText("");
            mAddVideoLayout.setVisibility(View.VISIBLE);
            mVideoPreviewLayout.setVisibility(View.GONE);
            titleViewParam.setMargins(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            tvPrice.setVisibility(View.VISIBLE);
            tvLocation.setVisibility(View.VISIBLE);
            tvEdit.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvVideoAddedMsg.setVisibility(View.GONE);
            mVideoAttachmentIcon.setVisibility(View.GONE);

            mAddVideoText.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            mVideoAttachmentTitle.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            tvLocation.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            tvEdit.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            tvCancel.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

            mAddVideoText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.body_medium_font_size));
            mVideoAttachmentTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.body_medium_font_size));
            mVideoAttachmentTitle.setText(sellSomethingFormValues.optString("title"));

            if (sellSomethingFormValues.optString("description") != null
                    && !sellSomethingFormValues.optString("description").isEmpty()) {
                mVideoAttachmentBody.setVisibility(View.VISIBLE);
                mVideoAttachmentBody.setMaxLines(Integer.MAX_VALUE);
                try {
                    if (sellSomethingFormValues.optString("description").trim().length()
                            > ConstantVariables.FEED_TITLE_BODY_LENGTH) {
                        mVideoAttachmentBody.setText(Html.fromHtml(sellSomethingFormValues.optString("description").substring(0, 150)
                                + "<font color=\"#a9a9a9\">" + " ..."
                                + mContext.getResources().getString(R.string.readMore) + " </font>"));
                        mVideoAttachmentBody.setOnClickListener(v -> {
                            if (isFullTextShowing) {
                                mVideoAttachmentBody.setText(Html.fromHtml(sellSomethingFormValues.optString("description").substring(0, 150)
                                        + "<font color=\"#a9a9a9\">" + " ..."
                                        + mContext.getResources().getString(R.string.readMore) + " </font>"));
                            } else {
                                mVideoAttachmentBody.setText(Html.fromHtml(sellSomethingFormValues.optString("description")
                                        + "<font color=\"#a9a9a9\">" + " ..."
                                        + mContext.getResources().getString(R.string.readLess) + " </font>"));
                            }
                            isFullTextShowing = !isFullTextShowing;
                        });
                    } else {
                        mVideoAttachmentBody.setText(sellSomethingFormValues.optString("description"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mVideoAttachmentBody.setVisibility(View.GONE);
            }

            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            tvPrice.setText("\uf155  " + sellSomethingFormValues.optString("currency")
                    + " " + sellSomethingFormValues.optDouble("price"));
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText("\uf041  " + sellSomethingFormValues.optString("location"));

            tvEdit.setText("\uf044 ");
            tvCancel.setText("\uf00d ");
            mAddVideoText.setText(" \uf02c " + getResources().getString(R.string.sell_something));
            tvEdit.setOnClickListener(this);
            tvCancel.setOnClickListener(this);

            if (mSelectPath != null && mSelectPath.size() > 0) {
                mSellSomethingPhotosRecyclerView.setVisibility(View.VISIBLE);
                new LoadImageAsync(mSellSomethingPhotosRecyclerView, false).execute();
            } else {
                mSellSomethingPhotosRecyclerView.setVisibility(View.GONE);
            }

        } else {
            mStatusBodyField.setVisibility(View.VISIBLE);
            mAddVideoLayout.setVisibility(View.GONE);
        }

    }

    private void addVideo() {
        String url;
        List<String> enabledModuleList = null;
        if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
            enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
        }
        if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains("sitevideo")) {
            url = AppConstant.DEFAULT_URL + "advancedvideos/create?post_attach=1";
        } else {
            url = AppConstant.DEFAULT_URL + "videos/create?post_attach=1";
        }
        Intent attachVideoIntent = new Intent(Status.this, CreateNewEntry.class);
        attachVideoIntent.putExtra(ConstantVariables.ATTACH_VIDEO, "attach_video");
        attachVideoIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "home");
        attachVideoIntent.putExtra(ConstantVariables.CREATE_URL, url);
        startActivityForResult(attachVideoIntent, ConstantVariables.REQUEST_VIDEO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void attachVideo() {
        Intent intent = new Intent(mContext, MultiMediaSelectorActivity.class);
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, false);
        // Selection type video to display items in grid
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_VIDEO);
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT, ConstantVariables.STORY_POST_COUNT_LIMIT);

        // Select mode
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE, MultiMediaSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, ConstantVariables.REQUEST_VIDEO_CODE);

    }

    public HashMap<String, String> getAttachmentPostParams(HashMap<String, String> postParams) {

        // Add Tagged Friends in post params
        if (selectedFriends != null && selectedFriends.size() != 0) {
            String mToValues = "";
            int j = 0;
            Set<String> mKeySet = selectedFriends.keySet();
            for (String key : mKeySet) {
                j++;
                if (j < selectedFriends.size()) {
                    mToValues += key + ",";
                } else {
                    mToValues += key;
                }
            }
            postParams.put("toValues", mToValues);
        }

        // Adding loaction params
        postParams.put("locationLibrary", "client");

        // Adding status body.
        postParams.put("body", Smileys.getEmojiFromString(mStatusBodyField.getText().toString().trim()));


        // Adding post privacy option.
        if (mMultiSelectUserPrivacy != null && !mMultiSelectUserPrivacy.isEmpty()) {
            for (Map.Entry<String, String> entry : mMultiSelectUserPrivacy.entrySet()) {
                if (entry.getValue().equals("1")) {
                    if (mSelectedPrivacyKey != null) {
                        mSelectedPrivacyKey += entry.getKey() + ",";
                    } else {
                        mSelectedPrivacyKey = entry.getKey() + ",";
                    }
                }
            }
            mSelectedPrivacyKey = mSelectedPrivacyKey.substring(0, mSelectedPrivacyKey.lastIndexOf(","));

        } else if (mSelectedPrivacyKey == null) {
            mSelectedPrivacyKey = "everyone";
        }
        postParams.put("auth_view", mSelectedPrivacyKey);

        // Adding composer values.
        JSONObject composerObject = new JSONObject();

        if (tagObject != null && tagObject.length() > 0) {
            try {
                composerObject.put("tag", tagObject.optString("tag"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Adding check-in option in params
        if (selectedLocation != null && selectedLocation.size() != 0) {
            try {
                composerObject.put("checkin", mSelectedLocationObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (isBannerSelected && isBannerSet && bannerObject != null && bannerObject.length() > 0) {
            try {
                composerObject.put("banner", bannerObject);
                postParams.put("post_attach", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Putting feeling/activity params if its selected.
        if (mFeelingActivityBundle != null && mFeelingActivityBundle.size() > 0) {
            JSONObject feelingObject = new JSONObject();
            try {
                feelingObject.put("parent", mFeelingActivityBundle.getString("parent_id"));
                feelingObject.put("child", mFeelingActivityBundle.getString("child_id"));
                feelingObject.put("type", mFeelingActivityBundle.getString("type"));
                feelingObject.put("childtitle", mFeelingActivityBundle.getString("childtitle"));
                composerObject.put("feeling", feelingObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Adding composer in param if there is any tag, checkin or banner.
        if (composerObject.length() > 0) {
            postParams.put("composer", composerObject.toString());
        }

        // Putting post schedule param if its selected.
        if (schedulePostFormValues != null && schedulePostFormValues.length() > 0) {
            Iterator<String> keySet = schedulePostFormValues.keys();
            while (keySet.hasNext()) {
                String key = keySet.next();
                postParams.put(key, schedulePostFormValues.optString(key));
            }
        }

        // Putting target audience param if its selected.
        if (targetAudienceFormValues != null && targetAudienceFormValues.length() > 0) {
            Iterator<String> keySet = targetAudienceFormValues.keys();
            while (keySet.hasNext()) {
                String key = keySet.next();
                postParams.put(key, targetAudienceFormValues.optString(key));
            }
        }

        // Adding attachment info if any.
        if (mAttachType != null && !mAttachType.isEmpty()) {
            postParams = UploadAttachmentUtil.getAttachmentPostParams(postParams, mAttachType,
                    uriText, mStickerGuid, mStickerImage, mSongId, mVideoId, sellSomethingFormValues);
        }

        if(mSelectedVideoPath != null){
            postParams.put("mVideoPath", mSelectedVideoPath);
            postParams.put("mVideoThumbnail", mSelectedVideoThumbnail);
        }
        return postParams;
    }

    /**
     * Method to show link block when user click on link icon.
     */
    public void addLinkBlock() {

        //Visible link block and disable other attachments until link option is not canceled.
        mAddLinkLayout.setVisibility(View.VISIBLE);
        isAttachmentAttached = true;

        String addLinkText = getResources().getString(R.string.add_link_text) + " " +
                "(" + getResources().getString(R.string.cancel_attachment) + ")";
        int i1 = addLinkText.indexOf("(");
        int i2 = addLinkText.indexOf(")");

        mAddLinkText.setMovementMethod(LinkMovementMethod.getInstance());
        mAddLinkText.setText(addLinkText, TextView.BufferType.SPANNABLE);
        Spannable photoTextSpannable = (Spannable) mAddLinkText.getText();
        ClickableSpan myClickSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                // When cancel the link attachment option then setting the default text color.
                // and setting the attach type to null value & enable other attachment options.
                mAttachType = "";
                isAttachmentAttached = false;
                mAddLinkLayout.setVisibility(View.GONE);
                mLinkAttachmentBlock.setVisibility(View.GONE);
                showHideBanner(true);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }
        };

        photoTextSpannable.setSpan(myClickSpan, i1 + 1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }


    // Get image from bitmap for real path
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (inImage != null) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            if (path != null && !path.isEmpty()) {
                return Uri.parse(path);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Method to show video attachment.
     *
     * @param mVideoTitle       title of video attachment.
     * @param mVideoImage       image of video attachment.
     * @param mVideoDescription description of video attachment.
     */
    public void showAttachedVideo(String mVideoTitle, String mVideoImage,
                                  String mVideoDescription) {

        if (mSelectedVideoPath != null) {
            mImageLoader.setImageUrl(mSelectedVideoThumbnail, mVideoAttachmentIcon);
        } else {
            mImageLoader.setImageUrl(mVideoImage, mVideoAttachmentIcon);
        }
        mVideoPreviewLayout.setVisibility(View.VISIBLE);
        mVideoAttachmentTitle.setTypeface(null);
        mVideoAttachmentTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.body_default_font_size));
        mAddVideoText.setTypeface(null);
        mAddVideoText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.body_default_font_size));
        mVideoAttachmentTitle.setText(Html.fromHtml(mVideoTitle));
        mVideoAttachmentBody.setMaxLines(3);
        mVideoAttachmentBody.setOnClickListener(null);
        mVideoAttachmentBody.setText(Html.fromHtml(mVideoDescription));

        titleViewParam.setMargins(0, 0, 0, 0);
        mAddVideoLayout.setVisibility(View.VISIBLE);
        tvVideoAddedMsg.setVisibility(View.VISIBLE);
        mVideoAttachmentBody.setVisibility(View.VISIBLE);
        mVideoAttachmentIcon.setVisibility(View.VISIBLE);
        tvPrice.setVisibility(View.GONE);
        tvLocation.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        mAttachType = "video";
        isAttachmentAttached = true;
        showHideBanner(false);

        String addVideoText = getResources().getString(R.string.add_video_text) + " " +
                "(" + getResources().getString(R.string.cancel_attachment) + ")";
        int index1 = addVideoText.indexOf("(");
        int index2 = addVideoText.indexOf(")");

        mAddVideoText.setMovementMethod(LinkMovementMethod.getInstance());
        mAddVideoText.setText(addVideoText, TextView.BufferType.SPANNABLE);
        Spannable mAddVideoTextSpannable = (Spannable) mAddVideoText.getText();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                // When cancel the video attachment option then setting the default text color.
                // and setting the attach type to null value & enable other attachment options.
                mAddVideoLayout.setVisibility(View.GONE);
                mAttachType = "";
                isAttachmentAttached = false;
                showHideBanner(true);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }
        };

        mAddVideoTextSpannable.setSpan(clickableSpan, index1 + 1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void showPopup(View v) {

        PopupMenu popup = new PopupMenu(mContext, v);


        if (mUserPrivacyObject != null && mUserPrivacyObject.length() != 0) {
            mPrivacyKeys = mUserPrivacyObject.names();
            popupMenuList.clear();

            for (int i = 0; i < mUserPrivacyObject.length(); i++) {
                String key = mPrivacyKeys.optString(i);
                popupMenuList.add(key);
                String privacyLabel = mUserPrivacyObject.optString(key);
                if (mSelectedPrivacyKey != null && mSelectedPrivacyKey.equals(key)) {
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(true).setChecked(true);
                } else if (mSelectedPrivacyKey == null && key.equals("everyone")
                        && (mMultiSelectUserPrivacy == null || mMultiSelectUserPrivacy.isEmpty())) {
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(true).setChecked(true);
                } else {
                    boolean isSelected = (mMultiSelectUserPrivacy != null && mMultiSelectUserPrivacy.size() > 0)
                            && mMultiSelectUserPrivacy.get(key) != null && mMultiSelectUserPrivacy.get(key).equals("1");
                    popup.getMenu().add(Menu.NONE, i, Menu.NONE, privacyLabel).setCheckable(isSelected).setChecked(isSelected);
                }
            }
        }

        if (mUserListObject != null && mUserListObject.length() != 0) {
            mPrivacyKeys = mUserListObject.names();

            for (int i = 0; i < mUserListObject.length(); i++) {
                String key = mPrivacyKeys.optString(i);
                popupMenuList.add(key);
                String privacyLabel = mUserListObject.optString(key);
                if (mSelectedPrivacyKey != null && mSelectedPrivacyKey.equals(key)) {
                    popup.getMenu().add(Menu.NONE, i + mUserPrivacyObject.length(), Menu.NONE,
                            privacyLabel).setCheckable(true).setChecked(true);
                } else {
                    popup.getMenu().add(Menu.NONE, i + mUserPrivacyObject.length(), Menu.NONE, privacyLabel);
                }
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            mSelectedPrivacyKey = popupMenuList.get(id);

            // Clearing list when any other popup option(other than multiple friend/network list) is clicked.
            if (!mSelectedPrivacyKey.equals("network_list_custom")
                    && !mSelectedPrivacyKey.equals("friend_list_custom")) {
                mMultiSelectUserPrivacy.clear();
            }

            setPrivacyOption(true);
            return true;
        });
        popup.show();
    }

    /**
     * Method to launch Form Creation activity for multiple friend/network list.
     *
     * @param isFriendList True if the form is to be load for friend list.
     * @param key          Key of the selected privacy option.
     */
    private void getPrivacyForm(boolean isFriendList, String key) {
        Intent intent = new Intent(mContext, CreateNewEntry.class);
        intent.putExtra("is_status_privacy", true);
        intent.putExtra("isFriendList", isFriendList);
        intent.putExtra("privacy_key", key);
        intent.putExtra("user_id", userId);
        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.HOME_MENU_TITLE);
        intent.putExtra(ConstantVariables.CONTENT_TITLE, mUserPrivacyObject.optString(key));
        startActivityForResult(intent, ConstantVariables.USER_PRIVACY_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        // Hiding emoji popup when the status page is back pressed.
        EmojiUtil.dismissEmojiPopup();
        super.onBackPressed();
        mAppConst.hideKeyboard();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (checkInLocationDialog != null && checkInLocationDialog.isShowing()){
            checkInLocationDialog.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {

            case ConstantVariables.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {

                    if (mSelectPath != null) {
                        mSelectPath.clear();
                    }
                    // Getting image path from uploaded images.
                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                    //Checking if there is any image or not.
                    if (mSelectPath != null) {
                        new LoadImageAsync(mPhotosRecyclerView, true).execute();
                    }

                } else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
                    isAttachmentAttached = false;
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.image_capturing_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User cancel the process
                    /**
                     * Finish this activity if Photo Option get clicked from Main Feed page
                     * And if user press back button on photoUploading, so as to show Feedpage again
                     */
                    if (data != null && data.hasExtra(MultiMediaSelectorActivity.OPEN_PHOTO_BLOCK)) {
                        boolean isOpenPhotoBlock = data.getExtras().getBoolean(MultiMediaSelectorActivity.
                                OPEN_PHOTO_BLOCK, false);
                        if (isOpenPhotoBlock) {
                            finish();
                        }
                    } else if (mSelectPath == null || mSelectPath.isEmpty()) {
                        mPhotosRecyclerView.setVisibility(View.GONE);
                        mAttachType = "";
                        isAttachmentAttached = false;
                        showHideBanner(true);
                    }
                }
                break;

            case ConstantVariables.REQUEST_MUSIC:
                if (resultCode == RESULT_OK && data != null) {

                    Uri selectedMusicUri = data.getData();
                    if (selectedMusicUri != null) {

                        // getting real path of music file
                        mSelectedMusicFile = GlobalFunctions.getMusicFilePathFromURI(this, selectedMusicUri);
                        mAttachType = "music";
                        mAttachPostUrl = AppConstant.DEFAULT_URL + "music/playlist/add-song";
                        // Getting attachment details.
                        new UploadAttachmentUtil(Status.this, mAttachPostUrl, mSelectedMusicFile).execute();
                    }

                } else if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.music_capturing_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User cancel the process
                    if (mSelectedMusicFile == null || mSelectedMusicFile.isEmpty()) {
                        mAttachType = "";
                        showHideBanner(true);
                    }
                }
                break;

            // When video is uploaded from my device option then loading aaf.
            case ConstantVariables.REQUEST_VIDEO:
                if (resultCode == ConstantVariables.CREATE_REQUEST_CODE) {
                    isPosted = true;
                    Intent intent = new Intent();
                    intent.putExtra("isPosted", isPosted);
                    setResult(ConstantVariables.FEED_REQUEST_CODE, intent);
                    finish();
                } else if (resultCode == 0 && mOpenVideoUpload) {
                    finish();
                }
                break;

            case ConstantVariables.STICKER_STORE_REQUEST:
                if (resultCode == ConstantVariables.STICKER_STORE_REQUEST) {
                    JSONObject jsonObject = ConstantVariables.STICKERS_STORE_ARRAY;
                    JSONArray collectionIds = jsonObject.names();
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject stickerStoreObject = jsonObject.optJSONObject(collectionIds.optString(i));
                        String stickerAction = stickerStoreObject.optString("action");
                        int collectionId = stickerStoreObject.optInt("collection_id");

                        if (stickerAction.equals("add")) {
                            StickersGridView stickersGridView = new StickersGridView(mContext, collectionId, mStickersPopup);
                            mStickersPopup.viewsList.add(stickersGridView);
                            mStickersPopup.stickerGridViewList.put(collectionId, stickersGridView);
                            ((PagerAdapter) mStickersPopup.myAdapter).notifyDataSetChanged();
                            mStickersPopup.mCollectionsList.put(stickerStoreObject);
                            mStickersPopup.setupTabIcons();
                        } else {
                            mStickersPopup.viewsList.remove(mStickersPopup.stickerGridViewList.get(collectionId));
                            mStickersPopup.stickerGridViewList.remove(collectionId);
                            ((PagerAdapter) mStickersPopup.myAdapter).notifyDataSetChanged();
                            for (int j = 0; j < mStickersPopup.mCollectionsList.length(); j++) {
                                JSONObject collectionObject = mStickersPopup.mCollectionsList.optJSONObject(j);
                                int collection_id = collectionObject.optInt("collection_id");

                                if (collection_id == collectionId) {
                                    mStickersPopup.mCollectionsList.remove(j);
                                    mStickersPopup.viewPager.setCurrentItem(0);
                                    break;
                                }
                            }
                            mStickersPopup.setupTabIcons();
                        }
                    }
                    ConstantVariables.STICKERS_STORE_ARRAY = new JSONObject();
                }
                break;
            case ConstantVariables.REQUEST_VIDEO_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedVideoPath = data.getStringExtra(MultiMediaSelectorActivity.VIDEO_RESULT);
                    attachVideoPreview();
                }
                break;
        }

        Bundle bundle = null;

        if (data != null) {
            bundle = data.getExtras();
        }

        // Check Result from Add People and Add Location Activities
        switch (resultCode) {

            case ConstantVariables.ADD_PEOPLE_CODE:
                Set<String> searchArgumentSet = null;
                if (bundle != null) {
                    searchArgumentSet = bundle.keySet();
                }
                selectedFriends.clear();
                mTaggedFriendsLayout.removeAllViews();
                // Add Text "with" if some friends are being tagged
                if (searchArgumentSet != null && searchArgumentSet.size() != 0) {
                    LinearLayout.LayoutParams layoutParams = CustomViews.getWrapLayoutParams();
                    layoutParams.setMargins(5, 5, 5, 0);
                    TextView tagTextView = new TextView(mContext);
                    tagTextView.setText(" -  " + getResources().getString(R.string.with_text) + "  ");
                    TextViewCompat.setTextAppearance(tagTextView, R.style.CaptionView);
                    tagTextView.setLayoutParams(layoutParams);
                    tagTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_text_color));
                    mTaggedFriendsLayout.addView(tagTextView);

                    CustomViews.setmOnCancelClickListener(this);
                    for (String key : searchArgumentSet) {
                        String value = bundle.getString(key);
                        selectedFriends.put(key, value);
                        selectedFriends = CustomViews.createSelectedUserLayout(mContext, Integer.parseInt(key), value,
                                mTaggedFriendsLayout, selectedFriends, 1);
                    }
                }
                break;

            case ConstantVariables.REQUEST_VIDEO:

                if (bundle != null) {
                    mOpenVideoUpload = false;
                    // Getting video attachment info.
                    mVideoId = bundle.getInt(ConstantVariables.CONTENT_ID);
                    String mVideoTitle = bundle.getString(ConstantVariables.CONTENT_TITLE);
                    if (mVideoId != 0 && mVideoTitle != null) {
                        String mVideoDescription = bundle.getString(ConstantVariables.DESCRIPTION);
                        String mVideoImage = bundle.getString(ConstantVariables.IMAGE);
                        if (bundle.containsKey(ConstantVariables.VIDEO_URL))
                            videoAttachedUrl = bundle.getString(ConstantVariables.VIDEO_URL);
                        showAttachedVideo(mVideoTitle, mVideoImage, mVideoDescription);
                    }
                }
                break;

            case ConstantVariables.STATUS_FORM_CODE:

                if (bundle != null) {
                    HashMap<String, String> param;
                    param = (HashMap<String, String>) bundle.getSerializable("param");
                    String formModule = bundle.getString(ConstantVariables.FORM_TYPE);
                    if (data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT) != null) {
                        if (mSelectPath != null) {
                            mSelectPath.clear();
                        }
                        // Getting image path from uploaded images.
                        mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                    } else if (formModule.equals("sellingForm") && mSelectPath != null) {
                        mSelectPath.clear();
                    }

                    checkForFormOptions(param, formModule);
                }
                break;

            case ConstantVariables.CHILD_FEELING_REQUEST_CODE:
                if (bundle != null && requestCode == ConstantVariables.FEELING_ACTIVITY_REQUEST_CODE) {
                    mFeelingActivityBundle = bundle;
                    final String feelingIcon = mFeelingActivityBundle.getString("photo");
                    if (feelingIcon != null
                            && CacheUtils.getInstance(mContext).getLru().get(feelingIcon) != null) {
                        setFeeling(CacheUtils.getInstance(mContext).getLru().get(feelingIcon));
                    } else if (feelingIcon != null) {
                        new BitMapCreatorUtil(mContext, feelingIcon, bitmap -> setFeeling(CacheUtils.getInstance(mContext).getLru().get(feelingIcon))).execute();
                    }
                }
                break;

            case ConstantVariables.USER_PRIVACY_REQUEST_CODE:
                if (bundle != null && requestCode == ConstantVariables.USER_PRIVACY_REQUEST_CODE
                        && bundle.getSerializable("param") != null) {
                    mMultiSelectUserPrivacy = (HashMap<String, String>) bundle.getSerializable("param");

                    if (bundle.getString("feed_post_menu") != null
                            && bundle.getString("feed_post_menu").length() > 0
                            && bundle.getString("privacy_key").equals("friend_list_custom")) {
                        try {
                            mFeedPostMenus = new JSONObject(bundle.getString("feed_post_menu"));
                            mUserPrivacyObject = mFeedPostMenus.optJSONObject("userprivacy");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mMultiSelectUserPrivacy != null && mMultiSelectUserPrivacy.size() > 0) {
                        boolean isAnyOptionSelected = false;
                        for (Map.Entry<String, String> entry : mMultiSelectUserPrivacy.entrySet()) {
                            if (entry.getValue().equals("1")) {
                                isAnyOptionSelected = true;
                                break;
                            }
                        }

                        // When there is any option is selected then showing the name of multi select list.
                        if (isAnyOptionSelected) {
                            mSelectedPrivacyKey = null;
                            setUserPrivacy("\uf013", bundle.getString("privacy_key"));
                            String multiOptions = null;
                            for (Map.Entry<String, String> entry : mMultiSelectUserPrivacy.entrySet()) {
                                if (entry.getValue().equals("1")) {
                                    if (multiOptions != null) {
                                        multiOptions += entry.getKey() + ",";
                                    } else {
                                        multiOptions = entry.getKey() + ",";
                                    }
                                }
                            }
                            if (multiOptions != null) {
                                multiOptions = multiOptions.substring(0, multiOptions.lastIndexOf(","));
                                PreferencesUtils.setStatusPrivacyKey(mContext, bundle.getString("privacy_key"));
                                PreferencesUtils.setStatusPrivacyMultiOptions(mContext, multiOptions);
                            }
                        } else {
                            mSelectedPrivacyKey = !PreferencesUtils.getStatusPrivacyKey(mContext).equals("network_list_custom")
                                    && !PreferencesUtils.getStatusPrivacyKey(mContext).equals("friend_list_custom")
                                    ? PreferencesUtils.getStatusPrivacyKey(mContext) : "everyone";
                            mMultiSelectUserPrivacy.clear();
                            PreferencesUtils.setStatusPrivacyKey(mContext, mSelectedPrivacyKey);
                            setUserPrivacy("\uf0ac", mSelectedPrivacyKey);
                        }
                    } else {
                        setDefaultPrivacyKey();
                    }
                } else {
                    mMultiSelectUserPrivacy.clear();
                    setDefaultPrivacyKey();
                }
                break;
        }

        if (isFacebookPost) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void attachVideoPreview() {
        if (mSelectedVideoPath == null) {
            return;
        }
        File file = new File(mSelectedVideoPath);
        long length = file.length();
        length = (int)length/(1024 * 1024);
        String message = ConstantVariables.VALID_FILE_SIZE;
        if ((message = GlobalFunctions.validateFileSize(length, mContext)).equals(ConstantVariables.VALID_FILE_SIZE)) {
            mAppConst.showProgressDialog();
            mImageLoader.getBitMapFromUrl(mSelectedVideoPath, bitmap -> {
                try {
                    File outputDir = mContext.getCacheDir();
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fileName = "Image-" + n ;
                    File outputFile = File.createTempFile(fileName, ".png", outputDir);
                    FileOutputStream out = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    mSelectedVideoThumbnail = outputFile.getAbsolutePath();
                    showAttachedVideo(GlobalFunctions.getFileNameFromPath(mSelectedVideoPath), "", "");
                    mAppConst.hideProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            mSelectedVideoPath = null;
            SnackbarUtils.displayMultiLineSnackbarWithAction(mContext, findViewById(R.id.rootView), message, mContext.getResources().
                    getString(R.string.try_again), this::attachVideo);
        }
    }

    /**
     * Method to check values for each form module.
     *
     * @param param      PostParam of current selected form.
     * @param formModule Form Module.
     */
    private void checkForFormOptions(HashMap<String, String> param, String formModule) {
        if (param != null && param.size() > 0) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                try {
                    switch (formModule) {
                        case "scheduleForm":
                            schedulePostFormValues.put(entry.getKey(), entry.getValue());
                            break;
                        case "targetForm":
                            targetAudienceFormValues.put(entry.getKey(), entry.getValue());
                            break;
                        case "sellingForm":
                            sellSomethingFormValues.put(entry.getKey(), entry.getValue());
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            switch (formModule) {
                case "scheduleForm":
                    schedulePostFormValues = new JSONObject();
                    break;
                case "targetForm":
                    targetAudienceFormValues = new JSONObject();
                    break;
                case "sellingForm":
                    sellSomethingFormValues = new JSONObject();
                    isAttachmentAttached = false;
                    mAttachType = "";
                    if (mSelectPath != null) {
                        mSelectPath.clear();
                    }
                    showHideBanner(true);
                    break;
            }
        }
        setScheduleSelected();
        setTargetAudienceSelected();
        setSellSomethingData();
    }

    // TODO in future
    public void createSelectedLocationLayout(String label) {

        mLocationLabel.setText(label);
        mLocationsLayout.setOnClickListener(view -> addLocation(false));
        mLocationsLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Method to start ImageUploadingActivity (MultiImageSelector)
     *
     * @param context      Context of Class.
     * @param selectedMode Selected mode i.e. multi images or single image.
     * @param showCamera   Whether to display the camera.
     * @param maxNum       Max number of images allowed to pick in case of MODE_MULTI.
     */
    public void startImageUploadActivity(Context context, int selectedMode, boolean showCamera, int maxNum,
                                         boolean isOpenPhotoBlock) {

        Intent intent;

        intent = new Intent(context, MultiMediaSelectorActivity.class);
        // Whether photoshoot
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // The maximum number of selectable image
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // Selection type photo to display items in grid
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
        // Select mode
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // The default selection
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiMediaSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        intent.putExtra(MultiMediaSelectorActivity.OPEN_PHOTO_BLOCK, isOpenPhotoBlock);
        ((Activity) context).startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        mStart = start;
        mCharSequence = charSequence;
        detectUri(charSequence, start, false);

        // Check user start typing text with @ or not if yes than show users search list for tag them in status post content
        if (charSequence != null && !charSequence.toString().trim().isEmpty() && charSequence.toString().contains("@")) {
            String chr = charSequence.toString().substring(charSequence.toString().indexOf("@"), charSequence.length());
            if (chr.length() > 1) {
                StringBuilder stringBuilder = new StringBuilder(chr);
                searchText = stringBuilder.deleteCharAt(0).toString();
                getFriendList(UrlUtil.GET_FRIENDS_LIST + "?search=" + searchText);
            } else {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        } else {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }


        // Status text conditions.
        if (feedDecoration != null && feedDecoration.length() > 0) {
            if (charSequence != null && charSequence.length() > (isBannerSelected ? feedDecoration.optInt("banner_feed_length") : decorationCharacterLength)) {
                setDecorationOnStatusBody(mContext.getResources().getDimension(R.dimen.title_medium_font_size), true);
            } else {
                setDecorationOnStatusBody(isBannerSelected ? bannerTextSize : fontSize, false);
            }
        }
    }

    private void detectUri(CharSequence charSequence, int start, boolean isPasted) {
        //Checking if there is no attachment already attached.
        if ((start > 0 || isPasted) && charSequence != null && charSequence.toString().trim().length() > 1 && !isAttachmentAttached && !isEditRequest) {
            //Getting all Url's of mStatusBodyField.
            Spannable mSpannableUrl = mStatusBodyField.getText();
            URLSpan[] mUrlSpans = mSpannableUrl.getSpans(0, mSpannableUrl.length(), URLSpan.class);

            char last = charSequence.charAt(charSequence.length() - 1);
            //Checking last entered character is space or not.
            if (' ' == last || isPasted) {
                statusBodyFieldUrlList.clear();
                for (URLSpan span : mUrlSpans) {
                    String url = span.getURL().replace("http://", "");
                    statusBodyFieldUrlList.put("", url);
                }
                // Checking map is not null & empty and checking url is valid or not.
                if (mUrlSpans.length >= 1 &&
                        statusBodyFieldUrlList != null && !statusBodyFieldUrlList.isEmpty() &&
                        GlobalFunctions.isValidUrl(statusBodyFieldUrlList.lastEntry().getValue())) {

                    addLinkBlock();
                    mEnterLinkText.setVisibility(View.GONE);
                    mEnterLinkText.setText(statusBodyFieldUrlList.lastEntry().getValue());
                    mAttachLinkButton.setVisibility(View.GONE);
                    mAttachPostUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/attach-link";
                    mAttachType = "link";
                    uriText = mEnterLinkText.getText().toString().trim();
                    mAppConst.hideKeyboard();
                    new UploadAttachmentUtil(Status.this, mAttachPostUrl, uriText, mAttachType).execute();
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // For detecting url's in edit text.
        Linkify.addLinks(s, Linkify.WEB_URLS);
    }

    public void getFriendList(String url) {
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject body) {
                if (body != null && body.length() != 0) {

                    mAddPeopleList.clear();
                    JSONArray guestListResponse = body.optJSONArray("response");

                    initFriendsListView(guestListResponse.length());

                    for (int i = 0; i < guestListResponse.length(); i++) {
                        JSONObject friendObject = guestListResponse.optJSONObject(i);
                        String username = friendObject.optString("label");
                        int userId = friendObject.optInt("id");
                        String userImage = friendObject.optString("image_icon");

                        mAddPeopleList.add(new AddPeopleList(userId, username, userImage));

                    }
                    mAddPeopleAdapter.notifyDataSetChanged();
                } else {
                    if (popupWindow != null && !popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });

    }

    // Create popup window for showing user search list
    private void initFriendsListView(int length) {

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }

        mUserListView = new ListView(mContext);
        mUserListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        mAddPeopleAdapter = new AddPeopleAdapter(this, R.layout.list_friends, mAddPeopleList);
        mUserListView.setAdapter(mAddPeopleAdapter);
        mUserListView.setOnItemClickListener(this);
        mUserListView.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.gray_light)));

        popupWindow = new PopupWindow(mUserListView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.gray_light)));
        if (length > 3) {
            popupWindow.setHeight(300);
        }
        popupWindow.showAsDropDown(mStatusBodyField);

    }

    @Override
    public void onAsyncSuccessResponse(JSONObject response, boolean isRequestSuccessful, boolean isAttachFileRequest) {
        try {
            LogUtils.LOGD(Status.class.getSimpleName(), "isRequestSuccessful: " + isRequestSuccessful + ", isAttachFileRequest: " + isAttachFileRequest + ", response: " + response);

            // If response is for post status and uploading files to server.
            if (!isAttachFileRequest) {
                if (isRequestSuccessful) {
                    isPosted = true;
                    Intent intent = new Intent();
                    if (isTwitterPost) {
                        intent.putExtra("isTwitterPost", true);
                        intent.putExtra("mStatusBodyText", mStatusBodyField.getText().toString().trim());
                        if (mSelectPath != null && !mSelectPath.isEmpty()) {
                            intent.putExtra("imagePath", mSelectPath.get(0));
                        } else if (videoAttachedUrl != null && !videoAttachedUrl.isEmpty()) {
                            intent.putExtra(ConstantVariables.VIDEO_URL, videoAttachedUrl);
                        }
                    } else {
                        intent.putExtra("isTwitterPost", false);
                    }

                    intent.putExtra("isPosted", isPosted);
                    intent.putExtra("isExternalShare", isExternalShare);
                    setResult(ConstantVariables.FEED_REQUEST_CODE, intent);
                    finish();
                } else {
                    isPosted = false;
                    SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.rootView),
                            response.optString("message"), this::finish);
                }

            } else {
                if (isRequestSuccessful) {
                    mBody = response.optJSONObject("body");
                    if (mBody == null) {
                        JSONArray mBodyArray = response.optJSONArray("body");
                        mBody = mAppConst.convertToJsonObject(mBodyArray);
                    }

                    showHideBanner(false);
                    switch (mAttachType) {
                        case "music":

                            mSongId = mBody.optInt("song_id");
                            String mSongTitle = mBody.optString("title");

                            //Showing uploaded music name and cancel option
                            musicAddedMessage.setVisibility(View.VISIBLE);
                            isAttachmentAttached = true;

                            String musicUploadMessage = getResources().
                                    getQuantityString(R.plurals.music_uploading_message_custom, 1, 1) +
                                    " : " + mSongTitle + " " + "(" + getResources().getString(R.string.cancel_attachment)
                                    + ")";
                            int index1 = musicUploadMessage.length() - 7;
                            int index2 = musicUploadMessage.length() - 1;

                            musicAddedMessage.setMovementMethod(LinkMovementMethod.getInstance());
                            musicAddedMessage.setText(musicUploadMessage, TextView.BufferType.SPANNABLE);
                            Spannable mAddVideoTextSpannable = (Spannable) musicAddedMessage.getText();
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {

                                    // When cancel the music option then setting the default text color.
                                    // and setting the attach type to null value & enable other attachment options.
                                    mAttachType = "";
                                    isAttachmentAttached = false;
                                    musicAddedMessage.setVisibility(View.GONE);
                                    mSelectedMusicFile = "";
                                    showHideBanner(true);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(false);
                                    ds.setColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                                }
                            };
                            mAddVideoTextSpannable.setSpan(clickableSpan, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            break;

                        case "link":
                            if (mBody.has("video_id")){
                                showAttachedVideo(mBody.optString("title"),mBody.optString("image"), mBody.optString("description"));
                                mAttachType = "video";
                                mVideoId = mBody.optInt("video_id");
                                mAddLinkLayout.setVisibility(View.GONE);
                            } else {
                                String url = mBody.optString("url");
                                String title = mBody.optString("title");
                                String description = mBody.optString("description");
                                String image = mBody.optString("thumb");
                                mLinkAttachmentUrl.setText(Html.fromHtml(url));
                                mLinkAttachmentTitle.setText(Html.fromHtml(title));

                                if(image != null && image.equals("null") && mBody.optJSONArray("images") != null) {
                                    image = mBody.optJSONArray("images").optString(0);
                                }

                                //Checking if image is coming in response or not.
                                // It will return value "null" in some case, so checking this condition also.
                                if (image != null && !image.isEmpty() && !image.equals("null")) {
                                    mLinkAttachmentImage.setVisibility(View.VISIBLE);
                                    mImageLoader.setImageUrl(image, mLinkAttachmentImage);
                                } else {
                                    mLinkAttachmentImage.setVisibility(View.GONE);
                                }

                                //Checking if description is coming in response or not.
                                // It will return value "null" in some case, so checking this condition also.
                                if (description != null && !description.equals("null")) {
                                    mLinkAttachmentDescription.setVisibility(View.VISIBLE);
                                    mLinkAttachmentDescription.setText(Html.fromHtml(description));
                                } else {
                                    mLinkAttachmentDescription.setVisibility(View.GONE);
                                }
                                mAddLinkLayout.setVisibility(View.VISIBLE);
                                mEnterLinkText.setVisibility(View.GONE);
                                mAttachLinkButton.setVisibility(View.GONE);
                                mLinkAttachmentBlock.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                } else {
                    //When error occured enable other attachments clickable.
                    mAddLinkLayout.setVisibility(View.GONE);
                    mAttachType = "";
                    isAttachmentAttached = false;
                    mSelectedMusicFile = "";
                    showHideBanner(true);
                    if (response != null) {
                        SnackbarUtils.displaySnackbar(findViewById(R.id.rootView),
                                response.optString("message"));
                    }
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*
        Method to show photo uploading block.
     */
    public void addPhotoBlock(boolean isOpenPhotoBlock) {
        startImageUploadActivity(mContext, MultiMediaSelectorActivity.MODE_MULTI, true, 10, isOpenPhotoBlock);
    }

    // ToDo in future
    public void addLocation(boolean isOpenCheckIn) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("openCheckIn", isOpenCheckIn);
        if (selectedLocation != null && selectedLocation.size() != 0) {
            bundle.putBundle("locationsBundle", mSelectedLocationBundle);
        }
        checkInLocationDialog = new CheckInLocationDialog(mContext, bundle);
        checkInLocationDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (checkInLocationDialog != null && checkInLocationDialog.isShowing()){
            checkInLocationDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted, proceed to the normal flow
                    if (mUploadingOption != null) {
                        switch (mUploadingOption){
                            case "photo":
                                addPhotoBlock(mShowPhotoBlock);
                                break;
                            case "music":
                                GlobalFunctions.addMusicBlock(Status.this);
                                break;
                            case "video":
                                attachVideo();
                                break;
                        }
                    } else {
                        if (isExternalShare) {
                            Intent intent = getIntent();
                            String action = intent.getAction();
                            String type = intent.getType();

                            if (action != null && type != null) {
                                switch (action) {
                                    case Intent.ACTION_SEND:
                                        if (type.startsWith("image/")) {
                                            handleSendImage(intent); // Handle single image being sent
                                        }
                                        break;
                                    case Intent.ACTION_SEND_MULTIPLE:
                                        if (type.startsWith("image/")) {
                                            handleSendMultipleImages(intent); // Handle multiple images being sent
                                        }
                                        break;
                                }
                            }

                        } else {
                            addPhotoBlock(mShowPhotoBlock);
                        }
                    }
                } else {
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, findViewById(R.id.rootView),
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

            case ConstantVariables.ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted, proceed to the normal flow
                    addLocation(mOpenCheckIn);

                } else {
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                ConstantVariables.ACCESS_FINE_LOCATION);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, findViewById(R.id.rootView),
                                ConstantVariables.ACCESS_FINE_LOCATION);

                    }
                }
                break;

        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            // Update UI to reflect text being shared
            Matcher m = Patterns.WEB_URL.matcher(sharedText);
            if (m.find()) {
                String url = m.group();
                attachLink(url);
            } else {
                mStatusBodyField.setText(sharedText);
            }
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Method to get the uri from bitmap
                Uri tempUri = getImageUri(mContext, bitmap);

                // get real path from uri and add it to mSelectPath
                if (tempUri != null) {
                    mSelectPath.add(getRealPathFromURI(tempUri));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (mSelectPath != null) {
                new LoadImageAsync(mPhotosRecyclerView, true).execute();
            }
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            for (int i = 0; i < imageUris.size(); i++) {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUris.get(i));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Method to get the uri from bitmap
                    Uri tempUri = getImageUri(mContext, bitmap);

                    // get real path from uri and add it to mSelectPath
                    if (tempUri != null) {
                        mSelectPath.add(getRealPathFromURI(tempUri));
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (mSelectPath != null) {
                new LoadImageAsync(mPhotosRecyclerView, true).execute();
            }
        }
    }

    /**
     * Method to retrieve the path of an image URI
     *
     * @param contentUri uri of image.
     * @return returns the real path of image.
     */
    private String getRealPathFromURI(Uri contentUri) {
        String result;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mContext, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    private void attachLink(String link) {
        // Hide Enter Link Text Block in case of External share.
        if (isExternalShare) {
            mEnterLinkText.setVisibility(View.GONE);
            mAttachLinkButton.setVisibility(View.GONE);
            mAddLinkText.setVisibility(View.GONE);
        }
        if (!link.isEmpty()) {
            //Checking if link is valid or not.
            if (GlobalFunctions.isValidUrl(link)) {
                mAttachPostUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/attach-link";
                mAttachType = "link";
                uriText = link;
                mAppConst.hideKeyboard();
                new UploadAttachmentUtil(Status.this, mAttachPostUrl, uriText, mAttachType).execute();
            } else {
                mEnterLinkText.setError(getResources().getString(R.string.url_not_valid));
            }

        } else {
            mEnterLinkText.setError(getResources().getString(R.string.empty_url_text));
        }
    }

    @Override
    public void onCheckInLocationChanged(Bundle data) {
        selectedLocation.clear();
        if (data != null && !data.isEmpty()) {
            mSelectedLocationBundle = data;
            String locationId = data.getString("placeId");
            String locationLabel = data.getString("locationLabel");
            try {
                mSelectedLocationObject = new JSONObject(data.getString("locationObject"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mLocationPrefix.setText(getResources().getString(R.string.at) + ":");
            selectedLocation.put(locationId, locationLabel);
            createSelectedLocationLayout(locationLabel);
        } else {
            mLocationsLayout.setVisibility(View.GONE);
            /* Finish this Activity
            *  if CheckIn Option Clicked from Main Feed
            *  and If no location is selected from dialog.
            * */
            if (mOpenCheckIn) {
                finish();
            }
        }
    }

    @Override
    public void onCancelButtonClicked(int userId) {

        View canceledView = mTaggedFriendsLayout.findViewById(userId);
        if (canceledView != null) {
            mTaggedFriendsLayout.removeView(canceledView);
            selectedFriends.remove(Integer.toString(userId));
        }

        if (selectedFriends == null || selectedFriends.size() == 0) {
            mTaggedFriendsLayout.setVisibility(View.GONE);
        }
    }

    public void onAsyncSuccessFacebookResponse(boolean isFacebookPost) {
        uploadFilesAndData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        popupWindow.dismiss();

        AddPeopleList addPeopleList = mAddPeopleList.get(position);
        String label = addPeopleList.getmUserLabel();
        int userId = addPeopleList.getmUserId();

        String string = "user_" + userId + "=" + label;

        if (tagString != null && tagString.length() > 0) {
            tagString = tagString + "&" + string;
        } else {
            tagString = string;
        }

        tagObject = new JSONObject();
        try {
            tagObject.put("tag", tagString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mAddPeopleAdapter != null) {
            mAddPeopleAdapter.clear();
        }
        mStatusBodyField.setText(mStatusBodyField.getText().toString().replace("@" + searchText, label));
        mStatusBodyField.setSelection(mStatusBodyField.getText().length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FORM_OBJECT = null;
        StickersUtil.isKeyboardOpen = false;
    }

    /**
     * Class to load the images in background thread
     * so that it will load the images in background and don't make the main thread slow.
     */
    public class LoadImageAsync extends AsyncTask<Void, String, Void> {

        private int columnWidth;
        private float padding;
        private boolean isPhotoPreview;
        private ProgressDialog mProgressDialog;
        private RecyclerView mRecyclerView;
        private List<ImageViewList> mPhotoUrls = new ArrayList<>();

        public LoadImageAsync(RecyclerView recyclerView, boolean isPhotoPreview) {
            this.mRecyclerView = recyclerView;
            this.isPhotoPreview = isPhotoPreview;
            padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    AppConstant.GRID_PADDING, getResources().getDisplayMetrics());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(getApplicationContext().getResources().
                    getString(R.string.loading_text));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> removeImageList = new ArrayList<>();

            for (int i = 0; i < mSelectPath.size(); i++) {
                // Getting Bitmap from its real path.
                Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(mContext, mSelectPath.get(i), width,
                        (int) getResources().getDimension(R.dimen.feed_attachment_image_height), false);
                if (bitmap != null) {
                    mPhotoUrls.add(new ImageViewList(bitmap));
                } else if (mSelectPath.get(i) != null) {
                    //When there are null images then add them in remove list.
                    removeImageList.add(mSelectPath.get(i));
                }
            }

            // Checking if there is any null images then remove them from the mSelectPath
            if (!removeImageList.isEmpty()) {
                for (String image : removeImageList) {
                    mSelectPath.remove(image);
                }
            }

            // Column width
            if (isPhotoPreview) {
                columnWidth = (int) ((mAppConst.getScreenWidth() - (11 * padding)) / (0.96));
            } else {
                if (mPhotoUrls.size() == 1) {
                    columnWidth = AppConstant.getDisplayMetricsWidth(mContext)
                            - mContext.getResources().getDimensionPixelSize(R.dimen.margin_60dp);
                } else {
                    columnWidth = (int) ((mAppConst.getScreenWidth() - (11 * padding)) / 1.58);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            mProgressDialog.dismiss();

            mCustomImageAdapter = new CustomImageAdapter(((Activity) mContext), mPhotoUrls, true, isPhotoPreview,
                    columnWidth, removedImagePosition -> {
                        if (mSelectPath != null && !mSelectPath.isEmpty()) {
                            mSelectPath.remove(removedImagePosition);
                            mPhotoUrls.remove(removedImagePosition);
                            if (!isPhotoPreview && mPhotoUrls.size() == 1) {
                                columnWidth = AppConstant.getDisplayMetricsWidth(mContext)
                                        - mContext.getResources().getDimensionPixelSize(R.dimen.margin_60dp);
                                mCustomImageAdapter.setColumnWidth(columnWidth);
                                mRecyclerView.setAdapter(mCustomImageAdapter);
                            }
                            mCustomImageAdapter.notifyDataSetChanged();

                            // If all images are removed then making other attachments clickable.
                            if (mSelectPath.isEmpty()) {
                                mRecyclerView.setVisibility(View.GONE);
                                // If canceled all the selected images then hide photoBlockLayout
                                // and enabled other attachement click.
                                if (isPhotoPreview) {
                                    mAttachType = "";
                                    isAttachmentAttached = false;
                                    showHideBanner(true);
                                }
                            }
                        }
                    });
            mRecyclerView.setAdapter(mCustomImageAdapter);
            mCustomImageAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);

            // Checking if there is any valid photo or not and make the attachment clickable accordingly.
            if (mSelectPath.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mAttachType = "";
                isAttachmentAttached = false;
                showHideBanner(true);
            } else {
                mAttachType = isPhotoPreview ? "photo" : "sell";
                isAttachmentAttached = true;
                showHideBanner(false);
            }
        }
    }
    private void copyInputStreamToFile( InputStream in, File file ) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
