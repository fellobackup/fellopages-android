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
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.MapActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.advancedEvents.ticketsSelling.AdvEventsBuyTicketsInfo;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AdvEventsInfoTabFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private AppConstant mAppConst;

    private CharSequence[] items = new CharSequence[3];
    private String menuName, title, mRequestUrl, mHostType, mItemViewUrl, dialogueMessage, dialogueTitle,
            dialogueButton, successMessage, updateReviewUrl, mClickedButton, mStatusColor, announcementTitle,
            announcementDescription;
    private int mEventId, mOccurrenceId, mSelectedRSVP, mAnnouncementCount, rating, mHostId, padding, margin;
    private boolean isAddToDiary = false, isWriteReview = false, isUpdateReview = false;

    private Typeface fontIcon;
    private Map<String, String> postParams;
    private JSONObject mDataResponse, mAnnouncementObject;
    private JSONArray mGutterMenus;

    private LinearLayout mEventRsvpForm, mEventJoinForm, mLocationInfoBlock, mHostInfo, mEventDiaryTab,
            mEventsDateInfo, mAnnouncementLayout, mPhoneInfoBlock,mEmailInfoBlock,mWebsiteInfoBlock;
    private Button mAttendingButton, mMayBeAttendingButton, mNotAttendingButton, mJoinButton,
            mWriteReviewButton, mAddToDiaryButton,mAddToCalendar;
    private TextView mHostName, mEventStatus, mShow, mHide, mLocationIcon, mDateIcon,
            mAnnouncementTitle, mAnnouncementDescription, mMoreText;
    private String location, hostTitle, startTime, endTime, hostImage, phone, email, website;
    private SelectableTextView mEventStartDate, mEventEndDateTime, mDescription;
    private BezelImageView mHostImage;
    private RatingBar mRating;
    private LinearLayout.LayoutParams layoutParams;
    private JSONObject mResponseJsonObject;
    private View mRootView;
    private TextView mEventLocation,mEventPhone,mEventEmail,mEventWebsite;
    private ImageLoader mImageLoader;
    public static String sGuid;


    public AdvEventsInfoTabFragment() {
        // Required empty public constructor
    }

    public static AdvEventsInfoTabFragment newInstance(Bundle bundle){
        AdvEventsInfoTabFragment fragment = new AdvEventsInfoTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_advanced_events_info, container, false);

        Bundle bundle = getArguments();
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mImageLoader = new ImageLoader(mContext);
        postParams = new HashMap<>();
        updateData(bundle, false);

        // Initializing views
        initializeView();

        return mRootView;
    }

    public void updateData(Bundle bundle, boolean isUpdateRequest) {
        try {
            String mDataResponse = bundle.getString(ConstantVariables.RESPONSE_OBJECT);
            mResponseJsonObject = new JSONObject(mDataResponse);
            mItemViewUrl = bundle.getString(ConstantVariables.VIEW_PAGE_URL);
            mItemViewUrl += "&rsvp_form=1";

        } catch (Exception e) {
            e.printStackTrace();
        }

        mGutterMenus = mResponseJsonObject.optJSONArray("gutterMenu");

        if (mGutterMenus != null) {
            for (int i = 0; i < mGutterMenus.length(); i++) {
                JSONObject menuJsonObject = mGutterMenus.optJSONObject(i);

                switch (menuJsonObject.optString("name")) {
                    case "diary":
                        isAddToDiary = true;
                        break;
                    case "createReview":
                        isWriteReview = true;
                        isUpdateReview = false;
                        break;
                    case "updateReview":
                        isWriteReview = false;
                        isUpdateReview = true;
                        updateReviewUrl = menuJsonObject.optString("url");
                        JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                        String url = AppConstant.DEFAULT_URL + menuJsonObject.optString("url");
                        if (urlParams != null && urlParams.length() != 0) {
                            JSONArray urlParamsNames = urlParams.names();
                            for (int j = 0; j < urlParams.length(); j++) {
                                String name = urlParamsNames.optString(j);
                                String value = urlParams.optString(name);
                                postParams.put(name, value);
                                updateReviewUrl = mAppConst.buildQueryString(url, postParams);
                            }

                        }
                        break;
                }
            }
        }
        if (isUpdateRequest && isAdded()) {
            setDataInViews(mResponseJsonObject);
        }
    }

    private void initializeView() {

        mEventRsvpForm = mRootView.findViewById(R.id.eventRsvpForm);
        mEventJoinForm = mRootView.findViewById(R.id.eventJoinForm);
        mAttendingButton = mRootView.findViewById(R.id.attendingButton);
        mMayBeAttendingButton = mRootView.findViewById(R.id.mayBeAttendingButton);
        mNotAttendingButton = mRootView.findViewById(R.id.notAttendingButton);
        mJoinButton = mRootView.findViewById(R.id.joinEventButton);
        mEventDiaryTab = mRootView.findViewById(R.id.tabsLayout);
        mWriteReviewButton = mRootView.findViewById(R.id.writeReview);
        mAddToDiaryButton  = mRootView.findViewById(R.id.addToDiary);
        mAddToCalendar = mRootView.findViewById(R.id.addToCalendar);

        layoutParams = CustomViews.getFullWidthLayoutParams();
        padding = (int) getResources().getDimension(R.dimen.padding_10dp);
        margin = (int) getResources().getDimension(R.dimen.margin_5dp);

        mAttendingButton.setOnClickListener(this);
        mMayBeAttendingButton.setOnClickListener(this);
        mNotAttendingButton.setOnClickListener(this);
        mJoinButton.setOnClickListener(this);
        mAddToDiaryButton.setOnClickListener(this);
        mWriteReviewButton.setOnClickListener(this);
        mAddToCalendar.setOnClickListener(this);

        mAttendingButton.setTypeface(fontIcon);
        mMayBeAttendingButton.setTypeface(fontIcon);
        mNotAttendingButton.setTypeface(fontIcon);

        mEventStartDate = mRootView.findViewById(R.id.eventStartDate);
        mEventEndDateTime = mRootView.findViewById(R.id.eventEndDateTime);
        mLocationInfoBlock = mRootView.findViewById(R.id.eventLocationInfo);
        mPhoneInfoBlock = mRootView.findViewById(R.id.eventPhoneInfo);
        mWebsiteInfoBlock = mRootView.findViewById(R.id.eventWebsiteInfo);
        mEmailInfoBlock = mRootView.findViewById(R.id.eventEmailInfo);
        mEventLocation = mRootView.findViewById(R.id.eventLocation);
        mEventPhone = mRootView.findViewById(R.id.eventPhone);
        mEventEmail = mRootView.findViewById(R.id.eventEmail);
        mEventWebsite = mRootView.findViewById(R.id.eventWebsite);
        mLocationIcon = mRootView.findViewById(R.id.locationLabel);
        mEventsDateInfo = mRootView.findViewById(R.id.eventDatesInfo);
        mDateIcon = mRootView.findViewById(R.id.dateLabel);
        mEventStatus = mRootView.findViewById(R.id.eventStatus);

        mLocationIcon.setTypeface(fontIcon);
        mLocationIcon.setText("\uf041" + " " + getResources().getString(R.string.location_label));
        mDateIcon.setTypeface(fontIcon);
        mDateIcon.setText("\uf017" + " " + getResources().getString(R.string.date_label));

        mHostInfo = mRootView.findViewById(R.id.host_info);
        mHostImage = mRootView.findViewById(R.id.host_image);
        mHostName = mRootView.findViewById(R.id.hostName);
        mHostInfo.setOnClickListener(this);

        mAnnouncementLayout = mRootView.findViewById(R.id.announcement_layout);
        mAnnouncementTitle = mRootView.findViewById(R.id.announcement_title);
        mAnnouncementDescription = mRootView.findViewById(R.id.announcement_description);
        mMoreText  = mRootView.findViewById(R.id.more);


        mRating = mRootView.findViewById(R.id.smallRatingBar);
        LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_ATOP);

        mDescription  = mRootView.findViewById(R.id.description);
        mShow = mRootView.findViewById(R.id.show);
        mHide = mRootView.findViewById(R.id.hide);

        items[0] = AdvEventsProfilePage.sAttending =  mContext.getResources().getString(R.string.rsvp_filter_attending);
        items[1] = AdvEventsProfilePage.sMayBeAttending = mContext.getResources().getString(R.string.rsvp_filter_may_be_attending);
        items[2] = AdvEventsProfilePage.sNotAttending = mContext.getResources().getString(R.string.rsvp_filter_not_attending);

        //Set data in views
        setDataInViews(mResponseJsonObject);
    }

    private void setDataInViews(JSONObject mResponseJsonObject) {

        if(mResponseJsonObject != null && mResponseJsonObject.length() != 0) {
            try {
                  /*
                Fetch Data from Response
                 */
                mDataResponse = mResponseJsonObject.getJSONObject("response");
                mEventId = mDataResponse.getInt("event_id");
                title = mDataResponse.getString("title");
                String descrption = mDataResponse.optString("body");
                location = mDataResponse.getString("location");
                String guid = mDataResponse.getString("guid");
                startTime = mDataResponse.getString("starttime");
                endTime = mDataResponse.getString("endtime");
                String eventStatus =  mDataResponse.getString("status");
                mStatusColor = mDataResponse.optString("status_color");
                mOccurrenceId = mDataResponse.optInt("occurrence_id");
                String timezone = mDataResponse.getString("timezone");
                timezone = timezone != null ? "\n(GMT): " + timezone : "";

                JSONObject contactObject = mDataResponse.getJSONObject("contact_info");
                phone = contactObject.getString("phone");
                email = contactObject.getString("email");
                website = contactObject.getString("website");

                JSONObject hostObject = mDataResponse.getJSONObject("host");
                JSONObject imageObject = hostObject.getJSONObject("image_icon");
                hostImage = imageObject.getString("image");
                hostTitle = hostObject.getString("host_title");
                mHostId = hostObject.getInt("host_id");
                mHostType = hostObject.getString("host_type");

                rating = mDataResponse.getInt("rating_avg");

                mAnnouncementObject = mResponseJsonObject.optJSONObject("announcement");
                if (mAnnouncementObject != null) {
                    mAnnouncementCount = mAnnouncementObject.optInt("announcementCount");
                    JSONArray announcementArray = mAnnouncementObject.optJSONArray("announcements");
                    JSONObject dataObject = announcementArray.optJSONObject(0);
                    announcementTitle = dataObject.optString("title");
                    announcementDescription = dataObject.optString("body");
                }

                // Set values in views
                mImageLoader.setPersonImageUrl(hostImage, mHostImage);

                if (location != null && !location.isEmpty()) {
                    mEventLocation.setMovementMethod(LinkMovementMethod.getInstance());
                    mEventLocation.setText(location);
                    mEventLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent mapIntent = new Intent(mContext, MapActivity.class);
                            mapIntent.putExtra("location", location);
                            startActivity(mapIntent);
                            ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });

                }else{
                    mLocationInfoBlock.setVisibility(View.GONE);
                }

                if (phone != null && !phone.isEmpty()) {
                    mEventPhone.setText(phone);
                }else{
                    mPhoneInfoBlock.setVisibility(View.GONE);
                }
                if (email != null && !email.isEmpty()) {
                    mEventEmail.setText(email);
                    //Uncomment to redirect
//                    mEventEmail.setOnClickListener(view -> {
//                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//                        emailIntent.setData(Uri.parse(email));
//
//                        try {
//                            startActivity(emailIntent);
//                        } catch (ActivityNotFoundException e) {
//                            //TODO: Handle case where no email app is available
//                        }
//                    });
                }else{
                    mEmailInfoBlock.setVisibility(View.GONE);
                }

                if (website != null && !website.isEmpty()) {
                    mEventWebsite.setText(website);
                    //Uncomment to redirect
//                    mEventWebsite.setOnClickListener(view -> {
//                        Intent lIntent = new Intent(Intent.ACTION_VIEW);
//                        lIntent.setData(Uri.parse(website));
//                        mContext.startActivity(Intent.createChooser(lIntent, mContext.getResources().getString(R.string.browse_dashboard_title)));
//
//                    });
                } else{
                    mWebsiteInfoBlock.setVisibility(View.GONE);
                }
                /*
                   hide location block
                 */
//                else {
//                    mLocationInfoBlock.setVisibility(View.GONE);
//                    mRootView.findViewById(R.id.event_vertical_line).setVisibility(View.GONE);
//                    layoutParams.setMargins(padding, 0, padding, 0);
//                    mEventsDateInfo.setLayoutParams(layoutParams);
//                }

                String dateFormat = AppConstant.getMonthFromDate(startTime, "MMM") + " " + AppConstant.getDayFromDate(startTime) +
                        ", " + AppConstant.getYearFormat(startTime);
                String timeFormat = AppConstant.getHoursFromDate(startTime,true);
                String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
                String dateDetail = String.format(createTextFormat, dateFormat,
                        mContext.getResources().getString(R.string.event_date_info), timeFormat);

                AdvEventsInfoTabFragment.sGuid = guid;

                mEventStartDate.setText(dateDetail);

                String dateFormat1 = AppConstant.getMonthFromDate(endTime, "MMM") + " " + AppConstant.getDayFromDate(endTime) +
                        ", " + AppConstant.getYearFormat(endTime);
                String timeFormat1 = AppConstant.getHoursFromDate(endTime,true);
                String createTextFormat1 = mContext.getResources().getString(R.string.event_date_info_format);
                String dateDetail1 = String.format(createTextFormat1, dateFormat1,
                        mContext.getResources().getString(R.string.event_date_info), timeFormat1);

                mEventEndDateTime.setText(dateDetail1 + timezone);

                /* Set color of event status */
                if(mStatusColor != null){
                    switch (mStatusColor) {
                        case "G":
                            mEventStatus.setTextColor(ContextCompat.getColor(mContext, R.color.bg_btn_join));
                            break;
                        case "B":
                            mEventStatus.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue));
                            break;
                        case "R":
                            mEventStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                            break;
                    }
                }

                if (ConstantVariables.BOOK_EVENT_TICKETS != 4) {
                    mEventStatus.setText(eventStatus);
                    mEventStatus.setVisibility(View.VISIBLE);
                }

                if (announcementTitle != null && announcementDescription != null) {
                    mAnnouncementTitle.setText(announcementTitle);
                    mAnnouncementDescription.setText(announcementDescription);
                    mAnnouncementLayout.setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.announcement_divider).setVisibility(View.VISIBLE);

                    if (mAnnouncementDescription.getLineCount() > 3) {
                        mAnnouncementDescription.setMaxLines(3);
                        mMoreText.setVisibility(View.VISIBLE);
                        mMoreText.setText(getResources().getString(R.string.more));
                        mMoreText.setOnClickListener(this);
                    }
                }

                // Check for add to diary option.
                if (isAddToDiary) {
                    mAddToDiaryButton.setVisibility(View.VISIBLE);
                } else {
                    mAddToDiaryButton.setVisibility(View.GONE);
                    layoutParams.setMargins(margin, 0, margin, 0);
                    mWriteReviewButton.setLayoutParams(layoutParams);
                }

                // Check for Write/Update Review option.
                if (isWriteReview) {
                    mWriteReviewButton.setVisibility(View.VISIBLE);
                    mWriteReviewButton.setText(getResources().getString(R.string.write_review_button_text));
                } else if (isUpdateReview) {
                    mWriteReviewButton.setVisibility(View.VISIBLE);
                    mWriteReviewButton.setText(getResources().getString(R.string.update_review_button_text));
                } else {
                    mWriteReviewButton.setVisibility(View.GONE);
                    layoutParams.setMargins(margin, 0, margin, 0);
                    mAddToDiaryButton.setLayoutParams(layoutParams);
                }

                if (!isAddToDiary && (!isWriteReview || !isUpdateReview)) {
                    mEventDiaryTab.setVisibility(View.GONE);
                }

                mHostName.setText(hostTitle);
                mRating.setRating(rating);
                mRating.setIsIndicator(true);

                if (descrption != null) {
                    mRootView.findViewById(R.id.host_info_divider).setVisibility(View.VISIBLE);
                    mDescription.setText(Html.fromHtml(descrption));
                    mShow.setText(mContext.getResources().getString(R.string.more));
                    mHide.setText(mContext.getResources().getString(R.string.readLess));

                  /* Set More and Less content functionality */

                    if (mDescription.getLineCount() > 3) {
                        mShow.setVisibility(View.VISIBLE);
                        mDescription.setMaxLines(3);
                        mHide.setVisibility(View.GONE);
                        mShow.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mShow.setVisibility(View.GONE);
                                mHide.setVisibility(View.VISIBLE);
                                mDescription.setMaxLines(Integer.MAX_VALUE);
                            }
                        });

                        mHide.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mHide.setVisibility(View.GONE);
                                mShow.setVisibility(View.VISIBLE);
                                mDescription.setMaxLines(3);

                            }
                        });
                    } else {
                        mShow.setVisibility(View.GONE);
                        mHide.setVisibility(View.GONE);
                    }
                } else {
                    mDescription.setVisibility(View.GONE);
                    mRootView.findViewById(R.id.host_info_divider).setVisibility(View.GONE);
                }

                // Setting up the member ship value accordingly.
                setMemberShipValues();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void setMemberShipValues() {

        if (AdvEventsProfilePage.sMembershipRequestCode != 0) {
            mEventJoinForm.setVisibility(View.VISIBLE);
            mJoinButton.setVisibility(View.VISIBLE);
            mEventRsvpForm.setVisibility(View.GONE);

            switch (AdvEventsProfilePage.sMembershipRequestCode) {

                case ConstantVariables.REQUEST_INVITE:
                    mJoinButton.setText(getResources().getString(R.string.request_invite));

                    mRequestUrl = AppConstant.DEFAULT_URL + "advancedevents/member/request/";
                    menuName = "request_invite";
                    dialogueMessage = getResources().getString(R.string.event_request_member_dialogue_message);
                    dialogueTitle = getResources().getString(R.string.event_request_member_dialogue_title);
                    dialogueButton = getResources().getString(R.string.event_request_member_dialogue_button);
                    successMessage = getResources().getString
                            (R.string.event_request_member_dialogue_success_message);
                    break;

                case ConstantVariables.CANCEL_INVITE:
                    mJoinButton.setText(getResources().getString(R.string.cancel_request_invite));

                    mRequestUrl = AppConstant.DEFAULT_URL + "advancedevents/member/cancel/";
                    menuName = "cancel_invite";
                    dialogueMessage = getResources().getString(R.string.event_cancel_request_dialogue_message);
                    dialogueTitle = getResources().getString(R.string.event_cancel_request_dialogue_title);
                    dialogueButton = getResources().getString(R.string.event_cancel_request_dialogue_button);
                    successMessage = getResources().getString(R.string.event_cancel_request_dialogue_success_message);
                    break;

                case ConstantVariables.JOIN_EVENT:
                    mJoinButton.setText(getResources().getString(R.string.join_event_text));

                    mRequestUrl = AppConstant.DEFAULT_URL + "advancedevents/member/join/";
                    menuName = "join";
                    dialogueMessage = getResources().getString(R.string.join_event_dialogue_message);
                    dialogueTitle = getResources().getString(R.string.join_event_dialogue_title);
                    dialogueButton = getResources().getString(R.string.join_event_dialogue_title);
                    successMessage = getResources().getString(R.string.join_event_dialogue_success_message);
                    break;

                case ConstantVariables.LEAVE_EVENT:
                    mEventRsvpForm.setVisibility(View.VISIBLE);
                    mEventJoinForm.setVisibility(View.GONE);
                    mJoinButton.setVisibility(View.GONE);

                    mRequestUrl = AppConstant.DEFAULT_URL + "advancedevents/member/leave/";
                    menuName = "leave";
                    setRsvpFormView(AdvEventsProfilePage.sSelectedRsvpValue);
                    break;

                case ConstantVariables.EVENT_WAIT_LIST:
                    mJoinButton.setText(getResources().getString(R.string.add_me_to_waitlist));

                    mRequestUrl = AppConstant.DEFAULT_URL + "advancedevents/waitlist/join";
                    menuName = "join_waitlist";
                    postParams.clear();
                    postParams.put("occurrence_id", String.valueOf(mOccurrenceId));
                    postParams.put("event_id", String.valueOf(mEventId));
                    dialogueMessage = getResources().getString(R.string.add_to_waitlist_member_dialogue_message);
                    dialogueTitle = getResources().getString(R.string.add_to_waitlist_member_dialogue_title);
                    dialogueButton = getResources().getString(R.string.add_to_waitlist_member_dialogue_button);
                    successMessage = getResources().getString
                            (R.string.add_to_waitlist_member_dialogue_success_message);
                    break;

                case ConstantVariables.BOOK_EVENT_TICKETS:
                    mJoinButton.setText(getResources().getString(R.string.book_now_button_text));

                    mRequestUrl = AppConstant.DEFAULT_URL + "advancedeventtickets/tickets/tickets-buy";
                    postParams.clear();
                    postParams.put("event_id", String.valueOf(mEventId));
                    mRequestUrl = mAppConst.buildQueryString(mRequestUrl, postParams);
                    break;
            }

            if (AdvEventsProfilePage.sMembershipRequestCode != ConstantVariables.BOOK_EVENT_TICKETS)
                mRequestUrl += mEventId;

        } else {
            mEventRsvpForm.setVisibility(View.GONE);
            mEventJoinForm.setVisibility(View.GONE);
            mJoinButton.setVisibility(View.GONE);
            mRootView.findViewById(R.id.rsvp_form_divider).setVisibility(View.GONE);
        }

    }

    private void setRsvpFormView(int rsvpValue) {

        switch (rsvpValue){
            case 0:
                mSelectedRSVP = 0;
                mNotAttendingButton = setButtonColorBlue(mNotAttendingButton);
                mMayBeAttendingButton = setButtonColorWhite(mMayBeAttendingButton);
                mAttendingButton = setButtonColorWhite(mAttendingButton);

                mAttendingButton.setClickable(true);
                mMayBeAttendingButton.setClickable(true);
                mNotAttendingButton.setClickable(false);
                break;
            case 1:
                mSelectedRSVP = 1;
                mMayBeAttendingButton = setButtonColorBlue(mMayBeAttendingButton);
                mNotAttendingButton = setButtonColorWhite(mNotAttendingButton);
                mAttendingButton = setButtonColorWhite(mAttendingButton);

                mAttendingButton.setClickable(true);
                mMayBeAttendingButton.setClickable(false);
                mNotAttendingButton.setClickable(true);
                break;
            case 2:
                mSelectedRSVP = 2;
                mNotAttendingButton = setButtonColorWhite(mNotAttendingButton);
                mMayBeAttendingButton = setButtonColorWhite(mMayBeAttendingButton);
                mAttendingButton = setButtonColorBlue(mAttendingButton);

                mAttendingButton.setClickable(false);
                mMayBeAttendingButton.setClickable(true);
                mNotAttendingButton.setClickable(true);
                break;
        }


    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        String clickedButton;

        switch (id){

            case R.id.notAttendingButton:
                mNotAttendingButton.setText("\uf110");
                clickedButton = "notAttendingButton";
                changeEventRsvp(0, clickedButton);
                break;
            case R.id.mayBeAttendingButton:
                mMayBeAttendingButton.setText("\uf110");
                clickedButton = "mayBeAttendingButton";
                changeEventRsvp(1, clickedButton);
                break;
            case R.id.attendingButton:
                clickedButton = "attendingButton";
                mAttendingButton.setText("\uf110");
                changeEventRsvp(2, clickedButton);
                break;

            case R.id.joinEventButton:
                if (AdvEventsProfilePage.sMembershipRequestCode == ConstantVariables.JOIN_EVENT) {
                    joinEntry();
                } else if (AdvEventsProfilePage.sMembershipRequestCode == ConstantVariables.BOOK_EVENT_TICKETS) {
                    Intent buyIntent = new Intent(mContext, AdvEventsBuyTicketsInfo.class);
                    buyIntent.putExtra(ConstantVariables.URL_STRING, mRequestUrl);
                    buyIntent.putExtra(ConstantVariables.TITLE, title);
                    buyIntent.putExtra("occurrence_id", String.valueOf(mOccurrenceId));
                    buyIntent.putExtra("location", location);
                    buyIntent.putExtra("starttime", startTime);
                    buyIntent.putExtra("endtime", endTime);
                    startActivityForResult(buyIntent, ConstantVariables.CREATE_REQUEST_CODE);
                } else {
                    performAction();
                }
                break;

            case R.id.addToDiary:
                String url = AppConstant.DEFAULT_URL + "advancedevents/diaries/add?event_id=" + mEventId;
                Intent addToDiary = new Intent(mContext, CreateNewEntry.class);
                addToDiary.putExtra(ConstantVariables.CREATE_URL, url);
                addToDiary.putExtra(ConstantVariables.FORM_TYPE, "add_to_diary");
                addToDiary.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                startActivityForResult(addToDiary, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.writeReview:
                String reviewUrl;
                if (isWriteReview) {
                    reviewUrl = AppConstant.DEFAULT_URL + "advancedevents/review/create/" + mEventId;
                    Intent reviewIntent = new Intent(mContext, CreateNewEntry.class);
                    reviewIntent.putExtra(ConstantVariables.CREATE_URL, reviewUrl);
                    reviewIntent.putExtra(ConstantVariables.FORM_TYPE, "create_review");
                    reviewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                    startActivityForResult(reviewIntent, ConstantVariables.CREATE_REQUEST_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent updateReviewIntent = new Intent(mContext, EditEntry.class);
                    updateReviewIntent.putExtra(ConstantVariables.URL_STRING, updateReviewUrl);
                    updateReviewIntent.putExtra(ConstantVariables.FORM_TYPE, "update_review");
                    updateReviewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                    startActivityForResult(updateReviewIntent, ConstantVariables.PAGE_EDIT_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                break;

            case R.id.host_info:
                Intent intent = new Intent(mContext, userProfile.class);
                if (mHostType.equals("user")) {
                    intent.putExtra(ConstantVariables.PROFILE_TYPE, "user_profile");
                } else if (mHostType.equals("siteevent_organizer")) {
                    intent.putExtra(ConstantVariables.PROFILE_TYPE, "organizer_profile");
                }
                intent.putExtra(ConstantVariables.USER_ID,  mHostId);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.addToCalendar:
                Calendar cal = Calendar.getInstance();
                Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
                calendarIntent.setType("vnd.android.cursor.item/event");
                calendarIntent.putExtra("beginTime", startTime);
                calendarIntent.putExtra("rrule", "FREQ=WEEKLY");
                calendarIntent.putExtra("endTime", endTime);
                calendarIntent.putExtra("title", title);
                calendarIntent.putExtra("description", "Hello");
                startActivity(calendarIntent);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.more:
                String redirectUrl = AppConstant.DEFAULT_URL + "advancedevents/announcement/" + mEventId;
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "announcement");
                bundle.putString(ConstantVariables.URL_STRING, redirectUrl);
                bundle.putString(ConstantVariables.CONTENT_TITLE, getResources().getString(R.string.announcement_title));
                bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, mAnnouncementCount);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, "core_main_siteevent");
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                mContext.startActivity(newIntent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            default:
                break;
        }

    }

    public void changeEventRsvp(int rsvpValue, String ClickedButton) {

        Map<String, String> postParam = new HashMap<>();
        mClickedButton = ClickedButton;
        mAttendingButton.setClickable(false);
        mMayBeAttendingButton.setClickable(false);
        mNotAttendingButton.setClickable(false);
        postParam.put("rsvp", String.valueOf(rsvpValue));

        mAppConst.postJsonResponseForUrl(mItemViewUrl, postParam, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                switch (mClickedButton) {
                    case "attendingButton":
                        mSelectedRSVP = 2;
                        mAttendingButton.setText(getResources().getString(R.string.rsvp_yes));
                        mNotAttendingButton = setButtonColorWhite(mNotAttendingButton);
                        mMayBeAttendingButton = setButtonColorWhite(mMayBeAttendingButton);
                        mAttendingButton = setButtonColorBlue(mAttendingButton);
                        mAttendingButton.setClickable(false);
                        //  continuously e
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(true);
                        break;
                    case "mayBeAttendingButton":
                        mSelectedRSVP = 1;
                        mMayBeAttendingButton.setText(getResources().getString(R.string.rsvp_maybe));
                        mNotAttendingButton = setButtonColorWhite(mNotAttendingButton);
                        mMayBeAttendingButton = setButtonColorBlue(mMayBeAttendingButton);
                        mAttendingButton = setButtonColorWhite(mAttendingButton);
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(false);
                        mNotAttendingButton.setClickable(true);
                        break;
                    case "notAttendingButton":
                        mSelectedRSVP = 0;
                        mNotAttendingButton.setText(getResources().getString(R.string.rsvp_no));
                        mNotAttendingButton = setButtonColorBlue(mNotAttendingButton);
                        mMayBeAttendingButton = setButtonColorWhite(mMayBeAttendingButton);
                        mAttendingButton = setButtonColorWhite(mAttendingButton);
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(false);
                        break;
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mRootView, message);

                switch (mSelectedRSVP) {
                    case 0:
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(false);
                        mNotAttendingButton.setText(getResources().getString(R.string.rsvp_no));
                        break;
                    case 1:
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(false);
                        mNotAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setText(getResources().getString(R.string.rsvp_maybe));
                        break;
                    case 2:
                        mAttendingButton.setClickable(false);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(true);
                        mAttendingButton.setText(getResources().getString(R.string.rsvp_yes));
                        break;
                }
            }
        });
    }

    public void joinEntry() {

        AdvEventsProfilePage.sSelectedRsvpValue = 0;

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setTitle(dialogueTitle);

        alertBuilder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which] == AdvEventsProfilePage.sNotAttending) {
                    AdvEventsProfilePage.sSelectedRsvpValue = 0;
                } else if (items[which] == AdvEventsProfilePage.sMayBeAttending) {
                    AdvEventsProfilePage.sSelectedRsvpValue = 1;
                } else if (items[which] == AdvEventsProfilePage.sAttending) {
                    AdvEventsProfilePage.sSelectedRsvpValue = 2;
                }
            }
        });

        alertBuilder.setPositiveButton(dialogueButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mAppConst.showProgressDialog();
                postParams.clear();
                postParams.put("rsvp", String.valueOf(AdvEventsProfilePage.sSelectedRsvpValue));

                mAppConst.postJsonResponseForUrl(mRequestUrl, postParams, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {

                        mAppConst.hideProgressDialog();
                        /* Show Message */
                        SnackbarUtils.displaySnackbar(mRootView, successMessage);

                        mEventRsvpForm.setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.rsvp_form_divider).setVisibility(View.VISIBLE);
                        mEventJoinForm.setVisibility(View.GONE);
                        setRsvpFormView(AdvEventsProfilePage.sSelectedRsvpValue);
                        AdvEventsProfilePage.sMembershipRequestCode = 4;
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        mAppConst.hideProgressDialog();
                        SnackbarUtils.displaySnackbar(mRootView, message);
                    }
                });
            }
        });

        alertBuilder.setNegativeButton(getResources().getString(R.string.cancel_dialogue_message),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }

    /*
    Code For Performing Action According to Selected MenuItem
     */

    public void performAction(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(dialogueMessage);
        alertBuilder.setTitle(dialogueTitle);

        alertBuilder.setPositiveButton(dialogueButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                    mAppConst.postJsonResponseForUrl(mRequestUrl, postParams, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {

                            mAppConst.hideProgressDialog();
                            SnackbarUtils.displaySnackbar(mRootView, successMessage);
                            switch (menuName) {

                                case "request_invite":
                                case "cancel_invite":
                                    AdvEventsProfilePage.sMembershipRequestCode =
                                            (AdvEventsProfilePage.sMembershipRequestCode == 1 ? 2 : 1);
                                    setMemberShipValues();
                                    break;

                                case "leave":
                                case "join":
                                    AdvEventsProfilePage.sMembershipRequestCode =
                                            (AdvEventsProfilePage.sMembershipRequestCode == 3 ? 4 : 3);
                                    setMemberShipValues();
                                    break;

                                default:
                                    getActivity().finish();
                                    startActivity( getActivity().getIntent());
                                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    break;
                            }
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            mAppConst.hideProgressDialog();
                            SnackbarUtils.displaySnackbar(mRootView, message);
                        }
                    });

            }
        });

        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel_dialogue_message),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }


    public Button setButtonColorWhite(Button buttonColorWhite) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(mContext, R.color.white));
        gd.setCornerRadius(3);
        gd.setStroke(4, ContextCompat.getColor(mContext, R.color.themeButtonColor));
        buttonColorWhite.setBackground(gd);
        buttonColorWhite.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

        return buttonColorWhite;
    }

    public Button setButtonColorBlue(Button buttonColorBlue) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
        gd.setCornerRadius(3);
        buttonColorBlue.setBackground(gd);
        buttonColorBlue.setTextColor(ContextCompat.getColor(mContext, R.color.white));

        return buttonColorBlue;
    }
}
