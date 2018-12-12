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
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.ui.BaseButton;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.NestedListView;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.SelectedFriendList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.editor.NewEditorActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsInfoTabFragment;
import com.fellopages.mobileapp.classes.modules.messages.SelectedFriendListAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FormEditText is used to inflate the fields for the Edit text with the error view
 * and the other module specific views.
 */

public class FormEditText extends FormWidget implements TextWatcher, AdapterView.OnItemClickListener,
        View.OnClickListener {

    // Member Variables.
    private Context mContext;
    private EditText etFieldValue;
    private AppCompatAutoCompleteTextView tvLocationField;
    private BaseButton btnCheckUrl;
    private NestedListView mFriendNestedListView;
    private RecyclerView mAddedFriendRecyclerView;
    private ProgressBar pbLoadFriendList;
    private int mContentId, hostPosition;
    private boolean isSearchHost = false, isCreateForm;
    private String mCurrentSelectedOption, mFieldName;
    private JSONObject jsonObjectProperty;
    private List<SelectedFriendList> mSelectedFriendList;
    private List<AddPeopleList> mAddPeopleList;
    private Map<Integer, String> mShowNonSelectedFriend;
    private Map<String, String> mSelectedFriendsMap;
    private Map<String, String> mPostParams;
    private Map<String, FormWidget> mFormWidgetMap;
    private ArrayList<FormWidget> mFormWidgetList;
    private FormWidget mFormWidget;
    private FormActivity mFormActivity;
    private AppConstant mAppConst;
    private SelectedFriendListAdapter mSelectedFriendListAdapter;
    private AddPeopleAdapter mAddPeopleAdapter;
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private String mValue;


    /**
     * Public constructor to inflate form field For the edit text.
     *
     * @param context               Context of calling class.
     * @param property              Property of the field.
     * @param jsonObjectProperty    Json object of the selected property.
     * @param description           Description of the field.
     * @param hasValidator          True if the field has validation (Compulsory field).
     * @param type                  Type of the field.
     * @param inputType             Input Type of the field.
     * @param widgets               List of FormWidget.
     * @param map                   Map of field name and formWidget.
     * @param contentId             Content id.
     * @param createForm            True if the form is loaded for creation.
     * @param isNeedToHideView      True if need to hide the inflated view.
     * @param value                 Value of the field.
     * @param currentSelectedOption Current selected module.
     */
    public FormEditText(Context context, String property, JSONObject jsonObjectProperty,
                        String description, boolean hasValidator, String type, String inputType,
                        ArrayList<FormWidget> widgets, Map<String, FormWidget> map, int contentId,
                        boolean createForm, boolean isNeedToHideView, String value, String currentSelectedOption) {

        super(context, property, hasValidator);

        // Initialize member variables.
        mContext = context;
        mFieldName = property;
        this.jsonObjectProperty = jsonObjectProperty;
        mFormWidgetList = widgets;
        mFormWidgetMap = map;
        mContentId = contentId;
        isCreateForm = createForm;
        mCurrentSelectedOption = currentSelectedOption;

        mFormActivity = new FormActivity();
        mAppConst = new AppConstant(mContext);

        mValue = value;

        // Inflate the field view layout.
        View inflateView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_form_text_block, null);
        getViews(inflateView, description);
        checkForTextArea(type, inputType);
        inflateView.setTag(mFieldName);

        // Adding view for the host.
        if (property.equals("host_title")) {
            int margin = (int) mContext.getResources().getDimension(R.dimen.margin_10dp);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tvHost = new TextView(context);
            tvHost.setText(mContext.getResources().getString(R.string.add_new_host_text));
            TextView tvCancel = new TextView(context);
            tvCancel.setPadding(margin, margin, margin, margin);
            tvCancel.setId(R.id.cancel);
            tvCancel.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            tvCancel.setText(" [" + mContext.getResources().getString(R.string.cancel) + "]");

            linearLayout.addView(tvHost);
            linearLayout.addView(tvCancel);
            _layout.addView(linearLayout);
            tvCancel.setOnClickListener(this);

        } else if (property.equals("host_auto")) {
            isSearchHost = true;

        } else if (!FormActivity.sIsCreateDiaryDescription && (mCurrentSelectedOption.equals("add_to_diary")
                || mCurrentSelectedOption.equals("add_wishlist")
                || mCurrentSelectedOption.equals("add_to_friend_list"))) {

            AppCompatTextView createDiaryTextView = new AppCompatTextView(context);
            int padding = (int) mContext.getResources().getDimension(R.dimen.padding_6dp);
            createDiaryTextView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    padding, mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
            createDiaryTextView.setText(FormActivity.createDiaryDescription);
            _layout.addView(createDiaryTextView);
            FormActivity.sIsCreateDiaryDescription = true;
        }

        _layout.addView(inflateView);

        if (isNeedToHideView) {
            _layout.setTag(mFieldName);
            _layout.setVisibility(View.GONE);
        }

    }

    /**
     * Method to get views from the form layout and set data in views..
     *
     * @param configFieldView View which is inflated.
     * @param description     Description of the field.
     */
    private void getViews(View configFieldView, String description) {

        // Getting label, description and field value views.
        TextView tvLabel = (TextView) configFieldView.findViewById(R.id.view_label);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        TextView tvDescription = (TextView) configFieldView.findViewById(R.id.view_description);
        etFieldValue = (EditText) configFieldView.findViewById(R.id.field_value);
        tvLocationField = (AppCompatAutoCompleteTextView) configFieldView.findViewById(R.id.location_field_value);
        btnCheckUrl = (BaseButton) configFieldView.findViewById(R.id.btn_check_url);

        // Showing auto complete text view only for the location.
        if (mFieldName.contains("location")) {
            etFieldValue.setVisibility(View.GONE);
            tvLocationField.setVisibility(View.VISIBLE);
            tvLocationField.setAdapter(new GooglePlacesAutocompleteAdapter(mContext, R.layout.simple_text_view));
        } else {
            etFieldValue.setVisibility(View.VISIBLE);
            etFieldValue.addTextChangedListener(this);
            tvLocationField.setVisibility(View.GONE);
        }

        // Showing check url option.
        if ((mCurrentSelectedOption.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)
                && mFieldName.equals("group_uri"))
                || (mCurrentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)
                && mFieldName.equals("channel_uri"))) {
            btnCheckUrl.setVisibility(View.VISIBLE);
            btnCheckUrl.setOnClickListener(this);

        } else if (mFieldName.equals("searchGuests") || mFieldName.equals("toValues")
                || mFieldName.equals("host_auto")) {
            mAddPeopleList = new ArrayList<>();
            mSelectedFriendList = new ArrayList<>();
            mSelectedFriendsMap = new HashMap<>();
            mShowNonSelectedFriend = new HashMap<>();

            pbLoadFriendList = (ProgressBar) configFieldView.findViewById(R.id.sentToLoadingProgressBar);
            mFriendNestedListView = (NestedListView) configFieldView.findViewById(R.id.friendListView);
            mAddPeopleAdapter = new AddPeopleAdapter(mContext, R.layout.list_friends, mAddPeopleList);
            mFriendNestedListView.setAdapter(mAddPeopleAdapter);
            mFriendNestedListView.setVisibility(View.VISIBLE);

            mAddedFriendRecyclerView = (RecyclerView) configFieldView.findViewById(R.id.addedFriendList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mAddedFriendRecyclerView.setLayoutManager(layoutManager);
            mSelectedFriendListAdapter = new SelectedFriendListAdapter(mSelectedFriendList,
                    mAddedFriendRecyclerView, mSelectedFriendsMap, mShowNonSelectedFriend, false);
            mAddedFriendRecyclerView.setAdapter(mSelectedFriendListAdapter);

            mFriendNestedListView.setOnItemClickListener(this);
        }

        // Setting up data in views.
        if (jsonObjectProperty.optString("label") != null && !jsonObjectProperty.optString("label").isEmpty()) {
            tvLabel.setVisibility(View.VISIBLE);
            etFieldValue.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0, 0);
            if (jsonObjectProperty.optString("label").toLowerCase().contains(getDisplayText().toLowerCase())) {
                tvLabel.setText(getDisplayText());
            } else {
                tvLabel.setText(jsonObjectProperty.optString("label"));
            }
        } else {
            tvLabel.setVisibility(View.GONE);
            etFieldValue.setPadding(0, 0, 0, 0);
        }

        //TODO, Uncomment this when ever the description is needed.
        // Showing description field if it is coming in response.
//        if (description != null && !description.isEmpty()) {
//            tvDescription.setVisibility(View.VISIBLE);
//            tvDescription.setText(description);
//        } else {
//            tvDescription.setVisibility(View.GONE);
//        }

        // Setting up the click listener on the overview field to open the editor.
        if (mFieldName.equals("overview") && (mCurrentSelectedOption.equals(ConstantVariables.MLT_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.ADD_PRODUCT)
                || mCurrentSelectedOption.equals(ConstantVariables.STORE_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.EDIT_PRODUCT))) {
            configFieldView.findViewById(R.id.form_main_view).setOnClickListener(this);
            etFieldValue.setOnClickListener(this);
            etFieldValue.setFocusableInTouchMode(false);
            etFieldValue.setFocusable(false);
            etFieldValue.setKeyListener(null);
        } else if(mFieldName.equals("product_search")){
            etFieldValue.setTag("product_search");
            etFieldValue.setFocusableInTouchMode(false);
            etFieldValue.setFocusable(false);
            etFieldValue.setKeyListener(null);
            etFieldValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent  intent = new Intent(mContext, FragmentLoadActivity.class);
                    intent.putExtra(ConstantVariables.FRAGMENT_NAME,ConstantVariables.SELECT_PRODUCT);
                    intent.putExtra(ConstantVariables.CONTENT_TITLE,"Choose Product");
                    intent.putExtra("store_id",mContentId);
                    ((Activity)mContext).startActivityForResult(intent,ConstantVariables.SELECT_PRODUCT_RETURN_CODE);
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_up_in, R.anim.push_up_out);
                }
            });
        } else if (mCurrentSelectedOption.equals(ConstantVariables.SIGN_UP_FIELDS) &&
                (mFieldName.contains("first_name") || mFieldName.contains("last_name"))) {
            setValue(mValue);
        }
        //TODO recheck it if need to implement this.
        // Setting up the max length on the edit text if it is coming in response.
//        if (jsonObjectProperty.has("maxlength") && jsonObjectProperty.optInt("maxlength") != 0) {
//            etFieldValue.setFilters(new InputFilter[] {
//                    new InputFilter.LengthFilter(jsonObjectProperty.optInt("maxlength")) {
//                        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                            CharSequence res = super.filter(source, start, end, dest, dstart, dend);
//                            if (res != null) {
//                                etFieldValue.setError("Limit exceeded! Max number of "
//                                        + jsonObjectProperty.optInt("maxlength") +" characters allowed.");
//                            }
//                            return res;
//                        }
//                    }
//            });
//        }
    }

    /***
     * Method to check the text type and set the input type and max lines accordingly.
     *
     * @param type Type of the field.
     * @param inputType Input Type of the field.
     */
    private void checkForTextArea(String type, String inputType) {
        switch (type) {
            case FormActivity.SCHEMA_KEY_TextArea:
            case FormActivity.SCHEMA_KEY_TextArea_LOWER:
                etFieldValue.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.attachment_small_image_size));
                etFieldValue.setMaxHeight(mContext.getResources().getDimensionPixelSize(R.dimen.attachment_small_image_size));
                etFieldValue.setSingleLine(false);
                etFieldValue.setGravity(Gravity.START | Gravity.TOP);
                break;

            case FormActivity.SCHEMA_KEY_PASSWORD:
                etFieldValue.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etFieldValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;

            default:
                etFieldValue.setGravity(Gravity.CENTER_VERTICAL);
                etFieldValue.setMaxLines(1);
                break;
        }

        // Setting input type to email address when the field name contains the email.
        if (mFieldName.toLowerCase().contains("email")) {
            etFieldValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        // Setting input type to number if field is phone with type text
        if ((mCurrentSelectedOption.equals(ConstantVariables.CONTACT_INFO_MENU_TITLE)
                && mFieldName.toLowerCase().contains("phone"))
                || (mCurrentSelectedOption.equals(ConstantVariables.MLT_MENU_TITLE)
                && mFieldName.toLowerCase().contains("contact"))) {
            etFieldValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        // setting input type to number if field is for numeric value.
        if (inputType != null && inputType.equals("number")) {
            etFieldValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence searchText, int start, int before, int count) {

        etFieldValue.setError(null);
        tvLocationField.setError(null);

        // Showing the check url button.
        if ((mCurrentSelectedOption.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)
                && mFieldName.equals("group_uri"))
                || (mCurrentSelectedOption.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)
                && mFieldName.equals("channel_uri"))) {
            etFieldValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            btnCheckUrl.setText(mContext.getResources().getString(R.string.check_url));
            btnCheckUrl.setVisibility(View.VISIBLE);

        } else if (mFriendNestedListView != null && searchText != null && searchText.length() != 0) {

            // Getting friend list when user type a name in edit text.
            mPostParams = new HashMap<>();
            String getFriendsUrl = null, key;
            if (isSearchHost) {
                if (FormActivity.hostKey != null && !FormActivity.hostKey.isEmpty()) {
                    getFriendsUrl = AppConstant.DEFAULT_URL + "advancedevents/get-hosts?host_type_select="
                            + FormActivity.hostKey;
                }
                key = "host_auto";
            } else if (mContentId != 0) {
                getFriendsUrl = AppConstant.DEFAULT_URL + "advancedgroups/members/getusers/"
                        + mContentId + "?limit=10";
                key = "text";
            } else {
                getFriendsUrl = AppConstant.DEFAULT_URL + "advancedevents/member-suggest?limit=10"
                        + "&subject=" + AdvEventsInfoTabFragment.sGuid;
                key = "value";
            }
            if (getFriendsUrl != null && !getFriendsUrl.isEmpty()) {
                mPostParams.put(key, String.valueOf(searchText));
                getFriendListFromUrl(mAppConst.buildQueryString(getFriendsUrl, mPostParams));
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        AddPeopleList addPeopleList = mAddPeopleList.get(position);
        String label = addPeopleList.getmUserLabel();
        int userId = addPeopleList.getmUserId();

        if (mAddPeopleAdapter != null) {
            mAddPeopleAdapter.clear();
        }

        // Checking for the request is for host or others.
        if (!isSearchHost) {
            mAddedFriendRecyclerView.setVisibility(View.VISIBLE);
            if (!mSelectedFriendsMap.containsKey(Integer.toString(userId))) {
                mSelectedFriendsMap.put(Integer.toString(userId), label);
                FormActivity.selectedGuest = mSelectedFriendsMap;
                mShowNonSelectedFriend.put(userId, label);
                mSelectedFriendList.add(new SelectedFriendList(userId, label));
                mSelectedFriendListAdapter.notifyDataSetChanged();
            }
        } else {

            for (int j = 0; j < mFormWidgetList.size(); j++) {
                if (mFormWidgetList.get(j).getPropertyName().equals("host_type_select")) {
                    position = j;
                }

                if (mFormWidgetList.get(j).getPropertyName().contains("host")) {
                    mFormWidgetList.remove(j);
                    j--;
                }
            }

            JSONObject jsonObject = addPeopleList.getmHostObject();
            String fieldName = FormHostChange.sEventHost;
            String fieldLabel = jsonObject.optString("host_title");

            mFormWidget = mFormActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, true,
                    null, mFormWidgetList, mFormWidgetMap, mCurrentSelectedOption, null,
                    null, null, null, null, null);

            if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                mFormWidget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

            mFormWidgetList.add(position, mFormWidget);
            mFormWidgetMap.put(fieldName, mFormWidget);

            FormActivity._layout.removeAllViews();
            for (int k = 0; k < mFormWidgetList.size(); k++) {
                FormActivity._layout.addView(mFormWidgetList.get(k).getView());
            }
        }
        etFieldValue.setText("");
    }

    @Override
    public String getValue() {

        if (mFieldName.contains("location")) {
            return tvLocationField.getText().toString();
        } else {
            return etFieldValue.getText().toString();
        }
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            return;
        }
        if (mFieldName.contains("location")) {
            tvLocationField.setText(value);
        } else {
            CustomViews.setEditText(etFieldValue, value);
        }
    }

    @Override
    public void setHint(String value) {
        // Showing hint on the respective views..
        if (value != null) {
            if (mFieldName.contains("location")) {
                tvLocationField.setHint(value);

            } else if (mFieldName.equals("phoneno")) {
                if (! jsonObjectProperty.optBoolean("hasValidator")) {
                    etFieldValue.setHint(mContext.getResources().getString(R.string.phoneno_optional_hint));
                } else {
                    etFieldValue.setHint(value);
                }
                etFieldValue.setVisibility(View.VISIBLE);

            } else {
                etFieldValue.setHint(value);
            }
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        // Showing error message on error view.
        if (errorMessage != null) {
            if (mFieldName.contains("location")) {
                tvLocationField.requestFocus();
                tvLocationField.setError(errorMessage);
            } else {
                etFieldValue.requestFocus();
                etFieldValue.setError(errorMessage);
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_check_url:
                if (etFieldValue.getText() != null && !etFieldValue.getText().toString().trim().isEmpty()) {
                    checkUrlAvailability(etFieldValue.getText().toString());
                } else {
                    SnackbarUtils.displaySnackbar(_layout,
                            mContext.getResources().getString(R.string.empty_url_text));
                }
                break;

            case R.id.cancel:
                FormActivity.hostKey = null;
                if (FormHostChange.sIsEditHost) {
                    FormHostChange.sIsEditHost = false;
                    for (int k = 0; k < mFormWidgetList.size(); k++) {
                        if (mFormWidgetList.get(k).getPropertyName().equals(FormHostChange.sEventHost)) {
                            mFormWidgetList.get(k).getView().setVisibility(View.VISIBLE);
                        } else if (mFormWidgetList.get(k).getPropertyName().equals("host_title") ||
                                mFormWidgetList.get(k).getPropertyName().equals("host_description") ||
                                mFormWidgetList.get(k).getPropertyName().equals("host_photo") ||
                                mFormWidgetList.get(k).getPropertyName().equals("host_link") ||
                                mFormWidgetList.get(k).getPropertyName().equals("host_facebook") ||
                                mFormWidgetList.get(k).getPropertyName().equals("host_twitter") ||
                                mFormWidgetList.get(k).getPropertyName().equals("host_website")) {
                            mFormWidgetList.get(k).getView().setVisibility(View.GONE);

                        }

                    }

                } else if (FormHostChange.sIsAddNewHost) {
                    FormActivity.loadEditHostForm = 0;
                    FormHostChange.sIsAddNewHost = false;

                    for (int j = 0; j < mFormWidgetList.size(); j++) {
                        if (mFormWidgetList.get(j).getPropertyName().contains("host") &&
                                !mFormWidgetList.get(j).getPropertyName().equals(FormHostChange.sEventHost)) {
                            mFormWidgetList.remove(j);
                            j--;
                        }
                    }

                    for (int j = 0; j < mFormWidgetList.size(); j++) {

                        if (mFormWidgetList.get(j).getPropertyName().equals(FormHostChange.sEventHost)) {
                            hostPosition = j;
                        }
                    }

                    hostPosition++;

                    for (int i = 0; i < FormActivity.mHostSelectionForm.length(); i++) {

                        JSONObject jsonObject = FormActivity.mHostSelectionForm.optJSONObject(i);
                        String fieldName = jsonObject.optString("name");
                        String fieldLabel = jsonObject.optString("label");

                        mFormWidget = mFormActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, isCreateForm,
                                null, mFormWidgetList, mFormWidgetMap, mCurrentSelectedOption, null,
                                null, null, null, null, null);

                        if (mFormWidget == null) continue;

                        if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                            mFormWidget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

                        mFormWidgetList.add(hostPosition + i, mFormWidget);
                        mFormWidgetMap.put(fieldName, mFormWidget);

                    }

                    FormActivity._layout.removeAllViews();
                    for (int i = 0; i < mFormWidgetList.size(); i++) {
                        if (mFormWidgetList.get(i).getPropertyName().equals(FormHostChange.sEventHost)) {
                            mFormWidgetList.get(i).getView().setVisibility(View.GONE);
                        }

                        FormActivity._layout.addView(mFormWidgetList.get(i).getView());
                    }


                }
                break;

            case R.id.form_main_view:
            case R.id.field_value:
                Intent intent = new Intent(mContext, NewEditorActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(NewEditorActivity.TITLE_PARAM, "");
                bundle.putString(NewEditorActivity.CONTENT_PARAM, FormActivity.overviewText);
                bundle.putString(NewEditorActivity.TITLE_PLACEHOLDER_PARAM,
                        mContext.getResources().getString(R.string.example_post_title_placeholder));
                bundle.putString(NewEditorActivity.CONTENT_PLACEHOLDER_PARAM,
                        mContext.getResources().getString(R.string.post_content_placeholder) + "…");
                bundle.putInt(NewEditorActivity.EDITOR_PARAM, NewEditorActivity.USE_NEW_EDITOR);
                bundle.putInt("textColorPrimary", mContext.getResources().getColor(R.color.textColorPrimary));
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedOption);
                bundle.putBoolean("isOverview", true);
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.OVERVIEW_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    /***
     * Method to get friend list.
     *
     * @param url Calling url.
     */
    private void getFriendListFromUrl(String url) {

        pbLoadFriendList.setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject body) {
                pbLoadFriendList.setVisibility(View.GONE);
                if (body != null && body.length() != 0) {
                    mAddPeopleList.clear();
                    JSONArray friendListResponse = body.optJSONArray("response");
                    if (friendListResponse != null && friendListResponse.length() > 0) {
                        for (int i = 0; i < friendListResponse.length(); i++) {
                            JSONObject friendObject = friendListResponse.optJSONObject(i);
                            if (!isSearchHost) {
                                String username = friendObject.optString("label");
                                int userId = friendObject.optInt("id");
                                String userImage = friendObject.optString("image_icon");

                                if (!mShowNonSelectedFriend.isEmpty()) {
                                    if (!mShowNonSelectedFriend.containsKey(userId)) {
                                        mAddPeopleList.add(new AddPeopleList(userId, username, userImage));
                                    }
                                } else {
                                    mAddPeopleList.add(new AddPeopleList(userId, username, userImage));
                                }
                            } else {
                                String username = friendObject.optString("host_title");
                                int userId = friendObject.optInt("host_id");
                                String userImage = friendObject.optString("image_icon");
                                mAddPeopleList.add(new AddPeopleList(userId, username, userImage, friendObject));
                            }
                        }
                        mAddPeopleAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                pbLoadFriendList.setVisibility(View.GONE);
                SnackbarUtils.displaySnackbar(_layout, message);
            }
        });

    }

    /**
     * Method to check Url availability.
     *
     * @param url Url which needs to be check.
     */
    private void checkUrlAvailability(String url) {

        btnCheckUrl.setClickable(false);
        btnCheckUrl.setText(mContext.getResources().getString(R.string.checking_url) + "...");
        mPostParams = new HashMap<>();

        String actionUrl;
        if (mCurrentSelectedOption.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)) {
            actionUrl = AppConstant.DEFAULT_URL + "advancedgroups/groupurlvalidation";
            mPostParams.put("group_uri", url);
        } else {
            actionUrl = AppConstant.DEFAULT_URL + "advancedvideos/channel/channelurl-validation";
            mPostParams.put("channel_uri", url);
        }

        mAppConst.getJsonResponseFromUrl(mAppConst.buildQueryString(actionUrl, mPostParams), new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        btnCheckUrl.setVisibility(View.GONE);
                        btnCheckUrl.setClickable(true);
                        setDrawableRight(R.drawable.ic_done_theme_color, R.color.light_green);
                        SnackbarUtils.displaySnackbarLongTime(_layout,
                                mContext.getResources().getString(R.string.url_available));
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        btnCheckUrl.setVisibility(View.GONE);
                        btnCheckUrl.setClickable(true);
                        setDrawableRight(R.drawable.ic_clear_white, R.color.red);
                        SnackbarUtils.displaySnackbarLongTime(_layout, message);
                    }
                }

        );
    }

    /**
     * Method to set Right Drawable on the edit text when the url is available or not.
     *
     * @param drawableRes Drawable resource id which need to be set.
     * @param colorRes    Color of the drawable.
     */
    private void setDrawableRight(int drawableRes, int colorRes) {
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableRes);
        drawable = drawable.mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, colorRes),
                PorterDuff.Mode.SRC_ATOP));
        etFieldValue.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        etFieldValue.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp));
    }

    /**
     * Class to suggest the locations on the basis of user entered string.
     */
    private class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {

        private ArrayList<String> resultList;

        /**
         * Public constructor to find the places.
         *
         * @param context            Context of calling class.
         * @param textViewResourceId Resource id of the text view.
         */
        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = locationAutoComplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    /**
     * Method to get result list of the locations.
     *
     * @param input Entered location by the user.
     * @return Returns the list of locations on the basis of input.
     */
    private ArrayList<String> locationAutoComplete(String input) {

        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String sb = UrlUtil.PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON + "?key="
                    + mContext.getResources().getString(R.string.places_api_key)
                    + "&input=" + URLEncoder.encode(input, "utf8");
            // sb.append("&components=country:gr");
            //           sb.append("&types=(cities)");

            URL url = new URL(sb);

            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            resultList = new ArrayList<>(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++) {

                JSONObject list = predsJsonArray.optJSONObject(i);
                String value = list.optString("description");
                resultList.add(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

}
