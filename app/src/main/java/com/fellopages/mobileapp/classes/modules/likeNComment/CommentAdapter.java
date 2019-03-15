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

package com.fellopages.mobileapp.classes.modules.likeNComment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
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
import java.util.SortedSet;
import java.util.TreeSet;

import github.ankushsachdeva.emojicon.EmojiconTextView;


public class CommentAdapter extends ArrayAdapter<CommentList> implements OnMenuClickResponseListener {

    private Context mContext;
    private List<CommentList> mCommentListItems;
    private CommentList mCommentList, mListComment;
    private int mLayoutResID, mPosition, itemPosition, mSubjectId, mActionId, mLikeCountPosition;
    private View mRootView;
    private JSONObject mLikeOptionArray, mDeleteOptionArray;
    private String mLikeCommentUrl, mDeleteCommentUrl, mSubjectType;
    private AppConstant mAppConst;
    private boolean isCommentPage;
    private boolean likeUnlikeAction = true;
    private GutterMenuUtils mGutterMenuUtils;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;
    private ArrayList<PhotoListDetails> mPhotoDetails;

    public CommentAdapter(Context context, int resource, List<CommentList> listItems,
                          CommentList commentList, boolean commentPage, String subject_type,
                          int subject_id, int action_id) {

        super(context, resource, listItems);
        mContext = context;
        mCommentListItems = listItems;
        mListComment = commentList;
        mLayoutResID = resource;
        isCommentPage = commentPage;

        mSubjectType = subject_type;
        mSubjectId = subject_id;
        mActionId = action_id;

        mAppConst = new AppConstant(context);
        mGutterMenuUtils = new GutterMenuUtils(context);
        mAlertDialogWithAction = new AlertDialogWithAction(context);
        mImageLoader = new ImageLoader(mContext);
        mPhotoDetails = new ArrayList<>();

    }

    public CommentAdapter(Context context, int resource, List<CommentList> listItems, CommentList commentList,
                          boolean commentPage) {

        super(context, resource, listItems);
        mContext = context;
        mCommentListItems = listItems;
        mListComment = commentList;
        mLayoutResID = resource;
        isCommentPage = commentPage;

        mAppConst = new AppConstant(context);
        mGutterMenuUtils = new GutterMenuUtils(context);
        mAlertDialogWithAction = new AlertDialogWithAction(context);
        mImageLoader = new ImageLoader(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        mRootView = convertView;
        mCommentList = mCommentListItems.get(position);
        final ListItemHolder listItemHolder;
        if(mRootView == null){

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);

            listItemHolder.mAuthorImage = mRootView.findViewById(R.id.authorImage);
            listItemHolder.mAuthorTitle = mRootView.findViewById(R.id.authorTitle);
            listItemHolder.mCommentBody = mRootView.findViewById(R.id.commentBody);
            listItemHolder.mCommentDate = mRootView.findViewById(R.id.commentDate);
            listItemHolder.mPostingText = mRootView.findViewById(R.id.postingText);
            listItemHolder.mStickerImage = mRootView.findViewById(R.id.stickerImage);
            listItemHolder.mGifIV = mRootView.findViewById(R.id.gif_icon);
            listItemHolder.mAttchmentView = mRootView.findViewById(R.id.attachment_imageview);

            listItemHolder.mCommentOptionsBlock = mRootView.findViewById(R.id.commentOptionsBlock);
            listItemHolder.mCommentLikeCount = mRootView.findViewById(R.id.commentLikeCount);
            listItemHolder.mCommentLikeCount.setTag(position);

            listItemHolder.mMemberOptions = mRootView.findViewById(R.id.memberOption);
            listItemHolder.mMemberOptions.setTag(position);
            listItemHolder.mMemberOptions.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            listItemHolder.mLikeOption = mRootView.findViewById(R.id.likeOption);
            listItemHolder.mLikeOption.setTag(position);

            listItemHolder.mDeleteOption = mRootView.findViewById(R.id.deleteOption);
            listItemHolder.mDeleteOption.setTag(position);

            listItemHolder.mReactionImage = mRootView.findViewById(R.id.reactionIcon);

            mRootView.setTag(listItemHolder);
            listItemHolder.mCommentBody.setTag(position);
        } else {
            listItemHolder = (ListItemHolder)mRootView.getTag();
            listItemHolder.mLikeOption.setTag(position);
            listItemHolder.mMemberOptions.setTag(position);
            listItemHolder.mCommentLikeCount.setTag(position);
            if (listItemHolder.mDeleteOption != null) {
                listItemHolder.mDeleteOption.setTag(position);
            }
            if (listItemHolder.mCommentBody != null) {
                listItemHolder.mCommentBody.setTag(position);
                listItemHolder.mCommentBody.setVisibility(View.GONE);
            }
        }

        listItemHolder.mLikeOption.setClickable(true);
        listItemHolder.mLikeOption.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

        listItemHolder.mCommentLikeCount.setClickable(true);
        listItemHolder.mCommentLikeCount.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

        listItemHolder.mDeleteOption.setClickable(true);
        listItemHolder.mDeleteOption.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));


        /*
        Set Data in the List View Items
         */

        final int userId = mCommentList.getmUserId();

        mImageLoader.setImageForUserProfile(mCommentList.getmAuthorPhoto(), listItemHolder.mAuthorImage);

        listItemHolder.mAuthorTitle.setText(mCommentList.getmAuthorTitle());

        if(isCommentPage){

            listItemHolder.mMemberOptions.setVisibility(View.GONE);
            listItemHolder.mAuthorImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent userProfileIntent = new Intent(mContext, userProfile.class);
                    userProfileIntent.putExtra("user_id", userId);
                    ((Activity) mContext).startActivityForResult(userProfileIntent, ConstantVariables.
                            USER_PROFILE_CODE);
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            listItemHolder.mAuthorTitle.setClickable(true);
            listItemHolder.mAuthorTitle.setMovementMethod(LinkMovementMethod.getInstance());
            listItemHolder.mAuthorTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent userProfileIntent = new Intent(mContext, userProfile.class);
                    userProfileIntent.putExtra("user_id", userId);
                    ((Activity) mContext).startActivityForResult(userProfileIntent, ConstantVariables.
                            USER_PROFILE_CODE);
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        } else {
            int marginTop = (int) (mContext.getResources().getDimension(R.dimen.margin_40dp) /
                    mContext.getResources().getDisplayMetrics().density);

            LinearLayout.LayoutParams layoutParams = CustomViews.getFullWidthLayoutParams();
            layoutParams.setMargins(0, marginTop, 0, 0);
            listItemHolder.mAuthorTitle.setLayoutParams(layoutParams);

            if(mCommentList.getmReactionIcon() != null && !mCommentList.getmReactionIcon().isEmpty()){
                mImageLoader.setImageUrl(mCommentList.getmReactionIcon(), listItemHolder.mReactionImage);
                listItemHolder.mReactionImage.setVisibility(View.VISIBLE);

            } else{
                listItemHolder.mReactionImage.setVisibility(View.GONE);
            }

        }


        listItemHolder.mCommentLikeCount.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        if(isCommentPage) {
            listItemHolder.mMemberOptions.setVisibility(View.GONE);

            if(mCommentList.getmCommentBody() != null && !mCommentList.getmCommentBody().isEmpty()){
                listItemHolder.mCommentBody.setVisibility(View.VISIBLE);
                listItemHolder.mCommentBody.setMovementMethod(LinkMovementMethod.getInstance());
                listItemHolder.mCommentBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        CommentList commentList = mCommentListItems.get(position);
                        if (commentList.getmCommentBody().trim().length()
                                > ConstantVariables.FEED_TITLE_BODY_LENGTH) {
                            commentList.showFullComment(!commentList.isFullCommentShowing());
                            notifyDataSetChanged();
                        }
                    }
                });

                HashMap<String, String> clickableParts = mCommentList.getmClickableStringsList();
                String commentBody;
                if (mCommentList.getmCommentBody().trim().length()
                        > ConstantVariables.FEED_TITLE_BODY_LENGTH) {
                    if (mCommentList.isFullCommentShowing()) {
                        commentBody = mCommentList.getmCommentBody().substring(0,
                                ConstantVariables.FEED_TITLE_BODY_LENGTH).concat("..."
                                + mContext.getResources().getString(R.string.more));
                    } else {
                        commentBody = mCommentList.getmCommentBody().concat("..."
                                + mContext.getResources().getString(R.string.readLess));
                    }
                } else {
                    commentBody = mCommentList.getmCommentBody();
                }

                // Show Clickable Parts and Apply Click Listener to redirect
                if (clickableParts != null && clickableParts.size() != 0) {
                    CharSequence title = Smileys.getEmojiFromString(Html.fromHtml(commentBody.replaceAll("\n", "<br/>")).toString());
                    SpannableString text = new SpannableString(title);
                    SortedSet<String> keys = new TreeSet<>(clickableParts.keySet());

                    int lastIndex = 0;
                    for (String key : keys) {

                        String[] keyParts = key.split("-");
                        final int attachment_id = Integer.parseInt(keyParts[2]);
                        final String value = clickableParts.get(key);

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
                                        redirectToActivity(attachment_id);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setUnderlineText(false);
                                        ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                                        ds.setFakeBoldText(true);
                                    }
                                };
                                text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                    seeMoreOption(listItemHolder.mCommentBody, title, text, mCommentList);
                } else {
                    CharSequence title = Smileys.getEmojiFromString(Html.fromHtml(commentBody.replaceAll("\n", "<br/>")).toString());
                    SpannableString text = new SpannableString(title);
                    seeMoreOption(listItemHolder.mCommentBody, title, text, mCommentList);
                }

            } else{
                listItemHolder.mCommentBody.setVisibility(View.GONE);
            }

            listItemHolder.imageUrl = mCommentList.getmStickerImage();

            if(mCommentList.getmImageBitmap() != null){
                listItemHolder.mAttchmentView.setVisibility(View.VISIBLE);
                int height = (int) mContext.getResources().getDimension(R.dimen.height_150dp);
                int width = mAppConst.getScreenWidth();
                LinearLayout.LayoutParams singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(width, height);
                listItemHolder.mAttchmentView.setLayoutParams(singleImageParam);
                listItemHolder.mGifIV.setVisibility(View.GONE);
                listItemHolder.mStickerImage.setImageBitmap(mCommentList.getmImageBitmap());

            } else if(listItemHolder.imageUrl != null && !listItemHolder.imageUrl.isEmpty()){
                LinearLayout.LayoutParams singleImageParam = getSingleImageParamFromWidthHeight(mCommentList.getmImageSize(), listItemHolder.imageUrl, listItemHolder.mGifIV, listItemHolder.mStickerImage);
                listItemHolder.mAttchmentView.setLayoutParams(singleImageParam);

                listItemHolder.mAttchmentView.setVisibility(View.VISIBLE);

                if (listItemHolder.imageUrl.contains(".gif")) {
                    listItemHolder.mGifIV.setVisibility(View.VISIBLE);
                } else {
                    listItemHolder.mGifIV.setVisibility(View.GONE);
                }

                mImageLoader.setStickerImage(listItemHolder.imageUrl, listItemHolder.mStickerImage, mCommentList.getmImageSize());

                listItemHolder.mGifIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listItemHolder.mGifIV.setVisibility(View.GONE);
                        mImageLoader.setAnimationImage(listItemHolder.imageUrl, listItemHolder.mStickerImage);
                    }
                });

                listItemHolder.mAttchmentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhotoDetails.clear();
                        mPhotoDetails.add(new PhotoListDetails(listItemHolder.imageUrl));
                        openLightBox();
                    }
                });

            } else{
                listItemHolder.mAttchmentView.setVisibility(View.GONE);
            }


            if(mCommentList.isShowPosting()){
                listItemHolder.mPostingText.setVisibility(View.VISIBLE);
                listItemHolder.mPostingText.setText(mContext.getResources().getString(R.string.comment_posting)
                        + "...");
                listItemHolder.mCommentOptionsBlock.setVisibility(View.GONE);

            }else{
                listItemHolder.mPostingText.setVisibility(View.GONE);
                listItemHolder.mCommentOptionsBlock.setVisibility(View.VISIBLE);

                String convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                        mCommentList.getmCommentDate());
                listItemHolder.mCommentDate.setText(convertedDate);

                if(mCommentList.getmLikeCount() > 0) {
                    listItemHolder.mCommentLikeCount.setText(String.format("\uF087 %d", mCommentList.getmLikeCount()));
                    listItemHolder.mCommentLikeCount.setVisibility(View.VISIBLE);
                }else{
                    listItemHolder.mCommentLikeCount.setVisibility(View.GONE);
                }

                listItemHolder.mLikeJsonObject = mCommentList.getmLikeJsonObject();
                if (listItemHolder.mLikeJsonObject != null) {
                    listItemHolder.mLikeOption.setVisibility(View.VISIBLE);
                    if (mCommentList.getmIsLike() == 0) {
                        listItemHolder.mLikeOption.setText(mContext.getString(R.string.like_text));
                    } else {
                        listItemHolder.mLikeOption.setText(mContext.getString(R.string.unlike));
                    }
                }else{
                    listItemHolder.mLikeOption.setVisibility(View.GONE);
                }

                listItemHolder.mDeleteJsonObject = mCommentList.getmDeleteJsonObject();
                if(mCommentList.getmDeleteJsonObject() != null) {
                    listItemHolder.mDeleteOption.setVisibility(View.VISIBLE);
                    listItemHolder.mDeleteOption.setText(mContext.getString(R.string.delete_dialogue_button));

                    listItemHolder.mDeleteOption.setClickable(true);
                }else {
                    listItemHolder.mDeleteOption.setVisibility(View.GONE);
                }
            }

            listItemHolder.mLikeOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    itemPosition = (int) view.getTag();
                    CommentList commentList = mCommentListItems.get(itemPosition);
                    int isLike = commentList.getmIsLike();

                    if (likeUnlikeAction) {
                        if (isLike == 0) {
                            listItemHolder.mLikeOption.setText(mContext.getString(R.string.unlike));
                            listItemHolder.mCommentLikeCount.setVisibility(View.VISIBLE);
                            listItemHolder.mCommentLikeCount.setText(
                                    String.format("\uF087 %d", commentList.getmLikeCount() + 1));

                        } else {
                            listItemHolder.mLikeOption.setText(mContext.getString(R.string.like_text));
                            listItemHolder.mCommentLikeCount.setText(
                                    String.format("\uF087 %d", commentList.getmLikeCount() - 1));
                            if(commentList.getmLikeCount() == 1){
                                listItemHolder.mCommentLikeCount.setVisibility(View.GONE);
                            }
                        }
                        doLikeUnlike();
                        likeUnlikeAction = false;
                    }
                }
            });

            listItemHolder.mDeleteOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPosition = (int) view.getTag();
                    deleteComment();
                }
            });

            listItemHolder.mCommentLikeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLikeCountPosition = (int) view.getTag();
                    viewCommentLikes();
                }
            });
        } else {
            listItemHolder.mCommentBody.setVisibility(View.GONE);
            listItemHolder.mPostingText.setVisibility(View.GONE);
            listItemHolder.mCommentOptionsBlock.setVisibility(View.GONE);

            // Showing friendship options if condition matches.
            if (mCommentList.getmFriendshipType() != null && !mCommentList.getmFriendshipType().isEmpty() &&
                    mCommentList.getmUserId() != 0) {
                listItemHolder.mMemberOptions.setVisibility(View.VISIBLE);
                switch (mCommentList.getmFriendshipType()) {
                    case "add_friend":
                    case "accept_request":
                    case "member_follow":
                        listItemHolder.mMemberOptions.setText("\uf234");
                        break;
                    case "remove_friend":
                    case "member_unfollow":
                        listItemHolder.mMemberOptions.setText("\uf235");
                        break;
                    case "cancel_request":
                    case "cancel_follow":
                        listItemHolder.mMemberOptions.setText("\uf00d");
                        break;
                }
                mGutterMenuUtils.setOnMenuClickResponseListener(this);
                listItemHolder.mMemberOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (int) view.getTag();
                        CommentList commentList = mCommentListItems.get(position);
                        mGutterMenuUtils.setPopUpForFriendShipType(position, null, commentList, null);
                    }
                });
            } else {
                listItemHolder.mMemberOptions.setVisibility(View.GONE);
            }
        }

        mRootView.setId(mCommentList.getmCommentId());
        return mRootView;
    }

    private LinearLayout.LayoutParams getSingleImageParamFromWidthHeight(JSONObject size, String url, ImageView gifView, ImageView stickerView) {

        float height = mContext.getResources().getDimension(R.dimen.height_150dp);
        int width = (int) height;
        LinearLayout.LayoutParams singleImageParam;
        if (size != null && size.optInt("width") != 0 && size.optInt("height") != 0) {
            float ratio = ((float)size.optInt("width") / (float)size.optInt("height"));
            width = (int) (height * ratio);
            if (url.contains(".gif")) {
                int gifWH = (int) mContext.getResources().getDimension(R.dimen.gif_wdith_height);
                int padding = (width /2) - gifWH/2;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(gifWH, gifWH);
                layoutParams.setMargins(padding, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                gifView.setLayoutParams(layoutParams);
            }
        }

        singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(width, (int) height);
        singleImageParam.setMargins(0,
                mContext.getResources().getDimensionPixelSize(R.dimen.element_spacing_small), mContext.getResources().getDimensionPixelSize(R.dimen.element_spacing_small), 0);
        return singleImageParam;
    }

    private void redirectToActivity(int attachment_id) {

        Intent viewIntent = new Intent(mContext, userProfile.class);
        viewIntent.putExtra(ConstantVariables.USER_ID, attachment_id);
        ((Activity) mContext).startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void openLightBox() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
        Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
        i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
        i.putExtras(bundle);
        mContext.startActivity(i);
    }

    /**
     * Method to show more/less option on comment body.
     * @param tvCommentBody Text view in which comment needs to be shown.
     * @param title Comment body text.
     * @param text Spannable String created from comment body.
     * @param commentList Comment list of particular position.
     */
    private void seeMoreOption(TextView tvCommentBody, CharSequence title, SpannableString text,
                               final CommentList commentList) {
        try {
            if (commentList.getmCommentBody().trim().length()
                    > ConstantVariables.FEED_TITLE_BODY_LENGTH) {
                int startIndex, endIndex;
                if (commentList.isFullCommentShowing()) {
                    startIndex = title.length() -
                            mContext.getResources().getString(R.string.more).length();
                    endIndex = title.length();
                } else {
                    startIndex = title.length() -
                            mContext.getResources().getString(R.string.readLess).length();
                    endIndex = title.length();
                }

                ClickableSpan myClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {

                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                        ds.setFakeBoldText(true);
                    }
                };
                text.setSpan(myClickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvCommentBody.setText(text);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemDelete(int position) {

    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {
        if (menuName.equals("friendship_type")) {
            mCommentListItems.set(position, (CommentList) itemList);
            notifyDataSetChanged();
        }
    }

    private static class ListItemHolder{

        ImageView mAuthorImage, mStickerImage;
        TextView mAuthorTitle,  mCommentDate, mLikeOption, mDeleteOption, mCommentLikeCount,
                mPostingText, mMemberOptions;
        JSONObject mLikeJsonObject, mDeleteJsonObject;
        EmojiconTextView mCommentBody;
        LinearLayout mCommentOptionsBlock;
        ImageView mReactionImage, mGifIV;
        RelativeLayout mAttchmentView;
        String imageUrl;
    }

    public void doLikeUnlike(){

        final CommentList commentList = mCommentListItems.get(itemPosition);
        mLikeOptionArray = commentList.getmLikeJsonObject();
        try {
            final String likeUrl = mLikeOptionArray.getString("url");
            final int isLikeValue = mLikeOptionArray.optInt("isLike");
            final String likeLabel = mLikeOptionArray.getString("label");
            final String likeName = mLikeOptionArray.getString("name");

            if (mSubjectType.equals("activity_action")) {
                mLikeCommentUrl = AppConstant.DEFAULT_URL + "advancedactivity/" + likeUrl;
            } else {
                mLikeCommentUrl = AppConstant.DEFAULT_URL + likeUrl;
            }
            JSONObject urlParamsJsonObject = mLikeOptionArray.getJSONObject("urlParams");

            Map<String, String> likeUnlikeParams = new HashMap<>();

            JSONArray urlParamsKeys = urlParamsJsonObject.names();

            for (int i = 0; i < urlParamsJsonObject.length(); i++) {
                String keyName = urlParamsKeys.getString(i);
                String value = urlParamsJsonObject.getString(keyName);
                likeUnlikeParams.put(keyName, value);
            }

            mAppConst.postJsonResponseForUrl(mLikeCommentUrl, likeUnlikeParams,
                    new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {

                            int isLike = commentList.getmIsLike();
                            if (isLike == 0) {
                                commentList.setmIsLike(1);
                                commentList.setmLikeCount(commentList.getmLikeCount() + 1);
                            }else {
                                commentList.setmIsLike(0);
                                commentList.setmLikeCount(commentList.getmLikeCount() - 1);
                            }

                            try {
                                if (isLikeValue == 0)
                                    mLikeOptionArray.put("isLike", 1);
                                else
                                    mLikeOptionArray.put("isLike", 0);
                                if (likeLabel.equals("Like"))
                                    mLikeOptionArray.put("label", "Unlike");
                                else
                                    mLikeOptionArray.put("label", "Like");
                                if (likeUrl.equals("like"))
                                    mLikeOptionArray.put("url", "unlike");
                                else
                                    mLikeOptionArray.put("url", "like");
                                if (likeName.equals("unlike"))
                                    mLikeOptionArray.put("name", "like");
                                else
                                    mLikeOptionArray.put("name", "unlike");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            commentList.setmLikeJsonObject(mLikeOptionArray);
                            notifyDataSetChanged();
                            likeUnlikeAction = true;

                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            notifyDataSetChanged();
                            likeUnlikeAction = true;
                            SnackbarUtils.displaySnackbar(mRootView, message);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteComment() {

        mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.delete_comment_dialogue_title),
                mContext.getResources().getString(R.string.delete_comment_dialogue_message),
                mContext.getResources().getString(R.string.delete_comment_dialogue_delete_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        final CommentList commentList = mCommentListItems.get(mPosition);
                        mDeleteOptionArray = commentList.getmDeleteJsonObject();
                        if (mDeleteOptionArray != null && mDeleteOptionArray.length() != 0) {
                            try {
                                if (mSubjectType.equals("activity_action")) {
                                    mDeleteCommentUrl = AppConstant.DEFAULT_URL + "advancedactivity/delete";
                                } else {
                                    mDeleteCommentUrl = AppConstant.DEFAULT_URL + mDeleteOptionArray.getString("url");
                                }
                                JSONObject urlParamsJsonObject = mDeleteOptionArray.getJSONObject("urlParams");
                                JSONArray urlParamsKeys = urlParamsJsonObject.names();
                                Map<String, String> deleteParams = new HashMap<>();

                                for (int i = 0; i < urlParamsJsonObject.length(); i++) {
                                    String keyName = urlParamsKeys.getString(i);
                                    String value = urlParamsJsonObject.getString(keyName);
                                    deleteParams.put(keyName, value);
                                }

                                if (deleteParams.size() != 0) {
                                    mDeleteCommentUrl = mAppConst.buildQueryString(mDeleteCommentUrl, deleteParams);
                                }
                                mAppConst.showProgressDialog();
                                mAppConst.deleteResponseForUrl(mDeleteCommentUrl, null, new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) {
                                        mAppConst.hideProgressDialog();
                                        /* Notify the adapter After Deleting the Entry */
                                        mCommentListItems.remove(mPosition);
                                        mListComment.setmTotalCommmentCount(mListComment.getmTotalCommmentCount() - 1);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        mAppConst.hideProgressDialog();
                                        SnackbarUtils.displaySnackbar(mRootView, message);
                                    }
                                });
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void viewCommentLikes(){

        CommentList commentList = mCommentListItems.get(mLikeCountPosition);
        String viewAllCommentLikesUrl;
        if (mSubjectType.equals("activity_action")) {
            viewAllCommentLikesUrl = UrlUtil.AAF_VIEW_LIKES_URL + "&comment_id=" + commentList.getmCommentId() +
                    "&action_id=" +mActionId;
        } else {
            viewAllCommentLikesUrl = UrlUtil.VIEW_LIKES_URL + "&subject_type=" + mSubjectType + "&subject_id=" + mSubjectId +
                    "&comment_id=" + commentList.getmCommentId();
        }
        Intent viewAllLikesIntent = new Intent(mContext, Likes.class);
        viewAllLikesIntent.putExtra("ViewAllLikesUrl", viewAllCommentLikesUrl);
        mContext.startActivity(viewAllLikesIntent);
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

}
