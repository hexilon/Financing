package com.hexon.chartlib.stock.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.utils.ObjectPool;

import java.util.List;

/**
 * Created by Tony Patino on 6/24/16.
 */
public class MPPointF extends ObjectPool.Poolable {

    private static ObjectPool<com.github.mikephil.charting.utils.MPPointF> pool;

    public float x;
    public float y;

    static {
        pool = ObjectPool.create(32, new com.github.mikephil.charting.utils.MPPointF(0, 0));
        pool.setReplenishPercentage(0.5f);
    }

    public MPPointF() {
    }

    public MPPointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static com.github.mikephil.charting.utils.MPPointF getInstance(float x, float y) {
        com.github.mikephil.charting.utils.MPPointF result = pool.get();
        result.x = x;
        result.y = y;
        return result;
    }

    public static com.github.mikephil.charting.utils.MPPointF getInstance() {
        return pool.get();
    }

    public static com.github.mikephil.charting.utils.MPPointF getInstance(com.github.mikephil.charting.utils.MPPointF copy) {
        com.github.mikephil.charting.utils.MPPointF result = pool.get();
        result.x = copy.x;
        result.y = copy.y;
        return result;
    }

    public static void recycleInstance(com.github.mikephil.charting.utils.MPPointF instance) {
        pool.recycle(instance);
    }

    public static void recycleInstances(List<com.github.mikephil.charting.utils.MPPointF> instances) {
        pool.recycle(instances);
    }

    public static final Parcelable.Creator<com.github.mikephil.charting.utils.MPPointF> CREATOR = new Parcelable.Creator<com.github.mikephil.charting.utils.MPPointF>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        @Override
        public com.github.mikephil.charting.utils.MPPointF createFromParcel(Parcel in) {
            com.github.mikephil.charting.utils.MPPointF r = new com.github.mikephil.charting.utils.MPPointF(0, 0);
            r.my_readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        @Override
        public com.github.mikephil.charting.utils.MPPointF[] newArray(int size) {
            return new com.github.mikephil.charting.utils.MPPointF[size];
        }
    };

    /**
     * Set the point's coordinates from the data stored in the specified
     * parcel. To write a point to a parcel, call writeToParcel().
     * Provided to support older Android devices.
     *
     * @param in The parcel to read the point's coordinates from
     */
    public void my_readFromParcel(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    @Override
    protected ObjectPool.Poolable instantiate() {
        return new com.github.mikephil.charting.utils.MPPointF(0, 0);
    }
}