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

package com.fellopages.mobileapp.classes.modules.poll;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.core.ConstantVariables;

public class PollUtil {
    public static Fragment getBrowsePageInstance(){
        return new BrowsePollFragment();
    }

    public static Fragment getManagePageInstance(){
        return new MyPollFragment();
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){

        Intent intent = new Intent(context, PollView.class);
        url += "polls/view/"+ id +"?gutter_menu=" + 1 ;
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.POLL_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        intent.putExtras(bundle);
        return intent;
    }
}
