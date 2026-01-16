package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_inventory")
public class CardInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;


    /**
     * 仓库名称
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 库存量
     */
    private Integer inventory;

    /**
     * 状态
     */
    private String state;

    /**
     * 类型
     */
    private String type;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    /**
     * 批次号
     */
    private String batchCode;

}
