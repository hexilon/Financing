package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.OneHourHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : OneHourHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface OneHourHistoryDao {
    /*query*/
    @Query("SELECT * from OneHourHistory WHERE :id = id order by timestamp ASC")
    abstract Single<OneHourHistory> query(long id);

    @Query("SELECT * from OneHourHistory WHERE :type = type")
    abstract Single<List<OneHourHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<OneHourHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(OneHourHistory history);

    /*update*/
    @Update
    abstract int update(OneHourHistory history);

    /*delete*/
    @Delete
    abstract void delete(OneHourHistory history);
}
