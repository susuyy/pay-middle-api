package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库 actual_card 表对应实体类
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardActualMapUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String vipUserOpenid;

    private Long userId;

    private String userPhone;

    private String cardNo;

    /**
     * 卡状态
     * 0-正常
     * 1-挂失
     * 2-冻结
     * 3-作废
     */
    private String cardSta;

    private String cardType;

    /**
     * 有效期
     */
    private Date validityDate;

    /**
     * 操作员id
     */
    private Long operatorId;

    /**
     * 操作员账号
     */
    private String operatorAccount;

    /**
     * 外联合作机构号
     */
    private String refBrhId;

    /**
     * 批次号
     */
    private String batchCode;

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date createAt;

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
////    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date updateAt;

    /**
     * 卡余额
     */
    @TableField(exist = false)
    private Integer accountBalance;

    /**
     * 实体卡类别
     */
    @TableField(exist = false)
    private String cardProductName;

    /**
     * 卡图片
     */
    private String backGround;

    /**
     * 字号颜色
     */
    private String color;

    /**
     * 所属主体
     */
    @TableField(exist = false)
    private String objMerchantCode;

    /**
     * 收款/支付方式
     */
    @TableField(exist = false)
    private String payType;

    /**
     * 录入实际收入金额
     */
    @TableField(exist = false)
    private String payAmount;
}
