package com.ht.feignapi.tonglian.merchant.entity;

import com.ht.feignapi.auth.entity.UserUsers;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MrcMapMerchantUser implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    private String merchantCode;

    private Long userId;

    private String state;

    private String type;

    private Date createAt;

    private Date updateAt;

    private UserUsers user;
}
