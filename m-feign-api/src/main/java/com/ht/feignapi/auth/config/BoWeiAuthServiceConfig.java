package com.ht.feignapi.auth.config;

public class BoWeiAuthServiceConfig {


    /**
     * 登录url
     */
    public static final String LOGIN_URL = "http://47.99.85.203:8084/api//blade-auth/oauth/token";


    /**
     * Authorization_KEY
     */
    public static final String Authorization_KEY = "Authorization";


    /**
     * Authorization_VALUE
     */
    public static final String Authorization_VALUE = "Basic MDAxOmpnZF9zZWNyZXQ=";

    /**
     * Tenant_Id_KEY
     */
    public static final String Tenant_Id_KEY = "Tenant-Id";

    /**
     * Tenant_Id_VALUE
     */
    public static final String Tenant_Id_VALUE = "000000";

    /**
     * tenantId_KEY
     */
    public static final String tenantId_KEY = "tenantId";


    /**
     * tenantId_VALUE
     */
    public static final String tenantId_VALUE = "000000";

    /**
     * 授权key
     */
    public static final String Blade_Auth_KEY = "Blade-Auth";

    /**
     * 授权key Bearer token
     */
    public static final String Bearer = "Bearer ";

}
