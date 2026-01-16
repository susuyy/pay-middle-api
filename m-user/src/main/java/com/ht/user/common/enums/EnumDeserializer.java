package com.ht.user.common.enums;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/23 18:52
 */
public class EnumDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object o) {
//        final JSONLexer lexer = parser.lexer;
//        Class cls = (Class) type;
//        Object[] enumConstants = cls.getEnumConstants();
//        if (BaseEnum.class.isAssignableFrom(cls)) {
//            for (Object enumConstant : enumConstants) {
//                BaseEnum baseEnum = (BaseEnum) enumConstant;
//                if (lexer.intValue() == baseEnum.getCode()) {
//                    return (T) baseEnum;
//                }
//            }
//        }
        return null;

    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
