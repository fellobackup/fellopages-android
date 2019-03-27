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

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class UploadFileToServerUtils extends AsyncTask<Void, Integer, String> {

    private Context mContext;
    private View mMainView;
    private ArrayList<String> mSelectPath, mSelectedMusicFiles;
    private Map<String, String> mPostParams;
    private HashMap<String, ArrayList> mHostMap;
    private JSONObject mResponseObject;
    private ProgressDialog mProgressDialog;
    private String mPostUrl, mSelectedVideoPath, mCurrentSelectedModule, mData, mUserPicture, mSelectedFilePath, mSelectedVideoThumb;
    private boolean mIsDataUploadRequest, mIsCreateForm, mIsError = false,
            mIsSignUpRequest = false, mIsMainActivityImageUploadRequest;
    private long totalSize;
    private AppConstant mAppConst;
    private OnUploadResponseListener mOnUploadResponseListener;

    // For Image File uploading
    public UploadFileToServerUtils(Context context, String postUrl, ArrayList<String> selectPath,
                                   OnUploadResponseListener onUploadResponseListener) {
        this.mContext = context;
        this.mPostUrl = postUrl;
        this.mSelectPath = selectPath;
        this.mOnUploadResponseListener = onUploadResponseListener;

        mIsDataUploadRequest = false;
        mAppConst = new AppConstant(mContext);
    }

    // For Image File uploading from MainActivity
    public UploadFileToServerUtils(Context context, View mainView, String postUrl,
                                   ArrayList<String> selectPath,
                                   OnUploadResponseListener onUploadResponseListener) {
        this.mContext = context;
        this.mMainView = mainView;
        this.mPostUrl = postUrl;
        this.mIsMainActivityImageUploadRequest = true;
        this.mSelectPath = selectPath;
        this.mOnUploadResponseListener = onUploadResponseListener;

        mIsDataUploadRequest = false;
        mAppConst = new AppConstant(mContext);
    }

    // For Create data uploading
    public UploadFileToServerUtils(Context context, String postUrl, String currentSelectedModule,
                                   String selectedVideoPath, String selectedVideoThumb, boolean isCreateForm,
                                   ArrayList<String> selectPath,
                                   ArrayList<String> selectedMusicFiles,
                                   Map<String, String> postParams,
                                   HashMap<String, ArrayList> hostMap,String mSelectedFilePath) {
        this.mContext = context;
        this.mPostUrl = postUrl;
        this.mCurrentSelectedModule = currentSelectedModule;
        this.mSelectedVideoPath = selectedVideoPath;
        this.mIsCreateForm = isCreateForm;
        this.mSelectPath = selectPath;
        this.mSelectedMusicFiles = selectedMusicFiles;
        this.mPostParams = postParams;
        this.mHostMap = hostMap;
        this.mSelectedFilePath = mSelectedFilePath;
        this.mSelectedVideoThumb = selectedVideoThumb;

        if (mSelectedVideoThumb != null && !mSelectedVideoThumb.isEmpty()) {
            mPostParams.put("type", "3");
        }

        mIsDataUploadRequest = true;
        mOnUploadResponseListener = (OnUploadResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
    }

    // For Edit data uploading
    public UploadFileToServerUtils(Context context, String postUrl, String currentSelectedModule,
                                   boolean isCreateForm, String data, String selectedFilePath,
                                   ArrayList<String> selectPath,
                                   ArrayList<String> selectedMusicFiles,
                                   Map<String, String> postParams,
                                   HashMap<String, ArrayList> hostMap) {
        this.mContext = context;
        this.mPostUrl = postUrl;
        this.mCurrentSelectedModule = currentSelectedModule;
        this.mIsCreateForm = isCreateForm;
        this.mData = data;
        this.mSelectedFilePath = selectedFilePath;
        this.mSelectPath = selectPath;
        this.mSelectedMusicFiles = selectedMusicFiles;
        this.mPostParams = postParams;
        this.mHostMap = hostMap;

        mIsDataUploadRequest = true;
        mOnUploadResponseListener = (OnUploadResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
    }

    // For Editor data uploading
    public UploadFileToServerUtils(Context context, String postUrl, String data,
                                   ArrayList<String> selectPath,
                                   Map<String, String> postParams) {
        this.mContext = context;
        this.mPostUrl = postUrl;
        this.mData = data;
        this.mSelectPath = selectPath;
        this.mPostParams = postParams;

        mIsDataUploadRequest = true;
        mOnUploadResponseListener = (OnUploadResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
    }

    // For SignUp
    public UploadFileToServerUtils(Context context, String postUrl, ArrayList<String> selectPath,
                                   Map<String, String> postParams, String picture) {
        this.mContext = context;
        this.mPostUrl = postUrl;
        this.mSelectPath = selectPath;
        this.mPostParams = postParams;
        this.mUserPicture = picture;

        mIsSignUpRequest = true;
        mIsDataUploadRequest = true;
        mOnUploadResponseListener = (OnUploadResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);

        if (!mIsMainActivityImageUploadRequest) {
            if (mIsDataUploadRequest) {
                mProgressDialog.setMessage(mContext.getResources().getString(R.string.progress_dialog_wait) + "…");
            } else {
                mProgressDialog.setMessage(mContext.getResources().getString(R.string.dialog_uploading_msg) + "…");
            }
            // Showing progress dialog with spinner when there is no file uploading.
            if ((mSelectPath == null || mSelectPath.isEmpty())
                    && (mSelectedMusicFiles == null || mSelectedMusicFiles.isEmpty())
                    && (mSelectedVideoPath == null || mSelectedVideoPath.isEmpty())) {
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            } else {
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setProgress(0);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressNumberFormat(null);
            }
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        } else {
            SnackbarUtils.displaySnackbarLongTime(mMainView, mContext.getResources().
                    getQuantityString(R.plurals.photo_uploading_msg,
                            mSelectPath.size()));
        }
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString;
        HttpPost httppost;
        HttpClient httpclient = MySSLSocketFactoryUtil.getNewHttpClient();
        mPostUrl = mAppConst.buildQueryString(mPostUrl, mAppConst.getAuthenticationParams());
        Log.d("mPostUrlHttp ", mPostUrl);
        // Put Language Params, location params, and version params
        if (mPostParams != null){
            mPostUrl = mAppConst.buildQueryString(mPostUrl, mPostParams);
        } else {
            mPostUrl = mAppConst.buildQueryString(mPostUrl, mAppConst.getRequestParams());
        }

        httppost = new HttpPost(mPostUrl);
        LogUtils.LOGD(UploadFileToServerUtils.class.getSimpleName(), "Post Url: " + mPostUrl);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            if (mIsDataUploadRequest) {

                // Adding post params into entity.
                if (mPostParams != null) {
                    Log.d("LoggedParamsInPost ", mPostParams.toString());
                    Set<String> keySet = mPostParams.keySet();
                    for (String key : keySet) {
                        if (!key.equals("photo")) {
                            String value = mPostParams.get(key);
                            entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
                        }
                    }
                    LogUtils.LOGD(UploadFileToServerUtils.class.getSimpleName(), "Post Params: " + entity);
                }

                // Adding Editor data
                if (mData != null) {
                    entity.addPart("body", new StringBody(mData, Charset.forName("UTF-8")));
                }

                // Adding video path into entity.
                if (mSelectedVideoPath != null && !mSelectedVideoPath.isEmpty()) {
                    entity.addPart("filedata", new FileBody(new File(mSelectedVideoPath)));
                }

                if (mSelectedVideoThumb != null && !mSelectedVideoThumb.isEmpty()) {
                    entity.addPart("photo", new FileBody(new File(mSelectedVideoThumb)));
                }

                // Adding file path into entity.
                if(mCurrentSelectedModule != null && mCurrentSelectedModule.equals(ConstantVariables.PRODUCT_MENU_TITLE) && mSelectedFilePath != null){
                    entity.addPart("upload_product", new FileBody(new File(mSelectedFilePath)));
                }else if(mSelectedFilePath != null) {
                    entity.addPart("filename", new FileBody(new File(mSelectedFilePath)));
                }

                // Adding music files into entity.
                if (mSelectedMusicFiles != null && !mSelectedMusicFiles.isEmpty()) {
                    for (int i = 0; i < mSelectedMusicFiles.size(); i++) {
                        if (i == 0) {
                            entity.addPart("songs", new FileBody(new File(mSelectedMusicFiles.get(i))));
                        } else {
                            entity.addPart("songs" + i, new FileBody(new File(mSelectedMusicFiles.get(i))));
                        }

                    }
                }

                // Adding host photo in case of advanced event.
                if (mCurrentSelectedModule != null
                        && mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)
                        && mIsCreateForm) {
                    if (mHostMap.containsKey("host_photo")) {
                        String list = mHostMap.get("host_photo").get(0).toString();
                        entity.addPart("host_photo", new FileBody(new File(list)));
                    }
                    if (mHostMap.containsKey("photo")) {
                        String list = mHostMap.get("photo").get(0).toString();
                        entity.addPart("photo", new FileBody(new File(list)));
                    }
                }

                // Adding registration id and device id in case of sign-up.
                if (mIsSignUpRequest) {
                    try {
                        String registrationId = FirebaseInstanceId.getInstance().getToken(mContext.getResources().getString(R.string.gcm_defaultSenderId), "FCM");
                        if (registrationId != null && !registrationId.isEmpty()) {
                            entity.addPart("registration_id", new StringBody(registrationId));
                            entity.addPart("device_uuid", new StringBody(mAppConst.getDeviceUUID()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            // Adding Image files into entity.
            if (mSelectPath != null && !mSelectPath.isEmpty()) {
                // Checking is there any image which required rotation.
                for (final String imagePath : mSelectPath) {
                    BitmapUtils.decodeSampledBitmapFromFile(mContext, imagePath,
                            AppConstant.getDisplayMetricsWidth(mContext),
                            (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height), false);
                }
                if (BitmapUtils.isImageRotated) {
                    for (final String imagePath : mSelectPath) {
                        BitmapUtils.decodeSampledBitmapFromFile(mContext, imagePath,
                                AppConstant.getDisplayMetricsWidth(mContext),
                                (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height), true);
                    }
                    mSelectPath = BitmapUtils.updateSelectPath();
                }
                for (int i = 0; i < mSelectPath.size(); i++) {
                    LogUtils.LOGD(UploadFileToServerUtils.class.getSimpleName(), "Image Url: " + mSelectPath.get(i));

                    if (i == 0) {
                        entity.addPart("photo", new FileBody(new File(mSelectPath.get(i))));
                    } else {
                        entity.addPart("photo" + i, new FileBody(new File(mSelectPath.get(i))));
                    }
                }
            } else if (mUserPicture != null && !mUserPicture.isEmpty()) {
                Bitmap image = BitmapUtils.getBitmapFromURL(mUserPicture);
                mSelectPath = BitmapUtils.storeImageOnLocalPathFromUrl(mContext, image, ".jpg");
                entity.addPart("photo", new FileBody(new File(mSelectPath.get(0))));
            }


            httppost.setEntity(entity);


            // Making server call
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            entity.writeTo(bytes);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            Log.d("ErrorResponse ", String.valueOf(statusCode));
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

            if (mIsDataUploadRequest && GlobalFunctions.isValidJson(responseString)) {
                mResponseObject = new JSONObject(responseString);
//                Log.d("finallyJsonObjectError ", mResponseObject);
                int responseStatusCode = mResponseObject.getInt("status_code");
                switch (responseStatusCode) {
                    case 400:
                        mProgressDialog.dismiss();
                        if (mOnUploadResponseListener != null) {
                            mResponseObject.put("showValidation", true);
                            mOnUploadResponseListener.onUploadResponse(mResponseObject, false);
                        }
                        mIsError = true;
                        break;

                    case 404:
                    case 401:
                    case 500:
                        mProgressDialog.dismiss();
                        mIsError = true;
                        break;

                    default:
                        mIsError = false;

                }
            }

        } catch (Exception | OutOfMemoryError e) {
            mProgressDialog.dismiss();
            mIsError = true;
            responseString = e.toString();
        }
        return responseString;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.setProgress((progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            Log.d("LoggedResult ", result);
            LogUtils.LOGD(UploadFileToServerUtils.class.getSimpleName(), "result: " + result);
            mProgressDialog.dismiss();

            if (BitmapUtils.isImageRotated) {
                BitmapUtils.deleteImageFolder();
            }
            if (!mIsError) {
                if (mOnUploadResponseListener != null) {

                    try {
                        JSONObject obj = new JSONObject(result);
                        int statusCode = obj.optInt("status_code");
                        mOnUploadResponseListener.onUploadResponse(obj,
                                AppConstant.isRequestSuccessful(statusCode));

                    } catch (JSONException e) {
                        mOnUploadResponseListener.onUploadResponse(GlobalFunctions.getErrorJsonString(mContext),
                                false);
                        e.printStackTrace();
                    }
                }
            } else if (mResponseObject != null && !mResponseObject.has("showValidation")) {
                if (mOnUploadResponseListener != null) {
                    mOnUploadResponseListener.onUploadResponse(mResponseObject, false);
                }
            } else if (result.contains("OutOfMemoryError")) {
                mOnUploadResponseListener.onUploadResponse(GlobalFunctions.getErrorJsonString(mContext.getResources().getString(R.string.large_size_file_error_message)),
                        false);
            }
        } catch (Exception e) {
            if (mOnUploadResponseListener != null) {
                mOnUploadResponseListener.onUploadResponse(GlobalFunctions.getErrorJsonString(mContext),
                        false);
            }
            e.printStackTrace();
        }
    }

}
