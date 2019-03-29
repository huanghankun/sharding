
package com.example.sharding.aop;


import com.example.sharding.aop.exception.MyException;
import com.example.sharding.common.BusinessConstant;
import com.example.sharding.common.SpringUtil;
import com.example.sharding.util.JSONUtils;
import com.util.EntityUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
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
     * @param pjp    切面对象，
     * @param opt
     * @param toData
     * @return
     * @throws Throwable
     */
    @Around("com.example.sharding.aop.OptRepeatCheckPointCut.anyOpertion() && @annotation(opt)  && args(toData)")
    public Object cache(ProceedingJoinPoint pjp, OptRepeatCheck opt, Object toData) throws Throwable {
        Class clazz = opt.serviceType();
        LOGGER.info("检索服务名称 = {}.{}({})", clazz.getName(), opt.methodName(), toData.getClass().getSimpleName());
        Object service = SpringUtil.getBean(clazz);
        Method method = clazz.getMethod(opt.methodName(), toData.getClass());
        Object fromData = method.invoke(service, toData);
        LOGGER.info("原数据 = {}", JSONUtils.toJson(fromData));
        LOGGER.info("目标数据 = {}", JSONUtils.toJson(toData));

        if (null == fromData) {
            return pjp.proceed();
        }


        /**
         * 检查是否重复操作
         */
        checkRepeat(toData, fromData);

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
     * @param toData
     * @param fromData
     */
    private void checkRepeat(Object toData, Object fromData) {
        LOGGER.info("检查状态控制..");
        Map<String, Object> toMap = EntityUtil.entity2Map(toData);
        Map<String, Object> fromMap = EntityUtil.entity2Map(fromData);
        Integer statusTo = (Integer) toMap.get("orderStatus");
        Integer statusFrom = (Integer) fromMap.get("orderStatus");
        Map<Integer, List<BusinessConstant.OrderStatusEnum>> transitionMap = BusinessConstant.OrderStatusEnum.getTransitionMap();
        List<BusinessConstant.OrderStatusEnum> tList = transitionMap.get(statusTo);
        LOGGER.info("来源状态[{}],目标[{}]", BusinessConstant.OrderStatusEnum.getMsgByCode(statusFrom), BusinessConstant.OrderStatusEnum.getMsgByCode(statusTo));
        if (!tList.contains(BusinessConstant.OrderStatusEnum.getByCode(statusFrom))) {
            throw new MyException("9999", "状态不可以从 " + BusinessConstant.OrderStatusEnum.getMsgByCode(statusFrom) + " 到 " + BusinessConstant.OrderStatusEnum.getMsgByCode(statusTo));
        }
        LOGGER.info("检查版本号..");
        Integer dataVersionTo = (Integer) toMap.get("dataVersion");
        Integer dataVersionFrom = (Integer) fromMap.get("dataVersion");

        if (dataVersionFrom == null) {
            throw new MyException("9997", " , 操作版本为空，请输入！ ");
        }

        int compare = Integer.compare(dataVersionTo, dataVersionFrom);
        if (compare > 0) {
            throw new MyException("9998", " , 请勿捏造操作版本 = " + dataVersionTo + ",当前数据库最新版本 = " + dataVersionFrom);
        } else if (compare < 0) {
            throw new MyException("9998", "当前数据版本 = " + dataVersionFrom + " , 操作版本 = " + dataVersionTo);
        }

    }


}
