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

package com.fellopages.mobileapp.classes.modules.user.profile;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.ui.CustomViewPager;

public class EditProfileActivity extends FormActivity implements TabLayout.OnTabSelectedListener {

    private Toolbar mToolBar;
    private CustomViewPager mViewPager;
    private TabLayout tabHost;
    private FragmentAdapter adapter;
    private Bundle bundle;
    public static boolean isProfileUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_activity_view);

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_user_personal_info_tab));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolBar);

        initBottomSheet();

        bundle = getIntent().getExtras();

        mViewPager = findViewById(R.id.viewpager);
        tabHost = findViewById(R.id.materialTabHost);

        if (mViewPager != null) {
            adapter = new FragmentAdapter(getSupportFragmentManager());

            adapter.addFragment(EditPersonalInfoFragment.newInstance(bundle),
                    getResources().getString(R.string.edit_user_personal_info_tab));
            adapter.addFragment(EditProfilePhotoFragment.newInstance(bundle),
                    getResources().getString(R.string.edit_user_profile_photo_tab));
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(adapter.getCount() + 1);
            tabHost.setupWithViewPager(mViewPager);

            if (bundle.getBoolean("is_photo_tab") && tabHost.getTabAt(1) != null) {
                mViewPager.setCurrentItem(1);
                setToolBarTitle(tabHost.getTabAt(1));
            }
        }

        // insert all tabs from pagerAdapter data
        tabHost.addOnTabSelectedListener(this);
    }

    private void initBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.edit_profile_save_button_text));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){

            case R.id.submit:
                return false;
            case R.id.delete:
                return false;
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(EditProfileActivity.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(EditProfileActivity.this);
                }
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * Method to set toolbar title according to selected tab.
     * @param tab Selected tab.
     */
    private void setToolBarTitle(TabLayout.Tab tab) {
        String tabTitle;
        if (tab.getPosition() == 0) {
            tabTitle = getResources().getString(R.string.edit_user_personal_info_tab);
        } else {
            tabTitle = getResources().getString(R.string.edit_user_profile_photo_tab);
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(tabTitle);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // when the tab is clicked the pager swipe content to the tab position
        mViewPager.setCurrentItem(tab.getPosition());
        setToolBarTitle(tab);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {

        // Setting result when the profile is updated.
        if (isProfileUpdated) {
            Intent intent = new Intent();
            setResult(ConstantVariables.USER_PROFILE_CODE, intent);
            isProfileUpdated = false;
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
