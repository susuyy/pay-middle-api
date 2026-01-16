package com.ht.feignapi.mall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.constant.MallConstant;
import com.ht.feignapi.mall.entity.MallProductions;
import com.ht.feignapi.mall.entity.MallShops;
import com.ht.feignapi.mall.entity.MallTemplateDetail;
import com.ht.feignapi.mall.entity.MallTemplateHeader;
import com.ht.feignapi.mall.service.MallProductionService;
import com.ht.feignapi.mall.util.JudgeParamUtil;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/11 17:22
 */
@RequestMapping("/mall/template")
@RestController
public class MallTemplateController {

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MallProductionService productionService;

    @Autowired
    private MallProductionService mallProductionService;

    /**
     * 保存店铺展示模板，店铺从列表选择
     *
     * @param mallShop
     */
    @PostMapping("/mallShop")
    public void saveMallShop(@RequestBody MallShops mallShop) throws Exception {
        Result<Merchants> merchantsResult = merchantsClientService.getMerchantByCode(mallShop.getMerchantCode());
        if (merchantsResult != null && merchantsResult.getData() != null) {
            mallAppShowClientService.saveMallShop(mallShop);
        } else {
            throw new Exception("店铺不存在!");
        }
    }

    /**
     * 获取某个category下的列表详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{mallCode}/{templateCode}/detailSubItems/{id}")
    public Result<Page> getTemplateCategorySubItem(
            @PathVariable Long id,
            @PathVariable String mallCode,
            @PathVariable String templateCode,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        Result<Page> pageResult = mallAppShowClientService.selectDetailSubItems(id, pageNo, pageSize, mallCode, templateCode);
        Result<MallTemplateDetail> detailResult = mallAppShowClientService.getTemplateDetail(id);
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(detailResult.getCode())
                && detailResult.getData() != null) {
            if (MallConstant.PRODUCTION.equals(detailResult.getData().getRefListType())) {
                List<MallProductions> mallProductionsList = new ArrayList<>();
                pageResult.getData().getRecords().forEach(e -> {
                    MallProductions mallProductions = JSON.toJavaObject((JSON) JSON.toJSON(e), MallProductions.class);
                    mallProductionService.decorateProductionInstrument(mallProductions);
                    mallProductionsList.add(mallProductions);
                });
                pageResult.getData().setRecords(mallProductionsList);
            }
        }
        return pageResult;
    }

    /**
     * 根据modelType，key，mallCode，pageNum，pageSize,sortType,lable01查询Template数据
     *
     * @param templateCode
     * @param resMap
     * @param mallCode
     * @return
     * @throws ResultException
     */
    @PostMapping("getTemplateDate/{templateCode}")
    @ApiOperation(value = "展示分类查询", notes =
            "\t\"modelType\":\"PRODUCTION\",\n" +
                    "  \t\"showCategoryCode\":\"category01\",\n" +
                    "  \t\"pageNum\":\"0\",\n" +
                    " \t \"pageSize\":\"10\",\n" +
                    " \t \"lable01\":\"\"   西餐，中餐等,\n" +
                    "  \t\"sortType\":\"zh    综合，销量，价格等\"")
    public Map<String, Object> selectTemplate(@PathVariable("templateCode") String templateCode, @RequestBody Map<String, String> resMap, @RequestHeader("mallCode") String mallCode) throws ResultException {
        resMap.put("mallCode", mallCode);
        try {
            //判断参数是否缺失
            List<String> paramList = Arrays.asList("modelType", "showCategoryCode", "mallCode",
                    "pageNum", "pageSize", "sortType", "lable01");
            boolean code = JudgeParamUtil.missParams(paramList, resMap).getCode();
            String message = JudgeParamUtil.missParams(paramList, resMap).getMessage();
            if (!code) {
                throw new ResultException(message);
            }
            //给分页查询设置初始值
            String pageNum = resMap.get("pageNum");
            if (StringUtils.isEmpty(pageNum)) {
                resMap.put("pageNum", "0");
            }
            String pageSize = resMap.get("pageSize");
            if (StringUtils.isEmpty(pageSize)) {
                resMap.put("pageSize", "10");
            }
            Result<Map<String, Object>> result = mallAppShowClientService.selectTemplate(resMap, mallCode);
            if (MallConstant.PRODUCTION.equals(resMap.get(MallConstant.MODELTYPE))) {
                List<MallProductions> productionsList = new ArrayList<>();
                JSONArray listProduction = (JSONArray) result.getData().get("dataList");
                listProduction.forEach(e -> {
                    MallProductions productions = ((JSONObject) e).toJavaObject(MallProductions.class);
                    productionService.parseEndDate(productions);
                    if (productions.getSortNum().equals(MallConstant.SOLD_OUT)) {
                        productions.setState(MallConstant.SOLD_OUT_STR);
                    } else {
                        productions.setState(MallConstant.NORMAL);
                    }
                    productionsList.add(productions);
                });
                result.getData().replace("dataList", productionsList);
            }
            Result<MallTemplateDetail> detailResult =
                    mallAppShowClientService.getTemplateDetail(templateCode, resMap.get("showCategoryCode"), resMap.get("modelType"));
            Assert.notNull(detailResult.getData(), "获取模板分类出错！");
            result.getData().put("categoryName", detailResult.getData().getDisplayName());
            result.getData().put("categoryImgUrl", detailResult.getData().getUrl());
            return result.getData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(ResultTypeEnum.SERVICE_ERROR);
        }
    }

    /**
     * 获取有效mallTemplate的templateDetail
     * 需要排除已经上架了的
     *
     * @param objMerchantCode
     * @return
     */

    @GetMapping("/enabledProTemp/{productionCode}/{objMerchantCode}")
    public Result<List<MallTemplateDetail>> getEnabledProTemp(
            @PathVariable("productionCode") String productionCode,
            @PathVariable("objMerchantCode") String objMerchantCode) {
        Result<List<MallTemplateDetail>> enableSaleListResult = mallAppShowClientService.getEnabledProTemp(objMerchantCode);
        Result<List<MallTemplateDetail>> onSaleList = mallAppShowClientService.getOnSaleTempDetail(productionCode, objMerchantCode);
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(enableSaleListResult.getCode())
                && enableSaleListResult.getData() != null) {
            if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(onSaleList.getCode())
                    && onSaleList.getData() != null) {
                enableSaleListResult.getData().removeAll(onSaleList.getData());
            }
        }
        return enableSaleListResult;
    }


    /**
     * 获取商户的商城模板
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/mall-template-header/mallList/{objMerchantCode}")
    public List<MallTemplateHeader> getMerchantTemplateList(
            @PathVariable("objMerchantCode") String objMerchantCode,
            @RequestParam("merchantCode") String merchantCode) {
        return mallAppShowClientService.getMerchantMallCode(objMerchantCode, merchantCode);
    }
}
