/*
 *
 * Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 * You may not use this file except in compliance with the
 * SocialEngineAddOns License Agreement.
 * You may obtain a copy of the License at:
 * https://www.socialengineaddons.com/android-app-license
 * The full copyright and license information is also mentioned
 * in the LICENSE file that was distributed with this
 * source code.
 *
 */

package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.activities.InviteGuest;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.advancedEvents.ticketsSelling.AdvEventsOrderViewPage;
import com.fellopages.mobileapp.classes.modules.offers.BrowseOffersFragment;


public class AdvEventsUtil {
    public static Fragment getBrowsePageInstance(){
        return new AdvEventsBrowseEventsFragment();
    }

    public static Fragment getManagePageInstance(){
        return new AdvEventsMyEventsFragment();
    }

    public static Fragment getDiariesPageInstance(){
        return new AdvEventsBrowseDiariesFragment();
    }

    public static Fragment getCalendarPageInstance(){
        return new AdvEventsCalendarFragment();
    }

    public static Fragment getBrowseDiariesInstance(){
        return new AdvEventsBrowseDiariesFragment();
    }

    public static Fragment getTicketPageInstance() {
        return new AdvEventsMyTicketsFragment();
    }

    public static Fragment getCouponPageInstance() {
        BrowseOffersFragment browseOffersFragment = new BrowseOffersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantVariables.URL_STRING, UrlUtil.BROWSE_COUPONS_ADV_EVENTS_URL);
        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
        browseOffersFragment.setArguments(bundle);
        return browseOffersFragment;
    }

    public static Intent getViewPageIntent(Context context, int id, String url, Bundle bundle){
        url += "advancedevents/view/"+ id +"?gutter_menu=" + 1;

        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        Intent intent = new Intent(context, AdvEventsProfilePage.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getViewDiaryIntent(Context context, int id, String url, Bundle bundle) {

        url += "advancedevents/diary/"+ id +"?&limit=" + AppConstant.LIMIT;

        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
        bundle.putString(ConstantVariables.VIEW_PAGE_URL, url);
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        Intent intent = new Intent(context, AdvEventsViewDiaries.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getUserReviewPageIntent (Context context, int id, String url, Bundle bundle ) {

        url += "advancedevents/reviews/browse/event_id/" + id;

        bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
        bundle.putString(ConstantVariables.URL_STRING, url);
        bundle.putString(ConstantVariables.FRAGMENT_NAME, "reviews");
        bundle.putInt(ConstantVariables.VIEW_PAGE_ID, id);
        Intent intent = new Intent(context, FragmentLoadActivity.class);
        intent.putExtras(bundle);
        return intent;

    }

    public static Intent getEventTicketOrderIntent (Context context, int id, String url, Bundle bundle) {

        url = UrlUtil.ORDER_VIEW_PAGE_ADV_EVENTS_URL + "order_id=" + id;

        bundle.putString(ConstantVariables.URL_STRING, url);
        bundle.putString("order_id", String.valueOf(id));
        Intent intent = new Intent(context, AdvEventsOrderViewPage.class);
        intent.putExtras(bundle);
        return intent;

    }

    public static Intent getInviteGuestIntent(Context context) {
        return new Intent(context, InviteGuest.class);
    }

}
