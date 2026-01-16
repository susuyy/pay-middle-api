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
 * 商户设置参数
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mrc_merchant_profiles")
public class MerchantProfiles implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 头像
     */
    private String headUrl;

    /**
     * 顶部展示图片url
     */
    private String topShowUrl;

    /**
     * 状态
     */
    private String state;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
