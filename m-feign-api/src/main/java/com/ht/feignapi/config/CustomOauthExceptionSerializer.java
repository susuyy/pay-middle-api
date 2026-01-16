package com.ht.feignapi.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ht.feignapi.result.ResultTypeEnum;


import java.io.IOException;

public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        //value内容适当的做一些错误类型判断
        gen.writeStartObject();
        gen.writeObjectField("code", ResultTypeEnum.AUTH_TOKEN_ERROR.getCode());
        gen.writeObjectField("msg",ResultTypeEnum.AUTH_TOKEN_ERROR.getMessage());
        gen.writeEndObject();
    }
}
