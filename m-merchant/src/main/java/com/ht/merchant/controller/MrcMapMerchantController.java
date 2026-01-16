package com.ht.merchant.controller;


import com.ht.merchant.entity.Merchants;
import com.ht.merchant.entity.MrcMapMerchant;
import com.ht.merchant.service.MrcMapMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-03-23
 */
@RestController
@RequestMapping("/mrcMapMerchant")
public class MrcMapMerchantController {

    @Autowired
    private MrcMapMerchantService mrcMapMerchantService;

    /**
     * 关联合作机构和主体
     * @param mrcMapMerchant
     */
    @PostMapping("/add")
    public void add (@RequestBody MrcMapMerchant mrcMapMerchant) {
        mrcMapMerchantService.addMrcMapMerchant(mrcMapMerchant.getSubMerchantCode(),mrcMapMerchant.getObjMerchantCode());
   }

    /**
     * 查询 某一商户下的主体 列表搜索用
     * @param subMerchantCode
     */
    @GetMapping("/queryObjMerchantBySub")
    public List<MrcMapMerchant> queryObjMerchantBySub(@RequestParam("subMerchantCode") String subMerchantCode){
        return mrcMapMerchantService.queryBySubMerchantCode(subMerchantCode);
    }



}

