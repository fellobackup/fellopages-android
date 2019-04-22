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

package com.fellopages.mobileapp.classes.modules.stickers;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.EmojiUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.story.photofilter.PhotoEditActivity;

import org.json.JSONObject;


public class StickersUtil {

    public static StickersPopup mStickersPopup;
    private static RelativeLayout mStickersParenView;
    private static AppConstant mAppConst;
    private static Context mContext;
    private static TextView mPhotoUploadingButton, mCommentPostButton;
    private static EditText mEditText;
    private static View mPersistentBottomSheet, mStickerView;
    private static BottomSheetBehavior mBottomSheetBehavior;
    public static boolean isKeyboardOpen = false;
    public static boolean isStorySticker = false;


    public static StickersPopup createStickerPopup(final Context context, View rootView,
                                                   RelativeLayout stickersParentView, final EditText editText,
                                                   final View persistentBottomSheet, BottomSheetBehavior bottomSheetBehavior,
                                                   View stickerView, JSONObject stickersResponse) {
        mContext = context;
        mStickersParenView = stickersParentView;
        mAppConst = new AppConstant(mContext);
        mEditText = editText;
        mPersistentBottomSheet = persistentBottomSheet;
        mBottomSheetBehavior = bottomSheetBehavior;
        mStickerView = stickerView;
        isStorySticker = false;

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        mStickersPopup = new StickersPopup(rootView, mContext, stickersResponse, stickersParentView);

        //Will automatically set size according to the soft keyboard size
        mStickersPopup.setSizeForSoftKeyboard();

        // Hide the emoji popup on click of edit text
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickersParenView.setVisibility(View.GONE);
                mStickerView.setVisibility(View.GONE);
                isKeyboardOpen = true;
                editText.requestFocus();
                EmojiUtil.dismissEmojiPopup();
                mPersistentBottomSheet.setVisibility(View.VISIBLE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if (mStickersParenView.getVisibility() == View.VISIBLE) {
                        mAppConst.hideKeyboard();
                    }
                    isKeyboardOpen = true;
                    mStickersParenView.setVisibility(View.GONE);
                    mStickerView.setVisibility(View.GONE);
                    EmojiUtil.dismissEmojiPopup();
                    mPersistentBottomSheet.setVisibility(View.VISIBLE);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    editText.requestFocus();
                }
            }
        });

        return mStickersPopup;
    }

    /**
     * Function to create a Emoji Popup
     * @param context Context of the activity
     * @param rootView View in which popup will be added
     * @param editText Reference of Edittext in which Emojis Will be shown when clicked.
     */

    public static StickersPopup createStickersPopup(Context context, View rootView, RelativeLayout stickersParentView,
                                                    final EditText editText,
                                                    JSONObject stickersResponse,
                                                    TextView photoUploadingButton, TextView commentPostButton){

        mContext = context;
        mPhotoUploadingButton = photoUploadingButton;
        mCommentPostButton = commentPostButton;
        mStickersParenView = stickersParentView;
        mAppConst = new AppConstant(mContext);
        mEditText = editText;
        isStorySticker = false;

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        mStickersPopup = new StickersPopup(rootView, mContext, stickersResponse, stickersParentView);

        //Will automatically set size according to the soft keyboard size
        mStickersPopup.setSizeForSoftKeyboard();

        // Hide the emoji popup on click of edit text
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickersParenView.setVisibility(View.GONE);
                editText.requestFocus();
                if (mCommentPostButton != null) {
                    mCommentPostButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                }
                if(mPhotoUploadingButton != null){
                    mPhotoUploadingButton.setClickable(true);
                    mPhotoUploadingButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                }
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mStickersParenView.setVisibility(View.GONE);
                    editText.requestFocus();
                    if (mCommentPostButton != null) {
                        mCommentPostButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    }
                    if(mPhotoUploadingButton != null){
                        mPhotoUploadingButton.setClickable(true);
                        mPhotoUploadingButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                    }
                }
            }
        });

        return mStickersPopup;

    }


    /**
     * Function to create a Emoji Popup for story photo filter
     * @param context Context of the activity
     * @param rootView View in which popup will be added
     */

    public static StickersPopup createStickersPopup(Context context, View rootView, RelativeLayout stickersParentView, LinearLayout stickerView,
                                                    JSONObject stickersResponse){

        mContext = context;
        mStickersParenView = stickersParentView;
        mAppConst = new AppConstant(mContext);
        mStickerView = stickerView;
        isStorySticker = true;

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        mStickersPopup = new StickersPopup(rootView, mContext, stickersResponse, stickersParentView);

        //Will automatically set size according to the soft keyboard size
        mStickersPopup.setSizeForSoftKeyboard();

        return mStickersPopup;

    }

    /**
     * Show Emoji Keybaord when Emoji Icon will be clicked
     */
    public static void showStickersKeyboard(){
        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        //If popup is not showing => sticker keyboard is not visible, we need to show it
        // Disable photo uploading button if stickers keyboard is visible

        if(mStickersParenView.getVisibility() == View.GONE){
            mAppConst.hideKeyboard();
            mEditText.clearFocus();
            mPhotoUploadingButton.setClickable(false);
            mPhotoUploadingButton.setTextColor(ContextCompat.getColor(mContext, R.color.gray_stroke_color));
            mCommentPostButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

            mStickersParenView.postDelayed( new Runnable() {
                @Override
                public void run() {
                    mStickersParenView.setVisibility(View.VISIBLE);
                }
            }, 100L);
        } else {
            mEditText.requestFocus();
            mPhotoUploadingButton.setClickable(true);
            mPhotoUploadingButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            mCommentPostButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            mStickersParenView.setVisibility(View.GONE);
        }
    }

    /**
     * Show Emoji Keybaord when Emoji Icon will be clicked
     */
    public static void showStickerView() {
        if(mStickersParenView.getVisibility() == View.GONE){
            mStickersParenView.setVisibility(View.VISIBLE);
            mStickerView.setVisibility(View.VISIBLE);
            mStickersParenView.bringToFront();
            mAppConst.hideKeyboard();
        } else {
            mStickersParenView.setVisibility(View.GONE);
            mStickerView.setVisibility(View.GONE);
        }
    }

    /**
     * Show Sticker when Sticker Icon will be clicked from story photo filter
     */
    public static void showStickerViewForFilter(boolean isVisible) {
        if(!isVisible){
            mStickersParenView.setVisibility(View.VISIBLE);
            mStickerView.setVisibility(View.VISIBLE);
            mStickersParenView.bringToFront();
            mStickerView.bringToFront();

            Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
            mStickersParenView.startAnimation(slideUp);

        } else {
            mStickersParenView.setVisibility(View.GONE);
            mStickerView.setVisibility(View.GONE);
        }
    }
}
