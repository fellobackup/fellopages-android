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

package com.fellopages.mobileapp.classes.modules.music.adapter;

import java.io.Serializable;

public class PlaylistDetail implements Serializable {
    String mOwnerImageUrl,mAlbumImageUrl;
    String mOwnerName,mAlbumName,mCreationDate,mAlbumDescription;
    String mTrackUrl,mTrackTitle;
    int mTotalTrackCount,mPlaylistId, mTrackId, mPlayCount;
    boolean isAllowedToView;

    public PlaylistDetail(String owner_image,String album_image,String owner_name,String album_title,
                          int total_Tracks,String creation_date,int item_id,String item_descr,
                          int song_id, String song_url,String song_title,int play_count, boolean isAllowToView){
        mOwnerImageUrl = owner_image;
        mAlbumImageUrl = album_image;
        mOwnerName = owner_name;
        mAlbumName = album_title;
        mCreationDate = creation_date;
        mTotalTrackCount = total_Tracks;
        mPlaylistId = item_id;
        mAlbumDescription = item_descr;
        isAllowedToView = isAllowToView;
        mTrackId = song_id;
        mTrackTitle = song_title;
        mTrackUrl = song_url;
        mPlayCount = play_count;

    }

    public int getTrackId() {
        return mTrackId;
    }

    public int getPlayCount() {
        return mPlayCount;
    }

    public String getTrackUrl() {
        return mTrackUrl;
    }

    public String getTrackTitle() {
        return mTrackTitle;
    }

    public void setTrackTitle(String mTrackTitle) {
        this.mTrackTitle = mTrackTitle;
    }

    public String getOwnerName() {
        return mOwnerName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public int getPlaylistId() {
        return mPlaylistId;
    }

}
