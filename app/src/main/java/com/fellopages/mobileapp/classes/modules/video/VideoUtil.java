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

package com.fellopages.mobileapp.classes.modules.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoView;

public class VideoUtil {
    public static Fragment getBrowsePageInstance(){
        return new BrowseVideoFragment();
    }

    public static Fragment getManagePageInstance() {
        return new MyVideoFragment();
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){
        Log.d("ThisWasClicke ", "true");
        Intent intent = new Intent(context, AdvVideoView.class);
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.VIDEO_MENU_TITLE);
        bundle.putString(ConstantVariables.VIDEO_URL, url);
        bundle.putInt(ConstantVariables.VIEW_ID, id);
        intent.putExtras(bundle);
        return intent;
    }
}
