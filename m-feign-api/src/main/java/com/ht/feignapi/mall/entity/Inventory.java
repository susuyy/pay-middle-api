package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-17
 */
@Data
@Accessors(chain = true)
@TableName("inv_inventory")
@ApiModel(value="Inventory对象", description="")
public class Inventory {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "产品编码")
    private String productionCode;

    @ApiModelProperty(value = "库存量")
    private Integer inventory;

    @ApiModelProperty(value = "批次号")
    private String batchCode;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "类型")
    private String type;


}
