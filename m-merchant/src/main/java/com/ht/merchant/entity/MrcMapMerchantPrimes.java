package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户-会员对应表
 * </p>
 *
 * @author ${author}
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mrc_map_merchant_primes")
public class MrcMapMerchantPrimes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String openId;

    private String tel;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态
     */
    private String state;

    /**
     * 通商云 bizUserId
     */
    private String bizUserId;

    /**
     * 通商云 userId
     */
    private String tsyUserId;

    /**
     * 会员类型
     */
    private String type;

    private String ex1;

    private String ex2;

    private String ex3;

    private String ex4;

    private Date createAt;

    private Date updateAt;

    private Integer dailyPaymentLimit;

    private Integer perPaymentLimit;

    /**
     * 会员累计积分
     */
    private Integer primePoints;

    @TableField(exist = false)
    private UsrUsers usrUsers;
}
