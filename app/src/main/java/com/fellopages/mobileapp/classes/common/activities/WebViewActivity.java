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

package com.fellopages.mobileapp.classes.common.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.fellopages.mobileapp.BuildConfig;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.LoginActivity;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.core.NewLoginActivity;
import com.fellopages.mobileapp.classes.core.startscreens.HomeScreen;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsProfilePage;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class WebViewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private WebView mWebView;
    private String mWebUrl;
    private String mToolBarTitle;
    private ProgressBar mProgressBar;
    private Map<String, String> urlParams;
    private AppConstant mAppConst;
    private ValueCallback<Uri> mUploadMessage;

    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private boolean isStorePayment = false;
    private boolean isSubscription = false, isPackagePayment = false, isRedirected, isChangeSubscriptionPlan = false,
            isTicketsPayment;
    private Context mContext;
    private String emailAddress, password, loginType;
    private static final long DELAY_TIME = 8000;
    private int mEventId;
    private boolean isAdvEventPayment, isAdvEventPaymentMethod = false;
    private AlertDialogWithAction alertDialogWithAction;
    private CreateNewEntry createNewEntry;
    private boolean isRedirectedFromEventProfile = false;
    private String smsUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mWebView = (WebView) findViewById(R.id.webView);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAppConst = new AppConstant(this);
        mContext = this;

        alertDialogWithAction = new AlertDialogWithAction(mContext);

        isRedirectedFromEventProfile = getIntent().getBooleanExtra("isRedirectedFromEventProfile", false);
        LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "isRedirectedFromEventProfile->" +isRedirectedFromEventProfile );

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        /*
         **Uncomment this code if Debugging is needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        */


        /* SetTitle on ToolBar */
        mToolBarTitle = getIntent().getStringExtra("headerText");
        if (mToolBarTitle != null && !mToolBarTitle.isEmpty()) {
            mToolbar.setTitle(mToolBarTitle);
        }

        mWebUrl = getIntent().getStringExtra("url");
        if (mWebUrl != null && !mWebUrl.isEmpty()) {
            urlParams = new HashMap<>();

            isStorePayment = getIntent().getBooleanExtra("cartorder",false);
            // Added Parameter to disable header and footer.

            if (!mWebUrl.contains("disableHeaderAndFooter")) {
                urlParams.put("disableHeaderAndFooter", String.valueOf(1));
            }

            // Added Current Selected Language in WebUrl
            urlParams.put("language", PreferencesUtils.getCurrentLanguage(this));

            // Added Current Selected Location in WebUrl
            if (PreferencesUtils.getDefaultLocation(this) != null && !PreferencesUtils.getDefaultLocation(this).isEmpty())
                urlParams.put("restapilocation", PreferencesUtils.getDefaultLocation(this));

            if (!mWebUrl.contains("token") && PreferencesUtils.getAuthToken(this) != null && !PreferencesUtils.getAuthToken(this).isEmpty())
                urlParams.put("token", PreferencesUtils.getAuthToken(this));

            if (urlParams != null && urlParams.size() != 0)
                mWebUrl = mAppConst.buildQueryString(mWebUrl, urlParams);
        }

        if(getIntent().hasExtra("isSubscription") || getIntent().hasExtra("isPackagePayment")
                || getIntent().hasExtra("isChangeSubscriptionPlan") || getIntent().hasExtra("isTicketsPayment")
                || getIntent().hasExtra("isAdvEventPayment")) {
            isSubscription = getIntent().getBooleanExtra("isSubscription", false);
            isPackagePayment = getIntent().getBooleanExtra("isPackagePayment", false);
            isChangeSubscriptionPlan = getIntent().getBooleanExtra("isChangeSubscriptionPlan", false);
            isTicketsPayment = getIntent().getBooleanExtra("isTicketsPayment", false);
            isAdvEventPayment = getIntent().getBooleanExtra("isAdvEventPayment", false);
            mEventId = getIntent().getIntExtra("isAdvEventId", mEventId);
            if(isAdvEventPayment){
                isAdvEventPaymentMethod = true;
            }
            LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "isAdvEventPayment--" + isAdvEventPayment);
            LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "isAdvEventPayment_eventId--" +mEventId);
            LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "isAdvEventPaymentMethod--" + isAdvEventPaymentMethod);

            if(isPackagePayment || isTicketsPayment || isAdvEventPayment){
                mToolbar.setTitle(mContext.getResources().getString(R.string.package_payment));
            } else {
                mToolbar.setTitle(mContext.getResources().getString(R.string.subscription));
            }

            /**
             * Clear Cache, cookies and history in case of subscription.
             */
            mWebView.clearCache(true);
            mWebView.clearHistory();
            clearCookies(this);
        }

        emailAddress = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        loginType = getIntent().getStringExtra("loginType");
        Bundle fbTwitterBundle = getIntent().getBundleExtra("fb_twitter_info");
        if (fbTwitterBundle != null) {
            loginType = fbTwitterBundle.getString("loginType");
        }

        mProgressBar.setVisibility(View.VISIBLE);

        GlobalFunctions.setWebSettings(mWebView, true);

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "Web url: " + mWebUrl);

        mWebView.loadUrl(mWebUrl);
        mWebView.setWebViewClient(new myWebClient());

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                openImageChooser();
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                openImageChooser();
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                openImageChooser();
            }

            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                openImageChooser();
                return true;
            }
        });
    }

    /**
     * Function to open Image Chooser
     */

    private void openImageChooser() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        GlobalFunctions.getFileUri(mContext, photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, getResources().getString(R.string.image_chooser));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, ConstantVariables.INPUT_FILE_REQUEST_CODE);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data != null){
            isRedirectedFromEventProfile = data.getBooleanExtra("isRedirectedFromEventProfile", false);
        }
        LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "request--result->" +requestCode+"-"+resultCode);
        LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "resultCodeData->" +data);
        LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "isRedirectedFromEventProfile->" + isRedirectedFromEventProfile);

        if(requestCode == ConstantVariables.CREATE_REQUEST_CODE
                && resultCode == ConstantVariables.IS_REDIRECTED_FROM_EVENT_PROFILE
                && isRedirectedFromEventProfile){
            Intent intent = new Intent();
            intent.putExtra("isRedirectedFromEventProfile", isRedirectedFromEventProfile);
            setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            finish();
            LogUtils.LOGD(WebViewActivity.class.getSimpleName(), "isRedirectedFromEventProfile->" + isRedirectedFromEventProfile);
        }

        if (requestCode != ConstantVariables.INPUT_FILE_REQUEST_CODE || (mFilePathCallback == null && mUploadMessage == null)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }


        Uri[] results = null;
        Uri result = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    result = Uri.parse(mCameraPhotoPath);
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                    result = Uri.parse(dataString);
                }
            }
        }

        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack() && !isSubscription && !isPackagePayment && !isChangeSubscriptionPlan) {
                        mWebView.goBack();
                    }else{
                        onBackPressed();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);

//        menu.findItem(R.id.submit).setVisible(false);
        if(isAdvEventPayment){
            menu.findItem(R.id.skip).setVisible(false);
            menu.findItem(R.id.submit).setVisible(false);
        }else{
            menu.findItem(R.id.skip).setVisible(false);
            menu.findItem(R.id.submit).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
            return true;
        }
        if(id == R.id.skip){
            sendToPaymentMethod();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        /**
         * Clear Twitter and Facebook instances if subscription
         * payment is not completed
         */
        if(isSubscription){
            SocialLoginUtil.clearFbTwitterInstances(this, loginType);
        }
        /**
         * Finish the webview activity in case of subscription when back button is pressed from any where.
         */
        if(isSubscription){
            super.onBackPressed();
            Intent intent = new Intent();
            setResult(ConstantVariables.SIGN_UP_WEBVIEW_CODE, intent);
            finish();
        }else if (isChangeSubscriptionPlan || isTicketsPayment) {
            Intent intent = new Intent();
            setResult(ConstantVariables.PAYMENT_FAILED_ACTIVITY_CODE, intent);
            super.onBackPressed();
            finish();
        } else if(isAdvEventPayment){
            showAlertPopup(true);
        }else
        {
            super.onBackPressed();
            setResult(RESULT_CANCELED);
            finish();
        }
    }


    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            /* code for ERR_CACHE_MISS error */
            if (Build.VERSION.SDK_INT >= 19) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            isRedirected = false;
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // Checking for the play store url. If it is a store url then opening the google play store directly.
            if (url.contains("https://play.google.com/store/apps/")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                finish();
            } else if (url.contains("mailto:")) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                finish();
            } else if (url.contains("smsto:")) {
                smsUrl = url;
                if(!mAppConst.checkManifestPermission(Manifest.permission.SEND_SMS)){
                    mAppConst.requestForManifestPermission(Manifest.permission.SEND_SMS,
                            ConstantVariables.SEND_SMS);
                } else {
                    openSmsApp();
                }
            } else {
                view.loadUrl(url);
                isRedirected = true;
            }
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(String.valueOf(request.getUrl()));
            isRedirected = true;
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isRedirected) {

                // Showing tool bar title which is the current page title in WebView.
                if (mToolbar != null && view.getTitle() != null && !view.getTitle().isEmpty()) {
                    mToolbar.setTitle(view.getTitle());
                }

                if (isStorePayment) {
                    if (url.contains("state/active") || url.contains("products/success")) {
                        setResult(RESULT_OK);
                        finish();
                    }else if (url.contains("state/")) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                    //TODO needs to check
                }else if(isAdvEventPayment){
                    if (url.contains("state/active")) {
                        sendToPaymentMethod();
                    } else if(url.contains("state/")){
                        showAlertPopup(false);
                    }
                } else if (isSubscription) {

                    /**
                     * In case of subscription
                     * if payment is completed/successful, send login request to login the new user.
                     * else show a message and redirect to login activity, so that he can continue the subscription payment
                     * after login
                     */
                    if (url.contains("state/active")) {

                        Map<String, String> params = new HashMap<>();
                        params.put("ip", GlobalFunctions.getLocalIpAddress());
                        if (loginType != null && !loginType.isEmpty()) {
                            params = SocialLoginUtil.getFacebookTwitterParams();
                        } else {
                            params.put("email", emailAddress);
                            params.put("password", password);
                        }

                        mAppConst.postLoginSignUpRequest(UrlUtil.LOGIN_URL, params, new OnResponseListener() {
                            @Override
                            public void onTaskCompleted(JSONObject jsonObject) {

                                PreferencesUtils.clearSharedPreferences(mContext);
                                PreferencesUtils.clearDashboardData(mContext);
                                DataStorage.clearApplicationData(mContext);

                                JSONObject userDetail = jsonObject.optJSONObject("user");
                                String user_language = userDetail.optString("language");

                                String oauthToken = jsonObject.optString("oauth_token");
                                String oauth_secret = jsonObject.optString("oauth_secret");
                                PreferencesUtils.updateUserPreferences(mContext, userDetail.toString(),
                                        oauth_secret, oauthToken);
                                // Save email and base64 encrypted password in SharedPreferences
                                if (password != null && !password.isEmpty()) {
                                    PreferencesUtils.UpdateLoginInfoPref(mContext, emailAddress, password,
                                            userDetail.optInt("user_id"));
                                }

                                /* English is coming from API instead of it's language code, It will automatically
                                 work when API issue will be resolved.. */
                                if (user_language.equals("English")) {
                                    user_language = "en";
                                }

                                // Set default language to current language when we have only single language for the app
                                String multiLanguages = PreferencesUtils.getLanguages(mContext);
                                if (multiLanguages != null && !multiLanguages.isEmpty()) {
                                    PreferencesUtils.updateDashBoardData(mContext,
                                            PreferencesUtils.CURRENT_LANGUAGE, user_language);
                                    mAppConst.changeAppLocale(user_language, false);

                                } else {
                                    PreferencesUtils.updateDashBoardData(mContext,
                                            PreferencesUtils.CURRENT_LANGUAGE, PreferencesUtils.getDefaultLanguage(mContext));
                                    mAppConst.changeAppLocale(PreferencesUtils.getDefaultLanguage(mContext), false);
                                }

                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.putExtra("isSetLocation", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }

                            @Override
                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.container),
                                        message, new SnackbarUtils.OnSnackbarDismissListener() {
                                            @Override
                                            public void onSnackbarDismissed() {
                                                SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
                                                finish();
                                            }
                                        });
                            }
                        });

                    } else if (url.contains("state/")) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent;
                                if (ConstantVariables.INTRO_SLIDE_SHOW_ENABLED == 1) {
                                    if (loginType != null && !loginType.isEmpty()) {
                                        SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
                                    }
                                    intent = new Intent(mContext, NewLoginActivity.class);
                                } else if (loginType != null && !loginType.isEmpty()) {
                                    SocialLoginUtil.clearFbTwitterInstances(mContext, loginType);
                                    intent = new Intent(mContext, HomeScreen.class);
                                } else {
                                    intent = new Intent(mContext, LoginActivity.class);
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }, DELAY_TIME);
                    }
                } else if (isPackagePayment && url.contains("state/")) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, DELAY_TIME);
                }
            } else if((isChangeSubscriptionPlan || isTicketsPayment ) && url.contains("/state/active")) {
                if (isTicketsPayment) {
                    setResult(ConstantVariables.PAYMENT_SUCCESS_ACTIVITY_CODE);
                }
                finish();
            }
        }
    }

    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public void sendToPaymentMethod(){
        Intent createEntry;
        String payment_method_url = null;
        createEntry = new Intent(WebViewActivity.this, CreateNewEntry.class);
        payment_method_url = AppConstant.DEFAULT_URL + "advancedeventtickets/order/payment-info?" + "event_id=" + mEventId;
        createEntry.putExtra(ConstantVariables.CREATE_URL, payment_method_url);
        createEntry.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_EVENT_PAYMENT_METHOD);
        createEntry.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.PAYMENT_CONFIG_METHOD);
        createEntry.putExtra("isAdvEventPaymentMethod", isAdvEventPaymentMethod);
        createEntry.putExtra("isAdvEventId", mEventId);
        mContext.startActivity(createEntry);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void showAlertPopup(boolean showCancel) {
        alertDialogWithAction.showAlertDialogWithOkAction(showCancel, mContext.getResources().getString(R.string.back_button_popup_title),
                mContext.getResources().getString(R.string.webview_popup_message),
                mContext.getResources().getString(R.string.back_button_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent viewIntent;
                        String url = AppConstant.DEFAULT_URL;
                        url += "advancedevents/view/" + mEventId + "?gutter_menu=" + 1;
                        viewIntent = new Intent(mContext, AdvEventsProfilePage.class);
//                        viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NO_HISTORY);
                        if (getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false))
                            viewIntent.putExtra(ConstantVariables.KEY_USER_CREATE_SESSION, true);
                        viewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
                        viewIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, url);
                        viewIntent.putExtra(ConstantVariables.VIEW_PAGE_ID, mEventId);
                        viewIntent.putExtra("isRedirectedFromEventProfile", true);
                        startActivityForResult(viewIntent, ConstantVariables.CREATE_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        /*mContext.startActivity(viewIntent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ConstantVariables.SEND_SMS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    openSmsApp();
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.
                        AlertDialogWithAction mAlertDialogWithAction = new AlertDialogWithAction(mContext);

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.SEND_SMS,
                                ConstantVariables.SEND_SMS);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.
                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mWebView, ConstantVariables.SEND_SMS);

                    }
                }
                break;
        }
    }

    private void openSmsApp() {
        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsUrl));
        if (GlobalFunctions.isIntentAvailable(mContext, sms_intent)) {
            startActivity(sms_intent);
        } else {
            SnackbarUtils.displaySnackbar(mWebView, "No activity available to handle your request");
        }
    }
}
