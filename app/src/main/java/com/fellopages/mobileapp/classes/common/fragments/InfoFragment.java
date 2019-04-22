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

package com.fellopages.mobileapp.classes.common.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.activities.MapActivity;
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment implements View.OnClickListener{

    private JSONObject mResponseJsonObject;
    private View mRootView;
    private WebView mViewDescription, mViewBody;
    private BezelImageView mOwnerImage;
    private TextView mOwnerTitle, mLocationLabel, mDateLabel;
    private TextView mCategoryIcon, mMemberCountIcon, mViewCountIcon, mCreationDateIcon,
            mModificationDateIcon, mEventLocation;
    private SelectableTextView mCategoryTitle, mMemberCount, mViewCount, mCreationDate, mLastUpdated,
            mEventDate, mEventDateTime, mDescription, mCategoryLabel, mMemberCountLabel, mViewCountLabel,
            mCreationDateLabel, mModificationDateLabel;
    private String ownerImageIcon, ownerTitle, categoryTitle, creationDate, modifiedDate, description, body;
    private int memberCount, viewCount;
    private Typeface fontIcon;
    private String mCurrentSelectedModule;
    private Context mContext;
    private LinearLayout mCategoryInfoLayout, mOwnerDetailView, mLocationDateLayout;
    private Button mAttendingButton, mMayBeAttendingButton, mNotAttendingButton;
    private int mProfileRsvpValue, mSelectedRSVP, mCategoryId, mUserId;
    private boolean showRsvp = false;
    private String mClickedButton, mItemViewUrl;
    private AppConstant mAppConst;
    private String location;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;
    private View mOverviewSeperaterView;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mRootView = inflater.inflate(R.layout.fragment_info, container, false);
        mContext = getContext();

        mAppConst = new AppConstant(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(mContext);

        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);

        //Fetch Current Selected Module

        mCurrentSelectedModule = getArguments().getString(ConstantVariables.EXTRA_MODULE_TYPE);
        mLocationDateLayout = mRootView.findViewById(R.id.location_date_layout);
        mLocationDateLayout.setVisibility(View.GONE);
        mOwnerDetailView = mRootView.findViewById(R.id.ownerDetailView);
        mOwnerImage = mRootView.findViewById(R.id.owner_image);
        mOwnerTitle = mRootView.findViewById(R.id.owner_title);
        mOwnerImage.setOnClickListener(this);
        mOwnerTitle.setOnClickListener(this);

        mCategoryLabel = mRootView.findViewById(R.id.category_label);
        mCategoryTitle = mRootView.findViewById(R.id.categoryTitle);
        mMemberCount = mRootView.findViewById(R.id.memberCount);
        mMemberCountLabel = mRootView.findViewById(R.id.member_count_label);
        mViewCount = mRootView.findViewById(R.id.viewCount);
        mViewCountLabel = mRootView.findViewById(R.id.view_count_label);

        mModificationDateLabel = mRootView.findViewById(R.id.modification_date_label);
        mCreationDateLabel = mRootView.findViewById(R.id.creation_date_label);
        mCreationDate = mRootView.findViewById(R.id.creationDate);
        mLastUpdated = mRootView.findViewById(R.id.lastUpdated);
        mCategoryIcon = mRootView.findViewById(R.id.category_icon);
        mMemberCountIcon = mRootView.findViewById(R.id.member_count_icon);
        mViewCountIcon = mRootView.findViewById(R.id.view_count_icon);
        mCreationDateIcon = mRootView.findViewById(R.id.creation_date_icon);
        mModificationDateIcon = mRootView.findViewById(R.id.modification_date_icon);

        mDescription = mRootView.findViewById(R.id.description);

        mDateLabel = mRootView.findViewById(R.id.dateLabel);
        mEventDate = mRootView.findViewById(R.id.eventStartDate);
        mEventDateTime = mRootView.findViewById(R.id.eventEndDateTime);
        mEventLocation = mRootView.findViewById(R.id.eventLocation);
        mLocationLabel = mRootView.findViewById(R.id.locationLabel);

        mAttendingButton = mRootView.findViewById(R.id.attendingButton);
        mMayBeAttendingButton = mRootView.findViewById(R.id.mayBeAttendingButton);
        mNotAttendingButton = mRootView.findViewById(R.id.notAttendingButton);

        mCategoryTitle.setOnClickListener(this);
        mAttendingButton.setOnClickListener(this);
        mMayBeAttendingButton.setOnClickListener(this);
        mNotAttendingButton.setOnClickListener(this);
        mAttendingButton.setTypeface(fontIcon);
        mMayBeAttendingButton.setTypeface(fontIcon);
        mNotAttendingButton.setTypeface(fontIcon);

        mCategoryInfoLayout = mRootView.findViewById(R.id.categoryInfo);
        updateData(bundle);

        return mRootView;
    }

    public void updateData(Bundle bundle) {

        try {
            mResponseJsonObject = new JSONObject(bundle.getString(ConstantVariables.RESPONSE_OBJECT));

            showRsvp = bundle.getBoolean("showRsvp");
            mProfileRsvpValue = bundle.getInt(ConstantVariables.PROFILE_RSVP_VALUE);

            mItemViewUrl = bundle.getString(ConstantVariables.VIEW_PAGE_URL);

            mUserId = mResponseJsonObject.optInt("user_id");
            ownerImageIcon = mResponseJsonObject.optString("owner_image_profile");
            ownerTitle = mResponseJsonObject.optString("owner_title");
            mCategoryId = mResponseJsonObject.optInt("category_id");
            categoryTitle = mResponseJsonObject.optString("category_title");
            memberCount = mResponseJsonObject.optInt("member_count");
            viewCount = mResponseJsonObject.optInt("view_count");
            creationDate = mResponseJsonObject.optString("creation_date");
            modifiedDate = mResponseJsonObject.optString("modified_date");
            description = mResponseJsonObject.optString("description");

            // If current module is sitereview listing then showing overview.
            if (mCurrentSelectedModule != null && mCurrentSelectedModule.equals("sitereview_listing")) {

                mViewBody = mRootView.findViewById(R.id.view_body);
                mViewDescription = mRootView.findViewById(R.id.view_overview);
                mOverviewSeperaterView = mRootView.findViewById(R.id.mOverviewSeperaterView);

                GlobalFunctions.setWebSettings(mViewBody, false);
                GlobalFunctions.setWebSettings(mViewDescription, false);

                mOwnerDetailView.setVisibility(View.GONE);
                body = mResponseJsonObject.optString("body");
                description = mResponseJsonObject.optString("overview");

                //Showing body block if there is any text in body.
                if (body != null && !body.isEmpty()) {
                    mViewBody.setVisibility(View.VISIBLE);

                /* Setting Body in TextView */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mViewBody.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                    } else {
                        mViewBody.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                    }
                    mViewBody.loadDataWithBaseURL("file:///android_asset/",
                            GlobalFunctions.getHtmlData(mContext, body, true), "text/html", "utf-8", null);
                } else {
                    mViewBody.setVisibility(View.GONE);
                }

                //Showing description block if there is any text in body.
                if (description != null && !description.isEmpty()) {
                    mOverviewSeperaterView.setVisibility(View.VISIBLE);
                    mViewDescription.setVisibility(View.VISIBLE);

                        /* Setting Body in TextView */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mViewDescription.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                    } else {
                        mViewDescription.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                    }
                    mViewDescription.loadDataWithBaseURL("file:///android_asset/",
                            GlobalFunctions.getHtmlData(mContext, description, true), "text/html", "utf-8", null);
                } else {
                    mViewDescription.setVisibility(View.GONE);
                    mOverviewSeperaterView.setVisibility(View.GONE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mImageLoader.setImageUrl(ownerImageIcon, mOwnerImage);
        mOwnerTitle.setText(ownerTitle);

        if(categoryTitle != null && !categoryTitle.isEmpty()) {
            mCategoryTitle.setText(categoryTitle);
            mCategoryLabel.setText(mContext.getResources().getString(R.string.category_label) + ":");
            mCategoryIcon.setTypeface(fontIcon);
            mCategoryIcon.setText("\uf097");
        } else {
            mCategoryInfoLayout.setVisibility(View.GONE);
        }

        if(mCurrentSelectedModule.equals(ConstantVariables.GROUP_MENU_TITLE)){

            mRootView.findViewById(R.id.memberCountInfo).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.viewCountInfo).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.creationDateInfo).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.modificationDateInfo).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.event_above_horizontal_line).setVisibility(View.GONE);
            mRootView.findViewById(R.id.event_below_horizontal_line).setVisibility(View.GONE);

            mMemberCountIcon.setTypeface(fontIcon);
            mViewCountIcon.setTypeface(fontIcon);
            mCreationDateIcon.setTypeface(fontIcon);
            mModificationDateIcon.setTypeface(fontIcon);
            mMemberCountIcon.setText("\uf007");
            mViewCountIcon.setText("\uf06e");
            mCreationDateIcon.setText("\uf017");
            mModificationDateIcon.setText("\uf017");
            mModificationDateLabel.setText(mContext.getResources().getString(R.string.modification_date_label) + ":");
            mMemberCount.setText("" + memberCount);
            mMemberCountLabel.setText(mContext.getResources().getString(R.string.member_count_label) + ":");
            mViewCount.setText("" + viewCount);
            mViewCountLabel.setText(mContext.getResources().getString(R.string.view_count_label) + ":");

            String creationDateString = getConvertedDate(creationDate);
            String modificationDateString = getConvertedDate(modifiedDate);

            mCreationDate.setText(creationDateString);
            mCreationDateLabel.setText(mContext.getResources().getString(R.string.creation_date_label) + ":");
            mLastUpdated.setText(modificationDateString);

        }else if (mCurrentSelectedModule.equals(ConstantVariables.EVENT_MENU_TITLE)) {

            mRootView.findViewById(R.id.eventDatesInfo).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.event_above_horizontal_line).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.event_below_horizontal_line).setVisibility(View.VISIBLE);
            String startTime = mResponseJsonObject.optString("starttime");
            String endTime = mResponseJsonObject.optString("endtime");
            location = mResponseJsonObject.optString("location");

            String startDate = getConvertedDate(startTime);
            String endDate = getConvertedDate(endTime);

            mEventDate.setText(startDate);
            mEventDateTime.setText(endDate);
            mDateLabel.setTypeface(fontIcon);
            mDateLabel.setText("\uf017 " + mContext.getResources().getString(R.string.date_label) + ":");

            if (location != null && !location.isEmpty()) {
                mLocationDateLayout.setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.eventLocationInfo).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.event_vertical_line).setVisibility(View.VISIBLE);

                mLocationLabel.setTypeface(fontIcon);
                mLocationLabel.setText("\uF041 " + mContext.getResources().getString(R.string.location_label) + ":");
                mEventLocation.setMovementMethod(LinkMovementMethod.getInstance());
                mEventLocation.setText(location);
                mEventLocation.setOnClickListener(this);

            } else {
                mLocationDateLayout.setVisibility(View.GONE);
                mRootView.findViewById(R.id.eventLocationInfo).setVisibility(View.GONE);
                mRootView.findViewById(R.id.event_vertical_line).setVisibility(View.GONE);
                int padding = (int) getResources().getDimension(R.dimen.padding_10dp);
                LinearLayout.LayoutParams layoutParams = CustomViews.getFullWidthLayoutParams();
                layoutParams.setMargins(padding, 0, padding, 0);
                mRootView.findViewById(R.id.eventDatesInfo).setLayoutParams(layoutParams);
            }

            if (showRsvp) {
                mRootView.findViewById(R.id.eventRsvpForm).setVisibility(View.VISIBLE);
                switch (mProfileRsvpValue) {
                    case 0:
                        mSelectedRSVP = 0;
                        mNotAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_green));
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(false);
                        break;
                    case 1:
                        mSelectedRSVP = 1;
                        mMayBeAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_green));
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(false);
                        mNotAttendingButton.setClickable(true);
                        break;
                    case 2:
                        mSelectedRSVP = 2;
                        mAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_green));
                        mAttendingButton.setClickable(false);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(true);
                        break;
                }
            } else {
                mRootView.findViewById(R.id.eventRsvpForm).setVisibility(View.GONE);
                mAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                        R.drawable.buttons_background_blue));
                mMayBeAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                        R.drawable.buttons_background_blue));
                mNotAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                        R.drawable.buttons_background_blue));
            }
        }

        if (mCurrentSelectedModule != null && !mCurrentSelectedModule.equals(ConstantVariables.MLT_MENU_TITLE)) {
            mDescription.setText(Html.fromHtml(Html.fromHtml(description).toString()));
        } else {
            mDescription.setVisibility(View.GONE);
        }
    }

    public String getConvertedDate(String date){

        String dateHours = AppConstant.getHoursFromDate(date);

        String convertedDate = AppConstant.getMonthFromDate(date, "MMM") +  " " + AppConstant.getDayFromDate(date) +
                ", "  + AppConstant.getYearFormat(date) + " " + dateHours;

        return convertedDate;

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        int rsvpValue = 0;
        String clickedButton;

        switch (id){

            case R.id.notAttendingButton:
                mNotAttendingButton.setText("\uf110");
                clickedButton = "notAttendingButton";
                rsvpValue = 0;
                changeEventRsvp(rsvpValue, clickedButton);
                break;
            case R.id.mayBeAttendingButton:
                mMayBeAttendingButton.setText("\uf110");
                clickedButton = "mayBeAttendingButton";
                rsvpValue = 1;
                changeEventRsvp(rsvpValue, clickedButton);
                break;
            case R.id.attendingButton:
                mAttendingButton.setText("\uf110");
                clickedButton = "attendingButton";
                rsvpValue = 2;
                changeEventRsvp(rsvpValue, clickedButton);
                break;
            case R.id.categoryTitle:
                Intent intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.CATEGORY_ID,String.valueOf(mCategoryId));
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.CATEGORY_VALUE, categoryTitle);
                startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.owner_image:
            case R.id.ownerTitle:
                Intent userProfilentent = new Intent(mContext, userProfile.class);
                userProfilentent.putExtra(ConstantVariables.USER_ID, mUserId);
                userProfilentent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((AppCompatActivity)mContext).startActivityForResult(userProfilentent, ConstantVariables.USER_PROFILE_CODE);
                ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.eventLocation:
                if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            ConstantVariables.ACCESS_COARSE_LOCATION);
                }else{
                    openMapActivity();
                }
                break;

            default:
                break;
        }
    }

    public void changeEventRsvp(int rsvpValue, String ClickedButton){

        Map<String, String> postParam = new HashMap<>();
        mClickedButton = ClickedButton;
        postParam.put("rsvp", String.valueOf(rsvpValue));
        mAttendingButton.setClickable(false);
        mMayBeAttendingButton.setClickable(false);
        mNotAttendingButton.setClickable(false);

        mAppConst.postJsonResponseForUrl(mItemViewUrl, postParam, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                switch (mClickedButton){
                    case "attendingButton":

                        mSelectedRSVP = 2;
                        mAttendingButton.setText(getResources().getString(R.string.rsvp_yes));
                        mAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_green));
                        mMayBeAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_blue));
                        mNotAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_blue));
                        mAttendingButton.setClickable(false);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(true);
                    break;

                    case "mayBeAttendingButton":

                        mSelectedRSVP = 1;
                        mMayBeAttendingButton.setText(getResources().getString(R.string.rsvp_maybe));
                        mAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_blue));
                        mMayBeAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_green));
                        mNotAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_blue));
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(false);
                        mNotAttendingButton.setClickable(true);
                    break;

                    case "notAttendingButton":

                        mSelectedRSVP = 0;
                        mNotAttendingButton.setText(getResources().getString(R.string.rsvp_no));
                        mAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_blue));
                        mMayBeAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_blue));
                        mNotAttendingButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.buttons_background_green));
                        mAttendingButton.setClickable(true);
                        mMayBeAttendingButton.setClickable(true);
                        mNotAttendingButton.setClickable(false);
                    break;
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mRootView, message);

                if (mCurrentSelectedModule.equals("core_main_event")) {
                    switch (mSelectedRSVP){
                        case 0:
                            mAttendingButton.setClickable(true);
                            mMayBeAttendingButton.setClickable(true);
                            mNotAttendingButton.setClickable(false);
                            break;
                        case 1:
                            mAttendingButton.setClickable(true);
                            mMayBeAttendingButton.setClickable(false);
                            mNotAttendingButton.setClickable(true);
                            break;
                        case 2:
                            mAttendingButton.setClickable(false);
                            mMayBeAttendingButton.setClickable(true);
                            mNotAttendingButton.setClickable(true);
                            break;
                    }
                    if (mClickedButton.equals("attendingButton")) {
                        mAttendingButton.setText(getResources().getString(R.string.rsvp_yes));
                    }
                    if (mClickedButton.equals("mayBeAttendingButton")) {
                        mMayBeAttendingButton.setText(getResources().getString(R.string.rsvp_maybe));
                    }
                    if (mClickedButton.equals("notAttendingButton")) {
                        mNotAttendingButton.setText(getResources().getString(R.string.rsvp_no));
                    }
                }
            }
        });
    }

    private void openMapActivity(){
        Intent mapIntent = new Intent(mContext, MapActivity.class);
        mapIntent.putExtra("location", location);
        startActivity(mapIntent);
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.ACCESS_COARSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    openMapActivity();
                } else {
                    // If user deny the permission popup
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                                ConstantVariables.ACCESS_COARSE_LOCATION);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mRootView,
                                ConstantVariables.ACCESS_COARSE_LOCATION);

                    }
                }
                break;
        }
    }
}
