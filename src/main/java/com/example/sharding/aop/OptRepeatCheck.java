package com.example.sharding.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 查看版本号和状态是否重复
 * withoutChectOpt - 指定不做检查的操作外，其他操作均不允许重复
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptRepeatCheck {

    /**
     * 查询Server类
     *
     * @return
     */
    Class serviceType();

    /**
     * 查询服务方法名称
     * @return
     */
    String methodName();

}
