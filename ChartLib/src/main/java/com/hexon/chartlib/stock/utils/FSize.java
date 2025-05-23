
package com.hexon.chartlib.stock.utils;

import com.github.mikephil.charting.utils.ObjectPool;

import java.util.List;

/**
 * Class for describing width and height dimensions in some arbitrary
 * unit. Replacement for the android.Util.SizeF which is available only on API >= 21.
 */
public final class FSize extends ObjectPool.Poolable{

    // TODO : Encapsulate width & height

    public float width;
    public float height;

    private static ObjectPool<com.github.mikephil.charting.utils.FSize> pool;

    static {
        pool = ObjectPool.create(256, new com.github.mikephil.charting.utils.FSize(0,0));
        pool.setReplenishPercentage(0.5f);
    }


    @Override
    protected ObjectPool.Poolable instantiate(){
        return new com.github.mikephil.charting.utils.FSize(0,0);
    }

    public static com.github.mikephil.charting.utils.FSize getInstance(final float width, final float height){
        com.github.mikephil.charting.utils.FSize result = pool.get();
        result.width = width;
        result.height = height;
        return result;
    }

    public static void recycleInstance(com.github.mikephil.charting.utils.FSize instance){
        pool.recycle(instance);
    }

    public static void recycleInstances(List<com.github.mikephil.charting.utils.FSize> instances){
        pool.recycle(instances);
    }

    public FSize() {
    }

    public FSize(final float width, final float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof com.github.mikephil.charting.utils.FSize) {
            final com.github.mikephil.charting.utils.FSize other = (com.github.mikephil.charting.utils.FSize) obj;
            return width == other.width && height == other.height;
        }
        return false;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(width) ^ Float.floatToIntBits(height);
    }
}
