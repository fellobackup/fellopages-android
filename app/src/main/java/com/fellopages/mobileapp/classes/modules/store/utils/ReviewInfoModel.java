package com.fellopages.mobileapp.classes.modules.store.utils;


import org.json.JSONArray;

public class ReviewInfoModel {
    String reviewTitle,reviewDesc, reviewOwner,reviewLikes,reviewViews,reviewComments;
    String reviewPros,reviewCons, reviewDate,userRecommendation;
    int reviewRating,reviewId;
    boolean isReviewLiked;
    JSONArray reviewOptions;
    public ReviewInfoModel(int review_id, String title, String body, String owner_title,
                           String like_count, String view_count, String comment_count, String pros,
                           String cons, int overall_rating, boolean isLiked, String date,
                           String recommendation, JSONArray reviewOptions) {
        reviewId = review_id;
        reviewTitle = title;
        reviewDesc = body;
        reviewOwner = owner_title;
        reviewLikes = like_count;
        reviewViews = view_count;
        reviewComments = comment_count;
        reviewPros = pros;
        reviewCons = cons;
        reviewRating = overall_rating;
        isReviewLiked = isLiked;
        reviewDate = date;
        userRecommendation = recommendation;
        this.reviewOptions = reviewOptions;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public String getReviewDesc() {
        return reviewDesc;
    }

    public String getReviewOwner() {
        return reviewOwner;
    }

    public String getReviewLikes() {
        return reviewLikes;
    }

    public String getReviewViews() {
        return reviewViews;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public String getReviewPros() {
        return reviewPros;
    }

    public String getReviewCons() {
        return reviewCons;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public boolean isReviewLiked() {
        return isReviewLiked;
    }

    public String getUserRecommendation() {
        return userRecommendation;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public JSONArray getReviewOptions() {
        return reviewOptions;
    }

    public void setReviewLiked(boolean reviewLiked) {
        isReviewLiked = reviewLiked;
    }

    public void setReviewLikes(String reviewLikes) {
        this.reviewLikes = reviewLikes;
    }
}
