package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户角色对应
 * </p>
 *
 * @author ${author}
 * @since 2020-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UsrMapUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 对应状态
     */
    private String mapState;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
