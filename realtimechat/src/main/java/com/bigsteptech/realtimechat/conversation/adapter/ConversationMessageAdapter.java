package com.bigsteptech.realtimechat.conversation.adapter;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.Utils;
import com.bigsteptech.realtimechat.conversation.data_model.Message;
import com.bigsteptech.realtimechat.conversation.view.ConversationMessageView;
import com.bigsteptech.realtimechat.interfaces.OnRetryClicked;
import com.bigsteptech.realtimechat.interfaces.OnSongPlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_THIS_USER = 0;
    private static final int VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE = 1;

    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS = 2;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS_OTHER_DATE = 3;

    private static final int VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE = 4;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE_OTHER_DATE = 5;

    private static final int VIEW_TYPE_MESSAGE_OTHER_USER_NAME = 6;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USER_NAME_OTHER_DATE = 7;

    private static final int VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE = 8;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE = 9;

    public static final int VIEW_PROG_BAR = 10;

    private List<Message> chat = new ArrayList<>();
    public static List<Message> selectedMessages = new ArrayList<>();

    public static List<MediaPlayer> medialPlayers = new ArrayList<>();
    public static Map<Handler, Runnable> medialPlayerHandlers = new HashMap<>();
    private String self;
    private final LayoutInflater inflater;
    private OnSongPlay mOnSongPlay;
    public static MediaPlayer mediaPlayer;
    private OnRetryClicked mOnRetryClicked;
    private Context mContext;

    public ConversationMessageAdapter(Context context, String user, LayoutInflater inflater,
                                      OnSongPlay onSongPlay, OnRetryClicked onRetryClicked) {
        this.self = user;
        this.inflater = inflater;
        mOnSongPlay = onSongPlay;
        mOnRetryClicked = onRetryClicked;
        setHasStableIds(true);
        this.mContext = context;
    }

    public void update(List<Message> chats) {
        this.chat = chats;
        Log.d(ConversationMessageAdapter.class.getSimpleName(), "update called  size:- " + chat.size());
        notifyDataSetChanged();
    }

    public void add(Message message) {
        this.chat.add(message);
    }

    public void addToLast(Message message) {
        this.chat.add(chat.size(), message);
        notifyItemInserted(chat.size()-1);
    }

    public void add(int position, Message message) {
        this.chat.add(position, message);
        notifyDataSetChanged();
    }

    public void remove(int position){
        this.chat.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = this.chat.size();
        this.chat.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConversationMessageView messageView = null;


        Log.d(ConversationMessageAdapter.class.getSimpleName(), "onCreateViewHolder called " + viewType);
        switch (viewType){

            case VIEW_TYPE_MESSAGE_THIS_USER:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_sender_view, parent, false);
//                return new ConversationMessageViewHolder(messageView);
                break;


            case VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_sender_other_date_view, parent, false);
//                return new ConversationMessageViewHolder(messageView);
            break;


            case VIEW_TYPE_MESSAGE_OTHER_USERS:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_view, parent, false);
                messageView.findViewById(R.id.user_image).setVisibility(View.INVISIBLE);
                messageView.findViewById(R.id.user_name).setVisibility(View.GONE);
//                return new ConversationMessageViewHolder(messageView);
            break;


            case VIEW_TYPE_MESSAGE_OTHER_USERS_OTHER_DATE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_other_date_view, parent, false);
                messageView.findViewById(R.id.user_image).setVisibility(View.INVISIBLE);
                messageView.findViewById(R.id.user_name).setVisibility(View.GONE);
//                return new ConversationMessageViewHolder(messageView);

                break;
            case VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_view, parent, false);
                messageView.findViewById(R.id.user_name).setVisibility(View.GONE);
//                return new ConversationMessageViewHolder(messageView);
                break;

            case VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE_OTHER_DATE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_other_date_view, parent, false);
                messageView.findViewById(R.id.user_name).setVisibility(View.GONE);
//                return new ConversationMessageViewHolder(messageView);
            break;


            case VIEW_TYPE_MESSAGE_OTHER_USER_NAME:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_view, parent, false);
                messageView.findViewById(R.id.user_image).setVisibility(View.INVISIBLE);
//                return new ConversationMessageViewHolder(messageView);
                break;


            case VIEW_TYPE_MESSAGE_OTHER_USER_NAME_OTHER_DATE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_other_date_view, parent, false);
                messageView.findViewById(R.id.user_image).setVisibility(View.INVISIBLE);
                break;
//                return new ConversationMessageViewHolder(messageView);

            case VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_view, parent, false);
//                return new ConversationMessageViewHolder(messageView);
            break;


            case VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE:
                messageView = (ConversationMessageView) inflater.inflate(R.layout.conversation_message_item_destination_other_date_view, parent, false);
//                return new ConversationMessageViewHolder(messageView);
            break;
        }

        if(viewType == VIEW_PROG_BAR){
            View view = inflater.inflate(R.layout.progress_item, parent, false);
            return new ProgressViewHolder(view);
        } else {
            return new ConversationMessageViewHolder(mContext, messageView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ConversationMessageViewHolder){
            ((ConversationMessageViewHolder) holder).bind(position, chat.get(position), mOnSongPlay,
                    mOnRetryClicked, self);
        } else {
            Message progressBarMessage = chat.get(position);
            if(progressBarMessage.isShowProgressBar()){
                ((ProgressViewHolder)holder).progressBar.setVisibility(View.VISIBLE);
                ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
            } else {
                ((ProgressViewHolder)holder).progressBar.setVisibility(View.GONE);
                ((ProgressViewHolder)holder).progressBar.setIndeterminate(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    @Override
    public long getItemId(int position) {
        return chat.get(position).getItemId();
    }

    public List<Message> getConversationList(){
        return chat;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0){
            // Position 0 will be progress bar always.
            return VIEW_PROG_BAR;

        } else if (position == 1){
            /*  Position 1 ->

                if there is only one chat, show date+image+name with that message.
                Date and Name will be always shown, Need to check for Image
             */

            if(chat.size() == 2){
                return chat.get(position).getOwnerId().equals(self) ?  VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE
                        : VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE;
            } else {
                try{
                    return chat.get(position).getOwnerId().equals(self) ? VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE :
                            !chat.get(position).getOwnerId().equals(chat.get(position + 1).getOwnerId()) ?
                                    VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE : VIEW_TYPE_MESSAGE_OTHER_USER_NAME_OTHER_DATE;
                } catch (IndexOutOfBoundsException exception){
                    // Last Position-> Image Will be always shown, Need to check for Name
                    return chat.get(position).getOwnerId().equals(self) ? VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE  :
                            !chat.get(position).getOwnerId().equals(chat.get(position-1).getOwnerId()) ? VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE :
                                    VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE_OTHER_DATE;
                }
            }
        } else {
            // If Dates are Different
            String[] date1 = Utils.getDateFromTimeStamp(chat.get(position - 1).getCreatedAt()).split("/");
            String[] date2 = Utils.getDateFromTimeStamp(chat.get(position).getCreatedAt()).split("/");
            String creator1 = chat.get(position).getOwnerId();
            String creator3 = chat.get(position - 1).getOwnerId();
            String concatDate1 = date1[0] + date1[1] + date1[2];
            String concatDate2 = date2[0] + date2[1] + date2[2];
            if (!concatDate1.equals(concatDate2)) {

                // If Dates are different

                try{
                    String creator2 = chat.get(position + 1).getOwnerId();

                    // Name will be shown with the first position of different date, need to check to display image
                    return creator1.equals(self) ? VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE :
                            !creator1.equals(creator2) ? VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE :
                                    VIEW_TYPE_MESSAGE_OTHER_USER_NAME_OTHER_DATE;
                } catch (IndexOutOfBoundsException e){
                    // Last Position-> Image Will be always shown, Need to check for Name
                    return creator1.equals(self) ? VIEW_TYPE_MESSAGE_THIS_USER_OTHER_DATE  :
                            !creator1.equals(creator3) ? VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE_OTHER_DATE :
                                    VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE_OTHER_DATE;
                }
            } else {

                // If Dates Are same

                try{
                    String ownerId1 = chat.get(position).getOwnerId();
                    String ownerId2 = chat.get(position -1).getOwnerId();
                    String ownerId3 = chat.get(position + 1).getOwnerId();
                    // Need to apply Name Logic here
                    return ownerId1.equals(self) ? VIEW_TYPE_MESSAGE_THIS_USER :
                            !ownerId1.equals(ownerId2) ? !ownerId1.equals(ownerId3) ?
                                    VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE : VIEW_TYPE_MESSAGE_OTHER_USER_NAME :
                                    !ownerId1.equals(ownerId3) ? VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE : VIEW_TYPE_MESSAGE_OTHER_USERS;
                } catch (IndexOutOfBoundsException exception){
                    // Last Position with same date => Image will be shown, Need to check for Name
                    return chat.get(position).getOwnerId().equals(self) ? VIEW_TYPE_MESSAGE_THIS_USER :
                            !chat.get(position).getOwnerId().equals(chat.get(position-1).getOwnerId()) ? VIEW_TYPE_MESSAGE_OTHER_USER_NAME_IMAGE
                                    : VIEW_TYPE_MESSAGE_OTHER_USER_IMAGE;
                }
            }
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

}
