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

package com.fellopages.mobileapp.classes.modules.editor;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;

import org.json.JSONArray;
import org.wordpress.android.editor.EditorMediaUploadListener;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.modules.forum.ForumUtil;

import org.json.JSONObject;
import org.wordpress.android.editor.EditorFragmentAbstract;
import org.wordpress.android.editor.EditorFragmentAbstract.EditorFragmentListener;

import org.wordpress.android.util.helpers.MediaFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewEditorActivity extends FormActivity implements EditorFragmentListener,
        OnUploadResponseListener {

    public static final String EDITOR_PARAM = "EDITOR_PARAM";
    public static final String TITLE_PARAM = "TITLE_PARAM";
    public static final String CONTENT_PARAM = "CONTENT_PARAM";
    public static final String DRAFT_PARAM = "DRAFT_PARAM";
    public static final String TITLE_PLACEHOLDER_PARAM = "TITLE_PLACEHOLDER_PARAM";
    public static final String CONTENT_PLACEHOLDER_PARAM = "CONTENT_PLACEHOLDER_PARAM";
    public static final String POST_URL = "POST_URL";
    public static final String POST_PARAM = "POST_PARAMS";
    public static final String SELECTED_PATHS = "IMAGE_PATHS";
    public static final String PAGE_DETAIL = "PAGE_DETAIL";
    public static final int USE_NEW_EDITOR = 1;
    public static final int EDIT_PAGE = 3;
    public static final int CREATE_PAGE = 4;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE = 1112;

    private static final int SELECT_PHOTO_MENU_POSITION = 0;
    private static final int SELECT_PHOTO_FAIL_MENU_POSITION = 1;

    private EditorFragmentAbstract mEditorFragment;

    private Map<String, String> mFailedUploads;
    private String uploadUrl, forumType, currentSelectedOption;
    private String mContentTitle, mContent;
    private boolean isRequestCompleted = false, isOverview = false;
    private AppConstant mAppConst;
    private Map<String, String> mPostParams;
    private ArrayList<String> mSelectedPath;
    private View mMainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra(EDITOR_PARAM, USE_NEW_EDITOR) == USE_NEW_EDITOR) {
            setContentView(R.layout.activity_new_editor);
        }

        mMainView = findViewById(R.id.editor_main);
        mAppConst = new AppConstant(this);
        mFailedUploads = new HashMap<>();
        mPostParams = new HashMap<>();

        //Fetch Current Selected Module
        currentSelectedOption = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        if (currentSelectedOption == null || currentSelectedOption.isEmpty()) {
            currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(this);
        }

        if(getIntent() != null) {
            uploadUrl = getIntent().getStringExtra(POST_URL);
            mSelectedPath = getIntent().getStringArrayListExtra(SELECTED_PATHS);
            mPostParams = (HashMap<String, String>) getIntent().getSerializableExtra(POST_PARAM);
            forumType = getIntent().getStringExtra("forumType");
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
        }

        isOverview = getIntent().getBooleanExtra("isOverview", false);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof EditorFragmentAbstract) {
            mEditorFragment = (EditorFragmentAbstract) fragment;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, SELECT_PHOTO_MENU_POSITION, 0, getString(R.string.select_photo));
        menu.add(0, SELECT_PHOTO_FAIL_MENU_POSITION, 0, getString(R.string.select_photo_fail));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);

        switch (item.getItemId()) {
            case SELECT_PHOTO_MENU_POSITION:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_photo));

                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_PHOTO_FAIL_MENU_POSITION:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_photo_fail));

                startActivityForResult(intent, ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        Uri imageUri = data.getData();

        MediaFile mediaFile = new MediaFile();
        String mediaId = String.valueOf(System.currentTimeMillis());
        mediaFile.setMediaId(mediaId);

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                mEditorFragment.appendMediaFile(mediaFile, imageUri.toString(), null);

                if (mEditorFragment instanceof EditorMediaUploadListener) {
                    simulateFileUpload(mediaId, imageUri.toString());
                }
                break;
            case ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE:
                mEditorFragment.appendMediaFile(mediaFile, imageUri.toString(), null);

                if (mEditorFragment instanceof EditorMediaUploadListener) {
                    simulateFileUploadFail(mediaId, imageUri.toString());
                }
        }
    }

    @Override
    public void onSettingsClicked() {
        // TODO
    }

    @Override
    public void onAddMediaClicked() {
        // TODO
    }

    @Override
    public void onMediaRetryClicked(String mediaId) {
        if (mFailedUploads.containsKey(mediaId)) {
            simulateFileUpload(mediaId, mFailedUploads.get(mediaId));
        }
    }

    @Override
    public void onMediaUploadCancelClicked(String mediaId) {

    }

    @Override
    public void onEditorFragmentInitialized() {
        // arbitrary setup
        mEditorFragment.setFeaturedImageSupported(true);
        mEditorFragment.setBlogSettingMaxImageWidth("600");
        mEditorFragment.setDebugModeEnabled(true);

        // get title and content and draft switch
        mContentTitle = getIntent().getStringExtra(TITLE_PARAM);
        mContent = getIntent().getStringExtra(CONTENT_PARAM);
        boolean isLocalDraft = getIntent().getBooleanExtra(DRAFT_PARAM, true);
        mEditorFragment.setTitle(mContentTitle);
        mEditorFragment.setContent(mContent);
        mEditorFragment.setTitlePlaceholder(getIntent().getStringExtra(TITLE_PLACEHOLDER_PARAM));
        mEditorFragment.setContentPlaceholder(getIntent().getStringExtra(CONTENT_PLACEHOLDER_PARAM));
        mEditorFragment.setColorTextColorPrimary(getIntent().getIntExtra("textColorPrimary", 0));
        mEditorFragment.setLocalDraft(isLocalDraft);
    }

    @Override
    public void saveMediaFile(MediaFile mediaFile) {
        // TODO
    }

    @Override
    public void uploadDataToServer(String htmlData) {

        mAppConst.hideKeyboard();
        if (isOverview) {
            Intent intent = new Intent(this, CreateNewEntry.class);
            intent.putExtra(ConstantVariables.EXTRA_CREATE_RESPONSE, htmlData);
            if (!htmlData.equals("<html></html>")) {
                setResult(RESULT_OK, intent);
            }
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if(mPostParams != null && !isRequestCompleted){

            if (getIntent().getIntExtra(PAGE_DETAIL, CREATE_PAGE) == CREATE_PAGE) {
                // uploading the file to server
                if( htmlData.equals("<html></html>") && forumType != null &&
                        forumType.equals("post_reply") && mSelectedPath != null && mSelectedPath.size()==0) {
                    Toast.makeText(this,getResources().getString(R.string.field_blank_msg),
                            Toast.LENGTH_SHORT).show();
                }else {
                    new UploadFileToServerUtils(NewEditorActivity.this, uploadUrl, htmlData, mSelectedPath,
                            mPostParams).execute();
                }
            } else {
                Intent intent = new Intent(this, EditEntry.class);
                intent.putExtra(ConstantVariables.EXTRA_CREATE_RESPONSE, htmlData);
                setResult(ConstantVariables.EDIT_ENTRY_RETURN_CODE, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        }
    }

    private void simulateFileUpload(final String mediaId, final String mediaUrl) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    float count = (float) 0.1;
                    while (count < 1.1) {
                        sleep(500);

                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress(mediaId, count);

                        count += 0.1;
                    }

                    ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(mediaId, mediaUrl);

                    if (mFailedUploads.containsKey(mediaId)) {
                        mFailedUploads.remove(mediaId);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void simulateFileUploadFail(final String mediaId, final String mediaUrl) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    float count = (float) 0.1;
                    while (count < 0.6) {
                        sleep(500);

                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress(mediaId, count);

                        count += 0.1;
                    }

                    ((EditorMediaUploadListener) mEditorFragment).onMediaUploadFailed(mediaId);

                    mFailedUploads.put(mediaId, mediaUrl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    @Override
    public void onUploadResponse(final JSONObject jsonObject, boolean isRequestSuccessful) {

        if (isRequestSuccessful) {
            isRequestCompleted = true;
            if (currentSelectedOption.equals(ConstantVariables.FORUM_MENU_TITLE)) {

                String message;
                ForumUtil.increaseProfilePageCounter();
                if (forumType.equals("move_topic")) {
                    message = getResources().getString(R.string.success_move_topic);
                } else {
                    ForumUtil.increaseViewTopicPageCounter();
                    message = getResources().getString(R.string.success_forum_create);
                }
                SnackbarUtils.displaySnackbarShortWithListener(mMainView, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
            } else {
                loadDefaultActivity(jsonObject);
            }

        } else if (jsonObject.has("showValidation")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject validationMessages = jsonObject.optJSONObject("message");
                    if (validationMessages != null) {
                        showValidations(validationMessages);
                    } else {
                        SnackbarUtils.displaySnackbar(mMainView, jsonObject.optString("message"));
                    }
                }
            });

        } else {
            isRequestCompleted = true;
            SnackbarUtils.displaySnackbarShortWithListener(mMainView, jsonObject.optString("message"),
                    new SnackbarUtils.OnSnackbarDismissListener() {
                        @Override
                        public void onSnackbarDismissed() {
                            finish();
                        }
                    });
        }
    }

    public void loadDefaultActivity(JSONObject jsonObject){

        Intent viewIntent = null;
        int content_id;

        JSONObject body = jsonObject.optJSONObject("body");
        JSONObject mDataResponse = body.optJSONObject("response");
        if (mDataResponse == null) {
            JSONArray mDataResponseArray = body.optJSONArray("response");
            mDataResponse = mAppConst.convertToJsonObject(mDataResponseArray);
        }
        content_id = GlobalFunctions.getIdOfModule(mDataResponse, currentSelectedOption);

        switch (currentSelectedOption){
            case "core_main_blog":
            case "core_main_classified":
                viewIntent = GlobalFunctions.getIntentForModule(this, content_id, currentSelectedOption, null);
                break;
        }
        assert viewIntent != null;
        viewIntent.putExtra(ConstantVariables.EXTRA_CREATE_RESPONSE, jsonObject.toString());
        viewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, currentSelectedOption);
        setResult(RESULT_OK);
        finish();
        startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

}
