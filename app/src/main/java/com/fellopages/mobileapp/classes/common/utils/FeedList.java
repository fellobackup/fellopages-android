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

package com.fellopages.mobileapp.classes.common.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fellopages.mobileapp.classes.core.ConstantVariables.STATUS_POST_OPTIONS;

public class FeedList implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FeedList> CREATOR = new Parcelable.Creator<FeedList>() {
        @Override
        public FeedList createFromParcel(Parcel in) {
            return new FeedList(in);
        }

        @Override
        public FeedList[] newArray(int size) {
            return new FeedList[size];
        }
    };
    public JSONArray mOtherMembers;
    public String posterTitle, posterThumb, postedItemBody, notifyPosition;
    public int mProgress;
    public boolean isRequestProcessing = false, requestCanceled = false;
    public int itemViewType;
    String mHiddenBodyText, mUndoHiddenFeedURl, mHideAllText, mHideAllUrl, mHideAllName, mFeedType, mObjectType;
    int mSubjectId, mActionId, mObjectId, mOwnerFeedType;
    HashMap<String, String> mClickableStringsList, mClickableStringsListNew;
    HashMap<Integer, String> mVideoInfo;
    HashMap<String, String> mUndoHiddenFeedParams;
    HashMap<String, String> mWordStylingClickableParts;
    boolean noFeed, isNotificationOn, isPostPinned;
    ArrayList<PhotoListDetails> mPhotoDetails;
    double mLatitude, mLongitude;
    String mPlaceId, mLocationLabel, mIsTranslation;
    int mReactionsEnabled, startIndex, endIndex;
    JSONObject mFeedReactions;
    JSONObject mReactions;
    JSONArray mUserTagArray;
    List<Object> browseListItem;
    int muteStoryCount;
    boolean isStoryDataChange;
    int mShareActionId, mShareSubjectId, mShareFeedType, mShareObjectId;
    String mShareObjectType, mShareBody, mShareDate, mShareFeedActionTitle, mShareFeedIcon;
    boolean mIsShareFeed = false, isGifLoad = false;
    HashMap<String, String> mShareClickableStringsList;
    private String mFeedIcon, mMenuUrl, mHashTagString, feedLink, schedulePostTime, postPrivacy, privacyIcon;
    private String mFeedTitle, mFeedPostTime, mWebUrl, mActionTypeBody, mFeedAttachmentType;
    private int mAttachmentCount, mLikeCount, mCommentCount, mIsLike, mPhotoAttachmentCount, pinPostDuration;
    private int mCommentAble, mShareAble, mIsSaveFeedOption;
    private int mCanComment;
    private JSONArray mFeedMenusArray, mFeedAttachmentArray, mFeedFilterArray;
    private JSONObject mFeedFooterMenus, mFeedPostMenus, mFeedObject, mMyFeedReactions, decoration,
            bannerObject, feelingObject, userTagObject, mCheckinObject, subjectProfileInfo;
    private JSONArray greetingsArray, birthdayArray;
    private boolean isGreetingSet;

    public FeedList(int actionId, int subjectId, int feedType, String objectType, int objectId,
                    String mFeedTitle, String mFeedIcon, String postPrivacy, String privacyIcon,
                    JSONArray mFeedMenusArray, String mFeedPostTime, String feedLink, String schedulePostTime,
                    int pinPostDuration, int attachmentCount, int likeCount, int commentCount, int canComment,
                    int isLike, JSONObject feedObject, JSONObject decoration, JSONObject bannerObject,
                    JSONObject feelingObject, JSONObject userTagObject, JSONArray feedAttachmentArray,
                    int photoAttachmentCount, JSONObject feedFooterMenus, int commentAble, int shareAble,
                    int isSaveFeedOption, boolean isNotificationTurnedOn, boolean isPostPinned,
                    HashMap<String, String> clickableStrings, HashMap<String, String> clickableStringsNew,
                    String actionTypeBody, HashMap<Integer, String> videoInfo,
                    HashMap<String, String> wordStylingClickableParts, String url, String feedAttachmentType,
                    String type, String locationLabel, double latitude, double longitude, String placeId,
                    String hashTagString, JSONObject feedReactions, JSONObject myFeedReactions,
                    JSONArray userTagArray, String isTranslation, int startIndex, int endIndex,
                    boolean isShareFeed, String shareFeedIcon, int shareActionId, int shareSubjectId,
                    int shareFeedType, String shareObjectType, int shareObjectId, String shareBody,
                    String shareDate, String shareFeedActionTitle, HashMap<String, String> shareClickableStrings, JSONArray otherMembers, JSONObject subjectProfile) {

        this.mSubjectId = subjectId;
        mOwnerFeedType = feedType;
        mObjectType = objectType;
        mObjectId = objectId;
        this.mFeedTitle = mFeedTitle;
        this.mFeedIcon = mFeedIcon;
        this.postPrivacy = postPrivacy;
        this.privacyIcon = privacyIcon;
        this.mFeedMenusArray = mFeedMenusArray;
        this.mFeedPostTime = mFeedPostTime;
        this.feedLink = feedLink;
        this.schedulePostTime = schedulePostTime;
        this.pinPostDuration = pinPostDuration;
        mAttachmentCount = attachmentCount;
        mLikeCount = likeCount;
        mCommentCount = commentCount;
        mIsLike = isLike;
        mCanComment = canComment;
        this.mFeedObject = feedObject;
        this.decoration = decoration;
        this.bannerObject = bannerObject;
        this.feelingObject = feelingObject;
        this.userTagObject = userTagObject;
        this.mFeedAttachmentArray = feedAttachmentArray;
        this.mPhotoAttachmentCount = photoAttachmentCount;
        this.mCommentAble = commentAble;
        this.mShareAble = shareAble;
        this.mIsSaveFeedOption = isSaveFeedOption;
        this.isNotificationOn = isNotificationTurnedOn;
        this.isPostPinned = isPostPinned;
        mFeedFooterMenus = feedFooterMenus;
        mActionId = actionId;
        mClickableStringsList = clickableStrings;
        mClickableStringsListNew = clickableStringsNew;
        mVideoInfo = videoInfo;
        mWordStylingClickableParts = wordStylingClickableParts;
        mWebUrl = url;
        mActionTypeBody = actionTypeBody;
        mFeedAttachmentType = feedAttachmentType;
        mFeedType = type;
        mLatitude = latitude;
        mLongitude = longitude;
        mPlaceId = placeId;
        mLocationLabel = locationLabel;
        mHashTagString = hashTagString;
        mFeedReactions = feedReactions;
        mMyFeedReactions = myFeedReactions;
        mUserTagArray = userTagArray;
        this.mIsTranslation = isTranslation;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        mShareFeedIcon = shareFeedIcon;
        mShareActionId = shareActionId;
        mShareSubjectId = shareSubjectId;
        mShareFeedType = shareFeedType;
        mShareObjectType = shareObjectType;
        mShareObjectId = shareObjectId;
        mShareBody = shareBody;
        mShareDate = shareDate;
        mShareFeedActionTitle = shareFeedActionTitle;
        mIsShareFeed = isShareFeed;
        mShareClickableStringsList = shareClickableStrings;
        isGifLoad = false;
        mOtherMembers = otherMembers;
        subjectProfileInfo = subjectProfile;
    }
    public FeedList(JSONObject feedPostMenus, JSONArray FeedFilterArray, boolean isNoFeed, int reactionEnable,
                    JSONObject reactions) {
        mFeedPostMenus = feedPostMenus;
        mFeedFilterArray = FeedFilterArray;
        this.noFeed = isNoFeed;
        mReactionsEnabled = reactionEnable;
        mReactions = reactions;
    }

    public FeedList(String notifyPosition) {
        this.notifyPosition = notifyPosition;
    }

    public FeedList(int likeCount) {
        mLikeCount = likeCount;
    }

    public FeedList(String title, String thumb, String body, ArrayList<String> selectedFiles, String notifyPosition, Map<String, String> postParams) {
        posterTitle = title;
        posterThumb = thumb;
        postedItemBody = body;
        itemViewType = 12;
        this.notifyPosition = notifyPosition;

        mPhotoAttachmentCount = mAttachmentCount = (selectedFiles != null) ? selectedFiles.size() : 0;
        try {
            mFeedAttachmentArray = new JSONArray();
            mFeedAttachmentType = mFeedType = postParams.get("type");
            if (postParams.get("composer") != null) {
                JSONObject composerObject = new JSONObject(postParams.get("composer"));
                JSONObject bannerObject = composerObject.optJSONObject("banner");
                mCheckinObject = composerObject.optJSONObject("checkin");
                mFeedType = (mFeedType == null && mCheckinObject != null) ? "sitetagcheckin_status" : mFeedType;
                this.bannerObject = bannerObject;
            }

            this.decoration = (JSONObject) STATUS_POST_OPTIONS.get(ConstantVariables.FEED_DECORATION);
            if (mFeedType != null) {
                _generateAttachment(selectedFiles, postParams);

            }

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    protected FeedList(Parcel in) {
        mFeedIcon = in.readString();
        mMenuUrl = in.readString();
        mHashTagString = in.readString();
        mFeedTitle = in.readString();
        mFeedPostTime = in.readString();
        postPrivacy = in.readString();
        privacyIcon = in.readString();
        feedLink = in.readString();
        schedulePostTime = in.readString();
        mWebUrl = in.readString();
        mActionTypeBody = in.readString();
        mFeedAttachmentType = in.readString();
        pinPostDuration = in.readInt();
        mAttachmentCount = in.readInt();
        mLikeCount = in.readInt();
        mCommentCount = in.readInt();
        mIsLike = in.readInt();
        mPhotoAttachmentCount = in.readInt();
        mCommentAble = in.readInt();
        mShareAble = in.readInt();
        mIsSaveFeedOption = in.readInt();
        mCanComment = in.readInt();
        startIndex = in.readInt();
        endIndex = in.readInt();
        try {
            mFeedMenusArray = in.readByte() == 0x00 ? null : new JSONArray(in.readString());
            mFeedAttachmentArray = in.readByte() == 0x00 ? null : new JSONArray(in.readString());
            mFeedFilterArray = in.readByte() == 0x00 ? null : new JSONArray(in.readString());
            mFeedFooterMenus = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            mFeedPostMenus = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            mFeedObject = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            decoration = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            bannerObject = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            feelingObject = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            userTagObject = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            mMyFeedReactions = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            mFeedReactions = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            greetingsArray = in.readByte() == 0x00 ? null : new JSONArray(in.readString());
            birthdayArray = in.readByte() == 0x00 ? null : new JSONArray(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mHiddenBodyText = in.readString();
        mUndoHiddenFeedURl = in.readString();
        mHideAllText = in.readString();
        mHideAllUrl = in.readString();
        mHideAllName = in.readString();
        mFeedType = in.readString();
        mObjectType = in.readString();
        mSubjectId = in.readInt();
        mActionId = in.readInt();
        mObjectId = in.readInt();
        mOwnerFeedType = in.readInt();
        mClickableStringsList = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mVideoInfo = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mUndoHiddenFeedParams = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mWordStylingClickableParts = (HashMap) in.readValue(HashMap.class.getClassLoader());
        noFeed = in.readByte() != 0x00;
        isNotificationOn = in.readByte() != 0x00;
        isPostPinned = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            mPhotoDetails = new ArrayList<PhotoListDetails>();
            in.readList(mPhotoDetails, PhotoListDetails.class.getClassLoader());
        } else {
            mPhotoDetails = null;
        }
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mPlaceId = in.readString();
        mLocationLabel = in.readString();
        mIsTranslation = in.readString();
        mShareFeedIcon = in.readString();
        mShareActionId = in.readInt();
        mShareSubjectId = in.readInt();
        mShareFeedType = in.readInt();
        mShareObjectType = in.readString();
        mShareObjectId = in.readInt();
        mShareBody = in.readString();
        mShareDate = in.readString();
        mShareFeedActionTitle = in.readString();
        mIsShareFeed = in.readByte() != 0x00;
        mShareClickableStringsList = (HashMap) in.readValue(HashMap.class.getClassLoader());
        isGifLoad = in.readByte() != 0x00;

    }

    private void _generateAttachment(ArrayList<String> selectedFiles, Map<String, String> postParams) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        switch (mFeedType) {
            case "sticker":
                JSONObject src = new JSONObject();
                src.put("src", postParams.get("thumb"));
                src.put("size", new JSONObject().put("height", "0").put("width", "0"));
                jsonObject.put("image_main", src);
                jsonObject.put("mode", 1);
                jsonObject.put("attachment_type", "sitereaction_sticker");
                mFeedAttachmentType = mFeedType = "sitereaction_sticker";
                mFeedAttachmentArray.put(jsonObject);
                mPhotoAttachmentCount = mAttachmentCount = 1;
                break;
            case "sell":
                JSONObject sellItem = new JSONObject();
                sellItem.put("title", postParams.get("title"));
                sellItem.put("body", postParams.get("description"));
                sellItem.put("attachment_type", "advancedactivity_sell");
                sellItem.put("place", postParams.get("location"));
                sellItem.put("currency", postParams.get("currency"));
                sellItem.put("price", postParams.get("price"));
                sellItem.put("mode", 1);
                JSONArray sellImages = new JSONArray();
                for (String file : selectedFiles) {
                    sellImages.put(new JSONObject().put("image_main", file).put("image_medium", file));
                }
                sellItem.put("sell_image", sellImages);
                mFeedAttachmentType = mFeedType = "advancedactivity_sell";
                mFeedAttachmentArray.put(sellItem);
                mPhotoAttachmentCount = mAttachmentCount = 1;
                break;
            case "photo":
                for (String file : selectedFiles) {

                    JSONObject photos = new JSONObject();
                    photos.put("attachment_type", "album_photo");
                    photos.put("mode", 2);
                    JSONObject photoSrc = new JSONObject();
                    photoSrc.put("src", file);
                    photoSrc.put("size", new JSONObject().put("height", "0").put("width", "0"));
                    photos.put("image_main", photoSrc);
                    mFeedAttachmentArray.put(photos);

                }
                break;
            case "link":
                mPhotoAttachmentCount = mAttachmentCount = Status.mBody.length();
                mFeedAttachmentType = "core_link";
                mFeedType = "core_link";
                Status.mBody.put("attachment_type", mFeedType);
                Status.mBody.put("mode", 1);
                Status.mBody.put("body", Status.mBody.optString("description"));
                JSONObject LinkSrc = new JSONObject();
                LinkSrc.put("src", Status.mBody.optString("thumb"));
                LinkSrc.put("size", new JSONObject().put("height", "0").put("width", "0"));
                Status.mBody.put("image_main", LinkSrc);
                mPhotoAttachmentCount = mAttachmentCount = 1;
                mFeedAttachmentArray.put(Status.mBody);
                break;
            case "video":
                if (Status.mBody != null) {
                    jsonObject.put("title", Status.mBody.optString("title"));
                    jsonObject.put("body", Status.mBody.optString("description"));
                    jsonObject.put("attachment_type", "video");
                    JSONObject photoSrc = new JSONObject();
                    photoSrc.put("src", Status.mBody.optString("image"));
                    photoSrc.put("size", new JSONObject().put("height", "0").put("width", "0"));
                    jsonObject.put("image_main", photoSrc);
                    jsonObject.put("mode", 1);
                    mPhotoAttachmentCount = mAttachmentCount = 1;
                    mFeedAttachmentArray.put(jsonObject);
                } else {
                    mFeedAttachmentType = mFeedType = "video";
                    jsonObject.put("title", " ");
                    jsonObject.put("body", " ");
                    jsonObject.put("attachment_type", "video");
                    JSONObject photoSrc = new JSONObject();
                    photoSrc.put("src", postParams.get("mVideoThumbnail"));
                    photoSrc.put("size", new JSONObject().put("height", "0").put("width", "0"));
                    jsonObject.put("image_main", photoSrc);
                    jsonObject.put("mode", 1);
                    mPhotoAttachmentCount = mAttachmentCount = 1;
                    mFeedAttachmentArray.put(jsonObject);
                }

                break;
            case "music":
                mFeedAttachmentType = mFeedType = "music_playlist_song";
                jsonObject.put("title", (Status.mBody != null) ? Status.mBody.get("title") : " ");
                jsonObject.put("body", " ");
                jsonObject.put("attachment_type", "music_playlist_song");
                jsonObject.put("mode", 1);
                mPhotoAttachmentCount = mAttachmentCount = 1;
                mFeedAttachmentArray.put(jsonObject);
                break;
            case "sitetagcheckin_status":
                mLatitude =  mCheckinObject.optDouble("latitude");
                mLongitude = mCheckinObject.optDouble("longitude");
                mLocationLabel = mCheckinObject.optString("label");
                break;

        }
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

    public String getIsTranslation() {
        return mIsTranslation;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public boolean isNoFeed() {
        return noFeed;
    }

    public String getFeedLink() {
        return feedLink;
    }

    public String getSchedulePostTime() {
        return schedulePostTime;
    }

    public String getPostPrivacy() {
        return postPrivacy;
    }

    public String getPrivacyIcon() {
        return privacyIcon;
    }

    public boolean isNotificationOn() {
        return isNotificationOn;
    }

    public void setNotificationOn(boolean notificationOn) {
        isNotificationOn = notificationOn;
    }

    public boolean isPostPinned() {
        return isPostPinned;
    }

    public String getmFeedIcon() {
        return mFeedIcon;
    }

    public String getmFeedTitle() {
        return mFeedTitle;
    }

    public String getmFeedPostTime() {
        return mFeedPostTime;
    }

    public JSONArray getmFeedMenusArray() {
        return mFeedMenusArray;
    }

    public void setmFeedMenusArray(JSONArray mFeedMenusArray) {
        this.mFeedMenusArray = mFeedMenusArray;
    }

    public String getHashTagString() {
        return mHashTagString;
    }

    public int getmAttachmentCount() {
        return mAttachmentCount;
    }

    public void setmAttachmentCount(int attachmentCount) {
        this.mAttachmentCount = attachmentCount;
    }

    public int getPinPostDuration() {
        return pinPostDuration;
    }

    public int getmLikeCount() {
        return mLikeCount;
    }

    public void setmLikeCount(int mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    public int getmCommentCount() {
        return mCommentCount;
    }

    public void setmCommentCount(int mCommentCount) {
        this.mCommentCount = mCommentCount;
    }

    public int getmIsLike() {
        return mIsLike;
    }

    public void setmIsLike(int mIsLike) {
        this.mIsLike = mIsLike;
    }

    public int ismCanComment() {
        return mCanComment;
    }

    public void setmCanComment(int mCanComment) {
        this.mCanComment = mCanComment;
    }

    public int getmPhotoAttachmentCount() {
        return mPhotoAttachmentCount;
    }

    public JSONArray getmFeedAttachmentArray() {
        return mFeedAttachmentArray;
    }

    public void setmFeedAttachmentArray(JSONArray mFeedAttachmentArray) {
        this.mFeedAttachmentArray = mFeedAttachmentArray;
    }

    public JSONObject getmFeedFooterMenus() {
        return mFeedFooterMenus;
    }

    public void setmFeedFooterMenus(JSONObject mFeedFooterMenus) {
        this.mFeedFooterMenus = mFeedFooterMenus;
    }

    public String getmHiddenBodyText() {
        return mHiddenBodyText;
    }

    public void setmHiddenBodyText(String mHiddenBodyText) {
        this.mHiddenBodyText = mHiddenBodyText;
    }

    public String getmUndoHiddenFeedURl() {
        return mUndoHiddenFeedURl;
    }

    public void setmUndoHiddenFeedURl(String mUndoHiddenFeedURl) {
        this.mUndoHiddenFeedURl = mUndoHiddenFeedURl;
    }

    public HashMap<String, String> getmUndoHiddenFeedParams() {
        return mUndoHiddenFeedParams;
    }

    public void setmUndoHiddenFeedParams(HashMap<String, String> mUndoHiddenFeedParams) {

        this.mUndoHiddenFeedParams = mUndoHiddenFeedParams;
    }

    public String getmHideAllText() {
        return mHideAllText;
    }

    public void setmHideAllText(String mHideAllText) {
        this.mHideAllText = mHideAllText;
    }

    public String getmHideAllUrl() {
        return mHideAllUrl;
    }

    public void setmHideAllUrl(String mHideAllUrl) {
        this.mHideAllUrl = mHideAllUrl;
    }

    public String getmHideAllName() {
        return mHideAllName;
    }

    public void setmHideAllName(String mHideAllName) {
        this.mHideAllName = mHideAllName;
    }

    public int getmCommentAble() {
        return mCommentAble;
    }

    public void setmCommentAble(int mCommentAble) {
        this.mCommentAble = mCommentAble;
    }

    public int getmShareAble() {
        return mShareAble;
    }

    public void setmShareAble(int mShareAble) {
        this.mShareAble = mShareAble;
    }

    public int getmIsSaveFeedOption() {
        return mIsSaveFeedOption;
    }

    public void setmIsSaveFeedOption(int mIsSaveFeedOption) {
        this.mIsSaveFeedOption = mIsSaveFeedOption;
    }

    public int getmSubjectId() {
        return mSubjectId;
    }

    public void setmSubjectId(int mSubjectId) {
        this.mSubjectId = mSubjectId;
    }

    public int getmActionId() {
        return mActionId;
    }

    public void setmActionId(int mActionId) {
        this.mActionId = mActionId;
    }

    public HashMap<String, String> getmClickableStringsList() {
        return mClickableStringsList;
    }

    public JSONArray getmFeedFilterArray() {
        return mFeedFilterArray;
    }

    public JSONObject getmFeedPostMenus() {
        return mFeedPostMenus;
    }

    public ArrayList<PhotoListDetails> getmPhotoDetails() {
        return mPhotoDetails;
    }

    public void setmPhotoDetails(ArrayList<PhotoListDetails> mPhotoDetails) {
        this.mPhotoDetails = mPhotoDetails;
    }

    public String getmActionTypeBody() {
        return mActionTypeBody;
    }

    public void setmActionTypeBody(String mActionTypeBody) {
        this.mActionTypeBody = mActionTypeBody;
    }

    public JSONObject getmFeedObject() {
        return mFeedObject;
    }

    public JSONObject getDecoration() {
        return decoration;
    }

    public JSONObject getBannerObject() {
        return bannerObject;
    }

    public JSONObject getFeelingObject() {
        return feelingObject;
    }

    public JSONObject getUserTagObject() {
        return userTagObject;
    }

    public HashMap<Integer, String> getmVideoInfo() {
        return mVideoInfo;
    }

    public HashMap<String, String> getWordStylingClickableParts() {
        return mWordStylingClickableParts;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }

    public String getmFeedType() {
        return mFeedType;
    }

    public String getmFeedAttachmentType() {
        return mFeedAttachmentType;
    }

    public String getmShareFeedIcon() {
        return mShareFeedIcon;
    }

    public int getmShareActionId() {
        return mShareActionId;
    }

    public int getmShareSubjectId() {
        return mShareSubjectId;
    }

    public int getmShareFeedType() {
        return mShareFeedType;
    }

    public int getmShareObjectId() {
        return mShareObjectId;
    }

    public String getmShareObjectType() {
        return mShareObjectType;
    }

    public String getmShareBody() {
        return mShareBody;
    }

    public String getmShareDate() {
        return mShareDate;
    }

    public String getmShareFeedActionTitle() {
        return mShareFeedActionTitle;
    }

    public boolean getmIsShareFeed() {
        return mIsShareFeed;
    }

    public HashMap<String, String> getmShareClickableStringsList() {
        return mShareClickableStringsList;
    }

    public boolean getIsGifLoad() {
        return isGifLoad;
    }

    public void setIsGifLoad(boolean gifLoad) {
        isGifLoad = gifLoad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFeedIcon);
        dest.writeString(mMenuUrl);
        dest.writeString(mHashTagString);
        dest.writeString(mFeedTitle);
        dest.writeString(mFeedPostTime);
        dest.writeString(postPrivacy);
        dest.writeString(privacyIcon);
        dest.writeString(feedLink);
        dest.writeString(schedulePostTime);
        dest.writeString(mWebUrl);
        dest.writeString(mActionTypeBody);
        dest.writeString(mFeedAttachmentType);
        dest.writeInt(pinPostDuration);
        dest.writeInt(mAttachmentCount);
        dest.writeInt(mLikeCount);
        dest.writeInt(mCommentCount);
        dest.writeInt(mIsLike);
        dest.writeInt(mPhotoAttachmentCount);
        dest.writeInt(mCommentAble);
        dest.writeInt(mShareAble);
        dest.writeInt(mIsSaveFeedOption);
        dest.writeInt(mCanComment);
        dest.writeInt(startIndex);
        dest.writeInt(endIndex);
        if (mFeedMenusArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedMenusArray.toString());
        }
        if (mFeedAttachmentArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedAttachmentArray.toString());
        }
        if (mFeedFilterArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedFilterArray.toString());
        }
        if (mFeedFooterMenus == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedFooterMenus.toString());
        }
        if (mFeedPostMenus == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedPostMenus.toString());
        }
        if (mFeedObject == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedObject.toString());
        }
        if (decoration == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(decoration.toString());
        }
        if (bannerObject == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(bannerObject.toString());
        }
        if (feelingObject == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(feelingObject.toString());
        }
        if (userTagObject == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(userTagObject.toString());
        }
        if (mMyFeedReactions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mMyFeedReactions.toString());
        }
        if (mFeedReactions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mFeedReactions.toString());
        }
        if (greetingsArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(greetingsArray.toString());
        }
        if (birthdayArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(birthdayArray.toString());
        }
        dest.writeString(mHiddenBodyText);
        dest.writeString(mUndoHiddenFeedURl);
        dest.writeString(mHideAllText);
        dest.writeString(mHideAllUrl);
        dest.writeString(mHideAllName);
        dest.writeString(mFeedType);
        dest.writeString(mObjectType);
        dest.writeInt(mSubjectId);
        dest.writeInt(mActionId);
        dest.writeInt(mObjectId);
        dest.writeInt(mOwnerFeedType);
        dest.writeValue(mClickableStringsList);
        dest.writeValue(mVideoInfo);
        dest.writeValue(mUndoHiddenFeedParams);
        dest.writeValue(mWordStylingClickableParts);
        dest.writeByte((byte) (noFeed ? 0x01 : 0x00));
        dest.writeByte((byte) (isNotificationOn ? 0x01 : 0x00));
        dest.writeByte((byte) (isPostPinned ? 0x01 : 0x00));
        if (mPhotoDetails == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mPhotoDetails);
        }
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeString(mPlaceId);
        dest.writeString(mLocationLabel);
        dest.writeString(mIsTranslation);
        dest.writeString(mShareFeedIcon);
        dest.writeInt(mShareActionId);
        dest.writeInt(mShareSubjectId);
        dest.writeInt(mShareFeedType);
        dest.writeString(mShareObjectType);
        dest.writeInt(mShareObjectId);
        dest.writeString(mShareBody);
        dest.writeString(mShareDate);
        dest.writeString(mShareFeedActionTitle);
        dest.writeByte((byte) (mIsShareFeed ? 0x01 : 0x00));
        dest.writeValue(mShareClickableStringsList);
        dest.writeByte((byte) (isGifLoad ? 0x01 : 0x00));
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public String getmPlaceId() {
        return mPlaceId;
    }

    public String getmLocationLabel() {
        return mLocationLabel;
    }

    public String getmMenuUrl() {
        return mMenuUrl;
    }

    public void setmMenuUrl(String mMenuUrl) {
        this.mMenuUrl = mMenuUrl;
    }

    public int getmReactionsEnabled() {
        return mReactionsEnabled;
    }

    public JSONObject getmReactions() {
        return mReactions;
    }

    public int getmObjectId() {
        return mObjectId;
    }

    public String getmObjectType() {
        return mObjectType;
    }

    public int getmOwnerFeedType() {
        return mOwnerFeedType;

    }

    public JSONObject getmFeedReactions() {
        return mFeedReactions;
    }

    public void setmFeedReactions(JSONObject mFeedReactions) {
        this.mFeedReactions = mFeedReactions;
    }

    public JSONObject getmMyFeedReactions() {
        return mMyFeedReactions;
    }

    public void setmMyFeedReactions(JSONObject mMyFeedReactions) {
        this.mMyFeedReactions = mMyFeedReactions;
    }

    public HashMap<String, String> getmClickableStringsListNew() {
        return mClickableStringsListNew;
    }

    public JSONArray getmUserTagArray() {
        return mUserTagArray;
    }

    public void setBrowseItemList(List<Object> browseListItem, int muteStoryCount, boolean isStoryDataChange) {
        this.browseListItem = browseListItem;
        this.muteStoryCount = muteStoryCount;
        this.isStoryDataChange = isStoryDataChange;
    }

    public List<Object> getBrowseListItem() {
        return browseListItem;
    }


    public int getMuteStoryCount() {
        return muteStoryCount;
    }

    public boolean getIsStoryDataChange() {
        return isStoryDataChange;
    }

    public void setStoryDataChange(boolean storyDataChange) {
        isStoryDataChange = storyDataChange;
    }

    public void setGreetingsArray(JSONArray greetingsArray, boolean isGreetingSet) {
        this.greetingsArray = greetingsArray;
        this.isGreetingSet = isGreetingSet;
    }

    public JSONArray getGreetingsArray() {
        return greetingsArray;
    }

    public JSONArray getBirthdayArray() {
        return birthdayArray;
    }

    public boolean isGreetingSet() {
        return isGreetingSet;
    }

    public void setGreetingSet(boolean greetingSet) {
        isGreetingSet = greetingSet;
    }

    public void setBirthdayArray(JSONArray birthdayArray) {
        this.birthdayArray = birthdayArray;
    }

    @Override
    public int hashCode() {
        if(this.notifyPosition != null) {
            int hash = 3;
            hash = 7 * hash + this.notifyPosition.hashCode();
            return hash;
        }
        return 0;
    }

    public String getNotifyPosition() {
        return notifyPosition;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            FeedList feedList = (FeedList) object;
            if (this.notifyPosition.equals(feedList.getNotifyPosition())) {
                result = true;
            }
        }
        return result;
    }
    public void setRequestCanceled(boolean isCancled){
        this.requestCanceled = isCancled;
    }
    public boolean isRequestCanceled(){
        return this.requestCanceled;
    }
    public JSONObject getSubjectProfileInfo() {
        return subjectProfileInfo;
    }
}
