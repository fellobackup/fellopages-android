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
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.adapters.SimpleSheetAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.SheetItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * FormMultiOptions is used to inflate the view for the radio buttons and spinner item which contains the multi-options.
 * Bottom sheet view is used to show the multi options.
 */

public class FormMultiOptions extends FormWidget implements View.OnClickListener {

    //Member variables.
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private EditText etFieldValue;
    private TextView tvError, tvAddNew;
    private AppCompatCheckedTextView isMonthlyTypeCheck;
    private List<SheetItemModel> mOptionsItemList = new ArrayList<>();
    private BottomSheetDialog mBottomSheetDialog;
    private SimpleSheetAdapter mSheetAdapter;
    private String mCurrentSelectedOption, mFieldName, mFieldValue = "", mAdvancedMemberWhatWhereWithinmile,
            mFieldCategoryLevel, mWidgetProperty;
    private JSONObject jsonObjectOptions, joProperty, joSubCategory, joSubSubCategory, joCategoryFields,
            joRepeatOccurrences, joFormValues;
    private ArrayList<FormWidget> mFormWidgetList;
    private Map<String, FormWidget> mFormWidgetMap;
    private FormWidget mFormWidget;
    private FormActivity mFormActivity;
    private RadioMultiOptionsClickListener mRadioMultiOptionsClickListener;
    private boolean isCreateForm, canChangeProfileFields = true, isFirstTimeLoadingFromEdit = true,
            mIsOptionWithIcon = false;
    private boolean isRemoveRepeatEvent = true;
    private int repeat_e_position = 0, size = 0, elementOrder = 0;

    public static int repeat_s_position = 0, sEditPageLoadingTime = 0, sEditPageLoadingTimeRepeatEvent = 0;
    public static int sPosition = 0, ePosition = 0, subSPosition = 0, subSubSPosition = 0;
    public static boolean isCategoryProfileFields = false, isSubCategoryProfileFields = false,
            isSubSubCategoryProfileFields = false, hasSubCategory = false, hasSubSubCategory = false;
    public static boolean sIsChangeOccurence = false, sIsChangedMonthlyField = false,
            sIsEventRepeatTypeMonthly = false;


    /**
     * Public constructor to inflate form field For the radio button items.
     *
     * @param context               Context of calling class.
     * @param property              Property of the field.
     * @param options               Object with multi options.
     * @param label                 Label of the field.
     * @param hasValidator          True if the field has validation (Compulsory field).
     * @param description           Description of the field.
     * @param jsonObjectProperty    Json object of the selected property.
     * @param currentSelectedOption Current selected module.
     */
    public FormMultiOptions(final Context context, String property, JSONObject options, String label,
                            boolean hasValidator, String description, JSONObject jsonObjectProperty,
                            String currentSelectedOption) {
        super(context, property, hasValidator);

        // Initializing member variables.
        mContext = context;
        mFieldName = property;
        jsonObjectOptions = options;
        this.joProperty = jsonObjectProperty;
        mCurrentSelectedOption = currentSelectedOption;

        initializeConstructorValues(label, description);

    }

    /**
     * Public constructor to inflate form field For the Spinner items.
     *
     * @param context                           Context of calling class.
     * @param property                          Property of the field.
     * @param options                           Object with multi options.
     * @param label                             Label of the field.
     * @param hasValidator                      True if the field has validation (Compulsory field).
     * @param description                       Description of the field.
     * @param jsonObjectProperty                Json object of the selected property.
     * @param currentSelectedOption             Current selected module.
     * @param isCreateForm                      True if the form is loaded for creation.
     * @param subCategory                       Json Object of the subcategory.
     * @param subSubCategory                    Json Object of the subSubCategory.
     * @param categoryFields                    Json Object of the category fields.
     * @param repeatOccurrences                 Json Object of the repeat Occurrences.
     * @param formValues                        Json Object of the form Values.
     * @param widgets                           List of FormWidget.
     * @param map                               Map of field name and formWidget.
     * @param advancedMemberWhatWhereWithinmile AdvancedMember text.
     * @param isOptionWithIcon                  True if need to show options with the icon.
     */
    public FormMultiOptions(final Context context, String property, JSONObject options, String label,
                            boolean hasValidator, String description, JSONObject jsonObjectProperty,
                            String currentSelectedOption, boolean isCreateForm, JSONObject subCategory,
                            JSONObject subSubCategory, JSONObject categoryFields, JSONObject repeatOccurrences,
                            JSONObject formValues, ArrayList<FormWidget> widgets, Map<String, FormWidget> map,
                            String advancedMemberWhatWhereWithinmile, boolean isOptionWithIcon) {

        super(context, property, hasValidator);

        // Initializing member variables.
        this.mContext = context;
        this.mFieldName = property;
        this.jsonObjectOptions = options;
        this.joProperty = jsonObjectProperty;
        this.mCurrentSelectedOption = currentSelectedOption;
        this.isCreateForm = isCreateForm;
        this.joSubCategory = subCategory;
        this.joSubSubCategory = subSubCategory;
        this.joCategoryFields = categoryFields;
        this.joRepeatOccurrences = repeatOccurrences;
        this.joFormValues = formValues;
        this.mFormWidgetList = widgets;
        this.mFormWidgetMap = map;
        mAdvancedMemberWhatWhereWithinmile = advancedMemberWhatWhereWithinmile;
        this.mIsOptionWithIcon = isOptionWithIcon;
        mFormActivity = new FormActivity();

        initializeConstructorValues(label, description);
    }

    /**
     * Method to initialize the constructor values and  inflate the field view accordingly.
     *
     * @param label       Label of the field.
     * @param description Description of the field.
     */
    private void initializeConstructorValues(String label, String description) {
        // Initializing member variables.
        mLayoutInflater = ((Activity) mContext).getLayoutInflater();

        // Fetching all keys from json object and adding data into list.
        Iterator<?> keys = jsonObjectOptions.keys();
        if (keys != null) {
            while (keys.hasNext()) {
                String key = (String) keys.next();
                switch (mFieldName) {
                    case "album":
                        if (key.equals("0")) {
                            mOptionsItemList.add(new SheetItemModel(getLabelFromKey(key), key));
                        }
                        break;
                    case "main_channel_id":
                        mOptionsItemList.add(new SheetItemModel(getLabelFromKey(key), key));
                        if (key.equals("0")) {
                            mOptionsItemList.add(new SheetItemModel(mContext.getResources().getString(R.string.title_activity_create_new_channel),
                                    "create_channel"));
                        }
                        break;
                    default:
                        mOptionsItemList.add(new SheetItemModel(getLabelFromKey(key), key));
                        break;
                }
            }
        }


        // Setting up the adapter.
        setAdapter();
        // Inflate the field view layout.
        View configFieldView = mLayoutInflater.inflate(R.layout.layout_form_select_option, null);
        getViews(configFieldView, label, description);
        _layout.addView(configFieldView);
        configFieldView.setTag(mFieldName);
        getWidgetPropertyName();

        // Setting up the value in the field value view when it is coming in response.
        if (joProperty != null && joProperty.length() > 0
                && jsonObjectOptions != null && jsonObjectOptions.length() > 0 && etFieldValue != null
                && joProperty.optString("value") != null && !joProperty.optString("value").isEmpty()) {
            String value = joProperty.optString("value");
            etFieldValue.setText(jsonObjectOptions.optString(value));
            etFieldValue.setTag(value);
            mFieldValue = value;
            checkVisibility();
        }

    }

    /**
     * Method to set option item click listener on radio button multi options.
     *
     * @param listener Listener instance from  calling class.
     */
    public void setOnRadioMultiOptionsClickListener(RadioMultiOptionsClickListener listener) {
        mRadioMultiOptionsClickListener = listener;
        LogUtils.LOGD(FormMultiOptions.class.getSimpleName(), "Is mRadioMultiOptionsClickListener null: "+(mRadioMultiOptionsClickListener == null));
        checkVisibility();
    }

    /**
     * Method to set adapter for the list items.
     */
    private void setAdapter() {
        if (!mIsOptionWithIcon) {
            mSheetAdapter = new SimpleSheetAdapter(mContext, mOptionsItemList);
        } else {
            mSheetAdapter = new SimpleSheetAdapter(mContext, mOptionsItemList, true);
        }
        mSheetAdapter.setOnItemClickListener(new SimpleSheetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SheetItemModel item, int position) {
                mBottomSheetDialog.dismiss();
                LogUtils.LOGD(FormMultiOptions.class.getSimpleName(), "onItemClick, Key: " + item.getKey());
                LogUtils.LOGD(FormMultiOptions.class.getSimpleName(), "onItemClick, Name: " + item.getName());

                if (item.getKey().equals("create_channel")) {
                    Intent intent = new Intent(mContext, CreateNewEntry.class);
                    intent.putExtra(ConstantVariables.CREATE_URL, AppConstant.DEFAULT_URL + "advancedvideos/channel/create");
                    intent.putExtra("add_to_new_channel", true);
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE);
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.UPDATE_REQUEST_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (mCurrentSelectedOption.equals("targetForm")
                        && (mFieldName.equals("min_age") || mFieldName.equals("max_age"))) {

                    etFieldValue.setText(item.getName());
                    etFieldValue.setTag(item.getKey());
                    tvError.setError(null);
                    tvError.setVisibility(View.GONE);

                    View minAgeConfigView = FormActivity._layout.findViewWithTag("min_age");
                    View maxAgeConfigView = FormActivity._layout.findViewWithTag("max_age");
                    EditText etMinAge = null, etMaxAge = null;
                    int minAge = 0, maxAge = 0;
                    if (minAgeConfigView != null && maxAgeConfigView != null) {
                        etMinAge = minAgeConfigView.findViewById(R.id.field_value);
                        etMaxAge = maxAgeConfigView.findViewById(R.id.field_value);
                        minAge = etMinAge.getTag() != null ? Integer.parseInt(etMinAge.getTag().toString()) : 0;
                        maxAge = etMaxAge.getTag() != null ? Integer.parseInt(etMaxAge.getTag().toString()) : 0;
                    }

                    int value = Integer.parseInt(item.getKey());

                    if (mFieldName.equals("min_age")) {
                        if (value > maxAge && maxAge != 0) {
                            SnackbarUtils.displaySnackbar(minAgeConfigView, mContext.getResources().getString(R.string.min_max_age_message));

                            if (etMinAge != null) {
                                etMinAge.setText("");
                                mSheetAdapter.setDefaultKey("0");
                                SheetItemModel sheetItemModel = mOptionsItemList.get(position);
                                sheetItemModel.setKey(String.valueOf(value));
                                mSheetAdapter.notifyDataSetChanged();
                            }
                        }

                    } else if (mFieldName.equals("max_age")) {
                        if (value < minAge && value != 0) {
                            if (etMaxAge != null) {
                                etMaxAge.setText("");
                                mSheetAdapter.setDefaultKey("0");
                                SheetItemModel sheetItemModel = mOptionsItemList.get(position);
                                sheetItemModel.setKey(String.valueOf(value));
                                mSheetAdapter.notifyDataSetChanged();
                            }
                            SnackbarUtils.displaySnackbar(minAgeConfigView, mContext.getResources().getString(R.string.max_min_age_message));
                        }
                    }

                } else {
                    etFieldValue.setText(item.getName());
                    etFieldValue.setTag(item.getKey());
                    tvError.setError(null);
                    tvError.setVisibility(View.GONE);
                    checkVisibility();
                    // Not performing any action when the same option is selected.
                    if (!mFieldValue.equals(item.getKey())) {
                        mFieldValue = item.getKey();

                        // Performing view creation on multi option selection for the specific modules.
                        if (joProperty != null
                                && (joProperty.optString("type").equals(FormActivity.SCHEMA_KEY_SELECT)
                                || joProperty.optString("type").equals(FormActivity.SCHEMA_KEY_SELECT_UPPER))) {

                            if (joProperty.optBoolean("hasSubForm",false)) {
                                inflateSubFormView(item.getKey());
                            } else {
                                checkForFieldOnOptionSelection(item.getKey());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Method to get views from the form layout and set data in views..
     *
     * @param configFieldView View which is inflated.
     * @param label           Label of the field.
     * @param description     Description of the field.
     */
    private void getViews(View configFieldView, String label, String description) {

        // Getting label, description and field value views.
        TextView tvLabel = configFieldView.findViewById(R.id.view_label);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        TextView tvDescription = configFieldView.findViewById(R.id.view_description);
        LinearLayout llTitleFields = configFieldView.findViewById(R.id.title_fields_layout);
        etFieldValue = configFieldView.findViewById(R.id.field_value);
        etFieldValue.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        tvError = configFieldView.findViewById(R.id.error_view);

        // Setting up click listener on form view.
        // Setting up click listener on form view.
        if  (!joProperty.optBoolean("isDisable", false) ) {
            etFieldValue.setOnClickListener(this);
            configFieldView.findViewById(R.id.form_main_view).setOnClickListener(this);
        } else {
            configFieldView.setEnabled(false);
            configFieldView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_light));
        }

        // Setting up data in views.
        tvLabel.setText(label);

        //TODO, Uncomment this when ever the description is needed.
        // Showing description field if it is coming in response.
//        if (description != null && !description.isEmpty()) {
//            tvDescription.setVisibility(View.VISIBLE);
//            tvDescription.setText(description);
//        } else {
//            tvDescription.setVisibility(View.GONE);
//        }

        // Showing title field layout for the subscription.
        if (mCurrentSelectedOption.equals(ConstantVariables.SETTING_SUBSCRIPTION_TITLE)) {
            TextView tvSubscriptionPlan = new TextView(mContext);
            TextView tvSubscriptionPrice = new TextView(mContext);
            tvSubscriptionPlan.setText(mContext.getResources().getString(R.string.subscription_plan_content_prefix)
                    + " " + FormActivity.subscriptionPlanDescription);
            tvSubscriptionPrice.setText(mContext.getResources().getString(R.string.subscription_plan_price_prefix)
                    + " " + FormActivity.subscriptionPriceDescription);
            tvSubscriptionPlan.setPadding(0, 0, 0, 5);
            tvSubscriptionPrice.setPadding(0, 0, 0, 5);
            llTitleFields.addView(tvSubscriptionPlan);
            llTitleFields.addView(tvSubscriptionPrice);
            llTitleFields.setVisibility(View.VISIBLE);
        } else {
            llTitleFields.setVisibility(View.GONE);
        }

        if (mFieldName.equals("host_type_select")) {
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(CustomViews.getWrapLayoutParams());

            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            SelectableTextView _label = new SelectableTextView(mContext);
            _label.setText(label);
            _label.setLayoutParams(FormActivity.defaultLayoutParamsWithMargins);

            TextView tvCancel = new TextView(mContext);
            tvCancel.setId(R.id.change_host);
            tvCancel.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            tvCancel.setLayoutParams(FormActivity.defaultLayoutParamsWithMargins);
            tvCancel.setText("[ " + mContext.getResources().getString(R.string.cancel_dialogue_message) + " ]");

            tvAddNew = new TextView(mContext);
            tvAddNew.setId(R.id.add_new_host);
            tvAddNew.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            tvAddNew.setLayoutParams(FormActivity.defaultLayoutParamsWithMargins);
            tvAddNew.setText("[ " + mContext.getResources().getString(R.string.add_new_text) + " ]");
            tvAddNew.setVisibility(View.GONE);

            tvAddNew.setOnClickListener(this);
            tvCancel.setOnClickListener(this);

            linearLayout.addView(_label);
            linearLayout.addView(tvAddNew);
            linearLayout.addView(tvCancel);
            _layout.addView(linearLayout);
        }
    }

    /**
     * Method to check set property name for the profile field generation.
     */
    private void getWidgetPropertyName() {
        switch (mCurrentSelectedOption) {
            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_MENU_TITLE:
                mWidgetProperty = "tags";
                break;

            case ConstantVariables.MLT_MENU_TITLE:
                mWidgetProperty = "auth_view";
                break;

            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                mWidgetProperty = "body";
                break;

            case ConstantVariables.STORE_MENU_TITLE:
            case ConstantVariables.PRODUCT_MENU_TITLE:
                mWidgetProperty = "minPrice";
                break;

        }
    }

    /**
     * Method to checkVisibility of the other views which are need to shown on a particular item click.
     */
    private void checkVisibility() {
        // Checking visibility for the below 2 options only.
        if (joProperty.optString(FormActivity.SCHEMA_KEY_TYPE).equals(FormActivity.SCHEMA_KEY_RADIO)
                && (joProperty.optString("name").equals("end_date_enable")
                || (mCurrentSelectedOption.equals("payment_method")
                && joProperty.optString("name").equals("method")))) {
            String value = etFieldValue.getTag() != null ? etFieldValue.getTag().toString() : "";
            if (mRadioMultiOptionsClickListener != null) {
                mRadioMultiOptionsClickListener.onOptionSelected(value, joProperty.optString("name"));
            }
        }
        if (joProperty.optString(FormActivity.SCHEMA_KEY_TYPE).equals(FormActivity.SCHEMA_KEY_RADIO)
                && (joProperty.optString("name").equals("is_same_end_date")
                && (mCurrentSelectedOption.equals("show_available_tickets")))) {
            String value = etFieldValue.getTag() != null ? etFieldValue.getTag().toString() : "";

            if (mRadioMultiOptionsClickListener != null) {
                LogUtils.LOGD(FormMultiOptions.class.getSimpleName(), "Is mRadioMultiOptionsClickListener null: "+(mRadioMultiOptionsClickListener == null));
                mRadioMultiOptionsClickListener.onOptionSelected(value, joProperty.optString("name"));
            }
        }
    }

    /**
     * Method to get label associated with the key.
     *
     * @param key Key of the object.
     * @return Returns the label.
     */
    private String getLabelFromKey(String key) {
        String label;

        // Getting object associated with key.
        JSONObject subscriptionDetails = jsonObjectOptions.optJSONObject(key);

        // Checking for the subscription condition,
        // because in this condition multi options are different from others.
        if ((mCurrentSelectedOption.equals("subscription_account")
                || mCurrentSelectedOption.equals("settings_subscription"))
                && subscriptionDetails != null && subscriptionDetails.length() > 0) {

            label = subscriptionDetails.optString("label");
            double price = subscriptionDetails.optDouble("price", 0);

            //Showing price with label if it is not 0.
            if (price != 0) {
                String priceWithCurrency = GlobalFunctions.getFormattedCurrencyString(
                        subscriptionDetails.optString("currency"), price);
                label += "(" + priceWithCurrency + ")";
            }
        } else {
            label = jsonObjectOptions.optString(key);
        }
        return label;
    }

    /**
     * Method to check other field generation and visibility function on a item selection.
     *
     * @param key Key of the selected item.
     */
    private void checkForFieldOnOptionSelection(String key) {
        try {
            switch (mCurrentSelectedOption) {
                case ConstantVariables.VIDEO_MENU_TITLE:
                case ConstantVariables.HOME_MENU_TITLE:
                case ConstantVariables.MLT_VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_EVENT_ADD_VIDEO:
                case ConstantVariables.SITE_PAGE_ADD_VIDEO:
                case ConstantVariables.SITE_STORE_ADD_VIDEO:
                case ConstantVariables.ADV_GROUPS_ADD_VIDEO:
                    checkForVideoUploadingOptions(false);
                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    if (mFieldName.equals("category_id") || mFieldName.equals("subcategory_id")
                            || mFieldName.equals("subsubcategory_id")) {
                        checkForCategorySelectionOptions(key);
                    } else {
                        checkForAdvEventMultiOptions(key);
                        checkFormPublishedOption(false);
                    }
                    break;

                case ConstantVariables.USER_MENU_TITLE:
                    checkForAdvancedMemberMultiOptions(key);
                    break;

                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    checkForVideoUploadingOptions(false);
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                case ConstantVariables.STORE_MENU_TITLE:
                case ConstantVariables.PRODUCT_MENU_TITLE:
                case ConstantVariables.MLT_MENU_TITLE:
                case ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE:
                case ConstantVariables.ADD_PRODUCT:
                    if (mFieldName.equals("category_id") || mFieldName.equals("subcategory_id")
                            || mFieldName.equals("subsubcategory_id")) {
                        checkForCategorySelectionOptions(key);
                    }
                    checkFormPublishedOption(false);
                    break;

                case "compose_message":
                    checkForComposeMessageOptions();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show uploading option according to video source type.
     *
     * @param isFirstTimeLoaded True if the option is checked when the form is loaded first time.
     */
    protected void checkForVideoUploadingOptions(boolean isFirstTimeLoaded) {
        if ((mCurrentSelectedOption.equals(ConstantVariables.VIDEO_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.HOME_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.MLT_VIDEO_MENU_TITLE)
                || mCurrentSelectedOption.equals(ConstantVariables.ADV_EVENT_ADD_VIDEO)
                || mCurrentSelectedOption.equals(ConstantVariables.ADV_GROUPS_ADD_VIDEO)
                || mCurrentSelectedOption.equals(ConstantVariables.SITE_PAGE_ADD_VIDEO)
                || mCurrentSelectedOption.equals(ConstantVariables.SITE_STORE_ADD_VIDEO))
                && mFieldName.equals("type") && FormActivity._layout != null) {

            View urlView = FormActivity._layout.findViewWithTag("url");
            View uploadVideoView = FormActivity._layout.findViewWithTag("filedata");

            if (urlView != null && uploadVideoView != null) {
                switch (mFieldValue) {
                    case "0":
                    case "":
                        uploadVideoView.setVisibility(View.GONE);
                        urlView.setVisibility(View.GONE);
                        break;

                    case "3":
                        uploadVideoView.setVisibility(View.VISIBLE);
                        urlView.setVisibility(View.GONE);
                        break;

                    default:
                        uploadVideoView.setVisibility(View.GONE);
                        urlView.setVisibility(View.VISIBLE);
                        break;
                }
            }
            final CreateNewEntry createNewEntry = CreateNewEntry.getInstance();
            if (createNewEntry != null && createNewEntry._viewport != null
                    && !mFieldValue.equals("") && !mFieldValue.equals("0") && !isFirstTimeLoaded) {
                createNewEntry._container.post(new Runnable() {
                    @Override
                    public void run() {
                        //call smooth scroll
                        createNewEntry._viewport.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    /**
     * Method to show message sending option according to selected source type.
     */
    protected void checkForComposeMessageOptions() {
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            if (mFormWidgetList.get(i).getPropertyName().equals("toValues")) {
                mFormWidgetList.get(i).getView().setVisibility(mFieldValue.equals("1") ? View.VISIBLE : View.GONE);
            } else if (mFormWidgetList.get(i).getPropertyName().equals("searchGuests")) {
                mFormWidgetList.get(i).getView().setVisibility(mFieldValue.equals("4") ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * Method to check the published option. and show the other options accordingly.
     *
     * @param isFirstTimeLoaded True if the publish option is checked when the form is loaded first time.
     */
    protected void checkFormPublishedOption(boolean isFirstTimeLoaded) {
        if (mFieldName.equals("draft")) {
            String value;
            if (mCurrentSelectedOption.equals(ConstantVariables.MLT_MENU_TITLE)
                    || mCurrentSelectedOption.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
                value = "0";
            } else {
                value = "1";
            }
            for (int i = 0; i < mFormWidgetList.size(); i++) {
                if (mFormWidgetList.get(i).getPropertyName().equals("creation_date")
                        || mFormWidgetList.get(i).getPropertyName().equals("search")) {
                    mFormWidgetList.get(i).getView().setVisibility(mFieldValue.equals(value) ? View.VISIBLE : View.GONE);
                }
            }

            final CreateNewEntry createNewEntry = CreateNewEntry.getInstance();
            if (createNewEntry != null && createNewEntry._viewport != null && !isFirstTimeLoaded) {
                createNewEntry._container.post(new Runnable() {
                    @Override
                    public void run() {
                        //call smooth scroll
                        createNewEntry._viewport.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    private void checkForAdvEventMultiOptions(String key) {
        /* Create sub category fields in the form on the basis of Category selection from spinner
		     in advanced events module */
        if (mFieldName.equals("category_id")) {
            sIsEventRepeatTypeMonthly = false;
        }

        for (int i = 0; i < mFormWidgetList.size(); i++) {
            if (mFormWidgetList.get(i).getPropertyName().equals("eventrepeat_id")) {
                repeat_s_position = i;
            }
        }

        /* Monthly type repeat have event have two type, checked the monthly
         type and show respective field in the form */
        checkMonthlyTypeRepeatEvent();

        repeat_s_position++;

        repeat_e_position = repeat_s_position + size;

        /* Create repeat event fields in the form on the basis of
		   repeat event type selected from spinner */
        createProfileFieldsForRepeatEvent(key);


        if (mFieldName.equals("host_type_select")) {

            if (FormActivity.mAddPeopleAdapter != null)
                FormActivity.mAddPeopleAdapter.clear();

            if (key.equals("siteevent_organizer")) {
                tvAddNew.setVisibility(View.VISIBLE);
            } else {
                tvAddNew.setVisibility(View.GONE);
            }
            FormActivity.hostKey = key;
        }
    }

    private void checkMonthlyTypeRepeatEvent() {

        isMonthlyTypeCheck = FormActivity._layout.findViewById(R.id.monthly_type);
        if (isMonthlyTypeCheck != null && !isRemoveRepeatEvent && !sIsChangedMonthlyField) {
            sIsChangedMonthlyField = true;
            sIsEventRepeatTypeMonthly = true;
            sEditPageLoadingTime++;

            if (isMonthlyTypeCheck.getTag().equals("1")) {
                for (int i = 0; i < mFormWidgetList.size(); i++) {
                    if (mFormWidgetList.get(i).getPropertyName().equals("repeat_day")) {
                        mFormWidgetList.get(i).getView().setVisibility(View.VISIBLE);
                    } else if (mFormWidgetList.get(i).getPropertyName().equals("repeat_week") || mFormWidgetList.get(i).getPropertyName().equals("repeat_weekday")) {
                        mFormWidgetList.get(i).getView().setVisibility(View.GONE);
                    }
                }

            } else {
                for (int i = 0; i < mFormWidgetList.size(); i++) {
                    if (mFormWidgetList.get(i).getPropertyName().equals("repeat_week") || mFormWidgetList.get(i).getPropertyName().equals("repeat_weekday")) {
                        mFormWidgetList.get(i).getView().setVisibility(View.VISIBLE);
                    } else if (mFormWidgetList.get(i).getPropertyName().equals("repeat_day")) {
                        mFormWidgetList.get(i).getView().setVisibility(View.GONE);
                    }
                }
            }
            isMonthlyTypeCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkForCheckChange(isMonthlyTypeCheck);
                }
            });
        }
    }

    private void checkForCheckChange(AppCompatCheckedTextView isMonthlyTypeCheck) {
        isMonthlyTypeCheck.setChecked(!isMonthlyTypeCheck.isChecked());
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            if (mFormWidgetList.get(i).getPropertyName().equals("repeat_day")) {
                mFormWidgetList.get(i).getView().setVisibility(isMonthlyTypeCheck.isChecked() ? View.VISIBLE : View.GONE);

            } else if (mFormWidgetList.get(i).getPropertyName().equals("repeat_week")
                    || mFormWidgetList.get(i).getPropertyName().equals("repeat_weekday")) {
                mFormWidgetList.get(i).getView().setVisibility(View.GONE);
                mFormWidgetList.get(i).getView().setVisibility(isMonthlyTypeCheck.isChecked() ? View.GONE : View.VISIBLE);
            }
        }
    }

    private void createProfileFieldsForRepeatEvent(String key) {

        if (key != null) {

            if (joRepeatOccurrences != null && joRepeatOccurrences.has(key)) {

                sEditPageLoadingTimeRepeatEvent++;

                JSONArray fieldsJsonArray = joRepeatOccurrences.optJSONArray(key);
                if (fieldsJsonArray != null) {

                    if (sIsChangeOccurence) {
                        for (int k = repeat_s_position; k <= repeat_e_position; k++) {
                            mFormWidgetList.remove(repeat_s_position);
                        }
                    }

                    if (!isRemoveRepeatEvent && sEditPageLoadingTimeRepeatEvent >= 2) {
                        int startRemove = 0, endRemove = 0;
                        for (int i = 0; i < mFormWidgetList.size(); i++) {
                            if (mFormWidgetList.get(i).getPropertyName().equals("eventrepeat_id")) {
                                startRemove = i;
                            } else if (mFormWidgetList.get(i).getPropertyName().equals("body")) {
                                endRemove = i;
                            }
                        }

                        startRemove++;

                        for (int i = startRemove; i < endRemove; i++) {
                            mFormWidgetList.remove(startRemove);
                        }

                        isRemoveRepeatEvent = true;

                    }

                    if (isCreateForm || sEditPageLoadingTimeRepeatEvent >= 2) {

                        try {

                            for (int l = 0; l < fieldsJsonArray.length(); l++) {

                                JSONObject fieldObject = fieldsJsonArray.getJSONObject(l);
                                String fieldName = fieldObject.getString("name");
                                String fieldLabel = fieldObject.optString("label");
                                String fieldDescription = fieldObject.optString("description");
                                boolean hasValidator = fieldObject.optBoolean("hasValidator");

                                mFormWidget = mFormActivity.getWidget(mContext, fieldName, fieldObject,
                                        fieldLabel, hasValidator, isCreateForm, fieldDescription,
                                        mFormWidgetList, mFormWidgetMap, mCurrentSelectedOption, null,
                                        joCategoryFields, joRepeatOccurrences, null, null, null);

                                if (mFormWidget == null) continue;

                                if (fieldObject.has(FormActivity.SCHEMA_KEY_HINT))
                                    mFormWidget.setHint(fieldObject.getString(FormActivity.SCHEMA_KEY_HINT));

                                mFormWidgetList.add(repeat_s_position + l, mFormWidget);
                                mFormWidgetMap.put(fieldName, mFormWidget);

                                repeat_e_position = repeat_s_position + l;

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        size = repeat_e_position - repeat_s_position;

                        FormActivity._layout.removeAllViews();

                        for (int i = 0; i < mFormWidgetList.size(); i++) {
                            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
                        }

                    /*Change type and field of monthly type repeat event on change checkbox value */

                        if (key.equals("monthly") && !sIsChangedMonthlyField) {
                            final AppCompatCheckedTextView isMonthlyTypeCheck1 = FormActivity._layout.findViewById(R.id.monthly_type);
                            for (int i = 0; i < mFormWidgetList.size(); i++) {
                                if (mFormWidgetList.get(i).getPropertyName().equals("repeat_week") || mFormWidgetList.get(i).getPropertyName().equals("repeat_weekday")) {
                                    mFormWidgetList.get(i).getView().setVisibility(View.VISIBLE);
                                } else if (mFormWidgetList.get(i).getPropertyName().equals("repeat_day")) {
                                    mFormWidgetList.get(i).getView().setVisibility(View.GONE);
                                }
                            }

                            isMonthlyTypeCheck1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    checkForCheckChange(isMonthlyTypeCheck1);
                                }
                            });
                        }
                    }
                }

                sIsChangeOccurence = true;


            } else if (key.equals("never")) {
                sEditPageLoadingTimeRepeatEvent++;

                size = 0;
                FormActivity._layout.removeAllViews();
                if (sIsChangeOccurence) {
                    for (int k = repeat_s_position; k <= repeat_e_position; k++) {
                        mFormWidgetList.remove(repeat_s_position);

                    }
                }

                sIsChangeOccurence = false;

                if (!isRemoveRepeatEvent) {
                    int startRemove = 0, endRemove = 0;
                    for (int i = 0; i < mFormWidgetList.size(); i++) {
                        if (mFormWidgetList.get(i).getPropertyName().equals("eventrepeat_id")) {
                            startRemove = i;
                        } else if (mFormWidgetList.get(i).getPropertyName().equals("body")) {
                            endRemove = i;
                        }
                    }

                    startRemove++;

                    for (int i = startRemove; i < endRemove; i++) {
                        mFormWidgetList.remove(startRemove);
                    }

                    isRemoveRepeatEvent = true;

                }

                for (int i = 0; i < mFormWidgetList.size(); i++) {
                    FormActivity._layout.addView(mFormWidgetList.get(i).getView());
                }

            }
        }

    }

    /**
     * Method to check and generate the profile fields from the option selection.
     *
     * @param key Key of the selected item.
     */
    private void checkForAdvancedMemberMultiOptions(String key) {
        if (mAdvancedMemberWhatWhereWithinmile != null && !mAdvancedMemberWhatWhereWithinmile.isEmpty()) {
            hasSubCategory = hasSubSubCategory = false;

            // Checking the selected type.
            if (mFieldName.equals("profile_type")) {
                canChangeProfileFields = true;
                subSPosition = subSubSPosition = 0;
            }

            for (int i = 0; i < mFormWidgetList.size(); i++) {
                if (mFormWidgetList.get(i).getPropertyName().equals("profile_type")) {
                    sPosition = i;
                    break;
                }
            }

            // When the profile fields exists for profile_type selection.
            if (joCategoryFields != null && joCategoryFields.has(key)
                    && mFieldName.equals("profile_type")) {
                // Inflating view for profile fields.
                inflateProfileFieldView(key);
            }

            // When there are no profile fields for a profile_type then removing all profile fields
            if ((joCategoryFields != null && !joCategoryFields.has(key)
                    && mFieldName.equals("profile_type")) ||
                    (key != null && mFieldName.equals("profile_type") && key.equals("0"))) {
                removeViewFromCategorySelection(key);
            }
        }
    }

    /**
     * Method to check fields for the spinner item selection.
     *
     * @param key Key of the selected item.
     */
    private void checkForCategorySelectionOptions(String key) {

        if (!isCreateForm && isFirstTimeLoadingFromEdit && joFormValues != null) {
            mFieldCategoryLevel = joFormValues.optString("fieldCategoryLevel");

            for (int i = 0; i < mFormWidgetList.size(); i++) {
                if (mFormWidgetList.get(i).getPropertyName().equals("category_id")) {
                    sPosition = i;
                } else if (mCurrentSelectedOption.equals(ConstantVariables.MLT_MENU_TITLE)
                        && mFormWidgetList.get(i).getPropertyName().equals("auth_view")) {
                    ePosition = i - 1;
                } else if (mFormWidgetList.get(i).getPropertyName().equals(mWidgetProperty)) {
                    ePosition = i - 1;
                    mFieldCategoryLevel = "category_id";
                }
            }

            if (mFieldCategoryLevel != null && !mFieldCategoryLevel.isEmpty()) {
                switch (mFieldCategoryLevel) {
                    case "category_id":
                        isCategoryProfileFields = true;
                        isSubCategoryProfileFields = false;
                        isSubSubCategoryProfileFields = false;
                        break;
                    case "subcategory_id":
                        isCategoryProfileFields = false;
                        isSubCategoryProfileFields = true;
                        isSubSubCategoryProfileFields = false;
                        break;
                    case "subsubcategory_id":
                        isCategoryProfileFields = false;
                        isSubCategoryProfileFields = false;
                        isSubSubCategoryProfileFields = true;
                        break;
                }
            } else {
                ePosition = 0;
                isCategoryProfileFields = false;
                isSubCategoryProfileFields = false;
                isSubSubCategoryProfileFields = false;
                canChangeProfileFields = true;
            }
            isFirstTimeLoadingFromEdit = false;
        }

        // Checking the selected type.
        if (mFieldName.equals("category_id")) {
            isCategoryProfileFields = false;
            isSubCategoryProfileFields = false;
            isSubSubCategoryProfileFields = false;
            canChangeProfileFields = true;
            subSPosition = subSubSPosition = 0;
        }


        for (int i = 0; i < mFormWidgetList.size(); i++) {
            if (mFormWidgetList.get(i).getPropertyName().equals("category_id")) {
                sPosition = i;
            } else if (mFormWidgetList.get(i).getPropertyName().equals("subcategory_id")) {
                subSPosition = i;
            } else if (mFormWidgetList.get(i).getPropertyName().equals("subsubcategory_id")) {
                subSubSPosition = i;
            }
        }

        // When subcategory exists.
        if (mFieldName.equals("category_id") && key != null && joSubCategory != null
                && joSubCategory.has(key)) {
            inflateSubcategoryView(key);
        }

        // When SubSubCategory exists.
        if (mFieldName.equals("subcategory_id") && key != null && joSubSubCategory != null
                && joSubSubCategory.has(key)) {
            // Inflating view from SubSubCategory selection.
            inflateSubSubcategoryView(key);
        }

        // When the profile fields exists for category/SubCategory/SubSubCategory selection.
        if (joCategoryFields != null && joCategoryFields.has(key)
                && !isCategoryProfileFields && (mFieldName.equals("category_id")
                || mFieldName.equals("subcategory_id") || mFieldName.equals("subsubcategory_id"))) {
            // Inflating view for profile fields.
            inflateProfileFieldView(key);
        }

        /**
         * Remove field code <--start-->
         */

        // When there are no profile fields for a category then removing all view
        // (i.e. SubCategory, SubSubCategory. and profile fields.).
        if ((joCategoryFields != null && !joCategoryFields.has(key) && mFieldName.equals("category_id")) ||
                (key != null && mFieldName.equals("category_id") && key.equals("0"))) {
            removeViewFromCategorySelection(key);
        }
        // When the selected type is SubSubCategory and previous fields are generated by SubSubCategory but the
        //new SubSubCategory does not contain profile fields then removing all profile fields.
        else if (joCategoryFields != null && !joCategoryFields.has(key) && mFieldName.equals("subsubcategory_id") && isSubSubCategoryProfileFields) {
            removeViewFromSubSubCategorySelection();

        } else if ((key != null && mFieldName.equals("subcategory_id") &&
                (key.equals("0") || (joCategoryFields != null && !joCategoryFields.has(key) && isSubCategoryProfileFields)))) {
            removeViewFromSubCategorySelection();
        }
    }

    /**
     * Method to inflate the subcategory view from the category selection.
     *
     * @param key Key of the selected item.
     */
    private void inflateSubcategoryView(String key) {
        hasSubCategory = true;
        // When category is selected.
        if (mFieldName.equals("category_id") && ePosition != 0) {
            if (subSPosition != 0 && subSubSPosition == ePosition)
                ePosition = subSubSPosition;
            int remove = 0;
            if (subSPosition != 0)
                remove = subSPosition;
            else if (!mFormWidgetList.get(sPosition).getPropertyName().equals("category_id"))
                remove = sPosition;
            else
                remove = sPosition + 1;

            for (int k = ePosition; k >= remove; k--) {
                mFormWidgetList.remove(k);
            }
            ePosition = 0;
            subSubSPosition = 0;
            isCategoryProfileFields = isSubCategoryProfileFields = isSubSubCategoryProfileFields = false;
            canChangeProfileFields = true;
        } else if (canChangeProfileFields && ePosition == 0 && subSPosition != 0 &&
                !isCategoryProfileFields && !isSubCategoryProfileFields && !isSubSubCategoryProfileFields) {
            mFormWidgetList.remove(subSPosition);
        }

        JSONObject jsonObject = joSubCategory.optJSONObject(key);
        JSONObject formObject = jsonObject.optJSONObject("form");
        joSubSubCategory = jsonObject.optJSONObject("subsubcategories");

        if (formObject != null) {

            sPosition++;
            String name = formObject.optString("name");
            String label = formObject.optString("label");

            mFormWidget = mFormActivity.getWidget(mContext, name, formObject, label, false, true, null, mFormWidgetList,
                    mFormWidgetMap, mCurrentSelectedOption, null, joCategoryFields, null, joSubSubCategory, null, null);

            mFormWidgetList.add(sPosition, mFormWidget);
            mFormWidgetMap.put(name, mFormWidget);
        }

        FormActivity._layout.removeAllViews();
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
        }
    }

    /**
     * Method to inflate the SubSubcategory view from the subCategory selection.
     *
     * @param key Key of the selected item.
     */
    private void inflateSubSubcategoryView(String key) {
        hasSubSubCategory = true;

        // when the fields are generated by subcategory or SubSubCategory then removing profile fields.
        if (!canChangeProfileFields && (isSubCategoryProfileFields || isSubSubCategoryProfileFields) && ePosition != 0) {
            for (int k = ePosition; k >= (subSPosition + 1); k--) {
                mFormWidgetList.remove(k);
            }
            ePosition = subSubSPosition;
            isSubCategoryProfileFields = isSubSubCategoryProfileFields = false;
            canChangeProfileFields = true;
        } else if (canChangeProfileFields && !isCategoryProfileFields && ePosition != 0 && subSubSPosition != 0) {
            for (int k = ePosition; k >= subSubSPosition; k--) {
                mFormWidgetList.remove(k);
            }
            ePosition = 0;
            isSubCategoryProfileFields = isSubSubCategoryProfileFields = false;
        } else if (canChangeProfileFields && isCategoryProfileFields) {
            boolean isSubcateExists = true;
            for (int i = 0; i < mFormWidgetList.size(); i++) {
                if (!mFormWidgetList.get(i).getPropertyName().equals("subsubcategory_id")) {
                    isSubcateExists = false;
                }
            }
            if (!isSubcateExists) {
                if (subSubSPosition == 0) {
                    ePosition = ePosition + 1;
                } else if (subSubSPosition != 0 && ePosition != subSubSPosition) {
                    mFormWidgetList.remove(subSubSPosition);
                }
            } else {
                subSubSPosition = subSPosition;
            }
        } else if (canChangeProfileFields && subSubSPosition != 0) {
            mFormWidgetList.remove(subSubSPosition);
            ePosition = 0;
        }

        JSONObject jsonObject = joSubSubCategory.optJSONObject(key);

        subSPosition++;
        String name = jsonObject.optString("name");
        String label = jsonObject.optString("label");

        mFormWidget = mFormActivity.getWidget(mContext, name, jsonObject, label, false, true, null, mFormWidgetList,
                mFormWidgetMap, mCurrentSelectedOption, null, joCategoryFields, null, null, null, null);

        mFormWidgetList.add(subSPosition, mFormWidget);
        mFormWidgetMap.put(name, mFormWidget);

        FormActivity._layout.removeAllViews();
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
        }
        subSubSPosition = subSPosition;
    }

    /**
     * Method to inflate the profile fields for the cateogy/subCateogry/subSubCategory.
     *
     * @param key Key of the selected item.
     */
    private void inflateProfileFieldView(String key) {
        JSONArray fieldsJsonArray = joCategoryFields.optJSONArray(key);

        if (fieldsJsonArray != null) {
            if (canChangeProfileFields) {

                // When choosing subcategory and profile fields are generated from SubSubCategory.
                //then checking if new subcategory contains SubSubCategory or not
                if (mFieldName.equals("subcategory_id") && !hasSubSubCategory && isSubSubCategoryProfileFields && ePosition != 0 && subSubSPosition != 0) {
                    mFormWidgetList.remove(subSubSPosition);
                    ePosition = ePosition - 1;
                }
                if (mFieldName.equals("subcategory_id") && !hasSubSubCategory && !isSubSubCategoryProfileFields && subSubSPosition != 0) {
                    mFormWidgetList.remove(subSubSPosition);
                    if (ePosition != 0) {
                        ePosition = ePosition - 1;
                    } else {
                        ePosition = subSubSPosition - 1;
                    }
                }

                // Setting condition true for which profile fields are generated.
                switch (mFieldName) {
                    case "category_id":
                        isCategoryProfileFields = true;
                        isSubCategoryProfileFields = false;
                        isSubSubCategoryProfileFields = false;
                        canChangeProfileFields = false;
                        break;
                    case "subcategory_id":
                        isCategoryProfileFields = false;
                        isSubCategoryProfileFields = true;
                        isSubSubCategoryProfileFields = false;
                        canChangeProfileFields = false;
                        break;
                    case "subsubcategory_id":
                        isCategoryProfileFields = false;
                        isSubCategoryProfileFields = false;
                        isSubSubCategoryProfileFields = true;
                        canChangeProfileFields = false;
                        break;
                    default:
                        isCategoryProfileFields = false;
                        isSubCategoryProfileFields = false;
                        isSubSubCategoryProfileFields = false;
                        canChangeProfileFields = true;
                        break;
                }
                generateProfileFields(fieldsJsonArray);
            } else if ((mFieldName.equals("subcategory_id") && (isSubCategoryProfileFields || isSubSubCategoryProfileFields)) ||
                    (mFieldName.equals("subsubcategory_id") && isSubSubCategoryProfileFields)) {
                generateProfileFields(fieldsJsonArray);
            }
        }
    }

    /**
     * Method to remove the views from category selection.
     *
     * @param key Key of the selected item.
     */
    private void removeViewFromCategorySelection(String key) {

        FormActivity._layout.removeAllViews();
        //removing subcategory and subsubcategory when the no category is chosen.
        if (key != null && key.equals("0") && (ePosition != 0 && subSPosition != 0 && ePosition == subSPosition ||
                ePosition != 0 && subSubSPosition != 0 && ePosition == subSubSPosition)) {
            ePosition = ePosition + 1;
        } else if (key != null && key.equals("0") && ePosition == 0 && canChangeProfileFields) {
            if (subSubSPosition != 0) {
                mFormWidgetList.remove(subSubSPosition);
            }
            if (subSPosition != 0) {
                mFormWidgetList.remove(subSPosition);
            }
        }

        // If there are profile fields then remove it.

        if (ePosition != 0) {
            for (int k = ePosition; k > sPosition; k--) {
                mFormWidgetList.remove(k);
            }
        }

        for (int i = 0; i < mFormWidgetList.size(); i++) {
            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
        }
        sPosition = ePosition = subSPosition = subSubSPosition = 0;
        isCategoryProfileFields = isSubCategoryProfileFields = isSubSubCategoryProfileFields = false;
        canChangeProfileFields = true;
    }

    /**
     * Method to remove the views from SubSubCategory selection.
     */
    private void removeViewFromSubSubCategorySelection() {
        FormActivity._layout.removeAllViews();

        if (ePosition != 0) {
            for (int k = ePosition; k > subSubSPosition; k--) {
                mFormWidgetList.remove(k);
            }
            ePosition = 0;
            isSubSubCategoryProfileFields = false;
            canChangeProfileFields = true;
        }
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
        }
    }

    /**
     * Method to remove the views from SubCategory selection.
     */
    private void removeViewFromSubCategorySelection() {

        FormActivity._layout.removeAllViews();

        if (isSubCategoryProfileFields || isSubSubCategoryProfileFields) {

            if (ePosition != 0) {
                for (int k = ePosition; k > subSPosition; k--) {
                    mFormWidgetList.remove(k);
                }
            } else if (subSubSPosition != 0) {
                mFormWidgetList.remove(subSubSPosition);
            }
            ePosition = 0;
            subSubSPosition = 0;
            isSubCategoryProfileFields = false;
            isSubSubCategoryProfileFields = false;
            canChangeProfileFields = true;

        } else if (isCategoryProfileFields && ePosition != 0 && subSubSPosition != 0) {
            mFormWidgetList.remove(subSubSPosition);
            subSubSPosition = 0;
            ePosition = ePosition - 1;

        } else if (subSubSPosition != 0) {
            mFormWidgetList.remove(subSubSPosition);
            subSubSPosition = 0;
            ePosition = 0;
        }
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
        }
    }

    /**
     * Method to generate profile fields according to selected type and key.
     *
     * @param fieldsJsonArray array which contains the profile fields.
     */
    private void generateProfileFields(JSONArray fieldsJsonArray) {

        int startPosition = sPosition;

        if (subSPosition != 0 && !mFormWidgetList.get(subSPosition).getPropertyName().equals(mWidgetProperty))
            startPosition = subSPosition;
        if (mFieldName.equals("subcategory_id") && !hasSubSubCategory && subSubSPosition != 0) {
            startPosition = subSubSPosition - 1;
        } else if (subSubSPosition != 0 && !mFormWidgetList.get(subSubSPosition).getPropertyName().equals(mWidgetProperty)) {
            startPosition = subSubSPosition;
        }

        if (ePosition != 0) {
            for (int k = ePosition; k > startPosition; k--) {
                mFormWidgetList.remove(k);
            }
            ePosition = 0;
        }

        try {

            for (int j = 0; j < fieldsJsonArray.length(); j++) {

                JSONObject fieldObject = fieldsJsonArray.getJSONObject(j);
                String fieldName = fieldObject.getString("name");
                String fieldLabel = fieldObject.optString("label");
                String fieldDescription = fieldObject.optString("description");
                boolean hasValidator = fieldObject.optBoolean("hasValidator");

                mFormWidget = mFormActivity.getWidget(mContext, fieldName, fieldObject, fieldLabel, hasValidator, isCreateForm,
                        fieldDescription, mFormWidgetList, mFormWidgetMap, mCurrentSelectedOption, null,
                        joCategoryFields, null, null, null, null);

                if (mFormWidget == null) continue;

                if (fieldObject.has(FormActivity.SCHEMA_KEY_HINT))
                    mFormWidget.setHint(fieldObject.getString(FormActivity.SCHEMA_KEY_HINT));

                ePosition = startPosition + j + 1;
                mFormWidgetList.add(startPosition + j + 1, mFormWidget);
                mFormWidgetMap.put(fieldName, mFormWidget);


            }

            FormActivity._layout.removeAllViews();
            for (int i = 0; i < mFormWidgetList.size(); i++) {
                FormActivity._layout.addView(mFormWidgetList.get(i).getView());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to reset the form static variables when the form is destroyed.
     */
    static void resetFormVariables() {
        sPosition = ePosition = subSPosition = subSubSPosition = 0;
        hasSubCategory = hasSubSubCategory = isCategoryProfileFields = isSubCategoryProfileFields = isSubSubCategoryProfileFields = false;
    }

    @Override
    public String getValue() {

        // Returning field value.
        return (etFieldValue != null && etFieldValue.getTag() != null ? etFieldValue.getTag().toString() : "");
    }

    @Override
    public void setValue(String value) {

        // Showing the field value when it is coming in response.
        if (value != null && etFieldValue != null && jsonObjectOptions != null
                && jsonObjectOptions.length() > 0) {
            etFieldValue.setText(getLabelFromKey(value));
            etFieldValue.setTag(value);
            mFieldValue = value;
            checkVisibility();
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
    public void setToggleHandler(FormActivity.FormWidgetToggleHandler handler) {
        super.setToggleHandler(handler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.field_value:
            case R.id.form_main_view:
                View inflateView = mLayoutInflater.inflate(R.layout.fragmen_cart, null);
                RecyclerView recyclerView = inflateView.findViewById(R.id.recycler_view);
                inflateView.findViewById(R.id.cart_bottom).setVisibility(View.GONE);
                recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(mSheetAdapter);
                mSheetAdapter.setDefaultKey(etFieldValue.getTag() != null
                        ? etFieldValue.getTag().toString() : null);
                mBottomSheetDialog = new BottomSheetDialog(mContext);
                mBottomSheetDialog.setContentView(inflateView);
                mBottomSheetDialog.show();
                break;

            case R.id.change_host:
                FormHostChange.sIsAddNewHost = false;
                for (int k = 0; k < mFormWidgetList.size(); k++) {
                    if (mFormWidgetList.get(k).getPropertyName().equals(FormHostChange.sEventHost)) {
                        mFormWidgetList.get(k).getView().setVisibility(View.VISIBLE);
                    } else if (mFormWidgetList.get(k).getPropertyName().equals("host_type_select") ||
                            mFormWidgetList.get(k).getPropertyName().equals("host_auto")) {
                        mFormWidgetList.get(k).getView().setVisibility(View.GONE);
                    }
                }
                break;

            case R.id.add_new_host:
                FormHostChange.sIsAddNewHost = true;
                FormHostChange.sIsCreateNewHost = true;
                FormActivity.loadEditHostForm++;
                int host_position = 0;

                for (int j = 0; j < mFormWidgetList.size(); j++) {

                    if (mFormWidgetList.get(j).getPropertyName().equals("host_type_select")) {
                        host_position = j;
                    } else if (!isCreateForm && (mFormWidgetList.get(j).getPropertyName().equals("host_facebook") ||
                            mFormWidgetList.get(j).getPropertyName().equals("host_twitter") ||
                            mFormWidgetList.get(j).getPropertyName().equals("host_website"))) {
                        mFormWidgetList.remove(j);
                        j--;
                    }
                }

                for (int i = 0; i < FormActivity.mHostCreateForm.length(); i++) {

                    JSONObject jsonObject = FormActivity.mHostCreateForm.optJSONObject(i);
                    String fieldName = jsonObject.optString("name");
                    String fieldLabel = jsonObject.optString("label");

                    mFormWidget = mFormActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, isCreateForm,
                            null, mFormWidgetList, mFormWidgetMap, mCurrentSelectedOption, null,
                            null, null, null, null, null);

                    if (mFormWidget == null) continue;

                    if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

                    mFormWidgetList.add(host_position + i, mFormWidget);
                    mFormWidgetMap.put(fieldName, mFormWidget);

                }

                int socialLinkPosition = host_position + FormActivity.mHostCreateForm.length();
                socialLinkPosition++;

                for (int i = 0; i < FormActivity.mHostCreateFormSocial.length(); i++) {

                    JSONObject jsonObject = FormActivity.mHostCreateFormSocial.optJSONObject(i);
                    String fieldName = jsonObject.optString("name");
                    String fieldLabel = jsonObject.optString("label");

                    mFormWidget = mFormActivity.getWidget(mContext, fieldName, jsonObject, fieldLabel, false, isCreateForm,
                            null, mFormWidgetList, mFormWidgetMap, mCurrentSelectedOption, null,
                            null, null, null, null, null);

                    if (mFormWidget == null) continue;

                    if (jsonObject.has(FormActivity.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(jsonObject.optString(FormActivity.SCHEMA_KEY_HINT));

                    mFormWidgetList.add(socialLinkPosition + i, mFormWidget);
                    mFormWidgetMap.put(fieldName, mFormWidget);

                }

                FormActivity._layout.removeAllViews();
                for (int k = 0; k < mFormWidgetList.size(); k++) {

                    if (mFormWidgetList.get(k).getPropertyName().equals(FormHostChange.sEventHost) ||
                            mFormWidgetList.get(k).getPropertyName().equals("host_type_select") ||
                            mFormWidgetList.get(k).getPropertyName().equals("host_auto") ||
                            mFormWidgetList.get(k).getPropertyName().equals("host_facebook") ||
                            mFormWidgetList.get(k).getPropertyName().equals("host_twitter") ||
                            mFormWidgetList.get(k).getPropertyName().equals("host_website")) {
                        mFormWidgetList.get(k).getView().setVisibility(View.GONE);
                    }
                    FormActivity._layout.addView(mFormWidgetList.get(k).getView());
                }
                break;
        }
    }

    public interface RadioMultiOptionsClickListener {
        void onOptionSelected(String value, String name);
    }
    /**
     * Method to set the order of the selected parent element.
     */
    public void setElementOrder(){
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            if (mFormWidgetList.get(i).getPropertyName().equals(mFieldName)) {
                elementOrder = i;
                break;
            }
        }
    }
    /**
     * Method to remove the child element of the selected parent element.
     *
     * @param parentName, name of the selected element.
     */
    private void removeChild(String parentName){
        String append = FormActivity.getAttribByProperty(parentName,"append",null);
        int appendValue = (append != null && !append.isEmpty()) ? Integer.parseInt(append) : 1;
        JSONObject formObject = FormActivity.getFormObject().optJSONObject("fields");
        String child = FormActivity.getRegistryByProperty(parentName,"child");
        if(child != null && !child.trim().equals("") && formObject.optJSONArray(child) != null){
            JSONArray subFormArray = formObject.optJSONArray(child);
            for (int i = subFormArray.length()-1; i >= 0 ; --i) {
                if(subFormArray.optJSONObject(i) != null && subFormArray.optJSONObject(i).optBoolean("hasSubForm",false)){
                    removeChild(subFormArray.optJSONObject(i).optString("name"));
                }
                try{
                    appendValue = (appendValue == 0 ) ? -1  : appendValue;
                    mFormWidgetList.remove(elementOrder + i + appendValue);
                }catch (IndexOutOfBoundsException e){
                    LogUtils.LOGD("Exception Removing ",e.getMessage());
                }
            }
        }
    }

    /**
     * Method to inflate the subForm view from the option selection.
     *
     * @param key Key of the selected item.
     */
    private void inflateSubFormView(String key) {
        JSONObject formObject = FormActivity.getFormObject().optJSONObject("fields");
        setElementOrder();
        removeChild(mFieldName);
        setElementOrder();
        JSONArray subFormArray = formObject.optJSONArray(mFieldName+"_"+key);
        if(subFormArray != null) {
            String append = FormActivity.getAttribByProperty(mFieldName,"uppend",null);
            int appendValue = (append != null && !append.isEmpty()) ? Integer.parseInt(append) : 1;
            for (int i = 0; i < subFormArray.length(); ++i) {
                JSONObject fieldsObject = subFormArray.optJSONObject(i);
                if (fieldsObject != null) {
                    String name = fieldsObject.optString("name");
                    String label = fieldsObject.optString("label");
                    mFormWidget = mFormActivity.getWidget(mContext, name, fieldsObject, label, false, true, null, mFormWidgetList,
                            mFormWidgetMap, mCurrentSelectedOption, null, null, null, null, null, null);
                    if (fieldsObject.has(FormActivity.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(fieldsObject.optString(FormActivity.SCHEMA_KEY_HINT));
                    try{
                        mFormWidgetList.add(elementOrder+i+appendValue, mFormWidget);

                    }catch (IndexOutOfBoundsException e){
                        LogUtils.LOGD("Exception  Adding",e.getMessage());
                    }
                    mFormWidgetMap.put(name, mFormWidget);
                }
            }
        }
        FormActivity._layout.removeAllViews();
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            FormActivity._layout.addView(mFormWidgetList.get(i).getView());
        }
        FormActivity.setRegistryByProperty(mFieldName,FormActivity.getFormObject().optJSONObject(mFieldName),(!key.equals("")) ? mFieldName+"_"+key : null);
    }

}
