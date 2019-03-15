/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.common.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.advancedGroups.AdvGroupsProfile;
import com.fellopages.mobileapp.classes.modules.directoryPages.SitePageProfilePage;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvModulesInfoTabFragment extends Fragment implements View.OnClickListener {

    private View mRootView;
    private Context mContext;
    private AppConstant mAppConst;
    private Typeface fontIcon;
    private GridLayout profileFieldLayout;
    private ImageView mOwnerImage;
    private WebView mViewDescription;
    private TextView mOwnerName, mWriteReviewIcon, mWriteReviewView,mFollowIcon, mFollowText;
    private LinearLayout mOwnerDetailBlock, mReviewBlock;
    private int mUserId, reviewCreateUpdate = 0, mContentId, mIsOwner, mPageId;
    private String updateReviewUrl, mContentIdString, mIntentAction, mUrlText, mPluralUrlText;
    private boolean isDataSet = true;
    private JSONObject mResponseJsonObject;
    private Map<String, String> mProfileFieldMap;
    private String actionUrl, dialogueMessage, dialogueTitle, dialogueButton, successMessage;
    private Map<String, String> postParams;
    private Boolean isShowFollowButton = false;
    LinearLayout.LayoutParams layoutParams;
    private String mCurrentSelectedModule;
    private ImageLoader mImageLoader;
    private TextView mContactIcon, mContactText;
    private JSONObject mContactInfoObject;


    private void initializeView() {

        postParams = new HashMap<>();

        layoutParams = CustomViews.getFullWidthLayoutParams();

        profileFieldLayout = mRootView.findViewById(R.id.profileFieldLayout);
        mOwnerDetailBlock = mRootView.findViewById(R.id.owner_detail_layout);
        mOwnerImage = mRootView.findViewById(R.id.owner_image);
        mOwnerName = mRootView.findViewById(R.id.owner_title);
        mOwnerName.setOnClickListener(this);
        mOwnerImage.setOnClickListener(this);

        mContactIcon = mRootView.findViewById(R.id.contact_info_icon);
        mContactText = mRootView.findViewById(R.id.contact_info_text);

        mFollowIcon = mRootView.findViewById(R.id.follow_icon);
        mFollowText = mRootView.findViewById(R.id.follow_text);

        mReviewBlock = mRootView.findViewById(R.id.review_block);
        mWriteReviewIcon = mRootView.findViewById(R.id.review_icon);
        mWriteReviewView = mRootView.findViewById(R.id.review_text);

        mRootView.findViewById(R.id.contact_info_block).setOnClickListener(this);
        mRootView.findViewById(R.id.review_block).setOnClickListener(this);
        mRootView.findViewById(R.id.follow_block).setOnClickListener(this);

        mContactIcon.setTypeface(fontIcon);
        mFollowIcon.setTypeface(fontIcon);
        mWriteReviewIcon.setTypeface(fontIcon);

        mViewDescription = mRootView.findViewById(R.id.view_description);

        //Set data in views
        setDataInViews(mResponseJsonObject);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentSelectedModule = getArguments().getString(ConstantVariables.EXTRA_MODULE_TYPE);
        if(mCurrentSelectedModule != null){
            switch (mCurrentSelectedModule){
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                    mContentIdString = "page_id";
                    mUrlText = mPluralUrlText = "sitepage";
                    break;
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    mContentIdString = "group_id";
                    mUrlText = "advancedgroup";
                    mPluralUrlText = "advancedgroups";
                    break;
            }
        }
    }

    public AdvModulesInfoTabFragment() {
        // Required empty public constructor
    }

    public static AdvModulesInfoTabFragment newInstance(Bundle bundle){
        AdvModulesInfoTabFragment fragment = new AdvModulesInfoTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_info_tab, container, false);

        Bundle bundle = getArguments();
        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mImageLoader = new ImageLoader(mContext);
        mProfileFieldMap = new LinkedHashMap<>();
        updateData(bundle, false);

        // Initializing views
        initializeView();

        return mRootView;
    }

    public void updateData(Bundle bundle, boolean isUpdateRequest) {
        try {
            String mDataResponse = bundle.getString(ConstantVariables.RESPONSE_OBJECT);
            mResponseJsonObject = new JSONObject(mDataResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isUpdateRequest && isAdded()) {
            setDataInViews(mResponseJsonObject);
        }
    }

    /**
     * Method for setting the data in respective views according to view types.
     * @param mResponseJsonObject response object.
     */
    public void setDataInViews(JSONObject mResponseJsonObject) {

        try {
            mProfileFieldMap.clear();
            JSONArray mGutterMenus = mResponseJsonObject.optJSONArray("gutterMenu");
            JSONObject responseObject;
            if(mCurrentSelectedModule.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)){
                responseObject = mResponseJsonObject.optJSONObject("response");
            }else{
                responseObject = mResponseJsonObject;
            }
            mContentId = responseObject.optInt(mContentIdString);
            mUserId = responseObject.optInt("owner_id");
            String body = responseObject.optString("body");
            String ownerTitle = responseObject.optString("owner_title");
            String ownerImage = responseObject.optString("owner_image");

            if (mGutterMenus != null) {
                for (int i = 0; i < mGutterMenus.length(); i++) {
                    JSONObject menuJsonObject = mGutterMenus.optJSONObject(i);
                    if (menuJsonObject.optString("name").equals("update_review")) {
                        reviewCreateUpdate = 1;
                        updateReviewUrl = menuJsonObject.optString("url");
                    } else if (menuJsonObject.optString("name").equals("create_review")) {
                        reviewCreateUpdate = 2;
                    }

                    if (menuJsonObject.optString("name").equals("follow") ||
                            menuJsonObject.optString("name").equals("unfollow")) {
                        isShowFollowButton = true;
                    }

                }
            }

            /* Showing owner details image and name */

            mOwnerDetailBlock.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            mOwnerDetailBlock.setVisibility(View.VISIBLE);
            mImageLoader.setImageForUserProfile(ownerImage, mOwnerImage);

            mOwnerName.setText(ownerTitle);
            mRootView.findViewById(R.id.owner_view_divider).setVisibility(View.VISIBLE);

            JSONObject mProfileFieldsObject = responseObject.optJSONObject("profile_fields");
            mIsOwner = responseObject.optInt("isOwner");
            mContactInfoObject = responseObject.optJSONObject("contactInfo");
            mPageId = responseObject.optInt("page_id");

            if (!responseObject.optString("price").equals("0") && !responseObject.optString("price").isEmpty()) {
                if(mProfileFieldsObject == null){
                    mProfileFieldsObject = new JSONObject();
                }
                mProfileFieldsObject.put(mContext.getResources().getString(R.string.price),
                        GlobalFunctions.getFormattedCurrencyString(responseObject.optString("currency"),
                                Double.parseDouble(responseObject.optString("price"))));
            }

            if (mProfileFieldsObject != null) {

                CustomViews.generateInfoFieldsView(mContext, mProfileFieldsObject, profileFieldLayout);
                mRootView.findViewById(R.id.profile_field_divider).setVisibility(View.VISIBLE);
            } else {
                profileFieldLayout.setVisibility(View.GONE);
            }

            /* Set text and visibility of follow / unfollow button */

            if (isShowFollowButton && !mAppConst.isLoggedOutUser()) {
                mRootView.findViewById(R.id.follow_block).setVisibility(View.VISIBLE);
                switch (mCurrentSelectedModule){
                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        if (SitePageProfilePage.sIsPageFollowed == 0)
                            mFollowText.setText(getResources().getString(R.string.follow_page_button));
                        else
                            mFollowText.setText(getResources().getString(R.string.unfollow_page_button));
                        break;
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (AdvGroupsProfile.sIsGroupFollowed == 0)
                            mFollowText.setText(getResources().getString(R.string.follow_page_button));
                        else
                            mFollowText.setText(getResources().getString(R.string.unfollow_page_button));
                        break;
                }
            } else {
                mRootView.findViewById(R.id.follow_block).setVisibility(View.GONE);
            }

            /* Set text and visibility of review button */

            if (reviewCreateUpdate != 0 && !mAppConst.isLoggedOutUser()) {
                mRootView.findViewById(R.id.review_block).setVisibility(View.VISIBLE);
                if (reviewCreateUpdate == 1)
                    mWriteReviewView.setText(getResources().getString(R.string.update_review_button_text));
                else
                    mWriteReviewView.setText(getResources().getString(R.string.write_review_button_text));
            } else {
                mRootView.findViewById(R.id.review_block).setVisibility(View.GONE);
            }

            /* Set visibility of contact button */

            if (mCurrentSelectedModule.equals(ConstantVariables.SITE_PAGE_MENU_TITLE)) {
                mContactText.setText(getResources().getString(R.string.contact_info_button_text));
                mRootView.findViewById(R.id.contact_info_block).setVisibility(View.VISIBLE);
            } else {
                mRootView.findViewById(R.id.contact_info_block).setVisibility(View.GONE);
            }

            /* Set font icon on button */

            mContactIcon.setText("\uf095");
            mWriteReviewIcon.setText("\uf040");
            mFollowIcon.setText("\uf087");

            // Showing description block if there is any text in body.
            if (body != null && !body.isEmpty()) {
                mViewDescription.setVisibility(View.VISIBLE);

                /* Setting Body in TextView */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mViewDescription.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                } else {
                    mViewDescription.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                }
                mViewDescription.loadDataWithBaseURL("file:///android_asset/",
                        GlobalFunctions.getHtmlData(mContext, body, true), "text/html", "utf-8", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){

            case R.id.review_block:
                if (reviewCreateUpdate == 1) {

                    String updateUrl = AppConstant.DEFAULT_URL + updateReviewUrl;
                    intent = new Intent(mContext, EditEntry.class);
                    intent.putExtra(ConstantVariables.URL_STRING, updateUrl);
                    intent.putExtra(ConstantVariables.FORM_TYPE, "update_review");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                    startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    String redirectUrl = AppConstant.DEFAULT_URL + mPluralUrlText + "/reviews/create/" + mContentId;
                    intent = new Intent(mContext, CreateNewEntry.class);
                    intent.putExtra(ConstantVariables.CREATE_URL, redirectUrl);
                    intent.putExtra(ConstantVariables.FORM_TYPE, "create_review");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                    intent.putExtra(ConstantVariables.REQUEST_CODE, ConstantVariables.CREATE_REQUEST_CODE);
                    startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.contact_info_block:
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "contact_info");
                bundle.putString(ConstantVariables.CONTENT_TITLE, getResources().getString(R.string.contact_info_page_title));
                bundle.putString(ConstantVariables.FORM_TYPE, "contactInfo");
                bundle.putInt(ConstantVariables.IS_OWNER, mIsOwner);
                bundle.putInt("page_id", mPageId);
                if (mContactInfoObject != null) {
                    bundle.putString(ConstantVariables.RESPONSE_OBJECT, mContactInfoObject.toString());
                }
                Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
                newIntent.putExtras(bundle);
                mContext.startActivity(newIntent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.follow_block:
                String redirectUrl = AppConstant.DEFAULT_URL + mUrlText + "/follow/" + mContentId;
                switch (mCurrentSelectedModule){
                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        if (SitePageProfilePage.sIsPageFollowed == 0) {
                            dialogueMessage = getResources().getString(R.string.follow_page_message);
                            dialogueTitle = getResources().getString(R.string.follow_page_title);
                            dialogueButton = getResources().getString(R.string.follow_page_button);
                            successMessage = getResources().getString(R.string.follow_page_success_message);
                            performAction(redirectUrl, dialogueMessage, dialogueTitle, dialogueButton,
                                    successMessage, getResources().getString(R.string.unfollow_page_button), postParams);

                        }else {
                            dialogueMessage = getResources().getString(R.string.unfollow_page_message);
                            dialogueTitle = getResources().getString(R.string.unfollow_page_title);
                            dialogueButton = getResources().getString(R.string.unfollow_page_button);
                            successMessage = getResources().getString(R.string.unfollow_page_success_message);
                            performAction(redirectUrl, dialogueMessage, dialogueTitle, dialogueButton,
                                    successMessage, getResources().getString(R.string.follow_page_button), postParams);
                        }
                        break;
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (AdvGroupsProfile.sIsGroupFollowed == 0) {
                            dialogueMessage = getResources().getString(R.string.follow_group_message);
                            dialogueTitle = getResources().getString(R.string.follow_group_title);
                            dialogueButton = getResources().getString(R.string.follow);
                            successMessage = getResources().getString(R.string.follow_group_success_message);
                            performAction(redirectUrl, dialogueMessage, dialogueTitle, dialogueButton,
                                    successMessage, getResources().getString(R.string.unfollow_page_button), postParams);

                        }else {
                            dialogueMessage = getResources().getString(R.string.unfollow_group_message);
                            dialogueTitle = getResources().getString(R.string.unfollow_group_title);
                            dialogueButton = getResources().getString(R.string.unfollow);
                            successMessage = getResources().getString(R.string.unfollow_group_success_message);
                            performAction(redirectUrl, dialogueMessage, dialogueTitle, dialogueButton,
                                    successMessage, getResources().getString(R.string.follow_page_button), postParams);
                        }
                        break;
                }
                break;

            case R.id.owner_title:
            case R.id.owner_image:
                if (mUserId != 0) {
                    intent = new Intent(mContext, userProfile.class);
                    intent.putExtra(ConstantVariables.USER_ID, mUserId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public void performAction(String url, String message, String title, String buttonTitle,
                              String showSuccessMessage, final String selectedMenuName, Map<String, String> params){

        try {
            actionUrl = url;
            successMessage = showSuccessMessage;
            postParams = params;

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

            alertBuilder.setMessage(message);
            alertBuilder.setTitle(title);

            alertBuilder.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mAppConst.showProgressDialog();
                    mAppConst.postJsonResponseForUrl(actionUrl, postParams, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {
                            mAppConst.hideProgressDialog();
                                /* Show Message */
                            switch (selectedMenuName) {
                                case "Unfollow":
                                case "Follow":
                                    switch (mCurrentSelectedModule){
                                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                                            if (SitePageProfilePage.sIsPageFollowed == 0) {
                                                mFollowText.setText(getResources().getString(R.string.unfollow_page_button));
                                                SitePageProfilePage.sIsPageFollowed = 1;
                                            } else {
                                                mFollowText.setText(getResources().getString(R.string.follow_page_button));
                                                SitePageProfilePage.sIsPageFollowed = 0;
                                            }
                                            break;
                                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                                            if (AdvGroupsProfile.sIsGroupFollowed == 0) {
                                                mFollowText.setText(getResources().getString(R.string.unfollow_page_button));
                                                AdvGroupsProfile.sIsGroupFollowed = 1;
                                            } else {
                                                mFollowText.setText(getResources().getString(R.string.follow_page_button));
                                                AdvGroupsProfile.sIsGroupFollowed = 0;
                                            }
                                            break;
                                    }

                                    break;
                            }

                            SnackbarUtils.displaySnackbar(mRootView, successMessage);
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            mAppConst.hideProgressDialog();
                            SnackbarUtils.displaySnackbar(mRootView, message);
                        }
                    });
                }
            });

            alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.delete_account_cancel_button_text),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertBuilder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
