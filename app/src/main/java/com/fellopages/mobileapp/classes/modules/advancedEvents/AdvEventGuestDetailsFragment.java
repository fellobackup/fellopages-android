/*
 *   Copyright (c) 2015 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */


package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.adapters.AdvModulesManageDataAdapter;
import com.fellopages.mobileapp.classes.common.adapters.SpinnerAdapter;
import com.fellopages.mobileapp.classes.common.fragments.FragmentUtils;
import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AdvEventGuestDetailsFragment  extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {


    // Member variables.
    private Context mContext;
    private AppCompatActivity mActivity;
    private AppConstant mAppConst;
    private BrowseListItems mBrowseList;
    private List<Object> mBrowseItemList;
    private Map<String, String> postParams;
    private String mListUrl, mCurrentSelectedModule, mTitle, firstStartDate, lastStartDate, minTime,
            mSearchText = null, hourString, minuteString, monthString, dateString, selectedDate,
            mCurrentList, title, mContentIdString;
    private int mLoadingPageNo = 1, mEventId, mWaitingItemCount, strDateId, endDateId, mSelectedItem = -1;
    private boolean isLoading = false, isVisibleToUser = false,
            isProfilePageRequest = false, isSearchPageRequest = false, isSearchGuest = false;
    private OnFragmentDataChangeListener mOnFragmentDataChangeListener;
    private AdvModulesManageDataAdapter mManageDataAdapter;

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private Spinner spinner;
    private SpinnerAdapter adapter;
    private TextView mWaitingMemberText, mNextIcon;
    private RelativeLayout mWaitingMemberBlock;
    private ListView mListView;
    private Typeface fontIcon;
    private EditText strDate, endDate;
    private SearchView mSearchView;
    private CardView filterLayout;
    private static int sEventId;


    public AdvEventGuestDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getContext();
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser && mContext != null) {
            sendRequestToServer(mListUrl, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();
        mContext = getActivity();
        mActivity = (AppCompatActivity) getActivity();
        mAppConst = new AppConstant(mContext);
        postParams = new HashMap<>();

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_guest_list, container, false);

        // Getting views.
        getViews();

        // Getting current module.
        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        if (mCurrentSelectedModule != null && !mCurrentSelectedModule.equals(ConstantVariables.ADVANCED_EVENT_MENU_TITLE)) {
            PreferencesUtils.updateCurrentModule(mContext, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        // Getting intent values.
        mContentIdString = "event_id";
        mCurrentList = getArguments().getString("list_name");
        mListUrl = getArguments().getString(ConstantVariables.URL_STRING);
        mEventId = getArguments().getInt(ConstantVariables.CONTENT_ID);
        isProfilePageRequest = getArguments().getBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST);
        isSearchPageRequest = getArguments().getBoolean("isSearchRequest");
        boolean isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

        if (mCurrentList != null && mCurrentList.equals("members_siteevent")) {
            title = getArguments().getString(ConstantVariables.CONTENT_TITLE);
            mTitle = getArguments().getString(ConstantVariables.CONTENT_TITLE);
            if (mEventId != 0) {
                AdvEventGuestDetailsFragment.sEventId = mEventId;
            }
        } else {
            mCurrentList = "occurrence_siteevent";
        }
        PreferencesUtils.updateCurrentList(mContext, mCurrentList);

        // Setting-up the adapter for guest list.
        mManageDataAdapter = new AdvModulesManageDataAdapter(mContext, R.layout.list_row, mBrowseItemList,
                mCurrentList, new OnItemDeleteResponseListener() {
            @Override
            public void onItemDelete(int itemCount, boolean isUserReviewDelete) {
                if (itemCount != 0 && mOnFragmentDataChangeListener != null) {
                    mOnFragmentDataChangeListener.onFragmentTitleUpdated(AdvEventGuestDetailsFragment.this, itemCount);

                } else if (itemCount == 0) {
                    onRefresh();
                }
            }
        });
        mListView.setAdapter(mManageDataAdapter);

        // Setting-up the adapter for the rsvp filter.
        adapter = new SpinnerAdapter(mContext, R.layout.simple_text_view, mSelectedItem);
        adapter.add(mContext.getResources().getString(R.string.rsvp_filter_attending_all));
        adapter.add(mContext.getResources().getString(R.string.rsvp_filter_attending));
        adapter.add(mContext.getResources().getString(R.string.rsvp_filter_may_be_attending));
        adapter.add(mContext.getResources().getString(R.string.rsvp_filter_not_attending));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Checking if the fragment is loaded from view page request or not.
        if (!isProfilePageRequest || isFirstTab) {
            sendRequestToServer(mListUrl, false);
        } else {
            mOnFragmentDataChangeListener = FragmentUtils.getOnFragmentDataChangeListener();
        }

        mWaitingMemberText.setOnClickListener(this);
        mListView.setOnItemClickListener(this);

        return rootView;
    }

    public void getViews() {

        mListView = (ListView) rootView.findViewById(R.id.listview);
        spinner = (Spinner) rootView.findViewById(R.id.filter_view);
        filterLayout = (CardView) rootView.findViewById(R.id.categoryFilterLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_listview_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        // Waiting member block views.
        mWaitingMemberBlock = (RelativeLayout) rootView.findViewById(R.id.waitingMemberBlock);
        mWaitingMemberText = (TextView) rootView.findViewById(R.id.waitingMemberText);
        mNextIcon  = (TextView) rootView.findViewById(R.id.nextIcon);
        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
    }

    /**
     * Method to send request to the server with the attached url.
     * @param url Url of calling service.
     * @param isSearch true if it is search query request.
     */
    public void sendRequestToServer(String url, final Boolean isSearch) {

        // Putting the search params if the url is called from search query filter.
        if (mSearchText != null && !mSearchText.isEmpty()) {
            HashMap<String, String> searchParams = new HashMap<>();
            searchParams.put("search", mSearchText);
            url = mAppConst.buildQueryString(url, searchParams);
            isSearchGuest = true;
            mBrowseItemList.clear();
            filterLayout.setVisibility(View.GONE);
        }

        mLoadingPageNo = 1;

        final String finalUrl = url;
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                isVisibleToUser = true;
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mAppConst.hideProgressDialog();

                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject, isSearch);
                mManageDataAdapter.notifyDataSetChanged();

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    sendRequestToServer(finalUrl, false);
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }
            }
        });

    }

    public void loadMoreData(String url) {

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                addDataToList(jsonObject, false);
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    public void addDataToList(JSONObject jsonObject, Boolean search) {

        if (jsonObject != null) {
            try {
                if (mCurrentList.equals("occurrence_siteevent")) {

                    JSONArray dataResponse = jsonObject.optJSONArray("response");
                    if (jsonObject.has("messageGuest")) {
                        String composeMessageUrl = jsonObject.optJSONObject("messageGuest").optString("url");

                        if (mOnFragmentDataChangeListener != null) {
                            mOnFragmentDataChangeListener.showMessageGuestIcon(AdvEventGuestDetailsFragment.this,
                                    true, composeMessageUrl);
                        }
                    }
                    if (dataResponse != null && dataResponse.length() != 0) {
                        rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                        for (int j = 0; j < dataResponse.length(); j++) {
                            JSONObject mDataObject = dataResponse.optJSONObject(j);
                            String startTime = mDataObject.optString("starttime");
                            if (j == 0) {
                                minTime = startTime;
                            }
                            String endTime = mDataObject.optString("endtime");
                            int occurrence_id = mDataObject.optInt("occurrence_id");
                            int totalMembers = mDataObject.optInt("totalMembers");
                            int rsvp = mDataObject.optInt("rsvp");
                            JSONArray menuArray = mDataObject.optJSONArray("menu");
                            JSONArray guestArray = mDataObject.optJSONArray("guest");
                            int event_id = mDataObject.optInt("event_id");
                            int isRequestInvite = mDataObject.optInt("isRequestInvite");
                            int isJoined = mDataObject.optInt("isJoined");
                            mBrowseItemList.add(new BrowseListItems(startTime, endTime, occurrence_id,
                                    totalMembers, rsvp, menuArray, guestArray, event_id, isRequestInvite, isJoined));
                        }

                    } else {
                        rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                        TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                        TextView errorMessage = (TextView) rootView.findViewById(R.id.error_message);
                        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        errorIcon.setText("\uf073");
                        errorMessage.setText(mContext.getResources().getString(R.string.no_occurrences_found));
                    }

                } else {
                    if (!isSearchGuest) {
                        filterLayout.setVisibility(View.VISIBLE);
                    }
                    mWaitingItemCount = jsonObject.optInt("getWaitingItemCount");
                    if (mWaitingItemCount != 0 && !isSearchPageRequest) {
                        mWaitingMemberBlock.setVisibility(View.VISIBLE);
                        mWaitingMemberText.setText(mWaitingItemCount + " " + mContext.getResources().getString(R.string.waiting_member_text));
                        mNextIcon.setTypeface(fontIcon);
                        mNextIcon.setText("\uf054");
                    } else {
                        mWaitingMemberBlock.setVisibility(View.GONE);
                    }

                    int totalItemCount = jsonObject.getInt("getTotalItemCount");
                    mBrowseList.setmTotalItemCount(totalItemCount);
                    if (mOnFragmentDataChangeListener != null) {
                        mOnFragmentDataChangeListener.onFragmentTitleUpdated(AdvEventGuestDetailsFragment.this, totalItemCount);
                    }
                    if (totalItemCount != 0){
                        rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                        JSONArray dataResponse = jsonObject.optJSONArray("members");

                        if (jsonObject.has("messageGuest")) {

                            String composeMessageUrl = jsonObject.optJSONObject("messageGuest").optString("url");

                            if (mOnFragmentDataChangeListener != null) {
                                mOnFragmentDataChangeListener.showMessageGuestIcon(AdvEventGuestDetailsFragment.this,
                                        true, composeMessageUrl);
                            }
                        }
                        if (dataResponse != null) {
                            for (int i = 0; i < dataResponse.length(); i++) {
                                JSONObject jsonDataObject = dataResponse.optJSONObject(i);
                                int user_id = jsonDataObject.optInt("user_id");
                                String name = jsonDataObject.optString("displayname");
                                String friendshipType = jsonDataObject.optString("friendship_type");

                                if (user_id == 0) {
                                    name = getResources().getString(R.string.deleted_member_text);
                                }
                                int rsvp = jsonDataObject.optInt("rsvp");
                                String guestImage = jsonDataObject.optString("image");
                                JSONArray menuArray = jsonDataObject.optJSONArray("menu");
                                int isVerified = jsonDataObject.optInt("isVerified");
                                if (menuArray != null){
                                    try {
                                        JSONObject friendshipObject = new JSONObject();
                                        friendshipObject.put("name", "friendship_type");
                                        menuArray.put(friendshipObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mBrowseItemList.add(new BrowseListItems(user_id, name, rsvp, guestImage,
                                            menuArray, true, friendshipType, isVerified));
                                }
                                else {
                                    mBrowseItemList.add(new BrowseListItems(user_id, name, rsvp, guestImage,
                                            false, friendshipType, isVerified));
                                }

                            }
                        }
                    } else {
                        rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                        TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                        TextView errorMessage = (TextView) rootView.findViewById(R.id.error_message);
                        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        errorIcon.setText("\uf007");
                        errorMessage.setText(mContext.getResources().getString(R.string.no_guest_found));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                mListView.setVisibility(View.INVISIBLE);
            }

        }
    }

    public void showDateTimeDialogue(final Context context, final EditText dateField, final String type) {

        AlertDialog.Builder builder;

        try {
            builder = new AlertDialog.Builder(context, R.style.AppTheme);
        } catch (NoSuchMethodError e) {
            builder = new AlertDialog.Builder(context);
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.datetimepicker_dialogue, null);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);

        DateFormat sourceFormat;
        if (AppConstant.mLocale != null) {
            sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", AppConstant.mLocale);
        } else {
            sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        Date dob_var = null;
        try {
            dob_var = sourceFormat.parse(minTime);

        }catch (ParseException e) {
            e.printStackTrace();
        }

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.setMinDate(dob_var.getTime());

        builder.setView(view);
        builder.setTitle(context.getResources().getString(R.string.date_time_dialogue_title));

        builder.setPositiveButton(context.getResources().getString(R.string.date_time_dialogue_ok_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();

                        int month = datePicker.getMonth() + 1;
                        int date = datePicker.getDayOfMonth();

                        if (hour < 10) {
                            hourString = "0" + hour;
                        } else if (hour > 12) {
                            hour -= 12;
                            hourString = "" + hour;

                        } else
                            hourString = "" + hour;

                        if (minute < 10) {
                            minuteString = "0" + minute;
                        } else {
                            minuteString = "" + minute;
                        }

                        if (month < 10)
                            monthString = "0" + month;
                        else
                            monthString = "" + month;

                        if (date < 10)
                            dateString = "0" + date;
                        else
                            dateString = "" + date;

                        selectedDate = datePicker.getYear() + "-" +
                                monthString + "-" + dateString + " " + hourString + ":" + minuteString;

                        if (type.equals("startDate")) {
                            firstStartDate = selectedDate;
                            dateField.setText(mContext.getResources().getString(R.string.star_date_text) + ": " + selectedDate);
                        } else if (type.equals("endDate")) {
                            lastStartDate = selectedDate;
                            dateField.setText(mContext.getResources().getString(R.string.end_date_text) + ": " +  selectedDate);
                        }


                    }
                });

        builder.setNegativeButton(context.getResources().getString(R.string.date_time_dialogue_cancel_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (!isProfilePageRequest) {

            // Get the SearchView and set the searchable configuration;
            SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            if (mSearchView != null) {
                mSearchView.setSearchableInfo(searchManager.getSearchableInfo(mActivity.getComponentName()));
                mSearchView.setQueryHint(mActivity.getString(R.string.search_guests));
                mSearchView.setOnQueryTextListener(this);
            }

            MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Loading guest list when returned back from the search query.
                    if (mSearchText != null && !mSearchText.isEmpty()) {
                        rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(true);
                        mSearchText = null;
                        isSearchGuest = false;
                        sendRequestToServer(mListUrl, false);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        AdvEventGuestDetailsFragment.sEventId = mEventId;

        if (id == R.id.action_message){

            String redirectUrl = mAppConst.DEFAULT_URL + "advancedevents/member/compose/" + mEventId;
            Intent message = new Intent(mContext, CreateNewEntry.class);
            message.putExtra(ConstantVariables.CREATE_URL, redirectUrl);
            message.putExtra(ConstantVariables.FORM_TYPE, "compose_message");
            startActivityForResult(message, ConstantVariables.CREATE_REQUEST_CODE);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            return true;

        } else if (id == R.id.search_occurrence) {

            if (mCurrentList.equals("occurrence_siteevent")) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                strDate = new EditText(mContext);
                endDate = new EditText(mContext);

                strDate.setFocusable(false);
                endDate.setFocusable(false);

                strDateId = R.id.starttime;
                endDateId = R.id.endtime;

                strDate.setId(strDateId);
                endDate.setId(endDateId);

                strDate.setTag("strDate");
                endDate.setTag("endDate");

                strDate.setHint(mContext.getResources().getString(R.string.from_hint));
                endDate.setHint(mContext.getResources().getString(R.string.to_hint));

                strDate.setOnClickListener(this);
                endDate.setOnClickListener(this);

                linearLayout.addView(strDate);
                linearLayout.addView(endDate);

                alertBuilder.setView(linearLayout);

                alertBuilder.setMessage(mContext.getResources().getString(R.string.select_start_end_date_dialoge_message));
                alertBuilder.setTitle(title);

                alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.date_time_dialogue_ok_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        postParams.put("firstStartDate", firstStartDate);
                        postParams.put("lastStartDate", lastStartDate);

                        String url = mListUrl = mAppConst.buildQueryString(mListUrl, postParams);
                        mAppConst.showProgressDialog();
                        sendRequestToServer(url, false);
                    }
                });

                alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.date_time_dialogue_cancel_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertBuilder.create().show();

            }
                return true;
            }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
        if (mCurrentList.equals("members_siteevent") && listItems.getmUserId()!=0) {
            Intent intent = new Intent(mContext, userProfile.class);
            intent.putExtra(ConstantVariables.USER_ID, listItems.getmUserId());
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int limit = firstVisibleItem + visibleItemCount;
        if(limit == totalItemCount && !isLoading) {
            if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo) <
                    mBrowseList.getmTotalItemCount()) {
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = mListUrl + "&page=" + mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        sendRequestToServer(mListUrl, false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String url = null;
        HashMap<String, String> rsvpParams = new HashMap<>();
        mSelectedItem = position;
        adapter.getCustomView(position, view, parent, mSelectedItem);
        switch(position){
            case 0 :
                url = mListUrl;
                break;
            case 1:
                rsvpParams.put("rsvp", "2");
                break;
            case 2:
                rsvpParams.put("rsvp", "1");
                break;
            default:
                rsvpParams.put("rsvp", "0");
                break;
        }

        if(rsvpParams.size() != 0){
            url = mAppConst.buildQueryString(mListUrl,rsvpParams);
        }
        sendRequestToServer(url, false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.waitingMemberText) {
            postParams.put("waiting", "1");
            String url = mAppConst.buildQueryString(mListUrl, postParams);
            Intent intent = new Intent(mContext, FragmentLoadActivity.class);
            intent.putExtra(ConstantVariables.URL_STRING, url);
            intent.putExtra(ConstantVariables.FRAGMENT_NAME, "waiting_member");
            intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
            intent.putExtra("waitingCount", mWaitingItemCount);
            intent.putExtra(ConstantVariables.CONTENT_TITLE, mTitle);
            intent.putExtra(ConstantVariables.IS_WAITING, true);
            intent.putExtra(mContentIdString, mEventId);
            startActivityForResult(intent, ConstantVariables.WAITING_MEMBERS_CODE);
            mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }

        String tag = (String) v.getTag();
        if (tag != null) {

            if (tag.equals("strDate")) {
                showDateTimeDialogue(mContext, strDate, "startDate");
            } else if (tag.equals("endDate")) {
                showDateTimeDialogue(mContext, endDate, "endDate");
            }
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query != null){
            mSearchText = query;
            String url = mListUrl;
            mSearchView.clearFocus();
            swipeRefreshLayout.setRefreshing(true);
            sendRequestToServer(url, true);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantVariables.WAITING_MEMBERS_CODE) {
            sendRequestToServer(mListUrl, false);
        }
    }

}
