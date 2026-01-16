package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2020-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MrcPrimeDiscountPoints implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 末级品类编码
     */
    private String categoryCode;

    /**
     * 数量
     */
    private Integer amount;

    /**
     * 每单可抵扣限制商品数
     */
    private Integer limitAmountPerOrder;

    /**
     * 累计可抵扣限制商品数
     */
    private Integer limitAmountTotal;

    /**
     * 积分点数
     */
    private Integer points;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;

    private Date createAt;

    private Date updateAt;


}
