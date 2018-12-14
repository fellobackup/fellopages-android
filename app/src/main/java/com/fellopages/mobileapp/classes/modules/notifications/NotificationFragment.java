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

package com.fellopages.mobileapp.classes.modules.notifications;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.FeedList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.SingleFeedPage;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoUtil;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.pushnotification.MyFcmListenerService;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * A simple {@link Fragment} subclass.
 *
 * The notifications tab item.
 */
public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mNotificationViewAdapter;
    private AppConstant mAppConst;
    private List<Object> mBrowseItemList;
    private List mDeletedModulesList;
    private BrowseListItems mBrowseList;
    private HashMap<String, String> postParams;
    int pageNumber = 1, mNotificationId, mCurrentUserId, mObjectId, mSubjectId, isRead;
    private String mNotificationRequestUrl;
    private boolean isVisibleToUser = false;
    String mSubjectType, mObjectType, mFeedTitle, mNotificationUrl;
    private String mViewForumTopicPageTitle, mViewForumTopicPageSlug;
    JSONObject mBody,mSubjectResponse, mObjectResponse, mNotificationObject;
    JSONArray mRecentUpdatedItemArray, mActionBodyParamsArray;
    String mNotificationType, mActionTypeBody;
    int mTotalUpdatedItemCount;
    private Snackbar snackbar;
    private AlertDialogWithAction mAlertDialogWithAction;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(Bundle bundle) {
        // Required  public constructor
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        postParams = new HashMap<>();
        mAppConst = new AppConstant(mContext);
        mDeletedModulesList = Arrays.asList(ConstantVariables.DELETED_MODULES);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mNotificationRequestUrl = UrlUtil.MAIN_NOTIFICATION_URL + "&page=" + pageNumber;

        mNotificationViewAdapter = new NotificationViewAdapter(getActivity(), mBrowseItemList,false,
                new NotificationViewAdapter.OnItemClickListener() {
                    BrowseListItems listItems;
                    int id;

                    @Override
                    public void onItemClick(View view, int position) {

                        listItems = (BrowseListItems) mBrowseItemList.get(position);

                        if(listItems.getIsRead() == 0){
                            view.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selectable_background_white));
                            String notificationReadUrl = UrlUtil.NOTIFICATION_READ_URL;
                            postParams.put("action_id", String.valueOf(listItems.getNotificationId()));

                            mAppConst.postJsonResponseForUrl(notificationReadUrl, postParams,
                                    new OnResponseListener() {
                                        @Override
                                        public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                            listItems.setIsRead(1);
                                            mNotificationViewAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                            SnackbarUtils.displaySnackbar(rootView, message);
                                        }
                                    });
                        }

                        JSONObject jsonObject;
                        String type;

                        switch (listItems.getNotificationType()) {
                            case "friend_accepted":
                            case "siteverify_new":
                                jsonObject = listItems.getSubjectResponse();
                                type = listItems.getNotificationSubjectType();
                                break;

                            case "admin_post_android":
                                jsonObject = listItems.getObjectResponse();
                                type = listItems.getNotificationType();
                                break;

                            default:
                                jsonObject = listItems.getObjectResponse();
                                type = listItems.getNotificationObjectType();
                                break;
                        }

                        id = GlobalFunctions.getIdOfModule(jsonObject, type);

                        if (listItems.getNotificationObjectType().equals("forum_topic")) {
                            mViewForumTopicPageTitle = jsonObject.optString("title");
                            mViewForumTopicPageSlug = jsonObject.optString("slug");
                        }

                        startNewActivity(type, id, listItems, jsonObject);
                    }

                    @Override
                    public void onProfilePictureClicked(View view, int position) {
                        listItems = (BrowseListItems) mBrowseItemList.get(position);
                        id = GlobalFunctions.getIdOfModule(listItems.getSubjectResponse(), listItems.getNotificationSubjectType());
                        startNewActivity(listItems.getNotificationSubjectType(), id, listItems, null);
                    }

                    @Override
                    public void onOptionSelected(View v, BrowseListItems listItems, int position) { }
                });

        mRecyclerView.setAdapter(mNotificationViewAdapter);
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible && !isVisibleToUser) {
            makeRequest();
        } else {
            if(snackbar != null && snackbar.isShown())
                snackbar.dismiss();
        }
    }

    public void startNewActivity(String type, int id, BrowseListItems customList, JSONObject jsonObject){
        Intent viewIntent;

        switch (type) {
            case "user":
                viewIntent = new Intent(mContext, userProfile.class);
                viewIntent.putExtra("user_id", id);
                getActivity().startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case ConstantVariables.VIDEO_TITLE:
            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_VIDEO_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_VIDEO_MENU_TITLE:
            case ConstantVariables.SITE_STORE_VIDEO_MENU_TITLE:
            case ConstantVariables.PRODUCT_VIDEO_MENU_TITLE:
                List<String> enabledModuleList = null;

                if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
                    enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
                }

                if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                        && !mDeletedModulesList.contains("core_main_sitevideo") && type.equals("video")) {
                    viewIntent = AdvVideoUtil.getViewPageIntent(mContext, id, jsonObject.optString("video_url"),
                            new Bundle());
                } else {
                    viewIntent = GlobalFunctions.getIntentForModule(mContext, id, type, mViewForumTopicPageSlug);

                    viewIntent.putExtra(ConstantVariables.VIDEO_TYPE, customList.getObjectResponse().optInt("type"));
                    viewIntent.putExtra(ConstantVariables.VIDEO_URL, customList.getObjectResponse().optString("video_url"));

                    if (!type.equals("video")) {
                        viewIntent = GlobalFunctions.setIntentParamForVideo(type, jsonObject, id, viewIntent);
                    }
                }
                if (viewIntent != null) {
                    startActivity(viewIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case "activity_action":
            case "activity_comment":
                int action_id;

                if (type.equals("activity_comment") && customList.getObjectResponse() != null && customList.getObjectResponse().length() > 0) {
                    action_id = customList.getObjectResponse().optInt("resource_id");
                } else {
                    action_id = customList.getNotificationObjectId();
                }

                if (type.equals("activity_action") && jsonObject.optInt("attachment_count") > 1) {
                    String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/likes-comments" ;
                    viewIntent = new Intent(mContext, Comment.class);

                    viewIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
                    viewIntent.putExtra(ConstantVariables.SUBJECT_TYPE, ConstantVariables.AAF_MENU_TITLE);
                    viewIntent.putExtra(ConstantVariables.SUBJECT_ID, action_id);
                    viewIntent.putExtra("reactionsEnabled", jsonObject.optInt("reactionsEnabled"));

                    if(jsonObject.optJSONObject("feed_reactions") != null){
                        viewIntent.putExtra("popularReactions", jsonObject.optJSONObject("feed_reactions").toString());
                    }

                } else {
                    viewIntent = new Intent(mContext, SingleFeedPage.class);
                }

                viewIntent.putExtra(ConstantVariables.ACTION_ID, action_id);
                startActivity(viewIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "core_comment":
                viewIntent = new Intent(mContext, Comment.class);
                viewIntent.putExtra(ConstantVariables.SUBJECT_TYPE, jsonObject.optString("resource_type"));
                viewIntent.putExtra(ConstantVariables.SUBJECT_ID, jsonObject.optInt("resource_id"));
                startActivity(viewIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "forum_topic":
                viewIntent = GlobalFunctions.getIntentForModule(mContext, id, type, mViewForumTopicPageSlug);
                viewIntent.putExtra(ConstantVariables.CONTENT_TITLE,mViewForumTopicPageTitle);
                startActivity(viewIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "sitepage_album":
                viewIntent = GlobalFunctions.getIntentForSubModule(mContext, id, jsonObject.optInt("album_id"), type);
                startActivity(viewIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "admin_post_android":
                JSONObject paramObject = jsonObject.optJSONObject("params");
                if (paramObject != null && paramObject.length() > 0) {
                    mAlertDialogWithAction.showPushNotificationAlertDialog(paramObject.optString("title"),
                            paramObject.optString("message"));
                }
                break;

            case ConstantVariables.ALBUM_PHOTO_MENU_TITLE:
                ArrayList<PhotoListDetails> mPhotoDetails = new ArrayList<>();

                if (jsonObject != null) {
                    int photoId = jsonObject.optInt("photo_id");
                    int albumId = jsonObject.optInt("album_id");
                    String image = jsonObject.optString("image_profile");
                    int likesCount = jsonObject.optInt("like_count");
                    int commentCount = jsonObject.optInt("comment_count");
                    int is_like = jsonObject.optInt("is_like");
                    String reactions = jsonObject.optString("reactions");
                    String mUserTagArray = jsonObject.optString("tags");
                    final String menuArray = jsonObject.optString("menu");

                    boolean likeStatus = is_like != 0;

                    String albumViewUrl = UrlUtil.ALBUM_VIEW_PAGE + albumId + "?gutter_menu=1";

                    mPhotoDetails.add(new PhotoListDetails(photoId, image, likesCount, commentCount, mUserTagArray, likeStatus, reactions));
                    openPhotoLightBox(mPhotoDetails, albumViewUrl, albumId);
                }

                break;

            default:
                viewIntent = GlobalFunctions.getIntentForModule(mContext, id, type, null);

                if (viewIntent != null && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains(type)) {

                    if (type.equals("sitereview_listing") || type.equals("sitereview_review")) {
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, jsonObject.optInt("listingtype_id"));
                    } else if (type.equals("sitereview_wishlist")) {
                        viewIntent.putExtra(ConstantVariables.CONTENT_TITLE, jsonObject.optString("title"));
                    }

                    startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if(customList.getmNotificationUrl() != null && !customList.getmNotificationUrl().isEmpty()) {
                    if(ConstantVariables.WEBVIEW_ENABLE == 1) {
                        Intent webViewActivity = new Intent(mContext, WebViewActivity.class);
                        webViewActivity.putExtra("headerText", customList.getNotificationObject().optString("title"));
                        webViewActivity.putExtra("url", customList.getmNotificationUrl());
                        startActivity(webViewActivity);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }else {
                        CustomTabUtil.launchCustomTab((Activity) mContext, GlobalFunctions.getWebViewUrl(customList.getmNotificationUrl(), mContext));
                    }
                }
        }
    }

    public void makeRequest() {
        mAppConst.getJsonResponseFromUrl(mNotificationRequestUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addNotificationToTheList(jsonObject);
                isVisibleToUser = true;
                mNotificationViewAdapter.notifyDataSetChanged();
                MyFcmListenerService.clearPushNotification();
                ShortcutBadger.removeCount(mContext);

                if(mBrowseItemList.size() == 0){
                    rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                    TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                    SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById(R.id.error_message);
                    errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    errorIcon.setText("\uf0f3");
                    errorMessage.setText(mContext.getResources().getString(R.string.no_notifications));
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }
            }
        });
    }

    public void addNotificationToTheList(JSONObject jsonObject){
        mBody = jsonObject;

        mTotalUpdatedItemCount = mBody.optInt("recentUpdateTotalItemCount");
        mBrowseList.setmTotalItemCount(mTotalUpdatedItemCount);

        if(mTotalUpdatedItemCount != 0){
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            mRecentUpdatedItemArray = mBody.optJSONArray("recentUpdates");

            for(int i = 0;i<mRecentUpdatedItemArray.length();i++){
                mNotificationObject = mRecentUpdatedItemArray.optJSONObject(i);
                mNotificationId = mNotificationObject.optInt("notification_id");
                mCurrentUserId = mNotificationObject.optInt("user_id");
                mSubjectId = mNotificationObject.optInt(ConstantVariables.SUBJECT_ID);
                isRead = mNotificationObject.optInt("read");
                mObjectId = mNotificationObject.optInt("object_id");
                mSubjectType = mNotificationObject.optString(ConstantVariables.SUBJECT_TYPE);
                mObjectType = mNotificationObject.optString("object_type");
                mActionTypeBody = mNotificationObject.optString("action_type_body");
                mFeedTitle = mNotificationObject.optString("feed_title");
                mNotificationType = mNotificationObject.optString("type");
                mNotificationUrl = mNotificationObject.optString("url");
                mSubjectResponse = mNotificationObject.optJSONObject("subject");
                mObjectResponse = mNotificationObject.optJSONObject("object");
                mActionBodyParamsArray = mNotificationObject.optJSONArray("action_type_body_params");

                mBrowseItemList.add(new BrowseListItems(mNotificationId,
                                                        mCurrentUserId,
                                                        mSubjectId,
                                                        mObjectId,
                                                        isRead,
                                                        mSubjectType,
                                                        mObjectType,
                                                        mNotificationObject,
                                                        mActionTypeBody,
                                                        mFeedTitle,
                                                        mNotificationType,
                                                        mNotificationUrl,
                                                        mSubjectResponse,
                                                        mObjectResponse,
                                                        mActionBodyParamsArray));
            }

            mBrowseItemList.add(ConstantVariables.FOOTER_TYPE);
            mNotificationViewAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            TextView errorMessage = (TextView) rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf0f3");
            errorMessage.setText(mContext.getResources().getString(R.string.no_notifications));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppConst.hideKeyboard();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mRecyclerView != null){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onRefresh() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                makeRequest();
            }
        });
    }

    private void openPhotoLightBox(ArrayList<PhotoListDetails> mFeedPhotoDetails, String albumUrl, int albumId){
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mFeedPhotoDetails);
        Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.SUBJECT_TYPE, "album_photo");
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
        i.putExtra(ConstantVariables.SHOW_ALBUM_BUTTON,true);
        i.putExtra(ConstantVariables.PHOTO_REQUEST_URL, albumUrl);
        i.putExtra(ConstantVariables.ALBUM_ID, albumId);
        i.putExtras(bundle);
        startActivity(i);
    }
}