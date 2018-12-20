package com.growalong.android.util;

import com.growalong.android.app.AppManager;
import com.growalong.android.ui.ChatActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 */
public class Utils {
    /**
     * 压缩阿里图片
     *
     * @param url
     * @return
     */
    public static String compressQualityOSSImageUrl(String url) {
        return url;
    }

    public static String stampToDate(long s, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static double div(double v1, double v2, int scale) {
        //部分6.0设备BigDecimal会有问题,所以涉及BigDecimal的都做一遍try catch,最后再操作一遍返回
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        try {
            b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
        }
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;
            } else {
                age--;
            }
        }
        return age;
    }

    public static String getIMTextString(String string) {
        try {
            int nation = AppManager.getInstance().getUserInfoModel().getNation();
            if (string.contains(ChatActivity.TRANSLATE_TAG)) {
                final String[] split = string.split(ChatActivity.TRANSLATE_TAG);
                String ch, en;
                ch = split[0].substring(1);
                en = split[1];
                if (ChatActivity.showTranslate == ChatActivity.ShowTranslate.ChineseAndEnglish) {
                    //中英文都显示
                    if (nation == 1) {
                        //中国人
                        string = ch + "\n------------\n" + en;
                    } else {
                        string = en + "\n------------\n" + ch;
                    }
                } else if (ChatActivity.showTranslate == ChatActivity.ShowTranslate.Chinese) {
                    //显示中文
                    string = ch;
                } else if (ChatActivity.showTranslate == ChatActivity.ShowTranslate.English) {
                    //显示英文
                    string = en;
                } else if (ChatActivity.showTranslate == ChatActivity.ShowTranslate.Normal) {
                    //显示原文
                    String type = split[0].substring(0, 1);
                    if ("0".equals(type)) {
                        string = ch;
                    } else {
                        string = en;
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }
}
