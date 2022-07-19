package com.hexon.chartlib.stock.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.hexon.chartlib.R;

public class KLineMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView mTvMarker;
    private String mValue;

    public KLineMarkerView(Context context) {
        super(context, R.layout.view_kline_marker);
        mTvMarker = (TextView) findViewById(R.id.marker_tv);
    }

    public KLineMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        //mFormat = new DecimalFormat("#0.00");
        mTvMarker = (TextView) findViewById(R.id.marker_tv);
    }

    public void setData(String value){
        mValue = value;
        mTvMarker.setText(mValue);
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

    }
}
