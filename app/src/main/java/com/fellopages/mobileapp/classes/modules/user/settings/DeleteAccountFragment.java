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

package com.fellopages.mobileapp.classes.modules.user.settings;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONObject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class DeleteAccountFragment extends Fragment {


    String mDeleteUrl;
    View mRootView;
    AppConstant mAppConst;
    Context mContext;
    Button mDeleteButton, mCancelButton;
    AlertDialogWithAction mAlertDialogWithAction;

    public DeleteAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =  inflater.inflate(R.layout.fragment_delete_account, container, false);

        mDeleteButton = (Button) mRootView.findViewById(R.id.deleteAccountButton);
        mCancelButton = (Button) mRootView.findViewById(R.id.cancelButton);

        mDeleteUrl = getArguments().getString("url");

        mAppConst = new AppConstant(getActivity());
        mContext = getContext();
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.delete_account_heading_text),
                        mContext.getResources().getString(R.string.delete_account_confirmation_message),
                        mContext.getResources().getString(R.string.delete_account_button_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mDeleteUrl != null) {

                                    mAppConst.deleteResponseForUrl(mDeleteUrl, null, new OnResponseListener() {
                                        @Override
                                        public void onTaskCompleted(JSONObject jsonObject) {
                                            mAppConst.eraseUserDatabase();
                                        }

                                        @Override
                                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                                        }
                                    });
                                }
                            }
                        });

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)mContext).finish();
            }
        });


        return mRootView;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
