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

package com.fellopages.mobileapp.classes.core;

public class DrawerItem {

    String ItemName, title, mItemRegName, mHeaderLabel, mItemUrl, mItemIcon, mListingSingularLabel;
    String mBadgeCount,mIconBackgroundColor;
    int mListingTypeId, mBrowseType, mViewType,mCanCreate, mPackagesEnabled,siteStoreEnabled,mListingEnabled;
    boolean canView = true;
    public int mSecondaryViewType;
    public int duplicate = 0;

    // For Notifications, Messages, Friend Request
    public DrawerItem(String headerTitle, String itemName, String itemRegName, String badgeCount, int canCreate,
                      int packagesEnabled, String icon, String iconColor, boolean canView){
        super();
        mHeaderLabel = headerTitle;
        ItemName=itemName;
        mItemRegName=itemRegName;
        mBadgeCount =  badgeCount;
        mCanCreate = canCreate;
        mPackagesEnabled = packagesEnabled;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
        this.canView = canView;
    }

    public DrawerItem(String headerTitle, String itemName, String itemRegName, String badgeCount, int canCreate,
                      int packagesEnabled, String icon, String iconColor, boolean canView, int duplicate){
        super();
        mHeaderLabel = headerTitle;
        ItemName=itemName;
        mItemRegName=itemRegName;
        mBadgeCount =  badgeCount;
        mCanCreate = canCreate;
        mPackagesEnabled = packagesEnabled;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
        this.canView = canView;
        this.duplicate = duplicate;
    }

    // For Other Options in drawer
    public DrawerItem(String headerTitle, String itemName, String itemRegName, String icon, String iconColor){
        super();
        mHeaderLabel = headerTitle;
        ItemName = itemName;
        mItemRegName = itemRegName;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
    }

    public DrawerItem(String itemName, String itemRegName, String headerLabel, String url, String icon, String iconColor){
        super();
        ItemName = itemName;
        mItemRegName = itemRegName;

        mHeaderLabel = headerLabel;
        mItemUrl = url;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
    }

    // For MLT
    public DrawerItem(String headerTitle, String itemName, String itemRegName, String listingSingularLabel,
                      String url, String icon, String iconColor, int listingTypeId, int browseType, int viewType, int canCreate,
                      int packagesEnabled, boolean canView, int secondaryViewType){
        super();
        mHeaderLabel = headerTitle;
        ItemName=itemName;
        mItemRegName=itemRegName;
        mListingSingularLabel = listingSingularLabel;
        mItemUrl = url;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
        mListingTypeId = listingTypeId;
        mBrowseType = browseType;
        mViewType = viewType;
        mCanCreate = canCreate;
        mPackagesEnabled = packagesEnabled;
        this.canView = canView;
        mSecondaryViewType = secondaryViewType;
    }

    public DrawerItem(String headerTitle, String title) {
        this(headerTitle, null, null, null, 0, 0, null, null, true);
        this.title = title;
    }

    public DrawerItem(String headerTitle, String itemName, String itemRegName, String badgeCount, int canCreate,
                      int packagesEnabled, String icon, String iconColor, int sitestore, int sitereview) {
        super();
        mHeaderLabel = headerTitle;
        ItemName=itemName;
        mItemRegName=itemRegName;
        mBadgeCount =  badgeCount;
        mCanCreate = canCreate;
        mPackagesEnabled = packagesEnabled;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
        siteStoreEnabled = sitestore;
        mListingEnabled = sitereview;
    }

    public int getCanCreate() {
        return mCanCreate;
    }
    public String getItemRegName() {

        return mItemRegName;
    }
    public String getBadgeCount() {
        return mBadgeCount;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getItemName() {

        return ItemName;
    }

    public String getmListingSingularLabel() {
        return mListingSingularLabel;
    }

    public int getmListingTypeId() {
        return mListingTypeId;
    }

    public int getmBrowseType() {
        return mBrowseType;
    }

    public int getmViewType() {
        return mViewType;
    }

    public void setBadgeCount(String mBadgeCount) {
        this.mBadgeCount = mBadgeCount;
    }

    public String getmHeaderLabel() {
        return mHeaderLabel;
    }

    public String getmItemUrl() {
        return mItemUrl;
    }

    public String getmItemIcon() {
        return mItemIcon;
    }

    public int getmPackagesEnabled() {
        return mPackagesEnabled;
    }

    public int getSiteStoreEnabled() {
        return siteStoreEnabled;
    }

    public int getListingEnabled() {
        return mListingEnabled;
    }

    public boolean isCanView() {
        return canView;
    }

    public String getmIconBackgroundColor() {
        return mIconBackgroundColor;
    }

    public int getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }
}
