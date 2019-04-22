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

public class NetworkList {

    int networkId, memberCount;
    String networkTitle;


    public NetworkList(int networkId, int memberCount, String networkTitle) {
        this.networkId = networkId;
        this.memberCount = memberCount;
        this.networkTitle = networkTitle;
    }

    public int getNetworkId() {
        return networkId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getNetworkTitle() {
        return networkTitle;
    }

}
