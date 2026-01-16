package com.ht.user.sysconstant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ht.user.sysconstant.entity.DicRegion;
import com.ht.user.sysconstant.mapper.DicRegionMapper;
import com.ht.user.sysconstant.service.DicRegionService;
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
public class DicRegionServiceImpl extends ServiceImpl<DicRegionMapper, DicRegion> implements DicRegionService {

    @Override
    public List<DicRegion> getProvinces(Integer regionid) {
        LambdaQueryWrapper<DicRegion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DicRegion::getPid,regionid);
        return this.list(wrapper);
    }
}
