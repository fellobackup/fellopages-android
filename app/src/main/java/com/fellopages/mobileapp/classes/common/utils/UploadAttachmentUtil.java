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
import android.os.AsyncTask;
import android.util.Log;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncFacebookResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnCommentPostListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnFeedPostListener;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;
import com.fellopages.mobileapp.classes.modules.likeNComment.CommentList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Uploading the Attachment files and data to server in background
 */
public class UploadAttachmentUtil extends AsyncTask<Void, Integer, String> {

    private Context mContext;
    private Map<String, String> mPostParams;
    private String mPostUrl, mAttachType, mUriText, mSelectedMusicFile, mVideoPath, mVideoThumb;
    private ArrayList<String> mSelectPath;
    private ProgressDialog mProgressDialog;
    private AppConstant mAppConst;
    private long totalSize;
    private boolean mIsAttachFileRequest, mIsNeedToShowDialog = true;
    private OnAsyncResponseListener mCaller;
    private OnAsyncFacebookResponseListener mFacebookListener;
    private OnCommentPostListener mOnCommentPostListener;
    private CommentList mCommentList;
    private Boolean mFacebookPost = false;
    private OnFeedPostListener mOnFeedPostListener;
    private FeedsFragment mFeedsFragment;
    private HttpPost httppost;
    private String mStoryVideoThumb;

    // For attaching Music
    public UploadAttachmentUtil(Context context, String postUrl, String selectedMusicFile) {
        mContext = context;
        mPostUrl = postUrl;
        mSelectedMusicFile = selectedMusicFile;
        mAttachType = "music";

        mIsAttachFileRequest = true;
        mCaller = (OnAsyncResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
    }

    // For attaching Link
    public UploadAttachmentUtil(Context context, String postUrl, String uriText, String attachType) {
        mContext = context;
        mPostUrl = postUrl;
        mUriText = uriText;
        mAttachType = attachType;

        mIsAttachFileRequest = true;
        mCaller = (OnAsyncResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
    }

    // For Uploading data with attachment.
    public UploadAttachmentUtil(Context context, String postUrl,
                                Map<String, String> postParams, ArrayList<String> selectPath) {
        mContext = context;
        mPostUrl = postUrl;
        mPostParams = postParams;
        mSelectPath = selectPath;

        mIsAttachFileRequest = false;
        mCaller = (OnAsyncResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
        if (postParams != null && postParams.containsKey("mVideoPath")) {
            mVideoPath = postParams.get("mVideoPath");
            mVideoThumb = postParams.get("mVideoThumbnail");
            mPostParams.put("type", "3");
        }
    }

    // For Uploading data with attachment.
    public UploadAttachmentUtil(boolean processDialog, Context context, String postUrl,
                                Map<String, String> postParams, ArrayList<String> selectPath, FeedsFragment feedsFragment) {
        mContext = context;
        mPostUrl = postUrl;
        mPostParams = postParams;
        mSelectPath = selectPath;

        mIsAttachFileRequest = false;
        mCaller = feedsFragment;
        mAppConst = new AppConstant(mContext);
        mIsNeedToShowDialog = processDialog;
        mOnFeedPostListener = mFeedsFragment;
        mFeedsFragment = feedsFragment;
        if (postParams.containsKey("mVideoPath")) {
            mVideoPath = postParams.get("mVideoPath");
            mVideoThumb = postParams.get("mVideoThumbnail");
            mPostParams.put("type", "3");
        }
    }

    // For Uploading data with attachment. (Story uploading)
    public UploadAttachmentUtil(Context context, String postUrl, String videoPath,
                                Map<String, String> postParams, ArrayList<String> selectPath,
                                OnAsyncResponseListener onAsyncResponseListener) {
        mContext = context;
        mPostUrl = postUrl;
        mVideoPath = videoPath;
        mPostParams = postParams;
        mSelectPath = selectPath;

        if (mVideoPath != null && !mVideoPath.isEmpty()) {
            mVideoThumb = postParams.get("videoThumb");
            mPostParams.put("type", "3");
        }

        mIsNeedToShowDialog = false;
        mIsAttachFileRequest = false;
        mCaller = onAsyncResponseListener;
        mAppConst = new AppConstant(mContext);
    }

    // For Uploading data with attachment. (Story uploading)
    public UploadAttachmentUtil(Context context, String postUrl, String videoPath, String storyVideoThumb,
                                Map<String, String> postParams, ArrayList<String> selectPath,
                                OnAsyncResponseListener onAsyncResponseListener) {
        mContext = context;
        mPostUrl = postUrl;
        mVideoPath = videoPath;
        mPostParams = postParams;
        mSelectPath = selectPath;
        mStoryVideoThumb = storyVideoThumb;

        mIsNeedToShowDialog = false;
        mIsAttachFileRequest = false;
        mCaller = onAsyncResponseListener;
        mAppConst = new AppConstant(mContext);
    }

    // For Uploading data with attachment.
    public UploadAttachmentUtil(Context context, String postUrl,
                                Map<String, String> postParams, ArrayList<String> selectPath, boolean isFacebookPost) {
        mContext = context;
        mPostUrl = postUrl;
        mPostParams = postParams;
        mSelectPath = selectPath;

        mIsAttachFileRequest = false;
        mFacebookListener = (OnAsyncFacebookResponseListener) mContext;
        mAppConst = new AppConstant(mContext);
        mFacebookPost = isFacebookPost;
    }

    //For Comment with photo uploading.
    public UploadAttachmentUtil(Context context, String postUrl, ArrayList<String> selectPath,
                                Map<String, String> params, CommentList commentList) {
        mContext = context;
        mPostUrl = postUrl;
        mSelectPath = selectPath;
        mIsAttachFileRequest = false;
        mPostParams = params;
        mCommentList = commentList;
        mAppConst = new AppConstant(mContext);
        mOnCommentPostListener = (OnCommentPostListener) mContext;
    }

    /**
     * Method to return attach params.
     *
     * @param postParams Post params in which attach params will be added.
     * @param attachType Type of attachment.
     * @param uriText    Link attachment uri.
     * @param songId     Music attachment id.
     * @param videoId    Video attachment id.
     * @return Returns the post params with attachment info.
     */
    public static Map<String, String> getAttachmentPostParams(Map<String, String> postParams,
                                                              String attachType, String uriText,
                                                              int songId, int videoId) {
        postParams.put("type", attachType);
        postParams.put("post_attach", "1");
        switch (attachType) {
            case "music":
                postParams.put("song_id", String.valueOf(songId));
                break;
            case "link":
                if (!uriText.contains("https://") && !uriText.contains("http://")) {
                    uriText = "https://" + uriText;
                }
                postParams.put("uri", uriText);
                break;
            case "video":
                postParams.put("video_id", String.valueOf(videoId));
                break;
        }
        return postParams;
    }

    /**
     * Method to return attach params.
     *
     * @param postParams  Post params in which attach params will be added.
     * @param attachType  Type of attachment.
     * @param uriText     Link attachment uri.
     * @param stickerGuid Sticker Guid.
     * @param songId      Music attachment id.
     * @param videoId     Video attachment id.
     * @return Returns the post params with attachment info.
     */
    public static HashMap<String, String> getAttachmentPostParams(HashMap<String, String> postParams,
                                                                      String attachType, String uriText,
                                                                      String stickerGuid, String stickerImage,
                                                                      int songId, int videoId, JSONObject sellSomethingValues) {
        postParams.put("type", attachType);
        postParams.put("post_attach", "1");
        switch (attachType) {
            case "music":
                postParams.put("song_id", String.valueOf(songId));
                break;
            case "link":
                if (!uriText.contains("https://") && !uriText.contains("http://")) {
                    uriText = "https://" + uriText;
                }
                postParams.put("uri", uriText);
                break;
            case "video":
                postParams.put("video_id", String.valueOf(videoId));
                break;
            case "sticker":
                postParams.put("sticker_guid", stickerGuid);
                postParams.put("thumb", stickerImage);
                break;
            case "sell":
                if (sellSomethingValues != null && sellSomethingValues.length() > 0) {
                    Iterator<String> keySet = sellSomethingValues.keys();
                    while (keySet.hasNext()) {
                        String key = keySet.next();
                        if (!key.equals("photo")) {
                            postParams.put(key, sellSomethingValues.optString(key));
                        }
                    }
                }
                break;
        }
        return postParams;
    }

    @Override
    protected void onPreExecute() {

        if (mIsNeedToShowDialog) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getResources().getString(R.string.progress_dialog_wait) + "…");

            // Showing progress dialog with spinner when there is no file uploading.
            if ((mSelectPath == null || mSelectPath.isEmpty())
                    && (mSelectedMusicFile == null || mSelectedMusicFile.isEmpty())
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


        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString;
        HttpClient httpclient = MySSLSocketFactoryUtil.getNewHttpClient();

        if (!mFacebookPost) {
            mPostUrl = mAppConst.buildQueryString(mPostUrl, mAppConst.getAuthenticationParams());
            // Put Language Params, location params, and version params
            mPostUrl = mAppConst.buildQueryString(mPostUrl, mAppConst.getRequestParams());
        }

        httppost = new HttpPost(mPostUrl);
        LogUtils.LOGD(UploadAttachmentUtil.class.getSimpleName(), "Post Url: " + mPostUrl);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            //Checking mIsAttachFileRequest and it is true if the request is for attaching attachment.
            if (!mIsAttachFileRequest) {

                // Adding post params into entity.
                if (mPostParams != null) {
                    Set<String> keySet = mPostParams.keySet();

                    for (String key : keySet) {
                        if (!key.equals("photo")) {
                            Log.d("ThisWasLoggedHereStory ", "true");
                            String value = mPostParams.get(key);
                            try {
                                entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
                            } catch (RuntimeException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    LogUtils.LOGD(UploadAttachmentUtil.class.getSimpleName(), "Post Params: " + mPostParams);
                }

                // Add photos in post params
                if (mSelectPath != null && !mSelectPath.isEmpty()) {
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
                            entity.addPart("photo", new FileBody(new File(mSelectPath.get(i))));
                        } else {
                            entity.addPart("photo" + i, new FileBody(new File(mSelectPath.get(i))));
                        }
                    }
                }

                if (mStoryVideoThumb != null && !mStoryVideoThumb.isEmpty()) {
                    entity.addPart("video_thumbnail", new FileBody(new File(mStoryVideoThumb)));
                }

                // Adding video path into entity.
                if (mVideoPath != null && !mVideoPath.isEmpty()) {
                    entity.addPart("filedata", new FileBody(new File(mVideoPath)));
                }
                if (mVideoThumb != null && !mVideoThumb.isEmpty()) {
                    entity.addPart("photo", new FileBody(new File(mVideoThumb)));
                }
            } else {
                // Add music in post params
                if (mSelectedMusicFile != null && !mSelectedMusicFile.isEmpty()) {
                    entity.addPart("post_attach", new StringBody("1"));
                    entity.addPart("type", new StringBody("wall"));
                    entity.addPart("Filedata", new FileBody(new File(mSelectedMusicFile)));
                }

                if (mAttachType.equals("link")) {
                    if (!mUriText.contains("https://") && !mUriText.contains("http://")) {
                        mUriText = "https://" + mUriText;
                    }
                    entity.addPart("uri", new StringBody(mUriText));
                }
            }

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = mContext.getResources().getString(R.string.status_code_error)
                        + ": " + statusCode;
            }

        } catch (IOException | NullPointerException e) {
            if (mIsNeedToShowDialog) {
                mProgressDialog.dismiss();
            }
            responseString = e.toString();
        }

        return responseString;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (mIsNeedToShowDialog) {
            mProgressDialog.setProgress((progress[0]));
        }
        if (mFeedsFragment != null && ((progress[0] % 5) == 0 || progress[0] > 90)) {
            mFeedsFragment.updateProgress(Integer.parseInt(String.valueOf(mPostParams.get("notifyItemAt"))),
                    progress[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mIsNeedToShowDialog) {
            mProgressDialog.dismiss();
        }

        LogUtils.LOGD(UploadAttachmentUtil.class.getSimpleName(), "result: " + result);

        if (BitmapUtils.isImageRotated) {
            BitmapUtils.deleteImageFolder();
        }

        if (mFacebookPost) {
            mFacebookListener.onAsyncSuccessFacebookResponse(mFacebookPost);
        } else {
            try {
                JSONObject obj = new JSONObject(result);
                int statusCode = obj.optInt("status_code");
                if (mOnCommentPostListener != null) {
                    mOnCommentPostListener.onCommentPost(obj, AppConstant.isRequestSuccessful(statusCode), mCommentList);
                } else {
                    mCaller.onAsyncSuccessResponse(obj,
                            AppConstant.isRequestSuccessful(statusCode), mIsAttachFileRequest);
                }

            } catch (JSONException e) {
                if (mOnCommentPostListener != null) {
                    mOnCommentPostListener.onCommentPost(GlobalFunctions.getErrorJsonString(mContext),
                            false, mCommentList);
                } else {
                    mCaller.onAsyncSuccessResponse(GlobalFunctions.getErrorJsonString(mContext),
                            false, mIsAttachFileRequest);
                }
                e.printStackTrace();
            }
        }


    }

}
