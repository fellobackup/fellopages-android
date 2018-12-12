package com.fellopages.mobileapp.classes.modules.store.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.ProgressBarHolder;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.store.utils.MyRatingsInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.ReviewInfoModel;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter {
    private static final int VIEW_ITEM = 1;
    private static final int VIEW_PROG = 0;
    private static final int VIEW_HEADER = 2;

    private Context mContext;
    private List<Object> mReviewList;
    private ReviewInfoModel mReviewInfo;
    private OnItemClickListener mCallback;
    private AppConstant mAppConst;

    public ReviewAdapter(Context context, List<Object> reviewList,OnItemClickListener callback){
        mContext = context;
        mReviewList = reviewList;
        mCallback = callback;
        mAppConst = new AppConstant(mContext);
    }
    @Override
    public int getItemViewType(int position) {
        if(mReviewList.get(position) != null){
            return mReviewList.get(position) instanceof MyRatingsInfoModel ? VIEW_HEADER : VIEW_ITEM;
        }else {
            return VIEW_PROG;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View itemView;
        switch (viewType) {
            case VIEW_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_rating_block, parent, false);
                viewHolder = new ReviewHeaderHolder(itemView);
                break;
            case VIEW_ITEM:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.review_layout, parent, false);
                viewHolder = new ReviewViewHolder(itemView);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false);
                viewHolder = new ProgressBarHolder(itemView);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_HEADER:
                ReviewHeaderHolder headerHolder = (ReviewHeaderHolder) holder;
                headerHolder.ratingBlock.setVisibility(View.VISIBLE);
                headerHolder.avgRatingBlock.setVisibility(View.VISIBLE);
                MyRatingsInfoModel infoModel = (MyRatingsInfoModel) mReviewList.get(position);
                LayerDrawable stars1 = (LayerDrawable) headerHolder.avgRatingBar.getProgressDrawable();
                stars1.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext,R.color.dark_yellow),
                        PorterDuff.Mode.SRC_ATOP);
                stars1.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                        PorterDuff.Mode.SRC_ATOP);
                headerHolder.avgRatingBar.setRating(infoModel.getAvgRating());
                headerHolder.avgRatingBar.setIsIndicator(true);
                headerHolder.userbaseText.setVisibility(View.GONE);
                headerHolder.recommendedText.setText(
                        mContext.getResources().getString(R.string.recommended_by)
                + " "+infoModel.getRecommendedCount() + " "+
                                mContext.getResources().getString(R.string.user_text));
                if(infoModel.getMyRatingParams() != null && infoModel.getMyRatingParams().length() > 0) {
                    headerHolder.myRatingBlock.setVisibility(View.VISIBLE);
                    headerHolder.myRatingBar.setRating(infoModel.getMyRatingParams().
                            optJSONObject(0).optInt("rating"));
                    headerHolder.updateReviewText.setVisibility(View.GONE);
                    headerHolder.updateReviewText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //TODO Enable update review option
                        }
                    });
                }
                break;
            case VIEW_ITEM:
                ReviewViewHolder reviewHolder = (ReviewViewHolder) holder;
                mReviewInfo = (ReviewInfoModel) mReviewList.get(position);
                reviewHolder.reviewTitle.setText(mReviewInfo.getReviewTitle());
                reviewHolder.reviewOwner.setText(mReviewInfo.getReviewOwner());
                reviewHolder.reviewDate.setText(AppConstant.convertDateFormat(mContext.getResources(),
                        mReviewInfo.getReviewDate()));

                reviewHolder.reviewDesc.setText(mReviewInfo.getReviewDesc());
                LayerDrawable stars = (LayerDrawable) reviewHolder.reviewRating.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext,R.color.dark_yellow),
                        PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray),
                        PorterDuff.Mode.SRC_ATOP);
                reviewHolder.reviewRating.setRating(mReviewInfo.getReviewRating());
                reviewHolder.reviewRating.setIsIndicator(true);
                reviewHolder.likeCount.setText(mReviewInfo.getReviewLikes()+" ");
                reviewHolder.commentButton.setColorFilter(ContextCompat.getColor(mContext,R.color.dark_gray));
                reviewHolder.commentCount.setText(mReviewInfo.getReviewComments()+" ");
                SpannableString proc = new SpannableString(mContext.getResources().
                        getString(R.string.pros_text) + " - " + mReviewInfo.getReviewPros());
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                proc.setSpan(boldSpan, 0,
                        mContext.getResources().getString(R.string.pros_text).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                proc.setSpan(new ForegroundColorSpan(Color.BLACK),0,
                        mContext.getResources().getString(R.string.pros_text).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                reviewHolder.reviewPros.setText(proc);
                SpannableString cons = new SpannableString(mContext.getResources().getString(R.string.cons_text)
                        + " - " + mReviewInfo.getReviewCons());
                cons.setSpan(boldSpan, 0, mContext.getResources().getString(R.string.cons_text).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cons.setSpan(new ForegroundColorSpan(Color.BLACK),0,
                        mContext.getResources().getString(R.string.cons_text).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                reviewHolder.reviewCons.setText(cons);
                SpannableString recomnd = new SpannableString(mContext.getResources().getString(R.string.recommended_text)
                        + " - " + mReviewInfo.getUserRecommendation());
                recomnd.setSpan(boldSpan, 0,
                        mContext.getResources().getString(R.string.recommended_text).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                recomnd.setSpan(new ForegroundColorSpan(Color.BLACK),0,
                        mContext.getResources().getString(R.string.recommended_text).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                reviewHolder.reviewRecmd.setText(recomnd);
                reviewHolder.likeButton.setTag(reviewHolder);
                if(mReviewInfo.isReviewLiked()){
                    reviewHolder.likeCount.setTextColor(ContextCompat.getColor(mContext,R.color.themeButtonColor));
                    reviewHolder.likeButton.setColorFilter(ContextCompat.getColor(mContext,R.color.themeButtonColor));
                }else {
                    reviewHolder.likeCount.setTextColor(ContextCompat.getColor(mContext,R.color.dark_gray));
                    reviewHolder.likeButton.setColorFilter(ContextCompat.getColor(mContext,R.color.dark_gray));
                }
                if(!mAppConst.isLoggedOutUser()) {
                    reviewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            likeButtonAction(view);
                        }
                    });
                    reviewHolder.commentButton.setTag(reviewHolder);
                    reviewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final ReviewViewHolder holder = (ReviewViewHolder) view.getTag();
                            final ReviewInfoModel reviewInfoModel = (ReviewInfoModel) mReviewList.get(holder.getAdapterPosition());
                            String mCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=sitestorereview_review" +
                                    "&subject_id=" + reviewInfoModel.getReviewId() + "&viewAllComments=1&page=1&limit=" +
                                    AppConstant.LIMIT;

                            Intent commentIntent = new Intent(mContext, Comment.class);
                            commentIntent.putExtra("feedItemPosition", holder.getAdapterPosition());
                            commentIntent.putExtra("PhotoComment", false);
                            commentIntent.putExtra("LikeCommentUrl", mCommentsUrl);
                            commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, "sitestorereview_review");
                            commentIntent.putExtra(ConstantVariables.SUBJECT_ID, reviewInfoModel.getReviewId());
                            commentIntent.putExtra("action_id", reviewInfoModel.getReviewId() );
                            commentIntent.putExtra("commentCount", Integer.parseInt(reviewInfoModel.getReviewComments()));
                            mContext.startActivity(commentIntent);
                        }
                    });
                }
                if(mReviewInfo.getReviewOptions() != null && mReviewInfo.getReviewOptions().length() > 0) {
                    reviewHolder.reviewOptions.setVisibility(View.VISIBLE);
                    reviewHolder.reviewOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mCallback.onItemClick(view, holder.getAdapterPosition());
                        }
                    });
                }else {
                    reviewHolder.reviewOptions.setVisibility(View.GONE);
                }


                break;

            default:
                ((ProgressBarHolder) holder).progressBar.setIndeterminate(true);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    private class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView reviewTitle, reviewOwner,reviewDesc,reviewDate,reviewPros,reviewCons;
        TextView likeCount,commentCount,reviewRecmd;
        RatingBar reviewRating;
        ImageView reviewOptions,likeButton,commentButton;

        private ReviewViewHolder(View itemView) {
            super(itemView);
            reviewTitle = (TextView) itemView.findViewById(R.id.review_title);
            reviewOwner = (TextView) itemView.findViewById(R.id.review_owner);
            reviewDesc = (TextView) itemView.findViewById(R.id.review_description);
            reviewDate = (TextView) itemView.findViewById(R.id.review_date);
            reviewRating = (RatingBar) itemView.findViewById(R.id.smallRatingBar);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
            likeCount = (TextView) itemView.findViewById(R.id.like_count);
            reviewPros = (TextView) itemView.findViewById(R.id.review_pros);
            reviewCons = (TextView) itemView.findViewById(R.id.review_cons);
            reviewRecmd = (TextView) itemView.findViewById(R.id.review_recomn);
            reviewOptions = (ImageView) itemView.findViewById(R.id.review_options);
            likeButton = (ImageView) itemView.findViewById(R.id.like_button);
            commentButton = (ImageView) itemView.findViewById(R.id.comment_icon);
        }
    }


    private class ReviewHeaderHolder extends RecyclerView.ViewHolder{
        TextView userbaseText;
        TextView recommendedText,updateReviewText;
        RatingBar avgRatingBar,myRatingBar;
        View avgRatingBlock,myRatingBlock,ratingBlock;

        private ReviewHeaderHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.ratingInfo).setVisibility(View.VISIBLE);
            avgRatingBar = (RatingBar) itemView.findViewById(R.id.avgRatingBar);
            userbaseText = (TextView) itemView.findViewById(R.id.userbaseText);
            recommendedText = (TextView) itemView.findViewById(R.id.recommendedText);
            updateReviewText = (TextView) itemView.findViewById(R.id.updateReviewText);
            myRatingBar = (RatingBar) itemView.findViewById(R.id.myRatingBar);
            myRatingBlock = itemView.findViewById(R.id.myRatingBlock);
            avgRatingBlock = itemView.findViewById(R.id.avgRatingBlock);
            ratingBlock = itemView.findViewById(R.id.ratingBlock);
        }
    }


    String mLikeUnlikeUrl;
    public void likeButtonAction(View view){
        final ReviewViewHolder holder = (ReviewViewHolder) view.getTag();
        final ReviewInfoModel reviewInfoModel = (ReviewInfoModel) mReviewList.get(holder.getAdapterPosition());
        Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, "sitestorereview_review");
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(reviewInfoModel.getReviewId()));

        mLikeUnlikeUrl = AppConstant.DEFAULT_URL + (reviewInfoModel.isReviewLiked() ? "unlike":"like");

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                if(!reviewInfoModel.isReviewLiked()) {
                    reviewInfoModel.setReviewLiked(true);
                    reviewInfoModel.setReviewLikes(String.valueOf(Integer.parseInt(reviewInfoModel.getReviewLikes()) + 1));
                    holder.likeButton.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    holder.likeCount.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    notifyDataSetChanged();
                }else {
                    reviewInfoModel.setReviewLiked(false);
                    reviewInfoModel.setReviewLikes(String.valueOf(Integer.parseInt(reviewInfoModel.getReviewLikes()) - 1));
                    holder.likeButton.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray));
                    holder.likeCount.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray));
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(holder.likeButton, message);
            }

        });
    }
}
