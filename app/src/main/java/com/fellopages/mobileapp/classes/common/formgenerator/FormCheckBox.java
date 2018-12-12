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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.BrowseMemberAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONObject;
import java.util.ArrayList;

/**
 * FormCheckBox is used to inflate the fields for the Check box.
 */

public class FormCheckBox extends FormWidget implements View.OnClickListener {

    // Member variables.
    private Context mContext;
    private AppCompatCheckedTextView checkedTextView;
    private AppConstant mAppConst;
    private ArrayList<FormWidget> widgets;
    private AlertDialogWithAction mAlertDialogWithAction;


    /**
     * Public constructor to inflate form field For the checkbox items.
     *
     * @param context               Context of calling class.
     * @param property              Property of the field.
     * @param label                 Label of the field.
     * @param hasValidator          True if the field has validation (Compulsory field).
     * @param defaultValue          Default value of the field.
     * @param _widget               List of FormWidget.
     * @param currentSelectedModule Current selected module.
     */
    public FormCheckBox(Context context, String property, String label, boolean hasValidator,
                        int defaultValue, ArrayList<FormWidget> _widget, final String currentSelectedModule) {
        super(context, property, hasValidator);

        // Initializing member variables.
        mContext = context;
        mAppConst = new AppConstant(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        widgets = _widget;

        // Added description when it is coming for these paritcular modules.
        if (!FormActivity.sIsAddToDiaryDescription
                && ((currentSelectedModule.equals(ConstantVariables.ADD_TO_DIARY) && (property.contains("diary") || property.contains("Diary")))
                || (currentSelectedModule.equals(ConstantVariables.ADD_TO_WISHLIST) && property.contains("wishlist"))
                || (currentSelectedModule.equals("add_to_friend_list"))
                || (currentSelectedModule.equals("add_to_playlist") && property.contains("inplaylist")))) {
            
            AppCompatTextView textView = new AppCompatTextView(context);
            int padding = (int) mContext.getResources().getDimension(R.dimen.padding_6dp);
            textView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), padding);
            textView.setText(FormActivity.addToDiaryDescription);
            _layout.addView(textView);
            FormActivity.sIsAddToDiaryDescription = true;
        }

        checkedTextView = new AppCompatCheckedTextView(mContext);
        checkedTextView.setText(label);
        checkedTextView.setGravity(Gravity.CENTER);
        checkedTextView.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_11dp));
        checkedTextView.setCheckMarkDrawable(GlobalFunctions.getCheckMarkDrawable(mContext));

        switch (property) {
            case "monthlyType":
                checkedTextView.setId(R.id.monthly_type);
                checkedTextView.setTag(property);
                checkedTextView.setChecked(defaultValue != 0);
                break;

            case "host_link":
                checkedTextView.setId(R.id.social_link);
                checkedTextView.setTag(R.id.social_link);
                break;

            case "isCopiedDetails":
                checkedTextView.setTag("copy_purchaser_info");
                break;

            case "isPaypalChecked":
                checkedTextView.setId(R.id.isPaypalChecked);
                break;

            case "isByChequeChecked":
                checkedTextView.setId(R.id.isByChequeChecked);
                break;

            default:
                checkedTextView.setId(R.id.form_checkbox);
                checkedTextView.setTag(property);
                break;
        }
        if (property.equals("host_link") && FormHostChange.sIsEditHost && defaultValue != 0) {
            checkedTextView.setChecked(true);
        } else {
            checkedTextView.setChecked(defaultValue != 0);
        }

        /**
         * Add Cross icon on Add To List page to delete Lists (Add to List which comes on Friends tab of
         * member profile page).
         */
        if (currentSelectedModule.equals("add_to_friend_list")) {

            RelativeLayout listLayout = new RelativeLayout(context);
            listLayout.setId(R.id.property);

            RelativeLayout.LayoutParams layoutParams = CustomViews.getFullWidthRelativeLayoutParams();
            listLayout.setLayoutParams(layoutParams);
            listLayout.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0,
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);

            TextView textView = new TextView(context);
            RelativeLayout.LayoutParams textViewParams = CustomViews.getFullWidthRelativeLayoutParams();

            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_END, R.id.property);
            textViewParams.addRule(RelativeLayout.RIGHT_OF, R.id.form_checkbox);

            int paddingLeft = (int) mContext.getResources().getDimension(R.dimen.padding_5dp);
            int paddingRight = (int) mContext.getResources().getDimension(R.dimen.padding_30dp);

            textView.setPadding(paddingLeft, paddingLeft, paddingRight, paddingLeft);

            textView.setLayoutParams(textViewParams);
            textView.setTag(property);
            textView.setGravity(Gravity.END);
            textView.setOnClickListener(this);

            Typeface fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
            textView.setTypeface(fontIcon);
            textView.setText("\uf00d");

            listLayout.setTag(property);

            listLayout.addView(checkedTextView);
            listLayout.addView(textView);

            _layout.addView(listLayout);
            _layout.setTag(property);

        }  else {
            checkedTextView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));
            _layout.addView(checkedTextView);
        }

        // Adding bottom line divider.
        View view = new View(mContext);
        view.setBackgroundResource(R.color.colordevider);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
        view.setLayoutParams(layoutParams);
        _layout.addView(view);

        // Applying click listener on the check box to mark checkbox as checked/unchecked.
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedTextView.setError(null);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                checkModuleSpecificConditions(view);
            }
        });

    }

    /**
     * Method to check module specific conditions on the checkbox click.
     *
     * @param view Clicked check box view.
     */
    private void checkModuleSpecificConditions(View view) {

        if (view.getId() == R.id.social_link && FormActivity.loadEditHostForm > 0) {
            for (int i = 0; i < widgets.size(); i++) {
                if (widgets.get(i).getPropertyName().equals("host_facebook") ||
                        widgets.get(i).getPropertyName().equals("host_twitter") ||
                        widgets.get(i).getPropertyName().equals("host_website")) {
                    if (checkedTextView.isChecked()) {
                        widgets.get(i).getView().setVisibility(View.VISIBLE);
                    } else {
                        widgets.get(i).getView().setVisibility(View.GONE);
                    }

                }
            }
        } else if(view.getId() == R.id.isPaypalChecked){
            for (int i = 0; i < widgets.size(); i++) {
            if (widgets.get(i).getPropertyName().equals("email") ||
                    widgets.get(i).getPropertyName().equals("username") ||
                    widgets.get(i).getPropertyName().equals("password") ||
                    widgets.get(i).getPropertyName().equals("signature")) {
                widgets.get(i).getView().setVisibility(checkedTextView.isChecked() ? View.VISIBLE : View.GONE);
            }
        }
        } else if(view.getId() == R.id.isByChequeChecked){
            for (int i = 0; i < widgets.size(); i++) {
                if (widgets.get(i).getPropertyName().equals("bychequeGatewayDetail")) {
                    widgets.get(i).getView().setVisibility(View.GONE);
                    widgets.get(i).getView().setVisibility(checkedTextView.isChecked() ? View.VISIBLE : View.GONE);
                }
            }
        }else if (view.getTag() != null && view.getTag().toString().equals("copy_purchaser_info")) {
            String fName = "", lName = "", email = "";
            for (int i = 0; i < widgets.size(); i++) {
                switch (widgets.get(i).getPropertyName()) {
                    case "fname":
                        fName = widgets.get(i).getValue();
                        break;
                    case "lname":
                        lName = widgets.get(i).getValue();
                        break;
                    case "email":
                        email = widgets.get(i).getValue();
                        break;
                }
                if (checkedTextView.isChecked()) {
                    if (widgets.get(i).getPropertyName().contains("fname_")) {
                        widgets.get(i).setValue(fName);
                    } else if (widgets.get(i).getPropertyName().contains("lname_")){
                        widgets.get(i).setValue(lName);
                    } else if (widgets.get(i).getPropertyName().contains("email_")) {
                        widgets.get(i).setValue(email);
                    }
                } else {
                    if (widgets.get(i).getPropertyName().contains("fname_") ||
                            widgets.get(i).getPropertyName().contains("lname_") ||
                            widgets.get(i).getPropertyName().contains("email_")) {
                        widgets.get(i).setValue("");
                    }
                }
            }
        }

    }

    public void hidePaymentMethodFormInitially(){
        for (int i = 0; i < widgets.size(); i++) {
                switch (widgets.get(i).getPropertyName()) {
                    case "email":
                    case "username":
                    case "password":
                    case "signature":
                    case "bychequeGatewayDetail":
                        widgets.get(i).getView().setVisibility(View.GONE);
                        break;
                }
        }
    }

    @Override
    public String getValue() {
        return String.valueOf(checkedTextView.isChecked() ? "1" : "0");
    }

    @Override
    public void setValue(String value) {
        checkedTextView.setChecked(value.equals("1"));
        if (checkedTextView.getId() == R.id.monthly_type) {
            checkedTextView.setTag(value);
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {

        // Showing error message.
        if (!checkedTextView.isChecked() && errorMessage != null) {
            checkedTextView.setError(errorMessage);
            checkedTextView.setFocusable(true);
            checkedTextView.requestFocus();
        }
    }

    @Override
    public void onClick(final View v) {

        /**
         * Work to delete the friends list on Add to List page.
         *
         */
        final String list_id = v.getTag().toString();

        final String actionUrl = AppConstant.DEFAULT_URL + "user/list-delete?list_id=" + list_id +
                "&friend_id=" + BrowseMemberAdapter.sFriendId;

        mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.delete_list_title),
                mContext.getResources().getString(R.string.delete_list_dialogue_message),
                mContext.getResources().getString(R.string.delete_list_title),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mAppConst.showProgressDialog();
                        mAppConst.deleteResponseForUrl(actionUrl, null, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {
                                mAppConst.hideProgressDialog();
                                /* Show Message */
                                SnackbarUtils.displaySnackbarLongTime(_layout,
                                        mContext.getResources().getString(R.string.successful_submit));
                                View view = _layout.findViewById(R.id.property);
                                _layout.removeView(view);

                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                            }
                        });
                    }
                });
    }

}
