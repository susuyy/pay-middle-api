package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商家卡券
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mrc_map_merchant_cards")
public class MapMerchantCards implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 商户卡券类型
     */
    private String type;

    /**
     * 商户卡券状态
     */
    private String state;

    /**
     * 卡面值
     */
    private String cardFaceValue;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    

}
