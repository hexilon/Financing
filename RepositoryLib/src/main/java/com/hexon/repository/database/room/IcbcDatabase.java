package com.hexon.repository.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hexon.repository.Constants;
import com.hexon.repository.Converters;
import com.hexon.repository.database.room.dao.DayHistoryDao;
import com.hexon.repository.database.room.dao.MonthHistoryDao;
import com.hexon.repository.database.room.dao.RealtimeDao;
import com.hexon.repository.database.room.dao.WeekHistoryDao;
import com.hexon.repository.database.room.entity.DayHistory;
import com.hexon.repository.database.room.entity.MonthHistory;
import com.hexon.repository.database.room.entity.Realtime;
import com.hexon.repository.database.room.entity.WeekHistory;


/**
 * Copyright (C), 2020-2025
 * FileName    : IcbcDatabase
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/19 9:24
 * Version     : V1.0
 */
@Database(entities = {DayHistory.class, WeekHistory.class, MonthHistory.class, Realtime.class},
        version = 1)
@TypeConverters(Converters.class)
public abstract class IcbcDatabase extends RoomDatabase {
    private static volatile IcbcDatabase sInstance;

    public abstract DayHistoryDao dayHistoryDao();

    public abstract MonthHistoryDao monthHistoryDao();

    public abstract WeekHistoryDao weekHistoryDao();

    public abstract RealtimeDao realtimeDao();

    public static IcbcDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (IcbcDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            IcbcDatabase.class, Constants.ROOM_ICBC_DB_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }
}
