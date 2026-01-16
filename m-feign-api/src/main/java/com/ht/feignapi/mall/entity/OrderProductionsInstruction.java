package com.ht.feignapi.mall.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderProductionsInstruction implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    private String productionCode;

    private String merchantCode;

    private String type;

    /**
     * 使用说明
     */
    private String instruction;

    private Long limitId;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;


}
