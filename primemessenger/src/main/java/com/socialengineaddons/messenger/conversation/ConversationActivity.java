package com.socialengineaddons.messenger.conversation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.Utils;
import com.socialengineaddons.messenger.adapter.AttachmentRecyclerViewAdapter;
import com.socialengineaddons.messenger.conversation.adapter.ConversationMessageAdapter;
import com.socialengineaddons.messenger.conversation.data_model.AttachmentItems;
import com.socialengineaddons.messenger.conversation.data_model.Message;
import com.socialengineaddons.messenger.conversation.database.FirebaseConversationDatabase;
import com.socialengineaddons.messenger.conversation.service.ConversationService;
import com.socialengineaddons.messenger.conversation.service.PersistedConversationService;
import com.socialengineaddons.messenger.conversation.view.BadgeLayout;
import com.socialengineaddons.messenger.groups.GroupDetails;
import com.socialengineaddons.messenger.groups.data_model.GroupsList;
import com.socialengineaddons.messenger.interfaces.OnDeleteClearChat;
import com.socialengineaddons.messenger.interfaces.OnItemClickListener;
import com.socialengineaddons.messenger.interfaces.OnRetryClicked;
import com.socialengineaddons.messenger.interfaces.OnSongPlay;
import com.socialengineaddons.messenger.interfaces.RecyclerItemClickListener;
import com.socialengineaddons.messenger.listners.FirebaseObservableListeners;
import com.socialengineaddons.messenger.multiimageselector.MultiImageSelectorActivity;
import com.socialengineaddons.messenger.service.MusicService;
import com.socialengineaddons.messenger.ui.CircularImageView;
import com.socialengineaddons.messenger.user.User;
import com.socialengineaddons.messenger.user.database.FirebaseUserDatabase;
import com.socialengineaddons.messenger.user.service.PersistedUserService;
import com.socialengineaddons.messenger.user.service.UserService;
import com.socialengineaddons.messenger.user.userProfile;
import com.socialengineaddons.messenger.utils.AlertDialogWithAction;
import com.socialengineaddons.messenger.utils.BitmapUtils;
import com.socialengineaddons.messenger.utils.GlobalFunctionsUtil;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;
import com.socialengineaddons.messenger.utils.PermissionsUtils;
import com.socialengineaddons.messenger.utils.PhotoUploadingUtils;
import com.socialengineaddons.messenger.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import rx.Subscriber;
import rx.Subscription;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnDeleteClearChat {

    private DatabaseReference chatRoomDb, chatMessagesDb, userDb, chatRoomMembersDb,
            blockedListDb, duplicateChatRoomsDb, filesDb;
    private String sender, destination, currentDate = "";;
    private ImageButton sendButton, attachmentOptionsButton;
    private EditText messageEditText;
    private Toolbar mToolbar;
    private TextView tvTitle, tvSubTitle, tvFloatingDate;
    private CircularImageView ivProfile;
    private BottomSheetDialog mBottomSheetDialog;
    private AttachmentRecyclerViewAdapter mAttachmentAdapter;
    public static String chatRoomId;
    private RecyclerView messageRecyclerView;
    private ConversationMessageAdapter conversationMessageAdapter;
    private ArrayList<String> mSelectPath = new ArrayList<>();
    private ArrayList<String> uploadedImagesUrl = new ArrayList<>();
    private Uri videoPath, audioPath, documentPath;
    private StorageReference mStorageRef;
    private Subscription subscription, typingSubscription;
    private UserService userService;
    private ConversationService conversationService;
    private int isUserOnline, visibility;
    private long userLastSeen;
    private boolean isTyping = false, isFloatingDateExist = false;
    private ArrayList<Message> messagesList = new ArrayList<>();
    private int typeOfChat =  1, profileColor;

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
    private String senderName, senderImage;
    private AlertDialogWithAction mAlertDialogWithAction;
    private Context mContext;
    private String lastSeenMessageId;

    private ArrayList<String> blockedContactsListOfSender = new ArrayList<>();
    private int LIMIT = 30;
    private String oldestKeyYouveSeen, tempOldestKey;
    private long oldestKeyTimeStamp;
    private String typingParticipantName;
    private int newMessageCount;
    private boolean isSenderActive = true, isSenderBlocked = false, isChatLoaded = false;
    private boolean isOldestKeyNull = false;
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
    private Handler handler = new Handler();

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
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        sendButton.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        attachmentOptionsButton = (ImageButton) findViewById(R.id.attachmentButton);
        attachmentOptionsButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        messageEditText.addTextChangedListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        tvTitle = (TextView) findViewById(R.id.title);
        tvSubTitle = (TextView) findViewById(R.id.sub_title);
        ivProfile = (CircularImageView) findViewById(R.id.profile_image);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivProfile.getLayoutParams();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            layoutParams.height = (int) (actionBarHeight * 0.75);
            layoutParams.width = (int) (actionBarHeight * 0.75);
        }

        tvFloatingDate = (TextView) findViewById(R.id.floatingDateTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        messageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        newMessageLayout = (RelativeLayout) findViewById(R.id.newMessageUi);
        newMessageLayout.setVisibility(View.GONE);
        newMessageCountBadge = (BadgeLayout) findViewById(R.id.newMessageCount);
        newMessageCountBadge.setText(String.valueOf(3));
        newMessageLayout.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
                        mActionMode = startSupportActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
        userDb = messengerDatabaseUtils.getUsersDatabase();
        chatRoomDb = messengerDatabaseUtils.getChatsDatabase();
        chatRoomMembersDb = messengerDatabaseUtils.getChatMembersDatabase();
        chatMessagesDb = messengerDatabaseUtils.getChatMessagesDatabase();
        blockedListDb = messengerDatabaseUtils.getBlockedListDatabase();
        duplicateChatRoomsDb = messengerDatabaseUtils.getDuplicateChatsDatabase();
        filesDb = messengerDatabaseUtils.getFilesDatabase();
        mStorageRef = messengerDatabaseUtils.getStorageRef();

        sender = getIntent().hasExtra(Constants.SENDER) ? getIntent().getStringExtra(Constants.SENDER) :
                messengerDatabaseUtils.getCurrentUserId() ;
        destination = getIntent().getStringExtra(Constants.DESTINATION);
        typeOfChat = getIntent().getIntExtra("typeOfChat", 0);
        profileColor = getIntent().getIntExtra(Constants.PROFILE_COLOR, 0);
        if (profileColor == 0) {
            Random random = new Random();
            this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
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

        conversationMessageAdapter = new ConversationMessageAdapter(mContext, sender, typeOfChat, LayoutInflater.from(this), new OnSongPlay() {
            @Override
            public void onPlayButtonClicked(int position, String songUrl) {

//                clickedPosition = position;
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

                // Showing date in floating view.
                int itemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                Message message = messagesList.get(itemPosition);
                if (itemPosition != 0 && !currentDate.equals(Utils.getDateOfMessage(mContext, message.getCreatedAt()))) {
                    currentDate = Utils.getDateOfMessage(mContext, message.getCreatedAt());
                    // called when the date is changed.
                    tvFloatingDate.bringToFront();
                    tvFloatingDate.setVisibility(View.VISIBLE);
                    tvFloatingDate.setText(Utils.getDateOfMessage(mContext, message.getCreatedAt()));

                    if (!isFloatingDateExist) {
                        isFloatingDateExist = true;
                        handler.removeCallbacks(runnableCode);
                        handler.postDelayed(runnableCode, 800);
                    }
                }

                if(firstVisible == 0 && oldestKeyYouveSeen != null && !isFirstRequest) {
                    loadMoreMessages(false);
                } else if (lastVisiblePosition == conversationMessageAdapter.getItemCount() - 1){
                    unReadMessageCount = 0;
                    newMessageLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isFloatingDateExist && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    tvFloatingDate.bringToFront();
                    tvFloatingDate.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(runnableCode);

                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handler.removeCallbacks(runnableCode);
                    handler.postDelayed(runnableCode, 800);
                }
            }
        });

        if(chatRoomId != null){
            isChatLoaded = true;
            getParticipants();
            fetchChatMessages();
        } else {
            isChatLoaded = false;
            checkExistingChatRoom();
        }

        setAttachmentAdapter();
        overridePendingTransition(0, 0);
    }

    // Define the task to be run here
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            tvFloatingDate.setVisibility(View.GONE);
        }
    };

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

            for (int i = 0; i < ConversationMessageAdapter.medialPlayers.size(); i++) {
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

    /**
     * Function to fetch blockedContacts list of the current logged-in user
     * todo this function needs to be replaced by mqtt api for getting blocked contacts list
     */
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

    /**
     * Function to hide message sending block
     * @param messageToShow hint message to show in Edit text
     */
    private void hideMessageBox(String messageToShow){
        attachmentOptionsButton.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        messageEditText.setEnabled(false);
        messageEditText.setHint(messageToShow);
    }

    /**
     * Function to show message sending block
     */
    private void showMessageBox() {

        attachmentOptionsButton.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        messageEditText.setEnabled(true);
        messageEditText.setHint(mContext.getResources().getString(R.string.type_message));
    }

    /**
     * Function to get the destination of the chat
     * this will be called only when conversation is getting opened from push notifications
     * in all other cases, we receive destination in intent, so no need to call this
     */
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

    /**
     * Function to get participants from members table of that conversation
     */
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
                                    if(typeOfChat == 1 && isSenderActive){
                                        setTitleInCenter(false);
                                        tvSubTitle.setText(nameOfParticipants[0]);
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

    /**
     * If the conversation will be one-one then need to show other user's title
     * and in case of group chat need to show title of the group, so we will apply observer accordingly.
     */
    private void setupToolBar() {


        if(typeOfChat == 1){

            chatRoomDb.child(chatRoomId).child("title").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvTitle.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            chatRoomDb.child(chatRoomId).child("profileImageUrl").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    setToolBarProfile(dataSnapshot.getValue(String.class));
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

                            isDeleted = user == null;
                            invalidateOptionsMenu();
                            tvTitle.setText(user != null ? user.getName() : mContext.getResources().
                                    getString(R.string.deleted_user));
                            isUserOnline = (user != null ? user.getIsOnline() : 0);
                            visibility = (user != null ? user.getVisibility() : 0);
                            userLastSeen = (user != null ? user.getLastSeen() : 0);

                            // Showing profile image in header.
                            ivProfile.setVisibility(View.VISIBLE);
                            setToolBarProfile(user != null ? user.getProfileImageUrl() : null);
                            setToolbarSubTitle();
                        }
                    });
        }

    }

    /**
     * Method to set tool bar title for one to one and group chat.
     */
    private void setToolBarProfile(String imageUrl) {

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.person_image_empty)
                    .into(ivProfile);
        } else {
            ivProfile.showText(tvTitle.getText().toString(), profileColor);
        }
    }

    /**
     * Function to set toolbar subtitle
     * if 1-1 chat then toolbar subtitle will be the online/offline/lastSeen
     * if group chat then toolbar subtitle will be conversation's participant's first name separated by comma
     * and if user is currently typing in the conversation, then we need to show typing.. text for both 1-1 and group chat
     */
    private void setToolbarSubTitle() {

        // Show subtitle i.e. online, typing status of the user only if the sender is not blocked by destination
        if((typeOfChat == 0 && !isSenderBlocked && isSenderActive) || (typeOfChat == 1 && isSenderActive) ){
            if(isTyping){
                setTitleInCenter(false);
                if(typeOfChat == 1) {
                    tvSubTitle.setText(String.format(mContext.getResources().
                            getString(R.string.user_typing), typingParticipantName));
                } else {
                    tvSubTitle.setText(mContext.getResources().getString(R.string.typing));
                }
            } else if(typeOfChat == 0) {
                if(visibility == 1) {
                    setTitleInCenter(false);
                    if(isUserOnline == 1) {
                        String onlineText = mContext.getResources().getString(R.string.online);
                        onlineText = Character.toLowerCase(onlineText.charAt(0)) + onlineText.substring(1);
                        tvSubTitle.setText(onlineText);
                    } else {
                        //Show Last Seen
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                        String timestamp = sdf.format(new Date(userLastSeen));
                        String today = Utils.getCurrentDate();
                        String[] time1 = timestamp.split("/");
                        String[] time2 = today.split("/");
                        if ((time1[0] + time1[1] + time1[2]).equals(time2[0] + time2[1] + time2[2])) {
                            String s = getResources().getString(R.string.lastseen_today);
                            tvSubTitle.setText(s.replace("time",time1[3] + ":" + time1[4]));
                        } else {
                            String s = getResources().getString(R.string.lastseen_date);
                            s = s.replace("date", time1[2] + "/" + time1[1]);
                            s = s.replace("time", time1[3] + ":" + time1[4]);
                            tvSubTitle.setText(s);
                        }
                    }
                } else {
                    tvSubTitle.setText("");
                    setTitleInCenter(true);
                }
            }
        } else {
            tvSubTitle.setText("");
            setTitleInCenter(true);
        }
    }

    /**
     * Method to set title view in center if the subtitle is not exist.
     * @param isNeedToShowInCenter True if need to show title in center.
     */
    private void setTitleInCenter(boolean isNeedToShowInCenter) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, isNeedToShowInCenter ? RelativeLayout.TRUE : 0);
        tvTitle.setLayoutParams(layoutParams);
    }

    /**
     * this function is used to check if a conversation is already exists between the pair of these 2 users
     * as only a single 1-1 conversation can exists between 2 users
     */
    private void checkExistingChatRoom() {

        /*
            check if chatRoom is already exist for these 2 users
            else create a new chatRoom
         */
        progressBar.bringToFront();
        progressBar.setVisibility(View.VISIBLE);
        hideMessageBox(mContext.getResources().getString(R.string.type_message));
        chatRoomDb.orderByChild("participants/" + sender + "/isDeleted").equalTo("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot groupDetails : dataSnapshot.getChildren()) {

                    DataSnapshot participants = groupDetails.child("participants");
                    if(participants.getChildrenCount() == 2 && participants.hasChild(destination)){
                        isChatLoaded = true;
                        chatRoomId = groupDetails.getKey();
                        Log.d(ConversationActivity.class.getSimpleName(), "chatRoomId: " + chatRoomId);
                        getParticipants();
                        fetchChatMessages();
                        break;
                    }
                }

                if (!isChatLoaded) {
                    isChatLoaded = true;
                    progressBar.setVisibility(View.GONE);
                    if (isUserBlocked) {
                        hideMessageBox(mContext.getResources().getString(R.string.blocked_message));
                    } else {
                        showMessageBox();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Function to add a listener to fetch messages from firebase database
     * todo this function needs to be replaced by mqtt api call
     */
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

    /**
     * Function to load More messages on request
     * @param updateLastMessage
     */
    private void loadMoreMessages(final boolean updateLastMessage) {

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

    /**
     * Function to add messages to list of adapter
     * @param dataSnapshot messages details
     * @param loadMoreData check if the function get called for loading more messages
     * @param position
     * @param loadingFromCache check if the messages are getting loaded from cache
     * @param updateLastMessage check we need to update last message id in the members table
     */
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

    /**
     * function to scroll the RecyclerView at the bottom or at lastSeenMessageId
     * @param loadMoreData boolean to check if the scrolling is called from load more messages
     */
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

    /**
     * Functio  to find the position of lastSeenMessageId in the adapter's list
     * @param lastSeenMessageId message id of the last seen message
     * @return
     */
    private int getPositionOfLastMessageId (String lastSeenMessageId) {

        Message message = new Message(lastSeenMessageId);
        if(conversationMessageAdapter.getConversationList().contains(message)) {
            return conversationMessageAdapter.getConversationList().indexOf(message);
        }
        return 0;
    }

    /**
     * observer to get the new message posted in the conversation
     */
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

    /**
     * Function to add last message in the list of all messages
     * @param messageSnapshot SnapShot of the last received message in conversation
     * @param message Object of message
     */
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

    /**
     * Function to fetch the attachment details from files table and add the message
     * with the details of attachment in messages list
     * @param message Message Object
     */
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

    /**
     * Observer to listen the event when attachment has been successfully uploaded
     * and attachmentId has been updated in the messages table
     * @param message Object of message
     */
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

    /**
     * Add observer on the messages which are of the current logged-in user to check the
     * status of the messages whether they are sent, delievered or read by other participants
     * @param messageSnapshot details of the message
     * @param message Object of message
     */
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

                        if(dataSnapshot.getKey().equals("recipients") && dataSnapshot.hasChild(sender)) {

                            // notify adapter
                            if(messagesList.contains(message)){

                                int index = messagesList.indexOf(message);
                                Message message1 = messagesList.get(index);
                                message.setItemId(message1.getItemId());
                                message.setStatus(setStatus(dataSnapshot));
                                messagesList.set(index, message);

                                conversationMessageAdapter.notifyItemChanged(index);
                                if(checkIfCacheExist()) {
                                    Message listMessage = new Message(messagesList);
                                    cacheManager.put("messages_" + chatRoomId, listMessage);
                                }
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

    /**
     * Function to get the status of message and set it in the list
     * @param messageRecipients
     * @return
     */
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
            menu.add(++i, MENU_VIEW_PROFILE, Menu.NONE, mContext.getResources().getString(R.string.view_profile));
        } else if (typeOfChat == 1 && isSenderActive){
            menu.add(++i, MENU_VIEW_PROFILE, Menu.NONE, mContext.getResources().getString(R.string.view_profile));
        }

        menu.add(++i, MENU_DELETE, Menu.NONE, mContext.getResources().getString(R.string.delete));
        menu.add(++i, MENU_CLEAR, Menu.NONE, mContext.getResources().getString(R.string.clear));

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Function to perform block/unblock action
     */
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
            if (!isChatLoaded) {
                hideMessageBox(mContext.getResources().getString(R.string.type_message));
            } else {
                showMessageBox();
            }
        }
    }

    /**
     * Open user profile page or group detail page
     * depending on one-one/group chat
     */
    private void viewProfilePage() {

        if(typeOfChat == 0){
            Intent intent = new Intent(this, userProfile.class);
            intent.putExtra("user_id", destination);
            intent.putExtra(Constants.PROFILE_COLOR, profileColor);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, GroupDetails.class);
            intent.putExtra("chatRoomId", chatRoomId);
            intent.putExtra("typeOfChat", 1);
            intent.putExtra(Constants.SENDER, sender);
            intent.putExtra(Constants.PROFILE_COLOR, profileColor);
            startActivityForResult(intent, Constants.GROUP_INFO_REQUEST);
        }
    }

    /**
     * Function to set typing subscription in order to track which users are typing
     * and show the typing status in toolbar
     */
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

    private void setAttachmentAdapter() {
        final List<AttachmentItems> attachmentItemsList = new ArrayList<>();
        attachmentItemsList.add(new AttachmentItems("photo", R.drawable.ic_photo_camera_black_24dp,
                mContext.getResources().getString(R.string.image)));
        attachmentItemsList.add(new AttachmentItems("video", R.drawable.ic_videocam_black_24dp,
                mContext.getResources().getString(R.string.video)));
        attachmentItemsList.add(new AttachmentItems("audio", R.drawable.ic_music_black_24dp,
                mContext.getResources().getString(R.string.audio)));

        mAttachmentAdapter = new AttachmentRecyclerViewAdapter(mContext, attachmentItemsList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mBottomSheetDialog.dismiss();
                AttachmentItems clickedAttachment = attachmentItemsList.get(position);
                switch (clickedAttachment.getKey()) {

                    case "photo":
                        selectedAttachmentOption = "image";
                        if(!PermissionsUtils.checkManifestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)){
                            PermissionsUtils.requestForManifestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Constants.READ_EXTERNAL_STORAGE);
                        }else{
                            PhotoUploadingUtils.openPhotoUploadingActivity(mContext, MultiImageSelectorActivity.MODE_MULTI,
                                    true, 10, false);
                        }
                        break;

                    case "video":
                        selectedAttachmentOption = "video";
                        if(!PermissionsUtils.checkManifestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)){
                            PermissionsUtils.requestForManifestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Constants.READ_EXTERNAL_STORAGE);
                        } else {
                            startVideoUploading();
                        }
                        break;

                    case "audio":
                        selectedAttachmentOption = "audio";
                        if(!PermissionsUtils.checkManifestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)){
                            PermissionsUtils.requestForManifestPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Constants.READ_EXTERNAL_STORAGE);
                        } else {
                            startAudioUploading();
                        }
                        break;
                }
            }
        });
    }

    private void showAttachmentSheet() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.recycler_view, null);

        TextView tvTitle = (TextView) inflatedView.findViewById(R.id.title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(mContext.getResources().getString(R.string.upload));

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
        layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        layoutParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_10dp);
        recyclerView.setMinimumHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_100dp));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.setAdapter(mAttachmentAdapter);

        mBottomSheetDialog = new BottomSheetDialog(mContext);
        mBottomSheetDialog.setContentView(inflatedView);
        mBottomSheetDialog.show();
    }

    /**
     * Function to create new Chat Room
     * this will be called in case of one-one chat only if conversation doesn't exists for these users
     * @return
     */
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

                        showAttachmentSheet();
                    }
                });

                alertBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.create().show();
            } else {
                showAttachmentSheet();
            }
        } else if(view.getId() == R.id.newMessageUi) {
            unReadMessageCount = 0;
            newMessageLayout.setVisibility(View.GONE);
            messageRecyclerView.smoothScrollToPosition(conversationMessageAdapter.getItemCount() -1);
        }

        // todo for future release
//            case R.id.document_attachment_btn:
//                openDocumentUploadingIntent();
//                break;
    }

    /**
     * Function to send text message
     */
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

    /**
     * Function to add sender in recipients map if he is not already added
     */
    private void addSenderInRecipients() {

        if(!blockedContactsListOfSender.contains(destination) && !recipients.containsKey(sender)){
            HashMap<String, Object> recipientValues = new HashMap<>();
            recipientValues.put("isExists", ServerValue.TIMESTAMP);
            recipientValues.put("status", "1");
            recipients.put(sender, recipientValues);
        }
    }

    /**
     * Function to add destination in recipients map if he is not already added
     */
    private void addDestinationInRecipients() {

        HashMap<String, Object> recipientValues = new HashMap<>();
        recipientValues.put("isExists", ServerValue.TIMESTAMP);
        recipientValues.put("status", "1");
        recipients.put(destination, recipientValues);
    }

    /**
     * Function to update last seen message id in the members table
     * for current logged-in user
     * @param lastMessageId
     */
    private void updateLastMessageInChatRoom(String lastMessageId){
        // this will not be removed
        chatRoomMembersDb.child(chatRoomId).child(sender).child("lastSeenMessageId").setValue(lastMessageId);
    }

    /**
     * Prepare a map of recipients
     */
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

    /**
     * Function to update new messagecount, lastMessageId, updatedAt, deleted values
     * for each member of the chat
     * @param newMessageCount message count of unread messages
     * @param messageId the last sent message id
     */
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

    /**
     * Start a intent to allow users to select audio files from device
     */
    private void startAudioUploading(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.REQUEST_AUDIO);
    }

    /**
     * Start a intent to allow users to select video files from device
     */
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
                    if (mSelectPath != null && !mSelectPath.isEmpty()){

                        // Checking is there any image which required rotation.
                        for (final String imagePath : mSelectPath) {
                            BitmapUtils.decodeSampledBitmapFromFile(mContext, imagePath, false);
                        }
                        if (BitmapUtils.isImageRotated) {
                            for (final String imagePath : mSelectPath) {
                                BitmapUtils.decodeSampledBitmapFromFile(mContext, imagePath, true);
                            }
                            mSelectPath = BitmapUtils.updateSelectPath();
                        }

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

                }
                break;

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

                }
                break;

            case Constants.INPUT_FILE_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null){
                    documentPath = data.getData();
//                    sendMessageWithDocument();

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

    /**
     * Function to send message with attached video file
     * @param bitmap
     */
    private void sendMessageWithVideo(Bitmap bitmap) {

        // save thumbnail as well in firebase database along with the video url

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.uploading));
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

    /**
     * Function to send message with attached images
     * first images will be uploaded to firebase storage which will return the url of file.
     * then we will update the url and other details in files table which will give us a file id
     * and then we will update the file id in messages table as attachment_id
     */
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
                            if (BitmapUtils.isImageRotated) {
                                BitmapUtils.deleteImageFolder();
                            }

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
                            if (BitmapUtils.isImageRotated) {
                                BitmapUtils.deleteImageFolder();
                            }

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

    /**
     * Function to update the firebase database after sending a message
     */
    private void updateDatabase() {

        setUnReadMessageCount(uploadedImagesUrl.size(), uploadedImagesUrl.get(uploadedImagesUrl.size()-1));
        updateLastMessageInChatRoom(uploadedImagesUrl.get(uploadedImagesUrl.size()-1));
    }

    /**
     * Function to send message with attached audio file
     * first audio file will be uploaded to firebase storage which will return the url of file.
     * then we will update the url and other details in files table which will give us a file id
     * and then we will update the file id in messages table as attachment_id
     */
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
        progressDialog.setTitle(getResources().getString(R.string.uploading));
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
        progressDialog.setTitle(getResources().getString(R.string.uploading));
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

    /**
     * Function to check if the conversation already exists in cache or not
     * @return
     */
    private boolean checkIfCacheExist() {
        Result<Message> messages = cacheManager.get("messages_" + chatRoomId, Message.class);
        return messages.getCachedObject() != null && messages.getCachedObject().getMessageArrayList().size() != 0;
    }

    /**
     * Function to delete the conversation
     */
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
        if (BitmapUtils.isImageRotated) {
            BitmapUtils.deleteImageFolder();
        }
        finish();
    }

    /**
     * Function to clear the conversation
     */
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
        if (BitmapUtils.isImageRotated) {
            BitmapUtils.deleteImageFolder();
        }
    }

    /**
     * Function called when multiple messages get selected for deletion
     * @param position
     */
    public void multi_select(int position) {
        if (mActionMode != null) {
            Message message = messagesList.get(position);
            if (ConversationMessageAdapter.selectedMessages.contains(message)) {
                ConversationMessageAdapter.selectedMessages.remove(message);
            } else {
                ConversationMessageAdapter.selectedMessages.add(message);
            }

            if (ConversationMessageAdapter.selectedMessages.size() > 0){
                mActionMode.setTitle("" + ConversationMessageAdapter.selectedMessages.size());
                conversationMessageAdapter.notifyDataSetChanged();
            }  else {
                mActionMode.finish();
                removeActionMode();
            }
            context_menu.findItem(R.id.action_copy).setVisible(
                    ConversationMessageAdapter.selectedMessages.size() == 1);
        }
    }

    /**
     * Function to remove action mode which will remove the contextual action bar
     */
    private void removeActionMode() {

        if(mActionMode != null) {
            mActionMode = null;
            isMultiSelect = false;
            ConversationMessageAdapter.selectedMessages = new ArrayList<>();
            conversationMessageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * to set the multiselect action mode layout
     * it will show the menus in contextual action bar
     */
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
            } else if (item.getItemId() == R.id.action_copy) {

                final Message message = ConversationMessageAdapter.selectedMessages.get(0);
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Message",
                        Html.fromHtml(message.getBody()));
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.text_copied),
                            Toast.LENGTH_SHORT).show();
                }

                mActionMode.finish();
                removeActionMode();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            removeActionMode();
        }
    };


    /**
     * show a alert dialog when multiple messages are selected and delete button is pressed
     * need to update last message in the members table of if the last message is selected and is deleted
     */
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
                            if (BitmapUtils.isImageRotated) {
                                BitmapUtils.deleteImageFolder();
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
