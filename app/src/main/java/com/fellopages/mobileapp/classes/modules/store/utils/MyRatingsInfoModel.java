package com.fellopages.mobileapp.classes.modules.store.utils;


import org.json.JSONArray;

public class MyRatingsInfoModel {
    int avgRating,userCount,reviewId;
    String recommendedCount;
    JSONArray breakDownPrams,myRatingParams;
    public MyRatingsInfoModel(int reviewId,int avgRating,int userCount,String recommendedCount,
                              JSONArray breakDownPrams,JSONArray myRatingParams){
        this.reviewId = reviewId;
        this.userCount = userCount;
        this.avgRating = avgRating;
        this.recommendedCount = recommendedCount;
        this.breakDownPrams = breakDownPrams;
        this.myRatingParams = myRatingParams;

    }

    public int getReviewId() {
        return reviewId;
    }

    public int getAvgRating() {
        return avgRating;
    }

    public int getUserCount() {
        return userCount;
    }

    public String getRecommendedCount() {
        return recommendedCount;
    }

    public JSONArray getBreakDownPrams() {
        return breakDownPrams;
    }

    public JSONArray getMyRatingParams() {
        return myRatingParams;
    }
}

