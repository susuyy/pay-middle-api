package com.ht.feignapi.prime.controller;


import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.PartyCardQueryData;
import com.ht.feignapi.prime.entity.QuerySummaryRefundCardData;
import com.ht.feignapi.prime.entity.SummaryCardNoRefundData;
import com.ht.feignapi.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 第三方平台 联调查询卡数据
 * @author: hy.wang
 * @Date: 2021/8/6
 */
@RestController
@RequestMapping("/ms/party/card")
@CrossOrigin(allowCredentials = "true")
public class PrimePartyCardController {


    @Autowired
    private MSPrimeClient msPrimeClient;

    private Logger log = LoggerFactory.getLogger(PrimePartyCardController.class);


    /**
     * 查询 卡同步数据
     * @param partyCardQueryData 卡号
     * @return
     */
    @PostMapping("/findPartyCardSynData")
    public Result findPartyCardSynData(@RequestBody PartyCardQueryData partyCardQueryData){
        try {
            return msPrimeClient.findPartyCardSynData(partyCardQueryData);
        } catch (Exception e) {
            log.error("findPartyCardSynData error={}",e);
            return Result.error(e.getMessage());
        }
    }


    /**
     * 退款,撤销订单列表 不分页
     *
     * @return
     */
    @GetMapping("/allList")
    public Result allList(){
        try {
            return msPrimeClient.allRefundList();
        } catch (Exception e) {
            log.error("allList error={}",e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 机构渠道 退款,撤销订单列表 不分页
     *
     * @return
     */
    @GetMapping("/allList/merId")
    public Result allListForMerId(@RequestParam("merId") String merId){
        try {
            return  msPrimeClient.allListForMerId(merId);
        } catch (Exception e) {
            log.error("allListForMerId error={}",e);
            return Result.error(e.getMessage());
        }
    }


    @PostMapping("/summaryRefundCardNoData/merId")
    public Result summaryRefundCardNoDataMerId(@RequestBody QuerySummaryRefundCardData querySummaryRefundCardData){
        try {

            String merId = querySummaryRefundCardData.getMerId();
            List<SummaryCardNoRefundData> summaryCardNoRefundDataList = querySummaryRefundCardData.getSummaryCardNoRefundDataList();
            return  msPrimeClient.summaryRefundCardNoDataMerId(merId,summaryCardNoRefundDataList);

        } catch (Exception e) {
            log.error("summaryRefundCardNoDataMerId error={}",e);
            return Result.error(e.getMessage());
        }
    }




}
