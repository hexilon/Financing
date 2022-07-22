package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.FourHoursHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : FourHoursHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface FourHoursHistoryDao {
    /*query*/
    @Query("SELECT * from FourHoursHistory WHERE :id = id order by timestamp ASC")
    abstract Single<FourHoursHistory> query(long id);

    @Query("SELECT * from FourHoursHistory WHERE :type = type")
    abstract Single<List<FourHoursHistory>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<FourHoursHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(FourHoursHistory history);

    /*update*/
    @Update
    abstract int update(FourHoursHistory history);

    /*delete*/
    @Delete
    abstract void delete(FourHoursHistory history);
}
