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

package com.fellopages.mobileapp.classes.modules.classified;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.common.activities.ViewItem;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

public class ClassifiedUtil {
    public static Fragment getBrowsePageInstance(){
        return new BrowseClassifiedFragment();
    }

    public static Fragment getManagePageInstance(){
        return new MyClassifiedFragment();
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){
        url += "classifieds/view/" + id + "?gutter_menu=1";
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.CLASSIFIED_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        Intent intent = new Intent(context, ViewItem.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static void checkPermission(Fragment callingFragment, String url){
        ((MyClassifiedFragment)callingFragment).checkStoragePermission(url);
    }
}
