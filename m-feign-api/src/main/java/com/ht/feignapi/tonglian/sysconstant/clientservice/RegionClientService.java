package com.ht.feignapi.tonglian.sysconstant.clientservice;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.sysconstant.entity.DicRegion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 18:11
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "region")
public interface RegionClientService {

    @GetMapping("/card-region/{regionId}")
    Result<List<DicRegion>> getRegionList(@PathVariable("regionId") Integer regionId);
}
