package com.hexon.repository.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hexon.repository.Constants;
import com.hexon.repository.Converters;
import com.hexon.repository.database.room.dao.DayHistoryDao;
import com.hexon.repository.database.room.dao.FiveMinutesHistoryDao;
import com.hexon.repository.database.room.dao.FourHoursHistoryDao;
import com.hexon.repository.database.room.dao.MonthHistoryDao;
import com.hexon.repository.database.room.dao.OneHourHistoryDao;
import com.hexon.repository.database.room.dao.OneMinuteHistoryDao;
import com.hexon.repository.database.room.dao.RealtimeDao;
import com.hexon.repository.database.room.dao.ThirtyMinutesHistoryDao;
import com.hexon.repository.database.room.dao.TwoHoursHistoryDao;
import com.hexon.repository.database.room.dao.WeekHistoryDao;
import com.hexon.repository.database.room.entity.DayHistory;
import com.hexon.repository.database.room.entity.FiveMinutesHistory;
import com.hexon.repository.database.room.entity.FourHoursHistory;
import com.hexon.repository.database.room.entity.MonthHistory;
import com.hexon.repository.database.room.entity.OneHourHistory;
import com.hexon.repository.database.room.entity.OneMinuteHistory;
import com.hexon.repository.database.room.entity.Realtime;
import com.hexon.repository.database.room.entity.ThirtyMinutesHistory;
import com.hexon.repository.database.room.entity.TwoHoursHistory;
import com.hexon.repository.database.room.entity.WeekHistory;

/**
 * Copyright (C), 2022-2040
 * ClassName: SinaDatabase
 * Description:
 * Author: Hexon
 * Date: 2022/7/21 9:42
 * Version V1.0
 */
@Database(entities = {OneMinuteHistory.class, FiveMinutesHistory.class, ThirtyMinutesHistory.class,
        OneHourHistory.class, TwoHoursHistory.class, FourHoursHistory.class, DayHistory.class,
        WeekHistory.class, MonthHistory.class},
        version = 1)
@TypeConverters(Converters.class)
public abstract class SinaDatabase extends RoomDatabase {
    private static volatile SinaDatabase sInstance;

    public abstract OneMinuteHistoryDao oneMinuteHistoryDao();

    public abstract FiveMinutesHistoryDao fiveMinutesHistoryDao();

    public abstract ThirtyMinutesHistoryDao thirtyMinutesHistoryDao();

    public abstract OneHourHistoryDao oneHourHistoryDao();

    public abstract TwoHoursHistoryDao twoHoursHistoryDao();

    public abstract FourHoursHistoryDao fourHoursHistoryDao();

    public abstract DayHistoryDao dayHistoryDao();

    public abstract MonthHistoryDao monthHistoryDao();

    public abstract WeekHistoryDao weekHistoryDao();

    public static SinaDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (IcbcDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                    SinaDatabase.class, Constants.ROOM_SINA_DB_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }
}
