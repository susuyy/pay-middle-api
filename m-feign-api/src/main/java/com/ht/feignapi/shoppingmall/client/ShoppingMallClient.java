package com.ht.feignapi.shoppingmall.client;


import com.ht.feignapi.result.Result;
import com.ht.feignapi.shoppingmall.entity.Merchant;
import com.ht.feignapi.shoppingmall.vo.MerchantDetailVo;
import com.ht.feignapi.shoppingmall.vo.MerchantVo;
import com.ht.feignapi.shoppingmall.vo.VipUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "${custom.client.shoppingmall.name}",contextId = "shoppingmall")
public interface ShoppingMallClient {


    /**
     * higo 小程序用户入库逻辑
     * @param vipUser
     * @return
     */
    @PostMapping("/userVip/userInfo")
    Result<VipUser> vipUserInfo(@RequestBody VipUser vipUser);

    /**
     * 新增商家
     * @param merchantVo
     * @return
     */
    @PostMapping("/merchant/add")
    Result addMerchant(@RequestBody MerchantVo merchantVo);

    /**
     * 修改商家
     * @param merchantVo
     * @return
     */
    @PostMapping("/merchant/modify")
    Result modifyMerchant(@RequestBody MerchantVo merchantVo);

    /**
     * 查询单个商家(主键查询)
     * @param id
     * @return
     */
    @PostMapping("/merchant/queryById")
    Result<MerchantDetailVo> queryById(@RequestParam("id") Long  id);
}
