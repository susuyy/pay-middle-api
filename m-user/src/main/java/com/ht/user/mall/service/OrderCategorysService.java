package com.ht.user.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.OrderCategorys;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-16
 */
public interface OrderCategorysService extends IService<OrderCategorys> {

    /**
     * 查询分类 根据末级分类编码
     * @param categoryThreeCode
     * @param merchantCode
     * @return
     */
    OrderCategorys queryLevelOneCode(String categoryThreeCode, String merchantCode);

    /**
     * 获取二级分类下拉框数据
     * @return
     * @param merchantCode
     * @param firstLevelCode
     */
    List<Map<String, String>> getSecondLevel(String merchantCode, String firstLevelCode);

    /**
     * 获取三级分类下拉框数据
     * @param merchantCode
     * @param secondLevelCode
     * @return
     */
    List<Map<String, String>> getThirdLevel(String merchantCode, String secondLevelCode);

    /**
     * 新增分类时，检查分类code是否已经存在
     * @param levelType 等级：level2，level3
     * @param code code
     * @return false:不存在；true：存在；
     */
    Boolean checkCategoryCodeExist(String levelType,String code);

    /**
     * 通过分类code，获取分类名称
     * @param categoryLevel02Code
     * @return
     */
    String getCategory02NameByCode(String categoryLevel02Code);
}
