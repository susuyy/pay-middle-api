package com.ht.feignapi.shoppingmall.vo;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2021/9/27 17:27
 */
@Data
public class MerchantUserVo {
    /**
     * 商家id
     */
    private Long id;

    /**
     * 商家名称
     */
    private String merName;

    /**
     * 商家手机号（商家注册电话）
     */
    private String tel;

    /**
     * 密码
     */
    private String password;

    /**
     * 商家店类型(购物=5、 出行=7、生活=8、娱乐=9、景点=4、酒店=6、美食=3)
     */
    private Integer merStoreType;
}
