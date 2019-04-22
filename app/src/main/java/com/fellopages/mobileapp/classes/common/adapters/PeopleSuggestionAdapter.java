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

package com.fellopages.mobileapp.classes.common.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.peopleSuggestion.FindFriends;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PeopleSuggestionAdapter extends RecyclerView.
        Adapter implements View.OnClickListener {

    //Member Variables.
    private Context mContext;
    private List<Object> mBrowseList;
    private BrowseListItems mBrowseListItems;
    private AppConstant mAppConst;
    private String mSuggestionViewType;
    private final int SUGGESTION_TYPE = 0, FOOTER_TYPE = 1;
    private OnItemDeleteListener mOnItemDeleteListener;
    private OnAddRequestSuccessListener mOnAddRequestSuccessListener;
    private ImageLoader mImageLoader;


    public PeopleSuggestionAdapter(Context context, List<Object> browseList, String suggestionView,
                                   OnItemDeleteListener onItemDeleteListener) {
        mContext = context;
        mBrowseList = browseList;
        mSuggestionViewType = suggestionView;
        mOnItemDeleteListener = onItemDeleteListener;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    public PeopleSuggestionAdapter(Context context, List<Object> browseList, String suggestionView,
                                   OnItemDeleteListener onItemDeleteListener,
                                   OnAddRequestSuccessListener onAddRequestSuccess) {
        mContext = context;
        mBrowseList = browseList;
        mSuggestionViewType = suggestionView;
        mOnItemDeleteListener = onItemDeleteListener;
        mOnAddRequestSuccessListener = onAddRequestSuccess;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    public interface OnItemDeleteListener {
        void onItemDelete();
    }

    public interface OnAddRequestSuccessListener {
        void onScrollPosition(int position);
    }

    /**
     * Return the View Type according to the position of the item
     */

    @Override
    public int getItemViewType(int position) {
        if (mBrowseList.get(position) instanceof BrowseListItems) {
            return SUGGESTION_TYPE;
        } else {
            // Footer to show See more option.
            return FOOTER_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == SUGGESTION_TYPE) {
            if (mSuggestionViewType.equals("feed_suggestion")) {
                return new PeopleSuggestionViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_suggestion_grid_list, parent, false), mSuggestionViewType);
            } else {
                return new PeopleSuggestionViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_suggestion_list, parent, false), mSuggestionViewType);
            }
        } else {
            if (mSuggestionViewType.equals("feed_suggestion")) {
                return new PeopleSuggestionViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.people_suggestion_grid_list, parent, false), mSuggestionViewType);
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        PeopleSuggestionViewHolder holder = null;
        int viewType = getItemViewType(position);
        switch (viewType) {

            case SUGGESTION_TYPE:
                holder = (PeopleSuggestionViewHolder) viewHolder;
                mBrowseListItems = (BrowseListItems) this.mBrowseList.get(position);

                String userProfileImage = mBrowseListItems.getmBrowseImgUrl();
                mImageLoader.setImageForUserProfile(userProfileImage, holder.ivUserProfile);

                // Showing member name.
                holder.tvUserName.setText(mBrowseListItems.getDisplayName());

                // Showing mutual friend count if it is greater than 0.
                if (mBrowseListItems.getmMutualFriendCount() > 0) {
                    holder.tvMutualFriendCount.setVisibility(View.VISIBLE);
                    holder.tvMutualFriendCount.setText(mContext.getResources().
                            getQuantityString(R.plurals.mutual_friend_text,
                                    mBrowseListItems.getmMutualFriendCount(),
                                    mBrowseListItems.getmMutualFriendCount()));
                } else {
                    holder.tvMutualFriendCount.setVisibility(View.GONE);
                }

                // Getting drawable for cancel and remove suggestion.
                Drawable cancelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_white_24dp);
                cancelDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.gray_text_color),
                        PorterDuff.Mode.SRC_ATOP));
                holder.tvCancelRequest.setCompoundDrawablesWithIntrinsicBounds(cancelDrawable, null, null, null);

                if (mSuggestionViewType.equals("list_view")) {
                    holder.tvCancelRequest.setText(mContext.getResources().
                            getString(R.string.cancel_dialogue_message).toUpperCase());
                } else {
                    holder.tvCancelRequest.setText(mContext.getResources().
                            getString(R.string.group_cancel_request_dialogue_button).toUpperCase());
                    holder.tvRemoveSuggestion.setCompoundDrawablesWithIntrinsicBounds(cancelDrawable, null, null, null);
                }

                holder.tvAddFriend.setText(mContext.getResources().getString(R.string.add_friend_title).toUpperCase());
                holder.tvRemoveSuggestion.setText(mContext.getResources().getString(R.string.remove_listing_dialogue_button).toUpperCase());

                // Showing progress bar when sending request of add/cancel request.
                if (mBrowseListItems.getmIsLoading()) {
                    holder.pbLoading.setVisibility(View.VISIBLE);
                    holder.tvAddFriend.setVisibility(View.GONE);
                    holder.tvRemoveSuggestion.setVisibility(View.GONE);
                    holder.tvCancelRequest.setVisibility(View.GONE);
                    hideShowLayout(holder, false, false);

                } else {
                    holder.pbLoading.setVisibility(View.GONE);
                    // Showing add/cancel request option according to response.
                    if (mBrowseListItems.getmIsRequestSent()) {
                        holder.tvAddFriend.setVisibility(View.GONE);
                        holder.tvRemoveSuggestion.setVisibility(View.GONE);
                        hideShowLayout(holder, false, true);
                        holder.tvCancelRequest.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvAddFriend.setVisibility(View.VISIBLE);
                        holder.tvRemoveSuggestion.setVisibility(View.VISIBLE);
                        hideShowLayout(holder, true, false);
                        holder.tvCancelRequest.setVisibility(View.GONE);
                    }
                }

                // Adding click listener in case of feed suggestions.
                if (mSuggestionViewType.equals("feed_suggestion")) {
                    holder.llAddFriend.setTag(position);
                    holder.llRemoveSuggestion.setTag(position);
                    holder.llCancelRequest.setTag(position);
                    holder.llAddFriend.setOnClickListener(this);
                    holder.llRemoveSuggestion.setOnClickListener(this);
                    holder.llCancelRequest.setOnClickListener(this);
                } else {
                    holder.tvAddFriend.setTag(position);
                    holder.tvRemoveSuggestion.setTag(position);
                    holder.tvAddFriend.setOnClickListener(this);
                    holder.tvRemoveSuggestion.setOnClickListener(this);
                    holder.tvCancelRequest.setTag(position);
                    holder.tvCancelRequest.setOnClickListener(this);
                }
                break;

            default:
                if (mSuggestionViewType.equals("feed_suggestion")) {
                    holder = (PeopleSuggestionViewHolder) viewHolder;
                    holder.mContainer.findViewById(R.id.user_details_layout).setVisibility(View.GONE);
                    holder.tvAddFriend.setVisibility(View.GONE);
                    holder.tvRemoveSuggestion.setVisibility(View.GONE);
                    holder.tvCancelRequest.setVisibility(View.GONE);
                    hideShowLayout(holder, false, false);

                    // Showing the find more friends text on the last item.
                    holder.mContainer.findViewById(R.id.find_more_friends).setVisibility(View.VISIBLE);

                    // Showing the people image at center.
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_supervisor_account_black_24dp);
                    drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.grey),
                            PorterDuff.Mode.SRC_ATOP));
                    holder.ivUserProfile.setImageDrawable(drawable);
                    holder.ivUserProfile.setScaleType(ImageView.ScaleType.CENTER);

                } else {
                    // Show Footer ProgressBar on Scrolling
                    // Show End Of Result Text if there are no more results.
                    ProgressViewHolder progressViewHolder = (ProgressViewHolder) viewHolder;
                    if (mBrowseList.get(position) == null) {
                        progressViewHolder.progressBar.setVisibility(View.VISIBLE);
                        progressViewHolder.progressBar.setIndeterminate(true);
                        progressViewHolder.mFooterText.setVisibility(View.GONE);
                    } else {
                        progressViewHolder.mFooterText.setVisibility(View.VISIBLE);
                        progressViewHolder.mFooterText.setText(mContext.getResources().
                                getString(R.string.end_of_results));
                        progressViewHolder.progressBar.setVisibility(View.GONE);
                    }
                }

                break;
        }

        // Set Onclick listener on each item of recycler view.
        if (holder != null) {
            final PeopleSuggestionViewHolder finalHolder = holder;
            finalHolder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GlobalFunctions.isNetworkAvailable(mContext)) {
                        if (getItemViewType(position) == SUGGESTION_TYPE) {
                            BrowseListItems browseListItems = (BrowseListItems) mBrowseList.get(finalHolder.getAdapterPosition());
                            Intent userProfileIntent = new Intent(mContext, userProfile.class);
                            userProfileIntent.putExtra("user_id", browseListItems.getmUserId());
                            ((Activity) mContext).startActivityForResult(userProfileIntent, ConstantVariables.
                                    USER_PROFILE_CODE);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            showAllSuggestionActivity();
                        }
                    } else {
                        showOfflineError(v);
                    }
                }
            });
        }
    }

    /***
     * Method to show and hide the add friend and remove suggestion linear layout in case of feed suggestions.
     * @param holder PeopleSuggestionViewHolder instance.
     * @param isMakeVisibleRequest True if need to make these layout visible else hide them.
     * @param isMakeCancelLayoutVisible True if need to show cancel request Layout.
     */
    private void hideShowLayout(PeopleSuggestionViewHolder holder, boolean isMakeVisibleRequest,
                                boolean isMakeCancelLayoutVisible) {
        if (mSuggestionViewType.equals("feed_suggestion")) {
            if (isMakeVisibleRequest) {
                holder.llAddFriend.setVisibility(View.VISIBLE);
                holder.llRemoveSuggestion.setVisibility(View.VISIBLE);
            } else {
                holder.llAddFriend.setVisibility(View.GONE);
                holder.llRemoveSuggestion.setVisibility(View.GONE);
            }

            // For hide/show cancel request layout.
            if (isMakeCancelLayoutVisible) {
                holder.llCancelRequest.setVisibility(View.VISIBLE);
            } else {
                holder.llCancelRequest.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBrowseList.size();
    }

    @Override
    public void onClick(View view) {
        final int position = (int) view.getTag();
        String url;

        // Sending request only when the network is available. Else show the error.
        if (GlobalFunctions.isNetworkAvailable(mContext)) {
            switch (view.getId()) {
                case R.id.add_friend:
                case R.id.add_friend_layout:
                    url = AppConstant.DEFAULT_URL + "user/add";
                    performAction(position, url, "add_friend");
                    break;

                case R.id.cancel_request:
                case R.id.cancel_request_layout:
                    url = AppConstant.DEFAULT_URL + "user/cancel";
                    performAction(position, url, "cancel_request");
                    break;

                case R.id.remove_suggestion:
                case R.id.remove_suggestion_layout:
                    url = AppConstant.DEFAULT_URL + "suggestions/remove";
                    performAction(position, url, "remove_suggestion");
                    break;
            }

        } else {
            showOfflineError(view);
        }
    }

    /**
     * Method to perform action on add/cancel/remove button.
     *
     * @param position Position of clicked item.
     * @param url      Url of action.
     * @param menuName Menu name for which action is to be performed.
     */
    public void performAction(final int position, String url, final String menuName) {

        final BrowseListItems browseListItems = (BrowseListItems) mBrowseList.get(position);
        Map<String, String> postParams = new HashMap<>();
        postParams.put("user_id", String.valueOf(browseListItems.getmUserId()));

        // Changing value in list item to Update UI accordingly before sending request to server.
        switch (menuName) {
            case "add_friend":
                if (mOnAddRequestSuccessListener != null) {
                    mOnAddRequestSuccessListener.onScrollPosition(position + 1);
                }
            case "cancel_request":
                browseListItems.setmIsLoading(!browseListItems.getmIsLoading());
                notifyItemChanged(position);
                break;

            case "remove_suggestion":
                mBrowseList.remove(browseListItems);
                notifyDataSetChanged();
                if (getItemCount() == 1 && mOnItemDeleteListener != null) {
                    mOnItemDeleteListener.onItemDelete();
                }
                break;
        }

        mAppConst.postJsonResponseForUrl(url, postParams, new OnResponseListener() {

            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                // Updating add/cancel friend request response.
                if (menuName.equals("add_friend") || menuName.equals("cancel_request")) {
                    browseListItems.setmIsLoading(!browseListItems.getmIsLoading());
                    browseListItems.setmIsRequestSent(!browseListItems.getmIsRequestSent());
                    notifyItemChanged(position);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                // When there is any error in sending/cancel friend request then hide the progress bar.
                if (menuName.equals("add_friend") || menuName.equals("cancel_request")) {
                    browseListItems.setmIsLoading(!browseListItems.getmIsLoading());
                    notifyItemChanged(position);
                }
            }
        });
    }

    /**
     * Method to launch Find Friends activity which contains all the tabs.
     */
    public void showAllSuggestionActivity() {
        Intent intent = new Intent(mContext, FindFriends.class);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Method to show offline error.
     *
     * @param view View on which snackbar is to be shown.
     */
    public void showOfflineError(View view) {
        try {
            SnackbarUtils.displaySnackbar(view, mContext.getResources().getString(R.string.network_connectivity_error));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static class PeopleSuggestionViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivUserProfile;
        public TextView tvUserName, tvMutualFriendCount, tvAddFriend, tvRemoveSuggestion,
                tvCancelRequest;
        public LinearLayout llAddFriend, llRemoveSuggestion, llCancelRequest;
        public ProgressBar pbLoading;
        public View mContainer;

        public PeopleSuggestionViewHolder(View view, String suggestionViewType) {
            super(view);
            mContainer = view;
            ivUserProfile = view.findViewById(R.id.user_profile_image);
            tvUserName = view.findViewById(R.id.user_name);
            tvMutualFriendCount = view.findViewById(R.id.mutual_friend_count);
            tvAddFriend = view.findViewById(R.id.add_friend);
            tvRemoveSuggestion = view.findViewById(R.id.remove_suggestion);
            tvCancelRequest = view.findViewById(R.id.cancel_request);
            pbLoading = view.findViewById(R.id.progressBar);
            if (suggestionViewType.equals("feed_suggestion")) {
                llAddFriend = view.findViewById(R.id.add_friend_layout);
                llRemoveSuggestion = view.findViewById(R.id.remove_suggestion_layout);
                llCancelRequest = view.findViewById(R.id.cancel_request_layout);
            }
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView mFooterText;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
            mFooterText = v.findViewById(R.id.footer_text);
        }
    }

}
