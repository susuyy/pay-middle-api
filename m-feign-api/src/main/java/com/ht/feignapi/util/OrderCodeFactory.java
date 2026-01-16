package com.ht.feignapi.util;

/**
 * suyangyu
 */

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

public class OrderCodeFactory {

    public static String getOrderCode(OrderEnum orderEnum) {
        String category = orderEnum.getCategory();
        String idStr = IdWorker.getIdStr();
        return category + idStr;
    }

}
