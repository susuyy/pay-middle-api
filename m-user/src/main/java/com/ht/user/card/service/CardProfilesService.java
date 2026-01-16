package com.ht.user.card.service;

import com.ht.user.card.entity.CardProfiles;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-28
 */
public interface CardProfilesService extends IService<CardProfiles> {

    /**
     * 根据卡号查询使用标签
     * @param cardCode
     * @return
     */
    List<CardProfiles> queryByCardCode(String cardCode);
}
