package com.ht.merchant.result;

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

    @ExceptionHandler({CodeExistException.class})
    public Result CodeExistExceptionHandler(CodeExistException e) {
        return new Result(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({SortExistException.class})
    public Result CodeExistExceptionHandler(SortExistException e) {
        return new Result(e.getCode(), e.getMessage());
    }
}
