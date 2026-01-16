package com.ht.merchant.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MrcMapMerchant implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 子商户编码
     */
    private String subMerchantCode;

    /**
     * 子商户名称
     */
    private String subMerchantName;

    /**
     * 主体商户编码
     */
    private String objMerchantCode;

    /**
     * 主体商户名称
     */
    private String objMerchantName;

    private Date createAt;

    private Date updateAt;


}
