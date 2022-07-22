package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.DayHistory;

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
public interface DayHistoryDao {
    /*query*/
    @Query("SELECT * from DayHistory WHERE :id = id order by timestamp ASC")
    Single<DayHistory> query(long id);

    @Query("SELECT * from DayHistory WHERE :type = type")
    Single<List<DayHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistories(List<DayHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DayHistory history);

    /*update*/
    @Update
    int update(DayHistory history);

    /*delete*/
    @Delete
    void delete(DayHistory history);
}
