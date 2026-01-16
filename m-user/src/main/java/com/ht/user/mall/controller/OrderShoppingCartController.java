package com.ht.user.mall.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.entity.CardMapMerchantCards;
import com.ht.user.mall.constant.ProductionsCategoryConstant;
import com.ht.user.mall.entity.*;
import com.ht.user.mall.service.OrderProductionsService;
import com.ht.user.mall.service.OrderShoppingCartDetailsService;
import com.ht.user.mall.service.OrderShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@RestController
@RequestMapping("/mall/shoppingCart")
@CrossOrigin(allowCredentials = "true")
public class OrderShoppingCartController {

    @Autowired
    private OrderShoppingCartService orderShoppingCartService;

    @Autowired
    private OrderShoppingCartDetailsService orderShoppingCartDetailsService;

    @Autowired
    private OrderProductionsService orderProductionsService;

    /**
     * 用户添加购物车
     * @param addShoppingCartDate
     */
    @PostMapping("/addShoppingCart")
    public void addShoppingCart(@RequestBody AddShoppingCartDate addShoppingCartDate){
        orderShoppingCartService.addShoppingCartDate(addShoppingCartDate);
    }

    /**
     * 用户 删除购物车
     * @param orderCode
     */
    @DeleteMapping("/delShoppingCart")
    public void delShoppingCart(@RequestParam("orderCode")String orderCode){
        orderShoppingCartService.delByOrderCode(orderCode);
        orderShoppingCartDetailsService.delByOrderCode(orderCode);
    }

    /**
     * 记录购物车选中状态
     * @param orderCode
     * @param selected
     */
    @PostMapping("/recordSelected")
    public void recordSelected(@RequestParam("orderCode")String orderCode,@RequestParam("selected")String selected){
        orderShoppingCartService.updateSelectedByOrderCode(orderCode,selected);
    }

    /**
     * 展示用户购物车数据 按店铺分页
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/showShoppingCart")
    public RetPageCartData showShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData){
        RetPageCartData retPageCartData = orderShoppingCartService.showShoppingCart(requestShowShoppingCartData.getMerchantCode(),
                requestShowShoppingCartData.getUserId(),
                requestShowShoppingCartData.getPageNo(),
                requestShowShoppingCartData.getPageSize());
        return retPageCartData;
    }

    /**
     * 修改购物车 数量
     * @param updateQuantityData
     * @return
     */
    @PostMapping("/updateQuantityById")
    public UpdateQuantityData updateQuantityById(@RequestBody UpdateQuantityData updateQuantityData){
        OrderShoppingCart orderShoppingCart = orderShoppingCartService.getById(Long.parseLong(updateQuantityData.getId()));
        OrderShoppingCartDetails orderShoppingCartDetails=orderShoppingCartDetailsService.queryByOrderCode(orderShoppingCart.getOrderCode());

        if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(updateQuantityData.getLevelThreeCode())){
            CardMapMerchantCards cardMapMerchantCards = updateQuantityData.getCardMapMerchantCards();
            orderShoppingCart.setQuantity(new BigDecimal(updateQuantityData.getQuantity()));
            orderShoppingCart.setAmount(cardMapMerchantCards.getPrice() * updateQuantityData.getQuantity());
            orderShoppingCartService.updateById(orderShoppingCart);
            orderShoppingCartDetails.setQuantity(new BigDecimal(updateQuantityData.getQuantity()));
            orderShoppingCartDetails.setAmount(cardMapMerchantCards.getPrice() * updateQuantityData.getQuantity());
            orderShoppingCartDetailsService.updateById(orderShoppingCartDetails);
            updateQuantityData.setAmount(cardMapMerchantCards.getPrice() * updateQuantityData.getQuantity());
        }else {
            OrderProductions orderProductions = updateQuantityData.getOrderProductions();
            orderShoppingCart.setQuantity(new BigDecimal(updateQuantityData.getQuantity()));
            orderShoppingCart.setAmount(orderProductions.getPrice() * updateQuantityData.getQuantity());
            orderShoppingCartService.updateById(orderShoppingCart);
            orderShoppingCartDetails.setQuantity(new BigDecimal(updateQuantityData.getQuantity()));
            orderShoppingCartDetails.setAmount(orderProductions.getPrice() * updateQuantityData.getQuantity());
            orderShoppingCartDetailsService.updateById(orderShoppingCartDetails);
            updateQuantityData.setAmount(orderProductions.getPrice() * updateQuantityData.getQuantity());
        }

        return updateQuantityData;
    }

    /**
     * 获取失效宝贝
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/invalidCart")
    public IPage<ShowShoppingCartDate> invalidCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData){
        return orderShoppingCartService.invalidCart(requestShowShoppingCartData.getUserId(),
                "invalid",
                requestShowShoppingCartData.getPageNo(),
                requestShowShoppingCartData.getPageSize());
    }

    /**
     * 获取购物车 商品数量
     * @param userId
     * @return
     */
    @GetMapping("/productionsCount")
    public Integer productionsCount(@RequestParam("userId")String userId){
        return orderShoppingCartService.productionsCount(userId);
    }

    /**
     * 清空失效宝贝
     * @param userId
     */
    @PostMapping("/delInvalidCart")
    public void delInvalidCart(@RequestParam("userId")String userId){
        orderShoppingCartService.delInvalidCart(userId);
    }

    /**
     * 获取更多 某个商家下的 购物车 数据
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/moreShoppingCart")
    public IPage<ShowShoppingCartDate> moreShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData){
        return orderShoppingCartService.moreShoppingCart(requestShowShoppingCartData.getMerchantCode(),
                requestShowShoppingCartData.getUserId(),
                requestShowShoppingCartData.getPageNo(),
                requestShowShoppingCartData.getPageSize());
    }

    /**
     * 根据batchCode删除购物车
     * @param orderCode
     */
    @PostMapping("/delByOrderCode")
    public void paySuccessDelByBatchCode(@RequestParam("orderCode") String orderCode){
        OrderShoppingCartDetails orderShoppingCartDetails = orderShoppingCartDetailsService.queryByOrderCode(orderCode);
        //清除购物车主表
        orderShoppingCartService.delByOrderCode(orderShoppingCartDetails.getOrderCode());
        //清除购物车明细表
        orderShoppingCartDetailsService.delByOrderCode(orderCode);
    }


    /**
     * 根据orderCode 查询购物车数据
     * @param shoppingCartOrderCode
     * @return
     */
    @PostMapping("/queryByOrderCode")
    public OrderShoppingCart queryByOrderCode(@RequestParam("shoppingCartOrderCode") String shoppingCartOrderCode){
        return orderShoppingCartService.queryByOrderCode(shoppingCartOrderCode);
    }

    /**
     * 查询购物车集合 根据orderCode集合 和商户编码
     * @param shoppingCartOrderCodeList
     * @param merchantCode
     * @return
     */
    @PostMapping("/queryByOrderCodeListAndMerchantCode")
    public List<ShowShoppingCartDate> queryByOrderCodeListAndMerchantCode(@RequestBody List<String> shoppingCartOrderCodeList,
                                                                          @RequestParam("merchantCode") String merchantCode){
        return orderShoppingCartService.queryByOrderCodeListAndMerchantCode(shoppingCartOrderCodeList,merchantCode);
    }

    /**
     * 查询购物车集合 根据orderCode集合
     * @param shoppingCartOrderCodeList
     * @return
     */
    @PostMapping("/queryByOrderCodeList")
    public List<ShowShoppingCartDate> queryByOrderCodeList(@RequestBody List<String> shoppingCartOrderCodeList){
        return orderShoppingCartService.queryByOrderCodeList(shoppingCartOrderCodeList);
    }

    /**
     * 根据id 查询购物车数据
     * @param id
     * @return
     */
    @GetMapping("/queryById")
    public ShowShoppingCartDate queryById(@RequestParam("id") String id){
        return orderShoppingCartService.queryShowShoppingCartDateById(id);
    }

    /**
     *  查询用户购物车所有数据
     * @param userId
     * @param state
     * @return
     */
    @GetMapping("/queryMyShoppingCartUnpaid")
    public List<ShowShoppingCartDate> queryMyShoppingCartUnpaid(@RequestParam("userId") Long userId,@RequestParam("state") String state){
        return orderShoppingCartService.queryMyShoppingCartUnpaid(userId,state);
    }

    /**
     * 根据id 更新购物车主表 和明细状态
     * @param id
     * @param state
     */
    @PostMapping("/updateStateById")
    public void updateStateById(@RequestParam("id") Long id, @RequestParam("state") String state){
        orderShoppingCartService.updateStateById(id,state);
        OrderShoppingCart orderShoppingCart = orderShoppingCartService.getById(id);
        orderShoppingCartDetailsService.updateStateByOrderCode(orderShoppingCart.getOrderCode(),state);
    }
}

