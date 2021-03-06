package com.example.sharding.aop.exception;

import com.example.sharding.aop.OptRepeatCheckPointCut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * controller 全局异常捕捉处理
 *
 * @author hhk
 * @since 2019/2/17
 */
@ControllerAdvice
public class MyControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyControllerAdvice.class);

    /**
     * 全局异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map errorHandler(Exception ex) {
        LOGGER.error(ex.getMessage(),ex);
        Map map = new HashMap();
        map.put("code", 100);
        map.put("msg", ex.getMessage());
        return map;
    }


    /**
     * 运行异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public Map runtimeExceptionHandler(RuntimeException ex) {
        LOGGER.error(ex.getMessage(),ex);
        Map map = new HashMap();
        map.put("code", 101);
        map.put("msg", ex.getMessage());
        return map;
    }

    /**
     * 自定义异常处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public Map myExceptionHandler(MyException ex) {
        LOGGER.error(ex.getMessage(),ex);
        Map map = new HashMap();
        map.put("code", ex.getCode());
        map.put("msg", ex.getMessage());
        return map;
    }

}