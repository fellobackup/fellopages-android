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

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class EmojiUtil {

    static EmojiconsPopup mEmojiPopup;

    /**
     * Function to create a Emoji Popup
     * @param context Context of the activity
     * @param rootView View in which popup will be added
     * @param editText Reference of Edittext in which Emojis Will be shown when clicked.
     */

    public static void createEmojiPopup(Context context, View rootView, final EmojiconEditText editText){

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        mEmojiPopup = new EmojiconsPopup(rootView, context);

        //Will automatically set size according to the soft keyboard size
        mEmojiPopup.setSizeForSoftKeyboard();

        //If the text keyboard closes, also dismiss the emoji popup
        mEmojiPopup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (mEmojiPopup.isShowing())
                    mEmojiPopup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        mEmojiPopup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (editText == null || emojicon == null) {
                    return;
                }

                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();
                if (start < 0) {
                    editText.append(emojicon.getEmoji());
                } else {
                    editText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        mEmojiPopup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                editText.dispatchKeyEvent(event);
            }
        });

        // Hide the emoji popup on click of edit text
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmojiPopup.dismiss();
            }
        });

    }

    /**
     * Show Emoji Keybaord when Emoji Icon will be clicked
     * @param context Context of Activity
     * @param editText Reference of edittext in which emojis will be shown when clicked.
     */
    public static void showEmojiKeyboard(Context context, EmojiconEditText editText){
        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        //If popup is not showing => emoji keyboard is not visible, we need to show it
        if (!mEmojiPopup.isShowing()) {

            //If keyboard is visible, simply show the emoji popup
            if (mEmojiPopup.isKeyBoardOpen()) {
                mEmojiPopup.showAtBottom();
            }

            //else, open the text keyboard first and immediately after that show the emoji popup
            else {
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                mEmojiPopup.showAtBottomPending();
                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
        //If popup is showing, simply dismiss it to show the undelying text keyboard
        else {
            mEmojiPopup.dismiss();
        }

    }

    /**
     * Method to hide emoji popup.
     */
    public static void dismissEmojiPopup() {
        if (mEmojiPopup != null) {
            mEmojiPopup.dismiss();
        }
    }
}
