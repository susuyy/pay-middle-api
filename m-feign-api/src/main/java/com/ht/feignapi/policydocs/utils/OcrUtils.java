package com.ht.feignapi.policydocs.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * <p>
 * </p>
 *
 * @author hy.wang
 * @since 21/1/7
 */
public class OcrUtils {



    public static String getImagesDocs(String base64){

        JSONObject jsonParam = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(base64);
        jsonParam.put("images",jsonArray);
        String result = HttpRequestUtil.HttpPostWithJson("http://153.0.175.3:8087/predict/ocr_system", jsonParam.toJSONString());

        JSONObject resultJson = JSONObject.parseObject(result);
        JSONArray resultsArray = resultJson.getJSONArray("results");
        JSONArray subResultsArray = resultsArray.getJSONArray(0);
        StringBuffer resultSB = new StringBuffer();
        for (int i = 0; i <subResultsArray.size() ; i++) {
            JSONObject subResultJson = subResultsArray.getJSONObject(i);
            String text = subResultJson.getString("text");
            resultSB.append(text);
        }
        return resultSB.toString();
    }

}
