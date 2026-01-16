package com.ht.feignapi.config;


import com.ht.feignapi.util.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
@Component
public abstract class GlobalDataAdvice implements ResponseBodyAdvice<Object> {
//        @Override
    public Object beforeBodyWrite(Object arg0, MethodParameter arg1, MediaType arg2,
                                  Class<? extends HttpMessageConverter<?>> arg3, ServerHttpRequest arg4, ServerHttpResponse arg5) {

        final String returnTypeName = arg1.getParameterType().getName();
        // 可判断方法返回类型，做出相应返回处理。例如：测试类中void的返回类型，获取到以后自动装载成全局统一返回格式。此处可做
        if ("void".equals(returnTypeName)) {
            return Result.success();
        }
        if ("com.fun.common.tools.com.ht.merchant.result.CustomResponse".equals(returnTypeName)) {
            return arg0;
        }
        return Result.success(arg0);
    }

    @Override
    public boolean supports(MethodParameter arg0, Class<? extends HttpMessageConverter<?>> arg1) {

        final String returnTypeName = arg0.getParameterType().getName();
        // 用于判断是否需要做处理
        return !"com.ht.feignapi.util.com.ht.merchant.result.Result".equals(returnTypeName);
    }
}