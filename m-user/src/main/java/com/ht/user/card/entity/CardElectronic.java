package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class CardElectronic implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    private String userPhone;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 合作机构号
     */
    private String refMerchantCode;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 具体卡号
     */
    private String cardNo;

    /**
     * 卡分类编码
     */
    private String categoryCode;

    /**
     * 卡分类名称
     */
    private String categoryName;

    /**
     * 卡面值（计次卡有用）
     */
    private String faceValue;

    /**
     * 原始卡金额
     */
    private int amount;

    /**
     * 卡分配状态
     */
    private String state;

    /**
     * 卡分配类型
     */
    private String type;

    /**
     * 卡类型(计次券,金额券,满减券,折扣券)
     */
    private String cardType;

    /**
     * 实体卡卡号
     */
    private String icCardId;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 卡券来源标识(商城购买来源 使用order_detatil的id)
     */
    private String refSourceKey;

    /**
     * 是否上架
     */
    private String sellState;

    /**
     * 售卖金额
     */
    private String sellAmount;

    private Date createAt;

    private Date updateAt;

    /**
     * 卡图片
     */
    private String backGround;

    /**
     * 字号颜色
     */
    private String color;


    /**
     * 已消费金额
     */
    @TableField(exist = false)
    private String consumeAmount;

    /**
     * 领取状态
     */
    @TableField(exist = false)
    private String getState;

    /**
     * 绑卡密码
     */
    private String password;
}
