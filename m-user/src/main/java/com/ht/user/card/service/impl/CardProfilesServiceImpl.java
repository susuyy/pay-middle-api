package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.card.entity.CardProfiles;
import com.ht.user.card.mapper.CardProfilesMapper;
import com.ht.user.card.service.CardProfilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-28
 */
@Service
public class CardProfilesServiceImpl extends ServiceImpl<CardProfilesMapper, CardProfiles> implements CardProfilesService {

    @Override
    public List<CardProfiles> queryByCardCode(String cardCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("card_code",cardCode);
        return this.baseMapper.selectList(queryWrapper);
    }
}
