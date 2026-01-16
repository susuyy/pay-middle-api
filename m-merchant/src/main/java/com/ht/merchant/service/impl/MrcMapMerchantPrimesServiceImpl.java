package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.merchant.entity.Merchants;
import com.ht.merchant.entity.vo.MerchantPrimeVo;
import com.ht.merchant.service.MerchantsConfigService;
import com.ht.merchant.service.MerchantsService;
import com.ht.merchant.service.MrcMapMerchantPrimesService;
import com.ht.merchant.utils.TimeUtil;
import com.ht.merchant.vo.VipSearch;
import com.ht.merchant.vo.VipVo;
import com.ht.merchant.entity.MrcMapMerchantPrimes;
import com.ht.merchant.mapper.MapMerchantPrimesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 商户-会员对应表 服务实现类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Service
public class MrcMapMerchantPrimesServiceImpl extends ServiceImpl<MapMerchantPrimesMapper, MrcMapMerchantPrimes> implements MrcMapMerchantPrimesService {

    private final static Logger logger = LoggerFactory.getLogger(MrcMapMerchantPrimesServiceImpl.class);

    @Autowired
    private MerchantsConfigService merchantsConfigService;

    @Autowired
    private MerchantsService merchantsService;

    /**
     * 根据userId查询商户关联
     *
     * @param userId
     * @return
     */
    @Override
    public MrcMapMerchantPrimes queryByUserId(Long userId) {
        return this.baseMapper.selectByUserId(userId);
    }

    @Override
    public MrcMapMerchantPrimes queryByUserIdAndMerchantCode(Long userId, String merchantCode) {
        QueryWrapper<MrcMapMerchantPrimes> queryWrapper = new QueryWrapper<MrcMapMerchantPrimes>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("merchant_code", merchantCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void add(Long userId, String merchantCode, String state, String type, String openId) {
        MrcMapMerchantPrimes mrcMapMerchantPrimes = new MrcMapMerchantPrimes();
        Integer perPaymentLimit = Integer.parseInt(merchantsConfigService.getListByKey(merchantCode,"PER_PAYMENT_LIMIT").getValue());
        Integer dailyPaymentLimit= Integer.parseInt(merchantsConfigService.getListByKey(merchantCode,"DAILY_PAYMENT_LIMIT").getValue());
        mrcMapMerchantPrimes.setDailyPaymentLimit(dailyPaymentLimit);
        mrcMapMerchantPrimes.setPerPaymentLimit(perPaymentLimit);
        mrcMapMerchantPrimes.setUserId(userId);
        mrcMapMerchantPrimes.setOpenId(openId);
        mrcMapMerchantPrimes.setState(state);
        mrcMapMerchantPrimes.setType(type);
        mrcMapMerchantPrimes.setMerchantCode(merchantCode);
        mrcMapMerchantPrimes.setPrimePoints(0);
        mrcMapMerchantPrimes.setCreateAt(new Date());
        mrcMapMerchantPrimes.setUpdateAt(new Date());
        this.baseMapper.insert(mrcMapMerchantPrimes);
    }

    @Override
    public List<VipVo> getVipListByMerchantCodes(String merchantCode, VipSearch vipSearch, IPage<VipVo> page) {
        return this.baseMapper.getVipList(merchantCode, vipSearch, page);
    }

    @Override
    public List<MrcMapMerchantPrimes> getUserByMemberType(String merchantCode, String memberType) {
        LambdaQueryWrapper<MrcMapMerchantPrimes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MrcMapMerchantPrimes::getMerchantCode,merchantCode);
        wrapper.eq(MrcMapMerchantPrimes::getType,memberType);
        return this.list(wrapper);
    }

    @Override
    public boolean checkUserExist(String merchantCode, Long userId) {
        LambdaQueryWrapper<MrcMapMerchantPrimes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MrcMapMerchantPrimes::getMerchantCode,merchantCode);
        wrapper.eq(MrcMapMerchantPrimes::getUserId,userId);
        return this.list(wrapper).size()>0;
    }

    private MrcMapMerchantPrimes saveNewPrime(String openId, String merchantCode, Long userId) {
        MrcMapMerchantPrimes prime;
        Integer perPaymentLimit = Integer.parseInt(merchantsConfigService.getListByKey(merchantCode,"PER_PAYMENT_LIMIT").getValue());
        Integer dailyPaymentLimit= Integer.parseInt(merchantsConfigService.getListByKey(merchantCode,"DAILY_PAYMENT_LIMIT").getValue());
        prime = new MrcMapMerchantPrimes();
        prime.setMerchantCode(merchantCode);
        prime.setOpenId(openId);
        prime.setUserId(userId);
        prime.setState("normal");
        prime.setPerPaymentLimit(perPaymentLimit);
        prime.setDailyPaymentLimit(dailyPaymentLimit);
        prime.setType("黄金会员");
        this.save(prime);
        return prime;
    }

    @Override
    public MrcMapMerchantPrimes getPrimeByOpenId(String openId, String merchantCode) {
        LambdaQueryWrapper<MrcMapMerchantPrimes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MrcMapMerchantPrimes::getOpenId,openId);
        queryWrapper.eq(MrcMapMerchantPrimes::getMerchantCode,merchantCode);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询用户在某个商户下的积分
     * @param userId
     * @param openId
     * @param merchantCode
     * @return
     */
    @Override
    public Integer queryUserPoint(Long userId, String openId, String merchantCode) {
        QueryWrapper<MrcMapMerchantPrimes> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("open_id",openId);
        queryWrapper.eq("merchant_code",merchantCode);
        List<MrcMapMerchantPrimes> mrcMapMerchantPrimesList = this.baseMapper.selectList(queryWrapper);
        Integer point = 0;
        if (mrcMapMerchantPrimesList!=null && mrcMapMerchantPrimesList.size()>0){
            for (MrcMapMerchantPrimes mrcMapMerchantPrimes : mrcMapMerchantPrimesList) {
                point = point+mrcMapMerchantPrimes.getPrimePoints();
            }
        }
        return point;
    }

    /**
     * 查询用户总积分
     * @param userId
     * @param openId
     * @return
     */
    @Override
    public Integer queryMyTotalPoint(String userId, String openId) {
        QueryWrapper<MrcMapMerchantPrimes> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("open_id",openId);
        List<MrcMapMerchantPrimes> mrcMapMerchantPrimesList = this.baseMapper.selectList(queryWrapper);
        Integer point = 0;
        if (mrcMapMerchantPrimesList!=null && mrcMapMerchantPrimesList.size()>0){
            for (MrcMapMerchantPrimes mrcMapMerchantPrimes : mrcMapMerchantPrimesList) {
                point = point+mrcMapMerchantPrimes.getPrimePoints();
            }
        }
        return point;
    }

    @Override
    public MrcMapMerchantPrimes queryMyMrcMapMerchantPrimes(Long userId, String openId, String merchantCode) {
        QueryWrapper<MrcMapMerchantPrimes> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("open_id",openId);
        queryWrapper.eq("merchant_code",merchantCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void deductPointsById(String id, Integer usePoints) {
        this.baseMapper.deductPointsById(id,usePoints);
    }

    @Override
    public MrcMapMerchantPrimes queryByTelAndMerchantCode(String tel, String merchantCode) {
        QueryWrapper<MrcMapMerchantPrimes> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("tel",tel);
        queryWrapper.eq("merchant_code",merchantCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<MerchantPrimeVo> getPrimeMonthlyIncrements(Date begin, Date end, List<String> merchantCodes) {
        List<MerchantPrimeVo> resultList = new LinkedList<>();
        Date beginDate = TimeUtil.formatDate(begin, "yyyy-MM");
        while (beginDate.before(TimeUtil.addMonth(end,1))){
            Date endDate = TimeUtil.addMonth(beginDate,1);
            for (String merchantCode:merchantCodes) {
                MerchantPrimeVo merchantPrimeVo = this.baseMapper.getPrimeMonthlyIncrements(beginDate,endDate,merchantCode);
                if (ObjectUtils.isEmpty(merchantPrimeVo)){
                    merchantPrimeVo = initMerchantPrimeVo(merchantCode);
                }
                merchantPrimeVo.setDate(beginDate);
                resultList.add(merchantPrimeVo);
            }
            beginDate = endDate;
        }
        return resultList;
    }

    @Override
    public List<MerchantPrimeVo> getPrimeTotalAmount(Date begin, Date end, List<String> merchantCodes) {
        List<MerchantPrimeVo> resultList = new LinkedList<>();
        Date deadline = TimeUtil.formatDate(begin, "yyyy-MM");
        while (deadline.before(TimeUtil.addMonth(end,1))){
            Date endDate = TimeUtil.addMonth(deadline,1);
            for (String merchantCode:merchantCodes) {
                MerchantPrimeVo merchantPrimeVo = this.baseMapper.getPrimeTotalAmount(deadline,merchantCode);
                if (ObjectUtils.isEmpty(merchantPrimeVo)){
                    merchantPrimeVo = initMerchantPrimeVo(merchantCode);
                }
                merchantPrimeVo.setDate(deadline);
                resultList.add(merchantPrimeVo);
            }
            deadline = endDate;
        }
        return resultList;
    }

    private MerchantPrimeVo initMerchantPrimeVo(String merchantCode) {
        MerchantPrimeVo merchantPrimeVo;
        merchantPrimeVo = new MerchantPrimeVo();
        Merchants merchants = merchantsService.getMerchantByCode(merchantCode);
        merchantPrimeVo.setMerchantCode(merchantCode);
        merchantPrimeVo.setMerchantName(merchants.getMerchantName());
        merchantPrimeVo.setCount(0);
        return merchantPrimeVo;
    }

    @Override
    public MrcMapMerchantPrimes queryByTelChangeObjectCode(String tel, String merchantCode) {
        Merchants merchant = merchantsService.getMerchantByCode(merchantCode);
        QueryWrapper<MrcMapMerchantPrimes> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("tel",tel);
        queryWrapper.eq("merchant_code",merchant.getBusinessSubjects());
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void addAndRegisterTsyMember(MrcMapMerchantPrimes mrcMapMerchantPrimes) {
        Integer perPaymentLimit = Integer.parseInt(merchantsConfigService.getListByKey(mrcMapMerchantPrimes.getMerchantCode(),"PER_PAYMENT_LIMIT").getValue());
        Integer dailyPaymentLimit= Integer.parseInt(merchantsConfigService.getListByKey(mrcMapMerchantPrimes.getMerchantCode(),"DAILY_PAYMENT_LIMIT").getValue());
        mrcMapMerchantPrimes.setDailyPaymentLimit(dailyPaymentLimit);
        mrcMapMerchantPrimes.setPerPaymentLimit(perPaymentLimit);
        this.baseMapper.insert(mrcMapMerchantPrimes);
    }

}
