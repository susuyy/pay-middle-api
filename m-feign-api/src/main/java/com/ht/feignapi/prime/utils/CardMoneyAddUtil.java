package com.ht.feignapi.prime.utils;


import com.ht.feignapi.prime.cardconstant.TongLianCardConfig;
import com.ht.feignapi.tonglian.utils.DESUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CardMoneyAddUtil {

    public static String cardMoneyAdd(String cardId,Integer amountData,String prdtNo) throws IOException {
        HttpClient client = new HttpClient();

        String url = TongLianCardConfig.URL_TEST;;  //请求地址
        String format1 = TongLianCardConfig.FORMAT;  //系统参数 格式
        String sign_method1 = TongLianCardConfig.SIGN_METHOD; //系统参数 签名方法
        String method1 = TongLianCardConfig.CARD_SINGLE_TOP_UP_ADD; //系统参数 接口名称
        String appKey = TongLianCardConfig.APP_KEY; //系统参数
        String password1 = TongLianCardConfig.PASSWORD;          //业务参数  密码
        String timestamp1 = DESUtil.date2StryyyyMMddHHmmss(new Date());  //系统参数  时间戳
        String v1 = TongLianCardConfig.VERSION;    //版本号
        String appSecrete = TongLianCardConfig.APP_SECRETE;        //请求密钥
        String dataSecret = TongLianCardConfig.DATA_SECRET;  //数据密钥

        String card_id1 = cardId; //业务参数   卡号
        String orderId = String.valueOf(System.currentTimeMillis());
        System.out.println(orderId);
        String order_id1 = orderId;
        String brh_id = TongLianCardConfig.BRH_ID;
        String brand_no = TongLianCardConfig.BRAND_NO;
        String chan_no = TongLianCardConfig.CHAN_NO;
        String prdt_no = prdtNo;
        String amount = amountData+"";
        String top_up_way = TongLianCardConfig.TOP_UP_WAY;

        //处理密码，加密             三个步骤    1 - 拼接字符串  时间戳+aop+密码      2 - DES加密      3 - BASE64转码
        password1 = (new StringBuilder(timestamp1)).append("aop").append(password1).toString();
        byte[] result = DESUtil.desCrypto(password1.getBytes(), dataSecret);
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

        treeMap.put("order_id", order_id1);
        treeMap.put("brh_id", brh_id);
        treeMap.put("card_id", card_id1);
        treeMap.put("brand_no", brand_no);
        treeMap.put("prdt_no", prdt_no);
        treeMap.put("amount", amount);
        treeMap.put("chan_no", chan_no);
        treeMap.put("top_up_way", top_up_way);
        treeMap.put("password", password1);

        System.out.println(treeMap);

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

        System.out.println(mac);

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

        arrayList.add(new NameValuePair("order_id", order_id1));
        arrayList.add(new NameValuePair("brh_id", brh_id));
        arrayList.add(new NameValuePair("card_id", card_id1));
        arrayList.add(new NameValuePair("brand_no", brand_no));
        arrayList.add(new NameValuePair("prdt_no", prdt_no));
        arrayList.add(new NameValuePair("amount", amount));
        arrayList.add(new NameValuePair("chan_no", chan_no));
        arrayList.add(new NameValuePair("top_up_way", top_up_way));
        arrayList.add(new NameValuePair("password", password1));
        NameValuePair[] nameValuePairArray = arrayList.toArray(new NameValuePair[arrayList.size()]);

        post.setRequestBody(nameValuePairArray);
        int status = client.executeMethod(post);
        System.out.println(status);
        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();
        return post.getResponseBodyAsString();
    }
}
