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
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.ShareEntry;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class SocialShareUtil {

    private Context mContext;
    ShareDialog shareDialog;

    public SocialShareUtil(Context context) {
        mContext = context;
        FacebookSdk.sdkInitialize(mContext.getApplicationContext());
        shareDialog = new ShareDialog((Activity) mContext);

    }

    public void sharePost(View view, final String title, final String image, final String url
            , final String type, final String linkUrl) {

        if (view != null) {
            PopupMenu popup = new PopupMenu(mContext, view);
            popup.getMenu().add(Menu.NONE, 0, Menu.NONE, mContext.getResources().getString(R.string.share_on_your_wall)
                    + " " + mContext.getResources().getString(R.string.app_name));
            popup.getMenu().add(Menu.NONE, 1, Menu.NONE, mContext.getResources().getString(R.string.social_share));

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == 0) {
                        Intent intent = new Intent(mContext, ShareEntry.class);
                        intent.putExtra("URL", url);
                        intent.putExtra("title", title);

                        if (image != null) {
                            intent.putExtra("image", image);
                        }

                        if (type != null && type.equals("music_playlist")) {
                            intent.putExtra("music_url", linkUrl);
                        }

                        mContext.startActivity(intent);

                    } else {
                        if (type != null && type.equals(ConstantVariables.KEY_SHARE_TYPE_MEDIA)) {
                            shareMedia(mContext, image);
                        } else {
                            shareContent(title, linkUrl);
                        }
                    }
                    return true;
                }
            });
            popup.show();
        }

    }

    public void shareContent(String title, String linkUrl) {
        shareExcludingOwnApp(null, title, linkUrl, false);
    }

    public void shareMedia(Context context, String image) {
        new SaveImageAsync(context, image).execute();
    }

    public class SaveImageAsync extends AsyncTask<Void, String, String> {

        String imgUrl;
        Context context;

        public SaveImageAsync(Context context, String url) {
            this.context = context;
            imgUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(imgUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap imageBitmap = BitmapFactory.decodeStream(input);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                return MediaStore.Images.Media.insertImage(
                        context.getContentResolver(), imageBitmap, "Image", null);

            } catch (IOException e) {
                // Log exception

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null && !result.isEmpty()) {
                shareExcludingOwnApp(result, null, null, true);
            }
        }

    }

    /**
     * Method to remove own app from sharing app list.
     *
     * @param mediaResult         Media result in case of image sharing.
     * @param title               Title of the sharing content.
     * @param linkUrl             Link url of the sharing content.
     * @param isMediaShareRequest True if the request is for media sharing.
     */
    public void shareExcludingOwnApp(String mediaResult, String title, String linkUrl,
                                     boolean isMediaShareRequest) {

        Intent intent = new Intent(Intent.ACTION_SEND);

        // Check for share type and set intent params accordingly.
        if (isMediaShareRequest) {
            intent.setType("image/*");
            Uri uri = Uri.parse(mediaResult);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        } else {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, linkUrl);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(intent, 0);

        // Check apps are available or not for accept action from intent.
        if (resInfo.size() > 0) {
            Intent chooserIntent = Intent.createChooser(intent,
                    mContext.getString(R.string.share_via) + "…");
            ((Activity) mContext).startActivityForResult(chooserIntent, ConstantVariables.OUTSIDE_SHARING_CODE);
        }
    }

}
