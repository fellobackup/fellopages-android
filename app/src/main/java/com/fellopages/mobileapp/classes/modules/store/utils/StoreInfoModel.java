package com.fellopages.mobileapp.classes.modules.store.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class StoreInfoModel {
    String storeTitle,storeImage,storeCategory,ownerImage,storeId;
    int likeCount,commentCount,isClosed,totalItemCount,ownerId,isFeatured,isSponsored;
    JSONArray menuArray;
    public JSONObject storeDetails;

    public StoreInfoModel(){

    }
    public StoreInfoModel(String store_id,String title, String image, String category,String owner_image,
                          int owner_id,int featured, int sponsored,int like_count,int comment_count) {
        storeId = store_id;
        storeTitle = title;
        storeImage = image;
        storeCategory = category;
        ownerImage = owner_image;
        likeCount = like_count;
        commentCount = comment_count;
        ownerId = owner_id;
        isFeatured = featured;
        isSponsored = sponsored;
    }
    public StoreInfoModel(String store_id,String title, String image, String category,String owner_image,
                          int owner_id,int featured, int sponsored,int like_count,int comment_count, JSONObject storeDetails) {
        storeId = store_id;
        storeTitle = title;
        storeImage = image;
        storeCategory = category;
        ownerImage = owner_image;
        likeCount = like_count;
        commentCount = comment_count;
        ownerId = owner_id;
        isFeatured = featured;
        isSponsored = sponsored;
        this.storeDetails = storeDetails;
    }
    public StoreInfoModel(String store_id, String title, String image, String category, String owner_image,
                          int owner_id,int featured, int sponsored, int like_count, int comment_count,
                          int closed ,JSONArray menuArray, JSONObject storeDetails) {
        storeId = store_id;
        storeTitle = title;
        storeImage = image;
        storeCategory = category;
        ownerImage = owner_image;
        likeCount = like_count;
        commentCount = comment_count;
        this.menuArray = menuArray;
        this.isClosed = closed;
        ownerId = owner_id;
        isFeatured = featured;
        isSponsored = sponsored;
        this.storeDetails = storeDetails;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreTitle() {
        return storeTitle;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public String getStoreCategory() {
        return storeCategory;
    }

    public String getOwnerImage() { return ownerImage; }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public JSONArray getMenuArray() {
        return menuArray;
    }

    public int getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(int isClosed) {
        this.isClosed = isClosed;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getIsFeatured() {
        return isFeatured;
    }

    public int getIsSponsored() {
        return isSponsored;
    }
}
