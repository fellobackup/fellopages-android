package com.bigsteptech.realtimechat;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.google.firebase.storage.StorageReference;
//import com.myprojects.marco.firechat.storage.FirebaseImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by marco on 13/07/16.
 */

public class Utils {

    public static String getCurrentTimestamp() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(new Date());
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(new Date());
    }

    public static String getTimestamp(long timestamp, Context context) {

        SimpleDateFormat sdf;

        if(DateFormat.is24HourFormat(context)){
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("hh:mm a");
        }

        String time = sdf.format(timestamp);

//        long millis = TimeZone.getDefault().getOffset(timestamp);
//        long hour = (millis / (1000 * 60 * 60)) % 24;
//        long minutes = (millis / (1000 * 60)) % 60;
//
//        String[] timestampPart = time.split("/");
//        long h = Long.parseLong(timestampPart[0]);
//        long m = Long.parseLong(timestampPart[1]);
//        h += hour;
//        h %= 24;
//        m += minutes;
//        m %= 60;
//
//        String output = h + ":" + m;
//        if (h < 10) {
//            if (m < 10) {
//                output = "0" + h + ":0" + m;
//            } else {
//                output = "0" + h + ":" + m;
//            }
//        } else if (m < 10) {
//            output = h + ":0" + m;
//        }
        return time;
    }

//    public static String getDate(String timestamp) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
//            Date date = sdf.parse(timestamp);
//
//            Log.d(Utils.class.getSimpleName(), "date " + date.toString());
//            long currentDate = date.getTime();
//
//            Log.d(Utils.class.getSimpleName(), "currentDate 1" + currentDate);
//            currentDate += TimeZone.getDefault().getOffset(currentDate);
//
//            Log.d(Utils.class.getSimpleName(), "currentDate 2" + currentDate);
//
//            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
//            return sdfDate.format(currentDate);
//        } catch (ParseException e) {
//
//        }
//        return null;
//    }

    public static String getDateFromTimeStamp(long timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        return sdf.format(timestamp);
    }

    public static String getDateOfMessage(Context context, long timestamp){

//        timestamp += TimeZone.getDefault().getOffset(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        if(DateUtils.isToday(timestamp)){
            return context.getResources().getString(R.string.today);
        } else if (DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS)){
            return context.getResources().getString(R.string.yesterday);
        } else {
            return sdf.format(timestamp);
        }
    }



    public static String getRelativeTimeString(long timestamp){

        CharSequence output = null;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dt = formatter.parse(formatter.format(timestamp));
            output = DateUtils.getRelativeTimeSpanString (dt.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(output != null){
             return output.toString();
        }

        return null;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000))%60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if(hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

}
