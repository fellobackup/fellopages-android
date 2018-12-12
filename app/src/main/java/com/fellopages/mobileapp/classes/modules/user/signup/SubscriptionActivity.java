/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.modules.user.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialLoginUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SubscriptionActivity extends FormActivity {

    private String mSignUpUrl;
    private AppConstant mAppConst;
    private RelativeLayout mAccountFormView;
    private Toolbar mToolbar;
    public static String TAG = "SignUp Activity";
    private Context mContext;
    private JSONArray mSubscriptionObject;
    private JSONObject mResponseObject;
    private Intent intent;
    private Bundle bundle;
    private String loginType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);

        mContext = this;
        mAppConst = new AppConstant(mContext);

        mSignUpUrl = AppConstant.DEFAULT_URL + "signup?subscriptionForm=1";
        mSignUpUrl = mAppConst.buildQueryString(mSignUpUrl, mAppConst.getAuthenticationParams());

        mAccountFormView = (RelativeLayout) findViewById(R.id.form_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        Intent loginIntent = getIntent();
        bundle = loginIntent.getBundleExtra("fb_twitter_info");
        if (bundle != null) {
            loginType = bundle.getString("loginType");
        }

        mAppConst.getJsonResponseFromUrl(mSignUpUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                mResponseObject = jsonObject;
                mSubscriptionObject = mResponseObject.optJSONArray("subscription");

                intent = new Intent(mContext, SignUpActivity.class);
                intent.putExtra("signup_response", mResponseObject.toString());
                intent.putExtra("fb_twitter_info", bundle);

                /**
                 * Check if subscription is not null
                 * Load subscription form else
                 * Redirect to SignupActivity to show Account creation form
                 */

                if (mSubscriptionObject != null) {
                    mAccountFormView.addView(generateForm(mResponseObject, false, "subscription_account"));
                } else if (!((Activity) mContext).isFinishing()) {
                    finish();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, ConstantVariables.SIGN_UP_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbar(mAccountFormView, message);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setIcon(null);
        menu.findItem(R.id.submit).setTitle(mContext.getResources().getString(R.string.signup_next_step));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){

            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                break;
            case R.id.submit:
                redirectToSignupActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void redirectToSignupActivity(){

        Map<String, String> postParams = save();
        // TODO recheck it, if the hasValidator is coming or not from api.
        // TODO otherwise show snackbar to select a subscription plan.
        if(intent != null && postParams != null && postParams.containsKey("package_id")
                && postParams.get("package_id") != null && !postParams.get("package_id").isEmpty()) {
            LogUtils.LOGD(SubscriptionActivity.class.getSimpleName(), "postParams: " + postParams);
            intent.putExtra("package_id", postParams.get("package_id"));
            startActivityForResult(intent, ConstantVariables.SIGN_UP_CODE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            SnackbarUtils.displaySnackbar(mAccountFormView, "Please select a subscription plan.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SocialLoginUtil.clearFbTwitterInstances(this, loginType);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Remove the existing form and generate the form again on back pressed from signupActivity activity.
         * As the FormActivity now have the layout of Signup activity
         * so, on this activity, the fields are blank and it does not proceed further.
         */

        if(requestCode == ConstantVariables.SIGN_UP_CODE && data != null){
            int packageId = data.getIntExtra("package_id", 0);

            mAccountFormView.removeView(mAccountFormView.findViewById(R.id.form_layout));

            try{
                if(packageId != 0){
                    JSONObject formValues = new JSONObject();
                    formValues.put("package_id", packageId);
                    mResponseObject.put("formValues", formValues);
                    mAccountFormView.addView(populate(mResponseObject, "subscription_account"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
