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
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

public class AddPeopleAdapter extends ArrayAdapter<AddPeopleList> {

    Context mContext;
    List<AddPeopleList> mAddPeopleList;
    AddPeopleList mListItem;
    int mLayoutResID;
    View mRootView;
    private ImageLoader mImageLoader;

    public AddPeopleAdapter(Context context, int layoutResourceId, List<AddPeopleList> friendsList) {

        super(context, layoutResourceId, friendsList);

        mContext = context;
        mAddPeopleList = friendsList;
        mLayoutResID = layoutResourceId;
        mImageLoader = new ImageLoader(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if(mRootView == null){

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);

            listItemHolder.mFriendImage = mRootView.findViewById(R.id.friendImage);
            listItemHolder.mFriendLabel = mRootView.findViewById(R.id.friendLabel);
            mRootView.setTag(listItemHolder);

        }else {
            listItemHolder = (ListItemHolder)mRootView.getTag();

        }

        mListItem = mAddPeopleList.get(position);

        /*
        Set Data in the List View Items
         */
        mImageLoader.setImageForUserProfile(mListItem.getmUserPhoto(), listItemHolder.mFriendImage);
        listItemHolder.mFriendLabel.setText(mListItem.getmUserLabel());

        return mRootView;
    }

    private static class ListItemHolder{

        ImageView mFriendImage;
        TextView mFriendLabel;
    }
}
