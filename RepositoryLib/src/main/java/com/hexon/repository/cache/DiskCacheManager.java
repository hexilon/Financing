package com.hexon.repository.cache;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hexon.util.LogUtils;
import com.hexon.util.PackageUtils;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author Hexh
 * @date 2019-06-14 20:39
 * description save our datas in the internal storage
 */
public class DiskCacheManager {
    private static final int DISK_CACHE_SIZE = 5 * 1024 * 1024;
    private static final String DISK_CACHE_DIR = "data";

    private static DiskCacheManager sInstance;

    enum ValueIndex {
        TIME,
        DATA
    }

    private DiskLruCache mDiskLruCache = null;
    private final Context mContext;
    private String mCacheDirName;
    private Gson mGson = new Gson();

    public static DiskCacheManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DiskCacheManager.class) {
                if (sInstance == null) {
                    sInstance = new DiskCacheManager(context);
                }
            }
        }

        return sInstance;
    }

    private DiskCacheManager(Context context) {
        mContext = context;
        File cacheDir = getDiskCacheDir(mContext, DISK_CACHE_DIR);
        mCacheDirName = cacheDir.getPath();
        LogUtils.d("mCacheDirName:" + mCacheDirName);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, PackageUtils.getAppVersionCode(mContext),
                    ValueIndex.values().length, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    public String hashKeyForDiskCache(String key) {
        LogUtils.d("key:" + key);
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T getData(String key, Class<T> classOfT) {
        T result = null;
        try {
            DiskLruCache.Snapshot snapshot =
                    mDiskLruCache.get(hashKeyForDiskCache(key));
            if (snapshot != null) {
                String jsonStr = snapshot.getString(ValueIndex.DATA.ordinal());
                result = (T) mGson.fromJson(jsonStr, classOfT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean putData(String key, Object value) {
        boolean result = false;
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(hashKeyForDiskCache(key));
            if (editor != null) {
                editor.set(ValueIndex.TIME.ordinal(), "" + new Date().getTime());
                editor.set(ValueIndex.DATA.ordinal(), mGson.toJson(value));
                editor.commit();
                mDiskLruCache.flush();
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public <T> Observable<List<T>> getList(String key) {
        LogUtils.d("getList");
        return Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> emitter) throws Exception {
                try {
                    DiskLruCache.Snapshot snapshot =
                            mDiskLruCache.get(hashKeyForDiskCache(key));
                    if (snapshot != null) {
                        String jsonStr = snapshot.getString(ValueIndex.DATA.ordinal());
                        Type listOfType = new TypeToken<List<T>>() {
                        }.getType();
                        emitter.onNext(mGson.fromJson(jsonStr, listOfType));
                        emitter.onComplete();
                    } else {
                        emitter.onNext(null);
                        emitter.onComplete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
    }

    public <T> void putList(String key, List<T> list) {
        if (list == null || list.size() <= 0) {
            return;
        }

        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(hashKeyForDiskCache(key));
            if (editor != null) {
                editor.set(ValueIndex.TIME.ordinal(), "" + new Date().getTime());
                Type listOfType = new TypeToken<List<T>>() {
                }.getType();
                editor.set(ValueIndex.DATA.ordinal(), mGson.toJson(list, listOfType));
                editor.commit();
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
