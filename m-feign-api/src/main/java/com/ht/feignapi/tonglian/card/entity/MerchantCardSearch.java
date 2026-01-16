package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

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
    private IPage<MerchantCardListVo> page;
    private List<String> merchantCodes;
}
