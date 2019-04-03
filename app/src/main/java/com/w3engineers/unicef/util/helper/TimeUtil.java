package com.w3engineers.unicef.util.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [18-Oct-2018 at 12:20 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [18-Oct-2018 at 12:20 PM].
 * * --> <Second Editor> on [18-Oct-2018 at 12:20 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [18-Oct-2018 at 12:20 PM].
 * * --> <Second Reviewer> on [18-Oct-2018 at 12:20 PM].
 * * ============================================================================
 **/


public class TimeUtil {

    private static String dateFormat1 = "yyyy-MM-dd HH:mm:ss";
    private static String dateFormat9 = "hh:mm aa";
    private static String dateFormat13 = "dd-MM-yyyy";

    public static long DEFAULT_MILLISEC = 1322018752992l; // Nov 22, 2011 9:25:52 PM

    @Nullable
    public static TimeUtil timeUtil;

    private TimeUtil() {
    }

    @NonNull
    public static TimeUtil getInstance(){
        if(timeUtil == null){
            timeUtil = new TimeUtil();
        }
        return timeUtil;
    }

    public static long toCurrentTime() {
        return System.currentTimeMillis();
    }

    @Nullable
    public static String getOnlyTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat9, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    @Nullable
    public static String getDateStirng(long milliSeconds) {
        DateFormat format = new SimpleDateFormat(dateFormat13, Locale.getDefault());

        format.setTimeZone(TimeZone.getDefault());

        return format.format(new Date(milliSeconds));
    }

    public boolean isSameDay(@NonNull Date date1, @NonNull Date date2){

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

        return sameDay;
    }

    @Nullable
    public synchronized Date getDateFromMillisecond(long timeMillis){

        DateFormat df = new SimpleDateFormat(dateFormat1, Locale.getDefault());
        Date formattedDate = null;

        df.setTimeZone(TimeZone.getDefault());

        Date date =new Date(timeMillis);
        String stringDate = df.format(date);

        try {
            formattedDate = df.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
}
