package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.FiveMinutesHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : FiveMinutesHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface FiveMinutesHistoryDao {
    /*query*/
    @Query("SELECT * from FiveMinutesHistory WHERE :id = id order by timestamp ASC")
    abstract Single<FiveMinutesHistory> query(long id);

    @Query("SELECT * from FiveMinutesHistory WHERE :type = type")
    abstract Single<List<FiveMinutesHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<FiveMinutesHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(FiveMinutesHistory history);

    /*update*/
    @Update
    abstract int update(FiveMinutesHistory history);

    /*delete*/
    @Delete
    abstract void delete(FiveMinutesHistory history);
}
