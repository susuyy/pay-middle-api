package com.ht.feignapi.util;

import com.sun.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
 * 功能描述: 逻辑表达式条件判断
 * @Author: liwg
 * @Date: 2020/10/12 17:20
 */
public class ElUtils {

    /**
     * 功能描述: 判断el表达式条件是否通过
     * @Author: liwg
     * @Date: 2020/10/12 17:19
     */
    public static  boolean isCondition(String el,String key,String value) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
    }
}