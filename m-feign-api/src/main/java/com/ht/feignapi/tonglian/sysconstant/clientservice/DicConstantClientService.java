package com.ht.feignapi.tonglian.sysconstant.clientservice;

import com.ht.feignapi.config.DbConstantGroupConfig;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.sysconstant.entity.DicConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 18:07
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "constant")
public interface DicConstantClientService {

    @GetMapping("/card-constant/key/{key}")
    Result<List<DicConstant>> getKeyValue(@PathVariable("key") String key );

    @GetMapping("/card-constant/group/{groupCode}")
    Result<List<DicConstant>> getListByGroupCode(@PathVariable("groupCode") String groupCode);
}
