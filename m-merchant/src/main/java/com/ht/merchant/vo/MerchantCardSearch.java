package com.ht.merchant.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/31 14:34
 */
@Data
public class MerchantCardSearch {
    private String merchantName;
    private String merchantCardName;
    private String type;
    private String published;
}
