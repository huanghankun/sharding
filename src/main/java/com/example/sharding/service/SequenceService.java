package com.example.sharding.service;

import com.example.sharding.entity.UserEntity;

import java.util.List;

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


}