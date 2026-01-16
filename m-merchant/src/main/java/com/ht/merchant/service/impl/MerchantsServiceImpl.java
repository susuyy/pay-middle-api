package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.merchant.common.MerchantConstant;
import com.ht.merchant.entity.*;
import com.ht.merchant.entity.vo.MerchantCountVo;
import com.ht.merchant.result.CodeExistException;
import com.ht.merchant.service.IMrcMapMerchantUserService;
import com.ht.merchant.service.MerchantsService;
import com.ht.merchant.service.MrcMapMerchantPrimesService;
import com.ht.merchant.service.MrcMapMerchantService;
import com.ht.merchant.utils.HeadCharUtil;
import com.ht.merchant.utils.TimeUtil;
import com.ht.merchant.vo.VipSearch;
import com.ht.merchant.vo.VipVo;
import com.ht.merchant.mapper.MerchantsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Service
public class MerchantsServiceImpl extends ServiceImpl<MerchantsMapper, Merchants> implements MerchantsService {

    private final static Logger logger = LoggerFactory.getLogger(MerchantsServiceImpl.class);

    @Autowired
    private MrcMapMerchantPrimesService merchantPrimesService;

    @Autowired
    private IMrcMapMerchantUserService mrcMapMerchantUserService;

    @Autowired
    private MrcMapMerchantService mrcMapMerchantService;


    @Override
    public Merchants getByUserId(Long userId) {
        return this.baseMapper.getByUserId(userId);
    }

    @Override
    public List<Merchants> getSubMerchants(String code) {
        return this.baseMapper.getSubMerchants(code);
    }

    @Override
    public IPage<MrcMapMerchantPrimes> getMapMerchantPrimes(String code) {
        return null;
    }

    @Override
    public List<VipVo> getMerchantAllVipUsers(String merchantCode, VipSearch vipSearch, IPage<VipVo> page) {
        return merchantPrimesService.getVipListByMerchantCodes(merchantCode, vipSearch, page);
    }

    /**
     * 根据商户编码查询商户主体信息
     * @param merchantCode
     * @return
     */
    @Override
    public Merchants getMerchantByCode(String merchantCode) {
        LambdaQueryWrapper<Merchants> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchants::getMerchantCode,merchantCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

//    @Override
//    public UsrUsers getMerchantAdminUser(String merchantCode) {
//        Merchants merchants = this.queryByMerchantCode(merchantCode);
//        Assert.notNull(merchants,"非法商户号");
//        return usrUsersService.getById(merchants.getUserId());
//    }
//
//    @Override
//    public Boolean checkMerchantPassword(Merchants merchant, String password) {
//        UsrUsers objectUser;
//        if ("OBJECT".equals(merchant.getType())){
//            objectUser = usrUsersService.getById(merchant.getUserId());
//        }else {
//            Merchants objectMerchant = this.queryByMerchantCode(merchant.getBusinessSubjects());
//            Assert.notNull(objectMerchant,"获取商户主体出错");
//            objectUser = usrUsersService.getById(objectMerchant.getUserId());
//        }
//        return objectUser.getPassword().equals(password);
//    }

    /**
     * 通过商户码查询父类商户编码
     * @param merchantCode
     * @return
     */
    @Override
    public String queryObjectMerchantCode(String merchantCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("merchant_code",merchantCode);
        Merchants merchants = this.baseMapper.selectOne(queryWrapper);
        if ("OBJECT".equals(merchants.getType())){
            return merchants.getMerchantCode();
        }else {
            return merchants.getBusinessSubjects();
        }
    }

    @Override
    public IPage<Merchants> getObjectMerchants(Long pageNo, Long pageSize, String merchantName){
        IPage<Merchants> page = new Page<>(pageNo,pageSize);
        LambdaQueryWrapper<Merchants> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchants::getType,"OBJECT");
        if (!StringUtils.isEmpty(merchantName)){
            wrapper.like(Merchants::getMerchantName,merchantName);
        }
        return this.baseMapper.selectPage(page,wrapper);
    }

    @Override
    public boolean save(Merchants merchants) {
        boolean mCodeExist = isMerchantCodeExist(merchants.getMerchantCode());
        if (mCodeExist){
            logger.info("****************MERCHANT-CODE-EXIST*****************" + merchants.getMerchantCode());
            throw new CodeExistException("商户号已存在");
        }
        return super.save(merchants);
    }

    @Override
    public Integer getMerchantsCountBeforeDate(Date end) {
        LambdaQueryWrapper<Merchants> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Merchants::getCreateAt,end);
        wrapper.eq(Merchants::getType, MerchantConstant.OBJECT_TYPE);
        return this.count(wrapper);
    }

    @Override
    public List<MerchantCountVo> getMerchantCount(String beginDate, String endDate) {
        List<MerchantCountVo> list = new ArrayList<>();
        long dateCount = TimeUtil.getDayInterval(beginDate,endDate);
        Date begin = TimeUtil.parseDate(beginDate,"yyyy-MM-dd");
        logger.info("开始时间：" + new Date());
        for (int i = 0; i<dateCount; i++){
            MerchantCountVo countVo = new MerchantCountVo();
            countVo.setDate(begin);
            begin = TimeUtil.addDay(begin,1);
            countVo.setCount(this.getMerchantsCountBeforeDate(begin));
            list.add(countVo);
        }
        logger.info("结束时间：" + new Date());
        return list;
    }

    @Override
    public String saveAndMapUser(SaveAndMapUser saveAndMapUser) {
        Merchants merchants = saveAndMapUser.getMerchants();
        this.baseMapper.insert(merchants);
        merchants.setMerchantCode(HeadCharUtil.getPinYinHeadChar(merchants.getMerchantName())+merchants.getId());
        this.baseMapper.updateById(merchants);
        UserUsers userUsers = saveAndMapUser.getUserUsers();
        MrcMapMerchantUser mrcMapMerchantUser = new MrcMapMerchantUser();
        mrcMapMerchantUser.setMerchantCode(merchants.getMerchantCode());
        mrcMapMerchantUser.setUserId(userUsers.getId());
        mrcMapMerchantUser.setState("enable");
        mrcMapMerchantUser.setType("1");
        mrcMapMerchantUser.setCreateAt(new Date());
        mrcMapMerchantUser.setUpdateAt(new Date());
        mrcMapMerchantUserService.save(mrcMapMerchantUser);

        MrcMapMerchant mrcMapMerchant = new MrcMapMerchant();
        mrcMapMerchant.setSubMerchantCode(merchants.getMerchantCode());
        mrcMapMerchant.setSubMerchantName(merchants.getMerchantName());
        Merchants objMerchant = getMerchantByCode(merchants.getBusinessSubjects());
        mrcMapMerchant.setObjMerchantCode(objMerchant.getMerchantCode());
        mrcMapMerchant.setObjMerchantName(objMerchant.getMerchantName());
        mrcMapMerchant.setCreateAt(new Date());
        mrcMapMerchant.setUpdateAt(new Date());
        mrcMapMerchantService.save(mrcMapMerchant);

        return merchants.getMerchantCode();
    }

    @Override
    public Page<Merchants> searchSubMerchants(SearchSubMerchantsData searchSubMerchantsData) {
        Page<MrcMapMerchant> queryPage = new Page<>(searchSubMerchantsData.getPageNo(), searchSubMerchantsData.getPageSize());
        QueryWrapper<MrcMapMerchant> queryWrapper=new QueryWrapper<>();
        if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchCode())){
            queryWrapper.like("sub_merchant_code",searchSubMerchantsData.getSearchCode());
        }
        if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchName())){
            queryWrapper.like("sub_merchant_name",searchSubMerchantsData.getSearchName());
        }
//        if (!StringUtils.isEmpty(searchSubMerchantsData.getObjectMerchantCode()) || !"TLZF".equals(searchSubMerchantsData.getObjectMerchantCode())) {
//            queryWrapper.eq("obj_merchant_code", searchSubMerchantsData.getObjectMerchantCode());
//        }

        if (!StringUtils.isEmpty(searchSubMerchantsData.getObjectMerchantCode())
                || !"TLZF".equals(searchSubMerchantsData.getObjectMerchantCode())
                || !"THSZ".equals(searchSubMerchantsData.getObjectMerchantCode())) {
            queryWrapper.eq("obj_merchant_code", searchSubMerchantsData.getObjectMerchantCode());
        }

        Page<MrcMapMerchant> page = mrcMapMerchantService.page(queryPage, queryWrapper);

        List<String> merchantCodeList = new ArrayList<>();
        List<MrcMapMerchant> records = page.getRecords();
        for (MrcMapMerchant record : records) {
            merchantCodeList.add(record.getSubMerchantCode());
        }

        Page<Merchants> merchantsPage = new Page<>();
        merchantsPage.setPages(page.getPages());
        merchantsPage.setCurrent(page.getCurrent());
        merchantsPage.setTotal(page.getTotal());
        merchantsPage.setSize(page.getSize());

        QueryWrapper<Merchants> merchantQueryWrapper=new QueryWrapper<>();
        merchantQueryWrapper.in("merchant_code",merchantCodeList);
        List<Merchants> merchants = this.baseMapper.selectList(merchantQueryWrapper);

        merchantsPage.setRecords(merchants);
        return merchantsPage;

//        Page<Merchants> queryPage = new Page<Merchants>(searchSubMerchantsData.getPageNo(), searchSubMerchantsData.getPageSize());
//        QueryWrapper<Merchants> queryWrapper=new QueryWrapper<>();
//        if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchCode())){
//            queryWrapper.like("merchant_code",searchSubMerchantsData.getSearchCode());
//        }
//        if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchName())){
//            queryWrapper.like("merchant_name",searchSubMerchantsData.getSearchName());
//        }
//        if (!StringUtils.isEmpty(searchSubMerchantsData.getObjectMerchantCode()) || !"TLZF".equals(searchSubMerchantsData.getObjectMerchantCode())) {
//            queryWrapper.eq("business_subjects", searchSubMerchantsData.getObjectMerchantCode());
//        }
//        Page<Merchants> merchantsPage = this.baseMapper.selectPage(queryPage, queryWrapper);
//        return merchantsPage;
    }

    @Override
    public Page<Merchants> searchAllMerchants(SearchSubMerchantsData searchSubMerchantsData) {
        Page<Merchants> queryPage = new Page<Merchants>(searchSubMerchantsData.getPageNo(), searchSubMerchantsData.getPageSize());
        QueryWrapper<Merchants> queryWrapper=new QueryWrapper<>();
        if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchCode())){
            queryWrapper.like("merchant_code",searchSubMerchantsData.getSearchCode());
        }
        if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchName())){
            queryWrapper.like("merchant_name",searchSubMerchantsData.getSearchName());
        }
        if (!StringUtils.isEmpty(searchSubMerchantsData.getType())){
            queryWrapper.like("type",searchSubMerchantsData.getType());
        }
        Page<Merchants> merchantsPage = this.baseMapper.selectPage(queryPage, queryWrapper);
        return merchantsPage;
    }

    @Override
    public List<String> getObjectMerchantCodes() {
        LambdaQueryWrapper<Merchants> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchants::getType,MerchantConstant.OBJECT_TYPE);
        List<Merchants> merchantsList = this.list(wrapper);
        if (!CollectionUtils.isEmpty(merchantsList)){
            return merchantsList.stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<Merchants> getObjectMerchant() {
        LambdaQueryWrapper<Merchants> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchants::getType,MerchantConstant.OBJECT_TYPE);
        return this.list(wrapper);
    }

    @Override
    public void removeBrhMerchant(String brhMerchantCode) {
        Merchants merchant = getMerchantByCode(brhMerchantCode);
        merchant.setBusinessSubjects("");
        this.baseMapper.updateById(merchant);
    }

    @Override
    public List<String> getObjMerchantCodes() {
        List<Merchants> objMerchants = this.getObjectMerchant();
        return objMerchants.stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
    }

    @Override
    public BizMerchantUserData queryMerchantBizUserIdAndPayerBizUserId(String merchantCode, Long userId) {
        Merchants subMerchants = getMerchantByCode(merchantCode);
        Merchants objectMerchant = getMerchantByCode(subMerchants.getBusinessSubjects());
        BizMerchantUserData bizMerchantUserData = new BizMerchantUserData();
        bizMerchantUserData.setObjectMerchantCode(objectMerchant.getMerchantCode());
        bizMerchantUserData.setSubMerchantCode(subMerchants.getMerchantCode());
        bizMerchantUserData.setBizObjectMerchantUserId(objectMerchant.getBizUserId());
        bizMerchantUserData.setBizSubMerchantUserId(subMerchants.getBizUserId());
        MrcMapMerchantPrimes mrcMapMerchantPrimes = merchantPrimesService.queryByUserIdAndMerchantCode(userId, objectMerchant.getMerchantCode());
        bizMerchantUserData.setUserId(userId);
        bizMerchantUserData.setBizUserId(mrcMapMerchantPrimes.getBizUserId());
        return bizMerchantUserData;
    }

    @Override
    public List<Merchants> getObjectMerchantCodesData(String subMerchantCode) {
        List<MrcMapMerchant> list = mrcMapMerchantService.queryBySubMerchantCode(subMerchantCode);
        List<String> merchantCodeList = new ArrayList<>();
        for (MrcMapMerchant mrcMapMerchant : list) {
            merchantCodeList.add(mrcMapMerchant.getObjMerchantCode());
        }
        QueryWrapper<Merchants> queryWrapper=new QueryWrapper<>();
        if (merchantCodeList.size()>0) {
            queryWrapper.in("merchant_code", merchantCodeList);
        }
//        queryWrapper.like("type","OBJECT");
        return this.baseMapper.selectList(queryWrapper);
    }

    private boolean isMerchantCodeExist(String merchantCode) {
        LambdaQueryWrapper<Merchants> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchants::getMerchantCode,merchantCode);
        return this.count(wrapper) > 0;
    }

}
