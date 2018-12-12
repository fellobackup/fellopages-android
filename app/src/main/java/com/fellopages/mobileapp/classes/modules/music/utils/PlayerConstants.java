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

package com.fellopages.mobileapp.classes.modules.music.utils;

import android.os.Handler;

import com.fellopages.mobileapp.classes.modules.music.adapter.PlaylistDetail;

import java.util.ArrayList;

public class PlayerConstants {

	//List of Songs
	public static ArrayList<PlaylistDetail> SONGS_LIST = new ArrayList<>();
	/* Song number which is playing right now from SONGS_LIST
	 Initialized with -1 to sync with onTrackLongClick in MusicView.
     Because we are not showing longClick option for the song which is currently playing.
     We are comparing song number with the position of the song and by default the position (int) is 0.
 	*/
	public static int SONG_NUMBER = -1;
	//song is playing or paused
	public static boolean SONG_PAUSED = true;
	//song changed (next, previous)
	public static boolean SONG_CHANGED = false;
	//handler for song changed(next, previous) defined in service(SongService)
	public static Handler SONG_CHANGE_HANDLER;
	//handler for song play/pause defined in service(SongService)
	public static Handler PLAY_PAUSE_HANDLER;
	//handler for showing song progress defined in Activities(MainActivity, AudioPlayerActivity)
	public static Handler PROGRESSBAR_HANDLER;

}
