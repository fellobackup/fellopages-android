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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BaseButton;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsBrowseDataAdapter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvEventsBuyTicketsInfo extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private AppConstant mAppConst;
    private Context mContext;
    private Toolbar mToolbar;
    private HashMap postParams, urlParams;
    private String mTickestInfoUrl, mTitle, mLocation, mStartTime, mEndTime, mSubjectType = "siteevent_tickets_info";
    private Double grandTotal, subTotal, discount, taxRate, tax;
    private JSONObject mBody, mCouponInfoObject;
    private List<Object> mBrowseItemList;
    ListView mListView;
    View headerView, footerView;
    private ProgressBar progressBar, progressBarSmall;
    TextView ticketsType, price, quantity, title, locationInfo, dateInfo,
            mSubtotalTextView, mDiscountTextView, mTaxTextView, mTaxText, mGrandTotalTextView,
            mHaveCouponTextView, mCouponMessageView;
    EditText mCouponEditText;
    private AdvEventsBrowseDataAdapter mBrowseDataAdapter;
    String mOrderInfoObject;

    BaseButton mBookNowButton, mSubmitCouponButton;
    Spinner occurrenceFilter;
    SpinnerAdapter spinnerAdapter;

    String currency, mEventId, mOccurrenceId;
    int mFilterSelectedItem = 0, isShowCoupon = 0, isShowBookNow = 0, ticketsCount = 0;
    JSONArray mOccurrenceResponse;

    Boolean isBookNowButtonCliked = false, isLoadOccurrenceFilter = true, isCouponApplied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_events_buy_tickets_info);

        /* Create Back Button On Action Bar **/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.action_bar_title_tickets));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mContext = this;
        postParams = new HashMap<>();
        urlParams = new HashMap<>();

        mBrowseItemList = new ArrayList<>();

        //Creating a new instance of AppConstant class
        mAppConst = new AppConstant(this);

        if(getIntent().hasExtra(ConstantVariables.URL_STRING)) {
            mTickestInfoUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);
            mOccurrenceId = getIntent().getStringExtra("occurrence_id");
            urlParams.put("occurrence_id", mOccurrenceId);
        }

        mTitle = getIntent().getStringExtra(ConstantVariables.TITLE);
        mLocation = getIntent().getStringExtra("location");
        mStartTime = getIntent().getStringExtra("starttime");
        mEndTime = getIntent().getStringExtra("endtime");


        mListView = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

        headerView =  ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.tickets_info_list_header, null, false);
        mListView.addHeaderView(headerView);

        footerView =  ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.tickets_info_list_footer, null, false);
        mListView.addFooterView(footerView);

        title = (TextView) headerView.findViewById(R.id.title);
        locationInfo = (TextView) headerView.findViewById(R.id.location_info);
        dateInfo = (TextView) headerView.findViewById(R.id.date_info);
        occurrenceFilter = (Spinner) headerView.findViewById(R.id.filter_view);

        ticketsType = (TextView) headerView.findViewById(R.id.tickets_type);
        price = (TextView) headerView.findViewById(R.id.price);
        quantity = (TextView) headerView.findViewById(R.id.quantity);

        mHaveCouponTextView = (TextView) footerView.findViewById(R.id.have_coupon_text);
        mCouponEditText = (EditText) footerView.findViewById(R.id.coupon_edit_text);
        mSubmitCouponButton = (BaseButton) footerView.findViewById(R.id.coupon_submit_button);
        progressBarSmall = (ProgressBar) footerView.findViewById(R.id.loadingBarSmall);
        mCouponMessageView = (TextView) footerView.findViewById(R.id.coupon_message);
        mSubtotalTextView = (TextView) footerView.findViewById(R.id.subtotal);
        mDiscountTextView = (TextView) footerView.findViewById(R.id.discount);
        mTaxText = (TextView) footerView.findViewById(R.id.taxText);
        mTaxTextView = (TextView) footerView.findViewById(R.id.tax);
        mGrandTotalTextView = (TextView) footerView.findViewById(R.id.total);
        mBookNowButton = (BaseButton) footerView.findViewById(R.id.bookNowButton);
        mBookNowButton.setVisibility(View.VISIBLE);

        spinnerAdapter = new SpinnerAdapter(this, R.layout.simple_text_view, mFilterSelectedItem);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        occurrenceFilter.setAdapter(spinnerAdapter);
        occurrenceFilter.setOnItemSelectedListener(this);

        mBrowseDataAdapter = new AdvEventsBrowseDataAdapter(this, R.layout.tickets_info_layout,
                mBrowseItemList, mSubjectType);
        mListView.setAdapter(mBrowseDataAdapter);

        String getInfoUrl = mAppConst.buildQueryString(mTickestInfoUrl, urlParams);
        makeRequest(getInfoUrl);

        mBrowseDataAdapter.setOnQuantityChangeListener(new AdvEventsBrowseDataAdapter.OnQuantityChangeListener() {
            @Override
            public void onDataChanged(int price, int count, int tickets_id, int isAdd) {
                ticketsCount = count;
                if (isShowBookNow == 1) {
                    if (isShowCoupon == 1 && isCouponApplied) {
                        mCouponMessageView.setVisibility(View.GONE);
                        mCouponEditText.setText("");
                        discount = 0.0;
                    }

                    if (isAdd == 1) {
                        subTotal = price + subTotal;
                        postParams.put("ticket_id_" + tickets_id, String.valueOf(count));
                    } else {
                        subTotal = subTotal-price;
                        postParams.remove("ticket_id_" + tickets_id);

                    }
                    mSubtotalTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, subTotal));
                    mDiscountTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, discount));

                    Double calculatedTax = (subTotal - discount) *(taxRate/100);
                    grandTotal = subTotal - discount + calculatedTax;

                    mTaxTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, calculatedTax));
                    mGrandTotalTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, grandTotal));
                }
            }
        });

        mBookNowButton.setOnClickListener(this);

    }


    public void makeRequest(String getInfoUrl) {

        mAppConst.getJsonResponseFromUrl(getInfoUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
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
        mBrowseItemList.clear();

        if (mTitle != null)
            title.setText(mTitle);

        if (mLocation != null && !mLocation.isEmpty()) {
            locationInfo.setText(mLocation);
        } else {
            locationInfo.setVisibility(View.GONE);
        }

        if (mStartTime != null && mEndTime != null) {
            String dateDetail = getFormattedDate(mStartTime);
            String eDateDetail = getFormattedDate(mEndTime);
            dateInfo.setText(dateDetail + " - " + eDateDetail);
        }

        if (isLoadOccurrenceFilter) {
            spinnerAdapter.clear();
            mOccurrenceResponse = mBody.optJSONArray("occurence");
            if (mOccurrenceResponse != null && mOccurrenceResponse.length() != 0) {
                headerView.findViewById(R.id.occurrenceFilter).setVisibility(View.VISIBLE);
                for (int j = 0; j < mOccurrenceResponse.length(); j++) {
                    JSONObject object = mOccurrenceResponse.optJSONObject(j);
                    String starttime = object.optString("starttime");
                    String endtime = object.optString("endtime");
                    String date = getFormattedDate(starttime) + " - " + getFormattedDate(endtime);
                    spinnerAdapter.add(date);
                }
            } else {
                headerView.findViewById(R.id.occurrenceFilter).setVisibility(View.GONE);
            }

            isLoadOccurrenceFilter = false;
        }

        JSONObject tabsObject = mBody.optJSONObject("tabs");
        String name_column = tabsObject.optString("name_column");
        String price_column = tabsObject.optString("price_column");
        String quantity_column = tabsObject.optString("name");

        isShowCoupon = mBody.optInt("canApplyCoupon");
        currency = mBody.optString("currency");
        subTotal = mBody.optDouble("subTotal");
        discount = mBody.optDouble("discountPrice");
        taxRate = mBody.optDouble("tax_rate");
        tax = mBody.optDouble("tax");
        grandTotal = mBody.optDouble("grandTotal");

        headerView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
        footerView.setVisibility(View.VISIBLE);

        ticketsType.setText(name_column);
        price.setText(price_column);
        quantity.setText(quantity_column);

        JSONArray ticketsJsonArray = mBody.optJSONArray("tickets");

        if(ticketsJsonArray != null && ticketsJsonArray.length() != 0) {
            for (int i = 0; i < ticketsJsonArray.length(); i++) {
                JSONObject jsonObject = ticketsJsonArray.optJSONObject(i);

                mEventId = jsonObject.optString("event_id");
                String title = jsonObject.optString("title");
                int price = jsonObject.optInt("price");
                String quantity = jsonObject.optString("quantity");
                int minValue = jsonObject.optInt("buy_limit_min");
                int maxValue = jsonObject.optInt("buy_limit_max");
                int ticketsId = jsonObject.optInt("ticket_id");
                String endDate = jsonObject.optString("sell_endtime");
                String ticketsStatus = jsonObject.optString("status");
                String statusColor = jsonObject.optString("statusColor");

                if (ticketsStatus.equals("1")) {
                    isShowBookNow = 1;
                }

                mBrowseItemList.add(new BrowseListItems(title, price, quantity, currency, minValue, maxValue,
                        ticketsId, endDate, ticketsStatus, statusColor, jsonObject, ticketsJsonArray.length()));

            }
        }

        if (mBody.has("info")) {
            mOrderInfoObject = mBody.optString("info");

            if (isBookNowButtonCliked) {
                if (mOrderInfoObject != null) {
                    String userInfoUrl = UrlUtil.USERS_INFO_ORDER_ADV_EVENTS_URL + "&event_id=" +
                            mEventId + "&order_info=" + mOrderInfoObject;

                    mAppConst.hideProgressDialog();

                    Intent intent = new Intent(AdvEventsBuyTicketsInfo.this, AdvEventsBuyersInfo.class);
                    intent.putExtra(ConstantVariables.URL_STRING, userInfoUrl);
                    intent.putExtra(ConstantVariables.SUBJECT_ID, mEventId);
                    intent.putExtra(ConstantVariables.RESPONSE_OBJECT, mOrderInfoObject);
                    if (mCouponInfoObject != null)
                    intent.putExtra("couponInfoObject", mCouponInfoObject.toString());
                    startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }

        if (isShowCoupon == 1) {
            mHaveCouponTextView.setVisibility(View.VISIBLE);
            mCouponEditText.setVisibility(View.VISIBLE);
            mSubmitCouponButton.setVisibility(View.VISIBLE);
            mSubmitCouponButton.setOnClickListener(this);
        }

        if (mBody.has("coupon_info")) {
            progressBarSmall.setVisibility(View.GONE);
            mCouponInfoObject = mBody.optJSONObject("coupon_info");
            String couponMessage = null;
            if (mCouponInfoObject.has("coupon_error_msg")) {
                couponMessage = mCouponInfoObject.optString("coupon_error_msg");
                mCouponMessageView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            } else if (mCouponInfoObject.has("coupon_success_msg")) {
                couponMessage = mCouponInfoObject.optString("coupon_success_msg");
                mCouponMessageView.setTextColor(ContextCompat.getColor(mContext, R.color.light_green));
            }

            if (couponMessage != null) {
                mCouponMessageView.setVisibility(View.VISIBLE);
                mCouponMessageView.setText(couponMessage);
                isCouponApplied = true;
            }

        } else {
            mCouponMessageView.setVisibility(View.GONE);
            footerView.findViewById(R.id.divider).setVisibility(View.GONE);
        }

        if (isShowBookNow == 1) {
            mSubtotalTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, subTotal));
            mDiscountTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, discount));
            mTaxText.setText(getResources().getString(R.string.tax_text) + " (" + taxRate + "%)");
            Double calculatedTax = (subTotal - discount) *(taxRate/100);
            mTaxTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, calculatedTax));
            mGrandTotalTextView.setText(GlobalFunctions.getFormattedCurrencyString(currency, grandTotal));
        } else {
            footerView.setVisibility(View.GONE);
        }

    }

    private String getFormattedDate(String time) {
        final String dateFormat = AppConstant.getMonthFromDate(time, "MMM") + " " + AppConstant.getDayFromDate(time) +
                ", " + AppConstant.getYearFormat(time);
        String timeFormat = AppConstant.getHoursFromDate(time);

        String createTextFormat = mContext.getResources().getString(R.string.event_date_info_format);
        String formattedDate = String.format(createTextFormat, dateFormat,
                mContext.getResources().getString(R.string.event_date_info), timeFormat);

        return formattedDate;

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
    public void onClick(View view) {
        int id = view.getId();
        String url;
        switch (id) {
            case R.id.bookNowButton:
                if (ticketsCount != 0) {
                    isBookNowButtonCliked = true;
                    mAppConst.showProgressDialog();
                    url = mAppConst.buildQueryString(mTickestInfoUrl, postParams);
                    url = mAppConst.buildQueryString(url, urlParams);
                    makeRequest(url);
                } else {
                    SnackbarUtils.displaySnackbarLongTime(mListView, getResources().getString(R.string.no_tickets_select_message));
                }
                break;

            case R.id.coupon_submit_button:
                if (mCouponEditText.getText() != null && !mCouponEditText.getText().toString().isEmpty() && ticketsCount != 0) {
                    progressBarSmall.setVisibility(View.VISIBLE);
                    postParams.put("coupon_code", mCouponEditText.getText().toString());
                    mCouponEditText.setCursorVisible(false);
                    url = mAppConst.buildQueryString(mTickestInfoUrl, postParams);
                    makeRequest(url);
                } else if (mCouponEditText.getText() == null || mCouponEditText.getText().toString().isEmpty()){
                    mCouponMessageView.setVisibility(View.VISIBLE);
                    mCouponMessageView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    mCouponMessageView.setText(getResources().getString(R.string.enter_coupon_code_text));
                } else {
                    mCouponMessageView.setVisibility(View.VISIBLE);
                    mCouponMessageView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    mCouponMessageView.setText(getResources().getString(R.string.tickets_count_text_message_for_apply_coupon));
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ConstantVariables.CREATE_REQUEST_CODE && resultCode == ConstantVariables.CREATE_REQUEST_CODE) {
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        progressBar.setVisibility(View.VISIBLE);
        urlParams.clear();
        spinnerAdapter.getCustomView(position, view, parent, mFilterSelectedItem);
        JSONObject object = mOccurrenceResponse.optJSONObject(position);
        String occurrence_id = object.optString("occurrence_id");
        urlParams.put("occurrence_id", occurrence_id);
        String getInfoUrl = mAppConst.buildQueryString(mTickestInfoUrl, urlParams);
        makeRequest(getInfoUrl);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

