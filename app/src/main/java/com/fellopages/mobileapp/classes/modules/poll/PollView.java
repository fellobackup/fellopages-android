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

package com.fellopages.mobileapp.classes.modules.poll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Context;
import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.text.Html;

import android.view.View;

import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;

import com.fellopages.mobileapp.classes.common.ui.SplitToolbar;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PollView extends AppCompatActivity implements View.OnClickListener, OnOptionItemClickResponseListener,
        View.OnLongClickListener {

    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private Context mContext;
    private View mainContent;
    private String mItemViewUrl;
    private JSONArray mDataResponseArray,mGutterMenus;
    private JSONObject mDataResponse,mBody;
    private Map<String, String> postParams;

    private String pollTitle, pollDescription, creationDate, convertedDate, viewCount, voteCount, ownerTitle,
            pollOption, mSubjectType, mModuleName, mShareImageUrl;
    int mCommentCount, mLikeCount, pollOptionId, pollPercentage, userId, pollId, closed = 0;
    private int mContent_id, pollVotes, hasVoted = 0, canChangeVote, mSubjectId;

    private TextView mLikeCountTextView, mCommentCountTextView, mLikeUnlikeText;
    private TextView mOptionTag, mQuestionTag, mCloseIcon, mCloseText, mViewCountDetail;
    private SelectableTextView mViewTitle, mCreatorView, mDateView, mDetailDescription;

    private LinearLayout mRadioOptionLayout, mBarChartLayout,
            mBarChartOptions, mBarChartText;
    private ScrollView mScrollView;
    private ProgressBar scrollViewProgressBar, mProgressBarMain;

    private LinearLayout mLikeCommentContent;
    private RadioGroup mRadioGroup;
    private RadioButton radioButton;

    private boolean mIsLike,isLoadingFromCreate = false, isContentEdited = false, isShowOption = false,
            isShowResult = false;

    private String mLikeUnlikeUrl, mLikeCommentsUrl, mContentUrl;
    private int flag = 0, check=0;
    private SplitToolbar mSplitToolBar;
    private Toolbar mToolbar;
    private ProgressBar mProgress;
    private int  colorValue = 1;
    private int checkedButtonId;
    private int right_padding, bottom_padding;
    SocialShareUtil socialShareUtil;
    private JSONObject mReactionsObject, myReaction, mAllReactionObject, mContentReactions;
    private int mReactionsEnabled;
    private ImageView mReactionIcon;
    private List<ImageViewList> reactionsImages;
    private ArrayList<JSONObject> mReactionsArray;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_view);

        /* Create Back Button On Action Bar **/

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mContext = this;
        postParams = new HashMap<>();
        mainContent = findViewById(R.id.main_content);

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        socialShareUtil = new SocialShareUtil(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mImageLoader = new ImageLoader(getApplicationContext());
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mContent_id = getIntent().getIntExtra(ConstantVariables.VIEW_PAGE_ID, 0);

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        /*
        Like and Unlike Fields...
         */

        mLikeCountTextView = findViewById(R.id.likeCount);
        mCommentCountTextView = findViewById(R.id.commentCount);
        mReactionIcon = findViewById(R.id.reactionIcon);
        mLikeUnlikeText = findViewById(R.id.likeUnlikeText);

        LinearLayout mLikeBlock = findViewById(R.id.likeBlock);
        LinearLayout mCommentBlock = findViewById(R.id.commentBlock);
        LinearLayout mLikeCommentBlock = findViewById(R.id.likeCommentBlock);

        mLikeBlock.setOnClickListener(this);
        mLikeBlock.setOnLongClickListener(this);
        mCommentBlock.setOnClickListener(this);
        mLikeCommentBlock.setOnClickListener(this);

        mLikeCommentContent = findViewById(R.id.likeCommentContent);

        //Getting all the views

        mViewTitle = findViewById(R.id.viewTitle);
        mCloseIcon = findViewById(R.id.closeIcon);
        mCloseText = findViewById(R.id.closeText);
        mCreatorView = findViewById(R.id.creator_view);
        mDateView = findViewById(R.id.dateView);
        mViewCountDetail = findViewById(R.id.viewCountDetail);
        mDetailDescription = findViewById(R.id.detailDescription);

        mDetailDescription.setMovementMethod(LinkMovementMethod.getInstance());
        mProgressBarMain = findViewById(R.id.progressBarMain);

        mSplitToolBar = findViewById(R.id.toolbarBottom);
        mScrollView = findViewById(R.id.scrollView);
        scrollViewProgressBar = findViewById(R.id.progressBar);

        mOptionTag = findViewById(R.id.optionTag);
        mQuestionTag = findViewById(R.id.questionTag);

        mRadioOptionLayout = findViewById(R.id.radioOptionLayout);
        mBarChartLayout = findViewById(R.id.barChartOptionLayout);

        mRadioGroup = findViewById(R.id.optionRadioGroup);

        mSubjectType = "poll";
        mSubjectId = mContent_id;

        right_padding = (int) getResources().getDimension(R.dimen.padding_10dp);
        bottom_padding = (int) getResources().getDimension(R.dimen.padding_10dp);

        mCreatorView.setOnClickListener(this);

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.
                OnScrollChangedListener() {

            // Keeps track of the overall vertical offset in the list
            int verticalOffset;

            // Determines the scroll UP/DOWN direction
            boolean scrollingUp;

            @Override
            public void onScrollChanged() {
                int scrollY = mScrollView.getScrollY();
                verticalOffset += scrollY;

                scrollingUp = scrollY > 0;
                int toolbarYOffset = (int) (scrollY + mSplitToolBar.getTranslationY());

                if (scrollingUp) {
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
                }
            }
        });

        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);

        mQuestionTag.setOnClickListener(this);
        mOptionTag.setOnClickListener(this);

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
                public void onTaskCompleted(JSONObject jsonObject) {
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

    private void toolbarAnimateShow(int verticalOffset) {
        mSplitToolBar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
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
                    scrollViewProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(mainContent, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    if (!isFinishing()) {
                                        finish();
                                    }
                                }
                            });
                }
            });
        }
    }

    private void loadDataInView() {

        colorValue = 1;
        isLoadingFromCreate = false;
        mProgressBarMain.setVisibility(View.GONE);
        findViewById(R.id.divider).setVisibility(View.VISIBLE);
        findViewById(R.id.optionTag).setVisibility(View.VISIBLE);

        // Parsing json object response
        // response will be a json object
        mDataResponse = mBody.optJSONObject("response");
        mGutterMenus = mBody.optJSONArray("gutterMenu");

        if (mGutterMenus != null) {
            invalidateOptionsMenu();
        }

        if (mDataResponse == null) {
            mDataResponseArray = mBody.optJSONArray("response");
            mDataResponse = mAppConst.convertToJsonObject(mDataResponseArray);
        }

        //Fetch Data from Response
        pollId = mDataResponse.optInt("poll_id");
        mSubjectId = pollId;
        userId = mDataResponse.optInt("user_id");
        pollTitle = mDataResponse.optString("title");
        pollDescription = mDataResponse.optString("description");
        mShareImageUrl = mDataResponse.optString("owner_image");
        creationDate = mDataResponse.optString("creation_date");
        viewCount = mDataResponse.optString("view_count");
        mCommentCount = mDataResponse.optInt("comment_count");
        voteCount = mDataResponse.optString("vote_count");
        closed = mDataResponse.optInt("closed");
        ownerTitle = mDataResponse.optString("owner_title");
        convertedDate = AppConstant.convertDateFormat(getResources(), creationDate);
        mContentUrl = mDataResponse.optString("content_url");
        mBrowseList = new BrowseListItems(pollId, pollTitle, mShareImageUrl, mContentUrl);

        final JSONArray mOptions = mDataResponse.optJSONArray("options");

        hasVoted = mDataResponse.optInt("hasVoted");
        if ((hasVoted != 0 || closed == 1 || isShowResult) && !isShowOption)
        {
            generatePollResult(mOptions);
        } else {
            generateVoteOptionView(mOptions);
        }

        canChangeVote = mDataResponse.optInt("canChangeVote");

        mIsLike = mDataResponse.optBoolean("is_like");
        mLikeCount = mDataResponse.optInt("like_count");

        // Setting up Like and Comment Count
        setLikeAndCommentCount();

        // setting up the values in views
        setValuesInViews();

        mItemViewUrl = UrlUtil.POLL_VIEW_URL + pollId + "?gutter_menu=" + 1;

        // Send response on server of vote on poll
        postParams = new HashMap<>();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                postParams.put("option_id", String.valueOf(i));

                String votePollUrl = AppConstant.DEFAULT_URL + "polls/vote/" + pollId;

                checkedButtonId = mRadioGroup.getCheckedRadioButtonId();
                if (checkedButtonId != hasVoted) {

                    mRadioOptionLayout.setVisibility(View.GONE);
                    scrollViewProgressBar.setVisibility(View.VISIBLE);

                    mAppConst.postJsonResponseForUrl(votePollUrl, postParams, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {

                            check++;
                            if (jsonObject != null) {
                                mAppConst.hideProgressDialog();
                                SnackbarUtils.displaySnackbar(mainContent,
                                        getResources().getString(R.string.vote_on_poll_success_message));

                                JSONArray mOptions = jsonObject.optJSONArray("response");
                                hasVoted = checkedButtonId;
                                generatePollResult(mOptions);
                            } else {
                                SnackbarUtils.displaySnackbar(mainContent,
                                        getResources().getString(R.string.vote_on_poll_denied_message));
                            }

                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                            mAppConst.hideProgressDialog();
                            SnackbarUtils.displaySnackbar(mainContent, message);

                        }

                    });
                }

            }
        });

    }

    // show poll result

    private void generatePollResult( JSONArray mOptions) {

        scrollViewProgressBar.setVisibility(View.GONE);
        mRadioOptionLayout.setVisibility(View.GONE);
        mBarChartLayout.setVisibility(View.VISIBLE);
        mOptionTag.setVisibility(View.GONE);
        mQuestionTag.setVisibility(View.VISIBLE);

        if (closed == 1) {
            mQuestionTag.setEnabled(false);
        } else {
            mQuestionTag.setEnabled(true);
        }

        if (mOptions != null)
        {
            mBarChartLayout.removeAllViews();
            mRadioGroup.removeAllViews();
            for (int i = 0; i < mOptions.length(); i++) {
                JSONObject optionObject = mOptions.optJSONObject(i);
                pollOptionId = optionObject.optInt("poll_option_id");
                pollOption = optionObject.optString("poll_option");
                pollVotes = optionObject.optInt("votes");
                pollPercentage = optionObject.optInt("percentage");

                mBarChartText = new LinearLayout(this);
                mBarChartText.setOrientation(LinearLayout.VERTICAL);
                mBarChartText.setLayoutParams(CustomViews.getFullWidthHeightLayoutParams());

                TextView textView = new TextView(this);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                textView.setText(Html.fromHtml(pollOption));
                textView.setId(pollOptionId);
                mBarChartText.addView(textView);
                generateBarChart(pollPercentage, pollVotes);

            }
        }
    }


    // setting up vote option (radio button) in views

    private void generateVoteOptionView(JSONArray mOptions) {

        scrollViewProgressBar.setVisibility(View.GONE);
        mRadioOptionLayout.setVisibility(View.VISIBLE);
        mQuestionTag.setVisibility(View.GONE);
        mOptionTag.setVisibility(View.VISIBLE);
        mOptionTag.setEnabled(true);

        for (int i = 0; i < mOptions.length(); i++) {
            JSONObject optionObject = mOptions.optJSONObject(i);
            pollOptionId = optionObject.optInt("poll_option_id");
            pollOption = optionObject.optString("poll_option");

            radioButton = new RadioButton(this);
            radioButton.setMovementMethod(LinkMovementMethod.getInstance());

            if (hasVoted == pollOptionId)
            {
                radioButton.setChecked(true);
            }
            radioButton.setText(String.format(" %s", Html.fromHtml(pollOption)));
            radioButton.setId(pollOptionId);
            mRadioGroup.addView(radioButton);

            if (closed == 1 || mAppConst.isLoggedOutUser() || (canChangeVote == 0 && hasVoted != 0))
            {
                radioButton.setEnabled(false);
                radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }
    }

    // set poll result in bar chart's

    private void generateBarChart(int value,int votes) {

            String vote;
            if (votes == 1)
                vote = "vote";
            else
                vote = "votes";

            if (value == 0)
                value = 1;

            mProgress = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressBarParams = CustomViews.getFullWidthLayoutParams();
            progressBarParams.weight = 0.70f;
            mProgress.setLayoutParams(progressBarParams);

            mBarChartOptions = new LinearLayout(this);
            mBarChartOptions.setOrientation(LinearLayout.HORIZONTAL);

            mBarChartOptions.setLayoutParams(CustomViews.getFullWidthLayoutParams());
            mBarChartOptions.setPadding(0,0,right_padding,0);

            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(null, null, null));

            // Set the progressBar color
            int color = colorHexCode(colorValue);
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(mContext, color));
            ClipDrawable progress = new ClipDrawable(shapeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

            mProgress.setProgressDrawable(progress);
            mProgress.setPadding(0, 0, right_padding, bottom_padding);
            mProgress.setProgress(value);
            mProgress.setMax(100); // Maximum Progress

            TextView textView = new TextView(this);
            textView.setPadding(0,0,right_padding,0);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
            textView.setText(String.format("%d %s", votes, vote));

            mBarChartOptions.addView(mProgress);
            mBarChartOptions.addView(textView);

            colorValue++;
            if (colorValue == 9)
            {
                colorValue = 1;
            }

            mBarChartLayout.addView(mBarChartText);
            mBarChartLayout.addView(mBarChartOptions);

    }

    // set color of bar's
    public int colorHexCode(int colorValue)
    {
        int colorHex = 0;
        switch (colorValue)
        {
                case 1:
                    colorHex = R.color.poll_vote_bar_colorName1;
                    break;
                case 2:
                    colorHex = R.color.poll_vote_bar_colorName2;
                    break;
                case 3:
                    colorHex = R.color.poll_vote_bar_colorName3;
                    break;
                case 4:
                    colorHex = R.color.poll_vote_bar_colorName4;
                    break;
                case 5:
                    colorHex = R.color.poll_vote_bar_colorName5;
                    break;
                case 6:
                    colorHex = R.color.poll_vote_bar_colorName6;
                    break;
                case 7:
                    colorHex = R.color.poll_vote_bar_colorName7;
                    break;
                case 8:
                    colorHex = R.color.poll_vote_bar_colorName8;
                    break;

        }
        return colorHex;
    }

    public void setValuesInViews(){

                mViewTitle.setText(pollTitle);
                // set poll closed icon and text in poll profile
                if (closed == 1) {
                    mCloseIcon.setVisibility(View.VISIBLE);
                    mCloseText.setVisibility(View.VISIBLE);
                    mCloseIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    mCloseIcon.setText("\uf023" + " ");
                    mCloseText.setText(R.string.close_poll_text);
                }

                String creatorTextFormat1 = getResources().getString(R.string.creator_salutation_format);
                String creatorText1 = String.format(creatorTextFormat1,
                        getResources().getString(R.string.album_owner_salutation), ownerTitle);
                mCreatorView.setText(Html.fromHtml(creatorText1));

                mDateView.setText(convertedDate);
                mViewCountDetail.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                mViewCountDetail.setText(voteCount + "  " + "\uF080" + "    " +
                        viewCount + "  " + "\uf06e");
                mViewCountDetail.setVisibility(View.VISIBLE);
                mDetailDescription.setText(Html.fromHtml(pollDescription));
    }

    public void setLikeAndCommentCount() {

        if(mAppConst.isLoggedOutUser()){
            mSplitToolBar.setVisibility(View.GONE);
        }else {
            mSplitToolBar.setVisibility(View.VISIBLE);
            mLikeCommentContent.setVisibility(View.VISIBLE);

            mLikeUnlikeText.setActivated(mIsLike);
            if (!mIsLike) {
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(this, R.color.grey_dark));
            } else {
                mLikeUnlikeText.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));
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
                    mLikeUnlikeText.setText(getString(R.string.like_text));
                }
            } else{
                mLikeUnlikeText.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                mLikeUnlikeText.setText(getString(R.string.like_text));
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
        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        } else {

            if(mGutterMenus != null) {

                mGutterMenuUtils.onMenuOptionItemSelected(mainContent, findViewById(item.getItemId()),
                        id, mGutterMenus);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.POLL_MENU_TITLE,
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
            if (isContentEdited) {
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id){

            case R.id.questionTag:
                isShowOption = true;
                isShowResult = false;
                scrollViewProgressBar.setVisibility(View.VISIBLE);
                mQuestionTag.setEnabled(false);
                mBarChartLayout.setVisibility(View.GONE);
                mRadioOptionLayout.setVisibility(View.VISIBLE);
                mBarChartLayout.removeAllViews();
                mRadioGroup.removeAllViews();
                flag = 1;
                makeRequest();
                break;

            case R.id.optionTag:
                isShowOption = false;
                isShowResult = true;
                scrollViewProgressBar.setVisibility(View.VISIBLE);
                mOptionTag.setEnabled(false);
                mBarChartLayout.setVisibility(View.VISIBLE);
                mRadioOptionLayout.setVisibility(View.GONE);
                mBarChartLayout.removeAllViews();
                mRadioGroup.removeAllViews();
                flag = 2;
                makeRequest();
                break;

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
                        "&viewAllComments=1&page=1&limit=" + AppConstant.LIMIT;
                Intent commentIntent = new Intent(this, Comment.class);
                commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
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

            case R.id.creator_view:
                Intent intent = new Intent(PollView.this, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID, userId);
                startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case ConstantVariables.USER_PROFILE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mModuleName);
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {
                    mCommentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mCommentCount);
                    mCommentCountTextView.setText(String.valueOf(mCommentCount));
                }
                break;
        }
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

    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){

        mLikeUnlikeText.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
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

        if(!mIsLike || isReactionChanged){
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
                    updateContentReactions(reactionId, reactionIcon, caption, mIsLike, isReactionChanged);

                    /* Calling to send notifications after like action */
                    if (!mIsLike) {
                        mAppConst.postJsonResponseForUrl(sendLikeNotificationUrl, likeParams, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {

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
                if(!mIsLike){
                    mLikeCount += 1;
                } else if( !isReactionChanged){
                    mLikeCount -= 1;
                }

                // Toggle isLike Variable if reaction is not changed
                if( !isReactionChanged)
                    mIsLike = !mIsLike;

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
                                        boolean isLiked, boolean isReactionChanged) {

        try {

            // Update the count of previous reaction in reactions object and remove the my_feed_reactions
            if (isLiked) {
                if (myReaction != null && mContentReactions != null) {
                    int myReactionId = myReaction.optInt("reactionicon_id");
                    if (mContentReactions.optJSONObject(String.valueOf(myReactionId)) != null) {
                        int myReactionCount = mContentReactions.optJSONObject(String.valueOf(myReactionId)).
                                optInt("reaction_count");
                        if ((myReactionCount - 1) <= 0) {
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
            if (!isLiked || isReactionChanged) {
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

    public void onItemDelete(String successMessage) {

    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;
    }
}

