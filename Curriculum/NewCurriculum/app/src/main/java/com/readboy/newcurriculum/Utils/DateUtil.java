package com.readboy.newcurriculum.Utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date getDateInWeekOfFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) dayOfWeek = 7;
        calendar.add(Calendar.DATE,-dayOfWeek + 1);
        return calendar.getTime();
    }

    public static int getDayAfterDay(Date date,int day) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        targetCalendar.add(Calendar.DATE,day);
        Calendar resultCalendar = Calendar.getInstance();
        resultCalendar.setTimeInMillis(targetCalendar.getTime().getTime());
        return resultCalendar.get(Calendar.DATE);
    }

    public static int getDayInWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    public static Date getDateAfterDay(Date date,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,day);
        return calendar.getTime();
    }
}
