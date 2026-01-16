package com.ht.user.utils;

import java.math.BigDecimal;

/**
 * @author: zheng weiguang
 * @Date: 2021/12/8 17:06
 */
public class MyMathUtil {

    /**
     * 保留小数点后两位
     * @param data
     * @return
     */
    public static BigDecimal KeepTwoDecimalPlaces(BigDecimal data) {
        return data.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
