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
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Map;

/**
 * FormMultiCheckBox is used to inflate the fields for the Multi-CheckBox with the label.
 */

public class FormMultiCheckBox extends FormWidget {

    // Member variables.
    private Context mContext;
    private SelectableTextView tvLabel;
    private ArrayAdapter<String> checkBoxAdapter;
    protected JSONObject jsonObjectOptions;
    protected ArrayList<AppCompatCheckedTextView> mCheckedTextViewList = new ArrayList<>();
    private ArrayList<FormWidget> mFormWidgetList;
    private Map<String, FormWidget> mFormWidgetMap;
    private int elementOrder = 0;
    private String mFieldName;
    private FormActivity mFormActivity;
    private FormWidget mFormWidget;

    /**
     * Public constructor to inflate form field For the multi checkbox items.
     *
     * @param context      Context of calling class.
     * @param property     Property of the field.
     * @param options      Object with multi options.
     * @param label        Label of the field.
     * @param hasValidator True if the field has validation (Compulsory field).
     * @param description  Description of the field.
     */
    public FormMultiCheckBox(Context context, String property, JSONObject options, String label,
                             boolean hasValidator, String description,ArrayList<FormWidget> widgets, Map<String, FormWidget> map, JSONObject joProperty) {
        super(context, property, hasValidator);

        // Initializing member variables.
        mContext = context;
        jsonObjectOptions = options;
        tvLabel = new SelectableTextView(mContext);

        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        tvLabel.setText(label);
        tvLabel.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
        if (joProperty.optBoolean("isDisable", false)) {
            tvLabel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_light));
        }
        _layout.addView(tvLabel);
        this.mFormWidgetList = widgets;
        this.mFormWidgetMap = map;
        this.mFieldName = property;
        mFormActivity = new FormActivity();

        //TODO, Uncomment this when ever the description is needed.
        // Showing description text view if description is not empty.
//        if (description != null && !description.isEmpty()) {
//            SelectableTextView tvDescription = new SelectableTextView(mContext);
//            tvDescription.setText(description);
//            tvDescription.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
//            _layout.addView(tvDescription);
//        }

        JSONArray propertyNames = options.names();
        String name, p;
        checkBoxAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

        // Adding check boxes.
        try {
            for (int i = 0; i < options.length(); i++) {
                name = propertyNames.getString(i);
                p = options.getString(name);

                // Adding checked text view.
                if (!p.isEmpty()) {
                    AppCompatCheckedTextView checkedTextView = new AppCompatCheckedTextView(mContext);
                    checkedTextView.setText(p);
                    checkedTextView.setId(i);
                    checkedTextView.setGravity(Gravity.CENTER);
                    checkedTextView.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                            0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_11dp));
                    checkedTextView.setCheckMarkDrawable(GlobalFunctions.getCheckMarkDrawable(mContext));
                    mCheckedTextViewList.add(checkedTextView);
                    checkBoxAdapter.add(name);
                    checkedTextView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp),
                            mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                            mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                            mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));
                    _layout.addView(checkedTextView);

                    // Adding bottom line divider.
                    View view = new View(mContext);
                    view.setBackgroundResource(R.color.colordevider);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.divider_line_view_height));
                    view.setLayoutParams(layoutParams);
                    _layout.addView(view);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Listener to mark check box as checked/unchecked.
        if (mCheckedTextViewList != null && mCheckedTextViewList.size() > 0) {
            for (int i = 0; i < mCheckedTextViewList.size(); i++) {
                final int finalI = i;
                mCheckedTextViewList.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvLabel.setError(null);
                        try{
                            inflateSubFormView(String.valueOf(finalI));
                            mCheckedTextViewList.get(finalI).setChecked(!mCheckedTextViewList
                                    .get(finalI).isChecked());
                        } catch (Exception e) {
                        mCheckedTextViewList.get(finalI).setChecked(!mCheckedTextViewList
                                .get(finalI).isChecked());
                        }
                    }
                });
            }
        }
    }

    @Override
    public String getValue() {

        String returnValues = "";

        for (int i = 0; i < mCheckedTextViewList.size(); i++) {

            int arrayLength = (mCheckedTextViewList.size()) - 1;

            if (mCheckedTextViewList.get(i).isChecked()) {
                AppCompatCheckedTextView checkBox = mCheckedTextViewList.get(i);
                String value = checkBoxAdapter.getItem(checkBox.getId());
                if (i < arrayLength) {
                    returnValues += value + ",";
                } else {
                    returnValues += value;
                }
            }
        }

        return returnValues;
    }

    @Override
    public void setValue(String value) {
        try {
            if (value != null && !value.isEmpty()) {

                Object json = new JSONTokener(value).nextValue();
                /* If Values are coming in form of JsonObject */
                if (json instanceof JSONObject) {
                    JSONObject valuesObject = (JSONObject) json;
                    if (valuesObject != null && valuesObject.length() != 0) {
                        JSONArray valueIds = valuesObject.names();

                        for (int i = 0; i < valueIds.length(); i++) {

                            String checkBoxId = valueIds.optString(i);
                            String checkBoxName = valuesObject.optString(checkBoxId);
                            String item = jsonObjectOptions.optString(checkBoxName);

                            if (item != null && !item.isEmpty()) {
                                AppCompatCheckedTextView checkBox = mCheckedTextViewList.get(checkBoxAdapter.getPosition(checkBoxName));
                                checkBox.setChecked(true);
                            }
                        }
                    }
                }
                /* If Values are coming in form of JsonArray */
                else if (json instanceof JSONArray) {
                    JSONArray valuesArray = (JSONArray) json;
                    for (int i = 0; i < valuesArray.length(); i++) {

                        String checkBoxName = valuesArray.optString(i);
                        String item = jsonObjectOptions.optString(checkBoxName);

                        if (item != null && !item.isEmpty()) {
                            AppCompatCheckedTextView checkBox = mCheckedTextViewList.get(checkBoxAdapter.getPosition(checkBoxName));
                            checkBox.setChecked(true);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        tvLabel.setError(errorMessage);
        tvLabel.setFocusable(true);
        tvLabel.requestFocus();
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
        JSONObject formObject = FormActivity.getFormObject().optJSONObject("fields");
        String child = FormActivity.getRegistryByProperty(parentName,"child");
        if(child != null && !child.trim().equals("") && formObject.optJSONArray(child) != null){
            JSONArray subFormArray = formObject.optJSONArray(child);
            for (int i = subFormArray.length()-1; i >= 0 ; --i) {
                if(subFormArray.optJSONObject(i) != null && subFormArray.optJSONObject(i).optBoolean("hasSubForm",false)){
                    removeChild(subFormArray.optJSONObject(i).optString("name"));
                }
                try{
                    mFormWidgetList.remove(elementOrder + i + 1);
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
        JSONArray subFormArray = formObject.optJSONArray(mFieldName+"_"+key);
        if(subFormArray != null) {
            for (int i = 0; i < subFormArray.length(); ++i) {
                JSONObject fieldsObject = subFormArray.optJSONObject(i);
                if (fieldsObject != null) {
                    String name = fieldsObject.optString("name");
                    String label = fieldsObject.optString("label");
                    mFormWidget = mFormActivity.getWidget(mContext, name, fieldsObject, label, false, true, null, mFormWidgetList,
                            mFormWidgetMap, null, null, null, null, null, null, null);
                    if (fieldsObject.has(FormActivity.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(fieldsObject.optString(FormActivity.SCHEMA_KEY_HINT));
                    try{
                        mFormWidgetList.add(elementOrder+i+1, mFormWidget);
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
