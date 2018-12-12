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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.ImageAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.ThemedTextView;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MultiPhotoRecyclerAdapter extends RecyclerView.Adapter<MultiPhotoRecyclerAdapter.ItemViewHolder> implements View.OnClickListener {

    // Member variables.
    private Context mContext;
    private int mReactionsEnabled;
    private JSONObject mReactions;
    private ArrayList<PhotoListDetails> mBrowseItemList;
    private List<ImageViewList> reactionsImages;
    private ArrayList<JSONObject> mReactionsArray;
    private Map<String, String> mPostParams = new HashMap<>();
    private PhotoListDetails mPhotoDetail;
    private AppConstant mAppConst;
    private SocialShareUtil socialShareUtil;
    private ImageAdapter reactionsAdapter;
    private ImageLoader mImageLoader;


    public MultiPhotoRecyclerAdapter(Context context, int reactionsEnabled, ArrayList<PhotoListDetails> listItem,
                                     JSONObject reactions, ArrayList<JSONObject> reactionsArray) {

        this.mContext = context;
        this.mReactionsEnabled = reactionsEnabled;
        this.mBrowseItemList = listItem;
        this.mReactions = reactions;
        this.mReactionsArray = reactionsArray;
        mAppConst = new AppConstant(mContext);
        socialShareUtil = new SocialShareUtil(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.photos_like_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        mPhotoDetail = mBrowseItemList.get(position);
        mImageLoader.loadAlbumImageWithPicasso(holder.ivMainImage, mPhotoDetail.getImageUrl());

        // Checking for footer block.
        if (!mAppConst.isLoggedOutUser()) {
            holder.footerBlock.setVisibility(View.VISIBLE);
        } else {
            holder.footerBlock.setVisibility(View.GONE);
        }

        JSONObject mContentReactionsObject, mPhotoReactions = null, myReactions = null;
        if (mPhotoDetail.getmReactionsObject() != null && !mPhotoDetail.getmReactionsObject().isEmpty()) {
            try {
                mContentReactionsObject = new JSONObject(mPhotoDetail.getmReactionsObject());
                mPhotoReactions = mContentReactionsObject.optJSONObject("feed_reactions");
                myReactions = mContentReactionsObject.optJSONObject("my_feed_reaction");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.tvLikeButton.setActivated(mPhotoDetail.isLiked());

        if (!mPhotoDetail.isLiked()) {
            holder.ivReactionIcon.setVisibility(View.GONE);
            holder.tvLikeButton.setText(mContext.getResources().getString(R.string.like_text));
            holder.tvLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_thumbs_up), null, null, null);
            holder.tvLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
        } else {
            /*
                If Reactions and Nestedcomments are enabled
                    -- Show Reaction Icon and Reactions Text On Like Button
                else
                    -- Show Only Like Icon
             */
            holder.tvLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

            if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                if (myReactions != null && myReactions.length() != 0) {
                    String reactionImage = myReactions.optString("reaction_image_icon");

                    holder.tvLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    holder.ivReactionIcon.setVisibility(View.VISIBLE);
                    mImageLoader.setImageUrl(reactionImage, holder.ivReactionIcon);
                    holder.tvLikeButton.setText(myReactions.optString("caption"));
                } else {
                    holder.ivReactionIcon.setVisibility(View.GONE);
                    holder.tvLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_thumbs_up),
                            null, null, null);
                    holder.tvLikeButton.setText(mContext.getResources().
                            getString(R.string.like_text));
                }
            } else {
                holder.tvLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_thumbs_up),
                        null, null, null);
                holder.tvLikeButton.setText(mContext.getResources().getString(R.string.like_text));
            }
        }

        // Hide count container if like and comment count are 0.
        if (mPhotoDetail.getImageLikeCount() == 0 && mPhotoDetail.getImageCommentCount() == 0) {
            holder.counterView.setVisibility(View.GONE);
        } else {
            holder.counterView.setVisibility(View.VISIBLE);
        }

        if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {

            // Show 3 popular reactions
            if (mPhotoReactions != null && mPhotoReactions.length() != 0) {
                holder.llPopularReactionView.removeAllViews();
                JSONArray reactionIds = mPhotoReactions.names();
                holder.llPopularReactionView.setVisibility(View.VISIBLE);
                for (int j = 0; j < mPhotoReactions.length() && j < 3; j++) {
                    String imageUrl = mPhotoReactions.optJSONObject(reactionIds.optString(j)).
                            optString("reaction_image_icon");
                    int reactionId = mPhotoReactions.optJSONObject(reactionIds.optString(j)).
                            optInt("reactionicon_id");

                    ImageView imageView = CustomViews.generateReactionImageView(mContext, reactionId, imageUrl);

                    holder.llPopularReactionView.addView(imageView);
                }
            } else {
                holder.llPopularReactionView.setVisibility(View.GONE);
            }
        } else {
            holder.llPopularReactionView.setVisibility(View.GONE);
        }

        // Showing photos like count.
        RelativeLayout.LayoutParams separatorParam = (RelativeLayout.LayoutParams) holder.countSeparator.getLayoutParams();
        if (mPhotoDetail.getImageLikeCount() > 0) {
            holder.tvLikeCount.setVisibility(View.VISIBLE);

            // Set Like Count
            if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                // You and count others
                if (mPhotoDetail.isLiked()) {
                    if (mPhotoDetail.getImageLikeCount() == 1) {
                        holder.tvLikeCount.setText(mContext.getResources().getString(R.string.reaction_string));
                    } else {
                        String likeText = mContext.getResources().getQuantityString(R.plurals.others,
                                mPhotoDetail.getImageLikeCount() - 1, mPhotoDetail.getImageLikeCount() - 1);
                        holder.tvLikeCount.setText(String.format(mContext.getResources().getString(R.string.reaction_text_format),
                                mContext.getResources().getString(R.string.you_and_text), likeText
                        ));
                    }
                } else {
                    // Only count
                    holder.tvLikeCount.setText(Integer.toString(mPhotoDetail.getImageLikeCount()));
                }
                separatorParam.addRule(RelativeLayout.BELOW, R.id.popularReactionIcons);
            } else {
                String likeText = mContext.getResources().getQuantityString(R.plurals.profile_page_like,
                        mPhotoDetail.getImageLikeCount());
                holder.tvLikeCount.setText(String.format(mContext.getResources().getString(R.string.like_count_text),
                        mPhotoDetail.getImageLikeCount(), likeText));
                separatorParam.addRule(RelativeLayout.BELOW, R.id.like_count);
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.tvCommentCount.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            holder.tvCommentCount.setLayoutParams(layoutParams);

        } else {
            holder.tvLikeCount.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.tvCommentCount.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            holder.tvCommentCount.setLayoutParams(layoutParams);
            separatorParam.addRule(RelativeLayout.BELOW, R.id.comment_count);
        }
        holder.countSeparator.setLayoutParams(separatorParam);

        // Showing the comment count for each image view.
        if (mPhotoDetail.getImageCommentCount() != 0) {
            holder.tvCommentCount.setVisibility(View.VISIBLE);
            String commentText = mContext.getResources().getQuantityString(R.plurals.profile_page_comment,
                    mPhotoDetail.getImageCommentCount());
            holder.tvCommentCount.setText(Html.fromHtml(String.format(
                    mContext.getResources().getString(R.string.comment_count_text),
                    mPhotoDetail.getImageCommentCount(), commentText)));
        } else {
            holder.tvCommentCount.setVisibility(View.GONE);
        }

        // Launching photo light box when clicked on photo.
        holder.ivMainImage.setTag(holder);
        holder.ivMainImage.setOnClickListener(this);

        // Applying click listener on comment button.
        holder.mCommentBlock.setTag(holder);
        holder.mCommentBlock.setOnClickListener(this);

        // Applying click listener on like/comment count view.
        holder.counterView.setTag(holder);
        holder.counterView.setOnClickListener(this);

        // Applying click listener on share button.
        holder.mShareBlock.setTag(holder);
        holder.mShareBlock.setOnClickListener(this);

        // Applying click listener on Like button.
        holder.mLikeBlock.setTag(holder);
        holder.mLikeBlock.setOnClickListener(this);

        if (mReactionsEnabled == 1) {
            // Applying long click listener on Like button.
            holder.mLikeBlock.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final PhotoListDetails photoListDetails = mBrowseItemList.get(holder.getAdapterPosition());

                    int[] location = new int[2];
                    holder.footerBlock.getLocationOnScreen(location);
                    RecyclerView reactionsRecyclerView = new RecyclerView(mContext);
                    reactionsRecyclerView.setHasFixedSize(true);
                    reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                            LinearLayoutManager.HORIZONTAL, false));
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
                    popUp.showAtLocation(reactionsRecyclerView, Gravity.TOP, location[0], location[1] - 50);


                    if (mReactions != null && mReactionsArray != null) {

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

                        reactionsAdapter = new ImageAdapter((Activity) mContext, reactionsImages, true,
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
                                        JSONObject myReactions = null;
                                        if (photoListDetails.getmReactionsObject() != null) {
                                            try {
                                                JSONObject reactionsObject = new JSONObject(photoListDetails.getmReactionsObject());
                                                myReactions = reactionsObject.optJSONObject("my_feed_reaction");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (myReactions != null) {
                                            if (myReactions.optInt("reactionicon_id") != reactionId) {
                                                doLikeUnlike(photoListDetails, reaction, true, reactionId, reactionIcon,
                                                        caption, holder.getAdapterPosition());
                                            }
                                        } else {
                                            doLikeUnlike(photoListDetails, reaction, false, reactionId, reactionIcon,
                                                    caption, holder.getAdapterPosition());
                                        }
                                    }
                                });

                        reactionsRecyclerView.setAdapter(reactionsAdapter);
                    }
                    return true;
                }
            });
        }

    }

    /***
     * Method to launch comment page when comment button or counter view is clicked.
     * @param position Clicked photo position.
     */
    private void launchCommentPage(int position) {
        PhotoListDetails photoDetails = mBrowseItemList.get(position);
        int photoId = photoDetails.getPhotoId();

        String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type="
                + "album_photo" + "&subject_id=" + photoId + "&viewAllComments=1";
        Intent commentIntent = new Intent(mContext, Comment.class);
        commentIntent.putExtra("photoComment", true);
        commentIntent.putExtra(ConstantVariables.PHOTO_POSITION, position);
        commentIntent.putExtra("LikeCommentUrl", mLikeCommentsUrl);
        commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, photoDetails.getmSubjectType());
        commentIntent.putExtra(ConstantVariables.SUBJECT_ID, photoId);
//        commentIntent.putExtra("commentCount", mGetTotalComments);
        ((Activity) mContext).startActivityForResult(commentIntent, ConstantVariables.VIEW_MULTI_IMAGE_COMMENT_PAGE_CODE);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    /**
     * Funtion to perform Like/Unlike Option on a photo
     *
     * @param photoListDetails
     * @param reaction
     * @param isReactionChanged
     * @param reactionId
     * @param reactionIcon
     * @param caption
     */
    private void doLikeUnlike(PhotoListDetails photoListDetails, String reaction, final boolean isReactionChanged, final int reactionId,
                              final String reactionIcon, final String caption, int photoPosition) {

        final String sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";
        final Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, photoListDetails.getmSubjectType());
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(photoListDetails.getPhotoId()));

        if (reaction != null) {
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
        String mLikeUnlikeUrl;
        if (!photoListDetails.isLiked() || isReactionChanged) {
            if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "advancedcomments/like?sendNotification=0";
                updateSinglePhotoReactions(reactionId, reactionIcon, caption, photoPosition, isReactionChanged);
            } else {
                mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "like?sendNotification=0";
            }
            if (!isReactionChanged) {
                photoListDetails.setImageLikeCount(photoListDetails.getImageLikeCount() + 1);
            }
            photoListDetails.setIsLiked(true);
        } else {
            mLikeUnlikeUrl = AppConstant.DEFAULT_URL + "unlike";
            if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                updateSinglePhotoReactions(reactionId, reactionIcon, caption, photoPosition, false);
            }
            photoListDetails.setImageLikeCount(photoListDetails.getImageLikeCount() - 1);
            photoListDetails.setIsLiked(false);
        }
        notifyItemChanged(photoPosition);

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {

                    /* Calling to send notifications after like action */
                    mAppConst.postJsonRequest(sendLikeNotificationUrl, likeParams);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }


    private void updateSinglePhotoReactions(int reactionId, String reactionIcon, String caption,
                                            int photoPosition, boolean isReactionChanged) {

        try {

            JSONObject mReactionsObject = null, mPhotoReactions = null, myReactions = null;

            PhotoListDetails photoDetails = mBrowseItemList.get(photoPosition);
            boolean isLiked = photoDetails.isLiked();

            if (photoDetails.getmReactionsObject() != null && !photoDetails.getmReactionsObject().isEmpty()) {
                try {
                    mReactionsObject = new JSONObject(photoDetails.getmReactionsObject());
                    mPhotoReactions = mReactionsObject.optJSONObject("feed_reactions");
                    myReactions = mReactionsObject.optJSONObject("my_feed_reaction");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mReactionsObject = new JSONObject();
            }


            if (mReactionsObject != null) {
                // Update the count of previous reaction in reactions object and remove the my_feed_reactions
                if (isLiked) {
                    if (myReactions != null && mPhotoReactions != null) {
                        int myReactionId = myReactions.optInt("reactionicon_id");
                        if (mPhotoReactions.optJSONObject(String.valueOf(myReactionId)) != null) {
                            int myReactionCount = mPhotoReactions.optJSONObject(String.valueOf(myReactionId)).
                                    optInt("reaction_count");
                            if ((myReactionCount - 1) <= 0) {
                                mPhotoReactions.remove(String.valueOf(myReactionId));
                            } else {
                                mPhotoReactions.optJSONObject(String.valueOf(myReactionId)).put("reaction_count",
                                        myReactionCount - 1);
                            }
                            mReactionsObject.put("feed_reactions", mPhotoReactions);
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
                    mReactionsObject.put("feed_reactions", mPhotoReactions);
                }

                photoDetails.setmReactionsObject(mReactionsObject.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        ItemViewHolder holder;
        holder = (ItemViewHolder) view.getTag();
        PhotoListDetails photoDetails = mBrowseItemList.get(holder.getAdapterPosition());

        switch (view.getId()) {
            case R.id.counts_container:
            case R.id.comment_view:
                launchCommentPage(holder.getAdapterPosition());
                break;

            case R.id.share_view:
                String url = AppConstant.DEFAULT_URL + "activity/share";
                url += "?type=" + photoDetails.getmSubjectType() + "&id=" + photoDetails.getPhotoId();

                socialShareUtil.sharePost(view, null, photoDetails.getImageUrl(),
                        url, "image", photoDetails.getImageUrl());
                break;

            case R.id.like_view:
                int reactionId = 0;
                String reactionIcon = null, caption = null;

                if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                    reactionId = mReactions.optJSONObject("like").optInt("reactionicon_id");
                    reactionIcon = mReactions.optJSONObject("like").optJSONObject("icon").
                            optString("reaction_image_icon");
                    caption = mContext.getResources().getString(R.string.like_text);
                }
                doLikeUnlike(photoDetails, null, false, reactionId, reactionIcon,
                        caption, holder.getAdapterPosition());
                break;

            case R.id.image:
                String albumViewUrl;
                if (photoDetails.getmSubjectType() != null &&
                        photoDetails.getmSubjectType().equals("album_photo")) {
                    albumViewUrl = UrlUtil.ALBUM_VIEW_PAGE + photoDetails.geAlbumId() + "?gutter_menu=1";

                } else {
                    albumViewUrl = UrlUtil.ALBUM_VIEW_URL + photoDetails.geAlbumId();
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mBrowseItemList);
                Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
                i.putExtra(ConstantVariables.ITEM_POSITION, holder.getAdapterPosition());
                i.putExtra(ConstantVariables.ENABLE_COMMENT_CACHE, true);
                i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, mBrowseItemList.size());
                i.putExtra(ConstantVariables.PHOTO_REQUEST_URL, albumViewUrl);
                i.putExtra(ConstantVariables.SHOW_ALBUM_BUTTON, true);
                i.putExtra(ConstantVariables.SUBJECT_TYPE, photoDetails.getmSubjectType());
                i.putExtra(ConstantVariables.IS_ALBUM_PHOTO_REQUEST, true);
                i.putExtra(ConstantVariables.ALBUM_ID, photoDetails.geAlbumId());
                i.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(i, ConstantVariables.VIEW_MULTI_IMAGE_COMMENT_PAGE_CODE);
                break;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public View container, counterView, countSeparator, footerBlock,
                mLikeBlock, mCommentBlock, mShareBlock;
        public LinearLayout llPopularReactionView;
        public ImageView ivMainImage, ivReactionIcon;
        public ThemedTextView tvLikeCount, tvCommentCount;
        public ActionIconThemedTextView tvShareButton, tvLikeButton, tvCommentButton;

        public ItemViewHolder(View view) {
            super(view);
            container = view;

            ivMainImage = (ImageView) view.findViewById(R.id.image);
            counterView = view.findViewById(R.id.counts_container);
            tvLikeCount = (ThemedTextView) view.findViewById(R.id.like_count);
            tvCommentCount = (ThemedTextView) view.findViewById(R.id.comment_count);
            llPopularReactionView = (LinearLayout) view.findViewById(R.id.popularReactionIcons);

            // Like/Comment views
            countSeparator = view.findViewById(R.id.counts_saperator);
            footerBlock = view.findViewById(R.id.feedFooterMenusBlock);
            ivReactionIcon = (ImageView) view.findViewById(R.id.reactionIcon);
            tvShareButton = (ActionIconThemedTextView) view.findViewById(R.id.share_button);
            tvLikeButton = (ActionIconThemedTextView) view.findViewById(R.id.like_button);
            tvCommentButton = (ActionIconThemedTextView) view.findViewById(R.id.comment_button);
            mLikeBlock = view.findViewById(R.id.like_view);
            mCommentBlock = view.findViewById(R.id.comment_view);
            mShareBlock = view.findViewById(R.id.share_view);
        }
    }

}
