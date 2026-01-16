package com.ht.feignapi.auth.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserMapUserGroup implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 分组编码
     */
    private String groupCode;

    /**
     * 状态
     */
    private String state;

    private Date createAt;

    private Date updateAt;


}
