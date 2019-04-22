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
import android.media.MediaPlayer;

import com.fellopages.mobileapp.R;

public class SoundUtil {


    /**
     * Method to play sound effect on back pressed.
     * @param mContext context of calling class.
     */
    public static void playSoundEffectOnBackPressed(Context mContext) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.back_sound);
        mediaPlayer.start();
    }

    /**
     * Method to play sound effect on pull to refresh.
     * @param mContext context of calling class.
     */
    public static void playSoundEffectOnPullToRefresh(Context mContext) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.pull_refresh_sound);
        mediaPlayer.start();
    }

    /**
     * Method to play sound effect on status/comment post.
     * @param mContext context of calling class.
     */
    public static void playSoundEffectOnPost(Context mContext) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.post_sound);
        mediaPlayer.start();
    }

    /**
     * Method to play sound effect on like.
     * @param mContext context of calling class.
     */
    public static void playSoundEffectOnLike(Context mContext) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.like_sound);
        mediaPlayer.start();
    }

    /**
     * Method to play sound effect on for reactions popup.
     * @param mContext context of calling class.
     */
    public static void playSoundEffectOnReactionsPopup(Context mContext) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.reactions_popup);
        mediaPlayer.start();
    }
}
