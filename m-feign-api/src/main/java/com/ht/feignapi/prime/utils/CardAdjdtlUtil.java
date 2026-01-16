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
 * 调取通联账户余额调整
 */
public class CardAdjdtlUtil {
    public static String cardAdjdt(String cardId,String prdtNo,String txnAt,String adjDirect) throws HttpException, IOException {
        HttpClient client = new HttpClient();
        String url = TongLianCardConfig.URL_TEST;  //请求地址
        String format1 = TongLianCardConfig.FORMAT;  //系统参数 格式
        String sign_method1 = TongLianCardConfig.SIGN_METHOD; //系统参数 签名方法
        String method1 = TongLianCardConfig.ADJDTL_METHOD; //系统参数 接口名称
        String appKey = TongLianCardConfig.APP_KEY; //系统参数
        String timestamp1 = DESUtil.date2StryyyyMMddHHmmss(new Date());  //系统参数  时间戳
        String v1 = TongLianCardConfig.VERSION;    //版本号
        String appSecrete = TongLianCardConfig.APP_SECRETE;        //请求密钥
        String dataSecret = TongLianCardConfig.DATA_SECRET;  //数据密钥

        String orderId = String.valueOf(System.currentTimeMillis());
        System.out.println(orderId);
        String order_id = orderId;
        String brh_id = TongLianCardConfig.BRH_ID;
        String card_id = "8661086160004624828";
        String prdt_no = prdtNo;
        String txn_at = txnAt;
        String adj_direct = adjDirect;
        String adj_reason = "hualiantian";


        Map<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("app_key", appKey);
        treeMap.put("format", format1);
        treeMap.put("sign_method", sign_method1);
        treeMap.put("method", method1);
        treeMap.put("sign_v", "1");
        treeMap.put("timestamp", timestamp1);
        treeMap.put("v", v1);

        treeMap.put("order_id", order_id);
        treeMap.put("brh_id", brh_id);
        treeMap.put("card_id", "8661086160004624828");
        treeMap.put("prdt_no", prdt_no);
        treeMap.put("txn_at", txn_at);
        treeMap.put("adj_direct", adj_direct);
        treeMap.put("adj_reason", adj_reason);

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

        arrayList.add(new NameValuePair("order_id", order_id));
        arrayList.add(new NameValuePair("brh_id", brh_id));
        arrayList.add(new NameValuePair("card_id", "8661086160004624828"));
        arrayList.add(new NameValuePair("prdt_no", prdt_no));
        arrayList.add(new NameValuePair("txn_at", txn_at));
        arrayList.add(new NameValuePair("adj_direct", adj_direct));
        arrayList.add(new NameValuePair("adj_reason", adj_reason));

        NameValuePair[] nameValuePairArray = arrayList.toArray(new NameValuePair[arrayList.size()]);

        post.setRequestBody(nameValuePairArray);
        int status = client.executeMethod(post);
        System.out.println(status);
        System.out.println(post.getResponseBodyAsString());
        post.releaseConnection();
        return post.getResponseBodyAsString();
    }


}
