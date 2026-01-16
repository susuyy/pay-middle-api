package com.ht.auth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.auth2.entity.UserMapRoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
public interface UserMapRoleMenuMapper extends BaseMapper<UserMapRoleMenu> {

    /**
     * 根据 角色编码 删除 对应菜单关联
     * @param roleCode
     */
    @Delete("delete from user_map_role_menu where role_code = #{roleCode}")
    void deleteByRoleCode(@Param("roleCode") String roleCode);

}
