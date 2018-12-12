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

package com.fellopages.mobileapp.classes.modules.music.controls;

import android.content.Context;

import com.fellopages.mobileapp.classes.modules.music.service.SongService;
import com.fellopages.mobileapp.classes.modules.music.utils.PlayerConstants;
import com.fellopages.mobileapp.classes.modules.music.utils.UtilFunctions;
import com.fellopages.mobileapp.R;


public class Controls {
	static String LOG_CLASS = "Controls";
	public static void playControl(Context context) {
		sendMessage(context.getResources().getString(R.string.play));
	}

	public static void pauseControl(Context context) {
		sendMessage(context.getResources().getString(R.string.pause));
	}

	public static void nextControl(Context context) {
		boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
		if (!isServiceRunning)

			return;
		if(PlayerConstants.SONGS_LIST.size() > 0 ){
			if(PlayerConstants.SONG_NUMBER < (PlayerConstants.SONGS_LIST.size()-1)){
				PlayerConstants.SONG_NUMBER++;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}else{
				PlayerConstants.SONG_NUMBER = 0;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}
		}
		PlayerConstants.SONG_PAUSED = false;
	}

	public static void previousControl(Context context) {
		boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
		if (!isServiceRunning)
			return;
		if(PlayerConstants.SONGS_LIST.size() > 0 ){
			if(PlayerConstants.SONG_NUMBER > 0){
				PlayerConstants.SONG_NUMBER--;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}else{
				PlayerConstants.SONG_NUMBER = PlayerConstants.SONGS_LIST.size() - 1;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}
		}
		PlayerConstants.SONG_PAUSED = false;
	}

	private static void sendMessage(String message) {
		try{
			PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
