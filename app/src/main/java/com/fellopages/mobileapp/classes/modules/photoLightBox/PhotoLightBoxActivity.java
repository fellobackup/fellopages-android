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

package com.fellopages.mobileapp.classes.modules.photoLightBox;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;

import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.AddPeople;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.wunderlist.slidinglayer.SlidingLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;


public class PhotoLightBoxActivity extends AppCompatActivity implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener,FullScreenImageAdapter.PhotoLongClickListener,
        View.OnLongClickListener, OnOptionItemClickResponseListener {


    public static final String EXTRA_IMAGE_URL_LIST = "Image Details";
    public static final String DELETED_POSITION = "position";
    private FullScreenImageAdapter fullScreenImageAdapter;
    private LoopViewPager viewPager;
    private ArrayList<PhotoListDetails> mPhotoDetails;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private Map<String,String> editParams;
    private String normalImgUrl,mRequestUrl, mCurrentImageUrl, mAttachmentUri;
    private JSONObject mBody;
    private JSONArray mDataResponseArray;
    private int mTotalItemCount, pageNo = 1,currentImageId, isEditable, mPosition, currentPosition;
    boolean isPageSelected = false , isContentEdited = false, showAlbumPage = false;
    private PhotoListDetails photoListDetails;
    private String currentSelectedOption;
    private ImageButton commentButton, editOption,shareButton, tagButton;
    private Button rotateLeft, rotateRight, flipHorizontal, flipVertical, saveImageBtn;
    private ImageView optionMenu,navigationBack, mReactionIcon;
    private Typeface fontIcon;
    private JSONObject menuJsonObject;
    private JSONArray mOptionMenuArray;
    private String mOptionMenuName, redirectUrl, owner_title, albumTitle, editImageUrl;
    private TextView mAlbumDetail, mAlbumName, mPhotoCounter, mLikeCount, mCommentCount;
    private TextView mPhotoTitle,mPhotoDescription;
    private SlidingLayer mSlidingLayer;
    private RelativeLayout mLikeCommentCountsContainer;
    private int mPhotoLikeCount, mPhotoCommentCount;
    private String mLikeUnlikeUrl, mLikeCommentsUrl;
    private int mSubjectId, mListingTypeId, albumId;
    SocialShareUtil socialShareUtil;
    private boolean isPhotoComment = false, showOptions = true,isShareClicked = false;
    private int mPhotoPosition;
    private JSONObject mReactionsObject, mPhotoReactions, myReactions, mContentReactionsObject;
    private LinearLayout popularReactionsView;
    private ActionIconThemedTextView likeButton;
    private boolean isReactionsEnabled, isNestedCommentsEnabled;
    private List<ImageViewList> reactionsImages;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ArrayList<JSONObject> mReactionsArray;
    private JSONArray mTagJsonArray;
    private HashMap<String, String> mClickableParts;
    private Context mContext;
    private ImageLoader mImageLoader;
    String photoDescription = "";
    private String fullCaption, shortCaption;
    private boolean isTextExpanded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_light_box);
        mAlertDialogWithAction = new AlertDialogWithAction(PhotoLightBoxActivity.this);
        mImageLoader = new ImageLoader(getApplicationContext());

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mContext = getApplicationContext();

        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer);

        mPhotoTitle = (TextView) findViewById(R.id.plb_photoTitle);
        mPhotoDescription = (TextView) findViewById(R.id.plb_photoDescription);
        mAlbumDetail = (TextView)findViewById(R.id.plb_albumDetail);
        mAlbumName = (TextView)findViewById(R.id.plb_albumName);
        mPhotoCounter = (TextView)findViewById(R.id.plb_photoNumber);
        mLikeCount = (TextView) findViewById(R.id.likeCount);
        mCommentCount = (TextView) findViewById(R.id.commentCount);
        likeButton = (ActionIconThemedTextView) findViewById(R.id.likeButton);
        commentButton = (ImageButton) findViewById(R.id.commentButton);
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        tagButton = (ImageButton) findViewById(R.id.tagButton);
        editOption = (ImageButton) findViewById(R.id.editOption);
        optionMenu = (ImageView) findViewById(R.id.optionMenu);
        optionMenu.setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_IN);
        navigationBack = (ImageView) findViewById(R.id.navigation_back);
        rotateLeft = (Button) findViewById(R.id.rotateLeft);
        rotateRight = (Button) findViewById(R.id.rotateRight);
        flipHorizontal = (Button) findViewById(R.id.flipHorizontal);
        flipVertical = (Button) findViewById(R.id.flipVertical);
        saveImageBtn = (Button)findViewById(R.id.saveImage);
        mLikeCommentCountsContainer = (RelativeLayout) findViewById(R.id.counts_container);
        popularReactionsView = (LinearLayout) findViewById(R.id.popularReactionIcons);
        mReactionIcon = (ImageView) findViewById(R.id.reactionIcon);

        isReactionsEnabled = PreferencesUtils.getReactionsEnabled(this) == 1;
        isNestedCommentsEnabled = PreferencesUtils.isNestedCommentEnabled(this);

        if(PreferencesUtils.getAllReactionsObject(this) != null){
            try {
                mReactionsObject = new JSONObject(PreferencesUtils.getAllReactionsObject(this));
                mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mReactionsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        fontIcon = GlobalFunctions.getFontIconTypeFace(this);
        mPhotoDescription.setMovementMethod(LinkMovementMethod.getInstance());


        rotateLeft.setTypeface(fontIcon);
        rotateRight.setTypeface(fontIcon);
        flipHorizontal.setTypeface(fontIcon);
        flipVertical.setTypeface(fontIcon);
        saveImageBtn.setTypeface(fontIcon);

        rotateRight.setText("\uf01e");
        rotateLeft.setText("\uf0e2");
        flipHorizontal.setText("\uf07e");
        flipVertical.setText("\uf07d");
        saveImageBtn.setText("\uf0ed");

        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
        flipHorizontal.setOnClickListener(this);
        flipVertical.setOnClickListener(this);
        editOption.setOnClickListener(this);
        saveImageBtn.setOnClickListener(this);
        //back Navigation
        navigationBack.setOnClickListener(this);
        //Menu click event
        optionMenu.setOnClickListener(this);
        //share button click listener
        shareButton.setOnClickListener(this);

        //Like click event
        likeButton.setOnClickListener(this);
        if(isReactionsEnabled && isNestedCommentsEnabled)
            likeButton.setOnLongClickListener(this);

        //Comment click event
        commentButton.setOnClickListener(this);
        mLikeCommentCountsContainer.setOnClickListener(this);
        viewPager = (LoopViewPager) findViewById(R.id.viewPager);

        //Tag click event
        tagButton.setOnClickListener(this);


        mAppConst = new AppConstant(this);
        socialShareUtil = new SocialShareUtil(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        Intent intent = getIntent();

        // Getting the current selected option from the intent if its coming.
        currentSelectedOption = intent.getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        if (currentSelectedOption == null || currentSelectedOption.isEmpty()) {
            currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(this);
        }

        //getting the position that user have selected
        mPhotoPosition = intent.getIntExtra(ConstantVariables.ITEM_POSITION, 0);
        isEditable = intent.getIntExtra(ConstantVariables.CAN_EDIT, 0);
        isPhotoComment = intent.getBooleanExtra(ConstantVariables.ENABLE_COMMENT_CACHE, false);
        mRequestUrl = intent.getStringExtra(ConstantVariables.PHOTO_REQUEST_URL);
        mTotalItemCount = intent.getIntExtra(ConstantVariables.TOTAL_ITEM_COUNT, 0);
        showOptions = intent.getBooleanExtra(ConstantVariables.SHOW_OPTIONS, true);
        mListingTypeId = intent.getIntExtra(ConstantVariables.LISTING_TYPE_ID, 0);
        showAlbumPage = intent.getBooleanExtra(ConstantVariables.SHOW_ALBUM_BUTTON,false);
        albumId = intent.getIntExtra(ConstantVariables.ALBUM_ID, 0);

        // Show tag button if photo is opened from Album module
        if (getSubjectType().equals("album_photo") && showOptions) {
            tagButton.setVisibility(View.VISIBLE);
        } else {
            tagButton.setVisibility(View.GONE);
        }

        Bundle data = intent.getExtras();

        //getting the loaded image list
        mPhotoDetails = (ArrayList<PhotoListDetails>) data.getSerializable(EXTRA_IMAGE_URL_LIST);

        fullScreenImageAdapter = new FullScreenImageAdapter(PhotoLightBoxActivity.this,this,mPhotoDetails);

        viewPager.setAdapter(fullScreenImageAdapter);

        // displaying selected image first
        viewPager.setCurrentItem(mPhotoPosition);

        //Setting Values for the first item
        if(!isPageSelected) {
            setPageDetails(viewPager.getCurrentItem());
        }

        //Setting the listener for getting the current page status so we can identify the last page
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                closeSlidingLayer();
            }

            @Override
            public void onPageSelected(int position) {
                isPageSelected = true;
                setPageDetails(position);
                if (position == mPhotoDetails.size() - 1 && mTotalItemCount > mPhotoDetails.size()) {
                    mAppConst.showProgressDialog();
                    //Sending request for getting more images from server
                    makeRequest();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setPageDetails(int position){
        try {
            optionMenu.setTag(position);
            editOption.setTag(position);
            currentPosition = position;
            photoListDetails = mPhotoDetails.get(position);
            mCurrentImageUrl = photoListDetails.getImageUrl();
            mAttachmentUri = photoListDetails.getmAttachmentUri();
            currentImageId = photoListDetails.getPhotoId();
            mSubjectId = currentImageId;
            albumTitle = photoListDetails.getAlbumTitle();
            if(photoListDetails.getOwnerTitle() != null) {
                mAlbumDetail.setText(Html.fromHtml(String.format(
                        getResources().getString(R.string.album_owner_salutation_format),
                        getResources().getString(R.string.album_owner_salutation),
                        photoListDetails.getOwnerTitle())));
                mAlbumDetail.setVisibility(View.VISIBLE);
            }else {
                mAlbumDetail.setVisibility(View.GONE);
            }

            if(photoListDetails.getAlbumTitle() != null) {
                mAlbumName.setText(Html.fromHtml(String.format(
                        getResources().getString(R.string.plb_album_name_format),
                        getResources().getString(R.string.plb_album_name),
                        photoListDetails.getAlbumTitle())));
                mAlbumName.setVisibility(View.VISIBLE);
            }else {
                mAlbumName.setVisibility(View.GONE);
            }
            mPhotoCounter.setText(String.format("%d-%d", position + 1, mTotalItemCount));

            if(photoListDetails.getImageTitle() != null && !photoListDetails.getImageTitle().isEmpty()){
                mPhotoTitle.setText(photoListDetails.getImageTitle());
                mPhotoTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
            }else {
                mPhotoTitle.setVisibility(View.GONE);
            }

            if (isEditable == 0) {
                editOption.setImageDrawable(
                        ContextCompat.getDrawable(this,R.drawable.ic_cloud_download_white_18dp));

            } else {
                editOption.setImageDrawable(
                        ContextCompat.getDrawable(this,R.drawable.ic_photo_filter_white_18dp));
            }

            if(photoListDetails.getMenuArray() != null && !photoListDetails.getMenuArray().isEmpty()) {
                try {
                    mOptionMenuArray = new JSONArray(photoListDetails.getMenuArray());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                optionMenu.setVisibility(View.VISIBLE);
            }else {
                optionMenu.setVisibility(View.INVISIBLE);
            }

            // Adding view album option when there is show album page request.
            if(showAlbumPage && albumId != 0) {
                JSONObject viewAlbumObject = new JSONObject();
                try {
                    viewAlbumObject.put("name", "view_album");
                    viewAlbumObject.put("label", getResources().getString(R.string.view_album));
                    if (mOptionMenuArray == null) {
                        mOptionMenuArray = new JSONArray();
                    }
                    mOptionMenuArray.put(viewAlbumObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (photoListDetails.getmUserTagArray() != null && !photoListDetails.getmUserTagArray().isEmpty()) {
                String userTag = photoListDetails.getmUserTagArray();
                try {
                    mTagJsonArray = new JSONArray(userTag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(photoListDetails.getImageDescription() != null && !photoListDetails.getImageDescription().isEmpty()) {
                photoDescription = photoListDetails.getImageDescription();
            }

            setTagViewContent(photoDescription , mTagJsonArray);

            setOptionsVisibility();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void setTagViewContent(String description, JSONArray tagJsonArray) {

        mTagJsonArray = tagJsonArray;

        if (mTagJsonArray != null && mTagJsonArray.length() > 0) {
            tagButton.setColorFilter(ContextCompat.getColor(this, R.color.themeButtonColor));
            description = getTaggedBody(description , mTagJsonArray);
        } else {
            tagButton.setColorFilter(ContextCompat.getColor(this, R.color.white));
        }

        if((description != null && !description.isEmpty())  ||
                (mTagJsonArray != null && mTagJsonArray.length() > 0)) {

            mPhotoDescription.setVisibility(View.VISIBLE);
            mPhotoDescription.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));

            // Show Clickable Parts and Apply Click Listener to redirect
            if (mClickableParts != null && mClickableParts.size() != 0) {

                CharSequence title = Html.fromHtml(description);
                SpannableString text = new SpannableString(title);
                SortedSet<String> keys = new TreeSet<>(mClickableParts.keySet());

                int lastIndex = 0;
                for (String key : keys) {

                    String[] keyParts = key.split("-");
                    final String attachment_type = keyParts[1];
                    final int attachment_id = Integer.parseInt(keyParts[2]);
                    final String value = mClickableParts.get(key);

                    if (value != null && !value.isEmpty()) {
                        int i1 = title.toString().indexOf(value, lastIndex);
                        if (i1 != -1) {
                            int i2 = i1 + value.length();
                            if (lastIndex != -1) {
                                lastIndex += value.length();
                            }
                            ClickableSpan myClickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    redirectToActivity(attachment_type, attachment_id);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(false);
                                    ds.setColor(ContextCompat.getColor(mContext, R.color.white));
                                    ds.setFakeBoldText(true);
                                }
                            };
                            text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
                mPhotoDescription.setText(text);
            } else {
                setCaptionInView(photoDescription);
            }

        } else {
            mPhotoDescription.setVisibility(View.GONE);
        }
    }

    private void setCaptionInView(String description) {
        if (description != null && !description.isEmpty()) {
            mPhotoDescription.setOnClickListener(this);
            fullCaption = description;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (description.length() < 100) {
                    mPhotoDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    mPhotoDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                }
            }

            if (description.length() > ConstantVariables.PHOTO_LIGHT_BOX_WORD_LIMIT) {
                shortCaption = description.substring(0, ConstantVariables.PHOTO_LIGHT_BOX_WORD_LIMIT) + "... <b>See more</b>";
                mPhotoDescription.setText(Html.fromHtml(shortCaption));
                mPhotoDescription.setClickable(true);
                mPhotoDescription.setOnClickListener(this);
            } else {
                mPhotoDescription.setClickable(false);
                mPhotoDescription.setText(fullCaption);
            }
            mPhotoDescription.setVisibility(View.VISIBLE);

        } else {
            mPhotoDescription.setVisibility(View.GONE);
            mPhotoDescription.setText("");
        }
    }

    private String getTaggedBody(String description, JSONArray actionTypeBodyParams) {
        mClickableParts = new HashMap<>();

        int order = 1, id;
        String type, keyForClick;

        // Make Tagged Friends Name Clickable
        for (int j = 0; j < actionTypeBodyParams.length(); j++) {

            if (j < 2) {
                JSONObject actionBodyObject = actionTypeBodyParams.optJSONObject(j);
                String label = actionBodyObject.optString("text");
                id = actionBodyObject.optInt("tag_id");
                type = actionBodyObject.optString("tag_type");

                keyForClick = order + "-" + type + "-" + id;
                label = label.replaceAll("\\s+", " ").trim();
                if (mClickableParts.containsKey(keyForClick)) {
                    keyForClick += "-" + label;
                }
                mClickableParts.put(keyForClick, label);
                ++order;

                if (j == 0) {
                    description += " - " + getResources().getString(R.string.with_tag_text) + " " + "<b>" + label + "</b>";
                } else if (j == 1 && actionTypeBodyParams.length() == 2) {
                    description += " " + getResources().getString(R.string.and)+ " " +  "<b>" + label + "</b>";
                }
            } else if (j == 2) {
                String label = actionTypeBodyParams.length()-1 + " " + getResources().getString(R.string.others_tag_text);
                id = 0;
                type = "tagged_users_list";

                keyForClick = order + "-" + type + "-" + id;
                label = label.replaceAll("\\s+", " ").trim();
                if (mClickableParts.containsKey(keyForClick)) {
                    keyForClick += "-" + label;
                }
                mClickableParts.put(keyForClick, label);

                ++order;
                description +=  " " + getResources().getString(R.string.and)+ " " + "<b>" + label + "</b>";
            }
        }
        return description;
    }

    private void redirectToActivity(String type, int id) {
        if (!type.equals("tagged_users_list")) {
            Intent viewIntent = new Intent(mContext, userProfile.class);
            viewIntent.putExtra(ConstantVariables.USER_ID, id);
            startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            redirectToAddPeopleClass();
        }
    }

    private void redirectToAddPeopleClass() {
        Intent addPeopleIntent = new Intent(PhotoLightBoxActivity.this, AddPeople.class);
        if (mTagJsonArray != null) {
            addPeopleIntent.putExtra("userTagArray", mTagJsonArray.toString());
        }
        addPeopleIntent.putExtra("isPhotoTag", true);
        addPeopleIntent.putExtra(ConstantVariables.SUBJECT_ID, mSubjectId);
        addPeopleIntent.putExtra(ConstantVariables.SUBJECT_TYPE, "album_photo");
        startActivityForResult(addPeopleIntent, ConstantVariables.ADD_PEOPLE_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setLikeCommentView(){
        /**
         * If Reactions and NestedComments plugin is enabled
         *
         * -- Show Reactions Image and Reactions Text
         * -- Show Popular Reactions
         * -- else show Like Icon only as Like Button
         */

        try {
            if(photoListDetails.getmReactionsObject() != null && !photoListDetails.getmReactionsObject().isEmpty()){
                mContentReactionsObject = new JSONObject(photoListDetails.getmReactionsObject());
                mPhotoReactions = mContentReactionsObject.optJSONObject("feed_reactions");
                myReactions = mContentReactionsObject.optJSONObject("my_feed_reaction");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        if(!photoListDetails.isLiked()){
            if(isReactionsEnabled && isNestedCommentsEnabled){
                likeButton.setText(getResources().getString(R.string.like_text));
            } else{
                likeButton.setText(getResources().getString(R.string.blank_string));
            }
            mReactionIcon.setVisibility(View.GONE);
            likeButton.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                    null, null, null);
            likeButton.setActivated(false);
            likeButton.setTextColor(
                    ContextCompat.getColor(this, R.color.white));
        } else {
            /*
                If Reactions and Nestedcomments are enabled
                    -- Show Reaction Icon and Reactions Text On Like Button
                else
                    -- Show Only Like Icon
             */
            likeButton.setActivated(true);
            likeButton.setTextColor(
                    ContextCompat.getColor(this, R.color.themeButtonColor));

            if(isReactionsEnabled && isNestedCommentsEnabled){
                if (myReactions != null && myReactions.length() != 0) {
                    String reactionImage = myReactions.optString("reaction_image_icon");

                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    mReactionIcon.setVisibility(View.VISIBLE);
                    mImageLoader.setImageUrl(reactionImage, mReactionIcon);
                    likeButton.setText(myReactions.optString("caption"));
                } else {
                    mReactionIcon.setVisibility(View.GONE);
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    likeButton.setText(this.getResources().
                            getString(R.string.like_text));
                }
            } else{
                likeButton.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                likeButton.setText(getResources().getString(R.string.blank_string));
            }
        }


        if(isReactionsEnabled && isNestedCommentsEnabled) {

            // Show 3 popular reactions
            if (mPhotoReactions != null && mPhotoReactions.length() != 0) {
                popularReactionsView.removeAllViews();
                JSONArray reactionIds = mPhotoReactions.names();
                popularReactionsView.setVisibility(View.VISIBLE);
                for (int i = 0; i < mPhotoReactions.length() && i < 3; i++) {
                    String imageUrl = mPhotoReactions.optJSONObject(reactionIds.optString(i)).
                            optString("reaction_image_icon");
                    int reactionId = mPhotoReactions.optJSONObject(reactionIds.optString(i)).
                            optInt("reactionicon_id");

                    ImageView imageView = CustomViews.generateReactionImageView(this, reactionId, imageUrl);

                    popularReactionsView.addView(imageView);
                }
            } else {
                popularReactionsView.setVisibility(View.GONE);
            }
        } else {
            popularReactionsView.setVisibility(View.GONE);
        }

        // Set Like and comment count
        mPhotoLikeCount = photoListDetails.getImageLikeCount();
        mPhotoCommentCount = photoListDetails.getImageCommentCount();

        if(mPhotoLikeCount != 0) {
            mLikeCount.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mCommentCount.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            mCommentCount.setLayoutParams(layoutParams);

            // Set Like Count
            if(isReactionsEnabled && isNestedCommentsEnabled){
                // You and count others
                if(photoListDetails.isLiked()){
                    if(mPhotoLikeCount == 1){
                        mLikeCount.setText(getResources().getString(R.string.reaction_string));
                    } else {
                        String likeText = getResources().getQuantityString(R.plurals.others,
                                mPhotoLikeCount - 1, mPhotoLikeCount - 1);
                        mLikeCount.setText(String.format(getResources().getString(R.string.reaction_text_format),
                                getResources().getString(R.string.you_and_text), likeText
                        ));
                    }
                } else {
                    // Only count
                    mLikeCount.setText(Integer.toString(mPhotoLikeCount));
                }
            } else {
                String likeText = getResources().getQuantityString(R.plurals.profile_page_like,
                        mPhotoLikeCount);
                mLikeCount.setText(String.format(getResources().getString(R.string.like_count_text),
                        mPhotoLikeCount, likeText));
            }
        }else {
            mLikeCount.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mCommentCount.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            mCommentCount.setLayoutParams(layoutParams);
        }

        if(mPhotoCommentCount != 0) {
            mCommentCount.setVisibility(View.VISIBLE);
            String commentText = getResources().getQuantityString(R.plurals.profile_page_comment,
                    mPhotoCommentCount);
            mCommentCount.setText(Html.fromHtml(String.format(
                    getResources().getString(R.string.comment_count_text),
                    mPhotoCommentCount, commentText
            )));
        }else {
            mCommentCount.setVisibility(View.GONE);
        }
    }

    public void setOptionsVisibility(){

        if(mAppConst.isLoggedOutUser() || !showOptions) {
            mLikeCount.setVisibility(View.GONE);
            mCommentCount.setVisibility(View.GONE);
            commentButton.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
            tagButton.setVisibility(View.GONE);
            optionMenu.setVisibility(View.INVISIBLE);
        }else {
            setLikeCommentView();
        }

        if(showAlbumPage){
            optionMenu.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Method to show comment count if any.
     * @param commentCount No. of comment count.
     */
    public void setCommentCount(int commentCount) {
        if(commentCount != 0) {
            mCommentCount.setVisibility(View.VISIBLE);
            String commentText = getResources().getQuantityString(R.plurals.profile_page_comment,
                    commentCount);
            mCommentCount.setText(Html.fromHtml(String.format(
                    getResources().getString(R.string.comment_count_text),
                    commentCount, commentText
            )));
        }else {
            mCommentCount.setVisibility(View.GONE);
        }
    }

    private String getSubjectType(){

        LogUtils.LOGD(PhotoLightBoxActivity.class.getSimpleName(), "currentSelectedOption: "+currentSelectedOption);
        String subjectType;
        switch(currentSelectedOption){
            case "core_main_group" :
                subjectType = "group_photo";
                break;

            case "core_main_event" :
                subjectType = "event_photo";
                break;

            case "core_main_album" :
                subjectType = "album_photo";
                break;

            case "core_main_classified" :
                subjectType = "classified_photo";
                break;
            case "core_main_siteevent":
                subjectType = "siteevent_photo";
                break;
            case "sitereview_listing":
            case "sitereview_wishlist":
                subjectType = "sitereview_photo";
                break;
            case "core_main_sitegroup":
                subjectType = "sitegroup_photo";
                break;
            case "core_main_sitepage":
                subjectType = "sitepage_photo";
                break;
            case ConstantVariables.PRODUCT_MENU_TITLE:
                subjectType = "sitestoreproduct_photo";
                break;
            case ConstantVariables.STORE_MENU_TITLE:
                subjectType = "sitestore_photo";
                break;
            case ConstantVariables.ADV_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                subjectType = "sitevideo_photo";
                break;
            default:
                subjectType = "album_photo";
        }

        if (getIntent().hasExtra(ConstantVariables.SUBJECT_TYPE)) {
            subjectType = getIntent().getStringExtra(ConstantVariables.SUBJECT_TYPE);
        }

        return subjectType;
    }

    public void makeRequest(){

        ++pageNo;
        mRequestUrl += "&page=" + pageNo;
        mAppConst.getJsonResponseFromUrl(mRequestUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                //Main body or response from the server
                mBody = jsonObject;
                //Adding the contents in the list so the adapter can show them in photoLightBox
                addPhotosToList();

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(mSlidingLayer, message);
            }
        });

    }

    public void addPhotosToList(){
        String image_title;
        try {
            switch (currentSelectedOption) {
                case "core_main_feed":
                case "core_main_album":
                    mDataResponseArray = mBody.optJSONArray("photos");
                    if (mDataResponseArray == null) {
                        mDataResponseArray = mBody.optJSONArray("images");
                    }
                    mTotalItemCount = mBody.optInt("totalPhotoCount");
                    break;

                case "core_main_event":
                case "core_main_group":
                    mDataResponseArray = mBody.optJSONArray("images");
                    mTotalItemCount = mBody.optInt("totalItemCount");
                    break;

                default:
                    break;

            }

            if (mDataResponseArray != null) {
                for (int i = 0; i < mDataResponseArray.length(); i++) {
                    JSONObject imageUrlsObj = mDataResponseArray.getJSONObject(i);
                    albumTitle = imageUrlsObj.optString("album_title");
                    owner_title = imageUrlsObj.optString("owner_title");
                    String menuArray = imageUrlsObj.optString("menu");
                    image_title = imageUrlsObj.optString("title");
                    String image_desc = imageUrlsObj.optString("description");
                    normalImgUrl = imageUrlsObj.optString("image");
                    int photo_id = imageUrlsObj.optInt("photo_id");
                    int likeCount = imageUrlsObj.optInt("like_count");
                    int commentCount = imageUrlsObj.optInt("comment_count");
                    boolean likeStatus = imageUrlsObj.optBoolean("is_like");
                    mPhotoDetails.add(new PhotoListDetails(albumTitle, owner_title, image_title,
                            image_desc,photo_id, normalImgUrl, likeCount, commentCount, likeStatus,
                            menuArray));
                }
                int newPosition = viewPager.getCurrentItem();
                fullScreenImageAdapter.notifyDataSetChanged();
                viewPager.setAdapter(fullScreenImageAdapter);
                viewPager.setCurrentItem(newPosition);
                mAppConst.hideProgressDialog();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            mAppConst.hideProgressDialog();
            SnackbarUtils.displaySnackbarLongTime(mSlidingLayer,
                    getResources().getString(R.string.no_data_available));

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantVariables.CREATE_REQUEST_CODE :
                if(data != null && resultCode != 0) {
                    data.putExtra(ConstantVariables.IS_CONTENT_EDITED, true);
                    setResult(ConstantVariables.LIGHT_BOX_EDIT,data);
                    finish();
                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                }
                break;

            // When Comment page is opened for any photo
            // and any changes occured at Comment page then update the comment count for the clicked photo.
            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {
                    int position = data.getIntExtra(ConstantVariables.PHOTO_POSITION, 0);
                    PhotoListDetails photoListDetails = mPhotoDetails.get(position);
                    mPhotoCommentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mPhotoCommentCount);
                    photoListDetails.setmImageCommentCount(mPhotoCommentCount);
                    setCommentCount(mPhotoCommentCount);
                    fullScreenImageAdapter.notifyDataSetChanged();
                }
                break;

            case ConstantVariables.ADD_PEOPLE_CODE:
                if (data != null && resultCode == ConstantVariables.ADD_PEOPLE_CODE) {
                    if (data.hasExtra("tagArray")) {
                        String tag = data.getStringExtra("tagArray");
                        if (tag != null && !tag.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(tag);
                                setTagViewContent(photoDescription, jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        setTagViewContent(photoDescription, null);
                    }
                }
                break;

        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {

            case R.id.navigation_back:
                updateOnBackPress();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(PhotoLightBoxActivity.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(PhotoLightBoxActivity.this);
                }
                break;

            case R.id.optionMenu:
                if (mOptionMenuArray != null || showAlbumPage) {
                    showMenus(view);
                }
                break;

            case R.id.shareButton:
                shareImageWithInApp(view);
                break;

            case R.id.likeButton:
                /**
                 * Apply animation on like button
                 */
                likeButton.startAnimation(AppConstant.getZoomInAnimation(this));
                int reactionId = 0;
                String reactionIcon = null, caption = null;

                if(isReactionsEnabled && mReactionsObject != null ){
                    reactionId = mReactionsObject.optJSONObject("like").optInt("reactionicon_id");
                    reactionIcon = mReactionsObject.optJSONObject("like").optJSONObject("icon").
                            optString("reaction_image_icon");
                    caption = getResources().getString(R.string.like_text);
                }
                doLikeUnlike(null, false, reactionId, reactionIcon, caption);
                break;

            case R.id.commentButton:
            case R.id.counts_container:

                mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type="
                        + getSubjectType() + "&subject_id=" + mSubjectId + "&viewAllComments=1";
                Intent commentIntent = new Intent(this, Comment.class);
                commentIntent.putExtra(ConstantVariables.IS_PHOTO_COMMENT, true);
                commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                commentIntent.putExtra(ConstantVariables.PHOTO_POSITION, viewPager.getCurrentItem());
                commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE,getSubjectType() );
                commentIntent.putExtra(ConstantVariables.SUBJECT_ID, mSubjectId);
                startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.editOption:
                mPosition = (int) editOption.getTag();
                if(isEditable == 0){
                    if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }else{
                        new SaveImageAsync(this,mCurrentImageUrl).execute();
                    }
                }else {
                    if(!mAppConst.isLoggedOutUser())
                        mSlidingLayer.openLayer(true);
                }

                break;

            case R.id.rotateLeft:
                sendImageRotationRequest("left");
                isContentEdited = true;
                break;

            case R.id.rotateRight:
                sendImageRotationRequest("right");
                isContentEdited = true;
                break;

            case R.id.flipHorizontal:
                sendImageRotationRequest("horizontal");
                isContentEdited = true;
                break;

            case R.id.flipVertical:
                sendImageRotationRequest("vertical");
                isContentEdited = true;
                break;

            case R.id.saveImage:
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    new SaveImageAsync(this,mCurrentImageUrl).execute();
                }
                break;

            case R.id.tagButton:
                redirectToAddPeopleClass();
                break;

            case R.id.plb_photoDescription:
                if (!isTextExpanded) {
                    mPhotoDescription.setText(fullCaption);
                } else {
                    mPhotoDescription.setText(Html.fromHtml(shortCaption));
                }
                isTextExpanded = !isTextExpanded;
                break;

        }

    }

    public void shareImageWithInApp(View view){

        Map<String, String> shareParams = new HashMap<>();

        if (mOptionMenuArray != null && mOptionMenuArray.length() != 0) {

            for (int i = 0; i < mOptionMenuArray.length(); i++) {

                try {
                    menuJsonObject = mOptionMenuArray.getJSONObject(i);
                    if(menuJsonObject.getString("name").equals("share")) {
                        //Setting up the share button tag equal to menu button id
                        mOptionMenuName = menuJsonObject.getString("name");
                        JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                        redirectUrl = AppConstant.DEFAULT_URL + menuJsonObject.getString("url");

                        if (urlParams != null && urlParams.length() != 0) {
                            JSONArray urlParamsNames = urlParams.names();
                            for (int j = 0; j < urlParams.length(); j++) {

                                String name = urlParamsNames.getString(j);
                                String value = urlParams.getString(name);
                                shareParams.put(name, value);
                            }

                            redirectUrl = mAppConst.buildQueryString(redirectUrl, shareParams);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }  else {
            String url = mAppConst.DEFAULT_URL + "activity/share";
            shareParams.put("type", getSubjectType());
            shareParams.put("id", String.valueOf(mSubjectId));

            redirectUrl = mAppConst.buildQueryString(url, shareParams);
        }
        if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            isShareClicked = true;
            mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
        }else{
            isShareClicked = false;
            socialShareUtil.sharePost(view, null, mCurrentImageUrl, redirectUrl, ConstantVariables.KEY_SHARE_TYPE_MEDIA, mAttachmentUri);
        }

    }

    public void sendImageRotationRequest(String rotation) {
        mAppConst.showProgressDialog();
        editParams = new HashMap<>();
        switch (rotation) {
            case "left":
                editParams.put("angle", String.valueOf(90));
                break;

            case "right":
                editParams.put("angle", String.valueOf(270));
                break;

            case "horizontal":
                editParams.put("direction", "horizontal");
                break;

            case "vertical":
                editParams.put("direction", "vertical");
                break;
        }

        switch (currentSelectedOption) {
            case "core_main_album":
                editImageUrl = AppConstant.DEFAULT_URL + "albums/photo/rotate?photo_id="+currentImageId;
                break;
            case "core_main_group":
                editImageUrl = AppConstant.DEFAULT_URL + "groups/photo/rotate?photo_id="+currentImageId;
                break;
            case "core_main_event":
                editImageUrl = AppConstant.DEFAULT_URL + "events/photo/rotate?photo_id="+currentImageId;
                break;

        }

        mAppConst.postJsonResponseForUrl(editImageUrl, editParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mAppConst.hideProgressDialog();
                if (jsonObject != null) {
                    String rotatedImage = jsonObject.optString("image");
                    photoListDetails.setImageUrl(rotatedImage);
                    mPhotoDetails.set(currentPosition, photoListDetails);
                    fullScreenImageAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(fullScreenImageAdapter);
                    viewPager.setCurrentItem(mPosition);

                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(mSlidingLayer, message);
            }
        });
    }

    public void showMenus(final View view) {

        mBrowseList = new BrowseListItems(mSubjectId);

        mGutterMenuUtils.showPopup(mSlidingLayer, view, mOptionMenuArray, mBrowseList,
                currentSelectedOption, "light_box", mListingTypeId);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        return true;
    }

    @Override
    public void onLongPressed(View view) {
        if (mOptionMenuArray != null || showAlbumPage) {
            showMenus(mLikeCommentCountsContainer);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int[] location = new int[2];
        mLikeCommentCountsContainer.getLocationOnScreen(location);
        RecyclerView reactionsRecyclerView = new RecyclerView(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reactionsRecyclerView.setHasFixedSize(true);
        reactionsRecyclerView.setLayoutManager(linearLayoutManager);
        reactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final PopupWindow popUp = new PopupWindow(reactionsRecyclerView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape));
        popUp.setTouchable(true);
        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.setAnimationStyle(R.style.customDialogAnimation);

        // Playing popup effect when user long presses on like button of a feed.
        if (PreferencesUtils.isSoundEffectEnabled(this)) {
            SoundUtil.playSoundEffectOnReactionsPopup(this);
        }
        popUp.showAtLocation(reactionsRecyclerView, Gravity.TOP, location[0], location[1]);

        if (mReactionsObject != null && mReactionsArray != null) {

            reactionsImages = new ArrayList<>();
            for (int i = 0; i < mReactionsArray.size(); i++) {
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

            ImageAdapter reactionsAdapter = new ImageAdapter(this, reactionsImages, true,
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
                            if (myReactions != null) {
                                if (myReactions.optInt("reactionicon_id") != reactionId) {
                                    doLikeUnlike(reaction, true, reactionId, reactionIcon, caption);
                                }
                            } else {
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
        SnackbarUtils.displaySnackbarShortWithListener(mSlidingLayer, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        Intent intent = new Intent();
                        intent.putExtra(DELETED_POSITION,currentPosition);
                        setResult(ConstantVariables.LIGHT_BOX_DELETE, intent);
                        finish();
                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                    }
                });
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;

        switch (menuName) {
            case "make_profile_photo":
                isContentEdited = true;
                break;

            case "view_album":
                Intent intent = null;

                intent = AlbumUtil.getViewPageIntent(this, albumId, mRequestUrl, new Bundle());
                if(intent != null){
                    if (getSubjectType().contains("_photo") && !getSubjectType().equals("album_photo")) {
                        mRequestUrl += "&subject_type=" + getSubjectType().replace("_photo", "_album");
                        intent.putExtra(ConstantVariables.SUBJECT_TYPE, getSubjectType());
                    }
                    intent.putExtra(ConstantVariables.VIEW_PAGE_URL, mRequestUrl);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                break;
        }
    }


    public class SaveImageAsync extends AsyncTask<Void,String,Void>{

        private Context mContext;
        private String imgUrl;

        private ProgressDialog mProgressDialog;

        public SaveImageAsync(Context context, String url) {
            mContext = context;
            imgUrl = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(getApplicationContext().getResources().
                    getString(R.string.save_image_sd_card));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            Bitmap finalBitmap = BitmapUtils.getBitmapFromURL(imgUrl);
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + mContext.getResources().getString(R.string.app_name));
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fName = "Image-"+ n +".jpg";
            File file = new File(myDir, fName);
            try {
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                assert finalBitmap != null;
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteOutputStream);
                byte[] mbitmapdata = byteOutputStream.toByteArray();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(mbitmapdata);
                OutputStream outputStream = new FileOutputStream(file);
                byteOutputStream.writeTo(outputStream);

                byte[] buffer = new byte[1024]; //Use 1024 for better performance
                int lengthOfFile = mbitmapdata.length;
                int totalWritten = 0;
                int bufferedBytes;

                while ((bufferedBytes = inputStream.read(buffer)) > 0) {
                    totalWritten += bufferedBytes;
                    publishProgress(Integer.toString(((totalWritten * 100) / lengthOfFile)));
                    outputStream.write(buffer, 0, bufferedBytes);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(mContext, new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void filename) {
            mProgressDialog.dismiss();
            SnackbarUtils.displaySnackbar(mSlidingLayer,
                    getResources().getString(R.string.save_img_msg));
            mProgressDialog = null;
        }
    }

    public void closeSlidingLayer(){
        if(mSlidingLayer.isOpened() ){
            mSlidingLayer.closeLayer(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(!isShareClicked) {
                        // permission was granted, proceed to the normal flow
                        new SaveImageAsync(this, mCurrentImageUrl).execute();
                    }
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

                        SnackbarUtils.displaySnackbarOnPermissionResult(PhotoLightBoxActivity.this,
                                mSlidingLayer, ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        updateOnBackPress();
        super.onBackPressed();
    }

    public void updateOnBackPress(){
        Intent intent = new Intent();
        intent.putExtra(ConstantVariables.IS_CONTENT_EDITED, isContentEdited);
        intent.putExtra(ConstantVariables.ITEM_POSITION, mPhotoPosition);
        intent.putExtra(ConstantVariables.IS_LIKED, photoListDetails.isLiked());
        intent.putExtra(ConstantVariables.PHOTO_LIKE_COUNT, mPhotoLikeCount);
        intent.putExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mPhotoCommentCount);
        // Add My Photo and popular Photo reactions
        if(mContentReactionsObject != null){
            intent.putExtra(ConstantVariables.MY_PHOTO_REACTIONS, mContentReactionsObject.optString("my_feed_reaction"));
            intent.putExtra(ConstantVariables.PHOTO_POPULAR_REACTIONS, mContentReactionsObject.optString("feed_reactions"));
        }
        if (getIntent().getExtras().getBoolean(ConstantVariables.IS_ALBUM_PHOTO_REQUEST)) {
            intent.putExtra(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
        }
        setResult(ConstantVariables.LIGHT_BOX_EDIT, intent);
        finish();
    }


    private void doLikeUnlike(String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption){


        isContentEdited = true;
        final String sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, getSubjectType());
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mSubjectId));

        if(reaction != null){
            likeParams.put("reaction", reaction);
        }

        /**
         * If Photo is not already liked...
         *  -- Increase the Like count and change color of Like Button
         *  -- Remove the left drawable from Like Button and show the reaction image and reaction text on Like Button
         *  -- if reactions and nestedcomment is enabled and show the popular reactions
         *
         *  Else If photo is already Like and Reaction is changed on that photo
         *   -- Just change the myReaction object and change the reaction icon and text on like Button
         *   -- Show the updated 3 popular reactions.
         *
         */
        if(!photoListDetails.isLiked() || isReactionChanged){
            if(isReactionsEnabled && isNestedCommentsEnabled){
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "advancedcomments/like?sendNotification=0";
                updateContentReactions(reactionId, reactionIcon, caption, photoListDetails.isLiked(),
                        isReactionChanged);
            } else{
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "like?sendNotification=0";
            }
            if(!isReactionChanged){
                mPhotoLikeCount += 1;
                photoListDetails.setImageLikeCount(mPhotoLikeCount);
            }
            photoListDetails.setIsLiked(true);
            setLikeCommentView();
        } else {
            mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "unlike";
            if(isReactionsEnabled && isNestedCommentsEnabled){
                updateContentReactions(reactionId, reactionIcon, caption, photoListDetails.isLiked(), false);
            }
            mPhotoLikeCount -= 1;
            photoListDetails.setImageLikeCount(mPhotoLikeCount);
            photoListDetails.setIsLiked(false);
            setLikeCommentView();
        }

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if(isReactionsEnabled && isNestedCommentsEnabled){

                    /* Calling to send notifications after like action */
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

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mSlidingLayer, message);
            }
        });

    }

    private void updateContentReactions(int reactionId, String reactionIcon, String caption,
                                        boolean isLiked, boolean isReactionChanged){

        try{


            if(mContentReactionsObject == null){
                mContentReactionsObject = new JSONObject();
            }

            // Update the count of previous reaction in reactions object and remove the my_feed_reactions
            if(isLiked){
                if(myReactions != null && mPhotoReactions != null){
                    int myReactionId = myReactions.optInt("reactionicon_id");
                    if(mPhotoReactions.optJSONObject(String.valueOf(myReactionId)) != null){
                        int myReactionCount = mPhotoReactions.optJSONObject(String.valueOf(myReactionId)).
                                optInt("reaction_count");
                        if((myReactionCount - 1) <= 0){
                            mPhotoReactions.remove(String.valueOf(myReactionId));
                        } else {
                            mPhotoReactions.optJSONObject(String.valueOf(myReactionId)).put("reaction_count",
                                    myReactionCount - 1);
                        }
                        mContentReactionsObject.put("feed_reactions", mPhotoReactions);
                    }
                }
                mContentReactionsObject.put("my_feed_reaction", null);
            }

            // Update the count of current reaction in reactions object and set new my_feed_reactions object.
            if(!isLiked || isReactionChanged){
                // Set the updated my Reactions

                JSONObject jsonObject = new JSONObject();
                jsonObject.putOpt("reactionicon_id", reactionId);
                jsonObject.putOpt("reaction_image_icon", reactionIcon);
                jsonObject.putOpt("caption", caption);
                mContentReactionsObject.put("my_feed_reaction", jsonObject);

                if (mPhotoReactions != null) {
                    if (mPhotoReactions.optJSONObject(String.valueOf(reactionId)) != null) {
                        int reactionCount = mPhotoReactions.optJSONObject(String.valueOf(reactionId)).optInt("reaction_count");
                        mPhotoReactions.optJSONObject(String.valueOf(reactionId)).putOpt("reaction_count", reactionCount + 1);
                    } else {
                        jsonObject.put("reaction_count", 1);
                        mPhotoReactions.put(String.valueOf(reactionId), jsonObject);
                    }
                } else {
                    mPhotoReactions = new JSONObject();
                    jsonObject.put("reaction_count", 1);
                    mPhotoReactions.put(String.valueOf(reactionId), jsonObject);
                }
                mContentReactionsObject.put("feed_reactions", mPhotoReactions);
            }

            photoListDetails.setmReactionsObject(mContentReactionsObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
