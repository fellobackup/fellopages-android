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

package com.fellopages.mobileapp.classes.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CircularImageView;
import com.fellopages.mobileapp.classes.common.ui.TopCropImageView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> data ;
    private LayoutInflater inflater;
    private Context mContext;
    private JSONObject mUserDetail;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private OnDrawerItemClickListener mOnDrawerItemClickListener;
    private ImageLoader mImageLoader;

    NavigationDrawerAdapter(Context context, List<Object> data,
                            OnDrawerItemClickListener onDrawerItemClickListener) {
        this.mContext = context;
        this.data = data;
        this.mOnDrawerItemClickListener = onDrawerItemClickListener;
        inflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(mContext);
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnDrawerItemClickListener {
        void onDrawerItemClick(View view, int position);
        void onUserLayoutClick(int userId);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) instanceof DrawerItem ? TYPE_ITEM : TYPE_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_ITEM) {
            viewHolder = new MyViewHolder(inflater.inflate(R.layout.custom_drawer_item, parent, false));
        } else {
            viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.layout_drawer_header, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if (getItemViewType(position) == TYPE_ITEM) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            DrawerItem current = (DrawerItem) data.get(position);


            if (current.getTitle() != null) {
                //Setting the Main Header
                holder.headerLayout.setVisibility(LinearLayout.VISIBLE);
                holder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
                holder.title.setText(current.getTitle());
                holder.container.setOnClickListener(null);
                holder.container.setBackgroundResource(0);

            } else {

                // Setting ripple effect only on menu items.
                TypedValue outValue = new TypedValue();
                mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                holder.container.setBackgroundResource(outValue.resourceId);

                //Setting the sub header
                holder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
                holder.itemLayout.setVisibility(LinearLayout.VISIBLE);

                holder.icon.setTextSize(TypedValue.COMPLEX_UNIT_PX, (mContext.getResources().getDimensionPixelSize(R.dimen.body_default_font_size) + 1));
                holder.icon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

                /*
                 Check if background color code is coming from server then parse it to color else
                 get color from menu name
                  */
                int backgroundColor;
                if (current.getmIconBackgroundColor() != null && !current.getmIconBackgroundColor().isEmpty()) {
                    backgroundColor = Color.parseColor(current.getmIconBackgroundColor());
                } else {
                    backgroundColor = ContextCompat.getColor(mContext,
                            GlobalFunctions.getIconBackgroundColor(current.getItemRegName()));
                }

                Drawable mDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_circle_bg);
                if (mDrawable != null) {
                    mDrawable.setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP));
                }
                holder.icon.setBackground(mDrawable);


                if (current.getmItemIcon() != null && !current.getmItemIcon().isEmpty()) {
                /*
                    Check if unicode is coming from server then convert it in Integer and set the text to
                    textview after converting it to string.
                 */
                    try {
                        holder.icon.setText(new String(Character.toChars(Integer.parseInt(
                                current.getmItemIcon(), 16))));
                    } catch (NumberFormatException e) {
                        holder.icon.setText("\uF08B");
                    }
                } else {

                    holder.icon.setText(GlobalFunctions.getItemIcon(current.getItemRegName()));
                }

                holder.count.setTextSize(TypedValue.COMPLEX_UNIT_PX, (mContext.getResources().getDimensionPixelSize(R.dimen.body_default_font_size) + 1));
                holder.ItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (mContext.getResources().getDimensionPixelSize(R.dimen.body_default_font_size) + 1));
                holder.ItemName.setText(current.getItemName());
                if(current.getBadgeCount() != null && !current.getBadgeCount().equals("0")
                        && !current.getBadgeCount().equals("") && !current.getBadgeCount().equals("null")) {
                    holder.count.setVisibility(View.VISIBLE);
                    holder.count.setText(current.getBadgeCount());
                }else {
                    holder.count.setVisibility(View.GONE);
                }
                holder.container.setOnClickListener(v -> mOnDrawerItemClickListener.onDrawerItemClick(v, position));

            }
        } else {
            Log.d("TestRefreshHere ", "test");
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            if (PreferencesUtils.getUserDetail(mContext) != null) {
                try {
                    mUserDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                    String userName = mUserDetail.optString("displayname");
                    String coverImageUrl = mUserDetail.optString("image");
                    String userCover = mUserDetail.optString("cover");
                    if ((coverImageUrl != null && !coverImageUrl.isEmpty()) && (userCover != null && !userCover.isEmpty())) {

                        // Blur Transform header image background image
                        mImageLoader.setImageWithBlur(userCover, headerViewHolder.ivUserImage);
                        mImageLoader.setImageForUserProfile(coverImageUrl, headerViewHolder.ivProfileImage);
                        headerViewHolder.ivProfileImage.setVisibility(View.VISIBLE);
                    }

                    headerViewHolder.tvUserName.setText(userName);
                    headerViewHolder.tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (mContext.getResources().getDimensionPixelSize(R.dimen.body_medium_font_size) + 1));

                    // Showing verification icon.
                    if (mUserDetail.optInt("showVerifyIcon") == 1) {
                        Drawable verifyDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_verification);
                        verifyDrawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp),
                                mContext.getResources().getDimensionPixelSize(R.dimen.margin_15dp));
                        headerViewHolder.tvUserName.setCompoundDrawables(null, null, verifyDrawable, null);
                    } else {
                        headerViewHolder.tvUserName.setCompoundDrawables(null, null, null, null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                headerViewHolder.ivUserImage.setOnClickListener(v -> mOnDrawerItemClickListener.onUserLayoutClick(mUserDetail.optInt("user_id")));
                headerViewHolder.ivProfileImage.setOnClickListener(v -> mOnDrawerItemClickListener.onUserLayoutClick(mUserDetail.optInt("user_id")));

            } else {
                headerViewHolder.ivProfileImage.setVisibility(View.VISIBLE);
            }
        }
    }


    private List<Object> deleteAnElement(List<Object> listData){

        listData.remove(5);

        return listData;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView ItemName, title;
        TextView icon,count;
        LinearLayout headerLayout, itemLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            title = itemView.findViewById(R.id.drawerTitle);
            headerLayout = itemView.findViewById(R.id.headerLayout);
            itemLayout = itemView
                    .findViewById(R.id.itemLayout);
            ItemName = itemView
                    .findViewById(R.id.drawer_itemName);
            icon = itemView.findViewById(R.id.drawer_icon);
            count = itemView.findViewById(R.id.material_drawer_badge);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TopCropImageView ivUserImage;
        RelativeLayout llDrawerHeader;
        CircularImageView ivProfileImage;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.user_name);
            ivUserImage = itemView.findViewById(R.id.cover_image);
            ivProfileImage = itemView.findViewById(R.id.user_profile_image);
            llDrawerHeader = itemView.findViewById(R.id.material_drawer_account_header_text_section);
        }
    }


    public void deletDuplicate(){
        int index = 0;
        for (int x = 0; x < data.size(); x++){
            DrawerItem drawerItem = (DrawerItem) data.get(x);
            if (drawerItem.getItemRegName().equals(ConstantVariables.SAVE_FEEDS)){
                index = x;
                break;
            }
        }
        Log.d("GetIndexHere ", String.valueOf(index));
        data.remove(index);
        notifyDataSetChanged();
    }
}
