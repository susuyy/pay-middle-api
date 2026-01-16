package com.ht.user.utils;

import com.ht.user.common.Result;
import com.ht.user.common.StatusCode;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/17 14:20
 */
public class ResultUtil {
    public static Result success(Object object) {
        return new Result(true, StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getDesc(), object);
    }

    public static Result success() {
        return success(null);
    }

    public static Result success(String message) {
        return new Result(true, StatusCode.SUCCESS.getCode(), message, null);
    }

    public static Result error(String message){
        return new Result(false,20001,message);
    }

    public static Result error(StatusCode statusCode) {
        return new Result(false, statusCode.getCode(), statusCode.getDesc());
    }

    public static Result error(Integer errorCode, String message) {
        return new Result(false, errorCode, message);
    }

    public static Result error() {
        return error(StatusCode.ERROR);
    }
}
