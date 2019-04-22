package com.socialengineaddons.messenger.groups;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.Utils;
import com.socialengineaddons.messenger.adapter.RecyclerViewAdapter;
import com.socialengineaddons.messenger.conversation.ConversationActivity;
import com.socialengineaddons.messenger.groups.data_model.GroupsList;
import com.socialengineaddons.messenger.interfaces.OnItemClickListener;
import com.socialengineaddons.messenger.ui.VerticalSpaceItemDecoration;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsListFragment extends Fragment {


    private View rootView;
    private RecyclerView mRecyclerView;
    public static List<Object> mGroupsList = new ArrayList<>();
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Context mContext;
    private DatabaseReference chatRoomDb, usersDb, chatMembersDb;
    private String selfUid;
    private ProgressBar progressBar;
    private MessengerDatabaseUtils messengerDatabaseUtils;
    private int counter = 0;
    private Query groupsQuery;
    private ChildEventListener childEventListener;
    private boolean isGroupsExists = false;

    public GroupsListFragment() {
        // Required empty public constructor
    }

    public static GroupsListFragment newInstance(Bundle bundle) {
        // Required  public constructor
        GroupsListFragment fragment = new GroupsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mContext = getActivity();
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        rootView = inflater.inflate(R.layout.recycler_view, null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mGroupsList = new ArrayList<>();

        selfUid = messengerDatabaseUtils.getCurrentUserId();

        Log.d(GroupsListFragment.class.getSimpleName(), "selfUId : " + selfUid);

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

        chatRoomDb = messengerDatabaseUtils.getChatsDatabase();
        chatMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        usersDb = messengerDatabaseUtils.getUsersDatabase();

        mRecyclerViewAdapter = new RecyclerViewAdapter(mContext, mGroupsList, selfUid, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                GroupsList groupsList = (GroupsList) mGroupsList.get(position);
                String chatRoomId = groupsList.getmGroupId();
                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("chatRoomId", chatRoomId);
                intent.putExtra("typeOfChat", 1);
                intent.putExtra(Constants.PROFILE_COLOR, groupsList.getProfileColor());
                intent.putExtra(Constants.SENDER, selfUid);


                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        getGroupsList();

        return rootView;
    }

    /**
     * Function to get the list of groups, adding a listener on users > selfUid > chats reference
     */
    private void getGroupsList() {

        progressBar.setVisibility(View.VISIBLE);

        Query query = usersDb.child(selfUid).child("chats").getRef();

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mGroupsList.clear();
                counter = 0;

                if(dataSnapshot.getChildrenCount() > 0) {
                    hideErrorMessage();
                    for(final DataSnapshot childDataSnapshot: dataSnapshot.getChildren()){

                        if(childDataSnapshot.getValue(String.class).equals("1")){
                            Log.d(GroupsListFragment.class.getSimpleName(), "Group Key: " + childDataSnapshot.getKey());
                            isGroupsExists = true;
                            addGroupToList(childDataSnapshot.getKey());
                        }
                    }

                    if(!isGroupsExists) {
                        showErrorMessage();
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

    private void addGroupToList(final String chatRoomId) {

        chatRoomDb.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    final int memberCount = dataSnapshot.child("memberCount").getValue(Integer.class);
                    final String groupImage = dataSnapshot.hasChild(Constants.FIREBASE_IMAGE_KEY) ?
                            dataSnapshot.child(Constants.FIREBASE_IMAGE_KEY).getValue(String.class) : null;
                    final String groupTitle = dataSnapshot.child("title").getValue(String.class);

                    chatMembersDb.child(chatRoomId).child(selfUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot memberSnapshot) {

                            long updatedAt = memberSnapshot.hasChild("updatedAt") ? memberSnapshot.child("updatedAt").
                                    getValue(Long.class) : 0;
                            String lastMessageTimeStamp = Utils.getRelativeTimeString(updatedAt);

                            GroupsList groupsList = new GroupsList(dataSnapshot.getKey(), groupImage,
                                    groupTitle, lastMessageTimeStamp, updatedAt, memberCount);

                            if(!mGroupsList.contains(groupsList)){
                                groupsList.setProfileColor();
                                mGroupsList.add(groupsList);
                            }

                            notifyAdapter();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void notifyAdapter () {

        if(counter < mGroupsList.size()){
            counter ++;
        }

        // Sort the List  and notify adapter when all the chatRooms are iterated
        if(counter == mGroupsList.size()){
            reloadData();
            removeObservers();
            setObservers();
        }
    }

    private void reloadData() {

        progressBar.setVisibility(View.GONE);
        sortGroupsListWithTimeStamp();
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void removeObservers() {

        if(groupsQuery != null && childEventListener != null) {
            groupsQuery.removeEventListener(childEventListener);
        }
    }

    private void setObservers () {

        groupsQuery = usersDb.child(selfUid).child("chats").getRef();

        childEventListener = groupsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getValue(String.class).equals("1")){
                    GroupsList groupsList = new GroupsList(dataSnapshot.getKey());
                    if(!mGroupsList.contains(groupsList)) {
                        hideErrorMessage();
                        addGroupToList(dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                GroupsList groupList = new GroupsList(dataSnapshot.getKey());
                if(mGroupsList.contains(groupList) && dataSnapshot.getValue(String.class).equals("0")) {
                    mGroupsList.remove(mGroupsList.indexOf(groupList));
                    mRecyclerViewAdapter.notifyItemRemoved(mGroupsList.indexOf(groupList));
                } else if(!mGroupsList.contains(groupList) && dataSnapshot.getValue(String.class).equals("1")) {
                    addGroupToList(dataSnapshot.getKey());
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
    }

    private void showErrorMessage() {

        // Show No Contacts View here
        progressBar.setVisibility(View.GONE);
        mGroupsList.clear();
        mRecyclerViewAdapter.update(mGroupsList);
        rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
        ImageView errorIcon = (ImageView) rootView.findViewById(R.id.error_icon);
        errorIcon.setImageResource(R.drawable.ic_people_white_24dp);
        TextView errorMessage = (TextView) rootView.findViewById(R.id.error_message);
        errorMessage.setText(mContext.getResources().getString(R.string.no_group));
    }

    private void hideErrorMessage(){
        rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
    }

    private void sortGroupsListWithTimeStamp() {

        Collections.sort(mGroupsList, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                GroupsList group1 = (GroupsList) o1;
                GroupsList group2 = (GroupsList) o2;

                return Long.valueOf(group2.getLastUpdated()).compareTo(group1.getLastUpdated());
            }

        });
    }

}
