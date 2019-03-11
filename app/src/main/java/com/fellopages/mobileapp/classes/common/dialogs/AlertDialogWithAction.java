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

package com.fellopages.mobileapp.classes.common.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.NewLoginActivity;
import com.fellopages.mobileapp.classes.core.WelcomeScreen;
import com.fellopages.mobileapp.classes.core.startscreens.HomeScreen;
import com.fellopages.mobileapp.classes.core.startscreens.NewHomeScreen;
import com.fellopages.mobileapp.classes.modules.pushnotification.MyFcmListenerService;

public class AlertDialogWithAction {

    private Context mContext;
    private AlertDialog.Builder mDialogBuilder;

    public AlertDialogWithAction(Context context) {
        this.mContext = context;
        mDialogBuilder = new AlertDialog.Builder(mContext);
    }


    /**
     * Method to show alert dialog on respective permission result.
     * @param permissionType Permission type for which manifest permission will be requested.
     * @param requestCode Request code for respective permission.
     */
    public void showDialogForAccessPermission(final String permissionType,
                                                final int requestCode) {
        final AppConstant appConstant = new AppConstant(mContext);
        String message;
        switch (permissionType) {

            case Manifest.permission.READ_EXTERNAL_STORAGE:
                message = mContext.getResources().getString(R.string.allow_read_external_storage);
            break;

            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                message = mContext.getResources().getString(R.string.allow_location_permission);
                break;

            case Manifest.permission.CAMERA:
                message = mContext.getResources().getString(R.string.allow_camera_permission);
            break;

            case Manifest.permission.READ_CONTACTS:
                message = mContext.getResources().getString(R.string.allow_contact_permission);
                break;

            case Manifest.permission.WAKE_LOCK:
                message = mContext.getResources().getString(R.string.screen_wake_lock_permission);
                break;

            case Manifest.permission.SEND_SMS:
                message = mContext.getResources().getString(R.string.allow_sms_permission);
                break;
            
            default:
                message = mContext.getResources().getString(R.string.allow_write_external_storage);


            break;
        }

        mDialogBuilder.setMessage(message);

        mDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.date_time_dialogue_ok_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        appConstant.requestForManifestPermission(permissionType, requestCode);

                    }
                });
        mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.date_time_dialogue_cancel_button), null);

        mDialogBuilder.create().show();
    }

    /**
     * Method to show alert dialog when user open the closed content.
     * @param title Title of the dialog
     * @param message Message of the dialog.
     */
    public void showDialogForClosedContent(String title, String message) {
        mDialogBuilder.setTitle(title)
                .setMessage(message);

        mDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.date_time_dialogue_ok_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = mDialogBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;
        dialog.show();
    }

    /**
     * Method to show push notification which is generated from admin panel
     * @param headerTitle Title of push notification.
     * @param message Message content.
     */
    public void showPushNotificationAlertDialog(String headerTitle, String message) {
        MyFcmListenerService.clearPushNotification();
        mDialogBuilder.setTitle(headerTitle);
        final SpannableString spannableMessage = new SpannableString(message);
        Linkify.addLinks(spannableMessage, Linkify.WEB_URLS);
        mDialogBuilder.setMessage(spannableMessage);
        mDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Method to show alert dialog on sign up error with respective alert messages.
     * @param errorType Sign-up error type.
     */
    public void showAlertDialogForSignUpError(String errorType) {
        boolean bModeSignUp = false;

        String buttonTitle = mContext.getResources().getString(R.string.date_time_dialogue_ok_button);
        switch (errorType) {

            case "email_not_verified":
                bModeSignUp = true;
                mDialogBuilder.setMessage(mContext.getResources().getString(R.string.signup_successful_message));
                mDialogBuilder.setTitle(mContext.getResources().getString(R.string.singup_success_dialogue_title));
                buttonTitle = mContext.getResources().getString(R.string.signup_success_dialogue_positive_button);
                break;

            case "not_approved":
                mDialogBuilder.setMessage(mContext.getResources().getString(R.string.signup_admin_approval));
                break;

            default:
                mDialogBuilder.setMessage(mContext.getResources().getString(R.string.subscription_unsuccessful_message));
                break;
        }

        boolean fnlbSignUp = bModeSignUp;
        mDialogBuilder.setPositiveButton(buttonTitle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Activity activity = (Activity) mContext;

                        boolean bCreateSession = activity.getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false);
                        if (fnlbSignUp && bCreateSession) {
                            Intent data = new Intent();
                            data.putExtra(ConstantVariables.KEY_USER_CREATE_SESSION_LOGIN, false);
                            activity.setResult(ConstantVariables.CODE_USER_CREATE_SESSION, data);
                            activity.finish();
                        } else {
                            Intent loginActivity;
                            if (ConstantVariables.INTRO_SLIDE_SHOW_ENABLED == 1) {
                                loginActivity = new Intent(mContext, NewLoginActivity.class);
                            } else {
                                loginActivity = new Intent(mContext, HomeScreen.class);
                            }
                            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            activity.finish();
                            mContext.startActivity(loginActivity);
                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });

        AlertDialog dialog = mDialogBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * Method to show Subscription dialog.
     * @param message Message of the dialog.
     * @param url Url of subscription.
     */
    public void showSubscriptionDialog(String message, final String url) {

        AlertDialog alertDialog = mDialogBuilder.create();

        /**
         * Show click here text as a clickable span clicking on which will open webview activity to create events
         * based on packages.
         */
        if(url != null){
            String messageClickable = mContext.getResources().getString(R.string.click_here_text);

            SpannableString messageSpannable = new SpannableString(message);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent webViewIntent = new Intent(mContext, WebViewActivity.class);
                    webViewIntent.putExtra("url", url);
                    mContext.startActivity(webViewIntent);
                    ((Activity)mContext).finish();
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                // override updateDrawState
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false); // set to false to remove underline
                    ds.setColor(Color.BLUE);
                }
            };
            messageSpannable.setSpan(clickableSpan, message.indexOf(messageClickable),
                    message.indexOf(messageClickable) + messageClickable.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            alertDialog.setMessage(messageSpannable);
        } else {
            alertDialog.setMessage(Html.fromHtml(message));
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
                mContext.getResources().getString(R.string.change_pass_alert_dialogue_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (url != null) {
                            ((Activity) mContext).finish();
                        }
                    }
                });
        alertDialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setLinksClickable(true);
        ((TextView) alertDialog.findViewById(android.R.id.message)).setLinkTextColor(Color.BLUE);
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * Method to show Alert dialog with message and title.
     * @param headerTitle Title of Dialog.
     * @param message Message content.
     * @param buttonTitle Position Button title of dialog.
     * @param onClickListener Click listener on Positive Button
     */
    public void showAlertDialogWithAction(String headerTitle, String message, String buttonTitle,
                                          DialogInterface.OnClickListener onClickListener) {

        mDialogBuilder.setTitle(headerTitle);
        mDialogBuilder.setMessage(message);
        mDialogBuilder.setPositiveButton(buttonTitle, onClickListener);
        mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void showAlertDialogWithTwoBtnAction(String headerTitle, String message, String buttonTitleOne,
                                          DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onCancelClickListener) {

        mDialogBuilder.setTitle(headerTitle);
        mDialogBuilder.setMessage(message);
        mDialogBuilder.setPositiveButton(buttonTitleOne, onClickListener);
        mDialogBuilder.setNegativeButton("Later", onCancelClickListener);
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * Method to show Alert dialog with message.
     * @param message Message content.
     * @param buttonTitle Position Button title of dialog.
     * @param onClickListener Click listener on Positive Button
     */

    public void showAlertDialogWithOkAction(boolean isCancelVisible, String headerTitle, String message, String buttonTitle,
                                                   DialogInterface.OnClickListener onClickListener) {
        mDialogBuilder.setTitle(headerTitle);
        mDialogBuilder.setMessage(message);
        mDialogBuilder.setCancelable(false);
        mDialogBuilder.setPositiveButton(buttonTitle, onClickListener);
        if(isCancelVisible) {
            mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);
        } else {
            mDialogBuilder.setNegativeButton(null, null);
        }
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Method to show Alert dialog with message.
     * @param message Message content.
     * @param buttonTitle Position Button title of dialog.
     * @param onClickListener Click listener on Positive Button
     */
    public void showAlertDialogWithAction(String message, String buttonTitle,
                                          DialogInterface.OnClickListener onClickListener) {

        mDialogBuilder.setMessage(Html.fromHtml(message));
        mDialogBuilder.setPositiveButton(buttonTitle, onClickListener);
        mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Method to show Alert dialog with message and title.
     * @param context Context of calling class.
     * @param onPositionButtonClickListener Click listener on Positive Button
     */
    public void showPasswordDialog(final Context context, final OnPositionButtonClickListener onPositionButtonClickListener) {

        mDialogBuilder.setTitle(context.getResources().getString(R.string.private_video));
        mDialogBuilder.setMessage(context.getResources().getString(R.string.password_protected_video));
        final TextInputLayout inputLayout = new TextInputLayout(context);
        inputLayout.setErrorTextAppearance(R.style.ErrorView);
        final TextInputEditText input = new TextInputEditText(context);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setHint(context.getResources().getString(R.string.lbl_enter_password));
        inputLayout.addView(input);
        mDialogBuilder.setView(inputLayout);
        mDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.access),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount=0.45f;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // used to prevent the dialog from closing when ok button is clicked (For email condition)
        Button alertDialogPositiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        alertDialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositionButtonClickListener.onButtonClick(inputLayout, alertDialog);
            }
        });
    }

    public void showCustomDialog(String formModule, int pinPostDuration, final OnCustomDialogListener onCustomDialogListener) {
        showCustomDialog(formModule, pinPostDuration, null, onCustomDialogListener);
    }

    public void showCustomDialog(String formModule, String userName, final OnCustomDialogListener onCustomDialogListener) {
        showCustomDialog(formModule, 0, userName, onCustomDialogListener);
    }

    public void showCustomDialog(String formModule, int pinPostDuration, String userName, final OnCustomDialogListener onCustomDialogListener) {
        final View inflatedView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.status_form, null);
        GradientDrawable drawable = (GradientDrawable) inflatedView.getBackground();
        drawable.setCornerRadius(30f);
        inflatedView.setBackground(drawable);
        TextView tvTitle = (TextView) inflatedView.findViewById(R.id.title);
        TextView tvDescription = (TextView) inflatedView.findViewById(R.id.description);
        TextView tvOkAction = (TextView) inflatedView.findViewById(R.id.tv_ok_action);
        inflatedView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        mDialogBuilder.setView(inflatedView);
        final AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        WindowManager.LayoutParams windowParam = new WindowManager.LayoutParams();
        windowParam.copyFrom(alertDialog.getWindow().getAttributes());
        windowParam.width = AppConstant.getDisplayMetricsWidth(mContext) - 100;
        windowParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParam.gravity = Gravity.CENTER;
        alertDialog.getWindow().setAttributes(windowParam);
        String title = null, btnTitle = null;
        switch (formModule) {
            case "pin_post":
                title = mContext.getResources().getString(R.string.set_pin_reset_time);
                btnTitle = mContext.getResources().getString(R.string.pin_title);
                tvDescription.setText(mContext.getResources().getString(R.string.pin_post_description) + " "
                        + pinPostDuration + " " + mContext.getResources().getString(R.string.unpin_automatically));
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setGravity(Gravity.CENTER_VERTICAL);
                inflatedView.findViewById(R.id.tv_clear).setVisibility(View.GONE);
                break;

            case "very_user":
                title = mContext.getResources().getString(R.string.verify_user_button) + " " + userName + " ?";
                btnTitle = mContext.getResources().getString(R.string.verify_user_button);
                tvDescription.setText(mContext.getResources().getString(R.string.verify_hint_message)
                        + " " + userName + "?");
                tvDescription.setVisibility(View.VISIBLE);
                inflatedView.findViewById(R.id.tv_clear).setVisibility(View.GONE);
                break;

            case "edit_verification":
                title = mContext.getResources().getString(R.string.edit_verification);
                btnTitle = mContext.getResources().getString(R.string.modify_user_button);
                tvDescription.setText(mContext.getResources().getString(R.string.verify_edit_message));
                tvDescription.setVisibility(View.VISIBLE);
                inflatedView.findViewById(R.id.tv_clear).setVisibility(View.GONE);
                break;

            case "cancel_verification":
                btnTitle = title = mContext.getResources().getString(R.string.cancel_verification);
                tvDescription.setText(mContext.getResources().getString(R.string.cancel_verification_message));
                tvDescription.setVisibility(View.VISIBLE);
                inflatedView.findViewById(R.id.tv_clear).setVisibility(View.GONE);
                inflatedView.findViewById(R.id.top_line).setVisibility(View.GONE);
                break;
        }
        tvTitle.setText(title);
        tvOkAction.setText(btnTitle);
        if (onCustomDialogListener != null) {
            onCustomDialogListener.onCustomDialogCreated(inflatedView, alertDialog);
        }
        tvOkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCustomDialogListener != null) {
                    onCustomDialogListener.onActionPerform(alertDialog);
                }
            }
        });

        inflatedView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCustomDialogListener != null) {
                    onCustomDialogListener.onCanceled(alertDialog);
                }
            }
        });

        inflatedView.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCustomDialogListener != null) {
                    onCustomDialogListener.onClear();
                }
            }
        });
    }

    public interface OnCustomDialogListener {
        void onCustomDialogCreated(View inflatedView, AlertDialog alertDialog);
        void onActionPerform(AlertDialog alertDialog);
        void onCanceled(AlertDialog alertDialog);
        void onClear();
    }

    public interface OnPositionButtonClickListener {
        void onButtonClick(TextInputLayout inputLayout, AlertDialog alertDialog);
    }

}
