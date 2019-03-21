package com.example.sharding.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by apple on 2018/10/6.
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})}
)
public class EntPluginInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntPluginInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        System.out.println("intercept...intercept:" + invocation.getMethod());
        Object target = invocation.getTarget();
        System.out.println("当前拦截到的对象：" + target);
        //拿到：StatementHandler==>ParameterHandler===>parameterObject
        //拿到target的元数据
        MetaObject metaObject = SystemMetaObject.forObject(target);
        Object value = metaObject.getValue("parameterHandler.parameterObject");
        System.out.println("sql语句用的参数是：" + value);
        Object[] args = invocation.getArgs();

        for (Object o : args) {

        }
        return invocation.proceed();

    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }

}
