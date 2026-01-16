package com.ht.user.outlets.util;

import java.text.DecimalFormat;

public class OrderCodeIntercept {

    /**
     * 从订单号头部开始截取,保留后面31位
     *
     * @return
     */
    public static String reserveThirtyOne(String orderCode) {
        if (StringGeneralUtil.checkNotNull(orderCode)&&orderCode.length()>31){
            return orderCode.substring(orderCode.length()-31);
        }else {
            return orderCode;
        }
    }

    public static void main(String[] args) {
        System.out.println(reserveThirtyOne("SO0000220230407240731680832804115").length());
    }

}
