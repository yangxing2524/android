package com.growalong.android.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

}
