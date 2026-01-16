package com.ht.feignapi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderWayBillsTrace implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 关联单号
     */
    private String refBillCode;

    /**
     * 关联单号类型
     */
    private String refBillType;

    /**
     * 备注
     */
    private String comments;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;

    private String createBy;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;

    private String updateBy;


}
