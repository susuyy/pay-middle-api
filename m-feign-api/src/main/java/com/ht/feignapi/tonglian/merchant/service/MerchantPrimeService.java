package com.ht.feignapi.tonglian.merchant.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.entity.VipSearch;
import com.ht.feignapi.tonglian.admin.entity.VipVo;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tongshangyun.client.TsyMemberClient;
import com.ht.feignapi.tongshangyun.constant.MemberConstant;
import com.ht.feignapi.tongshangyun.entity.BizMemberData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.toolkit.Assert;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 12:00
 */
@Service
public class MerchantPrimeService {

    @Autowired
    private MerchantsClientService merchantClientService;

    @Autowired
    private MapMerchantPrimesClientService merchantPrimesClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private TsyMemberClient tsyMemberClient;



    @Value("${custom.client.merchant.name}")
    private String applicationCode;

    @Transactional(rollbackFor = Exception.class)
    public void saveUserIcCard(String openId, String merchantCode, String icCardId, String phone) {
        UserUsers users = primeQueryUserByTelAndCode(phone, merchantCode);
        MrcMapMerchantPrimes prime = merchantClientService.getPrimeByOpenId(openId, merchantCode).getData();
        Assert.isFalse(prime!=null&&users!=null&&(!prime.getUserId().equals(users.getId())),
                "会员卡批量绑定:手机号:"+ phone +",openId:"+openId+",icCardId:"+icCardId+"与已存在会员冲突<br/>");
        Assert.isFalse(users==null&&prime!=null,"会员卡批量绑定:openId:"+ openId + "已存在。用户对应手机号:"+phone+"不存在<br/>");
        if (users == null){
            users = new UserUsers();
            users.setTel(phone);
            authClientService.register(users);
        }
        if (prime == null) {
            prime = this.saveNewPrime(openId,merchantCode,users.getId());
        }
        saveUserCard(merchantCode, icCardId, prime.getUserId());
    }

    /**
     * 检测改用户是否达到日面额度限制
     * @param merchantCode
     * @param openId
     * @return boolean：true未达到额度限制，可支付。false达到额度限制，不可支付
     */
    public Boolean checkUserDailyExpenditureLimit(String merchantCode, String openId) {
        MrcMapMerchantPrimes primes = merchantClientService.getPrimeByOpenId(openId, merchantCode).getData();
        Assert.notNull(primes,"该用户尚未在此商户下注册");
        Integer dailyExpenditure = orderClientService.getUserDailyExpenditure(merchantCode, primes.getUserId());
        return primes.getUserId()>dailyExpenditure;
    }

    private MrcMapMerchantPrimes saveNewPrime(String openId, String merchantCode, Long userId) {
        MrcMapMerchantPrimes prime;
        Integer perPaymentLimit = Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode,"PER_PAYMENT_LIMIT").getData());
        Integer dailyPaymentLimit= Integer.parseInt(merchantsConfigClientService.getConfigByKey(merchantCode,"DAILY_PAYMENT_LIMIT").getData());
        prime = new MrcMapMerchantPrimes();
        prime.setMerchantCode(merchantCode);
        prime.setOpenId(openId);
        prime.setUserId(userId);
        prime.setState("normal");
        prime.setPerPaymentLimit(perPaymentLimit);
        prime.setDailyPaymentLimit(dailyPaymentLimit);
        prime.setType("黄金会员");
        return merchantPrimesClientService.saveOrUpdate(prime).getData();
    }

    private void saveUserCard(String merchantCode, String icCardId, Long userId) {
        CardMapUserCards userCard = cardMapUserClientService.getUserVipCard(merchantCode, userId).getData();
        if (userCard == null) {
            userCard = new CardMapUserCards();
            userCard.setCardCode("867");
            userCard.setUserId(userId);
            userCard.setMerchantCode(merchantCode);
            userCard.setCardName("会员卡");
            userCard.setCardNo(IdWorker.getIdStr());
            userCard.setIcCardId(icCardId);
            userCard.setState("normal");
            cardMapUserClientService.saveOrUpdate(userCard);
        }
    }

    public Page<VipVo> getMerchantAllVipUsers(String merchantCode, VipSearch vipSearch, Long pageNo,Long pageSize) {
        vipSearch.setMerchantCode(merchantCode);
        vipSearch.setPageNo(pageNo);
        vipSearch.setPageSize(pageSize);
        Result<Page<VipVo>> result =  merchantPrimesClientService.getMerchantAllVipUsers(vipSearch);
        if (result!=null && result.getData()!=null){
            result.getData().getRecords().forEach(e->{
                Result<UserUsers> userUsersResult = authClientService.getUserByIdTL(e.getUserId().toString());
                if (userUsersResult!=null && userUsersResult.getData()!=null){
                    UserUsers users = userUsersResult.getData();
                    e.setGender(users.getGender());
                    e.setName(users.getRealName());
                    e.setNickName(users.getNickName());
                    e.setTel(users.getTel());
                }
            });
            if (!StringUtils.isBlank(vipSearch.getNickName())){
                List<VipVo> list = result.getData().getRecords().stream()
                        .filter(e->checkNickName(e,vipSearch.getNickName()))
                        .collect(Collectors.toList());
                result.getData().setRecords(list);
            }
            return result.getData();
        }
        return new Page<>();
    }

    private boolean checkNickName(VipVo vip,String nickName){
        if (!StringUtils.isBlank(vip.getNickName())){
            return vip.getNickName().contains(nickName);
        }
        return false;
    }

    /**
     * 根据手机号和merchantCode 查询主体会员信息 确定唯一用户
     * @param tel
     * @param merchantCode
     * @return
     */
    public UserUsers primeQueryUserByTelAndCode(String tel,String merchantCode){
        String objectMerchantCode = merchantClientService.queryObjectMerchantCode(merchantCode).getData();
        MrcMapMerchantPrimes mrcMapMerchantPrimes = merchantPrimesClientService.queryByTelAndMerchantCode(tel, objectMerchantCode).getData();
        if (mrcMapMerchantPrimes==null){
            throw new CheckException(ResultTypeEnum.USER_NULL);
        }
        return authClientService.getUserByIdTL(mrcMapMerchantPrimes.getUserId().toString()).getData();
    }

    /**
     * 添加主体会员信息 同时创建通商云会员
     * @param userId
     * @param merchantCode
     * @param normal
     * @param type
     * @param openId
     */
    public void addAndRegisterTsyMember(Long userId, String merchantCode, String normal, String type, String openId) {
        BizMemberData bizMemberData = tsyMemberClient.bizMemberRegister(userId + "", MemberConstant.MEMBER_TYPE_USER, MemberConstant.SOURCE_TYPE_MOBILE).getData();
        MrcMapMerchantPrimes mrcMapMerchantPrimes=new MrcMapMerchantPrimes();
        mrcMapMerchantPrimes.setOpenId(openId);
        mrcMapMerchantPrimes.setMerchantCode(merchantCode);
        mrcMapMerchantPrimes.setUserId(userId);
        mrcMapMerchantPrimes.setState(normal);
        mrcMapMerchantPrimes.setBizUserId(bizMemberData.getBizUserId());
        mrcMapMerchantPrimes.setTsyUserId(bizMemberData.getUserId());
        mrcMapMerchantPrimes.setType(type);
        mrcMapMerchantPrimes.setCreateAt(new Date());
        mrcMapMerchantPrimes.setUpdateAt(new Date());
        mrcMapMerchantPrimes.setPrimePoints(0);
        merchantPrimesClientService.addAndRegisterTsyMember(mrcMapMerchantPrimes);
    }
}
