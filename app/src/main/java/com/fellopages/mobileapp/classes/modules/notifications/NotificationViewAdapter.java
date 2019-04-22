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

package com.fellopages.mobileapp.classes.modules.notifications;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.friendrequests.FriendRequestViewAdapter;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class NotificationViewAdapter extends RecyclerView.Adapter{
    private final int VIEW_REQUEST = 2;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROGRESSBAR = 0;
    private List<Object> mBrowseItemList;
    private BrowseListItems mListItem;
    private String mSubjectLabel, mSubjectSearch, mObjectLabel, mObjectSearch, mSearch, mLabel;
    private int subjectStartIndex, objectStartIndex, subjectEndIndex, objectEndIndex;
    private boolean isRequestPage, isStickerStore = false;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private AppConstant mAppConst;
    private ImageLoader mImageLoader;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onProfilePictureClicked(View view,int position);
        void onOptionSelected(View v, BrowseListItems listItems, int position);
        void onAcceptButtonClick(View view, int position);
        void onIgnoreButtonClick(View view,int position);
    }

    public NotificationViewAdapter(Context context,List<Object> listItem,boolean isRequestPage,
                                   OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
        this.mContext=context;
        this.mBrowseItemList=listItem;
        this.isRequestPage = isRequestPage;
        mImageLoader = new ImageLoader(mContext);
    }

    public NotificationViewAdapter(Context context,List<Object> listItem, boolean isRequestPage, boolean isStickerStore,
                                   OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
        this.mContext=context;
        this.mBrowseItemList=listItem;
        this.isRequestPage = isRequestPage;
        this.isStickerStore = isStickerStore;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return mBrowseItemList.get(position) instanceof String ? VIEW_PROGRESSBAR : VIEW_ITEM;
//        if (mBrowseItemList.get(position) instanceof String){
//            return VIEW_PROGRESSBAR;
//        } else if(mBrowseItemList.get(position) instanceof BrowseListItems){
//            BrowseListItems mListItem = (BrowseListItems) mBrowseItemList.get(position);
//            if (mListItem.viewType == 2){
//                return VIEW_REQUEST;
//            } else {
//                return VIEW_ITEM;
//            }
//        }
//        return VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_notification, parent, false);
            viewHolder = new NotificationViewHolder(view);

        } else if (viewType == VIEW_REQUEST){
            Log.d("ThisIsExecuted ", "true");
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_friend_requests, parent, false);
            viewHolder = new FRequestViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);

            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NotificationViewHolder) {
            mListItem = (BrowseListItems) mBrowseItemList.get(position);
            ((NotificationViewHolder)holder).listItem = mListItem;
            String userImageUrl;

            if(isStickerStore){
                userImageUrl = mListItem.getmBrowseImgUrl();
                ((NotificationViewHolder) holder).mNotificationTitle.setText(mListItem.getmBrowseListTitle());
                ((NotificationViewHolder) holder).mNotificationTitle.setTypeface(null, Typeface.BOLD);
                ((NotificationViewHolder) holder).mNotificationTitle.setTextColor(ContextCompat.getColor(mContext,
                        R.color.black));

                ((NotificationViewHolder) holder).mDateView.setText(mContext.getResources().
                        getQuantityString(R.plurals.stickers_count, mListItem.getmStickersCount(),
                                mListItem.getmStickersCount()));
                ((NotificationViewHolder) holder).mDateView.setVisibility(View.VISIBLE);
                ((NotificationViewHolder) holder).mOptionIcon.setVisibility(View.GONE);

                if(mListItem.getMenuArray() != null ){
                    ((NotificationViewHolder) holder).mStickerOption.setVisibility(View.VISIBLE);
                    ((NotificationViewHolder) holder).mStickerOption.setTag(position);
                    ((NotificationViewHolder) holder).menuName = mListItem.getMenuArray().optJSONObject(1).optString("name");
                    if(((NotificationViewHolder) holder).menuName.equals("add")){
                        if(!mListItem.isAlreadyAdded()){
                            mListItem.setAlreadyAdded(false);
                        }
                        Drawable drawable = ContextCompat.getDrawable
                                (mContext, R.drawable.ic_add_circle_black_24dp);
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.themeButtonColor));
                        ((NotificationViewHolder) holder).mStickerOption.setBackground(drawable);
                    } else{
                        mListItem.setAlreadyAdded(true);
                        Drawable drawable = ContextCompat.getDrawable
                                (mContext, R.drawable.ic_remove_circle_black_24dp);
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.red));
                        ((NotificationViewHolder) holder).mStickerOption.setBackground(drawable);
                    }
                    ((NotificationViewHolder) holder).mStickerOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int itemPosition = (int) v.getTag();
                            addRemoveStickerStore(itemPosition);
                        }
                    });
                } else {
                    ((NotificationViewHolder) holder).mStickerOption.setVisibility(View.GONE);
                }
            } else{
                userImageUrl = mListItem.getSubjectResponse().optString("image_profile");
                String notificationBody = Html.fromHtml(mListItem.getActionTypeBody()).toString();
                SpannableString notificationBodySpannable;

                if(mListItem.getActionBodyParamsArray() != null) {
                    for (int i = 0; i < mListItem.getActionBodyParamsArray().length(); i++) {
                        JSONObject paramArray = mListItem.getActionBodyParamsArray().optJSONObject(i);
                        String subject = "$subject";
                        String object = "$object";
                        if (paramArray.optString("search").contains(subject)) {
                            mSubjectSearch = paramArray.optString("search");
                            mSubjectLabel = String.valueOf(Html.fromHtml(paramArray.optString("label")));
                            mSubjectLabel = StringUtils.capitalize(mSubjectLabel);
                            notificationBody = notificationBody.replace(mSubjectSearch, mSubjectLabel);
                        }
                        else if (paramArray.optString("search").contains(object)) {
                            mObjectSearch = paramArray.optString("search");
                            mObjectLabel = String.valueOf(Html.fromHtml(paramArray.optString("label")));
                            notificationBody = notificationBody.replace(mObjectSearch, mObjectLabel);

                        } else {
                            mSearch = paramArray.optString("search");
                            mLabel = String.valueOf(Html.fromHtml(paramArray.optString("label")));
                            notificationBody = notificationBody.replace(mSearch, mLabel);
                        }

                    }

                    ClickableSpan bodySpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            mOnItemClickListener.onItemClick(((NotificationViewHolder) holder).container, position);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                            ds.setUnderlineText(false);
                        }

                    };

                    notificationBody = StringUtils.capitalize(notificationBody);
                    notificationBodySpannable = new SpannableString(notificationBody);
                    notificationBodySpannable.setSpan(bodySpan, 0, notificationBody.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    /* Make Subject Clickable */
                    if (mSubjectLabel != null && mSubjectLabel.length() > 0) {
                        subjectStartIndex = notificationBody.indexOf(mSubjectLabel);
                        subjectEndIndex = subjectStartIndex + mSubjectLabel.length();
                        if (subjectStartIndex >= 0) {
                            notificationBodySpannable.setSpan(new StyleSpan(Typeface.BOLD), subjectStartIndex,
                                    subjectEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            notificationBodySpannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                                    subjectStartIndex, subjectEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    /* Make Objetc Clickable */
                    if (mObjectLabel != null && mObjectLabel.length() > 0) {
                        objectStartIndex = notificationBody.indexOf(mObjectLabel);
                        objectEndIndex = objectStartIndex + mObjectLabel.length();
                        if (objectStartIndex >= 0) {
                            notificationBodySpannable.setSpan(new StyleSpan(Typeface.BOLD), objectStartIndex,
                                    objectEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            notificationBodySpannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                                    objectStartIndex, objectEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                    }
                    ((NotificationViewHolder) holder).mNotificationTitle.setText(notificationBodySpannable);
                }else {
                    ((NotificationViewHolder) holder).mNotificationTitle.setText(Html.fromHtml(mListItem.getFeedTitle()));
                }

                String notificationIcon;
                switch (mListItem.getNotificationType()){
                    case "liked":
                        notificationIcon = "\uf164";
                        break;
                    case "liked_commented":
                    case "commented_commented":
                    case "commented":
                        notificationIcon = "\uf075";
                        break;
                    case "shared":
                        notificationIcon = "\uf064";
                        break;
                    case "friend_accepted":
                        notificationIcon = "\uf007";
                        break;
                    case "post_user":
                        notificationIcon = "\uf197";
                        break;
                    case "tagged":
                        notificationIcon = "\uf02b";
                        break;
                    default:
                        notificationIcon = "\uf024";
                }

                String convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                        mListItem.getNotificationObject().optString("date"));
                ((NotificationViewHolder) holder).mDateView.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                ((NotificationViewHolder) holder).mDateView.setText(String.format("%s  %s", notificationIcon, convertedDate));

                if(mListItem.getIsRead() == 0){
                    ((NotificationViewHolder) holder).container.setBackground(
                            ContextCompat.getDrawable(mContext, R.drawable.selectable_background_colored_light));
                    ((NotificationViewHolder) holder).container.getBackground().setAlpha(30);
                }else {
                    ((NotificationViewHolder) holder).container.setBackground(
                            ContextCompat.getDrawable(mContext, R.drawable.selectable_background_white));
                }

                ((NotificationViewHolder) holder).mOptionIcon.setTag(holder);
                if(isRequestPage){
                    ((NotificationViewHolder) holder).mOptionIcon.setVisibility(View.VISIBLE);
                    ((NotificationViewHolder) holder).mOptionIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NotificationViewHolder holder = (NotificationViewHolder)v.getTag();
                            mOnItemClickListener.onOptionSelected(v,holder.listItem, position);
                        }
                    });
                }else {
                    ((NotificationViewHolder) holder).mOptionIcon.setVisibility(View.GONE);
                }
            }

            mImageLoader.setImageForUserProfile(userImageUrl, ((NotificationViewHolder) holder).userImage);

            ((NotificationViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            ((NotificationViewHolder) holder).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnItemClickListener.onProfilePictureClicked(v, position);
                }
            });

        } else if (holder instanceof FRequestViewHolder){
            Log.d("ThisIsInitialized ", "true");
            mListItem = (BrowseListItems) mBrowseItemList.get(position);
            ((FRequestViewHolder) holder).listItem = mListItem;

            mImageLoader.setImageForUserProfile(mListItem.getmBrowseImgUrl(), ((FRequestViewHolder) holder).userImage);
            ((FRequestViewHolder) holder).mOwnerName.setText(mListItem.getmBrowseListOwnerTitle());

            ((FRequestViewHolder) holder).acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onAcceptButtonClick(v, position);
                }
            });
            ((FRequestViewHolder) holder).ignoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onIgnoreButtonClick(v, position);
                }
            });

            ((FRequestViewHolder) holder).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onProfilePictureClicked(v, position);
                }
            });

            ((FRequestViewHolder) holder).mOwnerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onProfilePictureClicked(v, position);
                }
            });
        } else {
            ProgressViewHolder.inflateFooterView(mContext, ((ProgressViewHolder) holder).progressView,
                    mBrowseItemList.get(position), ConstantVariables.ACTION_VIEW_ALL_NOTIFICATIONS);
        }
    }

    @Override
    public int getItemCount() {
        return mBrowseItemList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImage, mOptionIcon, mStickerOption;
        public BrowseListItems listItem;
        public TextView mNotificationTitle,mDateView;
        public View container;
        public String menuName;

        public NotificationViewHolder(View view) {
            super(view);
            view.setClickable(true);
            container = view;
            userImage = view.findViewById(R.id.userImage);
            mOptionIcon = view.findViewById(R.id.optionIcon);
            mNotificationTitle = view.findViewById(R.id.notificationTitle);
            mDateView = view.findViewById(R.id.dateIconView);
            mStickerOption = view.findViewById(R.id.stickerOption);
        }
    }

    private void addRemoveStickerStore(int itemPosition){

        String mStickerOptionUrl;
        final BrowseListItems listItem = (BrowseListItems) mBrowseItemList.get(itemPosition);

        final JSONObject menuObject = listItem.getMenuArray().optJSONObject(1);
        final String menuName = menuObject.optString("name");

        LogUtils.LOGD("NotificationAdapter", "menuName " + menuName);

        if(menuName.equals("add")){
            mStickerOptionUrl = UrlUtil.ADD_STICKERS_STORE_URL;
        } else{
            mStickerOptionUrl = UrlUtil.REMOVE_STICKERS_STORE_URL;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("collection_id", String.valueOf(listItem.getmListItemId()));

        mAppConst.postJsonResponseForUrl(mStickerOptionUrl, params, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                JSONObject stickerStoreObject = new JSONObject();
                try {
                    stickerStoreObject.put("collection_id", listItem.getmListItemId());
                    stickerStoreObject.put("image_icon", listItem.getmBrowseImgUrl());
                    stickerStoreObject.put("action", menuName);

                    if(menuName.equals("add")){
                        LogUtils.LOGD("NotificationAdapter", "menuName " + menuName + listItem.isAlreadyAdded());
                        if(!listItem.isAlreadyAdded()){
                            ConstantVariables.STICKERS_STORE_ARRAY.put(String.valueOf(listItem.getmListItemId()), stickerStoreObject);
                        } else if(ConstantVariables.STICKERS_STORE_ARRAY != null && ConstantVariables.STICKERS_STORE_ARRAY
                                .optJSONObject(String.valueOf(listItem.getmListItemId())) != null){
                            ConstantVariables.STICKERS_STORE_ARRAY.remove(String.valueOf(listItem.getmListItemId()));
                        }
                        menuObject.put("name", "remove");
                    } else{
                        menuObject.put("name", "add");
                        if(ConstantVariables.STICKERS_STORE_ARRAY != null && ConstantVariables.STICKERS_STORE_ARRAY
                                .optJSONObject(String.valueOf(listItem.getmListItemId())) != null){
                            ConstantVariables.STICKERS_STORE_ARRAY.remove(String.valueOf(listItem.getmListItemId()));
                        } else if (ConstantVariables.STICKERS_STORE_ARRAY != null){
                            ConstantVariables.STICKERS_STORE_ARRAY.put(String.valueOf(listItem.getmListItemId()), stickerStoreObject);
                        }
                    }
                    listItem.getMenuArray().put(1, menuObject);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }


    public static class FRequestViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public BrowseListItems listItem;
        public TextView mOwnerName;
        public Button acceptButton,ignoreButton;
        public View container;

        public FRequestViewHolder(View view) {

            super(view);
            container = view;
            userImage = view.findViewById(R.id.userImage);
            mOwnerName = view.findViewById(R.id.userName);
            acceptButton = view.findViewById(R.id.acceptRequest);
            ignoreButton = view.findViewById(R.id.ignoreRequest);

        }
    }
}
