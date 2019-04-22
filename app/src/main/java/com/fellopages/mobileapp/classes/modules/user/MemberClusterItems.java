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

package com.fellopages.mobileapp.classes.modules.user;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import org.json.JSONObject;

public class MemberClusterItems implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private final BitmapDescriptor mBitmapDescriptor;
    private final JSONObject mResponseObject;
    private final BrowseListItems mBrowseListItem;

    public MemberClusterItems(double lat, double lng, BitmapDescriptor bitmapDescriptor, JSONObject responseObject,
                              BrowseListItems browseListItems) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
        mBitmapDescriptor = bitmapDescriptor;
        mResponseObject = responseObject;
        mBrowseListItem = browseListItems;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public BitmapDescriptor getBitmapDescriptor() {
        return mBitmapDescriptor;
    }

    public JSONObject getResponseObject() {
        return mResponseObject;
    }

    public BrowseListItems getBrowseListItem() {
        return mBrowseListItem;
    }
}
