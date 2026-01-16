package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReturnShowShoppingCartData implements Serializable {

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 商户名
     */
    private String merchantName;

    /**
     * 购物车 商品展示数据
     */
    private List<ShowShoppingCartDate> showShoppingCartDateList;


//    /**
//     * 购物车 商品展示数据 分页
//     */
//    private Page<ShowShoppingCartDate> showShoppingCartDatePage;
}
