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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.music.controls.Controls;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.music.service.SongService;
import com.fellopages.mobileapp.classes.modules.music.utils.PlayerConstants;
import com.fellopages.mobileapp.classes.modules.music.utils.UtilFunctions;
import com.fellopages.mobileapp.R;

import java.util.HashMap;
import java.util.Map;


public class AudioPlayerActivity extends AppCompatActivity {

    private static String TAG="FullScreenPlayerActivity";
    private AppConstant mAppConst;
    private int mRunningTrackId;
    private ImageView btnBack, btnNext;
    static ImageView mPlayPause;
	private static Drawable mPauseDrawable;
	private static Drawable mPlayDrawable;
	static SelectableTextView textNowPlaying, textAlbumArtist;
	private static ImageView mBackgroundImage;
    private SeekBar mSeekbar;
	static Context context;
	private TextView textBufferDuration, textDuration,mLoadingText;
    private Toolbar mToolbar;
    private IntentFilter infSongPrepared;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent !=null){
                switch (intent.getAction()) {
                    case ConstantVariables.ACTION_SONG_COMPLETED:
                        mLoadingText.setText(getApplicationContext().getResources().
                                getString(R.string.loading_text) + "…");
                        break;

                    case ConstantVariables.ACTION_SONG_PREPARED:

                        mLoadingText.setText("");

                        mRunningTrackId = PlayerConstants.SONGS_LIST
                                .get(PlayerConstants.SONG_NUMBER)
                                .getTrackId();

                        Map<String, String> postParams = new HashMap<>();
                        postParams.put("song_id", String.valueOf(mRunningTrackId));
                        mAppConst.postJsonRequest(AppConstant.DEFAULT_URL+"music/song/tally", postParams);
                        break;

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

		setContentView(R.layout.activity_full_player);
		context = this;
        mAppConst=new AppConstant(this);


        /* Create Back Button On Action Bar **/
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

		init();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(AudioPlayerActivity.this)) {
                SoundUtil.playSoundEffectOnBackPressed(AudioPlayerActivity.this);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
		getViews();
		setListeners();
		PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
			 @Override
		        public void handleMessage(Message msg){
				 Integer i[] = (Integer[])msg.obj;
				 textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
				 textDuration.setText(UtilFunctions.getDuration(i[1]));
                 mSeekbar.setProgress(i[2]);
		    	}
		};
	}

	private void setListeners() {
		btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongService.isSongPlaying) {
                    SongService.isSongPlaying = false;
                    MusicView.counter++;
                    mLoadingText.setText(getApplicationContext().getResources().
                            getString(R.string.loading_text) + "…");
                    Controls.previousControl(getApplicationContext());
                }
            }
        });

        mPlayPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(PlayerConstants.SONG_PAUSED) {
                    Controls.playControl(getApplicationContext());
                }else {
                    Controls.pauseControl(getApplicationContext());
                }
			}
		});
		

		btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongService.isSongPlaying) {
                    SongService.isSongPlaying = false;
                    MusicView.counter++;
                    mLoadingText.setText(getApplicationContext().getResources().
                            getString(R.string.loading_text) + "…");
                    Controls.nextControl(getApplicationContext());
                }
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
	
	public static void changeUI(){
		updateUI();
		changeButton();
	}
	
	private void getViews() {
		btnBack = findViewById(R.id.btnBack);
		btnNext = findViewById(R.id.btnNext);
        mSeekbar = findViewById(R.id.seekBar1);
		mPauseDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white);
		mPlayDrawable =  ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_white);
        mPlayPause = findViewById(R.id.btnPlayPause);
		textNowPlaying = findViewById(R.id.textNowPlaying);
        mBackgroundImage = findViewById(R.id.background_image);
		textAlbumArtist = findViewById(R.id.textAlbumArtist);
		textBufferDuration = findViewById(R.id.textBufferDuration);
		textDuration = findViewById(R.id.textDuration);
        mLoadingText = findViewById(R.id.line3);
		textNowPlaying.setSelected(true);
		textAlbumArtist.setSelected(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        registerReceiver(br,infSongPrepared);
		boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
		if (isServiceRunning) {
			updateUI();
		}
		changeButton();
	}
	
	public static void changeButton() {
		if(PlayerConstants.SONG_PAUSED){
            mPlayPause.setImageDrawable(mPlayDrawable);
		}else{
            mPlayPause.setImageDrawable(mPauseDrawable);
		}
	}

	private static void updateUI() {
		try{
			String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTrackTitle();
			String artist = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getOwnerName();
			String album = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumName();
			textNowPlaying.setText(songName);
			textAlbumArtist.setText(String.format("%s - %s", artist, album));

            int albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getPlaylistId();
            Bitmap albumArt = UtilFunctions.getAlbumart(context, albumId);
            if(albumArt != null){
                mBackgroundImage.setImageBitmap(albumArt);
            }else{
                mBackgroundImage.setImageBitmap(UtilFunctions.getDefaultAlbumArt(context));
            }

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
