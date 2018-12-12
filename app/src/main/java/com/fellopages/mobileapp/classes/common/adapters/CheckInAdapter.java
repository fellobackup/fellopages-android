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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.utils.CheckInList;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

public class CheckInAdapter extends ArrayAdapter<CheckInList>{

    int mLayoutResID;
    View mRootView;
    Context mContext;
    List<CheckInList> mCheckInList;
    CheckInList mListItem;
    private ImageLoader mImageLoader;

    public CheckInAdapter(Context context, int layoutResourceId, List<CheckInList> checkInList) {
        super(context, layoutResourceId, checkInList);

        mContext = context;
        mLayoutResID = layoutResourceId;
        mCheckInList = checkInList;
        mImageLoader = new ImageLoader(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if(mRootView == null){

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);

            listItemHolder.mLocationLabel = (TextView) mRootView.findViewById(R.id.locationLabel);
            listItemHolder.mLocationIcon = (BezelImageView) mRootView.findViewById(R.id.location_icon);
            listItemHolder.mLocationInfo = (TextView) mRootView.findViewById(R.id.formattedAddress);
            mRootView.setTag(listItemHolder);

        }else {
            listItemHolder = (ListItemHolder)mRootView.getTag();

        }

        mListItem = this.mCheckInList.get(position);

        /*
        Set Data in the List View Items
         */

        listItemHolder.mLocationLabel.setText(mListItem.getmLocationLabel());
        mImageLoader.setImageUrl(mListItem.getmLocationIcon(), listItemHolder.mLocationIcon);
        if(mListItem.getmFormattedAddress() != null && !mListItem.getmFormattedAddress().isEmpty()){
            listItemHolder.mLocationInfo.setVisibility(View.VISIBLE);
            listItemHolder.mLocationInfo.setText(mListItem.getmFormattedAddress());
        }else{
            listItemHolder.mLocationInfo.setVisibility(View.GONE);
        }


        return mRootView;
    }

    private static class ListItemHolder{

        BezelImageView mLocationIcon;
        TextView mLocationLabel, mLocationInfo;
    }
}
