package com.ht.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 商户表
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mrc_merchants")
public class Merchants implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户对应的通商云 bizUserId
     */
    private String bizUserId;

    /**
     * 商户对应的通商云 UserId
     */
    private String tsyUserId;

    private Long userId;

    /**
     * 商户证书
     */
    private String cert;

    /**
     * 商户证书文件url
     */
    private String certUrl;

    private String province;

    private String city;

    /**
     * 区
     */
    private String county;

    /**
     * 商户地址
     */
    private String location;

    /**
     * 商户类型
     */
    private String type;

    /**
     * 商户状态
     */
    private String state;

    /**
     * 主体商户编码
     */
    private String businessSubjects;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date createAt;

    private Date updateAt;

    /**
     * 收银类型
     */
    private String chargeType;

    /**
     * 经营类型
     */
    private String businessType;

    /**
     * 头像
     */
    private String merchantPicUrl;

    /**
     * 电话
     */
    private String merchantContact;

    /**
     * 管理员
     */
    @TableField(exist = false)
    private String adminName;

    @TableField(exist = false)
    private List<Merchants> subjectMerchantsList;

}