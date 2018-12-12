/*
* Copyright (c) 2016 BigStep Technologies Private Limited.
*
* You may not use this file except in compliance with the
* SocialEngineAddOns License Agreement.
* You may obtain a copy of the License at:
* https://www.socialengineaddons.com/android-app-license
* The full copyright and license information is also mentioned
* in the LICENSE file that was distributed with this
* source code.
*/

package com.fellopages.mobileapp.classes.modules.user.staticpages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * A simple {@link Fragment} subclass.
 */
public class FooterMenusFragment extends Fragment {

    private String mFooterMenuUrl,mCurrentSelectedOption;
    private AppConstant mAppConst;
    private Context mContext;
    private View mRootView;
    private LinearLayout mContactUsBlock;
    private TextView mMessageIcon;
    private EditText nameField, emailField, bodyField;
    private WebView mWebView;
    private Map<String, String> postParams;
    private boolean isError = false;
    private String nameFieldValue, emailFieldValue, bodyFieldValue;

    public FooterMenusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_footer_menus, container, false);
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        postParams = new HashMap<>();
        mFooterMenuUrl = AppConstant.DEFAULT_URL;

        mContactUsBlock = (LinearLayout) mRootView.findViewById(R.id.contact_us_block);
        mWebView = (WebView) mRootView.findViewById(R.id.view_description);
        mMessageIcon = (TextView) mRootView.findViewById(R.id.messageIcon);
        mMessageIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mMessageIcon.setText("\uF0E0");

        nameField = (EditText) mRootView.findViewById(R.id.name);
        emailField = (EditText) mRootView.findViewById(R.id.email);
        bodyField = (EditText) mRootView.findViewById(R.id.body);

        if(getArguments() != null){
            mCurrentSelectedOption = getArguments().getString("currentOption");
        }

        if (mCurrentSelectedOption == null || mCurrentSelectedOption.isEmpty()) {
            mCurrentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        switch (mCurrentSelectedOption){
            case "contact_us":
                mFooterMenuUrl += "help/contact";
                mWebView.setVisibility(View.GONE);
                mContactUsBlock.setVisibility(View.VISIBLE);
                break;

            case "privacy_policy":
                mFooterMenuUrl += "help/privacy";
                makeRequest();
                break;

            case "terms_of_service":
                mFooterMenuUrl += "help/terms";
                makeRequest();
                break;
        }
        return mRootView;
    }

    public void makeRequest(){

        mContactUsBlock.setVisibility(View.GONE);
        mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(mFooterMenuUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                String body = jsonObject.optString("body");
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadData(body, "text/html; charset=utf-8", "UTF-8");
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentSelectedOption.equals("contact_us")) {
            MenuItem postContact = menu.findItem(R.id.post_contact);
            postContact.setVisible(true);
        }
        MenuItem toggle = menu.findItem(R.id.viewToggle);
        if (toggle != null) {
            toggle.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                return false;
            case R.id.post_contact:
                postContactForm();
                break;
            default:
                break;
        }
        return false;
    }

    public void postContactForm() {

        isError = false;

        mAppConst.hideKeyboard();
        nameFieldValue = nameField.getText().toString();
        emailFieldValue = emailField.getText().toString();
        bodyFieldValue = bodyField.getText().toString();

        isError = checkValidation();

        if (!isError) {
            postParams.put("name", nameFieldValue);
            postParams.put("email", emailFieldValue);
            postParams.put("body", bodyFieldValue);
            mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            mAppConst.postJsonResponseForUrl(mFooterMenuUrl, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (!isError) {
                        SnackbarUtils.displaySnackbarLongWithListener(mRootView,
                                mContext.getResources().getString(R.string.contact_us_success_message),
                                new SnackbarUtils.OnSnackbarDismissListener() {
                                    @Override
                                    public void onSnackbarDismissed() {
                                        Intent intent = new Intent(mContext, MainActivity.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    }
                                });
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    try {
                        JSONObject errorMessagesObject = new JSONObject(message);
                        Iterator<String> keys = errorMessagesObject.keys();
                        if (keys != null) {
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = errorMessagesObject.optString(key);
                                isError = showErrorMessage(key, value);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Method to show error message.
     * @param key name of the field.
     * @param value error message
     * @return return true if any error.
     */
    private boolean showErrorMessage(String key, String value) {
        switch (key) {
            case "name":
                nameField.setError(value);
                isError = true;
                break;
            case "email":
                emailField.setError(value);
                isError = true;
                break;
            case "body":
                bodyField.setError(value);
                isError = true;
                break;
        }
        return isError;
    }

    private boolean checkValidation() {

        if (TextUtils.isEmpty(nameFieldValue)) {
            nameField.setError(mContext.getResources().getString(R.string.widget_error_msg));
            isError = true;
        }
        if(TextUtils.isEmpty(emailFieldValue)){
            emailField.setError(mContext.getResources().getString(R.string.widget_error_msg));
            isError = true;
        }
        if(TextUtils.isEmpty(bodyFieldValue)){
            bodyField.setError(mContext.getResources().getString(R.string.widget_error_msg));
            isError = true;
        }

        return isError;
    }

}
