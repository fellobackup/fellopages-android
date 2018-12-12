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

package com.bigsteptech.realtimechat.contacts;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.adapter.RecyclerViewAdapter;
import com.bigsteptech.realtimechat.contacts.data_model.ContactsList;
import com.bigsteptech.realtimechat.conversation.ConversationActivity;
import com.bigsteptech.realtimechat.conversation.data_model.Conversation;
import com.bigsteptech.realtimechat.interfaces.OnItemClickListener;
import com.bigsteptech.realtimechat.search.SearchContactsActivity;
import com.bigsteptech.realtimechat.search.SearchList;
import com.bigsteptech.realtimechat.ui.VerticalSpaceItemDecoration;
import com.bigsteptech.realtimechat.user.User;
import com.bigsteptech.realtimechat.user.interfaces.OnContactSelectListener;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.himanshuvirmani.androidcache.CacheManager;
import com.himanshuvirmani.androidcache.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsListFragment extends Fragment {


    private View rootView;
    private RecyclerView mRecyclerView;
    public static List<Object> mContactsList = new ArrayList<>();
    public List<Object> mDeletedUsers = new ArrayList<>();
    public static List<Object> mBlockedContactsList = new ArrayList<>();
    public static List<Object> mNewConvContactsList = new ArrayList<>();
    public static List<Object> filteredList = new ArrayList<>();
    private RecyclerViewAdapter contactsListAdapter, newConversationPageAdapter, usersFilterAdapter, blockedContactsAdapter;
    private Context mContext;
    private DatabaseReference friendsDb, userDb, chatMembersDb, duplicateChatRef;
    private String selfUid;
    private ProgressBar progressBar;
    private OnContactSelectListener mContactSelectedListener;
    private boolean isNewConversationPage, blockedContacts;
    private ArrayList<String> participantsKeys = new ArrayList<>();
    private MessengerDatabaseUtils messengerDatabaseUtils;
    private int counter = 0;
    private CacheManager cacheManager;
    private int LIMIT = 500, contactsCount;
    private String lastContactKey;
    private long lastContactAddedTime;
    private boolean isLoading = false;
    private int listSize = 0;
    public static int DELETED_USER_COUNT = 0;


    public ContactsListFragment() {
        // Required empty public constructor
    }

    public static ContactsListFragment newInstance(Bundle bundle) {
        // Required  public constructor
        ContactsListFragment fragment = new ContactsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(ContactsListFragment.class.getSimpleName(), "onCreateView function called..");

        // Inflate the layout for this fragment
        mContext = getActivity();
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        cacheManager = messengerDatabaseUtils.getCacheManagerInstance();
        rootView = inflater.inflate(R.layout.recycler_view, null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                int lastVisibleItemPosition = linearLayoutManager1.findLastVisibleItemPosition() ;

                if(LIMIT == contactsCount && lastVisibleItemPosition == mContactsList.size() - 1 && !isLoading) {
                    isLoading = true;
                    listSize = 0;
                    loadMoreContacts();
                }
            }
        });

        if(getArguments() != null){
            isNewConversationPage = getArguments().getBoolean("isNewGroupsPage");
            blockedContacts = getArguments().getBoolean("blockedContacts");
            participantsKeys = getArguments().getStringArrayList("participants");
        }

        selfUid = messengerDatabaseUtils.getCurrentUserId();

        friendsDb = blockedContacts ? messengerDatabaseUtils.getBlockedListDatabase().child(selfUid) :
                messengerDatabaseUtils.getFriendsDatabase().child(selfUid);

        userDb = messengerDatabaseUtils.getUsersDatabase();
        chatMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        duplicateChatRef = messengerDatabaseUtils.getDuplicateChatsDatabase();

        contactsListAdapter = new RecyclerViewAdapter(mContext, isNewConversationPage, blockedContacts, selfUid, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ContactsList contactsList = (ContactsList) contactsListAdapter.getmConversationList().get(position);
                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra(Constants.SENDER, selfUid);
                intent.putExtra(Constants.DESTINATION, contactsList.getmUserId());
                startActivity(intent);

            }
        });

        blockedContactsAdapter = new RecyclerViewAdapter(mContext, isNewConversationPage, blockedContacts, selfUid, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ContactsList contactsList = (ContactsList) blockedContactsAdapter.getmConversationList().get(position);

                if(mContactSelectedListener != null){
                    mContactSelectedListener.onContactSelected(contactsList);
                }
            }
        });

        newConversationPageAdapter = new RecyclerViewAdapter(mContext, isNewConversationPage, blockedContacts, selfUid, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ContactsList contactsList = (ContactsList) newConversationPageAdapter.getmConversationList().get(position);
                Log.d(ContactsListFragment.class.getSimpleName(), "OnItemClicked called.. " +  contactsList.isContactSelected());
                if(mContactSelectedListener != null){
                    mContactSelectedListener.onContactSelected(contactsList);
                }
            }
        });

        usersFilterAdapter = new RecyclerViewAdapter(mContext, isNewConversationPage, false, selfUid, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ContactsList contactsList = (ContactsList) usersFilterAdapter.getmConversationList().get(position);

                if(isNewConversationPage && mContactSelectedListener != null){
                    mContactSelectedListener.onContactSelected(contactsList);
                } else {
                    Intent intent = new Intent(mContext, ConversationActivity.class);
                    intent.putExtra(Constants.SENDER, selfUid);
                    intent.putExtra(Constants.DESTINATION, contactsList.getmUserId());
                    startActivity(intent);
                }
            }
        });

        if(blockedContacts){
            mRecyclerView.setAdapter(blockedContactsAdapter);
        } else if(isNewConversationPage || (participantsKeys != null && participantsKeys.size() != 0)){
            mRecyclerView.setAdapter(newConversationPageAdapter);
        } else {
            mRecyclerView.setAdapter(contactsListAdapter);
        }
        getContacts();

        return rootView;
    }

    // Set The Selected Filters
    public void setmContactSelectedListener(OnContactSelectListener mFilterSelectedListener) {
        this.mContactSelectedListener = mFilterSelectedListener;
    }

    public void notifyAdapter(ContactsList contact) {
        contact.setContactSelected(false);
        newConversationPageAdapter.notifyDataSetChanged();
    }

    private void getContacts() {

        mNewConvContactsList.clear();
        mBlockedContactsList.clear();

        mNewConvContactsList.clear();
        mBlockedContactsList.clear();

        DELETED_USER_COUNT = 0;
        // Attach a listener to read the data at our posts reference
        friendsDb.orderByChild("addedAt").limitToLast(LIMIT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot usersDataSnapshot) {
                Log.d(ContactsListFragment.class.getSimpleName(), "users Count: " + usersDataSnapshot.getChildrenCount());
                addDataToList(usersDataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadMoreContacts() {


        Log.d(ContactsListFragment.class.getSimpleName(),  " Inside loadMoreContacts : " + lastContactKey);

        addProgressBar();

        // Attach a listener to read the data at our posts reference
        friendsDb.orderByChild("addedAt").endAt(lastContactAddedTime).limitToLast(LIMIT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot usersDataSnapshot) {

                Log.d(ContactsListFragment.class.getSimpleName(), "inside loadMoreContacts count: " +
                        usersDataSnapshot.getChildrenCount());
                RemoveProgressBar();
                addDataToList(usersDataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addDataToList(DataSnapshot usersDataSnapshot) {

        if(usersDataSnapshot.getChildrenCount() > 0){
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            contactsCount = (int)usersDataSnapshot.getChildrenCount();
            int i = 1;
            counter = 0;
            for (final DataSnapshot childDataSnapshot : usersDataSnapshot.getChildren()) {
                Log.d(ContactsListFragment.class.getSimpleName(), "Key: "  + childDataSnapshot.getKey());
                if(i == 1) {
                    lastContactKey = childDataSnapshot.getKey();
                    if(childDataSnapshot.hasChild("addedAt")) {
                        lastContactAddedTime = childDataSnapshot.child("addedAt").getValue(Long.class);
                    }
                }
                i++;

                getUserDetails(childDataSnapshot, usersDataSnapshot.getChildrenCount());
            }
        } else {
            // Show No Contacts View here
            progressBar.setVisibility(View.GONE);
            showErrorMessage();
            setFriendsChangeObserver();
        }
    }

    private void getUserDetails(DataSnapshot userSnapshot, final long userCount) {

        if(!isContactExist(userSnapshot.getKey())){
            Result<User> user = cacheManager.get(userSnapshot.getKey(), User.class);
            if(user.getCachedObject() != null) {
                User userDetails = user.getCachedObject();
                userDetails.setUid(userSnapshot.getKey());
                addContactToList(userDetails);
                counter ++;
                // Update Adapter
                if(userCount == 1 || (counter == userCount )){
                    notifyRecyclerViewAdapter();
                }
            } else {
                userDb.child(userSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        counter ++;
                        if(dataSnapshot.exists()){
                            User user = dataSnapshot.getValue(User.class);
                            user.setUid(dataSnapshot.getKey());
                            cacheManager.put(dataSnapshot.getKey(), user);
                            addContactToList(user);
                        } else {
                            mDeletedUsers.add(dataSnapshot.getKey());
                            DELETED_USER_COUNT++;
                            Log.d(ContactsListFragment.class.getSimpleName(), "count : " + DELETED_USER_COUNT);
                        }

                        // Update Adapter
                        if(userCount == 1 || (counter == userCount )){
                            notifyRecyclerViewAdapter();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else {
            counter ++;
            // Update Adapter
            if(userCount == 1 || (counter == userCount )){
                notifyRecyclerViewAdapter();
            }
        }
    }

    private void addContactToList(User user) {

        ContactsList contactsList = new ContactsList(user.getUid(), user.getName(), user.getProfileImageUrl(),
                user.getIsOnline(), user.getLastSeen());

        if(participantsKeys != null && participantsKeys.size() != 0){
            if(!participantsKeys.contains(user.getUid())){
                mNewConvContactsList.add(contactsList);
            }
        } else {
            if(blockedContacts) {
                mBlockedContactsList.add(contactsList);
            } else if(isNewConversationPage) {
                mNewConvContactsList.add(contactsList);
            } else {
                if(!isLoading) {
                    mContactsList.add(0, contactsList);
                } else {
                    if(listSize == 0) {
                        listSize = mContactsList.size();
                    }
                    mContactsList.add(listSize, contactsList);
                }
            }
        }

//        if(user.getIsOnline() == 1){
//            mOnlineMembers.add(contactsList);
//        } else {
//            mOfflineMembers.add(contactsList);
//        }
    }

    private void notifyRecyclerViewAdapter() {

        Log.d(ContactsListFragment.class.getSimpleName(), " notifyRecyclerViewAdapter called..");

        progressBar.setVisibility(View.GONE);
        isLoading = false;
        if(isNewConversationPage || (participantsKeys != null && participantsKeys.size() != 0)){
            newConversationPageAdapter.update(mNewConvContactsList);
        } else if(blockedContacts){
            blockedContactsAdapter.update(mBlockedContactsList);
        } else {
//            mContactsList = new ArrayList<>();
//            mContactsList.addAll(mOnlineMembers);
//            mContactsList.addAll(mOfflineMembers);
            contactsListAdapter.update(mContactsList);
        }
        setFriendsChangeObserver();
    }

    private void setFriendsChangeObserver () {

        friendsDb.orderByChild("addedAt").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // A New Friend has been added to the Friend List Or block List
                if(!isContactExist(dataSnapshot.getKey()) && !mDeletedUsers.contains(dataSnapshot.getKey())) {
                    Log.d(ContactsListFragment.class.getSimpleName(), "onChildAdded : " + dataSnapshot.getKey());
                    getUserDetails(dataSnapshot, 1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // An Existing Friends has been removed from the Friend List Or block List
                Log.d(ContactsListFragment.class.getSimpleName(), "onChildRemoved : " + dataSnapshot.getKey());
                ContactsList contactDetails = new ContactsList(dataSnapshot.getKey());
                removeContactFromList(contactDetails);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isContactExist(String userId) {

        ContactsList contactsList = new ContactsList(userId);

        if(isNewConversationPage){
            if(participantsKeys != null && participantsKeys.size() != 0) {
                return participantsKeys.contains(userId) || mNewConvContactsList.contains(contactsList) ;
            } else {
                return mNewConvContactsList.contains(contactsList);
            }
        } else if(blockedContacts){
            return mBlockedContactsList.contains(contactsList);
        } else {
            return mContactsList.contains(contactsList);
        }
    }

    private void removeContactFromList(ContactsList contactsList) {

        if(isNewConversationPage || (participantsKeys != null && participantsKeys.size() != 0)){
            if(mNewConvContactsList.contains(contactsList)) {
                mNewConvContactsList.remove(mNewConvContactsList.indexOf(contactsList));
                newConversationPageAdapter.notifyItemRemoved(mNewConvContactsList.indexOf(contactsList));
            }
        } else if(blockedContacts){
            if(mBlockedContactsList.contains(contactsList)) {
                mBlockedContactsList.remove(mBlockedContactsList.indexOf(contactsList));
                blockedContactsAdapter.notifyItemRemoved(mBlockedContactsList.indexOf(contactsList));
            }
        } else {
            if(mContactsList.contains(contactsList)) {
                mContactsList.remove(mContactsList.indexOf(contactsList));
                contactsListAdapter.notifyItemRemoved(mContactsList.indexOf(contactsList));
            }
        }
    }

    private void showErrorMessage() {

        if(blockedContacts){
            mBlockedContactsList.clear();
            blockedContactsAdapter.update(mBlockedContactsList);
        } else if(isNewConversationPage || (participantsKeys != null && participantsKeys.size() != 0)) {
            mNewConvContactsList.clear();
            newConversationPageAdapter.update(mContactsList);
        } else {
            mContactsList.clear();
            contactsListAdapter.update(mContactsList);
        }

        rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
        ImageView errorIcon = (ImageView) rootView.findViewById(R.id.error_icon);
        errorIcon.setImageResource(R.drawable.ic_assignment_ind_black_24dp);
        TextView errorMessage = (TextView) rootView.findViewById(R.id.error_message);
        if(blockedContacts){
            errorMessage.setText(mContext.getResources().getString(R.string.no_blocked_contacts));
        } else {
            errorMessage.setText(mContext.getResources().getString(R.string.no_contacts));
        }
    }

    private void addProgressBar () {

        if(blockedContacts){
            mBlockedContactsList.add(null);
            blockedContactsAdapter.notifyItemInserted(mBlockedContactsList.size() - 1);
        } else if(isNewConversationPage || (participantsKeys != null && participantsKeys.size() != 0)) {
            mNewConvContactsList.add(null);
            newConversationPageAdapter.notifyItemInserted(mNewConvContactsList.size() - 1);
        } else {
            mContactsList.add(null);
            contactsListAdapter.notifyItemInserted(mContactsList.size() - 1);
        }
    }


    private void RemoveProgressBar () {

        if(blockedContacts){
            mBlockedContactsList.remove(mBlockedContactsList.size() - 1);
            blockedContactsAdapter.notifyItemRemoved(mBlockedContactsList.size());
        } else if(isNewConversationPage || (participantsKeys != null && participantsKeys.size() != 0)) {
            mNewConvContactsList.remove(mNewConvContactsList.size() - 1);
            newConversationPageAdapter.notifyItemRemoved(mNewConvContactsList.size());
        } else {
            mContactsList.remove(mContactsList.size() - 1);
            contactsListAdapter.notifyItemRemoved(mContactsList.size());
        }
    }

    public void filterUsers(final String text){
        if (text.equals("")){
            mRecyclerView.setAdapter(newConversationPageAdapter);
        } else {
            if(ContactsListFragment.mContactsList.size() + DELETED_USER_COUNT >= Constants.CONTACTS_LIMIT) {
                Log.d(ContactsListFragment.class.getSimpleName(), "Inside filterUsers function");
                filteredList.clear();
                mRecyclerView.setAdapter(usersFilterAdapter);
                progressBar.setVisibility(View.VISIBLE);

                Query firebaseQuery = friendsDb.orderByChild("name").startAt(text.toLowerCase()).
                        endAt(text.toLowerCase()+"\uf8ff");

                firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addSearchedContactsToList(dataSnapshot, text);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                usersFilterAdapter.update(new ArrayList<>(newConversationPageAdapter.getmConversationList()));
                usersFilterAdapter.filter(text);
                mRecyclerView.setAdapter(usersFilterAdapter);
            }
        }
    }

    private void addSearchedContactsToList(final DataSnapshot friendsSnapshot, final String query) {

        Log.d(ContactsListFragment.class.getSimpleName(), "addSearchedContactsToList called.. " +
                friendsSnapshot.getChildrenCount() + "query : " + query);

        final int[] i = {0};
        filteredList.clear();

        // Need to add the contacts to search filter list
        for (DataSnapshot userSnapShot : friendsSnapshot.getChildren()) {

            Result<User> user = cacheManager.get(userSnapShot.getKey(), User.class);
            if(user.getCachedObject() != null) {
                i[0]++;
                User userDetails = user.getCachedObject();
                filteredList.add(new ContactsList(userDetails.getUid(), userDetails.getName(), userDetails.getProfileImageUrl(),
                        userDetails.getIsOnline(), userDetails.getLastSeen()));

                if(i[0] == friendsSnapshot.getChildrenCount()) {
                    notifySearchAdapter();
                }

            } else {
                userDb.child(userSnapShot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        i[0]++;
                        User userDetails = dataSnapshot.getValue(User.class);
                        filteredList.add(new ContactsList(userDetails.getUid(), userDetails.getName(), userDetails.getProfileImageUrl(),
                                userDetails.getIsOnline(), userDetails.getLastSeen()));
                        if(i[0] == friendsSnapshot.getChildrenCount()) {
                            notifySearchAdapter();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void notifySearchAdapter() {
        progressBar.setVisibility(View.GONE);
        usersFilterAdapter.update(filteredList);
        mRecyclerView.setAdapter(usersFilterAdapter);
    }

    public void unBlockSelectedMember(final String userId){

        chatMembersDb.orderByChild(selfUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isChatRoomExist = false;

                for (DataSnapshot groupDetails : dataSnapshot.getChildren()) {

                    if(groupDetails.getChildrenCount() == 2 && groupDetails.hasChild(userId) &&
                            groupDetails.hasChild(selfUid)){

                        isChatRoomExist = true;

                        String duplicateChatId = groupDetails.child(selfUid).child("duplicateChatId").getValue(String.class);

                        if(duplicateChatId != null){
                            duplicateChatRef.child(duplicateChatId).removeValue();
                        }

                        HashMap<String, Object> chatMembersUpdates = new HashMap<>();
                        chatMembersUpdates.put("isActive", "1");
                        chatMembersUpdates.put("duplicateChatId", null);

                        chatMembersDb.child(groupDetails.getKey()).child(selfUid).updateChildren(chatMembersUpdates);


                        // Remove the user from BlockedList table
                        friendsDb.child(userId).removeValue();
                        break;
                    }
                }

                // Check if there is no chat room exist for this user, then just remove entry from block list table
                if(!isChatRoomExist){
                    friendsDb.child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // todo send an API request to unblock the same user in socialEngine as well

    }

}
