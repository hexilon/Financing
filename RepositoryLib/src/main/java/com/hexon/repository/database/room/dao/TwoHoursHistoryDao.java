package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.TwoHoursHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : TwoHoursHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface TwoHoursHistoryDao {
    /*query*/
    @Query("SELECT * from TwoHoursHistory WHERE :id = id order by timestamp ASC")
    abstract Single<TwoHoursHistory> query(long id);

    @Query("SELECT * from TwoHoursHistory WHERE :type = type")
    abstract Single<List<TwoHoursHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<TwoHoursHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(TwoHoursHistory history);

    /*update*/
    @Update
    abstract int update(TwoHoursHistory history);

    /*delete*/
    @Delete
    abstract void delete(TwoHoursHistory history);
}
