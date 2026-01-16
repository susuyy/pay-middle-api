package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.card.entity.CardElectronicSell;
import com.ht.user.card.entity.CardOrderDetails;
import com.ht.user.card.entity.CardOrders;
import com.ht.user.card.entity.PrimeBuyCardData;
import com.ht.user.card.mapper.CardOrderDetailsMapper;
import com.ht.user.card.service.CardOrderDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.mall.entity.OrderOrderDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单明细 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Service
public class CardOrderDetailsServiceImpl extends ServiceImpl<CardOrderDetailsMapper, CardOrderDetails> implements CardOrderDetailsService {

    /**
     * 订单明细 根据明细id 查询
     * @param detailId
     * @return
     */
    @Override
    public CardOrderDetails queryByDetailId(String detailId) {
        return this.baseMapper.selectById(Long.parseLong(detailId));
    }

    /**
     * 根据订单号查询订单明细
     * @param orderCode
     * @return
     */
    @Override
    public List<CardOrderDetails> queryByOrderCode(String orderCode) {
        QueryWrapper<CardOrderDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * 修改订单明细状态 通过orderCode
     * @param orderCode
     * @param paid
     * @param date
     */
    @Override
    public void updateStateByOrderCode(String orderCode, String paid, Date date) {
        this.baseMapper.updateStateByOrderCode(orderCode,paid,date);
    }

    @Override
    public void createPrimeBuyCardOrderDetails(PrimeBuyCardData primeBuyCardData, CardOrders cardOrders) {
        List<CardElectronicSell> cardElectronicSellList = primeBuyCardData.getCardElectronicSellList();
        for (CardElectronicSell cardElectronicSell : cardElectronicSellList) {
            CardOrderDetails cardOrderDetails = new CardOrderDetails();
            cardOrderDetails.setOrderCode(cardOrders.getOrderCode());
            cardOrderDetails.setMerchantCode(cardOrders.getMerchantCode());
            cardOrderDetails.setQuantity(new BigDecimal(cardElectronicSell.getQuantity()));
            cardOrderDetails.setAmount((int) (cardElectronicSell.getSellAmount() * cardElectronicSell.getQuantity()));
            cardOrderDetails.setProductionName(cardElectronicSell.getCardName());
            cardOrderDetails.setState(cardOrders.getState());
            cardOrderDetails.setType(cardOrders.getType());
            cardOrderDetails.setDisccount(0);
            cardOrderDetails.setCreateAt(new Date());
            cardOrderDetails.setUpdateAt(new Date());
            cardOrderDetails.setBatchCode(cardElectronicSell.getBatchCode());

            cardOrderDetails.setUserPhone(primeBuyCardData.getUserPhone());
            cardOrderDetails.setCardType("online_sell");

            this.baseMapper.insert(cardOrderDetails);
        }
    }

    @Override
    public void updateProdCodeById(Long id, String proCodeListStr) {
        CardOrderDetails cardOrderDetails = new CardOrderDetails();
        cardOrderDetails.setId(id);
        cardOrderDetails.setProductionCode(proCodeListStr);
        this.baseMapper.updateById(cardOrderDetails);
    }

    @Override
    public List<CardOrderDetails> querySummaryDetails(String startTime, String endTime) {
        QueryWrapper<CardOrderDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.ge("create_at",startTime);
        queryWrapper.le("create_at",endTime);
        queryWrapper.eq("state","paid");
        queryWrapper.eq("type","prime_buy_card");
        return list(queryWrapper);
    }

    @Override
    public List<CardOrderDetails> querySummaryDetailsForMerchantCode(String merchantCode, String startTime, String endTime) {
        QueryWrapper<CardOrderDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("merchant_code",merchantCode);
        queryWrapper.ge("create_at",startTime);
        queryWrapper.le("create_at",endTime);
        queryWrapper.eq("state","paid");
        queryWrapper.eq("type","prime_buy_card");
        return list(queryWrapper);
    }
}
