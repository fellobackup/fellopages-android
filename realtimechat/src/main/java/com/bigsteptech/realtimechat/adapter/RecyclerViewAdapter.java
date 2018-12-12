package com.bigsteptech.realtimechat.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.contacts.ContactsListFragment;
import com.bigsteptech.realtimechat.contacts.data_model.ContactsList;
import com.bigsteptech.realtimechat.conversation.data_model.Conversation;
import com.bigsteptech.realtimechat.conversation.view.BadgeLayout;
import com.bigsteptech.realtimechat.groups.GroupDetails;
import com.bigsteptech.realtimechat.groups.data_model.GroupsList;
import com.bigsteptech.realtimechat.interfaces.OnItemClickListener;
import com.bigsteptech.realtimechat.interfaces.OnLongClickListener;
import com.bigsteptech.realtimechat.interfaces.OnMenuItemSelected;
import com.bigsteptech.realtimechat.ui.CircularImageView;
import com.bigsteptech.realtimechat.user.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter {


    private List<Object> mConversationList = new ArrayList<>();
    private Context mContext;
    private Conversation conversation;
    private ContactsList contactsList;
    private GroupsList groupsList;

    private static final int RECENT_CONVERSATION_LIST = 1;
    private static final int CONTACT_LIST_WITH_ONLINE_HEADER = 2;
    private static final int CONTACT_LIST_WITH_OFFLINE_HEADER = 3;
    private static final int CONTACT_LIST_WITHOUT_HEADER = 4;
    private static final int GROUPS_LIST = 5;
    private static final int GROUPS_PARTICIPANTS_LIST = 6;
    private static final int VIEW_PROG = 7;
    private OnItemClickListener mOnItemClickListener;
    private OnMenuItemSelected mOnMenuItemSelected;
    private OnLongClickListener mOnLongClickListener;

    private boolean isNewGroupsPage = false, blockedContactsPage = false;

    private String selfUid;


    public RecyclerViewAdapter(Context context, List<Object> listItem, String selfUid, OnItemClickListener onItemClickListener) {

        this.mContext = context;
        this.mConversationList = listItem;
        mOnItemClickListener = onItemClickListener;
        this.selfUid = selfUid;
    }


    public RecyclerViewAdapter(Context context, List<Object> listItem, String selfUid,
                               OnItemClickListener onItemClickListener, OnLongClickListener onLongClickListener) {

        this.mContext = context;
        this.mConversationList = listItem;
        mOnItemClickListener = onItemClickListener;
        this.selfUid = selfUid;
        this.mOnLongClickListener = onLongClickListener;
    }

    public RecyclerViewAdapter(Context context, List<Object> listItem, String selfUid, OnMenuItemSelected onMenuItemSelected) {

        this.mContext = context;
        this.mConversationList = listItem;
        mOnMenuItemSelected = onMenuItemSelected;
        this.selfUid = selfUid;
    }

    public RecyclerViewAdapter(Context context, boolean isNewGroupPage, boolean blockedContactsPage,
                               String selfUid, OnItemClickListener onItemClickListener) {

        this.mContext = context;
        mOnItemClickListener = onItemClickListener;
        this.isNewGroupsPage = isNewGroupPage;
        this.blockedContactsPage = blockedContactsPage;
        this.selfUid = selfUid;
    }


    @Override
    public int getItemCount() {
        return mConversationList.size();
    }

    public List<Object> getmConversationList(){

        return mConversationList;
    }

    public void update(List<Object> conversationList){
        mConversationList = conversationList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if(mConversationList.get(position) != null) {
            if(mConversationList.get(position) instanceof Conversation){
                return RECENT_CONVERSATION_LIST;
            } else if (mConversationList.get(position) instanceof ContactsList){
                if(isNewGroupsPage || blockedContactsPage){
                    return CONTACT_LIST_WITHOUT_HEADER;
                } else {
//                    if(position == 0){
//                        return CONTACT_LIST_WITH_ONLINE_HEADER;
//                    } else if(((ContactsList) mConversationList.get(position)).getmOnlineStatus() !=
//                            ((ContactsList) mConversationList.get(position - 1)).getmOnlineStatus()){
//                        return CONTACT_LIST_WITH_OFFLINE_HEADER;
//                    } else {
//                        return CONTACT_LIST_WITHOUT_HEADER;
//                    }
                    return CONTACT_LIST_WITHOUT_HEADER;
                }
            } else if(mConversationList.get(position) instanceof User){
                return GROUPS_PARTICIPANTS_LIST;
            } else {
                return GROUPS_LIST;
            }
        } else  {
            return VIEW_PROG;
        }
    }

    public void filter(String text) {
        Iterator<Object> it = mConversationList.iterator();
        while (it.hasNext()) {
            if (!((ContactsList)it.next()).getmUserTitle().toLowerCase().contains(text.toLowerCase()))
                it.remove();
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){

            case RECENT_CONVERSATION_LIST:
            case GROUPS_PARTICIPANTS_LIST:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recent_conversation_list, parent, false);
                viewHolder = new ItemViewHolder(itemView, mContext);
                break;

            case CONTACT_LIST_WITH_ONLINE_HEADER:
            case CONTACT_LIST_WITH_OFFLINE_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recent_conversation_list, parent, false);
                itemView.findViewById(R.id.header_text).setVisibility(View.VISIBLE);
                viewHolder = new ItemViewHolder(itemView, mContext);
                break;

            case CONTACT_LIST_WITHOUT_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recent_conversation_list, parent, false);
                itemView.findViewById(R.id.header_text).setVisibility(View.GONE);
                viewHolder = new ItemViewHolder(itemView, mContext);
                break;

            case GROUPS_LIST:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.groups_list, parent, false);
                viewHolder = new ItemViewHolder(itemView, mContext);
                break;

            case VIEW_PROG:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
                viewHolder = new ProgressViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){

            case RECENT_CONVERSATION_LIST:
                conversation = (Conversation) mConversationList.get(position);

                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams)((ItemViewHolder) holder).container.getLayoutParams();
                if(!conversation.isChatDeleted()) {
                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    ((ItemViewHolder) holder).container.setLayoutParams(params);
                    ((ItemViewHolder) holder).container.setVisibility(View.VISIBLE);
                    if(conversation.getmUserImage() != null && !conversation.getmUserImage().isEmpty()){
                        Log.d("Picasso Image - ",conversation.getmUserImage());

                        // todo fix to http/https issue
//                    Picasso.Builder builder = new Picasso.Builder(mContext);
//                    builder.listener(new Picasso.Listener() {
//                        @Override
//                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                            Log.e("Picasso Error", "Failed to load image: " + exception);
//                        }
//                    });
//                    builder.downloader(new OkHttpDownloader(mContext));
//                    Picasso pic = builder.build();
//                    pic.load(conversation.getmUserImage())
//                            .placeholder(R.drawable.person_image_empty)
//                            .fit()
//                            .into(((ItemViewHolder) holder).userImage);

                        Picasso.with(mContext)
                                .load(conversation.getmUserImage())
                                .placeholder(R.drawable.person_image_empty)
                                .fit()
                                .into(((ItemViewHolder) holder).userImage);
                    } else {
                        ((ItemViewHolder) holder).userImage.setImageResource(R.drawable.person_image_empty);
                    }

                    ((ItemViewHolder) holder).mUserTitle.setText(conversation.getmUserTitle());

                    if(conversation.getTypeOfChat() == 1){
                        ((ItemViewHolder) holder).onlineTextView.setVisibility(View.GONE);
                    } else {
                        ((ItemViewHolder) holder).onlineTextView.setVisibility(View.VISIBLE);
                        ((ItemViewHolder) holder).onlineTextView.setText("\uf111");

                        if(conversation.getVisibility() == 1){
                            switch (conversation.getOnlineStatus()){

                                case 0:
                                    ((ItemViewHolder) holder).onlineTextView.setTextColor(ContextCompat.
                                            getColor(mContext, R.color.grey));
                                    break;

                                case 1:
                                    ((ItemViewHolder) holder).onlineTextView.setTextColor(ContextCompat.
                                            getColor(mContext, R.color.light_green));
                                    break;
                            }
                        } else {
                            ((ItemViewHolder) holder).onlineTextView.setTextColor(ContextCompat.
                                    getColor(mContext, R.color.grey));
                        }

                    }

                    if(conversation.getmMessageId().equals("false")) {
                        ((ItemViewHolder) holder).mConversationTime.setVisibility(View.GONE);
                        ((ItemViewHolder) holder).mChatMessage.setVisibility(View.GONE);
                        ((ItemViewHolder) holder).badgeView.setVisibility(View.GONE);
                        ((ItemViewHolder) holder).statusImageView.setVisibility(View.GONE);
                    } else {
                        ((ItemViewHolder) holder).mConversationTime.setText(conversation.getLastUpdatedTime());
                        ((ItemViewHolder) holder).mConversationTime.setVisibility(View.VISIBLE);
                        if(conversation.getmChatMessage() != null) {
                            ((ItemViewHolder) holder).mChatMessage.setText(Html.fromHtml(Html.fromHtml(Html.fromHtml(
                                    conversation.getmChatMessage()).toString()).toString()));
                        }

                        if(conversation.isTyping() && !conversation.isDuplicateChatExist()) {
                            ((ItemViewHolder) holder).mTypingText.setText(mContext.getResources().
                                    getString(R.string.typing));
                            ((ItemViewHolder) holder).mTypingText.setVisibility(View.VISIBLE);
                            ((ItemViewHolder) holder).mChatMessage.setVisibility(View.GONE);
                        } else {
                            ((ItemViewHolder) holder).mTypingText.setVisibility(View.GONE);
                            ((ItemViewHolder) holder).mChatMessage.setVisibility(View.VISIBLE);
                        }

                        RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) ((ItemViewHolder) holder).
                                mChatMessage.getLayoutParams();

                        if(conversation.getUnreadMessageCount() != 0){
                            ((ItemViewHolder) holder).badgeView.setVisibility(View.VISIBLE);
                            ((ItemViewHolder) holder).badgeView.setText(String.valueOf(conversation.getUnreadMessageCount()));

                            messageLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.unReadMessageCount);
                            ((ItemViewHolder) holder).mChatMessage.setLayoutParams(messageLayoutParams);
                        } else {
                            ((ItemViewHolder) holder).badgeView.setVisibility(View.GONE);

                            messageLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.status);
                            ((ItemViewHolder) holder).mChatMessage.setLayoutParams(messageLayoutParams);
                        }

                        if(conversation.getLastMessageStatus() != 0){

                            ((ItemViewHolder) holder).statusImageView.setVisibility(View.VISIBLE);
                            switch (conversation.getLastMessageStatus()){

                                case 1:
                                    ((ItemViewHolder) holder).statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_sent_18dp));
                                    ((ItemViewHolder) holder).statusImageView.setColorFilter(ContextCompat.
                                            getColor(mContext, R.color.grey));
                                    break;

                                case 2:
                                    ((ItemViewHolder) holder).statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_delivered_15dp));
                                    ((ItemViewHolder) holder).statusImageView.setColorFilter(ContextCompat.
                                            getColor(mContext, R.color.grey));
                                    break;

                                case 3:
                                    ((ItemViewHolder) holder).statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_delivered_15dp));
                                    ((ItemViewHolder) holder).statusImageView.setColorFilter(ContextCompat.
                                            getColor(mContext, R.color.colorPrimary));
                                    break;
                            }
                        } else {
                            ((ItemViewHolder) holder).statusImageView.setVisibility(View.GONE);
                        }
                    }

                    ((ItemViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                        }
                    });

                    ((ItemViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnLongClickListener.onLongClick(holder.getAdapterPosition());
                            return false;
                        }
                    });
                } else {
                    params.height = 0; params.width = 0;
                    ((ItemViewHolder) holder).container.setLayoutParams(params);
                    ((ItemViewHolder) holder).container.setVisibility(View.GONE);
                }

                break;

            case CONTACT_LIST_WITH_OFFLINE_HEADER:
            case CONTACT_LIST_WITH_ONLINE_HEADER:
            case CONTACT_LIST_WITHOUT_HEADER:
                contactsList = (ContactsList) mConversationList.get(position);


                if(holder.getItemViewType() == CONTACT_LIST_WITH_ONLINE_HEADER){
                    ((ItemViewHolder) holder).headerTextView.setText(mContext.getResources().
                            getString(R.string.online));
                } else if(holder.getItemViewType() == CONTACT_LIST_WITH_OFFLINE_HEADER){
                    ((ItemViewHolder) holder).headerTextView.setText(mContext.getResources().
                            getString(R.string.offline));
                }

                if(contactsList.getmUserImage() != null ){
                    Picasso.with(mContext)
                            .load(contactsList.getmUserImage())
                            .placeholder(R.drawable.person_image_empty)
                            .into(((ItemViewHolder) holder).userImage);
                }

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((ItemViewHolder) holder).
                        mUserTitle.getLayoutParams();

                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                ((ItemViewHolder) holder).mUserTitle.setLayoutParams(layoutParams);
                ((ItemViewHolder) holder).mUserTitle.setText(contactsList.getmUserTitle());

                ((ItemViewHolder) holder).mChatMessage.setVisibility(View.GONE);
                ((ItemViewHolder) holder).mConversationTime.setVisibility(View.GONE);


                ((ItemViewHolder) holder).onlineTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                        "fontIcons/fontawesome-webfont.ttf"));
                ((ItemViewHolder) holder).onlineTextView.setText("\uf111");


                switch (contactsList.getmOnlineStatus()){
                    case 0:
                        ((ItemViewHolder) holder).onlineTextView.setTextColor(ContextCompat.
                                getColor(mContext, R.color.grey));
                        break;

                    case 1:
                        ((ItemViewHolder) holder).onlineTextView.setTextColor(ContextCompat.
                                getColor(mContext, R.color.light_green));
                        break;
                }

                if(isNewGroupsPage) {
                    ((ItemViewHolder) holder).mSelectUserCheckBox.setVisibility(View.VISIBLE);
                } else {
                    ((ItemViewHolder) holder).mSelectUserCheckBox.setVisibility(View.GONE);
                }

                ((ItemViewHolder) holder).mSelectUserCheckBox.setChecked(contactsList.isContactSelected());

                ((ItemViewHolder) holder).mSelectUserCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        Log.d(ContactsListFragment.class.getSimpleName(), "onCheckedChanged called.. " + b);
                        ContactsList contactsList = (ContactsList) mConversationList.get(holder.getAdapterPosition());
                        contactsList.setContactSelected(b);
                        mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                    }
                });

                ((ItemViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isNewGroupsPage){
                            ((CheckBox)view.findViewById(R.id.selectUserCheckBox)).setChecked(
                                    !((CheckBox)view.findViewById(R.id.selectUserCheckBox)).isChecked());
                        } else {
                            mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                        }

                    }
                });

                break;

            case GROUPS_PARTICIPANTS_LIST:

                User user = (User) mConversationList.get(position);

                RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) ((ItemViewHolder) holder).
                        userImage.getLayoutParams();
                imageLayoutParams.width = (int)mContext.getResources().getDimension(R.dimen.group_detail_image_height_width);
                imageLayoutParams.height = (int)mContext.getResources().getDimension(R.dimen.group_detail_image_height_width);

                ((ItemViewHolder) holder).userImage.setLayoutParams(imageLayoutParams);

                if(user.getProfileImageUrl() != null ) {
                    Picasso.with(mContext)
                            .load(user.getProfileImageUrl())
                            .placeholder(R.drawable.person_image_empty)
                            .into(((ItemViewHolder) holder).userImage);
                } else {
                    ((ItemViewHolder) holder).userImage.setImageResource(R.drawable.person_image_empty);
                }

                RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) ((ItemViewHolder) holder).
                        mUserTitle.getLayoutParams();

                titleLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                ((ItemViewHolder) holder).mUserTitle.setLayoutParams(titleLayoutParams);

                if(user.getUid().equals(GroupDetails.groupAdmin)){
                    ((ItemViewHolder) holder).mUserTitle.setText(String.format("%s (%s)", user.getName(),
                            mContext.getResources().getString(R.string.admin_text)));
                } else {
                    ((ItemViewHolder) holder).mUserTitle.setText(user.getName());
                }

                ((ItemViewHolder) holder).optionIcon.setVisibility(View.VISIBLE);

                ((ItemViewHolder) holder).optionIcon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        showMenusPopUp(view, holder.getAdapterPosition());
                    }
                });

                break;


            case GROUPS_LIST:
                groupsList = (GroupsList) mConversationList.get(position);


                int imageDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.conversation_user_image_width);

                if(groupsList.getProfileImageUrl() != null && !groupsList.getProfileImageUrl().isEmpty()){
                    Picasso.with(mContext)
                            .load(groupsList.getProfileImageUrl())
                            .placeholder(R.drawable.group_default_image)
                            .resize(imageDimen, imageDimen)
                            .centerCrop()
                            .into(((ItemViewHolder) holder).mGroupImage);
                } else {
                    ((ItemViewHolder) holder).mGroupImage.setImageResource(R.drawable.group_default_image);
                }

                ((ItemViewHolder) holder).mGroupTitle.setText(groupsList.getTitle());
                ((ItemViewHolder) holder).mLastActiveTime.setText(
                        String.format(mContext.getResources().
                                        getString(R.string.active_time_text),
                                mContext.getResources().getString(R.string.active), groupsList.getmLastActiveTime()
                        ));

                if(groupsList.getMemberCount() != 0){
                    ((ItemViewHolder) holder).mGroupMembers.setText(String.format(mContext.getResources().
                            getString(R.string.members_count_text), groupsList.getMemberCount()));
                }

                // todo code to show group members
//                if(groupsList.getParticipants() != null && groupsList.getParticipants().size() != 0){
//
//                    String groupMembers = "";
//                    int i = 0;
//
//                    for (String key : groupsList.getParticipants().keySet()) {
//
//                        if(i < groupsList.getParticipants().size() - 1){
//                            groupMembers += groupsList.getParticipants().get(key) + ", ";
//                        } else {
//                            groupMembers += groupsList.getParticipants().get(key);
//                        }
//                        i++;
//                    }
//                    ((ItemViewHolder) holder).mGroupMembers.setText(groupMembers);
//                }

                ((ItemViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                    }
                });

                break;

            case VIEW_PROG:
                ((ProgressViewHolder)holder).progressBar.setVisibility(View.VISIBLE);
                ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
                break;

        }
    }

    public void showMenusPopUp(final View v, final int position) {

        final PopupMenu popup = new PopupMenu(mContext, v);
        int i = 0;
        final User user = (User) mConversationList.get(position);
        if(user.getUid().equals(selfUid)){
            popup.getMenu().add(Menu.NONE, i, Menu.NONE, mContext.getResources().getString(R.string.leave_group));
        } else {
            if(!user.isDeleted()){
                popup.getMenu().add(Menu.NONE, ++i, Menu.NONE, mContext.getResources().
                        getString(R.string.new_message));
                popup.getMenu().add(Menu.NONE, ++i, Menu.NONE, mContext.getResources().
                        getString(R.string.view_profile_text));
            }
            if(selfUid.equals(GroupDetails.groupAdmin)){
                popup.getMenu().add(Menu.NONE, ++i, Menu.NONE, mContext.getResources().
                        getString(R.string.remove_user));
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                String creatorId = user.getUid();

                if(mOnMenuItemSelected != null){
                    mOnMenuItemSelected.onMenuItemSelected(id, creatorId);
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        // Fields for Recent Chats and Contacts List
        public CircularImageView userImage;
        public TextView mUserTitle, mChatMessage, mConversationTime, onlineTextView, headerTextView, mTypingText;
        public CheckBox mSelectUserCheckBox;

        // Fields for Groups List
        public CircularImageView mGroupImage;
        public TextView mGroupTitle, mLastActiveTime, mGroupMembers;

        public View container;

        public BadgeLayout badgeView;

        ImageView optionIcon, statusImageView;

        public ItemViewHolder(View view, Context context) {
            super(view);

            container = view;
            userImage = (CircularImageView) view.findViewById(R.id.user_image);
            mUserTitle = (TextView) view.findViewById(R.id.user_name);
            mChatMessage = (TextView) view.findViewById(R.id.chatMessage);
            mTypingText = (TextView) view.findViewById(R.id.typingText);
            mConversationTime = (TextView) view.findViewById(R.id.conversationTime);
            onlineTextView = (TextView) view.findViewById(R.id.online_icon);
            headerTextView = (TextView) view.findViewById(R.id.header_text);
            mSelectUserCheckBox = (CheckBox) view.findViewById(R.id.selectUserCheckBox);

            mGroupImage = (CircularImageView)view.findViewById(R.id.group_image);
            mGroupTitle = (TextView) view.findViewById(R.id.group_title);
            mLastActiveTime = (TextView) view.findViewById(R.id.activeTime);
            mGroupMembers = (TextView) view.findViewById(R.id.groupMembers);

            badgeView = (BadgeLayout) view.findViewById(R.id.unReadMessageCount);

            optionIcon = (ImageView) view.findViewById(R.id.optionIcon);
            statusImageView = (ImageView) view.findViewById(R.id.status);

            if(onlineTextView != null){
                onlineTextView.setTypeface(Typeface.createFromAsset(context.getAssets(),
                        "fontIcons/fontawesome-webfont.ttf"));
            }

            if(mChatMessage != null) {
                mChatMessage.setTypeface(Typeface.createFromAsset(context.getAssets(),
                        "fontIcons/fontawesome-webfont.ttf"));
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
