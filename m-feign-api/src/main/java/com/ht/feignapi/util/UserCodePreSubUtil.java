package com.ht.feignapi.util;

public class UserCodePreSubUtil {

    /**
     * 截取 前两位标识码 返回用户标识
     * @return
     */
    public static String userCodePreSubStr(String userFlagCode){
        return userFlagCode.substring(2);
    }

    public static void main(String[] args) {
        System.out.println(userCodePreSubStr("TLo7VW06OgWFhiQOVZJ3KyUq32nsak123"));
        System.out.println("o7VW06OgWFhiQOVZJ3KyUq32nsak".equals(userCodePreSubStr("TLo7VW06OgWFhiQOVZJ3KyUq32nsak")));
    }
}
