package com.hexon.repository;

import androidx.room.TypeConverter;

import java.util.Calendar;

/**
 * Copyright (C), 2020-2025
 * FileName    : Converters
 * Description :
 * Author      : Hexon
 * Date        : 2020/9/4 14:53
 * Version     : V1.0
 */
public class Converters {
    @TypeConverter
    public Calendar fromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }

    @TypeConverter
    public long calendarToTimestamp(Calendar calendar) {
        return calendar.getTimeInMillis();
    }
}
