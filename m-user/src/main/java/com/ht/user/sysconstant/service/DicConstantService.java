package com.ht.user.sysconstant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.outlets.vo.UpdateRefundPasswordVO;
import com.ht.user.sysconstant.entity.DicConstant;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
public interface DicConstantService extends IService<DicConstant> {

    /**
     * 通过groupcode获取常量列表
     * @param groupCode 组
     * @return
     */
    List<DicConstant> getListByGroupCode(String groupCode);

    /**
     * 通过常亮类型获取相关键值对
     * @param cardType
     * @return
     */
    Map<String, String> getConstantMap(String cardType);

    List<DicConstant> getKeyValue(String key);

    /**
     * 获取退款密码
     * @return
     */
    DicConstant findPassword();

    /**
     * 修改退款密码
     * @return
     */
    Boolean updatePassword(UpdateRefundPasswordVO updateRefundPasswordVO);

}
