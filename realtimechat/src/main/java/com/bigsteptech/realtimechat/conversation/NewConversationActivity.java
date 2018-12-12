package com.bigsteptech.realtimechat.conversation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.contacts.ContactsListFragment;
import com.bigsteptech.realtimechat.contacts.data_model.ContactsList;
import com.bigsteptech.realtimechat.conversation.view.PhotoPreviewActivity;
import com.bigsteptech.realtimechat.groups.data_model.GroupsList;
import com.bigsteptech.realtimechat.groups.ui.PredicateLayout;
import com.bigsteptech.realtimechat.multiimageselector.MultiImageSelectorActivity;
import com.bigsteptech.realtimechat.ui.CircularImageView;
import com.bigsteptech.realtimechat.user.interfaces.OnContactSelectListener;
import com.bigsteptech.realtimechat.utils.AlertDialogWithAction;
import com.bigsteptech.realtimechat.utils.BitmapUtils;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.bigsteptech.realtimechat.utils.PermissionsUtils;
import com.bigsteptech.realtimechat.utils.PhotoUploadingUtils;
import com.bigsteptech.realtimechat.utils.SnackbarUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class NewConversationActivity extends AppCompatActivity implements TextWatcher, OnContactSelectListener,
        View.OnClickListener {

    Fragment fragment;
    private EditText mGroupTitle, addMembers;
    private PredicateLayout predicateLayout;
    private Map<String, String> selectedMembers;
    private DatabaseReference chatRoomDb, chatMembersDb, userDb;
    private String chatRoomId;
    private String selfUid;
    private CircularImageView mGroupImage;
    private Button uploadImageButton;
    private ArrayList<String> mSelectPath = new ArrayList<>();
    private ArrayList<String> participantsKeys = new ArrayList<>();
    private Uri imagePath, imageUri;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    private boolean isAddMembers;
    private AlertDialogWithAction mAlertDialogWithAction;
    private Context mContext;
    private MessengerDatabaseUtils messengerDatabaseUtils;
    long idle_min = 1000; // 2 seconds after user stops typing
    long last_text_edit = 0;
    Handler typingHandler = new Handler();
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        mAlertDialogWithAction = new AlertDialogWithAction(this);
        mGroupTitle = (EditText) findViewById(R.id.groupTitle);
        addMembers = (EditText) findViewById(R.id.addMembers);
        predicateLayout = (PredicateLayout) findViewById(R.id.selectedMembers);
        mGroupImage = (CircularImageView) findViewById(R.id.group_image);
        uploadImageButton = (Button) findViewById(R.id.upload_image);
        addMembers.addTextChangedListener(this);

        addMembers.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        if(getIntent() != null && getIntent().hasExtra("isAddMembers")){
            isAddMembers = getIntent().getBooleanExtra("isAddMembers", false);
            chatRoomId = getIntent().getStringExtra("chatRoomId");
            findViewById(R.id.imageTitleView).setVisibility(View.GONE);
            participantsKeys = getIntent().getStringArrayListExtra("participants");
        }

        mStorageRef = messengerDatabaseUtils.getStorageRef();

        // Use Contacts Fragment to show the members to add in the group
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNewGroupsPage", true);
        bundle.putStringArrayList("participants", participantsKeys);
        fragment = ContactsListFragment.newInstance(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        ((ContactsListFragment)fragment).setmContactSelectedListener(this);

        selectedMembers = new HashMap<>();
        chatRoomDb = messengerDatabaseUtils.getChatsDatabase();
        chatMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        userDb = messengerDatabaseUtils.getUsersDatabase();
        selfUid = messengerDatabaseUtils.getCurrentUserId();

        mGroupImage.setOnClickListener(this);
        uploadImageButton.setOnClickListener(this);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + idle_min - 500)) {
                // user hasn't changed the EditText for longer than
                // the min delay (with half second buffer window)

                // Set typing indicator to false after 1 seconds of user stopped typing
                ((ContactsListFragment)fragment).filterUsers(searchText);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(typingHandler != null){
            typingHandler.removeCallbacksAndMessages(input_finish_checker);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isAddMembers){
            menu.findItem(R.id.submit).setVisible(false);
            menu.findItem(R.id.add_to_group).setVisible(true);
        } else {
            menu.findItem(R.id.add_to_group).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.submit) {
            if(mGroupTitle.getText().toString().isEmpty() ){
                Snackbar.make(findViewById(R.id.activity_new_conversation),
                        mContext.getResources().getString(R.string.enter_group_name), Snackbar.LENGTH_SHORT).show();
            } else if(selectedMembers.size() < 2){
                Snackbar.make(findViewById(R.id.activity_new_conversation),
                        mContext.getResources().getString(R.string.member_count_error_msg), Snackbar.LENGTH_SHORT).show();
            } else {
                //displaying a progress dialog while upload is going on
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Creating Group");
                progressDialog.show();

                if(imagePath != null){
                    uploadGroupImage();
                } else {
                    createNewChatRoom();
                }
                // Create New Group with Details of Members and title/image of the group
            }
        } else if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        } else if (item.getItemId() == R.id.add_to_group) {
            addMembersToGroup();
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadGroupImage(){

        StorageReference riversRef = mStorageRef.child("images/" + UUID.randomUUID() + ".jpg");
        riversRef.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        imageUri = taskSnapshot.getDownloadUrl();
                        createNewChatRoom();

                        //and displaying a success toast
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

    private void createNewChatRoom() {


        // create New ChatRoom here with Members
        chatRoomId = chatRoomDb.push().getKey();
        GroupsList groupsList;

        final HashMap<String, Object> userChats = new HashMap<>();
        userChats.put(chatRoomId, "1");

        LinkedHashMap<String, Object> chatParticipants = new LinkedHashMap<>();
        LinkedHashMap<String, String> isDeletedKey = new LinkedHashMap<>();
        isDeletedKey.put("isDeleted", "0");
        HashMap<String, Object> chatMembers = new HashMap<>();
        HashMap<String, Object> memberDetails = new HashMap<>();
        memberDetails.put("isActive", "1");
        memberDetails.put("isTyping", "0");
        memberDetails.put("mute", 0);
        memberDetails.put("resourceType", "user");
        memberDetails.put("deleted", "0");
        memberDetails.put("updatedAt", ServerValue.TIMESTAMP);

        for(String key : selectedMembers.keySet()){
            userDb.child(key).child("chats").updateChildren(userChats);
            chatParticipants.put(key, isDeletedKey);
            chatMembers.put(key, memberDetails);
        }

        userDb.child(selfUid).child("chats").updateChildren(userChats);
        chatParticipants.put(selfUid, isDeletedKey);
        chatMembers.put(selfUid, memberDetails);

        if(imageUri != null){
            groupsList = new GroupsList(selfUid, selectedMembers.size() + 1, "1", "user",
                    imageUri.toString(), mGroupTitle.getText().toString(), chatParticipants);
        } else {
            groupsList = new GroupsList(selfUid, selectedMembers.size() + 1, "1", "user", null,
                    mGroupTitle.getText().toString(), chatParticipants);
        }

        Map<String, Object> postValues = groupsList.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(chatRoomId, postValues);
        chatRoomDb.updateChildren(childUpdates);

        chatMembersDb.child(chatRoomId).updateChildren(chatMembers);

        progressDialog.dismiss();

        Intent intent = new Intent(mContext, ConversationActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("typeOfChat", 1);
        intent.putExtra(Constants.SENDER, selfUid);

        finish();
        startActivity(intent);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//        if(charSequence.length() > 0) {
//            last_text_edit = System.currentTimeMillis();
//            searchText = charSequence.toString();
//            typingHandler.postDelayed(input_finish_checker, idle_min);
//        } else {
//
//        }
        if(charSequence.length() == 0) {
            ((ContactsListFragment)fragment).filterUsers(charSequence.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onContactSelected(final ContactsList user) {

        if(user.isContactSelected()){
            if(predicateLayout.findViewWithTag(user.getmUserId()) == null){
                View likeCommentView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                        inflate(R.layout.selected_members_view, predicateLayout, false);

                CircularImageView userImage = (CircularImageView) likeCommentView.findViewById(R.id.user_image);
                TextView userName = (TextView) likeCommentView.findViewById(R.id.userName);
                ImageView cancelImageView = (ImageView) likeCommentView.findViewById(R.id.cancel);

                userName.setText(user.getmUserTitle());

                Picasso.with(this)
                        .load(user.getmUserImage())
                        .placeholder(R.drawable.person_image_empty)
                        .into(userImage);

                likeCommentView.setTag(user.getmUserId());
                cancelImageView.setTag(user.getmUserId());

                cancelImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(predicateLayout.findViewWithTag(user.getmUserId()) != null){
                            predicateLayout.removeView(predicateLayout.findViewWithTag(user.getmUserId()));
                            selectedMembers.remove(user.getmUserId());
                        }
                        ((ContactsListFragment)fragment).notifyAdapter(user);
                    }
                });

                selectedMembers.put(user.getmUserId(), user.getmUserTitle());

                predicateLayout.setVisibility(View.VISIBLE);

                predicateLayout.addView(likeCommentView, new PredicateLayout.LayoutParams(4, 4));
            }

        } else {
            if(predicateLayout.findViewWithTag(user.getmUserId()) != null){
                predicateLayout.removeView(predicateLayout.findViewWithTag(user.getmUserId()));
                selectedMembers.remove(user.getmUserId());
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.group_image || view.getId() == R.id.upload_image) {
            if(!PermissionsUtils.checkManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                PermissionsUtils.requestForManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Constants.READ_EXTERNAL_STORAGE);
            }else{
                PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_SINGLE, true, 1, false);
            }
        }
    }

    private void performSearch () {
        String srcText = addMembers.getText().toString();
        if(!srcText.isEmpty()) {
            ((ContactsListFragment)fragment).filterUsers(srcText);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_SINGLE, true, 1, false);
                } else{
                    // If user press deny in the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Constants.READ_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, findViewById(R.id.activity_new_conversation),
                                Constants.READ_EXTERNAL_STORAGE);

                    }
                }
                break;

        }
    }

    private void addMembersToGroup() {

        if(selectedMembers.size() == 0){
            Snackbar.make(findViewById(R.id.activity_new_conversation),
                    "Select member's name to add them to this group", Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> chatMembers = new HashMap<>();
            final HashMap<String, Object> userChats = new HashMap<>();
            userChats.put(chatRoomId, "1");

            final DatabaseReference participantsRef = chatRoomDb.child(chatRoomId).child("participants");

            HashMap<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("isActive", "1");
            memberDetails.put("duplicateChatId", null);
            memberDetails.put("isTyping", "0");
            memberDetails.put("mute", 0);
            memberDetails.put("resourceType", "user");
            memberDetails.put("updatedAt", ServerValue.TIMESTAMP);

            for(String key : selectedMembers.keySet()){
                chatMembers.put(key, memberDetails);
                userDb.child(key).child("chats").updateChildren(userChats);
                participantsRef.child(key).child("isDeleted").setValue("0");
            }

            chatMembersDb.child(chatRoomId).updateChildren(chatMembers);

            chatRoomDb.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    chatRoomDb.child(chatRoomId).child("memberCount").setValue(dataSnapshot.
                            child("memberCount").getValue(Integer.class) + selectedMembers.size());
                    userDb.updateChildren(userChats);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            finish();
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

                } else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
//                    Toast.makeText(getApplicationContext(),
//                            getResources().getString(R.string.image_capturing_failed),
//                            Toast.LENGTH_SHORT).show();
                } else {
                    // User cancel the process
                    /**
                     * Finish this activity if Photo Option get clicked from Main Feed page
                     * And if user press back button on photoUploading, so as to show Feedpage again
                     */
                }
                break;

            case Constants.UPLOAD_IMAGE:
                if(resultCode == Constants.UPLOAD_IMAGE){
                    Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(this, mSelectPath.get(0));
                    mGroupImage.setImageBitmap(bitmap);
                    mGroupImage.setVisibility(View.VISIBLE);
                    uploadImageButton.setVisibility(View.GONE);
                    imagePath = Uri.fromFile(new File(data.getStringExtra("imageUri")));

                }
                break;
        }

    }
}
