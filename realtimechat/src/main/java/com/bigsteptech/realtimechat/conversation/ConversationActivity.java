package com.bigsteptech.realtimechat.conversation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigsteptech.realtimechat.Constants;
import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.Utils;
import com.bigsteptech.realtimechat.conversation.adapter.ConversationMessageAdapter;
import com.bigsteptech.realtimechat.conversation.data_model.Conversation;
import com.bigsteptech.realtimechat.conversation.data_model.Message;
import com.bigsteptech.realtimechat.conversation.database.FirebaseConversationDatabase;
import com.bigsteptech.realtimechat.conversation.service.ConversationService;
import com.bigsteptech.realtimechat.conversation.service.PersistedConversationService;
import com.bigsteptech.realtimechat.conversation.view.BadgeLayout;
import com.bigsteptech.realtimechat.groups.GroupDetails;
import com.bigsteptech.realtimechat.groups.data_model.GroupsList;
import com.bigsteptech.realtimechat.interfaces.OnDeleteClearChat;
import com.bigsteptech.realtimechat.interfaces.OnRetryClicked;
import com.bigsteptech.realtimechat.interfaces.OnSongPlay;
import com.bigsteptech.realtimechat.interfaces.RecyclerItemClickListener;
import com.bigsteptech.realtimechat.listners.FirebaseObservableListeners;
import com.bigsteptech.realtimechat.multiimageselector.MultiImageSelectorActivity;
import com.bigsteptech.realtimechat.service.MusicService;
import com.bigsteptech.realtimechat.user.User;
import com.bigsteptech.realtimechat.user.database.FirebaseUserDatabase;
import com.bigsteptech.realtimechat.user.service.PersistedUserService;
import com.bigsteptech.realtimechat.user.service.UserService;
import com.bigsteptech.realtimechat.user.userProfile;
import com.bigsteptech.realtimechat.utils.AlertDialogWithAction;
import com.bigsteptech.realtimechat.utils.DialogUtils;
import com.bigsteptech.realtimechat.utils.GlobalFunctionsUtil;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;
import com.bigsteptech.realtimechat.utils.PermissionsUtils;
import com.bigsteptech.realtimechat.utils.PhotoUploadingUtils;
import com.bigsteptech.realtimechat.utils.SnackbarUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.himanshuvirmani.androidcache.CacheManager;
import com.himanshuvirmani.androidcache.Result;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import rx.Subscriber;
import rx.Subscription;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnDeleteClearChat {

    private DatabaseReference chatRoomDb, chatMessagesDb, userDb, chatRoomMembersDb,
            blockedListDb, friendListDb, duplicateChatRoomsDb, filesDb;
    private String sender, destination;
    private ImageButton sendButton, attachmentOptionsButton;
    private EditText messageEditText;
    public static String chatRoomId;
    private RecyclerView messageRecyclerView;
    private TextView currentMessageDateView;
    private ConversationMessageAdapter conversationMessageAdapter;
    private PopupWindow optionPopup;
    private ArrayList<String> mSelectPath = new ArrayList<>();
    private ArrayList<String> uploadedImagesUrl = new ArrayList<>();
    private Uri imagePath, videoPath, audioPath, documentPath;
    private StorageReference mStorageRef;
    private Subscription subscription, typingSubscription;
    private UserService userService;
    private ConversationService conversationService;
    private int isUserOnline, visibility;
    private long userLastSeen;
    private boolean isTyping = false;
    private ArrayList<Message> messagesList = new ArrayList<>();
    private int typeOfChat =  1;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private int clickedPosition;

    long idle_min = 3000; // 3 seconds after user stops typing
    long last_text_edit = 0;
    Handler typingHandler = new Handler();

    List<String> participantsKey = new ArrayList<>();
    HashMap<String, Object> recipients = new HashMap<>();
    HashMap<String, String> participantImages = new HashMap<>();
    HashMap<String, String> participantNames = new HashMap<>();
    private String nameOfParticipants;
    private String senderName, senderImage;
    private AlertDialogWithAction mAlertDialogWithAction;
    private static int firstVisibleInListview = -1;
    private Context mContext;
    private String lastSeenMessageId;
    private boolean isLastPositionReached;

    private ArrayList<String> blockedContactsListOfSender = new ArrayList<>();
    private int LIMIT = 30;
    private int pageNumber = 1;
    private String oldestKeyYouveSeen, tempOldestKey;
    private long oldestKeyTimeStamp;
    private String typingParticipantName;
    private int newMessageCount;
    private boolean isSenderActive = true, isSenderBlocked = false;
    private RelativeLayout sendMessageBlock;
    private boolean isOldestKeyNull = false;
    private ValueEventListener mListener;
    private DatabaseReference mMessagesReference;
    private String selectedAttachmentOption;
    private MessengerDatabaseUtils messengerDatabaseUtils;
    private ProgressBar progressBar;
    private boolean isDeleted = false, isUserBlocked = false;
    private HashMap<String, HashMap<DatabaseReference, ChildEventListener>> mListenerMap = new HashMap<>();
    private HashMap<Query, ChildEventListener> mMessageListenerMap = new HashMap<>();
    private RelativeLayout newMessageLayout;
    private BadgeLayout newMessageCountBadge;
    private int unReadMessageCount;
    private int counter = 1;
    private boolean isFirstRequest = false;
    private int itemIdOfMessages = 1;
    private CacheManager cacheManager;

    ActionMode mActionMode;
    Menu context_menu;
    public static boolean isMultiSelect = false;
    boolean isLastMsgDeleted = false;
    public static boolean IS_VISIBLE = false;
    List<String> messageKeys = new ArrayList<>();

    private static final int MENU_BLOCK = Menu.FIRST;
    private static final int MENU_VIEW_PROFILE = Menu.FIRST + 1;
    private static final int MENU_DELETE = Menu.FIRST + 2;
    private static final int MENU_CLEAR = Menu.FIRST + 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        mAlertDialogWithAction = new AlertDialogWithAction(this);

        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        cacheManager = messengerDatabaseUtils.getCacheManagerInstance();
        GlobalFunctionsUtil.setmOnDeleteClearChat(this);
        FirebaseObservableListeners firebaseObservableListeners = new FirebaseObservableListeners();
        FirebaseUserDatabase userDatabase = new FirebaseUserDatabase(messengerDatabaseUtils.getMessengerDatabaseInstance(),
                firebaseObservableListeners);
        FirebaseConversationDatabase conversationDatabase = new FirebaseConversationDatabase
                (messengerDatabaseUtils, firebaseObservableListeners);

        userService = new PersistedUserService(userDatabase);
        conversationService = new PersistedConversationService(conversationDatabase);

        sendMessageBlock = (RelativeLayout) findViewById(R.id.sendMessageBlock);
//        currentMessageDateView = (TextView) findViewById(R.id.currentMessageDateView);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        attachmentOptionsButton = (ImageButton) findViewById(R.id.attachmentButton);
        attachmentOptionsButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        messageEditText.addTextChangedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        messageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        newMessageLayout = (RelativeLayout) findViewById(R.id.newMessageUi);
        newMessageLayout.setVisibility(View.GONE);
        newMessageCountBadge = (BadgeLayout) findViewById(R.id.newMessageCount);
        newMessageCountBadge.setText(String.valueOf(3));
        newMessageLayout.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) messageRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        messageRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, messageRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect){
                    multi_select(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    ConversationMessageAdapter.selectedMessages = new ArrayList<>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));

//        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();

        userDb = messengerDatabaseUtils.getUsersDatabase();
        chatRoomDb = messengerDatabaseUtils.getChatsDatabase();
        chatRoomMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        chatMessagesDb = messengerDatabaseUtils.getChatMessagesDatabase();
        blockedListDb = messengerDatabaseUtils.getBlockedListDatabase();
        friendListDb = messengerDatabaseUtils.getFriendsDatabase();
        duplicateChatRoomsDb = messengerDatabaseUtils.getDuplicateChatsDatabase();
        filesDb = messengerDatabaseUtils.getFilesDatabase();
        mStorageRef = messengerDatabaseUtils.getStorageRef();

        sender = getIntent().hasExtra(Constants.SENDER) ? getIntent().getStringExtra(Constants.SENDER) :
                messengerDatabaseUtils.getCurrentUserId() ;
        destination = getIntent().getStringExtra(Constants.DESTINATION);
        typeOfChat = getIntent().getIntExtra("typeOfChat", 0);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chatRoomId = null;
        if(getIntent() != null && getIntent().hasExtra("chatRoomId")){
            chatRoomId = getIntent().getStringExtra("chatRoomId");
        }
        Log.d(ConversationActivity.class.getSimpleName(), " chatRoomId: " + chatRoomId);

        getNameImageOfSender();

        if(typeOfChat != 1){
            getDestination();
            getBlockedContactsList();
        } else {
            setupToolBar();
        }

        conversationMessageAdapter = new ConversationMessageAdapter(mContext, sender, LayoutInflater.from(this), new OnSongPlay() {
            @Override
            public void onPlayButtonClicked(int position, String songUrl) {

//                clickedPosition = position;
//                Log.d(ConversationActivity.class.getSimpleName(), "onPlayButtonClicked called"  + " songUrl " +
//                        songUrl);
//
//                musicSrv.playSong(songUrl);
            }
        }, new OnRetryClicked() {
            @Override
            public void onRetryViewClicked(Message message) {

                // Called when retry view of failed image will be clicked
                reUploadFailedImage(message);

            }
        });

        Message message = new Message(false);
        message.setItemId(itemIdOfMessages++);
        messagesList.add(new Message(false));
        conversationMessageAdapter.update(messagesList);
        messageRecyclerView.setAdapter(conversationMessageAdapter);
        messageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) messageRecyclerView
                        .getLayoutManager();

                int firstVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if(firstVisible == 0 && oldestKeyYouveSeen != null && !isFirstRequest) {
//                    loadMoreMessages(false);
                } else if (lastVisiblePosition == conversationMessageAdapter.getItemCount() - 1){
                    unReadMessageCount = 0;
                    newMessageLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        if(chatRoomId != null){
            getParticipants();
            fetchChatMessages();
        } else {
            checkExistingChatRoom();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        // close soft keyboard
        View view = ((Activity) mContext).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        if(ConversationMessageAdapter.medialPlayerHandlers != null && ConversationMessageAdapter.
                medialPlayerHandlers.size() != 0) {

            for (Handler handler : ConversationMessageAdapter.medialPlayerHandlers.keySet()) {
                handler.removeCallbacks(ConversationMessageAdapter.medialPlayerHandlers.get(handler));
            }
        }


        if(ConversationMessageAdapter.medialPlayers != null && ConversationMessageAdapter.medialPlayers.size() != 0) {

            for (int i = 0 ; i < ConversationMessageAdapter.medialPlayers.size(); i++) {
                MediaPlayer mediaPlayer = ConversationMessageAdapter.medialPlayers.get(i);
                if (mediaPlayer != null){
                    try{
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                    } catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                    mediaPlayer.release();
                }
            }
        }


        removeAllListeners();
    }

    private void reUploadFailedImage(final Message message) {

        StorageReference riversRef = mStorageRef.child("images/" + UUID.randomUUID() + ".jpg");

        Uri imagePath = Uri.fromFile(new File(message.getTemporaryPath()));
        riversRef.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog

                        HashMap<String, Object> fileMetaData = new HashMap<>();
                        fileMetaData.put("mimeType", taskSnapshot.getMetadata().getContentType());
                        fileMetaData.put("extension", "jpg");
                        fileMetaData.put("size", formatSize(taskSnapshot.getMetadata().getSizeBytes()));
                        fileMetaData.put("fileUrl", taskSnapshot.getDownloadUrl().toString());

                        HashMap<String, Object> fileUpdates = new HashMap<>();
                        fileUpdates.put(message.getAttachmentId(), fileMetaData);

                        filesDb.updateChildren(fileUpdates);

                        HashMap<String, Object> chatMessageUpdates = new HashMap<>();
                        chatMessageUpdates.put("recipients", recipients);
                        chatMessagesDb.child(chatRoomId).child(message.getMessageId()).updateChildren(chatMessageUpdates);

                        //and displaying a success toast
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successfull
                        //hiding the progress dialog
//                            progressDialog.dismiss();

                        //and displaying error message
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });

    }

    private void getNameImageOfSender() {

        userDb.child(sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderName = dataSnapshot.child("name").getValue(String.class);
                senderImage = dataSnapshot.child(Constants.FIREBASE_IMAGE_KEY).getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getBlockedContactsList(){

        blockedListDb.child(sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blockedContactsListOfSender.clear();
                isUserBlocked = false;

                for(DataSnapshot blockedContact :  dataSnapshot.getChildren()){

                    blockedContactsListOfSender.add(blockedContact.getKey());
                    if(blockedContactsListOfSender.size() == dataSnapshot.getChildrenCount()){

                        if(blockedContactsListOfSender.contains(destination)){
                            isUserBlocked = true;
                            hideMessageBox(mContext.getResources().getString(R.string.blocked_message));
                        }
                        invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideMessageBox(String messageToShow){
        attachmentOptionsButton.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        messageEditText.setEnabled(false);
        messageEditText.setHint(messageToShow);
    }

    private void showMessageBox() {

        attachmentOptionsButton.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        messageEditText.setEnabled(true);
        messageEditText.setHint(mContext.getResources().getString(R.string.type_message));
    }

    private void getDestination() {

        if(destination == null || destination.isEmpty()){


            chatRoomDb.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    DataSnapshot participants = dataSnapshot.child("participants");
                    for(DataSnapshot participant :  participants.getChildren()){
                        if(!participant.getKey().equals(sender)){
                            destination = participant.getKey();
                            setupToolBar();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            setupToolBar();
        }
    }

    // todo for next release
    private void getParticipants() {

        if(chatRoomId != null){
            chatRoomMembersDb.child(chatRoomId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    final int[] i = {0};
                    recipients.clear();
                    isSenderBlocked = false;
                    final String[] nameOfParticipants = {""};
                    HashMap<String, Object> recipientValues = new HashMap<>();
                    recipientValues.put("isExists", ServerValue.TIMESTAMP);
                    recipientValues.put("status", "1");

                    for(final DataSnapshot participant : dataSnapshot.getChildren()){

                        if(participant.hasChild("isActive") && participant.child("isActive").
                                getValue(String.class).equals("1")){

                            if(participant.getKey().equals(sender)){
                                isSenderActive = true;
                            }
                            recipients.put(participant.getKey(), recipientValues);

                            if(!participant.getKey().equals(sender) && !participantsKey.contains(participant.getKey())){
                                participantsKey.add(participant.getKey());
                            }
                        } else if(participant.getKey().equals(sender)){
                            isSenderActive = false;
                            invalidateOptionsMenu();
                            setToolbarSubTitle();
                        } else if (destination != null && participant.getKey().equals(destination)){
                            isSenderBlocked = true;
                            setToolbarSubTitle();
                        }

                        userDb.child(participant.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userDataSnapshot) {

                                if(!userDataSnapshot.exists()){
                                    participantImages.put(userDataSnapshot.getKey(), null);
                                    participantNames.put(userDataSnapshot.getKey(), mContext.getResources().
                                            getString(R.string.deleted_user));
                                } else {
                                    participantImages.put(userDataSnapshot.getKey(), userDataSnapshot.child(Constants.
                                            FIREBASE_IMAGE_KEY).getValue(String.class));
                                    String userName = userDataSnapshot.child("name").getValue(String.class);
                                    participantNames.put(userDataSnapshot.getKey(), userName);

                                    if(participantsKey.contains(userDataSnapshot.getKey()) ||
                                            (userDataSnapshot.getKey().equals(sender) && isSenderActive)){

                                        if(i[0] < dataSnapshot.getChildrenCount() - 1){
                                            nameOfParticipants[0] += (userDataSnapshot.getKey().equals(sender) ?
                                                    mContext.getResources().getString(R.string.self_member_text) : userName.split("\\s+")[0]) + ", ";
                                        } else{
                                            nameOfParticipants[0] += (userDataSnapshot.getKey().equals(sender) ?
                                                    mContext.getResources().getString(R.string.self_member_text) : userName.split("\\s+")[0]);
                                        }
                                    }
                                }

                                if(i[0] == dataSnapshot.getChildrenCount()-1){
                                    if(getSupportActionBar() != null && typeOfChat == 1 && isSenderActive){
                                        getSupportActionBar().setSubtitle(nameOfParticipants[0]);
                                    }
                                    setTypingSubscription();
                                }

                                i[0]++;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if((isSenderActive && typeOfChat == 1) || (typeOfChat == 0 && !isUserBlocked)) {
                        showMessageBox();
                    } else if(isUserBlocked){
                        hideMessageBox(mContext.getResources().getString(R.string.blocked_message));
                    } else {
                        hideMessageBox(mContext.getResources().getString(R.string.left_group_message));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            HashMap<String, Object> recipientValues = new HashMap<>();
            recipientValues.put("isExists", ServerValue.TIMESTAMP);
            recipientValues.put("status", "1");

            recipients.put(sender, recipientValues);
            recipients.put(destination, recipientValues);
        }

    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + idle_min - 500)) {
                // user hasn't changed the EditText for longer than
                // the min delay (with half second buffer window)

                // Set typing indicator to false after 5 seconds of user stopped typing
                conversationService.setTyping(chatRoomId, sender, "0");
            }
        }
    };

    //connect to the service
//    private ServiceConnection musicConnection = new ServiceConnection(){
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
//            //get service
//            musicSrv = binder.getService();
//            //pass list
////            musicSrv.setList(songList);
//            musicBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicBound = false;
//        }
//    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IS_VISIBLE = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_VISIBLE = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        IS_VISIBLE = false;
        conversationService.setTyping(chatRoomId, sender, "0");
        if(typingSubscription != null){
            typingSubscription.unsubscribe();
        }

        if(subscription != null){
            subscription.unsubscribe();
        }

        if(typingHandler != null){
            typingHandler.removeCallbacksAndMessages(input_finish_checker);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IS_VISIBLE = false;
    }

    private void setupToolBar() {


        if(typeOfChat == 1){

            chatRoomDb.child(chatRoomId).child("title").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(getSupportActionBar() != null){
                        getSupportActionBar().setTitle(dataSnapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            userService.getUser(destination)
                    .subscribe(new Subscriber<User>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(final User user) {

                            if(getSupportActionBar() != null){
                                isDeleted = user == null;
                                invalidateOptionsMenu();
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                getSupportActionBar().setTitle(user != null ? user.getName() : mContext.getResources().
                                        getString(R.string.deleted_user));
                                isUserOnline = user != null ? user.getIsOnline() : 0;
                                visibility = user != null ? user.getVisibility() : 0;
                                userLastSeen = user != null ? user.getLastSeen() : 0;
                                setToolbarSubTitle();
                            }
                        }
                    });
        }

    }

    private void setToolbarSubTitle() {

        // Show subtitle i.e. online, typing status of the user only if the sender is not blocked by destination
        if((typeOfChat == 0 && !isSenderBlocked && isSenderActive) || (typeOfChat == 1 && isSenderActive) ){
            if(isTyping){
                if(typeOfChat == 1)
                    getSupportActionBar().setSubtitle(String.format(mContext.getResources().
                            getString(R.string.user_typing), typingParticipantName));
                else
                    getSupportActionBar().setSubtitle(mContext.getResources().getString(R.string.typing));
            } else if(typeOfChat == 0) {
                if(visibility == 1) {
                    if(isUserOnline == 1) {
                        String onlineText = mContext.getResources().getString(R.string.online);
                        onlineText = Character.toLowerCase(onlineText.charAt(0)) + onlineText.substring(1);
                        getSupportActionBar().setSubtitle(onlineText);
                    } else {
                        //Show Last Seen
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                        String timestamp = sdf.format(new Date(userLastSeen));
                        String today = Utils.getCurrentDate();
                        String[] time1 = timestamp.split("/");
                        String[] time2 = today.split("/");
                        if ((time1[0] + time1[1] + time1[2]).equals(time2[0] + time2[1] + time2[2])) {
                            String s = getResources().getString(R.string.lastseen_today);
                            getSupportActionBar().setSubtitle(s.replace("time",time1[3] + ":" + time1[4]));
                        } else {
                            String s = getResources().getString(R.string.lastseen_date);
                            s = s.replace("date", time1[2] + "/" + time1[1]);
                            s = s.replace("time", time1[3] + ":" + time1[4]);
                            getSupportActionBar().setSubtitle(s);
                        }
                    }
                } else {
                    getSupportActionBar().setSubtitle("");
                }
            }
        } else {
            getSupportActionBar().setSubtitle("");
        }
    }

    private void checkExistingChatRoom() {

        /*
            check if chatRoom is already exist for these 2 users
            else create a new chatRoom
         */
        chatRoomDb.orderByChild("participants/" + sender + "/isDeleted").equalTo("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot groupDetails : dataSnapshot.getChildren()) {

                    DataSnapshot participants = groupDetails.child("participants");
                    if(participants.getChildrenCount() == 2 && participants.hasChild(destination)){
                        chatRoomId = groupDetails.getKey();
                        Log.d(ConversationActivity.class.getSimpleName(), "chatRoomId: " + chatRoomId);
                        getParticipants();
                        fetchChatMessages();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fetchChatMessages(){

        progressBar.setVisibility(View.VISIBLE);
        if(chatRoomId != null){

            chatRoomMembersDb.child(chatRoomId).child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("lastSeenMessageId")){
                        lastSeenMessageId = dataSnapshot.child("lastSeenMessageId").getValue(String.class);
                    }

                    if(dataSnapshot.hasChild("newMessageCount")){
                        newMessageCount = dataSnapshot.child("newMessageCount").getValue(Integer.class);
                    }

                    Result<Message> messages = cacheManager.get("messages_" + chatRoomId, Message.class);

                    if(messages.getCachedObject() == null || messages.getCachedObject().
                            getMessageArrayList().size() == 0 || messages.getCachedObject().
                            getMessageArrayList().size() == 1) {

                        Log.d(ConversationActivity.class.getSimpleName(), "Not exist in Cache");

                        Query query = chatMessagesDb.child(chatRoomId).orderByChild("recipients/" + sender + "/isExists").
                                limitToLast(LIMIT + newMessageCount);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount() > 0){
                                    addMessagesToList(dataSnapshot, false, 1, false, false);
                                } else {
                                    showNewReceivedMessages();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Log.d(ConversationActivity.class.getSimpleName(), "Exists in Cache");
                        Log.d(ConversationActivity.class.getSimpleName(), "Size of cache messages" + messages.getCachedObject().getMessageArrayList().size());
                        progressBar.setVisibility(View.GONE);
                        messagesList = new ArrayList<>(messages.getCachedObject().getMessageArrayList());
                        conversationMessageAdapter.update(messagesList);
                        scrollRecyclerView(false);

                        oldestKeyTimeStamp = messagesList.get(1).getMessageTimeStamp();
                        oldestKeyYouveSeen = messagesList.get(1).getMessageId();

                        for(int i = 1; i < messagesList.size(); i++) {
                            Message message = messagesList.get(i);
                            messageKeys.add(message.getMessageId());
                        }

                        long lastMessageTimeStamp = messagesList.get(messagesList.size() - 1).getMessageTimeStamp();

                        Query query = chatMessagesDb.child(chatRoomId).orderByChild("recipients/" + sender + "/isExists").
                                startAt(lastMessageTimeStamp);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                itemIdOfMessages = messagesList.size() + 1;

                                if(dataSnapshot.getChildrenCount() > 0){
                                    counter = messagesList.size();
                                    addMessagesToList(dataSnapshot, false, messagesList.size(), true, false);
                                } else {
                                    showNewReceivedMessages();
                                    progressBar.setVisibility(View.GONE);
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
    }

    private void loadMoreMessages(final boolean updateLastMessage) {

        pageNumber++;

        // Show Progress bar at the top when sending a request to load more items
//        if(conversationMessageAdapter.getConversationList() != null){
//            Message message = conversationMessageAdapter.getConversationList().get(0);
//            message.setShowProgressBar(true);
//            conversationMessageAdapter.notifyItemChanged(0);
//            messageRecyclerView.smoothScrollToPosition(0);
//        }

        chatMessagesDb.child(chatRoomId).orderByChild("recipients/" + sender + "/isExists").endAt(oldestKeyTimeStamp).limitToLast(LIMIT + 1).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Hide Progress bar at the top after response came
                        if(conversationMessageAdapter.getConversationList() != null){
                            Message message = conversationMessageAdapter.getConversationList().get(0);
                            message.setShowProgressBar(false);
                            conversationMessageAdapter.notifyItemChanged(0);
                        }

                        /*
                            Set the oldestKeyYouveSeen to null if there are no more messages in this conversation
                             so as to stop the calling again
                         */
                        if(dataSnapshot.getChildrenCount() > 1 ){
                            addMessagesToList(dataSnapshot, true, 1, false, updateLastMessage);
                        } else {
                            if(updateLastMessage) {
                                // Update the last message for this user in members table
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("updatedAt", ServerValue.TIMESTAMP);
                                childUpdates.put("lastMessageId", "false");
                                chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(childUpdates);
                            }
                            oldestKeyYouveSeen = null;
                            oldestKeyTimeStamp = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addMessagesToList(DataSnapshot dataSnapshot, final boolean loadMoreData,
                                   int position, boolean loadingFromCache, boolean updateLastMessage) {
        int i = 0, j = 0;

        tempOldestKey = oldestKeyYouveSeen;
        boolean isMessageExists = false;

        if(!loadMoreData) isFirstRequest = true;
        for (DataSnapshot messages : dataSnapshot.getChildren()) {

            j++;
            final int finalJ = j;
            final Message message = messages.getValue(Message.class);
            /*
                Set the first key of messages as the oldest key, which will be used to fetch the older results
             */
            if(!loadingFromCache && i == 0 && !isOldestKeyNull){
                if(messages.child("recipients").hasChild(sender)){
                    oldestKeyYouveSeen = messages.getKey();
                    oldestKeyTimeStamp = message.getCreatedAt();
                } else {
                    oldestKeyYouveSeen = null;
                }
            }

            if(messages.hasChild("recipients") && messages.child("recipients").hasChild(sender)){

                isMessageExists = true;
                DataSnapshot recipients = messages.child("recipients");

                message.setUserImage(participantImages.get(message.getOwnerId()));
                message.setUserName(participantNames.get(message.getOwnerId()));
                message.setMessageId(messages.getKey());
                if(recipients.child(sender).hasChild("isExists")) {
                    message.setMessageTimeStamp(recipients.child(sender).child("isExists").getValue(Long.class));
                }
                // Set Status of Message
                if(!messageKeys.contains(messages.getKey())) {
                    setStatusOfMessage(messages, message);
                }

                if(!messagesList.contains(message)){
                    message.setItemId(itemIdOfMessages++);
                    messageKeys.add(messages.getKey());
                    messagesList.add(position++, message);

                    if(message.getAttachmentId() != null){
                        switch (message.getAttachmentType()){

                            case "image":
                            case "audio":
                            case "video":
                            case "file":
                                filesDb.child(message.getAttachmentId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("temporaryPath")){
                                            message.setTemporaryPath(dataSnapshot.child("temporaryPath").getValue(String.class));
                                        } else {
                                            message.setFileUrl(dataSnapshot.child("fileUrl").getValue(String.class));
                                            if(message.getAttachmentType().equals("video")){
                                                message.setVideoThumbnail(dataSnapshot.child("thumbnailUrl").getValue(String.class));
                                            } else if(message.getAttachmentType().equals("audio")){
                                                message.setAudioDuration(dataSnapshot.hasChild("duration") ?
                                                        dataSnapshot.child("duration").getValue(Double.class) : 0);
                                            }
                                        }
                                        counter ++;

                                        if(counter == messagesList.size()) {
                                            conversationMessageAdapter.update(messagesList);
                                            scrollRecyclerView(loadMoreData);
                                            Message listMessage = new Message(messagesList);
                                            cacheManager.put("messages_" + chatRoomId, listMessage);
                                            Log.d(ConversationActivity.class.getSimpleName(), "Coming here 1");
                                            showNewReceivedMessages();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;

                            case "link":
                                break;
                        }
                    } else {
                        counter ++;
                    }
                }
                i++;

                if(finalJ >= dataSnapshot.getChildrenCount() -1){
                    progressBar.setVisibility(View.GONE);
                    HashMap<String, Object> membersUpdateMap = new HashMap<>();
                    if(updateLastMessage) {
                        // Update the last message for this user in members table
                        membersUpdateMap.put("updatedAt", message.getCreatedAt());
                        membersUpdateMap.put("lastMessageId", message.getMessageId());
                    }
                    membersUpdateMap.put("newMessageCount", null);
                    membersUpdateMap.put("lastSeenMessageId", message.getMessageId());
                    chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(membersUpdateMap);
                }
            } else {
                isOldestKeyNull = true;
            }

            /**
             * update newMessageCount and lastSeenMessageId after loading all messages.
             */
            if(finalJ >= dataSnapshot.getChildrenCount() -1){
                progressBar.setVisibility(View.GONE);
                HashMap<String, Object> membersUpdateMap = new HashMap<>();

                if(updateLastMessage) {
                    // Update the last message for this user in members table
                    membersUpdateMap.put("updatedAt", message.getCreatedAt());
                    membersUpdateMap.put("lastMessageId", isMessageExists ? message.getMessageId() : "false");
                }
                membersUpdateMap.put("newMessageCount", null);
                membersUpdateMap.put("lastSeenMessageId", message.getMessageId());
                chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(membersUpdateMap);
            }
        }

        if(counter == messagesList.size() && messagesList.size() != 1) {
            conversationMessageAdapter.update(messagesList);
            scrollRecyclerView(loadMoreData);
            Message listMessage = new Message(messagesList);
            cacheManager.put("messages_" + chatRoomId, listMessage);
            showNewReceivedMessages();
        } else if (counter == messagesList.size()) {
            showNewReceivedMessages();
        }
    }

    private void scrollRecyclerView(boolean loadMoreData) {

        if(loadMoreData && tempOldestKey != null) {

            int positionOfLastMessageId = getPositionOfLastMessageId(tempOldestKey);
            messageRecyclerView.scrollToPosition(positionOfLastMessageId);

        } else if(lastSeenMessageId != null && !lastSeenMessageId.isEmpty()) {

            int positionOfLastMessageId = getPositionOfLastMessageId(lastSeenMessageId);
            messageRecyclerView.scrollToPosition(positionOfLastMessageId);
        } else {
            int lastMessagePosition = conversationMessageAdapter.getItemCount() == 0 ? 0 : conversationMessageAdapter.getItemCount() - 1;
            messageRecyclerView.scrollToPosition(lastMessagePosition);
        }
        isFirstRequest = false;
    }

    private int getPositionOfLastMessageId (String lastSeenMessageId) {

        Message message = new Message(lastSeenMessageId);
        if(conversationMessageAdapter.getConversationList().contains(message)) {
            return conversationMessageAdapter.getConversationList().indexOf(message);
        }
        return 0;
    }

    private void showNewReceivedMessages() {

        Query query = chatMessagesDb.child(chatRoomId).orderByChild("recipients/" + sender + "/isExists").limitToLast(1);


        ChildEventListener childEventListener = query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                DataSnapshot recipients = dataSnapshot.child("recipients");
                if(recipients.hasChild(sender) && !messageKeys.contains(dataSnapshot.getKey())) {

                    Message message = dataSnapshot.getValue(Message.class);
                    message.setUserImage(participantImages.get(message.getOwnerId()));
                    message.setUserName(participantNames.get(message.getOwnerId()));
                    message.setMessageId(dataSnapshot.getKey());
                    if(recipients.child(sender).hasChild("isExists")) {
                        message.setMessageTimeStamp(recipients.child(sender).child("isExists").getValue(Long.class));
                    }

                    lastSeenMessageId = message.getMessageId();
                    HashMap<String, Object> membersUpdateMap = new HashMap<>();
                    membersUpdateMap.put("newMessageCount", null);
                    membersUpdateMap.put("lastSeenMessageId", message.getMessageId());
                    chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(membersUpdateMap);
                    addMessageToLast(dataSnapshot, message);
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
        mMessageListenerMap.put(query, childEventListener);
    }

    private void removeAllListeners() {

        if(mMessageListenerMap != null && mMessageListenerMap.size() != 0){
            for (Map.Entry<Query, ChildEventListener> entry : mMessageListenerMap.entrySet()) {
                Query ref = entry.getKey();
                ChildEventListener listener = entry.getValue();
                if(ref != null){
                    ref.removeEventListener(listener);
                }
            }
        }
    }

    private void addMessageToLast(final DataSnapshot messageSnapshot, final Message message) {

        if(message.getAttachmentId() == null) {
            setAttachmentIdAddedListener(message);
        }

        if(!messageKeys.contains(message.getMessageId())) {
            setStatusOfMessage(messageSnapshot, message);
            counter ++ ;
        }

        if(message.getAttachmentId() != null){
            addMessageWithAttachment(message);
        } else if(!messagesList.contains(message)){
            message.setItemId(itemIdOfMessages++);
            messageKeys.add(message.getMessageId());
            messagesList.add(message);
            Message listMessage = new Message(messagesList);
            cacheManager.put("messages_" + chatRoomId, listMessage);

            if( message.getOwnerId().equals(sender) || ((LinearLayoutManager)messageRecyclerView.getLayoutManager()).
                    findLastCompletelyVisibleItemPosition() == conversationMessageAdapter.getItemCount() - 2){
                unReadMessageCount = 0;
                newMessageLayout.setVisibility(View.GONE);
                conversationMessageAdapter.notifyItemInserted(messagesList.size() -1);
                int lastMessagePosition = conversationMessageAdapter.getItemCount() == 0 ? 0 : conversationMessageAdapter.getItemCount() - 1;
                messageRecyclerView.smoothScrollToPosition(lastMessagePosition);
            } else {
                unReadMessageCount++;
                conversationMessageAdapter.notifyItemInserted(messagesList.size() -1);
                newMessageLayout.setVisibility(View.VISIBLE);
                newMessageCountBadge.setText(String.valueOf(unReadMessageCount));
            }
        }

    }

    private void addMessageWithAttachment (final Message message) {

        switch (message.getAttachmentType()){

            case "image":
            case "audio":
            case "video":
            case "file":
                filesDb.child(message.getAttachmentId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("temporaryPath")){
                            message.setTemporaryPath(dataSnapshot.child("temporaryPath").getValue(String.class));
                        } else {
                            message.setFileUrl(dataSnapshot.child("fileUrl").getValue(String.class));
                            if(message.getAttachmentType().equals("video")){
                                message.setVideoThumbnail(dataSnapshot.child("thumbnailUrl").getValue(String.class));
                            } else if(message.getAttachmentType().equals("audio")){
                                message.setAudioDuration(dataSnapshot.hasChild("duration") ?
                                        dataSnapshot.child("duration").getValue(Double.class) : 0);
                            }
                        }

                        if( message.getOwnerId().equals(sender) || ((LinearLayoutManager)messageRecyclerView.getLayoutManager()).
                                findLastCompletelyVisibleItemPosition() == conversationMessageAdapter.getItemCount() - 1){

                            unReadMessageCount = 0;
                            newMessageLayout.setVisibility(View.GONE);

                            if(messagesList.contains(message)){
                                Message message1 = messagesList.get(messagesList.indexOf(message));
                                message.setItemId(message1.getItemId());
                                messagesList.set(messagesList.indexOf(message), message);
                                conversationMessageAdapter.notifyItemChanged(messagesList.indexOf(message));
                            } else {
                                message.setItemId(itemIdOfMessages++);
                                messageKeys.add(message.getMessageId());
                                messagesList.add(message);
                                conversationMessageAdapter.notifyItemInserted(messagesList.size() -1);
                            }
                            int lastMessagePosition = conversationMessageAdapter.getItemCount() == 0 ? 0 : conversationMessageAdapter.getItemCount() - 1;
                            messageRecyclerView.smoothScrollToPosition(lastMessagePosition);
                        } else {
                            unReadMessageCount++;
                            if(messagesList.contains(message)){
                                Message message1 = messagesList.get(messagesList.indexOf(message));
                                message.setItemId(message1.getItemId());
                                messagesList.set(messagesList.indexOf(message), message);
                                conversationMessageAdapter.notifyItemChanged(messagesList.indexOf(message));
                            } else {
                                message.setItemId(itemIdOfMessages++);
                                messageKeys.add(message.getMessageId());
                                messagesList.add(message);
                                conversationMessageAdapter.notifyItemInserted(messagesList.size() -1);
                            }
                            newMessageLayout.setVisibility(View.VISIBLE);
                            newMessageCountBadge.setText(String.valueOf(unReadMessageCount));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;

            case "link":
                break;
        }
    }

    private void setAttachmentIdAddedListener(final Message message) {

        final DatabaseReference messageRef = chatMessagesDb.child(chatRoomId).child(message.getMessageId()).getRef();
        final ChildEventListener listener = messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("attachmentId")) {
                    message.setAttachmentId(dataSnapshot.getValue(String.class));
                    addMessageWithAttachment(message);
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
        mMessageListenerMap.put(messageRef,  listener);
    }

    private void setStatusOfMessage(DataSnapshot messageSnapshot, final Message message) {

        DataSnapshot recipients = messageSnapshot.child("recipients");
        if(message.getOwnerId().equals(sender)){

            message.setStatus(setStatus(recipients));

            if(message.getStatus() != 3){
                HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();
                DatabaseReference messageRef = chatMessagesDb.child(chatRoomId).child(message.getMessageId()).getRef();
                ChildEventListener listener = messageRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {



                        Log.d(ConversationActivity.class.getSimpleName(), "onChildChanged called " +
                                dataSnapshot.getKey() + "Message Id: " + message.getMessageId());

                        if(dataSnapshot.getKey().equals("recipients") && dataSnapshot.hasChild(sender)) {

                            // notify adapter
                            if(messagesList.contains(message)){

                                int index = messagesList.indexOf(message);
                                Log.d(ConversationActivity.class.getSimpleName(), "Coming in if: " + index);
                                Message message1 = messagesList.get(index);
                                message.setItemId(message1.getItemId());
                                message.setStatus(setStatus(dataSnapshot));

                                Log.d(ConversationActivity.class.getSimpleName(), "New Status: " + message.getStatus());
                                messagesList.set(index, message);

                                Log.d(ConversationActivity.class.getSimpleName(), "New Message List Status : "
                                 + messagesList.get(index).getStatus());

                                conversationMessageAdapter.notifyItemChanged(index);
                                if(checkIfCacheExist()) {
                                    Log.d(ConversationActivity.class.getSimpleName(), "Coming in Put cache 3 Size : " + messagesList.size());
                                    Message listMessage = new Message(messagesList);
                                    cacheManager.put("messages_" + chatRoomId, listMessage);
                                }
                            } else {
                                Log.d(ConversationActivity.class.getSimpleName(), "Coming in else: ");
                            }

                            if(message.getStatus() == 3 && mListenerMap.containsKey(message.getMessageId())){

                                HashMap<DatabaseReference, ChildEventListener> listenerMap = mListenerMap.get(message.getMessageId());
                                for (Map.Entry<DatabaseReference, ChildEventListener> entry : listenerMap.entrySet()) {
                                    DatabaseReference ref = entry.getKey();
                                    ChildEventListener listener = entry.getValue();
                                    ref.removeEventListener(listener);
                                }
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
                mListenerMap.put(message.getMessageId(),  listenerMap);

            }

        } else if(recipients.hasChild(sender)){
                chatMessagesDb.child(chatRoomId).child(message.getMessageId()).
                        child("recipients").child(sender).child("status").setValue("3");
        }
    }

    private int setStatus(DataSnapshot messageRecipients) {

        int statusOfMessage = 1;
        int countOfDelivered = 0, countOfRead = 0;

        if(messageRecipients.getChildrenCount() > 1) {
            for(DataSnapshot recipient : messageRecipients.getChildren()){

                if(!recipient.getKey().equals(sender)){
                    if(recipient.child("status").getValue(String.class).equals("2")){
                        countOfDelivered++;
                    } else if(recipient.child("status").getValue(String.class).equals("3")){
                        countOfDelivered++;
                        countOfRead++;
                    }
                }
            }

            if(countOfRead == messageRecipients.getChildrenCount() -1 ){
                statusOfMessage = 3;
            } else if(countOfDelivered == messageRecipients.getChildrenCount() -1){
                statusOfMessage = 2;
            }
        } else {
            statusOfMessage = 1;
        }

        return statusOfMessage;
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
                // close soft keyboard
                View view = ((Activity) mContext).getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                onBackPressed();
                break;

            case MENU_BLOCK:
                blockUnblockUser();
                break;

            case MENU_VIEW_PROFILE:
                viewProfilePage();
                break;

            case MENU_DELETE:
                GlobalFunctionsUtil.deleteConversation(mContext, chatRoomId, chatMessagesDb, sender,
                        chatRoomMembersDb, typeOfChat);
                break;

            case MENU_CLEAR:
                GlobalFunctionsUtil.clearConversation(mContext, chatRoomId, chatMessagesDb, sender,
                        chatRoomMembersDb, typeOfChat);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        int i = 0;
        if(typeOfChat == 0 && !isDeleted){
            if(blockedContactsListOfSender.contains(destination)) {
                menu.add(i, MENU_BLOCK, Menu.NONE, mContext.getResources().getString(R.string.unblock));
            } else {
                menu.add(i, MENU_BLOCK, Menu.NONE, mContext.getResources().getString(R.string.block));
            }
            menu.add(++i, MENU_VIEW_PROFILE, Menu.NONE, mContext.getResources().getString(R.string.view_profile_text));
        } else if (typeOfChat == 1 && isSenderActive){
            menu.add(++i, MENU_VIEW_PROFILE, Menu.NONE, mContext.getResources().getString(R.string.view_profile_text));
        }

        menu.add(++i, MENU_DELETE, Menu.NONE, mContext.getResources().getString(R.string.delete));
        menu.add(++i, MENU_CLEAR, Menu.NONE, mContext.getResources().getString(R.string.clear));

        return super.onPrepareOptionsMenu(menu);
    }

    private void blockUnblockUser() {


        // todo send an API request to block/unblock the same user in socialEngine as well

        if(!blockedContactsListOfSender.contains(destination)){

            // Block the user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

            alertBuilder.setMessage(mContext.getResources().getString(R.string.block_user_alert));
            alertBuilder.setTitle(mContext.getResources().getString(R.string.block_user));

            alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.block), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    if(chatRoomId != null){

                        chatRoomMembersDb.child(chatRoomId).child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


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

                                isUserBlocked = true;

                                chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(chatMembersUpdates);

                                blockedListDb.child(sender).child(destination).setValue("true");

                                hideMessageBox(mContext.getResources().getString(R.string.blocked_message));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        isUserBlocked = true;
                        blockedListDb.child(sender).child(destination).setValue("true");
                    }

                }
            });

            alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertBuilder.create().show();
        } else {
            // Unblock the user

            if(chatRoomId != null){
                chatRoomMembersDb.child(chatRoomId).child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("duplicateChatId")) {
                            String duplicateChatId = dataSnapshot.child("duplicateChatId").getValue(String.class);
                            duplicateChatRoomsDb.child(duplicateChatId).removeValue();
                        }

                        HashMap<String, Object> chatMembersUpdates = new HashMap<>();
                        chatMembersUpdates.put("isActive", "1");
                        chatMembersUpdates.put("duplicateChatId", null);

                        isUserBlocked = false;
                        chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(chatMembersUpdates);
                        blockedListDb.child(sender).child(destination).setValue(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                isUserBlocked = false;
                blockedListDb.child(sender).child(destination).setValue(null);
            }
            showMessageBox();
        }
    }

    private void viewProfilePage() {

        if(typeOfChat == 0){
            Intent intent = new Intent(this, userProfile.class);
            intent.putExtra("user_id", destination);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, GroupDetails.class);
            intent.putExtra("chatRoomId", chatRoomId);
            intent.putExtra("typeOfChat", 1);
            intent.putExtra(Constants.SENDER, sender);
            startActivityForResult(intent, Constants.GROUP_INFO_REQUEST);
        }
    }

    private void setTypingSubscription() {

        final int[] notTypingCount = {0};
        if(participantsKey != null && participantsKey.size() != 0){

            for(int i = 0; i < participantsKey.size(); i++){

                final String participantKey = participantsKey.get(i);

                typingSubscription = conversationService.getTyping(chatRoomId, participantKey)
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                conversationService.setTyping(chatRoomId, participantKey, "0");
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {

                                if(typeOfChat == 1){
                                    if(!aBoolean){
                                        isTyping = false;
                                        notTypingCount[0]++;
                                    } else {
                                        isTyping = true;
                                        typingParticipantName = participantNames.get(participantKey);
                                        setToolbarSubTitle();
                                    }

                                    if(notTypingCount[0] >= participantsKey.size() && !isTyping){
                                        setToolbarSubTitle();
                                    }
                                }else {
                                    isTyping = aBoolean;
                                    setToolbarSubTitle();
                                }

                            }
                        });
            }
        }
    }

    /** Show the message option popup, From here the user can send images and location messages.*/
    public void showOptionPopup(){
        if (optionPopup!= null && optionPopup.isShowing()) {
            return;
        }

        optionPopup = DialogUtils.getMenuOptionPopup(this.getApplicationContext(), this);
        optionPopup.showAsDropDown(attachmentOptionsButton);
    }

    public void dismissOptionPopup(){
        if (optionPopup != null)
            optionPopup.dismiss();
    }

    private String createNewChatRoom() {

        LinkedHashMap<String, Object> chatParticipants = new LinkedHashMap<>();
        LinkedHashMap<String, String> isDeleted = new LinkedHashMap<>();
        isDeleted.put("isDeleted", "0");

        chatParticipants.put(sender, isDeleted);
        chatParticipants.put(destination, isDeleted);

        HashMap<String, Object> chatMembers = new HashMap<>();
        HashMap<String, Object> memberDetails = new HashMap<>();
        memberDetails.put("isActive", "1");
        memberDetails.put("isTyping", "0");
        memberDetails.put("mute", 0);
        memberDetails.put("resourceType", "user");
        memberDetails.put("updatedAt", ServerValue.TIMESTAMP);

        chatMembers.put(sender, memberDetails);
        chatMembers.put(destination, memberDetails);

        // create New ChatRoom here with Members
        chatRoomId = chatRoomDb.push().getKey();

        GroupsList groupsList = new GroupsList(sender, 2, "0", "user", chatParticipants);

        Map<String, Object> postValues = groupsList.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(chatRoomId, postValues);

        chatRoomDb.updateChildren(childUpdates);
        chatRoomMembersDb.child(chatRoomId).updateChildren(chatMembers);

        userDb.child(sender).child("chats").child(chatRoomId).setValue("0");
        userDb.child(destination).child("chats").child(chatRoomId).setValue("0");

        return chatRoomId;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.sendButton) {

            // Create a new node
            if(messageEditText.getText() != null && !messageEditText.getText().toString().trim().isEmpty()){

                if(blockedContactsListOfSender.contains(destination)){
                    // UnBlock the user
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

                    alertBuilder.setMessage(String.format(mContext.getResources().
                            getString(R.string.unblock_user_alert), participantNames.get(destination)));

                    alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.unblock), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            HashMap<String, Object> chatMembersUpdates = new HashMap<>();
                            chatMembersUpdates.put("isActive", "1");
                            chatMembersUpdates.put("duplicateChatId", null);

                            chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(chatMembersUpdates);

                            blockedContactsListOfSender.remove(destination);

                            blockedListDb.child(sender).child(destination).setValue(null);

                            if(chatRoomId == null || chatRoomId.isEmpty()){
                                chatRoomId = createNewChatRoom();
                                getParticipants();
                                addDestinationInRecipients();
                                sendMessage();
                                fetchChatMessages();
                            } else {
                                sendMessage();
                            }
                        }
                    });

                    alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.create().show();
                } else {

                    if(chatRoomId == null || chatRoomId.isEmpty()){
                        chatRoomId = createNewChatRoom();
                        getParticipants();
                        addDestinationInRecipients();
                        sendMessage();
                        fetchChatMessages();
                    } else {
                        sendMessage();
                    }
                }
            }
        } else if (view.getId() == R.id.attachmentButton) {
            if(blockedContactsListOfSender.contains(destination)){
                // Block the user
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

                alertBuilder.setMessage(String.format(mContext.getResources().
                        getString(R.string.unblock_user_alert),
                        participantNames.containsKey(destination) ? participantNames.get(destination) :
                mContext.getResources().getString(R.string.this_user)));

                alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.unblock), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        blockedListDb.child(sender).child(destination).setValue(null);

                        showOptionPopup();
                    }
                });

                alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.create().show();
            } else {
                showOptionPopup();
            }
        } else if (view.getId() == R.id.photo_attachment_btn) {

            dismissOptionPopup();
            selectedAttachmentOption = "image";
            if(!PermissionsUtils.checkManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                PermissionsUtils.requestForManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Constants.READ_EXTERNAL_STORAGE);
            }else{
                PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_MULTI,
                        true, 10, false);
            }

        } else if (view.getId() == R.id.video_attachment_btn) {
            selectedAttachmentOption = "video";
            dismissOptionPopup();
            if(!PermissionsUtils.checkManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                PermissionsUtils.requestForManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Constants.READ_EXTERNAL_STORAGE);
            } else {
                startVideoUploading();
            }
        } else if (view.getId() == R.id.audio_attachment_btn) {
            selectedAttachmentOption = "audio";
            dismissOptionPopup();
            if(!PermissionsUtils.checkManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                PermissionsUtils.requestForManifestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Constants.READ_EXTERNAL_STORAGE);
            } else {
                startAudioUploading();
            }
        } else if(view.getId() == R.id.newMessageUi) {
            unReadMessageCount = 0;
            newMessageLayout.setVisibility(View.GONE);
            messageRecyclerView.smoothScrollToPosition(conversationMessageAdapter.getItemCount() -1);
        }

        // todo for future release
//            case R.id.document_attachment_btn:
//                dismissOptionPopup();
//                openDocumentUploadingIntent();
//                break;
    }

    private void sendMessage() {

        conversationService.setTyping(chatRoomId, sender, "0");

        addSenderInRecipients();

        Message message = new Message(messageEditText.getText().toString(), sender, recipients, "text", null);
        message.setUserName(senderName);
        message.setUserImage(senderImage);
        String key = chatMessagesDb.child(chatRoomId).push().getKey();
        message.setMessageId(key);


        // Push Entry in Database and
        lastSeenMessageId = key;
        messageEditText.setText("");

        Map<String, Object> postValues = message.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(chatRoomId + "/" + key, postValues);

        chatMessagesDb.updateChildren(childUpdates);

        setUnReadMessageCount(1, key);

        updateLastMessageInChatRoom(key);
    }

    private void addSenderInRecipients() {

        if(!blockedContactsListOfSender.contains(destination) && !recipients.containsKey(sender)){
            HashMap<String, Object> recipientValues = new HashMap<>();
            recipientValues.put("isExists", ServerValue.TIMESTAMP);
            recipientValues.put("status", "1");
            recipients.put(sender, recipientValues);
        }
    }

    private void addDestinationInRecipients() {

        HashMap<String, Object> recipientValues = new HashMap<>();
        recipientValues.put("isExists", ServerValue.TIMESTAMP);
        recipientValues.put("status", "1");
        recipients.put(destination, recipientValues);
    }

    private void updateLastMessageInChatRoom(String lastMessageId){
        // this will not be removed
        chatRoomMembersDb.child(chatRoomId).child(sender).child("lastSeenMessageId").setValue(lastMessageId);
    }

    private void addRecipients() {
        if(participantsKey.size() != 0) {

            HashMap<String, Object> recipientValues = new HashMap<>();
            recipientValues.put("isExists", ServerValue.TIMESTAMP);
            recipientValues.put("status", "1");

            for (int i = 0; i < participantsKey.size(); i++) {

                if(!participantsKey.get(i).equals(sender)){
                    recipients.put(participantsKey.get(i), recipientValues);
                }
            }
        }
    }

    private void setUnReadMessageCount(final int newMessageCount, final String messageId){

        final HashMap<String, Object> chatMembersUpdate = new HashMap<>();
        chatMembersUpdate.put("deleted", "0");
        chatMembersUpdate.put("lastMessageId", messageId);
        chatMembersUpdate.put("updatedAt", ServerValue.TIMESTAMP);

        if(participantsKey.size() != 0) {

            for (int i = 0; i < participantsKey.size(); i++) {

                final DatabaseReference participantDb = chatRoomMembersDb.child(chatRoomId)
                        .child(participantsKey.get(i));

                final int finalI = i;
                participantDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild("isActive") && dataSnapshot.child("isActive").
                                        getValue(String.class).equals("1")){

                                    if(!participantsKey.get(finalI).equals(sender)){
                                        if(!dataSnapshot.hasChild("newMessageCount")){
                                            chatMembersUpdate.put("newMessageCount", newMessageCount);
                                        } else {
                                            chatMembersUpdate.put("newMessageCount",
                                                    dataSnapshot.child("newMessageCount").getValue(Integer.class) + newMessageCount);
                                        }
                                    }
                                    participantDb.updateChildren(chatMembersUpdate);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                });
            }

            chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(chatMembersUpdate);
        } else {
            chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(chatMembersUpdate);
            // update other user's member data only if the sender is not blocked by destination.
            if(!isSenderBlocked) {
                chatMembersUpdate.put("newMessageCount", newMessageCount);
                chatRoomMembersDb.child(chatRoomId).child(destination).updateChildren(chatMembersUpdate);
            }
        }
    }

    private void startAudioUploading(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.REQUEST_AUDIO);
    }

    private void startVideoUploading(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select a video"), Constants.REQUEST_VIDEO);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
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

                        if(chatRoomId == null || chatRoomId.isEmpty()){
                            chatRoomId = createNewChatRoom();
                            getParticipants();
                            addDestinationInRecipients();
                            sendMessageWithImages();
                            fetchChatMessages();
                        } else {
                            sendMessageWithImages();
                        }

                        // Todo show preview of images, implement it like Whatsapp does.
//                        Intent intent = new Intent(this, PhotoPreviewActivity.class);
//                        intent.putStringArrayListExtra("image", mSelectPath);
//                        startActivityForResult(intent, Constants.UPLOAD_IMAGE);
                    }

                } else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
//                    Toast.makeText(getApplicationContext(),
//                            getResources().getString(R.string.image_capturing_failed),
//                            Toast.LENGTH_SHORT).show();
                } else {
                    // User cancel the process
                }
                break;

//            case Constants.UPLOAD_IMAGE:
//                if(resultCode == Constants.UPLOAD_IMAGE){
//                    imagePath = Uri.fromFile(new File(data.getStringExtra("imageUri")));
//                    sendMessageWithImages();
//
//                }
//                break;

            case Constants.REQUEST_VIDEO:
                if(resultCode == RESULT_OK && data != null){
                    videoPath = data.getData();

                    if(getFileSize(videoPath) > 25.0){

                        // Show SnackBar here
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.file_size_limit), Toast.LENGTH_SHORT).show();
                    } else {

                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(getRealPathFromURI(this, videoPath),
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                        // Need to upload Video thumbnail to firebase storage
                        // Then upload Video file to firebase storage and update the database

                        if(chatRoomId == null || chatRoomId.isEmpty()){
                            chatRoomId = createNewChatRoom();
                            getParticipants();
                            addDestinationInRecipients();
                            sendMessageWithVideo(bitmap);
                            fetchChatMessages();
                        } else {
                            sendMessageWithVideo(bitmap);
                        }
                    }

                }else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
//                    SnackbarUtils.displaySnackbar(createFormView,
//                            getResources().getString(R.string.video_capturing_failed));

                }
                break;

            case Constants.REQUEST_AUDIO:
                if(resultCode == RESULT_OK && data != null){
                    audioPath = data.getData();

                    if(getFileSize(audioPath) > 25.0){
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.file_size_limit), Toast.LENGTH_SHORT).show();
                    } else {
                        if(chatRoomId == null || chatRoomId.isEmpty()){
                            chatRoomId = createNewChatRoom();
                            getParticipants();
                            addDestinationInRecipients();
                            sendMessageWithAudio();
                            fetchChatMessages();
                        } else {
                            sendMessageWithAudio();
                        }
                    }

                }else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
//                    SnackbarUtils.displaySnackbar(createFormView,
//                            getResources().getString(R.string.video_capturing_failed));

                }
                break;

            case Constants.INPUT_FILE_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null){
                    documentPath = data.getData();
//                    sendMessageWithDocument();

                }else if (resultCode != RESULT_CANCELED) {
                    // failed to capture image
//                    SnackbarUtils.displaySnackbar(createFormView,
//                            getResources().getString(R.string.video_capturing_failed));

                }
                break;

            case Constants.GROUP_INFO_REQUEST:
                if(resultCode == Constants.GROUP_INFO_REQUEST){
                    finish();
                }
                break;
        }

    }

    private float getFileSize(Uri videoPath){
        File file = new File(getRealPathFromURI(mContext, videoPath));

        long fileSizeInBytes = file.length();

        return fileSizeInBytes / (1024 * 1024);

    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    private void sendMessageWithVideo(Bitmap bitmap) {


        // Todo upload thumbnail of the video first in firebase storage and then upload video
        // save thumbnail as well in firebase database along with the video url


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        StorageReference imagesRef = mStorageRef.child("images/" + UUID.randomUUID() + ".jpg");
        final StorageReference riversRef = mStorageRef.child("videos/" + UUID.randomUUID() + ".mp4");

        imagesRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri thumbnailUrl = taskSnapshot.getDownloadUrl();

                        riversRef.putFile(videoPath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //if the upload is successfull
                                        //hiding the progress dialog
                                        progressDialog.dismiss();

                                        String fileId = filesDb.push().getKey();

                                        HashMap<String, Object> fileMetaData = new HashMap<>();
                                        fileMetaData.put("mimeType", taskSnapshot.getMetadata().getContentType());
                                        fileMetaData.put("extension", "mp4");
                                        fileMetaData.put("size", formatSize(taskSnapshot.getMetadata().getSizeBytes()));
                                        fileMetaData.put("fileUrl", taskSnapshot.getDownloadUrl().toString());
                                        fileMetaData.put("thumbnailUrl", thumbnailUrl.toString());
                                        HashMap<String, Object> fileUpdates = new HashMap<>();
                                        fileUpdates.put(fileId, fileMetaData);

                                        filesDb.updateChildren(fileUpdates);

                                        // Push Entry in Database and
                                        String key = chatMessagesDb.child(chatRoomId).push().getKey();
                                        lastSeenMessageId = key;
                                        Message message = new Message(null, sender, recipients, "video", fileId);
                                        message.setUserImage(senderImage);
                                        message.setUserName(senderName);
                                        message.setMessageId(key);
                                        Map<String, Object> postValues = message.toMap();

                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put(chatRoomId + "/" + key, postValues);
                                        chatMessagesDb.updateChildren(childUpdates);

                                        setUnReadMessageCount(1, key);

                                        updateLastMessageInChatRoom(key);

//                                        messagesList.add(message);
//                                        conversationMessageAdapter.notifyDataSetChanged();

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
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        //calculating progress percentage
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        //displaying percentage in progress dialog
                                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void sendMessageWithImages(){

        uploadedImagesUrl = new ArrayList<>();

        HashMap<String, Object> recipientData = new HashMap<>();
        HashMap<String, Object> recipientsMap = new HashMap<>();
        recipientData.put("isExists", ServerValue.TIMESTAMP);
        recipientData.put("status", "1");
        recipientsMap.put(sender, recipientData);

        Map<String, Object> childUpdates = new HashMap<>();

        for(int i = 0; i < mSelectPath.size(); i++){

            final String key = chatMessagesDb.child(chatRoomId).push().getKey();
            lastSeenMessageId = key;
            Message message = new Message(null, sender, recipientsMap, "image", null);
            Map<String, Object> postValues = message.toMap();

            childUpdates.put(chatRoomId + "/" + key, postValues);

            chatMessagesDb.updateChildren(childUpdates);

            StorageReference riversRef = mStorageRef.child("images/" + UUID.randomUUID() + ".jpg");
            Uri imagePath = Uri.fromFile(new File(mSelectPath.get(i)));
            final int finalI = i;

            riversRef.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog

                            uploadedImagesUrl.add(key);
                            addRecipients();
                            String fileId = filesDb.push().getKey();

                            HashMap<String, Object> fileMetaData = new HashMap<>();
                            fileMetaData.put("mimeType", taskSnapshot.getMetadata().getContentType());
                            fileMetaData.put("extension", "jpg");
                            fileMetaData.put("size", formatSize(taskSnapshot.getMetadata().getSizeBytes()));
                            fileMetaData.put("fileUrl", taskSnapshot.getDownloadUrl().toString());

                            HashMap<String, Object> fileUpdates = new HashMap<>();
                            fileUpdates.put(fileId, fileMetaData);

                            filesDb.updateChildren(fileUpdates);

                            HashMap<String, Object> chatMessageUpdates = new HashMap<>();
                            chatMessageUpdates.put("attachmentId", fileId);
                            chatMessageUpdates.put("recipients", recipients);
                            chatMessagesDb.child(chatRoomId).child(key).updateChildren(chatMessageUpdates);

                            updateDatabase();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog

                            HashMap<String, Object> recipientData = new HashMap<>();
                            HashMap<String, Object> recipientsMap = new HashMap<>();
                            recipientData.put("isExists", ServerValue.TIMESTAMP);
                            recipientData.put("status", "1");
                            recipientsMap.put(sender, recipientData);


                            String fileId = filesDb.push().getKey();

                            HashMap<String, Object> fileMetaData = new HashMap<>();
                            fileMetaData.put("temporaryPath", mSelectPath.get(finalI));

                            HashMap<String, Object> fileUpdates = new HashMap<>();
                            fileUpdates.put(fileId, fileMetaData);

                            filesDb.updateChildren(fileUpdates);

                            HashMap<String, Object> chatMessageUpdates = new HashMap<>();
                            chatMessageUpdates.put("attachmentId", fileId);
                            chatMessageUpdates.put("recipients", recipientsMap);

                            chatMessagesDb.child(chatRoomId).child(key).updateChildren(chatMessageUpdates);


                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        }

    }

    private void updateDatabase() {

        setUnReadMessageCount(uploadedImagesUrl.size(), uploadedImagesUrl.get(uploadedImagesUrl.size()-1));
        updateLastMessageInChatRoom(uploadedImagesUrl.get(uploadedImagesUrl.size()-1));
    }

    private void sendMessageWithAudio(){

        // load data file
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mContext, audioPath);

        // convert duration to minute:seconds
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        final double audioDuration = (double)Long.parseLong(duration)/1000;

        metaRetriever.release();

        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final StorageReference riversRef = mStorageRef.child("audio/" + UUID.randomUUID() + ".mp3");

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        riversRef.putFile(audioPath, metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();


                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String fileId = filesDb.push().getKey();

                                HashMap<String, Object> fileMetaData = new HashMap<>();
                                fileMetaData.put("mimeType", taskSnapshot.getMetadata().getContentType());
                                fileMetaData.put("extension", "mp3");
                                fileMetaData.put("size", formatSize(taskSnapshot.getMetadata().getSizeBytes()));
                                fileMetaData.put("fileUrl", uri.toString());
                                fileMetaData.put("duration", audioDuration);

                                HashMap<String, Object> fileUpdates = new HashMap<>();
                                fileUpdates.put(fileId, fileMetaData);

                                filesDb.updateChildren(fileUpdates);

                                // Push Entry in Database and
                                String key = chatMessagesDb.child(chatRoomId).push().getKey();
                                lastSeenMessageId = key;
                                Message message = new Message(null, sender, recipients, "audio", fileId);
                                message.setUserImage(senderImage);
                                message.setUserName(senderName);
                                message.setMessageId(key);
                                Map<String, Object> postValues = message.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(chatRoomId + "/" + key, postValues);
                                chatMessagesDb.updateChildren(childUpdates);

                                setUnReadMessageCount(1, key);

                                updateLastMessageInChatRoom(key);
                            }
                        });



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
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    private void sendMessageWithDocument(){


        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        StorageReference riversRef = mStorageRef.child("documents/" + UUID.randomUUID() + "." + getMimeType(this, documentPath));
        riversRef.putFile(documentPath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();
                        Uri documentUrl = taskSnapshot.getDownloadUrl();

                        // Push Entry in Database and
                        String key = chatMessagesDb.child(chatRoomId).push().getKey();
                        lastSeenMessageId = key;
                        Message message = new Message(null, sender, recipients, "file", null);
                        message.setMessageId(key);
                        Map<String, Object> postValues = message.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(chatRoomId + "/" + key, postValues);

                        chatMessagesDb.updateChildren(childUpdates);

                        setUnReadMessageCount(1, key);

                        updateLastMessageInChatRoom(key);

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
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if(charSequence.length() > 0){
            conversationService.setTyping(chatRoomId, sender, "1");
            last_text_edit = System.currentTimeMillis();
            typingHandler.postDelayed(input_finish_checker, idle_min);
        } else {
            conversationService.setTyping(chatRoomId, sender, "0");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * Method to launch intent for document.
     */
    public void openDocumentUploadingIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("*/*");
        String[] mimetypes = {"text/*", "application/pdf", "application/msword", "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint", "application/epub+zip", "application/vnd.sun.xml.calc",
                "application/vnd.sun.xml.impress", "application/vnd.sun.xml.draw", "image/tiff",
                "application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.spreadsheet",
                "application/vnd.oasis.opendocument.presentation", "application/vnd.sun.xml.writer",
                "application/vnd.oasis.opendocument.formula", "application/vnd.oasis.opendocument.graphics",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, Constants.INPUT_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    switch (selectedAttachmentOption){

                        case "image":
                            PhotoUploadingUtils.openPhotoUploadingActivity(this, MultiImageSelectorActivity.MODE_MULTI,
                                    true, 10, false);
                            break;

                        case "video":
                            startVideoUploading();
                            break;

                        case "audio":
                            startAudioUploading();
                            break;
                    }
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

    private String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    private boolean checkIfCacheExist() {
        Result<Message> messages = cacheManager.get("messages_" + chatRoomId, Message.class);
        return messages.getCachedObject() != null && messages.getCachedObject().getMessageArrayList().size() != 0;
    }

    @Override
    public void deleteChat() {
        messagesList.clear();
        conversationMessageAdapter.clear();
        ArrayList<Message> messages = new ArrayList<>();
        Message listMessage = new Message(messages);
        cacheManager.put("messages_" + chatRoomId, listMessage);
        Message message = new Message(false);
        message.setItemId(itemIdOfMessages++);
        messagesList.add(new Message(false));
        finish();
    }

    @Override
    public void clearChat() {
        messagesList.clear();
        conversationMessageAdapter.clear();
        ArrayList<Message> messages = new ArrayList<>();
        Message listMessage = new Message(messages);
        cacheManager.put("messages_" + chatRoomId, listMessage);

        Message message = new Message(false);
        message.setItemId(itemIdOfMessages++);
        messagesList.add(new Message(false));
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (ConversationMessageAdapter.selectedMessages.contains(messagesList.get(position)))
                ConversationMessageAdapter.selectedMessages.remove(messagesList.get(position));
            else
                ConversationMessageAdapter.selectedMessages.add(messagesList.get(position));


            Log.d(ConversationActivity.class.getSimpleName(), "Size : "+ ConversationMessageAdapter.
                    selectedMessages.size());

            if (ConversationMessageAdapter.selectedMessages.size() > 0){
                mActionMode.setTitle("" + ConversationMessageAdapter.selectedMessages.size());
                conversationMessageAdapter.notifyDataSetChanged();
            }  else {
                mActionMode.finish();
                removeActionMode();
            }
        }
    }

    private void removeActionMode() {

        if(mActionMode != null) {
            mActionMode = null;
            isMultiSelect = false;
            ConversationMessageAdapter.selectedMessages = new ArrayList<>();
            conversationMessageAdapter.notifyDataSetChanged();
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if(item.getItemId() == R.id.action_delete) {
                showAlertDialog();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(ConversationActivity.class.getSimpleName(), "onDestroyActionMode called..");
            removeActionMode();
        }
    };


    private void showAlertDialog() {

        isLastMsgDeleted = false;
        final int msgCount = ConversationMessageAdapter.selectedMessages.size();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        String messageText = mContext.getResources().getQuantityString(R.plurals.message_count,
                msgCount, msgCount);
        alertBuilder.setMessage(String.format(mContext.getResources().
                getString(R.string.delete_messages_alert), messageText));
        alertBuilder.setTitle(mContext.getResources().getString(R.string.delete_messages));

        alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                for(int i = 0; i < msgCount; i++){

                    final Message message = ConversationMessageAdapter.selectedMessages.get(i);
                    if(messagesList.indexOf(message) == messagesList.size() - 1) {
                        isLastMsgDeleted =  true;
                    }
                    messagesList.remove(message);

                    chatMessagesDb.child(chatRoomId).child(message.getMessageId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("recipients").getChildrenCount() == 1) {
                                chatMessagesDb.child(chatRoomId).child(message.getMessageId()).removeValue();
                            } else if(dataSnapshot.child("recipients").hasChild(sender)) {
                                chatMessagesDb.child(chatRoomId).child(dataSnapshot.getKey()).child("recipients").child(sender).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                if(isLastMsgDeleted) {


                    if(messagesList.size() == 1) {
                        counter = 1;
                        loadMoreMessages(true);
                    } else {
                        // Update the last message for this user in members table
                        Message message = messagesList.get(messagesList.size() -1);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("updatedAt", message.getCreatedAt());
                        childUpdates.put("lastMessageId", message.getMessageId());
                        chatRoomMembersDb.child(chatRoomId).child(sender).updateChildren(childUpdates);
                    }
                }


                Log.d(ConversationActivity.class.getSimpleName(), "Deleted Messages: " + messagesList.size());
                Message listMessage = new Message(messagesList);
                cacheManager.put("messages_" + chatRoomId, listMessage);
                conversationMessageAdapter.update(messagesList);

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getApplicationContext(), msgCount + " message deleted", Toast.LENGTH_SHORT).show();
                ConversationMessageAdapter.selectedMessages = new ArrayList<>();

            }
        });

        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.create().show();
    }
}
