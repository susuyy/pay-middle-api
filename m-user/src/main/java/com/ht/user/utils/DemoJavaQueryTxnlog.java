package com.ht.user.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DemoJavaQueryTxnlog {
    public static void main(String[] args) throws HttpException, IOException {
        HttpClient client = new HttpClient();

        String url = "http://116.228.64.55:8080/aop/rest";  //请求地址
        String format1 = "json";  //系统参数 格式
        String sign_method1 = "MD5"; //系统参数 签名方法
        String method1 = "allinpay.ppcs.txnlog.search"; //系统参数 接口名称
        String appKey = "test"; //系统参数
        String password1 = "111111";          //业务参数  密码
        String timestamp1 = date2StryyyyMMddHHmmss(new Date());  //系统参数  时间戳
        String v1 = "1.0";    //版本号
        String appSecrete = "test";        //请求密钥
        String dataSecret = "abcdefgh";  //数据密钥

        String card_id1 = "8661086160004183119"; //业务参数   卡号
        String begin_date = "20200510";
        String end_date = "20200617";
        String page_no = "1";
        String page_size = "10";

        //处理密码，加密             三个步骤    1 - 拼接字符串  时间戳+aop+密码      2 - DES加密      3 - BASE64转码
        password1 = (new StringBuilder(timestamp1)).append("aop").append(password1).toString();
        byte[] result = desCrypto(password1.getBytes(), dataSecret);
        BASE64Encoder encode = new BASE64Encoder();
        password1 = encode.encode(result);

        Map<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("app_key", appKey);
        treeMap.put("format", format1);
        treeMap.put("method", method1);
        treeMap.put("sign_method", sign_method1);
        treeMap.put("sign_v", "1");
        treeMap.put("timestamp", timestamp1);
        treeMap.put("v", v1);

        treeMap.put("card_id", card_id1);
        treeMap.put("begin_date", begin_date);
        treeMap.put("end_date", end_date);
        treeMap.put("page_no", page_no);
        treeMap.put("page_size", page_size);

        //对signSrc中拼接的报文进行字段的首字母排序
        StringBuilder appSort = new StringBuilder();
        Set<String> keySet = treeMap.keySet();
        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            appSort.append(key).append(treeMap.get(key));
        }

        //生成签名  规则：所有参数按照字母顺序以 参数名参数值 依次拼接；前后各自再拼接上请求密钥 ；md5加密
        String signSrc = (new StringBuilder(appSecrete))
                .append(appSort)
                .append(appSecrete)
                .toString();
        System.out.println(signSrc);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte md5Bytes[] = md5.digest(signSrc.getBytes("UTF-8"));//支持中文
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = md5Bytes[i] & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        String mac = hexValue.toString().toUpperCase();

        //发送请求
        PostMethod post = new PostMethod(url);

        List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(new NameValuePair("app_key", appKey));
        arrayList.add(new NameValuePair("format", format1));
        arrayList.add(new NameValuePair("sign_method", sign_method1));
        arrayList.add(new NameValuePair("method", method1));
        arrayList.add(new NameValuePair("sign", mac));
        arrayList.add(new NameValuePair("sign_v", "1"));
        arrayList.add(new NameValuePair("timestamp", timestamp1));
        arrayList.add(new NameValuePair("v", v1));


        arrayList.add(new NameValuePair("card_id", card_id1));
        arrayList.add(new NameValuePair("begin_date", begin_date));
        arrayList.add(new NameValuePair("end_date", end_date));
        arrayList.add(new NameValuePair("page_no", page_no));
        arrayList.add(new NameValuePair("page_size", page_size));
        NameValuePair[] nameValuePairArray = arrayList.toArray(new NameValuePair[arrayList.size()]);

        post.setRequestBody(nameValuePairArray);
        int status = client.executeMethod(post);
        System.out.println(status);
        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();
    }

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
