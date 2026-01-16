package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderRefundDetails implements Serializable {

    private static final long serialVersionUID=1L;


    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 原交易订单号 order_code
     */
    private String oriOrderCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 原订单明细id
     */
    private String orderDetailId;

    /**
     * 退款单号
     */
    private String backOrderCode;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * trxid 收银宝平台的退款交易流水号
     */
    private String trxId;

    /**
     * 退款金额
     */
    private Integer amount;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 退款成功标识 success成功  fail失败 
     */
    private String state;

    private String productionCode;

    private String productionName;

    private String productionCategoryCode;

    private String productionCategoryName;

    private String subMsg;

    private Date createAt;

    private Date updateAt;


}
