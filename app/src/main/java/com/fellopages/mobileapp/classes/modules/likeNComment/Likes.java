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

package com.fellopages.mobileapp.classes.modules.likeNComment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.fragments.ReactionsFragment;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Likes extends AppCompatActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener{

    private String mViewAllLikesUrl, mViewLikesBaseUrl, mResponseString = null;
    private AppConstant mAppConst;
    private JSONArray mAllLikesArray;
    private List<CommentList> mLikeListItems;
    private CommentList mCommentList;
    private CommentAdapter mLikeAdapter;
    private ListView mLikeListView;
    private Toolbar mToolbar;
    private int pageNumber = 1, mTotalLikes, mReactionsEnabled;
    private View footerView;
    private boolean isLoading = false, isGuestList = false;
    ViewPager viewPager;
    TabLayout mTabLayout;
    private JSONArray mReactionTabs;
    private Context mContext;
    private View mTabSaperator;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        /*
        Set Back Button on Action Bar
         */
        mToolbar = findViewById(R.id.toolbar);
        mContext = this;
        mImageLoader = new ImageLoader(getApplicationContext());

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mReactionsEnabled = getIntent().getIntExtra("reactionsEnabled", 0);

        if (getIntent().hasExtra(ConstantVariables.MENU_ARRAY)) {
            mResponseString = getIntent().getStringExtra(ConstantVariables.MENU_ARRAY);
            setTitle(getResources().getString(R.string.guest_list_text));
            isGuestList = true;
        } else if(mReactionsEnabled == 1){
            setTitle(getResources().getString(R.string.reaction_text));
        } else{
            setTitle(getResources().getString(R.string.like_activity_text));
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        mTabSaperator = findViewById(R.id.tabSaperator);
        viewPager = findViewById(R.id.viewpager);
        mTabLayout= findViewById(R.id.tabs);
        mLikeListView = findViewById(R.id.likeList);

        mAppConst = new AppConstant(this);

        footerView = CustomViews.getFooterView(getLayoutInflater());

        mViewAllLikesUrl = mViewLikesBaseUrl = getIntent().getStringExtra("ViewAllLikesUrl");
        mViewAllLikesUrl += "&page=" + pageNumber + "&limit=" + AppConstant.LIMIT;

        if(mReactionsEnabled != 1){

            mLikeListItems = new ArrayList<>();
            mCommentList = new CommentList();

            mLikeAdapter = new CommentAdapter(this, R.layout.list_comment, mLikeListItems, mCommentList, false);
            mLikeListView.setAdapter(mLikeAdapter);
            mLikeListView.setOnScrollListener(this);
            mLikeListView.setOnItemClickListener(this);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.VISIBLE);
            mLikeListView.setVisibility(View.GONE);
        }

        if (isGuestList) {
            try {
                if (mResponseString != null) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    JSONArray mDataResponse = new JSONArray(mResponseString);

                    for (int i = 0; i < mDataResponse.length(); i++){
                        JSONObject guestListObject = mDataResponse.getJSONObject(i);
                        int user_id = guestListObject.optInt("guest_id");
                        String displayName = guestListObject.optString("guest_title");
                        String photoUrl = guestListObject.optString("image_profile");
                        String friendshipType = guestListObject.optString("friendship_type");
                        int isVerified = guestListObject.optInt("isVerified");
                        mLikeListItems.add(new CommentList(user_id, displayName, photoUrl, friendshipType, isVerified));
                    }

                    mLikeAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            mAppConst.getJsonResponseFromUrl(mViewAllLikesUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (jsonObject != null) {
                        try {
                            mAllLikesArray = jsonObject.optJSONArray("viewAllLikesBy");
                            mTotalLikes = jsonObject.optInt("getTotalLikes");
                            if (mAllLikesArray != null) {
                                if(mReactionsEnabled == 1){
                                    mReactionTabs = jsonObject.optJSONArray("reactionTabs");
                                    MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
                                    viewPager.setAdapter(myAdapter);
                                    viewPager.setOffscreenPageLimit(myAdapter.getCount() + 1);
                                    mTabLayout.setTabTextColors(ContextCompat.getColor(mContext, R.color.grey),
                                            ContextCompat.getColor(mContext, R.color.themeButtonColor));
                                    mTabLayout.setupWithViewPager(viewPager);
                                    setupTabIcons();
                                    mTabSaperator.setVisibility(View.VISIBLE);
                                } else {
                                    for (int i = 0; i < mAllLikesArray.length(); i++){
                                        JSONObject likeInfoObject = mAllLikesArray.getJSONObject(i);
                                        int user_id = likeInfoObject.optInt("user_id");
                                        String displayName = likeInfoObject.getString("displayname");
                                        String photoUrl = likeInfoObject.optString("image_profile");
                                        String friendshipType = likeInfoObject.optString("friendship_type");
                                        int isVerified = likeInfoObject.optInt("isVerified");
                                        mLikeListItems.add(new CommentList(user_id, displayName, photoUrl,
                                                friendshipType, isVerified));
                                    }
                                    mLikeAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarShortWithListener(findViewById(R.id.like_activity),
                            message, new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    finish();
                                }
                            });
                }
            });

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(Likes.this)) {
                SoundUtil.playSoundEffectOnBackPressed(Likes.this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;

        if(limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * pageNumber) <
                    mTotalLikes) {
                CustomViews.addFooterView(mLikeListView, footerView);
                pageNumber += 1;
                String likeCommentsUrl = mViewLikesBaseUrl + "&page=" + pageNumber + "&limit=" + AppConstant.LIMIT;
                isLoading = true;
                loadMoreLikes(likeCommentsUrl);

            }
        }
    }

    public void loadMoreLikes(String url){
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                CustomViews.removeFooterView(mLikeListView, footerView);
                if (jsonObject != null) {
                    try {
                        mAllLikesArray = jsonObject.optJSONArray("viewAllLikesBy");
                        mTotalLikes = jsonObject.optInt("getTotalLikes");

                        if(mAllLikesArray != null){
                            for (int i = 0; i < mAllLikesArray.length(); i++){
                                JSONObject likeInfoObject = mAllLikesArray.getJSONObject(i);
                                int user_id = likeInfoObject.optInt("user_id");
                                String displayName = likeInfoObject.getString("displayname");
                                String photoUrl = likeInfoObject.optString("image_profile");
                                String friendshipType = likeInfoObject.optString("friendship_type");
                                int isVerified = likeInfoObject.optInt("isVerified");
                                mLikeListItems.add(new CommentList(user_id, displayName, photoUrl,
                                        friendshipType, isVerified));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mLikeAdapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(findViewById(R.id.like_activity), message);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CommentList clickedLikedList = mLikeListItems.get(position);
        int userId = clickedLikedList.getmUserId();

        Intent userProfileIntent = new Intent(this, userProfile.class);
        userProfileIntent.putExtra("user_id", userId);
        startActivityForResult(userProfileIntent, ConstantVariables.USER_PROFILE_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /***
     * View Pager Adapter to set Tabs
     */
    class MyAdapter extends FragmentStatePagerAdapter {


        int reactionCount;
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String reaction;

            JSONObject reactionObject = mReactionTabs.optJSONObject(position);
            reaction = reactionObject.optString("reaction");
            reactionCount = reactionObject.optInt("reaction_count");

            Bundle bundle = new Bundle();
            bundle.putString(ConstantVariables.REACTION_NAME, reaction);
            bundle.putString(ConstantVariables.REACTION_RESPONSE, mAllLikesArray.toString());
            bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, mTotalLikes);
            bundle.putString(ConstantVariables.URL_STRING, mViewLikesBaseUrl);
            Fragment returnFragment = new ReactionsFragment();
            returnFragment.setArguments(bundle);
            return returnFragment;
        }

        @Override
        public int getCount() {
            return mReactionTabs.length();
        }

    }

    private void setupTabIcons(){

        if(mReactionTabs != null && mReactionTabs.length() != 0){

            int imageWidthHeight = (int )mContext.getResources().getDimension(R.dimen.reactions_tab_icon_width_height);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidthHeight, imageWidthHeight);
            for(int i = 0; i < mReactionTabs.length(); i++){
                View linearLayout = LayoutInflater.from(this).inflate(R.layout.reactions_tab_layout, null);
                ImageView imageView = linearLayout.findViewById(R.id.reactionIcon);
                TextView textView = linearLayout.findViewById(R.id.reactionsCount);

                JSONObject jsonObject = mReactionTabs.optJSONObject(i);
                int reactionCount = jsonObject.optInt("reaction_count");
                imageView.setLayoutParams(layoutParams);
                if(i != 0){
                    String imageIcon = jsonObject.optJSONObject("reaction_icon").optString("reaction_image_icon");
                    mImageLoader.setImageUrl(imageIcon, imageView);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(reactionCount));
                } else{
                    imageView.setVisibility(View.GONE);
                    textView.setText(getResources().getString(R.string.rsvp_filter_attending_all) + " " +
                            String.valueOf(reactionCount));
                }

                mTabLayout.getTabAt(i).setCustomView(linearLayout);
            }
        }
    }
}
