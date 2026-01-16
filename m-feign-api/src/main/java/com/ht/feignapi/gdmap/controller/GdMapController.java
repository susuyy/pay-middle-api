package com.ht.feignapi.gdmap.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.gdmap.entity.GdMapGeoFence;
import com.ht.feignapi.gdmap.service.GdMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/geo/gdmap")
public class GdMapController {

    @Autowired
    private GdMapService gdMapService;
    @Value("${mapEngine.key}")
    public String dgmapKey;

    //get location via user input address

    /**
     * 根据地址获取当前用位置
     *
     * @param key     string 用户
     * @param address string 用户地址
     * @return {
     * "status": "1",
     * "info": "OK",
     * "infocode": "10000",
     * "count": "1",
     * "geocodes": [
     * {
     * "formatted_address": "海南省三亚市吉阳区棕榈滩|20号楼",
     * "country": "中国",
     * "province": "海南省",
     * "citycode": "0899",
     * "city": "三亚市",
     * "district": "吉阳区",
     * "township": [],
     * "neighborhood": {
     * "name": [],
     * "type": []
     * },
     * "building": {
     * "name": [],
     * "type": []
     * },
     * "adcode": "460203",
     * "street": [],
     * "number": [],
     * "location": "109.575406,18.266581",
     * "level": "门牌号"
     * }
     * ]
     * }
     */
    @RequestMapping(method = RequestMethod.GET, path = "/getLocation")
    public Object getLocation(@RequestParam("key") String key, @RequestParam("address") String address) {
        try {
            Object s;
            s = gdMapService.getFormmatedAddress(key, address);
            return s;
        } catch (Exception e) {
            return e;
        }
    }

    //create fence

    /**
     * 创建地域围栏
     *
     * @param key    string 用户
     * @param name   string 用户地址
     * @param points String 围栏点，至少三个，;号分割
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "/createFence")
    public Object createFence(@RequestParam("key") String key, @RequestParam("name") String name, @RequestParam("points") String points) {
        GdMapGeoFence fence = new GdMapGeoFence();
        try {
            //set the fence attribute
            fence.setName(name);
            fence.setPoints("109.588434,18.285465;109.549638,18.291363;109.566805,18.250611");
            fence.setRepeat("Mon,Tues,Wed,Thur,Fri,Sat,Sun");
            JSONObject jsonFence = (JSONObject) JSONObject.toJSON(fence);
            System.out.println(jsonFence);
            Object s;
            s = gdMapService.createFence(key, fence);
            return s;
        } catch (Exception e) {
            return e;
        }
    }

    //get fences

    /**
     * 获取地域围栏列表
     *
     * @param key string 用户key
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/getFences")
    public Object getFences(@RequestParam("key") String key) {
        try {
            Object s;
            s = gdMapService.getFence(key);
            return s;
        } catch (Exception e) {
            return e;
        }
    }

    //delete fence

    /**
     * 删除地域围栏
     *
     * @param key string 用户key
     * @param gid string 围栏地理位置id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/deleteFence")
    public Object deleteFence(@RequestParam("key") String key, @RequestParam("gid") String gid) {

        try {
            Object s;
            s = gdMapService.deleteFence(key, gid);
            return s;
        } catch (Exception e) {
            return e;
        }
    }

    //get status whether location in any fence

    /**
     * 判断用户是否在围栏内
     *
     * @param key       string 用户key
     * @param diu       string 手机设备号，最好是有imei号
     * @param uid       string 客户系统的用户标识id，例如自己开发系统的user_id
     * @param locations string  需要判定的位置，可输入多个位置，简化起见，只输入一个位置，格式：经度，纬度，时间戳;
     * @return {
     * "data": {
     * "fencing_event_list": [
     * {
     * "client_status": "in",
     * "client_action": "enter",
     * "enter_time": "2017-01-19 16:57:12",
     * "fence_info": {
     * "fence_gid": "76705713-7e6d-4b55-bea3-dc08286a9c4c",
     * "fence_center": "109.56829,18.275812",
     * "fence_name": "ds_fence03"
     * }
     * }
     * ],
     * "status": 0
     * },
     * "errcode": 0,
     * "errdetail": null,
     * "errmsg": "OK",
     * "ext": null
     * }
     */
    @RequestMapping(method = RequestMethod.GET, path = "/whetherInFence")
    public Object whetherInFence(@RequestParam("key") String key,
                                 @RequestParam("diu") String diu,
                                 @RequestParam("uid") String uid,
                                 @RequestParam("locations") String locations) {

        try {

            System.currentTimeMillis();
            Object s;
            s = gdMapService.whetherInFence(key, diu, uid, locations);
            return s;
        } catch (Exception e) {
            return e;
        }
    }
}
