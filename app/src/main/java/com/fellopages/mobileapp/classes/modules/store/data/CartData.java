package com.fellopages.mobileapp.classes.modules.store.data;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class CartData {

    public static final String PAYMENT_GATEWAY_PREF = "paymentgateway";
    public static final String SHIPPING_INFO_PREF = "shippinginfo";
    public static final String SHIPPING_ADD_INFO_PREF = "shippingaddinfo";
    public static final String BILLING_INFO_PREF = "billinginfo";
    public static final String STORE_PREF = "storeinfo";
    public static final String ORDER_PREF = "orderpref";


    public static final String PAYMENT_GATEWAY_KEY = "payment_gateway_id";
    public static final String STORE_KEY = "store_id";
    public static final String BILLING_KEY = "billingAddress_id";
    public static final String SHIPPING_ADDRESS_KEY ="shippingAddress_id";
    public static final String PRIVATE_ORDER_KEY = "is_private_order";


    //Coupon codes for store plugin
    public static final String STORE_COUPON_PREF = "store_coupons";


    public static void updatePaymentGateway(Context context,String paymentGatewayId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PAYMENT_GATEWAY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PAYMENT_GATEWAY_KEY, paymentGatewayId);
        editor.apply();
    }
    public static String getPaymentGatewayInfo(Context context){
        return context.getSharedPreferences(PAYMENT_GATEWAY_PREF, Context.MODE_PRIVATE)
                .getString(PAYMENT_GATEWAY_KEY, null);

    }

    public static void clearPaymentInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PAYMENT_GATEWAY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void updateShippingMethodInfo(Context context,String methodKey,String methodValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHIPPING_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(methodKey, methodValue);
        editor.apply();
    }
    public static String getShippingMethodInfo(Context context){
        String url = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHIPPING_INFO_PREF, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            url += "&" +entry.getKey() + "=" + entry.getValue();
        }
        return url;
    }

    public static void clearShippingInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHIPPING_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void updateStoreInfo(Context context, String store_id){
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORE_KEY, store_id);
        editor.apply();

    }
    public static String getStoreInfo(Context context){
        return context.getSharedPreferences(STORE_PREF, Context.MODE_PRIVATE)
                .getString(STORE_KEY, null);
    }

    public static void clearStoreInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void updateBillingAddressId(Context context,String addressId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(BILLING_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BILLING_KEY, addressId);
        editor.apply();
    }
    public static String getBillingAddressId(Context context){
        return context.getSharedPreferences(BILLING_INFO_PREF, Context.MODE_PRIVATE)
                .getString(BILLING_KEY, null);
    }

    public static void clearBillingAddInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(BILLING_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void updateShippingAddressId(Context context,String addressId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHIPPING_ADD_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHIPPING_ADDRESS_KEY, addressId);
        editor.apply();
    }

    public static String getShippingAddressId(Context context){
        return context.getSharedPreferences(SHIPPING_ADD_INFO_PREF, Context.MODE_PRIVATE)
                .getString(SHIPPING_ADDRESS_KEY, null);
    }
    public static void clearShippingAddInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHIPPING_ADD_INFO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Storing the coupon codes for logged out user.
     */

    public static void updateCouponCode(Context context,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_COUPON_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Getting the coupon codes for logged out user.
     */

    public static String getCouponCodeParams(Context context){
        String url = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_COUPON_PREF, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            url += "&" +entry.getKey() + "=" + entry.getValue();
        }
        return url;
    }
    public static void clearCouponData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_COUPON_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void updateOrderDetails(Context context,boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ORDER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PRIVATE_ORDER_KEY, value);
        editor.apply();
    }
    public static boolean getOrderDetails(Context context){
        return context.getSharedPreferences(ORDER_PREF, Context.MODE_PRIVATE)
                .getBoolean(PRIVATE_ORDER_KEY, true);
    }
    public static void clearOrderDetails(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ORDER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    public static void clearCartData(Context context){
        clearCouponData(context);
        clearBillingAddInfo(context);
        clearPaymentInfo(context);
        clearShippingInfo(context);
        clearShippingAddInfo(context);
        clearStoreInfo(context);
        clearOrderDetails(context);
    }
}
