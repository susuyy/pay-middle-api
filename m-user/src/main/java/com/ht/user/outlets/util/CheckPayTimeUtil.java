package com.ht.user.outlets.util;

import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckPayTimeUtil {

    /**
     * 校验订单支付时间是否超时
     * @param orderCreateAt 订单创建时间
     * @return
     */
    public static boolean checkPayTime(Date orderCreateAt){
        //订单过期支付限制
        long nowDate = new Date().getTime();
        long orderTime = orderCreateAt.getTime() + 605 * 1000;   //限制订单支付到期时间
        int diffSeconds = (int) ((orderTime - nowDate) / 1000);
        if (diffSeconds <5 ){
            return false;
        }
        return true;
    }

    /**
     * 获取订单支付截止日期
     * @param orderCreateAt 订单创建时间
     * @return
     */
    public static Date getLimitPayTime(Date orderCreateAt){
        long orderTime = orderCreateAt.getTime() + 600 * 1000;   //限制订单支付到期时间
        return new Date(orderTime);
    }
}
