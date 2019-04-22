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

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.modules.music.adapter.PlaylistDetail;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class UtilFunctions {
	static String LOG_CLASS = "UtilFunctions";
    static ArrayList<PlaylistDetail> songsList;

	/**
	 * Check if service is running or not
	 * @param serviceName
	 * @param context
	 * @return
	 */
	public static boolean isServiceRunning(String serviceName, Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if(serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void setListOfSongs(ArrayList<PlaylistDetail> list)
    {
        songsList = list;
    }
	/**
	 * Read the songs present in external storage
	 * @param context
	 * @return
	 */

	public static ArrayList<PlaylistDetail> listOfSongs(Context context){
		return songsList;
	}

	/**
	 * Get the album image from albumId
	 * @param context
	 * @param album_id
	 * @return
	 */
	public static Bitmap getAlbumart(Context context,int album_id){
		Bitmap bm = null;
		BitmapFactory.Options options = new BitmapFactory.Options();

		try{
			final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
			ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
			if (pfd != null){
				FileDescriptor fd = pfd.getFileDescriptor();
				bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
			}
		} catch(NullPointerException|FileNotFoundException e){
			e.printStackTrace();
		}
	    return bm;
	}


	/**
	 * @param context
	 * @return
	 */
	public static Bitmap getDefaultAlbumArt(Context context){
		Bitmap bm = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
	    try{
	    	bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_empty_music2, options);
	    } catch(NullPointerException e){
            e.printStackTrace();
        }
		return bm;
	}

	/**
	 * Convert milliseconds into time hh:mm:ss
	 * @param milliseconds
	 * @return time in String
	 */
	public static String getDuration(long milliseconds) {
		long sec = (milliseconds / 1000) % 60;
		long min = (milliseconds / (60 * 1000))%60;
		long hour = milliseconds / (60 * 60 * 1000);

		String s = (sec < 10) ? "0" + sec : "" + sec;
		String m = (min < 10) ? "0" + min : "" + min;
		String h = "" + hour;
		
		String time = "";
		if(hour > 0) {
			time = h + ":" + m + ":" + s;
		} else {
			time = m + ":" + s;
		}
		return time;
	}
	
	public static boolean currentVersionSupportBigNotification() {
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		return sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN;
	}
	
	public static boolean currentVersionSupportLockScreenControls() {
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		return sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
}
