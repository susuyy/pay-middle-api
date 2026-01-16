package com.ht.user.outlets.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/25 18:03
 */
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
     * 获取当前时间
     * @return
     */
    public static String nowDateStrToyyyyMMddHHmmss(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
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

    /**
     * 获取时间MMdd
     * @return
     */
    public static String dateStrMMdd(Date date){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMdd");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取时间MMdd
     * @return
     */
    public static String dateStrHHmmss(Date date){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HHmmss");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前时间
     * @return
     */
    public static Date StrToDateyyyyMMddHHmmss(String dateStr)throws Exception{
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.parse(dateStr);
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String jiaLianRetOrderDateChange(String dateStr)throws Exception{
        SimpleDateFormat simpleDateFormatDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormatDate.parse(dateStr);
        SimpleDateFormat simpleDateFormatStr=new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormatStr.format(date);
    }


}
