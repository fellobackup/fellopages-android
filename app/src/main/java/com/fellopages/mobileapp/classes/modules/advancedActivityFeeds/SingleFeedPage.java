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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.adapters.FeedAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnFeedDisableCommentListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.FeedList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadAttachmentUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.likeNComment.CommentAdapter;
import com.fellopages.mobileapp.classes.modules.likeNComment.CommentList;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.stickers.StickersClickListener;
import com.fellopages.mobileapp.classes.modules.stickers.StickersGridView;
import com.fellopages.mobileapp.classes.modules.stickers.StickersPopup;
import com.fellopages.mobileapp.classes.modules.stickers.StickersUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SingleFeedPage extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        AbsListView.OnScrollListener, OnFeedDisableCommentListener, OnAsyncResponseListener,
        AdapterView.OnItemClickListener {

    public RelativeLayout mStickersParentView;
    Toolbar mToolBar;
    int mActionId, mGetTotalComments, pageNumber = 1, mFeedPosition = -1;
    ListView mCommentsListView;
    CommentAdapter mCommentAdapter;
    String mCommentListUrl, mSubjectType, mFeedsUrl, mCommentPostUrl, mCommentNotificationUrl,
            mActionTypeBody = null;
    AppConstant mAppConst;
    List<CommentList> mCommentListItems;
    CommentList mCommentList;
    RecyclerView mFeedsRecyclerView;
    FeedAdapter mFeedAdapter;
    List<Object> mFeedItemsList;
    JSONArray mFilterTabsArray, mDataJsonArray, mAllCommentsArray, mHashTagArray;
    JSONObject mFeedPostMenu;
    HashMap<String, String> mClickablePartsNew, mShareClickableParts;
    HashMap<Integer, String> mVideoInformation;
    LinearLayout mCommentPostBlock;
    TextView mCommentPostButton;
    EditText mCommentBox;
    boolean isLoading = false, isLoadingFeed = true, isCommentsLoading = false, mIsMultiPhotoFeed;
    String searchText;
    private HashMap<String, String> mWordStylingClickableParts;
    private ProgressBar mProgressBar;
    private View footerView;
    private FeedList mFeedList;
    private Context mContext;
    private Map<String, String> params;
    private double mLatitude, mLongitude;
    private String mPlaceId = null, mHashTagString = "", mLocationLabel;
    private JSONObject commentsObject;
    private int mReactionsEnabled, mStickersEnabled;
    private JSONObject mReactions;
    private StickersPopup mStickersPopup;
    private ArrayList<String> mSelectPath = new ArrayList<>();
    private String mAttachmentType;
    private int width, startIndex, endIndex;
    private RelativeLayout mSelectedImageBlock;
    private ImageView mSelectedImageView, mCancelImageView;
    private TextView mPhotoUploadingButton;
    private AlertDialogWithAction mAlertDialogWithAction;
    private AddPeopleAdapter mAddPeopleAdapter;
    private List<AddPeopleList> mAddPeopleList;
    private String tagString;
    private ListView mUserListView;
    private CardView mUserView;
    private JSONObject tagObject;
    private HashMap<String, String> mClickableParts;
    private JSONArray otherMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_feed_page);

        mAddPeopleList = new ArrayList<>();

        mContext = this;

        /* Set Toolbar */

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolBar);

        mCommentsListView = (ListView) findViewById(R.id.commentList);
        mCommentsListView.setOnScrollListener(this);

        LayoutInflater inflater = getLayoutInflater();
        View header = (View) inflater.inflate(R.layout.single_feed_page_recyclerview, null, false);
        mCommentsListView.addHeaderView(header);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mFeedsRecyclerView = (RecyclerView) findViewById(R.id.feedsGrid);
        mFeedsRecyclerView.setNestedScrollingEnabled(false);
        mFeedsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ((SimpleItemAnimator) mFeedsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);


        // For tag user in comments
        mUserListView = (ListView) findViewById(R.id.userList);
        mUserView = (CardView) findViewById(R.id.users_view);

        mCommentPostBlock = (LinearLayout) findViewById(R.id.commentPostBlock);
        mCommentBox = (EditText) findViewById(R.id.commentBox);
        mCommentBox.addTextChangedListener(this);

        mCommentPostButton = (TextView) findViewById(R.id.commentPostButton);
        mCommentPostButton.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        mCommentPostButton.setOnClickListener(this);

        mPhotoUploadingButton = (TextView) findViewById(R.id.photoUploadingButton);
        mPhotoUploadingButton.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        mPhotoUploadingButton.setText("\uf030");
        mPhotoUploadingButton.setOnClickListener(this);

        footerView = CustomViews.getFooterView(getLayoutInflater());


        mCommentPostUrl = AppConstant.DEFAULT_URL + "advancedactivity/comment";
        mCommentNotificationUrl = AppConstant.DEFAULT_URL + "advancedactivity/add-comment-notifications";

        /* Get Values from intent */
        if (getIntent().getExtras() != null) {
            mActionId = getIntent().getIntExtra(ConstantVariables.ACTION_ID, 0);
            mFeedPosition = getIntent().getIntExtra(ConstantVariables.ITEM_POSITION, 0);
            mFeedList = getIntent().getParcelableExtra(ConstantVariables.FEED_LIST);
            mReactionsEnabled = getIntent().getIntExtra("reactionEnabled", 0);
            mStickersEnabled = getIntent().getIntExtra("stickersEnabled",
                    PreferencesUtils.getStickersEnabled(mContext));
            mIsMultiPhotoFeed = getIntent().getBooleanExtra(ConstantVariables.IS_MULTI_PHOTO_FEED, false);

            if (getIntent().getStringExtra("reactions") != null) {
                try {
                    mReactions = new JSONObject(getIntent().getStringExtra("reactions"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mStickersParentView = (RelativeLayout) findViewById(R.id.stickersMainLayout);
        if (mStickersEnabled == 1) {
            mCommentPostButton.setText("\uf118");
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));
        } else {
            mCommentPostButton.setText("\uf1d8");
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.gray_stroke_color));
        }

        mAppConst = new AppConstant(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mFeedItemsList = new ArrayList<>();
        mCommentListItems = new ArrayList<>();
        mCommentList = new CommentList();

        width = AppConstant.getDisplayMetricsWidth(mContext);
        mSubjectType = "activity_action";

        mFeedAdapter = new FeedAdapter(this, R.layout.list_feeds, mFeedItemsList, true, mCommentBox,
                mSubjectType, mActionId, null, mFeedPosition, false, null);

        mFeedsRecyclerView.setAdapter(mFeedAdapter);
        mFeedAdapter.setOnFeedDisableCommentListener(SingleFeedPage.this);

        mFeedsUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds?limit=" + AppConstant.LIMIT +
                "&object_info=1&getAttachedImageDimention=0&action_id=" + mActionId
                + "&feed_filter=0&post_elements=0&post_menus=0";

        getFeeds(mFeedsUrl);

        if (!mIsMultiPhotoFeed) {
            mCommentListUrl = UrlUtil.AAF_LIKE_COMMNET_URL + "&action_id=" + mActionId + "&page=" + pageNumber;
            sendRequestToServer(mCommentListUrl);
        } else {
            mCommentsListView.setPadding(0, 0, 0, 0);
            mCommentPostBlock.setVisibility(View.GONE);
            findViewById(R.id.bottom_view).setVisibility(View.GONE);
        }

        mCommentAdapter = new CommentAdapter(this, R.layout.list_comment, mCommentListItems,
                mCommentList, true, mSubjectType, mActionId, mActionId);
        mCommentsListView.setAdapter(mCommentAdapter);

        mSelectedImageBlock = (RelativeLayout) findViewById(R.id.selectedImageBlock);
        mSelectedImageView = (ImageView) findViewById(R.id.imageView);
        mCancelImageView = (ImageView) findViewById(R.id.removeImageButton);
        Drawable addDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_cancel_black_24dp);
        addDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.black),
                PorterDuff.Mode.SRC_ATOP));
        mCancelImageView.setImageDrawable(addDrawable);

        PreferencesUtils.updateReactionsEnabledPref(mContext, mReactionsEnabled);
        PreferencesUtils.updateStickersEnabledPref(mContext, mStickersEnabled);
    }

    /**
     * Method to check image posting option according to emojiEnabled value.
     */
    public void checkImagePostingOption() {
        if (PreferencesUtils.getEmojiEnabled(mContext) == 1) {
            mPhotoUploadingButton.setVisibility(View.VISIBLE);
        } else {
            mPhotoUploadingButton.setVisibility(View.GONE);
            LinearLayout.LayoutParams commentBoxParams = (LinearLayout.LayoutParams) mCommentBox.getLayoutParams();
            commentBoxParams.weight = 0.9f;
            mCommentBox.setLayoutParams(commentBoxParams);
        }
    }

    public void getFeeds(final String url) {

        isLoadingFeed = true;

        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                if (mFeedItemsList != null) {
                    mFeedItemsList.clear();
                }

                mProgressBar.setVisibility(View.GONE);

                if (jsonObject != null && jsonObject.length() != 0) {

                    // Send Stickers Request
                    if (mStickersEnabled == 1 && mStickersPopup == null) {
                        mAppConst.getJsonResponseFromUrl(UrlUtil.AAF_VIEW_STICKERS_URL, new OnResponseListener() {

                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

                                if (jsonObject != null) {
                                    mStickersPopup = StickersUtil.createStickersPopup(mContext, findViewById(R.id.commentList),
                                            mStickersParentView, mCommentBox,
                                            jsonObject, mPhotoUploadingButton, mCommentPostButton);

                                    mStickersPopup.setOnStickerClickedListener(new StickersClickListener() {
                                        @Override
                                        public void onStickerClicked(ImageViewList stickerItem) {
                                            params = new HashMap<>();
                                            postComment(null, stickerItem.getmStickerGuid(),
                                                    stickerItem.getmGridViewImageUrl());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                            }
                        });
                    }

                    try {
                        mFilterTabsArray = jsonObject.optJSONArray("filterTabs");
                        mDataJsonArray = jsonObject.optJSONArray("data");
                        mFeedPostMenu = jsonObject.optJSONObject("feed_post_menu");
                        mReactionsEnabled = jsonObject.optInt("reactionsEnabled");
                        mStickersEnabled = jsonObject.optInt("stickersEnabled");
                        mReactions = jsonObject.optJSONObject("reactions");
                        PreferencesUtils.setEmojiEnablePref(mContext, jsonObject.optInt("emojiEnabled"));
                        checkImagePostingOption();

                        if (mDataJsonArray != null && mDataJsonArray.length() != 0) {

                            // Add Header
                            if ((mFeedPostMenu != null && mFeedPostMenu.length() != 0) ||
                                    (mFilterTabsArray != null && mFilterTabsArray.length() != 0)) {
                                if (mDataJsonArray != null && mDataJsonArray.length() != 0)
                                    mFeedItemsList.add(0, new FeedList(mFeedPostMenu, mFilterTabsArray,
                                            false, mReactionsEnabled, mReactions));
                                else
                                    mFeedItemsList.add(0, new FeedList(mFeedPostMenu, mFilterTabsArray,
                                            true, mReactionsEnabled, mReactions));
                            } else {
                                mFeedItemsList.add(0, new FeedList(null, null, false, mReactionsEnabled, mReactions));
                            }

                            for (int i = 0; i < mDataJsonArray.length(); i++) {
                                String url = null;
                                JSONObject singleFeedJsonObject = mDataJsonArray.getJSONObject(i);

                                // Get Feed Info Object

                                JSONObject feedInfo = singleFeedJsonObject.optJSONObject("feed");
                                JSONObject userObject = feedInfo.optJSONObject("object");
                                if (userObject != null && userObject.length() != 0) {
                                    url = userObject.optString("url");
                                }
                                mHashTagArray = singleFeedJsonObject.optJSONArray("hashtags");
                                if (mHashTagArray != null) {
                                    mHashTagString = "";
                                    for (int j = 0; j < mHashTagArray.length(); j++) {
                                        mHashTagString += mHashTagArray.get(j) + " ";
                                    }
                                } else {
                                    mHashTagString = "";
                                }
                                JSONArray feedMenus = singleFeedJsonObject.optJSONArray("feed_menus");
                                JSONObject feedFooterMenus = singleFeedJsonObject.optJSONObject("feed_footer_menus");
                                int canComment = singleFeedJsonObject.optInt("can_comment");

                                if (canComment != 0 && !mIsMultiPhotoFeed) {
                                    mCommentPostBlock.setVisibility(View.VISIBLE);
//                                    mAppConst.showKeyboard();
                                }

                                int isLike = singleFeedJsonObject.optInt("is_like");

                                int actionId = feedInfo.optInt("action_id");
                                String feedIcon = feedInfo.optString("feed_icon");
                                int subjectId = feedInfo.optInt(ConstantVariables.SUBJECT_ID);
                                int feedType = feedInfo.optInt("feed_type");
                                String objectType = feedInfo.optString("object_type");
                                int objectId = feedInfo.optInt("object_id");
                                String body = feedInfo.optString("body");
                                int commentCount = feedInfo.optInt("comment_count");
                                int attachmentCount = feedInfo.optInt("attachment_count");
                                int likeCount = feedInfo.optInt("like_count");
                                int commentAble = feedInfo.optInt("commentable");
                                int shareAble = feedInfo.optInt("shareable");
                                int isSaveFeedOption = singleFeedJsonObject.optInt("isSaveFeedOption");
                                boolean isNotificationTurnedOn = singleFeedJsonObject.optBoolean("isNotificationTurnedOn");
                                String feedLink = singleFeedJsonObject.optString("feed_link");
                                String schedulePostTime = feedInfo.optString("publish_date");
                                int pinPostDuration = singleFeedJsonObject.optInt("pin_post_duration");
                                boolean isPostPinned = singleFeedJsonObject.optBoolean("isPinned");
                                String date = feedInfo.optString("date");
                                JSONObject feedObject = feedInfo.optJSONObject("object");
                                String postPrivacy = feedInfo.optString("privacy");
                                String privacyIcon = feedInfo.optString("privacy_icon");

                                String type = feedInfo.optString("type");
                                String feedAttachmentType = feedInfo.optString("attachment_content_type");
                                String isTranslation = feedInfo.optString("is_translation");

                                /* CODE FOR FETCHING FEED ATTACHMENT */

                                JSONArray attachmentArray = feedInfo.optJSONArray("attachment");
                                int photoAttachmentCount = feedInfo.optInt("photo_attachment_count");

                                /* CODE STARTS FOR  PREPARING ACTION BODY */
                                String actionTypeBody = feedInfo.optString("action_type_body");
                                JSONArray actionTypeBodyParams = feedInfo.optJSONArray("action_type_body_params");
                                JSONArray tagsJsonArray = feedInfo.optJSONArray("tags");
                                JSONObject paramsJsonObject = feedInfo.optJSONObject("params");
                                String feedActionTitle = feedInfo.optString("feed_title");

                                JSONArray userTagJsonArray = feedInfo.optJSONArray("userTag");
                                JSONArray wordStyleArray = feedInfo.optJSONArray("wordStyle");

                                JSONObject decoration = feedInfo.optJSONObject("decoration");
                                JSONObject bannerObject = null, feelingObject = null, userTagObject = null;
                                if (paramsJsonObject != null && paramsJsonObject.length() > 0) {
                                    bannerObject = paramsJsonObject.optJSONObject("feed-banner");
                                    feelingObject = paramsJsonObject.optJSONObject("feelings");
                                    userTagObject = paramsJsonObject.optJSONObject("tags");
                                }

                                  /* Variables for shared feed */
                                int shareActionId = 0, shareSubjectId = 0, shareFeedType = 0, shareObjectId = 0;
                                String shareObjectType = "", shareBody = "", shareDate = "", shareActionTypeBody = "",
                                        shareFeedActionTitle = "", shareFeedIcon = "";
                                JSONArray shareActionTypeBodyParams;
                                boolean isShareFeed = false;

                                if (attachmentArray != null && attachmentArray.optJSONObject(0) != null &&
                                        attachmentArray.optJSONObject(0).has("shared_post_data")) {
                                    shareActionId = actionId;
                                    shareSubjectId = subjectId;
                                    shareFeedType = feedType;
                                    shareObjectType = objectType;
                                    shareObjectId = objectId;
                                    shareBody = body;
                                    shareDate = date;
                                    shareActionTypeBody = actionTypeBody;
                                    shareActionTypeBodyParams = actionTypeBodyParams;
                                    shareFeedActionTitle = feedActionTitle;
                                    shareFeedIcon = feedIcon;
                                    isShareFeed = true;

                                    if (shareActionTypeBodyParams != null && shareActionTypeBodyParams.length() != 0) {
                                        shareFeedActionTitle = getActionBody(shareActionTypeBody, shareActionTypeBodyParams,
                                                null, null, null, null,
                                                null, null, null, true);
                                    }


                                    JSONArray shareFeedArray = attachmentArray.optJSONObject(0).optJSONArray("shared_post_data");
                                    JSONObject shareObject = shareFeedArray.optJSONObject(0);
                                    JSONObject shareFeedObject = shareObject.optJSONObject("feed");

                                    actionId = shareFeedObject.optInt("action_id");
                                    feedIcon = shareFeedObject.optString("feed_icon");
                                    subjectId = shareFeedObject.optInt(ConstantVariables.SUBJECT_ID);
                                    feedType = shareFeedObject.optInt("feed_type");
                                    objectType = shareFeedObject.optString("object_type");
                                    objectId = shareFeedObject.optInt("object_id");
                                    body = shareFeedObject.optString("body");

                                    attachmentCount = shareFeedObject.optInt("attachment_count");
                                    photoAttachmentCount = shareFeedObject.optInt("photo_attachment_count");
                                    date = shareFeedObject.optString("date");
                                    feedObject = shareFeedObject.optJSONObject("object");

                                    type = shareFeedObject.optString("type");
                                    feedAttachmentType = shareFeedObject.optString("attachment_content_type");

                                    actionTypeBody = shareFeedObject.optString("action_type_body");
                                    actionTypeBodyParams = shareFeedObject.optJSONArray("action_type_body_params");
                                    tagsJsonArray = shareFeedObject.optJSONArray("tags");
                                    paramsJsonObject = shareFeedObject.optJSONObject("params");

                                    userTagJsonArray = shareFeedObject.optJSONArray("userTag");
                                    feedActionTitle = shareFeedObject.optString("feed_title");

                                }

                                JSONObject feedReactions = singleFeedJsonObject.optJSONObject("feed_reactions");
                                JSONObject myFeedReaction = singleFeedJsonObject.optJSONObject("my_feed_reaction");
                                JSONObject subjectProfileInfo = feedInfo.optJSONObject("subjectIformation");
                                mActionTypeBody = null;
                                startIndex = 0;
                                endIndex = 0;

                                if (actionTypeBodyParams != null && actionTypeBodyParams.length() != 0) {
                                    feedActionTitle = getActionBody(actionTypeBody, actionTypeBodyParams,
                                            tagsJsonArray, wordStyleArray, paramsJsonObject, feelingObject, isTranslation, attachmentArray, userTagJsonArray, false);
                                } else {
                                    feedActionTitle = feedInfo.optString("feed_title");
                                }

                                /* END ACTION BODY CODE */

                                mFeedItemsList.add(new FeedList(actionId, subjectId, feedType, objectType,
                                        objectId, feedActionTitle, feedIcon, postPrivacy, privacyIcon, feedMenus, date, feedLink, schedulePostTime, pinPostDuration,
                                        attachmentCount, likeCount, commentCount, canComment, isLike, feedObject, decoration, bannerObject,
                                        feelingObject, userTagObject, attachmentArray, photoAttachmentCount, feedFooterMenus, commentAble,
                                        shareAble, isSaveFeedOption, isNotificationTurnedOn, isPostPinned, mClickableParts,
                                        mClickablePartsNew, mActionTypeBody, mVideoInformation, mWordStylingClickableParts, url, feedAttachmentType,
                                        type, mLocationLabel, mLatitude, mLongitude, mPlaceId, mHashTagString,
                                        feedReactions, myFeedReaction, userTagJsonArray, isTranslation, startIndex, endIndex, isShareFeed, shareFeedIcon, shareActionId, shareSubjectId, shareFeedType, shareObjectType, shareObjectId,
                                        shareBody, shareDate, shareFeedActionTitle, mShareClickableParts, otherMembers, subjectProfileInfo));

                            }

                        } else {
                            String message = getResources().getString(R.string.deleted_feed_message);
                            SnackbarUtils.displaySnackbarLongWithListener(mCommentsListView,
                                    message, new SnackbarUtils.OnSnackbarDismissListener() {
                                        @Override
                                        public void onSnackbarDismissed() {
                                            if (!isFinishing()) {
                                                finish();
                                            }
                                        }
                                    });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mFeedAdapter.notifyDataSetChanged();
                mCommentAdapter.notifyDataSetChanged();
                isLoadingFeed = false;

                if (!isCommentsLoading && commentsObject != null && !mIsMultiPhotoFeed) {
                    try {

                        mGetTotalComments = commentsObject.getInt("getTotalComments");

                        if (mGetTotalComments != 0) {
                            mCommentList.setmTotalCommmentCount(mGetTotalComments);
                            mAllCommentsArray = commentsObject.optJSONArray("viewAllComments");
                            addCommentsToList(mAllCommentsArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mStickersParentView != null && mStickersParentView.getVisibility() == View.VISIBLE) {
            mStickersParentView.setVisibility(View.GONE);
        } else {

            int totalComments = mCommentList.getmTotalCommmentCount();
            if (mFeedItemsList.size() > 0) {
                FeedList feedList = (FeedList) mFeedItemsList.get(1);
                Intent intent = new Intent();

                // Update comment count from comment list when the comments are loaded on single feed.
                if (mFeedList == null && commentsObject != null) {
                    feedList.setmCommentCount(totalComments);
                }
                intent.putExtra(ConstantVariables.ITEM_POSITION, mFeedPosition);
                intent.putExtra(ConstantVariables.FEED_LIST, feedList);
                setResult(ConstantVariables.VIEW_SINGLE_FEED_PAGE, intent);
            }

            mAppConst.hideKeyboard();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            super.onBackPressed();
        }
    }

    public void sendRequestToServer(String commentListUrl) {
        mAppConst.getJsonResponseFromUrl(commentListUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mProgressBar.setVisibility(View.GONE);
                mCommentListItems.clear();
                commentsObject = jsonObject;

                if (!isLoadingFeed && jsonObject.length() != 0) {
                    try {

                        mGetTotalComments = jsonObject.getInt("getTotalComments");
                        if (mGetTotalComments != 0) {
                            mCommentList.setmTotalCommmentCount(mGetTotalComments);
                            mAllCommentsArray = jsonObject.optJSONArray("viewAllComments");
                            addCommentsToList(mAllCommentsArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    /*
     Function to Prepare Action Body of Activity Feed
        */

    public String getActionBody(String actionTypeBody, JSONArray actionTypeBodyParams,
                                JSONArray tagsJsonArray, JSONArray wordStyleArray, JSONObject paramsJsonObject,
                                JSONObject feelingObject, String isTranslation, JSONArray attachmentArray, JSONArray userTagJsonArray, boolean isShareFeed) {

        mClickableParts = new HashMap<>();
        mVideoInformation = new HashMap<>();
        if (isShareFeed) {
            mShareClickableParts = new HashMap<>();
        }

        int order = 1, id;
        String type, keyForClick, url;

        try {
            otherMembers = null;
            for (int j = 0; j < actionTypeBodyParams.length(); j++) {

                JSONObject actionBodyObject = actionTypeBodyParams.getJSONObject(j);
                String search = actionBodyObject.optString("search");
                String label = actionBodyObject.optString("label");
                id = actionBodyObject.optInt("id");
                type = actionBodyObject.optString("type");
                url = actionBodyObject.optString("url");
                if (actionTypeBody.contains(search)) {
                    switch (search) {
                        case "{item:$subject}":
                        case "{item:$object}":
                        case "{item:$owner}":

                            keyForClick = order + "-" + type + "-" + id;

                            if (isShareFeed) {
                                if (mShareClickableParts.containsKey(keyForClick)) {
                                    keyForClick += "-" + label;
                                }
                                mShareClickableParts.put(keyForClick, label);
                            } else {
                                if (mClickableParts.containsKey(keyForClick)) {
                                    keyForClick += "-" + label;
                                }
                                mClickableParts.put(keyForClick, label);
                            }

                            if (type.equals("video")) {
                                if (attachmentArray != null && attachmentArray.length() != 0) {
                                    for (int k = 0; k < attachmentArray.length(); k++) {
                                        JSONObject singleAttachmentObject = attachmentArray.getJSONObject(k);
                                        String attachmentType = singleAttachmentObject.getString("attachment_type");
                                        if (attachmentType.equals("video")) {
                                            int attachmentId = singleAttachmentObject.getInt("attachment_id");
                                            String attachment_video_type = singleAttachmentObject.
                                                    optString("attachment_video_type");
                                            String attachment_video_url = singleAttachmentObject.
                                                    optString("attachment_video_url");
                                            String videoInfo = attachment_video_type + "-" + attachment_video_url;

                                            mVideoInformation.put(attachmentId, videoInfo);
                                        }

                                    }
                                }
                            }
                            ++order;
                            actionTypeBody = actionTypeBody.replace(search, "<b>" + label + "</b>");
                            break;
                        case "{body:$body}":
                            mActionTypeBody = label;

                            if (userTagJsonArray != null && userTagJsonArray.length() > 0) {

                                mClickablePartsNew = new HashMap<>();
                                int serial = 1;

                                for (int k = 0; k < userTagJsonArray.length(); k++) {
                                    JSONObject singleTagJsonObject = userTagJsonArray.optJSONObject(k);
                                    String tagType = singleTagJsonObject.optString("type");
                                    int user_id = singleTagJsonObject.optInt("resource_id");
                                    String taggedFriendName = singleTagJsonObject.optString("resource_name");

                                    if (mActionTypeBody.contains(taggedFriendName)) {
                                        keyForClick = serial + "-" + tagType + "-" + user_id;
                                        label = label.replaceAll("\\s+", " ").trim();
                                        if (mClickablePartsNew.containsKey(keyForClick)) {
                                            keyForClick += "-" + taggedFriendName;
                                        }
                                        mClickablePartsNew.put(keyForClick, taggedFriendName);

                                        ++serial;
                                    }
                                }
                            }

                            if (wordStyleArray != null && wordStyleArray.length() > 0) {
                                int counter = 1;

                                mWordStylingClickableParts = new HashMap<>();

                                for (int k = 0; k < wordStyleArray.length(); k++) {
                                    JSONObject wordStyleObject = wordStyleArray.optJSONObject(k);
                                    String wordStyleTitle = wordStyleObject.optString("title");

                                    if (mActionTypeBody.toLowerCase().contains(wordStyleTitle.toLowerCase())) {
                                        keyForClick = String.valueOf(counter);
                                        label = label.replaceAll("\\s+", " ").trim();
                                        if (mWordStylingClickableParts.containsKey(keyForClick)) {
                                            keyForClick += "-" + wordStyleTitle;
                                        }
                                        mWordStylingClickableParts.put(keyForClick, wordStyleObject.toString());

                                        ++counter;
                                    }
                                }
                            }

                            actionTypeBody = actionTypeBody.replace(search, "");

                            // Removing all line breaking from the action type body,
                            // because we are not showing body in feed title,
                            // so if there is any line break in body it will show in feed body.
                            actionTypeBody = actionTypeBody.replaceAll("<br />", "");
                            break;
                        case "{var:$type}":
                            actionTypeBody = actionTypeBody.replace(search, label);
                            break;

                        case "{item:$object:topic}":
                            String slug = actionBodyObject.optString("slug");
                            keyForClick = order + "-" + type + "-" + id + "-" + slug;

                            mClickableParts.put(keyForClick, label);
                            actionTypeBody = actionTypeBody.replace(search, "<font color=\"#000000\">"
                                    + label + "</font>");
                            ++order;
                            break;

                        case "{itemParent:$object:forum}":
                            String forumSlug = actionBodyObject.optString("slug");
                            keyForClick = order + "-" + type + "-" + id + "-" + forumSlug;

                            mClickableParts.put(keyForClick, label);
                            actionTypeBody = actionTypeBody.replace(search, "<font color=\"#000000\">"
                                    + label + "</font>");
                            ++order;
                            break;

                        case "{actors:$subject:$object}":
                            keyForClick = order + "-" + type + "-" + id;
                            mClickableParts.put(keyForClick, label);
                            ++order;

                            JSONObject objectDetails = actionBodyObject.optJSONObject("object");
                            if (objectDetails != null) {
                                String object_type = objectDetails.getString("type");
                                int object_id = objectDetails.getInt("id");
                                String object_label = objectDetails.getString("label");

                                mClickableParts.put(order + "-" + object_type + "-" + object_id, object_label);
                                ++order;
                                actionTypeBody = actionTypeBody.replace(search, "<font color=\"#000000\"><b>"
                                        + label + " → " + object_label + "</font></b>");
                            }
                            break;
                        case "{others:$otheritems}":
                            otherMembers = actionBodyObject.optJSONArray("groupUser");
                            keyForClick = order + "-" + type + "-" + id + "-otherMembers";
                            mClickableParts.put(keyForClick, label);
                            ++order;
                            actionTypeBody = actionTypeBody.replace(search, "<b>" + label + "</b>");
                        break;
                        default:

                            // Making a part is clickable when it contains the url,
                            // so that if its not integrated then it can be opened in WebView.
                            if (url != null && !url.isEmpty()) {
                                keyForClick = order + "-" + type + "-" + id;
                                if (isShareFeed) {
                                    mShareClickableParts.put(keyForClick, label);
                                } else {
                                    mClickableParts.put(keyForClick, label);
                                }
                                ++order;
                                actionTypeBody = actionTypeBody.replace(search, "<b>" + label + "</b>");
                            } else {
                                actionTypeBody = actionTypeBody.replace(search, label);
                            }
                            break;
                    }
                }
            }

            // Adding feeling Activity.
            if (feelingObject != null && feelingObject.length() > 0) {
                startIndex = Html.fromHtml(actionTypeBody).length();
                actionTypeBody += isTranslation + "  &nbsp;  " + feelingObject.optString("parenttitle")
                        + " " + feelingObject.optString("childtitle");
                endIndex = Html.fromHtml(actionTypeBody).length();
            }

            if (tagsJsonArray != null && tagsJsonArray.length() != 0) {
                actionTypeBody += " -  <font color=\"#de000000\">" + getResources().getString(R.string.location_with)
                        + " </font>";
                for (int k = 0; k < tagsJsonArray.length(); k++) {

                    JSONObject singleTagJsonObject = tagsJsonArray.getJSONObject(k);
                    String tagType = singleTagJsonObject.getString("tag_type");
                    JSONObject tagObject = singleTagJsonObject.getJSONObject("tag_obj");
                    int user_id = tagObject.getInt("user_id");
                    String taggedFriendName = tagObject.getString("displayname");

                    if (k == 0) {
                        if (taggedFriendName != null && !taggedFriendName.isEmpty()) {
                            actionTypeBody += "<b>" + taggedFriendName + "</b>";
                            mClickableParts.put(order + "-" + tagType + "-" + user_id, taggedFriendName);
                            ++order;
                        }
                    } else if (k > 0 && tagsJsonArray.length() == 2) {
                        actionTypeBody += " " + getResources().getString(R.string.and) + "  " + "<b>" +
                                taggedFriendName + "<b>";
                        mClickableParts.put(order + "-" + tagType + "-" + user_id, taggedFriendName);
                        ++order;
                    } else if (tagsJsonArray.length() > 2) {
                        if (k < tagsJsonArray.length() - 1) {
                            actionTypeBody += "<font color=\"#a9a9a9\">" + ","
                                    + " </font> " + "<b>" + taggedFriendName + "<b>";
                            mClickableParts.put(order + "-" + tagType + "-" + user_id, taggedFriendName);
                            ++order;
                        } else {
                            actionTypeBody += " " + getResources().getString(R.string.and) + "  " + "<b>"
                                    + taggedFriendName + "<b>";
                            mClickableParts.put(order + "-" + tagType + "-" + user_id, taggedFriendName);
                            ++order;
                        }

                    }
                }
            }

            if (paramsJsonObject != null && paramsJsonObject.length() != 0) {
                JSONObject checkInJsonObject = paramsJsonObject.optJSONObject("checkin");
                if (checkInJsonObject != null && checkInJsonObject.length() != 0) {
                    String checkIn_type = checkInJsonObject.optString("type");
                    if (checkIn_type != null && !checkIn_type.isEmpty() && checkIn_type.equals("place")) {
                        mLocationLabel = checkInJsonObject.optString("label");
                        String locationPrefix = checkInJsonObject.optString("prefixadd");
                        String locationId = checkInJsonObject.optString("id");
                        mLatitude = checkInJsonObject.optDouble("latitude");
                        mLongitude = checkInJsonObject.optDouble("longitude");
                        mPlaceId = checkInJsonObject.optString("place_id");

                        String[] locationIdParts = locationId.split("_");
                        if (locationIdParts.length == 2) {
                            int location_id = Integer.parseInt(locationIdParts[1]);
                            String locationKey = "checkIn" + "-" + location_id;
                            mClickableParts.put(order + "-" + locationKey, mLocationLabel);
                            ++order;

                            if (tagsJsonArray != null && tagsJsonArray.length() != 0)
                                actionTypeBody += "  <font color=\"#de000000\">" + locationPrefix + " </font>"
                                        + " " + "<b>" + mLocationLabel + "</b>";
                            else
                                actionTypeBody += " -  <font color=\"#de000000\">" + locationPrefix + " </font>"
                                        + " " + "<b>" + mLocationLabel + "</b>";
                        }

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return actionTypeBody;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.commentPostButton:
                String commentBody = mCommentBox.getText().toString().trim();
                if (mStickersEnabled == 1 && (commentBody.isEmpty() && mSelectPath.isEmpty())) {
                    if (mStickersPopup != null) {
                        StickersUtil.showStickersKeyboard();
                    }
                } else {
                    try {

                        byte[] bytes = mCommentBox.getText().toString().trim().getBytes("UTF-8");
                        commentBody = new String(bytes, Charset.forName("UTF-8"));

                        if ((commentBody.length() == 0 || commentBody.trim().isEmpty()) && mSelectPath.isEmpty()) {
                            Toast.makeText(this, getResources().getString(R.string.comment_empty_msg),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mAppConst.hideKeyboard();
                            params = new HashMap<>();

                            // Convert text smileys to emoticons in comments
                            commentBody = Smileys.getEmojiFromString(commentBody);
                            params.put("body", commentBody);

                            mCommentBox.setText("");
                            mCommentBox.setHint(getResources().getString(R.string.write_comment_text) + "…");
                            postComment(commentBody, null, null);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.photoUploadingButton:
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    startImageUploadActivity(mContext, MultiMediaSelectorActivity.MODE_SINGLE, true, 1);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if (limit == totalItemCount && !isLoading) {
            if (limit >= ConstantVariables.COMMENT_LIMIT && (ConstantVariables.COMMENT_LIMIT * pageNumber) <
                    mCommentList.getmTotalCommmentCount()) {
                CustomViews.addFooterView(mCommentsListView, footerView);
                pageNumber += 1;
                String likeCommentsUrl = UrlUtil.AAF_LIKE_COMMNET_URL + "&action_id=" + mActionId + "&page=" + pageNumber;
                isLoading = true;
                loadMoreComments(likeCommentsUrl);

            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        if ((charSequence != null && charSequence.length() > 0 &&
                !charSequence.toString().trim().isEmpty()) || (mAttachmentType != null && !mAttachmentType.isEmpty())) {
            mCommentPostButton.setText("\uf1d8");
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));

            if (charSequence.toString().contains("@")) {
                String chr = charSequence.toString().substring(charSequence.toString().indexOf("@"), charSequence.length());
                if (chr.length() > 1) {
                    StringBuilder stringBuilder = new StringBuilder(chr);
                    searchText = stringBuilder.deleteCharAt(0).toString();
                    getFriendList(UrlUtil.GET_FRIENDS_LIST + "?search=" + searchText);
                } else {
                    mUserView.setVisibility(View.GONE);
                }
            } else {
                mUserView.setVisibility(View.GONE);
            }

        } else if (mStickersEnabled == 1) {
            mCommentPostButton.setText("\uf118");
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));
            if (mUserView.getVisibility() == View.VISIBLE) {
                mUserView.setVisibility(View.GONE);
            }

        } else {
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.gray_stroke_color));
            if (mUserView.getVisibility() == View.VISIBLE) {
                mUserView.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * Method to start ImageUploadingActivity (MultiImageSelector)
     *
     * @param context      Context of Class.
     * @param selectedMode Selected mode i.e. multi images or single image.
     * @param showCamera   Whether to display the camera.
     * @param maxNum       Max number of images allowed to pick in case of MODE_MULTI.
     */
    public void startImageUploadActivity(Context context, int selectedMode, boolean showCamera, int maxNum) {

        Intent intent;

        intent = new Intent(context, MultiMediaSelectorActivity.class);
        // Selection type photo to display items in grid
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
        // Whether photoshoot
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // The maximum number of selectable image
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // Select mode
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // The default selection
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiMediaSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        ((Activity) context).startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted, proceed to the normal flow
                    startImageUploadActivity(mContext, MultiMediaSelectorActivity.MODE_SINGLE, true, 1);
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
                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mCommentsListView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;
        }
    }

    public void loadMoreComments(String commentListUrl) {
        mAppConst.getJsonResponseFromUrl(commentListUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(mCommentsListView, footerView);

                if (jsonObject.length() != 0) {
                    try {
                        mGetTotalComments = jsonObject.getInt("getTotalComments");
                        mCommentList.setmTotalCommmentCount(mGetTotalComments);
                        mAllCommentsArray = jsonObject.optJSONArray("viewAllComments");
                        addCommentsToList(mAllCommentsArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isLoading = false;
                }
            }

            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mCommentsListView, message);
            }
        });
    }

    private void addCommentsToList(JSONArray commentsArray) {
        isCommentsLoading = true;

        try {
            if (commentsArray != null && commentsArray.length() != 0) {
                for (int i = 0; i < commentsArray.length(); i++) {
                    JSONObject commentInfoObject = commentsArray.getJSONObject(i);
                    int userId = commentInfoObject.optInt("user_id");
                    int commentId = commentInfoObject.optInt("comment_id");
                    String authorPhoto = commentInfoObject.optString("author_image_profile");
                    String authorTitle = commentInfoObject.optString("author_title");
                    String commentDate = commentInfoObject.optString("comment_date");
                    int likeCount = commentInfoObject.optInt("like_count");
                    JSONObject deleteJsonObject = commentInfoObject.optJSONObject("delete");
                    JSONObject likeJsonObject = commentInfoObject.optJSONObject("like");
                    JSONObject attachmentObject = commentInfoObject.optJSONObject("attachment");

                    String stickerImage = null;
                    JSONObject imageSize = null;
                    if (attachmentObject != null) {
                        stickerImage = attachmentObject.optString("image_profile");
                        imageSize = attachmentObject.optJSONObject("size");
                    }


                    /* CODE STARTS FOR  PREPARING Tags and comments body */
                    String commentBody = commentInfoObject.getString("comment_body");
                    JSONArray tagsJsonArray = commentInfoObject.optJSONArray("userTag");
                    JSONObject paramsJsonObject = commentInfoObject.optJSONObject("params");

                    if (tagsJsonArray != null && tagsJsonArray.length() != 0) {
                        commentBody = getCommentsBody(commentBody, tagsJsonArray);
                    }

                    int isLike = 0;
                    if (likeJsonObject != null)
                        isLike = likeJsonObject.getInt("isLike");

                    mCommentListItems.add(new CommentList(userId, commentId, likeCount,
                            isLike, authorPhoto, authorTitle, commentBody, mClickableParts, commentDate,
                            deleteJsonObject, likeJsonObject, stickerImage, imageSize));
                }

                mCommentAdapter.notifyDataSetChanged();

                // Scroll scrollview to the bottom when any new comment is posted
                mCommentsListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //call smooth scroll
                        mCommentsListView.smoothScrollToPosition(mCommentAdapter.getCount());

                    }
                }, 500L);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getCommentsBody(String commentBody, JSONArray tagsJsonArray) {
        mClickableParts = new HashMap<>();

        int order = 1, id;
        String type, keyForClick;

        // Make Tagged Friends Name Clickable
        for (int j = 0; j < tagsJsonArray.length(); j++) {

            JSONObject actionBodyObject = tagsJsonArray.optJSONObject(j);
            String search = actionBodyObject.optString("resource_name");
            String label = search;
            id = actionBodyObject.optInt("resource_id");
            type = actionBodyObject.optString("type");

            if (commentBody.contains(search)) {
                keyForClick = order + "-" + type + "-" + id;
                label = label.replaceAll("\\s+", " ").trim();
                if (mClickableParts.containsKey(keyForClick)) {
                    keyForClick += "-" + label;
                }
                mClickableParts.put(keyForClick, label);

                ++order;

                commentBody = commentBody.replace(search, "<b>" + label + "</b>");
            }
        }

        return commentBody;
    }

    // For tagging friends in comments
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
                    mUserView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
            }
        });

    }

    // Display list for tagging friends
    private void initFriendsListView(int length) {

        RelativeLayout.LayoutParams layoutParams;
        if (length > 4) {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.user_list_tag_view_height);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.addRule(RelativeLayout.ABOVE, mCommentPostBlock.getId());

        int margin10 = (int) mContext.getResources().getDimension(R.dimen.margin_10dp);
        int margin50 = (int) mContext.getResources().getDimension(R.dimen.margin_50dp);

        layoutParams.setMargins(margin10, margin10, margin50, margin10);
        mUserView.setLayoutParams(layoutParams);
        mUserView.setVisibility(View.VISIBLE);

        mAddPeopleAdapter = new AddPeopleAdapter(this, R.layout.list_friends, mAddPeopleList);
        mUserListView.setAdapter(mAddPeopleAdapter);
        mUserListView.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtils.LOGD(SingleFeedPage.class.getSimpleName(), "requestCode: " + requestCode + ", resultCode: " + resultCode);
        switch (requestCode) {

            case ConstantVariables.VIEW_LIGHT_BOX:
                // When there is single image at single feed page
                // and any changes occured at photoLightBox page then update the like/comment count.
                if (resultCode == ConstantVariables.LIGHT_BOX_EDIT && data != null) {
                    Bundle bundle = data.getExtras();
                    int feedPosition = bundle.getInt(ConstantVariables.ITEM_POSITION);
                    int likeCount = bundle.getInt(ConstantVariables.PHOTO_LIKE_COUNT);
                    int commentCount = bundle.getInt(ConstantVariables.PHOTO_COMMENT_COUNT);
                    int isLike = bundle.getBoolean(ConstantVariables.IS_LIKED) ? 1 : 0;

                    if (mFeedItemsList != null && mFeedItemsList.size() != 0) {
                        FeedList selectedFeedRow = (FeedList) mFeedItemsList.get(feedPosition);

                        // When the single feed page is loaded from feed list
                        // and the comment is updated on single photo comment page.
                        if (mFeedList == null && selectedFeedRow.getmCommentCount() != commentCount) {
                            sendRequestToServer(mCommentListUrl);
                        }
                        selectedFeedRow.setmCommentCount(commentCount);
                        selectedFeedRow.setmLikeCount(likeCount);
                        selectedFeedRow.setmIsLike(isLike);
                        mFeedAdapter.updatePhotoLikeCommentCount(feedPosition);
                        mFeedAdapter.notifyItemChanged(feedPosition);
                    }
                }
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                // When comment page is opened from any image(from multiple images) then update the comment count.
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {
                    Bundle bundle = data.getExtras();
                    int feedPosition = bundle.getInt(ConstantVariables.ITEM_POSITION);

                    if (bundle.getBoolean(ConstantVariables.IS_PHOTO_COMMENT)) {
//                        setClickedPhotoCommentCount(bundle);

                    } else if (mFeedItemsList != null && mFeedItemsList.size() != 0) {
                        FeedList feedList = (FeedList) mFeedItemsList.get(feedPosition);
                        feedList.setmCommentCount(bundle.getInt(ConstantVariables.PHOTO_COMMENT_COUNT));
                        mFeedAdapter.updatePhotoLikeCommentCount(feedPosition);
                        mFeedAdapter.notifyItemChanged(feedPosition);
                    }
                    break;
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
                                    break;
                                }
                            }
                            mStickersPopup.setupTabIcons();
                        }
                    }
                    ConstantVariables.STICKERS_STORE_ARRAY = new JSONObject();
                }
                break;

            case ConstantVariables.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {

                    if (mSelectPath != null) {
                        mSelectPath.clear();
                    }
                    // Getting image path from uploaded images.
                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                    //Checking if there is any image or not.
                    if (mSelectPath != null) {
                        showSelectedImages(mSelectPath);
                    }

                } else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.image_capturing_failed),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case ConstantVariables.VIEW_MULTI_IMAGE_COMMENT_PAGE_CODE:
                // When comment page is opened from any image(from multiple images) then update the comment count.
                if (data != null) {
                    switch (resultCode) {
                        case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                            mFeedAdapter.updateMultiPhotoComment(data.getExtras());
                            break;

                        case ConstantVariables.LIGHT_BOX_EDIT:
                            ArrayList<PhotoListDetails> photoList = (ArrayList<PhotoListDetails>) data.getSerializableExtra(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST);
                            if (photoList != null && photoList.size() > 0) {
                                mFeedAdapter.updatePhotosDetail(photoList);
                            }
                            break;
                    }
                }
                break;

            case ConstantVariables.FEED_REQUEST_CODE:
                if (resultCode == ConstantVariables.FEED_REQUEST_CODE) {
                    getFeeds(mFeedsUrl);
                }
                break;
        }

    }

    @Override
    public void onFeedDisableComment(boolean isCommentEnabled) {
        if (isCommentEnabled) {
            mCommentPostBlock.setVisibility(View.VISIBLE);
        } else {
            mCommentPostBlock.setVisibility(View.GONE);
        }

    }

    private void postComment(final String commentBody, String stickerGuid, String stickerImage) {
        isLoading = true;
        Bitmap bitmap = null;
        if (mStickersEnabled == 1) {
            mCommentPostButton.setText("\uf118");
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));
        } else {
            mCommentPostButton.setText("\uf1d8");
            mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.gray_stroke_color));
        }

        if (mStickersParentView.getVisibility() == View.VISIBLE)
            mStickersParentView.setVisibility(View.GONE);

        try {

            if (stickerGuid != null) {
                params.put("attachment_type", "sticker");
                params.put("attachment_id", stickerGuid);
            } else if (mSelectPath != null && !mSelectPath.isEmpty()) {
                params.put("attachment_type", mAttachmentType);
                bitmap = BitmapUtils.decodeSampledBitmapFromFile(this, mSelectPath.get(0),
                        width, (int) getResources().getDimension(R.dimen.feed_attachment_image_height), false);
            }

            // Show Comment instantly when user post it without like/delete options.

            if (PreferencesUtils.getUserDetail(this) != null) {

                JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(this));
                String profileIconImage = userDetail.getString("image_profile");
                int mLoggedInUserId = userDetail.getInt("user_id");
                String userName = userDetail.optString("displayname");
                mCommentListItems.add(new CommentList(mLoggedInUserId, userName, profileIconImage,
                        commentBody, stickerImage, true, bitmap));
                mCommentAdapter.notifyDataSetChanged();

                // Playing postSound effect when comment is posted.
                if (PreferencesUtils.isSoundEffectEnabled(this)) {
                    SoundUtil.playSoundEffectOnPost(this);
                }

                // Scroll scrollview to the bottom when any new comment is posted
                mCommentsListView.post(new Runnable() {
                    @Override
                    public void run() {
                        //call smooth scroll
                        mCommentsListView.smoothScrollToPosition(mCommentAdapter.getCount());

                        if (commentBody != null) {
                            params.put("body", commentBody);
                        }

                        if (tagObject != null && tagObject.length() > 0) {
                            params.put("composer", tagObject.toString());
                        }

                        params.put("send_notification", "0");
                        params.put(ConstantVariables.ACTION_ID, String.valueOf(mActionId));

                        if (mSelectPath != null && mSelectPath.size() != 0) {
                            mSelectedImageBlock.setVisibility(View.GONE);

                            // Uploading files in background with the details.
                            new UploadAttachmentUtil(SingleFeedPage.this, mCommentPostUrl, params,
                                    mSelectPath).execute();
                        } else {

                            mAppConst.postJsonResponseForUrl(mCommentPostUrl, params, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    addCommentToList(jsonObject);
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mCommentListItems.remove(mCommentAdapter.getCount() - 1);
                                    SnackbarUtils.displaySnackbar(mCommentsListView, message);
                                }

                            });
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addCommentToList(JSONObject jsonObject) {

        if (jsonObject != null) {
            int commentId = 0;
            String stickerImage = null;
            JSONObject imageSize = null;

            try {
                int userId = jsonObject.optInt("user_id");
                commentId = jsonObject.getInt("comment_id");
                String authorPhoto = jsonObject.getString("image_icon");
                String authorTitle = jsonObject.getString("author_title");
                String commentDate = jsonObject.getString("comment_date");
                int likeCount = jsonObject.optInt("like_count");
                JSONObject deleteJsonObject = jsonObject.optJSONObject("delete");
                JSONObject likeJsonObject = jsonObject.optJSONObject("like");
                JSONObject attachmentObject = jsonObject.optJSONObject("attachment");
                if (attachmentObject != null) {
                    stickerImage = attachmentObject.optString("image_profile");
                    imageSize = attachmentObject.optJSONObject("size");
                }

            /* CODE STARTS FOR  PREPARING Tags and comments body */
                String commentBody = jsonObject.getString("comment_body");
                JSONArray tagsJsonArray = jsonObject.optJSONArray("userTag");
                JSONObject paramsJsonObject = jsonObject.optJSONObject("params");

                if (tagsJsonArray != null && tagsJsonArray.length() != 0) {
                    commentBody = getCommentsBody(commentBody, tagsJsonArray);
                }


                int isLike = 0;
                if (likeJsonObject != null)
                    isLike = likeJsonObject.optInt("isLike");
            /*
                Remove the instantly added comment from the adapter after comment posted
                successfully and add a comment in adapter with full details.
            */
                mCommentListItems.remove(mCommentListItems.size() - 1);
                mCommentListItems.add(new CommentList(userId, commentId, likeCount,
                        isLike, authorPhoto, authorTitle, commentBody, mClickableParts, commentDate,
                        deleteJsonObject, likeJsonObject, stickerImage, imageSize));
                mCommentList.setmTotalCommmentCount(mCommentList.getmTotalCommmentCount() + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mCommentAdapter.notifyDataSetChanged();

            // Scroll scrollview to the bottom when any new comment is posted
            final int finalCommentId = commentId;
            mCommentsListView.post(new Runnable() {
                @Override
                public void run() {
                    //call smooth scroll
                    mCommentsListView.smoothScrollToPosition(mCommentAdapter.getCount());

                    params.put("comment_id", String.valueOf(finalCommentId));
                    params.remove("body");
                    params.remove("send_notification");

                    mAppConst.postJsonRequest(mCommentNotificationUrl, params);

                    isLoading = false;
                    mPhotoUploadingButton.setClickable(true);
                    mPhotoUploadingButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                }
            });
        }
    }

    /**
     * Method to show selected images.
     *
     * @param mSelectPath list of selected images.
     */
    public void showSelectedImages(final ArrayList<String> mSelectPath) {

        mAttachmentType = "photo";
        mCommentPostButton.setText("\uf1d8");
        mCommentPostButton.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));
        mSelectedImageBlock.setVisibility(View.VISIBLE);

        for (final String imagePath : mSelectPath) {

            // Getting Bitmap from its real path.
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(this, imagePath,
                    (int) getResources().getDimension(R.dimen.profile_image_width_height),
                    (int) getResources().getDimension(R.dimen.profile_image_width_height), false);

            // If there is any null image then remove from image path.
            if (bitmap != null) {
                // Creating ImageView & params for this and adding selected images in this view.
                mSelectedImageView.setImageBitmap(bitmap);

                // Setting OnClickListener on cancelImage.
                mCancelImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mSelectPath.remove(imagePath);

                        // If canceled all the selected images then hide photoBlockLayout
                        // and enabled other attachement click.
                        if (mSelectPath.isEmpty()) {
                            mAttachmentType = null;
                            mSelectedImageBlock.setVisibility(View.GONE);
                            if (mCommentBox.getText().toString().trim().isEmpty()) {
                                if (mStickersEnabled == 1) {
                                    mCommentPostButton.setText("\uf118");
                                    mCommentPostButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                                } else {
                                    mCommentPostButton.setText("\uf1d8");
                                    mCommentPostButton.setTextColor(ContextCompat.getColor(mContext, R.color.gray_stroke_color));
                                }
                            }

                        }
                    }
                });
            }
        }
    }

    @Override
    public void onAsyncSuccessResponse(JSONObject response, boolean isRequestSuccessful, boolean isAttachFileRequest) {

        if (BitmapUtils.isImageRotated)
            BitmapUtils.deleteImageFolder();
        if (response != null) {
            JSONObject bodyObject = response.optJSONObject("body");
            addCommentToList(bodyObject);
            mSelectPath.clear();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mUserView.setVisibility(View.GONE);

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
        mCommentBox.setText(mCommentBox.getText().toString().replace("@" + searchText, label));
        mCommentBox.setSelection(mCommentBox.getText().length());
    }
}