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
 *
 */

package com.fellopages.mobileapp.classes.common.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.CustomImageAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsAvailableTickets;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsProfilePage;
import com.fellopages.mobileapp.classes.modules.editor.NewEditorActivity;
import com.fellopages.mobileapp.classes.modules.forum.ForumUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditEntry extends FormActivity implements OnUploadResponseListener {

    private String editFormUrl,currentSelectedOption, mFormType, mToolBarTitle = "", mSuccessMessage;
    static EditEntry editEntry;
    private AppConstant mAppConst;
    private HashMap<String, String> postParams;
    private static ArrayList<String> mSelectPath,mSelectedMusicFiles;
    private View mSelectFileInflatedView;
    private CustomImageAdapter mCustomImageAdapter, mMusicImageAdapter;
    private int columnWidth, editForumPosition, mSelectMode, mRequestCode, mContentId;
    private Intent intent;
    private Toolbar mToolbar;
    private RelativeLayout editFormView;
    private String mBodyString=null, scheduleFormValue;
    private Context mFormActivityContext;
    private boolean isStatusPage = false, mIsFormLoaded = false;
    private boolean mShowCamera;
    private boolean isFromWebViewPayment = false;
    private String mUploadingOption, property, mSelectedFilePath;
    private HashMap<String, ArrayList> mHashMap;
    private AlertDialogWithAction mAlertDialogWithAction;
    private int mEventId;
    private boolean isFromEventList = false;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);


        /* Create Back Button On Action Bar **/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSelectPath = new ArrayList<>();
        mSelectedMusicFiles = new ArrayList<>();
        mHashMap = new HashMap<>();

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editFormView = (RelativeLayout)findViewById(R.id.form_view);
        isFromWebViewPayment = getIntent().getBooleanExtra("isFromWebViewPayment", false);
        mFormType = getIntent().getStringExtra(ConstantVariables.FORM_TYPE);
        mRequestCode = getIntent().getIntExtra(ConstantVariables.REQUEST_CODE, ConstantVariables.PAGE_EDIT_CODE);
        isStatusPage = getIntent().getBooleanExtra("isStatusPage", false);

        isFromEventList = getIntent().getBooleanExtra("isFromEventList", false);

        scheduleFormValue = getIntent().getStringExtra("form_value");

        //Fetch Current Selected Module
        currentSelectedOption = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        if (currentSelectedOption == null || currentSelectedOption.isEmpty()) {
            currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(this);
        }
        mContentId = getIntent().getIntExtra(ConstantVariables.CONTENT_ID, 0);
        mAppConst = new AppConstant(this);
        editEntry = this;
        mAlertDialogWithAction = new AlertDialogWithAction(EditEntry.this);
        editFormUrl  = getIntent().getStringExtra(ConstantVariables.URL_STRING);
        mEventId = getIntent().getIntExtra("isAdvEventId", mEventId);

        switch (currentSelectedOption) {
            case ConstantVariables.FORUM_MENU_TITLE:
            case ConstantVariables.MLT_MENU_TITLE:
            case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
            case ConstantVariables.ADV_GROUPS_MENU_TITLE:
            case ConstantVariables.SITE_PAGE_MENU_TITLE:
            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
            case ConstantVariables.PRODUCT_MENU_TITLE:
            case ConstantVariables.STORE_MENU_TITLE:
            case ConstantVariables.DIARY_MENU_TITLE:
            case ConstantVariables.ADV_EVENT_PAYMENT_METHOD:
            case ConstantVariables.PAYMENT_METHOD_CONFIG:
            case ConstantVariables.HOME_MENU_TITLE:
            case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
            case ConstantVariables.CONTACT_INFO_MENU_TITLE:

                if (mFormType != null && !mFormType.isEmpty()) {
                    switch (mFormType) {
                        case "rename_topic":
                            mToolBarTitle = getResources().getString(R.string.title_rename_topic);
                            mSuccessMessage = getResources().getString(R.string.successful_edit);
                            break;
                        case "edit_post":
                            mToolBarTitle = getResources().getString(R.string.title_edit_post);
                            mSuccessMessage = getResources().getString(R.string.successful_edit);
                            editForumPosition = getIntent().getIntExtra(ConstantVariables.ITEM_POSITION, 0);
                            break;
                        case "update_review":
                            mToolBarTitle = getResources().getString(R.string.update_review_title);
                            mSuccessMessage = getResources().getString(R.string.review_update_success_message);
                            break;

                        case "apply_now":
                            mToolBarTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
                            mSuccessMessage = getResources().getString(R.string.apply_success_message);
                            break;
                        case "claim_listing":
                            mToolBarTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
                            mSuccessMessage = getResources().getString(R.string.claim_listing_success_message);
                            break;
                        case "tellafriend":
                            mSuccessMessage = getResources().getString(R.string.tell_friend_success_message);
                            mToolBarTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
                            break;

                        case "sellingForm":
                            if (getIntent().getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT) != null) {
                                mSelectPath = getIntent().getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                            }
                        case "edit_wishlist":
                        case "edit_listing":
                        case "scheduleForm":
                        case "targetForm":
                            mToolBarTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
                            break;
                        case "notification_settings":
                            mToolBarTitle = getResources().getString(R.string.title_notification_and_email_settings);
                            mSuccessMessage = getResources().getString(R.string.notification_setting_success_message);
                            break;
                        case "edit_diary":
                            mToolBarTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
                            break;
                        case "edit_event":
                            mToolBarTitle = getResources().getString(R.string.edit_event_title);
                            break;
                        case "buyer_form":
                            mToolBarTitle = getResources().getString(R.string.action_bar_title_buyer_info);
                            break;
                        case "payment_config_method":
                            mToolBarTitle = getResources().getString(R.string.payment_method_title);
                            mSuccessMessage = getResources().getString(R.string.payment_method_success_message);
                            break;
                        case "edit_store":
                            mToolBarTitle = getResources().getString(R.string.edit_store);
                            break;
                        case ConstantVariables.PAYMENT_METHOD_CONFIG:
                            mToolBarTitle = getResources().getString(R.string.payment_methods_label);
                            mSuccessMessage = getResources().getString(R.string.payment_method_configuration_message);
                            break;
                        case ConstantVariables.EDIT_METHOD:
                            mToolBarTitle = getResources().getString(R.string.edit_shipping_method);
                            mSuccessMessage = getResources().getString(R.string.edited_shipping_method_message);
                            break;
                        case ConstantVariables.EDIT_PRODUCT:
                            mToolBarTitle = getResources().getString(R.string.edit_product_label);
                            mSuccessMessage = getResources().getString(R.string.product_edited_message);
                            break;
                        case "edit_file":
                            mToolBarTitle = getResources().getString(R.string.edit_file);
                            mSuccessMessage = getResources().getString(R.string.file_edited_message);
                            break;

                        case "story_setting":
                            mToolBarTitle = getIntent().getStringExtra(ConstantVariables.CONTENT_TITLE);
                            mSuccessMessage = getResources().getString(R.string.story_setting_success_message);
                            break;
                        case "contact_info":
                            mToolBarTitle = getResources().getString(R.string.edit_contact_title);
                            mSuccessMessage = getResources().getString(R.string.edit_contact_success_msg);
                            break;
                    }
                } else {
                    mToolBarTitle = getResources().getString(R.string.edit);
                }
                break;

            default:
                mToolBarTitle = getResources().getString(R.string.edit);
                break;
        }

        getSupportActionBar().setTitle(mToolBarTitle);
        CustomViews.createMarqueeTitle(this, mToolbar);

        /*
        Code to Send Request for Edit Form
         */
        if (Status.FORM_OBJECT != null && Status.FORM_OBJECT.length() > 0) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            mIsFormLoaded = true;
            editFormView.addView(populate(Status.FORM_OBJECT, mFormType));
        } else {
            makeRequest();
        }
    }

    private void makeRequest() {
        mAppConst.getJsonResponseFromUrl(editFormUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                FormActivity.setFormObject(jsonObject);
                mIsFormLoaded = true;
                switch (currentSelectedOption) {

                    case ConstantVariables.FORUM_MENU_TITLE:
                    case ConstantVariables.BLOG_MENU_TITLE:
                    case ConstantVariables.CLASSIFIED_MENU_TITLE:
                        mBodyString = jsonObject.optJSONObject("formValues").optString("body");
                        editFormView.addView(populate(jsonObject, currentSelectedOption));
                        editFormView.findViewById(R.id.add_description).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openEditor();
                            }
                        });
                        break;

                    case ConstantVariables.MLT_MENU_TITLE:
                    case ConstantVariables.PRODUCT_MENU_TITLE:
                    case ConstantVariables.STORE_MENU_TITLE:
                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    case ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE:
                        if (mFormType != null && !mFormType.isEmpty()) {
                            switch (mFormType) {
                                case "update_review":
                                case "notification_settings":
                                case "buyer_form":
                                    editFormView.addView(populate(jsonObject, mFormType));
                                    break;
                                case ConstantVariables.EDIT_METHOD:
                                    editFormView.addView(populate(jsonObject, ConstantVariables.SHIPPING_METHOD));
                                    break;
                                case ConstantVariables.EDIT_PRODUCT:
                                    editFormView.addView(populate(jsonObject, ConstantVariables.EDIT_PRODUCT));
                                    break;
                                default:
                                    editFormView.addView(populate(jsonObject, currentSelectedOption));
                            }
                        } else {
                            editFormView.addView(populate(jsonObject, currentSelectedOption));
                        }
                        break;
                    case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
                    case ConstantVariables.CONTACT_INFO_MENU_TITLE:
                        if (mFormType !=  null && !mFormType.isEmpty() && (mFormType.equals("update_review")
                                || mFormType.equals("notification_settings") || mFormType.equals("buyer_form") ||
                                mFormType.equals("tellafriend"))) {
                            editFormView.addView(populate(jsonObject, mFormType));

                        } else {
                            editFormView.addView(populate(jsonObject, currentSelectedOption));
                        }
                        break;

                    case ConstantVariables.HOME_MENU_TITLE:
                        if (isStatusPage && mFormType !=  null && !mFormType.isEmpty()
                                && (mFormType.equals("scheduleForm")
                                || mFormType.equals("targetForm")
                                || mFormType.equals("sellingForm"))) {
                            Status.FORM_OBJECT = jsonObject.optJSONObject("form");
                            if (scheduleFormValue != null && !scheduleFormValue.isEmpty()) {
                                try {
                                    Status.FORM_OBJECT.put("formValues", new JSONObject(scheduleFormValue));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            invalidateOptionsMenu();
                            editFormView.addView(populate(Status.FORM_OBJECT, mFormType));
                        } else {
                            editFormView.addView(populate(jsonObject, currentSelectedOption));
                        }
                        break;

                    default:
                        editFormView.addView(populate(jsonObject, currentSelectedOption));
                        break;
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                mIsFormLoaded = false;
                SnackbarUtils.displaySnackbarLongWithListener(editFormView, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                setResult(ConstantVariables.REQUEST_CANCLED);
                                finish();
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.save_menu_title));
        menu.findItem(R.id.skip).setTitle(getResources().getString(R.string.skip));
        return true;
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        if (currentSelectedOption.equals("adv_event_payment_method")){
            menu.findItem(R.id.skip).setVisible(true);
        }

        if (isStatusPage && Status.FORM_OBJECT != null && Status.FORM_OBJECT.length() > 0) {
            menu.findItem(R.id.remove).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.submit:
                if (mIsFormLoaded) {
                    if (!isStatusPage) {
                        editEntry();
                    } else {
                        checkForStatusOptions();
                    }
                }
                break;

            case R.id.remove:
                if (Status.FORM_OBJECT.has("formValues")) {
                    Status.FORM_OBJECT.remove("formValues");
                }
                postParams = new HashMap<>();
                if (!isFinishing() && isStatusPage && Status.FORM_OBJECT != null && Status.FORM_OBJECT.length() > 0) {
                    Intent intent = new Intent();
                    if (mFormType.equals("sellingForm") && mSelectPath != null) {
                        mSelectPath.clear();
                        intent.putStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT, mSelectPath);
                    }
                    intent.putExtra("param", postParams);
                    intent.putExtra(ConstantVariables.FORM_TYPE, mFormType);
                    setResult(ConstantVariables.STATUS_FORM_CODE, intent);
                    finish();
                }
                break;
            case R.id.skip:
                redirectToAdvEventsAvailableTickets();

                break;
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(EditEntry.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(EditEntry.this);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to check for status post options.
     */
    private void checkForStatusOptions() {
        mAppConst.hideKeyboard();
        postParams = new HashMap<>();
        postParams = save();

        if (postParams != null && postParams.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra("param", postParams);
            intent.putExtra(ConstantVariables.FORM_TYPE, mFormType);
            if (mSelectPath != null && mFormType.equals("sellingForm")) {
                intent.putStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT, mSelectPath);
            }
            setResult(ConstantVariables.STATUS_FORM_CODE, intent);
            finish();
        }

    }

    public void editEntry() {
        mAppConst.hideKeyboard();
        postParams = new HashMap<>();
        postParams = save();

        //update the url for updating payment method configuration
        if(currentSelectedOption.equals("adv_event_payment_method") && mFormType != null
                && mFormType.equals("payment_config_method")) {
            editFormUrl = UrlUtil.PAYMENT_METHOD_CONFIG_UPDATE_URL + "event_id=" + mEventId;

        } else if(currentSelectedOption.equals(ConstantVariables.PAYMENT_METHOD_CONFIG)){
            editFormUrl = getIntent().getStringExtra(ConstantVariables.EDIT_URL_STRING);
        }
        if(FormActivity.selectedProducts != null && FormActivity.selectedProducts.size() > 0) {
            postParams.put("product_ids", android.text.TextUtils.join(",", FormActivity.selectedProducts));
        }
        if (postParams != null) {
            // uploading the file to server
            new UploadFileToServerUtils(EditEntry.this, editFormUrl, currentSelectedOption,
                    (mFormType != null && mFormType.equals("edit_event")), mBodyString,
                    mSelectedFilePath, mSelectPath, mSelectedMusicFiles, postParams, mHashMap).execute();
        }
    }

    public void openEditor(){

        postParams = save();

        if(postParams != null){

            Intent intent = new Intent(EditEntry.this, NewEditorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(NewEditorActivity.POST_URL, editFormUrl);
            bundle.putStringArrayList(NewEditorActivity.SELECTED_PATHS, mSelectPath);
            bundle.putString(NewEditorActivity.TITLE_PARAM, "");
            bundle.putString(NewEditorActivity.CONTENT_PARAM, mBodyString);
            bundle.putSerializable(NewEditorActivity.POST_PARAM, postParams);
            bundle.putString(NewEditorActivity.TITLE_PLACEHOLDER_PARAM,
                    getString(R.string.example_post_title_placeholder));
            bundle.putString(NewEditorActivity.CONTENT_PLACEHOLDER_PARAM,
                    getString(R.string.post_content_placeholder) + "…");
            bundle.putInt(NewEditorActivity.EDITOR_PARAM, NewEditorActivity.USE_NEW_EDITOR);
            bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, currentSelectedOption);
            bundle.putInt(NewEditorActivity.PAGE_DETAIL, NewEditorActivity.EDIT_PAGE);
            bundle.putString("forumType", mFormType);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantVariables.EDITOR_REQUEST_CODE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    public void checkPermission(Context context,int selectedMode,boolean showCamera, String uploadingOption,
                                String name, View configView){

        mFormActivityContext = context;
        mSelectMode = selectedMode;
        mShowCamera = showCamera;
        mUploadingOption = uploadingOption;
        mSelectFileInflatedView = configView;
        property = name;

        /* Check if permission is granted or not */
        if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
        }else{
            switch(mUploadingOption){
                case "photo":
                    startImageUploadActivity();
                    break;
                case "music":
                    GlobalFunctions.addMusicBlock(mFormActivityContext);
                    break;
                case "file":
                    GlobalFunctions.openDocumentUploadingIntent(EditEntry.this, configView);
                    break;
            }
        }
    }

    /**
     * Method to show selected images.
     * @param context Context of calling class.
     * @param name Name of field.
     * @param configView Inflated view.
     */
    public void showSelectedImages(Context context, String name, View configView) {

        if (mSelectPath != null && mSelectPath.size() > 0) {
            mFormActivityContext = context;
            mSelectFileInflatedView = configView;
            property = name;
            displaySelectedImages();
        }
    }

    public void startImageUploadActivity() {

        intent = new Intent((mFormActivityContext),MultiMediaSelectorActivity.class);
        // Selection type photo to display items in grid
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO);
        // Whether photoShoot
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        // The maximum number of selectable image
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT, ConstantVariables.FILE_UPLOAD_LIMIT);
        // Select mode
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE, mSelectMode);
        // The default selection
        if(mSelectPath != null && mSelectPath.size() > 0){
            intent.putExtra(MultiMediaSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        ((Activity)mFormActivityContext).startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ConstantVariables.REQUEST_IMAGE :
                if (resultCode == RESULT_OK) {
                    mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);

                    if (property != null && property.equals("host_photo")) {
                        mHashMap.put("host_photo", mSelectPath);
                    } else {
                        mHashMap.put("photo", mSelectPath);
                    }

                    displaySelectedImages();
                }
                break;
            case ConstantVariables.REQUEST_MUSIC:

                if(resultCode == RESULT_OK && data != null){

                    Uri selectedMusicUri = data.getData();
                    if (selectedMusicUri != null) {
                        if(mSelectedMusicFiles.size() < 5) {
                            mSelectedMusicFiles.add(GlobalFunctions.getMusicFilePathFromURI(this, selectedMusicUri));

                            InitializeColumnWidth(12);
                            final List<ImageViewList> photoUrls = new ArrayList<>();
                            final RecyclerView resultRecyclerView = (RecyclerView) mSelectFileInflatedView.findViewById(R.id.recycler_view_list);
                            resultRecyclerView.setLayoutManager(new LinearLayoutManager(EditEntry.this));
                            mMusicImageAdapter = new CustomImageAdapter(EditEntry.this, photoUrls, columnWidth,
                                    new OnCancelClickListener() {
                                        @Override
                                        public void onCancelButtonClicked(int removedSong) {
                                            if (mSelectedMusicFiles != null && !mSelectedMusicFiles.isEmpty()) {
                                                mSelectedMusicFiles.remove(removedSong);
                                                photoUrls.remove(removedSong);
                                                mMusicImageAdapter.notifyDataSetChanged();
                                                if (mSelectedMusicFiles.isEmpty()) {
                                                    resultRecyclerView.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    });
                            resultRecyclerView.setAdapter(mMusicImageAdapter);
                            resultRecyclerView.setVisibility(View.VISIBLE);
                            for(int i = 0; i < mSelectedMusicFiles.size(); i++) {
                                photoUrls.add(new ImageViewList(ContextCompat.getDrawable(EditEntry.this,
                                        R.drawable.ic_empty_music2), mSelectedMusicFiles.get(i)
                                        .substring(mSelectedMusicFiles.get(i).lastIndexOf("/") + 1)));
                            }
                            mMusicImageAdapter.notifyDataSetChanged();

                        }else {
                            SnackbarUtils.displaySnackbar(editFormView,
                                    getResources().getString(R.string.music_upload_limit_msg));
                        }
                    }

                }else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
                    SnackbarUtils.displaySnackbar(editFormView,
                            getResources().getString(R.string.music_capturing_failed));

                }
                break;

            case ConstantVariables.INPUT_FILE_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null){
                    Uri selectedFileUri = data.getData();
                    if (selectedFileUri != null) {
                        mSelectedFilePath = GlobalFunctions.getFileRealPathFromUri(EditEntry.this, selectedFileUri);
                        String fileDescription = "file";
                        if (mSelectedFilePath != null) {
                            fileDescription = mSelectedFilePath.substring(mSelectedFilePath.lastIndexOf("/") + 1);
                        }
                        fileDescription = "<b>" + fileDescription + "</b><br/>" + getResources().getString(R.string.file_uploading_message);

                        InitializeColumnWidth(10);
                        final List<ImageViewList> photoUrls = new ArrayList<>();
                        final RecyclerView resultRecyclerView = (RecyclerView) mSelectFileInflatedView.findViewById(R.id.recycler_view_list);
                        resultRecyclerView.setLayoutManager(new LinearLayoutManager(EditEntry.this));
                        CustomImageAdapter customImageAdapter = new CustomImageAdapter(EditEntry.this,
                                photoUrls, columnWidth, new OnCancelClickListener() {
                            @Override
                            public void onCancelButtonClicked(int userId) {
                                if (!photoUrls.isEmpty()) {
                                    photoUrls.remove(userId);
                                    mSelectedFilePath = null;
                                    resultRecyclerView.setVisibility(View.GONE);
                                }
                            }
                        });
                        resultRecyclerView.setAdapter(customImageAdapter);
                        resultRecyclerView.setVisibility(View.VISIBLE);
                        Drawable fileDrawable = ContextCompat.getDrawable(EditEntry.this, R.drawable.ic_attachment_white_24dp);
                        fileDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(EditEntry.this, R.color.dark_gray),
                                PorterDuff.Mode.SRC_ATOP));
                        photoUrls.add(new ImageViewList(fileDrawable, fileDescription));
                        customImageAdapter.notifyDataSetChanged();
                    }

                } else if (resultCode != RESULT_CANCELED) {
                    SnackbarUtils.displaySnackbar(editFormView,getResources().getString(R.string.file_uploading_failed));

                }
                break;
            case ConstantVariables.SELECT_PRODUCT_RETURN_CODE:
                if (FormActivity._layout != null && resultCode == ConstantVariables.SELECT_PRODUCT_RETURN_CODE) {
                    FormActivity.selectedProducts = data.getStringArrayListExtra(ConstantVariables.SELECT_PRODUCT);
                    View overView = FormActivity._layout.findViewWithTag("product_search");
                    EditText etOverView = (EditText) overView.findViewById(R.id.field_value);
                    CustomViews.setEditText(etOverView, FormActivity.selectedProducts.size() +" "+getResources().getString(R.string.product_selected));
                }
                break;
            case ConstantVariables.OVERVIEW_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    FormActivity.overviewText = data.getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE);
                    if (FormActivity._layout != null) {
                        View overView = FormActivity._layout.findViewWithTag("overview");
                        EditText etOverView = (EditText) overView.findViewById(R.id.field_value);
                        CustomViews.setEditText(etOverView, FormActivity.overviewText);
                    }
                }
                break;
            case ConstantVariables.EDITOR_REQUEST_CODE:
                if (resultCode == ConstantVariables.EDIT_ENTRY_RETURN_CODE)
                {
                    mBodyString = data.getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE);
                }

        }

    }

    /**
     * Method to show selected images.
     */
    private void displaySelectedImages() {
        InitializeColumnWidth(4);
        final List<ImageViewList> photoUrls = new ArrayList<>();
        final RecyclerView resultRecyclerView = (RecyclerView) mSelectFileInflatedView.findViewById(R.id.recycler_view_list);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(EditEntry.this,
                LinearLayoutManager.HORIZONTAL, false));
        mCustomImageAdapter = new CustomImageAdapter(EditEntry.this, photoUrls, columnWidth, new OnCancelClickListener() {
            @Override
            public void onCancelButtonClicked(int removedImage) {
                if (mSelectPath != null && !mSelectPath.isEmpty()) {
                    mSelectPath.remove(removedImage);
                    photoUrls.remove(removedImage);
                    mCustomImageAdapter.notifyDataSetChanged();
                    if (mSelectPath.isEmpty()) {
                        resultRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
        resultRecyclerView.setAdapter(mCustomImageAdapter);

        resultRecyclerView.setVisibility(View.VISIBLE);
        for(int i = 0; i < mSelectPath.size(); i++) {
            photoUrls.add(new ImageViewList(BitmapUtils.decodeSampledBitmapFromFile(EditEntry.this,
                    mSelectPath.get(i), AppConstant.getDisplayMetricsWidth(EditEntry.this),
                    (int) getResources().getDimension(R.dimen.feed_attachment_image_height), false)));
            mCustomImageAdapter.notifyDataSetChanged();
        }
    }

    private void InitializeColumnWidth(int numColumn) {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((mAppConst.getScreenWidth() - ((10 + 1) * padding)) /
                numColumn);
    }

    @Override
    public void onUploadResponse(final JSONObject jsonObject, boolean isRequestSuccessful) {

        if (isRequestSuccessful) {
            mIsFormLoaded = false;
            switch (currentSelectedOption){

                case ConstantVariables.FORUM_MENU_TITLE:
                    SnackbarUtils.displaySnackbarShortWithListener(editFormView,
                            getResources().getString(R.string.successful_edit),
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    ForumUtil.increaseViewTopicPageCounter();
                                    if (mFormType.equals("rename_topic")) {
                                        ForumUtil.increaseProfilePageCounter();
                                        startActivity(ForumUtil.getViewTopicPageIntent(EditEntry.this,
                                                postParams.get("title"),
                                                getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL)));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                        //When successfully updated name of topic then finish parent activity(i.e. ForumView)
                                        ForumUtil.finishViewTopicActivity();
                                    } else {
                                        ForumUtil.editForumPosition(editForumPosition);
                                        finish();
                                    }
                                }
                            });
                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                case ConstantVariables.MLT_MENU_TITLE:
                case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                case ConstantVariables.PRODUCT_MENU_TITLE:
                case ConstantVariables.STORE_MENU_TITLE:
                case ConstantVariables.ADV_EVENT_PAYMENT_METHOD:
                case ConstantVariables.PAYMENT_METHOD_CONFIG:
                case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
                case ConstantVariables.DIARY_MENU_TITLE:
                case ConstantVariables.CONTACT_INFO_MENU_TITLE:
                    if (mFormType == null || mFormType.isEmpty() || mSuccessMessage == null) {
                        mSuccessMessage = getResources().getString(R.string.successful_edit);
                    }
                    SnackbarUtils.displaySnackbarShortWithListener(editFormView,
                            mSuccessMessage, new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    Bundle bundle = new Bundle();
                                    Intent intent = new Intent();
                                    Log.d("ThisWasLoggedEdit ", mSuccessMessage+" "+"true");
                                    if (currentSelectedOption.equals(ConstantVariables.CONTACT_INFO_MENU_TITLE)) {
                                        String bodyObject = String.valueOf(jsonObject.optJSONObject("body"));
                                        bundle.putString(ConstantVariables.RESPONSE_OBJECT, bodyObject);
                                        intent.putExtras(bundle);
                                        setResult(ConstantVariables.CONTACT_INFO_CODE, intent);

                                    } else {
                                        if (mFormType != null && !mFormType.isEmpty() && mFormType.equals("story_setting")) {
                                            bundle.putString("response", jsonObject.toString());
                                        }

                                        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, currentSelectedOption);
                                        intent.putExtras(bundle);
                                        setResult(ConstantVariables.PAGE_EDIT_CODE, intent);
                                    }

                                    if(mFormType != null && mFormType.equals("edit_event")){
                                        finish();
                                    } else if(isFromEventList){
                                        finish();
                                    } else  {
                                        redirectToAdvEventsAvailableTickets();
                                    }
//                                    finish();
                                }
                            });
                    break;

                default:
                    SnackbarUtils.displaySnackbarShortWithListener(editFormView,
                            getResources().getString(R.string.successful_edit),
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, currentSelectedOption);
                                    Intent intent = new Intent();
                                    intent.putExtras(bundle);
                                    setResult(mRequestCode, intent);
                                    finish();
                                }
                            });
                    break;
            }

        } else if (jsonObject.has("showValidation")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject validationMessages = jsonObject.optJSONObject("message");
                    if (validationMessages != null) {
                        showValidations(validationMessages);
                    } else {
                        SnackbarUtils.displaySnackbar(editFormView, jsonObject.optString("message"));
                    }
                }
            });

        } else {
            mIsFormLoaded = false;
            SnackbarUtils.displaySnackbarLongWithListener(editFormView, jsonObject.optString("message"),
                    new SnackbarUtils.OnSnackbarDismissListener() {
                        @Override
                        public void onSnackbarDismissed() {
                            finish();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        mAppConst.hideKeyboard();
        if (isFromWebViewPayment){
            Intent viewIntent;
            String url = AppConstant.DEFAULT_URL;
            url += "advancedevents/view/" + mEventId + "?gutter_menu=" + 1;
            viewIntent = new Intent(mContext, AdvEventsProfilePage.class);
            if (getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false))
                viewIntent.putExtra(ConstantVariables.KEY_USER_CREATE_SESSION, true);
            viewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
            viewIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, url);
            viewIntent.putExtra(ConstantVariables.VIEW_PAGE_ID, mEventId);
            viewIntent.putExtra("isRedirectedFromEventProfile", true);
            startActivityForResult(viewIntent, ConstantVariables.CREATE_REQUEST_CODE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if(mFormType != null && mFormType.equals("edit_event")){
            finish();
        } else if (isFromEventList){
            finish();
        }else if ((currentSelectedOption.equals(ConstantVariables.PAYMENT_METHOD_CONFIG)
                && (mFormType != null && mFormType.equals(ConstantVariables.PAYMENT_METHOD_CONFIG)))) {
            setResult(ConstantVariables.REQUEST_CANCLED);
            finish();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FormActivity.hostKey = null;
    }

    public static EditEntry getInstance(){
        return editEntry;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow
                    switch(mUploadingOption){
                        case "photo":
                            startImageUploadActivity();
                            break;
                        case "music":
                            GlobalFunctions.addMusicBlock(mFormActivityContext);
                            break;
                        case "file":
                            GlobalFunctions.openDocumentUploadingIntent(EditEntry.this, mSelectFileInflatedView);
                            break;
                    }
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.
                        SnackbarUtils.displaySnackbarOnPermissionResult( EditEntry.this, editFormView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;

        }
    }

    public void redirectToAdvEventsAvailableTickets(){
        intent = new Intent(mContext, AdvEventsAvailableTickets.class);
        intent.putExtra("url", editFormUrl);
        intent.putExtra("isAdvEventId", mEventId);
        intent.putExtra("isFromWebViewPayment", true);
        mContext.startActivity(intent);
        finish();
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
