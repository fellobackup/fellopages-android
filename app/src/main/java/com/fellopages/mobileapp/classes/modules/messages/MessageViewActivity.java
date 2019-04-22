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

package com.fellopages.mobileapp.classes.modules.messages;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.AttachmentDialogUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncResponseListener;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadAttachmentUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageViewActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        OnAsyncResponseListener {

    private Toolbar mToolbar;
    private Context mContext;
    private AppConstant mAppConst;
    private String mItemViewUrl, mAttachUrl, mAttachType = "", mUploadingOption = "";
    private TextView btnSend;
    private EditText inputMsg, mEnterLinkText;
    private ArrayList<String> mSelectPath;
    private String mSongTitle, linkUrl, linkImage,  linkTitle, linkDescription, mSelectedMusicFile,
            mVideoTitle, mVideoImage, mVideoDescription, uriText;
    private int mVideoId, mSongId;
    private int link = 0, music = 0, video = 0, photo = 0;
    private List<MessageViewDetails> mMessageListDetail;
    private JSONArray messagesArray, replyFormArray;
    private JSONObject mBody, mConversationObject, mSenderDataResponse, mReceiverDataResponse, mMessageData, mAttachmentData;
    private String mUserDisplayName, mUserProfileImage, textAreaName, mMessageTitle, mMessageUpdatedDate, mMessageBody,
            mAttachmentType, mAttachmentTitle, mAttachmentBody, mAttachmentImage, mAttachmentUri, mVideoAttachContentUrl;
    private int mInboxRead, mInboxDeleted, mMessageId, mConversationId, mSenderId, mRecipientId,
            mParticipantCount, mAttachmentPlayListId, mAttachmentVideoId;
    ArrayList<PhotoListDetails> mPhotoDetails;
    private boolean isMessageSent = false;
    // Chat messages list adapter
    private MessageListAdapter adapter;
    private ListView listViewMessages;
    private Snackbar snackbar;
    private KeyListener keyListener;
    private MenuItem badgeMenuItem, attachmentMenuItem;
    private AttachmentDialogUtil mAttachmentDialogUtil;
    private PhotoListDetails photoListDetails;
    private int position = 0,mMessageUnreadCount;
    private AlertDialogWithAction mAlertDialogWithAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        /* Create Back Button On Action Bar **/
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setSubtitleTextAppearance(this, R.style.TextAppearance_AppCompat_Caption);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mContext = this;
        mAttachmentDialogUtil = new AttachmentDialogUtil(MessageViewActivity.this);
        mPhotoDetails = new ArrayList<>();
        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);

        mItemViewUrl = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        String userName = getIntent().getStringExtra("UserName");
        getSupportActionBar().setTitle(userName);

        mMessageListDetail = new ArrayList<>();
        btnSend = findViewById(R.id.btnSend);
        btnSend.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        btnSend.setText("\uf1d8");
        inputMsg = findViewById(R.id.inputMsg);
        inputMsg.addTextChangedListener(this);
        listViewMessages = findViewById(R.id.list_view_messages);
        btnSend.setOnClickListener(this);
        keyListener = inputMsg.getKeyListener();
        inputMsg.setKeyListener(null);

        adapter = new MessageListAdapter(this, mMessageListDetail);
        listViewMessages.setAdapter(adapter);

        makeRequest();
    }

    public void makeRequest(){
        mAppConst.getJsonResponseFromUrl(mItemViewUrl + "&post_attach=1", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                if (jsonObject != null) {
                    setDataInAdapter(jsonObject);
                    JSONObject feedPostMenuJsonObject = jsonObject.optJSONObject("feed_post_menu");
                    if (feedPostMenuJsonObject != null) {
                        photo = feedPostMenuJsonObject.optInt("photo");
                        video = feedPostMenuJsonObject.optInt("video");
                        music = feedPostMenuJsonObject.optInt("music");
                        link = feedPostMenuJsonObject.optInt("link");
                    }
                } else {
                    inputMsg.setKeyListener(keyListener);
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, btnSend, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(btnSend, message);
                }
            }
        });
    }

    /**
     * Set Data in Adapter and notify adapter
     * @param jsonObject Server Response
     */
    public void setDataInAdapter(JSONObject jsonObject){
        mBody = jsonObject;
        try {
            mPhotoDetails.clear();
            int conversationImageCount = mBody.optInt("conversation_image_count");
            messagesArray = mBody.optJSONArray("messages");
            replyFormArray = mBody.optJSONArray("reply_form");
            mConversationObject = mBody.optJSONObject("conversation");
            mParticipantCount = mConversationObject.optInt("recipients");
            if(mParticipantCount > 1) {
                mToolbar.setSubtitle("+" + getResources().getQuantityString(R.plurals.chat_view_subtitle,
                        mParticipantCount, mParticipantCount));
            }

            textAreaName = replyFormArray.getJSONObject(0).optString("name");
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject jsonDataObject = messagesArray.optJSONObject(i);
                mMessageData = jsonDataObject.optJSONObject("message");
                mAttachmentData = jsonDataObject.optJSONObject("attachment");
                mReceiverDataResponse = jsonDataObject.optJSONObject("recipient");
                mSenderDataResponse = jsonDataObject.optJSONObject("sender");

                mUserDisplayName = mSenderDataResponse.optString("displayname");
                mUserProfileImage = mSenderDataResponse.optString("image_profile");
                mSenderId = mSenderDataResponse.optInt("user_id");

                mConversationId = mMessageData.optInt("conversation_id");
                if(PreferencesUtils.getNotificationsCounts(mContext,
                        PreferencesUtils.MESSAGE_COUNT) != null) {
                    mMessageUnreadCount = Integer.parseInt(PreferencesUtils.getNotificationsCounts(mContext,
                            PreferencesUtils.MESSAGE_COUNT));
                }
                if(mReceiverDataResponse.optString("inbox_read") != null
                        && mReceiverDataResponse.optString("inbox_read").equals("0")
                        && mMessageUnreadCount !=0 ){
                    HashMap<String,String> postParams = new HashMap<>();
                    postParams.put("message_id", String.valueOf(mConversationId));
                    postParams.put("is_read", String.valueOf(1));
                    mAppConst.markAllMessageRead(postParams);
                    PreferencesUtils.updateNotificationPreferences(mContext,
                            String.valueOf(--mMessageUnreadCount), "0","0",
                            PreferencesUtils.getNotificationsCounts(mContext,PreferencesUtils.CART_COUNT));
                }
                mMessageId = mMessageData.optInt("message_id");
                mMessageUpdatedDate = mMessageData.optString("date");
                mMessageTitle = mMessageData.optString("title");
                mMessageBody = mMessageData.optString("body");
                mAttachmentType = mMessageData.optString("attachment_type");

                if (mMessageBody.contains("advancedactivity_story")) {
                    mAttachmentType = "story";
                    String s = "<img src=\"";
                    int ix = mMessageBody.indexOf(s)+s.length();
                    mAttachmentImage = mMessageBody.substring(ix, mMessageBody.indexOf("\"", ix+1));
                    String replacebleText = mMessageBody.substring(mMessageBody.indexOf(s), mMessageBody.indexOf(">")+1);
                    mMessageBody = mMessageBody.replace(replacebleText, "");
                }

                if (mAttachmentData != null) {
                    mAttachmentTitle = mAttachmentData.optString("title");
                    mAttachmentBody = mAttachmentData.optString("description");
                    mAttachmentPlayListId = mAttachmentData.optInt("playlist_id");
                    mAttachmentVideoId = mAttachmentData.optInt("video_id");
                    mAttachmentImage = mAttachmentData.optString("image");
                    mAttachmentUri = mAttachmentData.optString("uri");
                    mVideoAttachContentUrl = mAttachmentData.optString("content_url");
                    if (mAttachmentType.equals("album_photo")) {
                        int albumId = mAttachmentData.optInt("album_id");
                        int photoId = mAttachmentData.optInt("photo_id");
                        int likesCount = mAttachmentData.optInt("likes_count");
                        int commentCount = mAttachmentData.optInt("comment_count");
                        int isLike = mAttachmentData.optInt("is_like");
                        boolean likeStatus = false;
                        if (isLike != 0)
                            likeStatus = true;
                        String photoListUrl = UrlUtil.ALBUM_VIEW_PAGE
                                + albumId+"?gutter_menu=1";

                        photoListDetails = new PhotoListDetails(photoId, mAttachmentImage, photoListUrl,
                                conversationImageCount, likesCount, commentCount, likeStatus, position);
                        mPhotoDetails.add(photoListDetails);
                        position++;
                    }
                }

                mInboxRead = mReceiverDataResponse.optInt("inbox_read");
                mInboxDeleted = mReceiverDataResponse.optInt("inbox_deleted");
                mRecipientId = mReceiverDataResponse.optInt("user_id");

                mMessageListDetail.add(new MessageViewDetails(mSenderId, mUserDisplayName,
                        mUserProfileImage, mConversationId, mMessageId, mMessageUpdatedDate,
                        mMessageTitle, mMessageBody, mAttachmentType, mAttachmentTitle, mAttachmentBody,
                        mAttachmentImage, mAttachmentUri, mAttachmentPlayListId, mAttachmentVideoId,
                        mVideoAttachContentUrl, mPhotoDetails, mInboxRead, mInboxDeleted, mRecipientId, photoListDetails));

                adapter.notifyDataSetChanged();
                inputMsg.setKeyListener(keyListener);

            }
            position = 0;
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to show alert dialog for attaching link.
     */
    public void addLinkBlock() {

        mAttachmentDialogUtil.showAlertDialog();
        mAppConst.showKeyboard();
        final AlertDialog alertDialog = mAttachmentDialogUtil.getAlertDialog();
        mEnterLinkText = mAttachmentDialogUtil.getEnterLinkText();
        // used to prevent the dialog from closing when ok button is clicked (For rename condition)
        Button alertDialogPositiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        alertDialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppConst.hideKeyboard();
                final String linkText = mEnterLinkText.getEditableText().toString().trim();
                //Checking if link is valid or not.
                if (!linkText.isEmpty() && GlobalFunctions.isValidUrl(linkText)) {
                    mAttachUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/attach-link";
                    mAttachType = "link";
                    uriText = mEnterLinkText.getText().toString().trim();
                    alertDialog.dismiss();
                    mAppConst.hideKeyboard();
                    // Uploading files in background with the Message body.
                    new UploadAttachmentUtil(MessageViewActivity.this, mAttachUrl, uriText, mAttachType).execute();

                } else {
                    mEnterLinkText.setError(getResources().getString(R.string.url_not_valid));
                }
            }
        });
    }

    /**
     * Method to start ImageUploadingActivity (MultiImageSelector)
     * @param context Context of Class.
     * @param selectedMode Selected mode i.e. multi images or single image.
     * @param showCamera Whether to display the camera.
     * @param maxNum Max number of images allowed to pick in case of MODE_MULTI.
     */
    public void startImageUploadActivity(Context context, int selectedMode, boolean showCamera, int maxNum){

        Intent intent;

        intent = new Intent(context, MultiMediaSelectorActivity.class);
        // Selection type photo to display items in grid
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
        // Whether photoshoot
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // The maximum number of selectable image
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // Select mode
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // The default selection
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiMediaSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case ConstantVariables.REQUEST_IMAGE:
                if(resultCode == RESULT_OK){

                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                    //Checking if there is any image or not.
                    if (mSelectPath != null) {
                        mAttachmentDialogUtil.showAlertDialogWithAttachments("photo", mVideoTitle, mVideoImage,
                                mVideoDescription, linkTitle, linkUrl, linkImage, linkDescription, mSongTitle, mSelectPath);
                    }

                }else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.image_capturing_failed),
                            Toast.LENGTH_SHORT).show();
                } else if (mSelectPath == null || mSelectPath.isEmpty()){
                    // User cancel the process
                    mAttachType = "";
                }
                break;

            case ConstantVariables.REQUEST_MUSIC:
                if(resultCode == RESULT_OK && data != null){

                    Uri selectedMusicUri = data.getData();
                    if (selectedMusicUri != null) {

                        // getting real path of music file
                        mSelectedMusicFile = GlobalFunctions.getMusicFilePathFromURI(this, selectedMusicUri);
                        mAttachType = "music";
                        mAttachUrl = AppConstant.DEFAULT_URL + "music/playlist/add-song";
                        // Getting attachment details.
                        new UploadAttachmentUtil(MessageViewActivity.this, mAttachUrl, mSelectedMusicFile).execute();

                    }

                }else if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.music_capturing_failed),
                            Toast.LENGTH_SHORT).show();
                } else if (mSelectedMusicFile == null || mSelectedMusicFile.isEmpty()){
                    // User cancel the process
                    mAttachType = "";
                }
                break;
        }

        Bundle bundle = null;

        if(data != null) {
            bundle = data.getExtras();
        }

        // Check Result from Add People and Add Location Activities
        switch (resultCode) {
            case ConstantVariables.REQUEST_VIDEO:

                if(bundle != null) {
                    // Getting video attachment info.
                    mVideoId = bundle.getInt(ConstantVariables.CONTENT_ID);
                    mVideoTitle = bundle.getString(ConstantVariables.CONTENT_TITLE);
                    if (mVideoId != 0 && mVideoTitle != null) {
                        mVideoDescription = bundle.getString(ConstantVariables.DESCRIPTION);
                        mVideoImage = bundle.getString(ConstantVariables.IMAGE);
                        mAttachmentDialogUtil.showAlertDialogWithAttachments("video", mVideoTitle, mVideoImage,
                                mVideoDescription, linkTitle, linkUrl, linkImage, linkDescription, mSongTitle, mSelectPath);
                    }
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_attachments, menu);

        menu.findItem(R.id.submit).setVisible(false);
        menu.findItem(R.id.delete).setVisible(true);
        badgeMenuItem = menu.findItem(R.id.badge);
        attachmentMenuItem = menu.findItem(R.id.add_attachments);
        MenuItemCompat.setActionView(badgeMenuItem, R.layout.attachment_update_count);
        View addedAttachment = MenuItemCompat.getActionView(badgeMenuItem);
        ImageView addedAttachmentImage = addedAttachment.findViewById(R.id.added_attachment_image);
        addedAttachmentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAttachmentDialogUtil.showAlertDialogWithAttachments(mAttachType, mVideoTitle, mVideoImage,
                        mVideoDescription, linkTitle, linkUrl, linkImage, linkDescription, mSongTitle, mSelectPath);
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        mAttachType = mAttachmentDialogUtil.getAttachType();
        if (mAttachType != null && !mAttachType.isEmpty()) {
            badgeMenuItem.setVisible(true);
            attachmentMenuItem.setVisible(false);
        } else {
            attachmentMenuItem.setVisible(true);
            badgeMenuItem.setVisible(false);
        }
        mAttachmentDialogUtil.setOptionsInOptionMenu(menu, photo, video, link, music);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                break;

            case R.id.add_photo:
                mUploadingOption = "photo";
                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    startImageUploadActivity(getApplicationContext(), MultiMediaSelectorActivity.MODE_SINGLE, true, 10);
                }
                break;

            case R.id.add_link:
                addLinkBlock();
                break;

            case R.id.add_video:
                String url;
                List<String> enabledModuleList = null;
                if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
                    enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
                }
                if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                        && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains("sitevideo")) {
                    url = AppConstant.DEFAULT_URL + "advancedvideos/create?post_attach=1&message=1";
                } else {
                    url = AppConstant.DEFAULT_URL + "videos/create?post_attach=1&message=1";
                }
                Intent attachVideoIntent = new Intent(MessageViewActivity.this, CreateNewEntry.class);
                attachVideoIntent.putExtra(ConstantVariables.ATTACH_VIDEO, "attach_video");
                attachVideoIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "message");
                attachVideoIntent.putExtra(ConstantVariables.CREATE_URL, url);
                startActivityForResult(attachVideoIntent, ConstantVariables.REQUEST_VIDEO);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.add_music:
                mUploadingOption = "music";
                /* Check if permission is granted or not */
                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    GlobalFunctions.addMusicBlock(MessageViewActivity.this);
                }
                break;

            case R.id.delete:
                deleteConversationAlert();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == ConstantVariables.WRITE_EXTERNAL_STORAGE) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission granted, proceed to the normal flow
                if (mUploadingOption != null && mUploadingOption.equals("music")) {
                    GlobalFunctions.addMusicBlock(MessageViewActivity.this);
                } else {
                    startImageUploadActivity(getApplicationContext(), MultiMediaSelectorActivity.MODE_SINGLE, true, 10);
                }
            } else{
                // If user press deny in the permission popup
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user After the user
                    // sees the explanation, try again to request the permission.
                    mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    // If user pressed never ask again on permission popup
                    // show snackbar with open app info button
                    // user can revoke the permission from Permission section of App Info.
                    SnackbarUtils.displaySnackbarOnPermissionResult(mContext, btnSend,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAppConst.hideKeyboard();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnSend:
                try {
                    if (inputMsg.getText().length() > 0 && inputMsg.getText() != null && isMessageSent &&
                            !inputMsg.getText().toString().trim().isEmpty()) {

                        Map<String, String> postParams = new HashMap<>();

                        //Removing leading and trailing whitespace from the String..
                        String body = inputMsg.getText().toString().trim();
                        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                        body = new String(bytes, Charset.forName("UTF-8"));

                        // Convert text smileys to emoticons in comments
                        body = Smileys.getEmojiFromString(body);

                        postParams.put(textAreaName, body);

                        if (mAttachType != null && !mAttachType.isEmpty()) {
                            postParams = UploadAttachmentUtil.getAttachmentPostParams(postParams, mAttachType,
                                    uriText, mSongId, mVideoId);
                        }

                        // Clearing the input filed once message was sent
                        inputMsg.setKeyListener(null);
                        mAppConst.hideKeyboard();

                        // Uploading files in background with the Message body.
                        new UploadAttachmentUtil(MessageViewActivity.this, mItemViewUrl, postParams,
                                mSelectPath).execute();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        btnSend.setTextColor(ContextCompat.getColor(mContext, R.color.grey_light));
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        try {
            if(charSequence.length() > 0 && !charSequence.toString().trim().isEmpty()){
                btnSend.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                isMessageSent = true;
            }else {
                btnSend.setTextColor(ContextCompat.getColor(mContext, R.color.grey_light));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onAsyncSuccessResponse(JSONObject response, boolean isRequestSuccessful, boolean isAttachFileRequest) {
        try {
            // If response is for post message and uploading files to server.
            if (isRequestSuccessful && response != null) {
                if (!isAttachFileRequest) {
                    mAttachmentDialogUtil.setAttachtype("");
                    mAttachType = "";
                    invalidateOptionsMenu();
                    isMessageSent = false;
                    inputMsg.setKeyListener(keyListener);
                    inputMsg.setText("");
                    mMessageListDetail.clear();
                    JSONObject bodyJsonObject = response.optJSONObject("body");
                    setDataInAdapter(bodyJsonObject);
                } else {
                    JSONObject mBody = response.optJSONObject("body");
                    if (mBody == null) {
                        JSONArray mBodyArray = response.optJSONArray("body");
                        mBody = mAppConst.convertToJsonObject(mBodyArray);
                    }
                    switch (mAttachType) {
                        case "music":
                            mSongId = mBody.optInt("song_id");
                            mSongTitle = mBody.optString("title");
                            mAttachmentDialogUtil.showAlertDialogWithAttachments("music", mVideoTitle, mVideoImage,
                                    mVideoDescription, linkTitle, linkUrl, linkImage, linkDescription, mSongTitle, mSelectPath);
                            break;

                        case "link":
                            linkUrl = mBody.optString("url");
                            linkTitle = mBody.optString("title");
                            linkDescription = mBody.optString("description");
                            linkImage = mBody.optString("thumb");
                            mAttachmentDialogUtil.showAlertDialogWithAttachments("link", mVideoTitle, mVideoImage,
                                    mVideoDescription, linkTitle, linkUrl, linkImage, linkDescription, mSongTitle, mSelectPath);
                            break;
                    }
                }
            } else {
                inputMsg.setKeyListener(keyListener);
                mSelectedMusicFile = "";
                mAttachType = "";
                assert response != null;
                SnackbarUtils.displaySnackbar(btnSend, response.optString("message"));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    private void deleteConversationAlert() {
        AlertDialogWithAction dialog = new AlertDialogWithAction(mContext);
        dialog.showAlertDialogWithAction(getResources().getString(R.string.delete_conversation_alert_message),
                getResources().getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteConversation();
                    }
                });
    }
    private void deleteConversation() {
        if (inputMsg.getKeyListener() != null) {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("conversation_ids", mConversationObject.optString("conversation_id"));
            String deleteUrl = UrlUtil.MESSAGE_DELETE_URL;
            mAppConst.hideKeyboard();
            mAppConst.showProgressDialog();
            mAppConst.postJsonResponseForUrl(deleteUrl, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();
                    Intent data = new Intent();
                    setResult(ConstantVariables.MESSAGE_DELETED, data);
                    finish();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    SnackbarUtils.displaySnackbar(btnSend, message);
                }
            });
        }
    }
}
