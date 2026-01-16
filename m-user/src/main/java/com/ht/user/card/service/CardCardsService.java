package com.ht.user.card.service;

import com.ht.user.card.entity.CardCards;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 卡定义 服务类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
public interface CardCardsService extends IService<CardCards> {

    /**
     * 根据卡号查询卡信息
     *
     * @param cardCode
     * @return
     */
    CardCards queryByCardCode(String cardCode);


    /**
     * 根据卡号查询卡信息
     * @param cardCode
     * @return
     */
    CardCards selectByCardCode(String cardCode);


    @Override
    boolean save(CardCards cards);

}
