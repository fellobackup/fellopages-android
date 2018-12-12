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

package com.fellopages.mobileapp.classes.modules.user.profile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberInfoFragment extends Fragment {

    private String infoUrl;
    private View mRootView;
    private AppConstant mAppConst;
    private Context mContext;
    private JSONObject mBody;
    private LinearLayout mMemberInfoLayout, messageLayout;
    private LinearLayout.LayoutParams defaultParams;
    private boolean isVisibleToUser = false, mIsContactInfo = false;
    private int marginTop;
    private String infoType = "memberInfo";
    private TextView errorIcon;
    private SelectableTextView errorMessage;

    public MemberInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser && mContext != null) {
            makeRequest();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =  inflater.inflate(R.layout.fragment_member_info, container, false);
        mContext = getContext();
        mAppConst = new AppConstant(mContext);

        mMemberInfoLayout = (LinearLayout) mRootView.findViewById(R.id.memberInfoLayout);
        defaultParams = CustomViews.getFullWidthLayoutParams();

        // No data message views
        messageLayout = (LinearLayout) mRootView.findViewById(R.id.message_layout);
        errorIcon = (TextView) mRootView.findViewById(R.id.error_icon);
        errorMessage = (SelectableTextView) mRootView.findViewById(R.id.error_message);

        Bundle bundle = getArguments();

        //TODO Remove contains key condition
        if(bundle != null){

            infoType = bundle.getString(ConstantVariables.FORM_TYPE);
            mIsContactInfo = bundle.getBoolean("isContactInfo");

            if(infoType != null){
                switch (infoType) {
                    case "memberInfo":
                        infoUrl = AppConstant.DEFAULT_URL + "members/profile/get-member-info?user_id=" +
                                bundle.getInt(ConstantVariables.USER_ID);
                        break;

                    case "eventInfo":
                        infoUrl = AppConstant.DEFAULT_URL + "advancedevents/information/" +
                                bundle.getInt(ConstantVariables.CONTENT_ID);
                        break;

                    case "organizerInfo":
                        infoUrl = bundle.getString(ConstantVariables.URL_STRING);
                        makeRequest();
                        break;

                    case "description":
                    case "announcement":
                    case "occurrenceInfo":
                    case "overview":
                    case "info_tab":
                    case "ticket_info":
                        infoUrl = bundle.getString(ConstantVariables.URL_STRING);
                        break;
                }
            }

        }

        marginTop = (int ) mContext.getResources().getDimension(R.dimen.margin_5dp);
        if (getArguments() != null && !getArguments().getBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST)) {

            if (mIsContactInfo) {
                try {
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    String mDataResponse = getArguments().getString(ConstantVariables.RESPONSE_OBJECT);
                    if (mDataResponse != null) {
                        JSONObject contactInfoObject = new JSONObject(mDataResponse);
                        setContactDataInView(contactInfoObject);

                    } else {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        messageLayout.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.slider_view_height), 0 , 0);
                        messageLayout.setLayoutParams(layoutParams);
                        messageLayout.setGravity(Gravity.CENTER);
                        messageLayout.setVisibility(View.VISIBLE);
                        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        errorIcon.setText("\uf2b9");
                        errorMessage.setText(getResources().getString(R.string.no_contact_message_text));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                makeRequest();
            }
        }

        return mRootView;
    }

    public void makeRequest() {

        mAppConst.getJsonResponseFromUrl(infoUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                isVisibleToUser = true;
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mBody = jsonObject;

                if (mBody != null) {
                    int statusCode = mBody.optInt("status_code");
                    switch (infoType) {
                        case "description":
                            if (statusCode == 200) {
                                String description = mBody.optString("body");
                                SelectableTextView textView = new SelectableTextView(mContext);
                                textView.setLayoutParams(defaultParams);
                                textView.setText(Html.fromHtml(description));
                                mMemberInfoLayout.addView(textView);
                            }
                            break;

                        case "occurrenceInfo":
                            String description = mBody.optString("infoString");
                            SelectableTextView textView = new SelectableTextView(mContext);
                            textView.setLayoutParams(defaultParams);
                            textView.setText(description);
                            mMemberInfoLayout.addView(textView);
                            break;

                        case "overview":
                            if (statusCode == 200) {
                                String body = mBody.optString("body");

                                WebView overview = new WebView(mContext);
                                 /* Setting Body in TextView */
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    overview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                                } else {
                                    overview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                                }

                                GlobalFunctions.setWebSettings(overview, false);
                                overview.loadDataWithBaseURL("file:///android_asset/", GlobalFunctions.getHtmlData(mContext, body, true),
                                        "text/html", "utf-8", null);

                                mMemberInfoLayout.addView(overview);
                            }
                            break;

                        case "announcement":
                            JSONArray mResponseArray = mBody.optJSONArray("announcements");

                            for (int j = 0; j < mResponseArray.length(); j++) {
                                JSONObject announcementObj = mResponseArray.optJSONObject(j);

                                String title = announcementObj.optString("title");
                                String body = announcementObj.optString("body");

                                SelectableTextView mTitle = new SelectableTextView(mContext);
                                mTitle.setLayoutParams(defaultParams);
                                int padding_5dp = mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp);
                                int padding_10dp = mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp);
                                mTitle.setPadding(padding_5dp, padding_10dp, 0, 0);
                                mTitle.setText(title);
                                mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.body_medium_font_size));
                                mTitle.setTextColor(Color.BLACK);
                                SelectableTextView mBody = new SelectableTextView(mContext);
                                mBody.setPadding(padding_5dp, padding_5dp, 0, 0);
                                mBody.setLayoutParams(defaultParams);
                                mBody.setText(body);
                                mMemberInfoLayout.addView(mTitle);
                                mMemberInfoLayout.addView(mBody);

                            }
                            break;

                        default:
                            JSONArray infoFieldNamesArray = mBody.names();

                            for (int i = 0; i < mBody.length(); i++) {

                                String value;
                                try {
                                    String fieldName = infoFieldNamesArray.getString(i);

                                    JSONObject fieldJsonObject = mBody.optJSONObject(fieldName);

                                    defaultParams.setMargins(0, marginTop, 0, 0);

                                    // If Json Object is coming with Heading
                                    if (fieldJsonObject != null) {
                                        JSONArray fieldJsonNameArray = fieldJsonObject.names();
                                        boolean isHeadingCreated = false;
                                        for (int j = 0; j < fieldJsonObject.length(); j++) {

                                            String name = fieldJsonNameArray.getString(j);
                                            value = fieldJsonObject.getString(name);

                                            if (value != null && !value.isEmpty() && !value.equals("null")) {
                                                if (!isHeadingCreated) {
                                                    // Show Heading
                                                    LinearLayout headingLinearLayout = new LinearLayout(mContext);
                                                    headingLinearLayout.setLayoutParams(defaultParams);
                                                    headingLinearLayout.setOrientation(LinearLayout.VERTICAL);

                                                    SelectableTextView headingtextView = new SelectableTextView(mContext);
                                                    headingtextView.setText(fieldName);
                                                    headingtextView.setTypeface(Typeface.DEFAULT_BOLD);

                                                    View saperator = new View(mContext);
                                                    saperator.setLayoutParams(CustomViews
                                                            .getCustomWidthHeightLayoutParams(
                                                                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
                                                    saperator.setBackgroundColor(ContextCompat.getColor(mContext,
                                                            R.color.light_gray));

                                                    headingLinearLayout.addView(headingtextView);
                                                    headingLinearLayout.addView(saperator);

                                                    mMemberInfoLayout.addView(headingLinearLayout);
                                                }
                                                isHeadingCreated = true;
                                                addFieldLinearLayout(name, value);
                                            }
                                        }
                                    }
                                    // If Json Object is not coming with headings.
                                    else {
                                        value = mBody.getString(fieldName);

                                        if (value != null && !value.isEmpty() && !value.equals("null")) {
                                            addFieldLinearLayout(fieldName, value);
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            break;

                    }

                } else {
                    if (infoType.equals("memberInfo")) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        messageLayout.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.padding_70dp), 0 , 0);
                        messageLayout.setLayoutParams(layoutParams);
                        messageLayout.setGravity(Gravity.CENTER);
                        messageLayout.setVisibility(View.VISIBLE);
                        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        errorIcon.setText("\uf2b9");
                        errorMessage.setText(getResources().getString(R.string.no_profile_info_text));
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbar(mRootView, message);
            }

        });

    }

    public void setContactDataInView(JSONObject contactJsonObject) {

        String value;
        try {
            if (contactJsonObject != null) {
                JSONArray fieldJsonNameArray = contactJsonObject.names();
                for (int j = 0; j < contactJsonObject.length(); j++) {

                    String fieldName = fieldJsonNameArray.getString(j);
                    value = contactJsonObject.getString(fieldName);

                    if (value != null && !value.isEmpty() && !value.equals("null")) {
                        addFieldLinearLayout(fieldName, value);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void addFieldLinearLayout(String label, final String value){

        LinearLayout.LayoutParams fieldLayout;
        LinearLayout.LayoutParams fieldLayoutParams = CustomViews.getFullWidthLayoutParams();
        LinearLayout fieldLinearLayout = new LinearLayout(mContext);

        fieldLayoutParams.setMargins(0, marginTop, 0, 0);
        fieldLinearLayout.setLayoutParams(fieldLayoutParams);


        if(label.equals("About Me") || label.equals("description")){
            fieldLinearLayout.setOrientation(LinearLayout.VERTICAL);
            fieldLayout = CustomViews.getWrapLayoutParams();
        }else{
            fieldLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            fieldLayout = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, .5f);
        }

        //Checking if the field is rating/description or any other.
        if (infoType.equals("organizerInfo") && label.equals("total_rating")) {
            fieldLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            fieldLayout = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, .5f);
            SelectableTextView fieldTextView = new SelectableTextView(mContext);
            fieldTextView.setLayoutParams(fieldLayout);
            fieldTextView.setText(mContext.getResources().getString(R.string.total_rating_text));
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.small_rating_layout, null);
            view.setLayoutParams(fieldLayout);
            RatingBar mMyRatingBar = (RatingBar) view.findViewById(R.id.smallRatingBar);

            LayerDrawable myRatingStar = (LayerDrawable) mMyRatingBar.getProgressDrawable();
            myRatingStar.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                    PorterDuff.Mode.SRC_ATOP);
            myRatingStar.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                    PorterDuff.Mode.SRC_ATOP);
            mMyRatingBar.setRating(Float.parseFloat(value));
            mMyRatingBar.setIsIndicator(true);

            fieldLinearLayout.addView(fieldTextView);
            fieldLinearLayout.addView(view);

        } else if (!label.equals("description")) {
            SelectableTextView labelTextView = new SelectableTextView(mContext);
            labelTextView.setLayoutParams(fieldLayout);
            labelTextView.setText(label);

            SelectableTextView valueTextView = new SelectableTextView(mContext);
            valueTextView.setLayoutParams(fieldLayout);
            if (label.equals("Website")) {
                valueTextView.setAutoLinkMask(Linkify.WEB_URLS);
            }
            valueTextView.setText(value);

            if (infoType!= null && infoType.equals("eventInfo")) {

                labelTextView.setTextColor(Color.BLACK);
                valueTextView.setTextColor(Color.BLACK);
            }

            fieldLinearLayout.addView(labelTextView);
            fieldLinearLayout.addView(valueTextView);

        } else if (infoType.equals("organizerInfo") && label.equals("description")){
            SelectableTextView fieldTextView = new SelectableTextView(mContext);
            fieldTextView.setLayoutParams(fieldLayout);
            fieldTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            fieldTextView.setText(value);
            fieldLinearLayout.addView(fieldTextView);
        }

        mMemberInfoLayout.addView(fieldLinearLayout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantVariables.PAGE_EDIT_CODE &&
                resultCode == ConstantVariables.CONTACT_INFO_CODE) {
            try {
                if (data != null) {
                    JSONObject contactObject = new JSONObject(data.getStringExtra(ConstantVariables.RESPONSE_OBJECT));
                    mMemberInfoLayout.removeAllViews();
                    setContactDataInView(contactObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
