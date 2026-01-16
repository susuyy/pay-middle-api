package com.ht.user.outlets.paystrategy;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PayCompanyStrategyFactory {

    private static Map<String,PayCompanyStrategy> services = new ConcurrentHashMap<>();

    /**
     * 获取策略
     * @param companyChannel
     * @return
     */
    public static PayCompanyStrategy getByCompanyChannel(String companyChannel){
        return services.get(companyChannel);
    }

    /**
     * 策略实现类注册
     * @param companyChannel
     * @param payCompanyStrategy
     */
    public static void register(String companyChannel,PayCompanyStrategy payCompanyStrategy){
        Assert.notNull(companyChannel,"companyChannel can't be null");
        services.put(companyChannel,payCompanyStrategy);
    }
}
