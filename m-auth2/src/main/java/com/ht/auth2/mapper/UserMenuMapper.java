package com.ht.auth2.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ht.auth2.entity.UserGroupsTree;
import com.ht.auth2.entity.UserMenu;
import com.ht.auth2.entity.UserMenuTree;
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
public interface UserMenuMapper extends BaseMapper<UserMenu> {



    /**
     * 查询菜单树 带查询条件
     * @return
     * @param appCode
     * @param queryWrapper
     */
    @Select("select * from user_menu ${ew.customSqlSegment} AND parent_menu_code = '0' AND app_code = #{appCode}")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="menu_code", property="menuCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "menu_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserMenuMapper.selectByParentMenuCode"))
    })
    List<UserMenuTree> queryUserMenuTree(String appCode, @Param(Constants.WRAPPER)QueryWrapper queryWrapper);


    /**
     * 查询父菜单下的子菜单
     * @param parentMenuCode
     * @return
     */
    @Select("select * from user_menu where parent_menu_code = #{parentMenuCode}")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="menu_code", property="menuCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "menu_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserMenuMapper.selectByParentMenuCode"))
    })
    List<UserMenuTree> selectByParentMenuCode(@Param("parentMenuCode") String parentMenuCode);


    /**
     * 查询菜单树 不带查询条件
     * @return
     * @param appCode
     */
    @Select("select * from user_menu where parent_menu_code = '0' AND app_code = #{appCode}")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="menu_code", property="menuCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "menu_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserMenuMapper.selectByParentMenuCode"))
    })
    List<UserMenuTree> queryUserMenuTreeAll(String appCode);

    /**
     * 查询菜单树 带查询条件
     * @return
     * @param queryWrapper
     */
    @Select("select * from user_menu ${ew.customSqlSegment} AND parent_menu_code = '0' ")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.BIGINT, id=true),
            @Result(column="menu_code", property="menuCode", jdbcType= JdbcType.VARCHAR, id=true),
            @Result(property = "children",column = "menu_code",
                    many = @Many(select = "com.ht.auth2.mapper.UserMenuMapper.selectByParentMenuCode"))
    })
    List<UserMenuTree> selectMenuTreeList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);


    /**
     * 查询菜单树 带查询条件
     * @return
     * @param appCode
     * @param queryWrapper
     */
    @Select("select * from user_menu ${ew.customSqlSegment} AND parent_menu_code = '0' AND app_code = #{appCode}")
    List<UserMenuTree> queryUserMenuTreeNoAll(@Param("appCode") String appCode, @Param(Constants.WRAPPER)QueryWrapper queryWrapper);

    /**
     * 查询子类菜单树 带查询条件
     * @return
     * @param menuCode
     * @param queryWrapper
     */
    @Select("select * from user_menu ${ew.customSqlSegment} AND parent_menu_code = #{menuCode}")
    List<UserMenuTree> querySonMenuTree(@Param("menuCode") String menuCode, @Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 查询菜单树 带查询条件
     * @return
     * @param queryWrapper
     */
    @Select("select * from user_menu ${ew.customSqlSegment} AND parent_menu_code = '0' ")
    List<UserMenuTree> queryMenuTreeNotAllNotAppCode(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
