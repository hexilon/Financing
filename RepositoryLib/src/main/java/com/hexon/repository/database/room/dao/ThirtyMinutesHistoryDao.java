package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.ThirtyMinutesHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : ThirtyMinutesHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface ThirtyMinutesHistoryDao {
    /*query*/
    @Query("SELECT * from ThirtyMinutesHistory WHERE :id = id order by timestamp ASC")
    abstract Single<ThirtyMinutesHistory> query(long id);

    @Query("SELECT * from ThirtyMinutesHistory WHERE :type = type")
    abstract Single<List<ThirtyMinutesHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<ThirtyMinutesHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(ThirtyMinutesHistory history);

    /*update*/
    @Update
    abstract int update(ThirtyMinutesHistory history);

    /*delete*/
    @Delete
    abstract void delete(ThirtyMinutesHistory history);
}
