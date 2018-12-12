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

package com.fellopages.mobileapp.classes.common.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
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
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoTabFragment extends Fragment implements View.OnClickListener {

    private View mRootView;
    private Context mContext;
    private AppConstant mAppConst;
    private Typeface fontIcon;
    private GridLayout profileFieldLayout;
    private ImageView mOwnerImage;
    private WebView mViewDescription, mViewBody;
    private SelectableTextView mCoverTitle;
    private TextView mOwnerName, mWriteReviewIcon, mWriteReviewView, mAddWishListIcon, mCategory, mPostByText,
            mListingPostDate, mExpiryListingInfo, mApplyNowIcon, mApplyNowText;
    private LinearLayout mOwnerDetailBlock, mSimpleViewLayout, mWishListBlock;
    private int mMLTViewType, mListingTypeId, mUserId, categoryId, mListingId, reviewCreateUpdate = 0,
            mReviewId, canAddToWishList = 0, canApplyNow = 0, isApplyJob;
    private String category;
    private JSONObject mResponseJsonObject;
    private Map<String, String> mProfileFieldMap;
    private LinearLayout.LayoutParams layoutParams;
    private ImageLoader mImageLoader;
    private LinearLayout mApplyNowBlock;
    private String applyNowUrl, applyNowLabel;

    public InfoTabFragment() {
        // Required empty public constructor
    }

    public static InfoTabFragment newInstance(Bundle bundle) {
        InfoTabFragment fragment = new InfoTabFragment();
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
        mProfileFieldMap = new LinkedHashMap<>();
        mImageLoader = new ImageLoader(mContext);

        updateData(bundle, false);

        // Initializing respective views according to view types.
        initializeView(mMLTViewType);

        return mRootView;
    }

    public void updateData(Bundle bundle, boolean isUpdateRequest) {

        //Getting intent data from MLT.
        mMLTViewType = bundle.getInt(ConstantVariables.MLT_VIEW_TYPE, 3);
        mListingId = bundle.getInt(ConstantVariables.LISTING_ID, 0);
        mListingTypeId = bundle.getInt(ConstantVariables.LISTING_TYPE_ID, 0);

        try {
            String mDataResponse = bundle.getString(ConstantVariables.RESPONSE_OBJECT);
            mResponseJsonObject = new JSONObject(mDataResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isUpdateRequest && isAdded()) {
            setDataInViews(mMLTViewType, mResponseJsonObject);
        }
    }

    /**
     * Method to initialize view according to view type.
     *
     * @param mMLTViewType type contains the view page type in which view page needs to be shown.
     */
    public void initializeView(int mMLTViewType) {

        switch (mMLTViewType) {
            case ConstantVariables.BLOG_VIEW:

                mSimpleViewLayout = (LinearLayout) mRootView.findViewById(R.id.simple_view_layout);
                mCategory = (TextView) mRootView.findViewById(R.id.category);
                mCoverTitle = (SelectableTextView) mRootView.findViewById(R.id.view_title);
                mPostByText = (TextView) mRootView.findViewById(R.id.post_by_text);
                mOwnerName = (TextView) mRootView.findViewById(R.id.listing_post_owner);
                mListingPostDate = (TextView) mRootView.findViewById(R.id.listing_post_date);
                mCategory.setOnClickListener(this);
                mOwnerName.setOnClickListener(this);
                break;

            case ConstantVariables.CLASSIFIED_VIEW_WITH_CAROUSEL:
                mWriteReviewIcon = (TextView) mRootView.findViewById(R.id.review_icon);
                mWriteReviewView = (TextView) mRootView.findViewById(R.id.review_text);
                mAddWishListIcon = (TextView) mRootView.findViewById(R.id.wishlist_icon);
                mWishListBlock = (LinearLayout) mRootView.findViewById(R.id.wishlist_block);
                mApplyNowIcon = (TextView) mRootView.findViewById(R.id.apply_now_icon);
                mApplyNowText = (TextView) mRootView.findViewById(R.id.apply_now);
                mApplyNowBlock = (LinearLayout) mRootView.findViewById(R.id.apply_now_block);
                mRootView.findViewById(R.id.apply_now_block).setOnClickListener(this);
                mRootView.findViewById(R.id.review_block).setOnClickListener(this);
                mRootView.findViewById(R.id.wishlist_block).setOnClickListener(this);
                mWriteReviewIcon.setTypeface(fontIcon);
                mAddWishListIcon.setTypeface(fontIcon);
                mApplyNowIcon.setTypeface(fontIcon);
                mRootView.findViewById(R.id.review_block).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.wishlist_block).setVisibility(View.VISIBLE);

            case ConstantVariables.CLASSIFIED_VIEW_WITHOUT_CAROUSEL:
                profileFieldLayout = (GridLayout) mRootView.findViewById(R.id.profileFieldLayout);
                mOwnerImage = (ImageView) mRootView.findViewById(R.id.owner_image);
                mOwnerName = (TextView) mRootView.findViewById(R.id.owner_title);
                mOwnerDetailBlock = (LinearLayout) mRootView.findViewById(R.id.owner_detail_layout);
                mOwnerName.setOnClickListener(this);
                mOwnerImage.setOnClickListener(this);
                break;
        }
        mExpiryListingInfo = (TextView) mRootView.findViewById(R.id.expiryListingInfo);
        mViewDescription = (WebView) mRootView.findViewById(R.id.view_description);
        GlobalFunctions.setWebSettings(mViewDescription, false);

        layoutParams = CustomViews.getFullWidthLayoutParams();

        //Setting data in respective views
        setDataInViews(mMLTViewType, mResponseJsonObject);
    }

    /**
     * Method for setting the data in respective views according to view types.
     *
     * @param mMLTViewType        type for which data is to be set in views.
     * @param mResponseJsonObject response object.
     */
    public void setDataInViews(int mMLTViewType, JSONObject mResponseJsonObject) {

        try {
            mProfileFieldMap.clear();
            JSONArray mGutterMenus = mResponseJsonObject.optJSONArray("gutterMenu");
            mListingId = mResponseJsonObject.optInt("listing_id");
            mUserId = mResponseJsonObject.optInt("owner_id");
            String title = mResponseJsonObject.optString("title");
            String body = mResponseJsonObject.optString("body");
            if (mMLTViewType == ConstantVariables.BLOG_VIEW && mResponseJsonObject.optString("overview") != null
                    && !mResponseJsonObject.optString("overview").isEmpty()) {
                body = mResponseJsonObject.optString("overview");
            }

            String ownerTitle = mResponseJsonObject.optString("owner_title");
            categoryId = mResponseJsonObject.optInt("category_id");
            String creationDate = mResponseJsonObject.optString("creation_date");
            String ownerImage = mResponseJsonObject.optString("owner_image");
            String expiryListingInfo = mResponseJsonObject.optString("expiryString");
            String expiryListingInfoColor = mResponseJsonObject.optString("expiryStringColor");
            category = mResponseJsonObject.optString("categoryName");
            isApplyJob = mResponseJsonObject.optInt("isApplyJob");

            if (mGutterMenus != null) {
                for (int i = 0; i < mGutterMenus.length(); i++) {
                    JSONObject menuJsonObject = mGutterMenus.optJSONObject(i);
                    JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
                    if (menuJsonObject.optString("name").equals("update")) {
                        reviewCreateUpdate = 1;
                        mReviewId = urlParams.optInt("review_id");
                    }
                    if (menuJsonObject.optString("name").equals("review")) {
                        reviewCreateUpdate = 2;
                    }
                    if (menuJsonObject.optString("name").equals("wishlist")) {
                        canAddToWishList = 1;
                    }
                    if (menuJsonObject.optString("name").equals("apply-now")) {
                        canApplyNow = 1;
                        applyNowUrl = AppConstant.DEFAULT_URL + menuJsonObject.optString("url");
                        applyNowLabel = menuJsonObject.optString("label");
                    }

                }
            }

            switch (mMLTViewType) {

                case ConstantVariables.BLOG_VIEW:
                    mSimpleViewLayout.setVisibility(View.VISIBLE);
                    mCoverTitle.setText(title);
                    mCategory.setText(category);
                    mPostByText.setText("- " + mContext.getResources().getString(R.string.by_text));
                    mOwnerName.setText(ownerTitle);
                    mListingPostDate.setText("- " + AppConstant.convertDateFormat(mContext.getResources(), creationDate));
                    break;

                case ConstantVariables.CLASSIFIED_VIEW_WITH_CAROUSEL:
                    if (reviewCreateUpdate != 0 && !mAppConst.isLoggedOutUser()) {
                        mRootView.findViewById(R.id.review_block).setVisibility(View.VISIBLE);
                        if (reviewCreateUpdate == 1)
                            mWriteReviewView.setText(getResources().getString(R.string.update_review_button_text));
                        else
                            mWriteReviewView.setText(getResources().getString(R.string.write_review_button_text));
                    } else {
                        mRootView.findViewById(R.id.review_block).setVisibility(View.GONE);
                    }

                    if (canAddToWishList == 1 && !mAppConst.isLoggedOutUser()) {
                        mWishListBlock.setVisibility(View.VISIBLE);
                    } else {
                        mRootView.findViewById(R.id.wishlist_block).setVisibility(View.GONE);
                    }

                    if (canApplyNow == 1 || mResponseJsonObject.has("isApplyJob")) {
                        mApplyNowBlock.setVisibility(View.VISIBLE);
                        if (isApplyJob == 1) {
                            mApplyNowText.setText(getResources().getString(R.string.already_applied_text));
                            mApplyNowText.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                            mApplyNowBlock.setClickable(false);

                        } else {
                            mApplyNowText.setText(getResources().getString(R.string.apply_now_button_text));
                        }
                    } else {
                        mApplyNowBlock.setVisibility(View.GONE);
                    }

                    mWriteReviewIcon.setText("\uf040");
                    mAddWishListIcon.setText("\uf067");
                    mApplyNowIcon.setText("\uf090");

                case ConstantVariables.CLASSIFIED_VIEW_WITHOUT_CAROUSEL:
                    mRootView.findViewById(R.id.owner_view_divider).setVisibility(View.VISIBLE);
                    mOwnerDetailBlock.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    mOwnerDetailBlock.setVisibility(View.VISIBLE);
                    mImageLoader.setImageForUserProfile(ownerImage, mOwnerImage);
                    mOwnerName.setText(ownerTitle);

                    JSONObject mProfileFieldsObject = mResponseJsonObject.optJSONObject("profile_fields");
                    if (!mResponseJsonObject.optString("price").equals("0") &&
                            !mResponseJsonObject.optString("price").isEmpty()) {
                        if (mProfileFieldsObject == null) {
                            mProfileFieldsObject = new JSONObject();
                        }
                        mProfileFieldsObject.put(mContext.getResources().getString(R.string.price),
                                GlobalFunctions.getFormattedCurrencyString(mResponseJsonObject.optString("currency"),
                                        Double.parseDouble(mResponseJsonObject.optString("price"))));
                    }
                    if (mProfileFieldsObject != null) {

                        CustomViews.generateInfoFieldsView(mContext, mProfileFieldsObject, profileFieldLayout);
                        mRootView.findViewById(R.id.profile_field_divider).setVisibility(View.VISIBLE);
                    } else {
                        profileFieldLayout.setVisibility(View.GONE);
                        mRootView.findViewById(R.id.profile_field_divider).setVisibility(View.GONE);
                    }
                    break;
            }
            //Showing end date expiration message.
            if (expiryListingInfo != null && !expiryListingInfo.isEmpty() && expiryListingInfoColor != null &&
                    !expiryListingInfoColor.isEmpty()) {
                mExpiryListingInfo.setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.expiry_listing_info_layout_divider).setVisibility(View.VISIBLE);
                if (expiryListingInfoColor.equals("G")) {
                    mExpiryListingInfo.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    String endDate = mResponseJsonObject.optString("end_date");
                    String dateFormat = AppConstant.getMonthFromDate(endDate, "MMM") + " " + AppConstant.getDayFromDate(endDate) +
                            ", " + AppConstant.getYearFormat(endDate);
                    mExpiryListingInfo.setText(Html.fromHtml(expiryListingInfo).toString().trim() + " " +
                            dateFormat);
                } else {
                    mExpiryListingInfo.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    mExpiryListingInfo.setText(Html.fromHtml(expiryListingInfo).toString().trim());
                }
            } else {
                mExpiryListingInfo.setVisibility(View.GONE);
            }

            //Showing description block if there is any text in body.
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
        switch (id) {

            case R.id.review_block:
                if (reviewCreateUpdate == 1) {
                    String reviewUpdateUrl = AppConstant.DEFAULT_URL + "listings/review/update/" + mListingId +
                            "?listing_id=" + mListingId + "&listingtype_id=" + mListingTypeId + "&review_id=" + mReviewId;
                    intent = new Intent(mContext, EditEntry.class);
                    intent.putExtra(ConstantVariables.URL_STRING, reviewUpdateUrl);
                    intent.putExtra(ConstantVariables.FORM_TYPE, "update_review");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                    startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    String reviewCreateUrl = AppConstant.DEFAULT_URL + "listings/review/create/" + mListingId +
                            "?listing_id=" + mListingId + "&listingtype_id=" + mListingTypeId;
                    intent = new Intent(mContext, CreateNewEntry.class);
                    intent.putExtra(ConstantVariables.CREATE_URL, reviewCreateUrl);
                    intent.putExtra(ConstantVariables.FORM_TYPE, "create_review");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                    startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                break;

            case R.id.wishlist_block:
                String wishlistAddUrl = AppConstant.DEFAULT_URL + "listings/wishlist/add" +
                        "?listing_id=" + mListingId + "&listingtype_id=" + mListingTypeId;
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, wishlistAddUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, "add_wishlist");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.owner_title:
            case R.id.owner_image:
            case R.id.listing_post_owner:
                if (mUserId != 0) {
                    intent = new Intent(mContext, userProfile.class);
                    intent.putExtra(ConstantVariables.USER_ID, mUserId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.category:
                intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                intent.putExtra(ConstantVariables.CATEGORY_ID, String.valueOf(categoryId));
                intent.putExtra(ConstantVariables.CATEGORY_VALUE, category);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.apply_now_block:
                    intent = new Intent(mContext, EditEntry.class);
                    intent.putExtra(ConstantVariables.URL_STRING, applyNowUrl);
                    intent.putExtra(ConstantVariables.CONTENT_TITLE, applyNowLabel);
                    intent.putExtra(ConstantVariables.FORM_TYPE, "apply_now");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
