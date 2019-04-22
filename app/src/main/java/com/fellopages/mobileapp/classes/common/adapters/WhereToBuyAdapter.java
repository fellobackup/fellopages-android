/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.common.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

public class WhereToBuyAdapter extends RecyclerView.Adapter {

    private List<BrowseListItems> mBrowseItemList;
    private Context mContext;
    private ImageLoader mImageLoader;


    public WhereToBuyAdapter(Context context, List<BrowseListItems> listItem) {

        this.mContext = context;
        this.mBrowseItemList = listItem;
        mImageLoader = new ImageLoader(mContext);
    }


    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.where_to_buy_list, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        itemViewHolder.listItem = mBrowseItemList.get(position);
        if(itemViewHolder.listItem.getmBrowseListTitle() != null && !itemViewHolder.listItem.
                getmBrowseListTitle().equals("null")){
            itemViewHolder.mListTitle.setText(itemViewHolder.listItem.getmBrowseListTitle());
            itemViewHolder.mListTitle.setVisibility(View.VISIBLE);
            itemViewHolder.mListImage.setVisibility(View.INVISIBLE);
        } else{
            mImageLoader.setSingleFeedImage(itemViewHolder.listItem.getmListImage(), itemViewHolder.mListImage);
            itemViewHolder.mListTitle.setVisibility(View.GONE);
            itemViewHolder.mListImage.setVisibility(View.VISIBLE);
        }
        itemViewHolder.mPrice.setText(itemViewHolder.listItem.getmPrice());
        if(itemViewHolder.listItem.getmMinPriceOption() != 0){
            itemViewHolder.mMinPriceImage.setVisibility(View.VISIBLE);
            mImageLoader.setGreyPlaceHolder(itemViewHolder.listItem.getmPriceTagImage(), itemViewHolder.mMinPriceImage);
            if(itemViewHolder.listItem.getmBrowseListTitle() != null && !itemViewHolder.listItem.
                    getmBrowseListTitle().equals("null")){
                RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) itemViewHolder.
                        mMinPriceImage.getLayoutParams();
                relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.listTitle);
                relativeLayoutParams.addRule(RelativeLayout.END_OF, R.id.listTitle);
                relativeLayoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.listTitle);
                itemViewHolder.mMinPriceImage.setLayoutParams(relativeLayoutParams);

            } else{
                RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) itemViewHolder.
                        mMinPriceImage.getLayoutParams();
                relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.listImage);
                relativeLayoutParams.addRule(RelativeLayout.END_OF, R.id.listImage);
                relativeLayoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.listImage);
                itemViewHolder.mMinPriceImage.setLayoutParams(relativeLayoutParams);
            }
        } else{
            itemViewHolder.mMinPriceImage.setVisibility(View.GONE);
        }
        itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemViewHolder.listItem.getmListUrl() != null && !itemViewHolder.listItem.getmListUrl().isEmpty()){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemViewHolder.listItem.getmListUrl()));
                    mContext.startActivity(browserIntent);
                }
            }
        });
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private BrowseListItems listItem;
        private TextView mListTitle, mPrice;
        private View container;
        private ImageView mListImage, mMinPriceImage;

        ItemViewHolder(View view) {
            super(view);
            container = view;
            mListTitle = view.findViewById(R.id.listTitle);
            mPrice = view.findViewById(R.id.listPrice);
            mListImage = view.findViewById(R.id.listImage);
            mMinPriceImage = view.findViewById(R.id.minPriceImage);
        }
    }

}
