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

package com.fellopages.mobileapp.classes.modules.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.ui.ExpandableTextView;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<MessageViewDetails> messagesItems;
    private int mCurrentUserId;
    private MessageViewDetails messageViewDetails;
    private ImageLoader mImageLoader;

    public MessageListAdapter(Context context, List<MessageViewDetails> chatMessages) {
        this.context = context;
        this.messagesItems = chatMessages;
        mImageLoader = new ImageLoader(context);

        //Fetching Current Logged-in user's user id
        try {
            JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(context));
            mCurrentUserId = userDetail.getInt("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        messageViewDetails = messagesItems.get(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (messageViewDetails.getSenderId() == mCurrentUserId) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.message_view_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.message_view_left,
                    null);
        }

        ExpandableTextView txtMsg = convertView.findViewById(R.id.message);
        ExpandableTextView txtMsgWithAttachment = convertView.findViewById(R.id.message_with_attachment);
        SelectableTextView msgTimeStamp = convertView.findViewById(R.id.messagets);
        BezelImageView avatar = convertView.findViewById(R.id.avatar);
        LinearLayout messageAttachment = convertView.findViewById(R.id.message_attachment);
        LinearLayout messageContainer = convertView.findViewById(R.id.message_container);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageContainer.getLayoutParams();

        CardView cardViewAttachment = convertView.findViewById(R.id.card_view_attachments);
        cardViewAttachment.setOnClickListener(this);
        TextView attachmentBody = convertView.findViewById(R.id.attachment_body);
        TextView attachmentTitle = convertView.findViewById(R.id.attachment_title);
        LinearLayout musicAttachmentBlock = convertView.findViewById(R.id.music_attachment_block);
        TextView musicIcon = convertView.findViewById(R.id.music_icon);
        TextView musicTitle = convertView.findViewById(R.id.music_title);
        musicIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        ImageView attachmentIcon = convertView.findViewById(R.id.attachment_icon);
        TextView playIcon = convertView.findViewById(R.id.play_button);
        playIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(context));

        avatar.setOnClickListener(this);
        if (messageViewDetails.getSenderId() != mCurrentUserId) {
            avatar.setVisibility(View.VISIBLE);

            mImageLoader.setImageForUserProfile(messageViewDetails.getSenderImageUrl(), avatar);
            avatar.setTag(avatar.getId(), position);

        }
        String convertedDate = AppConstant.convertDateFormat(context.getResources(),
                messageViewDetails.getMessageUpdatedDate());
        msgTimeStamp.setText(convertedDate);

        String mAttachmentType = messageViewDetails.getmAttachmentType();
        if (mAttachmentType != null && !mAttachmentType.isEmpty()) {
            messageContainer.setLayoutParams(layoutParams);
            messageAttachment.setVisibility(View.VISIBLE);
            txtMsgWithAttachment.setVisibility(View.VISIBLE);
            txtMsg.setVisibility(View.GONE);
            cardViewAttachment.setTag(position);
            attachmentIcon.setVisibility(View.VISIBLE);
            if (mAttachmentType.equals("story")) {
                cardViewAttachment.setClickable(false);
            } else {
                cardViewAttachment.setClickable(true);
            }

            switch (mAttachmentType) {
                case "video":
                    playIcon.setVisibility(View.VISIBLE);
                    playIcon.setText("\uf01d");
                case "core_link":
                    attachmentIcon.setVisibility(View.VISIBLE);
                    attachmentBody.setVisibility(View.VISIBLE);
                    attachmentTitle.setVisibility(View.VISIBLE);
                    attachmentBody.setText(messageViewDetails.getmAttachmentBody());
                    attachmentTitle.setText(messageViewDetails.getmAttachmentTitle());

                    mImageLoader.setAlbumPhoto(messageViewDetails.getmAttachmentImage(), attachmentIcon);
                    break;
                case "album_photo":
                case "story":
                    attachmentIcon.setVisibility(View.VISIBLE);
                    mImageLoader.setAlbumPhoto(messageViewDetails.getmAttachmentImage(), attachmentIcon);
                    break;
                case "music_playlist_song":
                    musicAttachmentBlock.setVisibility(View.VISIBLE);
                    attachmentIcon.setVisibility(View.GONE);
                    musicIcon.setText("\uf001");
                    musicTitle.setText(messageViewDetails.getmAttachmentTitle());
                    break;
            }
            txtMsgWithAttachment.setText(Smileys.getEmojiFromString(Html.fromHtml(messageViewDetails.getMessageBody()).toString()));
        } else {
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            messageContainer.setLayoutParams(layoutParams);
            messageAttachment.setVisibility(View.GONE);
            txtMsgWithAttachment.setVisibility(View.GONE);
            txtMsg.setVisibility(View.VISIBLE);
            txtMsg.setText(Smileys.getEmojiFromString(Html.fromHtml(messageViewDetails.getMessageBody()).toString()));
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position;
        if (v.getId() == R.id.card_view_attachments) {
            position = (int) v.getTag();
            messageViewDetails = messagesItems.get(position);
            attachmentClicked(messageViewDetails.getmAttachmentType(), messageViewDetails.getmAttachmentPlayListId(),
                    messageViewDetails.getmAttachmentVideoId(), messageViewDetails.getmAttachmentUri(),
                    messageViewDetails.getmPhotoDetails());
        } else {
            position = (int) v.getTag(v.getId());
            messageViewDetails = messagesItems.get(position);
            int senderId = messageViewDetails.getSenderId();
            if (senderId != 0) {
                Intent viewIntent = new Intent(context, userProfile.class);
                viewIntent.putExtra("user_id", senderId);
                ((Activity) context).startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    /**
     * Method to redirect to attachment's view page.
     *
     * @param attachmentType       type of message attachment.
     * @param attachmentPlaylistId attachment id for music.
     * @param attachmentVideoId    attachment id for video.
     * @param attachmentUri        attachment uri in case of "core_link".
     * @param photoListDetail      Array list containing photo info.
     */
    public void attachmentClicked(String attachmentType, int attachmentPlaylistId, int attachmentVideoId,
                     String attachmentUri, ArrayList<PhotoListDetails> photoListDetail) {
        Intent viewIntent = null;
        switch (attachmentType) {
            case "music_playlist_song":
                viewIntent = GlobalFunctions.getIntentForModule(context, attachmentPlaylistId,
                        attachmentType, null);
                ((Activity)context).startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "video":
                viewIntent = GlobalFunctions.getIntentForModule(context, attachmentVideoId,
                        attachmentType, null);
                ((Activity)context).startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "album_photo":
                Bundle bundle = new Bundle();
                bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, photoListDetail);
                viewIntent = new Intent(context, PhotoLightBoxActivity.class);
                PhotoListDetails photoListDetails = messageViewDetails.getmPhotoListDetails();
                viewIntent.putExtra(ConstantVariables.ITEM_POSITION, photoListDetails.getmPosition());
                viewIntent.putExtra(ConstantVariables.PHOTO_REQUEST_URL, photoListDetails.getmImageRequestUrl());
                viewIntent.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, photoListDetails.getmTotalImageCount());
                viewIntent.putExtra(ConstantVariables.SHOW_ALBUM_BUTTON,true);
                viewIntent.putExtras(bundle);
                ((Activity)context).startActivityForResult(viewIntent, ConstantVariables.VIEW_LIGHT_BOX);
                break;

            default:
                if(ConstantVariables.WEBVIEW_ENABLE == 0){
                    CustomTabUtil.launchCustomTab((Activity)context,
                            GlobalFunctions.getWebViewUrl(attachmentUri, context));
                } else {
                    viewIntent = new Intent(context, WebViewActivity.class);
                    viewIntent.putExtra("url", attachmentUri);
                    ((Activity) context).startActivityForResult(viewIntent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
    }
}
