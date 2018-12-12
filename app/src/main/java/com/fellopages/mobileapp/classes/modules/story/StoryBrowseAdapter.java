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

package com.fellopages.mobileapp.classes.modules.story;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;
import com.fellopages.mobileapp.classes.modules.messages.CreateNewMessage;
import com.fellopages.mobileapp.classes.modules.store.adapters.SimpleSheetAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.SheetItemModel;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import java.util.ArrayList;
import java.util.List;


public class StoryBrowseAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    //Member Variables.
    private Context mContext;
    private List<Object> mBrowseList;
    private BrowseListItems mBrowseListItems;
    private AppConstant mAppConst;
    private FeedsFragment mFeedsFragment;
    private ImageLoader mImageLoader;


    public StoryBrowseAdapter(Context context, FeedsFragment feedsFragment, List<Object> browseList) {
        mContext = context;
        mFeedsFragment = feedsFragment;
        mBrowseList = browseList;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(
                R.layout.story_browse_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        mBrowseListItems = (BrowseListItems) this.mBrowseList.get(position);

        // Showing story's main image.
        mImageLoader.setImageForUserProfile(mBrowseListItems.getStoryImage(), itemViewHolder.ivStoryImage);

        // Set add story icon drawable color white
        Drawable addDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_add_black_24dp);
        addDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.white),
                PorterDuff.Mode.SRC_ATOP));
        itemViewHolder.ivStoryAdd.setImageDrawable(addDrawable);
        itemViewHolder.isMuteStory = mBrowseListItems.isMuteStory();

        // Showing the owner image if there is any story is published.
        if (mBrowseListItems.getStoryId() != 0) {
            if (itemViewHolder.isMuteStory == 1) {
                itemViewHolder.mContainer.setAlpha(.4f);
            } else {
                itemViewHolder.mContainer.setAlpha(1f);
            }

            itemViewHolder.ivStoryAdd.setVisibility(View.GONE);
            mImageLoader.setImageForUserProfile(mBrowseListItems.getOwnerImage(), itemViewHolder.ivUserProfile);

            if (mBrowseListItems.getmIsLoading()) {
                itemViewHolder.storyImageLayout.setBackgroundResource(R.color.white);
                itemViewHolder.ivUserProfile.setVisibility(View.GONE);
                showUploadingProgress(itemViewHolder);
            } else {
                itemViewHolder.mUploadingBar.setVisibility(View.GONE);
                itemViewHolder.mUploadingBar.clearAnimation();
                itemViewHolder.ivUserProfile.setVisibility(View.VISIBLE);
                itemViewHolder.storyImageLayout.setBackgroundResource(R.drawable.custom_circle_border);
            }

            if (position == 0) {
                itemViewHolder.ivStoryAdd.setVisibility(View.VISIBLE);
                itemViewHolder.ivUserProfile.setVisibility(View.GONE);
            } else {
                itemViewHolder.ivStoryAdd.setVisibility(View.GONE);
            }

        } else {
            itemViewHolder.ivUserProfile.setVisibility(View.GONE);
            itemViewHolder.storyImageLayout.setBackgroundResource(R.color.white);

            // Checking if no story is created then showing the add icon.
            if (mBrowseListItems.getmIsLoading()) {
                itemViewHolder.ivStoryAdd.setVisibility(View.GONE);
                showUploadingProgress(itemViewHolder);

            } else if (mBrowseListItems.getStoryOwnerId() == 0) {
                itemViewHolder.mUploadingBar.setVisibility(View.GONE);
                itemViewHolder.mUploadingBar.clearAnimation();
                itemViewHolder.ivStoryAdd.setVisibility(View.VISIBLE);

            } else {
                // If there are less story then showing the friends,
                // in this case adding color filter to make it different from stories.
                itemViewHolder.ivStoryImage.setColorFilter(Color.argb(211, 211, 211, 211));
                itemViewHolder.ivStoryAdd.setVisibility(View.GONE);
                itemViewHolder.mUploadingBar.setVisibility(View.GONE);
                itemViewHolder.mUploadingBar.clearAnimation();
            }
        }

        // Showing story owner title.
        itemViewHolder.tvUserName.setText(mBrowseListItems.getStoryOwner());

        itemViewHolder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BrowseListItems browseListItems = (BrowseListItems) mBrowseList.get(itemViewHolder.getAdapterPosition());
                // Checking if clicked on story.
                if (browseListItems.getStoryId() != 0) {
                    Intent intent = new Intent(mContext, StoryView.class);
                    if (itemViewHolder.getAdapterPosition() == 0) {
                        intent.putExtra("is_my_story", true);
                    }
                    StoryUtils.sCurrentStory = itemViewHolder.getAdapterPosition();
                    intent.putExtra(ConstantVariables.STORY_ID, browseListItems.getStoryId());
                    mFeedsFragment.startActivityForResult(intent, ConstantVariables.STORY_VIEW_PAGE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (browseListItems.getStoryOwnerId() == 0) {
                    // If add story item is clicked.
                    if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mFeedsFragment.checkManifestPermissions(null, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE, false, true);
                    } else {
                        mFeedsFragment.startStoryMediaPickerActivity();
                    }
                } else {
                    // If friends list item is clicked.
                    itemViewHolder.bottomSheetDialog.show();
                    itemViewHolder.tvInflatedViewTitle.setVisibility(View.VISIBLE);
                    itemViewHolder.inflatedView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
                    itemViewHolder.tvInflatedViewTitle.setText(browseListItems.getStoryOwner() + " "
                            + mContext.getResources().getString(R.string.story_not_added));
                    itemViewHolder.mSheetAdapter.setOnItemClickListener(new SimpleSheetAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(SheetItemModel value, int position) {
                            itemViewHolder.bottomSheetDialog.hide();
                            Intent intent;
                            if (position == 0) {
                                intent = new Intent(mContext, CreateNewMessage.class);
                                intent.putExtra(ConstantVariables.CONTENT_TITLE, browseListItems.getStoryOwner());
                                intent.putExtra("isSendMessageRequest", true);
                            } else {
                                intent = new Intent(mContext, userProfile.class);
                            }
                            intent.putExtra(ConstantVariables.USER_ID, browseListItems.getStoryOwnerId());
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                }
            }
        });


        // If add story icon is clicked.
        itemViewHolder.ivStoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mFeedsFragment.checkManifestPermissions(null, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE, false, true);
                } else {
                    mFeedsFragment.startStoryMediaPickerActivity();
                }
            }
        });

    }

    /**
     * Method to show uploading progress bar.
     *
     * @param itemViewHolder ItemViewHolder instance.
     */
    private void showUploadingProgress(final ItemViewHolder itemViewHolder) {
        itemViewHolder.mUploadingBar.setVisibility(View.VISIBLE);
        itemViewHolder.mUploadingBar.setProgress(0);
        itemViewHolder.ivStoryAdd.bringToFront();
        final ObjectAnimator anim = ObjectAnimator.ofInt(itemViewHolder.mUploadingBar, "progress", 0, 500);
        anim.setDuration(5000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                itemViewHolder.mUploadingBar.clearAnimation();
                itemViewHolder.mUploadingBar.setVisibility(View.GONE);
                anim.removeAllListeners();
                anim.cancel();
                notifyItemChanged(itemViewHolder.getAdapterPosition());
            }
        });
        anim.start();
    }

    @Override
    public int getItemCount() {
        return mBrowseList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivUserProfile, ivStoryImage, ivStoryAdd;
        public TextView tvUserName, tvInflatedViewTitle;
        public ProgressBar mUploadingBar;
        public View mContainer, inflatedView, storyImageLayout;
        public BottomSheetDialog bottomSheetDialog;
        public SimpleSheetAdapter mSheetAdapter;
        private int isMuteStory;

        public ItemViewHolder(Context context, View view) {
            super(view);
            mContainer = view;
            mUploadingBar = (ProgressBar) view.findViewById(R.id.progressBar);
            storyImageLayout = view.findViewById(R.id.story_image_layout);
            ivUserProfile = (ImageView) view.findViewById(R.id.owner_image);
            ivStoryImage = (ImageView) view.findViewById(R.id.story_image);
            ivStoryAdd = (ImageView) view.findViewById(R.id.add_story);
            tvUserName = (TextView) view.findViewById(R.id.owner_name);

            //Views for the friends view Bottom sheet dialog.
            List<SheetItemModel> list = new ArrayList<>();
            list.add(new SheetItemModel(context.getResources().getString(R.string.send_photo_video), "send", "f030"));
            list.add(new SheetItemModel(context.getResources().getString(R.string.view_profile_text), "view", "f007"));
            mSheetAdapter = new SimpleSheetAdapter(context, list, true);
            inflatedView = ((Activity) context).getLayoutInflater().inflate(R.layout.fragmen_cart, null);
            inflatedView.setBackgroundResource(R.color.white);
            tvInflatedViewTitle = (TextView) inflatedView.findViewById(R.id.header_title);
            RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
            inflatedView.findViewById(R.id.cart_bottom).setVisibility(View.GONE);
            recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(mSheetAdapter);
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(inflatedView);
        }
    }
}
