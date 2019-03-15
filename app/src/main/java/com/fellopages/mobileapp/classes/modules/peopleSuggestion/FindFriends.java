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

package com.fellopages.mobileapp.classes.modules.peopleSuggestion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.friendrequests.FeedFriendRequests;
import com.fellopages.mobileapp.classes.modules.user.BrowseMemberFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class FindFriends extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    //Member variables
    private Context mContext;
    private TabLayout mTabHost;
    private ViewPager mViewPager;
    private FragmentAdapter mFragmentAdapter;
    private MenuItem mSearchMenuItem;
    private AppConstant mAppConst;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_activity_view);
        mContext = FindFriends.this;
        mAppConst = new AppConstant(mContext);

        // Setting-up the tool bar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CustomViews.createMarqueeTitle(this, toolbar);

        initBottomSheet();
        // Getting views
        mViewPager = findViewById(R.id.viewpager);
        mTabHost = findViewById(R.id.materialTabHost);

        if (mViewPager != null) {
            mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

            // Adding all tabs.
            mFragmentAdapter.addFragment(PeopleSuggestionFragment.newInstance(null),
                    getResources().getString(R.string.suggestion_tab));
            mFragmentAdapter.addFragment(BrowseMemberFragment.newInstance(setBundleForFragment("search_tab")),
                    getResources().getString(R.string.search));
            mFragmentAdapter.addFragment(FeedFriendRequests.newInstance(null),
                    getResources().getString(R.string.request_tab));
            mFragmentAdapter.addFragment(BrowseMemberFragment.newInstance(setBundleForFragment("contact_tab")),
                    getResources().getString(R.string.contact_tab));

            // Showing Invite Tab if contacts are synced.
            if (PreferencesUtils.isContactSynced(mContext)
                    && mAppConst.checkManifestPermission(Manifest.permission.READ_CONTACTS)) {
                mFragmentAdapter.addFragment(new InviteContactFragment(),
                        getResources().getString(R.string.invite));
            } else {
                PreferencesUtils.setContactSyncedInfo(mContext, false);
            }
            mFragmentAdapter.addFragment(BrowseMemberFragment.newInstance(setBundleForFragment("friend_tab")),
                    getResources().getString(R.string.action_bar_title_friend));
            mFragmentAdapter.addFragment(BrowseMemberFragment.newInstance(setBundleForFragment("outgoing_tab")),
                    getResources().getString(R.string.outgoing_tab));

            mViewPager.setAdapter(mFragmentAdapter);
            mViewPager.setOffscreenPageLimit(mFragmentAdapter.getCount() + 1);
            mTabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
            mTabHost.setupWithViewPager(mViewPager);
        }

        mTabHost.setOnTabSelectedListener(this);
    }

    private void initBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    /**
     * Method to put bundle into fragment to be loaded.
     * @param fragmentName Fragment name in which bundle is to be set.
     * @return Returns the bundle with the attached data.
     */
    public Bundle setBundleForFragment(String fragmentName) {
        Bundle bundle = new Bundle();
        switch (fragmentName) {

            case "search_tab":
            case "contact_tab":
            case "outgoing_tab":
                bundle.putBoolean(fragmentName, true);
                break;

            case "friend_tab":
                bundle.putBoolean("isMemberFriends", true);
                bundle.putBoolean("isFindFriends", true);
                if(PreferencesUtils.getUserDetail(mContext) != null){
                    try {
                        JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                        bundle.putInt("user_id", Integer.parseInt(userDetail.optString("user_id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        return bundle;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_cart).setVisible(false);
        menu.findItem(R.id.action_location).setVisible(false);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        if (mSearchMenuItem != null) {
            mSearchMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }

        } else if (id == R.id.action_search) {
            //calling the search activity
            Intent searchActivity = new Intent(mContext, SearchActivity.class);
            searchActivity.putExtra(ConstantVariables.IS_SEARCHED_FROM_DASHBOARD, false);
            searchActivity.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "core_main_user");
            startActivity(searchActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // when the tab is clicked the pager swipe content to the tab position
        mViewPager.setCurrentItem(tab.getPosition());

        // When the search tab is visible then show the search icon in option menu.
        if (mSearchMenuItem != null) {
            mSearchMenuItem.setVisible(mViewPager.getCurrentItem() == 1);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (mFragmentAdapter != null && mFragmentAdapter.getCount() > 0
                && tab.getPosition() < mFragmentAdapter.getCount()
                && mFragmentAdapter.getItem(tab.getPosition()) != null) {
            Fragment fragment = mFragmentAdapter.getItem(tab.getPosition());
            fragment.onAttach(mContext);
        }
    }

}
