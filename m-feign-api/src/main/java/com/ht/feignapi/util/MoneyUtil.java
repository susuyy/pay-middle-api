package com.ht.feignapi.util;

import java.math.BigDecimal;

public class MoneyUtil {

    /**
     * 分转元，转换为bigDecimal在toString
     * @return
     */
    public static String changeF2Y(long price) {
        return BigDecimal.valueOf(price).divide(new BigDecimal(100)).toString();
    }

}
