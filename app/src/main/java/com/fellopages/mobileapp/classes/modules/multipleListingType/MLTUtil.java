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

package com.fellopages.mobileapp.classes.modules.multipleListingType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.wishlist.MLTWishList;
import com.fellopages.mobileapp.classes.modules.wishlist.WishlistView;

public class MLTUtil {

    public static Fragment getBrowsePageInstance(){
        return new BrowseMLTFragment();
    }

    public static Fragment getManagePageInstance(){
        return new MyMLTFragment();
    }

    public static Fragment getBrowseWishListPageInstance(){
        return new MLTWishList();
    }

    public static Fragment getWhereToBuyInstance(){
        return new WhereToBuyFragment();
    }

    //TODO
    public static int getSelectedViewType(){
        return BrowseMLTFragment.selectedViewType;
    }

    public static void checkPermission(Fragment callingFragment, String url){
        ((MyMLTFragment)callingFragment).checkStoragePermission(url);
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){
        url += "listing/view/"+ id + "?gutter_menu=1";
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        Intent intent = new Intent(context, MLTView.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getUserReviewPageIntent(Context context, int id, String url, Bundle bundle){
        url += "listings/reviews?" + "listing_id=" + id;
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.MLT_MENU_TITLE);
        bundle.putString(ConstantVariables.URL_STRING, url);
        bundle.putString(ConstantVariables.FRAGMENT_NAME, "reviews");
        bundle.putInt(ConstantVariables.LISTING_ID, id);
        Intent intent = new Intent(context, FragmentLoadActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getWishlistViewPageIntent(Context context, int id, String url, Bundle bundle){
        url += "listings/wishlist/" + id + "?limit=" + AppConstant.LIMIT;
        bundle.putString(ConstantVariables.URL_STRING, url);
        Intent intent = new Intent(context, WishlistView.class);
        intent.putExtras(bundle);
        return intent;
    }
}
