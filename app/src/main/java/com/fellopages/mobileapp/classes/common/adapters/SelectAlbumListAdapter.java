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
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import java.util.List;

public class SelectAlbumListAdapter extends ArrayAdapter<BrowseListItems> {
    Context mContext;
    List<BrowseListItems> mBrowseItemList;
    int mLayoutResID;
    View mRootView;
    BrowseListItems listItems;

    public SelectAlbumListAdapter(Context context, int resource, List<BrowseListItems> listItems) {
        super(context, resource,listItems);
        mContext = context;
        mBrowseItemList = listItems;
        mLayoutResID = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        mRootView = convertView;
        listItems = mBrowseItemList.get(position);
        AppCompatTextView mAlbumTitle = new AppCompatTextView(mContext);
        mAlbumTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.title_medium_font_size));
        mAlbumTitle.setTextColor(Color.BLACK);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp);
        mAlbumTitle.setPadding(padding, padding, padding, padding);
        mAlbumTitle.setText(listItems.getmBrowseListTitle());
        mAlbumTitle.setFitsSystemWindows(true);
        return mAlbumTitle;
    }

}
