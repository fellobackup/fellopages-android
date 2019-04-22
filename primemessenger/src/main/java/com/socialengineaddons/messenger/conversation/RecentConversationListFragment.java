/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.socialengineaddons.messenger.conversation;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.himanshuvirmani.androidcache.CacheManager;
import com.himanshuvirmani.androidcache.Result;
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.Utils;
import com.socialengineaddons.messenger.adapter.RecyclerViewAdapter;
import com.socialengineaddons.messenger.conversation.data_model.Conversation;
import com.socialengineaddons.messenger.interfaces.OnItemClickListener;
import com.socialengineaddons.messenger.interfaces.OnLongClickListener;
import com.socialengineaddons.messenger.user.User;
import com.socialengineaddons.messenger.utils.GlobalFunctionsUtil;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentConversationListFragment extends Fragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private List<Object> mConversationList;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mContext;
    private DatabaseReference chatRoomsDb, chatMessagesDb, userDb, chatRoomMembersDb, duplicateChatRoomsDb, blockedListDb;

    private String chatRoomId, messageId, toId, lastMessage, conversationTitle, conversationImage;
    private int typeOfChat;
    private long timeStamp;

    private ProgressBar progressBar;
    private String selfUid, pushNotificationClickedChatId = "";
    private String typeOfLastMessage;
    final int[] i = {0};
    private int counter = 0, chatsCount;
    private MessengerDatabaseUtils messengerDatabaseUtils;
    private HashMap<DatabaseReference, ChildEventListener> mListenerMap = new HashMap<>();
    private HashMap<String, HashMap<DatabaseReference, ChildEventListener>> mStatusListeners = new HashMap<>();
    private HashMap<DatabaseReference, ChildEventListener> mTypingListeners = new HashMap<>();
    private boolean isScrolling = false, isDataReceived = false, isLoading = false;
    private CacheManager cacheManager;
    private boolean isChatExist = false;
    private int chatCounter = 0;


    public RecentConversationListFragment() {
        // Required empty public constructor
    }

    public static RecentConversationListFragment newInstance(Bundle bundle) {
        // Required  public constructor
        RecentConversationListFragment fragment = new RecentConversationListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mContext = getActivity();
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        cacheManager = messengerDatabaseUtils.getCacheManagerInstance();
        rootView = inflater.inflate(R.layout.recycler_view, null);
        mConversationList = new ArrayList<>();
        if (getArguments() != null && getArguments().containsKey("chatKey")) {
            pushNotificationClickedChatId = getArguments().getString("chatKey");
        }

        initViews();
        initDatabaseRef();
        getRecentConversations();

        mRecyclerViewAdapter = new RecyclerViewAdapter(mContext, mConversationList, selfUid, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openConversationActivity(position);
            }
        }, new OnLongClickListener() {
            @Override
            public void onLongClick(int position) {
                showOptionsDialogue(position);
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return rootView;
    }

    /**
     * Function to initialize the views of this fragment
     */
    private void initViews() {

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(true);
        // Disable Animation on Item change.
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        int marginBottom = (int) (getResources().getDimension(R.dimen.margin_50dp));

        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 1);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 0, 0, marginBottom);
        mRecyclerView.setLayoutParams(marginLayoutParams);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        isScrolling = false;
                        // reloading data after scrolling done
                        if (isDataReceived) {
                            reloadData();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        isScrolling = true;
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        isScrolling = false;
                        // reload Data after scrolling done
                        if (isDataReceived) {
                            reloadData();
                        }
                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // todo to implement paginaton on recent page.
//                final LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) mRecyclerView
//                        .getLayoutManager();
//                int lastVisibleItemPosition = linearLayoutManager1.findLastVisibleItemPosition() ;
//                if(Constants.RECENT_PAGE_LIMIT == chatsCount && lastVisibleItemPosition == mConversationList.size() - 1 && !isLoading) {
//                    isLoading = true;
//                    loadMoreChats();
//                }
            }
        });
    }

    /**
     * Function to initialize the database references that are being used in this fragment
     */
    private void initDatabaseRef() {

        chatRoomsDb = messengerDatabaseUtils.getChatsDatabase();
        chatRoomMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        chatMessagesDb = messengerDatabaseUtils.getChatMessagesDatabase();
        userDb = messengerDatabaseUtils.getUsersDatabase();
        duplicateChatRoomsDb = messengerDatabaseUtils.getDuplicateChatsDatabase();
        blockedListDb = messengerDatabaseUtils.getBlockedListDatabase();

        selfUid = messengerDatabaseUtils.getCurrentUserId();
    }

    /**
     * Function to open conversation page
     * when a chat will be clicked from recent chats screen
     * @param position clicked position of chats
     */
    private void openConversationActivity(int position) {

        Conversation conversation = (Conversation) mConversationList.get(position);
        String chatRoomId = conversation.getmConversationId();
        Intent intent = new Intent(mContext, ConversationActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("typeOfChat", conversation.getTypeOfChat());
        intent.putExtra(Constants.SENDER, selfUid);
        intent.putExtra(Constants.DESTINATION, conversation.getToId());
        intent.putExtra(Constants.PROFILE_COLOR, conversation.getProfileColor());

        startActivity(intent);
    }

    /**
     * Function to open options dialogue which is having Delete/Clear/Block chat options
     * which will appear on Long press on any conversation
     * @param position position of the cell/item clicking on which we need to open the dialogue
     */
    private void showOptionsDialogue(int position) {

        final Conversation conversation = (Conversation) mConversationList.get(position);

        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_chat_options_dialog, null);
        TextView tvDelete = (TextView) view.findViewById(R.id.delete_conversation);
        TextView tvClear = (TextView) view.findViewById(R.id.clear_conversation);
        TextView tvBlock = (TextView) view.findViewById(R.id.block_conversation);

        if(conversation.isDuplicateChatExist()) {
            tvBlock.setText(mContext.getResources().getString(R.string.unblock));
        } else {
            tvBlock.setText(mContext.getResources().getString(R.string.block));
        }
        // hide block option in case of group chat
        if(conversation.getTypeOfChat() == 1) {
            tvBlock.setVisibility(View.GONE);
            int paddingBottom = mContext.getResources().getDimensionPixelOffset(R.dimen.padding_20dp);
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.padding_5dp);
            tvClear.setPadding(0, padding, 0, paddingBottom);


        } else {
            tvBlock.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext, R.style.Theme_Dialog);
        alertBuilder.setView(view);
        final AlertDialog dialog = alertBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = getDisplayMetricsWidth(mContext) - 170;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        /**
         * Adding click listener on Delete chat Button, which will delete the current logged-in user entry from messages
           recipient and will remove the cell from recent screen page.
         */

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GlobalFunctionsUtil.deleteConversation(mContext, conversation.getmConversationId(), chatMessagesDb, selfUid,
                        chatRoomMembersDb, conversation.getTypeOfChat());
            }
        });

        /**
         * Adding click listener on Clear chat Button, which will delete the current logged-in user entry from messages
           recipient, the cell will be still on the same position on recent screen but message details will not be shown.
         */
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GlobalFunctionsUtil.clearConversation(mContext, conversation.getmConversationId(), chatMessagesDb, selfUid,
                        chatRoomMembersDb, conversation.getTypeOfChat());
            }
        });

        /**
         * Adding Click Listener on Block button, this will be shown for one-one chat only, the user will be blocked
         * on clickin on this button.
         */
        tvBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                blockUnblockUser(conversation.getmConversationId(), conversation.getToId(), conversation.isDuplicateChatExist());
            }
        });

    }

    /**
     * Method to get the screen width pixels.
     *
     * @param context Context of calling class.
     * @return returns the screen width pixel.
     **/
    public static int getDisplayMetricsWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Function to get the recent conversation of the logged-in user
     * it fetches all chats from users/selfUid/chats reference
     * Then it'll fetch conversation details of all chats from chats>conversationId table
     * Then it'll  fetch members details for all chats from members > conversationId > selfUid
     * Then it'll  fetch the last message details from messages table at reference messages > convId > lastMessageId
     * Then it'll fetch the user details of a particular conversation and that conversation is added in list of all conversations
     * todo This function needs to be replaced with Mqtt client api function to get recent chats.
     */
    private void getRecentConversations() {

        userDb.child(selfUid).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    mConversationList.clear();
                    counter = 0;
                    i[0] = 0;
                    chatsCount = (int) dataSnapshot.getChildrenCount();

                    for (final DataSnapshot chatRoomId : dataSnapshot.getChildren()) {

                        Log.d(RecentConversationListFragment.class.getSimpleName(), "chatRoomId: " + chatRoomId.getKey());

                        chatRoomsDb.child(chatRoomId.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot chatRoom) {
                                getChatMembers(chatRoom);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                } else {
                    showErrorMessage();
                    setObservers();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Function to display a view to show a message if no chats are yet done by the user
     */
    private void showErrorMessage() {
        progressBar.setVisibility(View.GONE);

        rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
        ImageView errorIcon = (ImageView) rootView.findViewById(R.id.error_icon);
        errorIcon.setImageResource(R.drawable.ic_recent);
        TextView errorMessage = (TextView) rootView.findViewById(R.id.error_message);
        errorMessage.setText(mContext.getResources().getString(R.string.no_recent_chats));
    }

    /**
     * This function is to fetch the members details of a chat
     * @param chatRoom snapshot of chat details from chats table
     * todo this function needs to be removed when mqtt api will be implemented here
     */
    private void getChatMembers(final DataSnapshot chatRoom) {

        final Conversation[] recentConversation = new Conversation[1];
        final List <String> mPartipantsList = new ArrayList<>();

        chatRoomMembersDb.child(chatRoom.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                chatCounter++;

                for(DataSnapshot member : dataSnapshot.getChildren())  {
                    if(member.child("isActive").getValue(String.class) != null
                            && member.child("isActive").getValue(String.class).equals("1")
                            && !member.getKey().equals(selfUid)) {
                        mPartipantsList.add(member.getKey());
                    }
                }

                if (dataSnapshot.child(selfUid).hasChild("lastMessageId") && !dataSnapshot.child(selfUid).child("lastMessageId").getValue(String.class).isEmpty()) {
                    toId = null;
                    isChatExist = true;

                    if (dataSnapshot.child(selfUid).hasChild("duplicateChatId")) {

                        String duplicateChatRoomId = dataSnapshot.child(selfUid).child("duplicateChatId").getValue(String.class);

                        duplicateChatRoomsDb.child(duplicateChatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot chatDataSnapshot) {

                                chatRoomId = chatRoom.getKey();
                                typeOfChat = chatRoom.child("isGroup").getValue(String.class).equals("1") ? 1 : 0;

                                toId = getToIdOfChat(typeOfChat, chatRoom);

                                messageId = dataSnapshot.child(selfUid).hasChild("lastMessageId") ?
                                        dataSnapshot.child(selfUid).child("lastMessageId").getValue(String.class) :
                                        chatDataSnapshot.child("lastMessageId").getValue(String.class);

                                conversationTitle = chatDataSnapshot.child("title").getValue(String.class);
                                conversationImage = chatDataSnapshot.child(Constants.FIREBASE_IMAGE_KEY).getValue(String.class);

                                timeStamp = dataSnapshot.child(selfUid).hasChild("updatedAt") ? dataSnapshot.child(selfUid).child("updatedAt").getValue(Long.class) :
                                        chatDataSnapshot.child("updatedAt").getValue(Long.class);

                                recentConversation[0] = new Conversation(chatRoomId, messageId, timeStamp, toId,
                                        typeOfChat, conversationTitle, conversationImage,
                                        getTimeStampString(timeStamp), true, mPartipantsList);

                                if (dataSnapshot.child(selfUid).hasChild("deleted") && dataSnapshot.child(selfUid).child("deleted").getValue(String.class).equals("1")) {
                                    recentConversation[0].setChatDeleted(true);
                                } else {
                                    recentConversation[0].setChatDeleted(false);
                                }

                                if (!pushNotificationClickedChatId.equals(chatRoomId)
                                        && dataSnapshot.child(selfUid).hasChild("newMessageCount")) {
                                    int unReadMessageCount = dataSnapshot.child(selfUid).child("newMessageCount").
                                            getValue(Integer.class);
                                    recentConversation[0].setUnreadMessageCount(unReadMessageCount);
                                } else {
                                    recentConversation[0].setUnreadMessageCount(0);
                                }

                                fetchChatMessageDetails(recentConversation[0], messageId);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        messageId = dataSnapshot.child(selfUid).child("lastMessageId").getValue(String.class);
                        addConversationInList(dataSnapshot, chatRoom, messageId, dataSnapshot.child(selfUid).child("updatedAt").
                                getValue(Long.class), mPartipantsList);
                    }
                }

                /* If all chats has been fetched from firebase database and there are not chats available with messages
                    then show No chats available message to user.
                  */
                if(chatCounter == chatsCount && !isChatExist) {
                    showErrorMessage();
                    setObservers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Function to add conversation in the list of all conversations
     * @param dataSnapshot
     * @param chatRoom
     * @param messageId
     * @param lastMsgTimeStamp
     * @param participants
     */
    private void addConversationInList(DataSnapshot dataSnapshot, DataSnapshot chatRoom,
                                       String messageId, Long lastMsgTimeStamp, List<String> participants) {

        chatRoomId = chatRoom.getKey();
        Log.d(RecentConversationListFragment.class.getSimpleName(), "chatRoomId: "+chatRoomId+", DataSnapshot chatRoom: "+chatRoom);
        typeOfChat = chatRoom.child("isGroup").getValue(String.class).equals("1") ? 1 : 0;

        toId = getToIdOfChat(typeOfChat, chatRoom);

        conversationTitle = chatRoom.child("title").getValue(String.class);
        conversationImage = chatRoom.child(Constants.FIREBASE_IMAGE_KEY).getValue(String.class);

        timeStamp = lastMsgTimeStamp;

        Conversation recentConversation = new Conversation(chatRoomId, messageId, timeStamp, toId,
                typeOfChat, conversationTitle, conversationImage, getTimeStampString(timeStamp), false, participants);

        if (!pushNotificationClickedChatId.equals(chatRoomId)
                && dataSnapshot.child(selfUid).hasChild("newMessageCount")) {
            int unReadMessageCount = dataSnapshot.child(selfUid).child("newMessageCount").
                    getValue(Integer.class);
            recentConversation.setUnreadMessageCount(unReadMessageCount);
        } else {
            recentConversation.setUnreadMessageCount(0);
        }

        Log.d(RecentConversationListFragment.class.getSimpleName(), "Conversation Id: " + chatRoomId);


        if (dataSnapshot.child(selfUid).hasChild("deleted") && dataSnapshot.child(selfUid).child("deleted").getValue(String.class).equals("1")) {
            recentConversation.setChatDeleted(true);
        } else {
            recentConversation.setChatDeleted(false);
        }

        fetchChatMessageDetails(recentConversation, messageId);
    }

    /**
     * Function to fetch last Message details of a chat
     * @param recentConversation Details of a conversation
     * @param lastMessageId Last Message Id of a conversation for which we need to fetch the details
     */
    private void fetchChatMessageDetails(Conversation recentConversation, String lastMessageId) {

        final Conversation conversation;

        if (mConversationList.contains(recentConversation)) {
            mConversationList.set(mConversationList.indexOf(recentConversation),
                    recentConversation);
            conversation = (Conversation) mConversationList.get(mConversationList.
                    indexOf(recentConversation));
        } else {
            recentConversation.setProfileColor();
            mConversationList.add(recentConversation);
            conversation = (Conversation) mConversationList.get(i[0]);
            i[0]++;
        }

        Log.d(RecentConversationListFragment.class.getSimpleName(), "Chat Id 2: " + recentConversation.getmConversationId());

        if (recentConversation.isChatDeleted() || lastMessageId.equals("false")) {
            getUserDetails(conversation);
        } else {
            chatMessagesDb.child(conversation.getmConversationId()).child(lastMessageId).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            typeOfLastMessage = dataSnapshot.child("attachmentType").getValue(String.class);

                            lastMessage = dataSnapshot.child("body").getValue(String.class);
                            String ownerId = dataSnapshot.child("ownerId").getValue(String.class);
                            DataSnapshot recipients = dataSnapshot.child("recipients");

                            conversation.setmMessageId(dataSnapshot.getKey());
                            conversation.setTypeOfLastMessage(typeOfLastMessage);
                            conversation.setmChatMessage(lastMessage);
                            conversation.setLastMessageCreatorId(ownerId);

                            if (ownerId != null && !ownerId.equals(selfUid) && recipients.hasChild(selfUid) && recipients.child(selfUid).child("status").
                                    getValue(String.class).equals("1")) {

                                chatMessagesDb.child(conversation.getmConversationId())
                                        .child(conversation.getmMessageId()).child("recipients").child(selfUid).
                                        child("status").setValue("2");
                            }

                            toId = (conversation.getTypeOfChat() == 1) ? dataSnapshot.child("ownerId").
                                    getValue(String.class) : conversation.getToId();
                            conversation.setToId(toId);

                            // store the status of last message If the last message is sent by the current logged-in user
                            if (ownerId != null && ownerId.equals(selfUid)) {
                                conversation.setLastMessageStatus(getStatusOfMessage(recipients));
                            } else {
                                conversation.setLastMessageStatus(0);
                            }

                            getUserDetails(conversation);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    /**
     * Function to fetch the user details of the conversation
     * in one-one to chat fetch the other user details with which the conversation is done
     * in Group chat, if the last message done by some other user than the current logged-in user then fetch that user's details.
     * @param conversation
     */
    private void getUserDetails(final Conversation conversation) {

        if (conversation.getToId() != null) {

            Result<User> user = cacheManager.get(conversation.getToId(), User.class);
            if (user.getCachedObject() == null) {

                userDb.child(conversation.getToId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        User user;
                        if (dataSnapshot.exists()) {
                            user = dataSnapshot.getValue(User.class);
                        } else {
                            user = new User(mContext.getResources().getString(R.string.deleted_user),
                                    null, 0, 0L, 0);
                        }

                        cacheManager.put(conversation.getToId(), user);
                        addUserDetailsInConversation(user, conversation);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Log.d(RecentConversationListFragment.class.getSimpleName(), "Object Exists: " + conversation.getToId());
                User userDetails = user.getCachedObject();
                addUserDetailsInConversation(userDetails, conversation);
            }
        } else {
            addUserDetailsInConversation(null, conversation);
        }
    }

    /**
     * Function to add the user details in the list of all conversations.
     * @param user Details of the user
     * @param conversation Details of the conversation
     */
    private void addUserDetailsInConversation(User user, Conversation conversation) {

        if (conversation.getTypeOfChat() == 1) {

            conversation.setmUserTitle(conversation.getConversationTitle());
            conversation.setmUserImage(conversation.getConversationImage());
            if (conversation.getToId() != null && !conversation.getToId().equals(selfUid)) {
                conversation.setLastMessageCreator(user.getName());
            } else {
                conversation.setLastMessageCreator(null);
            }
        } else {
            conversation.setmUserTitle(user.getName());
            conversation.setmUserImage(user.getProfileImageUrl());
            conversation.setOnlineStatus(user.getIsOnline());
            conversation.setLastSeen(user.getLastSeen());
            conversation.setVisibility(user.getVisibility());
        }
        conversation.setmChatMessage(getChatMessage(conversation.getmChatMessage(),
                conversation.getLastMessageCreator(), conversation.getTypeOfLastMessage()));

        notifyAdapter();
    }

    /**
     * Function to notify adapter when all the data has been fetched from fireBase database
     */
    private void notifyAdapter() {

        if (counter < mConversationList.size()) {
            counter++;
        }

        // Sort the List  and notify adapter when all the chatRooms are iterated


        Log.d(RecentConversationListFragment.class.getSimpleName(), "Inside notifyAdapter function:  " +
                counter + "Size of List: " + mConversationList.size());

        if (counter == mConversationList.size()) {

            Log.d(RecentConversationListFragment.class.getSimpleName(), " Inside Condition 1");
            isLoading = false;
            reloadData();
            removeObservers();
            setObservers();
        }
    }

    /**
     * Function to notify a particular chat cell when any change occurs in that chat
     * @param indexOfItem index of the item which we need to update
     */
    private void notifyItemChange(int indexOfItem) {

        mRecyclerViewAdapter.notifyItemChanged(indexOfItem);
    }

    /**
     * Function to load Data in view and notify adapter
     * if the recycler view is scrolling and data has been changed at that time then the data is updated when
     * scrolling is stopped so as to avoid the freeze of application
     */
    private void reloadData() {

        if (isScrolling) {
            isDataReceived = true;
        } else {
            isDataReceived = false;
            Log.d(RecentConversationListFragment.class.getSimpleName(), " Inside Condition 2");
            sortListWithTimeStamp();
            mRecyclerViewAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            Log.d(RecentConversationListFragment.class.getSimpleName(), "Total ConversationList Size: "+mConversationList.size());
            pushNotificationClickedChatId = "";
        }
    }

    /**
     * Function to set observers on all chats
     */
    private void setObservers() {

        if (mConversationList != null && mConversationList.size() != 0) {

            for (int i = 0; i < mConversationList.size(); i++) {

                final Conversation conversation = (Conversation) mConversationList.get(i);
                if(!conversation.isDuplicateChatExist()) {
                   setTypingChangeObserver(conversation);
                }
                setConversationChangeObserver(conversation);
                setMessageStatusChangeObserver(conversation);
                setUserChangeObserver(conversation);
            }
        }

        /**
         * Observer to check any new chats and show them in recent chats list
         */
        DatabaseReference databaseReference = userDb.child(selfUid).child("chats").getRef();
        ChildEventListener childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversation conversation = new Conversation(dataSnapshot.getKey());
                if(!mConversationList.contains(conversation)){
                    setConversationAddObserver(conversation);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mListenerMap.put(databaseReference, childEventListener);

    }

    /**
     * Function to remove all obsevers added to the FireBase nodes
     */
    private void removeObservers() {


        if (mListenerMap != null && mListenerMap.size() != 0) {
            // Remove Listeners of messages whose status has been changed to 3
            for (Map.Entry<DatabaseReference, ChildEventListener> entry : mListenerMap.entrySet()) {
                DatabaseReference ref = entry.getKey();
                ChildEventListener listener = entry.getValue();
                ref.removeEventListener(listener);
            }
        }
    }

    /**
     * If last message of a chat has been changed then remove the listener attached to the previous last message
     * @param messageId previous last message id
     */
    private void removePreviousMessageChangeListener(String messageId) {

        if (mStatusListeners.containsKey(messageId)) {
            HashMap<DatabaseReference, ChildEventListener> listenerMap = mStatusListeners.get(messageId);
            for (Map.Entry<DatabaseReference, ChildEventListener> entry : listenerMap.entrySet()) {
                DatabaseReference ref = entry.getKey();
                ChildEventListener listener = entry.getValue();
                ref.removeEventListener(listener);
            }
        }
    }

    /**
     * Observer on typing indicator of all chats to show typing in recent chats
     * @param conversation Conversation on which we need to set typing observer
     */
    private void setTypingChangeObserver(final Conversation conversation) {

        List<String> participantsList = conversation.getParticipants();
        if(participantsList.size() != 0) {
            for( String participantKey : participantsList) {

                DatabaseReference databaseReference = chatRoomMembersDb.child(conversation.getmConversationId()).
                        child(participantKey).getRef();
                ChildEventListener childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getKey().equals(Constants.FIREBASE_CHAT_TYPING)) {
                            boolean isTyping = dataSnapshot.getValue(String.class).equals("1");
                            conversation.setTyping(isTyping);
                            notifyItemChange(mConversationList.indexOf(conversation));
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mListenerMap.put(databaseReference, childEventListener);
                mTypingListeners.put(databaseReference, childEventListener);
            }
        }
    }

    /**
     * Function to add a observer on members table if a message has been sent for the first time in any chat
     * @param conversation Conversation details on which we need to add the observer
     */
    private void setConversationAddObserver(final Conversation conversation) {

        DatabaseReference databaseReference = chatRoomMembersDb.child(conversation.getmConversationId()).
                child(selfUid).getRef();
        ChildEventListener childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().equals("lastMessageId")) {
                    hideErrorMessage();
                    chatRoomsDb.child(conversation.getmConversationId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            getChatMembers(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mListenerMap.put(databaseReference, childEventListener);
    }

    /**
     * Observers on members > conversationId > selfUid
     * this observer will get call in any case if newMessageCount has been added/changed/removed
     * will get call if duplicateChatId has been added/removed
     * will get call if lastMessageId has been changed i.e. a new message came in conversation
     * will get call when any conversation get deleted so as to hide that cell from recent chats
     * will get call when updatedAt will be changed so as to sort the list with new time
     * @param conversation
     */
    private void setConversationChangeObserver(final Conversation conversation) {

        DatabaseReference membersDatabaseReference = chatRoomMembersDb.child(conversation.getmConversationId()).child(selfUid).getRef();
        ChildEventListener membersChildEventListener = membersDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Show New Message Count here

                if(mConversationList.contains(conversation)) {
                    if (dataSnapshot.getKey().equals("newMessageCount")) {
                        conversation.setUnreadMessageCount(dataSnapshot.getValue(Integer.class));
                        notifyItemChange(mConversationList.indexOf(conversation));
                    } else if( dataSnapshot.getKey().equals("duplicateChatId")) {
                        conversation.setDuplicateChatExist(true);
                        notifyItemChange(mConversationList.indexOf(conversation));
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                Log.d(RecentConversationListFragment.class.getSimpleName(), "onChildChanged of chatRoomMembersDb called.." +
                        conversation.getmConversationId());

                switch (dataSnapshot.getKey()) {

                    case "lastMessageId":
                        Log.d(RecentConversationListFragment.class.getSimpleName(), "lastMessageId");
                        if (dataSnapshot.getValue(String.class).equals("false")) {
                            conversation.setmMessageId("false");
                            notifyItemChange(mConversationList.indexOf(conversation));
                        } else if (!conversation.isDuplicateChatExist()) {

                            Log.d(RecentConversationListFragment.class.getSimpleName(), "Inside chatRoomMembersDb lastMessageId : " +
                                    conversation.getmConversationId());

                            conversation.setChatDeleted(false);
                            removePreviousMessageChangeListener(conversation.getmMessageId());
                            fetchChatMessageDetails(conversation, dataSnapshot.getValue(String.class));
                        }
                        break;

                    case "deleted":
                        if (dataSnapshot.getValue(String.class).equals("1")) {
                            conversation.setChatDeleted(true);
                            notifyItemChange(mConversationList.indexOf(conversation));
                            reloadData();
                            removeObservers();
                            setObservers();
                        }
                        break;

                    case "updatedAt":
                        if(!conversation.isDuplicateChatExist()){
                            conversation.setChatDeleted(false);
                            conversation.setLastUpdated(dataSnapshot.getValue(Long.class));
                            conversation.setLastUpdatedTime(getTimeStampString(dataSnapshot.getValue(Long.class)));
                            sortListWithTimeStamp();
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        break;

                    // Show Updated Message Count here
                    case "newMessageCount":
                        if (mConversationList.contains(conversation)) {
                            conversation.setUnreadMessageCount(dataSnapshot.getValue(Integer.class));
                            notifyItemChange(mConversationList.indexOf(conversation));
                        }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(RecentConversationListFragment.class.getSimpleName(), "onChildRemoved : " + dataSnapshot.getKey());
                // Hide count ui if newMessageCount is removed
                if (mConversationList.contains(conversation)) {
                    if (dataSnapshot.getKey().equals("newMessageCount")) {
                        conversation.setUnreadMessageCount(0);
                        notifyItemChange(mConversationList.indexOf(conversation));

                    } else if (dataSnapshot.getKey().equals("duplicateChatId")) {
                        conversation.setDuplicateChatExist(false);
                        notifyItemChange(mConversationList.indexOf(conversation));
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Observer to check if title/profileImageUrl has been changed in case of group chats
         */
        DatabaseReference databaseReference = chatRoomsDb.child(conversation.getmConversationId()).getRef();
        ChildEventListener childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.d(RecentConversationListFragment.class.getSimpleName(), "onChildChanged of chatRoomsDb called.." +
                        conversation.getmConversationId());


                switch (dataSnapshot.getKey()) {

                    case "title":
                        if (conversation.getTypeOfChat() == 1) {
                            conversation.setmUserTitle(dataSnapshot.getValue(String.class));
                            notifyItemChange(mConversationList.indexOf(conversation));
                        }

                        break;

                    case "profileImageUrl":
                        if (conversation.getTypeOfChat() == 1) {
                            conversation.setmUserImage(dataSnapshot.getValue(String.class));
                            notifyItemChange(mConversationList.indexOf(conversation));
                        }
                        break;
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mListenerMap.put(databaseReference, childEventListener);
        mListenerMap.put(membersDatabaseReference, membersChildEventListener);
    }

    /**
     * Function to apply observer on last message sent by the current logged-in member in order to check
     * the status of that message, so we can update it. If that message is already read by other users of conversation
     * then no need to apply the observer
     * @param conversation
     */
    private void setMessageStatusChangeObserver(final Conversation conversation) {

        if (conversation.getLastMessageStatus() != 3) {
            HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();
            DatabaseReference messageRef = chatMessagesDb.child(conversation.getmConversationId()).
                    child(conversation.getmMessageId()).getRef();
            ChildEventListener listener = messageRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    if (conversation.getLastMessageCreatorId().equals(selfUid)) {
                        conversation.setLastMessageStatus(getStatusOfMessage(dataSnapshot));
                    } else {
                        conversation.setLastMessageStatus(0);
                    }

                    // notify adapter
                    if (mConversationList.contains(conversation)) {
                        notifyItemChange(mConversationList.indexOf(conversation));
                    }

                    // Remove Listeners of messages whose status has been changed to 3
                    if (conversation.getLastMessageStatus() == 3 && mStatusListeners.containsKey(conversation.getmMessageId())) {
                        HashMap<DatabaseReference, ChildEventListener> listenerMap = mStatusListeners.get(conversation.getmMessageId());
                        for (Map.Entry<DatabaseReference, ChildEventListener> entry : listenerMap.entrySet()) {
                            DatabaseReference ref = entry.getKey();
                            ChildEventListener listener = entry.getValue();
                            ref.removeEventListener(listener);
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            listenerMap.put(messageRef, listener);
            mStatusListeners.put(conversation.getmMessageId(), listenerMap);
            mListenerMap.put(messageRef, listener);

        }
    }

    /**
     * Function to apply observers on conversation user so as to update the title, image, online/offline
     * status of the user
     * @param conversation
     */
    private void setUserChangeObserver(final Conversation conversation) {

        if (conversation.getToId() != null) {

            DatabaseReference databaseReference = userDb.child(conversation.getToId()).getRef();
            ChildEventListener childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Result<User> user = cacheManager.get(conversation.getToId(), User.class);

                    switch (dataSnapshot.getKey()) {

                        case "name":
                            // If user exist in Cache, then update the user's name is cache object
                            if (user.getCachedObject() != null) {
                                User userDetails = user.getCachedObject();
                                userDetails.setName(dataSnapshot.getValue(String.class));
                                cacheManager.put(conversation.getToId(), userDetails);
                            }
                            if (conversation.getTypeOfChat() != 1) {
                                conversation.setmUserTitle(dataSnapshot.getValue(String.class));
                            } else if (conversation.getLastMessageCreator() != null) {
                                conversation.setmChatMessage(getChatMessage(conversation.getmChatMessage(),
                                        conversation.getLastMessageCreator(), conversation.getTypeOfLastMessage()));
                            }
                            notifyItemChange(mConversationList.indexOf(conversation));
                            break;

                        case "profileImageUrl":
                            // If user exist in Cache, then update the user's profileimage is cache object
                            if (user.getCachedObject() != null) {
                                User userDetails = user.getCachedObject();
                                userDetails.setProfileImageUrl(dataSnapshot.getValue(String.class));
                                cacheManager.put(conversation.getToId(), userDetails);
                            }

                            if (conversation.getTypeOfChat() != 1) {
                                conversation.setmUserImage(dataSnapshot.getValue(String.class));
                                notifyItemChange(mConversationList.indexOf(conversation));
                            }
                            break;

                        case "isOnline":
                            // If user exist in Cache, then update the user's isOnline is cache object
                            if (user.getCachedObject() != null) {
                                User userDetails = user.getCachedObject();
                                userDetails.setIsOnline(dataSnapshot.getValue(Integer.class));
                                cacheManager.put(conversation.getToId(), userDetails);
                            }
                            conversation.setOnlineStatus(dataSnapshot.getValue(Integer.class));
                            notifyItemChange(mConversationList.indexOf(conversation));
                            break;

                        case "visibility":
                            // If user exist in Cache, then update the user's visibility is cache object
                            if (user.getCachedObject() != null) {
                                User userDetails = user.getCachedObject();
                                userDetails.setVisibility(dataSnapshot.getValue(Integer.class));
                                cacheManager.put(conversation.getToId(), userDetails);
                            }

                            conversation.setVisibility(dataSnapshot.getValue(Integer.class));
                            notifyItemChange(mConversationList.indexOf(conversation));
                            break;

                        case "lastSeen":
                            // If user exist in Cache, then update the user's lastSeen is cache object
                            if (user.getCachedObject() != null) {
                                User userDetails = user.getCachedObject();
                                userDetails.setLastSeen(dataSnapshot.getValue(Long.class));
                                cacheManager.put(conversation.getToId(), userDetails);
                            }
                            conversation.setLastSeen(dataSnapshot.getValue(Long.class));
                            notifyItemChange(mConversationList.indexOf(conversation));
                            break;
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mListenerMap.put(databaseReference, childEventListener);
        }
    }

    /**
     * Prepare the message according to the attachment type of the message
     * @param message message body
     * @param lastMessageCreator if the last message creator is different then the current loggedin user then show user's name
     * @param typeOfLastMessage type of message
     * @return return the prepared message according to the message type
     */
    private String getChatMessage(String message, String lastMessageCreator, String typeOfLastMessage) {

        lastMessageCreator = lastMessageCreator != null ? lastMessageCreator.split("\\s+")[0] + ": " : "";

        if (typeOfLastMessage != null) {
            switch (typeOfLastMessage) {

                case "text":
                    // Text Message
                    return lastMessageCreator + message;

                case "image":
                    // Photo Message
                    return lastMessageCreator + "\uf030" + " " + mContext.getResources().
                            getString(R.string.image);

                case "video":
                    // Video Message
                    return lastMessageCreator + "\uf03d" + " " + mContext.getResources().
                            getString(R.string.video);

                case "audio":
                    // Video Message
                    return lastMessageCreator + "\uf001" + " " + mContext.getResources().
                            getString(R.string.audio);

                case "file":
                    // Video Message
                    return lastMessageCreator + "\uf15c" + " " + mContext.getResources().
                            getString(R.string.document);

                default:
                    return lastMessageCreator + "\uf15c" + " " + typeOfLastMessage;
            }
        } else {
            return message;
        }
    }

    /**
     * Function to check the status of message according to the status of it's recipients
     * @param recipients Recipients of the message
     * @return will return the calculated message status
     */
    private int getStatusOfMessage(DataSnapshot recipients) {

        int statusOfMessage = 1;
        int countOfDelivered = 0, countOfRead = 0;

        if (recipients.getChildrenCount() > 1) {
            for (DataSnapshot recipient : recipients.getChildren()) {
                if (!recipient.getKey().equals(selfUid)) {
                    if (recipient.child("status").getValue(String.class).equals("2")) {
                        countOfDelivered++;
                    } else if (recipient.child("status").getValue(String.class).equals("3")) {
                        countOfDelivered++;
                        countOfRead++;
                    }
                }
            }

            if (countOfRead == recipients.getChildrenCount() - 1) {
                statusOfMessage = 3;
            } else if (countOfDelivered == recipients.getChildrenCount() - 1) {
                statusOfMessage = 2;
            }
        } else {
            statusOfMessage = 1;
        }

        return statusOfMessage;

    }

    /**
     * Will return the toId (the other user id with which the conversation is done)
     * @param typeOfChat one to one chat or group chat
     * @param chatRoomSnapshot Details of conversation
     * @return
     */
    private String getToIdOfChat(int typeOfChat, DataSnapshot chatRoomSnapshot) {

        if (typeOfChat != 1) {
            for (DataSnapshot member : chatRoomSnapshot.child("participants").getChildren()) {
                if (!member.getKey().equals(selfUid)) {
                    return member.getKey();
                }
            }
        }
        return null;
    }

    private String getTimeStampString(Long timeStamp) {

        String time = Utils.getTimestamp(timeStamp, mContext);
        String date = Utils.getDateFromTimeStamp(timeStamp);
        String today = Utils.getCurrentTimestamp();

        String[] time1 = date.split("/");
        String[] time2 = today.split("/");
        if ((time1[0] + time1[1] + time1[2]).equals(time2[0] + time2[1] + time2[2])) {
            return time;
        } else {
            return date;
        }
    }

    /**
     * Function to sort the recent chat list according to updated time
     * in order to get the list in descending order of time
     */
    private void sortListWithTimeStamp() {

        Collections.sort(mConversationList, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                Conversation conversation1 = (Conversation) o1;
                Conversation conversation2 = (Conversation) o2;

                return Long.valueOf(conversation2.getLastUpdated()).compareTo(conversation1.getLastUpdated());
            }

        });
    }

    /**
     * Function to hide Error Message which is shown to display no chats
     */
    private void hideErrorMessage(){
        rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
    }

    /**
     * Function to block a particular user when block user option will be clicked.
     * to
     * @param conversationId Conversation id in which the user will be blocked
     * @param userId user to be blocked
     * @param isBlocked boolean to check if already blocked or not
     */
    private void blockUnblockUser(final String conversationId, final String userId, boolean isBlocked) {

        if(!isBlocked) {

            // Block the user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

            alertBuilder.setMessage(mContext.getResources().getString(R.string.block_user_alert));
            alertBuilder.setTitle(mContext.getResources().getString(R.string.block_user));

            alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.block), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    chatRoomMembersDb.child(conversationId).child(selfUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            Conversation conversation = new Conversation(conversationId);

                            String duplicateChatRoomId = duplicateChatRoomsDb.push().getKey();
                            HashMap<String, Object> duplicateChatRoomDetails = new HashMap<>();
                            duplicateChatRoomDetails.put("lastMessageId", dataSnapshot.child("lastMessageId").
                                    getValue(String.class));
                            duplicateChatRoomDetails.put("updatedAt", dataSnapshot.child("updatedAt").getValue(Long.class));

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(duplicateChatRoomId, duplicateChatRoomDetails);

                            duplicateChatRoomsDb.updateChildren(childUpdates);


                            HashMap<String, Object> chatMembersUpdates = new HashMap<>();
                            chatMembersUpdates.put("isActive", "0");
                            chatMembersUpdates.put("duplicateChatId", duplicateChatRoomId);

                            chatRoomMembersDb.child(conversationId).child(selfUid).updateChildren(chatMembersUpdates);

                            blockedListDb.child(selfUid).child(userId).setValue("true");

                            if(mConversationList.contains(conversation)) {
                                int index = mConversationList.indexOf(conversation);
                                conversation = (Conversation) mConversationList.get(index);
                                conversation.setDuplicateChatExist(true);
                                notifyItemChange(index);
                            }
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.
                                    user_blocked_message), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertBuilder.create().show();
        } else {
            chatRoomMembersDb.child(conversationId).child(selfUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild("duplicateChatId")) {
                        String duplicateChatId = dataSnapshot.child("duplicateChatId").getValue(String.class);
                        duplicateChatRoomsDb.child(duplicateChatId).removeValue();
                    }

                    HashMap<String, Object> chatMembersUpdates = new HashMap<>();
                    chatMembersUpdates.put("isActive", "1");
                    chatMembersUpdates.put("duplicateChatId", null);

                    chatRoomMembersDb.child(conversationId).child(selfUid).updateChildren(chatMembersUpdates);
                    blockedListDb.child(selfUid).child(userId).setValue(null);

                    Toast.makeText(mContext, mContext.getResources().getString(R.string.
                            user_unblocked_message), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
