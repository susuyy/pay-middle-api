package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.user.mall.entity.OrderCategorys;
import com.ht.user.mall.mapper.OrderCategorysMapper;
import com.ht.user.mall.service.OrderCategorysService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-16
 */
@Service
public class OrderCategorysServiceImpl extends ServiceImpl<OrderCategorysMapper, OrderCategorys> implements OrderCategorysService {

    /**
     * 查询顶级编码
     * @param categoryThreeCode
     * @param merchantCode
     * @return
     */
    @Override
    public OrderCategorys queryLevelOneCode(String categoryThreeCode, String merchantCode) {
        QueryWrapper<OrderCategorys> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("category_level03_code",categoryThreeCode);
        queryWrapper.eq("merchant_code",merchantCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Map<String, String>> getSecondLevel(String merchantCode, String firstLevelCode) {
        QueryWrapper<OrderCategorys> wrapper = new QueryWrapper<>();
        wrapper.eq("merchant_code",merchantCode);
        wrapper.eq("category_level01_code",firstLevelCode);
        wrapper.select("DISTINCT category_level02_code,category_level02_name");
        List<OrderCategorys> list = this.list(wrapper);
        List<Map<String, String>> dropDownList = new ArrayList<>();
        list.forEach(e->{
            Map<String,String> map = new HashMap<>();
            map.put("key",e.getCategoryLevel02Code());
            map.put("value",e.getCategoryLevel02Name());
            dropDownList.add(map);
        });
        return dropDownList;
    }

    @Override
    public List<Map<String, String>> getThirdLevel(String merchantCode, String secondLevelCode) {
        QueryWrapper<OrderCategorys> wrapper = new QueryWrapper<>();
        wrapper.eq("merchant_code",merchantCode);
        wrapper.eq("category_level02_code",secondLevelCode);
        wrapper.select("DISTINCT category_level03_code,category_level03_name");
        List<OrderCategorys> list = this.list(wrapper);
        List<Map<String, String>> dropDownList = new ArrayList<>();
        list.forEach(e->{
            Map<String,String> map = new HashMap<>();
            map.put("key",e.getCategoryLevel03Code());
            map.put("value",e.getCategoryLevel03Name());
            dropDownList.add(map);
        });
        return dropDownList;
    }

    @Override
    public Boolean checkCategoryCodeExist(String levelType, String code) {
        LambdaQueryWrapper<OrderCategorys> wrapper = new LambdaQueryWrapper<>();
        switch (levelType) {
            case "level2" :
                wrapper.eq(OrderCategorys::getCategoryLevel02Code,code);
                break;
            case "level3":
                wrapper.eq(OrderCategorys::getCategoryLevel03Code,code);
                break;
            default:break;
        }
        return !CollectionUtils.isEmpty(this.list());
    }

    /**
     * 通过分类code，获取分类名称
     * @param categoryLevel02Code
     * @return
     */
    @Override
    public String getCategory02NameByCode(String categoryLevel02Code){
        LambdaQueryWrapper<OrderCategorys> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderCategorys::getCategoryLevel02Code,categoryLevel02Code);
        OrderCategorys categories = this.getOne(lambdaQueryWrapper,false);
        if (categories!=null) {
            return categories.getCategoryLevel02Name();
        }
        return "";
    }
}
