package com.ht.feignapi.prime.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-02-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardElectronicSell implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 售价
     */
    private Long sellAmount;

    /**
     * 机构号
     */
    private String brhId;

    /**
     * 外部机构号
     */
    private String refBrhId;

    /**
     * 面值
     */
    private Integer faceValue;

    /**
     * 卡名
     */
    private String cardName;

    /**
     * 背景图
     */
    private String backGround;

    /**
     * 字号颜色
     */
    private String color;

    /**
     * 售价
     */
    private String state;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateAt;


}
