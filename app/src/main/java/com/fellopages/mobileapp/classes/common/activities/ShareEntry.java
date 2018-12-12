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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShareEntry extends FormActivity implements View.OnClickListener{

    private String shareUrl, mContentTitle, mContentImage;
    private AppConstant mAppConst;
    private EditText shareEditTExt;
    private ImageView mImageView;
    private SelectableTextView mTitle;
    private Toolbar mToolbar;
    private CardView mShareBlock;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_entry);

        //Setting up the action bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.VISIBLE);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CustomViews.createMarqueeTitle(this, mToolbar);

        mAppConst = new AppConstant(this);
        mImageLoader = new ImageLoader(getApplicationContext());

        shareUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);
        mContentTitle = getIntent().getStringExtra("title");
        mContentImage = getIntent().getStringExtra("image");

        mShareBlock = (CardView) findViewById(R.id.share_block);
        mShareBlock.setVisibility(View.VISIBLE);
        shareEditTExt = (EditText) findViewById(R.id.shareTextBox);
        shareEditTExt.setHint(getResources().getString(R.string.write_something) + "…");
        shareEditTExt.setGravity(Gravity.START|Gravity.TOP);

        mImageView = (ImageView) findViewById(R.id.contentImage);
        mTitle = (SelectableTextView) findViewById(R.id.contentTitle);

        if(mContentImage != null && !mContentImage.isEmpty()){
            mImageLoader.setImageUrl(mContentImage, mImageView);
        }else{
            mImageView.setVisibility(View.GONE);
        }

        if(mContentTitle != null && !mContentTitle.isEmpty()){
            mTitle.setText(mContentTitle);
        }else{
            mTitle.setVisibility(View.GONE);
        }

        if(mContentImage == null && mContentTitle == null ){
            mShareBlock.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.share_menu_title));
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
                if (PreferencesUtils.isSoundEffectEnabled(ShareEntry.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(ShareEntry.this);
                }
                return true;
            case R.id.submit:
                share();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void share(){

        mAppConst.hideKeyboard();
        String shareBody = shareEditTExt.getText().toString();
        Map<String, String> params = new HashMap<>();
        params.put("body", shareBody);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        mAppConst.postJsonResponseForUrl(shareUrl, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.shareBlock),
                        getResources().getString(R.string.share_success),
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
                SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.shareBlock),
                        message, new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        });
            }
        });

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
            case R.id.contentTitle:
            case R.id.contentImage:
                onBackPressed();
                break;
        }
    }
}
