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

package com.fellopages.mobileapp.classes.modules.store.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.modules.store.utils.SheetItemModel;

import java.util.List;

public class SimpleSheetAdapter extends RecyclerView.Adapter<SimpleSheetAdapter.ItemHolder> {

    private Context mContext;
    private List<SheetItemModel> list;
    private OnItemClickListener onItemClickListener;
    private boolean mIsOptionsWithIcon, mIsFilterOptions;
    private String mDefaultValue;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(SheetItemModel value, int position);
    }

    public SimpleSheetAdapter(List<SheetItemModel> list) {
        this.list = list;
    }

    public SimpleSheetAdapter(Context context, List<SheetItemModel> list) {
        this.mContext = context;
        this.list = list;
    }

    public SimpleSheetAdapter(Context context, List<SheetItemModel> list, boolean isOptionWithIcon) {
        this.mContext = context;
        this.list = list;
        this.mIsOptionsWithIcon = isOptionWithIcon;
    }

    public SimpleSheetAdapter(Context context, List<SheetItemModel> list,
                              boolean isOptionWithIcon, boolean isFilter) {
        this.mContext = context;
        this.list = list;
        this.mIsOptionsWithIcon = isOptionWithIcon;
        this.mIsFilterOptions = isFilter;
    }

    public void setDefaultKey(String defaultValue) {
        mDefaultValue = defaultValue;
    }

    @Override
    public SimpleSheetAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_feeds, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final SimpleSheetAdapter.ItemHolder holder, int position) {
        final SheetItemModel item = list.get(position);
        if (item.getName() != null) {
            holder.textView.setText(item.getName());
        } else {
            if (item.getKeyObject() == null) {
                LogUtils.LOGD("Adapter", "Key Object Null");
            }
            holder.textView.setText(item.getKeyObject().optString("label"));
        }

        // Showing the selected option with check mark.
        if (mContext != null && mDefaultValue != null && !mDefaultValue.equals("0")
                && mDefaultValue.equals(item.getKey())) {
            holder.viewContainer.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_done_24dp).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor),
                    PorterDuff.Mode.SRC_ATOP));
            holder.textView.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_5dp), 0);
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        if (mIsOptionsWithIcon) {
            holder.tvIcon.setVisibility(View.VISIBLE);
            holder.tvIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.body_medium_font_size));
            if (mContext != null) {
                holder.tvIcon.setMinWidth(mContext.getResources().getDimensionPixelSize(R.dimen.margin_30dp));
            }
            if (mIsFilterOptions) {
                int color = ContextCompat.getColor(mContext, GlobalFunctions.getIconColor(position % 8));
                holder.tvIcon.setTextColor(color);
                if (item.getIcon() != null && !item.getIcon().isEmpty()) {
                    try {
                        holder.tvIcon.setText(new String(
                                Character.toChars(Integer.parseInt(item.getIcon(), 16))));
                    } catch (NumberFormatException e) {
                        holder.tvIcon.setText("\uF08B");
                    }
                } else {
                    holder.tvIcon.setText("\uF08B");
                }

            } else if (item.getIcon() != null && !item.getIcon().isEmpty()) {
                holder.tvIcon.setVisibility(View.VISIBLE);
                try {
                    holder.tvIcon.setText(new String(Character.toChars(Integer.parseInt(
                            item.getIcon(), 16))));
                } catch (NumberFormatException e) {
                    holder.tvIcon.setText("\uf08b");
                }
            } else {
                switch (item.getKey()) {
                    case "1":
                        holder.tvIcon.setText("\uf167");
                        break;

                    case "2":
                        holder.tvIcon.setText("\uf27d");
                        break;

                    case "3":
                        holder.tvIcon.setText("\uf10b");
                        break;

                    default:
                        holder.tvIcon.setText("\uf01d");
                        break;
                }
            }

        } else {
            holder.tvIcon.setVisibility(View.GONE);
        }
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final OnItemClickListener listener = holder.adapter.getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(item, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {

        private SimpleSheetAdapter adapter;
        TextView textView, tvIcon;
        View mainView, viewContainer;

        public ItemHolder(View itemView, SimpleSheetAdapter parent) {
            super(itemView);
            mainView = itemView;
            this.adapter = parent;
            textView = (TextView) itemView.findViewById(R.id.hiddenFeedOptions);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            tvIcon = (TextView) itemView.findViewById(R.id.icon);
            viewContainer = itemView.findViewById(R.id.view_container);

        }
    }
}
