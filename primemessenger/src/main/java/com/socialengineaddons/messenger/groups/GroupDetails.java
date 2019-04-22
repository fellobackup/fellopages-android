package com.socialengineaddons.messenger.groups;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.Utils;
import com.socialengineaddons.messenger.adapter.RecyclerViewAdapter;
import com.socialengineaddons.messenger.conversation.ConversationActivity;
import com.socialengineaddons.messenger.conversation.NewConversationActivity;
import com.socialengineaddons.messenger.conversation.view.PhotoPreviewActivity;
import com.socialengineaddons.messenger.interfaces.OnMenuItemSelected;
import com.socialengineaddons.messenger.multiimageselector.MultiImageSelectorActivity;
import com.socialengineaddons.messenger.ui.CircularImageView;
import com.socialengineaddons.messenger.ui.VerticalSpaceItemDecoration;
import com.socialengineaddons.messenger.user.User;
import com.socialengineaddons.messenger.user.userProfile;
import com.socialengineaddons.messenger.utils.AlertDialogWithAction;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;
import com.socialengineaddons.messenger.utils.PermissionsUtils;
import com.socialengineaddons.messenger.utils.PhotoUploadingUtils;
import com.socialengineaddons.messenger.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GroupDetails extends AppCompatActivity implements View.OnClickListener {


    private String chatRoomId, groupName, groupProfileImage;
    private DatabaseReference chatRoomDb, usersDb, chatMembersDb, duplicateChatRoomsDb, chatMessagesDb;
    private StorageReference mStorageRef;
    private Uri imagePath;

    private Context mContext;

    private CircularImageView groupImage;
    private TextView groupTitle, lastActiveTime;
    private RecyclerView recyclerView;
    private CardView groupDetailsCardView;
    private LinearLayout addMembersBlock;
    private View separatorView;

    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Object> participantsList = new ArrayList<>();
    private ArrayList<String> participantsKeys = new ArrayList<>();
    private ArrayList<String> mSelectPath = new ArrayList<>();
    ProgressDialog progressDialog;
    private String selfUid;
    public static String groupAdmin;
    private String newGroupAdmin;
    private String lastMessageId;
    private int chatRoomMemberCount, profileColor;
    private ImageView addMembersIcon;
    private AlertDialogWithAction mAlertDialogWithAction;
    private MessengerDatabaseUtils messengerDatabaseUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        mAlertDialogWithAction = new AlertDialogWithAction(this);
        profileColor = getIntent().getIntExtra(Constants.PROFILE_COLOR, 0);
        if (profileColor == 0) {
            Random random = new Random();
            this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        selfUid = messengerDatabaseUtils.getCurrentUserId();
        chatRoomDb = messengerDatabaseUtils.getChatsDatabase();
        chatMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        usersDb = messengerDatabaseUtils.getUsersDatabase();
        duplicateChatRoomsDb = messengerDatabaseUtils.getDuplicateChatsDatabase();
        chatMessagesDb = messengerDatabaseUtils.getChatMessagesDatabase();
        mStorageRef = messengerDatabaseUtils.getStorageRef();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        getChatRoomDetails();
    }


    private void initViews() {

        groupDetailsCardView = (CardView) findViewById(R.id.groupDetails);
        groupDetailsCardView.setOnClickListener(this);
        groupTitle = (TextView) findViewById(R.id.group_title);
        groupImage = (CircularImageView) findViewById(R.id.group_image);
        groupImage.setOnClickListener(this);
        lastActiveTime = (TextView) findViewById(R.id.last_active_time);
        addMembersBlock = (LinearLayout) findViewById(R.id.add_members);
        separatorView = findViewById(R.id.separator);
        addMembersIcon = (ImageView) findViewById(R.id.addMembersIcon);
        GradientDrawable drawable = (GradientDrawable) addMembersIcon.getBackground();
        drawable.setColor(ContextCompat.getColor(mContext, R.color.bg_msg_from));
        addMembersIcon.setBackground(drawable);
        addMembersBlock.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.membersRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

        recyclerViewAdapter = new RecyclerViewAdapter(mContext, participantsList, selfUid, new OnMenuItemSelected() {
            @Override
            public void onMenuItemSelected(int selectedOptionId, User user) {

                switch (selectedOptionId){
                    case 0:
                        leaveGroup();
                        break;

                    case 1:
                        // Send Message
                        Intent intent = new Intent(mContext, ConversationActivity.class);
                        intent.putExtra("typeOfChat", 0);
                        intent.putExtra(Constants.SENDER, selfUid);
                        intent.putExtra(Constants.PROFILE_COLOR, user.getProfileColor());
                        intent.putExtra(Constants.DESTINATION, user.getUid());
                        startActivity(intent);
                        break;

                    case 2:
                        // View Profile make a page for this
                        Intent userProfileIntent = new Intent(mContext, userProfile.class);
                        userProfileIntent.putExtra("user_id", user.getUid());
                        userProfileIntent.putExtra(Constants.PROFILE_COLOR, user.getProfileColor());
                        startActivity(userProfileIntent);
                        break;

                    case 3:
                        // Remove from Group
                        updateDatabaseOnLeaveAndRemove(user.getUid(), !user.isDeleted());
                        break;
                }
            }
        });

        recyclerView.setAdapter(recyclerViewAdapter);

    }

    /**
     * Add a listener to chats > conversationId table to fetch the conversation details
     */
    private void getChatRoomDetails() {

        chatRoomDb.child(chatRoomId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                participantsList.clear();

                groupAdmin = dataSnapshot.child("ownerId").getValue(String.class);
                groupName = dataSnapshot.child("title").getValue(String.class);
                groupTitle.setText(groupName);
                groupProfileImage = dataSnapshot.child(Constants.FIREBASE_IMAGE_KEY).getValue(String.class);
                lastMessageId = dataSnapshot.child("lastMessageId").getValue(String.class);
                chatRoomMemberCount = dataSnapshot.child("memberCount").getValue(Integer.class);


                if (groupProfileImage != null && !groupProfileImage.isEmpty()) {
                    groupImage.hideText();
                    Picasso.with(mContext)
                            .load(groupProfileImage)
                            .placeholder(R.drawable.group_default_image)
                            .into(groupImage);
                } else {
                    groupImage.showText(groupName, profileColor);
                }

                addMembersBlock.setVisibility(selfUid.equals(groupAdmin) ? View.VISIBLE : View.GONE);
                separatorView.setVisibility(selfUid.equals(groupAdmin) ? View.VISIBLE : View.GONE);

                chatMembersDb.child(chatRoomId).orderByChild("isActive").equalTo("1").
                        addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot membersDataSnapshot) {

                        participantsKeys.clear();
                        participantsList.clear();

                        for(final DataSnapshot participant : membersDataSnapshot.getChildren()){

                            if (!participantsKeys.contains(participant.getKey())) {
                                participantsKeys.add(participant.getKey());
                            }
                            if(participant.getKey().equals(selfUid) && participant.hasChild("updatedAt")) {
                                lastActiveTime.setText(Utils.getRelativeTimeString(participant.
                                        child("updatedAt").getValue(Long.class)));
                            }
                            usersDb.child(participant.getKey()).addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    User user;
                                    if(dataSnapshot.exists()){
                                        user = dataSnapshot.getValue(User.class);
                                        user.setUid(participant.getKey());
                                    } else {
                                        user = new User();
                                        user.setName(mContext.getResources().getString(R.string.deleted_user));
                                        user.setDeleted(true);
                                        user.setUid(participant.getKey());
                                    }
                                    user.setProfileColor();
                                    if (!participantsList.contains(user)) {
                                        participantsList.add(user);
                                    }

                                    if(participantsList.size() == membersDataSnapshot.getChildrenCount()){
                                        recyclerViewAdapter.notifyDataSetChanged();
                                    }
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_default_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;

            case 0:

                if(!PermissionsUtils.checkManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    PermissionsUtils.requestForManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Constants.READ_EXTERNAL_STORAGE);
                }else{
                    PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_SINGLE,
                            true, 1, false);
                }
                break;

            case 1:
                changeGroupName();
                break;

            case 2:
                leaveGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_SINGLE,
                            true, 1, false);
                } else{
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Constants.READ_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(this, findViewById(R.id.rootView),
                                Constants.READ_EXTERNAL_STORAGE);

                    }
                }
                break;
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE, 0, Menu.NONE, mContext.getResources().getString(R.string.change_group_photo));
        menu.add(Menu.NONE, 1, Menu.NONE, mContext.getResources().getString(R.string.change_name));
        menu.add(Menu.NONE, 2, Menu.NONE, mContext.getResources().getString(R.string.leave_group));
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Function to change group photo, it wil upload the image in firebase storage and
     * update the image url in firebase database
     */
    private void changeGroupPhoto() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.saving) + "...");
        progressDialog.show();

        StorageReference riversRef = mStorageRef.child("images/" + UUID.randomUUID() + ".jpg");
        riversRef.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        Uri imageUri = taskSnapshot.getDownloadUrl();
                        progressDialog.dismiss();
                        if(imageUri != null){
                            chatRoomDb.child(chatRoomId).child(Constants.FIREBASE_IMAGE_KEY).setValue(imageUri.toString());
                            updateChatMembers();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();

                        //and displaying error message
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Function to change the name of the group
     */
    private void changeGroupName() {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        final EditText input = new EditText(mContext);


        float dpi = getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams layoutManager = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        input.setPadding((int)(10*dpi), (int)(15*dpi), (int)(10*dpi), (int)(10*dpi));
        input.setLayoutParams(layoutManager);

        if(groupName != null && !groupName.isEmpty()){
            input.setText(groupName);
        }
        alertBuilder.setView(input);

        alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                // Change the Group Name here.
                if(!input.getText().toString().isEmpty() && !input.getText().toString().equals(groupName)){

                    // Don't update the name if group name is same as before
                    HashMap<String, Object> chatRoomUpdates = new HashMap<>();
                    chatRoomUpdates.put("title", input.getText().toString().trim());
                    chatRoomDb.child(chatRoomId).updateChildren(chatRoomUpdates);
                    updateChatMembers();
                }
            }
        });

        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = alertBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout negativeButtonParent = (LinearLayout) negativeButton.getParent();
        negativeButtonParent.setGravity(Gravity.START);
    }

    /**
     * Function to update updatedAt of all chatMembers in members table if any of the activities
     * like name/photo change happens in group
     */
    private void updateChatMembers() {

        chatMembersDb.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot member :  dataSnapshot.getChildren()) {

                    // update updatedAt of all members who are currently active in the chat
                    if(member.child("isActive").getValue(String.class).equals("1")) {
                        chatMembersDb.child(chatRoomId).child(member.getKey()).child("updatedAt").
                                setValue(ServerValue.TIMESTAMP);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Function to perform leave operation if any of the member left the group
     * if admin left the group, we need to show a popup in which he need to make a new member as a group admin
     */
    public void leaveGroup() {

        /**
         * If the current logged-in user is admin of the group, then show
         * him a popup to make other member as admin
         */
        newGroupAdmin = null;
        if(selfUid.equals(groupAdmin)){

            if(chatRoomMemberCount == 1){
                deleteChatRoom();
            } else {

                final AlertDialog.Builder[] alertBuilder = {new AlertDialog.Builder(this)};

                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.make_group_admin_view, null);

                alertBuilder[0].setTitle(mContext.getResources().getString(R.string.leave_group));
                alertBuilder[0].setView(dialogView);

                TextView textView = (TextView) dialogView.findViewById(R.id.description);
                textView.setText(mContext.getResources().getString(R.string.create_admin_alert));
                RadioGroup radioGroup  = (RadioGroup) dialogView.findViewById(R.id.groupMembersList);

                int j = 0;
                for(int i  = 0; i < participantsList.size(); i++){

                    User user = (User) participantsList.get(i);
                    if(user.getUid().equals(selfUid) || user.isDeleted()) continue;

                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(user.getName());
                    radioButton.setTag(user.getUid());
                    radioGroup.addView(radioButton, j++);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                        newGroupAdmin = (String)checkedRadioButton.getTag();
                    }
                });
                alertBuilder[0].setPositiveButton(mContext.getResources().getString(R.string.leave), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(newGroupAdmin != null){
                            dialog.dismiss();
                            chatRoomDb.child(chatRoomId).child("ownerId").setValue(newGroupAdmin);
                            updateDatabaseOnLeaveAndRemove(selfUid, true);

                            Intent intent = new Intent();
                            setResult(Constants.GROUP_INFO_REQUEST, intent);
                            finish();
                        }

                    }
                });

                alertBuilder[0].setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder[0].create().show();
            }

        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

            alertBuilder.setMessage(mContext.getResources().getString(R.string.leave_group_alert));
            alertBuilder.setTitle(mContext.getResources().getString(R.string.leave_group));

            alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.leave), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    if(chatRoomMemberCount == 1){
                        deleteChatRoom();
                    } else {
                        updateDatabaseOnLeaveAndRemove(selfUid, true);
                    }

                    Intent intent = new Intent();
                    setResult(Constants.GROUP_INFO_REQUEST, intent);
                    finish();
                }
            });

            alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertBuilder.create().show();
        }
    }

    /**
     * Delete chats from chats table
     * Delete members from members table
     * Delete messages from messages table to delete the chatRoom
     */
    private void deleteChatRoom() {


        chatMembersDb.child(chatRoomId).removeValue();
        chatMessagesDb.child(chatRoomId).removeValue();
        chatRoomDb.child(chatRoomId).removeValue();

    }

    /**
     * Function to update database when any group member leaves or removed from group
     * @param memberId the member is who is leaving the group
     * @param isMemberExist True if the member exist which we're going to remove.
     */
    private void updateDatabaseOnLeaveAndRemove(String memberId, boolean isMemberExist) {

        // Push ChatRoom entries in duplicate chatRoom table to show the old entries of that chatRoom for this user
        String duplicateChatRoomId = duplicateChatRoomsDb.push().getKey();

        HashMap<String, Object> duplicateChatRoomDetails = new HashMap<>();
        duplicateChatRoomDetails.put("lastMessageId", lastMessageId);
        duplicateChatRoomDetails.put("profileImageUrl", groupProfileImage);
        duplicateChatRoomDetails.put("title", groupName);
        duplicateChatRoomDetails.put("updatedAt", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(duplicateChatRoomId, duplicateChatRoomDetails);

        duplicateChatRoomsDb.updateChildren(childUpdates);


        HashMap<String, Object> chatMembersUpdates = new HashMap<>();
        chatMembersUpdates.put("isActive", "0");
        chatMembersUpdates.put("duplicateChatId", duplicateChatRoomId);
        chatMembersDb.child(chatRoomId).child(memberId).updateChildren(chatMembersUpdates);
        chatRoomDb.child(chatRoomId).child("memberCount").setValue(chatRoomMemberCount - 1);
        if (isMemberExist) {
            usersDb.child(memberId).child("chats").child(chatRoomId).setValue("0");
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.groupDetails){

            changeGroupName();
        } else if (view.getId() == R.id.group_image) {

            // Marshmallow permission model
            PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_SINGLE, true, 1, false);
        } else if (view.getId() == R.id.add_members){
            Intent intent = new Intent(mContext, NewConversationActivity.class);
            intent.putExtra("isAddMembers", true);
            intent.putExtra("chatRoomId", chatRoomId);
            intent.putStringArrayListExtra("participants", participantsKeys);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case Constants.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {

                    if (mSelectPath != null) {
                        mSelectPath.clear();
                    }
                    // Getting image path from uploaded images.
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    //Checking if there is any image or not.
                    if (mSelectPath != null){
                        Intent intent = new Intent(this, PhotoPreviewActivity.class);
                        intent.putStringArrayListExtra("image", mSelectPath);
                        startActivityForResult(intent, Constants.UPLOAD_IMAGE);
                    }

                }
                break;

            case Constants.UPLOAD_IMAGE:
                if(resultCode == Constants.UPLOAD_IMAGE){
                    imagePath = Uri.fromFile(new File(data.getStringExtra("imageUri")));
                    changeGroupPhoto();
                }
                break;
        }

    }
}
