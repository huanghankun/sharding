
package com.example.sharding.aop;


import com.example.sharding.aop.exception.MyException;
import com.example.sharding.common.BusinessConstant;
import com.example.sharding.common.SpringUtil;
import com.example.sharding.util.EntityUtil;
import com.example.sharding.util.JSONUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class OptRepeatCheckPointCut {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptRepeatCheckPointCut.class);

    /**
     * 更新操作
     */
    @Pointcut("@annotation(com.example.sharding.aop.OptRepeatCheck)")
    public void anyOpertion() {
    }


    /**
     * 更新操作单个参数的方法
     *
     * @param pjp
     * @param opt
     * @param toData
     * @return
     * @throws Throwable
     */
    @Around("com.example.sharding.aop.OptRepeatCheckPointCut.anyOpertion() && @annotation(opt)  && args(toData)")
    public Object cache(ProceedingJoinPoint pjp, OptRepeatCheck opt, Object toData) throws Throwable {
        Class clazz = opt.serviceType();
        LOGGER.info("QueryMethodName = {}.{}({})", clazz.getName(), opt.methodName(), toData.getClass().getSimpleName());
        Object service = SpringUtil.getBean(clazz);
        Method method = clazz.getMethod(opt.methodName(), toData.getClass());
        Object fromData = method.invoke(service, toData);
        LOGGER.info("ToData = {}", JSONUtils.toJson(toData));
        LOGGER.info("FromData = {}", JSONUtils.toJson(fromData));
        if (null == fromData) {
            return pjp.proceed();
        }

        Map<String, Object> toMap = EntityUtil.entity2Map(toData);
        Map<String, Object> fromMap = EntityUtil.entity2Map(fromData);
        /**
         * 检查是否重复操作
         */
        LOGGER.info("checkRepeat..");
        checkRepeat(toMap, fromMap);

        Object obj = pjp.proceed();

        /**
         * 更新完毕之后，查询一次反馈到传进来的对象
         */
        fromData = method.invoke(service, toData);
        BeanUtils.copyProperties(toData, fromData);

        return obj;

    }

    /**
     * 检查是否重复操作
     * 1,
     *
     * @param toMap
     * @param fromMap
     */
    private void checkRepeat(Map<String, Object> toMap, Map<String, Object> fromMap) {
        Integer statusTo = (Integer) toMap.get("orderStatus");
        Integer statusFrom = (Integer) fromMap.get("orderStatus");
        Map<Integer, List<BusinessConstant.OrderStatusEnum>> transitionMap = BusinessConstant.OrderStatusEnum.getTransitionMap();
        List<BusinessConstant.OrderStatusEnum> tList = transitionMap.get(statusTo);
        LOGGER.info("statusFrom[{}],statusTo[{}]", statusFrom, statusTo);
        if(!tList.contains(BusinessConstant.OrderStatusEnum.getByCode(statusFrom))){
            throw new MyException("9999","状态不可以从 " + BusinessConstant.OrderStatusEnum.getMsgByCode(statusFrom) + " 到 " + BusinessConstant.OrderStatusEnum.getMsgByCode(statusTo) );

        }

//        Object dataVersionTo = paramMap.get("dataVersion");
//        Object dataVersionFrom = returnMap.get("dataVersion");

//        LOGGER.info("dataVersionFrom[{}],dataVersionTo[{}]", dataVersionFrom, dataVersionTo);


    }


}
