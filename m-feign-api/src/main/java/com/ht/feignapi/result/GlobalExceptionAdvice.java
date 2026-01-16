package com.ht.feignapi.result;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    //binding Excception handler
    @ExceptionHandler({BindException.class})
    public Result MethodArgumentNotValidExceptionHandler(BindException e) {
        return new Result(ResultTypeEnum.BIND_EXCEPTION, e.getStackTrace());
    }

    //user defined Excception handler
    @ExceptionHandler({UserDefinedException.class})
    public Result UserDefinedExceptionExceptionHandler(UserDefinedException e) {
        return new Result(e.getCode(), e.getMsg());
    }

    //feign调用异常
    @ExceptionHandler({feign.FeignException.class})
    public Result FeignExceptionHandler(feign.FeignException e) {
        return new Result(ResultTypeEnum.FEIGN_ERROR, e.getMessage());
    }

    //权限不足
    @ExceptionHandler({AccessDeniedException.class})
    public Result AccessDeniedExceptionHandler(AccessDeniedException e) {
        return new Result(ResultTypeEnum.ACCESS_DENIED, e.getMessage());
    }

    //token
    @ExceptionHandler({InvalidTokenException.class})
    public Result InvalidTokenExceptionHandler(InvalidTokenException e) {
        return new Result(ResultTypeEnum.TOKEN_ERROR, e.getMessage());
    }

    @ExceptionHandler({ClientException.class})
    public Result ClientExceptionHandler(ClientException e) {
        return new Result(ResultTypeEnum.SEND_CODE_ERROR, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public Result IllegalArgumentExceptionHandler(IllegalArgumentException e){
        return new Result(ResultTypeEnum.SERVICE_ERROR,e.getMessage());
    }

    @ExceptionHandler({CheckException.class})
    public Result CheckExceptionHandler(CheckException e) {
        return new Result(e.getCode(),e.getMsg());
    }

    @ExceptionHandler({ProductionCodeNotExistException.class})
    public Result ProductionCodeNotExistExceptionHandler(ProductionCodeNotExistException e) {
        return new Result(ResultTypeEnum.PRODUCTION_CODE_NOT_EXIST);
    }

    @ExceptionHandler({CodeSendException.class})
    public Result CodeSendExceptionHandler(CodeSendException e) {
        return new Result(ResultTypeEnum.SEND_CODE_ERROR);
    }

    @ExceptionHandler({OpenIdException.class})
    public Result OpenIdExceptionHandler(OpenIdException e) {
        return new Result(e.getCode(),e.getMsg());
    }

    @ExceptionHandler({ResultException.class})
    public Result OpenIdExceptionHandler(ResultException e) {
        return new Result(ResultTypeEnum.SERVICE_ERROR);
    }
}
