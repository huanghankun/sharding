package com.example.sharding.service;

import com.example.sharding.entity.UserEntity;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Kane on 2018/1/17.
 */
public interface User1Service {


    /**
     * 通过省份获取用户
     *
     * @param province
     * @return
     */
    public List<UserEntity> getUsersByProvince(String province);

    /**
     * 插入用户
     *
     * @param user
     */
    public void insertUser(UserEntity user);

    /**
     * 插入用户列表
     *
     * @param userList
     */
    public Future<Integer> insertUserList(List<UserEntity> userList);

    /**
     * 通过ID查询
     * @return
     */
    public UserEntity queryById(UserEntity user);

    /**
     * 更新
     * @return
     */
     void updateById(UserEntity user);

}