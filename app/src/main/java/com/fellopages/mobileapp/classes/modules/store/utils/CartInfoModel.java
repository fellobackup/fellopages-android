package com.fellopages.mobileapp.classes.modules.store.utils;


import org.json.JSONArray;
import org.json.JSONObject;

public class CartInfoModel {
    JSONArray productsArray,noteFormArray;
    JSONObject couponObject;
    String storeTitle,defaultCurrency,storeId, shippingMethodName;
    double subTotal,grandTotal,storeTax,storeTotalAmount,shippingMethodAmount;
    int productCount, productQty, totalItemCount,canApplyCoupon;
    public CartInfoModel(String store_id,String store_title, int products_count, int totalProductQty,double totalAmount,
                         double subTotal, double totalTax, JSONArray productArray, String currency, double grand_total, int canApplyCoupon
                        , int totalItemCount, JSONObject couponObject){
        storeId = store_id;
        storeTitle = store_title;
        defaultCurrency = currency;
        productsArray = productArray;
        productCount = products_count;
        productQty =  totalProductQty;
        this.totalItemCount = totalItemCount;
        this.subTotal = subTotal;
        grandTotal = grand_total;
        this.canApplyCoupon = canApplyCoupon;
        this.couponObject = couponObject;
        storeTotalAmount = totalAmount;
        storeTax = totalTax;

    }
    public CartInfoModel(String store_id,String store_title, int products_count, int totalProductQty,
                         double totalAmount,double subTotal, JSONArray productArray, String currency,
                         double grand_total, double tax, int canApplyCoupon, int totalItemCount,
                         JSONObject couponObject,String shippingMethodName,double shippingMethodPrice,
                         JSONArray noteForm){
        storeId = store_id;
        storeTitle = store_title;
        defaultCurrency = currency;
        productsArray = productArray;
        productCount = products_count;
        productQty =  totalProductQty;
        this.totalItemCount = totalItemCount;
        storeTotalAmount = totalAmount;
        this.subTotal = subTotal;
        grandTotal = grand_total;
        this.canApplyCoupon = canApplyCoupon;
        this.couponObject = couponObject;
        storeTax = tax;
        this.shippingMethodAmount = shippingMethodPrice;
        this.shippingMethodName = shippingMethodName;
        noteFormArray = noteForm;

    }
    public String getStoreId() {
        return storeId;
    }

    public JSONArray getProductsArray() {
        return productsArray;
    }

    public String getStoreTitle() {
        return storeTitle;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public int getProductQty() {
        return productQty;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public int getCanApplyCoupon() {
        return canApplyCoupon;
    }

    public JSONObject getCouponObject() {
        return couponObject;
    }

    public double getStoreTax() {
        return storeTax;
    }

    public JSONArray getNoteFormArray() {
        return noteFormArray;
    }

    public String getShippingMethodName() {
        return shippingMethodName;
    }

    public double getStoreTotalAmount() {
        return storeTotalAmount;
    }

    public double getShippingMethodAmount() {
        return shippingMethodAmount;
    }
}
