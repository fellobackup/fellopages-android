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

    private String ItemName, title, mItemRegName, mHeaderLabel, mItemUrl, mItemIcon, mListingSingularLabel;
    private String mBadgeCount,mIconBackgroundColor;
    private int mListingTypeId, mBrowseType, mViewType,mCanCreate, mPackagesEnabled,siteStoreEnabled,mListingEnabled;
    private boolean canView = true;
    int mSecondaryViewType;
    private int duplicate = 0;

    // For Notifications, Messages, Friend Request
    DrawerItem(String headerTitle, String itemName, String itemRegName, String badgeCount, int canCreate,
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

    DrawerItem(String headerTitle, String itemName, String itemRegName, String badgeCount, int canCreate,
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
    DrawerItem(String headerTitle, String itemName, String itemRegName, String icon, String iconColor){
        super();
        mHeaderLabel = headerTitle;
        ItemName = itemName;
        mItemRegName = itemRegName;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
    }

    DrawerItem(String itemName, String itemRegName, String headerLabel, String url, String icon, String iconColor){
        super();
        ItemName = itemName;
        mItemRegName = itemRegName;

        mHeaderLabel = headerLabel;
        mItemUrl = url;
        mItemIcon = icon;
        mIconBackgroundColor = iconColor;
    }

    // For MLT
    DrawerItem(String headerTitle, String itemName, String itemRegName, String listingSingularLabel,
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

    DrawerItem(String headerTitle, String title) {
        this(headerTitle, null, null, null, 0, 0, null, null, true);
        this.title = title;
    }

    DrawerItem(String headerTitle, String itemName, String itemRegName, String badgeCount, int canCreate,
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

    int getCanCreate() {
        return mCanCreate;
    }
    String getItemRegName() {

        return mItemRegName;
    }
    String getBadgeCount() {
        return mBadgeCount;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    String getItemName() {

        return ItemName;
    }

    String getmListingSingularLabel() {
        return mListingSingularLabel;
    }

    public int getmListingTypeId() {
        return mListingTypeId;
    }

    int getmBrowseType() {
        return mBrowseType;
    }

    int getmViewType() {
        return mViewType;
    }

    void setBadgeCount(String mBadgeCount) {
        this.mBadgeCount = mBadgeCount;
    }

    String getmHeaderLabel() {
        return mHeaderLabel;
    }

    String getmItemUrl() {
        return mItemUrl;
    }

    String getmItemIcon() {
        return mItemIcon;
    }

    int getmPackagesEnabled() {
        return mPackagesEnabled;
    }

    int getSiteStoreEnabled() {
        return siteStoreEnabled;
    }

    int getListingEnabled() {
        return mListingEnabled;
    }

    boolean isCanView() {
        return canView;
    }

    String getmIconBackgroundColor() {
        return mIconBackgroundColor;
    }

    public int getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }
}
