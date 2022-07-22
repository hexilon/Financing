package com.hexon.repository.base;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.mvvm.base.BaseApplication;
import com.hexon.repository.database.room.entity.History;
import com.hexon.util.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BaseRepository {
    protected BaseApplication mApp;
    protected SharedPrefsUtils mSpUtils;

    public BaseRepository(BaseApplication application) {
        mApp = application;
        mSpUtils = SharedPrefsUtils.getInstance(application, this.getClass().getSimpleName());
    }

    public List<HistoryEntity> convertEntityList(List<? extends History> list) {
        List<HistoryEntity> entityList = new ArrayList<>();
        for (History entry : list) {
            HistoryEntity entity = new HistoryEntity();
            entity.mDate = Calendar.getInstance();
            entity.mDate.setTimeInMillis(entry.mTimestamp);
            entity.mOpen = entry.mOpen;
            entity.mClose = entry.mClose;
            entity.mHigh = entry.mHigh;
            entity.mLow = entry.mLow;
            entityList.add(entity);
        }

        return entityList;
    }
}
