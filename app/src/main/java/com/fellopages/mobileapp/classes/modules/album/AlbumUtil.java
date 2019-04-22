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

package com.fellopages.mobileapp.classes.modules.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.core.ConstantVariables;

public class AlbumUtil {
    public static Fragment getBrowsePageInstance(){
        return new BrowseAlbumFragment();
    }

    public static Fragment getManagePageInstance(){
        return new MyAlbumFragment();
    }

    public static Fragment getPhotosFragmentInstance(){
        return new AlbumPhotosFragment();
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){
        Intent intent = new Intent(context, AlbumView.class);
        url += "albums/view/"+ id +"?gutter_menu=" + 1 ;
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ALBUM_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getViewPageIntent(Context context, int id, int subModuleId, String url, Bundle bundle, String type){

        switch (type) {
            case "sitepage_album":
                url += "sitepage/photos/viewalbum/" + id + "/" + subModuleId;
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.SITE_PAGE_MENU_TITLE);
                break;
            case "sitegroup_album":
                url += "advancedgroups/photos/viewalbum/" + id + "/" + subModuleId;
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_GROUPS_MENU_TITLE);
                break;
            case ConstantVariables.STORE_MENU_TITLE:
                url += "sitestore/photos/view-album/" + id + "?album_id=" + subModuleId;
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                break;
            case ConstantVariables.ALBUM_PHOTO_MENU_TITLE:
                url += "albums/view/"+ subModuleId +"?gutter_menu=1&photo_id=" + id;
                bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ALBUM_MENU_TITLE);
                break;
        }

        Intent intent = new Intent(context, AlbumView.class);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, subModuleId);
        intent.putExtras(bundle);
        return intent;
    }

    public static void checkPermission(Fragment callingFragment, String url){
        ((MyAlbumFragment)callingFragment).checkStoragePermission(url);
    }
}
