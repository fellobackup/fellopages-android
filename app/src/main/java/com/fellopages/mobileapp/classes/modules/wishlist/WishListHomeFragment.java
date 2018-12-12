package com.fellopages.mobileapp.classes.modules.wishlist;

import android.content.Context;
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

public class WishListHomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    //Member variables.
    private Context mContext;
    private View rootView;
    private ViewPager pager;
    private TabLayout tabHost;
    private FragmentAdapter adapter;

    public WishListHomeFragment() {
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

        mContext = getActivity();
        rootView = inflater.inflate(R.layout.view_pager, null);
        pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tabHost = (TabLayout) getActivity().findViewById(R.id.materialTabHost);
        tabHost.setVisibility(View.VISIBLE);
        tabHost.setTabMode(TabLayout.MODE_FIXED);



        if (pager != null) {
            adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
            adapter.addFragment(new ProductWishList(),getActivity().getResources().getString(R.string.browse_products));
            adapter.addFragment(new MLTWishList(),getActivity().getResources().getString(R.string.mlt_wishlist_browse_tab));

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        if (adapter != null && adapter.getCount() > 0 && tab.getPosition() < adapter.getCount()
                && adapter.getItem(tab.getPosition()) != null) {
            Fragment fragment = adapter.getItem(tab.getPosition());
            fragment.onAttach(mContext);
        }
    }
}
