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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUserLayoutClickListener;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_PROGRESS = 2;

    private Activity mActivity;
    private Bundle mBundle;
    private String mSubjectType;
    List<ImageViewList> mPhotoList;
    private int mImageWidth;
    ImageViewList mImageViewList;
    private OnItemClickListener mOnItemClickListener;
    private OnUserLayoutClickListener mOnUserLayoutClickListener;
    private OnCancelClickListener mOnCancelClickListener;
    private boolean mIsBitmapImage = false, mIsStatusImage = false, mIsPhotoAttachment,
            isCarouselImages = false, isStoryImage = false;
    ImageViewList mList;
    private ImageLoader mImageLoader;
    private int padding;


    /**
     * Initialize the data set of the Adapter.
     * @param activity Activity instance of the calling class.
     * @param list List object.
     * @param photoList Containing the data to populate views to be used by RecyclerView.
     * @param columnWidth Width of the each image.
     * @param bundle Bundle which contains the data.
     * @param subjectType Subject type of the images.
     * @param onItemClickListener ItemClickListener on each image.
     * @param onUserLayoutClickListener UserInfo click listener.
     */
    public CustomImageAdapter(Activity activity, ImageViewList list, List<ImageViewList> photoList,
                              int columnWidth, Bundle bundle, String subjectType,
                              OnItemClickListener onItemClickListener,
                              OnUserLayoutClickListener onUserLayoutClickListener) {

        mOnItemClickListener = onItemClickListener;
        mOnUserLayoutClickListener = onUserLayoutClickListener;
        mActivity = activity;
        mPhotoList = photoList;
        mImageWidth = columnWidth;
        mBundle = bundle;
        mSubjectType = subjectType;
        mList = list;
        mImageLoader = new ImageLoader(mActivity);
    }

    /**
     * Initialize the data set of the Adapter.
     * @param activity Activity instance of the calling class.
     * @param list List object.
     * @param photoList Containing the data to populate views to be used by RecyclerView.
     * @param columnWidth Width of the each image.
     * @param onItemClickListener ItemClickListener on each image.
     */
    public CustomImageAdapter(Activity activity, ImageViewList list, List<ImageViewList> photoList,
                              int columnWidth, OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
        mList = list;
        mActivity = activity;
        mPhotoList = photoList;
        mImageWidth = columnWidth;
        mBundle = new Bundle();
        mSubjectType = "";
        mImageLoader = new ImageLoader(mActivity);
    }

    /**
     * Initialize the data set of the Adapter.
     * @param activity Activity instance of the calling class.
     * @param photoList Containing the data to populate views to be used by RecyclerView.
     * @param columnWidth Width of the each image.
     * @param onItemClickListener ItemClickListener on each image.
     */
    public CustomImageAdapter(Activity activity, List<ImageViewList> photoList,
                              int columnWidth, OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
        mActivity = activity;
        mPhotoList = photoList;
        mImageWidth = columnWidth;
        mBundle = new Bundle();
        mSubjectType = "";
        isCarouselImages = true;
        mImageLoader = new ImageLoader(mActivity);
    }

    /**
     * Initialize the data set of the Adapter.
     * @param activity Activity instance of the calling class.
     * @param photoList Containing the data to populate views to be used by RecyclerView.
     * @param columnWidth Width of the each image.
     * @param onItemClickListener ItemClickListener on each image.
     */
    public CustomImageAdapter(Activity activity, List<ImageViewList> photoList,
                              int columnWidth, OnItemClickListener onItemClickListener, OnCancelClickListener onCancelClickListener) {

        mOnItemClickListener = onItemClickListener;
        mOnCancelClickListener = onCancelClickListener;
        mActivity = activity;
        mPhotoList = photoList;
        mImageWidth = columnWidth;
        mBundle = new Bundle();
        mSubjectType = "";
        isCarouselImages = false;
        mImageLoader = new ImageLoader(mActivity);
        this.isStoryImage = true;
        mIsBitmapImage = true;
        isCarouselImages = false;
        padding = (int) mActivity.getResources().getDimension(R.dimen.padding_2dp);
    }

    /**
     * Initialize the data set of the Adapter.
     * @param activity Activity instance of the calling class.
     * @param photoList Containing the data to populate views to be used by RecyclerView.
     * @param columnWidth Width of the each image.
     * @param onCancelClickListener CancelClickListener on each image to remove the item from the list.
     */
    public CustomImageAdapter(Activity activity, List<ImageViewList> photoList,
                              int columnWidth, OnCancelClickListener onCancelClickListener) {
        mList = new ImageViewList();
        mActivity = activity;
        mPhotoList = photoList;
        mImageWidth = columnWidth;
        mBundle = new Bundle();
        mSubjectType = "";
        mIsBitmapImage = true;
        mOnCancelClickListener = onCancelClickListener;
        mImageLoader = new ImageLoader(mActivity);
        isStoryImage = false;
    }

    /**
     * Initialize the data set of the Adapter.
     * @param activity Activity instance of the calling class.
     * @param photoList Containing the data to populate views to be used by RecyclerView.
     * @param isStatusImage True if its for status images.
     * @param columnWidth Width of the each image.
     * @param onCancelClickListener CancelClickListener on each image to remove the item from the list.
     */
    public CustomImageAdapter(Activity activity, List<ImageViewList> photoList, boolean isStatusImage,
                              boolean isPhotoAttachment, int columnWidth, OnCancelClickListener onCancelClickListener) {
        mList = new ImageViewList();
        mActivity = activity;
        mPhotoList = photoList;
        mImageWidth = columnWidth;
        mBundle = new Bundle();
        mSubjectType = "";
        mIsBitmapImage = true;
        mIsStatusImage = isStatusImage;
        mIsPhotoAttachment = isPhotoAttachment;
        mOnCancelClickListener = onCancelClickListener;
        mImageLoader = new ImageLoader(mActivity);
    }

    public void setColumnWidth(int columnWidth) {
        mImageWidth = columnWidth;
    }


    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        switch (viewType) {
            case TYPE_ITEM:
                //inflate your layout and pass it to view holder
                // Create a new view.
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.pager_photo_view, viewGroup, false);
                return new ViewHolder(v, mImageWidth);

            case TYPE_HEADER:
                //inflate your layout and pass it to view holder
                View headerView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_imageview_header, viewGroup, false);
                return new HeaderViewHolder(headerView);
            default:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.progress_item, viewGroup, false);

                return new ProgressViewHolder(view);
        }
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)


    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if (viewHolder instanceof ViewHolder) {
            final ViewHolder itemViewHolder = (ViewHolder) viewHolder;
            mImageViewList = getItem(position);
            itemViewHolder.holderImageList = mImageViewList;

            if (isCarouselImages) {
                itemViewHolder.container.findViewById(R.id.main_view).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (position == 0) {
                    if (mPhotoList.size() == 1) {
                        itemViewHolder.container.findViewById(R.id.main_view).setPadding(0, 0, 0, 0);
                    } else {
                        itemViewHolder.container.findViewById(R.id.main_view).setPadding(
                                mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0,
                                mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
                    }
                } else {
                    itemViewHolder.container.findViewById(R.id.main_view).setPadding(0, 0,
                            mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
                }
                mImageLoader.setAlbumPhoto(mImageViewList.getmGridViewImageUrl(), ((ViewHolder) viewHolder).imageView);

            } else if (isStoryImage) {
                itemViewHolder.container.findViewById(R.id.main_view).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemViewHolder.container.findViewById(R.id.main_view).setPadding(padding, padding, padding, padding);

                if (itemViewHolder.holderImageList.getmSelectedItemPos() == 1) {
                    ViewHolder.mSelectedPosition = position;
                    itemViewHolder.container.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.themeButtonColor));
                } else {
                    itemViewHolder.container.setBackgroundColor(Color.TRANSPARENT);
                }

                if (mImageViewList.getmGridPhotoUrl() != null) {
                    // Setting up the bitmap into image view.
                    itemViewHolder.imageView.setImageBitmap(mImageViewList.getmGridPhotoUrl());
                }

                itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {

                            if (ViewHolder.mSelectedPosition < getItemCount()) {
                                ImageViewList imageViewListOld = mPhotoList.get(ViewHolder.mSelectedPosition);
                                imageViewListOld.setmSelectedItemPos(0);
                            }

                            if (position < getItemCount()) {
                                ImageViewList imageViewList = mPhotoList.get(position);
                                imageViewList.setmSelectedItemPos(1);

                                mOnItemClickListener.onItemClick(v, position);
                            }

                            notifyDataSetChanged();

                        }
                    }
                });

                itemViewHolder.tvCancel.setTag(position);
                // Apply click listener on the cancel image view, to remove the item from the list.
                if (mOnCancelClickListener != null) {
                    Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_clear_grey);
                    drawable.setColorFilter(ContextCompat.getColor(mActivity, R.color.white), PorterDuff.Mode.SRC_ATOP);
                    drawable.setBounds(0, 0, mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.margin_20dp));
                    itemViewHolder.tvCancel.setCompoundDrawables(null, drawable, null, null);

                    itemViewHolder.tvCancel.setVisibility(View.VISIBLE);
                    itemViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnCancelClickListener.onCancelButtonClicked((int) ((ViewHolder) viewHolder).tvCancel.getTag());
                        }
                    });
                }
            }
            // This is for the attachments inside form creation.
            // When any image, music, video and file is picked from device the showing them into image view.
            else if (mIsBitmapImage && !mIsStatusImage) {
                itemViewHolder.container.findViewById(R.id.main_view).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemViewHolder.container.findViewById(R.id.main_view).setPadding(0, 0,
                        mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
                itemViewHolder.llDescriptionBlock.setVisibility(View.VISIBLE);

                // Checking if the image is in bitmap form or in drawable form.
                if (mImageViewList.getmGridPhotoUrl() != null) {

                    // Setting up the bitmap into image view.
                    itemViewHolder.imageView.setImageBitmap(mImageViewList.getmGridPhotoUrl());

                    // Assign layout params of the cancel image view, to show it on top right corner of the image.
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((ViewHolder)
                            viewHolder).llDescriptionBlock.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
                    layoutParams.addRule(RelativeLayout.END_OF, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.thumbnail);
                    layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.thumbnail);
                    layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.thumbnail);
                    itemViewHolder.llDescriptionBlock.setLayoutParams(layoutParams);
                } else {

                    // Setting up the drawable into image view.
                    itemViewHolder.imageView.setImageDrawable(mImageViewList.getDrawableIcon());
                    String description = mImageViewList.getAlbumDescription();

                    // Showing text view only for the description.
                    if (description != null && !description.isEmpty()) {
                        itemViewHolder.container.findViewById(R.id.main_view).setPadding(0, 0,
                                mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                                mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                        itemViewHolder.tvDescription.setVisibility(View.VISIBLE);

                        // When the attachment is video or file and contains the next line tag,
                        // Then increasing the max line limit to 2.
                        if (description.contains("</b><br/>")) {
                            itemViewHolder.tvDescription.setMaxLines(2);
                        }
                        itemViewHolder.tvDescription.setText(Html.fromHtml(description.trim()));
                    } else {
                        itemViewHolder.tvDescription.setVisibility(View.GONE);
                    }
                }

                itemViewHolder.ivCancelImage.setTag(position);
                // Apply click listener on the cancel image view, to remove the item from the list.
                if (mOnCancelClickListener != null) {
                    itemViewHolder.ivCancelImage.setVisibility(View.VISIBLE);
                    itemViewHolder.ivCancelImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnCancelClickListener.onCancelButtonClicked((int) ((ViewHolder) viewHolder).ivCancelImage.getTag());
                        }
                    });
                }
            } else if (mIsStatusImage && mIsBitmapImage) {
                itemViewHolder.container.findViewById(R.id.main_view).setPadding(0,
                        mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0,
                        mActivity.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                if (!mIsPhotoAttachment) {
                    itemViewHolder.container.findViewById(R.id.main_view).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_clear_grey);
                    drawable.setBounds(0, 0, mActivity.getResources().getDimensionPixelSize(R.dimen.margin_25dp),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.margin_25dp));
                    itemViewHolder.tvCancel.setCompoundDrawables(null, drawable, null, null);
                }

                // Checking if the image is in bitmap form or in drawable form.
                if (mImageViewList.getmGridPhotoUrl() != null) {
                    // Setting up the bitmap into image view.
                    itemViewHolder.imageView.setImageBitmap(mImageViewList.getmGridPhotoUrl());
                }

                itemViewHolder.tvCancel.setTag(position);
                // Apply click listener on the cancel image view, to remove the item from the list.
                if (mOnCancelClickListener != null) {
                    itemViewHolder.tvCancel.setVisibility(View.VISIBLE);
                    itemViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnCancelClickListener.onCancelButtonClicked((int) ((ViewHolder) viewHolder).tvCancel.getTag());
                        }
                    });
                }
            } else {
                mImageLoader.setAlbumPhoto(mImageViewList.getmGridViewImageUrl(), ((ViewHolder) viewHolder).imageView);
                if (mImageViewList.getmGridViewImageUrl() != null && !mImageViewList.getmGridViewImageUrl().isEmpty()
                        && mImageViewList.getmGridViewImageUrl().contains(".gif")) {
                    itemViewHolder.ivGifIcon.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.ivGifIcon.setVisibility(View.GONE);
                }

                mImageLoader.setAlbumPhoto(mImageViewList.getmGridViewImageUrl(), ((ViewHolder) viewHolder).imageView);
                itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, position - 1);
                        }
                    }
                });
            }
        } else if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            mImageLoader.setImageForUserProfile(mList.getOwnerImageUrl(), headerViewHolder.mOwnerImageView);

            headerViewHolder.mOwnerTitleView.setText(mList.getOwnerTitle());

            if (mList.getAlbumDescription() != null && mList.getAlbumDescription().length() > 0) {
                headerViewHolder.mViewDescription.setVisibility(View.VISIBLE);
                headerViewHolder.mViewDescription.setText(mList.getAlbumDescription());
            } else {
                headerViewHolder.mViewDescription.setVisibility(View.GONE);
            }
            headerViewHolder.mOwnerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnUserLayoutClickListener != null) {
                        mOnUserLayoutClickListener.onUserLayoutClick();
                    }
                }
            });
            headerViewHolder.mOwnerTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnUserLayoutClickListener != null) {
                        mOnUserLayoutClickListener.onUserLayoutClick();
                    }
                }
            });
        } else {
            ProgressViewHolder.inflateProgressBar(((ProgressViewHolder) viewHolder).progressView);
        }
    }

    // END_INCLUDE(recyclerViewOnBindViewHolder)

    @Override
    public int getItemViewType(int position) {

        // Hiding header for the subject type = sitereview_album and for the profile/cover update.
        if (position < 1 && mBundle == null && !mSubjectType.equals("sitereview_album")) {
            return TYPE_HEADER;
        } else if (getItem(position) == null) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mBundle == null && !mSubjectType.equals("sitereview_album")) {
            return mPhotoList.size() + 1;
        } else {
            return mPhotoList.size();
        }
    }

    private ImageViewList getItem(int position) {
        if (mBundle == null && !mSubjectType.equals("sitereview_album")) {
            return mPhotoList.get(position - 1);
        } else {
            return mPhotoList.get(position);
        }
    }
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView, ivCancelImage, ivGifIcon;
        private TextView tvDescription;
        public ImageViewList holderImageList;
        private RelativeLayout llDescriptionBlock;
        public TextView tvCancel;
        public View container;
        public static int mSelectedPosition;

        public ViewHolder(View v, int imageWidth) {
            super(v);

            container = v;
            imageView = v.findViewById(R.id.thumbnail);
            ivGifIcon = v.findViewById(R.id.gif_icon);
            tvCancel = v.findViewById(R.id.btn_image_remove);
            imageView.setLayoutParams(CustomViews.getCustomWidthHeightRelativeLayoutParams(imageWidth, imageWidth));
            ivCancelImage = v.findViewById(R.id.image_remove);
            tvDescription = v.findViewById(R.id.image_desc);
            llDescriptionBlock = v.findViewById(R.id.description_block);

        }

    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        BezelImageView mOwnerImageView;
        private TextView mOwnerTitleView;
        private SelectableTextView mViewDescription;
        public View mUserView;

        public HeaderViewHolder(View v) {
            super(v);
            mUserView = v;
            mOwnerImageView = v.findViewById(R.id.owner_image);
            mOwnerTitleView = v.findViewById(R.id.owner_title);
            mViewDescription = v.findViewById(R.id.view_description);

        }
    }

}
