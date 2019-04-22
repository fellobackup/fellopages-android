package com.fellopages.mobileapp.classes.common.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MyEventsPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();
    private final List<Drawable> mFragmentIcons = new ArrayList<>();

    public MyEventsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyEventsPagerAdapter(FragmentManager fm, JSONArray tabsArray){
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }
    public void addFragmentWithIcon(Fragment fragment,Drawable icon,String title ){
        mFragments.add(fragment);
        mFragmentIcons.add(icon);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    public Drawable getIcon(int position){
        return mFragmentIcons.get(position);
    }
}
