package com.fellopages.mobileapp.classes.common.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.MyEventsPagerAdapter;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsBrowseEventsFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsCalendarFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsMyEventsFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsMyTicketsFragment;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsUtil;

public class MyEventsFragment extends Fragment implements TabLayout.OnTabSelectedListener{
    Context mContext;
    private View rootView;
    private ViewPager pager;
    private TabLayout tabHost;
    MyEventsPagerAdapter adapter;
    private AppConstant mAppConst;

    public MyEventsFragment() {
    }

    public static MyEventsFragment newInstance(Bundle bundle) {
        // Required  public constructor
        MyEventsFragment fragment = new MyEventsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        rootView = inflater.inflate(R.layout.fragment_my_events, null);
        pager = rootView.findViewById(R.id.viewpager);
        tabHost = rootView.findViewById(R.id.materialTabHostHome);
        adapter = new MyEventsPagerAdapter(getChildFragmentManager());
        tabHost.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabHost.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

        adapter.addFragment(AdvEventsBrowseEventsFragment.newInstance(null), mContext.getResources().
                getString(R.string.advanced_events_browse_tab_title));
        adapter.addFragment(AdvBrowseCategoriesFragment.newInstance(null), mContext.getResources().
                getString(R.string.advanced_events_categories_tab_title));

        // Do not show Manage tab to Logged-out users
        if (!mAppConst.isLoggedOutUser()) {
            adapter.addFragment(AdvEventsMyEventsFragment.newInstance(null), mContext.getResources().
                    getString(R.string.advanced_events_my_tab_title));

            // Adding ticket and coupons tab when the ticket selling is enabled from admin panel.
            if (PreferencesUtils.isTicketEnabled(mContext)) {
                adapter.addFragment(AdvEventsMyTicketsFragment.newInstance(null), mContext.getResources().
                        getString(R.string.advanced_events_my_tickets_tab_title));
                adapter.addFragment(AdvEventsUtil.getCouponPageInstance(), mContext.getResources().
                        getString(R.string.coupon_tab));
            }
        }

        adapter.addFragment(AdvEventsCalendarFragment.newInstance(null), mContext.getResources().
                getString(R.string.advanced_events_calendar_tab_title));

        if (pager != null && adapter != null) {
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(adapter.getCount() + 1);
            tabHost.setupWithViewPager(pager);
            tabHost.addOnTabSelectedListener(this);
        }

        PreferencesUtils.updateCurrentList(mContext, "browse_siteevent");
        
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

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (adapter != null && adapter.getCount() > 0 && tab.getPosition() < adapter.getCount()
                && adapter.getItem(tab.getPosition()) != null) {
            Fragment fragment = adapter.getItem(tab.getPosition());
            fragment.onAttach(mContext);
        }
    }
}
