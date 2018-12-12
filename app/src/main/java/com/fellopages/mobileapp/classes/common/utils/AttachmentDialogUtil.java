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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.AppConstant;

import java.util.ArrayList;

public class AttachmentDialogUtil {

    private Context mContext;
    private String mAttachType;
    private EditText mEnterLinkText;
    private AlertDialog mAlertDialog;
    private ImageLoader mImageLoader;

    public AttachmentDialogUtil(Context context) {
        mContext = context;
        mImageLoader = new ImageLoader(mContext.getApplicationContext());
    }

    /**
     * Method to show image inside alert dialog.
     * @param selectPath image real path.
     */
    public AlertDialog showAlertDialogWithPhoto(ArrayList<String> selectPath) {

        // building alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.message_attachment, null);
        dialogBuilder.setView(dialogView);
        showSelectedImages(selectPath, dialogView);

        dialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.save_photo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        dialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel_dialogue_message),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Showing alert dialog
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        return alertDialog;

    }

    /**
     * * Method to show alert dialog with respective attachment.
     * @param attachType type of attachment.
     * @param videoTitle video title.
     * @param videoImage video image.
     * @param videoDescription description of video.
     * @param linkTitle title of link.
     * @param linkUrl url of link.
     * @param linkDescription description of link.
     * @param linkImage image of link.
     * @param songTitle title of song.
     * @param selectPath image real path.
     *
     */
    public void showAlertDialogWithAttachments(final String attachType, String videoTitle, String videoImage,
                                                 String videoDescription, String linkTitle, String linkUrl, String linkImage,
                                                 String linkDescription, String songTitle, ArrayList<String> selectPath) {

        setAttachtype(attachType);

        // building alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.message_attachment, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.attach_link),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setAttachtype(attachType);
                        ((Activity) mContext).invalidateOptionsMenu();
                    }
                });
        dialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel_dialogue_message),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        setAttachtype("");
                        ((Activity) mContext).invalidateOptionsMenu();
                    }
                });

        //Showing respective attachment.
        switch (attachType) {
            case "photo":
                showSelectedImages(selectPath, dialogView);
                break;
            case "video":
                showAttachedVideo(videoTitle, videoImage, videoDescription, dialogView);
                break;
            case "music":
                showAttachedMusic(songTitle, dialogView);
                break;
            case "link":
                showAttachedLink(linkUrl, linkTitle, linkImage, linkDescription, dialogView);
                break;
        }

        // Showing alert dialog
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        if (getAttachType() == null || getAttachType().isEmpty()) {
            alertDialog.dismiss();
        }

    }

    /**
     * Method to show music attachment
     * @param attachMusicTitle title of the music attachment.
     * @param dialogView View which is inflated on AlertDialog.
     */
    public void showAttachedMusic(String attachMusicTitle, View dialogView) {

        CardView mAddMusicBlock = (CardView) dialogView.findViewById(R.id.addMusicBlock);
        TextView musicIcon = (TextView) dialogView.findViewById(R.id.music_icon);
        TextView musicTitle = (TextView) dialogView.findViewById(R.id.music_title);
        musicIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mAddMusicBlock.setVisibility(View.VISIBLE);
        musicIcon.setText("\uf001");
        musicTitle.setText(attachMusicTitle);
    }

    /**
     * Method to show video attachment.
     * @param mVideoTitle title of video attachment.
     * @param mVideoImage image of video attachment.
     * @param mVideoDescription description of video attachment.
     * @param dialogView View which is inflated on AlertDialog.
     */
    public void showAttachedVideo(String mVideoTitle, String mVideoImage,
                                  String mVideoDescription, View dialogView) {

        LinearLayout mAddVideoLayout = (LinearLayout) dialogView.findViewById(R.id.addVideoLayout);
        ImageView mVideoAttachmentIcon = (ImageView) dialogView.findViewById(R.id.attachmentIcon);
        TextView mVideoAttachmentTitle = (TextView) dialogView.findViewById(R.id.attachmentTitle);
        TextView mVideoAttachmentBody = (TextView) dialogView.findViewById(R.id.attachmentBody);
        mAddVideoLayout.setVisibility(View.VISIBLE);
        mImageLoader.setImageUrl(mVideoImage, mVideoAttachmentIcon);
        mVideoAttachmentTitle.setText(Html.fromHtml(mVideoTitle));
        mVideoAttachmentBody.setText(Html.fromHtml(mVideoDescription));
    }

    /**
     * Method to show selected images.
     * @param mSelectPath list of selected images.
     * @param dialogView View which is inflated on AlertDialog.
     */
    public void showSelectedImages(final ArrayList<String> mSelectPath, View dialogView) {

        int width = AppConstant.getDisplayMetricsWidth(mContext);

        // Getting Bitmap from its real path.
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(mContext, mSelectPath.get(0), width,
                    (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height), false);

        // Checking If there is any null image.
        if (bitmap != null) {
            ImageView mSelectedImageView = (ImageView) dialogView.findViewById(R.id.selectedImage);
            mSelectedImageView.setVisibility(View.VISIBLE);
            mSelectedImageView.setImageBitmap(bitmap);
        } else {
            setAttachtype("");
        }
    }

    /**
     * Method to show attached link preview.
     * @param url url attached link.
     * @param title title of the link page.
     * @param image image preview of the page.
     * @param description description of the link page.
     * @param dialogView View which is inflated on AlertDialog.
     */
    public void showAttachedLink(String url, String title, String image, String description, View dialogView) {

        CardView mLinkAttachmentBlock = (CardView) dialogView.findViewById(R.id.linkAttachment);
        ImageView mLinkAttachmentImage = (ImageView) dialogView.findViewById(R.id.linkAttachmentImage);
        TextView mLinkAttachmentTitle = (TextView) dialogView.findViewById(R.id.linkAttachmentTitle);
        TextView mLinkAttachmentDescription = (TextView) dialogView.findViewById(R.id.linkAttachmentDescription);
        TextView mLinkAttachmentUrl = (TextView) dialogView.findViewById(R.id.linkAttachmentUrl);
        mLinkAttachmentBlock.setVisibility(View.VISIBLE);
        mLinkAttachmentUrl.setText(Html.fromHtml(url));
        mLinkAttachmentTitle.setText(Html.fromHtml(title));

        //Checking if image is coming in response or not.
        // It will return value "null" in some case, so checking this condition also.
        if (image != null && !image.isEmpty() && !image.equals("null")) {
            mLinkAttachmentImage.setVisibility(View.VISIBLE);
            mImageLoader.setImageUrl(image, mLinkAttachmentImage);
        } else {
            mLinkAttachmentImage.setVisibility(View.GONE);
        }

        //Checking if description is coming in response or not.
        // It will return value "null" in some case, so checking this condition also.
        if (description != null && !description.equals("null")) {
            mLinkAttachmentDescription.setVisibility(View.VISIBLE);
            mLinkAttachmentDescription.setText(Html.fromHtml(description));
        } else {
            mLinkAttachmentDescription.setVisibility(View.GONE);
        }
    }

    /**
     * Method to show alert dialog for attaching link.
     */
    public void showAlertDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle(mContext.getResources().getString(R.string.add_link_text));
        EditText mEnterLinkText = new EditText(mContext);
        mEnterLinkText.setBackgroundResource(R.drawable.gradient_blackborder);
        alertBuilder.setView(mEnterLinkText);
        alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.attach_link),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel_dialogue_message),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        setAlertDialogAndEditText(alertDialog, mEnterLinkText);
    }

    /**
     * Method to set alert dialog and edit text.
     * @param alertDialog alert dialog.
     * @param enterLinkText edit text which is shown in alert dialog.
     */
    public void setAlertDialogAndEditText (AlertDialog alertDialog, EditText enterLinkText) {

        mAlertDialog = alertDialog;
        mEnterLinkText = enterLinkText;
    }

    public AlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    public EditText getEnterLinkText() {
        return mEnterLinkText;
    }

    /**
     * Method to set Attach type.
     * @param attachtype attach type.
     */
    public void setAttachtype (String attachtype) {
        mAttachType = attachtype;
    }

    public String getAttachType () {
        return mAttachType;
    }

    /**
     * Method to set options in Optionmenu
     * @param menu object of Menu.
     * @param photo photo response.
     * @param video video response.
     * @param link link response.
     * @param music music response.
     */
    public void setOptionsInOptionMenu(Menu menu, int photo, int video, int link, int music) {

        menu.findItem(R.id.add_photo).setVisible(photo == 1);
        menu.findItem(R.id.add_video).setVisible(video == 1 && GlobalFunctions.isModuleEnabled("video"));
        menu.findItem(R.id.add_link).setVisible(link == 1);
        menu.findItem(R.id.add_music).setVisible(music == 1 && GlobalFunctions.isModuleEnabled("music"));

        if (photo == 0 && video == 0 && link == 0 && music == 0)
            menu.findItem(R.id.add_attachments).setVisible(false);
    }
}
