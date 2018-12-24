package com.growalong.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gangqing on 16/8/13.
 */

public class DateUtil {

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getTomorrowDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return String.valueOf(Integer.valueOf(df.format(new Date())) + 1);
    }

    /**
     * 获取当前日期字符串
     *
     * @return
     */
    public static String getCurrentDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        return df.format(new Date());
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH);
    }

    /**
     * 获取当前日
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE);
    }

    /**
     * 将时间戳转化为字符串
     *
     * @param showTime
     * @return
     */
    public static String formatTime2String(long showTime) {
        return formatTime2String(showTime, false);
    }

    public static String formatTime2String(long showTime, boolean haveYear) {
        String str = "";
        long distance = System.currentTimeMillis() - showTime;
        if (distance < 300) {
            str = "刚刚";
        } else if (distance >= 300 && distance < 600) {
            str = "5分钟前";
        } else if (distance >= 600 && distance < 1200) {
            str = "10分钟前";
        } else if (distance >= 1200 && distance < 1800) {
            str = "20分钟前";
        } else if (distance >= 1800 && distance < 2700) {
            str = "半小时前";
        } else if (distance >= 2700) {
            Date date = new Date(showTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = formatDateTime(sdf.format(date), haveYear);
        }
        return str;

    }

//    public static String formatTime2String(long showTime, boolean haveYear) {
//        String str = "";
//        long distance = System.currentTimeMillis() - showTime;
//        if (distance < 300) {
//            str = "刚刚";
//        } else if (distance >= 300 && distance < 600) {
//            str = "5分钟前";
//        } else if (distance >= 600 && distance < 1200) {
//            str = "10分钟前";
//        } else if (distance >= 1200 && distance < 1800) {
//            str = "20分钟前";
//        } else if (distance >= 1800 && distance < 2700) {
//            str = "半小时前";
//        } else if (distance >= 2700) {
//            Date date = new Date(showTime * 1000);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            str = formatDateTime(sdf.format(date), haveYear);
//        }
//        return str;
//    }

    public static String formatDateTime(String time, boolean haveYear) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time == null) {
            return "";
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);
        if (current.after(today)) {
            return "今天 " + time.split(" ")[1];
        } else if (current.before(today) && current.after(yesterday)) {
            return "昨天 " + time.split(" ")[1];
        } else {
            if (haveYear) {
                int index = time.indexOf(" ");
                return time.substring(0, index);
            } else {
                int yearIndex = time.indexOf("-") + 1;
                int index = time.indexOf(" ");
                time = time.substring(0, index);
                return time.substring(yearIndex, time.length());
            }
        }
    }


    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s, String format) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate(long s, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static int compareDate(String strDate1, String strDate2) {


        int returnVal = 0;
        try {
            Date date1 = getDateFromString(strDate1 + " 00:00:00");
            Date date2 = getDateFromString(strDate2 + " 00:00:00");
            returnVal = (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnVal;
    }

    public static Date getDateFromString(String s) {
        Date returnDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            returnDate = sdf.parse(s);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public static String format(long time) {
        long delta = System.currentTimeMillis() - time;
        if (delta < 1L * ONE_MINUTE) {
//            long seconds = toSeconds(delta);
//            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
            return "刚刚";
        }
        if (delta < 60L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
//        if (delta < 48L * ONE_HOUR) {
//            return "昨天";
//        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } else {
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }
}
