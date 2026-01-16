package com.ht.feignapi.util;

public interface BeanToJson {

    default String toJson() throws Exception {
        return FormatUtil.toJson(this);
    }

    default String toJsonNoException() {
        return FormatUtil.toJsonNoException(this);
    }
}