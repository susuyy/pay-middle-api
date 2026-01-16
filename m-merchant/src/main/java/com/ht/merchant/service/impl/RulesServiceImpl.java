package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.merchant.service.IRulesService;
import com.ht.merchant.entity.Rules;
import com.ht.merchant.mapper.RulesMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户规则主表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Service
public class RulesServiceImpl extends ServiceImpl<RulesMapper, Rules> implements IRulesService {

}
