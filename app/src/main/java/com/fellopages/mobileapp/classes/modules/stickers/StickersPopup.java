/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.modules.stickers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StickersPopup{

    private int keyBoardHeight = 498;
    StickersClickListener onStickerClickedListener;
    View rootView;
    Context mContext;
    private JSONObject mStickersResponse;
    public JSONArray mCollectionsList, mSearchList;

    public ViewPager viewPager;
    public TabLayout mTabLayout;
    public MyAdapter myAdapter;
    private ImageButton mAddStickerStoreButton;
    public List<StickersGridView> viewsList;
    public List<SearchStickersGridView> searchViewList;
    public HashMap<Integer, StickersGridView> stickerGridViewList = new HashMap<>();
    public RelativeLayout mStickersParentView;
    private AppConstant mAppConst;
    private ImageLoader mImageLoader;

    /**
     * Constructor
     * @param rootView	The top most layout in your view hierarchy. The difference of this view and the screen height will be used to calculate the keyboard height.
     * @param mContext The context of current activity.
     */
    public StickersPopup(View rootView, Context mContext, JSONObject stickersResponse, RelativeLayout stickersParentView){
        this.mContext = mContext;
        this.rootView = rootView;
        mStickersResponse = stickersResponse;
        this.mStickersParentView = stickersParentView;
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);
        keyBoardHeight = (int) (AppConstant.getDisplayMetricsHeight(mContext) / 2.5);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, keyBoardHeight);
        mStickersParentView.setLayoutParams(layoutParams);
        createCustomView();
    }

    /**
     * Set the listener for the event when any of the sticker is clicked
     */
    public void setOnStickerClickedListener(StickersClickListener listener){
        this.onStickerClickedListener = listener;
    }

    /**
     * Call this function to resize the stickers popup according to your soft keyboard size
     */
    public void setSizeForSoftKeyboard(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = mContext.getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= mContext.getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    keyBoardHeight = heightDifference;
                }
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, keyBoardHeight);
                mStickersParentView.setLayoutParams(layoutParams);
            }
        });
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return rootView.getRootView().getHeight();
        }
    }

    private View createCustomView() {
        mTabLayout = (TabLayout) mStickersParentView.findViewById(R.id.tabs);
        viewPager = (ViewPager) mStickersParentView.findViewById(R.id.viewpager);
        mAddStickerStoreButton = (ImageButton) mStickersParentView.findViewById(R.id.add_sticker_store);

        if (!StickersUtil.isStorySticker) {
            mTabLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_light));
            viewPager.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_background));
            mAddStickerStoreButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_background));
        } else {
            mTabLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
            viewPager.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
            mAddStickerStoreButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
        }

        mCollectionsList = mStickersResponse.optJSONArray("collectionList");
        mSearchList = mStickersResponse.optJSONArray("searchList");
        viewsList = new ArrayList<>();
        searchViewList = new ArrayList<>();

        if(mSearchList != null && mSearchList.length() != 0){
            searchViewList.add(new SearchStickersGridView(mContext, mSearchList, this));
        }

        for(int i = 0; i < mCollectionsList.length(); i++){
            JSONObject jsonObject = mCollectionsList.optJSONObject(i);
            StickersGridView stickersGridView = new StickersGridView(mContext, jsonObject.optInt("collection_id"), this);
            viewsList.add(stickersGridView);
            stickerGridViewList.put(jsonObject.optInt("collection_id"), stickersGridView);
        }

        myAdapter = new MyAdapter();
        viewPager.setAdapter(myAdapter);
        viewPager.setOffscreenPageLimit(myAdapter.getCount() + 1);
        mTabLayout.setTabTextColors(ContextCompat.getColor(mContext, R.color.textColorPrimary),
                ContextCompat.getColor(mContext, R.color.themeButtonColor));
        mTabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        mAddStickerStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StickerStore.class);
                ((Activity)mContext).startActivityForResult(intent, ConstantVariables.STICKER_STORE_REQUEST);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != 0){
                    mAppConst.hideKeyboard();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return mStickersParentView;
    }

    private class MyAdapter extends PagerAdapter {
        private ArrayList<View> views = new ArrayList<>();
        public MyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return viewsList.size() + searchViewList.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v;

            if (searchViewList != null && searchViewList.size() > 0) {
                if(position == 0){
                    v = searchViewList.get(position).rootView;
                } else{
                    v = viewsList.get(position - 1).rootView;
                }
            } else {
                v = viewsList.get(position).rootView;
            }

            views.add(v);
            container.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            if (position < views.size()) {
                views.remove(position);
                container.removeView((View)view);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object key) {
            return key == view;
        }

        @Override
        public int getItemPosition(Object object) {
            if (views.contains(object)) {
                return views.indexOf(object);
            } else {
                return POSITION_NONE;
            }
        }
    }

    public void setupTabIcons(){


        if(mSearchList != null && mSearchList.length() > 0){
            mTabLayout.getTabAt(0).setIcon(R.drawable.ic_action_search);
        }

        int imageWidthHeight = (int )mContext.getResources().getDimension(R.dimen.stickers_image_width_height);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidthHeight, imageWidthHeight);
        if(mCollectionsList != null && mCollectionsList.length() != 0){

            for(int i = 0; i < mCollectionsList.length() ; i++){

                View linearLayout = LayoutInflater.from(mContext).inflate(R.layout.reactions_tab_layout, null);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.reactionIcon);
                TextView textView = (TextView) linearLayout.findViewById(R.id.reactionsCount);

                imageView.setLayoutParams(layoutParams);
                JSONObject jsonObject = mCollectionsList.optJSONObject(i);
                String imageIcon = jsonObject.optString("image_icon");
                if (!((Activity)mContext).isFinishing()) {
                    mImageLoader.setImageUrl(imageIcon, imageView);
                }
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                if(mSearchList != null && mSearchList.length() > 0){
                    mTabLayout.getTabAt(i + 1).setCustomView(linearLayout);
                } else {
                    mTabLayout.getTabAt(i).setCustomView(linearLayout);
                }
            }
        }
    }

}