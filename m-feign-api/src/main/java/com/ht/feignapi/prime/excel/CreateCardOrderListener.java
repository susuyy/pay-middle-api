package com.ht.feignapi.prime.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;

import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.ActualCard;
import com.ht.feignapi.prime.entity.CardActualMapUser;
import com.ht.feignapi.prime.entity.CardElectronic;
import com.ht.feignapi.prime.entity.QueryExcelBuyCardData;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderClientService;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author:
 * @Date: 2020/7/2 16:38
 */
// 有个很重要的点 DataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class CreateCardOrderListener extends AnalysisEventListener<ActualCardImportVo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCardOrderListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 3000;
    List<ActualCardImportVo> list = new ArrayList<>();

    private String payType;

    private String payAmount;

    private OrderClientService orderClientService;

    private MSPrimeClient msPrimeClient;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public CreateCardOrderListener( String payType, String payAmount,OrderClientService orderClientService,MSPrimeClient msPrimeClient) {
        this.payType = payType;
        this.payAmount = payAmount;
        this.orderClientService = orderClientService;
        this.msPrimeClient = msPrimeClient;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     * one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @SneakyThrows
    @Override
    public void invoke(ActualCardImportVo data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() throws Exception {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());

//        for (ActualCardImportVo actualCardImportVo : list) {
//            CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(actualCardImportVo.getCardNo()).getData();
//            cardElectronic.setUserPhone(actualCardImportVo.getUserPhone());
//            String sellAmount = cardElectronic.getSellAmount();
//            if (StringUtils.isEmpty(sellAmount)){
//                sellAmount = "0";
//            }
//            orderClientService.createAdminSetUserCardOrder(cardElectronic,payType,sellAmount);
//        }

        Set<String> set=new HashSet<>();
        for (ActualCardImportVo actualCardImportVo : list) {
            set.add(actualCardImportVo.getCardNo());
        }

        List<QueryExcelBuyCardData> cardDataList = msPrimeClient.queryByCardNoList(set).getData();
        Map<String, CardElectronic> cardElectronicMap = cardDataList.stream().collect(Collectors.toMap(
                QueryExcelBuyCardData::getCardNo,
                QueryExcelBuyCardData::getCardElectronic));

        for (ActualCardImportVo actualCardImportVo : list) {
            CardElectronic cardElectronic = cardElectronicMap.get(actualCardImportVo.getCardNo());
            cardElectronic.setUserPhone(actualCardImportVo.getUserPhone());
        }

        orderClientService.createAdminSetUserCardOrderBatch(cardElectronicMap,payType);

        LOGGER.info("存储数据库成功！");
    }
}

