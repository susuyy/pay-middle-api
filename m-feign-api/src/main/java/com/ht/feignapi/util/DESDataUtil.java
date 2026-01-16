package com.ht.feignapi.util;

import com.ht.feignapi.config.DESConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Base64;


public class DESDataUtil {

    /**
     * 密钥算法
     */
    private static String ALGORITHM = "DES";

    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    /**
     * 默认编码
     */
    private static String CHARSET = "utf-8";


    /**
     * des加密秘钥 不能小于8位字符
     */
    private static String password = "hlms-hlta-password-0210112";

    /**
     * des偏移向量 8位字符
     */
    private static String ivParameter = "5cd%01&x";


    /**
     * 生成key
     * @return
     * @throws Exception
     */
    private static Key generateKey() throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }


    /**
     * DES加密字符串
     *
     * @param data 待加密字符串
     * @return 加密后内容
     */
    public static String encrypt( String data) {
        if (password== null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));

            //JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
            //Android平台可以使用android.util.Base64
            return new String(Base64.getEncoder().encode(bytes));

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES解密字符串
     *
     * @param data 待解密字符串
     * @return 解密后内容
     */
    public static String decrypt(String data) {
        if (password== null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(CHARSET))), CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    public static void main(String[] args) {
        String string = "{\"userFlagCode\":\"oA5C81PzM0hx7VI6jIGzrYJmCw0w1234\",\"amount\":\"11500\",\"merchantCode\":\"HLSC\",\"orderCode\":\"1291210891232931841\"}";
        String encrypt = encrypt(string);
        System.out.println("加密后的数据字符串:" + encrypt);
        String decrypt = decrypt(encrypt);
        System.out.println("解密后的数据字符串" + decrypt);
    }
}
