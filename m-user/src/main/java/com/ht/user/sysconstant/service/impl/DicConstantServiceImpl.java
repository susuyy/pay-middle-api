package com.ht.user.sysconstant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.vo.UpdateRefundPasswordVO;
import com.ht.user.result.ResultTypeEnum;
import com.ht.user.sysconstant.entity.DicConstant;
import com.ht.user.sysconstant.mapper.DicConstantMapper;
import com.ht.user.sysconstant.service.DicConstantService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@Service
public class DicConstantServiceImpl extends ServiceImpl<DicConstantMapper, DicConstant> implements DicConstantService {
    @Override
    public List<DicConstant> getListByGroupCode(String groupCode){
        LambdaQueryWrapper<DicConstant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DicConstant::getGroupCode,groupCode);
        return this.list(wrapper);
    }


    @Override
    public Map<String, String> getConstantMap(String cardType) {
        List<DicConstant> constants = this.getListByGroupCode(cardType);
        Map<String, String> map = new HashMap<>();
        constants.forEach(e -> {
            map.put(e.getKey(), e.getValue());
        });
        return map;
    }

    @Override
    public List<DicConstant> getKeyValue(String key) {
        LambdaQueryWrapper<DicConstant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DicConstant::getKey, key);
        return this.list(wrapper);
    }

    @Override
    public DicConstant findPassword() {
        return this.baseMapper.selectPassword();
    }

    @Override
    public Boolean updatePassword(UpdateRefundPasswordVO updateRefundPasswordVO) {
        //判断退款密码
        DicConstant dicConstant = findPassword();
        if (!dicConstant.getValue().equals(updateRefundPasswordVO.getOldPassword())) {
            return false;
        }
        //修改密码
        this.baseMapper.updatePassword(updateRefundPasswordVO.getNewPassword());
        return true;
    }
}
