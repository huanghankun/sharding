package com.example.sharding.service;

import com.example.sharding.entity.UserEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Kane on 2018/1/17.
 */
public interface SequenceService {


    /**
     * 获取下一个序列
     * @param clazz
     * @return
     */
    public  Long getNextSequenceByClass(Class clazz);


    public Future<String> doTaskOne () throws Exception;

    public Future<String> doTaskTwo () throws Exception;

    //任务3;
    @Async
    Future<String> doTaskThree() throws Exception;
}