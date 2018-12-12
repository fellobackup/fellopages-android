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
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

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
        mCaller = (OnAsyncResponseListener) feedsFragment;
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
                mFeedsFragment.cancelRequest(Integer.parseInt(tag.toString()));
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
            PostFormBuilder multipartBuilder = okHttpUtils.post();
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
                                    AppConstant.getDisplayMetricsWidth(mContext),
                                    (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height), true);
                        }
                        mSelectPath = BitmapUtils.updateSelectPath();
                    }
                    for (int i = 0; i < mSelectPath.size(); i++) {
                        if (i == 0) {
                            multipartBuilder.addFile("photo", mSelectPath.get(i), new File(mSelectPath.get(i)));
                        } else {
                            multipartBuilder.addFile("photo" + i, mSelectPath.get(i), new File(mSelectPath.get(i)));
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
}
