package com.ht.user.outlets.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("outlets_order_pos_ref_trace")
public class OutletsOrderPosRefTrace implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String orderCode;

    private String businessId;

    private String amount;

    /**
     * 手续费,单位分
     */
    private Integer fee;

    private String traceNo;

    private String expDate;

    private String batchNo;

    private String merchId;

    private String merchName;

    private String terId;

    private String refNo;

    private String authNo;

    private String rejcode;

    private String issName;

    private String cardno;

    private String refDate;

    private String refTime;

    private String rejcodeCn;

    private String cardTypeIdenty;

    private String wildCardSign;

    /**
     * 交易单号
     */
    private String transTicketNo;

    /**
     * 卡类型
     */
    private String cardtype;

    private Date createAt;

    private Date updateAt;


}
