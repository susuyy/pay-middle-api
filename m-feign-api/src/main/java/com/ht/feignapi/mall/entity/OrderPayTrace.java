package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 订单支付流水
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderPayTrace implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 订单明细编码
     */
    private Long orderDetailId;

    /**
     * 支付来源
     */
    private String payCode;

    /**
     * 支付类型
     */
    private String type;

    /**
     * 支付状态
     */
    private String state;

    /**
     * 支付来源
     */
    private String source;

    /**
     * 支付来源标识
     */
    private String sourceId;

    /**
     * 支付金额: 默认 分
     */
    private Integer amount;

    /**
     * pos机串号
     */
    private String posSerialNum;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;


}
