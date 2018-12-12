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
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mSelectedItem;
    private List<BrowseListItems> mBrowseList;

    public SpinnerAdapter(Context context, int resource, int selectedItem) {
        super(context, resource);

        mContext = context;
        mSelectedItem = selectedItem;
    }

    public SpinnerAdapter(Context context, int resource, int selectedItem,
                          List<BrowseListItems> browseList, ArrayList list) {
        super(context, resource, R.id.text, list);
        this.mContext = context;
        mSelectedItem = selectedItem;
        this.mBrowseList = browseList;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, int selectedItem) {

        View v;
        mSelectedItem = selectedItem;
        if (mBrowseList == null || mBrowseList.isEmpty()) {
            v = super.getDropDownView(position, null, parent);
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.spinner_value_layout, parent, false);
            TextView textView = (TextView) v.findViewById(R.id.text);
            TextView tvIcon = (TextView) v.findViewById(R.id.icon);
            tvIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

            BrowseListItems browseListItems = mBrowseList.get(position);
            if (browseListItems.getAlbumId() != null && !browseListItems.getAlbumId().isEmpty()) {
                tvIcon.setVisibility(View.VISIBLE);
                switch (browseListItems.getAlbumId()) {
                    case "1":
                        tvIcon.setText("\uf167");
                        break;

                    case "2":
                        tvIcon.setText("\uf27d");
                        break;

                    case "3":
                        tvIcon.setText("\uf10b");
                        break;

                    case "4":
                        tvIcon.setText("\uf01d");
                        break;
                }
            } else {
                tvIcon.setVisibility(View.VISIBLE);
            }
            textView.setText(browseListItems.getmBrowseListTitle());
        }

        // If this is the selected item position
        if (position == mSelectedItem) {
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_background));
        }
        return v;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mBrowseList != null && !mBrowseList.isEmpty()) {
            return getCustomView(position, convertView, parent, mSelectedItem);
        } else {
            return super.getView(position, convertView, parent);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, mSelectedItem);
    }
}
