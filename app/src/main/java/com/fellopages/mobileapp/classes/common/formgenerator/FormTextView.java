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
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class FormTextView extends FormWidget {

    protected SelectableTextView _label;
    protected View view;
    public static final LinearLayout.LayoutParams viewParams = CustomViews.
            getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
    public static CallbackManager callbackManager;
    public static TwitterLoginButton twitterLoginButton;
    private ArrayList<FormWidget> widgets;
    private String currentSelectedModule;

    public FormTextView(final Context context, final String property, boolean hasValidator,
                        boolean isNeedToAddPadding, String label, final JSONObject jsonObject, String currentSelectedOption, ArrayList<FormWidget> _widgets) {
        super(context, property, hasValidator);

        widgets = _widgets;
        currentSelectedModule = currentSelectedOption;
        _property = property;
        LinearLayout.LayoutParams layoutParams = CustomViews.getFullWidthLayoutParams();
        layoutParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                0, context.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.margin_12dp));
        layoutParams.gravity = Gravity.CENTER;
        if (currentSelectedOption != null && currentSelectedOption.equals(ConstantVariables.BUYER_FORM)) {
            isNeedToAddPadding = true;
        }

        // Adding bottom line divider.
        View dividerView = new View(context);
        dividerView.setBackgroundResource(R.color.colordevider);
        LinearLayout.LayoutParams dividerLayoutParams = CustomViews.getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
        dividerView.setLayoutParams(dividerLayoutParams);

        // Show terms of service link and make that clickable to open it in webview.
        if (property != null && property.equals("terms_url")) {
            AppCompatTextView termsTextView = new AppCompatTextView(context);
            termsTextView.setText(label);
            termsTextView.setTextColor(ContextCompat.getColor(context, R.color.themeButtonColor));
            termsTextView.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                    context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.margin_15dp));

            termsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent termsOfServiceIntent = new Intent(context, WebViewActivity.class);
                    termsOfServiceIntent.putExtra("url", jsonObject.optString("url"));
                    context.startActivity(termsOfServiceIntent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            _layout.addView(termsTextView);
            _layout.addView(dividerView);

        } else if (property != null && property.equals("facebook")) {
            // Adding facebook/twitter button when its coming in response for the integration.

            /* Initialize Facebook SDK, we need to initialize before using it ---- */
            SocialLoginUtil.initializeFacebookSDK(context);
            SocialLoginUtil.clearFbTwitterInstances(context, "facebook");

            callbackManager = CallbackManager.Factory.create();
            LoginButton facebookLoginButton = new LoginButton(context);
            facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
            facebookLoginButton.setPadding(context.getResources().getDimensionPixelSize(R.dimen.margin_15dp),
                    context.getResources().getDimensionPixelSize(R.dimen.login_button_top_bottom_padding),
                    0, context.getResources().getDimensionPixelSize(R.dimen.login_button_top_bottom_padding));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                facebookLoginButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            facebookLoginButton.setGravity(Gravity.CENTER);
            facebookLoginButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimensionPixelSize(R.dimen.body_default_font_size));
            facebookLoginButton.setLayoutParams(layoutParams);
            if (!context.getResources().getString(R.string.facebook_app_id).isEmpty()) {
                facebookLoginButton.setVisibility(View.VISIBLE);
                addView(context, label, true);
            } else {
                facebookLoginButton.setVisibility(View.GONE);
            }
            _layout.addView(facebookLoginButton);
            _layout.addView(dividerView);

            //Facebook login authentication process
            SocialLoginUtil.registerFacebookLoginCallback(context, _layout, callbackManager, true);

        } else if (property != null && property.equals("twitter")) {
            SocialLoginUtil.clearFbTwitterInstances(context, "twitter");
            twitterLoginButton = new TwitterLoginButton(context);

            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) context.getResources().getDimension(R.dimen.twitter_login_button_height));

            layoutParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                    0, context.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.margin_5dp));


            twitterLoginButton.setLayoutParams(layoutParams);

            twitterLoginButton.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                twitterLoginButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            twitterLoginButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,
                    R.drawable.ic_twitter_bird_icon), null, null, null);
            twitterLoginButton.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.padding_10dp));
            twitterLoginButton.setPadding((int) context.getResources().getDimension(R.dimen.padding_10dp), 0,
                    (int) context.getResources().getDimension(R.dimen.padding_30dp), 0);
            twitterLoginButton.setText(context.getResources().getString(R.string.twitter_login_text));
            twitterLoginButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimensionPixelSize(R.dimen.body_default_font_size));

            // Hide twitter button when twitter_key or twitter_secret is null or Empty.
            if (!context.getResources().getString(R.string.twitter_key).isEmpty() &&
                    !context.getResources().getString(R.string.twitter_secret).isEmpty()) {
                twitterLoginButton.setVisibility(View.VISIBLE);
                addView(context, label, true);
            } else {
                twitterLoginButton.setVisibility(View.GONE);
            }
            _layout.addView(twitterLoginButton);
            _layout.addView(dividerView);

            //Twitter login authentication process
            SocialLoginUtil.registerTwitterLoginCallback(context, _layout, twitterLoginButton, true);

        } else if(jsonObject != null && jsonObject.has("fieldType") && jsonObject.optString("fieldType") != null && jsonObject.optString("fieldType").equals("help")){
            setElementIconWithLabel(context, label, jsonObject);
        }  else if(jsonObject != null && jsonObject.has("subType") && jsonObject.optString("subType") != null && jsonObject.optString("subType").equals("payment_method")){
            setAccordionView(context, label, jsonObject, currentSelectedOption);
        } else {
            addView(context, label, isNeedToAddPadding);
        }
    }

    private void setElementIconWithLabel(final Context context, String label, final JSONObject jsonObject) {
        LinearLayout subView = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        subView.setLayoutParams(params);
        subView.setOrientation(LinearLayout.HORIZONTAL);
        AppCompatTextView helpIcon = new AppCompatTextView(context);
        helpIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_help_18dp), null);
        helpIcon.setLayoutParams(params);
        helpIcon.setGravity(Gravity.RIGHT);
        helpIcon.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_15dp),
                context.getResources().getDimensionPixelSize(R.dimen.margin_5dp));
        AppCompatTextView termsTextView = new AppCompatTextView(context);
        termsTextView.setText(label);

        termsTextView.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.margin_15dp));

        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsOfServiceIntent = new Intent(context, WebViewActivity.class);
                termsOfServiceIntent.putExtra("url", jsonObject.optString("url"));
                context.startActivity(termsOfServiceIntent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        subView.addView(termsTextView);
        subView.addView(helpIcon);
        _layout.addView(subView);
    }

    private void setAccordionView(final Context context, String label, final JSONObject jsonObject, final String currentSelectedModule) {
        LinearLayout subView = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        subView.setLayoutParams(params);
        subView.setOrientation(LinearLayout.HORIZONTAL);
        if (jsonObject.has("isActive")) {
            ImageView enabled = new ImageView(context);
            enabled.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_white_24dp));
            enabled.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    context.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                    context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.margin_15dp));
            if (jsonObject.optInt("isActive", 0) == 1) {
                enabled.setColorFilter(context.getResources().getColor(R.color.green_blue));
            } else {
                enabled.setColorFilter(context.getResources().getColor(R.color.light_gray));
            }
            subView.addView(enabled);
        }
        AppCompatTextView heading = new AppCompatTextView(context);
        heading.setText(label);
        heading.setLayoutParams(params);
        heading.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                context.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_10));
        heading.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_expend_drop_down), null);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setTag(_property);
        heading.setActivated(true);
        heading.setTextAppearance(context,android.R.style.TextAppearance_Large);
        heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkModuleSpecificConditions(view, currentSelectedModule);
            }
        });

        subView.addView(heading);
        _layout.addView(subView);
        // Adding bottom line divider.
        View view = new View(context);
        view.setBackgroundResource(R.color.colordevider);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
        view.setLayoutParams(layoutParams);
        _layout.addView(view);
    }

    public void checkModuleSpecificConditions(View view, String currentSelectedModule) {
        if(currentSelectedModule != null && currentSelectedModule.equals(ConstantVariables.PAYMENT_METHOD_CONFIG) && FormActivity.getAttribByProperty(String.valueOf(view.getTag()),"subType",null).equals("payment_method")){
            int visibility = -1;
            AppCompatTextView heading = (AppCompatTextView)view;
            for (int i = 0; i < widgets.size(); i++) {
                if(widgets.get(i).getPropertyName().equals(view.getTag())) {
                    visibility = heading.isActivated() ? View.VISIBLE : View.GONE;
                    heading.setActivated(!heading.isActivated());
                    continue;
                } else if(visibility != -1 && FormActivity.getAttribByProperty(widgets.get(i).getPropertyName(),"subType",null).equals("payment_method")) {
                    break;
                }
                if(visibility != -1) widgets.get(i).getView().setVisibility(visibility);
            }

        }
    }
    /**
     * Method to add label view.
     *
     * @param context      Context of the class.
     * @param label        Label to be shown.
     * @param isAddPadding True if need to add padding.
     */
    public void addView(Context context, String label, boolean isAddPadding) {
        _label = new SelectableTextView(context);
        _label.setText(label);
        _label.setTypeface(null, Typeface.BOLD);
        _label.setLayoutParams(FormActivity.defaultLayoutParams);
        if (isAddPadding) {
            _label.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
        }

        view = new View(context);
        view.setLayoutParams(viewParams);

        _layout.addView(_label);
        _layout.addView(view);
    }
}
