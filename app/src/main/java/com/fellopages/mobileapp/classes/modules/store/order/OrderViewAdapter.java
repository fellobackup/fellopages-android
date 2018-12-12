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

package com.fellopages.mobileapp.classes.modules.store.order;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.ProgressBarHolder;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.store.utils.OrderInfoModel;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.util.List;

public class OrderViewAdapter extends RecyclerView.Adapter {
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    public Context mContext;
    public List<OrderInfoModel> mOrderList;
    public OrderInfoModel mOrderInfo;
    private OnItemClickListener mOnItemClickListener;
    private boolean isDownloadable;
    private ImageLoader imageLoader;

    public OrderViewAdapter(Context context, List<OrderInfoModel> orderList,
                            OnItemClickListener onItemClickListener, boolean isDownloadable){
        mContext = context;
        mOrderList = orderList;
        this.isDownloadable = isDownloadable;
        this.mOnItemClickListener = onItemClickListener;
        imageLoader = new ImageLoader(mContext);

    }
    @Override
    public int getItemViewType(int position) {
        return mOrderList.get(position) != null ? VIEW_ITEM :VIEW_PROG;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View itemView;
        switch (viewType) {
            case VIEW_ITEM:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.order_item_view, parent, false);
                viewHolder = new OrderViewHolder(itemView);

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
            case VIEW_ITEM:
                OrderViewHolder orderViewHolder = (OrderViewHolder) holder;
                mOrderInfo = mOrderList.get(position);
                if(isDownloadable){

                    orderViewHolder.itemTitle.setText(mOrderInfo.getFileTitle());
                    orderViewHolder.dateView.setText(mContext.getResources().getString(R.string.my_downloads)+
                            " : "+mOrderInfo.getTotalDownloads());
                    orderViewHolder.statusView.setText(mContext.getResources().getString(R.string.remaining_downloads)
                            +" : "+mOrderInfo.getRemainingDownloads());

                    orderViewHolder.orderAmountView.setText("#"+mOrderInfo.getOrderId());
                    if(URLUtil.isValidUrl(mOrderInfo.getFileOption())){
                        orderViewHolder.optionIcon.setVisibility(View.VISIBLE);
                        orderViewHolder.optionIcon.setImageDrawable(ContextCompat.getDrawable(mContext,
                                R.drawable.ic_file_download_24dp));
                        orderViewHolder.optionIcon.getLayoutParams().width =
                                mContext.getResources().getDimensionPixelSize(R.dimen.speaker_image_size);
                        orderViewHolder.optionIcon.getLayoutParams().height =
                                mContext.getResources().getDimensionPixelSize(R.dimen.speaker_image_size);

                        orderViewHolder.optionIcon.setPadding(
                                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                        orderViewHolder.fileStatus.setVisibility(View.GONE);
                    }else {
                        orderViewHolder.fileStatus.setVisibility(View.VISIBLE);
                        orderViewHolder.fileStatus.setText(mOrderInfo.getFileOption());
                        orderViewHolder.optionIcon.setVisibility(View.GONE);
                    }
                    orderViewHolder.optionIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickListener.onItemClick(view, holder.getAdapterPosition());
                        }
                    });
                    orderViewHolder.itemThumb.setVisibility(View.GONE);
                }else {
                    orderViewHolder.optionIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickListener.onItemClick(view, holder.getAdapterPosition());
                        }
                    });
                    orderViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickListener.onItemClick(view, holder.getAdapterPosition());
                        }
                    });
                    orderViewHolder.titleView.setText(mContext.getResources().getString(R.string.order_id_label)
                            +"#" + mOrderInfo.getOrderId());
                    orderViewHolder.dateView.setText(AppConstant.convertDateFormat(mContext.getResources(),mOrderInfo.getOrderDate()));
                    orderViewHolder.statusView.setText(mOrderInfo.getOrderStatus());

                    orderViewHolder.orderAmountView.setText(GlobalFunctions.getFormattedCurrencyString(
                            mOrderInfo.getDefaultCurrency(), mOrderInfo.getOrderAmount()));
                    JSONObject itemDetails = mOrderInfo.getItemDetails();
                    if(itemDetails != null){
                        String itemThumbUrl = itemDetails.optJSONObject("image").optString("image_normal");
                        imageLoader.setImageUrl(itemThumbUrl, orderViewHolder.itemThumb);
                        orderViewHolder.itemTitle.setText(itemDetails.optString("title"));
                    }

                }

                break;
            default:
                ((ProgressBarHolder) holder).progressBar.setIndeterminate(true);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        View mainView;
        TextView titleView,statusView,dateView,orderAmountView,fileStatus,itemTitle;
        ImageView optionIcon, itemThumb;

        public OrderViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            titleView = (TextView) itemView.findViewById(R.id.order_title);
            optionIcon = (ImageView) itemView.findViewById(R.id.optionIcon);
            statusView = (TextView) itemView.findViewById(R.id.order_status);
            dateView = (TextView) itemView.findViewById(R.id.order_date);
            orderAmountView =(TextView) itemView.findViewById(R.id.order_amount);
            fileStatus = (TextView) itemView.findViewById(R.id.file_status);
            itemThumb = (ImageView) itemView.findViewById(R.id.item_thumb);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
        }
    }
}
