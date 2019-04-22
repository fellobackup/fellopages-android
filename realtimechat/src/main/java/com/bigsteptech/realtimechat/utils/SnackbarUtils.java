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

package com.bigsteptech.realtimechat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;

public class SnackbarUtils {

    public interface OnSnackbarDismissListener {
        void onSnackbarDismissed();
    }

    public interface OnSnackbarActionClickListener {
        void onSnackbarActionClick();
    }

    /**
     * Method to show a simple snackbar for shot time with message.
     * @param view View of current class.
     * @param message Message which is displayed on snackbar.
     */
    public static void displaySnackbar(View view, String message) {

        try {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show a simple snackbar for long time with message.
     * @param view View of current class.
     * @param message Message which is displayed on snackbar.
     */
    public static void displaySnackbarLongTime(View view, String message) {

        try {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show a simple snackbar with message for short period of time with dismiss listener.
     * @param view View of current class.
     * @param message Message which is displayed on snackbar.
     */
    public static void displaySnackbarShortWithListener(View view, String message,
                                                        final OnSnackbarDismissListener onSnackbarDismissListener) {
        try {
            Snackbar.make(view, message,
                    Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    onSnackbarDismissListener.onSnackbarDismissed();
                }
            }).show();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show a simple snackbar with message for long period of time with dismiss listener.
     * @param view View of current class.
     * @param message Message which is displayed on snackbar.
     */
    public static void displaySnackbarLongWithListener(View view, String message,
                                                       final OnSnackbarDismissListener onSnackbarDismissListener) {
        try {
            Snackbar.make(view, message,
                    Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    onSnackbarDismissListener.onSnackbarDismissed();
                }
            }).show();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to show snackbar with action button.
     * @param context Context of calling class.
     * @param view View of current class on which snackbar is to be displayed.
     * @param message Message which is displayed on snackbar.
     * @param onSnackbarActionClickListener Listener for action click.
     * @return Return the snackbar with action button.
     */
//    public static Snackbar displaySnackbarWithAction(Context context, View view, String message,
//                                                     final OnSnackbarActionClickListener onSnackbarActionClickListener) {
//
//        Snackbar snackbar = null;
//        try {
//            snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
//            snackbar.setAction(context.getResources().getString(R.string.retry_option),
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            onSnackbarActionClickListener.onSnackbarActionClick();
//                        }
//                    });
//            snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
//            snackbar.show();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        return snackbar;
//    }

    /**
     * Method to show snackbar with action button.
     * @param context Context of calling class.
     * @param view View of current class on which snackbar is to be displayed.
     * @param message Message which is displayed on snackbar.
     * @param buttonTitle Title of action button.
     * @param onSnackbarActionClickListener Listener for action click.
     */
    public static void displayMultiLineSnackbarWithAction(Context context, View view,
                                                              String message, String buttonTitle,
                                                              final OnSnackbarActionClickListener onSnackbarActionClickListener) {

        try {
            final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(buttonTitle, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                    onSnackbarActionClickListener.onSnackbarActionClick();
                }
            });
            snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

            /* Set Max Lines 4 on snackbar TextView to show full text in snackbar */
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(4);
            snackbar.show();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to show snackbar on permission result.
     * @param context Context of calling class.
     * @param view Current class view.
     * @param requestCode Request code for permission by which message is determined.
     */
    public static void displaySnackbarOnPermissionResult(final Context context, View view, int requestCode) {

        String message;
        switch (requestCode) {

            case Constants.READ_EXTERNAL_STORAGE:
                message = context.getResources().getString(R.string.storage_settings_message);
                break;

            default:
                message = context.getResources().getString(R.string.camera_settings_message);
                break;
        }

        displayMultiLineSnackbarWithAction(context, view, message, context.getResources().
                getString(R.string.open_app_info), new OnSnackbarActionClickListener() {

            @Override
            public void onSnackbarActionClick() {
                final Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        });
    }

}
