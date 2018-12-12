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

package com.fellopages.mobileapp.classes.modules.store.order;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.transition.Visibility;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class OrderViewActivity extends AppCompatActivity {
    private AppConstant mAppConst;
    private String orderViewUrl;
    private LinearLayout mBillingInfoView, mShippingDetailsView, mOrderInfoView, mPaymentInfoView;
    private LinearLayout mOrderSummaryView, mProductInfoView, mShippingInfoView;
    private GridLayout mPriceFieldsView;
    private TextView mOrderDate, mOrderStatus, mOrderTax, mOrderShipping, mOrderDelivery, mOrderIp, mOrderId;
    TextView storeTitle, totalAmount, shippingAmount, taxVat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.order_detail_label));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAppConst = new AppConstant(this);
        orderViewUrl = AppConstant.DEFAULT_URL;
        if(getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL) != null){
            orderViewUrl=getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        }
        if(getIntent().getStringExtra("shipping_url") != null){
            orderViewUrl=getIntent().getStringExtra("shipping_url");
        }
        mBillingInfoView = (LinearLayout) findViewById(R.id.billing_info_view);
        mShippingInfoView = (LinearLayout) findViewById(R.id.shipping_info_view);
        mOrderInfoView = (LinearLayout) findViewById(R.id.order_info_view);
        mPaymentInfoView = (LinearLayout) findViewById(R.id.payment_info_view);
        mOrderSummaryView = (LinearLayout) findViewById(R.id.order_summary_info_view);
        mProductInfoView = (LinearLayout) findViewById(R.id.product_info_view);
        mShippingDetailsView = (LinearLayout) findViewById(R.id.shipping_details_view);
        mPriceFieldsView = (GridLayout)findViewById(R.id.price_fields);
        mOrderDate =(TextView) findViewById(R.id.order_date);
        mOrderStatus =(TextView) findViewById(R.id.order_status);
        mOrderTax =(TextView) findViewById(R.id.order_tax_amount);
        mOrderShipping =(TextView) findViewById(R.id.order_shipping_amount);
        mOrderDelivery =(TextView) findViewById(R.id.order_delivery);
        mOrderIp =(TextView) findViewById(R.id.ip_address);
        mOrderId =(TextView) findViewById(R.id.order_id);

        storeTitle = (TextView) findViewById(R.id.store_title);
        totalAmount = (TextView) findViewById(R.id.total_amount);
        taxVat = (TextView) findViewById(R.id.taxVatAmount);
        shippingAmount = (TextView) findViewById(R.id.shipping_amount);

        getOrderDetails();
    }

    public void getOrderDetails(){
        mAppConst.getJsonResponseFromUrl(orderViewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                setOrderDetails(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);

            }
        });
    }

    public void setOrderDetails(JSONObject orderDetails){
        String currency = orderDetails.optString("currency");
        JSONObject orderObject =  orderDetails.optJSONObject("order");
        JSONArray shipmentTrackingObject = orderDetails.optJSONArray("shipment_tracking");
        JSONArray billingAddressArray =  orderDetails.optJSONArray("billing_address");
        JSONArray shippingAddressArray = orderDetails.optJSONArray("shipping_address");
        JSONObject paymentObject = orderDetails.optJSONObject("payment");
        JSONObject shippingObject = orderDetails.optJSONObject("shipping");
        JSONObject storeObject = orderDetails.optJSONObject("stores");
        JSONObject amountFieldObject = orderDetails.optJSONObject("totalAmountFields");

        if(shipmentTrackingObject != null){
            setTrackingDetails(shipmentTrackingObject);
        }
        setDetailsForArray(mBillingInfoView,billingAddressArray);
        setDetailsForArray(mShippingDetailsView,shippingAddressArray);

        if(orderObject != null){
            mOrderInfoView.setVisibility(View.VISIBLE);
            mOrderDate.setText(getResources().getString(R.string.order_date_label) +"   :   "
                    + AppConstant.convertDateFormat(getResources(),
                    orderObject.optString("order_date")));
            mOrderStatus.setText(getResources().getString(R.string.order_status_label) +"   :   "
                    +orderObject.optString("order_status"));
            mOrderTax.setText(getResources().getString(R.string.tax_vat) +"  :  "
                    +orderObject.optString("tax_amount"));
            mOrderShipping.setText(getResources().getString(R.string.shipping_price_label) +"   :   "
                    +orderObject.optString("shipping_amount"));
            mOrderDelivery.setText(getResources().getString(R.string.delivery_time_label) +"   :   "
                    +orderObject.optString("delivery_time"));
            mOrderIp.setText(getResources().getString(R.string.ip_address_label) +"   :   "
                    +orderObject.optString("ip_address"));
            mOrderId.setText(getResources().getString(R.string.order_id_label) + " : "+orderObject.optString("order_id"));

        }
        int dimen5dp = getResources().getDimensionPixelSize(R.dimen.padding_5dp);
        int dimen10dp = getResources().getDimensionPixelSize(R.dimen.padding_10dp);
        if(paymentObject != null){
            mPaymentInfoView.setVisibility(View.VISIBLE);
            AppCompatTextView labelView = new AppCompatTextView(this);
            AppCompatTextView paymentOption = new AppCompatTextView(this);
            labelView.setText(paymentObject.optString("label"));
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_medium_font_size));
            labelView.setTypeface(labelView.getTypeface(), Typeface.BOLD);
            labelView.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp), dimen5dp, dimen5dp, dimen5dp);

            paymentOption.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            paymentOption.setText(getResources().getString(R.string.payment_method_label) +"   :   "
                    +paymentObject.optString("payment_method"));
            paymentOption.setPadding(dimen10dp, dimen5dp, dimen5dp, dimen5dp);
            labelView.setTextColor(ContextCompat.getColor(this,R.color.black));
            paymentOption.setTextColor(ContextCompat.getColor(this,R.color.black));
            mPaymentInfoView.addView(labelView);
            mPaymentInfoView.addView(paymentOption);
        }

        if(shippingObject != null){
            mShippingInfoView.setVisibility(View.VISIBLE);
            AppCompatTextView labelView = new AppCompatTextView(this);
            AppCompatTextView shippingName = new AppCompatTextView(this);
            labelView.setText(shippingObject.optString("label"));
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_medium_font_size));
            labelView.setTypeface(labelView.getTypeface(), Typeface.BOLD);
            labelView.setPadding(dimen10dp, dimen5dp, dimen5dp, dimen5dp);

            shippingName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            shippingName.setText(getResources().getString(R.string.shipping_method_label) +"   :   "
                    +shippingObject.optString("name"));
            shippingName.setPadding(dimen10dp, dimen5dp, dimen5dp, dimen5dp);
            labelView.setTextColor(ContextCompat.getColor(this,R.color.black));
            shippingName.setTextColor(ContextCompat.getColor(this,R.color.black));
            mShippingInfoView.addView(labelView);
            mShippingInfoView.addView(shippingName);
        }

        if(amountFieldObject != null){
            mOrderSummaryView.setVisibility(View.VISIBLE);
            showOrderSummery(amountFieldObject,currency);
        }

        if(storeObject != null){
            mProductInfoView.setVisibility(View.VISIBLE);
            Iterator<?> keys = storeObject.keys();

            while( keys.hasNext() ) {
                JSONObject newObject = storeObject.optJSONObject(String.valueOf(keys.next()));
                storeTitle.setText(newObject.optString("name"));
                totalAmount.setText(GlobalFunctions.getFormattedCurrencyString(currency,newObject.optDouble("total")));
                taxVat.setText(GlobalFunctions.getFormattedCurrencyString(currency,newObject.optDouble("tax")));
                shippingAmount.setText(GlobalFunctions.getFormattedCurrencyString(
                        currency,newObject.optDouble("shipping_method_price")));

                JSONArray productArray = newObject.optJSONArray("products");
                if(productArray != null) {
                    for(int i = 0;i<productArray.length();i++) {
                        JSONObject productObject = productArray.optJSONObject(i);
                        View productInfoView = getLayoutInflater().inflate(R.layout.ordered_product_view, null);
                        productInfoView.findViewById(R.id.info_fields).setVisibility(View.GONE);
                        productInfoView.findViewById(R.id.product_info_fields).setVisibility(View.VISIBLE);
                        TextView productTitle = (TextView) productInfoView.findViewById(R.id.item_title);
                        TextView productSku = (TextView) productInfoView.findViewById(R.id.sku_name);
                        TextView productQty = (TextView) productInfoView.findViewById(R.id.product_qty);
                        TextView productPrice = (TextView) productInfoView.findViewById(R.id.product_price);
                        TextView productTax = (TextView) productInfoView.findViewById(R.id.taxVatAmount);
                        TextView productTotal = (TextView) productInfoView.findViewById(R.id.subtotal_amount);
                        TextView productStatus = (TextView) productInfoView.findViewById(R.id.product_status);
                        ImageView productThumb = (ImageView)productInfoView.findViewById(R.id.product_thumb);
                        String productImage = productObject.optString("image");
                        Picasso.with(OrderViewActivity.this)
                                .load(productImage)
                                .placeholder(R.drawable.nophoto_product)
                                .into(productThumb);
                        productTitle.setText(productObject.optString("title"));
                        productSku.setText(productObject.optString("product_sku"));
                        productQty.setText("Qty: " + productObject.optString("quantity"));
                        productPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                                currency,productObject.optDouble("unitPrice")));
                        productTax.setText("   :   " + GlobalFunctions.
                                getFormattedCurrencyString(currency,productObject.optDouble("tax")));
                        productTotal.setText("   :   " + GlobalFunctions.getFormattedCurrencyString(currency,productObject.optDouble("price")));
                        if(orderObject != null){
                            productStatus.setText(orderObject.optString("order_status")+"("+orderObject.optString("delivery_time")+")");
                        }
                        mProductInfoView.addView(productInfoView);
                    }
                }
            }

        }

    }

    public void setTrackingDetails(JSONArray shipmentTrackingArray){
        mShippingDetailsView.setVisibility(View.VISIBLE);
        for(int i=0;i < shipmentTrackingArray.length();i++){
            JSONObject trackingObject =  shipmentTrackingArray.optJSONObject(i);
            AppCompatTextView service = new AppCompatTextView(this);
            AppCompatTextView title = new AppCompatTextView(this);
            AppCompatTextView tracking_number = new AppCompatTextView(this);
            AppCompatTextView date = new AppCompatTextView(this);
            AppCompatTextView status = new AppCompatTextView(this);
            AppCompatTextView note = new AppCompatTextView(this);
            service.setText(getResources().getString(R.string.service_label) +"   :   "+
                    trackingObject.optString("service"));
            title.setText(getResources().getString(R.string.title_label) +"   :   "
                    +trackingObject.optString("title"));
            tracking_number.setText(getResources().getString(R.string.tracking_id) +"  :  "
                    +trackingObject.optString("tracking_number"));
            date.setText(getResources().getString(R.string.order_date_label) +"   :   "
                    +AppConstant.convertDateFormat(getResources(),trackingObject.optString("date")));
            status.setText(getResources().getString(R.string.order_status_label) +"   :   "
                    +trackingObject.optString("status"));
            note.setText(getResources().getString(R.string.note_label) +"   :   "
                    +trackingObject.optString("note"));


            service.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            service.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
            service.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            service.setTextColor(ContextCompat.getColor(this,R.color.black));

            title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            title.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
            title.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            title.setTextColor(ContextCompat.getColor(this,R.color.black));

            tracking_number.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            tracking_number.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
            tracking_number.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            tracking_number.setTextColor(ContextCompat.getColor(this,R.color.black));

            date.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            date.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
            date.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            date.setTextColor(ContextCompat.getColor(this,R.color.black));

            status.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            status.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
            status.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            status.setTextColor(ContextCompat.getColor(this,R.color.black));

            note.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            note.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
            note.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            note.setTextColor(ContextCompat.getColor(this,R.color.black));

            mShippingDetailsView.addView(service);
            mShippingDetailsView.addView(title);
            mShippingDetailsView.addView(tracking_number);
            mShippingDetailsView.addView(date);
            mShippingDetailsView.addView(status);
            mShippingDetailsView.addView(note);

        }

    }
    public void setDetailsForArray(final LinearLayout layoutView,JSONArray jsonArray){

        if(jsonArray != null){
            layoutView.setVisibility(View.VISIBLE);
            for(int i=0;i<jsonArray.length();i++){
                final AppCompatTextView textView = new AppCompatTextView(this);
                textView.setText(jsonArray.optString(i));
                textView.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
                textView.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                        getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                if(i==0){
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen.body_medium_font_size));
                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expend,0);
                    textView.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                            getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                            getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                            getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                    textView.setTag(jsonArray.length());
                    textView.setActivated(true);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(textView.isActivated()){
                                textView.setActivated(false);
                                setChildVisibility(layoutView,Integer.parseInt(textView.getTag().toString()), View.VISIBLE);
                            } else {
                                textView.setActivated(true);
                                setChildVisibility(layoutView,Integer.parseInt(textView.getTag().toString()), View.GONE);
                            }

                            layoutView.getChildAt(1).setVisibility(View.GONE);

                        }
                    });

                }else {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            getResources().getDimension(R.dimen.body_default_font_size));
                    textView.setVisibility(View.GONE);
                }
                layoutView.addView(textView);
                textView.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        }
    }
    public void setChildVisibility(LinearLayout layout,int childCount, int visibility){
        for(int i = 1; i< childCount; ++i){
            layout.getChildAt(i).setVisibility(visibility);
        }

    }

    public void showOrderSummery(JSONObject amountFieldObject, String currency){
        mPriceFieldsView.removeAllViews();

        Iterator<?> keys = amountFieldObject.keys();

        while( keys.hasNext() ) {
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
            AppCompatTextView labelView = new AppCompatTextView(this);
            AppCompatTextView mainText = new AppCompatTextView(this);
            String key = (String) keys.next();
            labelView.setText(key);
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.body_default_font_size));
            labelView.setTextColor(ContextCompat.getColor(this,R.color.black));
            labelView.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);
            labelView.setPadding(getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                    getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
            mainText.setText(GlobalFunctions.getFormattedCurrencyString(currency,amountFieldObject.optDouble(key)));
            mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.body_default_font_size));

            mainText.setLayoutParams(layoutParams);
            mainText.setGravity(GravityCompat.END| Gravity.CENTER_VERTICAL);
            mainText.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                    getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
            mPriceFieldsView.addView(labelView);
            mPriceFieldsView.addView(mainText);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(this)) {
                SoundUtil.playSoundEffectOnBackPressed(this);
            }
        }
        return true;
    }
}
