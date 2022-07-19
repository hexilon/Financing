package com.hexon.chartlib.stock.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.hexon.chartlib.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RealtimeMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView mTvMarker;
    private String mValue;
    private Postion mPos;

    public enum Postion {
        LEFT,
        RIGHT,
        BOTTOM
    };

    public RealtimeMarkerView(Context context) {
        super(context, R.layout.view_realtime_marker);
        mTvMarker = (TextView) findViewById(R.id.marker_tv);
    }

    public RealtimeMarkerView(Context context, Postion postion) {
        super(context, R.layout.view_realtime_marker);
        mPos = postion;
        mTvMarker = (TextView) findViewById(R.id.marker_tv);
    }

    public void setData(String value){
        mValue = value;
        mTvMarker.setText(mValue);
    }


    public void setData(Entry entry) {
        if (mPos == Postion.LEFT) {
            mValue = "" + entry.getY();
        } else if (mPos == Postion.BOTTOM) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss",//yyyy-MM-dd
                    Locale.getDefault());
            long lcc = Long.valueOf((long)entry.getX() *1000);
            mValue = format.format(new Date(lcc));
        } else {
            mValue = "error";
        }

        mTvMarker.setText(mValue);
    }

    public void setPercentData(float openPrice, Entry entry) {
        if (mPos != Postion.RIGHT) {
            return;
        }

        DecimalFormat mFormat = new DecimalFormat("#0.00%");
        mValue = mFormat.format((entry.getY() - openPrice)/openPrice);
        mTvMarker.setText(mValue);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //mTvMarker.setText(mFormat.format(mValue));
    }
}
