package com.example.sharding.mapper;


import com.example.sharding.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Kane on 2018/1/17.
 */
public interface User1Mapper {

    /**
     * 通过省份获取用户
     * @param province
     * @return
     */
    List<UserEntity> getUsersByProvince(@Param("province") String province);


    /**
     * 插入用户
     * @param user
     */
    void insert(UserEntity user);

    /**
     * 通过ID查询
     * @param user
     * @return
     */
    UserEntity queryById(UserEntity user);

    /**
     * 更新用户
     * @param user
     */
    void updateById(UserEntity user);


}
