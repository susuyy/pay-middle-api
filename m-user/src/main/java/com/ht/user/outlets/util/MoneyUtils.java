package com.ht.user.outlets.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class MoneyUtils {

    /**
     * 元转分，确保price保留两位有效数字
     *
     * @return
     */
    public static Integer changeY2F(double price) {
        DecimalFormat df = new DecimalFormat("#.00");
        price = Double.valueOf(df.format(price));
        int money = (int) (price * 100);
        return money;
    }

    /**
     * 分转元，转换为bigDecimal在toString
     *
     * @return
     */
    public static String changeF2Y(Integer price) {
        return BigDecimal.valueOf(Integer.valueOf(price)).divide(new BigDecimal(100)).toString();
    }


    /**
     * 分转元，int转换为bigDecimal
     *
     * @return
     */
    public static BigDecimal changeF2YBigDecimal(Integer price) {
        return BigDecimal.valueOf(Integer.valueOf(price)).divide(new BigDecimal(100));
    }

    /**
     * 分转元，bigDecimal转换为bigDecimal
     *
     * @return
     */
    public static BigDecimal changeBigDecimalF2YBigDecimal(BigDecimal price) {
        return price.divide(new BigDecimal(100));
    }




}