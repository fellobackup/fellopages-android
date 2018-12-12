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

package com.fellopages.mobileapp.classes.core.startscreens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.LoginActivity;
import com.fellopages.mobileapp.classes.core.MainActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;

import com.facebook.login.widget.LoginButton;
import com.fellopages.mobileapp.classes.modules.user.signup.SubscriptionActivity;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class HomeScreen extends FragmentActivity implements View.OnClickListener, SocialLoginUtil.OnSocialLoginSuccessListener {

    View mainView;
    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private Button mSignInBtn, mSignUpBtn;
    private TextView mBrowseAsGuest, mChooseLanguage;
    private List<Fragment> fragments = new Vector<>();
    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;
    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private TwitterLoginButton twitterLoginButton;
    private AppConstant mAppConst;
    private boolean mIsBrowseGuestEnabled = false, mIsMultiLanguageEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Initialize Facebook SDK, we need to initialize before using it ---- */
        SocialLoginUtil.initializeFacebookSDK(HomeScreen.this);

        SocialLoginUtil.setSocialLoginListener(this);

        mAppConst = new AppConstant(this);

        setContentView(R.layout.activity_home_screen);
        callbackManager = CallbackManager.Factory.create();
        mainView = findViewById(R.id.main_content);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        twitterLoginButton= (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        // Hide Facebook button when facebook_app_id is null or Empty.
        if (!getResources().getString(R.string.facebook_app_id).isEmpty()) {
            facebookLoginButton.setVisibility(View.VISIBLE);
        } else {
            facebookLoginButton.setVisibility(View.GONE);
        }

        // Hide twitter button when twitter_key or twitter_secret is null or Empty.
        if (!getResources().getString(R.string.twitter_key).isEmpty() &&
                !getResources().getString(R.string.twitter_secret).isEmpty()) {
            twitterLoginButton.setVisibility(View.VISIBLE);
            twitterLoginButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this,
                     R.drawable.ic_twitter_bird_icon), null, null, null);
            twitterLoginButton.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.padding_10dp));
            twitterLoginButton.setPadding((int) getResources().getDimension(R.dimen.padding_8dp), 0, (int) getResources().getDimension(R.dimen.margin_40dp), 0);
            twitterLoginButton.setText(getResources().getString(R.string.twitter_login_text));
            twitterLoginButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimensionPixelSize(R.dimen.body_default_font_size));
        } else {
            twitterLoginButton.setVisibility(View.GONE);
        }

        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));


        mBrowseAsGuest = (TextView) findViewById(R.id.browse_as_guest);
        mBrowseAsGuest.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        mBrowseAsGuest.setOnClickListener(this);

        mChooseLanguage = (TextView) findViewById(R.id.choose_language);
        mChooseLanguage.setTypeface(GlobalFunctions.getFontIconTypeFace(this));
        mChooseLanguage.setOnClickListener(this);

        mSignUpBtn = (Button) findViewById(R.id.signup_button);
        mSignUpBtn.setOnClickListener(this);

        mSignInBtn = (Button) findViewById(R.id.signin_button);
        mSignInBtn.setOnClickListener(this);
        mSignInBtn.setPadding(0, (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding),
                0, (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding));
        mSignUpBtn.setPadding(0, (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding),
                0, (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding));

        mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(this.mPagerAdapter);

        setBrowseGuestAndLanguageOptions();

        switch (ConstantVariables.TOTAL_SLIDESHOW_IMAGES) {

            case 1:

                fragments.add(ContentFragment.newInstance(getResources().getString(R.string.first_slide_title),
                        getResources().getString(R.string.first_slide_subtitle), R.drawable.first));
                break;

            case 2:
                fragments.add(ContentFragment.newInstance(getResources().getString(R.string.first_slide_title),
                        getResources().getString(R.string.first_slide_subtitle), R.drawable.first));
                fragments.add(ContentFragment.newInstance(getResources().getString(R.string.second_slide_title),
                        getResources().getString(R.string.second_slide_subtitle)
                        , R.drawable.second));
                break;
            case 3:
                fragments.add(ContentFragment.newInstance(getResources().getString(R.string.first_slide_title),
                        getResources().getString(R.string.first_slide_subtitle), R.drawable.first));
                fragments.add(ContentFragment.newInstance(getResources().getString(R.string.second_slide_title),
                        getResources().getString(R.string.second_slide_subtitle)
                        , R.drawable.second));
                fragments.add(ContentFragment.newInstance(getResources().getString(R.string.third_slide_title),
                        getResources().getString(R.string.third_slide_subtitle)
                        , R.drawable.third));
                break;
        }
        mPagerAdapter.notifyDataSetChanged();

        viewPager.setPageTransformer(true, new FadePageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, ANIM_VIEWPAGER_DELAY);
                        break;
                    default:
                        handler.removeCallbacks(runnable);
                }
            }
        });

        // check for user is already logged in app or not
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            startActivity(intent);
        }

        // check for user is already logged in app or not
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (session != null)
        {
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            startActivity(intent);
        }

        //Facebook login authentication process
        SocialLoginUtil.registerFacebookLoginCallback(HomeScreen.this, mainView, callbackManager, false);

        //Twitter login authentication process
        SocialLoginUtil.registerTwitterLoginCallback(HomeScreen.this, mainView, twitterLoginButton, false);

    }

    /**
     * Method to set the browse as a guest text view and choose language option text view.
     */
    private void setBrowseGuestAndLanguageOptions() {

        // Checking browse as a guest option.
        if(!PreferencesUtils.isGuestUserEnabled(this)){
            mBrowseAsGuest.setVisibility(View.GONE);
        } else {
            mIsBrowseGuestEnabled = true;
            mBrowseAsGuest.setVisibility(View.VISIBLE);
        }

        // Checking multiple language option.
        String languages = PreferencesUtils.getLanguages(this);
        if (languages != null) {
            try {
                JSONObject languageObject = new JSONObject(languages);
                if (languageObject.length() > 1) {
                    mIsMultiLanguageEnabled = true;
                    mChooseLanguage.setVisibility(View.VISIBLE);
                } else {
                    mChooseLanguage.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mChooseLanguage.setVisibility(View.GONE);
        }

        if (!mIsBrowseGuestEnabled && !mIsMultiLanguageEnabled) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            LinearLayout mainButtons = findViewById(R.id.main_buttons);
            mainButtons.setLayoutParams(layoutParams);
            mainButtons.setPadding(0, 0, 0,
            (int) getResources().getDimension(R.dimen.login_button_top_bottom_padding));
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {

            if (viewPager.getCurrentItem() >= fragments.size() - 1) {
                viewPager.setCurrentItem(0,false);
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, false);
            }
            handler.postDelayed(runnable, ANIM_VIEWPAGER_DELAY);

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (handler!= null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsBrowseGuestEnabled = false;
        mIsMultiLanguageEnabled = false;
        mBrowseAsGuest.setText(getResources().getString(R.string.browse_as_guest));
        setBrowseGuestAndLanguageOptions();

        if (PreferencesUtils.getAuthToken(this) != null &&
                !PreferencesUtils.getAuthToken(this).isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        // Always call the superclass method first
        handler.postDelayed(runnable, ANIM_VIEWPAGER_DELAY);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){

            case R.id.signin_button:
                Intent mainIntent = new Intent(HomeScreen.this,LoginActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.browse_as_guest:

                mBrowseAsGuest.setText("\uf110");
                // Updating default language as current language.
                PreferencesUtils.updateDashBoardData(HomeScreen.this,
                        PreferencesUtils.CURRENT_LANGUAGE,
                        PreferencesUtils.getDefaultLanguage(HomeScreen.this));
                Intent intent = new Intent(HomeScreen.this, MainActivity.class);
                intent.putExtra("isSetLocation",true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.signup_button:
                Intent signUpIntent = new Intent(HomeScreen.this, SubscriptionActivity.class);
                startActivity(signUpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.choose_language:
                mAppConst.changeLanguage(this, "home");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ConstantVariables.ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        AlertDialogWithAction mAlertDialogWithAction = new AlertDialogWithAction(this);
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                ConstantVariables.ACCESS_FINE_LOCATION);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(this, mainView,
                                ConstantVariables.ACCESS_FINE_LOCATION);

                    }
                }
                break;

        }
    }

    @Override
    public void onSuccess(String loginType) {

    }

    @Override
    public void onError(String loginType, String errorMessage) {
        SocialLoginUtil.clearFbTwitterInstances(this, loginType);
        switch (errorMessage) {
            case "email_not_verified":
                SnackbarUtils.displaySnackbar(viewPager, getResources().getString(R.string.email_not_verified));
                break;
            case "not_approved":
                SnackbarUtils.displaySnackbar(viewPager, getResources().getString(R.string.signup_admin_approval));
                break;
            default:
                SnackbarUtils.displaySnackbar(viewPager, errorMessage);
                break;
        }
    }

}
