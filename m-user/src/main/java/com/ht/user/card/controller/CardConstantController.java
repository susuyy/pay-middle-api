package com.ht.user.card.controller;

import com.ht.user.sysconstant.entity.DicConstant;
import com.ht.user.sysconstant.service.DicConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/18 9:56
 */
@RestController
@RequestMapping("/card-constant")
public class CardConstantController {
    @Autowired
    private DicConstantService constantService;

    /**
     * 通过key获取配置信息
     * @param key
     * @return
     */
    @GetMapping("/key/{key}")
    public List<DicConstant> getKeyValue(@PathVariable("key") String key ){
        return constantService.getKeyValue(key);
    }

    /**
     * 通过groupCode获取配置信息
     * @param groupCode
     * @return
     */
    @GetMapping("/group/{groupCode}")
    public List<DicConstant> getListByGroupCode(@PathVariable("groupCode") String groupCode){
        return constantService.getListByGroupCode(groupCode);
    }
}
