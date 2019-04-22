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

package com.fellopages.mobileapp.classes.common.ads.communityAds;


import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BaseButton;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SponsoredStoriesList;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoveAdHolder extends RecyclerView.ViewHolder{

    TextView mRemoveAdDescription, mUndoTextView;
    RadioGroup mRemoveAdOptions;
    EditText editText;
    BaseButton mReportAdButton;
    public View adView;
    public CommunityAdsList mCommunityAd;
    public SponsoredStoriesList mSponsoredStory;

    private static RecyclerView.Adapter mRecyclerViewAdapter;
    private static ArrayAdapter<Object> mArrayAdapter;
    private ArrayList<Integer> mRemoveAdsPositions;
    private AppConstant mAppConst;
    private List<Object> mBrowseListItems;

    public RemoveAdHolder(RecyclerView.Adapter recyclerViewAdapter, View itemView, ArrayList<Integer> removeAds,
                          List<Object> listItem) {
        super(itemView);
        mRecyclerViewAdapter = recyclerViewAdapter;
        adView = itemView;
        mRemoveAdsPositions = removeAds;
        mBrowseListItems = listItem;
    }

    public RemoveAdHolder(ArrayAdapter<Object> arrayAdapter, View itemView, ArrayList<Integer> removeAds, List<Object> listItem) {
        super(itemView);
        mArrayAdapter = arrayAdapter;
        adView = itemView;
        mRemoveAdsPositions = removeAds;
        mBrowseListItems = listItem;
    }

    /**
     * function to generate a view to remove Sponsored story
     * @param storiesList sponsored story list on which remove story button is clicked
     * @param adView
     * @param context
     * @param adPosition
     */
    public void removeAd(final SponsoredStoriesList storiesList, final View adView, final Context context, final int adPosition){

        mAppConst = new AppConstant(context);

        mUndoTextView = adView.findViewById(R.id.undoTextView);
        mRemoveAdOptions = adView.findViewById(R.id.removeAdsOption);
        mRemoveAdDescription = adView.findViewById(R.id.removeAdDescription);
        editText = adView.findViewById(R.id.editText);
        mReportAdButton = adView.findViewById(R.id.report_ad_button);

        editText.setVisibility(View.GONE);
        editText.setText(context.getResources().getString(R.string.blank_string));
        mReportAdButton.setVisibility(View.GONE);

        mRemoveAdDescription.setText(context.getResources().getString(R.string.report_ad_description));

        mUndoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mUndoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRemoveAdsPositions.contains(adPosition)) {
                    mRemoveAdsPositions.remove(adPosition);
                    if(mRecyclerViewAdapter != null){
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    } else if (mArrayAdapter != null){
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        JSONArray removeAdsObject = storiesList.getRemoveAdsOptions();
        JSONObject removeAdsOptions = removeAdsObject.optJSONObject(0).optJSONObject("multiOptions");

        JSONArray propertyNames = removeAdsOptions.names();

        final HashMap<String, String> _propmap = new HashMap<>();
        final ArrayAdapter<String> _adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

        mRemoveAdOptions.removeAllViews();
        for(int  i = 0; i < removeAdsOptions.length(); i++){
            String name = propertyNames.optString(i);
            String radioButtonText = removeAdsOptions.optString(name);

            _adapter.add( radioButtonText);
            _propmap.put(radioButtonText, name );

            AppCompatRadioButton newRadioButton = new AppCompatRadioButton(context);
            newRadioButton.setText(radioButtonText);
            newRadioButton.setId(i);

            mRemoveAdOptions.addView(newRadioButton);
        }


        mRemoveAdOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                String adCancelReason = _propmap.get(_adapter.getItem(i));
                if(adCancelReason.equals("Other")){
                    editText.setVisibility(View.VISIBLE);
                    mReportAdButton.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                    mReportAdButton.setVisibility(View.GONE);

                    removeAndLoadNewStory(context, adView, adPosition, storiesList, adCancelReason, null);
                }
            }
        });

        mReportAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAndLoadNewStory(context, adView, adPosition, storiesList, "Other", editText.getText().toString());
            }
        });
    }

    /**
     * function to generate a view to remove Community Ads
     * @param communityAdsList sponsored story list on which remove Ad button is clicked
     * @param adView
     * @param context
     * @param adPosition
     */
    public void removeAd(final CommunityAdsList communityAdsList, final View adView, final Context context, final int adPosition){

        mAppConst = new AppConstant(context);

        mUndoTextView = adView.findViewById(R.id.undoTextView);
        mRemoveAdOptions = adView.findViewById(R.id.removeAdsOption);
        mRemoveAdDescription = adView.findViewById(R.id.removeAdDescription);
        editText = adView.findViewById(R.id.editText);
        mReportAdButton = adView.findViewById(R.id.report_ad_button);

        editText.setVisibility(View.GONE);
        editText.setText(context.getResources().getString(R.string.blank_string));
        mReportAdButton.setVisibility(View.GONE);

        mRemoveAdDescription.setText(context.getResources().getString(R.string.report_ad_description));

        mUndoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mUndoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRemoveAdsPositions.contains(adPosition)) {
                    mRemoveAdsPositions.remove(adPosition);
                    if(mRecyclerViewAdapter != null){
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    } else if (mArrayAdapter != null){
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        JSONArray removeAdsObject = communityAdsList.getRemoveAdsOptions();
        JSONObject removeAdsOptions = removeAdsObject.optJSONObject(0).optJSONObject("multiOptions");

        JSONArray propertyNames = removeAdsOptions.names();

        final HashMap<String, String> _propmap = new HashMap<>();
        final ArrayAdapter<String> _adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

        mRemoveAdOptions.removeAllViews();
        for(int  i = 0; i < removeAdsOptions.length(); i++){
            String name = propertyNames.optString(i);
            String radioButtonText = removeAdsOptions.optString(name);

            _adapter.add( radioButtonText);
            _propmap.put(radioButtonText, name );

            AppCompatRadioButton newRadioButton = new AppCompatRadioButton(context);
            newRadioButton.setText(radioButtonText);
            newRadioButton.setId(i);

            mRemoveAdOptions.addView(newRadioButton);
        }


        mRemoveAdOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                String adCancelReason = _propmap.get(_adapter.getItem(i));
                if(adCancelReason.equals("Other")){
                    editText.setVisibility(View.VISIBLE);
                    mReportAdButton.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                    mReportAdButton.setVisibility(View.GONE);

                    removeAndLoadNewAdd(context, adView, adPosition, communityAdsList, adCancelReason, null);
                }
            }
        });

        mReportAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAndLoadNewAdd(context, adView, adPosition, communityAdsList, "Other", editText.getText().toString());
            }
        });
    }

    private void removeAndLoadNewStory(final Context context, final View adView, final int position, final SponsoredStoriesList sponsoredStoriesList,
                                     String adCancelReason, String text){

        String removeAdsUrl = UrlUtil.REMOVE_COMMUNITY_ADS_URL + "?adsId=" +
                sponsoredStoriesList.getmAdId() + "&placementCount=" +
                ConstantVariables.FEED_ADS_POSITION + "&type=" + ConstantVariables.FEED_ADS_TYPE;

        HashMap<String, String> adParams = new HashMap<>();
        adParams.put("adCancelReason", adCancelReason);
        if(text != null && !text.isEmpty()){
            adParams.put("adDescription", text);
        }
        mAppConst.hideKeyboard();

        mAppConst.postJsonResponseForUrl(removeAdsUrl, adParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if(mRemoveAdsPositions.contains(position)){
                    mRemoveAdsPositions.remove(position);
                }
                SnackbarUtils.displaySnackbar(adView, context.getResources().
                        getString(R.string.ad_report_successful_submitted));
                if(jsonObject.optJSONArray("advertisments") != null && jsonObject.optJSONArray("advertisments").length() != 0){
                    JSONObject singleAdObject = jsonObject.optJSONArray("advertisments").optJSONObject(0);

                    int adId = singleAdObject.optInt("ad_id");
                    int resourceId = singleAdObject.optInt("resource_id");
                    String resourceType = singleAdObject.optString("resource_type");
                    String resourceTitle = singleAdObject.optString("resource_title");
                    JSONArray likes = singleAdObject.optJSONArray("likes");
                    String contentUrl = singleAdObject.optString("content_url");
                    String image = singleAdObject.optString("image");
                    int isLike = singleAdObject.optInt("isLike");
                    String moduleTitle = singleAdObject.optString("module_title");

                    sponsoredStoriesList.setmAdId(adId);
                    sponsoredStoriesList.setmResourceId(resourceId);
                    sponsoredStoriesList.setmResourceType(resourceType);
                    sponsoredStoriesList.setmResourceTitle(resourceTitle);
                    sponsoredStoriesList.setmLikesArray(likes);
                    sponsoredStoriesList.setmContentUrl(contentUrl);
                    sponsoredStoriesList.setImage(image);
                    sponsoredStoriesList.setIsLike(isLike);
                    sponsoredStoriesList.setmModuleTitle(moduleTitle);

                    if(mRecyclerViewAdapter != null){
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    } else if (mArrayAdapter != null){
                        mArrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    if(mBrowseListItems != null) {
                        mBrowseListItems.remove(position);
                        if(mRecyclerViewAdapter != null){
                            mRecyclerViewAdapter.notifyItemRemoved(position);
                        } else {
                            mArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }


    private void removeAndLoadNewAdd(final Context context, final View adView, final int position, final CommunityAdsList communityAdsList,
                                     String adCancelReason, String text){

        String removeAdsUrl = UrlUtil.REMOVE_COMMUNITY_ADS_URL + "?adsId=" +
                communityAdsList.getmCommunityAdsId() + "&placementCount=" +
                ConstantVariables.FEED_ADS_POSITION + "&type=" + ConstantVariables.FEED_ADS_TYPE;

        HashMap<String, String> adParams = new HashMap<>();
        adParams.put("adCancelReason", adCancelReason);
        if(text != null && !text.isEmpty()){
            adParams.put("adDescription", text);
        }
        mAppConst.hideKeyboard();

        mAppConst.postJsonResponseForUrl(removeAdsUrl, adParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if(mRemoveAdsPositions.contains(position)){
                    mRemoveAdsPositions.remove(position);
                }
                SnackbarUtils.displaySnackbar(adView, context.getResources().
                        getString(R.string.ad_report_successful_submitted));

                if(jsonObject.optJSONArray("advertisments") != null && jsonObject.optJSONArray("advertisments").length() != 0){
                    JSONObject singleAdObject = jsonObject.optJSONArray("advertisments").optJSONObject(0);
                    int adId = singleAdObject.optInt("userad_id");
                    String ad_type = singleAdObject.optString("ad_type");
                    String cads_title = singleAdObject.optString("cads_title");
                    String cads_body = singleAdObject.optString("cads_body");
                    String cads_url = singleAdObject.optString("cads_url");
                    String image = singleAdObject.optString("image");

                    communityAdsList.setmCommunityAdBody(cads_body);
                    communityAdsList.setmCommunityAdImage(image);
                    communityAdsList.setmCommunityAdsId(adId);
                    communityAdsList.setmCommunityAdType(ad_type);
                    communityAdsList.setmCommunityAdTitle(cads_title);
                    communityAdsList.setmCommunityAdUrl(cads_url);

                    if(mRecyclerViewAdapter != null){
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    } else if (mArrayAdapter != null){
                        mArrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    if(mBrowseListItems != null) {
                        mBrowseListItems.remove(position);
                        if(mRecyclerViewAdapter != null){
                            mRecyclerViewAdapter.notifyItemRemoved(position);
                        } else {
                            mArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }
}