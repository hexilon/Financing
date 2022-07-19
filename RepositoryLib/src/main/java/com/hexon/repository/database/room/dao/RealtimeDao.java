package com.hexon.repository.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hexon.repository.database.room.entity.Realtime;

import java.util.List;

import io.reactivex.Single;

/**
 * Copyright (C), 2020-2025
 * FileName    : RealtimeDao
 * Description :
 * Author      : Hexon
 * Date        : 2020/8/18 17:48
 * Version     : V1.0
 */
@Dao
public interface RealtimeDao {
    /*query*/
    @Query("SELECT * from Realtime WHERE :type = type")
    public Single<List<Realtime>> query(int type);

    @Query("SELECT * from Realtime WHERE :type = type and :timestamp <= mTime")
    public Single<List<Realtime>> queryAfter(int type, long timestamp);

    @Query("SELECT * from Realtime WHERE :type = type and :start > mTime and :end < mTime")
    public Single<List<Realtime>> queryBetween(int type, long start, long end);

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertRealtimes(List<Realtime> realtimes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Realtime realtime);

    /*update*/
    @Update
    public int update(Realtime realtime);

    /*delete*/
    @Delete
    public void delete(Realtime realtime);
}
