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
 *
 */

package com.fellopages.mobileapp.classes.common.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ads.admob.AdMobViewHolder;
import com.fellopages.mobileapp.classes.common.ads.FacebookAdViewHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.CommunityAdsHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.RemoveAdHolder;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CircularImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;

import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsProfilePage;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.likeNComment.Likes;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvModulesManageDataAdapter extends ArrayAdapter<Object> implements OnMenuClickResponseListener {

    private Context mContext;
    private List<Object> mBrowseItemList;
    private int mLayoutResID, mPosition, mListingTypeId;
    private BrowseListItems listItems;
    private GutterMenuUtils mGutterMenuUtils;
    private View mRootView;
    private String currentSelectedOption, mCurrentSelectedList = null;
    private AppConstant mAppConst;
    private Typeface fontIcon;
    private LayoutInflater inflater;
    private Map<String, String> postParams = new HashMap<>();
    private CharSequence[] items = new CharSequence[3];
    LinearLayout ratingLayoutGrid;
    ArrayAdapter spinnerAdapter;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FB_AD = 1;
    private static final int TYPE_ADMOB = 2;
    private static final int TYPE_COMMUNITY_AD = 3;
    private static final int REMOVE_COMMUNITY_ADS = 4;
    private static final int TYPE_MAX_COUNT = REMOVE_COMMUNITY_ADS + 1;
    private Fragment mCallingFragment;
    private String mSubjectType, mUrlText;
    private OnItemDeleteResponseListener mOnItemDeleteListener;
    private ArrayList<Integer> mRemoveAds;
    private ImageLoader mImageLoader;


    public AdvModulesManageDataAdapter(Context context, int layoutResourceID, List<Object> listItem,
                                       String listName, OnItemDeleteResponseListener onItemDeleteListener) {

        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        this.mCurrentSelectedList = listName;
        this.mOnItemDeleteListener = onItemDeleteListener;

        mAppConst = new AppConstant(context);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);

        mRemoveAds = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);
    }

    public AdvModulesManageDataAdapter(Context context, int layoutResourceID, List<Object> listItem,
                                       String listName, Fragment callingFragment,
                                       OnItemDeleteResponseListener onItemDeleteListener) {

        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        this.mCurrentSelectedList = listName;
        this.mCallingFragment = callingFragment;
        this.mOnItemDeleteListener = onItemDeleteListener;

        mAppConst = new AppConstant(context);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);

        mRemoveAds = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);
    }

    public AdvModulesManageDataAdapter(Context context, int layoutResourceID, List<Object> listItem,
                                       String listName, Fragment callingFragment) {
        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        mAppConst = new AppConstant(context);
        this.mCurrentSelectedList = listName;

        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);

        mCallingFragment = callingFragment;
        mGutterMenuUtils = new GutterMenuUtils(mContext);

        mRemoveAds = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);
    }

    public AdvModulesManageDataAdapter(Context context, int layoutResourceID, List<Object> listItem,
                                       String listName, String currentSelectedOption, int listingTypeId) {
        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        mAppConst = new AppConstant(context);
        this.mCurrentSelectedList = listName;
        this.mListingTypeId = listingTypeId;

        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);

        mGutterMenuUtils = new GutterMenuUtils(mContext);
        this.currentSelectedOption = currentSelectedOption;

        mRemoveAds = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        if(mBrowseItemList.get(position) instanceof BrowseListItems){
            return TYPE_ITEM;
        } else if(mRemoveAds.size() != 0 && mRemoveAds.contains(position)){
            return REMOVE_COMMUNITY_ADS;
        } else{
            switch (ConstantVariables.ADV_EVENT_ADS_TYPE){
                case 0:
                    return TYPE_FB_AD;
                case 1:
                    return TYPE_ADMOB;
                default:
                    return TYPE_COMMUNITY_AD;
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

    public View getView(final int position, View convertView, ViewGroup parent) {

        //Fetch Current Selected Module
        if (mCurrentSelectedList == null) {
            mCurrentSelectedList = PreferencesUtils.getCurrentSelectedList(mContext);
        }

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

            switch (type) {
                case TYPE_ITEM:
                    mRootView = inflater.inflate(mLayoutResID, parent, false);
                    switch (mCurrentSelectedList) {
                        case "members_siteevent":
                            listItemHolder.mListImage = (ImageView) mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mRsvp = (TextView) mRootView.findViewById(R.id.contentDetail);
                            listItemHolder.mOptionIconImage = (ImageView) mRootView.findViewById(R.id.optionIcon);
                            listItemHolder.mOptionIconImage.setTag(position);
                            listItemHolder.mMemberOption = (TextView) mRootView.findViewById(R.id.memberOption);
                            listItemHolder.mMemberOption.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            listItemHolder.mMemberOption.setTag(position);
                            break;

                        case "occurrence_siteevent":
                            mRootView.findViewById(R.id.contentImage).setVisibility(View.GONE);
                            listItemHolder.mDateInfo = (LinearLayout) mRootView.findViewById(R.id.dateInfo);
                            listItemHolder.mDateInfo.setVisibility(View.VISIBLE);
                            listItemHolder.mDay = (TextView) mRootView.findViewById(R.id.day);
                            listItemHolder.mMonth = (TextView) mRootView.findViewById(R.id.month);
                            listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.contentTitle);
                            mRootView.findViewById(R.id.contentDetail).setVisibility(View.GONE);
                            listItemHolder.spinner = (Spinner) mRootView.findViewById(R.id.rsvpFilter);
                            listItemHolder.rsvpFilterLayout = (LinearLayout) mRootView.findViewById(R.id.rsvpFilterLayout);
                            listItemHolder.mOptionIconImage = (ImageView) mRootView.findViewById(R.id.optionIcon);
                            listItemHolder.mOptionIconImage.setTag(position);
                            listItemHolder.mContentTitle.setTag(position);
                            mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                            break;

                        case "manage_siteevent":
                            listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mListImageIcon = (ImageView) mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mHostImage = (CircularImageView) mRootView.findViewById(R.id.host_image);
                            listItemHolder.mEventLocation = (TextView) mRootView.findViewById(R.id.eventLocationInfo);
                            listItemHolder.mEventTime = (TextView) mRootView.findViewById(R.id.eventTime);
                            listItemHolder.mDateIcon = (TextView) mRootView.findViewById(R.id.date_icon);
                            listItemHolder.mLocationIcon = (TextView) mRootView.findViewById(R.id.location_icon);
                            listItemHolder.mDay = (TextView) mRootView.findViewById(R.id.day);
                            listItemHolder.mMonth = (TextView) mRootView.findViewById(R.id.month);
                            listItemHolder.mLocationLayout = (LinearLayout) mRootView.findViewById(R.id.location_layout);
                            listItemHolder.mLocationIcon.setTypeface(fontIcon);
                            listItemHolder.mDateIcon.setTypeface(fontIcon);

                            listItemHolder.mViewCountIcon = (TextView) mRootView.findViewById(R.id.view_count_icon);
                            listItemHolder.mViewCountText = (TextView) mRootView.findViewById(R.id.view_count);
                            listItemHolder.mMemberIcon = (TextView) mRootView.findViewById(R.id.review_count_icon);
                            listItemHolder.mMemberText = (TextView) mRootView.findViewById(R.id.review_count);
                            listItemHolder.mLikeIcon = (TextView) mRootView.findViewById(R.id.like_count_icon);
                            listItemHolder.mLike = (TextView) mRootView.findViewById(R.id.like_count);
                            mRootView.findViewById(R.id.comment_count).setVisibility(View.GONE);
                            mRootView.findViewById(R.id.comment_count_icon).setVisibility(View.GONE);

                            listItemHolder.mLikeIcon.setTypeface(fontIcon);
                            listItemHolder.mMemberIcon.setTypeface(fontIcon);
                            listItemHolder.mViewCountIcon.setTypeface(fontIcon);

                            listItemHolder.mOptionIcon = (TextView) mRootView.findViewById(R.id.optionsIcon);
                            listItemHolder.mOptionIcon.setTag(position);
                            break;

                        case "sitepage_review":
                        case "sitegroup_review":
                            mSubjectType = mCurrentSelectedList.equals("sitepage_review") ? "sitepagereview_review" :
                                    "sitegroupreview_review";
                            mUrlText = mCurrentSelectedList.equals("sitepage_review") ? "sitepage" : "advancedgroups";
                            listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.content_title);
                            listItemHolder.mRatingBar = (RatingBar) mRootView.findViewById(R.id.smallRatingBar);
                            ratingLayoutGrid = (LinearLayout) mRootView.findViewById(R.id.linearLayoutRating);
                            listItemHolder.mReviewDate = (TextView) mRootView.findViewById(R.id.date_view);
                            listItemHolder.mUserName = (TextView) mRootView.findViewById(R.id.user_name);
                            listItemHolder.mProsLabel = mRootView.findViewById(R.id.prosLabel);
                            listItemHolder.mConsLabel = mRootView.findViewById(R.id.consLabel);
                            listItemHolder.mPros = (TextView) mRootView.findViewById(R.id.pros_text);
                            listItemHolder.mCons = (TextView) mRootView.findViewById(R.id.cons_text);
                            listItemHolder.mRecomended = (TextView) mRootView.findViewById(R.id.member_recommendation_text);
                            listItemHolder.mSummary = (TextView) mRootView.findViewById(R.id.summary_text);
                            listItemHolder.mShow = (TextView) mRootView.findViewById(R.id.show);
                            listItemHolder.mHide = (TextView) mRootView.findViewById(R.id.hide);
                            listItemHolder.mLike = (TextView) mRootView.findViewById(R.id.like);
                            listItemHolder.mComments = (TextView) mRootView.findViewById(R.id.comment);
                            listItemHolder.mLikeIcon = (TextView) mRootView.findViewById(R.id.likeIcon);
                            listItemHolder.mOptionIconImage = (ImageView) mRootView.findViewById(R.id.optionIcon);
                            listItemHolder.mOptionIconImage.setTag(position);
                            break;

                        case "site_package":
                            listItemHolder.mContentTitle = (TextView) mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mContentDetails = (TextView) mRootView.findViewById(R.id.contentDetail);
                            listItemHolder.mOptionIconImage = (ImageView) mRootView.findViewById(R.id.optionIcon);
                            mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                            listItemHolder.mContentInfoBlock = (LinearLayout) mRootView.findViewById(R.id.contentInfoBlock);
                            listItemHolder.mOptionIconImage.setTag(position);
                            mRootView.findViewById(R.id.contentImage).setVisibility(View.GONE);
                            break;
                    }

                    mRootView.setTag(listItemHolder);
                    break;

                case TYPE_FB_AD:
                    mRootView = inflater.inflate(R.layout.feeds_ad_item_card, parent, false);
                    facebookAdViewHolder = new FacebookAdViewHolder(mRootView);
                    mRootView.setTag(facebookAdViewHolder);
                    break;

                case TYPE_ADMOB:
                    mRootView = inflater.inflate(R.layout.admob_ad_install, parent, false);
                    adMobViewHolder = new AdMobViewHolder(mRootView);
                    mRootView.setTag(adMobViewHolder);
                    break;

                case TYPE_COMMUNITY_AD:
                    mRootView = inflater.inflate(R.layout.fb_content_ad, parent, false);
                    communityAdsHolder = new CommunityAdsHolder(this, mRootView,
                            ConstantVariables.ADV_EVENT_ADS_POSITION, ConstantVariables.ADV_EVENT_ADS_TYPE, mRemoveAds);
                    mRootView.setTag(communityAdsHolder);
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
                    switch (mCurrentSelectedList) {
                        case "manage_siteevent":
                            listItemHolder.mOptionIcon.setTag(position);
                            break;

                        case "members_siteevent":
                            listItemHolder.mMemberOption.setTag(position);
                            listItemHolder.mOptionIconImage.setTag(position);
                            break;

                        default:
                            listItemHolder.mOptionIconImage.setTag(position);
                            if (listItemHolder.mContentTitle != null) {
                                listItemHolder.mContentTitle.setTag(position);
                            }
                            break;
                    }

                    break;
                case TYPE_FB_AD:
                    facebookAdViewHolder = (FacebookAdViewHolder) mRootView.getTag();
                    listItemHolder = null;
                    break;

                case TYPE_ADMOB:
                    adMobViewHolder = (AdMobViewHolder) mRootView.getTag();
                    listItemHolder = null;
                    break;

                case TYPE_COMMUNITY_AD:
                    communityAdsHolder = (CommunityAdsHolder) mRootView.getTag();
                    listItemHolder = null;
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

                items[0] = AdvEventsProfilePage.sAttending = mContext.getResources().getString(R.string.rsvp_filter_attending);
                items[1] = AdvEventsProfilePage.sMayBeAttending = mContext.getResources().getString(R.string.rsvp_filter_may_be_attending);
                items[2] = AdvEventsProfilePage.sNotAttending = mContext.getResources().getString(R.string.rsvp_filter_not_attending);
                mGutterMenuUtils.setOnMenuClickResponseListener(this);

                /*
                Set Data in the List View Items
                 */

                if (listItemHolder.mOptionIcon != null) {
                    listItemHolder.mOptionIcon.setTypeface(fontIcon);
                    listItemHolder.mOptionIcon.setText("\uf141");
                }

                switch (mCurrentSelectedList) {

                    case "members_siteevent":
                        mImageLoader.setImageForUserProfile(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);

                        String rsvp;
                        if (listItems.getmRsvp() == 2) {
                            rsvp = mContext.getResources().getString(R.string.rsvp_filter_attending);
                        } else if (listItems.getmRsvp() == 1) {
                            rsvp = mContext.getResources().getString(R.string.rsvp_filter_may_be_attending);
                        } else {
                            rsvp = mContext.getResources().getString(R.string.rsvp_filter_not_attending);
                        }
                        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());
                        listItemHolder.mRsvp.setText(rsvp);
                        listItemHolder.mRsvp.setVisibility(View.GONE);
                        listItemHolder.mOptionsArray = listItems.getmMenuArray();

                        if (listItemHolder.mOptionsArray != null) {

                            mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                            listItemHolder.mListItemId = listItems.getmUserId();
                            listItemHolder.mOptionIconImage.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    mPosition = (int) view.getTag();
                                    mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                            mPosition, mBrowseItemList, currentSelectedOption,
                                            mCallingFragment, mCurrentSelectedList);
                                }
                            });
                        } else {
                            mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.GONE);
                        }

                        // Showing friendship options if condition matches.
                        if (listItems.getmFriendShipType() != null &&
                                !listItems.getmFriendShipType().isEmpty() &&
                                listItems.getmMenuArray() == null && listItems.getmUserId() != 0) {

                            listItemHolder.mMemberOption.setVisibility(View.VISIBLE);

                            switch (listItems.getmFriendShipType()) {
                                case "add_friend":
                                case "accept_request":
                                case "member_follow":
                                    listItemHolder.mMemberOption.setText("\uf234");
                                    break;
                                case "remove_friend":
                                case "member_unfollow":
                                    listItemHolder.mMemberOption.setText("\uf235");
                                    break;
                                case "cancel_request":
                                case "cancel_follow":
                                    listItemHolder.mMemberOption.setText("\uf00d");
                                    break;
                            }

                            listItemHolder.mMemberOption.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int position = (int) view.getTag();
                                    BrowseListItems browseListItems = (BrowseListItems) mBrowseItemList.get(position);
                                    mGutterMenuUtils.setPopUpForFriendShipType(position, browseListItems,
                                            null, currentSelectedOption);
                                }
                            });
                        } else {
                            listItemHolder.mMemberOption.setVisibility(View.GONE);
                        }

                        break;

                    case "occurrence_siteevent":
                        String occurrenceDate = listItems.getmStartTime();
                        String occurrenceDay = AppConstant.getDayFromDate(occurrenceDate);
                        String occurrenceMonth = AppConstant.getMonthFromDate(occurrenceDate, "MMM");
                        listItemHolder.mDay.setText(occurrenceDay);
                        listItemHolder.mMonth.setText(occurrenceMonth);
                        listItemHolder.mOptionsArray = listItems.getmMenuArray();
                        listItemHolder.mGuestArray = listItems.getmGuestArray();
                        listItemHolder.mListItemId = listItems.getmOccurrenceId();
                        listItemHolder.mEventId = listItems.getmEventId();
                        listItemHolder.mRsvpValue = listItems.getmRsvp();

                        if (listItems.getmTotalItemCount() == 0) {
                            listItemHolder.mContentTitle.setSingleLine(false);
                            listItemHolder.mContentTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_text_color));
                            listItemHolder.mContentTitle.setText(mContext.getResources().getString(R.string.no_member_join_this_occurence_text));
                            listItemHolder.rsvpFilterLayout.setVisibility(View.GONE);
                        } else {
                            listItemHolder.rsvpFilterLayout.setVisibility(View.VISIBLE);
                            listItemHolder.mContentTitle.setSingleLine(true);
                            String guestText = mContext.getResources().getQuantityString(R.plurals.guest_text,
                                    listItems.getmTotalItemCount(), listItems.getmTotalItemCount());
                            listItemHolder.mContentTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                            listItemHolder.mContentTitle.setText(listItems.getmTotalItemCount() + " " + guestText);

                            listItemHolder.mContentTitle.setTag(position);
                            listItemHolder.mContentTitle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int clickedPosition = (int) v.getTag();
                                    BrowseListItems browseListItems = (BrowseListItems) mBrowseItemList.get(clickedPosition);
                                    if (browseListItems.getmGuestArray() != null
                                            && browseListItems.getmGuestArray().length() > 0) {
                                        Intent intent = new Intent(mContext, Likes.class);
                                        intent.putExtra(ConstantVariables.MENU_ARRAY, browseListItems.getmGuestArray().toString());
                                        mContext.startActivity(intent);
                                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                }
                            });

                            spinnerAdapter = new ArrayAdapter<>(mContext, R.layout.simple_text_view);
                            spinnerAdapter.add(mContext.getResources().getString(R.string.rsvp_filter_not_attending));
                            spinnerAdapter.add(mContext.getResources().getString(R.string.rsvp_filter_may_be_attending));
                            spinnerAdapter.add(mContext.getResources().getString(R.string.rsvp_filter_attending));

                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            listItemHolder.spinner.setAdapter(spinnerAdapter);

                            if (listItemHolder.mRsvpValue == 3) {
                                listItemHolder.rsvpFilterLayout.setVisibility(View.GONE);
                            } else {
                                listItemHolder.rsvpFilterLayout.setVisibility(View.VISIBLE);
                                listItemHolder.spinner.setSelection(listItemHolder.mRsvpValue);
                            }

                             /* Set item selected listener on Rsvp change dropdown */

                            listItemHolder.spinner.setTag(position);

                            listItemHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (listItemHolder.mRsvpValue != position) {
                                        int rsvpChangePosition = (int) parent.getTag();

                                        int rsvp = 0;
                                        switch (position) {
                                            case 0:
                                                rsvp = 0;
                                                break;
                                            case 1:
                                                rsvp = 1;
                                                break;
                                            case 2:
                                                rsvp = 2;
                                                break;
                                        }
                                        sendRequestToServer(rsvp, listItemHolder.mListItemId, listItemHolder.mEventId,
                                                rsvpChangePosition);
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        }

                         /* Set popup menu on more option icon */

                        listItemHolder.mOptionIconImage.setTag(position);

                        if (listItemHolder.mOptionsArray != null && listItemHolder.mOptionsArray.length() > 0) {

                            listItemHolder.mOptionIconImage.setVisibility(View.VISIBLE);
                            listItemHolder.mOptionIconImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    mPosition = (int) view.getTag();
                                    mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                            mPosition, mBrowseItemList, currentSelectedOption,
                                            mCallingFragment, mCurrentSelectedList);

                                }
                            });
                        } else {
                            listItemHolder.mOptionIconImage.setVisibility(View.GONE);
                        }

                        break;

                    case "manage_siteevent":
                        listItemHolder.mListItemId = listItems.getmListItemId();
                        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImageIcon);

                        if (listItems.getmIsShowOptionMenu()) {
                            listItemHolder.mOptionIcon.setVisibility(View.GONE);
                        } else {
                            listItemHolder.mOptionIcon.setVisibility(View.VISIBLE);
                        }

                        listItemHolder.mHostImage.setVisibility(View.VISIBLE);
                        mImageLoader.setPersonImageUrl(listItems.getmHostImageUrl(), listItemHolder.mHostImage);

                        listItemHolder.mHostType = listItems.getmHostType();
                        listItemHolder.mHostId = listItems.getmOwnerId();


                        listItemHolder.mHostImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (listItemHolder.mHostType != null && listItemHolder.mHostType.equals("user")) {

                                    Intent intent = new Intent(mContext, userProfile.class);
                                    intent.putExtra(ConstantVariables.USER_ID, listItemHolder.mHostId);
                                    intent.putExtra(ConstantVariables.PROFILE_TYPE, "user_profile");
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                } else if (listItemHolder.mHostType != null && listItemHolder.mHostType.equals("siteevent_organizer")) {

                                    Intent intent = new Intent(mContext, userProfile.class);
                                    intent.putExtra(ConstantVariables.USER_ID, listItemHolder.mHostId);
                                    intent.putExtra(ConstantVariables.PROFILE_TYPE, "organizer_profile");
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            }
                        });


                        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());

                        listItemHolder.mOptionsArray = listItems.getMenuArray();

                        listItemHolder.mStartDate = listItems.getmStartTime();

                        String day = AppConstant.getDayFromDate(listItemHolder.mStartDate);
                        String month = AppConstant.getMonthFromDate(listItemHolder.mStartDate, "MMM");
                        listItemHolder.mDay.setText(day);
                        listItemHolder.mMonth.setText(month);
                        final String dateFormat = AppConstant.getMonthFromDate(listItemHolder.mStartDate, "MMM") + " " + AppConstant.getDayFromDate(listItemHolder.mStartDate) +
                                ", " + AppConstant.getYearFormat(listItemHolder.mStartDate);

                        String timeFormat = AppConstant.getHoursFromDate(listItemHolder.mStartDate);

                        listItemHolder.mDateIcon.setTypeface(fontIcon);
                        listItemHolder.mDateIcon.setText("\uf017");

                        String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
                        String dateDetail = String.format(createTextFormat, dateFormat,
                                mContext.getResources().getString(R.string.event_date_info), timeFormat);
                        if (listItems.getmHasMultipleDates() == 1) {
                            listItemHolder.mEventTime.setText(dateDetail + "(" +
                                    mContext.getResources().getString(R.string.multiple_date_text) + ")");
                        } else {
                            listItemHolder.mEventTime.setText(dateDetail);
                        }


                        if (listItems.getmLocation() != null && !listItems.getmLocation().isEmpty()) {
                            listItemHolder.mLocationLayout.setVisibility(View.VISIBLE);
                            listItemHolder.mLocationIcon.setText("\uf041");
                            listItemHolder.mEventLocation.setText(listItems.getmLocation());
                        } else {
                            listItemHolder.mLocationLayout.setVisibility(View.GONE);
                        }

                        mRootView.findViewById(R.id.counts_container).setVisibility(View.VISIBLE);

                        listItemHolder.mViewCountIcon.setText("\uf06e");
                        listItemHolder.mViewCountText.setText(String.valueOf(listItems.getmViewCount()));

                        listItemHolder.mMemberIcon.setText("\uf007");
                        listItemHolder.mMemberText.setText(String.valueOf(listItems.getmMemberCount()));

                        listItemHolder.mLikeIcon.setText("\uF164");
                        listItemHolder.mLike.setText(String.valueOf(listItems.getmLikeCount()));

                        mRootView.setId(listItems.getmListItemId());

                        listItemHolder.mOptionIcon.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                mPosition = (int) view.getTag();
                                mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                        mPosition, mBrowseItemList, currentSelectedOption,
                                        mCallingFragment, mCurrentSelectedList);
                            }
                        });

                        break;

                    case "sitepage_review":
                    case "sitegroup_review":

                        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());

                        LayerDrawable avgRatingStar = (LayerDrawable) listItemHolder.mRatingBar.getProgressDrawable();
                        avgRatingStar.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                                PorterDuff.Mode.SRC_ATOP);
                        avgRatingStar.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray),
                                PorterDuff.Mode.SRC_ATOP);

                        float rating = Float.parseFloat(String.valueOf(listItems.getmOverallRating()));
                        listItemHolder.mRatingBar.setRating(rating);
                        listItemHolder.mRatingBar.setIsIndicator(true);

                        /* Inflating rating parameter in table structure  */

                        if (listItems.getmRatingParams() != null) {
                            ratingLayoutGrid = CustomViews.generateRatingView(mContext,
                                    "bottom_view", listItems.getmRatingParams(), ratingLayoutGrid);
                        }

                        listItemHolder.mReviewDate.setText(AppConstant.convertDateFormat(mContext.getResources(),
                                listItems.getmBrowseListCreationDate()) + " " + mContext.getResources().
                                getString(R.string.owner_info_salutation) + " ");
                        listItemHolder.mUserName.setText(listItems.getmBrowseListOwnerTitle());

                        if (listItems.getmIsShowProsCons() == 1) {
                            listItemHolder.mPros.setText(listItems.getmPros());
                            listItemHolder.mCons.setText(listItems.getmCons());
                            listItemHolder.mProsLabel.setText(mContext.getResources().getString(R.string.pros_text) + ":");
                            listItemHolder.mConsLabel.setText(mContext.getResources().getString(R.string.cons_text) + ":");
                            listItemHolder.mProsLabel.setVisibility(View.VISIBLE);
                            listItemHolder.mConsLabel.setVisibility(View.VISIBLE);
                            listItemHolder.mCons.setVisibility(View.VISIBLE);
                            listItemHolder.mPros.setVisibility(View.VISIBLE);
                        } else {
                            listItemHolder.mProsLabel.setVisibility(View.GONE);
                            listItemHolder.mConsLabel.setVisibility(View.GONE);
                            listItemHolder.mCons.setVisibility(View.GONE);
                            listItemHolder.mPros.setVisibility(View.GONE);
                        }
                        listItemHolder.mRecomended.setText(listItems.getmRecommendText());
                        listItemHolder.mSummary.setText(listItems.getmBrowseBody());
                        listItemHolder.mShow.setText(mContext.getResources().getString(R.string.more));
                        listItemHolder.mHide.setText(mContext.getResources().getString(R.string.readLess));

                        /* Set More and Less content functionality */

                        if (listItemHolder.mSummary.getLineCount() > 3) {
                            listItemHolder.mShow.setVisibility(View.VISIBLE);
                            listItemHolder.mSummary.setMaxLines(3);
                            listItemHolder.mHide.setVisibility(View.GONE);
                            listItemHolder.mShow.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    listItemHolder.mShow.setVisibility(View.GONE);
                                    listItemHolder.mHide.setVisibility(View.VISIBLE);
                                    listItemHolder.mSummary.setMaxLines(Integer.MAX_VALUE);
                                }
                            });

                            listItemHolder.mHide.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    listItemHolder.mHide.setVisibility(View.GONE);
                                    listItemHolder.mShow.setVisibility(View.VISIBLE);
                                    listItemHolder.mSummary.setMaxLines(3);

                                }
                            });
                        } else {
                            listItemHolder.mShow.setVisibility(View.GONE);
                            listItemHolder.mHide.setVisibility(View.GONE);
                        }


                        listItemHolder.mOptionsArray = listItems.getmMenuArray();
                        listItemHolder.mListItemId = listItems.getmListItemId();
                        listItemHolder.mReviewId = listItems.getmReviewId();

                        mRootView.setId(listItemHolder.mListItemId);

                        /* Set click listener on more icon */

                        if (listItemHolder.mOptionsArray != null && listItemHolder.mOptionsArray.length() > 0) {
                            mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.VISIBLE);
                            listItemHolder.mOptionIconImage.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    mPosition = (int) view.getTag();
                                    mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                            mPosition, mBrowseItemList, currentSelectedOption,
                                            mCallingFragment, mCurrentSelectedList);
                                }
                            });
                        } else {
                            mRootView.findViewById(R.id.option_icon_layout).setVisibility(View.GONE);
                        }

                        listItemHolder.isLiked = listItems.getmIsLiked();
                        listItemHolder.mLikeCount = listItems.getmLikeCount();
                        listItemHolder.mLikeIcon.setTypeface(fontIcon);

                        if (listItemHolder.mLikeCount > 0) {
                            listItemHolder.mLikeIcon.setVisibility(View.VISIBLE);
                            listItemHolder.mLikeIcon.setText("\uf087 " + listItemHolder.mLikeCount);
                        } else {
                            listItemHolder.mLikeIcon.setVisibility(View.GONE);
                        }

                        if (listItemHolder.isLiked) {
                            listItemHolder.mLike.setText(mContext.getResources().getString(R.string.unlike));
                        } else {
                            listItemHolder.mLike.setText(mContext.getResources().getString(R.string.like_text));
                        }

                        if (mAppConst.isLoggedOutUser()) {
                            listItemHolder.mLike.setVisibility(View.GONE);
                        } else {
                            listItemHolder.mLike.setVisibility(View.VISIBLE);
                        }

                        listItemHolder.mCommentCount = listItems.getmCommentCount();
                        listItemHolder.mCommentText = mContext.getResources().getQuantityString(R.plurals.profile_page_comment,
                                listItemHolder.mCommentCount, listItemHolder.mCommentCount);

                        listItemHolder.mComments.setText(listItemHolder.mCommentCount + " " + listItemHolder.mCommentText);

                        listItemHolder.mComments.setTag(position);

                        /* Set click listener on comments text  */

                        listItemHolder.mComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                int clickedPosition = (int) v.getTag();

                                String mCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=" +
                                        mSubjectType + "&subject_id=" + listItemHolder.mReviewId +
                                        "&viewAllComments=1&page=1&limit=" + AppConstant.LIMIT;

                                Intent commentIntent = new Intent(mContext, Comment.class);
                                commentIntent.putExtra(ConstantVariables.ITEM_POSITION, clickedPosition);
                                commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mCommentsUrl);
                                commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                                commentIntent.putExtra(ConstantVariables.SUBJECT_ID, listItemHolder.mReviewId);
                                commentIntent.putExtra(ConstantVariables.ACTION_ID, listItemHolder.mReviewId);

                                if (mCallingFragment != null) {
                                    mCallingFragment.startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                                } else {
                                    ((Activity) mContext).startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                                }

                                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });

                        /* Set click listener on like text */

                        listItemHolder.mLike.setTag(position);

                        listItemHolder.mLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String likeUnlikeUrl = null;

                                int clickedPosition = (int) v.getTag();
                                final BrowseListItems listItemsPosition = (BrowseListItems) mBrowseItemList.get(clickedPosition);

                                if (listItemHolder.isLiked) {
                                    likeUnlikeUrl = AppConstant.DEFAULT_URL + mUrlText + "/review/unlike/" +
                                            listItemHolder.mListItemId + "/" + listItemHolder.mReviewId;
                                } else {
                                    likeUnlikeUrl = AppConstant.DEFAULT_URL + mUrlText + "/review/like/" +
                                            listItemHolder.mListItemId + "/" + listItemHolder.mReviewId;
                                }

                                if (listItemHolder.isLiked) {
                                    listItemHolder.mLike.setText(mContext.getResources().getString(R.string.like_text));
                                    listItemHolder.mLikeCount--;
                                    listItemHolder.isLiked = false;
                                } else {
                                    listItemHolder.mLike.setText(mContext.getResources().getString(R.string.unlike));
                                    listItemHolder.mLikeCount++;
                                    listItemHolder.isLiked = true;
                                }

                                if (listItemHolder.mLikeCount > 0) {
                                    listItemHolder.mLikeIcon.setText("\uf087 " + listItemHolder.mLikeCount);
                                    listItemHolder.mLikeIcon.setVisibility(View.VISIBLE);
                                } else {
                                    listItemHolder.mLikeIcon.setVisibility(View.GONE);
                                }

                                mAppConst.postJsonResponseForUrl(likeUnlikeUrl, postParams, new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

                                        listItemsPosition.setmIsLiked(listItemHolder.isLiked);
                                        listItemsPosition.setmLikeCount(listItemHolder.mLikeCount);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        SnackbarUtils.displaySnackbar(mRootView, message);

                                        if (listItemHolder.isLiked) {
                                            listItemHolder.mLike.setText(mContext.getResources().getString(R.string.like_text));
                                            listItemHolder.mLikeCount--;
                                            listItemHolder.isLiked = false;
                                        } else {
                                            listItemHolder.mLike.setText(mContext.getResources().getString(R.string.unlike));
                                            listItemHolder.mLikeCount++;
                                            listItemHolder.isLiked = true;
                                        }

                                        if (listItemHolder.mLikeCount > 0) {
                                            listItemHolder.mLikeIcon.setText("\uf087 " + listItemHolder.mLikeCount);
                                            listItemHolder.mLikeIcon.setVisibility(View.VISIBLE);
                                        } else {
                                            listItemHolder.mLikeIcon.setVisibility(View.GONE);
                                        }

                                    }

                                });

                            }
                        });

                        break;

                    case "site_package":
                        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());


                        if (listItems.getmDescriptionObject() != null) {
                            listItemHolder.mContentDetails.setMaxLines(1);
                            listItemHolder.mContentDetails.setText(listItems.getmDescriptionObject().optString("value"));
                        }
                        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
                        listItemHolder.mContentInfoBlock.setPadding(0, padding, 0, padding);

                        listItemHolder.mOptionsArray = listItems.getMenuArray();
                        listItemHolder.mOptionIconImage.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                mPosition = (int) view.getTag();
                                mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                        mPosition, mBrowseItemList, currentSelectedOption, mListingTypeId, mCurrentSelectedList);
                            }
                        });

                        break;
                }

                break;

            case TYPE_FB_AD:
                if (facebookAdViewHolder.mNativeAd != null) {
                    facebookAdViewHolder.mNativeAd.unregisterView();
                }
                facebookAdViewHolder.mNativeAd = (NativeAd) mBrowseItemList.get(position);
                FacebookAdViewHolder.inflateAd(facebookAdViewHolder.mNativeAd,
                        facebookAdViewHolder.adView, mContext, false);
                break;

            case TYPE_ADMOB:
                AdMobViewHolder.inflateAd(mContext,
                        (NativeAppInstallAd) mBrowseItemList.get(position), adMobViewHolder.mAdView);
                break;

            case TYPE_COMMUNITY_AD:
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


    private void sendRequestToServer(final int rsvpValue, int occurrence_id, int event_id, int rsvpChangePosition) {

        Map<String, String> rsvpValues = new HashMap<>();
        rsvpValues.put("rsvp", String.valueOf(rsvpValue));
        rsvpValues.put("occurrence_id", String.valueOf(occurrence_id));

        final BrowseListItems listItemsPosition = (BrowseListItems) mBrowseItemList.get(rsvpChangePosition);
        String rsvpChangeUrl = AppConstant.DEFAULT_URL + "advancedevents/member/join/" + event_id;

        mAppConst.postJsonResponseForUrl(rsvpChangeUrl, rsvpValues, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                listItemsPosition.setmRsvp(rsvpValue);

                notifyDataSetChanged();

                        /* Show Message */
                Toast.makeText(mContext, mContext.getResources().getString(R.string.rsvp_change_success_message),
                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });

    }

    @Override
    public void onItemDelete(int position) {

        try {
            BrowseListItems browseList = (BrowseListItems) mBrowseItemList.get(position);
            JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
            mBrowseItemList.remove(position);
            notifyDataSetChanged();
            if (mOnItemDeleteListener != null) {
                mOnItemDeleteListener.onItemDelete(getCount(),
                        (browseList.getmUserId() == userDetail.optInt("user_id")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {
        mBrowseItemList.set(position, itemList);
        if (menuName.equals("upgrade_package")) {
            if (mOnPackageChangeListener != null) {
                mOnPackageChangeListener.onPackageChanged();
            }
        } else {
            notifyDataSetChanged();
        }
    }

    public interface OnPackageChangeListener{
        void onPackageChanged();
    }

    OnPackageChangeListener mOnPackageChangeListener;

    public void setOnPackageChangeListener(OnPackageChangeListener onPackageChangeListener){
        mOnPackageChangeListener = onPackageChangeListener;
    }


    private static class ListItemHolder {
        ImageView mListImageIcon, mListImage;
        CircularImageView mHostImage;
        TextView mContentTitle, mDay, mMonth, mLike, mLikeIcon, mComments, mShow, mHide, mContentDetails;
        TextView mRsvp, mOptionIcon, mReviewDate, mUserName, mMemberOption;
        TextView mDateIcon, mLocationIcon, mPros, mProsLabel, mConsLabel, mCons, mSummary, mRecomended, mMemberIcon, mMemberText, mViewCountIcon, mViewCountText;
        TextView mEventLocation, mEventTime, mTicketsName, mPrice, mQuantity, mMoreInfo, mClaimedTickets, mEndTime;
        JSONArray mOptionsArray, mGuestArray;
        LinearLayout mLocationLayout, mDateInfo, mTicketsInfo, mContentInfoBlock;
        int mListItemId, mHostId, mEventId, mRsvpValue, mCommentCount, mReviewId, mLikeCount = 0, mOrderId;
        String mHostType, mStartDate, mCommentText, mEndDate;
        ImageView mOptionIconImage;
        Spinner spinner;
        LinearLayout rsvpFilterLayout, mTicketsMainLayout;
        RatingBar mRatingBar;
        Boolean isLiked = false;


    }

}

