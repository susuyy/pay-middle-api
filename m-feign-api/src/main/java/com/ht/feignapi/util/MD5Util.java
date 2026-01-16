package com.ht.feignapi.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * 计算MD5工具类
 */
public class MD5Util {
	static MessageDigest MD5 = null;
	/**
	 * 对一个文件获取md5值
	 * @return md5串
	 */
	public static String getMD5(File file) {
		FileInputStream fileInputStream = null;
		if(!file.exists()||file.isDirectory()||!file.canRead()){
			return "";
		}
		try {
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192*10];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(MD5.digest()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 求一个字符串的md5值
	 * @param target 字符串
	 * @return md5 value
	 */
	public static String MD5(String target) {
		return DigestUtils.md5Hex(target);
	}

	public static void main(String[] args) throws Exception {
		String test1= getMD5("123qweQWE");
		System.out.println(test1);
	}

	public static String getMD5(String s) throws Exception{
        if(s == null) {
			return "";
		}
        else {
			return getMD5(s.getBytes("utf-8"));
		}
	}

	public static String getMD5(byte abyte0[]){
        String s = null;
        char ac[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
            'a', 'b', 'c', 'd', 'e', 'f'
        };
        try{
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(abyte0);
            byte abyte1[] = messagedigest.digest();
            char ac1[] = new char[32];
            int i = 0;
            for(int j = 0; j < 16; j++){
                byte byte0 = abyte1[j];
                ac1[i++] = ac[byte0 >>> 4 & 15];
                ac1[i++] = ac[byte0 & 15];
            }

            s = new String(ac1);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return s;
    }
	

}
