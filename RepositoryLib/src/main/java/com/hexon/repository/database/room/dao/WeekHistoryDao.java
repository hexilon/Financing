package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.WeekHistory;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : WeekHistoryDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 16:57
 * Version     : V1.0
 */
@Dao
public interface WeekHistoryDao {
    /*query sort by date*/
    @Query("SELECT * from WeekHistory WHERE :type = type order by timestamp ASC")
    public Single<List<WeekHistory>> query(int type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertHistories(List<WeekHistory> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(WeekHistory history);

    /*update*/
    @Update
    public int update(WeekHistory history);

    /*delete*/
    @Delete
    public void delete(WeekHistory history);
}
