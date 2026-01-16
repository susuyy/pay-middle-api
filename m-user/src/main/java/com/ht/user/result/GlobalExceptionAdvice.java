package com.ht.user.result;

import com.ht.user.outlets.exception.CheckException;
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

//    //user defined Excception handler
//    @ExceptionHandler({RuntimeException.class})
//    public Result MethodArgumentNotValidRuntimeExceptionHandler(RuntimeException e) {
//        return new Result(20001, e.getMessage());
//    }
//
//    @ExceptionHandler({Exception.class})
//    public Result MethodArgumentNotValidAllExceptionHandler(Exception e) {
//        return new Result(20001, e.getMessage());
//    }

    @ExceptionHandler({CheckException.class})
    public Result CheckExceptionHandler(CheckException e) {
        return new Result(e.getCode(),e.getMsg());
    }
}
