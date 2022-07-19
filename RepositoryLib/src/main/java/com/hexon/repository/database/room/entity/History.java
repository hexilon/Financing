package com.hexon.repository.database.room.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.repository.Constants;
import com.hexon.util.StringUtils;

/**
 * @author Hexh
 * @date 2018-12-08 13:51
 */
@Entity(indices = {@Index(value = {"type", "timestamp"}, unique = true)})
public abstract class History {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "type")
    public int mType;

    @ColumnInfo(name = "timestamp")
    public long mTimestamp;

    @ColumnInfo(name = "date")
    public String mStrDate;

    @ColumnInfo(name = "open")
    public float mOpen;

    @ColumnInfo(name = "high")
    public float mHigh;

    @ColumnInfo(name = "low")
    public float mLow;

    @ColumnInfo(name = "close")
    public float mClose;

    public History() {
    }

    public History(Constants.MetalType type, HistoryEntity entity) {
        mType = type.ordinal();
        mTimestamp = entity.mDate.getTimeInMillis();
        mStrDate = StringUtils.dateToDateString(entity.mDate.getTime(), Constants.PATTERN_DATE_HISTORY);
        mHigh = entity.mHigh;
        mLow = entity.mLow;
        mOpen = entity.mOpen;
        mClose = entity.mClose;
    }

    public History(Constants.MetalType type, long timestamp, String date, float high, float low, float open, float close) {
        mType = type.ordinal();
        mTimestamp = timestamp;
        mStrDate = date;
        this.mHigh = high;
        this.mLow = low;
        this.mOpen = open;
        this.mClose = close;
    }

    public void set(Constants.MetalType type, HistoryEntity entity) {
        mType = type.ordinal();
        mTimestamp = entity.mDate.getTimeInMillis();
        mStrDate = StringUtils.dateToDateString(entity.mDate.getTime(), Constants.PATTERN_DATE_HISTORY);
        mHigh = entity.mHigh;
        mLow = entity.mLow;
        mOpen = entity.mOpen;
        mClose = entity.mClose;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (o instanceof History) {
            History entity = (History) o;
            return id == entity.id && mType == entity.mType && mTimestamp == entity.mTimestamp
                    && mOpen == entity.mOpen && mClose == entity.mClose
                    && mHigh == entity.mHigh && mLow == entity.mLow;
        } else if (o instanceof HistoryEntity) {
            HistoryEntity entity = (HistoryEntity) o;
            return mTimestamp == entity.mDate.getTimeInMillis()
                    && mOpen == entity.mOpen && mClose == entity.mClose
                    && mHigh == entity.mHigh && mLow == entity.mLow;
        }

        return false;
    }

    @Override
    public String toString() {
        return "id:" + id
                + " type:" + mType
                + " timestamp:" + mTimestamp
                + " date:" + mStrDate
                + " open:" + mOpen
                + " high:" + mHigh
                + " low:" + mLow
                + " close:" + mClose;
    }
}
