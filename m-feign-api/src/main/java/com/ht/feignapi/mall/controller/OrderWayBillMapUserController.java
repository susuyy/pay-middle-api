package com.ht.feignapi.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.OrderWayBillsMapUserClientService;
import com.ht.feignapi.mall.entity.OrderWayBillMapUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-10-15
 */
@RestController
@RequestMapping("/mall/orderWayBillMapUser")
@CrossOrigin(allowCredentials = "true")
public class OrderWayBillMapUserController {

    @Autowired
    private OrderWayBillsMapUserClientService orderWayBillsMapUserClientService;

    @Autowired
    private AuthClientService authClientService;

    /**
     * 保存用户派送地址
     * @param orderWayBillMapUser
     */
    @PostMapping("/save")
    public void save(@RequestBody OrderWayBillMapUser orderWayBillMapUser){
        UserUsers userUsers = authClientService.queryByOpenid(orderWayBillMapUser.getOpenId()).getData();
        orderWayBillMapUser.setUserId(userUsers.getId());
        orderWayBillsMapUserClientService.save(orderWayBillMapUser);
    }

    /**
     * 修改用户派送地址
     * @param orderWayBillMapUser
     */
    @PostMapping("/update")
    public void update(@RequestBody OrderWayBillMapUser orderWayBillMapUser){
        orderWayBillsMapUserClientService.updateById(orderWayBillMapUser);
    }

    /**
     * 根据id 获取单条派送数据
     * @param id
     * @return
     */
    @GetMapping("/getOneById")
    public OrderWayBillMapUser getOneById(@RequestParam("id")String id){
        return orderWayBillsMapUserClientService.getById(id).getData();
    }

    /**
     * 获取用户的 派送信息列表
     * @param openId
     * @return
     */
    @GetMapping("/list")
    public List<OrderWayBillMapUser> list(@RequestParam("openId")String openId){
        UserUsers userUsers = authClientService.queryByOpenid(openId).getData();
        return orderWayBillsMapUserClientService.list(userUsers.getId()).getData();
    }

    /**
     * 根据id 删除单条派送数据
     * @param id
     * @return
     */
    @DeleteMapping("/delById")
    public void delById(@RequestParam("id")String id){
        orderWayBillsMapUserClientService.removeById(Long.parseLong(id));
    }

}

