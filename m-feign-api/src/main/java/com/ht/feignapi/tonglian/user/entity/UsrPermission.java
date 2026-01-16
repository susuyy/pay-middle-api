package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author ${author}
 * @since 2020-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UsrPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 权限码
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限状态
     */
    private String permissionState;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
