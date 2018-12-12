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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;

import java.util.List;

public class StatusPostRecyclerViewAdapter extends RecyclerView.Adapter<StatusPostRecyclerViewAdapter.StatusPostRecyclerViewHolder> {

    //Member Variables.
    private Context mContext;
    private List<BrowseListItems> mBrowseList;
    private OnItemClickListener mOnClickListener;


    public StatusPostRecyclerViewAdapter(Context context, List<BrowseListItems> browseList,
                                         OnItemClickListener onClickListener) {
        mContext = context;
        mBrowseList = browseList;
        mOnClickListener = onClickListener;
    }

    @Override
    public StatusPostRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new StatusPostRecyclerViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(
                R.layout.status_items, parent, false));
    }

    @Override
    public void onBindViewHolder(final StatusPostRecyclerViewHolder viewHolder, int position) {

        BrowseListItems browseItem = mBrowseList.get(position);

        if (browseItem.getIcon() != null && !browseItem.getIcon().isEmpty()) {
            /*
                Check if unicode is coming then convert it in Integer and set the text to
                textview after converting it to string.
             */
            try {
                viewHolder.tvIcon.setText(new String(Character.toChars(Integer.parseInt(
                        browseItem.getIcon(), 16))));
            } catch (NumberFormatException e) {
                viewHolder.tvIcon.setText("\uF08B");
            }
        } else {
            viewHolder.tvIcon.setText("\uF08B");
        }
        viewHolder.tvIcon.setTextColor(browseItem.getColor());

        viewHolder.tvTitle.setText(browseItem.getTitle());

        if (browseItem.isAlreadyAdded()) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_done_24dp).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor),
                    PorterDuff.Mode.SRC_ATOP));
            viewHolder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            viewHolder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        // Set Onclick listener on each item of recycler view.
        viewHolder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(v, viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBrowseList.size();
    }


    public static class StatusPostRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView tvIcon, tvTitle;
        public View mContainer;

        public StatusPostRecyclerViewHolder(Context context, View view) {
            super(view);
            mContainer = view;
            tvIcon = (TextView) view.findViewById(R.id.icon);
            tvTitle = (TextView) view.findViewById(R.id.title);
            tvIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
        }
    }
}
