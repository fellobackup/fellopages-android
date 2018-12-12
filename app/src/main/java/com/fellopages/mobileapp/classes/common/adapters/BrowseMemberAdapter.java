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

package com.fellopages.mobileapp.classes.common.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.BrowseMemberFragment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BrowseMemberAdapter extends ArrayAdapter<BrowseListItems> implements OnMenuClickResponseListener {

    private Context mContext;
    private int mLayoutResID;
    private List<BrowseListItems> mBrowseItemlist;
    private AppConstant mAppConst;
    private View mRootView;
    private BrowseListItems listItems;
    private Typeface fontIcon;
    private int mItemPosition;
    private boolean mIsMemberFriends;
    private ImageLoader mImageLoader;
    public static int sFriendId;
    private BrowseMemberFragment mBrowseMemberFragment;
    private GutterMenuUtils mGutterMenuUtils;


    public BrowseMemberAdapter(Context context, int layoutResourceID, List<BrowseListItems> listItem,
                               boolean isMemberFriends) {

        super(context, layoutResourceID, listItem);

        mContext = context;
        mLayoutResID = layoutResourceID;
        mBrowseItemlist = listItem;
        mAppConst = new AppConstant(context);

        mIsMemberFriends = isMemberFriends;
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mImageLoader = new ImageLoader(mContext);
        mGutterMenuUtils = new GutterMenuUtils(context);
        mGutterMenuUtils.setOnMenuClickResponseListener(this);
    }

    public BrowseMemberAdapter(Context context, int layoutResourceID, List<BrowseListItems> listItem,
                               boolean isMemberFriends, BrowseMemberFragment browseMemberFragment) {

        super(context, layoutResourceID, listItem);

        mContext = context;
        mLayoutResID = layoutResourceID;
        mBrowseItemlist = listItem;
        mAppConst = new AppConstant(context);

        mIsMemberFriends = isMemberFriends;
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mImageLoader = new ImageLoader(mContext);
        mBrowseMemberFragment = browseMemberFragment;
        mGutterMenuUtils = new GutterMenuUtils(context);
        mGutterMenuUtils.setOnMenuClickResponseListener(this);
    }


    public View getView(final int position, final View convertView, ViewGroup parent) {

        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if (mRootView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);
            listItemHolder.mOwnerName = (TextView) mRootView.findViewById(R.id.ownerTitle);
            listItemHolder.mMemberOptions = (TextView) mRootView.findViewById(R.id.memberOption);
            listItemHolder.mMemberOptions.setTypeface(fontIcon);
            listItemHolder.mMemberOptions.setMovementMethod(LinkMovementMethod.getInstance());
            listItemHolder.mListImage = (ImageView) mRootView.findViewById(R.id.ownerImage);
            listItemHolder.mAge = (TextView) mRootView.findViewById(R.id.age);
            listItemHolder.mLocation = (TextView) mRootView.findViewById(R.id.location);
            listItemHolder.mMutualFriendCount = (TextView) mRootView.findViewById(R.id.mutualFriendCount);
            listItemHolder.mAgeIcon = (TextView) mRootView.findViewById(R.id.age_icon);
            listItemHolder.mLocationIcon = (TextView) mRootView.findViewById(R.id.location_icon);
            listItemHolder.mMutualFriendIcon = (TextView) mRootView.findViewById(R.id.mutualFriendIcon);
            listItemHolder.mMemberOnlineIcon = (ImageView) mRootView.findViewById(R.id.online_icon);
            listItemHolder.ivMessage = mRootView.findViewById(R.id.message_icon);
            listItemHolder.mAgeIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            listItemHolder.mLocationIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            listItemHolder.mMutualFriendIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            listItemHolder.processingRequest = mRootView.findViewById(R.id.processing_request_pbr);
            listItemHolder.menuOption = mRootView.findViewById(R.id.option_icon_layout);
            listItemHolder.menuOption.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
            mRootView.setTag(listItemHolder);
        } else {
            listItemHolder = (ListItemHolder) mRootView.getTag();
        }

        listItemHolder.mMemberOptions.setGravity(Gravity.CENTER_VERTICAL);
        listItemHolder.mMemberOptions.setTag(position);
        listItemHolder.menuOption.setTag(position);
        listItemHolder.ivMessage.setTag(position);

        listItems = this.mBrowseItemlist.get(position);

        listItemHolder.mUserId = listItems.getmUserId();

        /*
        Set Data in the List View Items
         */

        mImageLoader.setImageForUserProfile(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);

        listItemHolder.mListImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToUserProfilePage(listItemHolder.mUserId);
            }
        });

        listItemHolder.mOwnerName.setText(listItems.getmBrowseListOwnerTitle());
        listItemHolder.mOwnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToUserProfilePage(listItemHolder.mUserId);
            }
        });

        // Adding verification icon.
        if (listItems.getIsMemberVerified() == 1) {
            listItemHolder.mMemberOnlineIcon.setVisibility(View.VISIBLE);
        } else {
            listItemHolder.mMemberOnlineIcon.setVisibility(View.GONE);
        }

        listItemHolder.menuJsonArray  = listItems.getmMemberMenus();

        if(listItemHolder.menuJsonArray == null) {
            listItemHolder.mMemberOptions.setVisibility(View.GONE);
            listItemHolder.menuOption.setVisibility(View.GONE);
            listItemHolder.ivMessage.setVisibility(View.GONE);

        } else if (listItems.getMenuArray() != null && listItems.getMenuArray().length() > 0) {
            listItemHolder.mMemberOptions.setVisibility(View.GONE);
            listItemHolder.menuOption.setVisibility(View.VISIBLE);

            if (listItems.getmFriendShipType().equals("remove_friend")
                    || listItems.getmFriendShipType().equals("member_unfollow")) {
                listItemHolder.ivMessage.setVisibility(View.VISIBLE);
                listItemHolder.ivMessage.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                        PorterDuff.Mode.SRC_ATOP);
                GradientDrawable drawable = (GradientDrawable) listItemHolder.ivMessage.getBackground();
                drawable.mutate();
                drawable.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_1dp),
                        ContextCompat.getColor(mContext, R.color.dark_gray));

                listItemHolder.ivMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BrowseListItems browseListItems = mBrowseItemlist.get((int) view.getTag());
                        GlobalFunctions.messageOwner(mContext, ConstantVariables.USER_MENU_TITLE,
                                browseListItems);
                    }
                });
            } else {
                listItemHolder.ivMessage.setVisibility(View.GONE);
            }

            listItemHolder.menuOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BrowseListItems browseListItems = mBrowseItemlist.get((int) view.getTag());
                    List<Object> list = new ArrayList<>();
                    list.addAll(mBrowseItemlist);
                    mGutterMenuUtils.showPopup(view, browseListItems.getMenuArray(),
                            (int) view.getTag(), list, ConstantVariables.USER_MENU_TITLE);
                }
            });

        } else {
            listItemHolder.mMemberOptions.setVisibility(View.VISIBLE);
            listItemHolder.menuOption.setVisibility(View.GONE);
            listItemHolder.ivMessage.setVisibility(View.GONE);
            String menuName = listItemHolder.menuJsonArray.optString("name");
            if(menuName != null){
                switch (menuName){
                    case "add_friend":
                    case "accept_request":
                    case "member_follow":
                        listItemHolder.mMemberOptions.setText("\uf234");
                        break;
                    case "remove_friend":
                    case "member_unfollow":
                        listItemHolder.mMemberOptions.setText("\uf235");
                        break;
                    case "cancel_request":
                    case "cancel_follow":
                        listItemHolder.mMemberOptions.setText("\uf00d");
                        break;
                }
            }
            listItemHolder.mMemberOptions.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(mIsMemberFriends)
                        listItemHolder.mMemberOptions.setText("\uf110");
                    mItemPosition = (int) v.getTag();
                    performLinkAction(mItemPosition);
                }
            });
            if (listItems.isRequestProcessing()) {
                listItemHolder.processingRequest.setVisibility(View.VISIBLE);
                listItemHolder.mMemberOptions.setVisibility(View.GONE);
            } else {
                listItemHolder.processingRequest.setVisibility(View.GONE);
                listItemHolder.mMemberOptions.setVisibility(View.VISIBLE);
            }
        }

        // Checking condition for AdvancedMember and show data accordingly.
        if (listItems.getmIsSiteMember() == 1) {

            // Showing age of the member if exists.
            if (listItems.getmAge() != 0) {
                listItemHolder.mAgeIcon.setText("\uf1fd");
                listItemHolder.mAge.setText(mContext.getResources().
                        getQuantityString(R.plurals.age_text,
                                listItems.getmAge(),
                                listItems.getmAge()));
                listItemHolder.mAgeIcon.setVisibility(View.VISIBLE);
                listItemHolder.mAge.setVisibility(View.VISIBLE);
            } else {
                listItemHolder.mAge.setVisibility(View.GONE);
                listItemHolder.mAgeIcon.setVisibility(View.GONE);
            }

            //Showing location if exists
            if (listItems.getmLocation() != null && !listItems.getmLocation().isEmpty()) {
                listItemHolder.mLocationIcon.setText("\uf041");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    listItemHolder.mLocation.setText(Html.fromHtml(listItems.getmLocation(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    listItemHolder.mLocation.setText(Html.fromHtml(listItems.getmLocation()));
                }
                listItemHolder.mLocation.setVisibility(View.VISIBLE);
                listItemHolder.mLocationIcon.setVisibility(View.VISIBLE);
            } else {
                listItemHolder.mLocationIcon.setVisibility(View.GONE);
                listItemHolder.mLocation.setVisibility(View.GONE);
            }

            // Showing mutual friend count of the member if exists.
            if (listItems.getmMutualFriendCount() >= 1) {
                listItemHolder.mMutualFriendIcon.setText("\uf0c0");
                listItemHolder.mMutualFriendCount.setText(mContext.getResources().
                        getQuantityString(R.plurals.mutual_friend_text,
                                listItems.getmMutualFriendCount(),
                                listItems.getmMutualFriendCount()));
                listItemHolder.mMutualFriendIcon.setText("\uf0c0");
                listItemHolder.mMutualFriendCount.setVisibility(View.VISIBLE);
                listItemHolder.mMutualFriendIcon.setVisibility(View.VISIBLE);
            } else {
                listItemHolder.mMutualFriendCount.setVisibility(View.GONE);
                listItemHolder.mMutualFriendIcon.setVisibility(View.GONE);
            }

            //Showing online icon if the member is online.
            if (listItems.getmIsMemberOnline() == 1) {
                Drawable onlineDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_dot);
                onlineDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.light_green),
                        PorterDuff.Mode.SRC_ATOP);
                onlineDrawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp));
                listItemHolder.mOwnerName.setCompoundDrawables(null, null, onlineDrawable, null);

            } else {
                listItemHolder.mOwnerName.setCompoundDrawables(null, null, null, null);
            }

            //Increasing height and width of image as we are showing more info.
            listItemHolder.mListImage.getLayoutParams().height = mContext.getResources().
                    getDimensionPixelSize(R.dimen.host_image_width_height);
            listItemHolder.mListImage.getLayoutParams().width = mContext.getResources().
                    getDimensionPixelSize(R.dimen.host_image_width_height);

        } else {
            listItemHolder.mAge.setVisibility(View.GONE);
            listItemHolder.mLocation.setVisibility(View.GONE);
            listItemHolder.mMutualFriendCount.setVisibility(View.GONE);
            listItemHolder.mAgeIcon.setVisibility(View.GONE);
            listItemHolder.mLocationIcon.setVisibility(View.GONE);
            listItemHolder.mMutualFriendIcon.setVisibility(View.GONE);
        }

        return mRootView;
    }

    private void redirectToUserProfilePage(int mUserId) {
        Intent userProfileIntent = new Intent(mContext, userProfile.class);
        userProfileIntent.putExtra("user_id", mUserId);
        if (mBrowseMemberFragment != null) {
            mBrowseMemberFragment.startActivityForResult(userProfileIntent, ConstantVariables.USER_PROFILE_CODE);
        } else {
            ((Activity)mContext).startActivityForResult(userProfileIntent, ConstantVariables.USER_PROFILE_CODE);
        }
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onItemDelete(int position) {

    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {
        BrowseListItems listItems = (BrowseListItems) itemList;
        mBrowseItemlist.set(position, listItems);
        notifyDataSetChanged();
    }

    private static class ListItemHolder{
        ImageView mListImage, mMemberOnlineIcon, ivMessage;
        TextView mOwnerName, mMemberOptions, mAge, mLocation, mMutualFriendCount, mAgeIcon, mLocationIcon,
                mMutualFriendIcon;
        JSONObject menuJsonArray;
        ProgressBar processingRequest;
        View menuOption;
        int mUserId;
    }

    public void performLinkAction(int itemPosition){
        final JSONObject mPreviousMenuArray, menuArray;
        String dialogueMessage = null, dialogueTitle = null, buttonTitle = null, successMessage = null;
        String changedMenuName = null, changedMenuUrl = null;
        final Map<String, String> postParams = new HashMap<>();
        final BrowseListItems memberList = mBrowseItemlist.get(itemPosition);
        mPreviousMenuArray = menuArray = memberList.getmMemberMenus();
        final String menuName = menuArray.optString("name");

        final String url = AppConstant.DEFAULT_URL + menuArray.optString("url");
        JSONObject urlParamsJsonObject = menuArray.optJSONObject("urlParams");
        if(urlParamsJsonObject != null && urlParamsJsonObject.length() != 0){
            JSONArray urlParamsNames = urlParamsJsonObject.names();
            for(int i = 0; i < urlParamsJsonObject.length(); i++){

                String name = urlParamsNames.optString(i);
                String value = urlParamsJsonObject.optString(name);
                    postParams.put(name, value);
            }
        }

        switch (menuName){
            case "add_friend":
                dialogueMessage = mContext.getResources().getString(R.string.add_friend_message);
                dialogueTitle = mContext.getResources().getString(R.string.add_friend_title);
                buttonTitle = mContext.getResources().getString(R.string.add_friend_button);
                successMessage = mContext.getResources().getString(R.string.add_friend_success_message);
                if (memberList.getIsFriendshipVerified() == 1) {
                    changedMenuUrl = "user/cancel";
                    changedMenuName = "cancel_request";
                } else {
                    changedMenuUrl = "user/remove";
                    changedMenuName = "remove_friend";
                }
                break;
            case "member_follow":
                dialogueMessage = mContext.getResources().getString(R.string.add_friend_message);
                dialogueTitle = mContext.getResources().getString(R.string.add_friend_title);
                buttonTitle = mContext.getResources().getString(R.string.add_friend_button);
                successMessage = mContext.getResources().getString(R.string.add_friend_success_message);
                if (memberList.getIsFriendshipVerified() == 1) {
                    changedMenuUrl = "user/cancel";
                    changedMenuName = "cancel_follow";
                } else {
                    changedMenuUrl = "user/remove";
                    changedMenuName = "member_unfollow";
                }
                break;
            case "accept_request":
                dialogueMessage = mContext.getResources().getString(R.string.accept_friend_request_message);
                dialogueTitle = mContext.getResources().getString(R.string.accept_friend_request_title);
                buttonTitle = mContext.getResources().getString(R.string.accept_friend_request_button);
                successMessage = mContext.getResources().getString(R.string.accept_friend_request_success_message);
                changedMenuName = "remove_friend";
                changedMenuUrl = "user/remove";
                break;
            case "remove_friend":
            case "member_unfollow":
                dialogueMessage = mContext.getResources().getString(R.string.remove_friend_message);
                dialogueTitle = mContext.getResources().getString(R.string.remove_friend_title);
                buttonTitle = mContext.getResources().getString(R.string.remove_friend_button);
                successMessage = mContext.getResources().getString(R.string.remove_friend_success_message);
                changedMenuUrl = "user/add";
                if (menuName.equals("member_unfollow")) {
                    changedMenuName = "member_follow";
                } else {
                    changedMenuName = "add_friend";
                }
                break;
            case "cancel_request":
            case "cancel_follow":
                dialogueMessage = mContext.getResources().getString(R.string.cancel_friend_request_message);
                dialogueTitle = mContext.getResources().getString(R.string.cancel_friend_request_title);
                buttonTitle = mContext.getResources().getString(R.string.cancel_friend_request_button);
                successMessage = mContext.getResources().getString(R.string.cancel_friend_request_success_message);
                changedMenuUrl = "user/add";
                if (menuName.equals("cancel_follow")) {
                    changedMenuName = "member_follow";
                } else {
                    changedMenuName = "add_friend";
                }
                break;
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(dialogueMessage);
        alertBuilder.setTitle(dialogueTitle);

        final String finalSuccessMessage = successMessage;
        final String finalChangedMenuName = changedMenuName;
        final String finalChangedMenuUrl = changedMenuUrl;
        processRequest(menuArray, finalChangedMenuName, finalChangedMenuUrl, memberList, url, postParams, mPreviousMenuArray);
        //TODO If want to show confirmation alert dialog
//        alertBuilder.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                processRequest(menuArray, finalChangedMenuName, finalChangedMenuUrl, memberList, url, postParams, mPreviousMenuArray);
//            }});
//        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel),
//                new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                try {
//                    menuArray.put("name", menuName);
//                    memberList.setmMemberMenus(menuArray);
//                    memberList.setRequestProcessing(false);
//                    notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                dialog.cancel();
//            }
//        });
//        alertBuilder.create().show();

    }

    private void processRequest(JSONObject menuArray, String finalChangedMenuName, String finalChangedMenuUrl, final BrowseListItems memberList, String url, Map<String, String> postParams, final JSONObject mPreviousMenuArray) {
        try {
            menuArray.put("name", finalChangedMenuName);
            menuArray.put("url", finalChangedMenuUrl);
            memberList.setmMemberMenus(menuArray);
            memberList.setCanAddToList(0);
            memberList.setRequestProcessing(true);
            notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAppConst.postJsonResponseForUrl(url, postParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                memberList.setRequestProcessing(false);
                notifyDataSetChanged();
                //TO DO If want to show confirmation message
                //View view = ((Activity)mContext).getCurrentFocus();
                //SnackbarUtils.displaySnackbar(view, finalSuccessMessage);

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                View view = ((Activity)mContext).getCurrentFocus();
                SnackbarUtils.displaySnackbar(view, message);
                    memberList.setmMemberMenus(mPreviousMenuArray);
                    memberList.setCanAddToList(0);
                    memberList.setRequestProcessing(false);
                    notifyDataSetChanged();
            }
        });
    }
}
