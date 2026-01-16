package com.ht.feignapi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class PosMD5 {

    /**
     * md5
     *
     * @param b
     * @return
     */
    public static String md5(byte[] b) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(b);
            byte[] hash = md.digest();
            StringBuffer outStrBuf = new StringBuffer(32);
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                if (v < 16) {
                    outStrBuf.append('0');
                }
                outStrBuf.append(Integer.toString(v, 16).toLowerCase());
            }
            return outStrBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new String(b);
        }
    }

    public static String unionSign(TreeMap<String, String> params) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length() > 0) {
                sb.append(entry.getKey()).append("=").append(entry.getValue())
                        .append("&");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        String sign = "";
        System.out.println(sb.toString());
        sign = md5(sb.toString().getBytes("UTF-8"));// 记得是md5编码的加签
        params.remove("key");
        return sign;
    }


    public static boolean validSign(String md5Str,TreeMap<String, String> param) throws Exception {
        if (param != null && !param.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length() > 0) {
                    sb.append(entry.getKey()).append("=")
                            .append(entry.getValue()).append("&");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return md5Str.toLowerCase().equals(
                    md5(sb.toString().getBytes("UTF-8")).toLowerCase());
        }
        return false;
    }
}
