package com.ht.feignapi.tonglian.utils;

import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 10:28
 */
public class TimeUtil {

    private static final Logger log = LoggerFactory.getLogger(TimeUtil.class);

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";// 时间格式
    public static final String DEFAULT_FORMAT1 = "yyyy/MM/dd HH:mm:ss";// 时间格式1
    public static final String DEFAULT_FORMATS = "yyyy-MM-dd";
    public static final String DATE_FOMATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String Message_TIME = "yyyy年MM月dd日HH点mm分";

    /**
     * 格式化时间(Date 转换成String)
     *
     * @param date   时间
     * @param format 时间格式 如： DEFAULT_FORMAT= "yyyy-MM-dd HH:mm:ss"
     * @return 字符串
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 字符串格式化为时间
     *
     * @param dateStr  时间字符串
     * @param format 时间格式 如：DEFAULT_FORMAT1 = "yyyy/MM/dd HH:mm:ss";// 时间格式1
     * @return
     */
    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        if (!StringUtils.isEmpty(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
        return date;
    }

    public static Date addHours(Date dateTime/*待处理的日期*/,int n/*加减小时*/){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.add(Calendar.HOUR, n);
        return calendar.getTime();
    }

    public static Date addDay(Date dateTime/*待处理的日期*/,int n/*天数*/){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        int hours = n*24;
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }
}
