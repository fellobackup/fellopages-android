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

package com.fellopages.mobileapp.classes.modules.music.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple view used to display the details of an artist.
 */
public class ArtistView extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {

    private BezelImageView mAvatar;
    private TextView mTracks, mPlayCount;
    private SelectableTextView mArtistName, mDescription;
    private ActionIconThemedTextView mLikeButton, mCommentButton;
    private TextView mLikeCountTextView, mCommentCountTextView;
    private LinearLayout mLikeBlock, mCountsBlock;
    private int mLikeCount, mCommentCount, mPlayListId, mUserId;
    private String mLikeUnlikeUrl;
    private boolean isLike;
    private AppConstant mAppConst;
    private Context mContext;
    private ImageView mReactionIcon;
    private int mReactionsEnabled;
    private JSONObject mReactionsObject, mAllReactionsObject, myReaction, mContentReactions;
    private List<ImageViewList> reactionsImages;
    private ArrayList<JSONObject> mReactionsArray;
    private ImageLoader mImageLoader;
    private String sendLikeNotificationUrl;

    /**
     * Simple view used to display the details of an artist.
     *
     * @param context holding context.
     */
    public ArtistView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
        mContext = context;
        mAppConst = new AppConstant(context);
    }

    /**
     * Simple view used to display the details of an artist.
     *
     * @param context holding context.
     * @param attrs   attrs from xml.
     */
    public ArtistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    /**
     * Simple view used to display the details of an artist.
     *
     * @param context      holding context.
     * @param attrs        attrs from xml.
     * @param defStyleAttr style from xml.
     */
    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    /**

     */
    public void setModel(int playlistId,int ownerId, String ownerImage,String owner_title,
                         String description, int trackCount,int playCount, int likeCount,
                         int commentCount, boolean isLiked, Context context, int reactionsEnabled,
                         JSONObject reactionsObject, JSONObject allReactionsObject, JSONObject contentReactions) {

        mPlayListId = playlistId;
        mUserId = ownerId;
        mImageLoader.setImageUrl(ownerImage, mAvatar);
        mArtistName.setText(owner_title);
        mTracks.setText(String.format("\uF001 %d", trackCount));
        mPlayCount.setText(String.format("\uF04B %d", playCount));
        if(description != null && !description.isEmpty()) {
            findViewById(R.id.top_divider).setVisibility(GONE);
            mDescription.setText(description);
        }else {
            findViewById(R.id.top_divider).setVisibility(VISIBLE);
            mDescription.setVisibility(GONE);
        }

        mReactionsEnabled = reactionsEnabled;
        mReactionsObject = reactionsObject;
        mAllReactionsObject = allReactionsObject;
        if(mAllReactionsObject != null){
            mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mAllReactionsObject);
        }
        mContentReactions = contentReactions;

        mLikeCount = likeCount;
        mCommentCount = commentCount;
        isLike = isLiked;

        setLikeAndCommentCount();

        this.setVisibility(VISIBLE);
    }

    private void setLikeAndCommentCount(){

        if(mAppConst.isLoggedOutUser()){
            findViewById(R.id.likeCommentContent).setVisibility(GONE);
        }else{
            findViewById(R.id.likeCommentContent).setVisibility(VISIBLE);

            mLikeButton.setActivated(isLike);

            if(!isLike){
                mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            }else{
                mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            }

            /*
             Set Like and Comment Count
            */

            // Check if Reactions is enabled, show that content reaction and it's icon here.
            if(mReactionsEnabled == 1 && mReactionsObject != null && mReactionsObject.length() != 0){

                myReaction = mReactionsObject.optJSONObject("my_feed_reaction");

                if(myReaction != null && myReaction.length() != 0){
                    mLikeButton.setText(myReaction.optString("caption"));
                    mLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    String reactionImage = myReaction.optString("reaction_image_icon");
                    mImageLoader.setImageUrl(reactionImage, mReactionIcon);
                    mReactionIcon.setVisibility(View.VISIBLE);
                } else {
                    mReactionIcon.setVisibility(View.GONE);
                    mLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp),
                            null, null, null);
                    mLikeButton.setText(mContext.getResources().getString(R.string.like_text));
                }

            } else{
                mLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_up_white_18dp),
                        null, null, null);
                mLikeButton.setText(mContext.getResources().getString(R.string.like_text));
            }

            mLikeCountTextView.setText(String.valueOf(mLikeCount));
            mCommentCountTextView.setText(String.valueOf(mCommentCount));
        }
    }

    public void setCommentCount(int commentCount){
        mCommentCountTextView.setText(String.valueOf(commentCount));
    }

    /**
     * Initialize internal component.
     *
     * @param context holding context.
     */
    private void init(Context context) {
        mContext = context;
        mImageLoader = new ImageLoader(mContext.getApplicationContext());
        LayoutInflater.from(context).inflate(R.layout.artist_view, this);
        this.setVisibility(INVISIBLE);
        mArtistName = findViewById(R.id.artist_author);
        mTracks = findViewById(R.id.artist_view_track_number);
        mDescription = findViewById(R.id.artist_view_description);
        mAvatar = findViewById(R.id.owner_image);
        mPlayCount = findViewById(R.id.track_play_count);
        mPlayCount.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        mTracks.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        mLikeBlock = findViewById(R.id.likeBlock);
        mReactionIcon = findViewById(R.id.reactionIcon);
        mLikeButton = findViewById(R.id.like_button);
        mCommentButton  = findViewById(R.id.comment_button);
        mCountsBlock = findViewById(R.id.countsBlock);
        mLikeCountTextView = findViewById(R.id.likeCount);
        mCommentCountTextView = findViewById(R.id.commentCount);
        mLikeCountTextView.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        mCommentCountTextView.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        mLikeBlock.setOnClickListener(this);
        mLikeBlock.setOnLongClickListener(this);
        mCommentButton.setOnClickListener(this);
        mCountsBlock.setOnClickListener(this);
        mArtistName.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {

            case R.id.likeBlock:

                int reactionId = 0;
                String reactionIcon = null, caption = null;

                if(mReactionsEnabled ==  1 && mAllReactionsObject != null ){
                    reactionId = mAllReactionsObject.optJSONObject("like").optInt("reactionicon_id");
                    reactionIcon = mAllReactionsObject.optJSONObject("like").optJSONObject("icon").
                            optString("reaction_image_icon");
                    caption = mContext.getResources().getString(R.string.like_text);
                }
                doLikeUnlike(null, false, reactionId, reactionIcon, caption);
                break;

            case R.id.comment_button:
            case R.id.countsBlock:

                String likeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=music_playlist"
                        + "&subject_id=" + mPlayListId + "&viewAllComments=1&page=1&limit=20";
                Intent commentIntent = new Intent(mContext, Comment.class);
                commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, likeCommentsUrl);
                commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, "music_playlist");
                commentIntent.putExtra(ConstantVariables.SUBJECT_ID, mPlayListId);
                commentIntent.putExtra("commentCount", mCommentCount);
                commentIntent.putExtra("reactionsEnabled", mReactionsEnabled);
                if(mContentReactions != null){
                    commentIntent.putExtra("popularReactions", mContentReactions.toString());
                }
                ((AppCompatActivity)mContext).startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.owner_image:
            case R.id.artist_author:
                Intent intent = new Intent(mContext, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID, mUserId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((AppCompatActivity)mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

    }

    @Override
    public boolean onLongClick(View v) {
        int[] location = new int[2];
        mLikeBlock.getLocationOnScreen(location);
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

        if(mAllReactionsObject != null && mReactionsArray != null) {

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

        mLikeButton.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mLikeButton.setText("\uf110");
        mLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null);

        if (PreferencesUtils.isNestedCommentEnabled(mContext)) {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        } else {
            sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "send-notification";
        }

        mLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mReactionIcon.setVisibility(View.GONE);
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, "music_playlist");
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mPlayListId));

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
                mLikeButton.setTypeface(null);

                // Set My FeedReaction Changed
                if (mReactionsEnabled == 1) {
                    /* If a Reaction is posted or a reaction is changed on content
                       put the updated reactions in my feed reactions array
                     */
                    updateContentReactions(reactionId, reactionIcon, caption, isLike, isReactionChanged);

                    /* Calling to send notifications after like action */
                    if (!isLike) {
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
                if (!isLike) {
                    mLikeCount += 1;
                } else if (!isReactionChanged) {
                    mLikeCount -= 1;
                }

                // Toggle isLike Variable if reaction is not changed
                if (!isReactionChanged)
                    isLike = !isLike;

                setLikeAndCommentCount();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mLikeButton.setTypeface(null);
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
