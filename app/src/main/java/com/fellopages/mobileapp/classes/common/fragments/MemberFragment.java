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

package com.fellopages.mobileapp.classes.common.fragments;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.adapters.MemberAdapter;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment implements SearchView.OnQueryTextListener,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private String memberListUrl, memberListBaseUrl, composeMessageUrl;
    private View rootView, infoView;
    private Context mContext;
    private String currentSelectedModule, title, contentIdString, mSearchText = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mNextIcon, mWaitingInfoTextView;
    private SelectableTextView tvNoMember;
    private RelativeLayout mWaitingMemberInfoContainer;
    private AppConstant mAppConst;
    private JSONObject mBody;
    private JSONArray mMemberResponseArray, menusArray;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private MemberAdapter mMemberAdapter;
    private int contentId, mSubjectId, mTotalMembers, mWaitingCount, mCanEdit, pageNumber = 1,
            mLoadingPageNo = 1;
    private SearchView mSearchView;
    private boolean mIsWaiting, isLoading = false, isVisibleToUser = false,
            isProfilePageRequest = false, isSearchPageRequest = false;
    private AppCompatActivity mActivity;
    private HashMap<String, String> scrollParams = new HashMap<>();
    private OnFragmentDataChangeListener mOnFragmentDataChangeListener;


    public MemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        // Make sure that currently visible

        if (visible && !isVisibleToUser && mContext != null) {
            sendMemberRequest(memberListUrl, false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        infoView = inflater.inflate(R.layout.fragment_no_data, container, false);

        mContext = getContext();
        mActivity = (AppCompatActivity) getActivity();
        mAppConst = new AppConstant(mContext);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        // Getting Arguments data.
        memberListUrl = memberListBaseUrl = getArguments().getString(ConstantVariables.URL_STRING);
        scrollParams.put("page", String.valueOf(pageNumber));
        scrollParams.put("limit", String.valueOf(AppConstant.LIMIT));
        memberListUrl = mAppConst.buildQueryString(memberListUrl, scrollParams);
        isSearchPageRequest = getArguments().getBoolean("isSearchRequest");
        mIsWaiting = getArguments().getBoolean(ConstantVariables.IS_WAITING, false);
        title = getArguments().getString(ConstantVariables.CONTENT_TITLE);
        mSubjectId = getArguments().getInt(ConstantVariables.SUBJECT_ID);
        isProfilePageRequest = getArguments().getBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST);

        /*
        Fetch Current Selected Module
         */
        currentSelectedModule = getArguments().getString(ConstantVariables.EXTRA_MODULE_TYPE);
        boolean isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);
        if (currentSelectedModule != null) {
            switch (currentSelectedModule) {
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    contentIdString = "group_id";
                    contentId = getArguments().getInt(ConstantVariables.CONTENT_ID);
                    break;
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                case ConstantVariables.EVENT_MENU_TITLE:
                    contentIdString = "event_id";
                    contentId = getArguments().getInt(contentIdString);
                    break;
            }
        }


        //Getting Views
        getViews();
        mMemberAdapter = new MemberAdapter(mActivity, R.layout.list_members, mBrowseItemList, mBrowseList,
                contentId, currentSelectedModule, new OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                if (listItems.getmUserId() != 0) {
                    Intent userProfileIntent = new Intent(mActivity, userProfile.class);
                    userProfileIntent.putExtra(ConstantVariables.USER_ID, listItems.getmUserId());
                    ((Activity) mContext).startActivityForResult(userProfileIntent, ConstantVariables.USER_PROFILE_CODE);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }

        }, new OnItemDeleteResponseListener() {

            @Override
            public void onItemDelete(int itemCount, boolean isUserReviewDelete) {

                // When the Member page is not loaded from view page.
                if (mOnFragmentDataChangeListener == null
                        && mActivity.getSupportActionBar() != null) {
                    // Set Action Bar title
                    setActionBarTitle(mActivity, itemCount, currentSelectedModule, title, mIsWaiting);
                }

                if (itemCount != 0 && mOnFragmentDataChangeListener != null) {
                    mOnFragmentDataChangeListener.onFragmentTitleUpdated(MemberFragment.this, itemCount);
                } else if (itemCount == 0) {
                    onRefresh();
                }
            }
        });

        mRecyclerView.setAdapter(mMemberAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager gridLayoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();
                int lastVisibleCount = gridLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) < mTotalMembers) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        scrollParams.clear();
                        scrollParams.put("page", String.valueOf(mLoadingPageNo));
                        scrollParams.put("limit", String.valueOf(AppConstant.LIMIT));
                        String url = mAppConst.buildQueryString(memberListBaseUrl, scrollParams);

                        isLoading = true;
                        loadMoreData(url);
                    }

                }
            }
        });

        if (!isProfilePageRequest) {
            sendMemberRequest(memberListUrl, !mIsWaiting);
        } else {
            mOnFragmentDataChangeListener = FragmentUtils.getOnFragmentDataChangeListener();
        }

        if (isFirstTab){
            sendMemberRequest(memberListUrl, false);
        }
        return rootView;
    }

    /**
     * Method to get views.
     */
    public void getViews() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Getting waiting member/no member views
        mWaitingMemberInfoContainer = (RelativeLayout) infoView.findViewById(R.id.waitingMemberInfoContainer);
        mNextIcon = (TextView) infoView.findViewById(R.id.nextIcon);
        mNextIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mWaitingInfoTextView = (TextView) infoView.findViewById(R.id.waitingMemberInfo);
        mWaitingMemberInfoContainer.setOnClickListener(this);
        tvNoMember = (SelectableTextView) infoView.findViewById(R.id.no_data_msg);

        // Adding info view at top of the recycler view.
        RelativeLayout mainView = (RelativeLayout) rootView.findViewById(R.id.main_view_recycler);
        mainView.addView(infoView);
        CustomViews.addHeaderView(R.id.fragment_main_view, mSwipeRefreshLayout);
    }

    public void initializeSearchView() {

        if (currentSelectedModule.equals("core_main_group") || currentSelectedModule.equals("core_main_sitegroup")) {
            mSearchView.setQueryHint(mActivity.getString(R.string.search_members));
        } else {
            mSearchView.setQueryHint(mActivity.getString(R.string.search_guests));
        }

        mSearchView.setOnQueryTextListener(this);
    }

    public void sendMemberRequest(String url, final boolean search) {

        if (mSearchText != null && !mSearchText.isEmpty()) {
            HashMap<String, String> searchParams = new HashMap<>();
            searchParams.put("search", mSearchText);
            url = mAppConst.buildQueryString(url, searchParams);
        }

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mBrowseItemList.clear();
                mBody = jsonObject;
                isVisibleToUser = true;

                if (mBody != null) {
                    mTotalMembers = mBody.optInt("getTotalItemCount");
                    mWaitingCount = mBody.optInt("getWaitingItemCount");
                    if (mOnFragmentDataChangeListener != null) {
                        mOnFragmentDataChangeListener.onFragmentTitleUpdated(MemberFragment.this, mTotalMembers);
                    }

                    // Set Action Bar title
                    if (!search && mActivity.getSupportActionBar() != null) {
                        setActionBarTitle(mActivity, mTotalMembers, currentSelectedModule, title, mIsWaiting);
                    }

                    // Checking whether it is waiting member list of normal member list.
                    if (!mIsWaiting) {

                        // Showing no member found message.
                        if (mTotalMembers == 0) {
                            tvNoMember.setVisibility(View.VISIBLE);
                            switch (currentSelectedModule) {
                                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                                    tvNoMember.setText(getResources().getString(R.string.no_guest_found));
                                    break;
                                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                                    tvNoMember.setText(getResources().getString(R.string.no_subscriber_found));
                                    break;
                                default:
                                    tvNoMember.setText(getResources().getString(R.string.no_member_found));
                                    break;
                            }
                        } else {
                            tvNoMember.setVisibility(View.GONE);
                        }

                        // Showing waiting member info if it not a search page request.
                        if (mWaitingCount != 0 && !isSearchPageRequest) {
                            mWaitingMemberInfoContainer.setVisibility(View.VISIBLE);
                            String membersText = mContext.getResources().getQuantityString(R.plurals.
                                    member_text, mWaitingCount);
                            mWaitingInfoTextView.setText(String.format(
                                    mContext.getResources().getString(R.string.members_waiting_text_format),
                                    mWaitingCount, membersText, mContext.getResources().getString(R.string.members_waiting_text)
                            ));
                            mNextIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            mNextIcon.setText("\uf054");
                        } else {
                            mWaitingMemberInfoContainer.setVisibility(View.GONE);
                        }

                    } else {
                        if (mWaitingCount == 0) {
                            if (mSearchView != null) {
                                mSearchView.setVisibility(View.GONE);
                            }
                            tvNoMember.setVisibility(View.VISIBLE);
                            if (currentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
                                tvNoMember.setText(
                                        getResources().getString(R.string.no_members_waiting_message_adv_event));
                            } else {
                                tvNoMember.setText(
                                        getResources().getString(R.string.no_members_waiting_message));
                            }
                        } else {
                            tvNoMember.setVisibility(View.GONE);
                            if (mSearchView != null) {
                                mSearchView.setVisibility(View.VISIBLE);
                            }
                        }
                        mWaitingInfoTextView.setVisibility(View.GONE);
                    }

                    mCanEdit = mBody.optInt("canEdit");
                    mBrowseList.setCanEdit(mCanEdit);

                    // Show Message Icon
                    if (mBody.has("messageGuest") && mBody.optJSONObject("messageGuest") != null) {
                        composeMessageUrl = mBody.optJSONObject("messageGuest").optString("url");

                        if (mOnFragmentDataChangeListener != null) {
                            mOnFragmentDataChangeListener.showMessageGuestIcon(MemberFragment.this,
                                    true, composeMessageUrl);
                        }
                    }

                    // Add Data to List
                    addDataToList(mBody);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                SnackbarUtils.displaySnackbar(rootView, message);

            }
        });

    }

    public void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mMemberAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mMemberAdapter.notifyItemRemoved(mBrowseItemList.size());

                // Add data to List
                addDataToList(jsonObject);
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    public void addDataToList(JSONObject jsonObject) {

        mBody = jsonObject;
        mMemberResponseArray = mBody.optJSONArray("members");
        if (mMemberResponseArray != null && mMemberResponseArray.length() != 0) {
            for (int i = 0; i < mMemberResponseArray.length(); i++) {
                String staff = "";
                JSONObject jsonDataObject = mMemberResponseArray.optJSONObject(i);
                String displayName = jsonDataObject.optString("displayname");
                String photoUrl = jsonDataObject.optString("image_profile");
                String friendshipType = jsonDataObject.optString("friendship_type");
                menusArray = jsonDataObject.optJSONArray("menu");
                int userId = jsonDataObject.optInt("user_id", 0);
                int isVerified = jsonDataObject.optInt("isVerified");
                if (userId == 0)
                    displayName = mContext.getResources().getString(R.string.deleted_member_text);

                if (menusArray != null) {
                    try {
                        JSONObject friendshipObject = new JSONObject();
                        friendshipObject.put("name", "friendship_type");
                        menusArray.put(friendshipObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                switch (currentSelectedModule) {
                    case ConstantVariables.GROUP_MENU_TITLE:
                    case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                        staff = jsonDataObject.optString("staff");
                        //TODO recheck it after work is done from api, staff work is also needed.
                        int isGroupAdmin = jsonDataObject.optInt("isGroupAdmin");
                        mBrowseItemList.add(new BrowseListItems(mSubjectId, photoUrl, displayName, staff, menusArray,
                                userId, friendshipType, isGroupAdmin, isVerified));
                        break;

                    case ConstantVariables.EVENT_MENU_TITLE:
                    case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                        int isOwner = jsonDataObject.optInt("is_owner");
                        int rsvp = jsonDataObject.optInt("rsvp");
                        mBrowseItemList.add(new BrowseListItems(photoUrl, displayName, isOwner, rsvp,
                                menusArray, userId, friendshipType, isVerified));
                        break;
                }
            }
        }
        mMemberAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                sendMemberRequest(memberListUrl, false);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        if (s != null) {
            mSearchText = s;
            String url = memberListUrl;
            mSearchView.clearFocus();
            mSwipeRefreshLayout.setRefreshing(true);
            sendMemberRequest(url, true);
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.waitingMemberInfoContainer) {

            String waitingMembersUrl = memberListBaseUrl + "?waiting=1";
            Intent membersIntent = new Intent(mContext, FragmentLoadActivity.class);
            membersIntent.putExtra(ConstantVariables.URL_STRING, waitingMembersUrl);
            membersIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, currentSelectedModule);
            membersIntent.putExtra("waitingCount", mWaitingCount);
            membersIntent.putExtra(ConstantVariables.FRAGMENT_NAME, "waiting_member");
            membersIntent.putExtra(ConstantVariables.CONTENT_TITLE, title);
            membersIntent.putExtra(ConstantVariables.IS_WAITING, true);
            membersIntent.putExtra(contentIdString, contentId);
            startActivityForResult(membersIntent, ConstantVariables.WAITING_MEMBERS_CODE);
            ((AppCompatActivity) mContext).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantVariables.WAITING_MEMBERS_CODE) {
            sendMemberRequest(memberListUrl, false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Get the SearchView and set the searchable configuration;
        if (!isProfilePageRequest) {
            SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(mActivity.getComponentName()));

            initializeSearchView();

            MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {

                    // Loading member list when returned back from the search query.
                    if (mSearchText != null && !mSearchText.isEmpty()) {
                        tvNoMember.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(true);
                        mSearchText = null;
                        sendMemberRequest(memberListUrl, false);
                    }
                    return true;
                }
            });
        }
    }


    public static void setActionBarTitle(AppCompatActivity activity, int totalMembers,
                                         String currentSelectedModule, String title, boolean isWaiting) {

        String toolBarTitle = null;

        if (!isWaiting) {
            switch (currentSelectedModule) {
                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    if (totalMembers != 0) {
                        toolBarTitle = activity.getResources().getString(R.string.action_bar_title_members)
                                + " (" + totalMembers + ")" + ": " + title;
                    } else {
                        toolBarTitle = activity.getResources().getString(R.string.action_bar_title_members)
                                + ": " + title;
                    }
                    break;

                case ConstantVariables.EVENT_MENU_TITLE:
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    if (totalMembers != 0) {
                        toolBarTitle = activity.getResources().getString(R.string.action_bar_title_guests)
                                + " (" + totalMembers + ")" + ": " + title;
                    } else {
                        toolBarTitle = activity.getResources().getString(R.string.action_bar_title_guests)
                                + ": " + title;
                    }
                    break;

            }
        } else {
            switch (currentSelectedModule) {

                case ConstantVariables.GROUP_MENU_TITLE:
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    if (totalMembers != 0) {
                        toolBarTitle = activity.getResources().getString(R.string.waiting_members_text)
                                + " (" + totalMembers + ")" + ": " + title;
                    } else {
                        toolBarTitle = activity.getResources().getString(R.string.waiting_members_text)
                                + ": " + title;
                    }
                    break;
                case ConstantVariables.EVENT_MENU_TITLE:
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    if (totalMembers != 0) {
                        toolBarTitle = activity.getResources().getString(R.string.waiting_guests_text)
                                + " (" + totalMembers + ")" + ": " + title;
                    } else {
                        toolBarTitle = activity.getResources().getString(R.string.waiting_guests_text)
                                + ": " + title;
                    }
                    break;
            }

        }

        if (toolBarTitle != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(toolBarTitle);
        }
    }
}
