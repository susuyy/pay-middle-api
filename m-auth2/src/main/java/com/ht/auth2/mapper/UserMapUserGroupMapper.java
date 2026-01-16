package com.ht.auth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.auth2.entity.UserMapUserGroup;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserMapUserGroupMapper extends BaseMapper<UserMapUserGroup> {

    /**
     * 根据userId查询用户所在分组
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user_map_user_group WHERE user_id = #{userId}")
    List<UserMapUserGroup> selectByUserId(Long userId);


}
