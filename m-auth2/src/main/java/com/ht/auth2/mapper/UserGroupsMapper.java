package com.ht.auth2.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ht.auth2.entity.UserGroups;
import com.ht.auth2.entity.UserGroupsTree;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserGroupsMapper extends BaseMapper<UserGroups> {

    /**
     * 查询角色树
     * @return
     * @param appCode
     */
    @Select("select * from user_groups where parent_group_code = '0' And app_code = #{appCode}")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="group_code", property="groupCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "group_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserGroupsMapper.selectByParentGroupCode"))
    })
    List<UserGroupsTree> queryUserGroupsTree(String appCode);

    /**
     * 查询父菜单下的子菜单
     * @param parentGroupCode
     * @return
     */
    @Select("select * from user_groups where parent_group_code = #{parentGroupCode}")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="group_code", property="groupCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "group_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserGroupsMapper.selectByParentGroupCode"))
    })
    List<UserGroupsTree> selectByParentGroupCode(@Param("parentGroupCode") String parentGroupCode);

    /**
     * 查询角色树 带条件
     * @return
     * @param
     */
    @Select("select * from user_groups ${ew.customSqlSegment} AND parent_group_code = '0' ")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="group_code", property="groupCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "group_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserGroupsMapper.selectByParentGroupCode"))
    })
    List<UserGroupsTree> selectGroupTreeList(@Param(Constants.WRAPPER)QueryWrapper queryWrapper);
}
