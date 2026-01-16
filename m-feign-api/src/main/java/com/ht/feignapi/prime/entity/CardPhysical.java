package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2021-03-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardPhysical implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private static final long serialVersionUID=1L;

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
     * 外部合作机构号
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
     * 磁轨密码
     */
    private String magTrack;

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
     * 原始总金额,开卡时的金额
     */
    private String amount;

    /**
     * 卡分配状态
     */
    private String state;

    /**
     * 卡分配类型
     */
    private String type;

    /**
     * 卡类型(online_sell线上售卖；offline体验 ; offline_sell线下售卖)
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
     * 售卖价格
     */
    private String sellAmount;

    /**
     * 上架状态
     */
    private String sellState;

    /**
     * 卡图片
     */
    private String backGround;

    /**
     * 页面展示字号颜色
     */
    private String color;
    /**
     * 订购单号
     */
    private String makeOrderCode;

    private Date createAt;

    private Date updateAt;


}
