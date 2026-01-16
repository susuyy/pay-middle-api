package com.ht.feignapi.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: Liwg
 * @Date: 2020/9/22 17:45
 */
public class GetErrorInfoFromException {
    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String s = sw.toString();
            sw.close();
            pw.close();
            return "\r\n" + s + "\r\n";
        } catch (Exception ex) {
            return "获得Exception信息的工具类异常";
        }
    }
}
