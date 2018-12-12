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

package com.fellopages.mobileapp.classes.common.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataStorage {
    // Names of the file as per their module name
    public static final String ALBUM_FILE = "ALBUMS";
    public static final String BLOG_FILE = "BLOG";
    public static final String VIDEO_FILE = "VIDEOS";
    public static final String MUSIC_FILE = "MUSIC";
    public static final String POLL_FILE = "POLLS";
    public static final String FORUM_FILE = "FORUMS";
    public static final String GROUP_FILE = "GROUPS";
    public static final String EVENT_FILE = "EVENTS";
    public static final String CLASSIFIEDS_FILE = "CLASSIFIEDS";
    public static final String MLT_FILE = "MLTS";
    public static final String MLT_WISHLIST_FILE = "MLTWISHLISTS";
    public static final String PRODUCT_WISHLIST_FILE = "STOREWISHLIST";
    public static final String ACTIVITY_FEED_FILE = "FEEDS";
    public static final String ADVANCED_EVENT_FILE = "ADVANCED_EVENT";
    public static final String DIARIES_ADV_EVENT_FILE = "DIARIES_ADV_EVENT";
    public static final String SITE_PAGE_FILE = "SITE_PAGES";
    public static final String ADV_GROUP_FILE = "ADV_GROUP";
    public static final String SITE_STORE_PRODUCT_FILE = "SITE_STORE_PRODUCT";
    public static final String SITE_STORE_FILE = "SITE_STORE";
    public static final String ADV_EVENT_FEATURED_CONTENT = "ADV_EVENT_FEATURED";
    public static final String ADV_GROUPS_FEATURED_CONTENT = "ADV_GROUP_FEATURED";
    public static final String SITE_PAGE_FEATURED_CONTENT = "SITE_PAGE_FEATURED";
    public static final String ADV_VIDEO_FILE = "ADV_VIDEO";
    public static final String ADV_VIDEO_FEATURED_CONTENT = "ADV_VIDEO_FEATURED";
    public static final String ADV_VIDEO_CHANNEL_FILE = "ADV_VIDEO_CHANNEL";
    public static final String ADV_VIDEO_CHANNEL_FEATURED_CONTENT = "ADV_VIDEO_CHANNEL_FEATURED";
    public static final String ADV_VIDEO_PLAYLIST_FILE = "ADV_VIDEO_PLAYLIST";
    public static final String MLT_FEATURED_CONTENT = "MLT_FEATURED";
    public static final String STORY_BROWSE = "STORY_BROWSE";
    public static final String AAF_GREETINGS_CONTENT = "AAF_GREETINGS_CONTENT";


    //Creating a file with cached data
    public static void createTempFile(Context context,String fileName,String content){
        try {

            // file = File.createTempFile("MyCache", null, getCacheDir());
            File file = new File(context.getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Getting the stored data from files
    public static String getResponseFromLocalStorage(Context context,String fileName){
        try {
            File file = new File(context.getCacheDir(), fileName);// Pass getFilesDir() and "MyFile" to read file
            if(file.exists()) {
                BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Deleting cache from local device
    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));

                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        //deleting the cache directory
        return dir.delete();
    }

}

