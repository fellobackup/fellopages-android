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

package com.fellopages.mobileapp.classes.common.adapters;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ads.admob.AdMobViewHolder;
import com.fellopages.mobileapp.classes.common.ads.FacebookAdViewHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.CommunityAdsHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.RemoveAdHolder;
import com.fellopages.mobileapp.classes.common.fragments.VideoLightBoxFragment;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class BrowseDataAdapter extends ArrayAdapter<Object> {

    private static final int TYPE_ITEM = 0;
    public static final int TYPE_FB_AD = 1;
    public static final int TYPE_ADMOB = 2;
    public static final int TYPE_COMMUNITY_ADS = 3;
    private static final int REMOVE_COMMUNITY_ADS = 4;
    private static final int TYPE_MAX_COUNT = REMOVE_COMMUNITY_ADS + 1;

    private Context mContext;
    private List<Object> mBrowseItemList;
    private int mLayoutResID;
    private BrowseListItems listItems;
    private View mRootView;
    private String currentSelectedOption,convertedDate;
    private AppConstant mAppConst;
    private Typeface fontIcon;
    private int placementCount, adType;
    private ArrayList<Integer> mRemoveAds;
    private ImageLoader mImageLoader;

    public BrowseDataAdapter(Context context, int layoutResourceID,List<Object> listItem) {

        super(context, layoutResourceID,listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        mAppConst = new AppConstant(context);
        //Fetch Current Selected Module
        currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mRemoveAds = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        if(mBrowseItemList.get(position) instanceof BrowseListItems){
            return TYPE_ITEM;
        } else if(mRemoveAds.size() != 0 && mRemoveAds.contains(position)){
            return REMOVE_COMMUNITY_ADS;
        } else {
            switch (currentSelectedOption){
                case "sitepage":
                    placementCount = ConstantVariables.SITE_PAGE_ADS_POSITION;
                    adType = ConstantVariables.SITE_PAGE_ADS_TYPE;
                    break;
                case "core_main_sitegroup":
                    placementCount = ConstantVariables.ADV_GROUPS_ADS_POSITION;
                    adType = ConstantVariables.ADV_GROUPS_ADS_TYPE;
                    break;
                case "core_main_group":
                    placementCount = ConstantVariables.GROUPS_ADS_POSITION;
                    adType = ConstantVariables.GROUPS_ADS_TYPE;
                    break;
                case "core_main_event":
                    placementCount = ConstantVariables.EVENT_ADS_POSITION;
                    adType = ConstantVariables.EVENT_ADS_TYPE;
                    break;
                case "core_main_blog":
                    placementCount = ConstantVariables.BLOG_ADS_POSITION;
                    adType = ConstantVariables.BLOG_ADS_TYPE;
                    break;
                case "core_main_poll":
                    placementCount = ConstantVariables.POLL_ADS_POSITION;
                    adType = ConstantVariables.POLL_ADS_TYPE;
                    break;
                case "core_main_video":
                    placementCount = ConstantVariables.VIDEO_ADS_POSITION;
                    adType = ConstantVariables.VIDEO_ADS_TYPE;
                    break;
                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    placementCount = ConstantVariables.ADV_VIDEO_ADS_POSITION;
                    adType = ConstantVariables.ADV_VIDEO_ADS_TYPE;
                    break;
            }

            switch (adType){
                case 0:
                    return TYPE_FB_AD;
                case 1:
                    return TYPE_ADMOB;
                default:
                    return TYPE_COMMUNITY_ADS;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mBrowseItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBrowseItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        mRootView = convertView;
        ListItemHolder listItemHolder = null;
        FacebookAdViewHolder facebookAdViewHolder = null;
        AdMobViewHolder adMobViewHolder = null;
        CommunityAdsHolder communityAdsHolder = null;
        RemoveAdHolder removeAdHolder = null;
        int type = getItemViewType(position);

        if(mRootView == null){

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            switch (type) {
                case TYPE_ITEM:
                    mRootView = inflater.inflate(mLayoutResID, parent, false);

                    if (currentSelectedOption != null) {
                        if (currentSelectedOption.equals("core_main_video")
                                || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                                || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)) {

                            mRootView.findViewById(R.id.video_view).setVisibility(View.VISIBLE);
                            listItemHolder.mContentTitle = mRootView.findViewById(R.id.videoTitle);
                            listItemHolder.mListImage = mRootView.findViewById(R.id.video_thumbnail);

                            listItemHolder.mCommentCount = mRootView.
                                    findViewById(R.id.videoCommentCount);
                            listItemHolder.mLikeCount = mRootView.
                                    findViewById(R.id.videoLikeCount);
                            listItemHolder.mDuration = mRootView.
                                    findViewById(R.id.video_duration);
                            listItemHolder.mUpdatedDate = mRootView.findViewById(R.id.video_createdDate);
                            listItemHolder.mLikeIcon = mRootView.findViewById(R.id.likeIcon);
                            listItemHolder.mCommentIcon = mRootView.findViewById(R.id.commentIcon);
                            listItemHolder.mPlayIcon = mRootView.findViewById(R.id.play_button);
                            listItemHolder.mRatingIcon = mRootView.findViewById(R.id.ratingIcon);
                            listItemHolder.mRatingCount = mRootView.findViewById(R.id.ratingCount);

                        } else {

                            switch (currentSelectedOption) {

                                case "core_main_group":
                                    listItemHolder.memberInfo = mRootView.findViewById(R.id.memberInfo);
                                    listItemHolder.memberInfo.setTypeface(fontIcon);
                                    listItemHolder.ownerInfo = mRootView.findViewById(R.id.ownerInfo);
                                    listItemHolder.ownerInfo.setTypeface(fontIcon);
                                    break;
                                case "core_main_event":
                                    listItemHolder.mEventLocation = mRootView.findViewById(R.id.eventLocationInfo);
                                    listItemHolder.mEventTime = mRootView.findViewById(R.id.eventTime);
                                    listItemHolder.mDateIcon = mRootView.findViewById(R.id.date_icon);
                                    listItemHolder.mLocationIcon = mRootView.findViewById(R.id.location_icon);
                                    listItemHolder.mLocationLayout = mRootView.findViewById(R.id.location_layout);
                                    listItemHolder.mDay = mRootView.findViewById(R.id.day);
                                    listItemHolder.mMonth = mRootView.findViewById(R.id.month);
                                    listItemHolder.mLocationIcon.setTypeface(fontIcon);
                                    listItemHolder.mDateIcon.setTypeface(fontIcon);
                                    break;
                                case "core_main_poll":
                                    listItemHolder.mViewCountDetail = mRootView.findViewById(R.id.viewCountDetail);
                                    listItemHolder.mContentDetail = mRootView.
                                            findViewById(R.id.contentDetail);
                                    listItemHolder.mListImageClosed = mRootView.findViewById(R.id.closeIcon);
                                    break;
                                case "sitepage":
                                    listItemHolder.mFollowCount = mRootView.findViewById(R.id.memberInfo);
                                    listItemHolder.mLikeCount = mRootView.findViewById(R.id.ownerInfo);
                                    listItemHolder.mListImageClosed = mRootView.findViewById(R.id.closeIcon);
                                    break;

                                case "core_main_sitegroup":
                                    listItemHolder.ownerInfo = mRootView.findViewById(R.id.ownerInfo);
                                    listItemHolder.mListImageClosed = mRootView.findViewById(R.id.closeIcon);
                                    listItemHolder.mFollowCount = mRootView.findViewById(R.id.memberInfo);
                                    listItemHolder.mLikeCount = mRootView.findViewById(R.id.likeCountInfo);
                                    listItemHolder.mLikeCount.setVisibility(View.VISIBLE);
                                    listItemHolder.mFeatured = mRootView.findViewById(R.id.featuredLabel);
                                    listItemHolder.mSponsored = mRootView.findViewById(R.id.sponsoredLabel);
                                    break;

                                case "core_main_offers":
                                    listItemHolder.mClaimCountDetail = mRootView.findViewById(R.id.claimCount);
                                    listItemHolder.mEndDate = mRootView.findViewById(R.id.contentDetail);
                                    listItemHolder.mStartDate = mRootView.findViewById(R.id.coupon_start_date);
                                    listItemHolder.mCouponCode = mRootView.findViewById(R.id.couponCode);
                                    break;

                                default:
                                    listItemHolder.mContentDetail = mRootView.
                                            findViewById(R.id.contentDetail);
                            }
                            listItemHolder.mContentTitle = mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mListImage = mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mOptionIcon = mRootView.findViewById(R.id.optionIcon);

                        }
                    }
                    mRootView.setTag(listItemHolder);
                    break;
                case TYPE_FB_AD:
                    switch (currentSelectedOption) {
                        case "sitepage":
                        case "core_main_sitegroup":
                        case "core_main_group":
                        case "core_main_event":
                            mRootView = inflater.inflate(R.layout.feeds_ad_item_card, parent, false);
                            break;
                        case "core_main_blog":
                        case "core_main_poll":
                            mRootView = inflater.inflate(R.layout.fb_ad_item_list, parent, false);
                            break;
                        case "core_main_video":
                        case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.fb_ad_item_card, parent, false);
                            break;

                    }
                    facebookAdViewHolder = new FacebookAdViewHolder(mRootView);
                    mRootView.setTag(facebookAdViewHolder);
                    break;

                case TYPE_COMMUNITY_ADS:
                    switch (currentSelectedOption) {
                        case "sitepage":
                        case "core_main_sitegroup":
                        case "core_main_group":
                        case "core_main_event":
                            mRootView = inflater.inflate(R.layout.fb_content_ad, parent, false);
                            break;
                        case "core_main_blog":
                        case "core_main_poll":
                            mRootView = inflater.inflate(R.layout.fb_ad_item_list, parent, false);
                            break;
                        case "core_main_video":
                        case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.fb_ad_item_card, parent, false);
                            break;

                    }
                    communityAdsHolder = new CommunityAdsHolder(this, mRootView, placementCount, adType, mRemoveAds);
                    mRootView.setTag(communityAdsHolder);
                    break;

                case TYPE_ADMOB:
                    switch (currentSelectedOption) {
                        case "sitepage":
                        case "core_main_group":
                        case "core_main_event":
                            mRootView = inflater.inflate(R.layout.admob_ad_install, parent, false);
                            break;
                        case "core_main_blog":
                        case "core_main_poll":
                            mRootView = inflater.inflate(R.layout.admob_install_list, parent, false);
                            break;
                        case "core_main_video":
                        case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.admob_install_card, parent, false);
                            break;
                    }
                    adMobViewHolder = new AdMobViewHolder(mRootView);
                    mRootView.setTag(adMobViewHolder);
                    break;

                case REMOVE_COMMUNITY_ADS:
                    mRootView = inflater.inflate(R.layout.remove_ads_layout, parent, false);
                    removeAdHolder =  new RemoveAdHolder(this, mRootView, mRemoveAds, mBrowseItemList);
                    mRootView.setTag(removeAdHolder);
                    break;
            }
        }else {
            switch (type) {
                case TYPE_ITEM:
                    listItemHolder = (ListItemHolder) mRootView.getTag();
                    break;

                case TYPE_FB_AD:
                    facebookAdViewHolder = (FacebookAdViewHolder) mRootView.getTag();
                    break;

                case TYPE_COMMUNITY_ADS:
                    communityAdsHolder = (CommunityAdsHolder) mRootView.getTag();
                    break;

                case TYPE_ADMOB:
                    adMobViewHolder = (AdMobViewHolder) mRootView.getTag();
                    break;

                case REMOVE_COMMUNITY_ADS:
                    removeAdHolder = (RemoveAdHolder) mRootView.getTag();
                    break;
            }
        }

        switch (type) {
            case TYPE_ITEM:

                listItems = (BrowseListItems)this.mBrowseItemList.get(position);
                /*
                Set Data in the List View Items
                 */
                if (currentSelectedOption.contains("video")) {
                    mImageLoader.setVideoImage(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);
                } else {
                    mImageLoader.setImageForUserProfile(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);
                }
                if ( listItems.getmBrowseListTitle() != null && !listItems.getmBrowseListTitle().isEmpty()) {
                    listItemHolder.mContentTitle.setVisibility(View.VISIBLE);
                    listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());
                } else {
                    listItemHolder.mContentTitle.setVisibility(View.GONE);
                }


                switch (currentSelectedOption) {

                    case "core_main_global_search":
                    case "home":
                        if(listItems.getmDescription() != null && !listItems.getmDescription().isEmpty()){
                            listItemHolder.mContentDetail.setMaxLines(2);
                            listItemHolder.mContentDetail.setText(Html.fromHtml(listItems.getmDescription()));
                        }

                        break;

                    case "core_main_group":

                        String membersText = mContext.getResources().getQuantityString(R.plurals.member_text,
                                listItems.getmMemberCount());
                        listItemHolder.memberInfo.setText(String.format("\uf007 " +
                                        mContext.getResources().getString(R.string.group_member_count_text),
                                listItems.getmMemberCount(), membersText
                        ));

                        listItemHolder.ownerInfo.setText("\uf19d " + mContext.getResources().getString
                                (R.string.led_by_text) + " " +
                                listItems.getmBrowseListOwnerTitle() );
                        break;

                    case "core_main_video":
                    case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:

                        listItemHolder.mLikeCount.setText(String.valueOf(listItems.getmLikeCount()));
                        listItemHolder.mLikeIcon.setTypeface(fontIcon);
                        listItemHolder.mLikeIcon.setText("\uf164");
                        listItemHolder.mCommentIcon.setTypeface(fontIcon);
                        listItemHolder.mRatingIcon.setTypeface(fontIcon);
                        listItemHolder.mRatingIcon.setText("\uf005");
                        listItemHolder.mRatingCount.setText(String.valueOf(listItems.getVideoRating()));
                        if (currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                                || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)) {
                            listItemHolder.mCommentCount.setText(String.valueOf(listItems.getmViewCount()));
                            listItemHolder.mCommentIcon.setText("\uf06e");
                        } else {
                            listItemHolder.mCommentCount.setText(String.valueOf(listItems.getmCommentCount()));
                            listItemHolder.mCommentIcon.setText("\uf075");
                        }

                        if (currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)) {
                            listItemHolder.mDuration.setText(mContext.getResources().
                                    getQuantityString(R.plurals.video_counts, listItems.getVideosCount(),
                                            listItems.getVideosCount()));
                            String detail = String.format(mContext.getResources().getString(R.string.channel_subscribe_like_count),
                                    listItems.getSubscribeCount(),
                                    mContext.getResources().getQuantityString(R.plurals.total_subscriber, listItems.getSubscribeCount()),
                                    listItems.getmLikeCount(),
                                    mContext.getResources().getQuantityString(R.plurals.total_like, listItems.getmLikeCount()));
                            listItemHolder.mUpdatedDate.setText(Html.fromHtml(detail));
                        } else {
                            listItemHolder.mDuration.setText(mAppConst.calculateDifference(listItems.getmDuration()));
                            listItemHolder.mUpdatedDate.setText(AppConstant.convertDateFormat(mContext.getResources(),
                                    listItems.getmBrowseListCreationDate()));
                        }

                        if (currentSelectedOption != null) {
                            switch (currentSelectedOption) {
                                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                                case ConstantVariables.SITE_VIDEO_CHANNEL_MENU_TITLE:
                                    listItemHolder.mPlayIcon.setVisibility(View.GONE);
                                    break;
                                default:
                                    listItemHolder.mPlayIcon.setTag(position);
                                    listItemHolder.mPlayIcon.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            int itemPosition = Integer.parseInt(view.getTag().toString());
                                            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(itemPosition);
                                            Intent lightBox = new Intent(mContext, VideoLightBoxFragment.class);
                                            Bundle args = new Bundle();
                                            args.putString(ConstantVariables.VIDEO_URL, listItems.getmVideoUrl());
                                            args.putInt(ConstantVariables.VIDEO_TYPE, listItems.getmVideoType());
                                            lightBox.putExtras(args);
                                            mContext.startActivity(lightBox);
                                            ((Activity) mContext).overridePendingTransition(R.anim.slide_up_in, R.anim.push_down_out);
                                        }
                                    });
                            }
                        }
                        break;


                    case "core_main_event":
                        String StartDate = listItems.getmStartTime();
                        String day = AppConstant.getDayFromDate(StartDate);
                        String month = AppConstant.getMonthFromDate(StartDate, "MMM");
                        listItemHolder.mDay.setText(day);
                        listItemHolder.mMonth.setText(month);
                        String dateFormat = AppConstant.getMonthFromDate(StartDate, "MMM") +  " " + AppConstant.getDayFromDate(StartDate) +
                                ", "  + AppConstant.getYearFormat(StartDate) ;
                        String timeFormat = AppConstant.getHoursFromDate(StartDate);
                        listItemHolder.mDateIcon.setText("\uf017");
                        String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
                        String dateDetail = String.format(createTextFormat, dateFormat,
                                mContext.getResources().getString(R.string.event_date_info), timeFormat);
                        listItemHolder.mEventTime.setText(dateDetail);

                        if (listItems.getmLocation() != null && !listItems.getmLocation().isEmpty()){
                            listItemHolder.mLocationLayout.setVisibility(View.VISIBLE);
                            listItemHolder.mLocationIcon.setText("\uf041 ");
                            listItemHolder.mEventLocation.setText(listItems.getmLocation());
                        } else {
                            listItemHolder.mLocationLayout.setVisibility(View.GONE);
                        }
                        break;

                    case "core_main_poll":
                        //setting closed poll icon in list
                        listItemHolder.mClosed = listItems.getmClosed();
                        if (listItemHolder.mClosed == 1) {
                            listItemHolder.mListImageClosed.setVisibility(View.VISIBLE);
                            listItemHolder.mListImageClosed.setTypeface(fontIcon);
                            listItemHolder.mListImageClosed.setText("\uf023");
                        }
                        else {
                            listItemHolder.mListImageClosed.setVisibility(View.GONE);
                        }

                        convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                                listItems.getmBrowseListCreationDate());
                        String creatorTextFormat1 = mContext.getResources().getString(R.string.creator_view_with_date_format);
                        String detail1 = String.format(creatorTextFormat1,
                                mContext.getResources().getString(R.string.album_owner_salutation),
                                listItems.getmBrowseListOwnerTitle(), convertedDate);
                        listItemHolder.mContentDetail.setText(Html.fromHtml(detail1));

                        listItemHolder.mViewCountDetail.setTypeface(fontIcon);
                        listItemHolder.mViewCountDetail.setText(
                                String.format("%s  \uF080    %s  \uF06E", listItems.getmBrowseVoteCount(),
                                        listItems.getmBrowseViewCount()));

                        listItemHolder.mViewCountDetail.setVisibility(View.VISIBLE);

                        break;

                    case "sitepage":
                        listItemHolder.mFollowCount.setText(mContext.getResources().getQuantityString(R.plurals.page_followers,
                                listItems.getmFollowCount(), listItems.getmFollowCount()));

                        listItemHolder.mLikeCount.setText( listItems.getmLikeCount() + " " +
                                mContext.getResources().getQuantityString(R.plurals.profile_page_like,
                                listItems.getmLikeCount()));

                        listItemHolder.mClosed = listItems.getmClosed();
                        if (listItemHolder.mClosed == 1) {
                            listItemHolder.mListImageClosed.setVisibility(View.VISIBLE);
                            listItemHolder.mListImageClosed.setTypeface(fontIcon);
                            listItemHolder.mListImageClosed.setText("\uF023");
                        } else {
                            listItemHolder.mListImageClosed.setVisibility(View.GONE);
                        }

                        break;

                    case "core_main_sitegroup":

                        listItemHolder.ownerInfo.setText(Html.fromHtml(mContext.getResources().getString
                                (R.string.created_by_text) + " " + listItems.getmBrowseListOwnerTitle()));
                        listItemHolder.mLikeCount.setText(listItems.getmLikeCount() + " " + mContext.getResources()
                                    .getQuantityString(R.plurals.profile_page_like, listItems.getmLikeCount()));
                        listItemHolder.mFollowCount.setText(mContext.getResources().getQuantityString(R.plurals.page_followers,
                                listItems.getmFollowCount(), listItems.getmFollowCount()));

                        listItemHolder.mClosed = listItems.getmClosed();
                        if (listItemHolder.mClosed == 1) {
                            listItemHolder.mListImageClosed.setVisibility(View.VISIBLE);
                            listItemHolder.mListImageClosed.setTypeface(fontIcon);
                            listItemHolder.mListImageClosed.setText("\uF023");
                        } else {
                            listItemHolder.mListImageClosed.setVisibility(View.GONE);
                        }

                        if(listItems.getmFeatured() == 1){
                            listItemHolder.mFeatured.setVisibility(View.VISIBLE);
                        }else{
                            listItemHolder.mFeatured.setVisibility(View.GONE);
                        }

                        if(listItems.getmSponsored() == 1){
                            listItemHolder.mSponsored.setVisibility(View.VISIBLE);
                        }else{
                            listItemHolder.mSponsored.setVisibility(View.GONE);
                        }
                        break;

                    case "core_main_offers":
                        listItemHolder.mEndDate.setVisibility(View.VISIBLE);
                        listItemHolder.mClaimCountDetail.setVisibility(View.VISIBLE);
                        listItemHolder.mCouponCode.setVisibility(View.VISIBLE);
                        listItemHolder.mStartDate.setVisibility(View.VISIBLE);

                        if(listItems.getmEndSetting() == 0){
                            listItemHolder.mEndDate.setText(mContext.getResources().
                                    getString(R.string.end_date_text) + ": " + mContext.getResources().
                                    getString(R.string.offer_never_expires));
                        }else{
                             convertedDate = AppConstant.getMonthFromDate(listItems.getmEndTime(), "MMM") +
                                     " " + AppConstant.getDayFromDate(listItems.getmEndTime()) +
                                    ", "  + AppConstant.getYearFormat(listItems.getmEndTime()) ;
                            listItemHolder.mEndDate.setText(mContext.getResources().
                                    getString(R.string.end_date_text) + ": " + convertedDate);
                        }

                        listItemHolder.mStartDate.setText(mContext.getResources().
                                getString(R.string.start_date_label) +": "+
                                AppConstant.getMonthFromDate(listItems.getStartTime(), "MMM") +
                                " " + AppConstant.getDayFromDate(listItems.getStartTime()) +
                                ", "  + AppConstant.getYearFormat(listItems.getStartTime()) );

                        if(listItems.getTotalClaims() == -1){
                            listItemHolder.mClaimCountDetail.setText(mContext.getResources().
                                    getString(R.string.claim_count_text, listItems.getmClaimCount()));
                        }else {
                            listItemHolder.mClaimCountDetail.setText(mContext.getResources().
                                    getString(R.string.claim_count_text, listItems.getmClaimCount()) + " - "
                                    + mContext.getResources().getQuantityString(R.plurals.coupon_left,
                                    listItems.getTotalClaims() - listItems.getmClaimCount(),
                                    listItems.getTotalClaims() - listItems.getmClaimCount()));
                        }

                        listItemHolder.mCouponCode.setText(mContext.getResources().
                                getString(R.string.coupon_code_text) + ": " + listItems.getmCouponCode());
                        break;

                    default:
                        convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                                listItems.getmBrowseListCreationDate());
                        String creatorTextFormat = mContext.getResources().getString(R.string.creator_view_with_date_format);
                        String detail = String.format(creatorTextFormat,
                                mContext.getResources().getString(R.string.album_owner_salutation),
                                listItems.getmBrowseListOwnerTitle(), convertedDate);
                        listItemHolder.mContentDetail.setText(Html.fromHtml(detail));

                }

                if(!currentSelectedOption.equals("core_main_video")
                        && !currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                        && !currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)
                        && !currentSelectedOption.equals("core_main_offers")){
                    listItemHolder.mOptionIcon.setVisibility(View.GONE);
                }

                mRootView.setId(listItems.getmListItemId());
                break;

            case TYPE_FB_AD:
                if (facebookAdViewHolder.mNativeAd != null) {
                    facebookAdViewHolder.mNativeAd.unregisterView();
                }
                facebookAdViewHolder.mNativeAd = (NativeAd) mBrowseItemList.get(position);

                if (currentSelectedOption.equals("core_main_video")
                        || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                        || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)) {
                    FacebookAdViewHolder.inflateAd(facebookAdViewHolder.mNativeAd,
                            facebookAdViewHolder.adView, mContext, true);
                } else {
                    FacebookAdViewHolder.inflateAd(facebookAdViewHolder.mNativeAd,
                            facebookAdViewHolder.adView, mContext, false);
                }
                break;

            case TYPE_ADMOB:
                AdMobViewHolder.inflateAd(mContext,
                        (NativeAppInstallAd) mBrowseItemList.get(position),adMobViewHolder.mAdView);
                break;

            case TYPE_COMMUNITY_ADS:
                communityAdsHolder.mCommunityAd = (CommunityAdsList) mBrowseItemList.get(position);
                CommunityAdsHolder.inflateAd(communityAdsHolder.mCommunityAd,
                            communityAdsHolder.adView, mContext, position);
                break;

            case REMOVE_COMMUNITY_ADS:

                // Show Hidden Type Feed
                removeAdHolder.mCommunityAd = (CommunityAdsList) mBrowseItemList.get(position);
                removeAdHolder.removeAd(removeAdHolder.mCommunityAd, removeAdHolder.adView, mContext, position);
                break;
        }

        return mRootView;
    }

    private static class ListItemHolder{

        TextView mContentTitle, mContentDetail, mDuration, mUpdatedDate, mViewCountDetail, mCouponCode;
        TextView mLikeCount,mCommentCount,ownerInfo, memberInfo,mRatingCount, mFollowCount;
        TextView mLikeIcon, mDateIcon, mLocationIcon,mDay,mMonth;
        TextView mCommentIcon, mListImageClosed;
        ImageView mPlayIcon;
        TextView mRatingIcon;
        LinearLayout mLocationLayout;
        TextView mStartDate, mEndDate, mClaimCountDetail;
        int mClosed;
        ImageView  mOptionIcon, mListImage;
        TextView mEventLocation, mEventTime;
        TextView mFeatured, mSponsored;

    }


}
