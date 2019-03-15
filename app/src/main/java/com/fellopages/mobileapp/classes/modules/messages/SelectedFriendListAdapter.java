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

package com.fellopages.mobileapp.classes.modules.messages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.SelectedFriendList;

import java.util.List;
import java.util.Map;

public class SelectedFriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SelectedFriendList> mSelectedFriendList;
    private SelectedFriendList mSelectedFriend;
    private RecyclerView recyclerView;
    private Map<String,String> mSelectedFriends;
    private Map<Integer, String> mShowNonSelectedFriend;
    private boolean mIsSendMessageRequest;

    public SelectedFriendListAdapter(List<SelectedFriendList> selectedFriendList,
                                     RecyclerView listview, Map<String, String> selectedFriends,
                                     Map<Integer, String> showNonSelectedFriend, boolean isSendMessageRequest){

        mSelectedFriendList = selectedFriendList;
        recyclerView = listview;
        mSelectedFriends = selectedFriends;
        mShowNonSelectedFriend = showNonSelectedFriend;
        mIsSendMessageRequest = isSendMessageRequest;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tagview_layout, parent, false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        mSelectedFriend = mSelectedFriendList.get(position);
        ((FriendViewHolder)holder).selectedFriend = mSelectedFriend;
        ((FriendViewHolder)holder).userName.setText(mSelectedFriend.getSelectedUserName());
        ((FriendViewHolder)holder).closeBtn.setTag(holder);

        // If it is send message request from UserProfile page then hiding close icon
        if (mIsSendMessageRequest) {
            ((FriendViewHolder)holder).closeBtn.setVisibility(View.GONE);
        } else {
            ((FriendViewHolder)holder).closeBtn.setVisibility(View.VISIBLE);
        }
        ((FriendViewHolder)holder).closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendViewHolder friendViewHolder = (FriendViewHolder) v.getTag();
                mSelectedFriendList.remove(friendViewHolder.selectedFriend);
                mSelectedFriends.remove(String.valueOf(friendViewHolder.selectedFriend.getSelectedUserId()));
                if (!mShowNonSelectedFriend.isEmpty())
                    mShowNonSelectedFriend.remove(friendViewHolder.selectedFriend.getSelectedUserId());
                notifyDataSetChanged();
                if(mSelectedFriendList.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectedFriendList.size();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView closeBtn;
        public TextView userName;
        public SelectedFriendList selectedFriend;
        public View container;

        public FriendViewHolder(View v) {
            super(v);

            container = v;
            closeBtn = v.findViewById(R.id.cancel);
            userName = v.findViewById(R.id.name);
        }
    }
}
