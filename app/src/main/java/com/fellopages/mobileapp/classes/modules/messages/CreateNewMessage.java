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
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.AttachmentDialogUtil;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.interfaces.OnAsyncResponseListener;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SelectedFriendList;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadAttachmentUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateNewMessage extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener,
        View.OnClickListener, OnAsyncResponseListener {

    public static final String SCHEMA_KEY_SENDTO = "toValues";
    public static final String SCHEMA_KEY_SUBJECT = "title";
    public static final String SCHEMA_KEY_TEXTAREA = "body";
    private AppConstant mAppConst;
    private String createFormUrl, mAttachUrl, mAttachType = "", uriText, mUploadingOption = "";
    private boolean isRecipientBlank = true,isBodyBlank = true;
    private Toolbar mToolbar;
    private LinearLayout main_msg_view;
    private String mSongTitle, linkUrl, linkImage,  linkTitle, linkDescription, mSelectedMusicFile,
            mVideoTitle, mVideoImage, mVideoDescription;
    private int mVideoId, mSongId;
    private int link = 0, music = 0, video = 0, photo = 0;

    private ArrayList<String> mSelectPath;
    private ListView mFriendListView;
    private RecyclerView mAddedFriendListView;

    private SelectedFriendListAdapter mSelectedFriendListAdapter;
    private List<SelectedFriendList> mSelectedFriendList;
    private AddPeopleAdapter mAddPeopleAdapter;
    private List<AddPeopleList> mAddPeopleList;
    private Map<String, String> selectedFriends;

    private JSONArray responseArray,friendListResponse;
    private ArrayList<String> fieldNameList;
    private Map<Integer, String> showNonSelectedFriend;
    private Map<String, String> mPostParams;

    private String sendToName, mUserDisplayName;
    private int sendToUserId;
    private boolean isSendMessageRequest = false, isStoryReply = false;
    private MenuItem badgeMenuItem, attachmentMenuItem;
    private JSONObject mSenderDataResponse;
    private AttachmentDialogUtil mAttachmentDialogUtil;
    private AlertDialogWithAction mAlertDialogWithAction;
    protected EditText mSubjectView, mRecipientView, mBodyView, mEnterLinkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_message);

        mAppConst = new AppConstant(this);
        showNonSelectedFriend = new HashMap<>();
        mAttachmentDialogUtil = new AttachmentDialogUtil(CreateNewMessage.this);
        mAlertDialogWithAction = new AlertDialogWithAction(this);

        fieldNameList = new ArrayList<>();
        mAddPeopleList = new ArrayList<>();
        mSelectedFriendList = new ArrayList<>();
        selectedFriends = new HashMap<>();
        mPostParams = new HashMap<>();
        isStoryReply = getIntent().getBooleanExtra("isStoryReply", false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (isStoryReply) {
                getSupportActionBar().setTitle(getResources().getString(R.string.replied_to_story));
            }
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        isSendMessageRequest = getIntent().getBooleanExtra("isSendMessageRequest", false);
        sendToUserId = getIntent().getIntExtra(ConstantVariables.USER_ID, 0);
        sendToName = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);

        if (isStoryReply) {
            mPostParams.put("story_id", getIntent().getStringExtra(ConstantVariables.STORY_ID));
        }

        //Views
        main_msg_view = (LinearLayout) findViewById(R.id.main_msg_view);
        mRecipientView = (EditText) findViewById(R.id.sendTo);
        mSubjectView = (EditText) findViewById(R.id.subject);
        mBodyView = (EditText) findViewById(R.id.msgDescription);

        mFriendListView = (ListView) findViewById(R.id.friendListView);
        mAddPeopleAdapter = new AddPeopleAdapter(this, R.layout.list_friends, mAddPeopleList);
        mFriendListView.setAdapter(mAddPeopleAdapter);

        mAddedFriendListView = (RecyclerView) findViewById(R.id.addedFriendList);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mAddedFriendListView.setLayoutManager(layoutManager);
        mSelectedFriendListAdapter = new SelectedFriendListAdapter(mSelectedFriendList,
                mAddedFriendListView,selectedFriends, showNonSelectedFriend, isSendMessageRequest);
        mAddedFriendListView.setAdapter(mSelectedFriendListAdapter);


        String getMessageFormUrl = AppConstant.DEFAULT_URL + "messages/compose?post_attach=1";
        createFormUrl = AppConstant.DEFAULT_URL + "messages/compose";
        if (isStoryReply) {
            createFormUrl += "?isStory=1";
            getMessageFormUrl += "&isStory=1";
            selectedFriends.put(Integer.toString(sendToUserId), sendToName);
        }

        /*
        Code to Send Request for Create Form
         */
        mAppConst.getJsonResponseFromUrl(getMessageFormUrl , new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                responseArray = jsonObject.optJSONArray("response");
                if (responseArray == null) {
                    responseArray = jsonObject.optJSONArray("form");
                }
                if (responseArray != null) {
                    for (int j = 0; j < responseArray.length(); j++) {
                        JSONObject formBody = responseArray.optJSONObject(j);
                        String fieldName = formBody.optString("name");
                        String fieldLabel = formBody.optString("label");
                        fieldNameList.add(fieldName);
                        setViewValue(fieldLabel, fieldName);
                    }
                }
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                main_msg_view.setVisibility(View.VISIBLE);

                //If it is send message request from UserProfile page then setting selected user in the "send to" field
                if (isSendMessageRequest) {
                    mAddedFriendListView.setVisibility(View.VISIBLE);
                    selectedFriends.put(Integer.toString(sendToUserId), sendToName);
                    mSelectedFriendList.add(new SelectedFriendList(sendToUserId, sendToName));
                    mSelectedFriendListAdapter.notifyDataSetChanged();
                    // disable sentTo EditText field
                    mRecipientView.setEnabled(false);
                }
                JSONObject feedPostMenuJsonObject = jsonObject.optJSONObject("feed_post_menu");
                if (feedPostMenuJsonObject != null) {
                    photo = feedPostMenuJsonObject.optInt("photo");
                    video = feedPostMenuJsonObject.optInt("video");
                    music = feedPostMenuJsonObject.optInt("music");
                    link = feedPostMenuJsonObject.optInt("link");
                    invalidateOptionsMenu();
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Log.d("Logger 3 ", "true");
                SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.create_form),
                        message, new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });

        //Setting the listeners
        mRecipientView.addTextChangedListener(this);
        mFriendListView.setOnItemClickListener(this);
    }

    public void setViewValue( String fieldLabel, String fieldName){

        switch (fieldName){
            case SCHEMA_KEY_SENDTO:
                mRecipientView.setHint(fieldLabel);
                mRecipientView.setTag(fieldName);
                break;

            case SCHEMA_KEY_SUBJECT:
                mSubjectView.setHint(fieldLabel);
                mSubjectView.setTag(fieldName);
                break;

            case SCHEMA_KEY_TEXTAREA:
                mBodyView.setHint(fieldLabel);
                mBodyView.setTag(fieldName);
                break;
        }
        if (isStoryReply) {
            mRecipientView.setVisibility(View.GONE);
            mSubjectView.setHint(getResources().getString(R.string.replied_to_story));
            mSubjectView.setHintTextColor(ContextCompat.getColor(this, R.color.black));
            mSubjectView.setClickable(false);
            mSubjectView.setEnabled(false);
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
                    new UploadAttachmentUtil(CreateNewMessage.this, mAttachUrl, uriText, mAttachType).execute();
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
                        new UploadAttachmentUtil(CreateNewMessage.this, mAttachUrl, mSelectedMusicFile).execute();
                    }

                }else if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.music_capturing_failed),
                            Toast.LENGTH_SHORT).show();
                } else if (mSelectedMusicFile == null || mSelectedMusicFile.isEmpty()) {
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

        badgeMenuItem = menu.findItem(R.id.badge);
        attachmentMenuItem = menu.findItem(R.id.add_attachments);
        MenuItemCompat.setActionView(badgeMenuItem, R.layout.attachment_update_count);
        View addedAttachment = MenuItemCompat.getActionView(badgeMenuItem);
        ImageView addedAttachmentImage = (ImageView) addedAttachment.findViewById(R.id.added_attachment_image);
        addedAttachmentImage.setOnClickListener(this);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem submit = menu.findItem(R.id.submit);
        Drawable drawable = submit.getIcon();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);

        mAttachType = mAttachmentDialogUtil.getAttachType();
        if(isStoryReply) {
            attachmentMenuItem.setVisible(false);
            badgeMenuItem.setVisible(false);
        } else if (mAttachType != null && !mAttachType.isEmpty()) {
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
        switch (id) {
            case R.id.submit:
                sendMessage();
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
                if (PreferencesUtils.getEnabledModuleList(this) != null) {
                    enabledModuleList = new ArrayList<>(Arrays.asList(PreferencesUtils.getEnabledModuleList(this).split("\",\"")));
                }
                if (enabledModuleList != null && enabledModuleList.contains("sitevideo")
                        && !Arrays.asList(ConstantVariables.DELETED_MODULES).contains("sitevideo")) {
                    url = AppConstant.DEFAULT_URL + "advancedvideos/create?post_attach=1&message=1";
                } else {
                    url = AppConstant.DEFAULT_URL + "videos/create?post_attach=1&message=1";
                }

                Intent attachVideoIntent = new Intent(CreateNewMessage.this, CreateNewEntry.class);
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
                    GlobalFunctions.addMusicBlock(CreateNewMessage.this);
                }
                break;
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(CreateNewMessage.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(CreateNewMessage.this);
                }
                return true;
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
                    GlobalFunctions.addMusicBlock(CreateNewMessage.this);
                } else {
                    startImageUploadActivity(getApplicationContext(), MultiMediaSelectorActivity.MODE_SINGLE, true, 10);
                }
            } else{
                // If user press deny in the permission popup
                if (ActivityCompat.shouldShowRequestPermissionRationale(CreateNewMessage.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user After the user
                    // sees the explanation, try again to request the permission.

                    mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }else{
                    // If user pressed never ask again on permission popup
                    // show snackbar with open app info button
                    // user can revoke the permission from Permission section of App Info.
                    SnackbarUtils.displaySnackbarOnPermissionResult(CreateNewMessage.this,
                            findViewById(R.id.create_form), ConstantVariables.WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        mAttachmentDialogUtil.showAlertDialogWithAttachments(mAttachType, mVideoTitle, mVideoImage,
                mVideoDescription, linkTitle, linkUrl, linkImage, linkDescription, mSongTitle, mSelectPath);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAppConst.hideKeyboard();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence searchText, int start, int before, int count) {
        if(searchText != null && searchText.length() != 0) {
            HashMap<String, String> searchTextParams = new HashMap<>();
            searchTextParams.put("search", searchText.toString());

            String getFriendsUrl = UrlUtil.GET_USERS_LIST;
            getFriendsUrl = mAppConst.buildQueryString(getFriendsUrl, searchTextParams);
            getFriendList(getFriendsUrl);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void getFriendList(String url) {

        findViewById(R.id.sentToLoadingProgressBar).setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject body) {
                if (body != null && body.length() != 0) {
                    mAddPeopleList.clear();
                    findViewById(R.id.sentToLoadingProgressBar).setVisibility(View.GONE);
                    friendListResponse = body.optJSONArray("response");
                    for (int i = 0; i < friendListResponse.length(); i++) {
                        JSONObject friendObject = friendListResponse.optJSONObject(i);
                        String username = friendObject.optString("label");
                        int userId = friendObject.optInt("id");
                        String userImage = friendObject.optString("image_icon");

                        try {
                            if (!showNonSelectedFriend.isEmpty()) {
                                if (!showNonSelectedFriend.containsKey(userId)) {
                                    mAddPeopleList.add(new AddPeopleList(userId, username, userImage));
                                }
                            } else {
                                mAddPeopleList.add(new AddPeopleList(userId, username, userImage));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    mAddPeopleAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.sentToLoadingProgressBar).setVisibility(View.GONE);
                Log.d("Logger 4 ", "true");
                SnackbarUtils.displaySnackbar(findViewById(R.id.create_form), message);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        AddPeopleList addPeopleList = mAddPeopleList.get(position);
        String label = addPeopleList.getmUserLabel();
        int userId = addPeopleList.getmUserId();

        if(mAddPeopleAdapter != null){
            mAddPeopleAdapter.clear();
        }
        mAddedFriendListView.setVisibility(View.VISIBLE);
        if(!selectedFriends.containsKey(Integer.toString(userId))) {
            selectedFriends.put(Integer.toString(userId), label);
            showNonSelectedFriend.put(userId, label);
            mSelectedFriendList.add(new SelectedFriendList(userId, label));
            mSelectedFriendListAdapter.notifyDataSetChanged();
        }
        mRecipientView.setText("");
    }

    public void sendMessage(){

        mAppConst.hideKeyboard();
        checkValidation();

        if(!isRecipientBlank){
            Set<String> keySet = selectedFriends.keySet();
            String res = "";
            for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
                res += iterator.next() + (iterator.hasNext() ? "," : "");
            }

            try {
                String body = mBodyView.getText().toString().trim();
                byte[] bytes = body.getBytes("UTF-8");
                body = new String(bytes, Charset.forName("UTF-8"));

                // Convert text smileys to emoticons in comments
                body = Smileys.getEmojiFromString(body);

                mPostParams.put(mRecipientView.getTag().toString(),res);
                mPostParams.put(mSubjectView.getTag().toString(),mSubjectView.getText().toString());
                mPostParams.put(mBodyView.getTag().toString(), body);

                if (mAttachType != null && !mAttachType.isEmpty()) {
                    mPostParams = UploadAttachmentUtil.getAttachmentPostParams(mPostParams, mAttachType,
                            uriText, mSongId, mVideoId);
                }

                // Uploading files in background with the Message details.
                new UploadAttachmentUtil(CreateNewMessage.this, createFormUrl, mPostParams,
                        mSelectPath).execute();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkValidation(){

        if(selectedFriends.size() <= 0 && !isStoryReply){
            mRecipientView.setError(getResources().getString(R.string.field_blank_msg));
            isRecipientBlank = true;
        }else{
            mRecipientView.setError(null);
            isRecipientBlank = false;
        }
//        if(mBodyView.getText().length() == 0 || mBodyView.getText().toString().trim().isEmpty()){
//            mBodyView.setError(getResources().getString(R.string.field_blank_msg));
//            isBodyBlank = true;
//        }else {
//            mBodyView.setError(null);
//            isBodyBlank = false;
//        }
    }

    @Override
    public void onAsyncSuccessResponse(JSONObject response, boolean isRequestSuccessful, boolean isAttachFileRequest) {
        try {
            // If response is for create message and uploading files to server.
            if (!isAttachFileRequest) {
                if (isRequestSuccessful) {
                    mAttachmentDialogUtil.setAttachtype("");
                    mAttachType = "";
                    invalidateOptionsMenu();
                    if (!isStoryReply) {
                        JSONObject bodyJsonObject = response.optJSONObject("body");
                        JSONObject conversationJSONObject = bodyJsonObject.optJSONObject("conversation");
                        int conversationId = conversationJSONObject.optInt("conversation_id");
                        JSONArray messagesJSONArray = bodyJsonObject.optJSONArray("messages");
                        for (int i = 0; i < messagesJSONArray.length(); i++) {
                            JSONObject jsonDataObject = messagesJSONArray.optJSONObject(i);
                            mSenderDataResponse = jsonDataObject.optJSONObject("sender");
                            mUserDisplayName = mSenderDataResponse.optString("displayname");
                        }
                        Intent mainIntent = new Intent(CreateNewMessage.this, MessageViewActivity.class);
                        String viewUrl = AppConstant.DEFAULT_URL + "messages/view/id/" +
                                conversationId + "?gutter_menu=1";
                        mainIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewUrl);
                        if (selectedFriends.size() > 1) {
                            mainIntent.putExtra("UserName", mUserDisplayName);
                        } else {
                            Map.Entry<String,String> entry = selectedFriends.entrySet().iterator().next();
                            mainIntent.putExtra("UserName", entry.getValue());
                        }
                        finish();
                        startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else {
                        SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.create_form),
                                getResources().getString(R.string.story_reply_success),
                                new SnackbarUtils.OnSnackbarDismissListener() {
                                    @Override
                                    public void onSnackbarDismissed() {
                                        finish();
                                    }
                                });
                    }

                } else {
                    String message;
                    if (response.optString("message").contains("RESPONSE_MESSAGE_VALIDATION_FAIL: ")){
                        message = response.optString("message")
                                .replace("RESPONSE_MESSAGE_VALIDATION_FAIL: ", "");
                    } else {
                        message = response.optString("message");
                    }
                    SnackbarUtils.displaySnackbar(findViewById(R.id.create_form),
                            message);
                }
            } else {
                if (isRequestSuccessful) {
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
                } else {
                    mSelectedMusicFile = "";
                    mAttachType = "";
                    assert response != null;
                    Log.d("Logger 1 ", "true");
                    SnackbarUtils.displaySnackbar(findViewById(R.id.create_form),
                            response.optString("message"));
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
