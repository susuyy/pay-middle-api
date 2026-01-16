package com.ht.feignapi.tonglian.admin.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.ht.feignapi.tonglian.admin.excel.entity.UserRealCardImportVo;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/6 15:56
 */
public class UserRealCardListener extends AnalysisEventListener<UserRealCardImportVo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardListener.class);

    /**
     * 每隔2000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 2000;
    List<UserRealCardImportVo> list = new ArrayList<UserRealCardImportVo>();

    private final String merchantCode;

    private final MerchantPrimeService merchantPrimeService;
    public UserRealCardListener(MerchantPrimeService merchantPrimeService, String merchantCode){
        this.merchantPrimeService = merchantPrimeService;
        this.merchantCode = merchantCode;
    }

    @Override
    public void invoke(UserRealCardImportVo userRealCardImportVo, AnalysisContext analysisContext) {
        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(userRealCardImportVo));
        list.add(userRealCardImportVo);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        list.forEach(e -> {
            merchantPrimeService.saveUserIcCard(e.getOpenId(), merchantCode, e.getIcCard(), e.getPhone());
        });
        LOGGER.info("存储数据库成功！");
    }
}
