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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncResponseListener;
import com.fellopages.mobileapp.classes.common.utils.okhttp.OkHttpUtils;
import com.fellopages.mobileapp.classes.common.utils.okhttp.PostFormBuilder;
import com.fellopages.mobileapp.classes.common.utils.okhttp.StringCallback;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class OkHttpUploadHandler extends StringCallback {

    private static int TYPE_STATUS = 0, TYPE_ATTACHMENT = 1;
    private Context mContext;
    private AppConstant mAppConst;
    private Map<String, String> mPostParams;
    private String mPostUrl, mVideoPath, mVideoThumb;
    private ArrayList<String> mSelectPath;
    private ProgressDialog mProgressDialog;
    private boolean mIsAttachFileRequest, mIsNeedToShowDialog = true, isNotificationUploader;
    private OnAsyncResponseListener mCaller;
    private FeedsFragment mFeedsFragment;
    private OkHttpUtils okHttpUtils;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private int notifyItemAt = 0, PROGRESS_MAX = 100;
    private int STATUS_UPDATE_TYPE = TYPE_STATUS;

    // For Uploading data with attachment.
    public OkHttpUploadHandler(boolean processDialog, Context context, String postUrl,
                               Map<String, String> postParams, ArrayList<String> selectPath, FeedsFragment feedsFragment, boolean isNotificationUploader) {
        mContext = context;
        mPostUrl = postUrl;
        mPostParams = postParams;
        mSelectPath = selectPath;
        mAppConst = new AppConstant(mContext);
        mIsAttachFileRequest = false;
        mCaller = feedsFragment;
        mIsNeedToShowDialog = processDialog;
        mFeedsFragment = feedsFragment;
        if (postParams != null && postParams.containsKey("mVideoPath")) {
            mVideoPath = postParams.get("mVideoPath");
            mVideoThumb = postParams.get("mVideoThumbnail");
            mPostParams.put("type", "3");
        }
        notifyItemAt = (mPostParams != null) ? Integer.parseInt(String.valueOf(mPostParams.get("notifyItemAt"))) : 0;
        this.isNotificationUploader = isNotificationUploader;
    }

    private void doOnResponse(String result, Object tag) {
        if (mIsNeedToShowDialog) {
            mProgressDialog.dismiss();
        }
        Log.d("ResultPost ", result);
        LogUtils.LOGD(OkHttpUploadHandler.class.getSimpleName(), "result: " + result);

        if (BitmapUtils.isImageRotated) {
            BitmapUtils.deleteImageFolder();
        }

        try {
            JSONObject obj = new JSONObject(result);
            int statusCode = obj.optInt("status_code");

            if (statusCode == 200 || statusCode == 204) {
                mCaller.onAsyncSuccessResponse(obj,
                        AppConstant.isRequestSuccessful(statusCode), mIsAttachFileRequest);
            } else {
                mFeedsFragment.cancelRequest(Integer.parseInt(tag.toString()), obj.optString("message"));
            }

        } catch (JSONException e) {
            mCaller.onAsyncSuccessResponse(GlobalFunctions.getErrorJsonString(mContext),
                    false, mIsAttachFileRequest);

            e.printStackTrace();
        }
    }

    public void cancelRequest() {
        if (okHttpUtils != null) {
            String tag = (mPostParams != null) ? mPostParams.get("notifyItemAt") : "request";
            okHttpUtils.cancelTag(tag);
        }
    }

    public void uploadUsingOkHttp() {

        try {
            Map<String, String> params = mPostParams;
            okHttpUtils = OkHttpUtils.getInstance();
            PostFormBuilder multipartBuilder = OkHttpUtils.post();
            mPostUrl = mAppConst.buildQueryString(mPostUrl, mAppConst.getAuthenticationParams());
            // Put Language Params, location addParams, and version addParams
            mPostUrl = mAppConst.buildQueryString(mPostUrl, mAppConst.getRequestParams());

            LogUtils.LOGD(OkHttpUploadHandler.class.getSimpleName(), "Post Url: " + mPostUrl);

            //Checking mIsAttachFileRequest and it is true if the request is for attaching attachment.
            if (!mIsAttachFileRequest) {

                // Adding post addParams into entity.
                if (mPostParams != null) {
                    params.remove("photo");
                    multipartBuilder.addParams(params);
                    LogUtils.LOGD(OkHttpUploadHandler.class.getSimpleName(), "Post Params: " + params);
                }

                // Add photos in post addParams
                if (mSelectPath != null && !mSelectPath.isEmpty()) {
                    STATUS_UPDATE_TYPE = TYPE_ATTACHMENT;
                    if (BitmapUtils.isImageRotated) {
                        for (final String imagePath : mSelectPath) {
                            BitmapUtils.decodeSampledBitmapFromFile(mContext, imagePath,
                                    30, 30, true);
                        }
                        mSelectPath = BitmapUtils.updateSelectPath();
                    }
                    for (int i = 0; i < mSelectPath.size(); i++) {
                        Log.d("LogGoesHere ", "true "+compressImage(mSelectPath.get(i)));
                        if (i == 0) {
                            multipartBuilder.addFile("photo", compressImage(mSelectPath.get(i)), new File(compressImage(mSelectPath.get(i))));
                        } else {
                            multipartBuilder.addFile("photo" + i, compressImage(mSelectPath.get(i)), new File(compressImage(mSelectPath.get(i))));
                        }
                    }
                }
                // Adding video path into entity.
                if (mVideoPath != null && !mVideoPath.isEmpty()) {
                    STATUS_UPDATE_TYPE = TYPE_ATTACHMENT;
                    multipartBuilder.addFile("filedata", mVideoPath, new File(mVideoPath));
                }
                if (mVideoThumb != null && !mVideoThumb.isEmpty()) {
                    multipartBuilder.addFile("photo", mVideoThumb, new File(mVideoThumb));
                }
            }
            String tag = (mPostParams != null) ?  mPostParams.get("notifyItemAt") : "request";
            multipartBuilder
                    .url(mPostUrl)
                    .tag(tag)
                    .build()
                    .execute(this);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onError(Call call, Exception e, Object tag) {
        if (isNotificationUploader) {
            notificationManager.cancelAll();

            String message;
            if (STATUS_UPDATE_TYPE == TYPE_STATUS) {
                message = mContext.getResources().getString(R.string.update_failed);
            } else {
                message = mContext.getResources().getString(R.string.uploading_failed);
            }

            mBuilder.setContentTitle(message)
                    .setContentText(mContext.getResources().getString(R.string.error_description))
                    .setProgress(0,0,false)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationManager.notify(notifyItemAt, mBuilder.build());
        }
        mFeedsFragment.cancelRequest(Integer.parseInt(tag.toString()));
    }

    @Override
    public void onResponse(String response, Object tag) {
        doOnResponse(response, tag);
    }

    @Override
    public void onBefore(Request request, Object tag) {
        if (mIsNeedToShowDialog) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getResources().getString(R.string.progress_dialog_wait) + "…");

            // Showing progress dialog with spinner when there is no file uploading.
            if ((mSelectPath == null || mSelectPath.isEmpty())
                    && (mVideoPath == null || mVideoPath.isEmpty())) {
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            } else {
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setProgress(0);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressNumberFormat(null);
            }
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        if (isNotificationUploader) {
            initializeNotification();
        }
        super.onBefore(request, tag);
    }

    @Override
    public void inProgress(float progress, long total, Object tag) {
        int uploadProgress = Math.round(100 * progress);
        if (mIsNeedToShowDialog) {
            mProgressDialog.setProgress(uploadProgress);
        }
        if (mFeedsFragment != null && ((uploadProgress % 5) == 0 || uploadProgress > 90)) {
            mFeedsFragment.updateProgress(Integer.parseInt(String.valueOf(mPostParams.get("notifyItemAt"))),
                    uploadProgress);
        }
        if (isNotificationUploader){
            if (uploadProgress > 98) {
                String message;
                if (STATUS_UPDATE_TYPE == TYPE_STATUS) {
                    message = mContext.getResources().getString(R.string.status_post_text_message);
                } else {
                    message = mContext.getResources().getString(R.string.file_uploaded_text_message);
                }
                mBuilder.setContentText(message)
                        .setOngoing(false)
                        .setProgress(100, 100, false);
                notificationManager.notify(notifyItemAt, mBuilder.build());
            } else {
                mBuilder.setProgress(100, uploadProgress, false);
                notificationManager.notify(notifyItemAt, mBuilder.build());
            }
        }
        super.inProgress(progress, total, tag);
    }

    public void execute() {
        uploadUsingOkHttp();
    }

    public void initializeNotification() {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext, "channel_id_" + notifyItemAt);

        String message;
        if (STATUS_UPDATE_TYPE == TYPE_STATUS) {
            message = mContext.getResources().getString(R.string.status_update_text);
        } else {
            message = mContext.getResources().getString(R.string.file_upload_text);
        }

        String pMessage;
        if (STATUS_UPDATE_TYPE == TYPE_STATUS) {
            pMessage = mContext.getResources().getString(R.string.update_in_progress_text);
        } else {
            pMessage = mContext.getResources().getString(R.string.upload_in_progress_text);
        }

        mBuilder.setContentTitle(message)
                .setContentText(pMessage)
                .setSmallIcon(R.drawable.ic_file_upload_white)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("channel_id_" + notifyItemAt);
            NotificationChannel channel = new NotificationChannel(
                    "channel_id_" + notifyItemAt,
                    mContext.getResources().getString(R.string.file_upload),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setSound(null, null);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    public String compressImage(String imageUri) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imageUri, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(imageUri, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(imageUri);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
        return uriSting;

    }

//    private String getRealPathFromURI(String contentURI) {
//        Uri contentUri = Uri.parse(contentURI);
//        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
//        if (cursor == null) {
//            return contentUri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            return cursor.getString(index);
//        }
//    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
