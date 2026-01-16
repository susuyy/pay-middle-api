package com.ht.user.sysconstant.service;

import com.ht.user.sysconstant.entity.DicRegion;
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
public interface DicRegionService extends IService<DicRegion> {

    List<DicRegion> getProvinces(Integer regionid);
}
