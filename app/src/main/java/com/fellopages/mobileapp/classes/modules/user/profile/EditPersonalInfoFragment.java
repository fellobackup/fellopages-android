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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPersonalInfoFragment extends Fragment {

    private View mRootView;
    private AppConstant mAppConst;
    private Context mContext;
    private String mEditProfileUrl;
    private RelativeLayout mFormContainer;
    private Map<String, String> postParams;
    private ProgressBar mProgressBar;

    public EditPersonalInfoFragment() {
        // Required empty public constructor
    }

    public static EditPersonalInfoFragment newInstance(Bundle bundle) {
        EditPersonalInfoFragment fragment = new EditPersonalInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        mRootView = inflater.inflate(R.layout.fragment_edit_personal_info, container, false);
        mProgressBar = mRootView.findViewById(R.id.progressBar);
        mContext = getContext();
        mAppConst = new AppConstant(mContext);

        mFormContainer = mRootView.findViewById(R.id.create_form);

        if (getArguments() != null) {
            mEditProfileUrl = getArguments().getString("url");
        }

        // calling for edit form load.
        makeRequest();


        return mRootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.submit:
                EditProfile();
                return true;
            case R.id.delete:
                return false;
            default:
                break;
        }
        return false;
    }


    /**
     * Method to send request to server to get edit personal info page data.
     */
    public void makeRequest() {
        if (mEditProfileUrl != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAppConst.getJsonResponseFromUrl(mEditProfileUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);
                    mFormContainer.addView(((FormActivity) mContext).populate(jsonObject, "edit_member_profile"));

                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }
            });
        }
    }

    public void EditProfile() {


        postParams = new HashMap<>();

        postParams = ((FormActivity) mContext).save();

        if (postParams != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAppConst.hideKeyboard();
            for (Map.Entry<String, String> entry : postParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                mEditProfileUrl = Uri.parse(mEditProfileUrl)
                        .buildUpon()
                        .appendQueryParameter(key, value)
                        .build().toString();
            }

            mAppConst.putResponseForUrl(mEditProfileUrl, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {

                    mProgressBar.setVisibility(View.GONE);
                    mAppConst.refreshUserData();
                    EditProfileActivity.isProfileUpdated = true;
                    SnackbarUtils.displaySnackbarLongWithListener(mRootView,
                            mContext.getResources().getString(R.string.changes_saved_success_message),
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    // Loading the form again on the successful form submission.
                                    // Removing the all old views.
                                    mFormContainer.removeAllViews();
                                    mFormContainer.addView(mProgressBar);
                                    makeRequest();
                                }
                            });
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);

                    // Showing error on respective widget.
                    try {
                        JSONObject errorMessagesObject = new JSONObject(message);
                        ((FormActivity) mContext).showValidations(errorMessagesObject);
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
}
