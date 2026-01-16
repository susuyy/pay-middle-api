package com.ht.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.card.entity.*;
import com.ht.user.card.service.*;
import com.ht.user.card.service.impl.CardOrdersServiceImpl;
import com.ht.user.card.vo.CardOrdersVO;
import com.ht.user.card.vo.UserCashCardPayOrderData;
import com.ht.user.config.TongLianCardState;

import com.ht.user.mall.controller.OrderOrdersController;
import com.ht.user.mall.entity.OrderOrders;
import com.ht.user.mall.service.OrderOrdersService;
import com.ht.user.mall.service.OrderShoppingCartService;
import com.ht.user.mall.service.impl.OrderOrdersServiceImpl;
import com.ht.user.mall.service.impl.OrderShoppingCartServiceImpl;
import com.ht.user.ordergoods.entity.UploadOrderDetails;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.entity.OutletsOrderCancelData;
import com.ht.user.outlets.entity.OutletsOrderRefTrace;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.service.IOutletsOrdersService;
import com.ht.user.outlets.util.CompanyPayWeight;
import com.ht.user.outlets.util.DESUtil;
import com.ht.user.utils.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class MUserApplicationTests {


    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

//    @Autowired
//    private UsrUsersMapper usrUsersMapper;

    /**
     * 开卡测试
     * @throws IOException
     */
    @Test
    public void openCard() throws IOException {
//        UsrUsers usrUsers = new UsrUsers();
//        usrUsers.setTel("15624895214");
//        usrUsersMapper.insert(usrUsers);
//        try {
//            String content = OpenCardUtil.callOpenCard("15624895214");
//            PpcsCloudCardOpenReturnData returnData = JSONObject.parseObject(content, PpcsCloudCardOpenReturnData.class);
//            PpcsCloudCardOpenResponse ppcsCloudCardOpenResponse = returnData.getPpcs_cloud_card_open_response();
//            if (0==ppcsCloudCardOpenResponse.getResult()) {
//                System.out.println("进来了");
//                cardMapUserCardsService.createCardMapUserCards(TongLianCardState.CARD_CODE.getCode()+"","15624895214", "HLTA_001",
//                        TongLianCardState.CARD_NAME.getDesc(),
//                        TongLianCardState.CATEGORY.getCode() + "",
//                        TongLianCardState.CATEGORY.getDesc(),
//                        TongLianCardState.STATE_NORMAL.getDesc(),
//                        TongLianCardState.TYPE.getDesc(),
//                        ppcsCloudCardOpenResponse.getCard_id());
//            }else {
//                System.out.println("开通过虚拟卡");
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
        String romCode = String.valueOf((long) ((Math.random() * 9 + 1) * 10000000000L));
        System.out.println(romCode);
        String content = OpenCardUtil.callOpenCard(romCode);
        System.out.println(content);
    }



    @Test
    public void queryUserMoney() throws IOException {
//        BigDecimal bigDecimal = cardMapUserCardsService.queryUserMoney(114L);
//        System.out.println(bigDecimal);
        try {
            String content = QueryCardInfoUtil.queryCardInfo("8661086160004627703");
            PpcsCardinfoGetReturnData ppcsCardinfoGetReturnData = JSONObject.parseObject(content, PpcsCardinfoGetReturnData.class);
            PpcsCardinfoGetResponse ppcsCardinfoGetResponse = ppcsCardinfoGetReturnData.getPpcsCardinfoGetResponse();
            CardInfo cardInfo = ppcsCardinfoGetResponse.getCardInfo();
            System.out.println(cardInfo);
            CardProductInfoArrays cardProductInfoArrays = cardInfo.getCardProductInfoArrays();
            if (cardProductInfoArrays==null){
                System.out.println("结束");
                return;
            }
            List<CardProductInfo> cardProductInfos = cardProductInfoArrays.getCardProductInfo();
            BigDecimal money = BigDecimal.ZERO;
            for (CardProductInfo cardProductInfo : cardProductInfos) {
                String accountBalance = cardProductInfo.getAccountBalance();
                money = money.add(new BigDecimal(accountBalance));
            }
            System.out.println(money);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @Test
    public void userMoneyPay() throws IOException {
        try {
            CardMoneyPayUtil.CardMoneyPay("100","8661086160004524230", "852146987462");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * 生成商户二维码
     * @throws IOException
     */
    @Test
    public void getMerchantPayQrCode() throws IOException {
        String s = QrCodeUtils.creatRrCode("https://allinpay.hualta.com/pay/getWXCode/WZSSH", 200, 200);
        System.out.println(s);
    }

    @Autowired
    private CardOrdersServiceImpl cardOrdersService;

    /**
     * 测试组合支付
     * @throws IOException
     */
    @Test
    public void testUserCashCardPay() throws IOException {
        UserCashCardPayOrderData userCashCardPayOrderData = new UserCashCardPayOrderData();
        userCashCardPayOrderData.setAmount(10000);
        List<String> list=new ArrayList<>();
        list.add("1288781843323965442");
        userCashCardPayOrderData.setCardNoList(list);
        userCashCardPayOrderData.setIsAccountPay(true);
        userCashCardPayOrderData.setMerchantCode("HLTA_001");
        userCashCardPayOrderData.setOpenid("ojnfgs1lO0I6Kgh125OpmyF77VpI");
        UserCashCardPayOrderReturn userCashCardPayOrderReturn = cardOrdersService.userAccountPay(userCashCardPayOrderData);
        System.out.println(userCashCardPayOrderReturn);
    }

    @Autowired
    private OrderOrdersServiceImpl orderOrdersServiceImpl;

    /**
     *
     * @throws IOException
     */
    @Test
    public void testCheckMoreMerchantCode() throws IOException {
        List<String> list=new ArrayList<>();
        list.add("HLTA001");
        list.add("HLTA001");
        list.add("HLTA002");
        list.add("HLTA002");
        list.add("HLTA002");
        list.add("HLTA003");
        list.add("HLTA003");
        list.add("HLTA004");
        list.add("HLTA005");
        Boolean aBoolean = orderOrdersServiceImpl.checkMoreMerchantCode(list);
        System.out.println(aBoolean);

        Set<String> set = orderOrdersServiceImpl.distinctMerchantCodeList(list);
        System.out.println(set);
    }

    @Autowired
    private CardCardsService cardCardsService;

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;

    /**
     * 测试 商城 添加商品
     * @throws IOException
     */
    @Test
    public void testMallSell() throws IOException {
        String cardCode = IdWorker.getIdStr();

        CardCards cardCards=new CardCards();
        cardCards.setCardCode(cardCode);
        cardCards.setCardName("生物体验");
        cardCards.setCategoryCode("category01");
        cardCards.setCategoryName("卡券分类");
        cardCards.setState("normal");
        cardCards.setType("number");
        cardCards.setCreateAt(new Date());
        cardCards.setUpdateAt(new Date());
        cardCards.setFaceValue(1);
        cardCards.setPrice(1);
        cardCards.setValidityType("beginToEnd");
        cardCards.setValidFrom(new Date());
        cardCards.setValidTo(TimeUtil.parseDate("2020-11-03 15:06:30","yyyy-MM-dd HH:mm:ss"));
        cardCards.setCardPicUrl("https://hlta-allinpay.oss-cn-shenzhen.aliyuncs.com/%E4%BC%98%E6%83%A0%E5%88%B811.png?Expires=1914476742&OSSAccessKeyId=LTAI4GDESiBWHwcNHPYTHaDt&Signature=60vNmK406bWDLrSgRneQAzWN8yw%3D");
        cardCards.setFlagTransfer(false);
        cardCards.setBatchTimes(1);
        cardCards.setNotice("一次核销");
        cardCards.setUnit("次");
        cardCardsService.save(cardCards);

        CardMapMerchantCards cardMapMerchantCards=new CardMapMerchantCards();
        cardMapMerchantCards.setMerchantCode("KDSW");
        cardMapMerchantCards.setCardCode(cardCode);
        cardMapMerchantCards.setCardType("number");
        cardMapMerchantCards.setCardName(cardCards.getCardName());
        cardMapMerchantCards.setType("mall_sell");
        cardMapMerchantCards.setState("normal");
        cardMapMerchantCards.setCardFaceValue("1");
        cardMapMerchantCards.setPrice(1);
        cardMapMerchantCards.setReferencePrice(1);
        cardMapMerchantCards.setOnSaleState("Y");
        cardMapMerchantCards.setCreateAt(new Date());
        cardMapMerchantCards.setUpdateAt(new Date());
        cardMapMerchantCards.setOnSaleDate(new Date());
        cardMapMerchantCards.setHaltSaleDate(new Date());
        cardMapMerchantCardsService.save(cardMapMerchantCards);
    }

    @Autowired
    private OrderOrdersController orderOrdersController;


    @Test
    public void testQueryOrderCode() throws IOException {
        OrderOrders orderOrders = orderOrdersController.queryByOrderCode("1310397630388117506");
        System.out.println(orderOrders);
    }

    /**
     * 生成会员码
     * @throws IOException
     */
    @Test
    public void userVipQrCode() throws IOException {
        String s = QrCodeUtils.creatRrCode("ojnfgs1lO0I6Kgh125OpmyF77VpI1234", 200, 200);
        System.out.println(s);
    }

    /**
     * 测试充值创建账户余额
     * @throws Exception
     */
    @Test
    public void testCreateUserAccount() throws Exception {
        CardOrdersVO cardOrdersVO = new CardOrdersVO();
        cardOrdersVO.setMerchantCode("HLMSD");
        cardOrdersVO.setUserId(900L);
        cardOrdersService.tongLianCardOpenAndTopUp("15632636952",1000,cardOrdersVO);
    }

    @Test
    public void testActivity(){
        cardMapUserCardsService.sendCardForFulfilQuota("HLMSD",1,299L);
    }

    @Autowired
    private CardOrderDetailsService cardOrderDetailsService;

    @Test
    public void testQueryDetails(){
        List<CardOrderDetails> cardOrderDetails = cardOrderDetailsService.queryByOrderCode("1358596246297645058");
        System.out.println(cardOrderDetails);
    }


    @Autowired
    private DESUtil desUtil;

    /**
     * 测试支付加密
     * @throws Exception
     */
    @Test
    public void testDESScanPayDta() throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("paymentQrCode","130563422656491469");
        objectObjectHashMap.put("amount","1");
        String idStr = IdWorker.getIdStr();
        String testOrderNo = "TS"+idStr+System.currentTimeMillis();
        System.out.println(testOrderNo);
        objectObjectHashMap.put("orderCode",testOrderNo);
        objectObjectHashMap.put("cashId","0012");
        objectObjectHashMap.put("storeCode","18521");
        objectObjectHashMap.put("actualPhone","15682059552");

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
//        String decrypt = desUtil.decrypt("IR+O5Btcm8se8OlAWbrQEoLgAU5icpY3LbDBy9ozguWs2fgoaVDv+fSCaMOPx1ySpfUIf2lDeW9+TzM1MAKrHS/gLlrtkjVx4QRDE1SP2iY=");
//        System.out.println(decrypt);

        System.out.println("查询数据加密-------------------");
        testDESSearchOrderUseCode(testOrderNo);
    }

    /**
     * 测试撤销加密
     * @throws Exception
     */
    @Test
    public void testDESCancel() throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("orderCode","TS16442287815651450891680849664475");
        objectObjectHashMap.put("operator","msadmin");
        String idStr = IdWorker.getIdStr();
        String testOrderNo = "TS"+idStr+System.currentTimeMillis();
        System.out.println(testOrderNo);
        objectObjectHashMap.put("cancelCode",testOrderNo);
        String s = JSONObject.toJSONString(objectObjectHashMap);
        System.out.println(s);

        String encrypt = desUtil.encrypt(s);
        System.out.println(encrypt);
//        String decrypt = desUtil.decrypt("IR+O5Btcm8se8OlAWbrQEirG/ranDmceBeIyyupFV0//63GPR/Vr9wNygf8sOTeSmM/iqCtpkswTmnw9SBhMKuuKsQJT0XrIV7+B9Nw1brM=");
//        System.out.println(decrypt);

    }


    /**
     * 测试退款加密
     * @throws Exception
     */
    @Test
    public void testDESRefund() throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("orderCode","TS17202689084978053131698979043493");
        objectObjectHashMap.put("operator","jladmin");
        String idStr = IdWorker.getIdStr();
        String testOrderNo = "TS"+idStr+System.currentTimeMillis();
        System.out.println(testOrderNo);
        objectObjectHashMap.put("refundCode",testOrderNo);
        objectObjectHashMap.put("refundAmount","1");
        String s = JSONObject.toJSONString(objectObjectHashMap);
        System.out.println(s);

        String encrypt = desUtil.encrypt(s);
        System.out.println(encrypt);
//        String decrypt = desUtil.decrypt("IR+O5Btcm8se8OlAWbrQEnfzssfmjhq2QFgSZPIE8EIKbMzOLsW5M/8SseZyDhPIFzMs3hhVFGFtFv7TR2zmJVDXHuK28WL8g4Vkx9TpJaU=");
//        System.out.println(decrypt);

    }

    /**
     * 测试 刷卡 mis 订单 上送 加密数据
     * @throws Exception
     */
    @Test
    public void testDESOrder() throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("amount","10000");
        String idStr = IdWorker.getIdStr();
        String testOrderNo = "TS"+idStr+System.currentTimeMillis();
        System.out.println(testOrderNo);
        objectObjectHashMap.put("orderCode",testOrderNo);
        objectObjectHashMap.put("cashId","HT000090");
        objectObjectHashMap.put("memo","备注");
        objectObjectHashMap.put("storeCode","18521");
        objectObjectHashMap.put("actualPhone","15682059552");

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

//        String encrypt = desUtil.encrypt(s);
//        System.out.println(encrypt);

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

    /**
     * 测试交易数据查询加密
     * @throws Exception
     */
    @Test
    public void testDESSearchOrder() throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("trxid","451099369905317311283235");
//        objectObjectHashMap.put("orderCode",OrderCode);
//        objectObjectHashMap.put("refundCancelCode","");
        String s = JSONObject.toJSONString(objectObjectHashMap);
        System.out.println(s);

        String encrypt = desUtil.encrypt(s);
        System.out.println(encrypt);
    }

    /**
     * 测试交易数据查询加密
     * @throws Exception
     */
    @Test
    public void testDESSearchOrderUseCode(String OrderCode) throws Exception {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("trxid","451099369905317311283235");
        objectObjectHashMap.put("orderCode",OrderCode);
//        objectObjectHashMap.put("refundCancelCode","");
        String s = JSONObject.toJSONString(objectObjectHashMap);
        System.out.println(s);

        String encrypt = desUtil.encrypt(s);
        System.out.println(encrypt);
    }

    @Autowired
    private SybPayService sybPayService;

    @Test
    public void testScanPay() throws Exception {
        String reqsn = String.valueOf(System.currentTimeMillis());
        Map<String, String> map = sybPayService.scanPay(1, reqsn,  "标题", "备注",  "134773643659700063","","","","");
        System.out.println("响应的map数据"+map);

        OutletsOrderRefTrace outletsOrderRefTrace = new OutletsOrderRefTrace();
        BeanMap beanMap = BeanMap.create(outletsOrderRefTrace);
        beanMap.putAll(map);
        System.out.println("转化为实体数据"+outletsOrderRefTrace);
    }

    @Autowired
    private IOutletsOrdersService outletsOrdersService;

    @Test
    public void testPosCancel() throws Exception {
//        OutletsOrderCancelData outletsOrderCancelData = new OutletsOrderCancelData();
//        outletsOrderCancelData.setOrderCode("o1637498614204");
//        outletsOrderCancelData.setOperator("admin");
//        outletsOrderCancelData.setCancelCode(IdWorker.getIdStr());
//        outletsOrdersService.orderCancel(outletsOrderCancelData);
//{randomstr=258022009235, trxcode=VSP501, sign=1F457CDFFB3D20B8D2D4C5147C16936A, errmsg=交易处理中, cmid=553642053118CXX, trxid=211205112408730036, trxstatus=2000, accttype=99, reqsn=test1467381917388480514, cusid=553642053118CXX, appid=00225872, initamt=1, acct=000000, trxamt=1, retcode=SUCCESS}
//        sybPayService.posRefund("4","211123115507797081",IdWorker.getIdStr(),"测试pos");
        Map<String, String> queryMap = sybPayService.query("7a33be3ed853495da26a450e696ba705", "");
        System.out.println(queryMap);
//        2021-12-05 14:35:58   2021-12-05 14:35:12 2021-12-05 14:35:58
        //{chnlid=205299480, randomstr=595667677042, trxcode=VSP501, fee=0, sign=FD5165733F68FDEC43D8CCFFFB60F2A7, errmsg=支付失败，请撤销订单, cmid=476207672, trxid=211205112408730036, trxstatus=3045, accttype=99, reqsn=test1467381917388480514, fintime=20211205143558, cusid=553642053118CXX, appid=00225872, initamt=1, acct=000000, trxamt=1, retcode=SUCCESS}
    }


}
