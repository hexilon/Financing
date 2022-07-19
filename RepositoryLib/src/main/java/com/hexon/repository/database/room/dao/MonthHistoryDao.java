package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.MonthHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : DayHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface MonthHistoryDao {
    /*query*/
    @Query("SELECT * from MonthHistory WHERE :type = type order by timestamp ASC")
    Single<List<MonthHistory>> query(int type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistories(List<MonthHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MonthHistory history);

    /*update*/
    @Update
    int update(MonthHistory history);

    /*delete*/
    @Delete
    void delete(MonthHistory history);
}
