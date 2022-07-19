package com.hexon.repository.model;

import com.hexon.util.LogUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * @author Hexon
 * @date 2019-08-26 18:41
 */
public class FXCalendarEvent implements Serializable {
    public enum Importance{
        NONE,HIGH, MID, LOW
    };

    public Calendar calendar;
    public boolean isShowTime;
    public String country;
    public String content;
    public Importance importance;
    public String strImportance;
    public String result;
    public String forecast;
    public String preResult;
    public String comment;

    public FXCalendarEvent(FXCalendarEvent event) {
        if (event == null) {
            return;
        }
        calendar = event.calendar;
        isShowTime = event.isShowTime;
        country = event.country;
        content = event.content;
        importance = event.importance;
        strImportance = event.strImportance;
        result = event.result;
        forecast = event.forecast;
        preResult = event.preResult;
        comment = event.comment;
    }

    public FXCalendarEvent() {
    }

    public static Importance parseImportance(String text) {
        if (text.equals("高")) {
            return Importance.HIGH;
        } else if (text.equals("中")) {
            return Importance.MID;
        } else if (text.equals("低")) {
            return Importance.LOW;
        } else {
            return Importance.NONE;
        }
    }

    public static void parseResult(String text, FXCalendarEvent event) {
        if (text == null || text.length() <= 0) {
            return;
        }

        String result[] = text.split(" ");
        for (int index = 0; index < result.length; index ++) {
            String temp = result[index];
            LogUtils.d("temp:" + temp);
            if (temp.equals("结果:")) {
                if (!result[index+1].equals("预测:")) {
                    event.result = result[index+1];
                    ++index;
                }
            } else if (temp.equals("预测:")) {
                if (!result[index+1].equals("前值:")) {
                    event.forecast = result[index+1];
                    ++index;
                }
            } else if (temp.equals("前值:")) {
                if (index +1 < result.length) {
                    event.preResult = result[index+1];
                }
            }
        }
    }

    //获取最近公布的数据时间
    public static Calendar getRecentPublishTime(List<FXCalendarEvent> list) {
        Calendar now = Calendar.getInstance();
        //第一个事件就比现在早
        if (list.get(0).calendar.after(now)) {
            return list.get(0).calendar;
        }

        for (int index = 0; index < list.size()-1; index ++) {
            //某一个事件早于现在,但是下一事件晚于现在
            if (list.get(index).calendar.before(now)
                && list.get(index +1).calendar.after(now)
                && (list.get(index +1).result == null || list.get(index +1).result.isEmpty())) {
                return list.get(index +1).calendar;
            }
        }

        return null;
    }

    //获取最后公布的数据时间
    public static Calendar getLatestPublishTime(List<FXCalendarEvent> list) {
        Calendar published = null;

        for (int index = 0; index < list.size()-1; index ++) {
            //某一个事件早于现在,但是下一事件晚上现在
            if (list.get(index).result != null && list.get(index).result.length() > 0) {
                if (published == null) {
                    published = list.get(index).calendar;
                } else if (published.before(list.get(index).calendar)) {
                    published = list.get(index).calendar;
                }
            }
        }

        return published;
    }

    @Override
    public String toString() {
        return "time:" + calendar.getTime()
                + " isShowTime:" + isShowTime
                + " country:" + country
                + " content:" + content
                + " importance:" + importance
                + " result:" + result
                + " forecast:" + forecast
                + " preResult:" + preResult
                + " comment:" + comment;
    }
}
