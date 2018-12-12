package com.fellopages.mobileapp.classes.common.utils;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import com.fellopages.mobileapp.R;

public class CustomTabUtil {

    public static void launchCustomTab(Activity activity, String url){
        int color = ContextCompat.getColor(activity, R.color.themeButtonColor);
        int secondaryColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(color);
        builder.setSecondaryToolbarColor(secondaryColor);
        builder.setCloseButtonIcon(
                BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_arrow_back));

        builder.setStartAnimations(activity, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(activity, R.anim.slide_in_left, R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }
}
