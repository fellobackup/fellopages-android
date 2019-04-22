package com.socialengineaddons.messenger.service;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.socialengineaddons.messenger.Constants;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener{

    //media player
    private static MediaPlayer player;

    //song list
    private ArrayList<String> songs;


    //current position
    private int songPosn;

    private AudioManager audioManager;

    private final IBinder musicBind = new MusicBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize position
        songPosn = 0;

        //create player
        player = new MediaPlayer();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);
    }

    public void setList(ArrayList<String> theSongs){
        songs = theSongs;
    }

    public void playSong(String songUrl){
        //play a song
        try{
            player.reset();
            player.setDataSource(songUrl);
            player.prepareAsync();
            Log.d(MusicService.class.getSimpleName(), "Duration = " + player.getDuration());
        } catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        if(mediaPlayer != null){
            mediaPlayer.start();

            Log.d(MusicService.class.getSimpleName(), "Duration = " + mediaPlayer.getDuration());

            //update seekBar
            handler.post(mRunnable);
        }
    }

    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(player != null){
                try{

                    int progress = (player.getCurrentPosition()*100) / player.getDuration();
                    Integer i[] = new Integer[3];
                    if(player.isPlaying()) {
                        i[0] = player.getCurrentPosition();
                        i[1] = player.getDuration();
                    }else {
                        i[0] = 0;
                        i[1] = 0;
                    }
                    i[2] = progress;

                    Constants.PROGRESSBAR_HANDLER.sendMessage(
                            Constants.PROGRESSBAR_HANDLER.obtainMessage(0, i));

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //repeat above code every second
            handler.postDelayed(this, 10);
        }
    };
}
