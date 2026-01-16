package com.ht.feignapi.prime.utils;



import com.ht.feignapi.prime.cardconstant.TongLianCardConfig;
import com.ht.feignapi.tonglian.utils.DESUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 调取通联单卡激活
 */
public class CardSingLeActiveUtil {

    public static String cardSingLeActive(String cardId) throws HttpException, IOException {
        HttpClient client = new HttpClient();
        String url = TongLianCardConfig.URL_TEST;  //请求地址
        String format1 = TongLianCardConfig.FORMAT;  //系统参数 格式
        String sign_method1 = TongLianCardConfig.SIGN_METHOD; //系统参数 签名方法
        String method1 = TongLianCardConfig.CARD_SINGLE_TOP_UP_ADD; //系统参数 接口名称
        String appKey = TongLianCardConfig.APP_KEY; //系统参数
        String timestamp1 = DESUtil.date2StryyyyMMddHHmmss(new Date());  //系统参数  时间戳
        String v1 = TongLianCardConfig.VERSION;    //版本号
        String appSecrete = TongLianCardConfig.APP_SECRETE;        //请求密钥
        String dataSecret = TongLianCardConfig.DATA_SECRET;  //数据密钥

        String order_id = String.valueOf(System.currentTimeMillis());
        String card_id = cardId;
        String type = "5";


        Map<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("app_key", appKey);
        treeMap.put("format", format1);
        treeMap.put("method", method1);
        treeMap.put("sign_method", sign_method1);
        treeMap.put("sign_v", "1");
        treeMap.put("timestamp", timestamp1);
        treeMap.put("v", v1);

        treeMap.put("order_id", order_id);
        treeMap.put("card_id", card_id);
        treeMap.put("type", type);

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

        arrayList.add(new NameValuePair("order_id", order_id));
        arrayList.add(new NameValuePair("card_id", card_id));
        arrayList.add(new NameValuePair("type", type));
        System.out.println(arrayList);
        NameValuePair[] nameValuePairArray = arrayList.toArray(new NameValuePair[arrayList.size()]);
        System.out.println(nameValuePairArray);
        post.setRequestBody(nameValuePairArray);
        int status = client.executeMethod(post);
        System.out.println(status);
        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();
        return post.getResponseBodyAsString();
    }


}
