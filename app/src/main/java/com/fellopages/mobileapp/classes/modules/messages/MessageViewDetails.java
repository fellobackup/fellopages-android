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

import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import java.util.ArrayList;

public class MessageViewDetails {

    int mSenderId, mConversationId, mMessageId, mRecipientId, mInboxRead, mInboxDeleted,
            mAttachmentPlayListId, mAttachmentVideoId;
    String mSenderImageUrl, mSenderName, mMessageTitle, mMessageUpdatedDate, mMessageBody, mAttachmentType, mAttachmentUri,
            mAttachmentTitle, mAttachmentBody, mAttachmentImage, mVideoAttachContentUrl;
    ArrayList<PhotoListDetails> mPhotoDetails = new ArrayList<>();
    PhotoListDetails mPhotoListDetails;

    public MessageViewDetails(int senderId,String senderName, String senderImage, int conversationId ,int messageId,
                              String messageDate,String messageTitle, String messageBody, String attachmentType,
                              String attachmentTitle, String attachmentBody, String attachmentImage, String attachmentUri,
                              int attachmentPlaylistId, int attachmentVideoId, String videoAttachContentUrl,
                              ArrayList<PhotoListDetails> photoDetails, int inboxRead, int inboxDeleted,
                              int recipientId, PhotoListDetails photoListDetails){
        mSenderId = senderId;
        mSenderName = senderName;
        mSenderImageUrl = senderImage;
        mConversationId = conversationId;
        mMessageId = messageId;
        mMessageUpdatedDate = messageDate;
        mMessageTitle = messageTitle;
        mMessageBody = messageBody;
        mAttachmentType = attachmentType;
        mAttachmentTitle = attachmentTitle;
        mAttachmentBody = attachmentBody;
        mAttachmentImage = attachmentImage;
        mAttachmentUri = attachmentUri;
        mAttachmentPlayListId = attachmentPlaylistId;
        mAttachmentVideoId = attachmentVideoId;
        mVideoAttachContentUrl = videoAttachContentUrl;
        mPhotoDetails = photoDetails;
        mInboxRead = inboxRead;
        mInboxDeleted = inboxDeleted;
        mRecipientId = recipientId;
        mPhotoListDetails = photoListDetails;
    }

    public int getSenderId() {
        return mSenderId;
    }

    public String getSenderImageUrl() {
        return mSenderImageUrl;
    }

    public String getMessageUpdatedDate() {
        return mMessageUpdatedDate;
    }

    public String getMessageBody() {
        return mMessageBody;
    }

    public String getmAttachmentImage() {
        return mAttachmentImage;
    }

    public String getmAttachmentBody() {
        return mAttachmentBody;
    }

    public String getmAttachmentTitle() {
        return mAttachmentTitle;
    }

    public String getmAttachmentUri() {
        return mAttachmentUri;
    }

    public String getmAttachmentType() {
        return mAttachmentType;
    }

    public int getmAttachmentPlayListId() {
        return mAttachmentPlayListId;
    }

    public int getmAttachmentVideoId() {
        return mAttachmentVideoId;
    }

    public ArrayList<PhotoListDetails> getmPhotoDetails() {
        return mPhotoDetails;
    }

    public PhotoListDetails getmPhotoListDetails() {
        return mPhotoListDetails;
    }
}
