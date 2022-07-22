package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.History;

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
public interface HistoryDao {
    /*query*/
    @Query("SELECT * from History WHERE :id = id order by timestamp ASC")
    abstract Single<History> query(long id);

    @Query("SELECT * from History WHERE :type = type")
    abstract Single<List<History>> query(String type);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertHistories(List<History> histories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insert(History history);

    /*update*/
    @Update
    abstract int update(History history);

    /*delete*/
    @Delete
    abstract void delete(History history);
}
