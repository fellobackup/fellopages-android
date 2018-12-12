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

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReportEntry extends FormActivity {

    private String reportUrl;
    private AppConstant mAppConst;
    private Map<String, String> postParams;
    private Toolbar mToolbar;
    private RelativeLayout reportFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);


        //Setting up the action bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);
        reportFormView = (RelativeLayout) findViewById(R.id.form_view);

        mAppConst = new AppConstant(this);
        reportUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);

        mAppConst.getJsonResponseFromUrl(reportUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                reportFormView.addView(generateForm(jsonObject, true, "report"));

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(reportFormView, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.report_text));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(ReportEntry.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(ReportEntry.this);
                }
                return true;
            case R.id.submit:
                report();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void report(){

        postParams = new HashMap<>();
        postParams = save();

        if(postParams != null){
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            mAppConst.hideKeyboard();

            mAppConst.postJsonResponseForUrl(reportUrl, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarShortWithListener(reportFormView,
                            getResources().getString(R.string.successful_report_submitted),
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }
                            });
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbar(reportFormView, message);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
