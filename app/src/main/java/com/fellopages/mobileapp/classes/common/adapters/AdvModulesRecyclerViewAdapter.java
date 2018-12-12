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

package com.fellopages.mobileapp.classes.common.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ads.FacebookAdViewHolder;
import com.fellopages.mobileapp.classes.common.ads.admob.AdMobViewHolder;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvModulesRecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener,
        OnMenuClickResponseListener{

    public static final int HEADER_TYPE = 4;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;
    public static final int TYPE_FB_AD = 2;
    public static final int TYPE_ADMOB = 3;


    private List<Object> mBrowseItemList;
    private BrowseListItems mListItem, mHeaderListItems;
    private String currentSelectedOption;
    private String mViewType;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private OnItemDeleteResponseListener mOnItemDeleteListener;
    private String mSubjectType;
    private int mSubjectId, mListingTypeId, mPlaylistId;
    private Boolean mIsHelpful = false, mIsNotHelpful = false, likeUnlikeAction = true;
    private AppConstant mAppConst;
    private Map<String, String> postParams = new HashMap<>();
    private GutterMenuUtils mGutterMenuUtils;
    private Fragment mCallingFragment;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;


    public AdvModulesRecyclerViewAdapter(Context context, List<Object> listItem, String viewType,
                                         int listingTypeId, Fragment callingFragment,
                                         OnItemClickListener onItemClickListener){

        mOnItemClickListener = onItemClickListener;
        this.mContext = context;
        this.mBrowseItemList = listItem;
        this.mViewType = viewType;
        this.mListingTypeId = listingTypeId;
        this.mCallingFragment = callingFragment;
        mAppConst = new AppConstant(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);
        mImageLoader = new ImageLoader(mContext);

        //Fetch Current Selected Module
        currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
    }

    public AdvModulesRecyclerViewAdapter(Context context, List<Object> listItem, String viewType,
                                         int listingTypeId, Fragment callingFragment,
                                         OnItemDeleteResponseListener onItemDeleteListener){

        this.mContext = context;
        this.mBrowseItemList = listItem;
        this.mViewType = viewType;
        this.mListingTypeId = listingTypeId;
        this.mCallingFragment = callingFragment;
        this.mOnItemDeleteListener = onItemDeleteListener;
        mAppConst = new AppConstant(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);
        mImageLoader = new ImageLoader(mContext);

        //Fetch Current Selected Module
        currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
    }

    public AdvModulesRecyclerViewAdapter(Context context, List<Object> listItem, String viewType){

        this.mContext = context;
        this.mBrowseItemList = listItem;
        this.mViewType = viewType;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);

        //Fetch Current Selected Module
        currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(mContext);
    }

    public AdvModulesRecyclerViewAdapter(Context context, List<Object> listItem, String viewType,
                                         String currentSelectedOption,
                                         OnItemClickListener onItemClickListener,
                                         OnItemDeleteResponseListener onItemDeleteListener){

        this.mContext = context;
        this.mBrowseItemList = listItem;
        this.mViewType = viewType;
        mOnItemClickListener = onItemClickListener;
        this.mOnItemDeleteListener = onItemDeleteListener;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);

        //Fetch Current Selected Module
        this.currentSelectedOption = currentSelectedOption;
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
    }

    public AdvModulesRecyclerViewAdapter(Context context, List<Object> listItem, String viewType,
                                         String currentSelectedOption,
                                         OnItemClickListener onItemClickListener){

        this.mContext = context;
        this.mBrowseItemList = listItem;
        this.mViewType = viewType;
        mOnItemClickListener = onItemClickListener;
        mAppConst = new AppConstant(mContext);

        //Fetch Current Selected Module
        this.currentSelectedOption = currentSelectedOption;
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        if ((currentSelectedOption.equals(ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE)
                && position == 0 && mBrowseItemList.get(position) instanceof BrowseListItems)) {
            return HEADER_TYPE;
        } else if (mBrowseItemList.get(position) instanceof BrowseListItems) {
            return VIEW_ITEM;
        } else if (mBrowseItemList.get(position) != null) {
            if (ConstantVariables.ENABLE_ADMOB == 0) {
                return TYPE_FB_AD;
            } else {
                return TYPE_ADMOB;
            }
        } else {
            return VIEW_PROG;
        }
    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder =  null;
        switch (viewType) {
            case VIEW_ITEM:
                View itemView = null;
                switch (mViewType) {
                    case "category":
                        itemView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.category_grid_view, parent, false);
                        break;
                    case "user_review":
                        itemView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.activity_user_review, parent, false);
                        break;
                    case "wishlist":
                        itemView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.list_event_info, parent, false);
                        break;
                    case "invite":
                        itemView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.list_contacts, parent, false);
                        break;
                    case "videos":
                        itemView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.people_suggestion_list, parent, false);
                        break;

                }
                viewHolder = new ItemViewHolder(mContext, itemView, mViewType);
                break;
            case TYPE_FB_AD:
                switch (mViewType) {
                    case "category":
                        itemView = LayoutInflater.
                                from(parent.getContext()).
                                inflate(R.layout.fb_ad_item_card, parent, false);
                        viewHolder = new FacebookAdViewHolder(itemView);
                        break;
                    case "wishlist":
                        itemView = LayoutInflater.
                                from(parent.getContext()).
                                inflate(R.layout.feeds_ad_item_card, parent, false);
                        viewHolder = new FacebookAdViewHolder(itemView);
                        break;
                    case "user_review":
                    case "invite":
                    case "videos":
                        itemView = LayoutInflater.
                                from(parent.getContext()).
                                inflate(R.layout.fb_ad_item_list, parent, false);
                        viewHolder = new FacebookAdViewHolder(itemView);
                        break;
                }

                break;

            case TYPE_ADMOB:
                switch (mViewType) {
                    case "category":
                        itemView = LayoutInflater.
                                from(parent.getContext()).
                                inflate(R.layout.admob_install_card, parent, false);
                        viewHolder = new AdMobViewHolder(itemView);
                        break;
                    case "wishlist":
                        itemView = LayoutInflater.
                                from(parent.getContext()).
                                inflate(R.layout.admob_ad_install, parent, false);
                        viewHolder = new AdMobViewHolder(itemView);
                        break;
                    case "user_review":
                    case "invite":
                    case "videos":
                        itemView = LayoutInflater.
                                from(parent.getContext()).
                                inflate(R.layout.admob_install_list, parent, false);
                        viewHolder = new AdMobViewHolder(itemView);
                        break;
                }
                break;

            case HEADER_TYPE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.artist_view, parent, false);
                viewHolder = new HeaderViewHolder(mContext, itemView);
                break;

            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false);

                viewHolder = new ProgressViewHolder(view);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case VIEW_ITEM:
                mListItem = (BrowseListItems) mBrowseItemList.get(position);
                final ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);

                itemViewHolder.listItem = mListItem;
                itemViewHolder.mOptionsArray = mListItem.getMenuArray();
                itemViewHolder.tvContentTitle.setText(mListItem.getmBrowseListTitle());

                if (!mViewType.equals("invite") && !mViewType.equals("videos")) {
                    mGutterMenuUtils.setOnMenuClickResponseListener(this);
                }

                if (!mViewType.equals("user_review") && !mViewType.equals("invite")) {
                    itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.onItemClick(v, position);
                        }
                    });
                }

                switch (mViewType) {
                    case "category":
                        // Setting listing image
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) itemViewHolder.itemImage.getLayoutParams();
                        layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.options_icon_height);
                        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.options_icon_height);
                        layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.album_info_right_margin), 0, 0);
                        itemViewHolder.itemImage.setLayoutParams(layoutParams);
                        mImageLoader.setCategoryImage(mListItem.getmBrowseImgUrl(), itemViewHolder.itemImage);
                        break;
                    case "user_review":
                        switch (currentSelectedOption) {
                            case ConstantVariables.MLT_MENU_TITLE:
                                mSubjectType = "sitereview_review";
                                break;
                            case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                                mSubjectType = "siteevent_review";
                                break;
                            default:
                                mSubjectType = "sitestoreproduct_review";
                                break;
                        }

                        mSubjectId = mListItem.getmReviewId();

                        // showing review creation date and owner title.
                        String reviewDate = AppConstant.convertDateFormat(mContext.getResources(),
                                mListItem.getmBrowseListCreationDate());
                        itemViewHolder.tvReviewDate.setText(reviewDate + " - " +
                                mContext.getResources().getString(R.string.by_text) + " ");
                        itemViewHolder.tvUserName.setText(mListItem.getmBrowseListOwnerTitle());
                        itemViewHolder.tvUserName.setOnClickListener(this);
                        itemViewHolder.tvUserName.setTag(holder);

                        // Setting rating bar for individual review.
                        LayerDrawable stars = (LayerDrawable) itemViewHolder.mRatingBar.getProgressDrawable();
                        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                                PorterDuff.Mode.SRC_ATOP);
                        float rating = Float.parseFloat(String.valueOf(mListItem.getmReviewRating()));
                        itemViewHolder.mRatingBar.setRating(rating);
                        itemViewHolder.mRatingBar.setIsIndicator(true);

                        // showing recommend icon if it is one.
                        if (mListItem.getmRecommend() == 1) {
                            itemViewHolder.tvRecommended.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            itemViewHolder.tvRecommended.setText(mContext.getResources().
                                    getString(R.string.recommended_text) + " " + "\uf00c");
                        } else {
                            itemViewHolder.tvRecommended.setText(mContext.getResources().
                                    getString(R.string.recommended_text) + " ");
                        }

                        //Showing pros., cons., and summary.
                        if (mListItem.getmPros() != null) {
                            itemViewHolder.tvProsLabel.setText(mContext.getResources().getString(R.string.pros_text) + ":");
                            itemViewHolder.tvPros.setText(mListItem.getmPros());
                        }
                        if (mListItem.getmCons() != null) {
                            itemViewHolder.tvConsLabel.setText(mContext.getResources().getString(R.string.cons_text) + ":");
                            itemViewHolder.tvCons.setText(mListItem.getmCons());
                        }
                        if (mListItem.getmBrowseBody() != null && !mListItem.getmBrowseBody().isEmpty()) {
                            itemViewHolder.tvSummary.setVisibility(View.VISIBLE);
                            itemViewHolder.tvSummaryLabel.setVisibility(View.VISIBLE);
                            itemViewHolder.tvSummaryLabel.setText(mContext.getResources().getString(R.string.summary) + ":");
                            itemViewHolder.tvSummary.setText(Html.fromHtml(
                                    mListItem.getmBrowseBody()));
                            // When review updated then showing the latest review summary.
                            if (mListItem.getmUpdatedReviewArray() != null &&
                                    mListItem.getmUpdatedReviewArray().length() > 0) {
                                JSONObject updatedReviewObject = mListItem.getmUpdatedReviewArray().optJSONObject(0);
                                itemViewHolder.tvSummary.setText(
                                        Html.fromHtml(updatedReviewObject.optString("body")).toString().trim());
                            }
                        } else {
                            itemViewHolder.tvSummary.setVisibility(View.GONE);
                            itemViewHolder.tvSummaryLabel.setVisibility(View.GONE);
                        }

                        //Showing comment count.
                        String commentText = mContext.getResources().getQuantityString(R.plurals.profile_page_comment,
                                mListItem.getmCommentCount());
                        itemViewHolder.tvComment.setText(Html.fromHtml(String.format(
                                mContext.getResources().getString(R.string.comment_count_text),
                                mListItem.getmCommentCount(), commentText)));
                        itemViewHolder.tvComment.setOnClickListener(this);
                        itemViewHolder.tvComment.setTag(holder);

                        //Showing like and unlike option.
                        if (mListItem.getmIsHelpful()) {
                            itemViewHolder.tvLike.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        }
                        itemViewHolder.tvLike.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        itemViewHolder.tvLike.setText(String.format("%d  \uF164",
                                mListItem.getmHelpfulCount()));

                        if (mListItem.getmIsNotHelpful()) {
                            itemViewHolder.tvUnlike.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        }
                        itemViewHolder.tvUnlike.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        itemViewHolder.tvUnlike.setText(String.format("%d  \uF165",
                                mListItem.getmNotHelpfulCount()));

                        itemViewHolder.tvLike.setOnClickListener(this);
                        itemViewHolder.tvUnlike.setOnClickListener(this);
                        itemViewHolder.tvLike.setTag(holder);
                        itemViewHolder.tvUnlike.setTag(holder);
                        if (!mAppConst.isLoggedOutUser()) {
                            itemViewHolder.tvLike.setClickable(true);
                            itemViewHolder.tvUnlike.setClickable(true);
                        } else {
                            itemViewHolder.tvLike.setClickable(false);
                            itemViewHolder.tvUnlike.setClickable(false);
                        }

                        //Showing option icon
                        itemViewHolder.tvOptionsIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        itemViewHolder.tvOptionsIcon.setText("\uf141");
                        break;

                    case "wishlist":
                        // Showing listing images according to total listing count.
                        int totalListingItemCount = mListItem.getmTotalListingItem();
                        itemViewHolder.tvListingCount.setText(String.valueOf(totalListingItemCount));

                        // Showing images according to image count.
                        switch (totalListingItemCount) {
                            case 0:
                            case 1:
                                itemViewHolder.mContentImage.setVisibility(View.VISIBLE);
                                mImageLoader.setListingImageUrl(mListItem.getmListingImage1(),
                                        itemViewHolder.mContentImage);
                                break;

                            case 2:
                                itemViewHolder.mFirstListingImage.setVisibility(View.VISIBLE);
                                itemViewHolder.mSecondListingImage.setVisibility(View.VISIBLE);
                                itemViewHolder.mContentImage.setVisibility(View.GONE);
                                mImageLoader.setListingImageUrl(mListItem.getmListingImage1(),
                                        itemViewHolder.mFirstListingImage);
                                mImageLoader.setListingImageUrl(mListItem.getmListingImage2(),
                                        itemViewHolder.mSecondListingImage);
                                break;

                            default:
                                itemViewHolder.mFirstListingImage.setVisibility(View.VISIBLE);
                                itemViewHolder.mSecondListingImage.setVisibility(View.GONE);
                                itemViewHolder.mThirdListingImage.setVisibility(View.VISIBLE);
                                itemViewHolder.mFourthListingImage.setVisibility(View.VISIBLE);
                                itemViewHolder.mContentImage.setVisibility(View.GONE);
                                mImageLoader.setListingImageUrl(mListItem.getmListingImage1(),
                                        itemViewHolder.mFirstListingImage);
                                mImageLoader.setListingImageUrl(mListItem.getmListingImage2(),
                                        itemViewHolder.mThirdListingImage);
                                mImageLoader.setListingImageUrl(mListItem.getmListingImage3(),
                                        itemViewHolder.mFourthListingImage);
                                break;
                        }

                        //Showing option icon
                        itemViewHolder.tvOptionsIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        itemViewHolder.tvOptionsIcon.setText("\uf141");
                        break;

                    case "invite":
                        if (GlobalFunctions.isValidEmail(mListItem.getmEmail())) {
                            itemViewHolder.tvEmail.setText(mListItem.getmEmail());
                        } else if (mListItem.getmEmail().startsWith("mobile_")) {
                            itemViewHolder.tvEmail.setText(mListItem.getmEmail().substring(7));
                        }

                        if (mListItem.getmIsLoading()) {
                            itemViewHolder.tvInvite.setVisibility(View.GONE);
                            itemViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                        } else {
                            itemViewHolder.tvInvite.setVisibility(View.VISIBLE);
                            itemViewHolder.mProgressBar.setVisibility(View.GONE);
                            if (mListItem.getmIsRequestSent()) {
                                itemViewHolder.tvInvite.setText(mContext.getResources().
                                        getString(R.string.sentbox_tab).toUpperCase());
                            } else {
                                itemViewHolder.tvInvite.setText(mContext.getResources().
                                        getString(R.string.invite).toUpperCase());
                            }
                        }
                        itemViewHolder.tvInvite.setTag(position);
                        itemViewHolder.tvInvite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                performInvite(v, (int) v.getTag(), true);
                            }
                        });
                        itemViewHolder.tvInvite.setClickable(!mListItem.getmIsRequestSent());
                        break;

                    case "videos":
                        // Setting ripple effect only on menu items.
                        TypedValue outValue = new TypedValue();
                        mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                        itemViewHolder.container.setBackgroundResource(outValue.resourceId);
                        mImageLoader.setVideoImage(mListItem.getmBrowseImgUrl(), itemViewHolder.itemImage);
                        itemViewHolder.tvVideoDuration.setText(mAppConst
                                .calculateDifference(mListItem.getmDuration()));
                        itemViewHolder.tvSummary.setText(mContext.getResources().getString(R.string.by_text)
                                + " " + mListItem.getmBrowseListOwnerTitle());

                        if (mListItem.canRemoveFromPlayList()) {
                            itemViewHolder.tvRemove.setVisibility(View.VISIBLE);
                            itemViewHolder.tvRemove.setTag(holder.getAdapterPosition());
                            itemViewHolder.tvRemove.setOnClickListener(this);
                        } else {
                            itemViewHolder.tvRemove.setVisibility(View.GONE);
                        }
                        break;

                }

                if (((ItemViewHolder) holder).tvOptionsIcon != null) {
                    if (((ItemViewHolder) holder).mOptionsArray != null) {
                        itemViewHolder.tvOptionsIcon.setVisibility(View.VISIBLE);
                        //Setting on click listener on optionIcon
                        itemViewHolder.tvOptionsIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                itemViewHolder.tvOptionsIcon.setTag(holder);
                                ItemViewHolder holder = (ItemViewHolder) view.getTag();

                                mGutterMenuUtils.showPopup(view, holder.mOptionsArray,
                                        holder.getAdapterPosition(), mBrowseItemList, currentSelectedOption,
                                        mCallingFragment, mViewType, mListingTypeId, true);
                            }
                        });
                    } else {
                        itemViewHolder.tvOptionsIcon.setVisibility(View.GONE);
                    }
                }

                break;
            case TYPE_FB_AD:
                FacebookAdViewHolder adMob = (FacebookAdViewHolder) holder;
                if(adMob.mNativeAd !=null){
                    adMob.mNativeAd.unregisterView();
                }
                adMob.mNativeAd = (NativeAd) mBrowseItemList.get(position);

                FacebookAdViewHolder.inflateAd(adMob.mNativeAd, adMob.adView, mContext,false);
                break;
            case TYPE_ADMOB:
                AdMobViewHolder adMobViewHolder = (AdMobViewHolder) holder;

                AdMobViewHolder.inflateAd(mContext,
                        (NativeAppInstallAd) mBrowseItemList.get(position),adMobViewHolder.mAdView);
                break;

            case HEADER_TYPE:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                mHeaderListItems = (BrowseListItems) mBrowseItemList.get(position);
                if (mHeaderListItems != null) {
                    mPlaylistId = mHeaderListItems.getmContentId();
                    headerViewHolder.tvOwnerTitle.setText(mHeaderListItems.getmBrowseListOwnerTitle());
                    headerViewHolder.tvLikeCount.setText(String.format("\uf164 %d", mHeaderListItems.getmLikeCount()));
                    headerViewHolder.tvViewCount.setText(String.format("\uf04b %d", mHeaderListItems.getmViewCount()));
                    mImageLoader.setImageForUserProfile(mHeaderListItems.getmListImage(), headerViewHolder.ivOwnerImage);
                    headerViewHolder.llUserInfo.setTag(mHeaderListItems.getmUserId());
                    headerViewHolder.tvOwnerTitle.setTag(mHeaderListItems.getmUserId());
                    headerViewHolder.ivOwnerImage.setTag(headerViewHolder.ivOwnerImage.getId(), mHeaderListItems.getmUserId());
                    headerViewHolder.llUserInfo.setOnClickListener(this);
                    headerViewHolder.tvOwnerTitle.setOnClickListener(this);
                    headerViewHolder.ivOwnerImage.setOnClickListener(this);

                    // Checking if there is any video or not.
                    if (mBrowseItemList.size() > 1) {
                        headerViewHolder.llErrorMessage.setVisibility(View.GONE);
                    } else {
                        headerViewHolder.llErrorMessage.setVisibility(View.VISIBLE);
                        headerViewHolder.tvErrorIcon.setText("\uf03d");
                        headerViewHolder.tvErrorMessage.setText(mContext.getResources()
                                .getString(R.string.no_video_in_playlist));
                    }
                }
                break;

            default:
                ProgressViewHolder.inflateProgressBar(((ProgressViewHolder) holder).progressView);
                break;
        }

    }

    public void performInvite(final View view, final int position, final boolean isSingleInvite) {
        String emails = "";

        if (isSingleInvite) {
            mListItem = (BrowseListItems) mBrowseItemList.get(position);
            emails = mListItem.getmEmail();
        } else {
            mAppConst.showProgressDialog();
            if (mBrowseItemList != null && mBrowseItemList.size() > 0) {
                for (int i = 0; i < mBrowseItemList.size(); i++) {
                    final BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(i);
                    emails += listItems.getmEmail() + ",";
                }
            }
        }

        // Checking for which type of invite is to be sent. (For email or mobile number).
        if (GlobalFunctions.isValidEmail(emails)) {
            mListItem.setmIsLoading(true);
            notifyItemChanged(position);
            postParams.put("emails", emails);

            mAppConst.postJsonResponseForUrl(AppConstant.DEFAULT_URL + "suggestions/send-invite",
                    postParams, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) {
                            try {
                                if (isSingleInvite) {
                                    mListItem.setmIsLoading(false);
                                    mListItem.setmIsRequestSent(true);
                                    notifyItemChanged(position);
                                } else {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(view, mContext.getResources().getString(R.string.invitation_successful));
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            try {
                                if (isSingleInvite) {
                                    mListItem.setmIsLoading(false);
                                    mListItem.setmIsRequestSent(false);
                                    notifyItemChanged(position);
                                } else {
                                    SnackbarUtils.displaySnackbar(view, message);
                                    mAppConst.hideProgressDialog();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } else {

            String message = mContext.getResources().getString(R.string.spread_the_word_title) + " "
                    + mContext.getResources().getString(R.string.app_name) + " "
                    + mContext.getResources().getString(R.string.app_text) + ". "
                    + mContext.getResources().getString(R.string.download_app_from) + " : "
                    + AppConstant.DEFAULT_URL.replace("api/rest/", "") + "siteapi/index/app-page";

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.putExtra("address", emails.substring(7));
            intent.putExtra("sms_body", message);
            intent.setType("vnd.android-dir/mms-sms");
            mContext.startActivity(intent);
        }
    }

        @Override
    public void onItemDelete(int position) {

        try {

            BrowseListItems browseList = (BrowseListItems) mBrowseItemList.get(position);
            JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
            mBrowseItemList.remove(position);
            notifyDataSetChanged();
            if (mOnItemDeleteListener != null) {
                mOnItemDeleteListener.onItemDelete(getItemCount(),
                        (browseList.getmUserId() == userDetail.optInt("user_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {
        mBrowseItemList.set(position, itemList);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(final View v) {
        final View windowView = ((AppCompatActivity) mContext).getCurrentFocus();
        ItemViewHolder holder = null;
        TextView like = null, unlike = null;
        BrowseListItems listItems = null;
        if (v.getTag() instanceof ItemViewHolder) {
            holder = (ItemViewHolder) v.getTag();
            like = holder.tvLike;
            unlike = holder.tvUnlike;
            listItems = holder.listItem;
            mIsHelpful = listItems.getmIsHelpful();
            mIsNotHelpful = listItems.getmIsNotHelpful();
        }

        Intent intent;
        switch (v.getId()) {
            case R.id.userName:
                if (listItems.getmUserId() != 0) {
                    intent = new Intent(mContext, userProfile.class);
                    intent.putExtra(ConstantVariables.USER_ID, listItems.getmUserId());
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.comment:
                mSubjectId = listItems.getmReviewId();
                String mCommentsUrl = AppConstant.DEFAULT_URL + "likes-comments?subject_type=" + mSubjectType +
                            "&subject_id=" + mSubjectId + "&viewAllComments=1&page=1&limit=" + AppConstant.LIMIT;

                intent = new Intent(mContext, Comment.class);
                intent.putExtra(ConstantVariables.ITEM_POSITION, holder.getAdapterPosition());
                intent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mCommentsUrl);
                intent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
                intent.putExtra(ConstantVariables.SUBJECT_ID, mSubjectId);
                if (mCallingFragment != null) {
                    mCallingFragment.startActivityForResult(intent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                } else {
                    ((Activity)mContext).startActivityForResult(intent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
                }
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.like:

                if (likeUnlikeAction && !listItems.getmIsHelpful()) {
                    //showing unlike option with decreasing notHelpful count (if it is not 0) when like option is clicked.
                    if (listItems.getmNotHelpfulCount() != 0 && listItems.getmIsNotHelpful())
                        unlike.setText("\uf165" + " " + (listItems.getmNotHelpfulCount() - 1));
                    unlike.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));

                    //showing like option with increasing helpfulCount when it is like request.
                    like.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    like.setText("\uf164" + " " + (listItems.getmHelpfulCount() + 1));
                    doLikeUnlike(like, unlike, listItems, true);
                    likeUnlikeAction = false;
                } else if (listItems.getmIsHelpful() && windowView != null) {
                    SnackbarUtils.displaySnackbar(windowView,
                            mContext.getResources().getString(R.string.already_feedback_msg));
                }
                break;
            case R.id.unlike:
                if (likeUnlikeAction && !listItems.getmIsNotHelpful()) {
                    //showing like option with decreasing Helpful count (if it is not 0) when unlike option is clicked.
                    if (listItems.getmHelpfulCount() != 0 && listItems.getmIsHelpful())
                        like.setText("\uf164" + " " + (listItems.getmHelpfulCount() - 1));
                    like.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));

                    //showing unlike option with increasing NothelpfulCount when it is unlike request.
                    unlike.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    unlike.setText("\uf165" + " " + (listItems.getmNotHelpfulCount() + 1));
                    doLikeUnlike(unlike, like, listItems, false);
                    likeUnlikeAction = false;
                } else if (listItems.getmIsNotHelpful() && windowView != null) {
                    SnackbarUtils.displaySnackbar(windowView,
                            mContext.getResources().getString(R.string.already_feedback_msg));
                }
                break;

            case R.id.user_info_layout:
            case R.id.artist_author:
            case R.id.owner_image:
                int userId;
                if (v.getId() == R.id.owner_image) {
                    userId = (int) v.getTag(v.getId());
                } else {
                    userId = (int) v.getTag();
                }
                if (userId != 0) {
                    intent = new Intent(mContext, userProfile.class);
                    intent.putExtra(ConstantVariables.USER_ID, userId);
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.remove_icon:
                final int position = (int) v.getTag();
                BrowseListItems browseListItems = (BrowseListItems) mBrowseItemList.get(position);
                final String removeVideoUrl = AppConstant.DEFAULT_URL + "advancedvideo/playlist/remove-from-playlist/"
                        + mPlaylistId;
                postParams.put("playlist_id", String.valueOf(mPlaylistId));
                postParams.put("video_id", String.valueOf(browseListItems.getmListItemId()));
                mAlertDialogWithAction.showAlertDialogWithAction(mContext.getResources().getString(R.string.remove_video),
                        mContext.getResources().getString(R.string.video_remove_confirmation_msg),
                        mContext.getResources().getString(R.string.remove_video), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAppConst.showProgressDialog();
                                mAppConst.postJsonResponseForUrl(removeVideoUrl, postParams,
                                        new OnResponseListener() {
                                            @Override
                                            public void onTaskCompleted(JSONObject jsonObject) {
                                                mAppConst.hideProgressDialog();
                                                mBrowseItemList.remove(position);
                                                notifyDataSetChanged();
                                                if (mOnItemDeleteListener != null) {
                                                    mOnItemDeleteListener.onItemDelete(position,
                                                            mBrowseItemList.size() <= 1);
                                                }
                                                SnackbarUtils.displaySnackbar(v,
                                                        mContext.getResources().getString(R.string.video_remove_success_msg));
                                            }

                                            @Override
                                            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                                mAppConst.hideProgressDialog();
                                            }
                                        });
                            }
                        });
                break;
        }
    }

    /**
     * Method to do like or unlike according to request.
     * @param requestedView like or unlike text view according to request.
     * @param anotherView another text view. (i.e. like in case of unlike request)
     * @param listItems listItems which contains response.
     * @param isHelpfulRequest whether it is like request or unlike request.
     */
    public void doLikeUnlike(final TextView requestedView, final TextView anotherView, final BrowseListItems listItems,
                             final boolean isHelpfulRequest) {

        final View windowView = ((AppCompatActivity) mContext).getCurrentFocus();
        Map<String, String> likeUnlikeParams = new HashMap<>();
        likeUnlikeParams.put("review_id", String.valueOf(listItems.getmReviewId()));
        String mLikeUrl;
        switch (mSubjectType) {
            case "sitereview_review":
                mLikeUrl = AppConstant.DEFAULT_URL + "listings/review/helpful/" + listItems.getmListItemId();
                likeUnlikeParams.put("listing_id", String.valueOf(listItems.getmListItemId()));
                likeUnlikeParams.put("listingtype_id", String.valueOf(mListingTypeId));
                break;

            case "siteevent_review":
                mLikeUrl = AppConstant.DEFAULT_URL + "advancedevents/review/helpful/" + listItems.getmListItemId();
                break;

            default:
                mLikeUrl = AppConstant.DEFAULT_URL + "sitestore/product/review/helpful/"
                        + listItems.getmListItemId() + "/" + listItems.getmReviewId();
                break;
        }
        if (isHelpfulRequest) {
            likeUnlikeParams.put("helpful", "1");
        } else {
            likeUnlikeParams.put("helpful", "2");
        }

        mAppConst.postJsonResponseForUrl(mLikeUrl, likeUnlikeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                if (windowView != null) {
                    SnackbarUtils.displaySnackbar(windowView,
                            mContext.getResources().getString(R.string.review_submit_success_message));
                }
                requestedView.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                if (isHelpfulRequest) {
                    //Decreasing notHelpful count (if it is not 0) when like option is clicked.
                    if (listItems.getmNotHelpfulCount() != 0 && listItems.getmIsNotHelpful())
                        listItems.setmNotHelpfulCount(listItems.getmNotHelpfulCount() - 1);
                    listItems.setmIsNotHelpful(false);
                    anotherView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                    anotherView.setText("\uf165" + " " + listItems.getmNotHelpfulCount());

                    //increasing helpfulCount when it is like request.
                    listItems.setmHelpfulCount(listItems.getmHelpfulCount() + 1);
                    listItems.setmIsHelpful(true);
                    requestedView.setText("\uf164" + " " + listItems.getmHelpfulCount());
                } else {

                    //Decreasing Helpful count (if it is not 0) when unlike option is clicked.
                    if (listItems.getmHelpfulCount() != 0 && listItems.getmIsHelpful())
                        listItems.setmHelpfulCount(listItems.getmHelpfulCount() - 1);
                    listItems.setmIsHelpful(false);
                    anotherView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                    anotherView.setText("\uf164" + " " + listItems.getmHelpfulCount());

                    //increasing NotHelpfulCount when it is unlike request.
                    listItems.setmNotHelpfulCount(listItems.getmNotHelpfulCount() + 1);
                    listItems.setmIsNotHelpful(true);
                    requestedView.setText("\uf165" + " " + listItems.getmNotHelpfulCount());
                }
                likeUnlikeAction = true;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                likeUnlikeAction = true;
                notifyDataSetChanged();
                requestedView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                if (isHelpfulRequest) {
                    listItems.setmIsNotHelpful(mIsNotHelpful);
                } else {
                    listItems.setmIsHelpful(mIsHelpful);
                }
                // checking for the condition when the review is neither liked nor unLiked.
                if (mIsHelpful || mIsNotHelpful)
                    anotherView.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                if (windowView != null) {
                    SnackbarUtils.displaySnackbar(windowView, message);
                }
            }

        });
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage, mFirstListingImage, mSecondListingImage, mThirdListingImage, mFourthListingImage,
                mContentImage;
        public BrowseListItems listItem;
        public TextView tvContentTitle, tvReviewDate, tvUserName, tvRecommended, tvProsLabel, tvPros, tvConsLabel,
                tvCons, tvSummaryLabel, tvSummary, tvComment, tvHelpful, tvLike, tvUnlike, tvOptionsIcon,
                tvListingCount, tvEmail, tvInvite, tvRemove, tvVideoDuration;
        public RatingBar mRatingBar;
        public ProgressBar mProgressBar;
        public View container;
        JSONArray mOptionsArray;

        public ItemViewHolder(Context context, View view, String viewType) {

            super(view);
            container = view;

            switch (viewType) {
                case "category":
                    tvContentTitle = (TextView) view.findViewById(R.id.wishlistTitle);
                    itemImage = (ImageView) view.findViewById(R.id.viewImage);
                    tvContentTitle.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.gradient_view).setVisibility(View.GONE);
                    view.findViewById(R.id.itemTitle).setVisibility(View.GONE);
                    break;
                case "user_review":
                    tvContentTitle = (TextView) view.findViewById(R.id.title);
                    tvReviewDate = (TextView) view.findViewById(R.id.reviewDate);
                    tvUserName = (TextView) view.findViewById(R.id.userName);
                    mRatingBar = (RatingBar) view.findViewById(R.id.smallRatingBar);
                    tvRecommended = (TextView) view.findViewById(R.id.recommended);
                    tvProsLabel = (TextView) view.findViewById(R.id.prosLabel);
                    tvPros = (TextView) view.findViewById(R.id.pros);
                    tvConsLabel = (TextView) view.findViewById(R.id.consLabel);
                    tvCons = (TextView) view.findViewById(R.id.cons);
                    tvSummaryLabel = (TextView) view.findViewById(R.id.summaryLabel);
                    tvSummary = (TextView) view.findViewById(R.id.summary);
                    tvComment = (TextView) view.findViewById(R.id.comment);
                    tvHelpful = (TextView) view.findViewById(R.id.helpful);
                    tvLike = (TextView) view.findViewById(R.id.like);
                    tvUnlike = (TextView) view.findViewById(R.id.unlike);
                    tvOptionsIcon = (TextView) view.findViewById(R.id.optionsIcon);
                    break;
                case "wishlist":
                    view.findViewById(R.id.optionIcon).setVisibility(View.GONE);
                    view.findViewById(R.id.bottomView).setVisibility(View.GONE);
                    view.findViewById(R.id.date_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.wishlistImageBlock).setVisibility(View.VISIBLE);
                    tvContentTitle = (TextView) view.findViewById(R.id.wishlistTitle);
                    tvContentTitle.setVisibility(View.VISIBLE);
                    tvListingCount = (TextView) view.findViewById(R.id.listingCount);
                    tvListingCount.setVisibility(View.VISIBLE);
                    tvOptionsIcon = (TextView) view.findViewById(R.id.wishlistOptionsIcon);
                    mContentImage = (ImageView) view.findViewById(R.id.contentImage);
                    mContentImage.setVisibility(View.GONE);
                    mFirstListingImage = (ImageView) view.findViewById(R.id.listingImage1);
                    mSecondListingImage = (ImageView) view.findViewById(R.id.listingImage2);
                    mThirdListingImage = (ImageView) view.findViewById(R.id.listingImage3);
                    mFourthListingImage = (ImageView) view.findViewById(R.id.listingImage4);
                    break;

                case "invite":
                    tvContentTitle = (TextView) view.findViewById(R.id.contentTitle);
                    tvEmail = (TextView) view.findViewById(R.id.email_id);
                    tvInvite = (TextView) view.findViewById(R.id.send_invite);
                    mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                    break;

                case "videos":
                    view.findViewById(R.id.action_button_layout).setVisibility(View.GONE);
                    tvContentTitle = (TextView) view.findViewById(R.id.user_name);
                    tvSummary = (TextView) view.findViewById(R.id.mutual_friend_count);
                    tvVideoDuration = (TextView) view.findViewById(R.id.duration);
                    tvVideoDuration.setVisibility(View.VISIBLE);
                    tvRemove = (TextView) view.findViewById(R.id.remove_icon);
                    tvRemove.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
                    tvRemove.setText("\uf00d");
                    itemImage = (ImageView) view.findViewById(R.id.user_profile_image);
                    itemImage.setLayoutParams(CustomViews.getCustomWidthHeightRelativeLayoutParams(
                            context.getResources().getDimensionPixelSize(R.dimen.image_collage_view_height),
                            context.getResources().getDimensionPixelSize(R.dimen.attachment_small_image_size)));

                    break;
            }
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public TextView tvLikeCount, tvViewCount, tvErrorIcon;
        public SelectableTextView tvOwnerTitle, tvErrorMessage;
        public BezelImageView ivOwnerImage;
        public LinearLayout llErrorMessage, llUserInfo;


        public HeaderViewHolder(Context context, View view) {
            super(view);
            container = view;
            view.findViewById(R.id.artist_view_description).setVisibility(View.GONE);

            // User info views.
            tvOwnerTitle = (SelectableTextView) view.findViewById(R.id.artist_author);
            ivOwnerImage = (BezelImageView) view.findViewById(R.id.owner_image);
            llUserInfo = (LinearLayout) view.findViewById(R.id.user_info_layout);
            // Count conatiner views.
            tvLikeCount = ((TextView) view.findViewById(R.id.artist_view_track_number));
            tvViewCount = (TextView) view.findViewById(R.id.track_play_count);
            tvLikeCount.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
            tvViewCount.setTypeface(GlobalFunctions.getFontIconTypeFace(context));

            // Inflating Error view to show no video exist.
            View errorView = LayoutInflater.from(context).inflate(R.layout.error_view, null);
            LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.artist_view_main_layout);
            mainLayout.addView(errorView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) errorView.getLayoutParams();
            layoutParams.setMargins(0, context.getResources().getDimensionPixelSize(R.dimen.margin_50dp), 0, 0);
            errorView.setLayoutParams(layoutParams);
            // No data message views
            llErrorMessage = (LinearLayout) errorView.findViewById(R.id.message_layout);
            tvErrorIcon = (TextView) errorView.findViewById(R.id.error_icon);
            tvErrorMessage = (SelectableTextView) errorView.findViewById(R.id.error_message);
            tvErrorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        }
    }

}
