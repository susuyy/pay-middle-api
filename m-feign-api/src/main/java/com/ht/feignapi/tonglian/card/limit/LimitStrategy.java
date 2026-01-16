package com.ht.feignapi.tonglian.card.limit;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 10:07
 */
public interface LimitStrategy {
    /**
     * 校验卡规则
     * @return 可用返回true，不可用，返回false
     */
    Boolean checkLimit();
}
