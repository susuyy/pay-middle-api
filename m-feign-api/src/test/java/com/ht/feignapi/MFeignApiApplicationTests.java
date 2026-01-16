package com.ht.feignapi;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.entity.RetRefundOrderData;
import com.ht.feignapi.posapi.controller.PosLoginController;
import com.ht.feignapi.posapi.controller.PosScanController;
import com.ht.feignapi.posapi.entity.FinishInterestSpecData;
import com.ht.feignapi.prime.entity.UploadOrderDetails;
import com.ht.feignapi.prime.service.PrimePayService;
import com.ht.feignapi.prime.utils.*;
import com.ht.feignapi.tonglian.admin.entity.LoginVo;
import com.ht.feignapi.tonglian.card.clientservice.CardProfilesClientService;
import com.ht.feignapi.tonglian.card.entity.CardProfiles;
import com.ht.feignapi.tonglian.card.service.CardLimitsService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.config.CardOrdersTypeConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.MisOrder;
import com.ht.feignapi.tonglian.order.service.MisOrderService;
import com.ht.feignapi.tonglian.user.controller.UserUsersController;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.util.DESUtil;
import com.ht.feignapi.util.DateStrUtil;
import com.ht.feignapi.util.PosMD5;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MFeignApiApplicationTests {

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Test
    public void queryCardNumTest(){
        Integer integer = cardUserService.queryCardNum(43L, "HLTA_001");
        System.out.println(integer);
    }


    @Test
    public void testInvalid() throws ParseException {
//        2020-09-02 17:15:47
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CardCards cardCards = new CardCards();
        cardCards.setPeriodOfValidity(2160);
        cardCards.setValidGapAfterApplied(24);
        cardCards.setValidityType("validDuration");
        Boolean aBoolean = cardUserService.checkUserCardInvalid(cardCards,simpleDateFormat.parse("2020-09-02 17:15:47"));
        System.out.println(aBoolean);
    }

    @Autowired
    private CardProfilesClientService cardProfilesClientService;

    @Test
    public void testCardProfiles() throws ParseException {
        List<CardProfiles> cardProfilesList= cardProfilesClientService.queryByCardCode("1301416259502215169").getData();
        System.out.println(cardProfilesList);
    }


    @Test
    public void testCardNum() throws ParseException {
        Integer integer= cardUserService.queryCardNum(114L, "KTYKT");
        System.out.println(integer);
        double v = 5 * 0.01;
        System.out.println(v);
    }


    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void queryMallPro() throws ParseException {
//        List<MallProductions> mallProductionsList = mallAppShowClientService.queryByCodeAndMerchant("1307890407358685186", "NCD").getData();
//        System.out.println(mallProductionsList);
        stringRedisTemplate.opsForValue().set("111","222",3, TimeUnit.MINUTES);
        String testStr =(String) stringRedisTemplate.opsForValue().get("111");
        System.out.println(testStr);
    }


    @Test
    public void testOpenCard() throws IOException {
        String content = OpenCardUtil.callOpenCard("15682059563");
        System.out.println(content);
    }

    @Test
    public void testBindCardPrdt() throws IOException {
        String content = BindProductCardUtil.bindproduct("8661086160004624828", "0001");
        System.out.println(content);
    }

    @Test
    public void testQueryCardInfo() throws Exception {
        String content = QueryCardInfoUtil.queryCardInfo("8661086160004627927");
        System.out.println(content);
    }

    @Test
    public void testCardMoneyPay() throws Exception {
        String content = CardMoneyPayUtil.CardMoneyPay("10", "8661086160004627927", IdWorker.getIdStr());

//        String test = CardMoneyPayProdUtil.cardMoneyPay("1", "8661086160004627927", IdWorker.getIdStr(),"0000000002", "test", "111111");

        System.out.println(content);
    }

    @Test
    public void testCardMoneyAdd() throws Exception {
        String content = CardMoneyAddUtil.cardMoneyAdd("8661086160004627927", 100, "0001");
        System.out.println(content);
    }


    @Test
    public void testAdjdt() throws Exception {
        String content = CardAdjdtlUtil.cardAdjdt("8661086160004624828","0001", "100", "0");
        System.out.println(content);
    }


    @Autowired
    private MerchantsClientService merchantsClientService;

    @Test
    public void testRetUrl() throws Exception {
        Merchants merchants = merchantsClientService.getMerchantByCode("KTYKT").getData();
        System.out.println(merchants);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void posMisPay() throws Exception {
        JSONObject custDataJson = new JSONObject();
        custDataJson.put("BUSINESS_ID", "100000003");
        custDataJson.put("AMOUNT", "000000000010");
        custDataJson.put("ORDER_NO", "2222222222");
        custDataJson.put("ORIG_TRACE_NO", "000800");

        JSONObject json = new JSONObject();
        json.put("BUSINESS_ID", "100000003");
        json.put("CUST_DATA", JSONObject.toJSONString(custDataJson));
        json.put("CASH_ID", "HT000085");
        json.put("STORE_ID", "0001");
        json.put("APP_ID", "22edd04a19dd4bae815f20a9ab052adf");
        json.put("SIGN_DATA", "3cff892726d44615a6668701a0a06ad4");
        json.put("APP_PACKAGE_NM", "");
        json.put("APP_CLASS_NM", "");

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpEntity<String> formEntity = new HttpEntity<String>(json.toString(), headers);
        String content= restTemplate.postForEntity("https://cloudmiscash.allinpay.com/cloudMis-transaction/api/v2/process",formEntity,String.class).getBody();
        System.out.println(content);
    }

    @Value("${payRedirect}")
    private String payRedirectUrl;

    @Test
    public void testReadPayRedirectUrl() throws Exception {
        System.out.println(payRedirectUrl);
    }

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private OrderClientService orderClientService;

    @Test
    public void testBindPhoneNum() throws Exception {
        List<CardOrderPayTrace> orderPayTraceList = orderClientService.checkHaveOrder("HT000091").getData();
        System.out.println(orderPayTraceList);
    }


    @Autowired
    private MisOrderService misOrderService;

    @Test
    public void testMD5Sign() throws Exception {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("BUSINESS_ID", "100000003");
        linkedHashMap.put("AMOUNT", "00000000303");
        String orderNo = IdWorker.getIdStr();
        System.out.println(orderNo);
        linkedHashMap.put("ORDER_NO", orderNo);
        linkedHashMap.put("ORIG_TRACE_NO", "000800");

        TreeMap json = new TreeMap();
        json.put("BUSINESS_ID", "100000003");
        JSONObject jsonObject = new JSONObject(true);
        System.out.println(jsonObject.toJSONString(linkedHashMap));
        json.put("CUST_DATA", jsonObject.toJSONString(linkedHashMap));
        json.put("CASH_ID", "HT000090");
        json.put("STORE_ID", "0001");
        json.put("APP_ID", "22edd04a19dd4bae815f20a9ab052adf");
        json.put("APP_PACKAGE_NM", "com.app");
        json.put("APP_CLASS_NM", "com.app.test");
        String md5 = PosMD5.unionSign(json);
        json.put("SIGN_DATA", md5);
        System.out.println(md5);

        MisOrder misOrder = new MisOrder();
        misOrder.setBUSINESS_ID("100000003");
        misOrder.setCUST_DATA(jsonObject.toJSONString(linkedHashMap));
        misOrder.setCASH_ID("HT000090");
        misOrder.setSTORE_ID("0001");
        misOrder.setAPP_ID("22edd04a19dd4bae815f20a9ab052adf");
        misOrder.setSIGN_DATA(md5);
        misOrder.setAPP_PACKAGE_NM("com.app");
        misOrder.setAPP_CLASS_NM("com.app.test");
        System.out.println(misOrder);

        boolean checkFlag = misOrderService.checkMD5Sign(misOrder);
        System.out.println(checkFlag);
    }

    @Autowired
    private UserUsersController userUsersController;

    @Test
    public void testRedisHash() throws Exception {
//        redisTemplate.opsForHash().put("testHash","one","one");
//        redisTemplate.opsForHash().put("testHash","two","two");
//        redisTemplate.opsForHash().put("testHash","three","three");
//        redisTemplate.opsForHash().put("testHash","four","four");

//        redisTemplate.opsForHash().put("testHash","five","5555555555");
//
//        Object o = redisTemplate.opsForHash().get("testHash", "five");
//        System.out.println(o);

//        String code = userUsersController.getUserQrCodeByOpenid("oA5C81PzM0hx7VI6jIGzrYJmCw0w");
//        System.out.println(code);
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse("2021-02-18 11:05:13");
        long minutes = ChronoUnit.MINUTES.between(Instant.ofEpochMilli(parse.getTime()), Instant.ofEpochMilli(new Date().getTime()));
        System.out.println(minutes);
    }


    @Autowired
    private DESUtil desUtil;

    @Test
    public void testDES() throws Exception {
//        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("userFlagCode","TLo7VW06OgWFhiQOVZJ3KyUq32nsak342");
//        objectObjectHashMap.put("amount","70000");
//        objectObjectHashMap.put("orderCode","1852121774411077858");
//        objectObjectHashMap.put("merchantCode","HLSC");
//        objectObjectHashMap.put("payCode","1852121774411077858");
//        objectObjectHashMap.put("storeCode","18521");
//        objectObjectHashMap.put("actualPhone","15682059552");
//        objectObjectHashMap.put("idCardNo","462022155874112563");
//
//        List<UploadOrderDetails> list = new ArrayList<>();
//        UploadOrderDetails uploadOrderDetails = new UploadOrderDetails();
//        uploadOrderDetails.setGoodsGroupCode("1232344");
//        uploadOrderDetails.setCategoryCode("12312344");
//        uploadOrderDetails.setBrandCode("123123");
//        uploadOrderDetails.setGoodsCode("234234");
//        uploadOrderDetails.setGoodsName("测试商品");
//        uploadOrderDetails.setGoodsCount("12");
//        uploadOrderDetails.setGoodsPrice("100");
//        uploadOrderDetails.setGoodsDiscount("10");
//        uploadOrderDetails.setGoodsPayPrice("90");
//        uploadOrderDetails.setGoodsActivityType("1010");
//
//        UploadOrderDetails uploadOrderDetailsTwo = new UploadOrderDetails();
//        uploadOrderDetailsTwo.setGoodsGroupCode("1232344");
//        uploadOrderDetailsTwo.setCategoryCode("12312344");
//        uploadOrderDetailsTwo.setBrandCode("123123");
//        uploadOrderDetailsTwo.setGoodsCode("234234");
//        uploadOrderDetailsTwo.setGoodsName("测试商品");
//        uploadOrderDetailsTwo.setGoodsCount("12");
//        uploadOrderDetailsTwo.setGoodsPrice("100");
//        uploadOrderDetailsTwo.setGoodsDiscount("10");
//        uploadOrderDetailsTwo.setGoodsPayPrice("90");
//        uploadOrderDetailsTwo.setGoodsActivityType("1010");
//
//        list.add(uploadOrderDetails);
//        list.add(uploadOrderDetailsTwo);
//
//        objectObjectHashMap.put("orderDetail",list);
//        String s = JSONObject.toJSONString(objectObjectHashMap);
//        System.out.println(s);

//        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("orderCode","12999999999999999906");
//        objectObjectHashMap.put("operator","msadmin");
//        objectObjectHashMap.put("merchantCode","HLSC");
//        objectObjectHashMap.put("cancelCode","12999999999999999906");
////        objectObjectHashMap.put("refundAmount","12010");
//        String s = JSONObject.toJSONString(objectObjectHashMap);
//        System.out.println(s);
////
//        String encrypt = desUtil.encrypt(s);
//        System.out.println(encrypt);
//        String decrypt = desUtil.decrypt("tOzfQz9PCdKXcj1e0fJcnv4kwprYpk/dqQR4n8cdMJ7n81pOnsk8TAr4kANPHQENquvKZFqFyDt0KzWYzLZYXAqfvOu8vvr5L/yHqdrr/Kz/do9vRkxIJg==");
//        System.out.println(decrypt);
//        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("userFlagCode","TLo7VW06OgWFhiQOVZJ3KyUq32nsak");
//        objectObjectHashMap.put("amount","100000");
//        objectObjectHashMap.put("orderCode","12912108912329555642111");
//        objectObjectHashMap.put("merchantCode","HLSC");
//        objectObjectHashMap.put("payCode","123456789412366042111");
//        String s = JSONObject.toJSONString(objectObjectHashMap);
//        System.out.println(s);

//        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("orderCode","6001200520210202105456000001");
//        objectObjectHashMap.put("operator","admin");
//        String s = JSONObject.toJSONString(objectObjectHashMap);
//        System.out.println(s);

//        String encrypt = desUtil.encrypt(s);
//        System.out.println(encrypt);
        String decrypt = desUtil.decrypt("tOzfQz9PCdKXcj1e0fJcnsUP4+B6LodFV/EwcOZj9J0XdsFlAjS1u7xr3253qQq9bBCAh6BJ74cQGRJh4lx25rWG/aQvSKahaRTuznIeYPLg6tILc28JJBPwUoEsi6JG");
        System.out.println(decrypt);
//        System.out.println("ELECARD1352520386185379851".length());
    }

    @Test
    public void testDESOrder() throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("businessId","000050");
        objectObjectHashMap.put("amount","10000");
        objectObjectHashMap.put("orderCode","13666728123249888881");
        objectObjectHashMap.put("merchantCode","HLSC");
        objectObjectHashMap.put("payCode","13666728123249888881");
        objectObjectHashMap.put("cashId","HT000090");
        objectObjectHashMap.put("storeId","HLSC");
        objectObjectHashMap.put("memo","备注");
        objectObjectHashMap.put("storeCode","18521");
        objectObjectHashMap.put("actualPhone","15682059552");
        objectObjectHashMap.put("idCardNo","462022155874112563");

        List<UploadOrderDetails> list = new ArrayList<>();
        UploadOrderDetails uploadOrderDetails = new UploadOrderDetails();
        uploadOrderDetails.setGoodsGroupCode("1232344");
        uploadOrderDetails.setCategoryCode("12312344");
        uploadOrderDetails.setBrandCode("123123");
        uploadOrderDetails.setGoodsCode("234234");
        uploadOrderDetails.setGoodsName("测试商品");
        uploadOrderDetails.setGoodsCount("12");
        uploadOrderDetails.setGoodsPrice("100");
        uploadOrderDetails.setGoodsDiscount("10");
        uploadOrderDetails.setGoodsPayPrice("90");
        uploadOrderDetails.setGoodsActivityType("1010");

        UploadOrderDetails uploadOrderDetailsTwo = new UploadOrderDetails();
        uploadOrderDetailsTwo.setGoodsGroupCode("1232344");
        uploadOrderDetailsTwo.setCategoryCode("12312344");
        uploadOrderDetailsTwo.setBrandCode("123123");
        uploadOrderDetailsTwo.setGoodsCode("234234");
        uploadOrderDetailsTwo.setGoodsName("测试商品");
        uploadOrderDetailsTwo.setGoodsCount("12");
        uploadOrderDetailsTwo.setGoodsPrice("100");
        uploadOrderDetailsTwo.setGoodsDiscount("10");
        uploadOrderDetailsTwo.setGoodsPayPrice("90");
        uploadOrderDetailsTwo.setGoodsActivityType("1010");

        list.add(uploadOrderDetails);
        list.add(uploadOrderDetailsTwo);

        objectObjectHashMap.put("orderDetail",list);


        String s = JSONObject.toJSONString(objectObjectHashMap);
        System.out.println(s);

        String encrypt = desUtil.encrypt(s);
        System.out.println(encrypt);

//        String decrypt = desUtil.decrypt("LrMjjJA8t107PFWymi/FZZt7ZihAjldrt1TDIsOHjhi/2/Q0nLpHcUeQhmKp0VqMBEosMcutaLarGDWQ26Jp/g==");
//        System.out.println(decrypt);

//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date orderDate = simpleDateFormat.parse("2021-05-22 19:02:00");
//
//        long nowDate = new Date().getTime();
//        long orderTime = orderDate.getTime() + 53 * 1000;   //限制订单支付到期时间
//        int diffSeconds = (int) ((orderTime - nowDate) / 1000);
//        if (diffSeconds >=7 ){
//            System.out.println("可支付");
//        }else {
//            System.out.println("订单过期");
//        }
    }

    @Autowired
    PosLoginController posLoginController;

    @Autowired
    PosScanController posScanController;

    @Test
    public void testLogin() throws Exception {
//        LoginVo loginVo = new LoginVo();
//        loginVo.setUserName("fhd");
//        loginVo.setPassword("fhdadmin");
//        loginVo.setMerchantCode("FHD");
//        loginVo.setSystemCode("HIGO");
//
//
//        Object o = posLoginController.loginSystem(loginVo);
//        System.out.println(o);

        FinishInterestSpecData finishInterestSpecData = new FinishInterestSpecData();
        finishInterestSpecData.setAccount("fhd");
        finishInterestSpecData.setMerchantCode("FHD");
        finishInterestSpecData.setInterestsSpecNo("HIGOxlsh7tewus743208962");

        Object o = posScanController.finishQrCode(finishInterestSpecData);
        System.out.println(o);
    }

}
