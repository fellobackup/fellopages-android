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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import java.util.List;

public class PackageViewAdapter extends BaseAdapter{

    private List<BrowseListItems> mPackageDetail;
    private BrowseListItems mPackageInfo;
    private Activity mActivity;
    View mRootView;

    public PackageViewAdapter(Activity activity,List<BrowseListItems> packageDetail){

        this.mActivity = activity;
        this.mPackageDetail = packageDetail;
    }

    @Override
    public int getCount() {
        return mPackageDetail.size();
    }

    @Override
    public Object getItem(int position) {
        return mPackageDetail.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if(mRootView == null){

            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(R.layout.package_view_detail, parent, false);

            listItemHolder.mPackageParamLabel = mRootView.findViewById(R.id.packageParamLabel);
            listItemHolder.mPackageParamValue = mRootView.findViewById(R.id.packageParamValue);
            mRootView.setTag(listItemHolder);

        }else {
            listItemHolder = (ListItemHolder)mRootView.getTag();
        }

        mPackageInfo = mPackageDetail.get(position);

        /*
        Set Data in the List View Items
         */

        listItemHolder.mPackageParamLabel.setText(mPackageInfo.getmPackageParamLabel());
        listItemHolder.mPackageParamValue.setText(mPackageInfo.getmPackageParamValue());

        return mRootView;
    }

    private static class ListItemHolder{

        TextView mPackageParamLabel, mPackageParamValue;
    }
}
