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

package com.fellopages.mobileapp.classes.common.formgenerator;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * FormPicker is used to inflate the fields for the attachment picker (Music, Photo, Video etc.),
 * Date/Time picker and Rating bar.
 */

public class FormPicker extends FormWidget implements View.OnClickListener,
        RatingBar.OnRatingBarChangeListener {

    // Member Variables.
    private Context mContext;
    private View mConfigFieldView;
    private EditText etFieldValue;
    private TextView tvLabel, tvError;
    private RatingBar mRatingBar;
    private int selectedMode;
    private String mCurrentSelectedOption, mLabel, mUploadingOption, mFieldName, mDateType;
    private boolean mIsCreateForm, mIsDatePicker = false, mIsRatingBarField = false,
            mIsAttachmentPicker = false, mIsDateSet = false;
    private Drawable mDrawableIcon;
    private CreateNewEntry createNewEntry;
    private EditEntry editEntry;
    private static long mMinDate = 0L;


    /**
     * Public constructor to inflate form field for date/time picker.
     *
     * @param context      Context of calling class.
     * @param name         Property of the field.
     * @param hasValidator True if the field has validation (Compulsory field).
     * @param type         Type of the date picker (date picker or date+time picker).
     */
    public FormPicker(Context context, String name, boolean hasValidator, String type) {

        super(context, name, hasValidator);

        // Initializing member variables.
        mContext = context;
        mFieldName = name;
        mDateType = type;
        createNewEntry = new CreateNewEntry();
        mIsDatePicker = true;

        // Inflate the field view layout.
        inflateView();
    }

    /**
     * Public constructor to inflate form field For the RatingBar.
     *
     * @param context      Context of calling class.
     * @param property     Property of the field.
     * @param label        Label of the field.
     * @param hasValidator True if the field has validation (Compulsory field).
     * @param isRatingBar  True if the field is inflated for a rating bar.
     */
    public FormPicker(Context context, String property, String label, boolean hasValidator,
                      boolean isRatingBar) {
        super(context, property, hasValidator);

        // Initializing member variables.
        mContext = context;
        mFieldName = property;
        mLabel = label;
        mIsRatingBarField = isRatingBar;

        // Inflate the field view layout.
        inflateView();
    }

    /**
     * Public constructor to inflate form field for attachment(music, photo, video etc.) picker.
     *
     * @param context               Context of calling class.
     * @param name                  Property of the field.
     * @param currentSelectedOption Current selected module.
     * @param createForm            True if the form is loaded for creation.
     */
    public FormPicker(Context context, final String name, String currentSelectedOption, boolean createForm) {
        super(context, name, false);

        // Initializing member variables.
        mContext = context;
        mFieldName = name;
        mIsCreateForm = createForm;
        mCurrentSelectedOption = currentSelectedOption;
        createNewEntry = CreateNewEntry.getInstance();
        editEntry = EditEntry.getInstance();
        mIsAttachmentPicker = true;

        // Inflate the field view layout.
        inflateView();
    }

    /**
     * Method to inflate view according to field type.
     */
    private void inflateView() {

        // Inflate the field view layout.
        mConfigFieldView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_form_select_option, null);
        if (mIsDatePicker) {
            mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_date_range_black_24dp);
        } else if (mIsAttachmentPicker) {
            checkModuleConditionsForAttachmentPicker();
        }
        getViews();
        mConfigFieldView.setTag(mFieldName);
        _layout.addView(mConfigFieldView);
    }

    /**
     * Method to check module specific conditions. like uploading option, label, icon etc.
     */
    private void checkModuleConditionsForAttachmentPicker() {
        switch (mCurrentSelectedOption) {
            case ConstantVariables.MUSIC_MENU_TITLE:
                if (mFieldName.equals("songs")) {
                    mUploadingOption = "music";
                    mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp);
                    mLabel = mContext.getResources().getString(R.string.select_music_file);
                } else {
                    mUploadingOption = "photo";
                    mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_24dp);
                    mLabel = mContext.getResources().getString(R.string.select_single_photo);
                }
                break;

            case ConstantVariables.CLASSIFIED_MENU_TITLE:
            case ConstantVariables.EVENT_MENU_TITLE:
            case ConstantVariables.GROUP_MENU_TITLE:
            case ConstantVariables.FORUM_MENU_TITLE:
                mUploadingOption = "photo";
                mLabel = mContext.getResources().getString(R.string.select_single_photo);
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_24dp);
                break;

            case ConstantVariables.VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_VIDEO_MENU_TITLE:
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_ADD_VIDEO:
            case ConstantVariables.SITE_PAGE_ADD_VIDEO:
            case ConstantVariables.SITE_STORE_ADD_VIDEO:
            case ConstantVariables.ADV_GROUPS_ADD_VIDEO:
                mUploadingOption = "video";
                mLabel = mContext.getResources().getString(R.string.select_video_btn);
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_videocam_white_24dp);
                break;

            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
            case ConstantVariables.MLT_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                mUploadingOption = "photo";
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_24dp);
                if (mFieldName.equals("host_photo")) {
                    mLabel = mContext.getResources().getString(R.string.host_photo_text);
                } else if (mCurrentSelectedOption.equals(ConstantVariables.MLT_MENU_TITLE)
                        && mFieldName.equals("filename")) {
                    mUploadingOption = "file";
                    mLabel = mContext.getResources().getString(R.string.upload_file);
                    mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_file_upload_white);
                } else {
                    mLabel = mContext.getResources().getString(R.string.select_main_photo);
                }
                break;

            case ConstantVariables.ALBUM_MENU_TITLE:
            case "sellingForm":
                mUploadingOption = "photo";
                mLabel = mContext.getResources().getString(R.string.go_select);
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_24dp);
                selectedMode = MultiMediaSelectorActivity.MODE_MULTI;
                break;
            case "main_file":
            case "sample_file":
                mUploadingOption = "upload_product";
                mLabel = mContext.getResources().getString(R.string.upload_file);
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_file_upload_white);
                break;

            default:
                mUploadingOption = "photo";
                mLabel = mContext.getResources().getString(R.string.select_main_photo);
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_24dp);
                selectedMode = MultiMediaSelectorActivity.MODE_SINGLE;
                break;

        }
    }

    /**
     * Method to get views from the form layout and set data in views..
     */
    private void getViews() {

        // Getting label, description and field value views.
        tvLabel = mConfigFieldView.findViewById(R.id.view_label);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        tvLabel.setText(mLabel != null ? mLabel : getDisplayText());
        TextView tvDescription = mConfigFieldView.findViewById(R.id.view_description);
        etFieldValue = mConfigFieldView.findViewById(R.id.field_value);
        tvError = mConfigFieldView.findViewById(R.id.error_view);
        mRatingBar = mConfigFieldView.findViewById(R.id.ratingBar);

        // Checking for the inflated field type.
        if (mIsRatingBarField) {

            // Showing the rating bar view when the view is inflated for the rating bar.
            etFieldValue.setVisibility(View.GONE);
            mRatingBar.setVisibility(View.VISIBLE);
            mRatingBar.setNumStars(5);
            mRatingBar.setIsIndicator(false);
            LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                    PorterDuff.Mode.SRC_ATOP);
            mRatingBar.setOnRatingBarChangeListener(this);

        } else {

            // Showing the attachment picker/date picker options.
            etFieldValue.setVisibility(View.VISIBLE);
            mRatingBar.setVisibility(View.GONE);

            // Showing the right drawable icon on the field value view.
            if (mDrawableIcon != null) {
                mDrawableIcon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.light_gray),
                        PorterDuff.Mode.SRC_ATOP));
                etFieldValue.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawableIcon, null);
                etFieldValue.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp));
            }

            //TODO, Uncomment this when ever the description is needed.
            // Showing description for the music picker.
//            if (mUploadingOption != null && !mUploadingOption.isEmpty() && mUploadingOption.equals("music")) {
//                tvDescription.setVisibility(View.VISIBLE);
//                tvDescription.setText(mContext.getResources().getString(R.string.music_limit_description));
//                tvDescription.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
//            } else {
//                tvDescription.setVisibility(View.GONE);
//            }

            // Setting up click listener on form view.
            etFieldValue.setOnClickListener(this);
            mConfigFieldView.findViewById(R.id.form_main_view).setOnClickListener(this);
        }

    }

    /**
     * Method to set Minimum date from the selected start date.
     * @param startDate Start date.
     */
    private void setMinDate(String startDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(startDate);
            mMinDate = date.getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setHint(String value) {

        // Showing the hint as a label.
        if (mIsDatePicker && value != null && !value.isEmpty()) {
            tvLabel.setText(value);
            if (!mFieldName.equals("schedule_time")) {
                etFieldValue.setHint(mContext.getResources().getString(R.string.select_text) + " " + value);
            } else {
                etFieldValue.setHint(value);
            }
        }
    }

    @Override
    public String getValue() {

        // Returning field value according to the inflated view type.
        if (mIsDatePicker) {
            return etFieldValue.getText().toString();
        } else if (mIsRatingBarField) {
            return String.valueOf(mRatingBar.getRating());
        } else {
            return "";
        }
    }

    @Override
    public void setValue(String value) {

        // Showing the field value according to inflated view type when it is coming in response.
        if (mIsDatePicker && etFieldValue != null && value != null) {
            etFieldValue.setText(value);
        } else if (mRatingBar != null && value != null && !value.isEmpty()) {
            mRatingBar.setRating(Float.valueOf(value));
        } else if (mCurrentSelectedOption.equals("sellingForm") && editEntry != null) {
            editEntry.showSelectedImages(mContext, mFieldName, mConfigFieldView);
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {

        // Showing error message on error view.
        if (tvError != null && errorMessage != null) {
            tvError.setVisibility(View.VISIBLE);
            tvError.requestFocus();
            tvError.setError(errorMessage);
        }
    }

    @Override
    public void onClick(View v) {

        // Hiding error view when the attachment option is clicked.
        tvError.setError(null);
        tvError.setVisibility(View.GONE);

        // Perform action on onClick according to inflated view type.
        if (mIsDatePicker) {
            View startTimeConfigView = FormActivity._layout.findViewWithTag("starttime");
            if (mFieldName != null && mFieldName.equals("scheduleForm")) {
                startTimeConfigView = FormActivity._layout.findViewWithTag("schedule_time");
            }
            View endTimeConfigView = FormActivity._layout.findViewWithTag("endtime");
            EditText etStartTime = null, etEndTime = null;
            if (startTimeConfigView != null && endTimeConfigView != null) {
                etStartTime = startTimeConfigView.findViewById(R.id.field_value);
                etEndTime = endTimeConfigView.findViewById(R.id.field_value);
            }

            // When the form is loaded from edit form then setting the min date with the start date.
            if (!mIsCreateForm && !mIsDateSet && etStartTime != null && etStartTime.getText() != null
                    && !etStartTime.getText().toString().trim().isEmpty()) {
                setMinDate(etStartTime.getText().toString());
                mIsDateSet = true;
            }
            switch (mFieldName) {
                case "starttime":
                case "schedule_time":
                    createNewEntry.showDateTimeDialogue(mContext, etFieldValue, mDateType, System.currentTimeMillis() - 1000);
                    final EditText finalEtEndTime = etEndTime;
                    createNewEntry.setOnDateSelectedListener(new CreateNewEntry.OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String date) {
                            setMinDate(date);
                            /// When the user select the start date greater than the end date
                            // then resetting the end date with the start date.
                            if (finalEtEndTime != null && finalEtEndTime.getText() != null
                                    && finalEtEndTime.getText().toString().trim().length() > 0
                                    && GlobalFunctions.minutesDifferenceFromEndDate(date, finalEtEndTime.getText().toString()) < 0) {
                                finalEtEndTime.setText(date);
                            }
                        }
                    });
                    break;
                case "endtime":
                    if (etStartTime != null && etStartTime.getText().length() > 0) {
                        createNewEntry.showDateTimeDialogue(mContext, etFieldValue, mDateType, mMinDate);
                    } else {
                        SnackbarUtils.displaySnackbar(mConfigFieldView, mContext.getResources().getString(R.string.select_start_time_first));
                    }
                    break;
                default:
                    createNewEntry.showDateTimeDialogue(mContext, etFieldValue, mDateType, 0L);
                    break;
            }
        } else if (mIsCreateForm) {
            createNewEntry.checkPermission(mContext, selectedMode, true, mUploadingOption, mFieldName, mConfigFieldView);
        } else {
            editEntry.checkPermission(mContext, selectedMode, true, mUploadingOption, mFieldName, mConfigFieldView);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        // Hiding error view when the rating is changed.
        if (tvError != null) {
            tvError.setError(null);
            tvError.setVisibility(View.GONE);
        }
    }

}
