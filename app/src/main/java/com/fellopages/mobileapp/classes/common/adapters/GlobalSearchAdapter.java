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

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;
public class GlobalSearchAdapter extends ArrayAdapter<BrowseListItems> {

    Context mContext;
    int mLayoutResID;
    List<BrowseListItems> mBrowseItemlist;
    AppConstant mAppConst;
    View mRootView;
    BrowseListItems listItems;
    private ImageLoader mImageLoader;

    public GlobalSearchAdapter(Context context, int layoutResourceID, List<BrowseListItems> listItem) {

        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemlist = listItem;
        mAppConst = new AppConstant(context);
        mImageLoader = new ImageLoader(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if (mRootView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);
            listItemHolder.mGlobalSearchLayout = (LinearLayout) mRootView.findViewById(R.id.globalSearchLayout);
            listItemHolder.mGlobalSearchLayout.setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.contentTitle).setVisibility(View.GONE);
            listItemHolder.mContentDetail = (TextView) mRootView.findViewById(R.id.contentDetail);
            listItemHolder.mTitle = (TextView) mRootView.findViewById(R.id.title);
            listItemHolder.mModuleName = (TextView) mRootView.findViewById(R.id.moduleName);
            listItemHolder.mListImage = (ImageView) mRootView.findViewById(R.id.contentImage);
            listItemHolder.mOptionIcon = (ImageView) mRootView.findViewById(R.id.optionIcon);
            listItemHolder.mOptionIcon.setVisibility(View.GONE);

            mRootView.setTag(listItemHolder);
        } else {
            listItemHolder = (ListItemHolder) mRootView.getTag();
        }

        listItems =  this.mBrowseItemlist.get(position);

        /*
        Set Data in the List View Items
         */
        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);
        if (listItems.getmBrowseListTitle() != null && !listItems.getmBrowseListTitle().isEmpty()) {
            listItemHolder.mTitle.setVisibility(View.VISIBLE);
            listItemHolder.mTitle.setText(listItems.getmBrowseListTitle());
        } else {
            listItemHolder.mTitle.setVisibility(View.GONE);
        }

        if (listItems.getmModuleName() != null && !listItems.getmModuleName().isEmpty()) {
            listItemHolder.mModuleName.setText(listItems.getmModuleName());
        }

        if (listItems.getmDescription() != null && !listItems.getmDescription().isEmpty()) {
            listItemHolder.mContentDetail.setMaxLines(2);
            listItemHolder.mContentDetail.setText(Html.fromHtml(listItems.getmDescription()));
        }

        mRootView.setId(listItems.getmListItemId());

        return mRootView;
    }

    private static class ListItemHolder {
        ImageView  mOptionIcon, mListImage;
        TextView mTitle, mContentDetail,mModuleName;
        LinearLayout mGlobalSearchLayout;

    }
}