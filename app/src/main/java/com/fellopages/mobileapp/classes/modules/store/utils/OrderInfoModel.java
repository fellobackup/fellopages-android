package com.fellopages.mobileapp.classes.modules.store.utils;


import org.json.JSONArray;
import org.json.JSONObject;

public class OrderInfoModel {
    int orderId,totalDownloads,remainingDownloads,totalOrderCount;
    double orderAmount;
    String orderDate,orderStatus,fileTitle,fileOption,defaultCurrency;
    JSONArray orderOptions;
    JSONObject itemDetails;

    public OrderInfoModel(){

    }
    public OrderInfoModel(int orderId,String orderDate,String orderStatus, double orderAmount,
                          JSONArray orderOptions,String defaultCurrency,JSONObject item_details){
        this.orderId =  orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderAmount = orderAmount;
        this.orderOptions = orderOptions;
        this.defaultCurrency = defaultCurrency;
        this.itemDetails = item_details;
    }
    public OrderInfoModel(int orderId,String fileTitle,String fileOption, int totalDownloads,
                          int remainingDownloads){
        this.orderId =  orderId;
        this.fileTitle = fileTitle;
        this.fileOption = fileOption;
        this.totalDownloads = totalDownloads;
        this.remainingDownloads = remainingDownloads;
    }
    public int getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public JSONArray getOrderOptions() {
        return orderOptions;
    }

    public int getTotalDownloads() {
        return totalDownloads;
    }

    public int getRemainingDownloads() {
        return remainingDownloads;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public String getFileOption() {
        return fileOption;
    }

    public int getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(int totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }
    public JSONObject getItemDetails(){
        return itemDetails;
    }
}
