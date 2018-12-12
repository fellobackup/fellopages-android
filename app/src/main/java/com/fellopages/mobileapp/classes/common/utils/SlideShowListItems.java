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
 *
 */

package com.fellopages.mobileapp.classes.common.utils;

public class SlideShowListItems {

    String mBrowseImgUrl, mBrowseListTitle, mBrowseListOwnerTitle;
    int mListItemId;
    boolean allowToView;

    // For Browse Advanced Events
    public SlideShowListItems(String imgurl, String listTitle, int listItemId){
        mBrowseImgUrl = imgurl;
        mBrowseListTitle = listTitle;
        mListItemId = listItemId;
    }

    public String getmBrowseImgUrl() {
        return mBrowseImgUrl;
    }


    public String getmBrowseListTitle() {
        return mBrowseListTitle;
    }

    public String getmBrowseListOwnerTitle() {
        return mBrowseListOwnerTitle;
    }

     public int getmListItemId() {
        return mListItemId;
    }

}
