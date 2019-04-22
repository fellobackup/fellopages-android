/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.modules.music;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;

import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.music.adapter.PlaylistDetail;
import com.fellopages.mobileapp.classes.modules.music.adapter.TracksAdapter;
import com.fellopages.mobileapp.classes.modules.music.controls.Controls;
import com.fellopages.mobileapp.classes.modules.music.service.SongService;
import com.fellopages.mobileapp.classes.modules.music.ui.ArtistView;
import com.fellopages.mobileapp.classes.modules.music.ui.TrackView;
import com.fellopages.mobileapp.classes.modules.music.utils.PlayerConstants;
import com.fellopages.mobileapp.classes.modules.music.utils.UtilFunctions;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MusicView extends AppCompatActivity implements TracksAdapter.Listener, AppBarLayout.OnOffsetChangedListener, OnOptionItemClickResponseListener {

    private static RecyclerView mTracksRecyclerView;
    private TrackView.Listener mRetrieveTracksListener;
    private ArrayList<PlaylistDetail> mPlaylistDetail;
    private TracksAdapter mAdapter;
    private ArtistView mArtistView;
    private RecyclerView.OnScrollListener mRetrieveTracksScrollListener;

    private AppConstant mAppConst;
    private String mModuleName;
    private String  actionUrl, dialogueMessage, dialogueTitle, dialogueButton, successMessage;
    private String menuName,mItemViewUrl;
    private Map<String, String> postParams;
    private JSONArray mGutterMenus, playlist_Songs;
    private JSONObject mBody;
    private String description,owner_image,owner_title,creation_date;
    private String album_image, album_Title;
    private int mTotalTracks = 0, mPlaylistId, mAlbumPlayCount, mLikeCount, mCommentCount;
    private boolean mIsLiked = false, isLoadingFromCreate = false, isContentEdited = false, isContentDeleted = false;
    private int mRunningTrackId, mOwnerId;
    static TextView playingSong;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton playAllButton;
    static ImageView btnPause, btnPlay, btnNext, btnPrevious;
    private ImageView btnStop;
    public static CardView control_container;
    private ProgressBar progressBar;
    public static SeekBar mSeekBar;
    private TextView textBufferDuration, textDuration;
    private SelectableTextView mContentTile;
    private ImageView mMusicCover;
    static ImageView imageViewAlbumArt;
    static Context context;
    private Toolbar mToolbar;
    private IntentFilter infSongPrepared;
    public static ProgressBar loadingProgressBar;
    public static int counter = 0;

    private String song_url, mContentUrl;
    private SocialShareUtil socialShareUtil;

    public boolean isDeletePermission = false;
    private EditText input;
    private ArrayList<PhotoListDetails> mCoverImageDetails;
    private TextView mToolBarTitle;
    private AppBarLayout appBar;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseList;
    private View mainContent;
    private int trackPosition, mReactionsEnabled;
    private JSONObject mReactionsObject, mAllReactionObject, mContentReactions;
    private Context mContext;
    private AlertDialogWithAction mAlertDialogWithAction;
    private ImageLoader mImageLoader;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( intent!=null) {
                switch (intent.getAction()) {

                    case ConstantVariables.ACTION_SONG_PREPARED:
                        mRunningTrackId = PlayerConstants.SONGS_LIST
                                .get(PlayerConstants.SONG_NUMBER)
                                .getTrackId();

                        Map<String, String> postParams = new HashMap<>();
                        postParams.put("song_id", String.valueOf(mRunningTrackId));
                        mAppConst.postJsonRequest(AppConstant.DEFAULT_URL + "music/song/tally", postParams);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infSongPrepared = new IntentFilter();
        infSongPrepared.addAction(ConstantVariables.ACTION_SONG_PREPARED);
        infSongPrepared.addAction(ConstantVariables.ACTION_SONG_COMPLETED);
        registerReceiver(br, infSongPrepared);

        if (Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            Window w = getWindow();
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_music_view);

        /* Create Back Button On Action Bar **/

        mToolbar = findViewById(R.id.toolbar);
        mToolBarTitle = findViewById(R.id.toolbar_title);
        mToolBarTitle.setSelected(true);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCoverImageDetails = new ArrayList<>();


        mPlaylistDetail = new ArrayList<>();
        context = MusicView.this;
        mAlertDialogWithAction = new AlertDialogWithAction(MusicView.this);
        mImageLoader = new ImageLoader(getApplicationContext());

        progressBar = findViewById(R.id.progressBar);

        mContext = this;
        mAppConst = new AppConstant(this);
        postParams = new HashMap<>();

        mainContent = findViewById(R.id.coordinator_layout_music);

        socialShareUtil = new SocialShareUtil(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);

        Intent intent = getIntent();
        mModuleName = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        mItemViewUrl = intent.getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        mPlaylistId = intent.getIntExtra(ConstantVariables.VIEW_PAGE_ID, 0);

        // If response coming from create page.
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));

        mTracksRecyclerView = findViewById(R.id.tracks_list);
        initTracksRecyclerView();

        getViews();
        setListeners();
        playingSong.setSelected(true);
        mSeekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                PorterDuff.Mode.SRC_IN);

        mReactionsEnabled = PreferencesUtils.getReactionsEnabled(this);

        /*
            Check if Reactions and nested comment plugin is enabled on the site
            send request to get the reactions on a particular content
            send this request only if the reactions Enabled is not saved yet in Preferences
             or if it is set to 1
         */
        if(mReactionsEnabled == 1 || mReactionsEnabled == -1){
            String getContentReactionsUrl = UrlUtil.CONTENT_REACTIONS_URL + "&subject_type=music_playlist" +
                    "&subject_id=" + mPlaylistId;
            mAppConst.getJsonResponseFromUrl(getContentReactionsUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mReactionsObject = jsonObject;
                    JSONObject reactionsData = mReactionsObject.optJSONObject("reactions");
                    mContentReactions = mReactionsObject.optJSONObject("feed_reactions");
                    if(reactionsData != null){
                        mReactionsEnabled = reactionsData.optInt("reactionsEnabled");
                        PreferencesUtils.updateReactionsEnabledPref(mContext, mReactionsEnabled);
                        mAllReactionObject = reactionsData.optJSONObject("reactions");
                    }

                    // Send Request to load View page data after fetching Reactions on the content.
                    if(mBody != null) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        setDetailsInView();
                    }else {
                        makeRequest();

                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    // Send Request to load View page data after fetching Reactions on the content.
                    if(mBody != null) {
                        isLoadingFromCreate = true;
                        isContentEdited = true;
                        setDetailsInView();
                    }else {
                        makeRequest();

                    }
                }
            });
        } else{
            if(mBody != null) {
                isLoadingFromCreate = true;
                isContentEdited = true;
                setDetailsInView();
            }else {
                makeRequest();

            }
        }

    }

    private void getViews() {

        playAllButton = findViewById(R.id.play_all_btn);
        mContentTile = findViewById(R.id.content_title);
        playingSong = findViewById(R.id.textNowPlaying);
        btnPause = findViewById(R.id.btnPause);
        btnPlay = findViewById(R.id.btnPlay);
        control_container = findViewById(R.id.controls_container);
        mSeekBar = findViewById(R.id.playback_view_seekbar);
        btnStop = findViewById(R.id.btnStop);
        textBufferDuration = findViewById(R.id.textBufferDuration);
        textDuration = findViewById(R.id.textDuration);
        imageViewAlbumArt = findViewById(R.id.imageViewAlbumArt);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        appBar = findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(this);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mMusicCover = findViewById(R.id.activity_album_art);
        loadingProgressBar = findViewById(R.id.loadingProgress);

    }

    private void playMusic(int position) {
        if (!progressBar.isShown() && mPlaylistDetail.size() > 0) {
            if (SongService.isSongPlaying || counter == 0) {
                SongService.isSongPlaying = false;
                counter++;
                loadingProgressBar.setVisibility(View.VISIBLE);
                PlayerConstants.SONGS_LIST = mPlaylistDetail;
                PlayerConstants.SONG_PAUSED = false;
                PlayerConstants.SONG_NUMBER = position;

                updateUI(mPlaylistDetail.get(position));

                changeButton();
                startMusicService();
                mSeekBar.setEnabled(false);
            }
        } else {
            SnackbarUtils.displaySnackbarLongTime(findViewById(R.id.coordinator_layout_music),
                    getResources().getString(R.string.no_songs_to_play));
        }
    }

    private void setListeners() {

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    trackPosition = 0;
                } else {
                    playMusic(0);
                }

            }
        });
        
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SongService.isMediaPlayerPlaying())
                    Controls.playControl(getApplicationContext());
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongService.isMediaPlayerPlaying())
                    Controls.pauseControl(getApplicationContext());
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SongService.isSongPlaying) {
                    SongService.isSongPlaying = false;
                    counter++;
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    mSeekBar.setEnabled(false);
                    Controls.nextControl(getApplicationContext());
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SongService.isSongPlaying) {
                    SongService.isSongPlaying = false;
                    counter++;
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    mSeekBar.setEnabled(false);
                    Controls.previousControl(getApplicationContext());
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongService.isMediaPlayerIntialized) {
                    counter = 0;
                    SongService.isSongPlaying = true;
                    Intent i = new Intent(getApplicationContext(), SongService.class);
                    stopService(i);
                    control_container.setVisibility(View.GONE);
                    mTracksRecyclerView.setPadding(0, 0, 0, 0);
                    mAdapter.notifyDataSetChanged();
                    PlayerConstants.SONG_NUMBER = -1;
                    SongService.isMediaPlayerIntialized = false;
                    if (SongService.mp != null) {
                        SongService.mp.stop();
                        SongService.mp.reset();
                    }
                }

            }
        });
        imageViewAlbumArt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MusicView.this, AudioPlayerActivity.class);
                startActivity(i);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent in = new Intent();
                in.setAction(ConstantVariables.ACTION_UPDATE_SONG);
                in.putExtra("progress", seekBar.getProgress());
                sendBroadcast(in);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, infSongPrepared);
        try{
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
            if (isServiceRunning) {
                updateUI(null);
            }else{
                control_container.setVisibility(View.GONE);
                mTracksRecyclerView.setPadding(0, 0, 0, 0);
            }
            changeButton();
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    Integer i[] = (Integer[])msg.obj;
                    textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                    textDuration.setText(UtilFunctions.getDuration(i[1]));
                    mSeekBar.setProgress(i[2]);
                }
            };
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(br);
    }

    public void makeRequest(){
        if(!isLoadingFromCreate) {
            mAppConst.getJsonResponseFromUrl(mItemViewUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mBody = jsonObject;
                    setDetailsInView();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    progressBar.setVisibility(View.INVISIBLE);
                    SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.coordinator_layout_music),
                            message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    if (!isFinishing()) {
                                        finish();
                                    }
                                }
                            });
                }
            });
        }
    }

    public void setDetailsInView(){

        mPlaylistDetail.clear();
        progressBar.setVisibility(View.INVISIBLE);

        try {
            mGutterMenus = mBody.optJSONArray("gutterMenu");

            if (mGutterMenus != null) {
                invalidateOptionsMenu();
            }

            mPlaylistId = mBody.getInt("playlist_id");
            album_Title = mBody.getString("title");
            description = mBody.optString("description");
            owner_image = mBody.getString("owner_image");
            album_image = mBody.optString("image");
            owner_title = mBody.getString("owner_title");
            creation_date = mBody.getString("creation_date");
            playlist_Songs = mBody.optJSONArray("playlist_songs");
            mAlbumPlayCount = mBody.optInt("play_count");
            mLikeCount = mBody.optInt("like_count");
            mOwnerId = mBody.optInt("owner_id");
            mCommentCount = mBody.optInt("comment_count");
            mIsLiked = mBody.optBoolean("is_like");
            if(playlist_Songs != null) {
                mTotalTracks = playlist_Songs.length();
                for (int i = 0; i < mTotalTracks; i++) {
                    JSONObject songObject = playlist_Songs.optJSONObject(i);
                    int song_id = songObject.optInt("song_id");
                    song_url = songObject.optString("filePath");
                    String song_name = songObject.optString("title");
                    int play_count = songObject.optInt("play_count");
                    mPlaylistDetail.add(new PlaylistDetail(owner_image, album_image, owner_title,
                            album_Title, mTotalTracks, creation_date, mPlaylistId, description, song_id,
                            song_url, song_name, play_count, true));

                }
            }

            mContentUrl = mBody.optString("content_url");
            mBrowseList = new BrowseListItems(mPlaylistId, album_Title, album_image, mContentUrl);

        } catch (Exception e) {
            e.printStackTrace();
            SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                    getResources().getString(R.string.no_data_available));
        }

        mAdapter.notifyDataSetChanged();
        getArtistData();
        mContentTile.setText(album_Title);
        collapsingToolbar.setTitle(album_Title);
        mToolBarTitle.setText(album_Title);

        mCoverImageDetails.add(new PhotoListDetails(album_image));
        mImageLoader.setMusicImage(album_image, mMusicCover);

        mMusicCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mCoverImageDetails);
                Intent i = new Intent(context, PhotoLightBoxActivity.class);
                i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                i.putExtra(ConstantVariables.SHOW_OPTIONS, false);
                i.putExtras(bundle);
                startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
            }
        });

        UtilFunctions.setListOfSongs(mPlaylistDetail);
        playAllButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            if (isContentEdited || isContentDeleted) {
                Intent intent = new Intent();
                setResult(ConstantVariables.VIEW_PAGE_CODE, intent);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(MusicView.this)) {
                SoundUtil.playSoundEffectOnBackPressed(MusicView.this);
            }
        } else if(mGutterMenus != null) {
                mGutterMenuUtils.onMenuOptionItemSelected(mainContent, findViewById(item.getItemId()),
                        id, mGutterMenus);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        if(mGutterMenus != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, ConstantVariables.MUSIC_MENU_TITLE,
                    mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * Used to retrieved the tracks of the artist as well as artist details.
     */
    private void getArtistData() {

        mArtistView.setModel(mPlaylistId, mOwnerId, owner_image, owner_title, description, mTotalTracks,
                mAlbumPlayCount, mLikeCount, mCommentCount, mIsLiked, getApplicationContext(),
                mReactionsEnabled, mReactionsObject, mAllReactionObject, mContentReactions);

    }



    private void initTracksRecyclerView() {
        mArtistView = new ArtistView(this);
        mRetrieveTracksListener = new TrackView.Listener() {
            @Override
            public void onTrackClicked(PlaylistDetail track,int position) {
                if(track != null) {
                    if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                        trackPosition = position;

                    } else {
                        playMusic(position);
                    }
                }
            }

            @Override
            public void onTrackLongClick(View view, final PlaylistDetail track, final int position) {
                JSONObject menuJsonObject;
                String menuName;
                if(mGutterMenus != null) {
                    for (int i = 0; i < mGutterMenus.length(); i++) {
                        menuJsonObject = mGutterMenus.optJSONObject(i);
                        menuName = menuJsonObject.optString("name");
                        if (menuName.equals("edit") || menuName.equals("delete"))
                            isDeletePermission = true;
                    }
                }

                if (isDeletePermission) {
                    // Check to determine if song which is going to be delete/rename is not currently playing
                    if (PlayerConstants.SONG_NUMBER != position ) {

                        PopupMenu popup = new PopupMenu(MusicView.this,
                                view);
                        popup.getMenu().add(getResources().getString(R.string.delete));
                        popup.getMenu().add(getResources().getString(R.string.rename));
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                if (item.getTitle().equals(getResources().getString(R.string.delete))) {
                                    dialogueMessage = getResources().getString(R.string.delete_dialogue_message);
                                    dialogueTitle = getResources().getString(R.string.delete_dialogue_title);
                                    dialogueButton = getResources().getString(R.string.delete_dialogue_button);
                                    successMessage = getResources().getString(R.string.delete_dialogue_success_message);

                                    String deleteSongUrl = AppConstant.DEFAULT_URL + "music/song/" +
                                            track.getTrackId() + "/delete";
                                    performAction(deleteSongUrl, dialogueMessage, dialogueTitle,
                                            dialogueButton, successMessage, "popupdelete", position, track);

                                } else if (item.getTitle().equals(getResources().getString(R.string.rename))) {
                                    dialogueMessage = getResources().getString(R.string.rename_dialogue_message);
                                    dialogueTitle = getResources().getString(R.string.rename_dialogue_title);
                                    dialogueButton = getResources().getString(R.string.rename_dialogue_button);
                                    successMessage = getResources().getString(R.string.rename_dialogue_success_message);

                                    String renameSongUrl = AppConstant.DEFAULT_URL + "music/song/rename";
                                    performAction(renameSongUrl, dialogueMessage, dialogueTitle, dialogueButton,
                                            successMessage, "popuprename", position, track);
                                }
                                return true;
                            }
                        });
                        popup.show();
                    } else {
                        SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                getResources().getString(R.string.stop_music_msg));
                    }
                }
            }

        };

        mPlaylistDetail = new ArrayList<>();
        mTracksRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new TracksAdapter(mRetrieveTracksListener, mPlaylistDetail);
        mAdapter.setHeaderView(mArtistView);
        mTracksRecyclerView.setAdapter(mAdapter);

        mRetrieveTracksScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        };
        mTracksRecyclerView.addOnScrollListener(mRetrieveTracksScrollListener);

    }

    public void startMusicService(){

        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
        if (!isServiceRunning) {
            Intent i = new Intent(getApplicationContext(),SongService.class);
            startService(i);
        } else {
            PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
        }
    }


    @Override
    public void onTrackDismissed(int i) {

    }

    @SuppressWarnings("deprecation")
    public static void updateUI(PlaylistDetail data) {
        try{
            if(data == null) {
                data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            }
            playingSong.setText(String.format("%s %s-%s", data.getTrackTitle(), data.getOwnerName(),
                    data.getAlbumName()));

            Bitmap albumArt = UtilFunctions.getAlbumart(context, data.getPlaylistId());
            if(albumArt != null){
                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(albumArt));
            }else{
                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(UtilFunctions.getDefaultAlbumArt(context)));
            }

            control_container.setVisibility(View.VISIBLE);
            mTracksRecyclerView.setPadding(0, 0, 0, control_container.getHeight());


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void changeButton() {
        if(PlayerConstants.SONG_PAUSED){
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        }else{
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    public static void changeUI(){
        updateUI(null);
        changeButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case  ConstantVariables.VIEW_PAGE_EDIT_CODE:
                if(resultCode == ConstantVariables.VIEW_PAGE_EDIT_CODE){
                    isContentEdited = true;
                    makeRequest();
                }
                break;
            case ConstantVariables.USER_PROFILE_CODE:
                PreferencesUtils.updateCurrentModule(this, mModuleName);
                break;

            case ConstantVariables.VIEW_COMMENT_PAGE_CODE:
                if (resultCode == ConstantVariables.VIEW_COMMENT_PAGE_CODE && data != null) {
                    mCommentCount = data.getIntExtra(ConstantVariables.PHOTO_COMMENT_COUNT, mCommentCount);
                    if(mArtistView != null){
                        mArtistView.setCommentCount(mCommentCount);
                    }
                }
                break;
        }
    }

    /*
    Code For Performing Action According to Name
     */

    public void performAction(String url, String message, String title, String buttonTitle, String showSuccessMessage,
                              String selectedMenuName, final int position, final PlaylistDetail track){

        try {
            actionUrl = url;
            successMessage = showSuccessMessage;
            menuName = selectedMenuName;

            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MusicView.this);
            alertBuilder.setTitle(title);
            alertBuilder.setMessage(message);
            if (menuName.equals("popuprename")) {
                input = new EditText(this);
                input.setText(track.getTrackTitle());
                alertBuilder.setView(input);
            }

            alertBuilder.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertBuilder.setNegativeButton(getResources().getString(R.string.cancel_dialogue_message), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            final AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();

            // used to prevent the dialog from closing when ok button is clicked (For rename condition)
            Button alertDialogPositiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            alertDialogPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mAppConst.hideKeyboard();
                    mAppConst.showProgressDialog();
                    switch (menuName) {
                        case "delete":
                            alertDialog.dismiss();
                            mAppConst.deleteResponseForUrl(actionUrl, postParams, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    mAppConst.hideProgressDialog();
                                    /* Show Message */
                                    SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.coordinator_layout_music),
                                            successMessage,
                                            new SnackbarUtils.OnSnackbarDismissListener() {
                                                @Override
                                                public void onSnackbarDismissed() {
                                                    if (!isFinishing()) {
                                                        finish();
                                                    }
                                                }
                                            });

                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                            message);
                                }
                            });
                            break;

                        case "popupdelete":
                            alertDialog.dismiss();
                            mAppConst.deleteResponseForUrl(actionUrl, null, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                            successMessage);
                                    mPlaylistDetail.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                            message);
                                }
                            });
                            break;

                        case "popuprename":
                            final String renameTitle = input.getEditableText().toString();
                            if (renameTitle.length() > 0 && !renameTitle.trim().isEmpty()) {
                                alertDialog.dismiss();
                                postParams.put("title", renameTitle);
                                postParams.put("song_id", String.valueOf(track.getTrackId()));
                                mAppConst.postJsonResponseForUrl(actionUrl, postParams, new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) {
                                        mAppConst.hideProgressDialog();
                                        SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                                successMessage);
                                        track.setTrackTitle(renameTitle);
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        mAppConst.hideProgressDialog();
                                        SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                                message);
                                    }
                                });
                            } else {
                                mAppConst.hideProgressDialog();
                                input.setError(getResources().getString(R.string.empty_song_title));
                            }
                            break;

                        default:
                            alertDialog.dismiss();
                            mAppConst.postJsonResponseForUrl(actionUrl, postParams, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                            /* Show Message */
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.coordinator_layout_music),
                                            successMessage, new SnackbarUtils.OnSnackbarDismissListener() {
                                                @Override
                                                public void onSnackbarDismissed() {
                                                    finish();
                                                }
                                            });
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    mAppConst.hideProgressDialog();
                                    SnackbarUtils.displaySnackbar(findViewById(R.id.coordinator_layout_music),
                                            message);
                                }
                            });
                            break;
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    playMusic(trackPosition);

                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.
                        SnackbarUtils.displaySnackbarOnPermissionResult(MusicView.this,
                                findViewById(R.id.coordinator_layout_music),
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        CustomViews.showMarqueeTitle(verticalOffset, collapsingToolbar, mToolbar, mToolBarTitle, album_Title);
        if (verticalOffset == 0) {
            collapsingToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            appBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            mToolbar.setBackground(ContextCompat.getDrawable(mContext, R.drawable.album_view_background));
        } else {
            collapsingToolbar.setBackgroundColor(0);
            mToolbar.setBackgroundColor(0);
        }
    }

    @Override
    public void onItemDelete(String successMessage) {

    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseList = (BrowseListItems) itemList;
    }
}
