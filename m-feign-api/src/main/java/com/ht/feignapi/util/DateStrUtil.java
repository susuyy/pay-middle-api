package com.ht.feignapi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStrUtil {

    /**
     * 获取当前时间
     * @return
     */
    public static String nowDateStr(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String nowDateStrYearMoonDay(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 转化Date
     * @return
     */
    public static String dateToStr(Date date){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }

    /**
     * 转化Date
     * @return
     */
    public static String dateToStrSs(Date date){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前时间HHmmSS
     * @return
     */
    public static String nowDateStrHHmmSS(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取当前时间MMdd
     * @return
     */
    public static String nowDateStrMMdd(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM-dd");
        return simpleDateFormat.format(new Date());
    }

}
