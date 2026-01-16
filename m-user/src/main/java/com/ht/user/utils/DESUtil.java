package com.ht.user.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 调取通联 加密工具类
 */
public class DESUtil {

    /* DES加密
     * datasource:待加密的值
     * password： 数据密钥
     */
    public static byte[] desCrypto(byte[] datasource, String password) {
        try {
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // 偏移量
            IvParameterSpec iv = new IvParameterSpec(password.getBytes());
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, iv);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String date2StryyyyMMddHHmmss(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
        return ft.format(date);
    }
}
