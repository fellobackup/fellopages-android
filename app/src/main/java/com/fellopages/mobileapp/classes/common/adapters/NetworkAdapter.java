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
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnSuccessListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.NetworkList;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkAdapter extends ArrayAdapter<NetworkList> {

    private Context mContext;
    private int mLayoutResID, mItemPosition;
    private List<NetworkList> mNetWorkList;
    private NetworkList mListItem;
    private AppConstant mAppConst;
    private View mRootView;
    private boolean mIsJoined;
    private OnSuccessListener mOnSuccessListener;

    public NetworkAdapter(Context context, int resource, List<NetworkList> networkList, boolean isJoined,
                          OnSuccessListener onSuccessListener) {
        super(context, resource, networkList);

        mContext = context;
        this.mLayoutResID = resource;
        mNetWorkList = networkList;
        mAppConst = new AppConstant(context);
        mIsJoined = isJoined;
        mOnSuccessListener = onSuccessListener;

    }

    public View getView(int position, View convertView, ViewGroup parent){

        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if(mRootView == null){

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);

            listItemHolder.mNetworkTitle = mRootView.findViewById(R.id.networkTitle);
            listItemHolder.mMemberCount = mRootView.findViewById(R.id.memberCount);
            listItemHolder.mNetworkOption = mRootView.findViewById(R.id.leavejoinOption);
            listItemHolder.mNetworkOption.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            listItemHolder.mNetworkOption.setTag(position);

            mRootView.setTag(listItemHolder);

        }else {
            listItemHolder = (ListItemHolder)mRootView.getTag();
        }

        mListItem = this.mNetWorkList.get(position);

        listItemHolder.mNetworkTitle.setText(mListItem.getNetworkTitle());
        if(mIsJoined)
            listItemHolder.mNetworkOption.setText(mContext.getResources().getString(R.string.leave_network_text));
        else
            listItemHolder.mNetworkOption.setText(mContext.getResources().getString(R.string.join_network_text));

        listItemHolder.mNetworkOption.setMovementMethod(LinkMovementMethod.getInstance());
        listItemHolder.mNetworkOption.setClickable(true);
        listItemHolder.mNetworkOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int position = (int ) view.getTag();
                listItemHolder.mNetworkOption.setText("\uf110");
                leaveJoinNetwork(position);
            }
        });


        String membersText = mContext.getResources().getQuantityString(R.plurals.member_text,
                mListItem.getMemberCount());
        listItemHolder.mMemberCount.setText(String.format(
                mContext.getResources().getString(R.string.group_member_count_text),
                mListItem.getMemberCount(), membersText
        ));

        return mRootView;
    }

    private static class ListItemHolder{
        SelectableTextView mNetworkTitle, mMemberCount;
        TextView mNetworkOption;
    }

    public void leaveJoinNetwork(int position){

        mItemPosition = position;
        NetworkList networkInfo = mNetWorkList.get(mItemPosition);
        int networkId = networkInfo.getNetworkId();

        Map<String, String> postParam = new HashMap<>();

        if(mIsJoined){
            postParam.put("leave_id", String.valueOf(networkId));
        }else{
            postParam.put("join_id", String.valueOf(networkId));
        }

        final String url = AppConstant.DEFAULT_URL + "members/settings/network";


        mAppConst.postJsonResponseForUrl(url, postParam, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                if (mOnSuccessListener != null) {
                    mOnSuccessListener.onSuccess();
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });


    }


}
