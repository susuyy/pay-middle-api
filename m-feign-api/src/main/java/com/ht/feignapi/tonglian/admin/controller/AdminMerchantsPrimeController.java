package com.ht.feignapi.tonglian.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.config.DbConstantGroupConfig;
import com.ht.feignapi.tonglian.admin.entity.OrdersVo;
import com.ht.feignapi.tonglian.admin.entity.VipLevelVo;
import com.ht.feignapi.tonglian.admin.entity.VipSearch;
import com.ht.feignapi.tonglian.admin.entity.VipVo;
import com.ht.feignapi.tonglian.admin.excel.entity.MemberImportVo;
import com.ht.feignapi.tonglian.admin.excel.entity.UserRealCardImportVo;
import com.ht.feignapi.tonglian.admin.excel.listener.DataListener;
import com.ht.feignapi.tonglian.admin.excel.listener.UserRealCardListener;
import com.ht.feignapi.tonglian.card.service.CardMapMerchantCardService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfig;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/19 16:03
 */
@RequestMapping("/admin/merchantsPrime")
@RestController
@CrossOrigin
public class AdminMerchantsPrimeController {

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantPrimeService merchantPrimeService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private CardMapMerchantCardService cardMapMerchantCardService;

    @Autowired
    private UserUsersService userUsersService;

    /**
     * 获取商户会员
     *
     * @param merchantCode 商户号
     * @param pageNo       页码
     * @param pageSize     每页展示数据条数
     * @return 列表
     */
    @GetMapping("/vip/{merchantCode}")
    public IPage<VipVo> getMerchantsPrimes(@PathVariable("merchantCode") String merchantCode,
                                           VipSearch vipSearch,
                                           @RequestParam(required = false, defaultValue = "0") Long pageNo,
                                           @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        return merchantPrimeService.getMerchantAllVipUsers(merchantCode, vipSearch, pageNo,pageSize);
    }

    /**
     * 保存vip状态
     * @param vipId 商户会员id
     * @return 保存结果
     */
//    @PutMapping("/{vipId}")
//    public Result saveVipState(@PathVariable("vipId") Long vipId,
//                               @RequestBody Map<String,String> map) {
//        Assert.isTrue(map.containsKey("state"),"缺少state参数");
//        MrcMapMerchantPrimes merchantPrimes = mrcMapMerchantPrimesService.getById(vipId);
//        merchantPrimes.setState(map.get("state"));
//        Boolean result = mrcMapMerchantPrimesService.updateById(merchantPrimes);
//        Assert.isTrue(result,"保存失败");
//        return ResultUtil.success("保存成功");
//    }

    /**
     * 重置密码
     *
     * @param id       商户会员id
     * @param map 密码
     * @return 保存结果
     */
    @PutMapping("/resetPassword/{id}")
    public String reset(@PathVariable("id") Long id,@RequestBody HashMap<String,String> map) {
        Assert.isTrue(map.containsKey("password"),"密码不能为空！");
        UserUsers userUsers = new UserUsers();
        userUsers.setId(id);
        userUsers.setPassword(map.get("password"));
        authClientService.updatePasswordByUserIdTL(userUsers);
//        authClientService.resetPassword(map.get("password"),id);
        return "重置成功";
    }

    /**
     * 调账
     * @param merchantCode
     * @param userId
     * @param amount
     * @param adminId
     * @param comments
     * @return
     */
//    @PutMapping("/balance/{merchantCode}/{userId}/{amount}/{adminId}")
//    public Result changeBalance(
//            @PathVariable("merchantCode") String merchantCode,
//            @PathVariable("userId") Long userId,
//            @PathVariable("amount") Integer amount,
//            @PathVariable("adminId") Long adminId,
//            @RequestParam(required = false, defaultValue = "") String comments) {
//        AdjustAccount adjustAccount = new AdjustAccount();
//        adjustAccount.setAmount(amount);
//        adjustAccount.setComments(comments);
//        adjustAccount.setMerchantCode(merchantCode);
//        adjustAccount.setUserId(userId);
//        adjustAccount.setOperatorId(adminId);
//        orderService.saveOrder(adjustAccount);
//        return ResultUtil.success("调账成功");
//    }

    /**
     * 列表充值审核
     * @param merchantCode
     * @param type
     * @param state
     * @param startTime
     * @param endTime
     * @param operator
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/recharge/{merchantCode}")
    public IPage<OrdersVo> getRechargeList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false, defaultValue = "0") Integer state,
            @RequestParam(required = false, defaultValue = "") String startTime,
            @RequestParam(required = false, defaultValue = "") String endTime,
            @RequestParam(required = false, defaultValue = "") String operator,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        return cardMapMerchantCardService.getRechargeOrders(merchantCode,"admin_recharge",pageNo,pageSize);
    }

    /**
     * 调账审核列表
     * @param merchantCode
     * @param type      充值账户类型
     * @param state     状态
     * @param startTime 充值时间开始时间
     * @param endTime   充值时间结束时间
     * @param operator  操作员
     * @param pageNo    页码
     * @param pageSize  每页展示数据条数
     * @return 列表
     */
    @GetMapping("/adjust/{merchantCode}")
    public IPage<OrdersVo> getAdjustList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false, defaultValue = "0") Integer state,
            @RequestParam(required = false, defaultValue = "") String startTime,
            @RequestParam(required = false, defaultValue = "") String endTime,
            @RequestParam(required = false, defaultValue = "") String operator,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        return cardMapMerchantCardService.getRechargeOrders(merchantCode,"admin_adjust",pageNo,pageSize);
    }


    /**
     * 获取用户状态
     * @return 用户状态
     */
//    @GetMapping("/memberState")
//    public Result getMemberState(){
//        Map<String,String> map = dicConstantService.getConstantMap(DbConstantGroupConfig.VIP_STATE);
//        return ResultUtil.success(map);
//    }

    /**
     * 获取商家下的会员等级
     * @param merchantCode 商户code
     * @return 会员等级信息
     */
    @GetMapping("/vipLevel/{merchantCode}")
    public List<VipLevelVo> getVipLevel(@PathVariable("merchantCode") String merchantCode) {
        List<MerchantsConfig> vipConfigs = merchantsConfigClientService.getListByGroupCode(merchantCode, DbConstantGroupConfig.VIP_LEVEL).getData();
        List<VipLevelVo> list = new ArrayList<>();
        vipConfigs.forEach(e->{
            VipLevelVo vo = new VipLevelVo();
            vo.setLevelName(e.getValue());
            vo.setLevelType(e.getType());
            list.add(vo);
        });
        return list;
    }


    /**
     * excel导入
     * @param file
     * @param merchantCode
     * @param memberType
     * @return
     */
    @PostMapping("/{merchantCode}/{memberType}/excel")
    public String importMemberData(
            @RequestParam("file") MultipartFile file,
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("memberType") String memberType){
        Assert.isTrue(checkFile(file),"文件格式不正确！");
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), MemberImportVo.class, new DataListener(userUsersService,merchantCode,memberType)).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
            return "导入成功";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
         return "导入失败";
    }

    /**
     * 批量绑定实体卡
     * @param file
     * @param merchantCode
     * @return
     */
    @PostMapping("/userVipCard/{merchantCode}/import")
    public String sendVipCard(
            @RequestParam("file") MultipartFile file,
            @PathVariable("merchantCode") String merchantCode) {
        Assert.isTrue(checkFile(file), "文件格式不正确！");
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), UserRealCardImportVo.class,
                    new UserRealCardListener(merchantPrimeService, merchantCode)).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
            return "导入成功";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
        return "导入失败";
    }


    private boolean checkFile(MultipartFile file) {
//        CollectionUtils.lastElement(new ArrayList(file.getOriginalFilename().split(".")))
        return true;
    }
}

