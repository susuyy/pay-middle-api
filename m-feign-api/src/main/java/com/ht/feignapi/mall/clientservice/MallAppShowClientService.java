package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.appshow.entity.MallCoupon;
import com.ht.feignapi.appshow.entity.MallCouponSearch;
import com.ht.feignapi.mall.entity.MallProductions;
import com.ht.feignapi.mall.entity.MallShops;
import com.ht.feignapi.mall.entity.MallTemplateDetail;
import com.ht.feignapi.mall.entity.MallTemplateHeader;
import com.ht.feignapi.result.Result;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/11 17:59
 */
@FeignClient(name = "${custom.client.appshow}",contextId = "mallAppShow")
public interface MallAppShowClientService {

     /**
      * 保存mallProduction信息
      * @param mallProductions
      */
     @PostMapping("/mall-productions")
     void saveMallProduction(@RequestBody MallProductions mallProductions);

     @PutMapping("/mall-productions")
     void updateMallProduction(@RequestBody MallProductions mallProductions);

     /**
      * 保存mallshop信息
      * @param mallShop
      */
     @PostMapping("/mall-shops")
     void saveMallShop(@RequestBody MallShops mallShop);

     /**
      * 根据productionCode和merchantCode 查询展示商品
      * @param productionCode
      * @param merchantCode
      * @return
      */
     @GetMapping("/mall-productions/queryByCodeAndMerchant")
     Result<List<MallProductions>> queryByCodeAndMerchant(@RequestParam("productionCode") String productionCode,
                                         @RequestParam("merchantCode") String merchantCode);

     /**
      * 获取主体商户下的卡券列表
      * @param mallCode
      * @param merchantCodes
      * @param couponType
      * @param mallCouponSearch
      * @param pageSize
      * @param pageNo
      * @return
      */
     @GetMapping("/mall_coupon/mall/{mallCode}/{merchantCodes}/{couponType}")
     Result<Page<MallCoupon>> getMallCouponList(@PathVariable("mallCode") String mallCode,
                                                @PathVariable("merchantCodes") List<String> merchantCodes,
                                                @PathVariable("couponType") String couponType,
                                                MallCouponSearch mallCouponSearch,
                                                @RequestParam("pageSize") Long pageSize,
                                                @RequestParam("pageNo") Long pageNo);

     /**
      * 获取cardCode对应的coupon
      * @param cardCode
      * @param merchantCode
      * @return
      */
     @GetMapping("/mall_coupon/{cardCode}/{merchantCode}")
     Result<MallCoupon> getCouponByCardCode(@PathVariable("cardCode") String cardCode,@PathVariable("merchantCode") String merchantCode);

     /**
      * 获取production列表的page相关属性
      * @param param
      * @param mallCode
      * @return
      */
     @PostMapping("/mall-productions/selectByPage")
    Result<Page<MallProductions>> selectByPage(@RequestBody Map<String, String> param,@RequestHeader("mallCode") String mallCode);

     /**
      * 获取产品详情
      * @param id
      * @return
      */
     @GetMapping("/mall-productions/{id}")
     Result<MallProductions> getMallProduction(@PathVariable("id") Long id);

    /**
     * 获取产品详情
     * @param productionCode
     * @param showCategoryCode
     * @return
     */
    @GetMapping("/mall-productions/showCategoryPro/{productionCode}")
    Result<MallProductions> getMallProductionByCode(
            @PathVariable("productionCode") String productionCode,
            @RequestParam("showCategoryCode") String showCategoryCode);

    /**
     * 获取所有的production
     * @return
     */
     @GetMapping("/mall-productions/all")
     Result<List<MallProductions>> getAllProduction();

    /**
     * 获取category进去的产品列表
     * @param resMap
     * @param mallCode
     * @return
     */
     @PostMapping("/common/getTemplateDate")
     Result<Map<String, Object>> selectTemplate(@RequestBody Map<String, String> resMap, @RequestHeader("mallCode") String mallCode);

     @GetMapping("/mall-template-detail/{templateCode}/showCategory/{categoryCode}/{refListType}")
     Result<MallTemplateDetail> getTemplateDetail(
            @PathVariable("templateCode") String templateCode,
            @PathVariable("categoryCode") String categoryCode,
            @PathVariable("refListType") String refListType);

    @GetMapping("/mall-template-detail/{id}")
    Result<MallTemplateDetail> getTemplateDetail(@PathVariable("id") Long id);

    /**
     * 保存商城免费卡券
     * @param mallCoupon
     */
     @PostMapping("/mall_coupon")
     void saveMallCoupon(MallCoupon mallCoupon);

    /**
     * 获取子商户下，所有的展示商品
     * @param merchantCodes
     * @param pageNo
     * @param pageSize
     * @param productionName
     * @param categoryCode
     * @return
     */
     @PostMapping("/mall-productions/show-productions/{merchantCodes}")
     Result<Page<MallProductions>> getMerchantsAllProduction(
             @PathVariable("merchantCodes") List<String> merchantCodes,
             @RequestParam("pageNo") Long pageNo,
             @RequestParam("pageSize") Long pageSize,
             @RequestParam("productionName") String productionName,
             @RequestParam("categoryCode") String categoryCode);

    /**
     * 获取有效mallTemplate的templateDetail
     * @param objMerchantCode
     * @return
     */
    @GetMapping("/mall-template-detail/enabledProTemp/{objMerchantCode}")
    Result<List<MallTemplateDetail>> getEnabledProTemp(@PathVariable("objMerchantCode") String objMerchantCode);

    /**
     * 通过templateCode获取对应的mallCode
     * @param templateCode
     * @return
     */
    @GetMapping("/mall-template-header/templateCode/{templateCode}")
    Result<MallTemplateHeader> getMallTemplateHeader(@PathVariable("templateCode") String templateCode);

    /**
     * 获取已经上架的列表
     * @param productionCode
     * @param objMerchantCode
     * @return
     */
    @GetMapping("/mall-template-detail/onSaleTempDetail/{productionCode}/{objMerchantCode}")
    Result<List<MallTemplateDetail>> getOnSaleTempDetail(
            @PathVariable("productionCode") String productionCode,
            @PathVariable("objMerchantCode") String objMerchantCode);

    /**
     * 通过产品code和商户号，获取展示商品数据
     * @param productionCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/mall-productions/{merchantCode}/productionCode/{productionCode}")
    Result<List<MallProductions>> getMallProductionList(
            @PathVariable("productionCode") String productionCode,
            @PathVariable("merchantCode") String merchantCode);

    /**
     * 保存mallProductions信息
     * @param mallProductions
     */
    @PostMapping("/mall-productions/batches")
    void saveMallProductionsBatch(@RequestBody List<MallProductions> mallProductions);

    @GetMapping("/mallList/{objMerchantCode}")
    List<MallTemplateHeader> getMerchantMallCode(@PathVariable("objMerchantCode") String objMerchantCode,
                                                 @RequestParam("merchantCode")String merchantCode);


    /**
     * 获取某个category的子列表
     * @param id
     * @param pageNo
     * @param pageSize
     * @param mallCode
     * @param templateCode
     * @return
     */
    @GetMapping("/mall-template-detail/{mallCode}/{templateCode}/detailSubItems/{id}")
    Result<Page> selectDetailSubItems(
            @PathVariable("id") Long id,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize,
            @PathVariable("mallCode") String mallCode,
            @PathVariable("templateCode") String templateCode);

    /**
     * 删除某个显示的商品
     * @param productionCode
     * @param showCategoryCode
     * @return
     */
    @DeleteMapping("/mall-productions/{showCategoryCode}/{productionCode}")
    Result<Boolean> deleteMallProduction(@PathVariable("productionCode") String productionCode, @PathVariable("showCategoryCode") String showCategoryCode);
}
