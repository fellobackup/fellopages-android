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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BrowseListItems {
    String mBrowseImgUrl,mVideoUrl, mStaff,mArtistImageUrl, mHostImageUrl, mEventStatus;
    String mBrowseListTitle, mDescription, mFriendShipType, mEmail;
    String mBrowseListOwnerTitle;
    String mBrowseViewCount, mBrowseVoteCount;
    String mBrowseListCreationDate, mLocation, mStartTime;
    int mListItemId, mPhotoCount, mCommentCount, mMemberCount, mUserId , mTotalItemCount, mIsRequestInvite, mIsPaid;
    int mViewCount, mLikeCount, mDuration, mVideoType, mVideoRating, mRatingCount, mIsGroupAdmin;
    JSONArray menuArray, mRatingParams;
    boolean mIsRequestSent, mIsLoading, allowToView, mShowAddPeople, mGroupPublished, isLessTextShowing;
    int mIsOwner, mRsvp, mClosed, mEventId, mEventCount, mSubscribed, mWatched, mSticky, mJoined, mProfileRsvpValue;
    JSONObject mMemberMenus, responseObject, bannerObject, parentObject;
    int mTotalTracks, canEdit, mTotalPlayCount, mContentId, mIsClosed, mStatusCode;
    String mModuleType, mAlbumId, mRedirectUrl, mContentUrl, mModuleName, mBody, mBrowseListName;

    //For Messages
    String mMessageTitle, mMessageBody;
    int mMessageId, mConversationId, mInboxRead, mInboxDeleted, mPopularityCount, mCanAddToList;
    JSONObject mReceiverObject;

    //For Advanced Member
    int mIsSiteMember, mMutualFriendCount, mAge, mIsMemberOnline;
    int isFriendshipVerified, isMemberVerified;
    boolean isAdminApprovalRequired, isAdminApproved;

    //For Notifications
    JSONObject mNotificationObject, mSubjectResponse, mObjectResponse;
    String mActionTypeBody, mFeedTitle, mNotificationType, mNotificationSubjectType, mNotificationObjectType;
    String mNotificationUrl;
    JSONArray mActionBodyParamsArray, mMenuArray, mGuestArray;
    int mNotificationId, mNotificationSubjectId, mNotificationObjectId, size=0,isRead, mOwnerId;


    //For MLT
    int mListingTypeId, mTotalListingItem, mReviewId, mReviewRating, mLiked, mFollowed,storeId, mIsApplyJob;
    String mListingImage1, mListingImage2, mListingImage3, mPrice, mCurrency;
    JSONArray mUpdatedReviewArray;
    boolean mIsHeader;
    public JSONObject sliderObject;

    String mOrder, mImage1, mImage2, mImage3, mHostType;
    boolean mIsShowOptionMenu, isFollowingMember;
    int mHasMultipleDates;
    private String icon, title, key, userDisplayName, userProfileImageUrl,
            messageOwnerUrl, messageOwnerTitle;
    private int color, userId;

    public double mLatitude, mLongitude;
    /*
        Forum Module items
    */
    //ForumHome items


    int categoryId, forumId, topicCount, postCount, mPageId, mSponsored, mFeatured;
    String forumTitle, categoryTitle, slug;
    boolean forumAllowToView;

    //ForumProfile items
    boolean topicAllowToView;
    String topicTitle, topicSlug, topicModifiedDate, topicDescription, topicOwnerImage, displayName;
    int topicLastPostByUserId, totalReplies, topicId, forumTopicId, isSticky, isTopicClose, topicTotalItemCount;

    //ForumView items
    int postId,ownerPostCount, postTotalItemCount, postedByUserId, postEditId, postEditByUserId;
    String postCreationDate, postModifiedDate, postBody, postByName, postByImage, postEditByDisplayName;
    JSONArray forumMenuArray;

    //user reviews advanced events
    int mHelpfulCount, mRecommend, mNotHelpfulCount, mOccurrenceId, mOverallRating, mReviewCount,totalClaims;
    String mPros, mCons, mBrowseBody, mEndTime, mRecommendText,startTime;
    Boolean mIsHelpful = false, mIsNotHelpful= false, mIsWatingList, mIsLiked;


    // SitePage
    int mFollowCount, mIsShowProsCons;

    //For Package Listing
    int mPackageId;
    JSONObject mPackageObject, mDescriptionObject;

    // Package View Page
    String mPackageParamLabel, mPackageParamValue;

    int mPublished = 0;

    // Browse Offers

    String mCouponCode, mOfferDescription;
    int mOfferId, mEndSetting, mClaimCount;

    // Stickers Items
    int mStickersCount;

    //My Tickets
    JSONObject mOrderInfo, mShippingItem;
    String mStatus;
    int mOrderId, mMinValue, mMaxValue, mTicketsPrice;
    String mQuantity, mStatusColor;
    Double mSubTotal;

    private int mMinPriceOption;
    private String mListImage, mPriceTagImage, mListUrl;

    // For Advanced video.
    int subscribeCount, videosCount, isFavouriteOption;
    boolean canRemoveFromPlayList;

    // For Story.
    JSONObject storyObject;
    boolean isMuteStory;
    int mMuteStoryCount;
    boolean mIsPackageUpgrade;

    //for Available Tickets
    String mticket_title, mticket_price, mticket_quantity;
    int mticket_id;
    JSONArray mMenuJsonArray;

    public JSONObject mMapData;
    private boolean isRequestProcessing;

    public static final int VIEW_REQUEST = 2;
    public static final int VIEW_ITEM = 1;

    public int viewType;


//    private

    public BrowseListItems(String image, String title, int listingId) {
        mListItemId = listingId;
        mBrowseListTitle = title;
        mBrowseImgUrl = image;
    }

    public BrowseListItems(JSONObject jsonObject, int lisitngId) {
        sliderObject = jsonObject;
        mListingTypeId = lisitngId;
    }


    public int getStoryId() {
        return storyObject.optInt("story_id");
    }

    public int getStoryOwnerId() {
        return storyObject.optInt("owner_id");
    }

    public String getStoryOwner() {
        return storyObject.optString("owner_title");
    }

    public String getStoryImage() {
        if (storyObject.optInt("story_id") != 0) {
            return storyObject.optString("image_profile");
        } else {
            return storyObject.optString("owner_image_profile");
        }
    }

    public String getOwnerImage() {
        return storyObject.optString("owner_image_icon");
    }

    public int isMuteStory() {
        return storyObject.optInt("isMute");
    }

    public String getStoryVideoUrl() {
        return storyObject.optString("videoUrl");
    }

    public String getStoryCreationDate() {
        return storyObject.optString("create_date");
    }

    public int getMuteStoryCount() {
        return mMuteStoryCount;
    }

    public void setMuteStoryCount(int muteStoryCount) {
        this.mMuteStoryCount = muteStoryCount;
    }

    public BrowseListItems(String imageUrl, JSONObject bannerObject) {
        mBrowseImgUrl = imageUrl;
        this.bannerObject = bannerObject;
    }

    public BrowseListItems(String imageUrl, String title, JSONObject parentObject) {
        mBrowseImgUrl = imageUrl;
        mBrowseListTitle = title;
        this.parentObject = parentObject;
    }

    public JSONObject getParentObject() {
        return parentObject;
    }

    public JSONObject getBannerObject() {
        return bannerObject;
    }

    public boolean isAlreadyAdded() {
        return isAlreadyAdded;
    }

    public void setAlreadyAdded(boolean alreadyAdded) {
        isAlreadyAdded = alreadyAdded;
    }

    boolean isAlreadyAdded = false;

    public BrowseListItems(){

    }

    /*
        For Manage Forum Module items
    */
    //ForumHome Page
    public BrowseListItems(int categoryId, String categoryTitle, int forumId) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.forumId = forumId;
    }
    public BrowseListItems(int forumId, int topicCount,int postCount,
                           String forumTitle, String slug,boolean forumAllowToView) {
        this.forumId = forumId;
        this.topicCount = topicCount;
        this.postCount = postCount;
        this.forumTitle = forumTitle;
        this.slug = slug;
        this.forumAllowToView = forumAllowToView;
    }

    //ForumProfile Page
    public BrowseListItems(int isTopicClose, int isSticky, int forumTopicId, int totalReplies, int topicLastPostByUserId,
                           int topicId, String displayName, String topicOwnerImage, String topicDescription,
                           String topicModifiedDate, String topicSlug, String topicTitle, boolean topicAllowToView) {
        this.isTopicClose = isTopicClose;
        this.isSticky = isSticky;
        this.forumTopicId = forumTopicId;
        this.totalReplies = totalReplies;
        this.topicLastPostByUserId = topicLastPostByUserId;
        this.topicId = topicId;
        this.displayName = displayName;
        this.topicOwnerImage = topicOwnerImage;
        this.topicDescription = topicDescription;
        this.topicModifiedDate = topicModifiedDate;
        this.topicSlug = topicSlug;
        this.topicTitle = topicTitle;
        this.topicAllowToView = topicAllowToView;
    }

    //ForumView Page
    public BrowseListItems(String postByImage, String postByName, String postCreationDate,
                           int postEditId, String postBody, int postId, int ownerPostCount,JSONArray forumMenuArray,
                           String postModifiedDate, int postedByUserId, int postEditByUserId, String postEditByDisplayName) {
        this.postByImage = postByImage;
        this.postByName = postByName;
        this.postCreationDate = postCreationDate;
        this.postEditId = postEditId;
        this.postBody = postBody;
        this.postId = postId;
        this.ownerPostCount = ownerPostCount;
        this.forumMenuArray = forumMenuArray;
        this.postModifiedDate = postModifiedDate;
        this.postedByUserId = postedByUserId;
        this.postEditByUserId = postEditByUserId;
        this.postEditByDisplayName = postEditByDisplayName;
    }



    //For Manage Blog and Classified Items
    public BrowseListItems(String imgurl,String listTitle,String ownerTitle,String creationDate,
                           int listItemId, JSONArray menuJsonArray){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mBrowseListCreationDate = creationDate;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
    }

    //For Browse Blog
    public BrowseListItems(String imgurl,String listTitle,String ownerTitle,String creationDate,
                           int listItemId, boolean viewAllowed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mBrowseListCreationDate = creationDate;
        mListItemId = listItemId;
        allowToView = viewAllowed;
    }

    //For Browse Available Tickets
    public BrowseListItems(String ticket_title, String ticket_price, String ticket_quantity,
                           int ticket_id, JSONArray menuJsonArray){
        mBrowseListTitle = ticket_title;
        mticket_price = ticket_price;
        mticket_quantity = ticket_quantity;
        mticket_id = ticket_id;
        menuArray = menuJsonArray;
    }

    //For Browse Classified
    public BrowseListItems(String imgurl,String listTitle,String ownerTitle,String creationDate,
                           int listItemId, boolean viewAllowed, int isClosed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mBrowseListCreationDate = creationDate;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mIsClosed = isClosed;
    }

    //For Browse Poll
    public BrowseListItems(String imgurl,String listTitle,String ownerTitle,String creationDate,
                           String voteCount,String viewCount, int closed, int listItemId, boolean viewAllowed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mBrowseListCreationDate = creationDate;
        mBrowseVoteCount = voteCount;
        mBrowseViewCount = viewCount;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mClosed = closed;

    }

    //For Manage Poll
    public BrowseListItems(String imgurl,String listTitle,String ownerTitle,String creationDate,
                           String voteCount,String viewCount,int closed,int listItemId,
                           JSONArray menuJsonArray, int listSize){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mBrowseListCreationDate = creationDate;
        mBrowseVoteCount = voteCount;
        mBrowseViewCount = viewCount;
        mClosed = closed;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
        size = listSize;
    }

    //For Browse album Items
    public BrowseListItems(String imgurl,String listTitle,String ownerTitle,int photoCount,
                           int commentCount,int viewCount, int likeCount,int listItemId,boolean viewAllowed){

        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mListItemId = listItemId;
        mPhotoCount=photoCount;
        mCommentCount=commentCount;
        allowToView = viewAllowed;
        mLikeCount = likeCount;
        mViewCount = viewCount;
    }
    //For Manage album
    public BrowseListItems(String imgurl,String listTitle,int photoCount,String ownerTitle,
                           int listItemId,JSONArray menuJsonArray){

        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle= ownerTitle;
        mListItemId = listItemId;
        mPhotoCount=photoCount;
        menuArray = menuJsonArray;

    }


    //For Browse Group items
    public BrowseListItems(String imgurl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, boolean viewAllowed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        allowToView = viewAllowed;
    }

    //For Manage Group
    public BrowseListItems(String imgurl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, JSONArray menuJsonArray){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
    }

    //For Group Members
    public BrowseListItems(int contentId, String imgurl, String ownerTitle, String staff, JSONArray menuJsonArray,
                           int userId, String friendshipType, int isGroupAdmin, int isFriendshipVerified){
        mListItemId = contentId;
        mBrowseImgUrl = imgurl;
        mBrowseListOwnerTitle = ownerTitle;
        mStaff = staff;
        menuArray = menuJsonArray;
        mUserId = userId;
        mFriendShipType = friendshipType;
        mIsGroupAdmin = isGroupAdmin;
        this.isFriendshipVerified = isFriendshipVerified;
    }




    //For Browse Video page
    public BrowseListItems(String imgurl,String listTitle,String createdDate,int likeCount,
                           int commentCount,int viewCount,int rating,int rating_count, int duration,int listItemId,
                           String videoUrl,String contentUrl, int videoType, boolean viewAllowed){

        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListCreationDate = createdDate;
        mCommentCount = commentCount;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mViewCount = viewCount;
        mLikeCount = likeCount;
        mDuration = duration;
        mVideoUrl = videoUrl;
        mContentUrl = contentUrl;
        this.mVideoType = videoType;
        mVideoRating = rating;
        mRatingCount = rating_count;


    }
    //For Manage Video page
    public BrowseListItems(String imgurl,String listTitle,String createdDate,int likeCount, int commentCount,
                           int viewCount,int rating, int rating_count,int duration, int listItemId,
                           int event_id, String videoUrl,String contentUrl, int videoType, JSONArray menuJsonArray){

        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListCreationDate = createdDate;
        mCommentCount = commentCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
        mViewCount = viewCount;
        mLikeCount = likeCount;
        mDuration = duration;
        mEventId = event_id;
        mVideoUrl = videoUrl;
        mContentUrl = contentUrl;
        this.mVideoType = videoType;
        mVideoRating = rating;
        mRatingCount = rating_count;


    }

    //For Browse Music page
    public BrowseListItems(String owner_image,String album_image,String owner_name,String album_title,
                           int total_Tracks,int totalPlayCount,String creation_date,int item_id,
                           String item_descr, boolean isAllowToView){
        mBrowseImgUrl = owner_image;
        mBrowseListTitle = album_title;
        mArtistImageUrl = album_image;
        mBrowseListOwnerTitle = owner_name;
        mBrowseListCreationDate = creation_date;
        mListItemId = item_id;
        mDescription = item_descr;
        allowToView = isAllowToView;
        mTotalTracks = total_Tracks;
        mTotalPlayCount = totalPlayCount;

    }

    //For Manage Music Page

    public BrowseListItems(String owner_image,String album_image,String owner_name,String album_title,
                           int total_Tracks,int totalPlayCount,String creation_date,int item_id,
                           String item_descr,JSONArray menuJsonArray){
        mBrowseImgUrl = owner_image;
        mBrowseListTitle = album_title;
        mArtistImageUrl = album_image;
        mBrowseListOwnerTitle = owner_name;
        mBrowseListCreationDate = creation_date;
        mListItemId = item_id;
        mDescription = item_descr;
        menuArray = menuJsonArray;
        mTotalTracks = total_Tracks;
        mTotalPlayCount = totalPlayCount;

    }


    //For Event Guest
    public BrowseListItems(String imgurl, String ownerTitle, int isOwner, int rsvp, JSONArray menuJsonArray,
                           int userId, String friendShipType, int isFriendshipVerified){
        mBrowseImgUrl = imgurl;
        mBrowseListOwnerTitle = ownerTitle;
        mIsOwner = isOwner;
        mRsvp = rsvp;
        menuArray = menuJsonArray;
        mUserId = userId;
        mFriendShipType = friendShipType;
        this.isFriendshipVerified = isFriendshipVerified;
    }

    //For Browse Events
    public BrowseListItems(String imgurl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, boolean viewAllowed, String location, String startTime){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mLocation = location;
        mStartTime = startTime;
    }

    //For Manage Events
    public BrowseListItems(String imgurl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, JSONArray menuJsonArray, String location, String startTime, int joined){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
        mLocation = location;
        mStartTime = startTime;
        mJoined = joined;
    }


    //For Browse Members
    public BrowseListItems(int isSiteMember, int userId, String imgurl, String ownerTitle,
                           JSONObject menuJsonArray, int mutualFriendCount, int age, int verified,
                           String location, int isMemberOnline, int canAddToList,
                           JSONObject responseObject, int isFriendshipVerified, JSONObject mapData,
                           String friendShipType, JSONArray menuArray){
        mIsSiteMember = isSiteMember;
        mBrowseImgUrl = imgurl;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberMenus = menuJsonArray;
        mUserId = userId;
        mMutualFriendCount = mutualFriendCount;
        mAge = age;
        isMemberVerified = verified;
        mLocation = location;
        mIsMemberOnline = isMemberOnline;
        mCanAddToList = canAddToList;
        this.responseObject = responseObject;
        this.isFriendshipVerified = isFriendshipVerified;
        mMapData = mapData;
        this.mFriendShipType = friendShipType;
        this.menuArray = menuArray;
    }


    //For Messages
    public BrowseListItems(String userDisplayName, String userImageUrl,String messageTitle,
                           String messageBody, int conversationId, int messageId, String messageDate,
                           JSONObject receiverObject, int inboxRead, int inboxDeleted){

        mBrowseImgUrl = userImageUrl;
        mBrowseListOwnerTitle = userDisplayName;
        mMessageTitle = messageTitle;
        mMessageBody = messageBody;
        mConversationId = conversationId;
        mMessageId = messageId;
        mReceiverObject = receiverObject;
        mInboxRead = inboxRead;
        mInboxDeleted = inboxDeleted;
        mBrowseListCreationDate = messageDate;


    }

    //For Friend Requests
    public BrowseListItems(int requestSenderId,String requestSenderName,String requestSenderImageUrl){

        mBrowseImgUrl =requestSenderImageUrl;
        mBrowseListOwnerTitle = requestSenderName;
        mUserId = requestSenderId;
    }

    public BrowseListItems(int viewType, int requestSenderId,String requestSenderName,String requestSenderImageUrl){
        this.viewType = viewType;
        mBrowseImgUrl =requestSenderImageUrl;
        mBrowseListOwnerTitle = requestSenderName;
        mUserId = requestSenderId;
    }



    //For Global Search
    public BrowseListItems(String imgUrl, String listTitle, String body, String type, int id, String contentUrl,
                           String moduleName, int listingTypeId, int listingId, String topicSlug){

        mBrowseImgUrl = imgUrl;
        mBrowseListTitle = listTitle;
        mDescription = body;
        mModuleType = type;
        mContentId = id;
        mContentUrl = contentUrl;
        mModuleName = moduleName;
        mListingTypeId = listingTypeId;
        mListItemId = listingId;
        this.topicSlug = topicSlug;
    }



    //For notifications
    public BrowseListItems(int notificationId,int userId,int subjectId,int objectId,int read,
                           String subjectType, String objectType, JSONObject notificationObject,
                           String actionTypeBody, String feedTitle, String notificationType,String url,
                           JSONObject subjectResponse, JSONObject objectResponse, JSONArray actionBodyParamsArray) {
        mUserId = userId;
        mNotificationObjectId = objectId;
        mNotificationSubjectId =subjectId;
        isRead = read;
        mNotificationId =notificationId;
        mNotificationObjectType = objectType;
        mNotificationSubjectType = subjectType;
        mNotificationObject =notificationObject;
        mActionTypeBody = actionTypeBody;
        mFeedTitle = feedTitle;
        mNotificationType = notificationType;
        mSubjectResponse = subjectResponse;
        mObjectResponse = objectResponse;
        mActionBodyParamsArray =actionBodyParamsArray;
        mNotificationUrl = url;
    }

    public BrowseListItems(int viewType, int notificationId,int userId,int subjectId,int objectId,int read,
                           String subjectType, String objectType, JSONObject notificationObject,
                           String actionTypeBody, String feedTitle, String notificationType,String url,
                           JSONObject subjectResponse, JSONObject objectResponse, JSONArray actionBodyParamsArray) {
        this.viewType = viewType;
        mUserId = userId;
        mNotificationObjectId = objectId;
        mNotificationSubjectId =subjectId;
        isRead = read;
        mNotificationId =notificationId;
        mNotificationObjectType = objectType;
        mNotificationSubjectType = subjectType;
        mNotificationObject =notificationObject;
        mActionTypeBody = actionTypeBody;
        mFeedTitle = feedTitle;
        mNotificationType = notificationType;
        mSubjectResponse = subjectResponse;
        mObjectResponse = objectResponse;
        mActionBodyParamsArray =actionBodyParamsArray;
        mNotificationUrl = url;
    }

    /*
        For MLT Module items
    */
    // BrowseMLT
    public BrowseListItems(int listingId, int listingTypeId, String listingTitle, String listingImage, String listingOwnerTitle,
                           String creationDate, String location, String price, String currency, boolean isAllowToView, int isClosed,
                           int featured, int sponsored, double latitude, double longitude, String categoryTitle) {

        mListItemId = listingId;
        this.mListingTypeId = listingTypeId;
        mBrowseListTitle = listingTitle;
        mBrowseImgUrl = listingImage;
        mBrowseListOwnerTitle = listingOwnerTitle;
        mBrowseListCreationDate = creationDate;
        mLocation = location;
        mPrice = price;
        this.mCurrency = currency;
        this.allowToView = isAllowToView;
        mClosed = isClosed;
        mFeatured = featured;
        mSponsored = sponsored;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.categoryTitle = categoryTitle;
    }

    // ManageMLT
    public BrowseListItems(int listingId, int listingTypeId, String listingTitle, String body, String listingImage, String listingOwnerTitle,
                           String creationDate, String location, String price, String currency, int viewCount,
                           int reviewCount, int commentCount, int likeCount, boolean isAllowToView,
                           int isClosed, JSONArray menuArray) {

        mListItemId = listingId;
        this.mListingTypeId = listingTypeId;
        mBrowseListTitle = listingTitle;
        mDescription = body;
        mBrowseImgUrl = listingImage;
        mBrowseListOwnerTitle = listingOwnerTitle;
        mBrowseListCreationDate = creationDate;
        mLocation = location;
        mPrice = price;
        mCurrency = currency;
        mViewCount = viewCount;
        mReviewCount = reviewCount;
        mCommentCount = commentCount;
        mLikeCount = likeCount;
        this.allowToView = isAllowToView;
        mClosed = isClosed;
        this.menuArray = menuArray;
    }

    // Browse Wishlist
    public BrowseListItems(int listingId, int listingTypeId, String wishlistTitle, String listingTitle, String body,
                           String listingImage, String listingOwnerTitle, String creationDate, String location, String price,
                           String currency, boolean isAllowToView, int isClosed, JSONArray menuArray) {

        mListItemId = listingId;
        this.mListingTypeId = listingTypeId;
        this.topicTitle = wishlistTitle;
        mBrowseListTitle = listingTitle;
        mDescription = body;
        mBrowseImgUrl = listingImage;
        mBrowseListOwnerTitle = listingOwnerTitle;
        mBrowseListCreationDate = creationDate;
        mLocation = location;
        mPrice = price;
        this.mCurrency = currency;
        this.allowToView = isAllowToView;
        mClosed = isClosed;
        this.menuArray = menuArray;
    }

    // MLT ReviewProfile.

    public BrowseListItems(int reviewId, int resourceId,int store_id,int ownerId, int rating, String ownerImage,
                           String contentUrl, String title, String body, String pros, String cons,
                           String creationDate, int likeCount, int commentCount, int helpfulCount,
                           int notHelpfulCount, Boolean isHelpful, Boolean isNotHelpful, int recommend,
                           String ownerTitle, JSONArray menuArray, JSONArray updatedReviewArray) {
        mReviewId = reviewId;
        mListItemId = resourceId;
        mUserId = ownerId;
        mReviewRating = rating;
        mBrowseImgUrl = ownerImage;
        mContentUrl = contentUrl;
        mBrowseListTitle = title;
        mBrowseBody = body;
        mPros = pros;
        mCons = cons;
        mBrowseListCreationDate = creationDate;
        mLikeCount = likeCount;
        mCommentCount = commentCount;
        mHelpfulCount = helpfulCount;
        mNotHelpfulCount = notHelpfulCount;
        mIsHelpful = isHelpful;
        mIsNotHelpful = isNotHelpful;
        mRecommend = recommend;
        mBrowseListOwnerTitle = ownerTitle;
        this.menuArray = menuArray;
        this.mUpdatedReviewArray = updatedReviewArray;
        storeId = store_id;
    }

    // MLT Browse Wishlist.
    public BrowseListItems(int wishlistId, int liked, int followed, String title, String creationDate, int totalListingItem,
                           String listingImage1, String listingImage2, String listingImage3, JSONArray menuArray) {
        mListItemId = wishlistId;
        mLiked = liked;
        mFollowed = followed;
        mBrowseListTitle = title;
        mBrowseListCreationDate = creationDate;
        mTotalListingItem = totalListingItem;
        mListingImage1 = listingImage1;
        mListingImage2 = listingImage2;
        mListingImage3 = listingImage3;
        this.menuArray = menuArray;
    }

    // For Browse Advanced Events
    public BrowseListItems(String imgurl, String hostImageUrl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, boolean viewAllowed, String location, String startTime, int ownerId,
                            String hostType, int hasMultipleDates, int featured, int sponsored){
        mBrowseImgUrl = imgurl;
        mHostImageUrl = hostImageUrl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mLocation = location;
        mStartTime = startTime;
        mOwnerId = ownerId;
        mHostType = hostType;
        mHasMultipleDates = hasMultipleDates;
        mFeatured = featured;
        mSponsored = sponsored;
    }

    // For Manage Advanced Events
    /**
     * @deprecated eventStatus was added
     */
    @Deprecated
    public BrowseListItems(String imgurl, String hostImageUrl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, int closed, JSONArray menuJsonArray, String location, String startTime, int ownerId,
                           int likesCount, int viewCount, String hostType, boolean allowToView,
                           Boolean isShowOptionMenu, int hasMultipleDates, int joined){
        mBrowseImgUrl = imgurl;
        mHostImageUrl = hostImageUrl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
        mLocation = location;
        mStartTime = startTime;
        mOwnerId = ownerId;
        mLikeCount = likesCount;
        mViewCount = viewCount;
        mClosed = closed;
        mHostType = hostType;
        this.allowToView = allowToView;
        mIsShowOptionMenu = isShowOptionMenu;
        mHasMultipleDates = hasMultipleDates;
        mJoined = joined;
    }

    public BrowseListItems(String imgurl, String hostImageUrl, String listTitle, String ownerTitle, int memberCount,
                           int listItemId, int closed, JSONArray menuJsonArray, String location, String startTime, int ownerId,
                           int likesCount, int viewCount, String hostType, boolean allowToView,
                           Boolean isShowOptionMenu, int hasMultipleDates, int joined, String eventStatus){
        mBrowseImgUrl = imgurl;
        mHostImageUrl = hostImageUrl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mMemberCount = memberCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
        mLocation = location;
        mStartTime = startTime;
        mOwnerId = ownerId;
        mLikeCount = likesCount;
        mViewCount = viewCount;
        mClosed = closed;
        mHostType = hostType;
        this.allowToView = allowToView;
        mIsShowOptionMenu = isShowOptionMenu;
        mHasMultipleDates = hasMultipleDates;
        mJoined = joined;
        mEventStatus = eventStatus;
    }

    //For Browse Diaries Advanced Events
    public BrowseListItems(int diaryId, String image1, String image2, String image3, String listTitle, String body, int ownerId,
                           int eventId, int viewCount, int eventCount, boolean viewAllowed){
        mListItemId = diaryId;
        mImage1 = image1;
        mImage2 = image2;
        mImage3 = image3;
        mBrowseListTitle = listTitle;
        mBody = body;
        mOwnerId = ownerId;
        mEventId = eventId;
        mViewCount = viewCount;
        mEventCount = eventCount;
        allowToView = viewAllowed;
    }

    //Browse Categories Advanced Events and Browse Categories MLT.
     public BrowseListItems(int categoryId, String CategoryName, String CategoryImage, String order, int count)
     {
         mListItemId = categoryId;
         mBrowseListTitle = CategoryName;
         mBrowseImgUrl = CategoryImage;
         mOrder = order;
         mTotalItemCount = count;

     }

    //Category HomePage Advanced Events
    public BrowseListItems(String image_icon, String image, String title, String owner_title, int event_id,
                           String location, String starttime, int owner_id, int popularityCount, int host_id,
                           String host_type, int hasMultipleDates) {

        mBrowseImgUrl = image_icon;
        mHostImageUrl = image;
        mBrowseListTitle = title;
        mBrowseListOwnerTitle = owner_title;
        mListItemId = event_id;
        mLocation = location;
        mStartTime = starttime;
        mOwnerId = owner_id;
        mPopularityCount = popularityCount;
        mOwnerId = host_id;
        mHostType = host_type;
        mHasMultipleDates = hasMultipleDates;

    }


    //For selecting Album For editing photos
    public BrowseListItems(String albumId, String AlbumName){
        mAlbumId = albumId;
        mBrowseListTitle = AlbumName;
    }

    // Guest list Advanced Events
    public BrowseListItems(int user_id, String name, int rsvp, String guestImage,
                           Boolean isWaiting, String friendshipType, int isFriendshipVerified) {

        mUserId = user_id;
        mBrowseListTitle = name;
        mRsvp = rsvp;
        mBrowseImgUrl = guestImage;
        mIsWatingList = isWaiting;
        mFriendShipType = friendshipType;
        this.isFriendshipVerified = isFriendshipVerified;
    }

    // Waiting guest list Advanced Events
    public BrowseListItems(int user_id, String name, int rsvp, String guestImage, JSONArray menuArray,
                           Boolean isWaiting, String friendshipType, int isFriendshipVerified) {

        mUserId = user_id;
        mBrowseListTitle = name;
        mRsvp = rsvp;
        mBrowseImgUrl = guestImage;
        mMenuArray = menuArray;
        mIsWatingList = isWaiting;
        mFriendShipType = friendshipType;
        this.isFriendshipVerified = isFriendshipVerified;
    }

    // User reviews list Advanced Events
    public BrowseListItems(int reviewId, int resource_id, int userId, String hostType, String title, String body, String pros, String cons, String cDate,
                           int likeCount, int commentCount, int helpfulCount, int nothelpfulCount, Boolean is_helpful,
                           Boolean is_not_helpful, int recommend, String ownerTitle, int overallrating) {

        mReviewId = reviewId;
        mEventId = resource_id;
        mUserId = userId;
        mHostType = hostType;
        mBrowseListTitle = title;
        mBrowseBody = body;
        mPros = pros;
        mCons = cons;
        mBrowseListCreationDate = cDate;
        mLikeCount = likeCount;
        mCommentCount = commentCount;
        mHelpfulCount = helpfulCount;
        mNotHelpfulCount = nothelpfulCount;
        mRecommend = recommend;
        mBrowseListOwnerTitle = ownerTitle;
        mIsHelpful = is_helpful;
        mIsNotHelpful = is_not_helpful;
        mOverallRating = overallrating;

    }

    //Occurrence page
    public BrowseListItems (String startTime, String endTime, int occurrence_id, int totalMembers, int rsvp,
                            JSONArray menuArray, JSONArray guestArray, int event_id, int isRequestInvite, int isJoined) {

        mStartTime = startTime;
        mEndTime = endTime;
        mOccurrenceId = occurrence_id;
        mTotalItemCount = totalMembers;
        mRsvp = rsvp;
        mMenuArray = menuArray;
        mGuestArray = guestArray;
        mEventId = event_id;
        mIsRequestInvite = isRequestInvite;
        mJoined = isJoined;
    }

    // Package List
    public BrowseListItems (int packageId, JSONObject packageObject, JSONObject descriptionObject,
                            JSONArray menuArray, String title, boolean isPackageUpgrade, int isPaid) {

        mListItemId = packageId;
        mPackageObject = packageObject;
        mDescriptionObject = descriptionObject;
        this.menuArray = menuArray;
        mBrowseListTitle = title;
        mIsPackageUpgrade = isPackageUpgrade;
        mIsPaid = isPaid;
    }

    // Package View
    public BrowseListItems (String paramKey, String packageParamLabel, String packageParamValue) {
        mPackageParamLabel = packageParamLabel;
        mPackageParamValue = packageParamValue;
    }


    // FOR Browse site page, Category home page and Popular page
    public BrowseListItems(String imgurl, String listTitle, int listItemId, boolean viewAllowed,
                           int likeCount, int followCount, int isClosed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mLikeCount = likeCount;
        mFollowCount = followCount;
        mClosed = isClosed;
    }

    //For Manage site page
    public BrowseListItems(String imgurl, String listTitle, int likeCount, int followCount,
                           int listItemId, JSONArray menuJsonArray){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mLikeCount = likeCount;
        mFollowCount = followCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
    }

    // User reviews list Site Pages
    public BrowseListItems(int reviewId, int page_id, int userId, String hostType, String title, String body,
                           String pros, String cons, String cDate, int likeCount, Boolean isLiked, int commentCount, String recommend,
                           String ownerTitle, int overallrating, JSONArray gutterMenus, JSONArray ratingParams, int isShowProsCons) {

        mReviewId = reviewId;
        mListItemId = page_id;
        mUserId = userId;
        mHostType = hostType;
        mBrowseListTitle = title;
        mBrowseBody = body;
        mPros = pros;
        mCons = cons;
        mBrowseListCreationDate = cDate;
        mLikeCount = likeCount;
        mCommentCount = commentCount;
        mRecommendText = recommend;
        mBrowseListOwnerTitle = ownerTitle;
        mOverallRating = overallrating;
        mMenuArray = gutterMenus;
        mRatingParams = ratingParams;
        mIsLiked = isLiked;
        mIsShowProsCons = isShowProsCons;
    }

    // FOR Browse adv group
    public BrowseListItems(String imgurl, String listTitle, String ownerTitle, int listItemId, boolean viewAllowed,
                           int likeCount, int followCount, int featured, int sponsored, int closed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mListItemId = listItemId;
        allowToView = viewAllowed;
        mLikeCount = likeCount;
        mFollowCount = followCount;
        mFeatured = featured;
        mSponsored = sponsored;
        mClosed = closed;
    }

    //For Manage adv group
    public BrowseListItems(String imgurl, String listTitle, String ownerTitle, int likeCount, int followCount,
                           int listItemId, JSONArray menuJsonArray, int closed){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mBrowseListOwnerTitle = ownerTitle;
        mLikeCount = likeCount;
        mFollowCount = followCount;
        mListItemId = listItemId;
        menuArray = menuJsonArray;
        mClosed = closed;
    }

    // Browse Offers
    public BrowseListItems(String imgurl, String listTitle, String couponCode, int claimCount,int totalClaims,
                           int endSetting, String endTime, String startTime, int offerId, String offerDescription){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mCouponCode = couponCode;
        mEndSetting = endSetting;
        mEndTime = endTime;
        this.startTime = startTime;
        this.totalClaims = totalClaims;
        mOfferId = offerId;
        mClaimCount = claimCount;
        mOfferDescription = offerDescription;
    }

    // MLT view page.
    public BrowseListItems(int listingId, String title, String coverImage, String mContentUrl,
                           int isListingClosed, int isListingSubscribed, int isApplyJob) {

        this.mListItemId = listingId;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = coverImage;
        this.mContentUrl = mContentUrl;
        this.mClosed = isListingClosed;
        this.mSubscribed = isListingSubscribed;
        this.mIsApplyJob = isApplyJob;
    }

    // Group/event view page.
    public BrowseListItems(String title, String image, String mContentUrl, int contentId,
                           int mMembershipRequestCode, int joined, int profileRsvpValue) {
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = image;
        this.mContentUrl = mContentUrl;
        this.mListItemId = contentId;
        this.mIsRequestInvite = mMembershipRequestCode;
        this.mJoined = joined;
        this.mProfileRsvpValue = profileRsvpValue;
    }

    // Video, Poll, WishList, Adv. Event Diaries view page (For share data).
    public BrowseListItems(int contentId, String title, String imageIcon, String contentUrl) {
        this.mListItemId = contentId;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = imageIcon;
        this.mContentUrl = contentUrl;
    }

    // Forum View page.
    public BrowseListItems(int close, int sticky, int watch, String url, String title) {
        this.mClosed = close;
        this.mSticky = sticky;
        this.mWatched = watch;
        this.mContentUrl = url;
        this.mBrowseListTitle = title;
    }

    // Adv. group view page.
    public BrowseListItems(int contentId, String title, String coverImage, String contentUrl,
                           int isGroupClosed, int isGroupJoined, int mMembershipRequestCode,
                           boolean showAddPeople, boolean isGroupPublished) {
        this.mListItemId = contentId;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = coverImage;
        this.mContentUrl = contentUrl;
        this.mClosed = isGroupClosed;
        this.mJoined = isGroupJoined;
        this.mIsRequestInvite = mMembershipRequestCode;
        this.mShowAddPeople = showAddPeople;
        this.mGroupPublished = isGroupPublished;
    }

    // Adv. Event profile page.
    public BrowseListItems(int contentId, int occurrenceId, String title, String image,
                           String contentUrl, int isClosed) {
        this.mListItemId = contentId;
        this.mOccurrenceId = occurrenceId;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = image;
        this.mContentUrl = contentUrl;
        this.mClosed = isClosed;
    }

    // User Profile page.
    public BrowseListItems(String title, int userId, String displayName) {
        this.mBrowseListTitle = title;
        this.mUserId = userId;
        this.displayName = displayName;
    }

    // Album view page.
    public BrowseListItems(int contentId, String title, String imageIcon, String contentUrl, String url) {
        this.mListItemId = contentId;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = imageIcon;
        this.mContentUrl = contentUrl;
        this.mRedirectUrl = url;
    }

    // Classified, Blog view page.
    public BrowseListItems(int listingId, String title, String coverImage, String mContentUrl,
                           int isListingClosed, int isListingSubscribed, String url) {

        this.mListItemId = listingId;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = coverImage;
        this.mContentUrl = mContentUrl;
        this.mClosed = isListingClosed;
        this.mSubscribed = isListingSubscribed;
        this.mRedirectUrl = url;
    }

    // Channel view page.
    public BrowseListItems(int contentId, int isFavouriteOption, String title, String imageIcon, String contentUrl) {
        this.mListItemId = contentId;
        this.isFavouriteOption = isFavouriteOption;
        this.mBrowseListTitle = title;
        this.mBrowseImgUrl = imageIcon;
        this.mContentUrl = contentUrl;
    }

    // Member verification details
    public BrowseListItems(String userName, String userImage, String commentDescription, int userId) {
        this.displayName = userName;
        this.mListImage = userImage;
        this.mDescription = commentDescription;
        this.mUserId = userId;
    }

    public boolean isFollowingMember() {
        return isFollowingMember;
    }

    public void setFollowingMember(boolean followingMember) {
        isFollowingMember = followingMember;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getMessageOwnerUrl() {
        return messageOwnerUrl;
    }

    public void setMessageOwnerUrl(String messageOwnerUrl) {
        this.messageOwnerUrl = messageOwnerUrl;
    }

    public String getMessageOwnerTitle() {
        return messageOwnerTitle;
    }

    public void setMessageOwnerTitle(String messageOwnerTitle) {
        this.messageOwnerTitle = messageOwnerTitle;
    }

    // PhotoLight Box.
    public BrowseListItems(int subjectId) {
        this.mContentId = subjectId;
    }

    public String getmRedirectUrl() {
        return mRedirectUrl;
    }

    public void setmRedirectUrl(String mRedirectUrl) {
        this.mRedirectUrl = mRedirectUrl;
    }

    public int getmProfileRsvpValue() {
        return mProfileRsvpValue;
    }

    public void setmProfileRsvpValue(int mProfileRsvpValue) {
        this.mProfileRsvpValue = mProfileRsvpValue;
    }

    public int getmJoined() {
        return mJoined;
    }

    public void setmJoined(int mJoined) {
        this.mJoined = mJoined;
    }

    public boolean ismGroupPublished() {
        return mGroupPublished;
    }

    public void setmGroupPublished(boolean mIsGroupPublished) {
        this.mGroupPublished = mIsGroupPublished;
    }

    public boolean ismShowAddPeople() {
        return mShowAddPeople;
    }

    public void setmShowAddPeople(boolean mShowAddPeople) {
        this.mShowAddPeople = mShowAddPeople;
    }

    public int getmSticky() {
        return mSticky;
    }

    public void setmSticky(int mSticky) {
        this.mSticky = mSticky;
    }

    public int getmWatched() {
        return mWatched;
    }

    public void setmWatched(int mWatched) {
        this.mWatched = mWatched;
    }

    public int getmSubscribed() {
        return mSubscribed;
    }

    public void setmSubscribed(int mSubscribed) {
        this.mSubscribed = mSubscribed;
    }

    // Browse Stickers
    public BrowseListItems(int stickerId, String imgurl){
        mBrowseImgUrl = imgurl;
        mListItemId = stickerId;
    }

    // Sticker Store
    public BrowseListItems(int collectionId, String imgUrl, String stickerStoreTitle,
                           JSONArray menuArray, int stickersCount){
        mBrowseImgUrl = imgUrl;
        mListItemId = collectionId;
        mBrowseListTitle = stickerStoreTitle;
        this.menuArray = menuArray;
        this.mStickersCount = stickersCount;
    }

    // My Tickets Advanced Events
    public BrowseListItems(int order_id, int event_id, String event_title, String event_time, String image, JSONObject order_info) {
        mOrderId = order_id;
        mListItemId = event_id;
        mBrowseListTitle = event_title;
        mStartTime = event_time;
        mBrowseImgUrl = image;
        mOrderInfo = order_info;
    }

    // Order view page tickets listing
    public BrowseListItems(int mStatusCode, String title, int price, String quantity, String currency, int minValue, int maxValue, int ticketsId, String endDate,
                           String ticketsStatus, String colorCode, JSONObject jsonObject, int listSize) {
        this.mStatusCode = mStatusCode;
        mBrowseListTitle = title;
        mTicketsPrice = price;
        mQuantity = quantity;
        mCurrency = currency;
        mMinValue = minValue;
        mMaxValue = maxValue;
        mListItemId = ticketsId;
        mEndTime = endDate;
        mStatus = ticketsStatus;
        mObjectResponse = jsonObject;
        size = listSize;
        mStatusColor = colorCode;

    }

    // Ordered view page tickets listing
    public BrowseListItems(String title, int price, String currency, String quantity, Double subTotal) {
        mBrowseListTitle = title;
        mTicketsPrice = price;
        mQuantity = quantity;
        mSubTotal = subTotal;
        mCurrency = currency;
    }

    //Where To Buy Page
    public BrowseListItems(String listTitle, String price, String listUrl, String image,
                           int minPriceOption, String priceTagImage) {
        this.mBrowseListTitle = listTitle;
        this.mPrice = price;
        this.mListUrl = listUrl;
        this.mListImage = image;
        this.mMinPriceOption = minPriceOption;
        this.mPriceTagImage = priceTagImage;

    }

    //For Browse channel page
    public BrowseListItems(String imgurl, String listTitle, int likeCount, int subscribeCount, int viewCount,
                           int rating_count, int videosCount, int listItemId, String contentUrl, boolean viewAllowed) {

        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mLikeCount = likeCount;
        this.subscribeCount = subscribeCount;
        mViewCount = viewCount;
        mVideoRating = rating_count;
        this.videosCount = videosCount;
        mListItemId = listItemId;
        mContentUrl = contentUrl;
        allowToView = viewAllowed;
    }

    //For Manage channel page
    public BrowseListItems(String imgurl, String listTitle, int likeCount, int subscribeCount, int viewCount,
                           int rating_count, int videosCount, int listItemId, String contentUrl, JSONArray menuJsonArray) {

        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mLikeCount = likeCount;
        this.subscribeCount = subscribeCount;
        mViewCount = viewCount;
        mVideoRating = rating_count;
        this.videosCount = videosCount;
        mListItemId = listItemId;
        mContentUrl = contentUrl;
        menuArray = menuJsonArray;
    }

    //For Browse Playlist
    public BrowseListItems(int listItemId, String listTitle, String imgUrl, int videosCount) {
        mListItemId = listItemId;
        mBrowseListTitle = listTitle;
        mBrowseImgUrl = imgUrl;
        this.videosCount = videosCount;
    }

    //For Manage Playlist
    public BrowseListItems(int listItemId, String listTitle, String imgUrl, int videosCount, JSONArray menuArray) {
        mListItemId = listItemId;
        mBrowseListTitle = listTitle;
        mBrowseImgUrl = imgUrl;
        this.videosCount = videosCount;
        this.menuArray = menuArray;
    }

    public BrowseListItems(int videoId, String title, String ownerTitle, String image, int duration, String videoUrl,
                           int type, boolean allowToView, boolean canRemoveFromPlayList) {

        mListItemId = videoId;
        mBrowseListTitle = title;
        mBrowseListOwnerTitle = ownerTitle;
        mBrowseImgUrl = image;
        mDuration = duration;
        mVideoUrl = videoUrl;
        this.mVideoType = type;
        this.allowToView = allowToView;
        this.canRemoveFromPlayList = canRemoveFromPlayList;
    }

    public BrowseListItems(int playlistId, String ownerTitle, String ownerImage, int ownerId, int likeCount,
                           int viewCount) {
        mContentId = playlistId;
        mBrowseListOwnerTitle = ownerTitle;
        mListImage = ownerImage;
        mUserId = ownerId;
        mLikeCount = likeCount;
        mViewCount = viewCount;
    }
    // Store view page.
    public BrowseListItems(int storeId, String storeTitle,int isStoreClosed, String coverImage, String mStoreUrl) {

        mContentId = storeId;
        title = storeTitle;
        mBrowseImgUrl = coverImage;
        mContentUrl = mStoreUrl;
        mClosed = isStoreClosed;

    }
    // Store Manage Shipping & Manage Downloadable Activity.
    public BrowseListItems(JSONObject argItem) {
        mShippingItem = argItem;
        menuArray = argItem.optJSONArray("menu");
        mClosed = argItem.optJSONObject("status") != null ? argItem.optJSONObject("status").optInt("value",0) : 0;
        this.storyObject = argItem;
    }


    public String getmPriceTagImage() {
        return mPriceTagImage;
    }

    public String getmListImage() {
        return mListImage;
    }

    public int getmMinPriceOption() {
        return mMinPriceOption;
    }

    public String getmListUrl() {
        return mListUrl;
    }

    // People Suggestion
    public BrowseListItems(int userId, int mutualFriendCount, String userName, String userImage,
                           boolean isRequestCanceled, JSONArray userMenuArray) {
        mUserId = userId;
        mMutualFriendCount = mutualFriendCount;
        displayName = userName;
        mBrowseImgUrl = userImage;
        mIsRequestSent = isRequestCanceled;
        menuArray = userMenuArray;
    }

    // Invite contacts on app.
    public BrowseListItems(String email, String userName, boolean isRequestSent, boolean isLoading) {
        this.mEmail = email;
        this.mBrowseListTitle = userName;
        this.mIsRequestSent = isRequestSent;
        this.mIsLoading = isLoading;
    }

    // Status post items.
    public BrowseListItems(String icon, int color, String key, String title) {
        this.icon = icon;
        this.key = key;
        this.title = title;
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public int getColor() {
        return color;
    }


    public String getAlbumId() {
        return mAlbumId;
    }

    public int getmContentId() {
        return mContentId;
    }

    public String getmModuleType() {
        return mModuleType;
    }

    public String getFeedTitle() {
        return mFeedTitle;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getmNotificationUrl() {
        return mNotificationUrl;
    }

    public int getNotificationObjectId() {
        return mNotificationObjectId;
    }

    public JSONObject getNotificationObject() {
        return mNotificationObject;
    }

    public JSONObject getSubjectResponse() {
        return mSubjectResponse;
    }

    public JSONObject getObjectResponse() {
        return mObjectResponse;
    }

    public String getActionTypeBody() {
        return mActionTypeBody;
    }

    public String getNotificationType() {
        return mNotificationType;
    }

    public JSONArray getActionBodyParamsArray() {
        return mActionBodyParamsArray;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getNotificationSubjectType() {
        return mNotificationSubjectType;
    }

    public String getNotificationObjectType() {
        return mNotificationObjectType;
    }

    public int getNotificationId() {
        return mNotificationId;
    }

    //For Messages
    public String getMessageTitle() {
        return mMessageTitle;
    }

    public String getMessageBody() {
        return mMessageBody;
    }

    public int getInboxRead() {
        return mInboxRead;
    }

    public int getConversationId() {
        return mConversationId;
    }

    // For Advanced Member
    public int getmIsSiteMember() {
        return mIsSiteMember;
    }

    public int getmMutualFriendCount() {
        return mMutualFriendCount;
    }

    public int getmAge() {
        return mAge;
    }

    public int getIsMemberVerified() {
        return isMemberVerified;
    }

    public int getmIsMemberOnline() {
        return mIsMemberOnline;
    }

    //For Music
    public String getArtistImageUrl(){
        return mArtistImageUrl;
    }

    public int getTotalPlayCount(){
        return mTotalPlayCount;
    }

    public int getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(int canEdit) {
        this.canEdit = canEdit;
    }

    //For Videos
    public int getVideoRating() {
        return mVideoRating;
    }

    public String getmVideoUrl() {
        return mVideoUrl;
    }

    public int getmVideoType() {
        return mVideoType;
    }

    public int getmDuration() {
        return mDuration;
    }

    public int getmLikeCount() {
        return mLikeCount;
    }

    public void setmLikeCount(int mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    //For MLT

    public int getmListingTypeId() {
        return mListingTypeId;
    }

    public int getmReviewRating() {
        return mReviewRating;
    }

    public void setmIsHelpful(boolean mIsHelpful) {
        this.mIsHelpful = mIsHelpful;
    }

    public void setmIsNotHelpful(boolean mIsNotHelpful) {
        this.mIsNotHelpful = mIsNotHelpful;
    }

    public Boolean getmIsHelpful() {
        return mIsHelpful;
    }

    public Boolean getmIsNotHelpful() {
        return mIsNotHelpful;
    }

    public int getmTotalListingItem() {
        return mTotalListingItem;
    }

    public String getmListingImage1() {
        return mListingImage1;
    }

    public String getmListingImage2() {
        return mListingImage2;
    }

    public String getmListingImage3() {
        return mListingImage3;
    }

    public int getmLiked() {
        return mLiked;
    }

    public void setmLiked(int mLiked) {
        this.mLiked = mLiked;
    }

    public int getmFollowed() {
        return mFollowed;
    }

    public void setmFollowed(int mFollowed) {
        this.mFollowed = mFollowed;
    }

    public JSONArray getmUpdatedReviewArray() {
        return mUpdatedReviewArray;
    }

    //For others
    public boolean isAllowToView() {
        return allowToView;
    }

    public int getmTotalItemCount() {
        return mTotalItemCount;
    }

    public void setmTotalItemCount(int mTotalItemCount) {
        this.mTotalItemCount = mTotalItemCount;
    }

    public int getmReviewCount() {
        return mReviewCount;
    }

    public int getmMemberCount() {
        return mMemberCount;
    }

    public int getmListItemId(){

        return mListItemId;
    }

    public int getmPhotoCount() {
        return mPhotoCount;
    }

    public int getmCommentCount() {
        return mCommentCount;
    }

    public void setmCommentCount(int mCommentCount) {
        this.mCommentCount = mCommentCount;
    }

    public String getmBrowseImgUrl(){

        return mBrowseImgUrl;

    }

    public void setmBrowseListTitle(String mBrowseListTitle) {
        this.mBrowseListTitle = mBrowseListTitle;
    }

    public String getmBrowseListTitle(){

        return mBrowseListTitle;
    }

    public String getmBrowseViewCount() {
        return mBrowseViewCount;
    }

    public String getmBrowseVoteCount() {
        return mBrowseVoteCount;
    }

    public String getmBrowseListOwnerTitle(){

        return mBrowseListOwnerTitle;

    }

    public String getmBrowseListCreationDate(){

        return mBrowseListCreationDate;

    }

    public int getmIsPaid(){
        return mIsPaid;
    }

    public int getmClosed() {
        return mClosed;
    }

    public void setmClosed(int mClosed) {
        this.mClosed = mClosed;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public JSONArray getMenuArray() {
        return menuArray;
    }

    public void setMenuArray(JSONArray menuArray) {
        this.menuArray = menuArray;
    }

    public String getmStaff() {
        return mStaff;
    }

    public void setmStaff( String staff) {
        this.mStaff = staff;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmPrice() {
        return mPrice;
    }

    public String getmCurrency() {
        return mCurrency;
    }

    public String getmStartTime() {

        return mStartTime;
    }

    public int getmIsOwner() {
        return mIsOwner;
    }

    public int getmRsvp() {
        return mRsvp;
    }

    public void setmRsvp(int mRsvp) {
        this.mRsvp = mRsvp;
    }

    public JSONObject getmMemberMenus() {
        return mMemberMenus;
    }

    public void setmMemberMenus(JSONObject mMemberMenus) {
        this.mMemberMenus = mMemberMenus;
    }

    public boolean isForumAllowToView() {
        return forumAllowToView;
    }

    public String getSlug() {
        return slug;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getForumTitle() {
        return forumTitle;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public int getForumId() {
        return forumId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getTopicTotalItemCount() {
        return topicTotalItemCount;
    }

    public void setTopicTotalItemCount(int topicTotalItemCount) {
        this.topicTotalItemCount = topicTotalItemCount;
    }

    public int getIsTopicClose() {
        return isTopicClose;
    }

    public int getTopicId() {
        return topicId;
    }

    public int getTotalReplies() {
        return totalReplies;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTopicOwnerImage() {
        return topicOwnerImage;
    }

    public String getTopicModifiedDate() {
        return topicModifiedDate;
    }

    public String getTopicSlug() {
        return topicSlug;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public boolean isTopicAllowToView() {
        return topicAllowToView;
    }

    public String getPostByImage() {
        return postByImage;
    }

    public String getPostByName() {
        return postByName;
    }

    public String getPostCreationDate() {
        return postCreationDate;
    }

    public String getPostModifiedDate() {
        return postModifiedDate;
    }

    public int getPostEditId() {
        return postEditId;
    }

    public String getPostBody() {
        return postBody;
    }

    public int getPostId() {
        return postId;
    }

    public int getOwnerPostCount() {
        return ownerPostCount;
    }

    public JSONArray getForumMenuArray() {
        return forumMenuArray;
    }

    public int getPostTotalItemCount() {
        return postTotalItemCount;
    }

    public void setPostTotalItemCount(int postTotalItemCount) {
        this.postTotalItemCount = postTotalItemCount;
    }

    public String getPostEditByDisplayName() {
        return postEditByDisplayName;
    }

    public int getPostEditByUserId() {
        return postEditByUserId;
    }

    public int getPostedByUserId() {
        return postedByUserId;
    }

    public int getTopicLastPostByUserId() {
        return topicLastPostByUserId;
    }

    public String getmContentUrl() {
        return mContentUrl;
    }

    public int getmIsClosed() {
        return mIsClosed;
    }

    public String getmModuleName() {
        return mModuleName;
    }

    public String getmHostImageUrl() {
        return mHostImageUrl;
    }

    public String getmEventStatus() {
        return mEventStatus;
    }

    public int getmOwnerId() {
        return mOwnerId;
    }

    public int getmViewCount() {
        return mViewCount;
    }

    public String getmBrowseListName() {

        return mBrowseListName;
    }

    public int getmEventCount() {
        return mEventCount;
    }

    public String getmPros() {
        return mPros;
    }

    public String getmCons() {
        return mCons;
    }

    public String getmBrowseBody() {
        return mBrowseBody;
    }

    public int getmRecommend() {
        return mRecommend;
    }

    public int getmHelpfulCount() {
        return mHelpfulCount;
    }

    public void setmIsHelpful(Boolean mIsHelpful) {
        this.mIsHelpful = mIsHelpful;
    }

    public void setmHelpfulCount(int mHelpfulCount) {
        this.mHelpfulCount = mHelpfulCount;
    }

    public void setmNotHelpfulCount(int mNotHelpfulCount) {
        this.mNotHelpfulCount = mNotHelpfulCount;
    }

    public void setmIsNotHelpful(Boolean mIsNotHelpful) {
        this.mIsNotHelpful = mIsNotHelpful;
    }

    public int getmNotHelpfulCount() {

        return mNotHelpfulCount;
    }

    public int getmReviewId() {
        return mReviewId;
    }

    public Boolean getIsHelpful() {
        return mIsHelpful;
    }

    public Boolean getIsNotHelpful() {
        return mIsNotHelpful;
    }

    public int getmEventId() {
        return mEventId;
    }

    public void setmEventId(int mEventId) {
        this.mEventId = mEventId;
    }

    public String getmImage1() {
        return mImage1;
    }

    public String getmImage2() {
        return mImage2;
    }

    public String getmImage3() {
        return mImage3;
    }

    public Boolean getmIsWatingList() {
        return mIsWatingList;
    }

    public JSONArray getmMenuArray() {
        return mMenuArray;
    }

    public void setmMenuArray(JSONArray mMenuArray) {
        this.mMenuArray = mMenuArray;
    }

    public String getmHostType() {
        return mHostType;
    }

    public int getmOccurrenceId() {
        return mOccurrenceId;
    }

    public JSONArray getmGuestArray() {
        return mGuestArray;
    }

    public void setmGuestArray(JSONArray mGuestArray) {
        this.mGuestArray = mGuestArray;
    }

    public Boolean getmIsShowOptionMenu() {
        return mIsShowOptionMenu;
    }

    public int getmOverallRating() {
        return mOverallRating;
    }

    public void setmStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public void setmEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getTotalClaims() {
        return totalClaims;
    }

    public void setmOccurrenceId(int mOccurrenceId) {
        this.mOccurrenceId = mOccurrenceId;
    }

    public int getmHasMultipleDates() {
        return mHasMultipleDates;
    }

    public int getmFollowCount() {
        return mFollowCount;
    }

    public JSONArray getmRatingParams() {
        return mRatingParams;
    }

    public String getmRecommendText() {
        return mRecommendText;
    }

    public Boolean getmIsLiked() {
        return mIsLiked;
    }

    public void setmIsLiked(Boolean mIsLiked) {
        this.mIsLiked = mIsLiked;
    }

    public int getmPackageId() {
        return mPackageId;
    }

    public JSONObject getmDescriptionObject() {
        return mDescriptionObject;
    }

    public JSONObject getmPackageObject() {
        return mPackageObject;

    }

    public String getmPackageParamLabel() {
        return mPackageParamLabel;
    }

    public String getmPackageParamValue() {
        return mPackageParamValue;
    }

    public int getmSponsored() {
        return mSponsored;
    }

    public int getmFeatured() {
        return mFeatured;
    }

    public int getmCanAddToList() {
        return mCanAddToList;
    }

    public void setCanAddToList(int canAddToList) {
        this.mCanAddToList = canAddToList;
    }

    public int getmPublished() {
        return mPublished;
    }

    public void setmPublished(int mPublished) {
        this.mPublished = mPublished;
    }

    public int getmClaimCount() {
        return mClaimCount;
    }

    public int getmEndSetting() {
        return mEndSetting;
    }

    public int getmOfferId() {
        return mOfferId;
    }

    public String getmCouponCode() {
        return mCouponCode;
    }

    public String getmEndTime() {
        return mEndTime;
    }

    public String getmOfferDescription() {
        return mOfferDescription;
    }

    public String getmFriendShipType() {
        return mFriendShipType;
    }

    public void setmFriendShipType(String mFriendShipType) {
        this.mFriendShipType = mFriendShipType;
    }

    public int getmIsRequestInvite() {
        return mIsRequestInvite;
    }

    public void setmIsRequestInvite(int mIsRequestInvite) {
        this.mIsRequestInvite = mIsRequestInvite;
    }

    public int getmIsGroupAdmin() {
        return mIsGroupAdmin;
    }

    public void setmIsGroupAdmin(int mIsGroupAdmin) {
        this.mIsGroupAdmin = mIsGroupAdmin;
    }

    public int getmStickersCount() {
        return mStickersCount;
    }

    public JSONObject getmOrderInfo() {
        return mOrderInfo;
    }

    public int getmOrderId() {
        return mOrderId;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public int getmMinValue() {
        return mMinValue;
    }

    public int getmMaxValue() {
        return mMaxValue;
    }

    public String getmStatus() {
        return mStatus;
    }

    public int getmTicketsPrice() {
        return mTicketsPrice;
    }

    public Double getmSubTotal() {
        return mSubTotal;
    }

    public boolean getmIsRequestSent() {
        return mIsRequestSent;
    }

    public void setmIsRequestSent(boolean mIsRequestSent) {
        this.mIsRequestSent = mIsRequestSent;
    }

    public String getmEmail() {
        return mEmail;
    }

    public boolean getmIsLoading() {
        return mIsLoading;
    }

    public void setmIsLoading(boolean mIsLoading) {
        this.mIsLoading = mIsLoading;
    }

    public JSONObject getSliderObject() {
        return sliderObject;
    }

    public int getStoreId() {
        return storeId;
    }


    public String getmStatusColor() {
        return mStatusColor;
    }

    public JSONObject getResponseObject() {
        return responseObject;
    }

    public int getVideosCount() {
        return videosCount;
    }

    public int getSubscribeCount() {
        return subscribeCount;
    }

    public int getIsFavouriteOption() {
        return isFavouriteOption;
    }

    public void setIsFavouriteOption(int isFavored) {
        this.isFavouriteOption = isFavored;
    }

    public boolean canRemoveFromPlayList() {
        return canRemoveFromPlayList;
    }

    public int getIsFriendshipVerified() {
        return isFriendshipVerified;
    }

    public JSONObject getmReceiverObject() {
        return mReceiverObject;
    }

    public boolean getmIsPackageUpgrade() {
        return mIsPackageUpgrade;
    }

    public String getTicketTitle(){
        return mticket_title;
    }
    public void setTicketTitle(String TicketTitle){
        this.mticket_title = TicketTitle;
    }
    public String getTicketPrice(){
        return mticket_price;
    }
    public void setTicketsPrice(String TicketPrice){
        this.mticket_price = TicketPrice;
    }
    public String getTicketQuantity(){
        return mticket_quantity;
    }
    public void setTicketsQuantity(String TicketQuantity){
        this.mticket_quantity = TicketQuantity;
    }
    public int getTicketId(){
        return mticket_id;
    }
    public void setTicketsQuantity(int TicketId) {
        this.mticket_id = TicketId;
    }

    public JSONObject getmShippingItem() {
        return mShippingItem;
    }
    public boolean isLessTextShowing() {
        return isLessTextShowing;
    }

    public void setLessTextShowing(boolean isLessTextShowing) {
        this.isLessTextShowing = isLessTextShowing;
    }

    public void setIsAdminApprovalRequired(boolean isAdminApprovalRequired) {
        this.isAdminApprovalRequired = isAdminApprovalRequired;
    }

    public boolean isAdminApprovalRequired() {
        return isAdminApprovalRequired;
    }

    public void setIsAdminApproved(boolean adminApprove) {
        this.isAdminApproved = isAdminApproved;
    }

    public boolean isAdminApproved() {
        return isAdminApproved;
    }

    public int getIsApplyJob() {
        return mIsApplyJob;
    }

    public void setRequestProcessing(boolean isProcessing){
        this.isRequestProcessing = isProcessing;
    }
    public boolean isRequestProcessing(){
        return isRequestProcessing;
    }

    public int getmIsShowProsCons() {
        return mIsShowProsCons;
    }

    public int getmStatusCode() {
        return mStatusCode;
    }

    public void setmStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }
}
