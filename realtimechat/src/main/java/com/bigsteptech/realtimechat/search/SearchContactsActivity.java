package com.bigsteptech.realtimechat.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.Utils;
import com.bigsteptech.realtimechat.contacts.ContactsListFragment;
import com.bigsteptech.realtimechat.contacts.data_model.ContactsList;
import com.bigsteptech.realtimechat.conversation.ConversationActivity;
import com.bigsteptech.realtimechat.groups.GroupsListFragment;
import com.bigsteptech.realtimechat.groups.data_model.GroupsList;
import com.bigsteptech.realtimechat.interfaces.OnItemClickListener;
import com.bigsteptech.realtimechat.ui.VerticalSpaceItemDecoration;
import com.bigsteptech.realtimechat.user.User;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.himanshuvirmani.androidcache.CacheManager;
import com.himanshuvirmani.androidcache.Result;

import java.util.ArrayList;
import java.util.List;

public class SearchContactsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<SearchList> mPeopleGroupsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchListAdapter searchListAdapter, searchFilterAdapter;
    private String selfUid;
    private DatabaseReference friendsDb, userDb, chatMembersDb, chatRoomDb;
    private ProgressBar progressBar;
    private boolean showOnlyContacts = false;
    private MessengerDatabaseUtils messengerDatabaseUtils;
    private boolean isContactsExists = true;
    private List<SearchList> filteredList = new ArrayList<>();
    private CacheManager cacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        // Setup Back Button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        cacheManager = messengerDatabaseUtils.getCacheManagerInstance();
        recyclerView = (RecyclerView)  findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

        selfUid = messengerDatabaseUtils.getCurrentUserId();

        friendsDb = messengerDatabaseUtils.getFriendsDatabase().child(selfUid);
        userDb = messengerDatabaseUtils.getUsersDatabase();
        chatMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        chatRoomDb = messengerDatabaseUtils.getChatsDatabase();

        searchListAdapter = new SearchListAdapter(this, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                openConversationActivity(position, false);
            }
        });

        recyclerView.setAdapter(searchListAdapter);

        searchFilterAdapter = new SearchListAdapter(this, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                openConversationActivity(position, true);
            }
        });
        showOnlyContacts = getIntent().hasExtra("showOnlyContacts");


        /**
         * Fetch All the Contacts and Groups of the currently logged-in user
         */
        if(ContactsListFragment.mContactsList != null && ContactsListFragment.mContactsList.size() != 0){
            addContactsToList();
        } else {
            getContactsList();
        }

        if(!showOnlyContacts){
            if(GroupsListFragment.mGroupsList != null && GroupsListFragment.mGroupsList.size() != 0){
                addGroupsToList();
            } else {
                getGroupsList();
            }
        }

    }

    private void openConversationActivity(int position, boolean isFilteredResult){

        SearchList searchList;

        if(isFilteredResult){
            searchList = searchFilterAdapter.getmSearchList().get(position);
        } else {
            searchList = searchListAdapter.getmSearchList().get(position);
        }

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("chatRoomId", searchList.getConversationId());
        intent.putExtra("typeOfChat", searchList.getTypeOfChat());
        intent.putExtra(Constants.SENDER, selfUid);
        intent.putExtra(Constants.DESTINATION, searchList.getUserId());
        startActivity(intent);

    }

    private void getContactsList() {

        // Attach a listener to read the data at our posts reference
        friendsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot usersDataSnapshot) {

                final int[] i = {0};
                final int[] j = {0};

                if(usersDataSnapshot.getChildrenCount() > 0){
                    hideErrorMessage();
                    for (final DataSnapshot childDataSnapshot : usersDataSnapshot.getChildren()) {
                        ContactsListFragment.mContactsList.add(new ContactsList(childDataSnapshot.getKey()));


                        if(ContactsListFragment.mContactsList.size() > 0 && i[0] <= ContactsListFragment.mContactsList.size()-1){

                            final ContactsList contactsList = (ContactsList) ContactsListFragment.mContactsList.get(i[0]);
                            i[0]++;
                            userDb.child(childDataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    j[0]++;

                                    if(dataSnapshot.exists()) {
                                        User user = dataSnapshot.getValue(User.class);
                                        contactsList.setmUserTitle(user.getName());
                                        contactsList.setmUserImage(user.getProfileImageUrl());
                                        contactsList.setmOnlineStatus(user.getIsOnline());
                                        contactsList.setLastSeen(user.getLastSeen());
                                    } else {
                                        ContactsListFragment.mContactsList.remove(ContactsListFragment.
                                                mContactsList.indexOf(contactsList));
                                    }

                                    if( j[0] >= usersDataSnapshot.getChildrenCount()){
                                        addContactsToList();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } else {
                    isContactsExists = false;
                    if(showOnlyContacts){
                        showErrorMessage(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void getGroupsList() {


        chatMembersDb.orderByChild(selfUid + "/isActive").equalTo("1").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int k = 0;
                if(dataSnapshot.getChildrenCount() > 0){
                    for(final DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){

                        k++;
                        final int finalK = k;


                        if(childDataSnapshot.getChildrenCount() > 2){
                            hideErrorMessage();

                            chatRoomDb.child(childDataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    int memberCount = dataSnapshot.child("memberCount").getValue(Integer.class);
                                    String groupImage = dataSnapshot.child(Constants.FIREBASE_IMAGE_KEY).getValue(String.class);
                                    String groupTitle = dataSnapshot.child("title").getValue(String.class);

                                    String lastMessageTimeStamp = Utils.getRelativeTimeString(childDataSnapshot.child(selfUid).child("updatedAt").
                                            getValue(Long.class));

                                    GroupsList groupsList = new GroupsList(dataSnapshot.getKey(), groupImage,
                                            groupTitle, lastMessageTimeStamp, childDataSnapshot.child(selfUid).
                                            child("updatedAt").getValue(Long.class), memberCount);
                                    GroupsListFragment.mGroupsList.add(groupsList);

                                    if(finalK >= GroupsListFragment.mGroupsList.size()){
                                        addGroupsToList();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } else if(!isContactsExists){
                    showErrorMessage(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showErrorMessage(boolean isSearch) {

        progressBar.setVisibility(View.GONE);

        findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
        ImageView errorIcon = (ImageView) findViewById(R.id.error_icon);
        errorIcon.setImageResource(R.drawable.ic_assignment_ind_black_24dp);
        TextView errorMessage = (TextView) findViewById(R.id.error_message);
        if(isSearch){
            errorMessage.setText(getResources().getString(R.string.no_data_msg));
        } else {
            errorMessage.setText(getResources().getString(R.string.no_contacts));
        }
    }

    public void hideErrorMessage() {
        findViewById(R.id.message_layout).setVisibility(View.GONE);
    }

    private void addContactsToList() {
        int i = 0;

        for (Object aMContactsList : ContactsListFragment.mContactsList) {
            i++;
            ContactsList contactsList = (ContactsList) aMContactsList;
            mPeopleGroupsList.add(new SearchList(contactsList.getmUserTitle(), contactsList.getmUserImage(),
                    null, 0, contactsList.getmUserId()));
            if(i >= ContactsListFragment.mContactsList.size()){
                progressBar.setVisibility(View.GONE);
                searchListAdapter.update(mPeopleGroupsList);
            }
        }
    }

    private void addGroupsToList() {

        int i = 0;
        for (Object aMGroupsList : GroupsListFragment.mGroupsList) {
            i++;
            GroupsList groupsList = (GroupsList) aMGroupsList;
            SearchList searchList = new SearchList(groupsList.getTitle(), groupsList.getProfileImageUrl(),
                    groupsList.getmGroupId(), 1, null);
            if(!mPeopleGroupsList.contains(searchList))
                mPeopleGroupsList.add(searchList);
            if(i >= GroupsListFragment.mGroupsList.size()){
                progressBar.setVisibility(View.GONE);
                searchListAdapter.update(mPeopleGroupsList);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);

        if (searchItem != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();

            if (view != null) {
                view.setLayoutParams(lp);
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(this) ;
                view.setQueryHint(getResources().getString(R.string.search));
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {

        Log.d(SearchContactsActivity.class.getSimpleName(),  "onQueryTextSubmit called..");
        if(query != null && !query.isEmpty()) {
            if(ContactsListFragment.mContactsList.size() + ContactsListFragment.DELETED_USER_COUNT >=
                    Constants.CONTACTS_LIMIT) {
                progressBar.setVisibility(View.VISIBLE);
                filteredList.clear();
                recyclerView.setAdapter(searchFilterAdapter);
                Query firebaseQuery = friendsDb.orderByChild("name").startAt(query.toLowerCase()).
                        endAt(query.toLowerCase()+"\uf8ff");

                firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addSearchedContactsToList(dataSnapshot, query);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                searchFilterAdapter.update(new ArrayList<>(searchListAdapter.getmSearchList()));
                searchFilterAdapter.filter(query);
                recyclerView.setAdapter(searchFilterAdapter);
            }
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.equals("")){
            if(searchListAdapter.getItemCount() != 0){
                hideErrorMessage();
            }
            recyclerView.setAdapter(searchListAdapter);
        }
        return false;
    }

    private void addSearchedContactsToList(final DataSnapshot friendsSnapshot, final String query) {

        Log.d(SearchContactsActivity.class.getSimpleName(), "addSearchedContactsToList called.. " +
                friendsSnapshot.getChildrenCount() + "query : " + query);

        final int[] i = {0};

        // Need to add the contacts to search filter list
        for (DataSnapshot userSnapShot : friendsSnapshot.getChildren()) {

            Result<User> user = cacheManager.get(userSnapShot.getKey(), User.class);
            if(user.getCachedObject() != null) {
                i[0]++;
                User userDetails = user.getCachedObject();
                filteredList.add(new SearchList(userDetails.getName(), userDetails.getProfileImageUrl(),
                        null, 0, userSnapShot.getKey()));

                Log.d(SearchContactsActivity.class.getSimpleName(), "1 i "  + i[0]);
                if(i[0] == friendsSnapshot.getChildrenCount()) {
                    filterGroupsList(query);
                }
            } else {
                userDb.child(userSnapShot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        i[0]++;
                        String userTitle = dataSnapshot.child("name").getValue(String.class);
                        String userImage = dataSnapshot.child("profileImageUrl").getValue(String.class);
                        filteredList.add(new SearchList(userTitle, userImage, null, 0, dataSnapshot.getKey()));

                        Log.d(SearchContactsActivity.class.getSimpleName(), "2 i " + i[0]);

                        if(i[0] == friendsSnapshot.getChildrenCount()) {
                            filterGroupsList(query);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void filterGroupsList(String query) {

        for (SearchList searchList : searchListAdapter.getmSearchList()) {
            if (searchList.getTypeOfChat() == 1 && searchList.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(searchList);
            }
        }
        notifySearchAdapter();
    }


    private void notifySearchAdapter() {
        Log.d(SearchContactsActivity.class.getSimpleName(), "notifySearchAdapter called" );
        progressBar.setVisibility(View.GONE);
        searchFilterAdapter.update(filteredList);
        recyclerView.setAdapter(searchFilterAdapter);
    }
}
