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

package com.fellopages.mobileapp.classes.common.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

public class CheckInList implements Parcelable {

    String mLocationLabel, mPlaceId, mLocationIcon, mFormattedAddress, mVicinity;
    Double latitude, longitude;
    JSONArray mTypesArray;


    public CheckInList(String locationIcon, String placeId, String locationLabel, String formattedAddress,
                       Double lat, Double lng, String vicinity, JSONArray typesArray) {
        this.mLocationIcon = locationIcon;
        this.mPlaceId = placeId;
        this.mLocationLabel = locationLabel;
        this.mFormattedAddress = formattedAddress;
        this.mVicinity = vicinity;
        this.latitude = lat;
        this.longitude = lng;
        this.mTypesArray = typesArray;
    }

    public CheckInList(Parcel in) {
        super();
        mLocationLabel = in.readString();
        mPlaceId = in.readString();
        mLocationIcon = in.readString();
    }

    public static final Parcelable.Creator<CheckInList> CREATOR = new Parcelable.Creator<CheckInList>() {
        public CheckInList createFromParcel(Parcel in) {
            return new CheckInList(in);
        }

        public CheckInList[] newArray(int size) {

            return new CheckInList[size];
        }

    };


    public String getmLocationLabel() {
        return mLocationLabel;
    }

    public String getmPlaceId() {
        return mPlaceId;
    }

    public String getmLocationIcon() {
        return mLocationIcon;
    }

    public String getmFormattedAddress() {
        return mFormattedAddress;
    }

    public String getmVicinity() {
        return mVicinity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public JSONArray getmTypesArray() {
        return mTypesArray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(mLocationLabel);
        parcel.writeString(mPlaceId);
        parcel.writeString(mLocationIcon);
    }
}
