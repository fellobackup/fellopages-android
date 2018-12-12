package com.socialengineaddons.messenger.adapter;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.contacts.data_model.ContactsList;
import com.socialengineaddons.messenger.conversation.data_model.Conversation;
import com.socialengineaddons.messenger.conversation.view.BadgeLayout;
import com.socialengineaddons.messenger.groups.GroupDetails;
import com.socialengineaddons.messenger.groups.data_model.GroupsList;
import com.socialengineaddons.messenger.interfaces.OnItemClickListener;
import com.socialengineaddons.messenger.interfaces.OnLongClickListener;
import com.socialengineaddons.messenger.interfaces.OnMenuItemSelected;
import com.socialengineaddons.messenger.ui.CircularImageView;
import com.socialengineaddons.messenger.user.User;
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
            ContactsList contactsList = (ContactsList) it.next();
            if (contactsList.getmUserTitle() != null
                    && !contactsList.getmUserTitle().toLowerCase().contains(text.toLowerCase())) {
                it.remove();
            } else if (contactsList.getmUserTitle() == null) {
                it.remove();
            }
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
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).cleanup();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ItemViewHolder itemViewHolder;
        switch (holder.getItemViewType()){

            // Load data on recent conversation page
            case RECENT_CONVERSATION_LIST:
                conversation = (Conversation) mConversationList.get(position);
                itemViewHolder = (ItemViewHolder) holder;

                /*
                    show the cell only if the chat is not deleted else set the params width and height to zero
                    in order to hide the cell
                  */
                if(!conversation.isChatDeleted()) {
                    itemViewHolder.showView();

                    if(conversation.getmUserImage() != null && !conversation.getmUserImage().isEmpty()){
                        itemViewHolder.userImage.hideText();
                        Picasso.with(mContext)
                                .load(conversation.getmUserImage())
                                .placeholder(R.drawable.person_image_empty)
                                .error(R.drawable.person_image_empty)
                                .resize(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_50dp),
                                        mContext.getResources().getDimensionPixelSize(R.dimen.dimen_50dp))
                                .centerCrop()
                                .into(itemViewHolder.userImage);
                    } else {
                        itemViewHolder.userImage.showText(conversation.getmUserTitle(), conversation.getProfileColor());
                    }

                    itemViewHolder.mUserTitle.setText(conversation.getmUserTitle());

                    // show the online status of user only in case of one-one chat else hide it for group chat
                    if (conversation.getTypeOfChat() == 0 && conversation.getVisibility() == 1
                            && conversation.getOnlineStatus() == 1) {
                        itemViewHolder.ivOnlineIcon.setVisibility(View.VISIBLE);
                    } else {
                        itemViewHolder.ivOnlineIcon.setVisibility(View.GONE);
                    }

                    /*
                        don't show message details like message body, time and status of message in case
                        if the chat has been cleared, only show title and image of the conversation in this case
                      */
                    if(conversation.getmMessageId().equals("false")) {
                        itemViewHolder.mConversationTime.setVisibility(View.GONE);
                        itemViewHolder.mChatMessage.setVisibility(View.GONE);
                        itemViewHolder.badgeView.setVisibility(View.GONE);
                        itemViewHolder.statusImageView.setVisibility(View.GONE);
                        itemViewHolder.setTitleInCenter(RelativeLayout.TRUE);

                    } else {
                        itemViewHolder.mConversationTime.setText(conversation.getLastUpdatedTime());
                        itemViewHolder.mConversationTime.setVisibility(View.VISIBLE);
                        if(conversation.getmChatMessage() != null) {
                            itemViewHolder.mChatMessage.setText(Html.fromHtml(Html.fromHtml(Html.fromHtml(
                                    conversation.getmChatMessage()).toString()).toString()));
                        }
                        itemViewHolder.setTitleInCenter(0);

                        /*
                            show typing indicator only for those users which are not blocked and had not left the group
                            i.e. the users which are currently active in the conversation
                          */
                        if(conversation.isTyping() && !conversation.isDuplicateChatExist()) {
                            itemViewHolder.mTypingText.setText(mContext.getResources().
                                    getString(R.string.typing));
                            itemViewHolder.mTypingText.setVisibility(View.VISIBLE);
                            itemViewHolder.mChatMessage.setVisibility(View.GONE);
                        } else {
                            itemViewHolder.mTypingText.setVisibility(View.GONE);
                            itemViewHolder.mChatMessage.setVisibility(View.VISIBLE);
                        }

                        RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) itemViewHolder.
                                mChatMessage.getLayoutParams();

                        // show unRead message counter
                        if(conversation.getUnreadMessageCount() != 0){
                            itemViewHolder.badgeView.setVisibility(View.VISIBLE);
                            itemViewHolder.badgeView.setText(String.valueOf(conversation.getUnreadMessageCount()));
                            itemViewHolder.mConversationTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

                            messageLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.unReadMessageCount);
                            itemViewHolder.mChatMessage.setLayoutParams(messageLayoutParams);
                        } else {
                            itemViewHolder.badgeView.setVisibility(View.GONE);
                            itemViewHolder.mConversationTime.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text_color));

                            messageLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.status);
                            itemViewHolder.mChatMessage.setLayoutParams(messageLayoutParams);
                        }

                        // show last message status to display whether it is sent, delivered or read
                        if(conversation.getLastMessageStatus() != 0){

                            itemViewHolder.statusImageView.setVisibility(View.VISIBLE);
                            switch (conversation.getLastMessageStatus()){

                                case 1:
                                    itemViewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_sent_18dp));
                                    itemViewHolder.statusImageView.setColorFilter(ContextCompat.
                                            getColor(mContext, R.color.grey));
                                    break;

                                case 2:
                                    itemViewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_delivered_15dp));
                                    itemViewHolder.statusImageView.setColorFilter(ContextCompat.
                                            getColor(mContext, R.color.grey));
                                    break;

                                case 3:
                                    itemViewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_status_delivered_15dp));
                                    itemViewHolder.statusImageView.setColorFilter(ContextCompat.
                                            getColor(mContext, R.color.colorPrimary));
                                    break;
                            }
                        } else {
                            itemViewHolder.statusImageView.setVisibility(View.GONE);
                        }
                    }

                    itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                        }
                    });

                    itemViewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnLongClickListener.onLongClick(holder.getAdapterPosition());
                            return false;
                        }
                    });
                } else {
                    // set layout params widt and height to zero to hide the cell in case of deleted chat
                    itemViewHolder.hideView();
                }

                break;

            // to show contacts on contacts screen/new conversation creation page
            case CONTACT_LIST_WITH_OFFLINE_HEADER:
            case CONTACT_LIST_WITH_ONLINE_HEADER:
            case CONTACT_LIST_WITHOUT_HEADER:
                contactsList = (ContactsList) mConversationList.get(position);
                itemViewHolder = (ItemViewHolder) holder;

                if(holder.getItemViewType() == CONTACT_LIST_WITH_ONLINE_HEADER){
                    itemViewHolder.headerTextView.setText(mContext.getResources().
                            getString(R.string.online));
                } else if(holder.getItemViewType() == CONTACT_LIST_WITH_OFFLINE_HEADER){
                    itemViewHolder.headerTextView.setText(mContext.getResources().
                            getString(R.string.offline));
                }

                if(contactsList.getmUserImage() != null && !contactsList.getmUserImage().isEmpty()){
                    itemViewHolder.userImage.hideText();
                    Picasso.with(mContext)
                            .load(contactsList.getmUserImage())
                            .placeholder(R.drawable.person_image_empty)
                            .resize(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_50dp),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_50dp))
                            .centerCrop()
                            .into(itemViewHolder.userImage);
                } else {
                    itemViewHolder.userImage.showText(contactsList.getmUserTitle(), contactsList.getProfileColor());
                }

                itemViewHolder.setTitleInCenter(RelativeLayout.TRUE);
                itemViewHolder.mUserTitle.setText(contactsList.getmUserTitle());

                itemViewHolder.mChatMessage.setVisibility(View.GONE);
                itemViewHolder.mConversationTime.setVisibility(View.GONE);

                if (contactsList.getmOnlineStatus() == 1) {
                    itemViewHolder.ivOnlineIcon.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.ivOnlineIcon.setVisibility(View.GONE);
                }

                if(isNewGroupsPage) {
                    itemViewHolder.mSelectUserCheckBox.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.mSelectUserCheckBox.setVisibility(View.GONE);
                }

                itemViewHolder.mSelectUserCheckBox.setChecked(contactsList.isContactSelected());

                // select the user when checkbox is clicked for any contact on new conversation creation page
                itemViewHolder.mSelectUserCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        ContactsList contactsList = (ContactsList) mConversationList.get(holder.getAdapterPosition());
                        contactsList.setContactSelected(b);
                        mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                    }
                });

                itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
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

            // to show participants of a group on group details page
            case GROUPS_PARTICIPANTS_LIST:

                User user = (User) mConversationList.get(position);
                itemViewHolder = (ItemViewHolder) holder;

                if(user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    itemViewHolder.userImage.hideText();
                    Picasso.with(mContext)
                            .load(user.getProfileImageUrl())
                            .placeholder(R.drawable.person_image_empty)
                            .resize(mContext.getResources().getDimensionPixelSize(R.dimen.group_detail_image_height_width),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.group_detail_image_height_width))
                            .centerCrop()
                            .into(itemViewHolder.userImage);
                } else {
                    String userTitle;
                    if(user.getUid().equals(GroupDetails.groupAdmin)){
                        userTitle = String.format("%s (%s)", user.getName(),
                                mContext.getResources().getString(R.string.admin_text));
                    } else {
                        userTitle = user.getName();
                    }
                    itemViewHolder.userImage.showText(userTitle, user.getProfileColor());
                }

                itemViewHolder.setTitleInCenter(RelativeLayout.TRUE);

                if(user.getUid().equals(GroupDetails.groupAdmin)){
                    itemViewHolder.mUserTitle.setText(String.format("%s (%s)", user.getName(),
                            mContext.getResources().getString(R.string.admin_text)));
                } else {
                    itemViewHolder.mUserTitle.setText(user.getName());
                }

                itemViewHolder.optionIcon.setVisibility(View.VISIBLE);

                itemViewHolder.optionIcon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        showMenusPopUp(view, holder.getAdapterPosition());
                    }
                });

                break;


            // to show groups list on groups listing page
            case GROUPS_LIST:
                groupsList = (GroupsList) mConversationList.get(position);
                itemViewHolder = (ItemViewHolder) holder;

                if(groupsList.getProfileImageUrl() != null && !groupsList.getProfileImageUrl().isEmpty()){
                    itemViewHolder.mGroupImage.hideText();
                    Picasso.with(mContext)
                            .load(groupsList.getProfileImageUrl())
                            .placeholder(R.drawable.group_default_image)
                            .resize(mContext.getResources().getDimensionPixelSize(R.dimen.conversation_user_image_width),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.conversation_user_image_width))
                            .centerCrop()
                            .into(itemViewHolder.mGroupImage);
                } else {
                    itemViewHolder.mGroupImage.showText(groupsList.getTitle(), groupsList.getProfileColor());
                }

                itemViewHolder.mGroupTitle.setText(groupsList.getTitle());
                itemViewHolder.mLastActiveTime.setText(
                        String.format(mContext.getResources().
                                        getString(R.string.active_time_text),
                                mContext.getResources().getString(R.string.active), groupsList.getmLastActiveTime()
                        ));

                if(groupsList.getMemberCount() != 0){
                    itemViewHolder.mGroupMembers.setText(String.format(mContext.getResources().
                            getString(R.string.members_count_text), groupsList.getMemberCount()));
                }

                itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Function to show Menus popup
     * @param v
     * @param position
     */
    private void showMenusPopUp(final View v, final int position) {

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
                        getString(R.string.view_profile));
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

                if(mOnMenuItemSelected != null){
                    mOnMenuItemSelected.onMenuItemSelected(id, user);
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

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        // Fields for Recent Chats and Contacts List
        CircularImageView userImage, ivOnlineIcon;
        TextView mUserTitle, mChatMessage, mConversationTime, headerTextView, mTypingText;
        CheckBox mSelectUserCheckBox;

        // Fields for Groups List
        private CircularImageView mGroupImage;
        TextView mGroupTitle, mLastActiveTime, mGroupMembers;

        public View container;

        BadgeLayout badgeView;

        ImageView optionIcon, statusImageView;
        RecyclerView.LayoutParams layoutParams;
        RelativeLayout.LayoutParams userParams;

        ItemViewHolder(View view, Context context) {
            super(view);

            container = view;
            layoutParams = (RecyclerView.LayoutParams) container.getLayoutParams();
            userImage = (CircularImageView) view.findViewById(R.id.user_image);
            ivOnlineIcon = (CircularImageView) view.findViewById(R.id.online_icon);
            mUserTitle = (TextView) view.findViewById(R.id.user_name);
            mChatMessage = (TextView) view.findViewById(R.id.chatMessage);
            mTypingText = (TextView) view.findViewById(R.id.typingText);
            mConversationTime = (TextView) view.findViewById(R.id.conversationTime);
            headerTextView = (TextView) view.findViewById(R.id.header_text);
            mSelectUserCheckBox = (CheckBox) view.findViewById(R.id.selectUserCheckBox);

            mGroupImage = (CircularImageView)view.findViewById(R.id.group_image);
            mGroupTitle = (TextView) view.findViewById(R.id.group_title);
            mLastActiveTime = (TextView) view.findViewById(R.id.activeTime);
            mGroupMembers = (TextView) view.findViewById(R.id.groupMembers);

            badgeView = (BadgeLayout) view.findViewById(R.id.unReadMessageCount);

            optionIcon = (ImageView) view.findViewById(R.id.optionIcon);
            statusImageView = (ImageView) view.findViewById(R.id.status);

            if (mUserTitle != null) {
                userParams = (RelativeLayout.LayoutParams) mUserTitle.getLayoutParams();
            }

            if(mChatMessage != null) {
                mChatMessage.setTypeface(Typeface.createFromAsset(context.getAssets(),
                        "fontIcons/fontawesome-webfont.ttf"));
            }

            if (ivOnlineIcon != null) {
                ivOnlineIcon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.light_green),
                        PorterDuff.Mode.SRC_ATOP));
            }
        }

        private void showView() {
            layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            layoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT;
            container.setLayoutParams(layoutParams);
            container.setVisibility(View.VISIBLE);
        }

        private void hideView() {
            layoutParams.height = 0;
            layoutParams.width = 0;
            container.setLayoutParams(layoutParams);
            container.setVisibility(View.GONE);
        }

        /**
         * Method to set center vertical param of user title.
         * @param centerValue Value of center vertival.
         */
        private void setTitleInCenter(int centerValue) {
            userParams.addRule(RelativeLayout.CENTER_VERTICAL, centerValue);
            mUserTitle.setLayoutParams(userParams);
        }

        /**
         * Method to cleanup the image.
         */
        private void cleanup() {
            if (userImage != null) {
                Picasso.with(userImage.getContext())
                        .cancelRequest(userImage);
                userImage.setImageDrawable(null);
            }

            if (mGroupImage != null) {
                Picasso.with(mGroupImage.getContext())
                        .cancelRequest(mGroupImage);
                mGroupImage.setImageDrawable(null);
            }
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    private static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}
