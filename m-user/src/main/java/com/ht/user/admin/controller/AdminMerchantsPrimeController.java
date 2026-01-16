package com.ht.user.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.*;
import com.ht.user.card.entity.CardMapUserCards;
import com.ht.user.card.entity.CardOrders;
import com.ht.user.card.service.CardMapUserCardsService;
import com.ht.user.card.service.CardOrdersService;
import com.ht.user.config.UserCardsTypeConfig;
import com.ht.user.sysconstant.DbConstantGroupConfig;
import com.ht.user.sysconstant.service.DicConstantService;
import com.ht.user.utils.CardMoneyAddUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/19 16:03
 */
@RequestMapping("/admin/merchantsPrime")
@RestController
@CrossOrigin
public class AdminMerchantsPrimeController {

    @Autowired
    private DicConstantService dicConstantService;

    @Autowired
    private CardMapUserCardsService userCardsService;

    @Autowired
    private CardOrdersService orderService;





    /**
     * 调账
     * @param merchantCode
     * @param userId
     * @param amount
     * @param adminId
     * @param comments
     * @return
     */
    @PutMapping("/balance/{merchantCode}/{userId}/{amount}/{adminId}")
    public String changeBalance(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("userId") Long userId,
            @PathVariable("amount") Integer amount,
            @PathVariable("adminId") Long adminId,
            @RequestParam(required = false, defaultValue = "") String comments) {
        AdjustAccount adjustAccount = new AdjustAccount();
        adjustAccount.setAmount(amount);
        adjustAccount.setComments(comments);
        adjustAccount.setMerchantCode(merchantCode);
        adjustAccount.setUserId(userId);
        adjustAccount.setOperatorId(adminId);
        orderService.saveOrder(adjustAccount);
        return "调账成功";
    }

    /**
     * 发放会员卡号
     * @param merchantCode 商户号
     * @param id   用户id
     * @param code 会员卡号
     * @return 返回发放结果
     */
    @PutMapping("/cardCode/{merchantCode}/{id}/{code}")
    public String sendVipVirtualCard(@PathVariable("merchantCode") String merchantCode,@PathVariable("id") Long id, @PathVariable("code") String code) {
        userCardsService.updateUserCode(id,code,merchantCode);
        return "发放成功";
    }

    /**
     * 余额充值
     * @param amount
     * @param merchantCode
     * @param ids
     * @param adminId
     * @return
     */
    @PutMapping("/recharge/{merchantCode}/{amount}/{ids}/{adminId}")
    public String recharge(@PathVariable("amount") Integer amount,
                           @PathVariable("merchantCode") String merchantCode,
                           @PathVariable("ids") Long[] ids,
                           @PathVariable("adminId") Long adminId) {
        Recharge recharge = new Recharge();
        recharge.setAmount(amount);
        recharge.setMerchantCode(merchantCode);
        recharge.setOperatorId(adminId);
        recharge.setUserIds(CollectionUtils.arrayToList(ids));
        orderService.recharge(recharge);
        return "提交充值单成功";
    }

    /**
     * 列表充值审核
     * @param orderType
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
    @GetMapping("/recharge/{merchantCode}/{orderType}")
    public IPage<OrdersVo> getRechargeList(
            @PathVariable("orderType") String orderType,
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false, defaultValue = "0") Integer state,
            @RequestParam(required = false, defaultValue = "") String startTime,
            @RequestParam(required = false, defaultValue = "") String endTime,
            @RequestParam(required = false, defaultValue = "") String operator,
            @RequestParam(required = false, defaultValue = "0") Long pageNo,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        IPage<OrdersVo> page = new Page<>(pageNo,pageSize);
        List<OrdersVo> list = orderService.getRechargeOrders(merchantCode, orderType , page);
        page.setRecords(list);
        return page;
    }

    /**
     * 充值审核--确认/拒绝
     * @param orderCode
     * @param amount
     * @param state 状态，审核通过：audit_pass,审核不通过:audit_unpass
     * @return
     */
    @PostMapping("/recharge/{amount}/{orderCode}/{state}")
    public String saveRechargeResult(
            @PathVariable("orderCode") String orderCode,
            @PathVariable("amount") Integer amount,
            @PathVariable("state") String state) {
        try {
            CardOrders order = orderService.getOrder(orderCode);
            if ("audit_pass".equals(state)){
                CardMapUserCards cardMapUserCards = userCardsService.queryByUserIdAndAccount(order.getUserId(), UserCardsTypeConfig.ACCOUNT);
                Assert.notNull(cardMapUserCards,"用户尚未开卡");
                CardMoneyAddUtil.cardMoneyAdd(cardMapUserCards.getCardNo(),amount);
            }
            order.setState(state);
            orderService.save(order);
        } catch (IOException e){
            e.printStackTrace();
        }
        return "充值成功";
    }

    /**
     * 获取用户状态
     * @return 用户状态
     */
    @GetMapping("/memberState")
    public Map<String,String> getMemberState(){
        Map<String,String> map = dicConstantService.getConstantMap(DbConstantGroupConfig.VIP_STATE);
        return map;
    }


//    /**
//     * 批量绑定实体卡
//     * @param file
//     * @param merchantCode
//     * @return
//     */
//    @PostMapping("/userVipCard/{merchantCode}/import")
//    public String sendVipCard(
//            @RequestParam("file") MultipartFile file,
//            @PathVariable("merchantCode") String merchantCode) throws IOException {
//        Assert.isTrue(checkFile(file), "文件格式不正确！");
//        ExcelReader excelReader = null;
//        try {
//            excelReader = EasyExcel.read(file.getInputStream(), UserRealCardImportVo.class,
//                    new UserRealCardListener( mrcMapMerchantPrimesService, merchantCode)).build();
//            ReadSheet readSheet = EasyExcel.readSheet(0).build();
//            excelReader.read(readSheet);
//            return "导入成功";
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw e;
//        } finally {
//            if (excelReader != null) {
//                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
//                excelReader.finish();
//            }
//        }
//    }


    private boolean checkFile(MultipartFile file) {
//        CollectionUtils.lastElement(new ArrayList(file.getOriginalFilename().split(".")))
        return true;
    }
}

