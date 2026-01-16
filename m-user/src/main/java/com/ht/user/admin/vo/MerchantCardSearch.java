package com.ht.user.admin.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/31 14:34
 */
@Data
public class MerchantCardSearch {
    private String merchantName;
    private String merchantCardName;

    /**
     * 卡券的类型：计次券，金额券，折扣券
     */
    private String type;
    private String published;
    private IPage<MerchantCardListVo> page;
    private List<String> merchantCodes;
}
