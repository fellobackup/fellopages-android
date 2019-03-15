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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsBrowseDataAdapter;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvEventsOrderViewPage extends AppCompatActivity implements View.OnClickListener {

    private AppConstant mAppConst;
    private Context mContext;
    private Toolbar mToolbar;
    private String mTitle, mStartTime, mEndTime, mSubjectType = "ordered_tickets_info";
    private Double grandTotal, subTotal;
    private JSONObject mBody;
    private List<Object> mBrowseItemList;
    ListView mListView;
    View headerView, footerView;
    private ProgressBar progressBar;
    private LinearLayout mTaxPayerBlock;
    private int user_id, mEventId;

    TextView ticketsType, price, quantity, title, dateInfo, mSubtotalTextView, mTaxTextView, mTaxText,
            mGrandTotalTextView, mUserNameTextView, mEmailTextView, mPaymentMethodTextView, mOrderDateTextView,
            mTaxAmountTextView, mTaxPayerIdTextView, subtotalColumn;
    TextView mOrderStatusTextView, mCommissionTypeTextView, mCommissiontRateTextView, mCommissionAmountTextView;

    private AdvEventsBrowseDataAdapter mBrowseDataAdapter;
    String currency, mOrderId, mOrderGetInfoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_events_buy_tickets_info);

        /* Create Back Button On Action Bar **/

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getIntent().hasExtra("order_id"))
            mOrderId = getIntent().getStringExtra("order_id");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.action_bar_title_order_view_page) + " #" +mOrderId);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mContext = this;
        mBrowseItemList = new ArrayList<>();

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);

        if(getIntent().hasExtra(ConstantVariables.URL_STRING))
            mOrderGetInfoUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);

        mListView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progressBarMain);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,0);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);

        mListView.setFooterDividersEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.setLayoutParams(layoutParams);

        headerView =  ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.order_info_header, null, false);
        mListView.addHeaderView(headerView);

        footerView =  ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.tickets_info_list_footer, null, false);

        int padding10 = (int)getResources().getDimension(R.dimen.padding_10dp);

        footerView.setPadding(padding10, padding10, padding10, padding10);
        mListView.addFooterView(footerView);

        title = headerView.findViewById(R.id.title);
        dateInfo = headerView.findViewById(R.id.date_info);
        mEmailTextView = headerView.findViewById(R.id.email);
        mUserNameTextView = headerView.findViewById(R.id.user_name);
        mOrderDateTextView = headerView.findViewById(R.id.order_date);
        mOrderStatusTextView = headerView.findViewById(R.id.order_status);
        mPaymentMethodTextView = headerView.findViewById(R.id.payment_method);
        mCommissionTypeTextView = headerView.findViewById(R.id.commission_type);
        mCommissiontRateTextView = headerView.findViewById(R.id.commission_rate);
        mCommissionAmountTextView = headerView.findViewById(R.id.commission_amount);

        mTaxAmountTextView = headerView.findViewById(R.id.tax_amount);
        mTaxPayerBlock = headerView.findViewById(R.id.taxpayer_block);
        mTaxPayerIdTextView = headerView.findViewById(R.id.taxpayer_id);

        ticketsType = headerView.findViewById(R.id.tickets_type);
        price = headerView.findViewById(R.id.price);
        quantity = headerView.findViewById(R.id.quantity);
        subtotalColumn = headerView.findViewById(R.id.subtotal);

        mSubtotalTextView = footerView.findViewById(R.id.subtotal);
        mTaxText = footerView.findViewById(R.id.taxText);
        mTaxTextView = footerView.findViewById(R.id.tax);
        mGrandTotalTextView = footerView.findViewById(R.id.total);
        footerView.findViewById(R.id.order_summary_text).setVisibility(View.VISIBLE);
        footerView.findViewById(R.id.divider).setVisibility(View.GONE);
        footerView.findViewById(R.id.discount).setVisibility(View.GONE);
        footerView.findViewById(R.id.discountText).setVisibility(View.GONE);
        footerView.findViewById(R.id.bookNowButton).setVisibility(View.GONE);

        title.setOnClickListener(this);
        mUserNameTextView.setOnClickListener(this);

        mBrowseDataAdapter = new AdvEventsBrowseDataAdapter(this, R.layout.tickets_info_layout,
                mBrowseItemList, mSubjectType);

        mListView.setAdapter(mBrowseDataAdapter);

        makeRequest(mOrderGetInfoUrl);

    }



    public void makeRequest(String getInfoUrl) {

        mAppConst.getJsonResponseFromUrl(getInfoUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBody = jsonObject;
                loadDataInView(mBody);
            }
            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                progressBar.setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(mListView, message, new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {
                        finish();
                    }
                });
            }
        });
    }

    private void loadDataInView(JSONObject mBody) {

        progressBar.setVisibility(View.GONE);
        mBrowseItemList.clear();

        currency = mBody.optString("currency");
        mTitle = mBody.optString("event_title");
        mEventId = mBody.optInt("event_id");
        String userTitle = mBody.optString("user_title");
        String userEmail = mBody.optString("user_email");
        user_id = mBody.optInt("user_id");
        String paymentMethod = mBody.optString("payment_method");
        String orderDate = mBody.optString("creation_date");
        String orderStatus = mBody.optString("order_status_text");
        String commissionType = mBody.optString("commission_type");
        String commissionRate = mBody.optString("commission_rate");
        String commissionAmount = mBody.optString("commission_value");
        Double taxAmount = mBody.optDouble("tax_amount");


        String taxPayerId = mBody.optString("tax_id_no");
        mStartTime = mBody.optString("occurrence_starttime");
        mEndTime = mBody.optString("occurrence_endtime");

        JSONObject tabsObject = mBody.optJSONObject("tabs");
        String ticketLabel = tabsObject.optString("ticket");
        String priceLabel = tabsObject.optString("price");
        String quantityLabel = tabsObject.optString("quantity");
        String subtotalLabel = tabsObject.optString("subtotal");

        subTotal = mBody.optDouble("sub_total");
        grandTotal = mBody.optDouble("grand_total");

        headerView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
        footerView.setVisibility(View.VISIBLE);

        title.setText(mTitle);

        final String dateFormat = AppConstant.getMonthFromDate(mStartTime, "MMM") + " " + AppConstant.getDayFromDate(mStartTime) +
                ", " + AppConstant.getYearFormat(mStartTime);
        String timeFormat = AppConstant.getHoursFromDate(mStartTime);

        String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
        String dateDetail = String.format(createTextFormat, dateFormat,
                mContext.getResources().getString(R.string.event_date_info), timeFormat);

        final String eDateFormat = AppConstant.getMonthFromDate(mEndTime, "MMM") + " " + AppConstant.getDayFromDate(mEndTime) +
                ", " + AppConstant.getYearFormat(mEndTime);
        String eTimeFormat = AppConstant.getHoursFromDate(mEndTime);

        String eDateDetail = String.format(createTextFormat, eDateFormat,
                mContext.getResources().getString(R.string.event_date_info), eTimeFormat);

        dateInfo.setText(dateDetail + " - " + eDateDetail);

        final String orderDateFormat = AppConstant.getMonthFromDate(orderDate, "MMM") + " " + AppConstant.getDayFromDate(orderDate) +
                ", " + AppConstant.getYearFormat(orderDate);
        String orderTimeFormat = AppConstant.getHoursFromDate(orderDate);

        String orderDateDetail = String.format(createTextFormat, orderDateFormat,
                mContext.getResources().getString(R.string.event_date_info), orderTimeFormat);


        mUserNameTextView.setText(userTitle);
        mEmailTextView.setText(userEmail);
        mPaymentMethodTextView.setText(": " + paymentMethod);
        mOrderDateTextView.setText(": " + orderDateDetail);
        mOrderStatusTextView.setText(": " + orderStatus);
        mCommissionTypeTextView.setText(": " + commissionType);
        mCommissiontRateTextView.setText(": " + commissionRate);
        mCommissionAmountTextView.setText(": " + commissionAmount);
        mTaxAmountTextView.setText(": " + GlobalFunctions.getFormattedCurrencyString(currency, taxAmount));

        if (taxPayerId != null && !taxPayerId.isEmpty()) {
            mTaxPayerIdTextView.setText(": " + taxPayerId);
        } else {
            mTaxPayerBlock.setVisibility(View.GONE);
        }

        ticketsType.setText(ticketLabel);
        price.setText(priceLabel);
        quantity.setText(quantityLabel);
        subtotalColumn.setText(subtotalLabel);

        JSONArray ticketsJsonArray = mBody.optJSONArray("tickets");

        for (int i = 0; i < ticketsJsonArray.length(); i++) {
            JSONObject jsonObject = ticketsJsonArray.optJSONObject(i);

            String title = jsonObject.optString("title");
            int price = jsonObject.optInt("price");
            String quantity = jsonObject.optString("quantity");
            Double subTotal = jsonObject.optDouble("subTotal");
            mBrowseItemList.add(new BrowseListItems(title, price, currency, quantity, subTotal));
        }
        mSubtotalTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, subTotal));
        mTaxText.setText(getResources().getString(R.string.tax_text));
        mTaxTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, taxAmount));
        mGrandTotalTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, grandTotal));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.user_name:
                Intent intent = new Intent(mContext, userProfile.class);
                intent.putExtra(ConstantVariables.USER_ID,  user_id);
                mContext.startActivity(intent);
                break;

            case R.id.title:
                Intent mainIntent = GlobalFunctions.getIntentForModule(mContext, mEventId,
                        "core_main_siteevent", null);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }
}

