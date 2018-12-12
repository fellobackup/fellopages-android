package com.uniprogy.outquiz.models;

import android.text.format.DateUtils;

import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Show {

    public int id, amount, live;
    public Date schedule;

    public Show(int id, String scheduleISO8601, int amount, int live)
    {
        this.id = id;
        this.amount = amount;
        this.live = live;

        String iso8601format = "yyyy-MM-dd'T'HH:mm:ssZZZ";
        DateFormat dateFormat = new SimpleDateFormat(iso8601format);
        try {
            schedule = dateFormat.parse(scheduleISO8601);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String scheduleFormatted()
    {
        if(schedule == null)
        {
            return null;
        }

        // today
        if(DateUtils.isToday(schedule.getTime()))
        {
            DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(schedule);
        }

        // yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule);
        calendar.add(Calendar.DATE, -1);
        if(DateUtils.isToday(calendar.getTime().getTime()))
        {
            DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            dateFormat.setTimeZone(TimeZone.getDefault());
            return String.format("%1$s, %2$s",
                    App.getContext().getString(R.string.tr_tomorrow),
                    dateFormat.format(schedule));
        }

        // date in future
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return dateFormat.format(schedule);
    }

    public String amountFormatted()
    {
        return String.format("%1$s%2$d", App.getContext().getResources().getString(R.string.app_currencySymbol), amount);
    }

}
