package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.card.entity.CardCards;
import com.ht.user.card.entity.CardLimits;
import com.ht.user.card.entity.CardMapMerchantCards;
import com.ht.user.card.mapper.CardCardsMapper;
import com.ht.user.card.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.result.CodeExistException;
import com.ht.user.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 卡定义 服务实现类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Service
public class CardCardsServiceImpl extends ServiceImpl<CardCardsMapper, CardCards> implements CardCardsService {

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;


    /**
     * 根据卡号查询卡信息
     *
     * @param cardCode
     * @return
     */
    @Override
    public CardCards queryByCardCode(String cardCode) {
        CardCards cardCards = this.baseMapper.selectByCardCode(cardCode);
        return cardCards;
    }



    @Override
    public CardCards selectByCardCode(String cardCode){
        return this.baseMapper.selectByCardCode(cardCode);
    }

    @Override
    public boolean save(CardCards cards) {
        if (this.queryByCardCode(cards.getCardCode())!=null){
            throw new CodeExistException("卡号已存在");
        }
        return super.save(cards);
    }

}
