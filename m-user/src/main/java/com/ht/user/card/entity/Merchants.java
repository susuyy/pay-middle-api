package com.ht.user.card.entity;

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
 * 商户表
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@Data
public class Merchants implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 商户名称
     */
    private String merchantName;

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

    private String adminName;

}
