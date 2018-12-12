package com.bigsteptech.realtimechat.settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.interfaces.OnItemClickListener;
import com.bigsteptech.realtimechat.settings.adapter.SettingsListAdapter;
import com.bigsteptech.realtimechat.settings.data_model.Settings;
import com.bigsteptech.realtimechat.ui.SimpleDividerItemDecoration;
import com.bigsteptech.realtimechat.user.BlockedContactsActivity;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    private View rootView;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private DatabaseReference usersDb;
    private List<Settings> settingsList;
    private String self;
    private int onlineStatus, visibility;
    private MessengerDatabaseUtils messengerDatabaseUtils;

    private SettingsListAdapter settingsListAdapter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(Bundle bundle) {
        // Required  public constructor
        SettingsFragment fragment = new SettingsFragment();
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
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mContext));

        usersDb = messengerDatabaseUtils.getUsersDatabase();
        self = messengerDatabaseUtils.getCurrentUserId();

        settingsList = new ArrayList<>();

        settingsListAdapter = new SettingsListAdapter(mContext, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                switch (settingsList.get(position).getSettingName()){

                    case "blockList":
                        // Open Contacts Fragment inside an activity

                        Intent intent = new Intent(mContext, BlockedContactsActivity.class);
                        startActivity(intent);
                        break;

                    case "status":
                        // Open Dialog box to select Options "Online", "Away", "Offline"
                        showStatusSelectionDialog();
                        break;

                    case "language":
                        // Do not show anything for now
                        break;

                    case "notifications":
                        // Change the value to switch

                        usersDb.child(self).child("notification").setValue(settingsList.get(position)
                                .isNotificationsEnabled() == 0 ? 1 : 0);
                        break;
                }
            }
        });

        mRecyclerView.setAdapter(settingsListAdapter);

        getUserSettings();

        return rootView;

    }

    private void getUserSettings() {

        usersDb.child(self).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                settingsList.clear();
                settingsListAdapter.clear();

                final int notificationsEnabled = dataSnapshot.child("notification").getValue(Integer.class);
                onlineStatus = dataSnapshot.child("isOnline").getValue(Integer.class);
                visibility = dataSnapshot.child("visibility").getValue(Integer.class);
                String language = dataSnapshot.child("language").getValue(String.class);

                if(language == null || language.isEmpty()){
                    language = "en";
                }

                settingsList.add(new Settings("blockList", mContext.getResources().getString(R.string.blocked_users),
                        visibility, notificationsEnabled, language));
                settingsList.add(new Settings("language", mContext.getResources().getString(R.string.default_language),
                        visibility, notificationsEnabled, language));
                settingsList.add(new Settings("notifications", mContext.getResources().getString(R.string.notifications),
                        visibility, notificationsEnabled, language));
                settingsList.add(new Settings("status", mContext.getResources().getString(R.string.im),
                        visibility, notificationsEnabled, language));

                progressBar.setVisibility(View.GONE);
                settingsListAdapter.update(settingsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showStatusSelectionDialog(){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle(getResources().getString(R.string.select_status));

        final ArrayAdapter<String> statusOptionsAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_singlechoice);

        statusOptionsAdapter.add(mContext.getResources().getString(R.string.online));
        statusOptionsAdapter.add(mContext.getResources().getString(R.string.offline));


        int selectedPosition = visibility == 1 ? 0 : 1;

        alertBuilder.setSingleChoiceItems(statusOptionsAdapter, selectedPosition,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int newVisibilityValue = which == 1 ? 0 : 1;

                        Map<String, Object> visibilityValues = new HashMap<>();
                        visibilityValues.put("visibility", newVisibilityValue);
                        if(newVisibilityValue == 0){
                            visibilityValues.put("lastSeen", ServerValue.TIMESTAMP);
                            visibilityValues.put("isOnline", 0);
                        } else {
                            visibilityValues.put("lastSeen", 0);
                            visibilityValues.put("isOnline", 1);
                        }
                        usersDb.child(self).updateChildren(visibilityValues);
                        dialog.dismiss();
                    }
        });

        alertBuilder.create().show();
    }

}
