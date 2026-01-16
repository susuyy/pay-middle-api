package com.ht.user.sysconstant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.sysconstant.entity.DicConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-24
 */
@Mapper
public interface DicConstantMapper extends BaseMapper<DicConstant> {

    @Select("SELECT * FROM dic_constant WHERE `key` = 'REFUND_PASSWORD' AND group_code = 'refund_password'")
    DicConstant selectPassword();

    @Update("UPDATE dic_constant SET `value` = #{password} WHERE `key` = 'REFUND_PASSWORD' AND `group_code` = 'refund_password'")
    void updatePassword(@Param("password") String password);

}
