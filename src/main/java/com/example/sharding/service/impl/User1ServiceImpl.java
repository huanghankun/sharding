package com.example.sharding.service.impl;

import com.example.sharding.aop.OptRepeatCheck;
import com.example.sharding.entity.UserEntity;
import com.example.sharding.mapper.SequenceMapper;
import com.example.sharding.mapper.User1Mapper;
import com.example.sharding.service.SequenceService;
import com.example.sharding.service.User1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SocketUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Kane on 2018/1/17.
 *
 * @author hhk
 */

@Service
public class User1ServiceImpl implements User1Service {

    @Resource
    private User1Mapper user1Mapper;
    @Resource
    private SequenceService sequenceService;



    @Override
    public List<UserEntity> getUsersByProvince(String province) {
        if (null == province || province.isEmpty()) {
            return null;
        }
        List<UserEntity> users = user1Mapper.getUsersByProvince(province);
        return users;
    }

    @Override
    public void insertUser(UserEntity user)  {
        Long id = sequenceService.getNextSequenceByClass(UserEntity.class);
        user.setId(id);
        user.setUserId(id);
        user.setOrderStatus(1);
        user1Mapper.insert(user);
        try {
            Future<String> task1 = sequenceService.doTaskOne();
            Future<String> task2 = sequenceService.doTaskTwo();
            Future<String> task3 = sequenceService.doTaskThree();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserList(List<UserEntity> userList) {
        if (!CollectionUtils.isEmpty(userList)) {
            for (UserEntity user : userList) {
                insertUser(user);
            }
        }
    }

    @Override
    public UserEntity queryById(UserEntity user) {
        if (null == user||null == user.getId()) {
            return null;
        }
        UserEntity u = user1Mapper.queryById(user);
        return u;
    }

    @Override
    @OptRepeatCheck(serviceType = User1ServiceImpl.class,methodName = "queryById")
    public void updateById(UserEntity user) {
        if (null == user||null == user.getId()) {
            return ;
        }
        user1Mapper.updateById(user);

    }


}