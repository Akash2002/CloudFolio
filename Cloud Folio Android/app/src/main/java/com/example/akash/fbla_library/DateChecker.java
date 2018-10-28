package com.example.akash.fbla_library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by akash on 2/7/2018.
 */

public class DateChecker {
    public static String addDaysToDate(Date date, int numDays){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, numDays);
        return simpleDateFormat.format(c.getTime());
    }
    public static String calculateDays(Date d1, Date d2){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        System.out.println(diff/1000/60/60/24 + "Diff");
        System.out.println("Time1 " + c1.getTime() + " Time2" + c2.getTime());
        return String.valueOf(Math.round(diff/ (1000 * 60 * 60 * 24)));
    }
}
