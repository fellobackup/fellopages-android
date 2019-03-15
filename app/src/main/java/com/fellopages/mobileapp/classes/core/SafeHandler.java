package com.fellopages.mobileapp.classes.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SafeHandler extends AppCompatActivity {
    Bundle bundle;
    TextView errorTitle, errorDescription;
    ImageView errorImage,errorImagePlaceholder;
    Context mContext;
    private Toolbar mToolbar;
    private AppConstant mAppConstant;
    Button goToHome,contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safe_handler);
        setTitle("");
        mContext = this;
        mAppConstant = new AppConstant(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        bundle = getIntent().getExtras();
        HashMap postParams = new HashMap();
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
        gd.setCornerRadius(3);
        gd.setStroke(4, ContextCompat.getColor(mContext, R.color.themeButtonColor));

        errorTitle = findViewById(R.id.error_title);
        errorDescription = findViewById(R.id.error_description);
        errorImage = findViewById(R.id.error_image);
        errorImagePlaceholder = findViewById(R.id.error_image_placeholder);
        goToHome = findViewById(R.id.go_to_home);
        goToHome.setBackground(gd);
        goToHome.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent settingsActivity = new Intent(SafeHandler.this, FragmentLoadActivity.class);
                settingsActivity.putExtra(ConstantVariables.FRAGMENT_NAME,ConstantVariables.CONTACT_US_MENU_TITLE);
                settingsActivity.putExtra(ConstantVariables.CONTENT_TITLE,getResources().getString(R.string.action_bar_title_contact_us));
                startActivity(settingsActivity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        contact.setBackground(gd);
        contact.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        setErrorLayout();
        postParams.put("message", bundle.getString("message"));
        postParams.put("error_code", bundle.getString("error_code"));
        String mURL = AppConstant.DEFAULT_URL + "applog/write";
        mAppConstant.postJsonRequest(mURL, postParams);

        if (PreferencesUtils.getUserDetail(mContext) != null) {
            try {
                JSONObject userObject = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                Crashlytics.setUserEmail(userObject.optString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Crashlytics.logException(new Exception(bundle.getString("message")));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMain();
    }

    public void goToMain() {
        Intent settingsActivity = new Intent(SafeHandler.this, MainActivity.class);
        startActivity(settingsActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                goToMain();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setErrorLayout() {
        mAppConstant.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "get-error-layout",
                new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        if (jsonObject != null) {
                                if(jsonObject.optString("title") != null && jsonObject.optString("title").isEmpty()){
                                    errorTitle.setVisibility(View.GONE);
                                } else {
                                    errorTitle.setText(jsonObject.optString("title"));
                                }
                                errorDescription.setText(jsonObject.optString("description", getResources().getString(R.string.error_description)));
                            ImageLoader imageLoader = new ImageLoader(SafeHandler.this);
                            imageLoader.setImageAnimationListener(jsonObject.optString("image"),errorImagePlaceholder,errorImage);
                            }
                        goToHome.setVisibility(View.VISIBLE);
                        contact.setVisibility(View.VISIBLE);

                    }
                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        goToHome.setVisibility(View.VISIBLE);
                        contact.setVisibility(View.VISIBLE);
                    }
                });
    }
}
