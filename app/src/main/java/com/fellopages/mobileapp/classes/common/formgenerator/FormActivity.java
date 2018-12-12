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
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * FormActivity allows you to create dynamic form layouts based upon a json schema file.
 * This class should be sub-classed.
 *
 * @author Jeremy Brown
 */

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SCHEMA_KEY_TYPE = "type";
    public static final String SCHEMA_KEY_SLIDER = "Slider";
    public static final String SCHEMA_KEY_BOOL_UPPER = "Checkbox";
    public static final String SCHEMA_KEY_BOOL = "checkbox";
    public static final String SCHEMA_KEY_SELECT_UPPER = "Select";
    public static final String SCHEMA_KEY_SELECT = "select";
    public static final String SCHEMA_KEY_STRING_UPPER = "Text";
    public static final String SCHEMA_KEY_STRING = "text";
    public static final String SCHEMA_KEY_PASSWORD = "Password";
    public static final String SCHEMA_KEY_TextArea = "Textarea";
    public static final String SCHEMA_KEY_TextArea_LOWER = "textarea";
    public static final String SCHEMA_KEY_TOGGLES = "toggles";
    public static final String SCHEMA_KEY_DEFAULT = "default";
    public static final String SCHEMA_KEY_OPTIONS = "multiOptions";
    public static final String SCHEMA_KEY_HINT = "label";
    public static final String SCHEMA_KEY_FILE = "File";
    public static final String SCHEMA_KEY_FILE1 = "file";
    public static final String SCHEMA_KEY_FLOAT = "Float";
    public static final String SCHEMA_KEY_INTEGER = "Integer";
    public static final String TAG = "FormActivity";
    public static final String SCHEMA_KEY_RADIO = "Radio";
    public static final String SCHEMA_KEY_MULTI_CHECKBOX = "Multicheckbox";
    public static final String SCHEMA_KEY_MULTI_CHECKBOX_UPPER = "MultiCheckbox";
    public static final String SCHEMA_KEY_MULTI_CHECKBOX_UNDERSCORE = "Multi_checkbox";
    public static final String SCHEMA_KEY_MULTI_SELECT = "Multiselect";
    public static final String SCHEMA_KEY_DATE = "Date";
    public static final String SCHEMA_KEY_DATE_LOWER = "date";
    public static final String SCHEMA_KEY_HIDDEN = "Hidden";
    public static final String SCHEMA_KEY_RATING = "Rating";
    public static final String SCHEMA_KEY_BLOCKED_LIST = "blockList";
    public static final String SCHMEA_KEY_HOST = "host";
    public static final String SCHEMA_KEY_DUMMY = "Dummy";
    public static final String Poll_OPTIONS = "options";
    public static final String Poll_OPTIONS_NAME = "name";
    public static final LayoutParams defaultLayoutParams = CustomViews.getFullWidthLayoutParams();
    public static final LayoutParams defaultLayoutParamsWithMargins = CustomViews.getFullWidthLayoutParams();

    public List<String> pollOptionsList = new ArrayList<>();
    public List<String> pollOptionListCount = new ArrayList<>();

    // -- data
    public static ArrayList<FormWidget> _widgets;
    private FormMultiCheckBox formMultiCheckBox;
    private FormCheckBox mFormCheckBox;
    private FormMultiOptions mFormMultiOptions, mRadioFormMultiOptions, mEndTicketRadioOptions;
    protected Map<String, FormWidget> _map;

    // -- widgets
    private View mAddOptionView;
    protected LinearLayout _container;
    protected static LinearLayout _layout;
    protected ScrollView _viewport;

    // Member variables.
    public Context mContext = this;
    private String mCurrentSelectedOption = null, mAdvancedMemberWhatWhereWithinmile, first_name, last_name;
    private int flag = 3, optionListSize, mContentId;
    private boolean hasBody = false, mIsSpinnerWithIcon = false;
    private JSONArray chequeForm;
    protected JSONObject hostDetails, subCategory, categoryFields, repeatOccurences, formValues = null;

    public static Map<String, String> selectedGuest;
    public static String createDiaryDescription, addToDiaryDescription, hostType, hostKey, host_id,
            subscriptionPriceDescription, subscriptionPlanDescription, overviewText = "";
    public static JSONObject mHostForm, buyerFormValues, _formObject;
    public static JSONArray mHostSelectionForm, mHostCreateForm, mHostCreateFormSocial;
    public static int reset = 0, loadEditHostForm = 0;
    public static Boolean sIsAddToDiaryDescription = false, sIsCreateDiaryDescription = false;
    public static AddPeopleAdapter mAddPeopleAdapter;
    public static JSONArray mTickerHolderForm;
    public static ArrayList<String> selectedProducts, selectedProductTypes;


    // -----------------------------------------------
    //
    // parse data and build view
    //
    // -----------------------------------------------

    /**
     * parses a supplied schema of raw json data and creates widgets
     *
     * @param data - the raw json data as a String
     */
    public View generateForm(JSONObject data, boolean createForm, String currentSelectedOption, int contentId) {

        mContentId = contentId;
        return generateForm(data, createForm, currentSelectedOption);
    }

    /**
     * parses a supplied schema of raw json data and creates widgets
     *
     * @param data      - the raw json data as a String
     * @param firstName -to set first_name value in sign_up form
     * @param lastName  - to set last_name value in sign_up form
     */
    public View generateForm(JSONObject data, boolean createForm, String currentSelectedOption,
                             String firstName, String lastName) {

        first_name = firstName;
        last_name = lastName;
        return generateForm(data, createForm, currentSelectedOption);
    }


    // -----------------------------------------------
    //
    // parse data and build view
    //
    // -----------------------------------------------

    /**
     * parses a supplied schema of raw json data and creates widgets
     *
     * @param data - the raw json data as a String
     */
    public View generateForm(JSONObject data, boolean createForm, String currentSelectedOption) {
        int margin = (int) (getResources().getDimension(R.dimen.margin_5dp) /
                getResources().getDisplayMetrics().density);
        JSONArray formArray = null;
        defaultLayoutParamsWithMargins.setMargins(margin, margin, margin, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            defaultLayoutParamsWithMargins.setMarginEnd(margin);
            defaultLayoutParamsWithMargins.setMarginStart(margin);
        }
        mCurrentSelectedOption = currentSelectedOption;
        _widgets = new ArrayList<>();
        _map = new HashMap<>();
        selectedGuest = new HashMap<>();
        selectedProducts = new ArrayList<>();
        selectedProductTypes = new ArrayList<>();
        sIsAddToDiaryDescription = false;
        sIsCreateDiaryDescription = false;
        FormHostChange.sIsCreateNewHost = false;
        loadEditHostForm = 0;

        try {
            FormWidget widget;
            JSONArray names;
            JSONArray ratingArray = null;

            if (!currentSelectedOption.equals(ConstantVariables.SIGN_UP_FIELDS) && !currentSelectedOption.
                    equals("edit_member_profile")
                    && !currentSelectedOption.equals(ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE)) {
                formArray = data.optJSONArray("form");
                if (formArray == null) {
                    formArray = data.optJSONArray("response");
                    if (currentSelectedOption.equals(ConstantVariables.HOME_MENU_TITLE) && formArray != null) {
                        mIsSpinnerWithIcon = true;
                    }
                }

                if (createForm) {
                    switch (currentSelectedOption) {
                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            hostDetails = data.optJSONObject("host");
                            subCategory = data.optJSONObject("subcategories");
                            categoryFields = data.optJSONObject("fields");
                            repeatOccurences = data.optJSONObject("repeatOccurences");
                            mHostSelectionForm = data.optJSONArray("hostSelectionForm");
                            mHostCreateForm = data.optJSONArray("hostCreateForm");
                            mHostCreateFormSocial = data.optJSONArray("hostCreateFormSocial");
                            break;

                        case ConstantVariables.CREATE_REVIEW:
                            ratingArray = data.optJSONArray("ratingParams");
                            break;

                        case ConstantVariables.STORE_MENU_TITLE:
                        case ConstantVariables.PRODUCT_MENU_TITLE:
                        case ConstantVariables.ADD_PRODUCT:
                        case ConstantVariables.MLT_MENU_TITLE:
                        case ConstantVariables.MLT_VIDEO_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            ratingArray = data.optJSONArray("ratingParams");
                            subCategory = data.optJSONObject("subcategories");
                            if (subCategory == null)
                                subCategory = data.optJSONObject("categoriesForm");
                            categoryFields = data.optJSONObject("fields");
                            break;

                        case ConstantVariables.ADD_TO_WISHLIST:
                            createDiaryDescription = data.optString("create_wishlist_descriptions");
                            addToDiaryDescription = data.optString("add_wishlist_description");
                            break;

                        case "add_to_playlist":
                            createDiaryDescription = data.optString("create_playlist_description");
                            addToDiaryDescription = data.optString("add_playlist_description");
                            break;

                        case ConstantVariables.ADD_TO_DIARY:
                            createDiaryDescription = data.optString("create_diary_descriptions");
                            addToDiaryDescription = data.optString("add_diary_description");
                            break;

                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            subCategory = data.optJSONObject("subCategories");
                            categoryFields = data.optJSONObject("fields");
                            break;

                        case ConstantVariables.USER_MENU_TITLE:
                            mAdvancedMemberWhatWhereWithinmile = data.optString("whatWhereWithinmile");
                            categoryFields = data.optJSONObject("fields");
                            break;

                        case ConstantVariables.ADD_TO_FRIEND_LIST:
                            createDiaryDescription = getResources().getString(R.string.create_list_description);
                            addToDiaryDescription = getResources().getString(R.string.add_to_list_description);
                            break;

                        case ConstantVariables.SETTING_SUBSCRIPTION_TITLE:
                            formArray = data.optJSONArray("subscription_form");
                            subscriptionPlanDescription = data.optString("current_subsciption_plan");
                            subscriptionPriceDescription = data.optString("current_subsciption_price");
                            break;

                        case ConstantVariables.PAYMENT_METHOD:
                            formArray = data.optJSONArray("paymentForm");
                            chequeForm = data.optJSONArray("chequeForm");
                            break;

                        case "userlist":
                        case "multiple_networklist":
                            formArray = data.optJSONArray(currentSelectedOption);
                            break;
                    }
                } else {
                    switch (currentSelectedOption) {
                        case ConstantVariables.SIGN_UP_ACCOUNT:
                            formArray = data.optJSONArray("account");
                            break;

                        case ConstantVariables.SUBSCRIPTION_ACCOUNT:
                            formArray = data.optJSONArray("subscription");
                            break;

                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            hostDetails = data.optJSONObject("host");
                            subCategory = data.optJSONObject("subcategories");
                            categoryFields = data.optJSONObject("fields");
                            repeatOccurences = data.optJSONObject("repeatOccurences");
                            mHostSelectionForm = data.optJSONArray("hostSelectionForm");
                            mHostCreateForm = data.optJSONArray("hostCreateForm");
                            mHostCreateFormSocial = data.optJSONArray("hostCreateFormSocial");
                            formValues = data.optJSONObject("formValues");
                            break;

                        case ConstantVariables.STORE_MENU_TITLE:
                        case ConstantVariables.PRODUCT_MENU_TITLE:
                        case ConstantVariables.MLT_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            ratingArray = data.optJSONArray("ratingParams");
                            subCategory = data.optJSONObject("subcategories");
                            categoryFields = data.optJSONObject("fields");
                            formValues = data.optJSONObject("formValues");
                            break;

                        case ConstantVariables.UPDATE_REVIEW:
                            ratingArray = data.optJSONArray("ratingParams");
                            break;

                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            subCategory = data.optJSONObject("subCategories");
                            categoryFields = data.optJSONObject("fields");
                            formValues = data.optJSONObject("formValues");
                            break;

                        case ConstantVariables.BUYER_FORM:
                            formArray = data.optJSONArray("buyerForm");
                            mTickerHolderForm = data.optJSONArray("tickerHolderForm");
                            break;

                        case ConstantVariables.SHIPPING_METHOD:
                            formArray = data.optJSONArray("editForm");
                            break;

                        case "scheduleForm":
                        case "targetForm":
                        case "sellingForm":
                            formArray = data.optJSONArray(currentSelectedOption);
                            break;
                    }
                }

                if (formArray != null) {

                    // If rating params exists then showing them on top of the form.
                    if (ratingArray != null) {

                        getFormWidgets(ratingArray, createForm);

                    }

                    if (currentSelectedOption.equals(ConstantVariables.SETTING_NOTIFICATIONS)) {

                        // Show Notification types with the category as heading on Notifications Setting page
                        for (int j = 0; j < formArray.length(); j++) {
                            JSONObject jsonObject = formArray.getJSONObject(j);

                            // Added description at the top.
                            if (j == 0) {
                                widget = getTextViewWidget(this, SCHEMA_KEY_STRING,
                                        mContext.getResources().getString(R.string.notification_description), true);
                                _widgets.add(widget);
                            }

                            String category = jsonObject.optString("category");
                            if (category != null) {
                                widget = getTextViewWidget(this, SCHEMA_KEY_STRING, category, true);
                                _widgets.add(widget);
                            }
                            JSONArray typeJsonArray = jsonObject.optJSONArray("types");
                            getFormWidgets(typeJsonArray, createForm);
                        }

                    } else {
                        getFormWidgets(formArray, createForm);
                    }
                }

                if (currentSelectedOption.equals(ConstantVariables.BUYER_FORM) && mTickerHolderForm != null) {

                    getFormWidgets(mTickerHolderForm, createForm);

                } else if (mCurrentSelectedOption.equals(ConstantVariables.PAYMENT_METHOD) && chequeForm != null) {

                    getFormWidgets(chequeForm, createForm);
                }

            } else {
                if (data != null) {

                    if (currentSelectedOption.equals(ConstantVariables.EDIT_MEMBER_PROFILE)
                            || currentSelectedOption.equals(ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE)) {
                        data = data.optJSONObject("form");
                    }
                    names = data.names();

                    for (int i = 0; i < data.length(); i++) {
                        String jsonArrayKey = names.optString(i);

                        if (!jsonArrayKey.equals("") && !jsonArrayKey.isEmpty()) {
                            widget = getTextViewWidget(this, SCHEMA_KEY_STRING, jsonArrayKey, true);
                            _widgets.add(widget);
                        }

                        getFormWidgets(data.getJSONArray(jsonArrayKey), createForm);

                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // -- create the layout

        _container = new LinearLayout(this);
        _container.setOrientation(LinearLayout.VERTICAL);
        _container.setLayoutParams(FormActivity.defaultLayoutParams);

        _viewport = new ScrollView(this);
        _viewport.setLayoutParams(FormActivity.defaultLayoutParams);

        _layout = new LinearLayout(this);
        _layout.setOrientation(LinearLayout.VERTICAL);
        _layout.setLayoutParams(FormActivity.defaultLayoutParams);


        initToggles();

        for (int i = 0; i < _widgets.size(); i++) {

            if (mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE) && !createForm) {
                switch (_widgets.get(i).getPropertyName()) {
                    case "host_title":
                    case "host_description":
                    case "host_photo":
                    case "host_link":
                    case "host_facebook":
                    case "host_twitter":
                    case "host_website":
                        _widgets.get(i).getView().setVisibility(View.GONE);
                        break;
                }
            }

            if(mCurrentSelectedOption.equals(ConstantVariables.ADV_EVENT_PAYMENT_METHOD)){

                formValues = data.optJSONObject("formValues");
                if (formValues != null) {
                    int isByChequeChecked = formValues.optInt("isByChequeChecked");
                    int isPaypalChecked = formValues.optInt("isPaypalChecked");

                    switch (_widgets.get(i).getPropertyName()) {
                        case "email":
                        case "username":
                        case "password":
                        case "signature":
                            _widgets.get(i).getView().setVisibility(isPaypalChecked == 1 ? View.VISIBLE : View.GONE);
                            break;

                        case "bychequeGatewayDetail":
                            _widgets.get(i).getView().setVisibility(isByChequeChecked == 1 ? View.VISIBLE : View.GONE);
                            break;
                    }
                }

            } else if(mCurrentSelectedOption.equals(ConstantVariables.PAYMENT_METHOD_CONFIG)){

                switch (FormActivity.getAttribByProperty(String.valueOf(_widgets.get(i).getPropertyName()),"subType",null)) {
                    case "payment_method":
                        break;
                    default:
                    _widgets.get(i).getView().setVisibility(View.GONE);
                }
            }

            _layout.addView(_widgets.get(i).getView());
            if (mCurrentSelectedOption.equals(ConstantVariables.POLL_MENU_TITLE)
                    && _widgets.get(i).getPropertyName().equals("options_2")) {
                mAddOptionView = inflateAddOptionButton();
                _layout.addView(mAddOptionView);
            }
        }

        switch (mCurrentSelectedOption) {
            case ConstantVariables.POLL_MENU_TITLE:
                optionListSize = pollOptionListCount.size();
                break;

            case ConstantVariables.EVENT_MENU_TITLE:
            case ConstantVariables.GROUP_MENU_TITLE:
            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                final AppCompatCheckedTextView checkBox = (AppCompatCheckedTextView) _layout.findViewById(R.id.form_checkbox);
                if (checkBox != null) {
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkBox.setChecked(!checkBox.isChecked());
                            if (formMultiCheckBox != null) {
                                for (int i = 0; i < formMultiCheckBox.jsonObjectOptions.length(); i++) {
                                    AppCompatCheckedTextView compatCheckBox = formMultiCheckBox.mCheckedTextViewList.get(i);
                                    if (compatCheckBox != null) {
                                        compatCheckBox.setChecked(checkBox.isChecked());
                                    }
                                }
                            }

                            /**
                             * Hide Venue name and Location fields if Online Event checkbox is checked in Adv Events
                             * creation form.
                             */
                            if (mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                                    || mCurrentSelectedOption.equals(ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE)) {
                                for (int i = 0; i < _widgets.size(); i++) {
                                    if (_widgets.get(i).getPropertyName().equals("venue_name") ||
                                            _widgets.get(i).getPropertyName().equals("location")
                                            || _widgets.get(i).getPropertyName().equals("is_same_end_date")) {
                                        _widgets.get(i).getView().setVisibility(checkBox.isChecked() ? View.GONE : View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }

                if (mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                        || mCurrentSelectedOption.equals(ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE)
                        && mFormMultiOptions != null) {
                    mFormMultiOptions.checkFormPublishedOption(true);
                }

                break;

            case ConstantVariables.FORUM_MENU_TITLE:
            case ConstantVariables.CLASSIFIED_MENU_TITLE:
            case ConstantVariables.BLOG_MENU_TITLE:
                Button continueButton = new Button(this);
                if (createForm) {
                    continueButton.setText(getResources().getString(R.string.create_desc_button_title));
                } else {
                    continueButton.setText(getResources().getString(R.string.edit_desc_button_title));
                }
                continueButton.setLayoutParams(defaultLayoutParams);
                continueButton.setId(R.id.add_description);
                if (hasBody) {
                    continueButton.setVisibility(View.VISIBLE);
                } else {
                    continueButton.setVisibility(View.GONE);
                }
                _layout.addView(continueButton);

                break;

            case ConstantVariables.MLT_MENU_TITLE:
                if (mRadioFormMultiOptions != null) {
                    mRadioFormMultiOptions.setOnRadioMultiOptionsClickListener(new FormMultiOptions.RadioMultiOptionsClickListener() {
                        @Override
                        public void onOptionSelected(String value, String name) {
                            if (name.equals("end_date_enable")) {
                                for (int i = 0; i < _widgets.size(); i++) {
                                    if (_widgets.get(i).getPropertyName().equals("end_date")) {
                                        _widgets.get(i).getView().setVisibility(value.equals("0") ? View.GONE : View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }
                if (mFormMultiOptions != null) {
                    mFormMultiOptions.checkFormPublishedOption(true);
                }
                break;

            case ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE:
                LogUtils.LOGD(FormActivity.class.getSimpleName(), "Is mEndTicketRadioOptions null: "+(mEndTicketRadioOptions == null));
                if (mEndTicketRadioOptions != null) {
                    mEndTicketRadioOptions.setOnRadioMultiOptionsClickListener(new FormMultiOptions.RadioMultiOptionsClickListener() {
                        @Override
                        public void onOptionSelected(String value, String name) {
                            LogUtils.LOGD(FormActivity.class.getSimpleName(), "value: " + value + ", name: " + name);
                            if (name.equals("is_same_end_date")) {
                                for (int i = 0; i < _widgets.size(); i++) {
                                    if (_widgets.get(i).getPropertyName().equals("sell_endtime")) {
                                        _widgets.get(i).getView().setVisibility(value.equals("1") ? View.GONE : View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }
                break;

            case ConstantVariables.SITE_PAGE_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                if (mFormMultiOptions != null) {
                    mFormMultiOptions.checkFormPublishedOption(true);
                }
                break;

            case ConstantVariables.HOME_MENU_TITLE:
            case "message":
                LinearLayout.LayoutParams layoutParams = CustomViews.getFullWidthLayoutParams();
                layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                        0, 0);
                LinearLayout uploadVideoLayout = new LinearLayout(this);
                uploadVideoLayout.setOrientation(LinearLayout.VERTICAL);
                uploadVideoLayout.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_8dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
                TextView uploadVideoText = new TextView(this);
                uploadVideoText.setText(getResources().getString(R.string.my_device_video_msg));
                uploadVideoText.setLayoutParams(layoutParams);
                uploadVideoText.setLayoutParams(layoutParams);
                uploadVideoLayout.addView(uploadVideoText);
                Button uploadVideo = new Button(this);
                uploadVideo.setText(getResources().getString(R.string.upload));
                layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                        0, 0);
                uploadVideo.setLayoutParams(layoutParams);
                uploadVideoLayout.addView(uploadVideo);

                // Adding bottom line divider.
                View view = new View(mContext);
                view.setBackgroundResource(R.color.colordevider);
                LinearLayout.LayoutParams dividerLineLayoutParams = CustomViews.getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
                dividerLineLayoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp),
                        0, 0);
                view.setLayoutParams(dividerLineLayoutParams);
                uploadVideoLayout.addView(view);

                uploadVideoLayout.setVisibility(View.GONE);
//                uploadVideoLayout.setId(R.id.upload_video);
                uploadVideoLayout.setTag("filedata");
                uploadVideoLayout.setId(R.id.button);
                _layout.addView(uploadVideoLayout);

                uploadVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent createEntry = new Intent(FormActivity.this, CreateNewEntry.class);
                        List<String> enabledModuleList = null;
                        if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
                            enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
                        }
                        String url;
                        if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                                && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains("sitevideo")) {
                            url = AppConstant.DEFAULT_URL + "advancedvideos/create";
                            createEntry.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_MENU_TITLE);
                        } else {
                            url = AppConstant.DEFAULT_URL + "videos/create";
                            createEntry.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.VIDEO_MENU_TITLE);
                        }
                        createEntry.putExtra(ConstantVariables.CREATE_URL, url);
                        createEntry.putExtra(ConstantVariables.AAF_VIDEO, true);
                        startActivityForResult(createEntry, ConstantVariables.CREATE_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                if (mFormMultiOptions != null) {
                    mFormMultiOptions.checkForVideoUploadingOptions(true);
                }
                break;

            case ConstantVariables.PAYMENT_METHOD:
                if (mRadioFormMultiOptions != null) {
                    mRadioFormMultiOptions.setOnRadioMultiOptionsClickListener(new FormMultiOptions.RadioMultiOptionsClickListener() {
                        @Override
                        public void onOptionSelected(String value, String name) {
                            if (name.equals("method")) {
                                for (int i = 0; i < _widgets.size(); i++) {
                                    if (_widgets.get(i).getPropertyName().equals("cheque_no") ||
                                            _widgets.get(i).getPropertyName().equals("signature") ||
                                            _widgets.get(i).getPropertyName().equals("account_no") ||
                                            _widgets.get(i).getPropertyName().equals("routing_no")) {
                                        _widgets.get(i).getView().setVisibility(value.equals("3") ? View.VISIBLE : View.GONE);
                                    }
                                }
                            }
                        }
                    });
                }
                break;

            case ConstantVariables.VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_VIDEO_MENU_TITLE:
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_ADD_VIDEO:
            case ConstantVariables.SITE_PAGE_ADD_VIDEO:
            case ConstantVariables.SITE_STORE_ADD_VIDEO:
            case ConstantVariables.ADV_GROUPS_ADD_VIDEO:
                if (mFormMultiOptions != null) {
                    mFormMultiOptions.checkForVideoUploadingOptions(true);
                }
                break;

            case ConstantVariables.COMPOSE_MESSAGE:
                if (mFormMultiOptions != null) {
                    mFormMultiOptions.checkForComposeMessageOptions();
                }
                break;

            /*case ConstantVariables.ADV_EVENT_PAYMENT_METHOD:
                if(mFormCheckBox != null){
                    mFormCheckBox.hidePaymentMethodFormInitially();
                }
                break;*/
        }

        _viewport.addView(_layout);
        _container.addView(_viewport);
        _container.setId(R.id.form_layout);

        return _container;
    }

    //Add Poll Options, click on add another options button
    @Override
    public void onClick(View view) {

        if (optionListSize > 0) {
            String fieldName = "options_" + flag;

            _layout.findViewWithTag(fieldName).setVisibility(View.VISIBLE);
            _layout.removeAllViews();
            for (int i = 0; i < _widgets.size(); i++) {
                _layout.addView((_widgets.get(i).getView()));
                if (mCurrentSelectedOption.equals(ConstantVariables.POLL_MENU_TITLE)
                        && _widgets.get(i).getPropertyName().equals(fieldName)) {
                    _layout.addView(mAddOptionView);
                }
            }

            optionListSize--;
            flag++;

            // Hiding add option button when all the options are added into form.
            if (optionListSize == 0 && mAddOptionView != null) {
                mAddOptionView.setVisibility(View.GONE);
            }
        }
    }


    public void getFormWidgets(JSONArray jsonArray, boolean createForm) {
        FormWidget widget;
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject jsonObject = jsonArray.optJSONObject(j);

            String fieldName = jsonObject.optString("name");
            String fieldLabel = jsonObject.optString("label");
            String fieldDescription = jsonObject.optString("description");

            if (jsonObject.optString("type").equals("Checkbox") && fieldName.equals("terms"))
                fieldLabel = jsonObject.optString("description");

            boolean hasValidator = jsonObject.optBoolean("hasValidator");

            boolean toggles = hasToggles(jsonObject);

            widget = getWidget(this, fieldName, jsonObject, fieldLabel, hasValidator, createForm,
                    fieldDescription, _widgets, _map, mCurrentSelectedOption, subCategory, categoryFields, repeatOccurences, null, formValues, mAdvancedMemberWhatWhereWithinmile);

            if (widget == null) continue;

            if (toggles) {
                widget.setToggles(processToggles(jsonObject));
                widget.setToggleHandler(new FormWidgetToggleHandler());
            }

            if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                widget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

            _widgets.add(widget);
            _map.put(fieldName, widget);

        }

    }

    // -----------------------------------------------
    ///
    // populate and save
    //
    // -----------------------------------------------

    /**
     * this method fills the form with existing data
     * get the json string stored in the record we are editing
     * create a json object ( if this fails then we know there is now existing record )
     * create a list of property names from the json object
     * loop through the map returned by the Form class that maps widgets to property names
     * if the map contains the property name as a key that means there is a mFormWidget to populate w/ a value
     */
    public View populate(JSONObject jsonString, String currentSelectedOption) {
        mCurrentSelectedOption = currentSelectedOption;
        if(mCurrentSelectedOption.equals(ConstantVariables.EDIT_PRODUCT) && jsonString.optJSONObject("formValues") != null){
            mContentId = jsonString.optJSONObject("formValues").optInt("store_id",0);
        }
        View view = generateForm(jsonString, false, currentSelectedOption);

        try {
            FormWidget widget;
            switch (currentSelectedOption) {
                case ConstantVariables.SETTING_NOTIFICATIONS: {
                    JSONArray formValues = jsonString.optJSONArray("formValues");
                    for (int i = 0; i < formValues.length(); i++) {
                        String name = formValues.optString(i);
                        if ((!name.equals("photo")) && _map.containsKey(name)) {
                            widget = _map.get(name);
                            widget.setValue("1");
                        }
                    }
                    break;
                }

                case ConstantVariables.SIGN_UP_ACCOUNT: {

                    JSONArray formFields = jsonString.optJSONArray("account");
                    JSONObject formValues = jsonString.optJSONObject("formValues");
                    if (formFields != null && formFields.length() != 0) {
                        for (int j = 0; j < formFields.length(); j++) {
                            JSONObject jsonObject = formFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if ((!name.equals("photo")) && _map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(formValues.optString(name));
                            }
                        }
                    }
                    break;
                }

                case ConstantVariables.SUBSCRIPTION_ACCOUNT: {

                    JSONArray formFields = jsonString.optJSONArray("subscription");
                    JSONObject formValues = jsonString.optJSONObject("formValues");
                    if (formFields != null && formFields.length() != 0) {
                        for (int j = 0; j < formFields.length(); j++) {
                            JSONObject jsonObject = formFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if ((!name.equals("photo")) && _map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(formValues.optString(name));
                            }
                        }
                    }
                    break;
                }

                case ConstantVariables.UPDATE_REVIEW: {
                    JSONArray ratingFields = jsonString.optJSONArray("ratingParams");
                    JSONObject formValues = jsonString.optJSONObject("formValues");
                    JSONArray formFields = jsonString.optJSONArray("form");

                    if (formFields != null && formFields.length() != 0) {
                        for (int j = 0; j < formFields.length(); j++) {
                            JSONObject jsonObject = formFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if (_map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(formValues.optString(name));

                            }
                        }
                    }

                    if (ratingFields != null && ratingFields.length() != 0) {
                        for (int j = 0; j < ratingFields.length(); j++) {
                            JSONObject jsonObject = ratingFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if (_map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(formValues.optString(name));

                            }
                        }
                    }


                    break;
                }

                case ConstantVariables.SETTING_PRIVACY:
                case ConstantVariables.NOTIFICATION_SETTING: {
                    JSONArray formFields = jsonString.optJSONArray("form");
                    JSONObject formValues = jsonString.optJSONObject("formValues");
                    if (formFields != null && formFields.length() != 0) {
                        for (int j = 0; j < formFields.length(); j++) {
                            JSONObject jsonObject = formFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if ((!name.equals("photo")) && _map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(formValues.optString(name));
                            }
                        }
                    }
                    break;

                }

                case ConstantVariables.BUYER_FORM: {
                    JSONArray formFields = jsonString.optJSONArray("buyerForm");
                    buyerFormValues = jsonString.optJSONObject("buyerFormValues");
                    if (formFields != null && formFields.length() != 0) {
                        for (int j = 0; j < formFields.length(); j++) {
                            JSONObject jsonObject = formFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if ((!name.equals("photo")) && _map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(buyerFormValues.optString(name));
                            }
                        }
                    }
                    break;
                }

                default: {
                    JSONArray formFields;
                    if(currentSelectedOption.equals(ConstantVariables.SHIPPING_METHOD)){
                         formFields = jsonString.optJSONArray("editForm");
                    } else {
                        formFields = jsonString.optJSONArray("form");
                    }
                    switch (currentSelectedOption) {
                        case "scheduleForm":
                        case "targetForm":
                        case "sellingForm":
                            formFields = jsonString.optJSONArray(currentSelectedOption);
                            break;
                    }
                    JSONObject formValues = jsonString.optJSONObject("formValues");
                    if (formFields != null && formFields.length() != 0) {
                        for (int j = 0; j < formFields.length(); j++) {
                            JSONObject jsonObject = formFields.optJSONObject(j);
                            String name = jsonObject.optString("name");
                            if ((!name.equals("photo")) && _map.containsKey(name)) {
                                widget = _map.get(name);
                                widget.setValue(formValues.optString(name));
                            }
                            if(name.equals("product_search") && formValues.optJSONArray("product_ids") != null){
                                JSONArray groupedProducts = formValues.optJSONArray("product_ids");
                                for(int x = 0; x < groupedProducts.length(); ++x){
                                    FormActivity.selectedProducts.add(groupedProducts.optJSONObject(x).optString("id"));
                                }
                                widget = _map.get("product_search");
                                widget.setValue(FormActivity.selectedProducts.size()+" products selected.");
                            }
                            if(name.equals("overview")) {
                                overviewText = formValues.optString(name);
                            }
                        }
                    } else if (formValues != null){
                        JSONArray mFieldNames = formValues.names();
                        for (int j = 0; j < formValues.length(); j++) {
                            String fieldName = mFieldNames.optString(j);
                            JSONObject fieldJsonObject = formValues.optJSONObject(fieldName);
                            if ((!fieldName.equals("photo")) && _map.containsKey(fieldName)) {
                                widget = _map.get(fieldName);
                                widget.setValue(fieldJsonObject.getString("value"));
                            }
                        }
                    }

                    if (formValues != null) {
                        JSONObject profileFieldsObject = formValues.optJSONObject("profile_fields");
                        if (profileFieldsObject == null) {
                            JSONArray profileFieldsArray = formValues.optJSONArray("profile_fields");
                            profileFieldsObject = convertToJsonObject(profileFieldsArray);
                        }

                        if (formFields != null) {
                            for (int j = 0; j < formFields.length(); j++) {
                                JSONObject jsonObject = formFields.optJSONObject(j);
                                String name = jsonObject.optString("name");
                                if ((!name.equals("photo")) && _map.containsKey(name) && profileFieldsObject != null) {
                                    widget = _map.get(name);
                                    if (profileFieldsObject.has(name)) {
                                        widget.setValue(profileFieldsObject.optString(name));
                                    } else {
                                        widget.setValue(formValues.optString(name));
                                    }
                                } else {
                                    widget = _map.get(name);
                                    if (formValues.has(name))
                                        if (widget != null) {
                                            widget.setValue(formValues.optString(name));
                                        }
                                }
                            }
                        }
                    }

                    break;
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }

    /**
     * this method preps the data and saves it
     * if there is a problem w/ creating the json string, the method fails
     * loop through each mFormWidget and set a property on a json object to the value of the mFormWidget's getValue() method
     */
    public HashMap<String, String> save() {
        FormWidget widget;
        HashMap<String, String> params = new HashMap<>();

        boolean success = true;
        if (_widgets != null) {
            for (int i = 0; i < _widgets.size(); i++) {
                widget = _widgets.get(i);
                if (mCurrentSelectedOption != null && mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                        && widget.getPropertyName().equals(FormHostChange.sEventHost)) {

                    if (hostType != null) {
                        params.put("host_type", hostType);
                    }
                    if (!FormHostChange.sIsAddNewHost) {
                        params.put("host_id", host_id);
                    }
                } else {
                    if (!widget.getPropertyName().equals("searchGuests")
                            && !widget.getPropertyName().equals("toValues")) {
                        if (!widget.getPropertyName().equals("text")) {
                            if (widget.is_hasValidator() && widget.getValue().isEmpty() &&
                                    widget.getView().getVisibility() == View.VISIBLE) {

                                success = false;
                                widget.setErrorMessage(getResources().getString(R.string.widget_error_msg));

                            } else if (widget.getPropertyName().equals("overview")
                                    && (mCurrentSelectedOption.equals(ConstantVariables.MLT_MENU_TITLE)
                                    || mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                                    || mCurrentSelectedOption.equals(ConstantVariables.STORE_MENU_TITLE))
                                    && overviewText != null) {
                                params.put("overview", overviewText);

                            } else {
                                params.put(widget.getPropertyName(), widget.getValue());
                            }
                        }

                    } else {
                        Set<String> keySet = selectedGuest.keySet();

                        String res = "";
                        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                            res += iterator.next() + (iterator.hasNext() ? "," : "");
                        }

                        params.put("toValues", res);
                    }
                }
            }
        }

        if (success) {
            return params;
        }

        return null;
    }

    public boolean showValidations(JSONObject validationMessages) {

        FormWidget widget;
        boolean error = false;
        for (int i = 0; i < _widgets.size(); i++) {
            widget = _widgets.get(i);
            String fieldName = widget.getPropertyName();

            if (validationMessages.has(fieldName)) {

                try {
                    String message = validationMessages.getString(fieldName);
                    widget.setErrorMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                error = true;
            }
        }
        return error;
    }

    // -----------------------------------------------
    //
    // toggles
    //
    // -----------------------------------------------

    /**
     * creates the map a map of values for visibility and references to the widgets the value affects
     */
    protected HashMap<String, ArrayList<String>> processToggles(JSONObject property) {
        try {
            ArrayList<String> toggled;
            HashMap<String, ArrayList<String>> toggleMap = new HashMap<>();

            JSONObject toggleList = property.getJSONObject(FormActivity.SCHEMA_KEY_TOGGLES);
            JSONArray toggleNames = toggleList.names();

            for (int j = 0; j < toggleNames.length(); j++) {
                String toggleName = toggleNames.getString(j);
                JSONArray toggleValues = toggleList.getJSONArray(toggleName);
                toggled = new ArrayList<>();
                toggleMap.put(toggleName, toggled);
                for (int k = 0; k < toggleValues.length(); k++) {
                    toggled.add(toggleValues.getString(k));
                }
            }

            return toggleMap;

        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * returns a boolean indicating that the supplied json object contains a property for toggles
     */
    protected boolean hasToggles(JSONObject obj) {
        try {
            obj.getJSONObject(FormActivity.SCHEMA_KEY_TOGGLES);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * initializes the visibility of widgets that are togglable
     */
    protected void initToggles() {
        int i;
        FormWidget widget;

        for (i = 0; i < _widgets.size(); i++) {
            widget = _widgets.get(i);
            updateToggles(widget);
        }
    }

    /**
     * updates any widgets that need to be toggled on or off
     *
     * @param widget
     */
    protected void updateToggles(FormWidget widget) {
        int i;
        String name;
        ArrayList<String> toggles;
        ArrayList<FormWidget> ignore = new ArrayList<>();

        toggles = widget.getToggledOn();
        for (i = 0; i < toggles.size(); i++) {
            name = toggles.get(i);
            if (_map.get(name) != null) {
                FormWidget toggle = _map.get(name);
                ignore.add(toggle);
                toggle.setVisibility(View.VISIBLE);
            }
        }

        toggles = widget.getToggledOff();
        for (i = 0; i < toggles.size(); i++) {
            name = toggles.get(i);
            if (_map.get(name) != null) {
                FormWidget toggle = _map.get(name);
                if (ignore.contains(toggle)) continue;
                toggle.setVisibility(View.GONE);
            }
        }
    }


    /**
     * simple callbacks for widgets to use when their values have changed
     */
    class FormWidgetToggleHandler {
        public void toggle(FormWidget widget) {
            updateToggles(widget);
        }
    }


    /**
     * factory method for actually instantiating widgets
     */

    protected FormWidget getWidget(Context context, String name, JSONObject property, String label, boolean hasValidator,
                                   boolean createForm, String description, ArrayList<FormWidget> _widgets,
                                   Map<String, FormWidget> _map, String currentSelectedOption, JSONObject subCategory,
                                   JSONObject categoryFields, JSONObject repeatOccurences, JSONObject subSubCategory, JSONObject formValues, String mAdvancedMemberWhatWhereWithinmile) {
        JSONArray jsonArray;
        JSONObject jsonObject;
        String value;
        mCurrentSelectedOption = currentSelectedOption;
        if (property != null) {
            String type = property.optString(FormActivity.SCHEMA_KEY_TYPE);

            switch (type) {

                case FormActivity.SCHEMA_KEY_PASSWORD:
                case FormActivity.SCHEMA_KEY_SLIDER:
                case FormActivity.SCHEMA_KEY_FLOAT:
                case FormActivity.SCHEMA_KEY_INTEGER:
                case FormActivity.SCHEMA_KEY_STRING:
                case FormActivity.SCHEMA_KEY_STRING_UPPER:
                    if (mCurrentSelectedOption.equals(ConstantVariables.POLL_MENU_TITLE)) {
                        String optionName = property.optString(FormActivity.Poll_OPTIONS_NAME);

                        if (optionName.startsWith(FormActivity.Poll_OPTIONS)) {
                            pollOptionsList.add(optionName);

                            if (pollOptionsList.size() < 3) {
                                return new FormEditText(context, name, property, description, hasValidator,
                                        type, property.optString("inputType"), _widgets, _map, mContentId,
                                        createForm, false, property.optString("value"), mCurrentSelectedOption);

                            } else {
                                pollOptionListCount.add(optionName);
                                return new FormEditText(context, name, property, description, hasValidator,
                                        type, property.optString("inputType"), _widgets, _map, mContentId,
                                        createForm, true, property.optString("value"), mCurrentSelectedOption);
                            }

                        } else {
                            return new FormEditText(context, name, property, description, hasValidator, type,
                                    property.optString("inputType"), _widgets, _map, mContentId, createForm,
                                    false, property.optString("value"), mCurrentSelectedOption);

                        }
                    } else {
                        if (name.contains("first_name")) {
                            value = first_name;
                        } else if (name.contains("last_name")) {
                            value = last_name;
                        } else {
                            value = property.optString("value");
                        }
                        return new FormEditText(context, name, property, description, hasValidator, type,
                                property.optString("inputType"), _widgets, _map, mContentId, createForm,
                                false, value, mCurrentSelectedOption);
                    }

                case FormActivity.SCHEMA_KEY_BOOL:
                case FormActivity.SCHEMA_KEY_BOOL_UPPER:
                    return new FormCheckBox(context, name, label, hasValidator, property.optInt("value"), _widgets, mCurrentSelectedOption);

                case FormActivity.SCHEMA_KEY_FILE:
                case FormActivity.SCHEMA_KEY_FILE1:
                    return new FormPicker(context, name, mCurrentSelectedOption, createForm);

                case FormActivity.SCHEMA_KEY_RADIO:
                    jsonObject = property.optJSONObject(FormActivity.SCHEMA_KEY_OPTIONS);
                    if (jsonObject == null) {
                        jsonArray = property.optJSONArray(FormActivity.SCHEMA_KEY_OPTIONS);
                        jsonObject = convertToJsonObject(jsonArray);
                    }
                    if (jsonObject != null && jsonObject.length() != 0) {
                        if (mCurrentSelectedOption.equals(ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE)
                                && name.equals("is_same_end_date")) {
                            mEndTicketRadioOptions = new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                    description, property, mCurrentSelectedOption);
                            return mEndTicketRadioOptions;

                        } else if (mCurrentSelectedOption.equals(ConstantVariables.SHIPPING_METHOD)) {
                            return new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                    description, property, mCurrentSelectedOption);

                        } else {
                            mRadioFormMultiOptions = new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                    description, property, mCurrentSelectedOption);
                            return mRadioFormMultiOptions;
                        }
                    }

                case FormActivity.SCHEMA_KEY_MULTI_CHECKBOX_UPPER:
                case FormActivity.SCHEMA_KEY_MULTI_CHECKBOX:
                case FormActivity.SCHEMA_KEY_MULTI_CHECKBOX_UNDERSCORE:
                case FormActivity.SCHEMA_KEY_MULTI_SELECT:

                    jsonObject = property.optJSONObject(FormActivity.SCHEMA_KEY_OPTIONS);
                    if (jsonObject == null) {
                        jsonArray = property.optJSONArray(FormActivity.SCHEMA_KEY_OPTIONS);
                        jsonObject = convertToJsonObject(jsonArray);
                    }
                    if (jsonObject != null && jsonObject.length() != 0) {
                        formMultiCheckBox = new FormMultiCheckBox(context, name, jsonObject, label, hasValidator, description, _widgets, _map, property);
                        formMultiCheckBox.setValue(property.optString("value"));
                        return formMultiCheckBox;
                    }

                case FormActivity.SCHEMA_KEY_SELECT:
                case FormActivity.SCHEMA_KEY_SELECT_UPPER:
                    if (property.has(FormActivity.SCHEMA_KEY_OPTIONS)) {
                        jsonObject = property.optJSONObject(FormActivity.SCHEMA_KEY_OPTIONS);
                        if (jsonObject == null) {
                            jsonArray = property.optJSONArray(FormActivity.SCHEMA_KEY_OPTIONS);
                            jsonObject = convertToJsonObject(jsonArray);
                        }
                        if (jsonObject != null && jsonObject.length() != 0) {

                            switch (mCurrentSelectedOption) {
                                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                                    if (createForm && name.equals("type")
                                            && CreateNewEntry.getInstance().mIsAAFVideoUpload) {
                                        try {
                                            property.put("value", "3");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                                case ConstantVariables.MLT_MENU_TITLE:
                                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                                case ConstantVariables.USER_MENU_TITLE:
                                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                                case ConstantVariables.STORE_MENU_TITLE:
                                case ConstantVariables.PRODUCT_MENU_TITLE:
                                case ConstantVariables.ADD_PRODUCT:
                                    mFormMultiOptions = new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                            description, property, mCurrentSelectedOption, createForm,
                                            subCategory, subSubCategory, categoryFields, repeatOccurences, formValues, _widgets,
                                            _map, mAdvancedMemberWhatWhereWithinmile, mIsSpinnerWithIcon);
                                    return mFormMultiOptions;

                                case ConstantVariables.COMPOSE_MESSAGE:
                                    mFormMultiOptions = new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                            description, property, mCurrentSelectedOption, createForm,
                                            null, null, null, null, null, _widgets, _map, null, mIsSpinnerWithIcon);
                                    return mFormMultiOptions;

                                case ConstantVariables.VIDEO_MENU_TITLE:
                                    if (createForm && name.equals("type")
                                            && CreateNewEntry.getInstance().mIsAAFVideoUpload) {
                                        try {
                                            property.put("value", "3");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    mFormMultiOptions = new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                            description, property, mCurrentSelectedOption, createForm,
                                            null, null, null, null, null, _widgets, _map, null, mIsSpinnerWithIcon);
                                    return mFormMultiOptions;

                                case ConstantVariables.SHIPPING_METHOD:
                                    return new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                            description, property, mCurrentSelectedOption, createForm,
                                            subCategory, subSubCategory, categoryFields, repeatOccurences, formValues, _widgets,
                                            _map, mAdvancedMemberWhatWhereWithinmile, mIsSpinnerWithIcon);

                                default:
                                    mFormMultiOptions = new FormMultiOptions(context, name, jsonObject, label, hasValidator,
                                            description, property, mCurrentSelectedOption, createForm,
                                            null, null, null, null, null, _widgets, _map, null, mIsSpinnerWithIcon);
                                    return mFormMultiOptions;
                            }
                        }
                    }
                    break;

                case FormActivity.SCHEMA_KEY_DATE:
                case FormActivity.SCHEMA_KEY_DATE_LOWER:
                    return new FormPicker(context, name, hasValidator, property.optString("format"));

                case FormActivity.SCHEMA_KEY_RATING:
                    return new FormPicker(context, name, label, hasValidator, true);

                case FormActivity.SCHEMA_KEY_BLOCKED_LIST:
                    return new FormBlockedUsers(this, name, label, hasValidator, description);

                case FormActivity.SCHEMA_KEY_HIDDEN:
                    return new FormCheckBox(context, name, label, hasValidator, property.optInt("value"), _widgets, mCurrentSelectedOption);

                case FormActivity.SCHEMA_KEY_TextArea:
                case FormActivity.SCHEMA_KEY_TextArea_LOWER:
                    if (mCurrentSelectedOption.equals(ConstantVariables.BLOG_MENU_TITLE) ||
                            mCurrentSelectedOption.equals(ConstantVariables.CLASSIFIED_MENU_TITLE)
                            || mCurrentSelectedOption.equals(ConstantVariables.FORUM_MENU_TITLE)) {
                                /* We will not return any view in this case because editor
                                 will be used for adding description */
                        hasBody = true;
                        break;
                    } else {
                        value = property.optString("value");
                        return new FormEditText(context, name, property, description, hasValidator, type,
                                property.optString("inputType"), _widgets, _map, mContentId, createForm,
                                false, value, mCurrentSelectedOption);
                    }

                case FormActivity.SCHMEA_KEY_HOST:
                    name = FormHostChange.sEventHost;
                    mHostForm = property;
                    return new FormHostChange(context, name, property, createForm, _widgets, _map, mCurrentSelectedOption);

                case FormActivity.SCHEMA_KEY_DUMMY:
                    return new FormTextView(context, name, hasValidator, false, label, property, currentSelectedOption, _widgets);

            }
        } else {
            return new FormTextView(context, name, hasValidator, false, label, null, currentSelectedOption, _widgets);
        }
        return null;
    }

    /**
     * Method to inflate the add poll option button layout.
     *
     * @return Returns the inflated layout with the add option button.
     */
    private View inflateAddOptionButton() {

        LinearLayout.LayoutParams layoutParams = CustomViews.getFullWidthLayoutParams();
        layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp), 0, 0);
        LinearLayout addPollOptionLayout = new LinearLayout(this);
        addPollOptionLayout.setOrientation(LinearLayout.VERTICAL);

        Button addAnotherOption = new Button(this);
        addAnotherOption.setText(getResources().getString(R.string.add_another_poll_option));
        addAnotherOption.setLayoutParams(layoutParams);
        addAnotherOption.setOnClickListener(this);
        addPollOptionLayout.addView(addAnotherOption);

        // Adding bottom line divider.
        View view = new View(mContext);
        view.setBackgroundResource(R.color.colordevider);
        LinearLayout.LayoutParams dividerLineLayoutParams = CustomViews.getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
        dividerLineLayoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_12dp),
                0, 0);
        view.setLayoutParams(dividerLineLayoutParams);
        addPollOptionLayout.addView(view);
        return addPollOptionLayout;
    }

    protected FormWidget getTextViewWidget(Context context, String name, String label, boolean isNeedToAddPadding) {
        return new FormTextView(context, name, false, isNeedToAddPadding, label, null, mCurrentSelectedOption, _widgets);
    }

    /**
     * Method to convert jsonArray in JsonObject
     *
     * @param jsonArray JsonArray to convert in JsonObject
     * @return Converted JsonObject
     */
    public JSONObject convertToJsonObject(JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    String value = jsonArray.optString(i);
                    jsonObject.put("" + i, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        } else {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FormMultiOptions.resetFormVariables();
    }
    public static String getAttribByProperty(String propertyName,String attributeName,String isFieldsKey){
        if(isFieldsKey != null) {
            JSONArray formArray = _formObject.optJSONObject("fields").optJSONArray(isFieldsKey);
            for (int i = 0; i < formArray.length(); i++) {
                if (formArray.optJSONObject(i).opt("name").equals(propertyName)) {
                    return formArray.optJSONObject(i).optString(attributeName);
                }
            }
        } else {
            JSONArray formArray = _formObject.optJSONArray("form");
            for (int i = 0; i < formArray.length(); i++) {
                if (formArray.optJSONObject(i).opt("name").equals(propertyName)) {
                    return formArray.optJSONObject(i).optString(attributeName);
                }
            }
        }
        return null;
    }
    public static void setFormObject(JSONObject form){
        _formObject = form;
    }
    public static JSONObject getFormObject(){
       return _formObject;
    }
    public static boolean setAttribByProperty(String propertyName,String attributeName,String attributeValue){
        JSONArray formArray = _formObject.optJSONArray("form");
        for(int i = 0; i < formArray.length(); i++){
            if(formArray.optJSONObject(i).opt("name").equals(propertyName)){
                 try {
                     formArray.optJSONObject(i).put(attributeName, attributeValue);
                     return true;
                 }catch (JSONException e){
                     return false;
                 }
            }
        }
        return false;
    }
    public static boolean setRegistryByProperty(String propertyName,JSONObject parent,String child){
        try {
            JSONObject element = new JSONObject();
            String parentKey = (parent != null) ? parent.optString("parent") : null;
            if(child != null && !child.trim().equals("")) {
                element.put("parent",parentKey);
                element.put("child",child);
                _formObject.putOpt(propertyName, element);

                JSONArray subFormElements = _formObject.optJSONObject("fields").optJSONArray(child);
                if(subFormElements != null) {
                    for (int i = 0; i < subFormElements.length(); i++) {
                        if (subFormElements.optJSONObject(i).optBoolean("hasSubForm")) {
                            JSONObject childElement = new JSONObject();
                            childElement.put("parent", propertyName);
                            //setRegistryByProperty(subFormElements.optJSONObject(i).optString("name"),childElement,null);
                            _formObject.putOpt(subFormElements.optJSONObject(i).optString("name"), childElement);
                        }
                    }
                }

            } else if(parentKey != null) {
                element.put("parent",parentKey);
                element.put("child",child);
                _formObject.putOpt(propertyName, element);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static String getRegistryByProperty(String propertyName,String key){
        if(getFormObject().optJSONObject(propertyName) != null){
            return getFormObject().optJSONObject(propertyName).optString(key);
        }
        return null;
    }



}
