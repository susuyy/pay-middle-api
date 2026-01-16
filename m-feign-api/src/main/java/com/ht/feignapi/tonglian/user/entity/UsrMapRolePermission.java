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
public class UsrMapRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String roleCode;

    private String permissionCode;

    private String mapState;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
