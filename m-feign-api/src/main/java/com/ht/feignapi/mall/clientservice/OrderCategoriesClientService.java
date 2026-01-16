package com.ht.feignapi.mall.clientservice;


import com.ht.feignapi.mall.entity.OrderCategorys;
import com.ht.feignapi.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-16
 */
@FeignClient(name = "${custom.client.order.name}",contextId = "OrderCategorys")
public interface OrderCategoriesClientService {


    /**
     * 查询顶级编码
     * @param categoryThreeCode
     * @return
     */
    @GetMapping("/mall/orderCategorys/queryLevelOneCode")
    Result<OrderCategorys> queryLevelOneCode(@RequestParam("categoryCode")String categoryThreeCode,
                             @RequestParam("merchantCode")String merchantCode);
}

