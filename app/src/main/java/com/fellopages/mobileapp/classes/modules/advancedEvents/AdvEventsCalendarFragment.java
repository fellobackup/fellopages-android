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

package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AdvEventsCalendarFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    private String mBrowseCalendarUrl, mCurrentSelectedModule;
    private View rootView;
    private AppConstant mAppConst;
    private JSONObject mBody;
    private List<Object> mBrowseItemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context mContext;
    private Snackbar snackbar;
    private JSONArray mDataResponse;

    private List<String> daysList;

    private static final String tag = "SimpleCalender";

    private TextView currentMonth, mSun, mMon, mTue, mWed, mThu, mFri, mSat;
    private ImageView prevYear, nextYear;
    private TextView prevMonth, nextMonth ;
    private GridView calendarView;
    private GridCellAdapter adapter;
    private Calendar _calendar;
    private int month, year;
    private HashMap<String, String> postParams;
    Boolean isVisibleToUser = false;

    public static AdvEventsCalendarFragment newInstance(Bundle bundle) {
        // Required public constructor
        AdvEventsCalendarFragment fragment = new AdvEventsCalendarFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBrowseItemList = new ArrayList<>();
        daysList = new ArrayList<>();

        mAppConst = new AppConstant(mContext);

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.calendar_view_layout, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);

        if(mCurrentSelectedModule != null && !mCurrentSelectedModule.equals("core_main_siteevent")){
            PreferencesUtils.updateCurrentModule(mContext,"core_main_siteevent");
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        mBrowseCalendarUrl = UrlUtil.BROWSE_CALENDAR_ADV_EVENTS_URL;

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);

        prevYear = rootView.findViewById(R.id.prevYear);
        nextYear = rootView.findViewById(R.id.nextYear);

        prevMonth = rootView.findViewById(R.id.prevMonth);
        nextMonth = rootView.findViewById(R.id.nextMonth);

        mSun = rootView.findViewById(R.id.day_sun);
        mMon = rootView.findViewById(R.id.day_mon);
        mTue = rootView.findViewById(R.id.day_tue);
        mWed = rootView.findViewById(R.id.day_wed);
        mThu = rootView.findViewById(R.id.day_thu);
        mFri = rootView.findViewById(R.id.day_fri);
        mSat = rootView.findViewById(R.id.day_sat);

        prevMonth.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        nextMonth.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

        prevYear.setOnClickListener(this);
        nextYear.setOnClickListener(this);
        prevMonth.setOnClickListener(this);
        nextMonth.setOnClickListener(this);

        currentMonth = rootView.findViewById(R.id.currentMonth);

        calendarView = rootView.findViewById(R.id.calendar);

        // Initialised
        adapter = new GridCellAdapter(mContext, R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        prevMonth.setText("\uf100");
        nextMonth.setText("\uf101");
        currentMonth.setText(adapter.getMonthAsString(month-1) + " " + year);
        mSun.setText(adapter.getWeekDayAsString(0));
        mMon.setText(adapter.getWeekDayAsString(1));
        mTue.setText(adapter.getWeekDayAsString(2));
        mWed.setText(adapter.getWeekDayAsString(3));
        mThu.setText(adapter.getWeekDayAsString(4));
        mFri.setText(adapter.getWeekDayAsString(5));
        mSat.setText(adapter.getWeekDayAsString(6));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        int columnWidth = width/7;

        calendarView.setColumnWidth(columnWidth);

        return rootView;
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            sendRequestToServer(mBrowseCalendarUrl);
        } else if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public void sendRequestToServer(final String url) {

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                calendarView.setVisibility(View.VISIBLE);

                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addDataToList(jsonObject);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                calendarView.setVisibility(View.VISIBLE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    sendRequestToServer(url);
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }

            }
        });

    }

    public void addDataToList(JSONObject jsonObject) {

        mBody = jsonObject;
        String startTime = null;
        daysList.clear();
        if (mBody != null) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            mDataResponse = mBody.optJSONArray("response");

            if (mDataResponse != null ) {
                for (int i = 0; i < mDataResponse.length(); i++) {

                    JSONObject jsonObj = mDataResponse.optJSONObject(i);

                    //    Getting data from individual JSONObject
                    startTime = jsonObj.optString("starttime");
                    int occurrence_id = jsonObj.optInt("occurrence_id");
                    String day = AppConstant.getDayFromDate(startTime);

                    daysList.add(day);

                }
            }

            String monthString = adapter.getMonthAsString(month-1);
            currentMonth.setText(monthString + " " + year);
            setGridCellAdapterToDate(month, year);

        } else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            TextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf073");
            errorMessage.setText(mContext.getResources().getString(R.string.no_events));
        }

    }


    @Override
    public void onRefresh() {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                sendRequestToServer(mBrowseCalendarUrl);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    /**
     * Load more data on scrolling
     * @param url Url to send request on server
     */
    public void loadMoreData(String url){

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                addDataToList(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(rootView, message);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* Update currentSelected Option on back press*/
        switch (requestCode){
            case ConstantVariables.VIEW_PAGE_CODE:
                PreferencesUtils.updateCurrentModule(mContext, mCurrentSelectedModule);
                break;
        }

    }

    /**
     *
     * @param month
     * @param year
     */
    private void setGridCellAdapterToDate(int month, int year)
    {
        adapter = new GridCellAdapter(mContext, R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month-1, _calendar.get(Calendar.DAY_OF_MONTH));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        postParams = new HashMap<>();

        if (v == prevMonth) {
            if (month <= 1)
            {
                month = 12;
                year--;
            }
            else
            {
                month--;
            }
        }
        else if (v == nextMonth) {
            if (month > 11)
            {
                month = 1;
                year++;
            }
            else
            {
                month++;
            }
        } else if (v == nextYear) {
            year++;
        } else if (v == prevYear) {
            year--;
        }

        rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        calendarView.setVisibility(View.GONE);

        String date = "1-" + month + "-" + year;
        postParams.put("date_current", date);
        String url = mAppConst.buildQueryString(mBrowseCalendarUrl, postParams);

        sendRequestToServer(url);

    }

    public class GridCellAdapter extends BaseAdapter implements View.OnClickListener {
        private static final String tag = "GridCellAdapter";
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = new String[7];
        private final String[] months = new String[12];
        private final String[] monthsNum = new String[12];
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private final int month, year;
        private int daysInMonth, prevMonthDays;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private TextView gridcell;
        private TextView num_events_per_day;
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId, int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            this.month = month;
            this.year = year;

            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

            // Get month name from string file

            months[0] = _context.getResources().getString(R.string.month_jan);
            months[1] = _context.getResources().getString(R.string.month_feb);
            months[2] = _context.getResources().getString(R.string.month_mar);
            months[3] = _context.getResources().getString(R.string.month_apr);
            months[4] = _context.getResources().getString(R.string.month_may);
            months[5] = _context.getResources().getString(R.string.month_jun);
            months[6] = _context.getResources().getString(R.string.month_jul);
            months[7] = _context.getResources().getString(R.string.month_aug);
            months[8] = _context.getResources().getString(R.string.month_sep);
            months[9] = _context.getResources().getString(R.string.month_oct);
            months[10] = _context.getResources().getString(R.string.month_nov);
            months[11] = _context.getResources().getString(R.string.month_dec);

            // Get month number from string file

            monthsNum[0] = _context.getResources().getString(R.string.month_1);
            monthsNum[1] = _context.getResources().getString(R.string.month_2);
            monthsNum[2] = _context.getResources().getString(R.string.month_3);
            monthsNum[3] = _context.getResources().getString(R.string.month_4);
            monthsNum[4] = _context.getResources().getString(R.string.month_5);
            monthsNum[5] = _context.getResources().getString(R.string.month_6);
            monthsNum[6] = _context.getResources().getString(R.string.month_7);
            monthsNum[7] = _context.getResources().getString(R.string.month_8);
            monthsNum[8] = _context.getResources().getString(R.string.month_9);
            monthsNum[9] = _context.getResources().getString(R.string.month_10);
            monthsNum[10] = _context.getResources().getString(R.string.month_11);
            monthsNum[11] = _context.getResources().getString(R.string.month_12);

            // Get Week day from string file

            weekdays[0] = _context.getResources().getString(R.string.day_sun);
            weekdays[1] = _context.getResources().getString(R.string.day_mon);
            weekdays[2] = _context.getResources().getString(R.string.day_tue);
            weekdays[3] = _context.getResources().getString(R.string.day_wed);
            weekdays[4] = _context.getResources().getString(R.string.day_thu);
            weekdays[5] = _context.getResources().getString(R.string.day_fri);
            weekdays[6] = _context.getResources().getString(R.string.day_sat);

            // Print Month
            printMonth(month, year);

        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getMonthAsNum(int i) {
            return monthsNum[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        private void printMonth(int mm, int yy) {

            // The number of days to leave blank at
            // the start of this month.
            int trailingSpaces = 0;
            int leadSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            }


            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
                ++daysInMonth;
            }

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsNum(prevMonth) + "-" + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                if (i == getCurrentDayOfMonth()) {
                    list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsNum(currentMonth) + "-" + yy);
                } else {
                    list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsNum(currentMonth) + "-" + yy);
                }
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsNum(nextMonth) + "-" + nextYear);
            }
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.day_grid_cell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = row.findViewById(R.id.calendar_day_gridcell);

            String[] day_color = list.get(position).split("-");
            String theday = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];

            if ((!day_color[1].equals("GREY")) && (!daysList.isEmpty()) && (daysList != null)) {

                int day = Integer.parseInt(theday);
                String theDays = null;

                if (day < 10) {
                    theDays = "0" + theday;
                } else {
                    theDays = theday;
                }

                 if (daysList.contains(theDays)) {
                     row.findViewById(R.id.num_events).setVisibility(View.VISIBLE);
                     gridcell.setOnClickListener(this);
                 } else {
                     row.findViewById(R.id.num_events).setVisibility(View.GONE);
                 }
            }

            // Set the Day GridCell
            gridcell.setText(theday);
            gridcell.setTag(theyear + "-" + themonth + "-" + theday);

            if (day_color[1].equals("GREY")) {
                gridcell.setTextColor(Color.LTGRAY);
                row.findViewById(R.id.num_events).setVisibility(View.GONE);
            }
            else if (day_color[1].equals("WHITE")) {
                gridcell.setTextColor(Color.BLACK);
            }
            else if (day_color[1].equals("BLUE")) {
                gridcell.setTextColor(Color.BLUE);
            }
            return row;
        }

        @Override
        public void onClick(View view) {

            String date_month_year = (String) view.getTag();
            String[] day_color = date_month_year.split("-");

            String theyear = day_color[0];
            String themonth = day_color[1];
            String theday = day_color[2];

            themonth = getMonthAsString(Integer.parseInt(themonth)-1);
            String titleDate = themonth + " " + theday + ", " + theyear;

            Bundle searchParamsBundle = new Bundle();
            searchParamsBundle.putString("date_current", date_month_year);
            searchParamsBundle.putString("viewtype", "list");
            searchParamsBundle.putString(ConstantVariables.FRAGMENT_NAME, "search_by_date");
            searchParamsBundle.putString(ConstantVariables.CONTENT_TITLE, titleDate);
            Intent newIntent = new Intent(mContext, FragmentLoadActivity.class);
            newIntent.putExtras(searchParamsBundle);
            mContext.startActivity(newIntent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }
}

