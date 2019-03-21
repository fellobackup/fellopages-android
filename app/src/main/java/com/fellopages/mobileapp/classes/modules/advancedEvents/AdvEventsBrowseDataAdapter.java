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


package com.fellopages.mobileapp.classes.modules.advancedEvents;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.ads.admob.AdMobViewHolder;
import com.fellopages.mobileapp.classes.common.ads.FacebookAdViewHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.CommunityAdsHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.RemoveAdHolder;
import com.fellopages.mobileapp.classes.common.ui.CircularImageView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AdvEventsBrowseDataAdapter extends ArrayAdapter<Object> {

    private Context mContext;
    private List<Object> mBrowseItemList;
    private int mLayoutResID;
    private BrowseListItems listItems;
    private View mRootView;
    private Typeface fontIcon;
    private String mCurrentSelectedList;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FB_AD = 1;
    private static final int TYPE_ADMOB = 2;
    private static final int TYPE_COMMUNITY_AD = 3;
    private static final int REMOVE_COMMUNITY_ADS = 4;
    private static final int TYPE_MAX_COUNT = REMOVE_COMMUNITY_ADS + 1;
    private ArrayList<Integer> mRemoveAds;
    private ImageLoader mImageLoader;


    public AdvEventsBrowseDataAdapter(Context context, int layoutResourceID,
                                      List<Object> listItem,String selectedList) {

        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        if(selectedList != null){
            mCurrentSelectedList =  selectedList;
        }else {
            mCurrentSelectedList = PreferencesUtils.getCurrentSelectedList(mContext);
        }
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


    public interface OnQuantityChangeListener{
        void onDataChanged(int price, int count, int tickets_id, int isAdd);
    }

    OnQuantityChangeListener mOnQuantityChangeListener;

    public void setOnQuantityChangeListener(OnQuantityChangeListener onQuantityChangeListener){
        mOnQuantityChangeListener = onQuantityChangeListener;
    }

    private void doButtonOneClickActions(int price, int count, int tickets_id, int isAdd) {
        if(mOnQuantityChangeListener != null){
            mOnQuantityChangeListener.onDataChanged(price, count, tickets_id, isAdd);
        }
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        mRootView = convertView;
        final ListItemHolder listItemHolder;
        FacebookAdViewHolder facebookAdViewHolder = null;
        AdMobViewHolder adMobViewHolder = null;
        CommunityAdsHolder communityAdsHolder = null;
        RemoveAdHolder removeAdHolder = null;
        int type = getItemViewType(position);

        if (mRootView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
//            Log.d("ViewTypeList ", type);
            switch (type) {
                case TYPE_ITEM:
                    mRootView = inflater.inflate(mLayoutResID, parent, false);

                    switch (mCurrentSelectedList) {
                        case "members_siteevent":
                            listItemHolder.mListImage = mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mContentTitle = mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mRsvp = mRootView.findViewById(R.id.contentDetail);
                            mRootView.findViewById(R.id.optionsIcon).setVisibility(View.GONE);
                            break;

                        case "browse_diaries_siteevent":
                            listItemHolder.mListImage = mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mDiaryImage1 = mRootView.findViewById(R.id.diaryImage1);
                            listItemHolder.mDiaryImage2 = mRootView.findViewById(R.id.diaryImage2);
                            listItemHolder.mDiaryImage3 = mRootView.findViewById(R.id.diaryImage3);
                            listItemHolder.mDiaryImage4 = mRootView.findViewById(R.id.diaryImage4);
                            listItemHolder.mDiaryImage5 = mRootView.findViewById(R.id.diaryImage5);

                            listItemHolder.mContentTitle = mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mDiaryTitle = mRootView.findViewById(R.id.diaryTitle);
                            listItemHolder.mHostImage = mRootView.findViewById(R.id.host_image);
                            listItemHolder.mEventCount = mRootView.findViewById(R.id.eventCount);
                            listItemHolder.mEventLocation = mRootView.findViewById(R.id.eventLocationInfo);
                            listItemHolder.mEventTime = mRootView.findViewById(R.id.eventTime);
                            listItemHolder.mDateIcon = mRootView.findViewById(R.id.date_icon);
                            listItemHolder.mLocationIcon = mRootView.findViewById(R.id.location_icon);
                            listItemHolder.mLocationLayout = mRootView.findViewById(R.id.location_layout);
                            listItemHolder.mDay = mRootView.findViewById(R.id.day);
                            listItemHolder.mMonth = mRootView.findViewById(R.id.month);
                            break;

                        case "siteevent_tickets_info":
                            listItemHolder.mTicketsName = mRootView.findViewById(R.id.tickets_name);
                            listItemHolder.mPrice = mRootView.findViewById(R.id.price);
                            listItemHolder.mQuantity = mRootView.findViewById(R.id.quantity);
                            listItemHolder.mMoreInfo = mRootView.findViewById(R.id.more_text);
                            listItemHolder.mLessInfo = mRootView.findViewById(R.id.less_text);
                            listItemHolder.mClaimedTickets = mRootView.findViewById(R.id.claimed_tickets);
                            listItemHolder.mEndTime = mRootView.findViewById(R.id.end_date);
                            break;

                        case "ordered_tickets_info":
                            listItemHolder.mTicketsMainLayout = mRootView.findViewById(R.id.tickets_main_layout);
                            listItemHolder.mTicketsName = mRootView.findViewById(R.id.tickets_name);
                            listItemHolder.mPrice = mRootView.findViewById(R.id.price);
                            listItemHolder.mQuantity = mRootView.findViewById(R.id.quantity);
                            listItemHolder.mSubTotal = mRootView.findViewById(R.id.subtotal);
                            listItemHolder.mSubTotal.setVisibility(View.VISIBLE);
                            mRootView.findViewById(R.id.more_text).setVisibility(View.GONE);
                            break;

                        case "my_tickets_siteevent":
                            listItemHolder.mListImage = mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mContentTitle = mRootView.findViewById(R.id.contentTitle);
                            mRootView.findViewById(R.id.date_layout).setVisibility(View.VISIBLE);
                            mRootView.findViewById(R.id.contentDetail).setVisibility(View.GONE);
                            mRootView.findViewById(R.id.counts_container).setVisibility(View.GONE);
                            listItemHolder.mDateIcon = mRootView.findViewById(R.id.date_icon);
                            listItemHolder.mEventTime = mRootView.findViewById(R.id.eventTime);
                            listItemHolder.mTicketsInfo = mRootView.findViewById(R.id.orderInfo);
                            break;

                        default:
                            listItemHolder.mListImage = mRootView.findViewById(R.id.contentImage);
                            listItemHolder.mContentTitle = mRootView.findViewById(R.id.contentTitle);
                            listItemHolder.mDiaryTitle = mRootView.findViewById(R.id.diaryTitle);
                            listItemHolder.mHostImage = mRootView.findViewById(R.id.host_image);
                            listItemHolder.mEventCount = mRootView.findViewById(R.id.eventCount);
                            listItemHolder.mEventLocation = mRootView.findViewById(R.id.eventLocationInfo);
                            listItemHolder.mEventTime = mRootView.findViewById(R.id.eventTime);
                            listItemHolder.mDateIcon = mRootView.findViewById(R.id.date_icon);
                            listItemHolder.mLocationIcon = mRootView.findViewById(R.id.location_icon);
                            listItemHolder.mLocationLayout = mRootView.findViewById(R.id.location_layout);
                            listItemHolder.mDay = mRootView.findViewById(R.id.day);
                            listItemHolder.mMonth = mRootView.findViewById(R.id.month);
                            listItemHolder.mDateMonthIcon = mRootView.findViewById(R.id.day_month_layout);
                            mRootView.findViewById(R.id.counts_container).setVisibility(View.GONE);
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
                    break;
            }

        }


        switch (type) {
            case TYPE_ITEM:
                listItems = (BrowseListItems) this.mBrowseItemList.get(position);
                listItemHolder.mBrowseListName = listItems.getmBrowseListName();
                Log.d("ListEventCat ", mCurrentSelectedList);
                /*
                Set Data in the List View Items
                 */

                switch (mCurrentSelectedList) {
                    case "browse_siteevent":
                        mRootView.findViewById(R.id.contentImage).setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.diaryImageView1).setVisibility(View.GONE);
                        mRootView.findViewById(R.id.diaryImageView2).setVisibility(View.GONE);
                        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);
                        Log.d("SampleEventText", listItems.getmBrowseListTitle());
                        if (listItems.getmBrowseListTitle() != null && !listItems.getmBrowseListTitle().isEmpty()) {
                            listItemHolder.mContentTitle.setVisibility(View.VISIBLE);
                            listItemHolder.mContentTitle.setText(Html.fromHtml(listItems.getmBrowseListTitle()));

                        } else {
                            listItemHolder.mContentTitle.setVisibility(View.GONE);
                        }
                        mImageLoader.setPersonImageUrl(listItems.getmHostImageUrl(), listItemHolder.mHostImage);

                        listItemHolder.mHostImage.setVisibility(View.VISIBLE);
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

                        if(listItems.getmFeatured() == 1) {
                            mRootView.findViewById(R.id.featuredLabel).setVisibility(View.VISIBLE);
                        } else {
                            mRootView.findViewById(R.id.featuredLabel).setVisibility(View.GONE);
                        }

                        if (listItems.getmSponsored() == 1) {
                            mRootView.findViewById(R.id.sponsoredLabel).setVisibility(View.VISIBLE);
                        } else {
                            mRootView.findViewById(R.id.sponsoredLabel).setVisibility(View.GONE);
                        }

                        listItemHolder.mStartDate = listItems.getmStartTime();

                        String day = AppConstant.getDayFromDate(listItemHolder.mStartDate);
                        String month = AppConstant.getMonthFromDate(listItemHolder.mStartDate, "MMM");
                        listItemHolder.mDay.setText(day);
                        listItemHolder.mMonth.setText(month);
                        final String dateFormat = AppConstant.getMonthFromDate(listItemHolder.mStartDate, "MMM") + " " + AppConstant.getDayFromDate(listItemHolder.mStartDate) +
                                ", " + AppConstant.getYearFormat(listItemHolder.mStartDate);

                        final String searchDate =  AppConstant.getYearFormat(listItemHolder.mStartDate) + "-" +
                                AppConstant.getMonthFromDate(listItemHolder.mStartDate, "MM") + "-" +
                                AppConstant.getDayFromDate(listItemHolder.mStartDate);

                        String timeFormat = AppConstant.getHoursFromDate(listItemHolder.mStartDate,true);

                        listItemHolder.mDateIcon.setTypeface(fontIcon);
                        listItemHolder.mDateIcon.setText("\uf017");

                        String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
                        String dateDetail = String.format(createTextFormat, dateFormat,
                                mContext.getResources().getString(R.string.event_date_info), timeFormat);

                        if(listItems.getmHasMultipleDates() == 1) {

                            Log.d("EventTimeStart ", dateDetail + "(" +
                                    mContext.getResources().getString(R.string.multiple_date_text) + ")");
                            listItemHolder.mEventTime.setText(dateDetail + "(" +
                                    mContext.getResources().getString(R.string.multiple_date_text) + ")");
                        }else{
                            listItemHolder.mEventTime.setText(dateDetail);
                            Log.d("EventTimeStart ", dateDetail);
                        }


                        listItemHolder.mDateMonthIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Bundle searchParamsBundle = new Bundle();
                                searchParamsBundle.putString("date_current", searchDate);
                                searchParamsBundle.putString("viewtype", "list");
                                searchParamsBundle.putString(ConstantVariables.FRAGMENT_NAME, "search_by_date");
                                searchParamsBundle.putString(ConstantVariables.CONTENT_TITLE, dateFormat);
                                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                                newIntent.putExtras(searchParamsBundle);
                                mContext.startActivity(newIntent);
                                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        });

                        if (listItems.getmLocation() != null && !listItems.getmLocation().isEmpty()) {
                            listItemHolder.mLocationLayout.setVisibility(View.VISIBLE);
                            listItemHolder.mLocationIcon.setTypeface(fontIcon);
                            listItemHolder.mLocationIcon.setText("\uf041 ");
                            listItemHolder.mEventLocation.setText(listItems.getmLocation());
                        } else {
                            listItemHolder.mLocationLayout.setVisibility(View.GONE);
                        }

                        break;

                    case "browse_diaries_siteevent":
                        if (listItems.getmEventCount() == 0 || listItems.getmEventCount() == 1) {
                            listItemHolder.mListImage.setImageResource(R.drawable.defualt_diary_profile);
                            mRootView.findViewById(R.id.contentImage).setVisibility(View.VISIBLE);
                            mRootView.findViewById(R.id.diaryImageView1).setVisibility(View.GONE);
                            mRootView.findViewById(R.id.diaryImageView2).setVisibility(View.GONE);
                            mImageLoader.setImageUrl(listItems.getmImage1(), listItemHolder.mListImage);

                        } else if (listItems.getmEventCount() == 2) {

                            mRootView.findViewById(R.id.contentImage).setVisibility(View.GONE);
                            mRootView.findViewById(R.id.diaryImageView1).setVisibility(View.VISIBLE);
                            mRootView.findViewById(R.id.diaryImageView2).setVisibility(View.GONE);
                            mImageLoader.setImageUrl(listItems.getmImage1(), listItemHolder.mDiaryImage1);
                            mImageLoader.setImageUrl(listItems.getmImage2(), listItemHolder.mDiaryImage2);

                        } else {
                            mRootView.findViewById(R.id.contentImage).setVisibility(View.GONE);
                            mRootView.findViewById(R.id.diaryImageView1).setVisibility(View.GONE);
                            mRootView.findViewById(R.id.diaryImageView2).setVisibility(View.VISIBLE);
                            mImageLoader.setImageUrl(listItems.getmImage1(), listItemHolder.mDiaryImage3);
                            mImageLoader.setImageUrl(listItems.getmImage2(), listItemHolder.mDiaryImage4);
                            mImageLoader.setImageUrl(listItems.getmImage3(), listItemHolder.mDiaryImage5);

                        }

                        mRootView.findViewById(R.id.eventInfo).setVisibility(View.GONE);
                        if (listItems.getmBrowseListTitle() != null && !listItems.getmBrowseListTitle().isEmpty()) {
                            listItemHolder.mDiaryTitle.setVisibility(View.VISIBLE);
                            listItemHolder.mDiaryTitle.setText(listItems.getmBrowseListTitle());
                        } else {
                            listItemHolder.mDiaryTitle.setVisibility(View.GONE);
                        }

                        listItemHolder.mEventCount.setText(String.valueOf(listItems.getmEventCount()));
                        mRootView.findViewById(R.id.eventCount).setVisibility(View.VISIBLE);

                        break;

                    case "members_siteevent":
                        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);

                        String rsvp;
                        if (listItems.getmRsvp() == 2) {
                            rsvp = "Attending";
                        } else if (listItems.getmRsvp() == 1) {
                            rsvp = "Maybe Attending";
                        } else {
                            rsvp = "Not Attending";
                        }
                        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());
                        listItemHolder.mRsvp.setText(rsvp);

                        break;

                    case "my_tickets_siteevent":
                        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);

                        listItemHolder.mStartDate = listItems.getmStartTime();
                        listItemHolder.mOptionsArray = listItems.getmMenuArray();
                        listItemHolder.mOrderId = listItems.getmOrderId();

                        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());
                        final String date_format = AppConstant.getMonthFromDate(listItemHolder.mStartDate, "MMM") + " " + AppConstant.getDayFromDate(listItemHolder.mStartDate) +
                                ", " + AppConstant.getYearFormat(listItemHolder.mStartDate);

                        String time_format = AppConstant.getHoursFromDate(listItemHolder.mStartDate);

                        listItemHolder.mDateIcon.setTypeface(fontIcon);
                        listItemHolder.mDateIcon.setText("\uf017");

                        String create_text_format = mContext.getResources().getString(R.string.event_date_info_format);
                        String dateDetails = String.format(create_text_format, date_format,
                                mContext.getResources().getString(R.string.event_date_info), time_format);
                        listItemHolder.mEventTime.setText(dateDetails);

                        listItemHolder.mTicketsInfo.setVisibility(View.VISIBLE);

                        listItemHolder.mTicketsInfo.removeAllViews();

                        if(listItems != null && listItems.getmOrderInfo() != null) {
                            Iterator iter = listItems.getmOrderInfo().keys();
                            while (iter.hasNext()) {
                                String key = (String) iter.next();
                                String value = listItems.getmOrderInfo().optString(key);

                                TextView textView = new TextView(mContext);
                                textView.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_3));
                                textView.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.padding_5dp), 0, 0);
                                textView.setText(key + ": " + value);
                                listItemHolder.mTicketsInfo.addView(textView);
                            }
                        }
                        break;
                    case "siteevent_tickets_info":
                        int padding10 = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
                        listItemHolder.mTickestPrice = listItems.getmTicketsPrice();
                        listItemHolder.mCount = 0;
                        listItemHolder.mTicketsId = listItems.getmListItemId();
                        listItemHolder.mMinQuantity = listItems.getmMinValue();
                        listItemHolder.mMaxQuantity = listItems.getmMaxValue();
                        listItemHolder.mEndDate = listItems.getmEndTime();

                        listItemHolder.mTicketsName.setText(listItems.getmBrowseListTitle());

                        if (listItemHolder.mTickestPrice == 0) {
                            listItemHolder.mPrice.setText(mContext.getResources().getString(R.string.free_text));
                        } else {
                            listItemHolder.mPrice.setText(listItems.getmCurrency()+""+listItemHolder.mTickestPrice);
                        }
                        Log.d("TicketsMStatus ", listItems.getmStatus());
                        if (listItems.getmStatus().equals("1")) {
                            int padding5 = (int) mContext.getResources().getDimension(R.dimen.padding_5dp);
                            listItemHolder.mQuantity.setText(String.valueOf(listItemHolder.mCount));
                            listItemHolder.mQuantity.setPadding(padding10, padding5, padding10, padding5);
                            listItemHolder.mQuantity.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_box));
                            if (listItemHolder.mMaxQuantity == 0) {
                                listItemHolder.mMaxQuantity = ConstantVariables.DEFAULT_TICKETS_COUNT;
                            }
                        }else if (listItems.getmStatus().equals("hidden")){
                            listItemHolder.mQuantity.setText(listItems.getmStatus());
                            listItemHolder.mQuantity.setTextColor(ContextCompat.getColor(mContext, R.color.gray_text_color));
                            listItemHolder.mQuantity.setEnabled(false);
                        } else if (listItems.getmStatus().contains("Sold Out")){
                            int color = ContextCompat.getColor(mContext, R.color.gray_text_color);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                            listItemHolder.mQuantity.setLayoutParams(layoutParams);

                            if (listItems.getmStatusColor().equals("G")) {
                                color = ContextCompat.getColor(mContext, R.color.light_green);
                            } else if (listItems.getmStatusColor().equals("R")) {
                                color = ContextCompat.getColor(mContext, R.color.red);
                            }

//                            int padding5 = (int) mContext.getResources().getDimension(R.dimen.padding_5dp);
//                            listItemHolder.mQuantity.setText(String.valueOf(listItemHolder.mCount));
//                            listItemHolder.mQuantity.setPadding(padding10, padding5, padding10, padding5);
//                            listItemHolder.mQuantity.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_box));
//                            if (listItemHolder.mMaxQuantity == 0) {
//                                listItemHolder.mMaxQuantity = ConstantVariables.DEFAULT_TICKETS_COUNT;
//                            }
//                            listItemHolder.mQuantity.setText(String.valueOf(listItemHolder.mCount));
                            listItemHolder.mQuantity.setTextColor(color);
                            listItemHolder.mQuantity.setText(listItems.getmStatus());
                        } else {
                            int color = ContextCompat.getColor(mContext, R.color.gray_text_color);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                            listItemHolder.mQuantity.setLayoutParams(layoutParams);

                            if (listItems.getmStatusColor().equals("G")) {
                                color = ContextCompat.getColor(mContext, R.color.light_green);
                            } else if (listItems.getmStatusColor().equals("R")) {
                                color = ContextCompat.getColor(mContext, R.color.red);
                            }

//                            int padding5 = (int) mContext.getResources().getDimension(R.dimen.padding_5dp);
//                            listItemHolder.mQuantity.setText(String.valueOf(listItemHolder.mCount));
//                            listItemHolder.mQuantity.setPadding(padding10, padding5, padding10, padding5);
//                            listItemHolder.mQuantity.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_box));
//                            if (listItemHolder.mMaxQuantity == 0) {
//                                listItemHolder.mMaxQuantity = ConstantVariables.DEFAULT_TICKETS_COUNT;
//                            }
//                            listItemHolder.mQuantity.setText(String.valueOf(listItemHolder.mCount));
                            listItemHolder.mQuantity.setTextColor(color);
                            listItemHolder.mQuantity.setText(listItems.getmStatus());
                        }

                        listItemHolder.mClaimedTickets.setText(mContext.getResources().getString(R.string.claimed_tickets) +
                                ": " + listItems.getmQuantity());

                        final String endDateFormat = AppConstant.getMonthFromDate(listItemHolder.mEndDate, "MMM") + " "
                                + AppConstant.getDayFromDate(listItemHolder.mEndDate) +
                                ", " + AppConstant.getYearFormat(listItemHolder.mEndDate);
                        String endTimeFormat = AppConstant.getHoursFromDate(listItemHolder.mEndDate);

                        String createDateFormat = mContext.getResources().getString(R.string.event_date_info_format);
                        String endDate = String.format(createDateFormat, endDateFormat,
                                mContext.getResources().getString(R.string.event_date_info), endTimeFormat);

                        listItemHolder.mEndTime.setText(mContext.getResources().getString(R.string.end_date_text) + ": " + endDate);

                        listItemHolder.mMoreInfo.setTag(position);
                        listItemHolder.mLessInfo.setTag(position);

                        if (listItems.getSize() > 1) {
                            listItemHolder.mMoreInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listItemHolder.mClaimedTickets.setVisibility(View.VISIBLE);
                                    listItemHolder.mEndTime.setVisibility(View.VISIBLE);
                                    listItemHolder.mLessInfo.setVisibility(View.VISIBLE);
                                    listItemHolder.mMoreInfo.setVisibility(View.GONE);
                                }
                            });

                            listItemHolder.mLessInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listItemHolder.mClaimedTickets.setVisibility(View.GONE);
                                    listItemHolder.mEndTime.setVisibility(View.GONE);
                                    listItemHolder.mLessInfo.setVisibility(View.GONE);
                                    listItemHolder.mMoreInfo.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            listItemHolder.mClaimedTickets.setPadding(padding10, 0, padding10, padding10);
                            listItemHolder.mClaimedTickets.setVisibility(View.VISIBLE);
                            listItemHolder.mEndTime.setVisibility(View.VISIBLE);
                            listItemHolder.mMoreInfo.setVisibility(View.GONE);
                            listItemHolder.mEndTime.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                            listItemHolder.mClaimedTickets.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

                        }
                        if(!listItems.getmStatus().contains("Sale starts on") && !listItems.getmStatus().contains("Sold Out") && !listItems.getmStatus().contains("Sale ended")){
                            listItemHolder.mQuantity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!listItems.getmStatus().contains("Sale starts on") && !listItems.getmStatus().contains("Sold Out")){
//                                    SnackbarUtils.

                                        int size = listItemHolder.mMaxQuantity + 1;

                                        if (listItemHolder.mMinQuantity > 1) {
                                            size = size - listItemHolder.mMinQuantity;
                                            size++;
                                        }

                                /*final CharSequence[] items = new CharSequence[size];
                                items [0] = "0";
                                int value = listItemHolder.mMinQuantity;
                                for (int i = 1; i < size; i++) {
                                    items[i] = String.valueOf(value);
                                    value++;
                                }*/

                                        final CharSequence[] items = new CharSequence[size];
                                        int value = listItemHolder.mMinQuantity;
                                        if( listItemHolder.mMinQuantity != 0){
                                            items [0] = "0";
                                            for (int i = 1; i < size; i++) {
                                                items[i] = String.valueOf(value);
                                                value++;
                                            }
                                        }else{
                                            for (int i = 0; i < size; i++) {
                                                items[i] = String.valueOf(value);
                                                value++;
                                            }
                                        }

                                        // Creating and Building the Dialog
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle(mContext.getResources().getString(R.string.chosse_tickets_quantity));
                                        builder.setCancelable(true);
                                        builder.setSingleChoiceItems(items, listItemHolder.selectedPosition, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int pos) {

                                                listItemHolder.selectedPosition = pos;
                                                int quantity = Integer.parseInt(items[pos].toString());
                                                listItemHolder.mQuantity.setText(String.valueOf(quantity));
                                                int price = 0, isAdd = 0;
                                                if (listItemHolder.mCount > quantity) {
                                                    isAdd = 0;
                                                    price = (listItemHolder.mCount - quantity) * listItemHolder.mTickestPrice;
                                                } else if (listItemHolder.mCount < quantity){
                                                    isAdd = 1;
                                                    price = ( quantity - listItemHolder.mCount) * listItemHolder.mTickestPrice;
                                                }
                                                if (listItemHolder.mCount != quantity) {
                                                    listItemHolder.mCount = quantity;
                                                    doButtonOneClickActions(price, listItemHolder.mCount, listItemHolder.mTicketsId, isAdd);
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                    } else {
                                        return;
                                    }
                                }
                            });
                        }

//                        if(!listItems.getmStatus().contains("Sale starts on") && !listItems.getmStatus().contains("Sold Out")){
//
//                        }

                        break;

                    case "ordered_tickets_info":
                        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
                        listItemHolder.mTicketsMainLayout.setPadding(padding, 0, padding, 0);
                        listItemHolder.mTicketsName.setText(listItems.getmBrowseListTitle());
                        listItemHolder.mPrice.setText(GlobalFunctions.getFormattedCurrencyString(listItems.getmCurrency(), listItems.getmTicketsPrice()));
                        listItemHolder.mQuantity.setText(listItems.getmQuantity());
                        listItemHolder.mSubTotal.setText(GlobalFunctions.getFormattedCurrencyString(listItems.getmCurrency(), listItems.getmSubTotal()));
                        break;
                }

                mRootView.setId(listItems.getmListItemId());

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
                removeAdHolder.mCommunityAd = (CommunityAdsList) mBrowseItemList.get(position);
                removeAdHolder.removeAd(removeAdHolder.mCommunityAd, removeAdHolder.adView, mContext, position);
                break;
        }
        return mRootView;
    }


    private static class ListItemHolder {

        ImageView mListImage, mDiaryImage1, mDiaryImage2, mDiaryImage3, mDiaryImage4, mDiaryImage5;
        CircularImageView mHostImage;
        TextView mContentTitle, mDiaryTitle;
        TextView mDateIcon, mLocationIcon, mDay, mMonth;
        TextView mTicketsName, mPrice, mQuantity, mMoreInfo, mClaimedTickets, mEndTime, mLessInfo, mSubTotal;
        LinearLayout mTicketsMainLayout, mTicketsInfo;
        String mEndDate;
        int mHostId, mCount, mTickestPrice, mMinQuantity, mMaxQuantity,  selectedPosition = 0;
        JSONArray mOptionsArray;
        public int mTicketsId;
        int mOrderId;
        LinearLayout mLocationLayout, mDateMonthIcon;
        TextView mEventLocation, mEventTime, mEventCount, mRsvp;
        String mBrowseListName, mHostType, mStartDate;
    }
}

