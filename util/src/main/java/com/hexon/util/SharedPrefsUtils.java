package com.hexon.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class SharedPrefsUtils {

    private static Map<String, SharedPrefsUtils> sSPMap = new HashMap<>();
    private SharedPreferences mSp;

    /**
     * 获取SP实例
     *
     * @return {@link SharedPrefsUtils}
     */
    public static SharedPrefsUtils getInstance(Application application) {
        return getInstance(application, "");
    }

    /**
     * 获取SP实例
     *
     * @param spName sp名
     * @return {@link SharedPrefsUtils}
     */
    public static SharedPrefsUtils getInstance(Application application, String spName) {
        if (isSpace(spName)) spName = application.getPackageName();
        SharedPrefsUtils sp = sSPMap.get(spName);
        if (sp == null) {
            sp = new SharedPrefsUtils(application, spName);
            sSPMap.put(spName, sp);
        }
        return sp;
    }

    private SharedPrefsUtils(Context context, final String spName) {
        mSp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 保存数据到SharedPreferences
     *
     * @param key   键
     * @param value 需要保存的数据
     * @return 保存结果
     */
    public boolean putData(String key, Object value) {
        boolean result;
        SharedPreferences.Editor editor = mSp.edit();
        String type = value.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    editor.putBoolean(key, (Boolean) value);
                    break;
                case "Long":
                    editor.putLong(key, (Long) value);
                    break;
                case "Float":
                    editor.putFloat(key, (Float) value);
                    break;
                case "String":
                    editor.putString(key, (String) value);
                    break;
                case "Integer":
                    editor.putInt(key, (Integer) value);
                    break;
                default:
                    Gson gson = new Gson();
                    String json = gson.toJson(value);
                    editor.putString(key, json);
                    break;
            }
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 获取SharedPreferences中保存的数据
     *
     * @param key          键
     * @param defaultValue 获取失败默认值
     * @return 从SharedPreferences读取的数据
     */
    public <T extends Object> T getData(String key, T defaultValue) {
        T result;
        String type = defaultValue.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    result = (T) (Boolean) mSp.getBoolean(key, (Boolean) defaultValue);
                    break;
                case "Long":
                    result = (T) (Long) mSp.getLong(key, (Long) defaultValue);
                    break;
                case "Float":
                    result = (T) (Float) mSp.getFloat(key, (Float) defaultValue);
                    break;
                case "String":
                    result = (T) (String) mSp.getString(key, (String) defaultValue);
                    break;
                case "Integer":
                    result = (T) (Integer) mSp.getInt(key, (Integer) defaultValue);
                    break;
                default:
                    Gson gson = new Gson();
                    String json = mSp.getString(key, "");
                    if (!json.equals("") && json.length() > 0) {
                        result = (T) gson.fromJson(json, defaultValue.getClass());
                    } else {
                        result = defaultValue;
                    }
                    break;
            }
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 用于保存集合
     *
     * @param key  key
     * @param list 集合数据
     * @return 保存结果
     */
    public <T> boolean putListData(String key, List<T> list) {
        boolean result;
        String type;
        if (list != null && list.size() != 0) {
            type = list.get(0).getClass().getSimpleName();
        } else {
            SharedPreferences.Editor editor = mSp.edit();
            editor.putString(key, "");
            editor.apply();
            return true;
        }
        SharedPreferences.Editor editor = mSp.edit();
        JsonArray array = new JsonArray();
        try {
            switch (type) {
                case "Boolean":
                    for (int i = 0; i < list.size(); i++) {
                        array.add((Boolean) list.get(i));
                    }
                    break;
                case "Long":
                    for (int i = 0; i < list.size(); i++) {
                        array.add((Long) list.get(i));
                    }
                    break;
                case "Float":
                    for (int i = 0; i < list.size(); i++) {
                        array.add((Float) list.get(i));
                    }
                    break;
                case "String":
                    for (int i = 0; i < list.size(); i++) {
                        array.add((String) list.get(i));
                    }
                    break;
                case "Integer":
                    for (int i = 0; i < list.size(); i++) {
                        array.add((Integer) list.get(i));
                    }
                    break;
                default:
                    Gson gson = new Gson();
                    for (int i = 0; i < list.size(); i++) {
                        JsonElement obj = gson.toJsonTree(list.get(i));
                        array.add(obj);
                    }
                    break;
            }
            editor.putString(key, array.toString());
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 获取保存的List
     *
     * @param key key
     * @return 对应的Lis集合
     */
    public <T> List<T> getListData(String key, Class<T> cls) {
        List<T> list = new ArrayList<>();
        String json = mSp.getString(key, "");
        if (!json.equals("") && json.length() > 0) {
            Gson gson = new Gson();
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (JsonElement elem : array) {
                list.add(gson.fromJson(elem, cls));
            }
        }
        return list;
    }

    /**
     * 用于保存集合
     *
     * @param key key:String类型
     * @param map map数据
     * @return 保存结果
     */
    public <K, V> boolean putHashMapData(String key, Map<K, V> map) {
        boolean result;
        SharedPreferences.Editor editor = mSp.edit();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            editor.putString(key, json);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 用于保存集合
     *
     * @param key key
     * @return HashMap
     */
    public <V> HashMap<String, V> getHashMapData(String key, Class<V> clsV) {
        String json = mSp.getString(key, "");
        if (json.isEmpty()) {
            return null;
        }
        //log.d(TAG, "json:" + json);
        HashMap<String, V> map = new HashMap<>();
        Gson gson = new Gson();
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String entryKey = entry.getKey();
            JsonElement value = (JsonElement) entry.getValue();
            map.put(entryKey, gson.fromJson(value, clsV));
        }

        return map;
    }

    public boolean contains(@NonNull final String key) {
        return mSp.contains(key);
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public void remove(@NonNull final String key) {
        mSp.edit().remove(key).apply();
    }

    /**
     * SP中清除所有数据
     */
    public void clear() {
        mSp.edit().clear().apply();
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
