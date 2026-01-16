package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.*;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.common.CardLimitType;
import com.ht.user.card.common.CardType;
import com.ht.user.card.entity.*;
import com.ht.user.card.mapper.CardMapMerchantCardsMapper;
import com.ht.user.card.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.config.UserCardsStateConfig;
import com.ht.user.sysconstant.DbConstantGroupConfig;
import org.springframework.beans.BeanUtils;
import com.ht.user.config.MerchantCardsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商家卡券 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Service
public class CardMapMerchantCardsServiceImpl extends ServiceImpl<CardMapMerchantCardsMapper, CardMapMerchantCards> implements CardMapMerchantCardsService {

    @Autowired
    private CardCardsService cardCardsService;

    @Autowired
    private CardMapMerchantCardsService merchantCardsService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private DistributeTraceService distributeTraceService;

    @Autowired
    private CardProfilesService cardProfilesService;

    /**
     * 根据卡号查询商家卡券
     *
     * @param cardCode
     * @param batchCode
     * @return
     */
    @Override
    public CardMapMerchantCards getCardMsgByCardCode(String cardCode, String batchCode) {
        QueryWrapper<CardMapMerchantCards> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_code", cardCode);
        queryWrapper.eq("batch_code", batchCode);
        this.list(queryWrapper);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据商户号 分类 获取商家 上架 卡券列表
     *
     * @param merchantCode
     * @param type
     * @return
     */
    @Override
    public List<CardMapMerchantCards> queryListByMerchantCode(String merchantCode, String type) throws ParseException {
        List<CardMapMerchantCards> cardMapMerchantCards = null;
        if (MerchantCardsType.SELL.equals(type)) {
            cardMapMerchantCards = this.baseMapper.selectListByMerchantCodeAndTypeSale(merchantCode, MerchantCardsType.SELL);
        } else if (MerchantCardsType.NEED_TOP_UP.equals(type)) {
            cardMapMerchantCards = this.baseMapper.selectListByMerchantCodeAndTypeSale(merchantCode, MerchantCardsType.NEED_TOP_UP);
        } else if (MerchantCardsType.FREE.equals(type)) {
            cardMapMerchantCards = this.baseMapper.selectListByMerchantCodeAndTypeSale(merchantCode, MerchantCardsType.FREE);
        } else {
            cardMapMerchantCards = this.baseMapper.selectListByMerchantCodeAndTypeSaleNoType(merchantCode);
        }
        Date now = new Date();
        System.out.println(cardMapMerchantCards);
        List<CardMapMerchantCards> collect = cardMapMerchantCards.stream().filter(e->{if (e.getOnSaleDate()!=null && e.getHaltSaleDate()!=null){
            return now.after(e.getOnSaleDate())&&now.before(e.getHaltSaleDate());
        }
        return true;}).collect(Collectors.toList());
        return collect;
    }

    @Override
    public CardMapMerchantCards queryByCardCodeAndMerchantCode(String cardCode, String merchantCode) {
        return this.baseMapper.selectByCardCodeAndMerchantCode(cardCode, merchantCode);
    }

    @Override
    public CardMapMerchantCards queryByCardCodeAndMerchantCodeBatchCode(String cardCode, String merchantCode,String batchCode) {
        return this.baseMapper.selectByCardCodeAndMerchantCodeAndBatchCode(cardCode, merchantCode,batchCode);
    }

    @Override
    public List<CardListVo> getCardsByMerchantCode(String merchantCode, IPage<CardListVo> page, CodeSearch codeSearch) {
        return this.baseMapper.getCardsByMerchantCode(merchantCode, page, codeSearch);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMerchantCards(CardEditVo card) {
        CardCards cardEntity = new CardCards();
        BeanUtils.copyProperties(card, cardEntity);
        cardEntity.setCardCode(IdWorker.getIdStr());
        if (card.getValidType()!=null && CardConstant.CARD_VALID_TYPE_BEGIN_TO_DURATION.equals(card.getValidType())) {
            cardEntity.setPeriodOfValidity(card.getEffectFromActive() * 24);
            cardEntity.setValidGapAfterApplied(card.getActiveFromGet() * 24);
        }
        if (StringUtils.isEmpty(card.getCardPicUrl())){
            cardEntity.setCardPicUrl(CardConstant.CARD_DEFAULT_BANNER);
        }
        if (CardType.COUPON.getKey().equals(card.getType())){
            cardEntity.setFaceValue(card.getFaceValue()*100);
        }
        cardEntity.setPrice(0);
        cardEntity.setState(CardConstant.CARD_STATE_NORMAL);
        cardEntity.setValidityType(card.getValidType());
        cardCardsService.save(cardEntity);
        saveCardProfiles(card,cardEntity.getCardCode());
        saveCardLimits(card, cardEntity);
        saveMapMerchantCards(card, cardEntity);
    }

    private void saveCardProfiles(CardEditVo card, String cardCode) {
        if (!CollectionUtils.isEmpty(card.getProfiles())){
            Assert.isTrue(card.getProfiles().size()<=3,"标签数目多于3条");
            card.getProfiles().forEach(e->{
                if (!StringUtils.isEmpty(e)){
                    CardProfiles profiles = new CardProfiles();
                    profiles.setCardCode(cardCode);
                    profiles.setGroupCode(DbConstantGroupConfig.CARD_USER_RULE);
                    profiles.setKey(e);
                    profiles.setValue(e);
                    profiles.setState("1");
                    profiles.setType("1");
                    cardProfilesService.save(profiles);
                }
            });
        }
    }

    private void createCardLimit(UserFreeCard userFreeCard,String batchCode) {
        cardLimitsService.createLimit(CardLimitType.GET_DURATION_LIMIT,userFreeCard.getCardCode(),userFreeCard.getDurationLimit(), batchCode);
        cardLimitsService.createLimit(CardLimitType.GET_DAILY_LIMIT,userFreeCard.getCardCode(),userFreeCard.getDailyLimit(), batchCode);
        cardLimitsService.createLimit(CardLimitType.GET_TOTAL_LIMIT,userFreeCard.getCardCode(),userFreeCard.getTotalLimit(), batchCode);
        if (!CollectionUtils.isEmpty(userFreeCard.getDayLimit())){
            userFreeCard.getDayLimit().forEach(e->  cardLimitsService.createLimit(CardLimitType.GET_DAY_LIMIT,userFreeCard.getCardCode(),e, batchCode));
        }
        if (!CollectionUtils.isEmpty(userFreeCard.getWeekLimit())){
            userFreeCard.getWeekLimit().forEach(e->  cardLimitsService.createLimit(CardLimitType.GET_WEEK_LIMIT,userFreeCard.getCardCode(),e, batchCode));
        }
        if (!CollectionUtils.isEmpty(userFreeCard.getHourLimit())){
            userFreeCard.getHourLimit().forEach(e->  cardLimitsService.createLimit(CardLimitType.GET_HOUR_LIMIT,userFreeCard.getCardCode(),e, batchCode));
        }
    }

    @Override
    public void createUserFreeCards(UserFreeCard userFreeCard, String merchantCode) {
        CardCards cards = cardCardsService.selectByCardCode(userFreeCard.getCardCode());
        Assert.notNull(cards,"卡号不存在");
        createMerchantCard(cards,MerchantCardsType.FREE,merchantCode,userFreeCard.getBatchCode());
        distributeTraceService.createDistributeTrace(merchantCode,userFreeCard.getCardCode(),userFreeCard.getInventory(),"后台用户领券",userFreeCard.getAdminMerchantCode(),"", userFreeCard.getBatchCode());
        userFreeCard.getMemberLevelsLimit().forEach(e->cardLimitsService.createLimit(CardLimitType.GET_LEVEL_LIMIT,userFreeCard.getCardCode(),e,userFreeCard.getBatchCode()));
        createCardLimit(userFreeCard,userFreeCard.getBatchCode());
    }

    private void createMerchantCard(CardCards cards,String type,String merchantCode,String batchCode) {
        CardMapMerchantCards merchantCards = new CardMapMerchantCards();
        merchantCards.setMerchantCode(merchantCode);
        merchantCards.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_Y);
        merchantCards.setType(type);
        merchantCards.setState(CardConstant.MERCHANT_CARD_STATE_NORMAL);
        merchantCards.setCardType(cards.getType());
        merchantCards.setCardFaceValue(String.valueOf(cards.getFaceValue()));
        merchantCards.setCardName(cards.getCardName());
        merchantCards.setCardCode(cards.getCardCode());
        merchantCards.setBatchCode(batchCode);
        this.saveOrUpdate(merchantCards);
    }

    @Override
    public CardMapMerchantCards getCard(String merchantCode, String cardCode) {
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardMapMerchantCards::getMerchantCode, merchantCode);
        wrapper.eq(CardMapMerchantCards::getCardCode, cardCode);
        List<CardMapMerchantCards> merchantCards = this.list(wrapper);
        if (CollectionUtils.isEmpty(merchantCards)){
            return null;
        }
        return merchantCards.get(0);
    }

    @Override
    public List<MerchantUserCardVo> getUserCardList(String merchantCode, IPage<MerchantUserCardVo> page) {
        return this.baseMapper.getUserCardList(merchantCode, page);
    }

    @Override
    public List<CardMapMerchantCards> getPosCardList(String merchantCode,String cardName) {
        return this.baseMapper.getPosCardList(merchantCode,cardName);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean createBatchMerchantCard(MerchantCardEditVo cards,String merchantCode,String cardCode){
        CardMapMerchantCards merchantCards = new CardMapMerchantCards();

        CardCards cardCards = cardCardsService.selectByCardCode(cardCode);

        merchantCards.setMerchantCode(merchantCode);
        merchantCards.setCardCode(cardCode);
        merchantCards.setBatchCode(cards.getBatchCode());
        merchantCards.setCardType(cardCards.getType());
        merchantCards.setCardName(cards.getCardName());
        merchantCards.setPrice(cards.getPrice().intValue());
        merchantCards.setReferencePrice(cards.getReferencePrice().intValue());
        merchantCards.setType(MerchantCardsType.SELL);
        merchantCards.setState(CardConstant.MERCHANT_CARD_STATE_NORMAL);
        merchantCards.setCardFaceValue(String.valueOf(cardCards.getFaceValue()));
        merchantCards.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_Y);
        merchantCards.setOnSaleDate(cards.getOnSaleDate());
        merchantCards.setHaltSaleDate(cards.getHaltSaleDate());
        this.save(merchantCards);

        return true;
    }

    @Override
    public String getShowTimeScope(CardMapMerchantCards cardMapMerchantCards) throws ParseException {
        List<CardLimits> getCardLimitList = cardLimitsService.queryCardGetLimit(cardMapMerchantCards.getCardCode(), cardMapMerchantCards.getBatchCode());
        ActiveTimeScope activity = new ActiveTimeScope();
        for (CardLimits cardLimits : getCardLimitList) {
            String type = cardLimits.getType();
            String limitKey = cardLimits.getLimitKey();
            if (CardLimitType.GET_DURATION_LIMIT.equals(type)){
                activity.setGetTimeScope(limitKey);
            }
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (MerchantCardsType.SELL.equals(cardMapMerchantCards.getType())){
            Date onSaleDate = cardMapMerchantCards.getOnSaleDate();
            String dateStr = simpleDateFormat.format(onSaleDate);
            Date haltSaleDate = cardMapMerchantCards.getHaltSaleDate();
            if (haltSaleDate==null){
                dateStr = dateStr+" 至 永久";
            }else {
                String haltTime = simpleDateFormat.format(haltSaleDate);
                dateStr = dateStr + " 至 "+haltTime;
            }
            return dateStr;
        }
        if (MerchantCardsType.FREE.equals(cardMapMerchantCards.getType())){
            String activityTimeScope = activity.getGetTimeScope();
            String[] split = activityTimeScope.split("~");
            Date parse = simpleDateFormat.parse(split[0]);
            String format = simpleDateFormat.format(parse);
            Date parseEnd = simpleDateFormat.parse(split[1]);
            String formatEnd = simpleDateFormat.format(parseEnd);
            String dateStr = format + " 至 " + formatEnd ;
            return dateStr;
        }
        String format = simpleDateFormat.format(new Date());
        return format+" 至 永久";
    }

    @Override
    public CardCards getShowOtherData(CardMapMerchantCards cardMapMerchantCard) {
        CardCards queryCard = cardCardsService.queryByCardCode(cardMapMerchantCard.getCardCode());
        queryCard.setMerchantCardType(cardMapMerchantCard.getType());
        queryCard.setBatchCode(cardMapMerchantCard.getBatchCode());
        queryCard.setPrice(cardMapMerchantCard.getPrice());
        queryCard.setCardName(cardMapMerchantCard.getCardName());
        queryCard.setOnSaleDate(cardMapMerchantCard.getOnSaleDate());
        queryCard.setHaltSaleDate(cardMapMerchantCard.getHaltSaleDate());
        return queryCard;
    }

    @Override
    public List<String> getCardMerchants(String cardCode) {
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardMapMerchantCards::getCardCode,cardCode);
        List<CardMapMerchantCards> list = this.list(wrapper);
        return list.stream().map(CardMapMerchantCards::getMerchantCode).distinct().collect(Collectors.toList());
    }

    @Override
    public List<MerchantCardListVo> getObjectAndSonMerchantCards(List<String> merchantCodes, MerchantCardSearch merchantCardSearch, IPage<MerchantCardListVo> page) {
        return this.baseMapper.getObjectAndSonMerchantCards(merchantCodes,merchantCardSearch,page);
    }

    /**
     * 封装卡券 使用时间
     * @param cardCards
     * @return
     */
    @Override
    public String packageValidTimeStr(CardCards cardCards) {
        if ("beginToEnd".equals(cardCards.getValidityType())){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return "有效期："+ simpleDateFormat.format(cardCards.getValidFrom()).substring(0,16) + " ~ " + simpleDateFormat.format(cardCards.getValidTo()).substring(0,16);
        } else if ("validDuration".equals(cardCards.getValidityType())){
            return "领券后" +cardCards.getValidGapAfterApplied()/24 + "天生效，有效"+cardCards.getPeriodOfValidity()/24+"天";
        }
        return "无限制";
    }

    @Override
    public CardMapMerchantCards queryByCardCodeAndBatchCode(String cardCode, String batchCode) {
        return this.baseMapper.selectByCardCodeAndBatchCode(cardCode,batchCode);
    }

    @Override
    public CardMapMerchantCards mallQueryCodeMerchantCodeType(String cardCode, String storeMerchantCode, String type) {
        QueryWrapper<CardMapMerchantCards> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("card_code",cardCode);
        queryWrapper.eq("merchant_code",storeMerchantCode);
        queryWrapper.eq("type",type);
        CardMapMerchantCards cardMapMerchantCards = getOne(queryWrapper,false);
        CardCards cardCards = cardCardsService.queryByCardCode(cardCode);
        cardMapMerchantCards.setCategoryCode(cardCards.getCategoryCode());
        cardMapMerchantCards.setCategoryName(cardCards.getCategoryName());
        cardMapMerchantCards.setCardCardsState(cardCards.getState());
        return cardMapMerchantCards;
    }

    private void saveMapMerchantCards(CardEditVo card, CardCards cardEntity) {
        Assert.isTrue(!CollectionUtils.isEmpty(card.getMerchantCodes()),"请选择子商铺");
        card.getMerchantCodes().forEach(e->{
            CardMapMerchantCards merchantCards = new CardMapMerchantCards();
            merchantCards.setCardCode(cardEntity.getCardCode());
            merchantCards.setMerchantCode(e);
            merchantCards.setCardName(card.getCardName());
            merchantCards.setCardType(card.getType());
            merchantCards.setState(CardConstant.MERCHANT_CARD_STATE_NORMAL);
            merchantCards.setType(MerchantCardsType.TEMPLATE);
            merchantCards.setCardFaceValue(String.valueOf(card.getFaceValue()));
            merchantCards.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_N);
            merchantCardsService.save(merchantCards);
        });
    }

    @Override
    public List<MerchantCardListVo> getCardProductsByMerchantCode(String merchantCode, MerchantCardSearch search, IPage<MerchantCardListVo> page) {
        return this.baseMapper.getCardProductsByMerchantCode(merchantCode, search, page);
    }

    private void saveCardLimits(CardEditVo card, CardCards cardEntity) {
        List<CardLimits> cardLimitsList = new ArrayList<>();
        if (card.getLimitDay() != null) {
            card.getLimitDay().forEach(e -> {
                CardLimits cardLimits = createLimit(CardLimitType.LIMIT_DAY, cardEntity.getType(), cardEntity.getCardCode(), e);
                cardLimitsList.add(cardLimits);
            });
        }
        if (card.getLimitHour() != null) {
            card.getLimitHour().forEach(e -> {
                CardLimits cardLimits = createLimit(CardLimitType.LIMIT_HOUR, cardEntity.getType(), cardEntity.getCardCode(), e);
                cardLimitsList.add(cardLimits);
            });
        }
        if (card.getLimitWeek() != null) {
            card.getLimitWeek().forEach(e -> {
                CardLimits cardLimits = createLimit(CardLimitType.LIMIT_WEEK, cardEntity.getType(), cardEntity.getCardCode(), e);
                cardLimitsList.add(cardLimits);
            });
        }
        if (CardType.COUPON.getKey().equals(card.getType())) {
            cardLimitsList.add(createLimit(CardLimitType.LIMIT_UNIT, cardEntity.getType(), cardEntity.getCardCode(), card.getLimitUnit().toString()));
            cardLimitsList.add(createLimit(CardLimitType.LIMIT_TOTAL, cardEntity.getType(), cardEntity.getCardCode(), String.valueOf(card.getLimitTotal()*100)));
        }
        cardLimitsService.saveBatch(cardLimitsList);
    }

    private CardLimits createLimit(String limitType, String cardType, String cardCode, String value) {
        CardLimits cardLimits = new CardLimits();
        cardLimits.setType(limitType);
        cardLimits.setCardType(cardType);
        cardLimits.setCardCode(cardCode);
        cardLimits.setLimitKey(value);
        cardLimits.setState("1");
        return cardLimits;
    }

    @Override
    public IPage<CardMapMerchantCards> getUserCardInCardCodes(List<String> cardCodes, String type, Integer pageNo, Integer pageSize){
        IPage<CardMapMerchantCards> page = new Page<>(pageNo,pageSize);
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isEmpty(cardCodes)){
            return new Page<>();
        }
        wrapper.in(CardMapMerchantCards::getCardCode,cardCodes);
        wrapper.eq(CardMapMerchantCards::getType,type);
        return this.baseMapper.selectPage(page,wrapper);
    }

    @Override
    public  IPage<CardMapMerchantCards> getMallSellCard(List<String> merchantCodes, Long pageNo, Long pageSize, String productionName, String productionCode, String onSaleState){
        Page<CardMapMerchantCards> page = new Page<>(pageNo,pageSize);
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CardMapMerchantCards::getMerchantCode,merchantCodes);
        wrapper.eq(CardMapMerchantCards::getType,CardConstant.MALL_SELL_CARD);
        if (!StringUtils.isEmpty(productionName)){
            wrapper.like(CardMapMerchantCards::getCardName,productionName);
        }
        if (!StringUtils.isEmpty(productionCode)){
            wrapper.like(CardMapMerchantCards::getCardCode,productionCode);
        }
        if (!(StringUtils.isEmpty(onSaleState)||CardConstant.ON_SALE_STATE_ALL.equals(onSaleState))){
            wrapper.eq(CardMapMerchantCards::getOnSaleState,onSaleState);
        }
        return this.baseMapper.selectPage(page,wrapper);
    }

    @Override
    public CardMapMerchantCards getMerchantCard(String merchantCode, String cardCode){
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardMapMerchantCards::getMerchantCode,merchantCode);
        wrapper.eq(CardMapMerchantCards::getCardCode,cardCode);
        return this.getOne(wrapper,false);
    }

    @Override
    public IPage<CardMapMerchantCards> getCardProductionPage(List<String> merchantCodes, String state, Long pageNo, Long pageSize, String type) {
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        Page<CardMapMerchantCards> page = new Page<>(pageNo,pageSize);
        if (!StringUtils.isEmpty(state)){
            wrapper.eq(CardMapMerchantCards::getState,state);
        }
        wrapper.in(CardMapMerchantCards::getMerchantCode,merchantCodes);
        if (!StringUtils.isEmpty(type)){
            wrapper.eq(CardMapMerchantCards::getType,type);
        }
        return this.baseMapper.selectPage(page,wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMallCoupon(CardCards card, String merchantCode) {
        cardCardsService.save(card);

        if (!CollectionUtils.isEmpty(card.getLimits())){
            card.getLimits().forEach(e->cardLimitsService.save(e));
        }
        CardMapMerchantCards cardMapMerchantCards = new CardMapMerchantCards();
        cardMapMerchantCards.setHaltSaleDate(card.getHaltSaleDate());
        cardMapMerchantCards.setOnSaleDate(card.getOnSaleDate());
        cardMapMerchantCards.setOnSaleState("Y");
        cardMapMerchantCards.setPrice(0);
        cardMapMerchantCards.setCategoryCode(card.getCategoryCode());
        cardMapMerchantCards.setType(UserCardsStateConfig.MALL_FREE);
        cardMapMerchantCards.setCardType(card.getType());
        cardMapMerchantCards.setMerchantCode(merchantCode);
        cardMapMerchantCards.setCardCode(card.getCardCode());
        cardMapMerchantCards.setCardName(card.getCardName());
        cardMapMerchantCards.setState(CardConstant.MERCHANT_CARD_STATE_NORMAL);
        cardMapMerchantCards.setCardFaceValue(String.valueOf(card.getFaceValue()));
        cardMapMerchantCards.setBatchCode(card.getBatchCode());
        this.save(cardMapMerchantCards);
    }

    @Override
    public boolean save(CardMapMerchantCards cardMapMerchantCards) {
//        if(this.getCard(cardMapMerchantCards.getMerchantCode(),cardMapMerchantCards.getCardCode())!=null){
//            throw new CodeExistException("卡号已存在");
//        }
        return super.save(cardMapMerchantCards);
    }

    @Override
    public void createActivityCard(CardCards cards, String merchantCode, String activityCode) {
        CardMapMerchantCards cardMapMerchantCards = new CardMapMerchantCards();
        cardMapMerchantCards.setCardName(cards.getCardName());
        cardMapMerchantCards.setCardCode(cards.getCardCode());
        cardMapMerchantCards.setMerchantCode(merchantCode);
        cardMapMerchantCards.setCardType(cards.getType());
        cardMapMerchantCards.setType(CardConstant.ACTIVITY_CARD);
        cardMapMerchantCards.setState(CardConstant.CARD_STATE_NORMAL);
        cardMapMerchantCards.setCardFaceValue(cards.getFaceValue().toString());
        cardMapMerchantCards.setPrice(0);
        cardMapMerchantCards.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_N);
        cardMapMerchantCards.setBatchCode(activityCode);
        this.save(cardMapMerchantCards);
    }

    @Override
    public CardMapMerchantCards getActivityCard(String cardCode, String merchantCode) {
        LambdaQueryWrapper<CardMapMerchantCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardMapMerchantCards::getCardCode,cardCode);
        wrapper.eq(CardMapMerchantCards::getMerchantCode,merchantCode);
        wrapper.eq(CardMapMerchantCards::getType,CardConstant.ACTIVITY_CARD);
        return this.getOne(wrapper,false);
    }
}
