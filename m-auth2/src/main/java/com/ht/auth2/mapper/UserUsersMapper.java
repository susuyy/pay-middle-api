package com.ht.auth2.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.auth2.entity.UserUsers;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserUsersMapper extends BaseMapper<UserUsers> {


    /**
     * 根据账号查询用户
     * @param account
     * @return
     */
    @Select("SELECT * FROM user_users WHERE account = #{account} AND app_code = #{appCode}")
    UserUsers selectByAccount(@Param("account") String account,@Param("appCode") String appCode);

    /**
     * 根据账号查询用户
     * @param account
     * @return
     */
    @Select("SELECT * FROM user_users WHERE account = #{account}")
    UserUsers selectByAccountNoCode(@Param("account") String account);
}
