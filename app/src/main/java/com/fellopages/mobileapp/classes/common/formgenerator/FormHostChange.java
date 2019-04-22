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
import android.support.v4.content.ContextCompat;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;


public class FormHostChange extends FormWidget implements View.OnClickListener {

    Context mContext;
    TextView _hostLabel, _hostName, _hostChangeText, _hostEditText;
    RelativeLayout relativeLayout;
    ImageView _hostImageView;
    FormActivity formActivity;
    FormWidget widget;
    ArrayList<FormWidget> _widgetsNew;
    Map<String, FormWidget> _mapNew;
    String mCurrentSelectedOption;
    Boolean isCreateForm, isShowSocialField;
    LinearLayout linearLayout, verticalLinearLayout;
    private ImageLoader mImageLoader;
    public static String sEventHost = "event_host";
    public static boolean sIsAddNewHost = false, sIsEditHost = false, sIsCreateNewHost = false;

    public FormHostChange(Context context, String property, JSONObject jsonObject, Boolean createForm,
                          ArrayList<FormWidget> _widgets, Map<String, FormWidget> _map, String currentSelectedOption) {
        super( context, property, false);

        mContext  = context;
        mCurrentSelectedOption = currentSelectedOption;
        _widgetsNew = _widgets;
        formActivity = new FormActivity();
        _mapNew = _map;
        isCreateForm = createForm;
        isShowSocialField = false;

        if (jsonObject.has("host_type")) {
            FormActivity.hostType = jsonObject.optString("host_type");
        } else if (FormActivity.hostKey != null){
            FormActivity.hostType = FormActivity.hostKey;
        }

        RelativeLayout.LayoutParams params = CustomViews.getWrapRelativeLayoutParams();

        int margins = (int) mContext.getResources().getDimension(R.dimen.margin_10dp);

        params.setMargins(margins,margins,margins,margins);

        relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(params);

        linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(CustomViews.getWrapLayoutParams());

        verticalLinearLayout = new LinearLayout(mContext);
        verticalLinearLayout.setOrientation(LinearLayout.VERTICAL);

        _hostLabel = new TextView(mContext);
        _hostImageView = new ImageView(mContext);
        _hostName = new TextView(mContext);
        _hostChangeText = new TextView(mContext);
        mImageLoader = new ImageLoader(mContext);



        _hostLabel.setLayoutParams(params);
        _hostLabel.setText(mContext.getResources().getString(R.string.host_text));
        _hostLabel.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);

        int size = (int) mContext.getResources().getDimension(R.dimen.profile_image_width_height);
        params = CustomViews.getCustomWidthHeightRelativeLayoutParams(size, size);
        params.setMargins(margins,margins,margins,margins);
        _hostImageView.setLayoutParams(params);
        _hostImageView.setId(R.id.image);

        mImageLoader.setImageUrl(jsonObject.optString("image_icon"), _hostImageView);

        params = CustomViews.getWrapRelativeLayoutParams();


        params.addRule(RelativeLayout.RIGHT_OF, R.id.host_image);
        params.setMargins(0,margins,margins,0);
        _hostName.setLayoutParams(params);
        _hostName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        _hostName.setText(jsonObject.optString("host_title"));
        _hostName.setId(R.id.host_name);

        if (!createForm && jsonObject.optString("host_type").equals("siteevent_organizer")) {

            _hostEditText = new TextView(mContext);
            _hostEditText.setPadding(0,margins,margins,margins);
            _hostEditText.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            _hostEditText.setText("[" + mContext.getResources().getString(R.string.edit_this_host) +"]");
            _hostEditText.setOnClickListener(this);
            _hostEditText.setId(R.id.edit_host);
        }

        _hostChangeText.setPadding(0,margins,margins,margins);
        _hostChangeText.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
        _hostChangeText.setText("[" + mContext.getResources().getString(R.string.host_change_text) +"]");

        FormActivity.host_id = jsonObject.optString("host_id");

        if (!createForm && jsonObject.optString("host_type").equals("siteevent_organizer")) {
            linearLayout.addView(_hostEditText);
        }

        linearLayout.addView(_hostChangeText);

        params = CustomViews.getWrapRelativeLayoutParams();

        params.addRule(RelativeLayout.RIGHT_OF, R.id.image);
        params.setMargins(margins,margins,margins,margins);
        verticalLinearLayout.setLayoutParams(params);

        verticalLinearLayout.addView(_hostName);
        verticalLinearLayout.addView(linearLayout);

        relativeLayout.addView( _hostImageView);
        relativeLayout.addView(verticalLinearLayout);

        _layout.addView(_hostLabel);
        _layout.addView(relativeLayout);
        relativeLayout.setId(R.id.host_id);

        // Adding bottom line divider.
        View view = new View(mContext);
        view.setBackgroundResource(R.color.colordevider);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
        view.setLayoutParams(layoutParams);
        _layout.addView(view);

        _hostChangeText.setOnClickListener(this);
        _hostChangeText.setId(R.id.change_host);

    }

    @Override
    public String getValue() {

        return null;
    }

    @Override
    public void setValue(String value)
    {

    }



    @Override
    public void setToggleHandler( FormActivity.FormWidgetToggleHandler handler )
    {
        super.setToggleHandler(handler);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.change_host:

                int position = 0;
                for (int j = 0; j < _widgetsNew.size(); j++) {
                    if (_widgetsNew.get(j).getPropertyName().equals("host_type_select") ||
                            _widgetsNew.get(j).getPropertyName().equals("host_auto")) {
                        _widgetsNew.remove(j);
                        j--;
                    }
                }

                for (int j = 0; j < _widgetsNew.size(); j++) {
                    if (_widgetsNew.get(j).getPropertyName().equals(sEventHost)) {
                        position = j;
                    }
                }

                for (int i = 0; i < FormActivity.mHostSelectionForm.length(); i++) {

                    JSONObject jsonObject = FormActivity.mHostSelectionForm.optJSONObject(i);
                    String fieldName = jsonObject.optString("name");
                    String fieldLabel = jsonObject.optString("label");

                    widget = formActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, isCreateForm,
                            null, _widgetsNew, _mapNew, mCurrentSelectedOption, null,
                            null, null, null, null, null);

                    if (widget == null) continue;

                    if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                        widget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

                    _widgetsNew.add(position + i, widget);
                    _mapNew.put(fieldName, widget);

                }

                FormActivity._layout.removeAllViews();
                for (int k = 0; k < _widgetsNew.size(); k++) {

                    if (_widgetsNew.get(k).getPropertyName().equals("host_type_select") ||
                            _widgetsNew.get(k).getPropertyName().equals("host_auto")) {
                        _widgetsNew.get(k).getView().setVisibility(View.VISIBLE);
                    } else if (_widgetsNew.get(k).getPropertyName().contains("host")){
                        _widgetsNew.get(k).getView().setVisibility(View.GONE);
                    }

                    FormActivity._layout.addView(_widgetsNew.get(k).getView());
                }

                break;

            case R.id.edit_host:
                FormActivity.loadEditHostForm++;
                sIsEditHost = true;
                if (!sIsCreateNewHost) {
                    for (int i = 0; i < _widgetsNew.size(); i++) {

                        if (_widgetsNew.get(i).getPropertyName().equals("host_link") &&
                                _widgetsNew.get(i).getValue().equals("1")) {
                            isShowSocialField = true;
                        }

                        if (_widgetsNew.get(i).getPropertyName().equals(sEventHost)) {
                            _widgetsNew.get(i).getView().setVisibility(View.GONE);
                        } else if (_widgetsNew.get(i).getPropertyName().equals("host_title") ||
                                _widgetsNew.get(i).getPropertyName().equals("host_description") ||
                                _widgetsNew.get(i).getPropertyName().equals("host_photo") ||
                                _widgetsNew.get(i).getPropertyName().equals("host_link")) {
                            _widgetsNew.get(i).getView().setVisibility(View.VISIBLE);
                        }  else if (isShowSocialField && (_widgetsNew.get(i).getPropertyName().equals("host_facebook") ||
                                _widgetsNew.get(i).getPropertyName().equals("host_twitter") ||
                                _widgetsNew.get(i).getPropertyName().equals("host_website"))) {
                            _widgetsNew.get(i).getView().setVisibility(View.VISIBLE);
                        }

                    }
                } else {
                    sIsAddNewHost = true;
                    FormActivity.loadEditHostForm++;
                    int host_position = 0;

                    for (int j = 0; j < _widgetsNew.size(); j++) {
                        if (_widgetsNew.get(j).getPropertyName().equals(sEventHost)) {
                            host_position = j;
                        }
                    }

                    for (int j = 0; j < _widgetsNew.size(); j++) {
                         if (_widgetsNew.get(j).getPropertyName().equals("host_type_select") ||
                                _widgetsNew.get(j).getPropertyName().equals("host_auto")) {
                            _widgetsNew.remove(j);
                            j--;
                        }
                    }

                    for (int i = 0; i < FormActivity.mHostCreateForm.length(); i++) {

                        JSONObject jsonObject = FormActivity.mHostCreateForm.optJSONObject(i);
                        String fieldName = jsonObject.optString("name");
                        String fieldLabel = jsonObject.optString("label");

                        widget = formActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, isCreateForm,
                                null, _widgetsNew, _mapNew, mCurrentSelectedOption, null,
                                null, null, null, null, null);

                        if (widget == null) continue;

                        if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                            widget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

                        _widgetsNew.add(host_position + i, widget);
                        _mapNew.put(fieldName, widget);

                    }

                    int socialLinkPosition = host_position + FormActivity.mHostCreateForm.length();
                    socialLinkPosition++;

                    for (int i = 0; i < FormActivity.mHostCreateFormSocial.length(); i++) {

                        JSONObject jsonObject = FormActivity.mHostCreateFormSocial.optJSONObject(i);
                        String fieldName = jsonObject.optString("name");
                        String fieldLabel = jsonObject.optString("label");

                        widget = formActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, isCreateForm,
                                null, _widgetsNew, _mapNew, mCurrentSelectedOption, null,
                                null, null, null, null, null);

                        if (widget == null) continue;

                        if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                            widget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

                        _widgetsNew.add(socialLinkPosition + i, widget);
                        _mapNew.put(fieldName, widget);

                    }

                    FormActivity._layout.removeAllViews();
                    for (int k = 0; k < _widgetsNew.size(); k++) {

                        if (_widgetsNew.get(k).getPropertyName().equals("host_link") &&
                                _widgetsNew.get(k).getValue().equals("1")) {
                            isShowSocialField = true;
                        }

                        if (_widgetsNew.get(k).getPropertyName().equals(sEventHost)) {
                            _widgetsNew.get(k).getView().setVisibility(View.GONE);
                        } else if ((_widgetsNew.get(k).getPropertyName().equals("host_facebook") ||
                                _widgetsNew.get(k).getPropertyName().equals("host_twitter") ||
                                _widgetsNew.get(k).getPropertyName().equals("host_website"))) {

                            if (isShowSocialField) {
                                _widgetsNew.get(k).getView().setVisibility(View.VISIBLE);
                            } else {
                                _widgetsNew.get(k).getView().setVisibility(View.GONE);
                            }
                        }

                        FormActivity._layout.addView(_widgetsNew.get(k).getView());

                    }
                }

                break;
        }
    }
}
