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

package com.fellopages.mobileapp.classes.modules.advancedActivityFeeds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CacheUtils;
import com.fellopages.mobileapp.classes.common.utils.BitMapCreatorUtil;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BannerPhotoRecyclerAdapter extends RecyclerView.Adapter<BannerPhotoRecyclerAdapter.ItemViewHolder> {

    // Member variables.
    private Context mContext;
    private List<BrowseListItems> mBrowseItemList;
    private ArrayList<Integer> mSelectedBanner = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;


    public BannerPhotoRecyclerAdapter(Context context, List<BrowseListItems> browseItemList,
                                      OnItemClickListener onItemClickListener) {

        this.mContext = context;
        mBrowseItemList = browseItemList;
        mOnItemClickListener = onItemClickListener;
        mSelectedBanner.clear();
        mSelectedBanner.add(0);
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.banner_photo_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        BrowseListItems browseListItems = mBrowseItemList.get(position);
        holder.setIsRecyclable(false);

        JSONObject bannerObject = browseListItems.getBannerObject();
        if (browseListItems.getmBrowseImgUrl() != null && !browseListItems.getmBrowseImgUrl().isEmpty()) {
            mImageLoader.setImageUrl(browseListItems.getmBrowseImgUrl(), holder.ivMainImage);
            new BitMapCreatorUtil(mContext, browseListItems.getmBrowseImgUrl(), null).execute();

        } else if (bannerObject.optString("background-color") != null) {
            Bitmap image;
            if (bannerObject.optString("background-color") != null
                    && CacheUtils.getInstance(mContext).getLru().get(bannerObject.optString("background-color")) != null) {
                image = CacheUtils.getInstance(mContext).getLru().get(bannerObject.optString("background-color"));
            } else {
                image = Bitmap.createBitmap(mContext.getResources().getDimensionPixelSize(R.dimen.margin_30dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.margin_30dp), Bitmap.Config.ARGB_8888);
                try {
                    image.eraseColor(Color.parseColor(bannerObject.optString("background-color")));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                CacheUtils.getInstance(mContext).getLru().put(bannerObject.optString("background-color"), image);
            }

            holder.ivMainImage.setImageBitmap(image);
        }

        // Showing selected border on banner photo when any banner is selected.
        if (!mSelectedBanner.isEmpty() && mSelectedBanner.get(0) == position) {
            holder.ivMainImage.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_white_border));
            GradientDrawable drawable = (GradientDrawable) holder.ivMainImage.getBackground();
            drawable.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                    ContextCompat.getColor(mContext, R.color.themeButtonColor));
        } else {
            holder.ivMainImage.setBackground(null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.ivMainImage.setClipToOutline(!mSelectedBanner.isEmpty() && mSelectedBanner.get(0) == position);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
                    mSelectedBanner.clear();
                    mSelectedBanner.add(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Method to update editing feed's banner in status body's background.
     * @param selectedBanner Selected banner's position.
     */
    public void setEditingBanner(int selectedBanner) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, selectedBanner);
            mSelectedBanner.clear();
            mSelectedBanner.add(selectedBanner);
            notifyDataSetChanged();
        }
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ImageView ivMainImage;

        public ItemViewHolder(View view) {
            super(view);
            container = view;

            ivMainImage = (ImageView) view.findViewById(R.id.main_image);
        }
    }

}
