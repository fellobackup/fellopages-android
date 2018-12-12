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
 *
 */

package com.fellopages.mobileapp.classes.common.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.SimpleSearchAdapter;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;
import com.fellopages.mobileapp.classes.modules.advancedGroups.AdvGroupUtil;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoUtil;
import com.fellopages.mobileapp.classes.modules.directoryPages.SitePageUtil;
import com.fellopages.mobileapp.classes.modules.multipleListingType.MLTUtil;
import com.fellopages.mobileapp.classes.modules.advancedEvents.AdvEventsUtil;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;
import com.fellopages.mobileapp.classes.modules.user.BrowseMemberFragment;
import com.fellopages.mobileapp.classes.modules.globalSearch.GlobalSearchFragment;
import com.fellopages.mobileapp.classes.modules.classified.ClassifiedUtil;
import com.fellopages.mobileapp.classes.modules.event.EventUtil;
import com.fellopages.mobileapp.classes.modules.group.GroupUtil;
import com.fellopages.mobileapp.classes.modules.music.MusicUtil;
import com.fellopages.mobileapp.classes.modules.blog.BlogUtil;
import com.fellopages.mobileapp.classes.modules.poll.PollUtil;
import com.fellopages.mobileapp.classes.modules.video.VideoUtil;
import com.fellopages.mobileapp.classes.modules.album.AlbumUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SearchActivity extends FormActivity implements View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private Toolbar mToolbar;
    private String currentSelectedOption, currentsSelectedList = null, mPreviousSelectedModule;
    private String mQuery = "",searchUrl = null,categoryValue,categoryId, categoryForumTopic, customSearchUrl,
            mTagName, mTagId, mViewTag;
    private int mPreviousSelectedModuleListingTypeId, mWhatWhereWithinmile, mListingTypeId;
    private AppConstant mAppConst;
    private DrawerLayout mDrawerLayout;
    private LinearLayout drawerView;
    private Map<String, String> postParams;
    private EditText filterTextView, adminWishlist;
    private Bundle searchParamsBundle;
    public boolean isFilterApplied = false, isDashBoardSearch, isHashTagSearch,
            isSearchingHashTag = false, isLoadHashTagSuggestion = false, isAdvSearch = false;
    private SearchView mSearchView = null;
    public String QUERY_STRING = "query";
    public static String[] columns = new String[]{"_id", "hashtag_label", "hashtag_url"};
    private SimpleSearchAdapter mSearchSuggestionAdapter;
    private TextView mSearchOtherModules;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Setting toolbar as action bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //setting up the back icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mPreviousSelectedModule = PreferencesUtils.getCurrentSelectedModule(this);
        if (mPreviousSelectedModule != null && mPreviousSelectedModule.equals("sitereview_listing")) {
            mPreviousSelectedModuleListingTypeId = PreferencesUtils.getCurrentSelectedListingId(this);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(SearchActivity.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(SearchActivity.this);
                }

            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_search);
        drawerView = (LinearLayout) findViewById(R.id.drawer);
        mSearchOtherModules = (TextView) findViewById(R.id.search_other_modules);
        mSearchOtherModules.setOnClickListener(this);
        Button buttonCloseDrawer = (Button) findViewById(R.id.apply_btn);
        buttonCloseDrawer.setOnClickListener(this);
        mAppConst = new AppConstant(this);

        //Fetch Current Selected Module
        currentSelectedOption = getIntent().getStringExtra(ConstantVariables.EXTRA_MODULE_TYPE);
        if (currentSelectedOption == null || currentSelectedOption.isEmpty()) {
            currentSelectedOption = PreferencesUtils.getCurrentSelectedModule(this);
        }
        //getting the query hint
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;

        mTagName = getIntent().getStringExtra("tag");
        mTagId = getIntent().getStringExtra("tag_id");
        categoryValue = getIntent().getStringExtra(ConstantVariables.CATEGORY_VALUE);
        categoryId = getIntent().getStringExtra(ConstantVariables.CATEGORY_ID);
        switch (currentSelectedOption) {
            case ConstantVariables.FORUM_MENU_TITLE:
                categoryForumTopic = getIntent().getStringExtra(ConstantVariables.CATEGORY_FORUM_TOPIC);
                break;
        }

        isDashBoardSearch = getIntent().getBooleanExtra(ConstantVariables.IS_SEARCHED_FROM_DASHBOARD, false);
        isHashTagSearch = getIntent().getBooleanExtra(ConstantVariables.HASTAG_SEARCH, false);

        getSearchUrl(query);

        if (!isHashTagSearch) {
            //Setting up the filter option in drawer view by sending asynchronous request
            setDrawerViewForFilter();
        } else {

            loadSearchFragment();
        }
        //setting up the query hint
        if (mSearchView != null) {
            mSearchView.setQueryHint(mQuery);
        }
        overridePendingTransition(0, 0);

    }

    private void getSearchUrl(String query) {

        // update the Query hint according to the module

        if (currentSelectedOption != null && (!isDashBoardSearch || isAdvSearch)) {

            switch (currentSelectedOption) {

                case ConstantVariables.HOME_MENU_TITLE:
                    if (isHashTagSearch) {
                        mQuery = query;
                        QUERY_STRING = "hashtag";
                    } else {
                        mQuery = getResources().getString(R.string.search) + "…";
                        QUERY_STRING = "query";
                    }
                    customSearchUrl = "search";
                    break;

                case ConstantVariables.GLOBAL_SEARCH_MENU_TITLE:
                    if (query != null && !query.isEmpty())
                        mQuery = query;
                    else
                        mQuery = getResources().getString(R.string.search) + "…";
                    customSearchUrl = "search";
                    break;

                case ConstantVariables.CLASSIFIED_MENU_TITLE:
                case ConstantVariables.CLASSIFIED_TITLE:
                    mQuery = getResources().getString(R.string.query_search_classified);
                    customSearchUrl = "classifieds/search-form";
                    break;

                case ConstantVariables.BLOG_MENU_TITLE:
                case ConstantVariables.BLOG_TITLE:
                    mQuery = getResources().getString(R.string.query_search_blog);
                    customSearchUrl = "blogs/search-form";
                    break;

                case ConstantVariables.ALBUM_MENU_TITLE:
                case ConstantVariables.ALBUM_TITLE:
                    mQuery = getResources().getString(R.string.query_search_album);
                    customSearchUrl = "albums/search-form";
                    break;

                case ConstantVariables.VIDEO_MENU_TITLE:
                case ConstantVariables.VIDEO_TITLE:
                    mQuery = getResources().getString(R.string.query_search_video);
                    customSearchUrl = "videos/search-form";
                    break;

                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_VIDEO_TITLE:
                    mQuery = getResources().getString(R.string.query_search_video);
                    customSearchUrl = "advancedvideos/search-form";
                    break;

                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    mQuery = getResources().getString(R.string.query_search_channel);
                    customSearchUrl = "advancedvideos/channel/search-form";
                    break;

                case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                    mQuery = getResources().getString(R.string.query_search_playlist);
                    customSearchUrl = "advancedvideos/playlist/search-form";
                    break;

                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.GROUP_TITLE:
                    mQuery = getResources().getString(R.string.query_search_group);
                    customSearchUrl = "groups/search-form";
                    break;

                case ConstantVariables.EVENT_MENU_TITLE:
                case ConstantVariables.EVENT_TITLE:
                    mQuery = getResources().getString(R.string.query_search_event);
                    customSearchUrl = "events/search-form";
                    break;

                case ConstantVariables.MUSIC_MENU_TITLE:
                case ConstantVariables.MUSIC_TITLE:
                case ConstantVariables.MUSIC_PLAYLIST_TITLE:
                case ConstantVariables.MUSIC_PLAYLIST_SONG_TITLE:
                    mQuery = getResources().getString(R.string.query_search_music);
                    customSearchUrl = "music/search-form";
                    break;

                case ConstantVariables.FORUM_MENU_TITLE:
                case ConstantVariables.FORUM_TITLE:
                    if (query != null && !query.isEmpty())
                        mQuery = query;
                    else
                        mQuery = getResources().getString(R.string.query_search_forum);
                    customSearchUrl = "search";
                    break;

                case ConstantVariables.USER_MENU_TITLE:
                case ConstantVariables.USER_TITLE:
                    mQuery = getResources().getString(R.string.search_members);
                    customSearchUrl = "members/index/search-form";
                    break;

                case ConstantVariables.MESSAGE_MENU_TITLE:
                    mQuery = getResources().getString(R.string.search_message);
                    break;

                case ConstantVariables.POLL_MENU_TITLE:
                case ConstantVariables.POLL_TITLE:
                    mQuery = getResources().getString(R.string.search_poll);
                    customSearchUrl = "polls/search-form";
                    break;

                case ConstantVariables.MLT_MENU_TITLE:
                case ConstantVariables.MLT_TITLE:
                    mQuery = getResources().getString(R.string.search) + " " +
                            PreferencesUtils.getCurrentSelectedListingSingularLabel(this,
                                    PreferencesUtils.getCurrentSelectedListingId(this));
                    customSearchUrl = "listings/search-form?listingtype_id=" + PreferencesUtils.getCurrentSelectedListingId(this);
                    break;

                case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                    mQuery = getResources().getString(R.string.search_wishlist);
                    customSearchUrl = "listings/wishlist/search-form";
                    break;

                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                case ConstantVariables.ADVANCED_EVENT_TITLE:
                    mQuery = getResources().getString(R.string.query_search_event);
                    customSearchUrl = "advancedevents/search-form";
                    break;

                case ConstantVariables.DIARY_MENU_TITLE:
                    mQuery = getResources().getString(R.string.query_search_diaries);
                    customSearchUrl = "advancedevents/diaries/search-form";
                    break;

                case ConstantVariables.SITE_PAGE_MENU_TITLE:
                case ConstantVariables.SITE_PAGE_TITLE:
                    mQuery = getResources().getString(R.string.query_search_pages);
                    customSearchUrl = "sitepages/search-form";
                    break;

                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_TITLE:
                    mQuery = getResources().getString(R.string.query_search_group);
                    customSearchUrl = "advancedgroups/search-form";
                    break;

                case ConstantVariables.STORE_MENU_TITLE:
                case ConstantVariables.STORE_TITLE:
                    mQuery = getResources().getString(R.string.query_search_store);
                    customSearchUrl = "sitestore/search-form";
                    break;

                case ConstantVariables.PRODUCT_MENU_TITLE:
                case ConstantVariables.PRODUCT_TITLE:
                    mQuery = getResources().getString(R.string.query_search_products);
                    customSearchUrl = "sitestore/product/product-search-form";
                    break;

                case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
                    mQuery = getResources().getString(R.string.search_wishlist);
                    customSearchUrl = "sitestore/product/wishlist/search-form";
                    break;

                case ConstantVariables.STORE_ORDER_MENU_TITLE:
                    mQuery = getResources().getString(R.string.query_search_orders);
                    customSearchUrl = "sitestore/orders/search-form";
                    break;

                default:
                    mQuery = query;
                    customSearchUrl = "search";
                    break;
            }
        } else if (isDashBoardSearch) {
            if (query != null && !query.isEmpty()) mQuery = query;
            else mQuery = getResources().getString(R.string.search) + "…";
            customSearchUrl = "search";
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(SearchManager.QUERY)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            isHashTagSearch = getIntent().getBooleanExtra(ConstantVariables.HASTAG_SEARCH, false);
            if (!TextUtils.isEmpty(query) && filterTextView != null) {
                mSearchView.setQuery(query, false);
                filterTextView.setText(query);
                loadSearchFragment();
            } else if (!TextUtils.isEmpty(query) && isHashTagSearch) {
                mSearchView.setQuery(query, false);
                loadSearchFragment();
            }
        }
    }

    public void setDrawerViewForFilter() {

        //create search page url  page url settings

        if (customSearchUrl == null || customSearchUrl.isEmpty())
            customSearchUrl = "search";

        searchUrl = AppConstant.DEFAULT_URL + customSearchUrl;

        if (drawerView.findViewById(R.id.form_layout) != null) {
            drawerView.removeView(drawerView.findViewById(R.id.form_layout));
        }

        /* Show Other Module Search Link when Adv Search is performed */
        if (isAdvSearch) {
            mSearchOtherModules.setVisibility(View.VISIBLE);
        } else {
            mSearchOtherModules.setVisibility(View.GONE);
        }
         /*
        Code to Send Request for Create Form
         */

        mAppConst.getJsonResponseFromUrl(searchUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                if (jsonObject != null) {
                    if (isDashBoardSearch) {
                        drawerView.addView(generateForm(jsonObject, true, "core_main_global_search"));
                    } else if (currentsSelectedList != null) {

                        switch (currentsSelectedList) {
                            case "browse_diaries_siteevent":
                                drawerView.addView(generateForm(jsonObject, true, "search_diary"));
                                break;
                            default:
                                drawerView.addView(generateForm(jsonObject, true, currentSelectedOption));
                                break;
                        }
                    } else {
                        drawerView.addView(generateForm(jsonObject, true, currentSelectedOption));
                    }
                    setFilterFormParams(jsonObject);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAppConst.hideKeyboard();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_search, menu);
        if (isHashTagSearch || (currentSelectedOption != null
                && currentSelectedOption.equals("core_main_sitestoreproduct"))) {
            menu.findItem(R.id.menu_filter).setVisible(false);
        }
        Drawable drawable = menu.findItem(R.id.menu_filter).getIcon();
        drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        int width = (int) mContext.getResources().getDimension(R.dimen.search_view_width);

        if (searchItem != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view != null) {
                view.setLayoutParams(lp);
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(this);
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
                view.setOnSuggestionListener(this);
                mSearchSuggestionAdapter = new SimpleSearchAdapter(this, R.layout.simple_text_view, null, columns, null, -1000);
                view.setSuggestionsAdapter(mSearchSuggestionAdapter);
            }

            if (view != null) {
                if (!isHashTagSearch) {
                    view.setQueryHint(mQuery);
                } else {
                    view.setQuery(mQuery, false);
                    mSearchView.clearFocus();
                }

            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void loadSearchFragment() {

        try {
            Fragment fragment = null;
            postParams = new HashMap<>();
            searchParamsBundle = new Bundle();

            switch (currentSelectedOption) {
                case ConstantVariables.EVENT_MENU_TITLE:
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.EVENT_TITLE:
                case ConstantVariables.GROUP_TITLE:
                    mViewTag = "search_text";
                    break;

                case ConstantVariables.HOME_MENU_TITLE:
                case ConstantVariables.FORUM_MENU_TITLE:
                case ConstantVariables.FORUM_TITLE:
                    mViewTag = "query";
                    break;

                case ConstantVariables.USER_MENU_TITLE:
                    if (mWhatWhereWithinmile != 0) {
                        mViewTag = "search";
                    } else {
                        mViewTag = "displayname";
                    }
                    break;

                case ConstantVariables.STORE_ORDER_MENU_TITLE:
                    mViewTag = "order_id";
                    break;

                default:
                    mViewTag = "search";
                    break;
            }

            View inflateView = drawerView.findViewWithTag(mViewTag);
            if (inflateView != null) {
                filterTextView = (EditText) inflateView.findViewById(R.id.field_value);
            }

            if (mTagName != null && mTagId != null) {
                searchParamsBundle.putString("tag", mTagName);
                searchParamsBundle.putString("tag_id", mTagId);
            }

            if (mSearchView != null && filterTextView != null) {

                mSearchView.setQuery(filterTextView.getText().toString(), false);
                if (isSearchingHashTag) {
                    searchParamsBundle.putString(QUERY_STRING, mSearchView.getQuery().toString().trim());
                }
            } else if (mSearchView != null && !mSearchView.getQuery().toString().isEmpty()) {
                searchParamsBundle.putString(QUERY_STRING, mSearchView.getQuery().toString().trim());
            } else {
                searchParamsBundle.putString(QUERY_STRING, mQuery.trim());
            }

            if (categoryId != null && !isFilterApplied) {

                if (isDashBoardSearch) {
                    searchParamsBundle.putString("query", "");
                } else {
                    String queryParam = getQueryStringParam();
                    searchParamsBundle.putString(queryParam, "");
                }

                switch (currentSelectedOption) {

                    case ConstantVariables.MLT_MENU_TITLE:
                        searchParamsBundle.putString("category_id", categoryId);
                        break;
                    default:
                        searchParamsBundle.putString("category", categoryId);
                        break;
                }
            } else if (postParams != null && !isHashTagSearch || !isSearchingHashTag) {
                postParams = save();
                if (postParams != null) {
                    Set<String> keySet = postParams.keySet();
                    for (String key : keySet) {
                        String value = postParams.get(key);
                        searchParamsBundle.putString(key, value);
                    }

                    // If the fragment is loaded for wish list.
                    if (currentSelectedOption.equals(ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE)
                            || currentSelectedOption.equals(ConstantVariables.MLT_WISHLIST_MENU_TITLE)) {
                        String text = "";
                        if (adminWishlist != null && !adminWishlist.getText().toString().isEmpty()) {
                            text = adminWishlist.getText().toString();
                        }
                        searchParamsBundle.putString("text", text);
                    }

                    // if Global search is being performed and Adv search of the modules is selected
                    // Then Load that Module's Fragment and send Request to load Filter form of
                    // the selected module.

                    if ((currentSelectedOption.equals(ConstantVariables.HOME_MENU_TITLE) || isDashBoardSearch) &&
                            postParams.containsKey("type") && !postParams.get("type").equals("0")
                            && !postParams.get("type").equals("forum")) {

                        isAdvSearch = true;
                        // Check if the selected type is Review Module's Listing
                        if (postParams.get("type").split("_").length > 1) {
                            String[] parts = postParams.get("type").split("_");
                            currentSelectedOption = parts[0];
                            mListingTypeId = Integer.parseInt(parts[1]);
                            PreferencesUtils.setCurrentSelectedListingId(this, mListingTypeId);
                        } else {
                            currentSelectedOption = postParams.get("type");
                        }
                        // Change the query param according to the selected type
                        if (searchParamsBundle.containsKey("query")) {
                            searchParamsBundle.putString(getQueryStringParam(), searchParamsBundle.getString("query"));
                            searchParamsBundle.remove("query");
                        }
                        getSearchUrl(null);
                        setDrawerViewForFilter();
                        //setting up the query hint
                        if (mSearchView != null) {
                            mSearchView.setQueryHint(mQuery);
                        }

                    }
                }
            }

            if (currentSelectedOption != null && (!isDashBoardSearch || isAdvSearch)) {
                switch (currentSelectedOption) {

                    case ConstantVariables.HOME_MENU_TITLE:
                    case ConstantVariables.GLOBAL_SEARCH_MENU_TITLE:
                    case ConstantVariables.FORUM_MENU_TITLE:
                    case ConstantVariables.FORUM_TITLE:
                        if (isHashTagSearch || isSearchingHashTag) {
                            fragment = new FeedsFragment();
                        } else {
                            fragment = new GlobalSearchFragment();
                        }
                        break;

                    case ConstantVariables.CLASSIFIED_MENU_TITLE:
                    case ConstantVariables.CLASSIFIED_TITLE:
                        fragment = ClassifiedUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.BLOG_MENU_TITLE:
                    case ConstantVariables.BLOG_TITLE:
                        fragment = BlogUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.ALBUM_MENU_TITLE:
                    case ConstantVariables.ALBUM_TITLE:
                        fragment = AlbumUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.VIDEO_MENU_TITLE:
                    case ConstantVariables.VIDEO_TITLE:
                        fragment = VideoUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                    case ConstantVariables.ADV_VIDEO_TITLE:
                        fragment = AdvVideoUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                        fragment = AdvVideoUtil.getChannelBrowsePageInstance();
                        break;

                    case ConstantVariables.ADV_VIDEO_PLAYLIST_MENU_TITLE:
                        fragment = AdvVideoUtil.getPlaylistBrowsePageInstance();
                        break;

                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.GROUP_TITLE:
                        fragment = GroupUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                    case ConstantVariables.EVENT_TITLE:
                        fragment = EventUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.MUSIC_MENU_TITLE:
                    case ConstantVariables.MUSIC_TITLE:
                    case ConstantVariables.MUSIC_PLAYLIST_TITLE:
                    case ConstantVariables.MUSIC_PLAYLIST_SONG_TITLE:
                        fragment = MusicUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.USER_MENU_TITLE:
                    case ConstantVariables.USER_TITLE:
                        fragment = new BrowseMemberFragment();
                        break;

                    case ConstantVariables.POLL_MENU_TITLE:
                    case ConstantVariables.POLL_TITLE:
                        fragment = PollUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.MLT_MENU_TITLE:
                    case ConstantVariables.MLT_TITLE:
                        fragment = MLTUtil.getBrowsePageInstance();
                        if (mListingTypeId != 0) {
                            searchParamsBundle.putInt(ConstantVariables.LISTING_TYPE_ID, mListingTypeId);
                        }
                        break;

                    case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                        fragment = MLTUtil.getBrowseWishListPageInstance();
                        break;

                    case ConstantVariables.PRODUCT_WISHLIST_MENU_TITLE:
                        fragment = StoreUtil.getBrowseWishListPageInstance();
                        break;

                    case ConstantVariables.STORE_ORDER_MENU_TITLE:
                        fragment = StoreUtil.getBrowseOrderPageInstance();
                        break;

                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    case ConstantVariables.ADVANCED_EVENT_TITLE:
                        fragment = AdvEventsUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.DIARY_MENU_TITLE:
                        fragment = AdvEventsUtil.getBrowseDiariesInstance();
                        break;

                    case ConstantVariables.SITE_PAGE_MENU_TITLE:
                    case ConstantVariables.SITE_PAGE_TITLE:
                        fragment = SitePageUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_TITLE:
                        fragment = AdvGroupUtil.getBrowsePageInstance();
                        break;

                    case ConstantVariables.STORE_MENU_TITLE:
                    case ConstantVariables.STORE_TITLE:
                        fragment = StoreUtil.getBrowseStoreInstance();
                        break;

                    case ConstantVariables.PRODUCT_MENU_TITLE:
                    case ConstantVariables.PRODUCT_TITLE:
                        fragment = StoreUtil.getBrowseProductPageInstance();
                        break;

                    default:
                        break;
                }
            } else if (isSearchingHashTag) {
                fragment = new FeedsFragment();
            } else {
                fragment = new GlobalSearchFragment();
            }
            if (fragment != null && !isFinishing()) {

                fragment.setArguments(searchParamsBundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_filter) {
            mDrawerLayout.openDrawer(drawerView);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreviousSelectedModule != null) {
            PreferencesUtils.updateCurrentModule(this, mPreviousSelectedModule);
            if (mPreviousSelectedModule.equals("sitereview_listing") && mPreviousSelectedModuleListingTypeId != 0) {
                PreferencesUtils.setCurrentSelectedListingId(this, mPreviousSelectedModuleListingTypeId);
            } else if (mListingTypeId != 0) {
                PreferencesUtils.setCurrentSelectedListingId(this, mListingTypeId);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.apply_btn:
                mSearchView.clearFocus();
                mSearchView.setSuggestionsAdapter(null);
                mDrawerLayout.closeDrawers();

                isFilterApplied = categoryId != null;

                loadSearchFragment();
                break;

            case R.id.search_other_modules:
                isAdvSearch = false;
                currentSelectedOption = mPreviousSelectedModule;
                getSearchUrl(null);
                setDrawerViewForFilter();
                //setting up the query hint
                if (mSearchView != null) {
                    mSearchView.setQueryHint(mQuery);
                }
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String searchText) {

        if (currentSelectedOption != null && currentSelectedOption.equals("home")) {
            mQuery = searchText;
            if (isHashTagSearch) {
                QUERY_STRING = "hashtag";
            } else if (searchText.trim().startsWith("#")) {
                isSearchingHashTag = true;
                QUERY_STRING = "hashtag";
            } else {
                isSearchingHashTag = false;
                QUERY_STRING = "query";
            }
        }

        mSearchView.clearFocus();
        if (filterTextView != null) {
            filterTextView.setText(searchText);
        }

        loadSearchFragment();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {
        if (filterTextView != null) {
            filterTextView.setText(searchText);
        }
        if (currentSelectedOption != null && currentSelectedOption.equals("home")
                && searchText.trim().startsWith("#")) {
            isSearchingHashTag = true;
            QUERY_STRING = "hashtag";
            loadHashTagSuggestion(searchText.trim());
        } else {
            mSearchView.setSuggestionsAdapter(null);
            isSearchingHashTag = false;
            QUERY_STRING = "query";
        }
        isLoadHashTagSuggestion = true;

        return true;
    }

    private void loadHashTagSuggestion(String hashtag) {
        try {

            if (isLoadHashTagSuggestion) {
                mAppConst.getJsonResponseFromUrl(UrlUtil.BROWSE_HASHTAG_URL
                        + "hashtag=" + URLEncoder.encode(hashtag, "utf-8"), new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                        JSONArray response = jsonObject.getJSONArray("response");
                        mSearchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
                        mSearchSuggestionAdapter.changeCursor(getSuggestionCursor(response));

                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    }
                });
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private MatrixCursor getSuggestionCursor(JSONArray response) {
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < response.length(); i++) {
            String temp[] = new String[3];
            temp[0] = Integer.toString(response.optJSONObject(i).optInt("id"));
            temp[1] = response.optJSONObject(i).optString("label");
            temp[2] = response.optJSONObject(i).optString("url");
            cursor.addRow(temp);
        }
        return cursor;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
        String feedName = cursor.getString(1);
        mSearchView.setQuery(feedName, false);
        mSearchView.clearFocus();

        loadSearchFragment();
        mSearchView.setSuggestionsAdapter(null);
        return true;
    }

    private String getQueryStringParam() {

        String queryString;
        switch (currentSelectedOption) {
            case ConstantVariables.EVENT_MENU_TITLE:
            case ConstantVariables.GROUP_MENU_TITLE:
            case ConstantVariables.EVENT_TITLE:
            case ConstantVariables.GROUP_TITLE:
                queryString = "search_text";
                break;

            case ConstantVariables.GLOBAL_SEARCH_MENU_TITLE:
            case ConstantVariables.HOME_MENU_TITLE:
                queryString = "query";
                break;

            default:
                queryString = "search";
                break;
        }
        return queryString;
    }

    private void setFilterFormParams(JSONObject jsonObject) {

        if (!isDashBoardSearch || isAdvSearch) {
            switch (currentSelectedOption) {
                case ConstantVariables.EVENT_MENU_TITLE:
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.EVENT_TITLE:
                case ConstantVariables.GROUP_TITLE:
                    mViewTag = "search_text";
                    break;

                case ConstantVariables.HOME_MENU_TITLE:
                case ConstantVariables.FORUM_MENU_TITLE:
                case ConstantVariables.FORUM_TITLE:
                    mViewTag = "query";
                    break;

                case ConstantVariables.USER_MENU_TITLE:
                    if (jsonObject != null && jsonObject.optInt("whatWhereWithinmile") != 0) {
                        mWhatWhereWithinmile = jsonObject.optInt("whatWhereWithinmile");
                        mViewTag = "search";
                    } else {
                        mViewTag = "displayname";
                    }
                    break;

                default:
                    mViewTag = "search";
                    break;
            }
        } else {
            mViewTag = "query";
        }

        View inflateView = drawerView.findViewWithTag(mViewTag);
        if (inflateView != null) {
            filterTextView = (EditText) inflateView.findViewById(R.id.field_value);

            // Populate filter text field if Adv search is performed with any query
            if (mSearchView != null && mSearchView.getQuery() != null && !mSearchView.getQuery().toString().isEmpty() &&
                    filterTextView != null) {
                filterTextView.setText(mSearchView.getQuery());
            }

            // If wishlist is searched for a member then showing the member name in search query.
            View adminWishlistView = drawerView.findViewWithTag("text");
            if (adminWishlistView != null) {
                adminWishlist = (EditText) adminWishlistView.findViewById(R.id.field_value);
                if (adminWishlist != null && categoryValue != null) {
                    adminWishlist.setText(categoryValue);
                    loadSearchFragment();
                }
            }

            // If category is search from activity then showing the category value in form and loaded the fragment.
            if (categoryId != null && categoryValue != null) {
                View categoryView = drawerView.findViewWithTag("category");
                if (categoryView == null) {
                    categoryView = drawerView.findViewWithTag("category_id");
                }
                if (categoryView != null) {
                    EditText etCategory = (EditText) categoryView.findViewById(R.id.field_value);
                    etCategory.setText(categoryValue);
                    etCategory.setTag(categoryId);
                }
                loadSearchFragment();
            }
            if (mTagName != null && !mTagName.isEmpty() && mTagId != null && !mTagId.isEmpty()) {
                loadSearchFragment();
            }

            // If the form is loaded for forum then showing the forum option is pre selected.
            if (categoryForumTopic != null) {
                try {
                    View categoryView = drawerView.findViewWithTag("type");
                    if (categoryView != null && jsonObject != null) {
                        JSONArray formArray = jsonObject.optJSONArray("form");
                        if (formArray == null) {
                            formArray = jsonObject.optJSONArray("response");
                        }
                        JSONObject selectJsonObject = formArray.optJSONObject(1);
                        JSONObject jsonObjectMultiOptions = selectJsonObject.optJSONObject("multiOptions");
                        EditText etCategory = (EditText) categoryView.findViewById(R.id.field_value);
                        etCategory.setText(jsonObjectMultiOptions.optString("forum"));
                        etCategory.setTag("forum");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
