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

package com.fellopages.mobileapp.classes.modules.store.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;

public class OrderHomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private View rootView;
    private ViewPager pager;
    private TabLayout tabHost;
    private FragmentAdapter adapter;

    public OrderHomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggle = menu.findItem(R.id.viewToggle);
        if (toggle != null) {
            toggle.setVisible(false);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.view_pager, null);
        pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tabHost = (TabLayout) getActivity().findViewById(R.id.materialTabHost);
        tabHost.setVisibility(View.VISIBLE);
        tabHost.setTabMode(TabLayout.MODE_FIXED);

        if (pager != null) {
            adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());

            adapter.addFragment(new BrowseOrderFragment(),
                    getActivity().getResources().getString(R.string.my_orders));
            adapter.addFragment(new MyDownloadsFragment(),
                    getActivity().getResources().getString(R.string.my_downloads));
            Fragment storeOrderFragment = new BrowseOrderFragment();
            Bundle args = new Bundle();
            args.putString("isStoreOrders","1");
            storeOrderFragment.setArguments(args);
            adapter.addFragment(storeOrderFragment,
                    getActivity().getResources().getString(R.string.my_store_orders));

            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(adapter.getCount() + 1);
            tabHost.setupWithViewPager(pager);
        }

        // insert all tabs from pagerAdapter data
        tabHost.addOnTabSelectedListener(this);
        return rootView;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabHost = null;
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
