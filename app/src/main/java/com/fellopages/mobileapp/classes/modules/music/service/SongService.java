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

package com.fellopages.mobileapp.classes.modules.music.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Build;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.music.AudioPlayerActivity;
import com.fellopages.mobileapp.classes.modules.music.MusicView;
import com.fellopages.mobileapp.classes.modules.music.adapter.PlaylistDetail;
import com.fellopages.mobileapp.classes.modules.music.controls.Controls;
import com.fellopages.mobileapp.classes.modules.music.receiver.NotificationBroadcast;
import com.fellopages.mobileapp.classes.modules.music.utils.PlayerConstants;
import com.fellopages.mobileapp.classes.modules.music.utils.UtilFunctions;

import java.io.IOException;


@SuppressWarnings("deprecation")
public class SongService extends Service implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {
    public static final String NOTIFY_PREVIOUS = "com.fellopages.mobileapp.player.previous";
    public static final String NOTIFY_DELETE = "com.fellopages.mobileapp.player.delete";
    public static final String NOTIFY_PAUSE = "com.fellopages.mobileapp.player.pause";
    public static final String NOTIFY_PLAY = "com.fellopages.mobileapp.player.play";
    public static final String NOTIFY_NEXT = "com.fellopages.mobileapp.player.next";
    public static MediaPlayer mp;
    public static boolean isMediaPlayerIntialized = false;
    public static boolean isSongPlaying = false;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;
    String LOG_CLASS = "SongService";
    int NOTIFICATION_ID = 1111;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    int seekProgress = 0;
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    private IntentFilter infProgress;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mp != null && intent != null) {

                seekProgress = intent.getIntExtra("progress", 0);
                //set seek time
                int seekForwardTime = (mp.getDuration() * seekProgress) / 100;
                mp.seekTo(seekForwardTime);

            }
        }
    };
    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mp != null) {
                try {
                    int progress = (mp.getCurrentPosition() * 100) / mp.getDuration();
                    Integer i[] = new Integer[3];
                    if (mp.isPlaying()) {
                        i[0] = mp.getCurrentPosition();
                        i[1] = mp.getDuration();
                    } else {
                        i[0] = 0;
                        i[1] = 0;
                    }
                    i[2] = progress;

                    PlayerConstants.PROGRESSBAR_HANDLER.sendMessage(
                            PlayerConstants.PROGRESSBAR_HANDLER.obtainMessage(0, i));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //repeat above code every second
            handler.postDelayed(this, 10);
        }
    };

    public static boolean isMediaPlayerPlaying() {
        if (mp.isPlaying()) {
            isMediaPlayerIntialized = true;
            return true;
        } else
            return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        infProgress = new IntentFilter();
        infProgress.addAction("com.bigsteptech.music.updateProgress");
        registerReceiver(broadcastReceiver, infProgress);
        mp = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent in = new Intent();
                in.setAction(ConstantVariables.ACTION_SONG_COMPLETED);
                sendBroadcast(in);
                Controls.nextControl(getApplicationContext());
                if (MusicView.loadingProgressBar != null) {
                    MusicView.loadingProgressBar.setVisibility(View.VISIBLE);
                }
                if (MusicView.mSeekBar != null) {
                    MusicView.mSeekBar.setEnabled(false);
                }
            }
        });
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
        super.onCreate();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
            //update seekBar
            handler.post(mRunnable);
        }
        Intent in = new Intent();
        in.setAction(ConstantVariables.ACTION_SONG_PREPARED);
        sendBroadcast(in);
        if (isMediaPlayerPlaying()) {
            if (MusicView.loadingProgressBar != null) {
                MusicView.loadingProgressBar.setVisibility(View.INVISIBLE);
                isSongPlaying = true;
                MusicView.counter = 0;
            }
            if (MusicView.mSeekBar != null) {
                MusicView.mSeekBar.setEnabled(true);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            if (PlayerConstants.SONGS_LIST.size() <= 0) {
                PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());
            }
            PlaylistDetail data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            if (currentVersionSupportLockScreenControls) {
                RegisterRemoteClient();
            }
            String songPath = data.getTrackUrl();
            playSong(songPath, data);
            newNotification();

            PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    PlaylistDetail data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                    String songPath = data.getTrackUrl();
                    newNotification();
                    try {
                        handler.removeCallbacks(mRunnable);
                        playSong(songPath, data);
                        MusicView.changeUI();
                        AudioPlayerActivity.changeUI();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });


            PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String) msg.obj;
                    if (mp == null)
                        return false;
                    if (message.equalsIgnoreCase(getResources().getString(R.string.play))) {
                        PlayerConstants.SONG_PAUSED = false;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        if (!isMediaPlayerPlaying())
                            mp.start();
                    } else if (message.equalsIgnoreCase(getResources().getString(R.string.pause))) {
                        PlayerConstants.SONG_PAUSED = true;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

                        }
                        if (isMediaPlayerPlaying())
                            mp.pause();
                    }
                    newNotification();
                    try {
                        MusicView.changeButton();
                        AudioPlayerActivity.changeButton();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification() {
        Context mContext = getApplicationContext();
        String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTrackTitle();
        String albumName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumName();

        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.big_notification);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_id_" + NOTIFICATION_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("channel_id_" + NOTIFICATION_ID);
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    "channel_id_" + NOTIFICATION_ID,
                    mContext.getResources().getString(R.string.playing_song) + songName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = mBuilder
                .setSmallIcon(R.drawable.ic_music_white)
                .setContentTitle(songName).build();
        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if (currentVersionSupportBigNotification) {
            notification.bigContentView = expandedView;
        }

        try {
            int albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getPlaylistId();
            Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
            if (albumArt != null) {
                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                if (currentVersionSupportBigNotification) {
                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                }
            } else {
                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt,
                        R.drawable.ic_empty_music2);
                if (currentVersionSupportBigNotification) {
                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt,
                            R.drawable.ic_empty_music2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PlayerConstants.SONG_PAUSED) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        } else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);
        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
        if (currentVersionSupportBigNotification) {
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Notification click listeners
     *
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous,
                PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete,
                PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause,
                PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next,
                PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play,
                PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    @Override
    public void onDestroy() {
        audioManager.abandonAudioFocus(this);
        if (mp != null) {
            handler.removeCallbacks(mRunnable);
            mp.stop();
            mp = null;
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    /**
     * Play song, Update Lockscreen fields
     *
     * @param songPath
     * @param data
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void playSong(String songPath, PlaylistDetail data) {
        try {
            if (currentVersionSupportLockScreenControls) {
                UpdateMetadata(data);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            if (isMediaPlayerPlaying())
                mp.stop();
            mp.reset();
            mp.setDataSource(songPath);
            mp.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void UpdateMetadata(PlaylistDetail data) {
        if (remoteControlClient == null)
            return;
        MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbumName());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getOwnerName());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getTrackTitle());
        mDummyAlbumArt = UtilFunctions.getAlbumart(getApplicationContext(), data.getPlaylistId());
        if (mDummyAlbumArt == null) {
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.ic_empty_music2);
        }
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {

            case AudioManager.AUDIOFOCUS_GAIN:
                mp.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Controls.pauseControl(getApplicationContext());
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Controls.pauseControl(getApplicationContext());
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mp.setVolume(0.1f, 0.1f);
                break;

            default:
                break;
        }
    }
}
