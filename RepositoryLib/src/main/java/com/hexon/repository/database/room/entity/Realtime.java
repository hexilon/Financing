package com.hexon.repository.database.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.hexon.chartlib.stock.model.RealtimeEntity;
import com.hexon.repository.Constants;
import com.hexon.util.StringUtils;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by hexiaohong on 2017/4/12.
 */
@Entity(indices = {@Index(value = "mTime", unique = true)})
public class Realtime extends RealtimeEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "type")
    public int mType;

    //@ColumnInfo(name = "timestamp")
    //public long mTimestamp;

    //@ColumnInfo(name = "date")
    @Ignore
    public String mDate;

    //@ColumnInfo(name = "price")
    //public float mPrice;

    public Realtime() {}

    public Realtime(Constants.MetalType type, Calendar calendar, float price) {
        super(calendar, price);
        mType = type.ordinal();
        mDate = StringUtils.dateToDateString(mTime.getTime(), Constants.PATTERN_DATE_REALTIME);
    }

    public Realtime(Constants.MetalType type, Date date, float price) {
        this(type, date.getTime(), 0f);
    }

    public Realtime(Constants.MetalType type, long timestamp, float price) {
        super(timestamp, price);
        mType = type.ordinal();
        mDate = StringUtils.dateToDateString(mTime.getTime(), Constants.PATTERN_DATE_REALTIME);
    }

    @Override
    public String toString() {
        return "type:" + mType
                + "date:" + mDate
                + " price:" + mPrice;
    }
}
