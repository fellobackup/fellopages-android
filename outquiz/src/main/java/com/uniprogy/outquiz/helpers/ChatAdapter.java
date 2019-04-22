package com.uniprogy.outquiz.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.models.ChatMessage;
import com.uniprogy.outquiz.models.Player;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<ChatMessage> messages;

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        ImageView avatarImageView;

        public ChatViewHolder(View view) {
            super(view);
            messageTextView = view.findViewById(R.id.messageTextView);
            avatarImageView = view.findViewById(R.id.avatarImageView);
        }
    }

    public ChatAdapter(ArrayList<ChatMessage> messages)
    {
        this.messages = messages;
    }

    public void add(ChatMessage message)
    {
        messages.add(message);
        notifyItemInserted(messages.size()-1);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_chat, parent, false);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        try {
            ChatMessage obj = messages.get(position);
            obj.setAvatar(holder.avatarImageView);
            holder.messageTextView.setText(obj.getSpannableString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
