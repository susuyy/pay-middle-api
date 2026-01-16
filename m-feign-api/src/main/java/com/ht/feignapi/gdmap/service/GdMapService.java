package com.ht.feignapi.gdmap.service;

import com.ht.feignapi.gdmap.entity.GdMapGeoFence;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service


/**
 * help guide: https://blog.csdn.net/coderyin/article/details/90752333
 *
 * different pata for feign
 *
 * location picker： https://lbs.amap.com/console/show/picker
 * api-documents for GD map fence: https://lbs.amap.com/api/webservice/guide/api/geofence_service
 *
 *
 * create by yuc.sun
 * 2020.3.11
 *
 */
@FeignClient(url = "${mapEngine.url}", name = "GD-map")
public interface GdMapService {
    //GD Map interface

    // get stardard location address via user input address
    @RequestMapping(value = "/v3/geocode/geo", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getFormmatedAddress(@RequestParam("key") String key, @RequestParam("address") String address);

    //创建围栏
    @PostMapping(value = "/v4/geofence/meta", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object createFence(@RequestParam("key") String key, @RequestBody GdMapGeoFence gdMapGeoFence);

    //查询围栏
    @RequestMapping(value = "/v4/geofence/meta", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getFence(@RequestParam("key") String key);

    //修改围栏
    @PostMapping(value = "/v4/geofence/meta", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object updateFence(@RequestParam("key") String key, @RequestParam("gid") String gid);

    //删除围栏
    @DeleteMapping(value = "/v4/geofence/meta", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object deleteFence(@RequestParam("key") String key, @RequestParam("gid") String gid);

    //判断是否在围栏内
    @RequestMapping(value = "/v4/geofence/status", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object whetherInFence(@RequestParam("key") String key,
                                 @RequestParam("diu") String diu,
                                 @RequestParam("uid") String uid,
                                 @RequestParam("locations") String locations);
}