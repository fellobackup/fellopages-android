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

package com.fellopages.mobileapp.classes.modules.directoryPages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.core.ConstantVariables;

public class SitePageUtil {
    public static Fragment getBrowsePageInstance(){
        return new SitePageBrowseFragment();
    }

    public static Fragment getManagePageInstance(){
        return new SitePageManageFragment();
    }

    public static Fragment getPopularPageInstance(){
        return new SitePagePopularFragment();
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){

        url += "sitepage/view/"+ id +"?gutter_menu=" + 1;

        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.SITE_PAGE_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        Intent intent = new Intent(context, SitePageProfilePage.class);
        intent.putExtras(bundle);
        return intent;
    }

}
