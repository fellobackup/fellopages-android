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

package com.fellopages.mobileapp.classes.modules.forum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.core.ConstantVariables;

public class ForumUtil {

    public static Fragment getHomePageInstance(){
        return new ForumHome();
    }

    public static Intent getProfilePageIntent(Context context, int id, String url, Bundle bundle,String slug){

        url += "forums/" + id + "/";
        if(slug != null){
            url += slug;
        }
        Intent intent = new Intent(context, ForumProfile.class);
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt("forumId", id);
        bundle.putString("forumSlug", slug);
        intent.putExtras(bundle);
        return intent;

    }

    public static Intent getViewTopicPageIntent(Context context,String title,String viewUrl){
        Intent redirectIntent = new Intent(context, ForumView.class);
        redirectIntent.putExtra(ConstantVariables.CONTENT_TITLE, title);
        redirectIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, viewUrl);
        redirectIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.FORUM_MENU_TITLE);
        return redirectIntent;
    }

    public static void finishViewTopicActivity(){
        Activity parentActivity;
        parentActivity = (Activity) ForumView.contextOfParent;
        parentActivity.finish();
    }

    public static void increaseProfilePageCounter(){
        ForumProfile.counter++;
    }
    public static void increaseViewTopicPageCounter(){
        ForumView.mViewForumTopicCounter++;
    }

    public static void editForumPosition(int editForumPosition){
        ForumView.editForumPosition = editForumPosition;
    }
}

