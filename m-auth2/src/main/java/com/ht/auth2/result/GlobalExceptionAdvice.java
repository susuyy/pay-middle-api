package com.ht.auth2.result;

import org.springframework.boot.context.properties.bind.BindException;
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
    public Result MethodArgumentNotValidExceptionHandler(UserDefinedException e) {
        return new Result(e.getCode(), e.getMsg());
    }

    /**
     * 重复绑定业务异常
     * @param e
     * @return
     */
    @ExceptionHandler({AddMapException.class})
    public Result AddMapExceptionExceptionHandler(AddMapException e) {
        return new Result(ResultTypeEnum.MAP_EXIST.getCode(), ResultTypeEnum.MAP_EXIST.getMessage());
    }

}
