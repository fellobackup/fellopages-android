package com.socialengineaddons.messenger.conversation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.socialengineaddons.messenger.conversation.data_model.Message;
import com.socialengineaddons.messenger.conversation.view.ConversationMessageView;
import com.socialengineaddons.messenger.interfaces.OnRetryClicked;
import com.socialengineaddons.messenger.interfaces.OnSongPlay;

class ConversationMessageViewHolder extends RecyclerView.ViewHolder {

    private final ConversationMessageView conversationMessageView;

    ConversationMessageViewHolder(Context context, ConversationMessageView messageView) {
        super(messageView);
        this.conversationMessageView = messageView;
    }

    void bind(int position, Message message, OnSongPlay onSongPlay,
              OnRetryClicked onRetryClicked, String selfUid) {
        conversationMessageView.display(position, message, onSongPlay, onRetryClicked, selfUid);
    }
}
