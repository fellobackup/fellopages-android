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

package com.fellopages.mobileapp.classes.modules.advancedEvents.ticketsSelling;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AdvEventsBuyersInfo extends FormActivity {

    private AppConstant mAppConst;
    private Context mContext;
    private Toolbar mToolbar;
    private HashMap postParams;
    private String  mBuyersInfoUrl;
    private RelativeLayout formView;
    private String subject_id, mOrderInfo, mCouponInfo;
    private ProgressBar progressBar;
    private JSONArray ticketsIdArray;
    private JSONObject buyerObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_creation_view);

        /* Create Back Button On Action Bar **/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.action_bar_title_buyer_info));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mContext = this;
        postParams = new HashMap<>();

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);

        mBuyersInfoUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);
        subject_id = getIntent().getStringExtra(ConstantVariables.SUBJECT_ID);
        mOrderInfo = getIntent().getStringExtra(ConstantVariables.RESPONSE_OBJECT);
        mCouponInfo = getIntent().getStringExtra("couponInfoObject");

        formView = (RelativeLayout)findViewById(R.id.form_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        makeRequest(mBuyersInfoUrl);

    }


    public void makeRequest(String getInfoUrl) {
        mAppConst.getJsonResponseFromUrl(getInfoUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                ticketsIdArray = jsonObject.optJSONArray("ticketIds");
                formView.addView(populate(jsonObject, "buyer_form"));
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                progressBar.setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(formView, message,
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.submit:
                mAppConst.hideKeyboard();
                postParams = new HashMap<>();
                postParams = save();

                if (postParams != null && ticketsIdArray != null) {
                    try {
                        buyerObject = new JSONObject();
                        JSONObject ticketsObject= new JSONObject();

                        for(int i = 0; i < ticketsIdArray.length(); i++) {
                            JSONObject jsonObject = ticketsIdArray.optJSONObject(i);
                            String ticketsKey = jsonObject.optString("ticket_id");
                            int count = jsonObject.optInt("ticket_count");
                            JSONObject keyObject = new JSONObject();
                            for (int j = 1; j <= count; j++) {
                                String getKey = ticketsKey + "_" + j;
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("fname", postParams.get("fname_" + getKey));
                                jsonObject1.put("lname", postParams.get("lname_" + getKey));
                                jsonObject1.put("email", postParams.get("email_" + getKey));
                                keyObject.put(String.valueOf(j), jsonObject1);
                            }

                            ticketsObject.put(ticketsKey, keyObject);

                        }

                        buyerObject.put("buyer_detail", ticketsObject);
                        buyerObject.put("isCopiedDetails", postParams.get("isCopiedDetails").toString() == "1" ? "on" : "off");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String paymentMethodUrl = UrlUtil.PAYMENT_METHOD_ADV_EVENTS_URL + "event_id=" +
                            subject_id + "&order_info=" + mOrderInfo;

                    String placeOrderUrl = UrlUtil.PLACED_ORDER_ADV_EVENTS_URL;

                    Intent intent = new Intent(AdvEventsBuyersInfo.this, CreateNewEntry.class);
                    intent.putExtra(ConstantVariables.CREATE_URL, paymentMethodUrl);
                    intent.putExtra(ConstantVariables.FORM_TYPE, "payment_method");
                    intent.putExtra(ConstantVariables.URL_STRING, placeOrderUrl);
                    intent.putExtra(ConstantVariables.SUBJECT_ID, subject_id);
                    intent.putExtra(ConstantVariables.RESPONSE_OBJECT, mOrderInfo);
                    intent.putExtra("buyerInfoObject", buyerObject.toString());
                    intent.putExtra("couponInfoObject", mCouponInfo);
                    intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "core_main_siteevent");
                    startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                    SoundUtil.playSoundEffectOnBackPressed(mContext);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.delete).setVisible(false);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ConstantVariables.CREATE_REQUEST_CODE && resultCode == ConstantVariables.CREATE_REQUEST_CODE) {
            setResult(ConstantVariables.CREATE_REQUEST_CODE);
        }
        finish();
    }

}

