package com.bigsteptech.realtimechat.conversation.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.conversation.ConversationActivity;
import com.bigsteptech.realtimechat.conversation.data_model.Message;
import com.bigsteptech.realtimechat.conversation.view.ConversationMessageView;
import com.bigsteptech.realtimechat.interfaces.OnRetryClicked;
import com.bigsteptech.realtimechat.interfaces.OnSongPlay;

/**
 * Created by marco on 29/07/16.
 */

class ConversationMessageViewHolder extends RecyclerView.ViewHolder {

    private final ConversationMessageView conversationMessageView;
    private Context mContext;

    public ConversationMessageViewHolder(Context context, ConversationMessageView messageView) {
        super(messageView);
        this.mContext = context;
        this.conversationMessageView = messageView;
    }

    public void bind(int position, Message message, OnSongPlay onSongPlay,
                     OnRetryClicked onRetryClicked, String selfUid) {
        conversationMessageView.display(position, message, onSongPlay, onRetryClicked, selfUid);
    }
}
