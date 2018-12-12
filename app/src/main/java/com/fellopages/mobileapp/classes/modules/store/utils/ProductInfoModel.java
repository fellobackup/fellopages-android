package com.fellopages.mobileapp.classes.modules.store.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductInfoModel {
    String productTitle,productImage,currency,errorMsg;
    double ratingCount,productPrice,discountedPrice, discountValue, unitPrice;
    int userType,productId,storeId,productQty,productCount;
    int discountAvailable, featured,sponsored, newItem;
    JSONArray wishListArray, optionsMenu;
    JSONObject productConfigurations,productConfigurationsKeys;
    boolean allowModification,addedInWishList,isSelectProduct,isSelectProductChecked = false;

    public ProductInfoModel(){

    }
    public ProductInfoModel(int store_id,int productId, String productTitle, String productImage, double ratingCount,
                            double productPrice, double discountedPrice, int discount, int user_type,
                            double discount_value, int featured, int sponsored, int newLabel, String currency,
                            boolean addedInWishList,boolean isSelectProduct,boolean isSelectProductChecked, JSONArray menu){

        this.storeId =  store_id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.ratingCount = ratingCount;
        this.productPrice = productPrice;
        this.discountedPrice = discountedPrice;
        this.discountAvailable = discount;
        this.userType = user_type;
        this.discountValue = discount_value;
        this.featured = featured;
        this.sponsored = sponsored;
        this.newItem = newLabel;
        this.currency = currency;
        this.addedInWishList = addedInWishList;
        this.isSelectProduct = isSelectProduct;
        this.isSelectProductChecked = isSelectProductChecked;
        this.optionsMenu = menu;

    }

    public ProductInfoModel(String productImage){
        this.productImage = productImage;
    }

    public ProductInfoModel(String productTitle, String productImage, int productId, int productQty,
                            double productPrice, double unitPrice, String error,
                            JSONObject productConfig,String currency, boolean allowModification){
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.productId = productId;
        this.productPrice = productPrice;
        this.productQty = productQty;
        productConfigurations = productConfig;
        this.unitPrice = unitPrice;
        errorMsg = error;
        this.currency = currency;
        this.allowModification = allowModification;

    }
    public ProductInfoModel(String productTitle, String productImage, int productId, int productQty,
                            double productPrice, double unitPrice, String error,
                            JSONObject productConfig,JSONObject productConfigKeys, String currency,
                            boolean allowModification){
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.productId = productId;
        this.productPrice = productPrice;
        this.productQty = productQty;
        productConfigurations = productConfig;
        productConfigurationsKeys = productConfigKeys;
        this.unitPrice = unitPrice;
        errorMsg = error;
        this.currency = currency;
        this.allowModification = allowModification;

    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public double getRatingCount() {
        return ratingCount;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getProductId() {
        return productId;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public int getUserType() {
        return userType;
    }

    public int isDiscountAvailable() {
        return discountAvailable;
    }

    public int isFeatured() {
        return featured;
    }

    public int isSponsored() {
        return sponsored;
    }

    public int isNewItem() {
        return newItem;
    }

    public boolean isAddedInWishList() {
        return addedInWishList;
    }
    public void setAddedInWishList(boolean addedInWishList) {
        this.addedInWishList = addedInWishList ;
    }

    public String getCurrency(){return currency; }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getProductQty() {
        return productQty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public JSONObject getProductConfigurations() {
        return productConfigurations;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public boolean isAllowModification() {
        return allowModification;
    }

    public int getTotalProductCount(){
        return productCount;
    }

    public void setTotalProductCount(int count){
        productCount = count;
    }

    public JSONObject getProductConfigurationsKeys() {
        return productConfigurationsKeys;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public boolean isSelectProduct(){
        return isSelectProduct;
    }
    public boolean isSelectProductChecked(){
        return isSelectProductChecked;
    }
    public void setSelectProductCheckedValue(boolean isChecked){
         this.isSelectProductChecked = isChecked;
    }
    public JSONArray getOptionsMenu(){
        return this.optionsMenu;
    }
}
