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

package com.fellopages.mobileapp.classes.modules.messages;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageHomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private Context mContext;
    private View rootView;
    private ViewPager pager;
    private TabLayout tabHost;
    private FragmentAdapter adapter;

    public MessageHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        rootView = inflater.inflate(R.layout.view_pager, null);
        pager = (ViewPager) rootView.findViewById(R.id.viewpager);

        tabHost = (TabLayout) getActivity().findViewById(R.id.materialTabHost);
        tabHost.setVisibility(View.VISIBLE);
        tabHost.setTabMode(TabLayout.MODE_FIXED);
        if (pager != null) {
            adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());

            adapter.addFragment(InboxFragment.newInstance(null),
                    getResources().getString(R.string.inbox_tab));
            adapter.addFragment(SentBoxFragment.newInstance(null),
                    getResources().getString(R.string.sentbox_tab));

            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(adapter.getCount() + 1);
            tabHost.setupWithViewPager(pager);
        }



        // insert all tabs from pagerAdapter data
        tabHost.setOnTabSelectedListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabHost = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // when the tab is clicked the pager swipe content to the tab position
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (adapter != null && adapter.getCount() > 0) {
            Fragment fragment = adapter.getItem(tab.getPosition());
            fragment.onAttach(mContext);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

}
