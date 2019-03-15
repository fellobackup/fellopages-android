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

package com.fellopages.mobileapp.classes.common.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.Invite;
import com.fellopages.mobileapp.classes.common.activities.InviteGuest;
import com.fellopages.mobileapp.classes.common.activities.ReportEntry;
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.adapters.BrowseMemberAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BadgeView;
import com.fellopages.mobileapp.classes.common.interfaces.OnPopUpDismissListener;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsAvailableTickets;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsProfilePage;
import com.fellopages.mobileapp.classes.modules.advancedGroups.AdvGroupsProfile;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;
import com.fellopages.mobileapp.classes.modules.classified.ClassifiedUtil;
import com.fellopages.mobileapp.classes.modules.directoryPages.SitePageProfilePage;
import com.fellopages.mobileapp.classes.modules.likeNComment.CommentList;
import com.fellopages.mobileapp.classes.modules.messages.CreateNewMessage;
import com.fellopages.mobileapp.classes.modules.multipleListingType.MLTUtil;
import com.fellopages.mobileapp.classes.modules.packages.PackageView;
import com.fellopages.mobileapp.classes.modules.user.MemberDetailsDialog;
import com.fellopages.mobileapp.classes.modules.packages.SelectPackage;
import com.fellopages.mobileapp.classes.modules.store.CartView;
import com.fellopages.mobileapp.classes.modules.store.ShippingMethods;
import com.fellopages.mobileapp.classes.modules.store.StoreViewPage;
import com.fellopages.mobileapp.classes.modules.user.profile.EditProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GutterMenuUtils {

    // Member variables
    private View mMainView;
    private Context mContext;
    private AppConstant mAppConst;
    private SocialShareUtil mSocialShareUtil;
    private String mRedirectUrl, mChangedFriendShipType, mFriendShipUrl, mMenuName,
            mDialogueMessage, mDialogueTitle, mDialogueButton, mSuccessMessage,
            mMenuLabel, mCurrentSelectedModule;
    private int mSelectedRsvpValue = 0, mPosition, mListingTypeId, mEventId;
    private boolean isNeedToDismiss = true, isActionPerformed = false;
    private Map<String, String> mPostParams;
    private JSONObject mMenuJsonObject, mUrlParams;
    private OnPopUpDismissListener mOnPopUpDismissListener;
    private OnMenuClickResponseListener mOnMenuClickResponseListener;
    private OnOptionItemClickResponseListener mOnOptionItemClickResponseListener;
    private BrowseListItems mBrowseListItems = new BrowseListItems();
    // private FeedList mFeedList;
    public FeedList mFeedList;
    private CommentList mCommentList;
    private Fragment mCallingFragment;
    private AlertDialogWithAction mAlertDialogWithAction;
    private EditText etValue;


    /**
     * Public constructor of GutterMenuUtils.
     *
     * @param context context of calling class.
     */
    public GutterMenuUtils(Context context) {
        mContext = context;
        mPostParams = new HashMap<>();
        mAppConst = new AppConstant(mContext);
        mSocialShareUtil = new SocialShareUtil(mContext);
    }

    public void setEventId(int mEventId){
        this.mEventId = mEventId;
        Log.d("ThisWasTriggered", String.valueOf(mEventId));
    }

    /**
     * Method to set OnPopUpDismissListener on popup click.
     * @param onPopUpDismissListener OnPopUpDismissListener context from calling class.
     */
    public void setOnPopUpDismissListener (OnPopUpDismissListener onPopUpDismissListener) {
        this.mOnPopUpDismissListener = onPopUpDismissListener;
    }

    /**
     * Public constructor of GutterMenuUtils.
     *
     * @param context             context of calling class.
     * @param isAlertDialogNeeded True if alert dialog is needed.
     */
    public GutterMenuUtils(Context context, boolean isAlertDialogNeeded) {
        mContext = context;
        mPostParams = new HashMap<>();
        mAppConst = new AppConstant(mContext);
        mSocialShareUtil = new SocialShareUtil(mContext);
        if (isAlertDialogNeeded) {
            mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        }
    }


    /**
     * Method to set onMenuClickResponseListener on popup click.
     *
     * @param onMenuClickResponseListener onMenuClickResponseListener context from calling class.
     */
    public void setOnMenuClickResponseListener(OnMenuClickResponseListener onMenuClickResponseListener) {
        this.mOnMenuClickResponseListener = onMenuClickResponseListener;
    }


    /**
     * Method to set onOptionItemClickResponseListener on popup click.
     *
     * @param onOptionItemClickResponseListener onOptionItemClickResponseListener context from calling class.
     */
    public void setOnOptionItemClickResponseListener(OnOptionItemClickResponseListener onOptionItemClickResponseListener) {
        this.mOnOptionItemClickResponseListener = onOptionItemClickResponseListener;
    }


    /**
     * Method to show pop up on optionIcon click
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param browseItemList        List of item.
     * @param currentSelectedModule current Selected module.
     */
    public void showPopup(final View view, final JSONArray menuArray,
                          BrowseListItems browseItemList, final String currentSelectedModule) {

        this.mBrowseListItems = browseItemList;
        // call to main method which contains all functionality.
        showPopup(view, menuArray, 0, null, currentSelectedModule, null, null, 0, true);

    }

    /**
     * Method to show pop up on optionIcon click
     * @param view View on which pop is shown.
     * @param position Position of clicked item.
     * @param menuArray gutterMenu array which contains all the option data.
     * @param browseItemList List of item.
     * @param currentSelectedModule current Selected module.
     */
    public void showPopup(final View view, int position, final JSONArray menuArray,
                          BrowseListItems browseItemList, final String currentSelectedModule) {

        this.mMainView = view;
        this.mBrowseListItems = browseItemList;
        // call to main method which contains all functionality.
        showPopup(view, menuArray, position, null, currentSelectedModule, null, null, 0, true);

    }


    /**
     * Method to show pop up on optionIcon click
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param browseItemList        List of item.
     * @param currentSelectedModule current Selected module.
     */
    public void showPopup(View mainView, final View view, final JSONArray menuArray,
                          BrowseListItems browseItemList, final String currentSelectedModule,
                          String currentList, int listingTypeId) {

        this.mBrowseListItems = browseItemList;
        this.mMainView = mainView;
        // call to main method which contains all functionality.
        showPopup(view, menuArray, 0, null, currentSelectedModule, null, currentList, listingTypeId, true);

    }


    /**
     * Method to show pop up on optionIcon click
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param position              position of current selected item.
     * @param mBrowseItemList       List of item.
     * @param currentSelectedModule current Selected module.
     */
    public void showPopup(final View view, final JSONArray menuArray, final int position,
                          List<Object> mBrowseItemList, final String currentSelectedModule) {

        // call to main method which contains all functionality.
        showPopup(view, menuArray, position, mBrowseItemList, currentSelectedModule, null, null, 0, true);

    }


    /**
     * Method to show pop up on optionIcon click
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param position              position of current selected item.
     * @param mBrowseItemList       List of item.
     * @param currentSelectedModule current Selected module.
     * @param callingFragment       Fragment of calling class.
     * @param currentList           CurrentList in case of Adv. Event.
     */
    public void showPopup(final View view, final JSONArray menuArray, final int position,
                          List<Object> mBrowseItemList, final String currentSelectedModule,
                          Fragment callingFragment, String currentList) {

        // call to main method which contains all functionality.
        showPopup(view, menuArray, position, mBrowseItemList, currentSelectedModule,
                callingFragment, currentList, 0, false);

    }


    /**
     * Method to show pop up on optionIcon click
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param position              position of current selected item.
     * @param mBrowseItemList       List of item.
     * @param currentSelectedModule current Selected module.
     * @param callingFragment       Fragment of calling class.
     */
    public void showPopup(final View view, final JSONArray menuArray, final int position,
                          List<Object> mBrowseItemList, final String currentSelectedModule,
                          final Fragment callingFragment) {

        // call to main method which contains all functionality.
        showPopup(view, menuArray, position, mBrowseItemList, currentSelectedModule,
                callingFragment, null, 0, false);
    }

    /**
     * Method to show pop up on optionIcon click
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param position              position of current selected item.
     * @param mBrowseItemList       List of item.
     * @param currentSelectedModule current Selected module.
     * @param listingTypeId         ListingTypeId of current listing type.
     */
    public void showPopup(final View view, final JSONArray menuArray, final int position,
                          List<Object> mBrowseItemList, final String currentSelectedModule,
                          int listingTypeId, String currentList) {

        // call to main method which contains all functionality.
        showPopup(view, menuArray, position, mBrowseItemList, currentSelectedModule,
                null, currentList, listingTypeId, false);
    }


    /**
     * Main Method to show pop up on optionIcon click.
     *
     * @param view                  View on which pop is shown.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param position              position of current selected item.
     * @param mBrowseItemList       List of item.
     * @param currentSelectedModule current Selected module.
     * @param callingFragment       Fragment of calling class.
     * @param currentList           CurrentList in case of Adv. Event.
     * @param listingTypeId         ListingTypeId of current listing type.
     * @param isBrowsePage          true if called from browse page.
     */
    public void showPopup(final View view, final JSONArray menuArray, final int position,
                          List<Object> mBrowseItemList, final String currentSelectedModule,
                          final Fragment callingFragment, final String currentList,
                          final int listingTypeId, final boolean isBrowsePage) {

        mPosition = position;
        mListingTypeId = listingTypeId;
        mCurrentSelectedModule = currentSelectedModule;
        mCallingFragment = callingFragment;

        PopupMenu popup = new PopupMenu(mContext, view);

        if (mBrowseItemList != null) {
            if (mBrowseItemList.get(mPosition) instanceof BrowseListItems) {
                mBrowseListItems = (BrowseListItems) mBrowseItemList.get(mPosition);
            } else {
                mFeedList = (FeedList) mBrowseItemList.get(mPosition);
            }
        }

        if (menuArray.length() != 0) {

            for (int i = 0; i < menuArray.length(); i++) {

                try {
                    mMenuJsonObject = menuArray.optJSONObject(i);
                    mMenuName = mMenuJsonObject.optString("name");

                    //Set items in the Popup menu
                    switch (mMenuName) {

                        case "update_save_feed":
                        case "disable_comment":
                        case "lock_this_feed":
                        case "on_off_notification":
                        case "like":
                        case "follow":
                        case "close_poll":
                        case "open_poll":
                        case "close":
                        case "open":
                        case "request_invite":
                        case "cancel_invite":
                        case "friendship_type":
                        case "make_admin":
                        case "remove_admin":
                        case "make_officer":
                        case "demote_officer":
                        case "join":
                        case "leave":
                        case "book_now":
                        case "enable_method":
                        case "disable_method":
                        case "enable_file":
                        case "follow_unfollow_user":
                            mMenuLabel = getMenuLabel(false, mMenuName);
                            popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuLabel);
                            break;
                        case "create":
                            if (!mAppConst.isLoggedOutUser()) {
                                popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.
                                        optString("label").trim());
                            }
                            break;
                        case "publish":
                            if (mBrowseListItems.getmPublished() == 0) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.publish_group);
                                popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuLabel);
                            }
                            break;

                        case "share":
                            break;

                        case "payment_method":
                            mMenuLabel = getMenuLabel(false, mMenuName);
                            popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuLabel);
                            break;

                        case "create_ticket":
                            mMenuLabel = getMenuLabel(false, mMenuName);
                            popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuLabel);
                            break;

                        case "add_to_list":
                            if (mBrowseListItems.getmFriendShipType().equals("remove_friend")
                                    || mBrowseListItems.getmFriendShipType().equals("member_unfollow")) {
                                popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.
                                        optString("label").trim());
                            }
                            break;

                        default:
                            popup.getMenu().add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.
                                    optString("label").trim());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                onMenuItemSelected(view, id, menuArray, isBrowsePage, false, currentList);
                return true;
            }
        });
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                if (mOnPopUpDismissListener != null && isNeedToDismiss) {
                    mOnPopUpDismissListener.onPopUpDismiss(true);
                }
            }
        });
        popup.show();

    }

    /***
     * Method to show OptionMenu items when click on option Icon from Tool bar.
     *
     * @param menu                  menu which contains all the items.
     * @param gutterMenus           gutterMenu array which contains all the option data.
     * @param currentSelectedModule current Selected module.
     * @param browseListItems       List of item.
     */
    public void showOptionMenus(Menu menu, JSONArray gutterMenus, String currentSelectedModule,
                                BrowseListItems browseListItems) {

        this.mCurrentSelectedModule = currentSelectedModule;
        this.mBrowseListItems = browseListItems;

        if (gutterMenus != null) {
            for (int i = 0; i < gutterMenus.length(); i++) {
                try {
                    mMenuJsonObject = gutterMenus.getJSONObject(i);
                    mMenuName = mMenuJsonObject.optString("name");

                    switch (mMenuName) {
                        case "share":
                            Drawable shareIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_share_white);
                            shareIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
                            menu.add(Menu.NONE, i, Menu.FIRST, mMenuJsonObject.getString("label").trim()).
                                    setIcon(shareIcon).
                                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                            break;

                        case "create":
                            if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
                                menu.add(Menu.NONE, i, Menu.FIRST, mMenuJsonObject.getString("label").trim()).
                                        setIcon(R.drawable.ic_action_new).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                            } else {
                                menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            }
                            break;

                        case "post_reply":
                            if (mBrowseListItems.getmClosed() == 1) {
                                menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            }
                            break;
                        case "cart":
                            Drawable cartIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart);
                            cartIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);

                            menu.add(Menu.NONE, i, Menu.FIRST, mMenuJsonObject.getString("label").trim())
                                    .setIcon(cartIcon)
                                    .setActionView(R.layout.cart_menu)
                                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                            BadgeView mCartCountBadge = (BadgeView) menu.getItem(i)
                                    .getActionView().findViewById(R.id.cart_item_count);
                            menu.getItem(i)
                                    .getActionView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, CartView.class);
                                    ((Activity)mContext).startActivityForResult(intent,ConstantVariables.VIEW_PAGE_CODE);
                                }
                            });
                            if(mCartCountBadge != null && !PreferencesUtils.getNotificationsCounts(mContext,PreferencesUtils.CART_COUNT).equals("0") &&
                                    !PreferencesUtils.getNotificationsCounts(mContext,PreferencesUtils.CART_COUNT).equals("")
                                    && !PreferencesUtils.getNotificationsCounts(mContext,PreferencesUtils.CART_COUNT).equals("null")){
                                mCartCountBadge.setVisibility(View.VISIBLE);
                                mCartCountBadge.setText(PreferencesUtils.getNotificationsCounts(mContext,PreferencesUtils.CART_COUNT));
                            }
                            break;
                        case "watch_topic":
                        case "stop_watch_topic":
                        case "make_sticky":
                        case "remove_sticky":
                        case "close":
                        case "open":
                        case "unsubscribe":
                        case "subscribe":
                        case "follow":
                        case "unfollow":
                        case "following":
                        case "request_invite":
                        case "cancel_invite":
                        case "cancel":
                        case "request_member":
                        case "cancel_request":
                        case "join":
                        case "leave":
                        case "favourite":
                            mMenuLabel = getMenuLabel(true, mMenuName);
                            menu.add(Menu.NONE, i, Menu.NONE, mMenuLabel);
                            break;

                        case "invite":
                            if (!mCurrentSelectedModule.equals(ConstantVariables.ADV_GROUPS_MENU_TITLE)
                                    || mBrowseListItems.ismShowAddPeople()) {
                                menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            }

                            break;

                        case "publish":
                            if (!mBrowseListItems.ismGroupPublished()) {
                                menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            }
                            break;

                        case "ticket_details":
                            menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            break;

                        case "ticket_edit":
                            menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            break;

                        case "ticket_delete":
                            menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            break;

                        case "book_now":
                            String label = mMenuJsonObject.getString("label").trim();
                            // TODO: This will override the string Book Now to Register. Proper approach for this is to change the API response.
                            // TODO: Remove the entire case block if the text in API response is already changed.
                            if(label.toLowerCase().equals("book now")){
                                label = "Register";
                            }

                            menu.add(Menu.NONE, i, Menu.NONE, label);
                            break;

                        default:
                            menu.add(Menu.NONE, i, Menu.NONE, mMenuJsonObject.getString("label").trim());
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Method to get Menu Label from Menu Name.
     *
     * @param isRequestFromViewPage true if called from view page request.
     * @param menuName              menu name for which label is decided.
     * @return returns the menu label.
     */
    public String getMenuLabel(boolean isRequestFromViewPage, String menuName) {

        switch (menuName) {
            case "update_save_feed":
                if (mFeedList.getmIsSaveFeedOption() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.saved_feed);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.unsaved_feed);
                }
                break;

            case "disable_comment":
                if (mFeedList.ismCanComment() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.disable_comments);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.enable_comments);
                }
                break;

            case "lock_this_feed":
                if (mFeedList.getmShareAble() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.lock_feed);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.unlock_feed);
                }
                break;

            case "on_off_notification":
                if (mFeedList.isNotificationOn()) {
                    mMenuLabel = mContext.getResources().getString(R.string.notification_off);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.notification_on);
                }
                break;

            case "like":
                if (mBrowseListItems.getmLiked() == 0) {
                    mMenuLabel = mContext.getResources().getString(R.string.like_text);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.unlike);
                }
                break;

            case "follow":
            case "unfollow":
                if (!isRequestFromViewPage) {
                    if (mBrowseListItems.getmFollowed() == 0) {
                        mMenuLabel = mContext.getResources().getString(R.string.follow);
                    } else {
                        mMenuLabel = mContext.getResources().getString(R.string.unfollow);
                    }

                } else {
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.SITE_PAGE_MENU_TITLE:
                            if (SitePageProfilePage.sIsPageFollowed == 0) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.follow_page_button);
                            } else {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.unfollow_page_button);
                            }
                            break;

                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            if (AdvGroupsProfile.sIsGroupFollowed == 0) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.follow);
                            } else {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.unfollow);
                            }
                            break;
                    }
                }
                break;

            case "close_poll":
            case "open_poll":
                if (mBrowseListItems.getmClosed() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.open_poll_dialogue_title);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.close_poll_dialogue_title);
                }
                break;

            case "close":
            case "open":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.MLT_MENU_TITLE:
                    case ConstantVariables.CLASSIFIED_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 0) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.close_listing_dialogue_title);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.open_listing_dialogue_title);
                        }
                        break;

                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 0) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.close_group_dialogue_title);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.open_group_dialogue_title);
                        }
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 0) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.cancel_event_request);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.re_publish_event_request);
                        }
                        break;

                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 0) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.close_page_label);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.open_page_label);
                        }
                        break;

                    case ConstantVariables.FORUM_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 1) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.close_topic_listing_label);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.open_topic_listing_label);
                        }
                        break;
                    case ConstantVariables.STORE_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 1) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.open_store);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.close_store);
                        }
                        break;
                }
                break;

            case "follow_unfollow_user":
                if (mBrowseListItems.isFollowingMember()) {
                    mMenuLabel = mContext.getResources().getString(R.string.unfollow);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.follow);
                }
                break;

            case "friendship_type":
                if (mBrowseListItems.getmFriendShipType() != null &&
                        !mBrowseListItems.getmFriendShipType().isEmpty() && mBrowseListItems.getmUserId() != 0) {
                    switch (mBrowseListItems.getmFriendShipType()) {
                        case "add_friend":
                            mMenuLabel = mContext.getResources().getString(R.string.add_friend_title);
                            break;

                        case "member_follow":
                            mMenuLabel = mContext.getResources().getString(R.string.follow);
                            break;

                        case "member_unfollow":
                            mMenuLabel = mContext.getResources().getString(R.string.unfollow);
                            break;

                        case "cancel_follow":
                            mMenuLabel = mContext.getResources().getString(R.string.cancel_follow_request);
                            break;

                        case "accept_request":
                            mMenuLabel = mContext.getResources().getString(R.string.accept_friend_request_title);
                            break;

                        case "remove_friend":
                            mMenuLabel = mContext.getResources().getString(R.string.remove_friend_title);
                            break;

                        case "cancel_request":
                            mMenuLabel = mContext.getResources().getString(R.string.cancel_friend_request_title);
                            break;
                    }
                }
                break;

            case "watch_topic":
            case "stop_watch_topic":
                if (mBrowseListItems.getmWatched() == 1) {
                    mMenuLabel = mContext.getResources().
                            getString(R.string.watch_listing_label);
                } else {
                    mMenuLabel = mContext.getResources().
                            getString(R.string.stop_watch_listing_label);
                }
                break;

            case "make_sticky":
            case "remove_sticky":

                if (mBrowseListItems.getmSticky() == 1) {
                    mMenuLabel = mContext.getResources().
                            getString(R.string.sticky_listing_label);
                } else {
                    mMenuLabel = mContext.getResources().
                            getString(R.string.remove_sticky_listing_label);
                }
                break;

            case "unsubscribe":
            case "subscribe":
                if (mBrowseListItems.getmSubscribed() == 0) {
                    mMenuLabel = mContext.getResources().
                            getString(R.string.subscribe_listing_button);
                } else {
                    mMenuLabel = mContext.getResources().
                            getString(R.string.unsubscribe_listing_button);
                }
                break;

            case "request_invite":
            case "cancel_invite":
            case "cancel":
            case "request_member":
            case "cancel_request":
                if (!isRequestFromViewPage) {
                    if (mBrowseListItems.getmIsRequestInvite() == 1) {
                        mMenuLabel = mContext.getResources().
                                getString(R.string.request_invite);
                    } else {
                        mMenuLabel = mContext.getResources().
                                getString(R.string.cancel_request_invite);
                    }
                } else {
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.GROUP_MENU_TITLE:
                        case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                            if (mBrowseListItems.getmIsRequestInvite() == 0) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.request_membership);
                            } else {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.cancel_request_membership);
                            }
                            break;

                        case ConstantVariables.EVENT_MENU_TITLE:
                            if (mBrowseListItems.getmIsRequestInvite() == 0) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.request_invite);
                            } else {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.cancel_request_invite);
                            }
                            break;

                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            if (AdvEventsProfilePage.sMembershipRequestCode == ConstantVariables.REQUEST_INVITE) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.request_invite);
                            } else if (AdvEventsProfilePage.sMembershipRequestCode == ConstantVariables.CANCEL_INVITE) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.cancel_request_invite);
                            }
                            break;

                        default:
                            mMenuLabel = mMenuJsonObject.optString("label").trim();
                            break;
                    }
                }

                break;

            case "join":
            case "leave":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        if (isRequestFromViewPage) {
                            if (AdvEventsProfilePage.sMembershipRequestCode == ConstantVariables.JOIN_EVENT) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.join_event_text);
                            } else if (AdvEventsProfilePage.sMembershipRequestCode == ConstantVariables.LEAVE_EVENT) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.leave_event_dialogue_title);
                            }
                        } else {
                            if (mBrowseListItems.getmJoined() == 0) {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.join_event_dialogue_title);
                            } else {
                                mMenuLabel = mContext.getResources().
                                        getString(R.string.leave_event_dialogue_title);
                            }
                        }
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                        if (mBrowseListItems.getmJoined() == 0) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.join_event_dialogue_title);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.leave_event_dialogue_title);
                        }
                        break;

                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (mBrowseListItems.getmJoined() == 0) {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.join_group_dialogue_title);
                        } else {
                            mMenuLabel = mContext.getResources().
                                    getString(R.string.leave_group_dialogue_title);
                        }
                        break;
                }
                break;

            case "make_officer":
            case "demote_officer":
                if (mBrowseListItems.getmIsGroupAdmin() == 0) {
                    mMenuLabel = mContext.getResources().getString(R.string.make_officer_menu_label);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.demote_officer_menu_label);
                }
                break;

            case "make_admin":
            case "remove_admin":
                if (mBrowseListItems.getmIsGroupAdmin() == 0) {
                    mMenuLabel = mContext.getResources().getString(R.string.make_group_admin_dialogue_title);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.remove_group_admin_dialogue_title);
                }
                break;

            case "favourite":
                if (mBrowseListItems.getIsFavouriteOption() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.favourite_title);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.unfavourite_title);
                }
                break;
            case "enable_method":
            case "disable_method":
                if (mBrowseListItems.getmClosed() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.disable_shipping);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.enable_shipping);
                }
                break;
            case "enable_file":
                if (mBrowseListItems.getmClosed() == 1) {
                    mMenuLabel = mContext.getResources().getString(R.string.disable_file);
                } else {
                    mMenuLabel = mContext.getResources().getString(R.string.enable_file);
                }
                break;

        }
        return mMenuLabel;
    }


    /**
     * Method encounter when option item is selected from option menus.
     *
     * @param mainView    MainView of activity.
     * @param view        optionIcon View.
     * @param id          id of selected item.
     * @param gutterMenus gutterMenu array which contains all the option data.
     */
    public void onMenuOptionItemSelected(View mainView, View view, int id,
                                         JSONArray gutterMenus) {

        // call to main method which perform action.
        onMenuOptionItemSelected(mainView, view, id, gutterMenus, 0, "");
    }

    /**
     * Method encounter when option item is selected from option menus.
     *
     * @param mainView    MainView of activity.
     * @param view        optionIcon View.
     * @param id          id of selected item.
     * @param gutterMenus gutterMenu array which contains all the option data.
     * @param currentList CurrentList in case of Adv. Event.
     */
    public void onMenuOptionItemSelected(View mainView, View view, int id,
                                         JSONArray gutterMenus, String currentList) {

        // call to main method which perform action.
        onMenuOptionItemSelected(mainView, view, id, gutterMenus, 0, currentList);
    }

    /**
     * Method which encounter when option item is selected from option menus.
     *
     * @param mainView      MainView of activity.
     * @param view          optionIcon View.
     * @param id            id of selected item.
     * @param gutterMenus   gutterMenu array which contains all the option data.
     * @param listingTypeId ListingTypeId of current listing type.
     */
    public void onMenuOptionItemSelected(View mainView, View view, int id, JSONArray gutterMenus,
                                         int listingTypeId) {
        // call to main method which perform action.
        onMenuOptionItemSelected(mainView, view, id, gutterMenus, listingTypeId, "");
    }


    /**
     * Main Method which encounter when option item is selected from option menus.
     *
     * @param mainView      MainView of activity.
     * @param view          optionIcon View.
     * @param id            id of selected item.
     * @param gutterMenus   gutterMenu array which contains all the option data.
     * @param listingTypeId ListingTypeId of current listing type.
     */
    public void onMenuOptionItemSelected(View mainView, View view, int id, JSONArray gutterMenus,
                                         int listingTypeId, String currentList) {
        mListingTypeId = listingTypeId;
        onMenuItemSelected(view, id, gutterMenus, true, true, currentList);
    }

    /**
     * Main Method which encounter when option item is selected from option menus.
     *
     * @param view        MainView of activity.
     * @param id          id of selected item.
     * @param gutterMenus gutterMenu array which contains all the option data.
     */
    public void onMenuItemSelected(View view, int id, JSONArray gutterMenus, String currentSelectedModule,
                                   BrowseListItems browseListItems) {
        mMainView = view;
        mBrowseListItems = browseListItems;
        mCurrentSelectedModule = currentSelectedModule;
        onMenuItemSelected(view, id, gutterMenus, false, true, null);
    }


    /**
     * Method to perform action on option item selection.
     *
     * @param view                  optionIcon View.
     * @param id                    id of selected item.
     * @param menuArray             gutterMenu array which contains all the option data.
     * @param isBrowsePage          true if called from browse page.
     * @param isRequestFromViewPage true if called from view page request.
     * @param currentList           CurrentList in case of Adv. Event.
     */
    public void onMenuItemSelected(View view, int id, JSONArray menuArray, boolean isBrowsePage,
                                   boolean isRequestFromViewPage, final String currentList) {

        try {
            mMainView = view;
            mPostParams.clear();
            mMenuJsonObject = menuArray.optJSONObject(id);
            mMenuName = mMenuJsonObject.optString("name");
            mUrlParams = mMenuJsonObject.optJSONObject("urlParams");
            mRedirectUrl = AppConstant.DEFAULT_URL + mMenuJsonObject.optString("url");


            if (mUrlParams != null && mUrlParams.length() != 0) {
                JSONArray urlParamsNames = mUrlParams.names();
                for (int j = 0; j < mUrlParams.length(); j++) {
                    String name = urlParamsNames.getString(j);
                    String value = mUrlParams.getString(name);
                    mPostParams.put(name, value);
                }
                mRedirectUrl = mAppConst.buildQueryString(mRedirectUrl, mPostParams);
            }

            switch (mMenuName) {
                case "photo":
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.CLASSIFIED_MENU_TITLE:
                            mRedirectUrl = AppConstant.DEFAULT_URL + "classifieds/photo/upload/" +
                                    mBrowseListItems.getmListItemId();
                            break;
                        case ConstantVariables.ALBUM_MENU_TITLE:
                            mRedirectUrl = AppConstant.DEFAULT_URL + "albums/upload?album_id=" +
                                    mBrowseListItems.getmListItemId();
                            break;
                    }
                    break;

                case "payment":
                case "makePayment":
                case "package_payment":
                    mRedirectUrl = mMenuJsonObject.optString("url");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent;

        switch (mMenuName) {

            case "choose_from_album":
                mBrowseListItems.setmBrowseListTitle(mMenuJsonObject.optString("label").trim());
            case "upload_cover_photo":
            case "upload_photo":
            case "view_profile_photo":
            case "view_cover_photo":
                if (mOnOptionItemClickResponseListener != null) {
                    mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                }
                break;

            case "user_home_edit":
                intent = new Intent(mContext, EditProfileActivity.class);
                intent.putExtra("url", mRedirectUrl);
                intent.putExtra("is_photo_tab", mMenuJsonObject.optBoolean("is_photo_tab"));
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "user_profile_send_message":
                intent = new Intent(mContext, CreateNewMessage.class);
                intent.putExtra(ConstantVariables.USER_ID, mBrowseListItems.getmUserId());
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mBrowseListItems.getDisplayName());
                intent.putExtra("isSendMessageRequest", true);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "post_reply":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE, "post_reply");
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "rename":
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, "rename_topic");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mBrowseListItems.getmBrowseListTitle());
                intent.putExtra(ConstantVariables.VIEW_PAGE_URL, mBrowseListItems.getmContentUrl());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "move":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE, "move_topic");
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "createReview":
                mRedirectUrl = mRedirectUrl + "/" + mBrowseListItems.getmListItemId();
            case "review":
            case "create_review":
                if (mCurrentSelectedModule.equals(ConstantVariables.MLT_MENU_TITLE)) {
                    mRedirectUrl = mRedirectUrl + "?listing_id=" + mBrowseListItems.getmListItemId() +
                            "&listingtype_id=" + mListingTypeId;
                }
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, "create_review");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.REQUEST_CODE, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "update":
                mRedirectUrl = mRedirectUrl + "&listing_id=" + mBrowseListItems.getmListItemId() +
                        "&listingtype_id=" + mListingTypeId;
            case "update_review":
            case "edit_review":
            case "updateReview":
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.FORM_TYPE, "update_review");
                if (mCallingFragment != null) {
                    mCallingFragment.startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                } else {
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                }
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "askopinion":
            case "tellafriend":
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.FORM_TYPE, "tellafriend");
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "messageowner":
            case "messageOwner":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.FORM_TYPE, "message_owner");
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "memberDiaries":
                Bundle bundle = new Bundle();
                bundle.putString(ConstantVariables.FRAGMENT_NAME, "isMemberDiaries");
                bundle.putString(ConstantVariables.URL_STRING, mRedirectUrl);
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                bundle.putString(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                intent = new Intent(mContext, FragmentLoadActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "memberWishlist":
                intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.CATEGORY_VALUE, mPostParams.get("text"));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "wishlist":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl + "&listingtype_id=" + mListingTypeId);
                intent.putExtra(ConstantVariables.FORM_TYPE, "add_wishlist");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "claim":
                if (mCurrentSelectedModule.equals(ConstantVariables.MLT_MENU_TITLE)) {
                    mRedirectUrl = mRedirectUrl + "?listing_id=" + mBrowseListItems.getmListItemId() +
                            "&listingtype_id=" + mListingTypeId;
                }
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                intent.putExtra(ConstantVariables.FORM_TYPE, "claim_listing");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "addVideos":
            case "videoCreate":
                intent = new Intent(mContext, CreateNewEntry.class);
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.MLT_MENU_TITLE:
                        intent.putExtra(ConstantVariables.FORM_TYPE, "add_video");
                        mRedirectUrl = mRedirectUrl + "&listingtype_id=" + mListingTypeId;
                        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                        intent.putExtra(ConstantVariables.LISTING_TYPE_ID, mListingTypeId);
                        break;
                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        intent.putExtra(ConstantVariables.FORM_TYPE, "add_video_siteevent");
                        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "add_video_siteevent");
                        break;
                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        intent.putExtra(ConstantVariables.FORM_TYPE, "add_video_sitepage");
                        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "add_video_sitepage");
                        break;
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        intent.putExtra(ConstantVariables.FORM_TYPE, "add_video_sitegroup");
                        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "add_video_sitegroup");
                        break;
                }

                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.ADD_VIDEO_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "notification_settings":
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, "notification_settings");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "diary":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, "add_to_diary");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "capacity_waitlist":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, "capacity_waitlist");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "package_payment":
            case "payment":
            case "makePayment":
                intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("isPackagePayment", true);
                intent.putExtra("url", mRedirectUrl);
                intent.putExtra("isAdvEventId", mEventId);
                Log.d("finallythisisIt ", String.valueOf(mEventId));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case ConstantVariables.PAYMENT_METHOD:
                Log.d("SampleJsonHere ", String.valueOf(mUrlParams));
                if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
                    intent = new Intent(mContext, EditEntry.class);
                    int mEventId = mUrlParams.optInt("event_id");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_EVENT_PAYMENT_METHOD);
                    intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.PAYMENT_CONFIG_METHOD);
                    intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                    intent.putExtra("isAdvEventId", mEventId);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    String editUrl = AppConstant.DEFAULT_URL + "sitestore/set-store-gateway-info/" + mBrowseListItems.getmContentId();
                    GlobalFunctions.showValidationPopup(mContext, mRedirectUrl, editUrl);
                }
                break;

            case "package_info":
                intent = new Intent(mContext, PackageView.class);
                intent.putExtra("packageObject", mBrowseListItems.getmPackageObject().toString());
                intent.putExtra("packageTitle", mBrowseListItems.getmBrowseListTitle());
                intent.putExtra("isPackageUpgrade", mBrowseListItems.getmIsPackageUpgrade());
                intent.putExtra("urlParams", mUrlParams.toString());
                intent.putExtra("upgrade_url", mRedirectUrl);

                if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)){
                    intent.putExtra("isPackagePaid", mBrowseListItems.getmIsPaid());
                }
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "upgrade_package":
                AlertDialog.Builder alert_builder = new AlertDialog.Builder(mContext);
                alert_builder.setMessage(R.string.change_package_message);
                alert_builder.setTitle(R.string.change_package_title);
                alert_builder.setCancelable(false);
                alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent;
                        PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
                        if (currentList != null && !currentList.isEmpty()) {
                            if (currentList.equals("site_package")) {
                                mSuccessMessage = mContext.getResources().getString(R.string.upgrade_package_success_message);
                                mDialogueMessage = mContext.getResources().getString(R.string.upgrade_package_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.upgrade_package_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.upgrade_package_dialogue_button);

                                performAction();
                            } else {
                                intent = new Intent(mContext, SelectPackage.class);
                                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                                intent.putExtra("postParams", mPostParams.toString());
                                mContext.startActivity(intent);
                                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        } else {
                            intent = new Intent(mContext, SelectPackage.class);
                            intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });
                alert_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alert_builder.create().show();
                break;

            case "create_ticket":
                intent = new Intent(mContext, AdvEventsAvailableTickets.class);
                intent.putExtra("url", mRedirectUrl);
                intent.putExtra("urlParams", mUrlParams.toString());
                Log.d("CreateTicketParams ", mUrlParams.toString());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            //region dev-sareno@Changes ~com.fellopages.mobileapp.classes.common.utils.projectchanges.ChangesTracker.ISSUE_NO_5
            case "dashboard":
                AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(mContext);
                dlgBuilder.setTitle(null);
                dlgBuilder.setMessage(mContext.getResources().getString(R.string.browse_dashboard_dialog_message));
                dlgBuilder.setCancelable(true);
                dlgBuilder.setNegativeButton(mContext.getResources().getString(R.string.browse_dashboard_dialog_negative_button), (dialog, which) -> dialog.dismiss());
                dlgBuilder.setPositiveButton(mContext.getResources().getString(R.string.browse_dashboard_dialog_positive_button), (dialog, which) -> {
                    String webUrl = mRedirectUrl.replace("api/rest/", "");
                    if (URLUtil.isValidUrl(webUrl)) {
                        Intent lIntent = new Intent(Intent.ACTION_VIEW);
                        lIntent.setData(Uri.parse(webUrl));
                        mContext.startActivity(Intent.createChooser(lIntent, mContext.getResources().getString(R.string.browse_dashboard_title)));
                    }

                    dialog.dismiss();
                });
                dlgBuilder.create().show();

                break;
            //endregion

            case "invite":
            case "suggest":
                switch (mCurrentSelectedModule) {

                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.EVENT_MENU_TITLE:
                        intent = new Intent(mContext, Invite.class);
                        break;

                    default:
                        intent = new Intent(mContext, InviteGuest.class);
                        break;
                }
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "create":
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);

                if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                                && mBrowseListItems.getmIsPaid() == 1){
                    intent.putExtra("isAdvEventPayment", true);
                }
                if (currentList != null && currentList.equals("diaries_siteevent")) {
                    intent.putExtra(ConstantVariables.FORM_TYPE, "create_new_diary");
                }
                intent.putExtra(ConstantVariables.LISTING_TYPE_ID, mListingTypeId);
                if (mCurrentSelectedModule.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE)
                        && mUrlParams != null && mUrlParams.length() > 0) {
                    intent.putExtra("channel_id", mUrlParams.optInt("main_channel_id"));
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_VIDEO_MENU_TITLE);
                } else {
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                }
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "ticket_details":
                Intent detailsIntent = new Intent(mContext, FragmentLoadActivity.class);
                String url = mRedirectUrl;
                detailsIntent.putExtra(ConstantVariables.URL_STRING, url);
                detailsIntent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.TICKET_DETAILS_FORM_TYPE);
                detailsIntent.putExtra(ConstantVariables.FRAGMENT_NAME, ConstantVariables.TICKET_DETAILS_FRAGMENT_NAME);
                detailsIntent.putExtra(ConstantVariables.CONTENT_TITLE, mContext.getResources().getString(R.string.ticket_details_title));
                detailsIntent.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                detailsIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE);
                mContext.startActivity(detailsIntent);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "report":
            case "user_profile_report":

                intent = new Intent(mContext, ReportEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "edit":
            case "edit_settings":
            case "edit_privacy":
            case "ticket_edit":

                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.FORUM_MENU_TITLE:
                        intent.putExtra(ConstantVariables.FORM_TYPE, "edit_post");
                        intent.putExtra(ConstantVariables.ITEM_POSITION, mPosition);
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    case ConstantVariables.DIARY_MENU_TITLE:
                    case ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE:
                        if (currentList != null && currentList.equals("diaries_siteevent")) {
                            intent.putExtra(ConstantVariables.FORM_TYPE, "edit_diary");
                            intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label").trim());
                        } else {
                            intent.putExtra(ConstantVariables.FORM_TYPE, "edit_event");
                        }
                        break;

                    case ConstantVariables.MLT_MENU_TITLE:
                        if (currentList != null && currentList.equals("light_box")) {
                            mRedirectUrl += "?photo_id=" + mBrowseListItems.getmContentId() +
                                    "&listingtype_id=" + mListingTypeId;

                        } else {
                            mRedirectUrl += "?listingtype_id=" + mListingTypeId;
                            intent.putExtra(ConstantVariables.FORM_TYPE, "edit_listing");
                            intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label").trim());
                        }
                        break;

                    case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                        intent.putExtra(ConstantVariables.FORM_TYPE, "edit_wishlist");
                        intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label").trim());
                        break;
                }
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);

                if (isRequestFromViewPage) {
                    intent.putExtra(ConstantVariables.REQUEST_CODE, ConstantVariables.VIEW_PAGE_EDIT_CODE);
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.VIEW_PAGE_EDIT_CODE);

                } else if (mCurrentSelectedModule.equals(ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE)){
                    ((Activity)mContext).startActivityForResult(intent, ConstantVariables.VIEW_PAGE_EDIT_CODE);
                } else {
                    if (mCallingFragment != null) {
                        mCallingFragment.startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                    } else if (currentList != null && currentList.equals("light_box")) {
                        ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                    } else {
                        mContext.startActivity(intent);
                    }
                }
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "quote":
                String quote = "[blockquote][b]" + mBrowseListItems.getPostByName() + " " +
                        mContext.getResources().getString(R.string.said_text) + ":[/b]\n" +
                        "\n" + mBrowseListItems.getPostBody() +
                        "\n" + "[/blockquote]";

                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE, "quote");
                intent.putExtra("quote_text", quote);
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

//            case "photo":
//                intent = new Intent(mContext, MultiImageSelectorActivity.class);
//                // Whether photoshoot
//                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
//                // The maximum number of selectable image
//                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT,
//                        ConstantVariables.FILE_UPLOAD_LIMIT);
//                // Select mode
//                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
//                        MultiImageSelectorActivity.MODE_MULTI);
//
//                intent.putExtra(MultiImageSelectorActivity.EXTRA_URL, mRedirectUrl);
//
//                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);
//
//                break;

            case "add":
            case "add_photo":
            case "addphoto":
            case "photo":
            case "addPhotos":
                if (!isBrowsePage) {
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.CLASSIFIED_MENU_TITLE:
                            ClassifiedUtil.checkPermission(mCallingFragment, mRedirectUrl);
                            break;
                        case ConstantVariables.ALBUM_MENU_TITLE:
                            AlbumUtil.checkPermission(mCallingFragment, mRedirectUrl);
                            break;
                        case ConstantVariables.MLT_MENU_TITLE:
                            MLTUtil.checkPermission(mCallingFragment, mRedirectUrl + "?listingtype_id=" +
                                    mBrowseListItems.getmListingTypeId());
                            break;
                    }
                } else if (isRequestFromViewPage && mOnOptionItemClickResponseListener != null) {
                    mBrowseListItems.setmRedirectUrl(mRedirectUrl);
                    mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                }
                break;

            case "share":
                mSocialShareUtil.sharePost(view, mBrowseListItems.getmBrowseListTitle(),
                        mBrowseListItems.getmBrowseImgUrl(),
                        mRedirectUrl, mCurrentSelectedModule, mBrowseListItems.getmContentUrl());
                break;

            case "delete":
            case "delete_album":
            case "delete_event":
            case "delete_poll":
            case "delete_method":
            case "delete_file":
            case "delete_product":
//                if (mCurrentSelectedModule.equals(ConstantVariables.MLT_MENU_TITLE) && mListingTypeId != 0
//                        && currentList != null) {
//
//                }
                if (currentList.equals("user_review")) {
                    mRedirectUrl += "&listing_id=" + mBrowseListItems.getmListItemId() +
                            "&listingtype_id=" + mListingTypeId;
                } else if (currentList.equals("light_box")) {
                    mRedirectUrl += "?photo_id=" + mBrowseListItems.getmContentId() +
                            "&listingtype_id=" + mListingTypeId;
                }

                mDialogueMessage = mContext.getResources().getString(R.string.delete_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.delete_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.delete_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.delete_dialogue_success_message);
                performAction();
                break;

            case "ticket_delete":
                if (mCurrentSelectedModule.equals(ConstantVariables.MLT_MENU_TITLE) && mListingTypeId != 0
                        && currentList != null) {
                    if (currentList.equals("user_review")) {
                        mRedirectUrl += "&listing_id=" + mBrowseListItems.getmListItemId() +
                                "&listingtype_id=" + mListingTypeId;
                    } else if (currentList.equals("light_box")) {
                        mRedirectUrl += "?photo_id=" + mBrowseListItems.getmContentId() +
                                "&listingtype_id=" + mListingTypeId;
                    }
                }


                mDialogueMessage = mContext.getResources().getString(R.string.delete_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.delete_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.delete_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.delete_dialogue_success_message);
                performAction();
                break;

            case "mute":
                if (mRedirectUrl.contains("is_mute=1")) {
                    mDialogueMessage = mContext.getResources().getString(R.string.mute_story_dialogue_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.mute_story_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.mute_story_dialogue_button);
                    mSuccessMessage = mContext.getResources().getString(R.string.mute_story_dialogue_success_message);
                } else {
                    mDialogueMessage = mContext.getResources().getString(R.string.unmute_story_dialogue_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.unmute_story_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.unmute_story_dialogue_button);
                    mSuccessMessage = mContext.getResources().getString(R.string.unmute_story_dialogue_success_message);
                }
                performAction();
                break;

            case "join":
            case "leave":
            case "join-waitlist":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                        if (mBrowseListItems.getmJoined() == 0) {
                            mRedirectUrl = mRedirectUrl.replace("leave/", "join/");
                            mDialogueMessage = mContext.getResources().getString(R.string.join_group_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.join_group_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.join_group_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.join_group_dialogue_success_message);
                        } else {
                            mRedirectUrl = mRedirectUrl.replace("join/", "leave/");
                            mDialogueMessage = mContext.getResources().getString(R.string.leave_group_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.leave_group_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.leave_group_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.leave_group_dialogue_success_message);
                        }
                        performAction();
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:

                        if (mBrowseListItems.getmJoined() == 0) {
                            mRedirectUrl = mRedirectUrl.replace("leave/", "join/");
                            mDialogueMessage = mContext.getResources().getString(R.string.join_event_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.join_event_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.join_event_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.join_event_dialogue_success_message);
                            performActionWithMultipleOptions(isRequestFromViewPage);
                        } else {
                            mRedirectUrl = mRedirectUrl.replace("join/", "leave/");
                            mDialogueMessage = mContext.getResources().getString(R.string.leave_event_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.leave_event_dialogue_success_message);
                            performAction();
                        }
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        if (isRequestFromViewPage) {
                            if (AdvEventsProfilePage.sMembershipRequestCode
                                    == ConstantVariables.JOIN_EVENT) {
                                mRedirectUrl = mRedirectUrl.replace("leave/", "join/");
                                mDialogueMessage = mContext.getResources().getString(R.string.join_event_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.join_event_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.join_event_dialogue_title);
                                mSuccessMessage = mContext.getResources().getString(R.string.join_event_dialogue_success_message);
                                performActionWithMultipleOptions(true);

                            } else if (AdvEventsProfilePage.sMembershipRequestCode
                                    == ConstantVariables.LEAVE_EVENT) {
                                mRedirectUrl = mRedirectUrl.replace("join/", "leave/");
                                mDialogueMessage = mContext.getResources().getString(R.string.leave_event_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                                mSuccessMessage = mContext.getResources().getString(R.string.leave_event_dialogue_success_message);
                                performAction();

                            } else if (AdvEventsProfilePage.sMembershipRequestCode
                                    == ConstantVariables.EVENT_WAIT_LIST) {
                                mPostParams.put("occurrence_id", String.valueOf(mBrowseListItems.getmOccurrenceId()));
                                mPostParams.put("event_id", String.valueOf(mBrowseListItems.getmListItemId()));
                                mDialogueMessage = mContext.getResources().getString(R.string.add_to_waitlist_member_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.add_to_waitlist_member_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.add_to_waitlist_member_dialogue_button);
                                mSuccessMessage = mContext.getResources().getString(R.string.add_to_waitlist_member_dialogue_success_message);

                                performAction();
                            }
                        } else {
                            if (mBrowseListItems.getmJoined() == 0) {
                                mRedirectUrl = mRedirectUrl.replace("leave/", "join/");
                                mDialogueMessage = mContext.getResources().getString(R.string.join_event_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.join_event_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.join_event_dialogue_title);
                                mSuccessMessage = mContext.getResources().getString(R.string.join_event_dialogue_success_message);
                                performActionWithMultipleOptions(false);
                            } else {
                                mRedirectUrl = mRedirectUrl.replace("join/", "leave/");
                                mDialogueMessage = mContext.getResources().getString(R.string.leave_event_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                                mSuccessMessage = mContext.getResources().getString(R.string.leave_event_dialogue_success_message);
                                mPostParams.put("getJoinInfo", "1");
                                performAction();
                            }
                        }
                        break;

                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (mBrowseListItems.getmJoined() == 0) {
                            mRedirectUrl = mRedirectUrl.replace("leave/", "join/");
                            Intent invite = new Intent(mContext, Invite.class);
                            invite.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_GROUPS_MENU_TITLE);
                            invite.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                            ((Activity) mContext).startActivityForResult(invite, ConstantVariables.ADV_GROUPS_INVITE_REQUEST);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            mRedirectUrl = mRedirectUrl.replace("join/", "leave/");
                            mDialogueMessage = mContext.getResources().getString(R.string.leave_group_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.leave_group_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.leave_group_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.leave_group_dialogue_success_message);
                            performAction();
                        }
                        break;
                }
                break;

            case "leave_group":

                mDialogueMessage = mContext.getResources().getString(R.string.leave_group_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.leave_group_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.leave_group_dialogue_title);
                mSuccessMessage = mContext.getResources().getString(R.string.leave_group_dialogue_success_message);
                performAction();
                break;

            case "leave_event":

                mDialogueMessage = mContext.getResources().getString(R.string.leave_event_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.leave_event_dialogue_title);
                mSuccessMessage = mContext.getResources().getString(R.string.leave_event_dialogue_success_message);
                performAction();
                break;

            case "request_member":
            case "cancel_request":
            case "cancel_follow":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                        if (mBrowseListItems.getmIsRequestInvite() == 0) {
                            mRedirectUrl = mRedirectUrl.replace("request-cancel/", "request/");
                            mDialogueMessage = mContext.getResources().getString(R.string.group_request_member_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.group_request_member_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.group_request_member_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString
                                    (R.string.group_request_member_dialogue_success_message);
                        } else {
                            mRedirectUrl = mRedirectUrl.replace("request/", "request-cancel/");
                            mDialogueMessage = mContext.getResources().getString(R.string.group_cancel_request_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.group_cancel_request_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.group_cancel_request_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.group_cancel_request_dialogue_success_message);

                        }
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                        if (mBrowseListItems.getmIsRequestInvite() == 0) {
                            mRedirectUrl = mRedirectUrl.replace("cancel/", "request/");
                            mDialogueMessage = mContext.getResources().getString(R.string.event_request_member_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.event_request_member_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.event_request_member_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.event_request_member_dialogue_success_message);
                        } else {
                            mRedirectUrl = mRedirectUrl.replace("request/", "cancel/");
                            mDialogueMessage = mContext.getResources().getString(R.string.event_cancel_request_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.event_cancel_request_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.event_cancel_request_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.event_cancel_request_dialogue_success_message);

                        }
                        break;

                    case ConstantVariables.USER_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.cancel_friend_request_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.cancel_friend_request_title);
                        mDialogueButton = mContext.getResources().getString(R.string.cancel_friend_request_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.cancel_friend_request_success_message);
                        break;
                }
                performAction();
                break;

            case "accept_request":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.group_accept_request_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.group_accept_request_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.group_accept_request_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.group_accept_request_dialogue_success_message);
                        performAction();
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_accept_request_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_accept_request_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_accept_request_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_accept_request_dialogue_success_message);

                        performActionWithMultipleOptions(false);
                        break;

                    case ConstantVariables.USER_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.accept_friend_request_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.accept_friend_request_title);
                        mDialogueButton = mContext.getResources().getString(R.string.accept_friend_request_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.accept_friend_request_success_message);
                        performAction();
                        break;
                }
                break;

            case "reject_request":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.group_reject_request_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.group_reject_request_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.group_reject_request_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.group_reject_request_dialogue_success_message);
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_reject_request_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_reject_request_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_reject_request_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_reject_request_dialogue_success_message);
                        break;
                }
                performAction();
                break;

            case "like":
                if (mBrowseListItems.getmLiked() == 0) {
                    mRedirectUrl = AppConstant.DEFAULT_URL + "like";
                } else {
                    mRedirectUrl = AppConstant.DEFAULT_URL + "unlike";
                }

            case "follow":
            case "unfollow":
            case "following":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                    case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
                        performActionWithoutDialog();
                        break;

                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        if (SitePageProfilePage.sIsPageFollowed == 0) {
                            mDialogueMessage = mContext.getResources().getString(R.string.follow_page_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.follow_page_title);
                            mDialogueButton = mContext.getResources().getString(R.string.follow_page_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.follow_page_success_message);

                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.unfollow_page_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.unfollow_page_title);
                            mDialogueButton = mContext.getResources().getString(R.string.unfollow_page_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.unfollow_page_success_message);
                        }
                        performAction();
                        break;

                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (AdvGroupsProfile.sIsGroupFollowed == 0) {
                            mDialogueMessage = mContext.getResources().getString(R.string.follow_group_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.follow_group_title);
                            mDialogueButton = mContext.getResources().getString(R.string.follow);
                            mSuccessMessage = mContext.getResources().getString(R.string.follow_group_success_message);

                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.unfollow_group_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.unfollow_group_title);
                            mDialogueButton = mContext.getResources().getString(R.string.unfollow);
                            mSuccessMessage = mContext.getResources().getString(R.string.unfollow_group_success_message);
                        }
                        performAction();
                        break;

                    case ConstantVariables.USER_MENU_TITLE:
                        if(mMenuName.equals("follow")) {
                            mDialogueMessage = mContext.getResources().getString(R.string.follow_member_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.follow_member_title);
                            mDialogueButton = mContext.getResources().getString(R.string.follow);
                            mSuccessMessage = mContext.getResources().getString(R.string.follow_member_success_message);

                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.unfollow_member_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.unfollow_member_title);
                            mDialogueButton = mContext.getResources().getString(R.string.unfollow);
                            mSuccessMessage = mContext.getResources().getString(R.string.unfollow_member_success_message);
                        }
                        performAction();

                        break;
                }
                break;

            case "close_poll":
            case "open_poll":

                mPostParams.clear();
                if (mBrowseListItems.getmClosed() == 1) {
                    mPostParams.put("closed", String.valueOf("0"));
                    mDialogueMessage = mContext.getResources().getString(R.string.open_poll_msg);
                    mDialogueTitle = mContext.getResources().getString(R.string.open_poll_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.open_poll_dialogue_title);
                    mSuccessMessage = mContext.getResources().getString(R.string.open_poll_success);
                } else {
                    mPostParams.put("closed", String.valueOf("1"));
                    mDialogueMessage = mContext.getResources().getString(R.string.close_poll_msg);
                    mDialogueTitle = mContext.getResources().getString(R.string.close_poll_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.close_poll_dialogue_title);
                    mSuccessMessage = mContext.getResources().getString(R.string.close_poll_success);
                }
                performAction();
                break;

            case "close":
            case "open":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.MLT_MENU_TITLE:
                        mPostParams.put("close", String.valueOf(mBrowseListItems.getmClosed()));
                    case ConstantVariables.CLASSIFIED_MENU_TITLE:
                        if (mCurrentSelectedModule.equals(ConstantVariables.CLASSIFIED_MENU_TITLE)) {

                            mPostParams.put("closed",
                                    String.valueOf(mBrowseListItems.getmClosed() == 1 ? 0 : 1));
                        }
                        if (mBrowseListItems.getmClosed() == 1) {
                            mDialogueMessage = mContext.getResources().getString(R.string.open_listing_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.open_listing_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.open_listing_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.open_listing_success);

                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.close_listing_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.close_listing_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.close_listing_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.close_listing_success);
                        }
                        break;

                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        mPostParams.put("close", String.valueOf(mBrowseListItems.getmClosed()));
                        if (mBrowseListItems.getmClosed() == 1) {
                            mDialogueMessage = mContext.getResources().getString(R.string.open_group_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.open_group_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.open_group_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.open_group_success);
                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.close_group_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.close_group_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.close_group_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.close_group_success);

                        }
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        if (mBrowseListItems.getmClosed() == 0) {
                            mDialogueMessage = mContext.getResources().getString(R.string.event_cancel_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.event_cancel_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.event_cancel_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.event_cancel_dialogue_success_message);
                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.event_re_publish_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.event_re_publish_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.event_re_publish_dialogue_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.event_re_publish_dialogue_success_message);
                        }
                        break;

                    case ConstantVariables.FORUM_MENU_TITLE:
                        mPostParams.put("close", String.valueOf(mBrowseListItems.getmClosed()));
                        if (mBrowseListItems.getmClosed() == 1) {
                            mDialogueMessage = mContext.getResources().getString(R.string.close_topic_listing_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.close_topic_listing_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.close_topic_listing_dialogue_btn_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.close_topic_listing_success);
                        } else {

                            mDialogueMessage = mContext.getResources().getString(R.string.open_topic_listing_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.open_topic_listing_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.open_topic_listing_dialogue_btn_title);
                            mSuccessMessage = mContext.getResources().getString(R.string.open_topic_listing_success);
                        }
                        break;

                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                        mPostParams.put("close", String.valueOf(mBrowseListItems.getmClosed()));
                        if (mBrowseListItems.getmClosed() == 1) {
                            mDialogueMessage = mContext.getResources().getString(R.string.open_page_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.open_page_label);
                            mDialogueButton = mContext.getResources().getString(R.string.open_page_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.open_page_success);

                        } else {
                            mDialogueMessage = mContext.getResources().getString(R.string.close_page_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.close_page_label);
                            mDialogueButton = mContext.getResources().getString(R.string.close_page_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.close_page_success);
                        }
                        break;
                    case ConstantVariables.STORE_MENU_TITLE:
                        mPostParams.put("closed", String.valueOf(mBrowseListItems.getmClosed()));
                        if(mBrowseListItems.getmClosed() == 1) {
                            mDialogueMessage = mContext.getResources().getString(R.string.open_store_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.open_store_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.yes_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.open_store_success);

                        }else {
                            mDialogueMessage = mContext.getResources().getString(R.string.close_store_msg);
                            mDialogueTitle = mContext.getResources().getString(R.string.close_store_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.yes_label);
                            mSuccessMessage = mContext.getResources().getString(R.string.close_store_success);
                        }
                        break;
                }

                performAction();
                break;

            case "watch_topic":
            case "stop_watch_topic":

                mPostParams.put("watch", String.valueOf(mBrowseListItems.getmWatched()));
                if (mBrowseListItems.getmWatched() == 1) {
                    mDialogueMessage = mContext.getResources().getString(R.string.watch_listing_msg);
                    mDialogueTitle = mContext.getResources().getString(R.string.watch_listing_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.watch_listing_dialogue_btn_title);
                    mSuccessMessage = mContext.getResources().getString(R.string.watch_listing_success);
                    performAction();
                } else {
                    mDialogueMessage = mContext.getResources().getString(R.string.stop_watch_listing_msg);
                    mDialogueTitle = mContext.getResources().getString(R.string.stop_watch_listing_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.stop_watch_listing_dialogue_btn_title);
                    mSuccessMessage = mContext.getResources().getString(R.string.stop_watch_listing_success);
                    performAction();
                }
                break;

            case "make_sticky":
            case "remove_sticky":

                mPostParams.put("sticky", String.valueOf(mBrowseListItems.getmSticky()));
                if (mBrowseListItems.getmSticky() == 1) {
                    mDialogueMessage = mContext.getResources().getString(R.string.sticky_listing_msg);
                    mDialogueTitle = mContext.getResources().getString(R.string.sticky_listing_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.sticky_listing_dialogue_btn_title);
                    mSuccessMessage = mContext.getResources().getString(R.string.sticky_listing_success);
                    performAction();
                } else {
                    mDialogueMessage = mContext.getResources().getString(R.string.remove_sticky_listing_msg);
                    mDialogueTitle = mContext.getResources().getString(R.string.remove_sticky_listing_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.remove_sticky_listing_dialogue_btn_title);
                    mSuccessMessage = mContext.getResources().getString(R.string.remove_sticky_listing_success);
                    performAction();
                }
                break;

            case "unsubscribe":
            case "subscribe":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.MLT_MENU_TITLE:
                        if (mBrowseListItems.getmSubscribed() == 1) {
                            mRedirectUrl = AppConstant.DEFAULT_URL + "listing/remove/" +
                                    mBrowseListItems.getmListItemId() + "?listing_id=" +
                                    mBrowseListItems.getmListItemId() + "&listingtype_id=" + mListingTypeId;
                            mDialogueMessage = mContext.getResources().getString(R.string.unsubscribe_listing_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.unsubscribe_listing_title);
                            mDialogueButton = mContext.getResources().getString(R.string.unsubscribe_listing_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.unsubscribe_listing_success_message);

                        } else {
                            mRedirectUrl = AppConstant.DEFAULT_URL + "listing/add/" +
                                    mBrowseListItems.getmListItemId() + "?listing_id=" +
                                    mBrowseListItems.getmListItemId() + "&listingtype_id=" + mListingTypeId;
                            mDialogueMessage = mContext.getResources().getString(R.string.subscribe_listing_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.subscribe_listing_title);
                            mDialogueButton = mContext.getResources().getString(R.string.subscribe_listing_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.subscribe_listing_success_message);
                        }
                        break;

                    case ConstantVariables.BLOG_MENU_TITLE:
                        if (mBrowseListItems.getmSubscribed() == 1) {
                            mRedirectUrl = AppConstant.DEFAULT_URL + "blogs/unsubscribe";
                            mDialogueMessage = mContext.getResources().getString(R.string.unsubscribe_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.unsubscribe_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.unsubscribe_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.unsubscribe_dialogue_success_message);

                        } else {
                            mRedirectUrl = AppConstant.DEFAULT_URL + "blogs/subscribe";
                            mDialogueMessage = mContext.getResources().getString(R.string.subscribe_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.subscribe_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.subscribe_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.subscribe_dialogue_success_message);

                        }
                        break;
                }
                performAction();
                break;

            case "publish":
                mDialogueMessage = mContext.getResources().getString(R.string.publish_group_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.publish_group_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.publish_group_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.publish_group_success);
                performAction();
                break;

            case "remove":
                mDialogueMessage = mContext.getResources().getString(R.string.remove_listing_dialogue_message) + "?";
                mDialogueTitle = mContext.getResources().getString(R.string.remove_listing_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.remove_listing_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.remove_listing_dialogue_success_message) + ".";
                performAction();
                break;

            case "remove_member":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:

                        mDialogueMessage = mContext.getResources().getString(R.string.remove_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.remove_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.remove_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.remove_member_dialogue_success_message);
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_remove_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_remove_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_remove_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_remove_member_dialogue_success_message);
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_remove_guest_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_remove_guest_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_remove_guest_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_remove_guest_dialogue_success_message);
                        break;
                }
                performAction();

                break;

            case "make_admin":
            case "remove_admin":
                if (mBrowseListItems.getmIsGroupAdmin() == 0) {
                    mMenuName = "make_admin";
                    mRedirectUrl = AppConstant.DEFAULT_URL + "advancedgroups/member/makeadmin/"
                            + mBrowseListItems.getmListItemId();
                    mDialogueMessage = mContext.getResources().getString(R.string.make_group_admin_dialogue_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.make_group_admin_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.make_group_admin_dialogue_button);
                    mSuccessMessage = mContext.getResources().getString(R.string.make_group_admin_dialogue_success_message);
                } else {
                    mMenuName = "remove_admin";
                    mRedirectUrl = AppConstant.DEFAULT_URL + "advancedgroups/member/removeadmin/"
                            + mBrowseListItems.getmListItemId();
                    mDialogueMessage = mContext.getResources().getString(R.string.remove_group_admin_dialogue_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.remove_group_admin_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.remove_group_admin_dialogue_button);
                    mSuccessMessage = mContext.getResources().getString(R.string.remove_group_admin_dialogue_success_message);
                }
                performAction();
                break;

            case "make_officer":
            case "demote_officer":
                if (mBrowseListItems.getmIsGroupAdmin() == 0) {
                    mRedirectUrl = AppConstant.DEFAULT_URL + "groups/member/promote/"
                            + mBrowseListItems.getmListItemId();
                    mDialogueMessage = mContext.getResources().getString(R.string.make_officer_dialogue_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.make_officer_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.make_officer_dialogue_button);
                    mSuccessMessage = mContext.getResources().getString(R.string.make_officer_dialogue_success_message);
                } else {
                    mRedirectUrl = AppConstant.DEFAULT_URL + "groups/member/demote/"
                            + mBrowseListItems.getmListItemId();
                    mDialogueMessage = mContext.getResources().getString(R.string.demote_officer_dialogue_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.demote_officer_dialogue_title);
                    mDialogueButton = mContext.getResources().getString(R.string.demote_officer_dialogue_button);
                    mSuccessMessage = mContext.getResources().getString(R.string.demote_officer_dialogue_success_message);
                }
                performAction();
                break;

            case "approved_member":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.approved_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.approved_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.approved_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.approved_member_dialogue_success_message);
                        break;
                    case ConstantVariables.EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_approved_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_approved_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_approved_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_approved_member_dialogue_success_message);
                        break;
                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_approved_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_approved_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_approved_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_approved_member_dialogue_success_message);
                        break;
                }
                performAction();
                break;

            case "reject_member":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.reject_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.reject_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.reject_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.reject_member_dialogue_success_message);
                        break;
                    case ConstantVariables.EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_reject_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_reject_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_reject_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_reject_member_dialogue_success_message);
                        break;
                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_reject_member_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_reject_member_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_reject_member_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_reject_member_dialogue_success_message);
                        break;
                }
                performAction();
                break;

            case "request_invite":
            case "cancel_invite":
            case "cancel":
                switch (mCurrentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                        if (!isRequestFromViewPage) {
                            mDialogueMessage = mContext.getResources().getString(R.string.cancel_invite_dialogue_message);
                            mDialogueTitle = mContext.getResources().getString(R.string.cancel_invite_dialogue_title);
                            mDialogueButton = mContext.getResources().getString(R.string.cancel_invite_dialogue_button);
                            mSuccessMessage = mContext.getResources().getString(R.string.cancel_invite_dialogue_success_message);
                        } else {
                            if (mBrowseListItems.getmIsRequestInvite() == 0) {
                                mRedirectUrl = mRedirectUrl.replace("cancel/", "request/");
                                mDialogueMessage = mContext.getResources().getString(R.string.group_request_member_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.group_request_member_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.group_request_member_dialogue_button);
                                mSuccessMessage = mContext.getResources().getString(R.string.group_request_member_dialogue_success_message);
                            } else {
                                mRedirectUrl = mRedirectUrl.replace("request/", "cancel/");
                                mDialogueMessage = mContext.getResources().getString(R.string.group_cancel_request_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.group_cancel_request_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.group_cancel_request_dialogue_button);
                                mSuccessMessage = mContext.getResources().getString(R.string.group_cancel_request_dialogue_success_message);
                            }
                        }
                        break;
                    case ConstantVariables.EVENT_MENU_TITLE:
                        mDialogueMessage = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_message);
                        mDialogueTitle = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_title);
                        mDialogueButton = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_button);
                        mSuccessMessage = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_success_message);
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        if (isRequestFromViewPage) {
                            if (AdvEventsProfilePage.sMembershipRequestCode
                                    == ConstantVariables.REQUEST_INVITE) {
                                mRedirectUrl = mRedirectUrl.replace("cancel/", "request/");
                                mDialogueMessage = mContext.getResources().getString(R.string.event_request_member_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.event_request_member_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.event_request_member_dialogue_button);
                                mSuccessMessage = mContext.getResources().getString(R.string.event_request_member_dialogue_success_message);
                            } else if (AdvEventsProfilePage.sMembershipRequestCode
                                    == ConstantVariables.CANCEL_INVITE) {
                                mRedirectUrl = mRedirectUrl.replace("request/", "cancel/");
                                mDialogueMessage = mContext.getResources().getString(R.string.event_cancel_request_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.event_cancel_request_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.event_cancel_request_dialogue_button);
                                mSuccessMessage = mContext.getResources().getString(R.string.event_cancel_request_dialogue_success_message);
                            }
                        } else {
                            if (currentList != null && currentList.equals("occurrence_siteevent")) {
                                if (mBrowseListItems.getmIsRequestInvite() == 1) {
                                    mRedirectUrl = mRedirectUrl.replace("cancel/", "request/");
                                    mDialogueMessage = mContext.getString(R.string.event_occurrence_request_member_dialogue_message);
                                    mDialogueTitle = mContext.getString(R.string.event_occurrence_request_member_dialogue_title);
                                    mDialogueButton = mContext.getString(R.string.event_occurrence_request_member_dialogue_button);
                                    mSuccessMessage = mContext.getString(R.string.event_occurrence_request_member_dialogue_success_message);
                                } else {
                                    mRedirectUrl = mRedirectUrl.replace("request/", "cancel/");
                                    mDialogueMessage = mContext.getString(R.string.event_occurrence_cancel_request_dialogue_message);
                                    mDialogueTitle = mContext.getString(R.string.event_occurrence_cancel_request_dialogue_title);
                                    mDialogueButton = mContext.getString(R.string.event_occurrence_cancel_request_dialogue_button);
                                    mSuccessMessage = mContext.getString(R.string.event_occurrence_cancel_request_dialogue_success_message);
                                }
                            } else {
                                mDialogueMessage = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_message);
                                mDialogueTitle = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_title);
                                mDialogueButton = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_button);
                                mSuccessMessage = mContext.getResources().getString(R.string.event_cancel_invite_dialogue_success_message);
                                break;
                            }
                        }
                }
                performAction();
                break;

            case "accept_invite":
                mDialogueMessage = mContext.getResources().getString(R.string.event_accept_request_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.event_accept_request_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.event_accept_request_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.event_accept_request_dialogue_success_message);
                performActionWithMultipleOptions(true);
                break;

            case "ignore_invite":
                mDialogueMessage = mContext.getResources().getString(R.string.event_reject_request_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.event_reject_request_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.event_reject_request_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.event_reject_request_dialogue_success_message);
                performAction();
                break;

            case "remove_cover_photo":
                mDialogueMessage = mContext.getResources().getString(R.string.remove_cover_photo_message);
                mDialogueTitle = mContext.getResources().getString(R.string.remove_cover_photo_title);
                mDialogueButton = mContext.getResources().getString(R.string.remove_member_dialogue_button);
                mSuccessMessage = mContext.getResources().getString(R.string.remove_cover_photo_success_message);
                performAction();
                break;

            case "remove_photo":
                mDialogueMessage = mContext.getResources().getString(R.string.remove_user_profile_photo_message);
                mDialogueTitle = mContext.getResources().getString(R.string.remove_user_profile_photo_title);
                mDialogueButton = mContext.getResources().getString(R.string.remove_user_profile_photo_title);
                mSuccessMessage = mContext.getResources().getString(R.string.profile_photo_deleted);
                performAction();
                break;

            case "user_profile_block":
                mDialogueMessage = mContext.getResources().getString(R.string.block_member_message);
                mDialogueTitle = mContext.getResources().getString(R.string.block_member_title);
                mDialogueButton = mContext.getResources().getString(R.string.block_member_button);
                mSuccessMessage = mContext.getResources().getString(R.string.block_member_success_message);
                performAction();
                break;

            case "user_profile_unblock":
                mDialogueMessage = mContext.getResources().getString(R.string.unblock_member_message);
                mDialogueTitle = mContext.getResources().getString(R.string.unblock_member_title);
                mDialogueButton = mContext.getResources().getString(R.string.unblock_member_button);
                mSuccessMessage = mContext.getResources().getString(R.string.unblock_member_success_message);
                performAction();
                break;

            case "add_friend":
            case "member_follow":
                mDialogueMessage = mContext.getResources().getString(R.string.add_friend_message);
                mDialogueTitle = mContext.getResources().getString(R.string.add_friend_title);
                mDialogueButton = mContext.getResources().getString(R.string.add_friend_button);
                mSuccessMessage = mContext.getResources().getString(R.string.add_friend_success_message);
                performAction();
                break;

            case "remove_friend":
            case "member_unfollow":
                mDialogueMessage = mContext.getResources().getString(R.string.remove_friend_message);
                mDialogueTitle = mContext.getResources().getString(R.string.remove_friend_title);
                mDialogueButton = mContext.getResources().getString(R.string.remove_friend_button);
                mSuccessMessage = mContext.getResources().getString(R.string.remove_friend_success_message);
                performAction();
                break;

            case "friendship_type":
                if (mBrowseListItems.getmFriendShipType() != null &&
                        !mBrowseListItems.getmFriendShipType().isEmpty()) {
                    setPopUpForFriendShipType(mPosition, mBrowseListItems, null, mCurrentSelectedModule);
                }
                break;

            case "favourite":
                mPostParams.put("value", String.valueOf(mBrowseListItems.getIsFavouriteOption()));
            case "update_save_feed":
            case "lock_this_feed":
            case "disable_comment":
            case "on_off_notification":
            case "hide":
            case "report_feed":
            case "make_profile_photo":
            case "unpin_post":
                performActionWithoutDialog();
                break;

            case "pin_post":
                performActionOnPinPost();
                break;

            case "feed_link":
                if (mFeedList.getFeedLink() != null && !mFeedList.getFeedLink().isEmpty()) {
                    Log.d("Feed Link", "Feed link: " + mFeedList.getFeedLink());
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Feed Link", mFeedList.getFeedLink());
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.feed_link_copied),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case "copy_link":
                if (mRedirectUrl != null && !mRedirectUrl.isEmpty()) {
                    Log.d("Feed Link", "Copy link: " + mRedirectUrl);
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Event Link", mRedirectUrl);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.feed_link_copied),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case "delete_feed":
                mDialogueMessage = mContext.getResources().getString(R.string.feed_delete_dialogue_message);
                mDialogueTitle = mContext.getResources().getString(R.string.feed_delete_dialogue_title);
                mDialogueButton = mContext.getResources().getString(R.string.feed_delete_dialogue_button);
                performAction();
                break;

            case "edit_feed":
                mFeedList.setmActionId(mUrlParams != null ? mUrlParams.optInt("action_id") : 0);
                mFeedList.setmMenuUrl(mRedirectUrl);
                if (mOnMenuClickResponseListener != null) {
                    mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mFeedList, mMenuName);
                }
                break;

            case "view_album":
                if (mOnOptionItemClickResponseListener != null) {
                    mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                }
                break;

            case "book_now":
                if (mOnOptionItemClickResponseListener != null) {
                    mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                }
                break;

            case "download":
                if (mOnOptionItemClickResponseListener != null) {
                    mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                }
                break;

            case "apply-now":
                if (mBrowseListItems.getIsApplyJob() == 1) {
                    SnackbarUtils.displaySnackbarLongTime(mMainView, mContext.getResources().getString(R.string.job_already_applied_msg));
                } else {
                    intent = new Intent(mContext, EditEntry.class);
                    intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                    intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                    intent.putExtra(ConstantVariables.FORM_TYPE, "apply_now");
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case "very_user":
                mSuccessMessage = mContext.getResources().getString(R.string.verify_user_success_message)
                        + " " + mBrowseListItems.getDisplayName();
                performActionOnVerificationRequests((mMenuJsonObject.optInt("is_comment") == 1), null);
                break;

            case "edit_verification":
                mSuccessMessage = mContext.getResources().getString(R.string.verify_user_success_message)
                        + " " + mBrowseListItems.getDisplayName();
                performActionOnVerificationRequests(true, mMenuJsonObject.optString("comments"));
                break;

            case "cancel_verification":
                mSuccessMessage = mContext.getResources().getString(R.string.verification_cancel_success);
                performActionOnVerificationRequests(false, null);
                break;

            case "view_details":
                Bundle viewDetailsBundle = new Bundle();
                viewDetailsBundle.putString(ConstantVariables.TITLE, mBrowseListItems.getDisplayName() + " "
                        + mContext.getResources().getString(R.string.verified_by) + ":");
                viewDetailsBundle.putString(ConstantVariables.URL_STRING, mRedirectUrl);
                viewDetailsBundle.putString(ConstantVariables.USER_TITLE, mBrowseListItems.getDisplayName());
                viewDetailsBundle.putBoolean("is_admin_approval_required", mBrowseListItems.isAdminApprovalRequired());
                viewDetailsBundle.putBoolean("admin_approve", mBrowseListItems.isAdminApproved());
                MemberDetailsDialog memberDetailsDialog = new MemberDetailsDialog();
                memberDetailsDialog.setArguments(viewDetailsBundle);
                memberDetailsDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "list");
                break;

            case "setting":
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.CONTENT_TITLE, mMenuJsonObject.optString("label"));
                intent.putExtra(ConstantVariables.FORM_TYPE, "story_setting");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
                ((Activity)mContext).startActivityForResult(intent, ConstantVariables.PAGE_EDIT_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case ConstantVariables.EDIT_STORE:
            case ConstantVariables.EDIT_METHOD:
            case ConstantVariables.EDIT_PRODUCT:
            case ConstantVariables.OVERVIEW_KEY:
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, mMenuName);
                if (mCallingFragment != null){
                    mCallingFragment.startActivityForResult(intent, ConstantVariables.EDIT_ENTRY_RETURN_CODE);
                } else {
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.EDIT_ENTRY_RETURN_CODE);
                }
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case ConstantVariables.SHIPPING_METHOD:
                intent = new Intent(mContext, ShippingMethods.class);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.CONTENT_ID, mBrowseListItems.getmContentId());
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "enable_method":
            case "disable_method":
                mPostParams.clear();
                if (mBrowseListItems.getmClosed() == 1) {
                    mDialogueMessage = mContext.getResources().getString(R.string.disable_shipping_alert_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.disable_shipping);
                    mDialogueButton = mContext.getResources().getString(R.string.yes_label);
                    mSuccessMessage = mContext.getResources().getString(R.string.disable_shipping_success_message);
                } else {
                    mDialogueMessage = mContext.getResources().getString(R.string.enable_shipping_alert_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.enable_shipping);
                    mDialogueButton = mContext.getResources().getString(R.string.yes_label);
                    mSuccessMessage = mContext.getResources().getString(R.string.enable_shipping_success_message);
                }
                performAction();
                break;
            case ConstantVariables.ADD_PRODUCT:
                intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.ADD_PRODUCT);
                intent.putExtra(ConstantVariables.CONTENT_ID, mBrowseListItems.getmContentId());
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "edit_file":
                intent = new Intent(mContext, EditEntry.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PRODUCT_MENU_TITLE);
                intent.putExtra(ConstantVariables.URL_STRING, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, mMenuName);
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.EDIT_ENTRY_RETURN_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "enable_file":
                mPostParams.clear();
                if (mBrowseListItems.getmClosed() == 1) {
                    mDialogueMessage = mContext.getResources().getString(R.string.disable_file_alert_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.disable_file);
                    mDialogueButton = mContext.getResources().getString(R.string.yes_label);
                    mSuccessMessage = mContext.getResources().getString(R.string.disable_file_success_message);
                } else {
                    mDialogueMessage = mContext.getResources().getString(R.string.enable_file_alert_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.enable_file);
                    mDialogueButton = mContext.getResources().getString(R.string.yes_label);
                    mSuccessMessage = mContext.getResources().getString(R.string.enable_file_success_message);
                }
                performAction();
                break;

            case "save":
            case "save_video":
                if (mOnMenuClickResponseListener != null) {
                    mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mBrowseListItems, mMenuName);
                }
                break;
            case "configure_product":
                intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("url", mMenuJsonObject.optString("webUrl"));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "add_to_list":
                BrowseMemberAdapter.sFriendId = mBrowseListItems.getUserId();
                String addListUrl = AppConstant.DEFAULT_URL + "user/list-add?friend_id=" +
                        mBrowseListItems.getUserId();
                Intent addToList = new Intent(mContext, CreateNewEntry.class);
                addToList.putExtra("friend_id", mBrowseListItems.getUserId());
                addToList.putExtra(ConstantVariables.CREATE_URL, addListUrl);
                addToList.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "add_to_friend_list");
                ((Activity)mContext).startActivityForResult(addToList, ConstantVariables.CREATE_REQUEST_CODE);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "follow_unfollow_user":
                if(mBrowseListItems.isFollowingMember()) {
                    mDialogueMessage = mContext.getResources().getString(R.string.unfollow_member_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.unfollow_member_title);
                    mDialogueButton = mContext.getResources().getString(R.string.unfollow);
                    mSuccessMessage = mContext.getResources().getString(R.string.unfollow_member_success_message);
                } else {
                    mDialogueMessage = mContext.getResources().getString(R.string.follow_member_message);
                    mDialogueTitle = mContext.getResources().getString(R.string.follow_member_title);
                    mDialogueButton = mContext.getResources().getString(R.string.follow);
                    mSuccessMessage = mContext.getResources().getString(R.string.follow_member_success_message);
                }
                performAction();
                break;
        }

    }


    /**
     * Method to set pop for friendship type.
     *
     * @param clickedPosition       position of clicked member.
     * @param browseListItems       Browse List items.
     * @param commentList           Comment list items.
     * @param currentSelectedModule CurrentSelectedModule.
     */
    public void setPopUpForFriendShipType(int clickedPosition, BrowseListItems browseListItems,
                                          CommentList commentList, String currentSelectedModule) {

        mPosition = clickedPosition;
        mCurrentSelectedModule = currentSelectedModule;
        String friendshipType;
        int isFriendshipVerified = 0;
        if (browseListItems != null) {
            mBrowseListItems = browseListItems;
            friendshipType = browseListItems.getmFriendShipType();
            isFriendshipVerified = browseListItems.getIsFriendshipVerified();
            mPostParams.put("user_id", String.valueOf(browseListItems.getmUserId()));

        } else {
            mCommentList = commentList;
            friendshipType = commentList.getmFriendshipType();
            isFriendshipVerified = commentList.getIsFriendshipVerified();
            mPostParams.put("user_id", String.valueOf(commentList.getmUserId()));

        }
        switch (friendshipType) {
            case "add_friend":
                mDialogueMessage = mContext.getResources().getString(R.string.add_friend_message);
                mDialogueTitle = mContext.getResources().getString(R.string.add_friend_title);
                mDialogueButton = mContext.getResources().getString(R.string.add_friend_button);
                mSuccessMessage = mContext.getResources().getString(R.string.add_friend_success_message);
                mFriendShipUrl = "user/add";
                if (isFriendshipVerified == 1) {
                    mChangedFriendShipType = "cancel_request";
                } else {
                    mChangedFriendShipType = "remove_friend";
                }
                break;

            case "member_follow":
                mDialogueMessage = mContext.getResources().getString(R.string.add_friend_message);
                mDialogueTitle = mContext.getResources().getString(R.string.add_friend_title);
                mDialogueButton = mContext.getResources().getString(R.string.add_friend_button);
                mSuccessMessage = mContext.getResources().getString(R.string.add_friend_success_message);
                mFriendShipUrl = "user/add";
                if (isFriendshipVerified == 1) {
                    mChangedFriendShipType = "cancel_follow";
                } else {
                    mChangedFriendShipType = "member_unfollow";
                }
                break;

            case "accept_request":
                mDialogueMessage = mContext.getResources().getString(R.string.accept_friend_request_message);
                mDialogueTitle = mContext.getResources().getString(R.string.accept_friend_request_title);
                mDialogueButton = mContext.getResources().getString(R.string.accept_friend_request_button);
                mSuccessMessage = mContext.getResources().getString(R.string.accept_friend_request_success_message);
                mChangedFriendShipType = "remove_friend";
                mFriendShipUrl = "user/confirm";
                break;

            case "remove_friend":
            case "member_unfollow":
                mDialogueMessage = mContext.getResources().getString(R.string.remove_friend_message);
                mDialogueTitle = mContext.getResources().getString(R.string.remove_friend_title);
                mDialogueButton = mContext.getResources().getString(R.string.remove_friend_button);
                mSuccessMessage = mContext.getResources().getString(R.string.remove_friend_success_message);
                mFriendShipUrl = "user/remove";
                if (friendshipType.equals("member_unfollow")) {
                    mChangedFriendShipType = "member_follow";
                } else {
                    mChangedFriendShipType = "add_friend";
                }
                break;

            case "cancel_request":
            case "cancel_follow":
                mDialogueMessage = mContext.getResources().getString(R.string.cancel_friend_request_message);
                mDialogueTitle = mContext.getResources().getString(R.string.cancel_friend_request_title);
                mDialogueButton = mContext.getResources().getString(R.string.cancel_friend_request_button);
                mSuccessMessage = mContext.getResources().getString(R.string.cancel_friend_request_success_message);
                mFriendShipUrl = "user/cancel";
                if (friendshipType.equals("cancel_follow")) {
                    mChangedFriendShipType = "member_follow";
                } else {
                    mChangedFriendShipType = "add_friend";
                }
                break;
        }

        mRedirectUrl = AppConstant.DEFAULT_URL + mFriendShipUrl;
        mMenuName = "friendship_type";
        performAction();
    }

    public void performActionOnVerificationRequests(final boolean isCommentBox, final String comment) {
        try {

            mAlertDialogWithAction.showCustomDialog(mMenuName, mBrowseListItems.getDisplayName(), new AlertDialogWithAction.OnCustomDialogListener() {
                @Override
                public void onCustomDialogCreated(View inflatedView, AlertDialog alertDialog) {
                    inflatedView.findViewById(R.id.action_view).setVisibility(View.VISIBLE);
                    inflatedView.findViewById(R.id.et_bottom_line).setVisibility(View.VISIBLE);
                    inflatedView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                    if (isCommentBox) {
                        inflatedView.findViewById(R.id.edit_text_layout).setVisibility(View.VISIBLE);
                        inflatedView.findViewById(R.id.tv_label).setVisibility(View.GONE);
                        etValue = (EditText) inflatedView.findViewById(R.id.et_value);
                        final TextView tvCharLeft = (TextView) inflatedView.findViewById(R.id.et_char_left);
                        tvCharLeft.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) etValue.getLayoutParams();
                        layoutParams.weight = 2;
                        etValue.setLayoutParams(layoutParams);
                        etValue.requestFocus();
                        etValue.setSingleLine(false);
                        etValue.setMaxLines(Integer.MAX_VALUE);
                        etValue.setHint(mContext.getResources().getString(R.string.title_activity_comment));
                        if (comment != null && !comment.isEmpty()) {
                            etValue.setText(comment);
                            etValue.setSelection(etValue.getText().length());
                        }
                        tvCharLeft.setText((300 - (etValue.getText() != null ? etValue.getText().length() : 0))
                                + " " + mContext.getResources().getString(R.string.char_left));

                        etValue.setFilters(new InputFilter[] {
                                new InputFilter.LengthFilter(300) {
                                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                                        return super.filter(source, start, end, dest, dstart, dend);
                                    }
                                }
                        });
                        etValue.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s != null) {
                                    tvCharLeft.setText((300 - s.length()) + " "
                                            + mContext.getResources().getString(R.string.char_left));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    } else {
                        inflatedView.findViewById(R.id.top_line).setVisibility(View.GONE);
                        inflatedView.findViewById(R.id.description).setVisibility(View.GONE);
                    }

                }

                @Override
                public void onActionPerform(final AlertDialog alertDialog) {
                    if (etValue != null) {
                        mPostParams.put("comments", etValue.getText().toString());
                        mAppConst.hideKeyboardInDialog(etValue);
                    }
                    mAppConst.showProgressDialog();

                    mAppConst.postJsonResponseForUrl(mRedirectUrl, mPostParams, new OnResponseListener() {

                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {
                            mAppConst.hideProgressDialog();
                            if (mSuccessMessage != null) {
                                Toast.makeText(mContext, mSuccessMessage, Toast.LENGTH_SHORT).show();
                            }
                            if (mOnOptionItemClickResponseListener != null) {
                                mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                            }
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            mAppConst.hideProgressDialog();
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCanceled(AlertDialog alertDialog) {
                    if (etValue != null) {
                        etValue.setError(null);
                    }
                    alertDialog.hide();
                }

                @Override
                public void onClear() {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void performActionOnPinPost() {
        try {

            mAlertDialogWithAction.showCustomDialog("pin_post", mFeedList.getPinPostDuration(), new AlertDialogWithAction.OnCustomDialogListener() {
                @Override
                public void onCustomDialogCreated(View inflatedView, AlertDialog alertDialog) {
                    inflatedView.findViewById(R.id.action_view).setVisibility(View.VISIBLE);
                    inflatedView.findViewById(R.id.edit_text_layout).setVisibility(View.VISIBLE);
                    inflatedView.findViewById(R.id.et_bottom_line).setVisibility(View.VISIBLE);
                    inflatedView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    TextView tvLabel = (TextView) inflatedView.findViewById(R.id.tv_label);
                    etValue = (EditText) inflatedView.findViewById(R.id.et_value);
                    tvLabel.setText(mContext.getResources().getString(R.string.pin_feed));
                    etValue.setHint(mContext.getResources().getString(R.string.enter_pin_post_time));
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etValue.requestFocus();

                }

                @Override
                public void onActionPerform(final AlertDialog alertDialog) {
                    mAppConst.hideKeyboardInDialog(etValue);
                    mAppConst.showProgressDialog();
                    etValue.setError(null);

                    String pinPostTime = etValue.getText().toString();

                    if (pinPostTime.length() > 0 && !pinPostTime.trim().isEmpty()) {
                        mPostParams.put("time", pinPostTime);

                        mAppConst.postJsonResponseForUrl(mRedirectUrl, mPostParams, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {
                                alertDialog.dismiss();
                                mAppConst.hideProgressDialog();
                                mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mFeedList, mMenuName);
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                mAppConst.hideProgressDialog();
                                if (GlobalFunctions.isValidJson(message)) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(message);
                                        message = jsonObject.optString(jsonObject.keys() != null && jsonObject.keys().next() != null
                                                && !jsonObject.keys().next().isEmpty() ? jsonObject.keys().next() : message);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                /* Show Message */
                                etValue.setError(message);
                            }
                        });

                    } else {
                        mAppConst.hideProgressDialog();
                        etValue.setError(mContext.getResources().getString(R.string.pin_empty_error_message)
                                + " " + mFeedList.getPinPostDuration());
                    }
                }

                @Override
                public void onCanceled(AlertDialog alertDialog) {
                    if (etValue != null) {
                        etValue.setError(null);
                    }
                    alertDialog.hide();
                }

                @Override
                public void onClear() {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to perform action by showing an alert dialog with ok/cancel button.
     */
    public void performAction() {
        isNeedToDismiss = false;
        try {

            if (mMainView == null) {
                mMainView = ((AppCompatActivity) mContext).getCurrentFocus();
            }

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
            alertBuilder.setMessage(mDialogueMessage);
            alertBuilder.setTitle(mDialogueTitle);

            // Show search checkbox in dialog box when publish Group Menu gets clicked
            if (mMenuName.equals("publish")) {

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View checkBoxView = inflater.inflate(R.layout.checkbox, null, false);
                CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mPostParams.put("search", String.valueOf(isChecked ? 1 : 0));
                    }
                });
                alertBuilder.setView(checkBoxView);
            }


            alertBuilder.setPositiveButton(mDialogueButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mAppConst.showProgressDialog();

                    switch (mMenuName) {

                        case "delete":
                        case "delete_album":
                        case "delete_event":
                        case "delete_poll":
                        case "remove_admin":
                        case "remove_member":
                        case "delete_feed":
                        case "ticket_delete":
                        case "delete_method":
                        case "delete_file":
                        case "delete_product":
                            isActionPerformed = true;
                            mAppConst.deleteResponseForUrl(mRedirectUrl, mPostParams, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    mAppConst.hideProgressDialog();
                                    isActionPerformed = false;
                                    isNeedToDismiss = true;
                                    if (mOnMenuClickResponseListener != null) {
                                        if (mSuccessMessage != null && !mSuccessMessage.isEmpty()) {
                                            SnackbarUtils.displaySnackbar(mMainView, mSuccessMessage);
                                        }
                                        mOnMenuClickResponseListener.onItemDelete(mPosition);
                                    }
                                    if (mOnOptionItemClickResponseListener != null) {
                                        mOnOptionItemClickResponseListener.onItemDelete(mSuccessMessage);
                                    }
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mAppConst.hideProgressDialog();
                                    isActionPerformed = false;
                                    isNeedToDismiss = true;
                                    SnackbarUtils.displaySnackbar(mMainView, message);
                                }
                            });
                            break;

                        default:
                            mAppConst.postJsonResponseForUrl(mRedirectUrl, mPostParams, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(((Activity)mContext).findViewById(android.R.id.content), mSuccessMessage);

                                    switch (mMenuName) {

                                        case "close_poll":
                                        case "open_poll":
                                        case "close":
                                        case "open":
                                        case "enable_method":
                                        case "disable_method":
                                        case "enable_file":
                                            mBrowseListItems.setmClosed(mBrowseListItems.getmClosed() == 1 ? 0 : 1);
                                            break;

                                        case "watch_topic":
                                        case "stop_watch_topic":
                                            mBrowseListItems.setmWatched(mBrowseListItems.getmWatched() == 1 ? 0 : 1);
                                            break;

                                        case "make_sticky":
                                        case "remove_sticky":
                                            mBrowseListItems.setmSticky(mBrowseListItems.getmSticky() == 1 ? 0 : 1);
                                            break;

                                        case "unsubscribe":
                                        case "subscribe":
                                            mBrowseListItems.setmSubscribed(mBrowseListItems.getmSubscribed() == 1
                                                    ? 0 : 1);
                                            break;

                                        case "leave":
                                        case "join":
                                            switch (mCurrentSelectedModule) {
                                                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                                                    mBrowseListItems.setmShowAddPeople(!mBrowseListItems.
                                                            ismShowAddPeople());
                                                case ConstantVariables.GROUP_MENU_TITLE:
                                                case ConstantVariables.EVENT_MENU_TITLE:
                                                    mBrowseListItems.setmJoined(mBrowseListItems.getmJoined() == 1
                                                            ? 0 : 1);
                                                    break;
                                            }

                                            break;

                                        case "request_invite":
                                        case "cancel_invite":
                                        case "cancel":
                                        case "request_member":
                                        case "cancel_request":
                                            mBrowseListItems.setmIsRequestInvite(mBrowseListItems.getmIsRequestInvite()
                                                    == 1 ? 0 : 1);
                                            break;

                                        case "follow_unfollow_user":
                                            mBrowseListItems.setFollowingMember(!mBrowseListItems.isFollowingMember());
                                            break;

                                        case "friendship_type":
                                            if (mCommentList != null) {
                                                mCommentList.setmFriendshipType(mChangedFriendShipType);
                                            } else {
                                                mBrowseListItems.setmFriendShipType(mChangedFriendShipType);
                                            }
                                            break;

                                        case "publish":
                                            switch (mCurrentSelectedModule) {
                                                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                                                    mBrowseListItems.setmGroupPublished(!mBrowseListItems.
                                                            ismGroupPublished());
                                                    break;

                                                default:
                                                    mBrowseListItems.setmPublished(mBrowseListItems.getmPublished()
                                                            == 1 ? 0 : 1);
                                                    break;
                                            }
                                            break;

                                        case "make_admin":
                                        case "remove_admin":
                                        case "make_officer":
                                        case "demote_officer":
                                            mBrowseListItems.setmIsGroupAdmin(mBrowseListItems.getmIsGroupAdmin()
                                                    == 1 ? 0 : 1);
                                            break;
                                    }

                                    if (mOnMenuClickResponseListener != null) {

                                        if (mMenuName.equals("upgrade_package")) {
                                            SnackbarUtils.displaySnackbarLongWithListener(mMainView, mSuccessMessage,
                                                    new SnackbarUtils.OnSnackbarDismissListener() {
                                                        @Override
                                                        public void onSnackbarDismissed() {
                                                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
                                                                    mBrowseListItems, mMenuName);
                                                        }
                                                    });
                                        } else if (mCommentList != null) {
                                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
                                                    mCommentList, mMenuName);

                                        } else if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE) &&
                                                (mMenuName.equals("leave") || mMenuName.equals("join"))) {
                                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
                                                    updateItemInfo(jsonObject, mBrowseListItems), mMenuName);
                                        } else {
                                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
                                                    mBrowseListItems, mMenuName);
                                        }
                                    }

                                    if (mOnOptionItemClickResponseListener != null) {
                                        mOnOptionItemClickResponseListener.onOptionItemActionSuccess(
                                                mBrowseListItems, mMenuName);
                                    }
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(((Activity)mContext).findViewById(android.R.id.content), message);
                                }
                            });
                            break;
                    }
                }
            });

            alertBuilder.setNegativeButton(mContext.getResources().
                            getString(R.string.delete_account_cancel_button_text),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mOnPopUpDismissListener != null && !isActionPerformed) {
                        isNeedToDismiss = true;
                        mOnPopUpDismissListener.onPopUpDismiss(true);
                    }
                }
            });
            alertBuilder.create().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    /**
     * Method to perform action with multiple options.
     *
     * @param isRequestFromViewPage true if called from view page request.
     */
    public void performActionWithMultipleOptions(final boolean isRequestFromViewPage) {

        final CharSequence[] items = new CharSequence[3];
        items[0] = AdvEventsProfilePage.sAttending = mContext.getResources().getString(R.string.rsvp_filter_attending);
        items[1] = AdvEventsProfilePage.sMayBeAttending = mContext.getResources().getString(R.string.rsvp_filter_may_be_attending);
        items[2] = AdvEventsProfilePage.sNotAttending = mContext.getResources().getString(R.string.rsvp_filter_not_attending);
        if (isRequestFromViewPage &&
                mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
            AdvEventsProfilePage.sSelectedRsvpValue = 0;
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setTitle(mDialogueTitle);

        alertBuilder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (items[which] == AdvEventsProfilePage.sNotAttending) {
                    mSelectedRsvpValue = 0;
                } else if (items[which] == AdvEventsProfilePage.sMayBeAttending) {
                    mSelectedRsvpValue = 1;
                } else if (items[which] == AdvEventsProfilePage.sAttending) {
                    mSelectedRsvpValue = 2;
                }
            }
        });


        alertBuilder.setPositiveButton(mDialogueButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Map<String, String> rsvpValues = new HashMap<>();
                if (isRequestFromViewPage &&
                        mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
                    AdvEventsProfilePage.sSelectedRsvpValue = mSelectedRsvpValue;
                }

                rsvpValues.put("rsvp", String.valueOf(mSelectedRsvpValue));
                if (mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE) &&
                        !isRequestFromViewPage) {
                    rsvpValues.put("getJoinInfo", "1");
                }
                mAppConst.showProgressDialog();

                mAppConst.postJsonResponseForUrl(mRedirectUrl, rsvpValues, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {

                        mAppConst.hideProgressDialog();

                        /* Show Message */
                        Toast.makeText(mContext, mSuccessMessage, Toast.LENGTH_LONG).show();
                        if (mOnMenuClickResponseListener != null) {
                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
                                    updateItemInfo(jsonObject, mBrowseListItems), mMenuName);
                        }

                        if (mOnOptionItemClickResponseListener != null) {

                            if (mCurrentSelectedModule.equals(ConstantVariables.EVENT_MENU_TITLE) &&
                                    (mMenuName.equals("join") || mMenuName.equals("leave"))) {
                                if (mBrowseListItems.getmJoined() == 0) {
                                    mBrowseListItems.setmJoined(1);
                                    mBrowseListItems.setmProfileRsvpValue(mSelectedRsvpValue);
                                } else {
                                    mBrowseListItems.setmJoined(0);
                                }
                            }
                            mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems,
                                    mMenuName);
                        }
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        mAppConst.hideProgressDialog();
                    }
                });
            }
        });

        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }


    /**
     * Method to perform action without showing any alert dialog.
     */
    public void performActionWithoutDialog() {

        if (mMainView == null) {
            mMainView = ((AppCompatActivity) mContext).getCurrentFocus();
        }

        mAppConst.showProgressDialog();
        mAppConst.postJsonResponseForUrl(mRedirectUrl, mPostParams, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                switch (mMenuName) {
                    case "like":
                        mBrowseListItems.setmLiked(mBrowseListItems.getmLiked() == 1 ? 0 : 1);
                        break;

                    case "follow":
                        mBrowseListItems.setmFollowed(mBrowseListItems.getmFollowed() == 1 ? 0 : 1);
                        break;

                    case "favourite":
                        mBrowseListItems.setIsFavouriteOption(mBrowseListItems.getIsFavouriteOption() == 1 ? 0 : 1);
                        break;

                    case "disable_comment":
                        mFeedList.setmCanComment(mFeedList.ismCanComment() == 1 ? 0 : 1);
                        break;

                    case "lock_this_feed":
                        mFeedList.setmShareAble(mFeedList.getmShareAble() == 1 ? 0 : 1);
                        break;

                    case "update_save_feed":
                        mFeedList.setmIsSaveFeedOption(mFeedList.getmIsSaveFeedOption() == 1 ? 0 : 1);
                        break;

                    case "on_off_notification":
                        mFeedList.setNotificationOn(!mFeedList.isNotificationOn());
                        break;
                }

                if (mOnMenuClickResponseListener != null) {
                    switch (mMenuName) {
                        case "hide":
                        case "report_feed":
                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
                                    updateFeedInfo(jsonObject, mFeedList), mMenuName);
                            break;

                        case "disable_comment":
                        case "lock_this_feed":
                        case "update_save_feed":
                        case "unpin_post":
                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mFeedList, mMenuName);
                            break;

                        default:
                            mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mBrowseListItems,
                                    mMenuName);
                            break;
                    }
                }

                if (mOnOptionItemClickResponseListener != null) {
                    if (mMenuName.equals("favourite")) {
                        mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
                    } else if (mMenuName.equals("make_profile_photo") && mMainView != null) {
                        mAppConst.refreshUserData();
                        SnackbarUtils.displaySnackbar(mMainView,
                                mContext.getResources().getString(R.string.profile_photo_updated));
                    }
                }
                mAppConst.hideProgressDialog();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(mMainView, message);
            }
        });
    }


    /**
     * Method to update feed data on action.
     *
     * @param jsonObject JsonObject which contains the all new data.
     * @param feedList   list of feedItem.
     * @return returns the updated feedList.
     */
    public FeedList updateFeedInfo(JSONObject jsonObject, FeedList feedList) {

        if (jsonObject != null) {

                /* SET UNDO PARAMETERS */
            JSONObject undoJsonObject = jsonObject.optJSONObject("undo");
            String hiddenLabel = undoJsonObject.optString("label");
            String undoUrl = AppConstant.DEFAULT_URL + undoJsonObject.optString("url");
            JSONObject undoUrlParams = undoJsonObject.optJSONObject("urlParams");

            if (undoUrlParams != null && undoUrlParams.length() != 0) {
                mPostParams.clear();

                JSONArray urlParamsNames = undoUrlParams.names();
                for (int j = 0; j < undoUrlParams.length(); j++) {

                    String paramName = urlParamsNames.optString(j);
                    String value = undoUrlParams.optString(paramName);
                    mPostParams.put(paramName, value);
                }
                undoUrl = mAppConst.buildQueryString(undoUrl, mPostParams);
            }
            feedList.setmUndoHiddenFeedURl(undoUrl);
            feedList.setmHiddenBodyText(hiddenLabel);
            feedList.setmUndoHiddenFeedParams((HashMap<String, String>) mPostParams);

                /* SET HIDE ALL PARAMETERS */
            JSONObject hideAllJsonObject = jsonObject.optJSONObject("hide_all");
            if (hideAllJsonObject != null && hideAllJsonObject.length() != 0) {
                String hideAllLabel = hideAllJsonObject.optString("label");
                String hideAllName = hideAllJsonObject.optString("name");
                String hideAllUrl = AppConstant.DEFAULT_URL +
                        hideAllJsonObject.optString("url");
                JSONObject hideAllUrlParams = hideAllJsonObject.optJSONObject("urlParams");

                if (hideAllUrlParams != null && hideAllUrlParams.length() != 0) {
                    mPostParams.clear();
                    JSONArray urlParamsNames = hideAllUrlParams.names();
                    for (int j = 0; j < hideAllUrlParams.length(); j++) {
                        String paramName = urlParamsNames.optString(j);
                        String value = hideAllUrlParams.optString(paramName);
                        mPostParams.put(paramName, value);
                    }
                    hideAllUrl = mAppConst.buildQueryString(hideAllUrl, mPostParams);

                }
                feedList.setmHideAllText(hideAllLabel);
                feedList.setmHideAllUrl(hideAllUrl);
                feedList.setmHideAllName(hideAllName);
            }
        }

        return feedList;
    }


    /**
     * Method to update data on action.
     *
     * @param jsonObject      JsonObject which contains the all new data.
     * @param browseListItems list of browseItem.
     * @return returns the updated browseList.
     */
    public BrowseListItems updateItemInfo(JSONObject jsonObject, BrowseListItems browseListItems) {
        JSONArray mDataResponse = jsonObject.optJSONArray("response");

        if (mDataResponse != null && mDataResponse.length() != 0) {

            for (int j = 0; j < mDataResponse.length(); j++) {
                JSONObject mDataObject = mDataResponse.optJSONObject(j);
                String startTime = mDataObject.optString("starttime");
                String endTime = mDataObject.optString("endtime");
                int occurrence_id = mDataObject.optInt("occurrence_id");
                int totalMembers = mDataObject.optInt("totalMembers");
                int rsvp = mDataObject.optInt("rsvp");
                JSONArray menuArray = mDataObject.optJSONArray("menu");
                JSONArray guestArray = mDataObject.optJSONArray("guest");
                int event_id = mDataObject.optInt("event_id");
                int isRequestInvite = mDataObject.optInt("isRequestInvite");
                int isJoined = mDataObject.optInt("isJoined");

                browseListItems.setmStartTime(startTime);
                browseListItems.setmEndTime(endTime);
                browseListItems.setmOccurrenceId(occurrence_id);
                browseListItems.setmTotalItemCount(totalMembers);
                browseListItems.setmRsvp(rsvp);
                browseListItems.setmMenuArray(menuArray);
                browseListItems.setmGuestArray(guestArray);
                browseListItems.setmEventId(event_id);
                browseListItems.setmIsRequestInvite(isRequestInvite);
                browseListItems.setmJoined(isJoined);
            }
        }
        return browseListItems;
    }

}
