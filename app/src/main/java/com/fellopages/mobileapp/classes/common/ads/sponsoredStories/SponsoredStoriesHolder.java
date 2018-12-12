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

package com.fellopages.mobileapp.classes.common.ads.sponsoredStories;


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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SponsoredStoriesList;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SponsoredStoriesHolder extends RecyclerView.ViewHolder {

    public SponsoredStoriesList mSponsoredStory;
    public View adView;
    private static int mPlacementCount, mAdType;
    private static RecyclerView.Adapter mRecyclerViewAdapter;
    private static HashMap<String, String> clickableParts;
    private static List mIncludedModulesList, mDeletedModulesList;
    private static ArrayList<Integer> mRemoveAdsPosition;
    private static String likeUnlikeUrl;

    public SponsoredStoriesHolder(RecyclerView.Adapter recyclerViewAdapter, View itemView,
                                  int placementCount, int adType, ArrayList<Integer> removeAdsPosition) {
        super(itemView);
        mRecyclerViewAdapter = recyclerViewAdapter;
        adView = itemView;
        mPlacementCount = placementCount;
        mAdType = adType;

        mIncludedModulesList = Arrays.asList(ConstantVariables.INCLUDED_MODULES_ARRAY);
        mDeletedModulesList = Arrays.asList(ConstantVariables.DELETED_MODULES);

        mRemoveAdsPosition = removeAdsPosition;
    }

    public static void inflateAd(final SponsoredStoriesList mSponsoredStory,
                                 final View adView, final Context context, final int adPosition) {

        final AppConstant mAppConst = new AppConstant(context);
        clickableParts = new HashMap<>();

        TextView storyTitle = (TextView) adView.findViewById(R.id.story_title);
        storyTitle.setMovementMethod(LinkMovementMethod.getInstance());
        BezelImageView userProfileImage = (BezelImageView) adView.findViewById(R.id.user_profile_image);

        ImageView adCover = (ImageView) adView.findViewById(R.id.attachment_preview);
        TextView adTitle = (TextView) adView.findViewById(R.id.attachment_title);
        TextView likeCount = (TextView) adView.findViewById(R.id.like_count);

        final TextView likeView = (TextView) adView.findViewById(R.id.like_view);
        likeView.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        likeView.setText("\uf164");

        String userLikedTitle = null, profileImage = null;


        /**
         * Code to make story title according to the number of users liked this story
         * Make the name of users and content title clickable spans
         * redirect to the view pages of corresponding content
         */
        if(mSponsoredStory.getmLikesArray() != null && mSponsoredStory.getmLikesArray().length() != 0){
            adView.findViewById(R.id.userLikedView).setVisibility(View.VISIBLE);

            int friendFlag = 0;
            String [] userTitleArray = new String [mSponsoredStory.getmLikesArray().length()];

            String likeText = context.getResources().getQuantityString(R.plurals.comment_page_like,
                    mSponsoredStory.getmLikesArray().length());
            String andText = context.getResources().getString(R.string.and);

            for(int i = 0; i < mSponsoredStory.getmLikesArray().length(); i++){

                JSONObject likeJsonObject = mSponsoredStory.getmLikesArray().optJSONObject(i);
                if(friendFlag == 0){
                    profileImage = likeJsonObject.optString("image_profile");
                }

                clickableParts.put("user-" + likeJsonObject.optInt("user_id"), likeJsonObject.optString("user_title"));
                userTitleArray[i] = likeJsonObject.optString("user_title");

                switch (friendFlag){
                    case 0:
                        userLikedTitle = String.format(context.getResources().
                                        getString(R.string.one_friend_like_text),userTitleArray [0],
                                likeText, mSponsoredStory.getmResourceTitle());
                        break;
                    case 1:
                        userLikedTitle = String.format(context.getResources().
                                        getString(R.string.two_friend_like_text),userTitleArray [0], andText,
                                userTitleArray [1], likeText, mSponsoredStory.getmResourceTitle());
                        break;
                    default:
                        userLikedTitle = String.format(context.getResources().
                                        getString(R.string.three_friend_like_text),userTitleArray [0],
                                userTitleArray [1], andText, userTitleArray [2], likeText, mSponsoredStory.getmResourceTitle());
                        break;
                }
                friendFlag++;
            }
            clickableParts.put(mSponsoredStory.getmResourceType() + "-" + mSponsoredStory.getmResourceId(),
                    mSponsoredStory.getmResourceTitle());
        } else {
            adView.findViewById(R.id.userLikedView).setVisibility(View.GONE);
        }

        if (clickableParts != null && clickableParts.size() != 0 && userLikedTitle != null) {
            SpannableString text = new SpannableString(userLikedTitle);
            SortedSet<String> keys = new TreeSet<>(clickableParts.keySet());

            CharSequence title = Html.fromHtml(userLikedTitle);

            int lastIndex = 0;
            for (String key : keys) {

                String[] keyParts = key.split("-");
                final String attachment_type = keyParts[0];
                final int attachment_id = Integer.parseInt(keyParts[1]);

                final String value = clickableParts.get(key);

                if (value != null && !value.isEmpty()) {
                    int i1 = title.toString().indexOf(value);
                    if (i1 != -1) {
                        int i2 = i1 + value.length();
                        if (lastIndex != -1) {
                            lastIndex += value.length();
                        }

                        ClickableSpan myClickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                redirectToActivity(value, attachment_type, attachment_id,
                                        mSponsoredStory.getmContentUrl(), context);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setFakeBoldText(true);
                                ds.setColor(ContextCompat.getColor(context, R.color.black));
                            }
                        };
                        text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            storyTitle.setText(text);
        }

        ImageLoader imageLoader = new ImageLoader(context);
        imageLoader.setImageForUserProfile(profileImage, userProfileImage);
        imageLoader.setImageUrl(mSponsoredStory.getImage(), adCover);

        adTitle.setText(mSponsoredStory.getmResourceTitle());
        likeCount.setText(mSponsoredStory.getmLikeCount() + " " +
                context.getResources().getQuantityString(R.plurals.comment_page_like, mSponsoredStory.getmLikeCount()));

        setLikeButtonText(mSponsoredStory.getIsLike(), context, likeView);

        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap<String, String> likeParams = new HashMap<>();
                likeParams.put(ConstantVariables.SUBJECT_TYPE, mSponsoredStory.getmResourceType());
                likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mSponsoredStory.getmResourceId()));
                mAppConst.postJsonResponseForUrl(likeUnlikeUrl, likeParams, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

                        if(PreferencesUtils.isNestedCommentEnabled(context)){
                            String sendNotificationUrl  = AppConstant.DEFAULT_URL + "advancedcomments/send-like-notitfication";

                            /* Calling to send notifications after like action */
                            mAppConst.postJsonRequest(sendNotificationUrl, likeParams);
                        }

                        mSponsoredStory.setIsLike(mSponsoredStory.getIsLike() == 0 ? 1 : 0);
                        setLikeButtonText(mSponsoredStory.getIsLike(), context, likeView);
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    }
                });
            }
        });


        adView.findViewById(R.id.attachment_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToActivity(mSponsoredStory.getmResourceTitle(), mSponsoredStory.getmResourceType(),
                        mSponsoredStory.getmResourceId(), mSponsoredStory.getmContentUrl(), context);
            }
        });

        adTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToActivity(mSponsoredStory.getmResourceTitle(), mSponsoredStory.getmResourceType(),
                        mSponsoredStory.getmResourceId(), mSponsoredStory.getmContentUrl(), context);
            }
        });

        if(adView.findViewById(R.id.remove_ads_button) != null){
            adView.findViewById(R.id.remove_ads_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String removeAdsUrl = UrlUtil.REMOVE_COMMUNITY_ADS_URL + "?adsId=" +
                            mSponsoredStory.getmAdId() + "&placementCount=" +
                            mPlacementCount + "&type=" + mAdType;

                    AppConstant appConstant = new AppConstant(context);
                    appConstant.getJsonResponseFromUrl(removeAdsUrl, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) throws JSONException {

                            mSponsoredStory.setRemoveAdsOptions(jsonObject.optJSONArray("form"));
                            mRemoveAdsPosition.add(adPosition);

                            if(mRecyclerViewAdapter != null){
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                        }
                    });
                }
            });
        }
    }

    private static void setLikeButtonText(int isLike, Context context, TextView likeView){

        likeUnlikeUrl = AppConstant.DEFAULT_URL;

        if(isLike == 0){
            likeView.setTextColor(ContextCompat.getColor(context, R.color.gray_text_color));
            if(PreferencesUtils.isNestedCommentEnabled(context)){
                likeUnlikeUrl += "advancedcomments/like?sendNotification=0";
            } else{
                likeUnlikeUrl += "like";
            }
        } else {
            likeView.setTextColor(ContextCompat.getColor(context, R.color.themeButtonColor));
            likeUnlikeUrl += "unlike";
        }

    }

    /**
     * Method to check for parameter before transfer to respective activity.
     * @param title Title of the feed.
     * @param attachmentType Type of attachment.
     * @param attachmentId Id of attachment.
     */
    private static void redirectToActivity(String title, String attachmentType, int attachmentId,
                                           String contentUrl, Context context) {

        Intent viewIntent;
        int listingTypeId = 0;

        if (attachmentType.equals("user")) {
            viewIntent = new Intent(context, userProfile.class);
            viewIntent.putExtra(ConstantVariables.USER_ID, attachmentId);
            ((Activity) context).startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (mIncludedModulesList.contains(attachmentType) && !mDeletedModulesList.contains(attachmentType)) {

            viewIntent = GlobalFunctions.getIntentForModule(context, attachmentId, attachmentType, null);

            switch (attachmentType) {
                case ConstantVariables.FORUM_TITLE:
                    // Add Slug in Forum Urls
                    viewIntent.putExtra(ConstantVariables.CONTENT_TITLE, title);
                    break;

                case ConstantVariables.MLT_MENU_TITLE:
                    viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listingTypeId);
                    break;
            }

            if (viewIntent != null) {
                ((Activity) context).startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (ConstantVariables.WEBVIEW_ENABLE == 0) {
            CustomTabUtil.launchCustomTab((Activity) context, GlobalFunctions.getWebViewUrl(contentUrl, context));
        } else {
            Intent webViewActivity = new Intent(context, WebViewActivity.class);
            webViewActivity.putExtra("url", contentUrl);
            ((Activity) context).startActivityForResult(webViewActivity, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

}

