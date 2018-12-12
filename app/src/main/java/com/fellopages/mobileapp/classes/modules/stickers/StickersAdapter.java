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

package com.fellopages.mobileapp.classes.modules.stickers;


import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;


/**
 * @author Ankush Sachdeva (sankush@yahoo.co.in)
 */
class StickersAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageViewList> mStickersList;
    private LayoutInflater inflater;
    private ImageLoader mImageLoader;


    public StickersAdapter(Context context, List<ImageViewList> stickersList){
        this.mContext = context;
        this.mStickersList = stickersList;
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getCount() {
        return mStickersList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStickersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View view = convertView;

        if (convertView == null){
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.stickers_search, null);
            holder = new ViewHolder();
            assert view != null;
            holder.stickersLayout = (LinearLayout) view.findViewById(R.id.stickersLayout);
            holder.imageView = (ImageView) view.findViewById(R.id.stickerImage);
            holder.stickersText = (TextView) view.findViewById(R.id.stickersText);
            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }

        ImageViewList stickersInfo = mStickersList.get(position);

        if(stickersInfo.getmStickerBackGroundColor() != null){
            GradientDrawable drawable = (GradientDrawable) holder.stickersLayout.getBackground();
            drawable.setColor(Color.parseColor(stickersInfo.getmStickerBackGroundColor()));
        }
        mImageLoader.setReactionImageUrl(stickersInfo.getmGridViewImageUrl(), holder.imageView);
        holder.stickersText.setText(stickersInfo.getmStickerTitle());

        return view;
    }

    static class ViewHolder {
        LinearLayout stickersLayout;
        ImageView imageView;
        TextView stickersText;
    }
}