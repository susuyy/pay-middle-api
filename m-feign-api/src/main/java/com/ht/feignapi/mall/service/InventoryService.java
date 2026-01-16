package com.ht.feignapi.mall.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.entity.Inventory;
import com.ht.feignapi.mall.entity.ReturnCheckInventoryMsg;
import com.ht.feignapi.mall.entity.ShowShoppingCartDate;
import com.ht.feignapi.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    public Inventory createInventory(String merchantCode, String cardCode, Integer amount){
        Map<String,Integer> inventory = new HashMap<>(1);
        inventory.put("amount",amount);
        Result<Inventory> inventoryResult = inventoryClientService.createProductionInventory(merchantCode,cardCode,inventory);
        Assert.notNull(inventory,"库存保存出错");
        return inventoryResult.getData();
    }

    /**
     * 校验库存是否满足
     * @param productionCode
     * @param storeMerchantCode
     * @param quantity
     * @return
     */
    public boolean checkProductionInventory(String productionCode, String storeMerchantCode, Integer quantity) {
        Integer totalQuantity = 0;
        try {
            totalQuantity = inventoryClientService.getInventory(storeMerchantCode, productionCode).getData();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
        return totalQuantity>=quantity;
    }

    /**
     * 校验一批商品的库存是否满足
     * @param orderShoppingCartList
     * @return
     */
    public ReturnCheckInventoryMsg checkProductionListInventory(List<String> orderShoppingCartList) {
        List<ShowShoppingCartDate> showShoppingCartDateList = shoppingCartClientService.queryByOrderCodeList(orderShoppingCartList).getData();
        ReturnCheckInventoryMsg returnCheckInventoryMsg = new ReturnCheckInventoryMsg();
        returnCheckInventoryMsg.setHasInventory(true);
        String nameMsg = "";
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            Integer totalQuantity = inventoryClientService.getInventory(showShoppingCartDate.getMerchantCode(), showShoppingCartDate.getProductionCode()).getData();
            if (totalQuantity<showShoppingCartDate.getQuantity().intValue()){
                returnCheckInventoryMsg.setHasInventory(false);
                nameMsg = nameMsg + showShoppingCartDate.getProductionName() + " ";
                returnCheckInventoryMsg.setMessage(nameMsg);
            }
        }
        return returnCheckInventoryMsg;
    }

    public void addInventory(String merchantCode, String productionCode, int count,String batchCode) {
        Map<String,String> map = new HashMap<>();
        map.put("batchCode", batchCode);
        map.put("amount",String.valueOf(count));
        logger.info("******进入退回库存阶段********  batchCode:" + batchCode + "    count:" + count);
        inventoryClientService.addInventory(merchantCode,productionCode,map);
    }

    public void subtractInventory(String merchantCode, String productionCode, int i) {
        Map<String,Integer> map = new HashMap<>();
        map.put("amount",i);
        inventoryClientService.subtractInventory(merchantCode,productionCode,map);
    }
}
