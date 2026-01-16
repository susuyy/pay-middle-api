package com.ht.feignapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 将该项目作为资源服务器的配置
 * <p>
 * author : suyangyu
 */

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    /***
     * 定义JwtTokenStore
     * @param
     * @return
     */
    @Bean
    @Primary
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /***
     * 定义JJwtAccessTokenConverter
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // 配置jwt使用的密钥
        jwtAccessTokenConverter.setSigningKey(JWTKeyConfig.JWT_KEY);
        return jwtAccessTokenConverter;
    }


    /***
     * Http安全配置，对每个到达系统的http请求链接进行校验
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //所有请求必须认证通过
        http.authorizeRequests()
                //下边的路径放行
                .antMatchers(
                        "/auth/userUsers/register",
                        "/auth/userUsers/login",
                        "/auth/userUsers/loginOpenid",
                        "/appshow/**",
                        "/tonglian/orders/**",
                        "/tonglian/pay/**",
                        "/tonglian/**",
                        "/admin/**",
                        "/mall/**",
                        "/ms/**",
                        "/higo/**",
                        "/pos/**",
                        "/quiz/userUsers/register",
                        "/bpmn/**",
                        "/docs/**",
                        "/file/**",
                        "/swagger-ui.html",
                        "/swagger/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/wxOfficial/**",
                        "/v2/**",
                        "/static/**",
                        "/shopping-mall/user/app/**",
                        "/auth/adminLogin/**"
                ). //配置地址放行
                permitAll()
                .anyRequest().
                authenticated();    //其他地址需要认证授权
        //开启跨域放行
        http.cors().configurationSource(CorsConfigurationSource());
    }

    //配置跨域访问资源
    private CorsConfigurationSource CorsConfigurationSource() {
        CorsConfigurationSource source =   new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");	//同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedHeader("*");//header，允许哪些header
        corsConfiguration.addAllowedMethod("*");	//允许的请求方法，PSOT、GET等
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**",corsConfiguration); //配置允许跨域访问的url
        return source;
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionTranslator(new CustomExceptionTranslator());
        resources.authenticationEntryPoint(authenticationEntryPoint);
    }
}
