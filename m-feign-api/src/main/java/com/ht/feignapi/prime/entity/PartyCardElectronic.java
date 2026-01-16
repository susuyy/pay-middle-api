package com.ht.feignapi.prime.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户，卡绑定关系
 * </p>
 *
 * @author ${author}
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("party_card_electronic")
public class PartyCardElectronic implements Serializable {



    @ExcelIgnore
    private static final long serialVersionUID=1L;

    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @ExcelIgnore
    private Long userId;

    @ExcelProperty(value = { "用户手机号" }, index = 0)
    private String userPhone;

    /**
     * 商户编码
     */
    @ExcelProperty(value = { "机构编码" }, index = 1)
    private String merchantCode;

    /**
     * 合作机构号
     */
    @ExcelIgnore
    private String refMerchantCode;

    /**
     * 储值渠道方
     */
    private String channelId;


    /**
     * 渠道合作商编码
     */
    private String channelPartnerCode;


    /**
     * 卡编码
     */
    @ExcelIgnore
    private String cardCode;

    /**
     * 卡名称
     */
    @ExcelProperty(value = { "卡名" }, index = 2)
    private String cardName;

    /**
     * 具体卡号
     */
    @ExcelProperty(value = { "卡号" }, index = 3)
    private String cardNo;

    /**
     * 卡分类编码
     */
    @ExcelIgnore
    private String categoryCode;

    /**
     * 卡分类名称
     */
    @ExcelIgnore
    private String categoryName;

    /**
     * 卡面值（计次卡有用）
     */
    @ExcelProperty(value = { "余额" }, index = 4)
    private String faceValue;

    /**
     * 原始卡金额
     */
    @ExcelProperty(value = { "开卡面额" }, index = 5)
    private int amount;

    /**
     * 卡分配状态
     */
    @ExcelIgnore
    private String state;

    /**
     * 卡分配类型
     */
    @ExcelIgnore
    private String type;

    /**
     * 卡类型
     */
    @ExcelProperty(value = { "卡类型" }, index = 6)
    private String cardType;

    /**
     * 实体卡卡号
     */
    @ExcelIgnore
    private String icCardId;

    /**
     * 批次号
     */
    @ExcelProperty(value = { "批次号" }, index = 7)
    private String batchCode;

    /**
     * 卡券来源标识(商城购买来源 使用order_detatil的id)
     */
    @ExcelIgnore
    private String refSourceKey;

    /**
     * 返利明细
     */
    @ExcelIgnore
    private String couponDetail;

    /**
     * 是否上架
     */
    @ExcelIgnore
    private String sellState;

    /**
     * 售卖金额
     */
    @ExcelProperty(value = { "售卖金额" }, index = 8)
    private String sellAmount;

    @ExcelIgnore
    private Date createAt;

    @ExcelIgnore
    private Date updateAt;

    /**
     * 卡图片
     */
    @ExcelIgnore
    private String backGround;

    /**
     * 字号颜色
     */
    @ExcelIgnore
    private String color;

    /**
     * 已消费金额
     */
    @ExcelProperty(value = { "已消费" }, index = 9)
    @TableField(exist = false)
    private String consumeAmount;

    /**
     * 领取状态
     */
    @ExcelProperty(value = { "领取状态" }, index = 10)
    @TableField(exist = false)
    private String getState;

    /**
     * 绑卡密码
     */
    private String password;

    /**
     * 卡有效期开始时间
     */
    @TableField(exist = false)
    @ExcelIgnore
    private Date validFrom;

    /**
     * 卡有效期
     */
    @TableField(exist = false)
    @ExcelIgnore
    private Date validityDate;
}
