package com.ht.user.common.formatter;

import com.ht.user.common.enums.CardTypEnum;
import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/23 18:11
 */
public class CardTypeConverter implements Converter<String, CardTypEnum> {

     @Override
     public CardTypEnum convert(String source) {
      return null;
     }
}
