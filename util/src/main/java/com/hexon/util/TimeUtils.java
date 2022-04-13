package com.hexon.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Copyright (C), 2020-2025
 * FileName    : TimeUtils
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/7 11:02
 * Version     : V1.0
 */
public class TimeUtils {
    public static final long DAY_MILLI = 24 * 60 * 60 * 1000;
    public static final long HOUR_MILLI = 60 * 60 * 1000;
    public static final long MINUTE_MILLI = 60 * 1000;

    public static int countDays(Calendar from, Calendar end) {
        return (int) ((end.getTimeInMillis() - from.getTimeInMillis()) / DAY_MILLI) + 1;
    }

    public static int coutinousDays(Calendar from, Calendar end) {
        int day1 = from.get(Calendar.DAY_OF_YEAR);
        int day2 = end.get(Calendar.DAY_OF_YEAR);

        int year1 = from.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {//闰年
                    timeDistance += 366;
                } else {//不是闰年
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1 + 1);
        } else {   //不同年
            return day2 - day1 + 1;
        }
    }

    /**
     * 得到传入日期的周一
     *
     * @return yyyy-MM-dd
     */
    public static Calendar getMondayOfThisWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK);
        if (day_of_week == Calendar.SUNDAY) {
            c.add(Calendar.DATE, -6);
        } else {
            c.add(Calendar.DATE, -day_of_week + Calendar.MONDAY);
        }
        System.out.println(c.getTime());
        return c;
    }

    /**
     * 得到传入日期的周日
     *
     * @return yyyy-MM-dd
     */
    public static Calendar getSundayOfThisWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK);
        if (day_of_week != Calendar.SUNDAY) {
            c.add(Calendar.DATE, Calendar.SATURDAY - day_of_week + 1);
        }
        System.out.println(c.getTime());
        return c;
    }
}
