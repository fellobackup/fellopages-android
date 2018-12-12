package com.fellopages.mobileapp.classes.modules.store.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.modules.store.fragments.BrowseProductFragment;
import com.fellopages.mobileapp.classes.modules.store.fragments.BrowseStoreFragment;
import com.fellopages.mobileapp.classes.modules.store.fragments.ManageStoreFragment;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.ProductViewPage;
import com.fellopages.mobileapp.classes.modules.store.StoreViewPage;
import com.fellopages.mobileapp.classes.modules.store.order.BrowseOrderFragment;
import com.fellopages.mobileapp.classes.modules.store.order.OrderHomeFragment;
import com.fellopages.mobileapp.classes.modules.store.order.OrderViewActivity;
import com.fellopages.mobileapp.classes.modules.wishlist.ProductWishList;
import com.fellopages.mobileapp.classes.modules.wishlist.WishlistView;

import org.json.JSONObject;


public class StoreUtil {

    public static Fragment getBrowsePageInstance(){
        return new BrowseStoreFragment();
    }

    public static Fragment getManagePageInstance(){
        return new ManageStoreFragment();
    }

    public static Fragment getProductsBrowsePageInstance() {
        return new BrowseProductFragment();
    }

    public static Fragment getProductsManagePageInstance() {
        BrowseProductFragment browseProductFragment = new BrowseProductFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantVariables.IS_MANAGE_VIEW, true);
        browseProductFragment.setArguments(bundle);
        return browseProductFragment;
    }

    public static Fragment getOrderHomeFragment(){return new OrderHomeFragment();}
    public static Fragment getBrowseStoreInstance(){return new BrowseStoreFragment();}
    public static Fragment getBrowseProductPageInstance(){return new BrowseProductFragment();}

    public static Intent getUserReviewPageIntent(Context context, int id, String url, Bundle bundle){
        url+= "?getRating=1";
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PRODUCT_MENU_TITLE);
        bundle.putString(ConstantVariables.URL_STRING, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        bundle.putString(ConstantVariables.FRAGMENT_NAME, "reviews");
        Intent intent = new Intent(context, FragmentLoadActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Fragment getBrowseWishListPageInstance(){
        return new ProductWishList();
    }
    public static Fragment getBrowseOrderPageInstance(){
        return new BrowseOrderFragment();
    }

    public static Intent getWishlistViewPageIntent(Context context, int id, String url, Bundle bundle){
        Intent intent = new Intent(context, WishlistView.class);
        intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,"sitestoreproduct_wishlist");
        intent.putExtra(ConstantVariables.URL_STRING, UrlUtil.PRODUCT_WISHLIST_VIEW_URL+id);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getProductViewPageIntent(Context context,int product_id){
        Intent intent = new Intent(context, ProductViewPage.class);
        intent.putExtra("product_id", product_id);
        return intent;
    }

    public static Intent getStoreViewPageIntent(Context context,String store_id){
        Intent intent = new Intent(context, StoreViewPage.class);
        intent.putExtra("store_id", store_id);
        return intent;
    }

    public static Intent getStoreViewPageIntent(Context context, String store_id, @NonNull JSONObject storeDetails){
        Intent intent = new Intent(context, StoreViewPage.class);
        intent.putExtra("store_id", store_id);
        intent.putExtra("store_details", storeDetails.toString());
        return intent;
    }

    public static Intent getOrderViewPageIntent(Context context,int order_id){
        Intent intent = new Intent(context,OrderViewActivity.class);
        intent.putExtra(ConstantVariables.VIEW_PAGE_URL,UrlUtil.ORDER_VIEW_URL+order_id);
        return intent;
    }
}
