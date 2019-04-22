
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

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.PredicateLayout;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;


public class FormBlockedUsers extends FormWidget implements OnCancelClickListener {

    // Member variables.
    private Context mContext;
    private View mDividerView;
    private SelectableTextView tvLabel;
    private Map<String, String> postParams;
    private LinearLayout.LayoutParams defaultLayoutParams;
    private PredicateLayout mSelectedFriendsLayout;
    private AppConstant mAppConst;
    private AlertDialogWithAction mAlertDialogWithAction;


    public FormBlockedUsers(final Context context, String property, String label,
                            boolean hasValidator, String description) {
        super(context, property, hasValidator);

        // Initializing member variables.
        mContext = context;
        mAppConst = new AppConstant(mContext);
        postParams = new HashMap<>();
        CustomViews.setmOnCancelClickListener(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        //Set Label
        tvLabel = new SelectableTextView(mContext);
        tvLabel.setText(label);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        tvLabel.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
        _layout.addView(tvLabel);

        //Set description about blocked users
        if (description != null && !description.isEmpty()) {
            SelectableTextView tvDescription = new SelectableTextView(mContext);
            tvDescription.setText(description);
            tvDescription.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_2dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
            _layout.addView(tvDescription);
        }

        // Adding bottom line divider.
        mDividerView = new View(mContext);
        mDividerView.setBackgroundResource(R.color.colordevider);
        LinearLayout.LayoutParams layoutParams = CustomViews.getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
        layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_8dp), 0, 0);
        mDividerView.setLayoutParams(layoutParams);
        _layout.addView(mDividerView);
    }


    @Override
    public String getValue() {
        return tvLabel.getText().toString();
    }

    @Override
    public void setValue(String value) {

        if (value != null && !value.isEmpty()) {

            // Create layout for show blocked users list
            mSelectedFriendsLayout = new PredicateLayout(mContext);

            defaultLayoutParams = CustomViews.getWrapLayoutParams();
            int marginTop = (int) (mContext.getResources().getDimension(R.dimen.margin_10dp) /
                    mContext.getResources().getDisplayMetrics().density);
            defaultLayoutParams.setMargins(marginTop, marginTop, marginTop, marginTop);

            mSelectedFriendsLayout.setLayoutParams(defaultLayoutParams);

            try {
                Object json = new JSONTokener(value).nextValue();

                JSONArray valuesArray = (JSONArray) json;
                for (int i = 0; i < valuesArray.length(); i++) {
                    JSONObject jsonObject = valuesArray.getJSONObject(i);
                    final int user_id = jsonObject.optInt("user_id");
                    String display_name = jsonObject.optString("displayname");
                    if (user_id != 0) {
                        CustomViews.createSelectedUserLayout(mContext, user_id, display_name,
                                mSelectedFriendsLayout, null, 1);
                    }
                }

                _layout.addView(mSelectedFriendsLayout);
                _layout.removeView(mDividerView);
                _layout.addView(mDividerView);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void setErrorMessage(String errorMessage) {
        tvLabel.setError(errorMessage);
    }

    @Override
    public void onCancelButtonClicked(int userId) {

        final View canceledView = mSelectedFriendsLayout.findViewById(userId);
        postParams.put("user_id", String.valueOf(userId));

        mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.unblock_member_title),
                mContext.getResources().getString(R.string.unblock_member_message),
                mContext.getResources().getString(R.string.unblock_member_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mAppConst.postJsonResponseForUrl(AppConstant.DEFAULT_URL + "block/remove", postParams,
                                new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) {
                                        SnackbarUtils.displaySnackbarLongTime(_layout,
                                                mContext.getResources().getString(R.string.unblock_member_success_message));
                                        _layout.removeView(mSelectedFriendsLayout);
                                        mSelectedFriendsLayout.removeView(canceledView);
                                        _layout.addView(mSelectedFriendsLayout);
                                        _layout.removeView(mDividerView);
                                        _layout.addView(mDividerView);
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        SnackbarUtils.displaySnackbarLongTime(_layout,
                                                message);
                                    }
                                });
                    }
                });
    }

}
