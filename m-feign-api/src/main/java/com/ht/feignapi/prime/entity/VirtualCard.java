package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据库 virtual_card 对应实体类
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VirtualCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员用户对应的openid
     */
    private String vipUserOpenid;

    private Long userId;

    /**
     * 虚拟卡订单号
     */
    private String cardId;

    /**
     * 机构号
     */
    private String brhId;

    /**
     * 品牌号
     */
    private String brandNo;

    /**
     * 开卡的订单号
     */
    private String orderId;


    private Date createTime;

    private Date updateTime;

    /**
     * 卡余额
     */
    @TableField(exist = false)
    private BigDecimal accountBalance;


}
