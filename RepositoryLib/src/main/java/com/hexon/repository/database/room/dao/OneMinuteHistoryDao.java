package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.OneMinuteHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : OneMinuteHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface OneMinuteHistoryDao {
    /*query*/
    @Query("SELECT * from OneMinuteHistory WHERE :id = id order by timestamp ASC")
    abstract Single<OneMinuteHistory> query(long id);

    @Query("SELECT * from OneMinuteHistory WHERE :type = type")
    abstract Single<List<OneMinuteHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<OneMinuteHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(OneMinuteHistory history);

    /*update*/
    @Update
    abstract int update(OneMinuteHistory history);

    /*delete*/
    @Delete
    abstract void delete(OneMinuteHistory history);
}
