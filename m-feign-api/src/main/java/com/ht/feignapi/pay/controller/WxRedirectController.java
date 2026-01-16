package com.ht.feignapi.pay.controller;

import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.pay.client.PayProjectClient;
import com.ht.feignapi.pay.service.WeChatService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfig;
import com.ht.feignapi.tonglian.user.entity.WXData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/tonglian/wxRedirect")
@CrossOrigin(allowCredentials = "true")
public class WxRedirectController {


    private Logger logger = LoggerFactory.getLogger(WxRedirectController.class);

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private PayProjectClient payProjectClient;

    @Autowired
    private MerchantsClientService merchantsClientService;


    /**
     * 用户扫二维码请求
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping("/getWXCode/{merchantCode}")
    public void getWXCode(HttpServletRequest request, HttpServletResponse response, @PathVariable("merchantCode") String merchantCode) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        try {
            String ua = request.getHeader("User-Agent");
            logger.info(ua);
            boolean wxFlag = ua.contains("MicroMessenger");
            Merchants merchant = merchantsClientService.getMerchantByCode(merchantCode).getData();
            if (wxFlag) {
                String uri = URLEncoder.encode("https://allinpay.hualta.com/m-feign-api/tonglian/wxRedirect/getOpenidToPay/" + merchantCode+"/"+merchant.getBusinessSubjects(), "UTF-8");//回调地址，获取code
//            String uri= URLEncoder.encode("https://allinpay-web-v2.hualta.com/pay/getOpenidToPay/"+merchantCode, "UTF-8");//回调地址，获取code 测试用
                String wxAppId = merchantsConfigClientService.getConfigByKey(merchant.getBusinessSubjects(), "WX_APPID").getData();
                logger.info("wxAppId为=============" + wxAppId);
                StringBuffer url = new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?redirect_uri=" + uri +
                        "&appid=" + wxAppId + "&response_type=code&scope=snsapi_base&state=1#wechat_redirect");//替换appid
                response.sendRedirect(url.toString());//这里请不要使用get请求单纯的将页面跳转到该url即可
            } else {
                String payDataMerchantCode;
                if (MerchantChargeTypeConstant.CHARGE_BY_ENTITY.equals(merchant.getChargeType())){
                    payDataMerchantCode = merchant.getBusinessSubjects();
                }else {
                    payDataMerchantCode = merchant.getMerchantCode();
                }
                String c = payProjectClient.queryC(payDataMerchantCode).getData();
                response.sendRedirect("https://syb.allinpay.com/op?c=" + c);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 微信跳转根据code获取openid
     *
     * @param request
     * @param response
     */
    @GetMapping("/getOpenidToPay/{merchantCode}/{objMerchantCode}")
    public void getOpenidToPay(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable("merchantCode") String merchantCode,
                               @PathVariable("objMerchantCode") String objMerchantCode) throws IOException {
        String code = request.getParameter("code");
        logger.info("code为" + code);
        String wxAppId = "";
        String wxAppSecret = "";
        List<MerchantsConfig> merchantsConfigList = merchantsConfigClientService.getListByGroupCode(objMerchantCode, "get_openid_data").getData();
        for (MerchantsConfig merchantsConfig : merchantsConfigList) {
            if ("WX_APPID".equals(merchantsConfig.getKey())) {
                wxAppId = merchantsConfig.getValue();
            }
            if ("WX_APPSECRET".equals(merchantsConfig.getKey())) {
                wxAppSecret = merchantsConfig.getValue();
            }
        }
        WXData wxData = weChatService.getOpenid(code, wxAppId, wxAppSecret);
        logger.info("获取openid的微信数据" + wxData);
        String openid = wxData.getOpenid();
        System.out.println("获取的openid为" + openid);
        logger.info("获取的openid为=============" + openid);
        response.sendRedirect("http://allinpay.hualta.com/pay.html?merchantCode=" + merchantCode + "&openid=" + openid);
//        response.sendRedirect("http://allinpay-web-v2.hualta.com/pay.html?merchantCode="+merchantCode+"&openid="+openid);  测试用
    }

    /**
     * 测试判断请求的浏览器信息
     * @param request
     * @return
     */
    @GetMapping("/testUa")
    public String testUa(HttpServletRequest request){
        String ua = request.getHeader("User-Agent");
        logger.info(ua);
        boolean microMessenger = ua.contains("MicroMessenger");
        logger.info(microMessenger+"");
        return ua;
    }
}
