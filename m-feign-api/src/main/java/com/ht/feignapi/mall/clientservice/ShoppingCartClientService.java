package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.higo.entity.HiGoAddShoppingCartData;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${custom.client.order.name}",contextId = "shoppingCart")
public interface ShoppingCartClientService {

    /**
     * 用户添加购物车
     * @param addShoppingCartDate
     */
    @PostMapping("/mall/shoppingCart/addShoppingCart")
    void addShoppingCartDate(@RequestBody AddShoppingCartDate addShoppingCartDate);

    /**
     * 展示用户 购物车 按店铺分页
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/mall/shoppingCart/showShoppingCart")
    Result<RetPageCartData> showShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData);

    /**
     * 展示用户 购物车 按店铺分页
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/mall/shoppingCart/showShoppingCartHiGo")
    Result<RetPageCartData> showShoppingCartHiGo(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData);

    /**
     * 修改购物车 数量
     * @param updateQuantityData
     * @return
     */
    @PostMapping("/mall/shoppingCart/updateQuantityById")
    Result<UpdateQuantityData> updateQuantityById(@RequestBody UpdateQuantityData updateQuantityData);

    /**
     * 获取用户 失效宝贝
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/mall/shoppingCart/invalidCart")
    Result<Page<ShowShoppingCartDate>> invalidCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData);

    /**
     * 获取用户 购物车 商品数量
     * @param userId
     * @return
     */
    @GetMapping("/mall/shoppingCart/productionsCount")
    Result<Integer> productionsCount(@RequestParam("userId") Long userId);

    /**
     * 清空失效宝贝
     * @param userId
     */
    @PostMapping("/mall/shoppingCart/delInvalidCart")
    void delInvalidCart(@RequestParam("userId") Long userId);

    /**
     *  获取更多 某个商家下的 购物车 数据
     * @param requestShowShoppingCartData
     * @return
     */
    @PostMapping("/mall/shoppingCart/moreShoppingCart")
    Result<Page<ShowShoppingCartDate>> moreShoppingCart(@RequestBody RequestShowShoppingCartData requestShowShoppingCartData);

    /**
     * 根据batchCode删除购物车
     * @param orderCode
     */
    @PostMapping("/mall/shoppingCart/delByOrderCode")
    void paySuccessDelByOrderCode(@RequestParam("orderCode") String orderCode);

    /**
     * 根据orderCode 查询购物车数据
     * @param shoppingCartOrderCode
     * @return
     */
    @PostMapping("/mall/shoppingCart/queryByOrderCode")
    Result<OrderShoppingCart> queryByOrderCode(@RequestParam("shoppingCartOrderCode") String shoppingCartOrderCode);

    /**
     * 查询购物车集合 根据orderCode集合 和商户编码
     * @param shoppingCartOrderCodeList
     * @param merchantCode
     * @return
     */
    @PostMapping("/mall/shoppingCart/queryByOrderCodeListAndMerchantCode")
    Result<List<ShowShoppingCartDate>> queryByOrderCodeListAndMerchantCode(@RequestBody List<String> shoppingCartOrderCodeList,@RequestParam("merchantCode") String merchantCode);

    /**
     * 查询购物车集合 根据orderCode集合
     * @param shoppingCartOrderCodeList
     * @return
     */
    @PostMapping("/mall/shoppingCart/queryByOrderCodeList")
    Result<List<ShowShoppingCartDate>> queryByOrderCodeList(@RequestBody List<String> shoppingCartOrderCodeList);

    /**
     * 根据id 查询购物车数据
     * @param id
     * @return
     */
    @GetMapping("/mall/shoppingCart/queryById")
    Result<ShowShoppingCartDate> queryById(@RequestParam("id") String id);

    /**
     *  查询用户购物车所有数据
     * @param userId
     * @param state
     * @return
     */
    @GetMapping("/mall/shoppingCart/queryMyShoppingCartUnpaid")
    Result<List<ShowShoppingCartDate>> queryMyShoppingCartUnpaid(@RequestParam("userId") Long userId,@RequestParam("state") String state);

    /**
     * 根据id 更新购物车主表 和明细状态
     * @param id
     * @param state
     */
    @PostMapping("/mall/shoppingCart/updateStateById")
    void updateStateById(@RequestParam("id") Long id, @RequestParam("state") String state);

    /**
     * 用户 删除购物车
     * @param orderCode
     */
    @DeleteMapping("/mall/shoppingCart/delShoppingCart")
    void delShoppingCart(@RequestParam("orderCode") String orderCode);

    /**
     * 记录购物车选中状态
     * @param orderCode
     * @param selected
     */
    @PostMapping("/mall/shoppingCart/recordSelected")
    void updateSelectedByOrderCode(@RequestParam("orderCode")String orderCode,@RequestParam("selected") String selected);

    /**
     * HIGO商品添加购物车
     * @param hiGoAddShoppingCartData
     */
    @PostMapping("/mall/shoppingCart/addShoppingCartDataHiGo")
    void addShoppingCartDateHiGo(@RequestBody HiGoAddShoppingCartData hiGoAddShoppingCartData);

    /**
     * 修改购物车 数量
     * @param updateQuantityData
     * @return
     */
    @PostMapping("/mall/shoppingCart/hiGoUpdateQuantityById")
    Result<UpdateQuantityData> hiGoUpdateQuantityById(UpdateQuantityData updateQuantityData);

    /**
     * 获取用户 购物车 商品数量
     * @param userId
     * @return
     */
    @GetMapping("/mall/shoppingCart/productionsCountHiGo")
    Result<Integer> productionsCountHiGo(@RequestParam("userId")long userId);

}
