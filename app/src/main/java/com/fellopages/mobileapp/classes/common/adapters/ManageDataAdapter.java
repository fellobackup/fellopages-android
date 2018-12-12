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
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class ManageDataAdapter extends ArrayAdapter<Object> implements OnMenuClickResponseListener {

    private static final int TYPE_ITEM = 0;
    public static final int TYPE_FB_AD = 1;
    public static final int TYPE_ADMOB = 2;
    public static final int TYPE_COMMUNITY_ADS = 3;
    private static final int REMOVE_COMMUNITY_ADS = 4;
    private static final int TYPE_MAX_COUNT = REMOVE_COMMUNITY_ADS + 1;

    private Context mContext;
    private List<Object> mBrowseItemList;
    private int mLayoutResID, mPosition;
    private BrowseListItems listItems;
    private View mRootView;
    private String currentSelectedOption, convertedDate;
    private AppConstant mAppConst;
    private BrowseListItems mBrowseList;
    private Typeface fontIcon;
    private LayoutInflater inflater;
    private Fragment mFragment;
    private GutterMenuUtils mGutterMenuUtils;
    private ArrayList<Integer> mRemoveAds;
    private int placementCount, adType;
    private ImageLoader mImageLoader;
    public ManageDataAdapter(Context context, int layoutResourceID, List<Object> listItem,
                             String list_name, Fragment fragment) {
        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        mAppConst = new AppConstant(context);
        mGutterMenuUtils = new GutterMenuUtils(context);

        mBrowseList = new BrowseListItems();

        //Check Current list name, if not availabel then fetch current selected module

        if (list_name != null && !list_name.isEmpty()) {
            currentSelectedOption = list_name;
        } else {
            currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        this.mFragment = fragment;
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

    public View getView(int position, View convertView, ViewGroup parent) {

        mRootView = convertView;
        final ListItemHolder listItemHolder;
        FacebookAdViewHolder facebookAdViewHolder = null;
        AdMobViewHolder adMobViewHolder = null;
        CommunityAdsHolder communityAdsHolder = null;
        RemoveAdHolder removeAdHolder = null;
        int type = getItemViewType(position);
        if (mRootView == null) {

            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);

            switch (type) {
                case TYPE_ITEM:
                    mRootView = inflater.inflate(mLayoutResID, parent, false);

                    if (currentSelectedOption.equals(ConstantVariables.VIDEO_MENU_TITLE)
                            || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                            || currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)) {
                        mRootView.findViewById(R.id.video_view).setVisibility(View.VISIBLE);

                        listItemHolder.mListImageIcon = (ImageView) mRootView.findViewById(R.id.video_thumbnail);
                        listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.videoTitle);
                        listItemHolder.mUpdatedDate = (TextView) mRootView.findViewById(R.id.video_createdDate);
                        listItemHolder.mCommentCount = (TextView) mRootView.
                                findViewById(R.id.videoCommentCount);
                        listItemHolder.mLikeCount = (TextView) mRootView.
                                findViewById(R.id.videoLikeCount);
                        listItemHolder.mDuration = (TextView) mRootView.
                                findViewById(R.id.video_duration);
                        listItemHolder.mLikeIcon = (TextView) mRootView.findViewById(R.id.likeIcon);
                        listItemHolder.mCommentIcon = (TextView) mRootView.findViewById(R.id.commentIcon);
                        listItemHolder.mPlayIcon = (ImageView) mRootView.findViewById(R.id.play_button);
                        listItemHolder.mRatingIcon = (TextView) mRootView.findViewById(R.id.ratingIcon);
                        listItemHolder.mRatingCount = (TextView) mRootView.findViewById(R.id.ratingCount);
                        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.option_icon_layout_video);
                        layout.setVisibility(View.VISIBLE);
                        listItemHolder.mOptionIcon = (ImageView) layout.findViewById(R.id.optionIcon);
                        listItemHolder.mOptionIcon.setTag(position);
                    } else {
                        switch (currentSelectedOption) {


                            case "core_main_group":

                                listItemHolder.memberInfo = (TextView) mRootView.findViewById(R.id.memberInfo);
                                listItemHolder.ownerInfo = (TextView) mRootView.findViewById(R.id.ownerInfo);
                                listItemHolder.memberInfo.setTypeface(fontIcon);
                                listItemHolder.ownerInfo.setTypeface(fontIcon);
                                listItemHolder.mOptionIcon = (ImageView) mRootView.findViewById(R.id.optionIcon);

                                listItemHolder.mOptionIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_item_overflow));
                                listItemHolder.mOptionIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.white),
                                        PorterDuff.Mode.SRC_IN);
                                mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                                listItemHolder.mOptionIcon.setTag(position);
                                break;

                            case "core_main_event":

                                listItemHolder.mEventLocation = (TextView) mRootView.findViewById(R.id.eventLocationInfo);
                                listItemHolder.mEventTime = (TextView) mRootView.findViewById(R.id.eventTime);
                                listItemHolder.mDateIcon = (TextView) mRootView.findViewById(R.id.date_icon);
                                listItemHolder.mLocationIcon = (TextView) mRootView.findViewById(R.id.location_icon);
                                listItemHolder.mDay = (TextView) mRootView.findViewById(R.id.day);
                                listItemHolder.mMonth = (TextView) mRootView.findViewById(R.id.month);
                                listItemHolder.mLocationLayout = (LinearLayout) mRootView.findViewById(R.id.location_layout);
                                listItemHolder.mLocationIcon.setTypeface(fontIcon);
                                listItemHolder.mDateIcon.setTypeface(fontIcon);
                                listItemHolder.mOptionIcon = (ImageView) mRootView.findViewById(R.id.optionIcon);
                                mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                                listItemHolder.mOptionIcon.setTag(position);
                                break;

                            case "core_main_poll":
                                listItemHolder.mViewCountDetail = (TextView) mRootView.findViewById(R.id.viewCountDetail);
                                listItemHolder.mContentDetail = (TextView) mRootView.
                                        findViewById(R.id.contentDetail);
                                listItemHolder.mListImageClosed = (TextView) mRootView.findViewById(R.id.closeIcon);
                                listItemHolder.mOptionIcon = (ImageView) mRootView.findViewById(R.id.optionIcon);
                                mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                                listItemHolder.mOptionIcon.setTag(position);
                                break;

                            case "sitepage":
                                listItemHolder.mFollowCount = (TextView) mRootView.findViewById(R.id.memberInfo);
                                listItemHolder.mLikeCount = (TextView) mRootView.findViewById(R.id.ownerInfo);
                                listItemHolder.mOptionIconHoriz = (TextView) mRootView.findViewById(R.id.optionIconHoriz);
                                listItemHolder.mOptionIconHoriz.setVisibility(View.VISIBLE);
                                mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.GONE);
                                listItemHolder.mOptionIconHoriz.setTag(position);
                                break;

                            case "core_main_sitegroup":
                                listItemHolder.mFollowCount = (TextView) mRootView.findViewById(R.id.memberInfo);
                                listItemHolder.ownerInfo = (TextView) mRootView.findViewById(R.id.ownerInfo);
                                listItemHolder.mLikeCount = (TextView) mRootView.findViewById(R.id.likeCountInfo);
                                listItemHolder.mLikeCount.setVisibility(View.VISIBLE);
                                listItemHolder.mOptionIconHoriz = (TextView) mRootView.findViewById(R.id.optionIconHoriz);
                                listItemHolder.mOptionIconHoriz.setVisibility(View.VISIBLE);
                                listItemHolder.mOptionIconHoriz.setTag(position);
                                mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.GONE);
                                break;

                            default:
                                listItemHolder.mContentDetail = (TextView) mRootView.findViewById(R.id.contentDetail);
                                listItemHolder.mOptionIcon = (ImageView) mRootView.findViewById(R.id.optionIcon);
                                mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                                listItemHolder.mOptionIcon.setTag(position);

                        }

                        listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.contentTitle);
                        listItemHolder.mListImageIcon = (ImageView) mRootView.findViewById(R.id.contentImage);
                        listItemHolder.mContentInfoBlock = (LinearLayout) mRootView.findViewById(R.id.contentInfoBlock);

                    }
                    mRootView.setTag(listItemHolder);
                    break;
                case TYPE_FB_AD:
                    switch (currentSelectedOption) {
                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        case ConstantVariables.EVENT_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        case ConstantVariables.GROUP_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.feeds_ad_item_card, parent, false);
                            break;
                        case ConstantVariables.BLOG_MENU_TITLE:
                        case ConstantVariables.POLL_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.fb_ad_item_list, parent, false);
                            break;
                        case ConstantVariables.VIDEO_MENU_TITLE:
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
                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        case ConstantVariables.EVENT_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        case ConstantVariables.GROUP_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.fb_content_ad, parent, false);
                            break;
                        case ConstantVariables.BLOG_MENU_TITLE:
                        case ConstantVariables.POLL_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.fb_ad_item_list, parent, false);
                            break;
                        case ConstantVariables.VIDEO_MENU_TITLE:
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
                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        case ConstantVariables.EVENT_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        case ConstantVariables.GROUP_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.admob_ad_install, parent, false);
                            break;
                        case ConstantVariables.BLOG_MENU_TITLE:
                        case ConstantVariables.POLL_MENU_TITLE:
                            mRootView = inflater.inflate(R.layout.admob_install_list, parent, false);
                            break;
                        case ConstantVariables.VIDEO_MENU_TITLE:
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
        } else {

            switch (type) {
                case TYPE_ITEM:
                    listItemHolder = (ListItemHolder) mRootView.getTag();
                    switch (currentSelectedOption) {
                        case "sitepage":
                        case "core_main_sitegroup":
                            listItemHolder.mOptionIconHoriz.setTag(position);
                            break;
                        default:
                            listItemHolder.mOptionIcon.setTag(position);
                            break;

                    }
                    break;

                case TYPE_FB_AD:
                    listItemHolder = null;
                    facebookAdViewHolder = (FacebookAdViewHolder) mRootView.getTag();
                    break;

                case TYPE_COMMUNITY_ADS:
                    listItemHolder = null;
                    communityAdsHolder = (CommunityAdsHolder) mRootView.getTag();
                    break;

                case TYPE_ADMOB:
                    listItemHolder = null;
                    adMobViewHolder = (AdMobViewHolder) mRootView.getTag();
                    break;

                case REMOVE_COMMUNITY_ADS:
                    listItemHolder = null;
                    removeAdHolder = (RemoveAdHolder) mRootView.getTag();
                    break;

                default:
                    listItemHolder = null;
            }

        }

        switch (type) {
            case TYPE_ITEM:

                listItems = (BrowseListItems) mBrowseItemList.get(position);

                /*
                Set Data in the List View Items
                 */
                listItemHolder.mListItemId = listItems.getmListItemId();
                if (listItems.getmBrowseImgUrl() != null && !listItems.getmBrowseImgUrl().isEmpty()) {
                    listItemHolder.mListImageIcon.setVisibility(View.VISIBLE);
                    if (currentSelectedOption.contains("video")) {
                        mImageLoader.setVideoImage(listItems.getmBrowseImgUrl(), listItemHolder.mListImageIcon);
                    } else {
                        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImageIcon);
                    }
                } else {
                    listItemHolder.mListImageIcon.setVisibility(View.GONE);
                }
                listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());
                listItemHolder.mOptionsArray = listItems.getMenuArray();

                mGutterMenuUtils.setOnMenuClickResponseListener(this);
                switch (currentSelectedOption) {

                    case "core_main_group":

                        String membersText = mContext.getResources().getQuantityString(R.plurals.member_text,
                                listItems.getmMemberCount());
                        listItemHolder.memberInfo.setText(String.format("\uf007 " +
                                        mContext.getResources().getString(R.string.group_member_count_text),
                                listItems.getmMemberCount(), membersText
                        ));

                        listItemHolder.ownerInfo.setText(Html.fromHtml("\uf19d " + mContext.getResources().getString
                                (R.string.led_by_text) + " " +
                                listItems.getmBrowseListOwnerTitle()));
                        break;

                    case ConstantVariables.VIDEO_MENU_TITLE:
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
                            String subscribeText = mContext.getResources().getQuantityString(R.plurals.total_subscriber,
                                    listItems.getSubscribeCount());
                            String likeText = mContext.getResources().getQuantityString(R.plurals.total_like,
                                    listItems.getmLikeCount());
                            String creatorTextFormat = mContext.getResources().getString(R.string.channel_subscribe_like_count);
                            String detail = String.format(creatorTextFormat, listItems.getSubscribeCount(),
                                    subscribeText, listItems.getmLikeCount(), likeText);
                            listItemHolder.mUpdatedDate.setText(Html.fromHtml(detail));
                        } else {
                            listItemHolder.mDuration.setText(mAppConst.calculateDifference(listItems.getmDuration()));
                            listItemHolder.mUpdatedDate.setText(AppConstant.convertDateFormat(mContext.getResources(),
                                    listItems.getmBrowseListCreationDate()));
                        }
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
                                ((Activity) mContext).startActivity(lightBox);
                                ((Activity) mContext).overridePendingTransition(R.anim.slide_up_in, R.anim.push_down_out);
                            }
                        });
                        break;

                    case "core_main_event":
                        String StartDate = listItems.getmStartTime();
                        String day = AppConstant.getDayFromDate(StartDate);
                        String month = AppConstant.getMonthFromDate(StartDate, "MMM");
                        listItemHolder.mDay.setText(day);
                        listItemHolder.mMonth.setText(month);
                        String dateFormat = AppConstant.getMonthFromDate(StartDate, "MMM") + " " + AppConstant.getDayFromDate(StartDate) +
                                ", " + AppConstant.getYearFormat(StartDate);
                        String timeFormat = AppConstant.getHoursFromDate(StartDate);
                        listItemHolder.mDateIcon.setText("\uf017");
                        String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
                        String dateDetail = String.format(createTextFormat, dateFormat,
                                mContext.getResources().getString(R.string.event_date_info), timeFormat);
                        listItemHolder.mEventTime.setText(dateDetail);

                        if (listItems.getmLocation() != null && !listItems.getmLocation().isEmpty()) {
                            listItemHolder.mLocationLayout.setVisibility(View.VISIBLE);
                            listItemHolder.mLocationIcon.setText("\uf041 ");
                            listItemHolder.mEventLocation.setText(listItems.getmLocation());
                        } else {
                            listItemHolder.mLocationLayout.setVisibility(View.GONE);
                        }
                        break;

                    case "core_main_poll":

                        // Set Closed Poll icon on poll list
                        if (listItems.getmClosed() == 1) {
                            listItemHolder.mListImageClosed.setVisibility(View.VISIBLE);
                            listItemHolder.mListImageClosed.setTypeface(fontIcon);
                            listItemHolder.mListImageClosed.setText("\uf023");
                        } else {
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
                        listItemHolder.mViewCountDetail.setText(listItems.getmBrowseVoteCount() + "  " + "\uF080" + "    " +
                                listItems.getmBrowseViewCount() + "  " + "\uf06e");

                        listItemHolder.mViewCountDetail.setVisibility(View.VISIBLE);

                    case "core_main_siteevent":
                    case "sitereview_listing":
                        if (listItems.getmDescriptionObject() != null) {
                            listItemHolder.mContentDetail.setMaxLines(1);
                            listItemHolder.mContentDetail.setText(listItems.getmDescriptionObject().optString("value"));
                        }

                        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
                        listItemHolder.mContentInfoBlock.setPadding(0, padding, 0, padding);
                        break;

                    case "sitepage":
                        listItemHolder.mFollowCount.setText(mContext.getResources().getQuantityString(R.plurals.page_followers,
                                listItems.getmFollowCount(), listItems.getmFollowCount()));
                        listItemHolder.mLikeCount.setText(listItems.getmLikeCount() + " " + mContext.getResources().getQuantityString(R.plurals.profile_page_like,
                                listItems.getmLikeCount()));
                        listItemHolder.mOptionIconHoriz.setTypeface(fontIcon);
                        listItemHolder.mOptionIconHoriz.setText("\uf141");
                        listItemHolder.mOptionIconHoriz.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                mPosition = (int) view.getTag();
                                mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                        mPosition, mBrowseItemList, currentSelectedOption, mFragment);
                            }
                        });
                        break;

                    case "core_main_sitegroup":
                        listItemHolder.ownerInfo.setText(Html.fromHtml(mContext.getResources().getString
                                (R.string.led_by_text) + " " +
                                listItems.getmBrowseListOwnerTitle()));
                        listItemHolder.mLikeCount.setText(listItems.getmLikeCount() + " " + mContext.getResources()
                                .getQuantityString(R.plurals.profile_page_like, listItems.getmLikeCount()));
                        listItemHolder.mFollowCount.setText(mContext.getResources().getQuantityString(R.plurals.page_followers,
                                listItems.getmFollowCount(), listItems.getmFollowCount()));
                        listItemHolder.mOptionIconHoriz.setTypeface(fontIcon);
                        listItemHolder.mOptionIconHoriz.setText("\uf141");
                        listItemHolder.mOptionIconHoriz.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                mPosition = (int) view.getTag();
                                mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                        mPosition, mBrowseItemList, currentSelectedOption, mFragment);
                            }
                        });
                        break;

                    default:

                        convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                                listItems.getmBrowseListCreationDate());
                        listItemHolder.mContentDetail.setText(convertedDate);
                }

                mRootView.setId(listItems.getmListItemId());

                if (!currentSelectedOption.equals("sitepage")
                        && !currentSelectedOption.equals("core_main_sitegroup")
                        && listItems.getMenuArray() != null && listItems.getMenuArray().length() > 0) {
                    listItemHolder.mOptionIcon.setVisibility(View.VISIBLE);
                    listItemHolder.mOptionIcon.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            mPosition = (int) view.getTag();
                            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(mPosition);
                            mGutterMenuUtils.showPopup(view, listItems.getMenuArray(),
                                    mPosition, mBrowseItemList, currentSelectedOption, mFragment);
                        }
                    });
                } else if (listItemHolder.mOptionIcon != null) {
                    listItemHolder.mOptionIcon.setVisibility(View.GONE);
                }

                break;

            case TYPE_FB_AD:
                if (facebookAdViewHolder.mNativeAd != null) {
                    facebookAdViewHolder.mNativeAd.unregisterView();
                }
                facebookAdViewHolder.mNativeAd = (NativeAd) mBrowseItemList.get(position);

                if (currentSelectedOption.equals(ConstantVariables.VIDEO_MENU_TITLE)
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
                        (NativeAppInstallAd) mBrowseItemList.get(position), adMobViewHolder.mAdView);
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


    @Override
    public void onItemDelete(int position) {
        mBrowseItemList.remove(position);
        mBrowseList.setmTotalItemCount(mBrowseList.getmTotalItemCount() - 1);
        notifyDataSetChanged();
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {

        switch (menuName) {
            case "open_poll":
            case "close_poll":
            case "close":
            case "open":
            case "publish":
                mBrowseItemList.set(position, itemList);
                notifyDataSetChanged();
                break;

            default:
                mBrowseItemList.remove(position);
                mBrowseList.setmTotalItemCount(mBrowseList.getmTotalItemCount() - 1);
                notifyDataSetChanged();
                break;
        }
    }

    private static class ListItemHolder {
        ImageView mListImageIcon;
        TextView mContentTitle, mContentDetail, mRatingCount, mViewCountDetail, mDay, mMonth, mOptionIconHoriz;
        TextView mLikeCount, mCommentCount, ownerInfo, memberInfo, mDuration, mUpdatedDate, mFollowCount;
        TextView mDateIcon, mLocationIcon, mLikeIcon, mCommentIcon, mRatingIcon, mListImageClosed;
        ImageView mOptionIcon, mPlayIcon;
        TextView mEventLocation, mEventTime;
        JSONArray mOptionsArray;
        LinearLayout mLocationLayout, mContentInfoBlock;
        int mListItemId;

    }

}
