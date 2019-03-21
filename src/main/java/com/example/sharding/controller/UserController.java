package com.example.sharding.controller;


import com.example.sharding.common.BusinessConstant;
import com.example.sharding.entity.ProvinceEnum;
import com.example.sharding.entity.UserEntity;
import com.example.sharding.entity.UserSexEnum;
import com.example.sharding.service.User1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Kane on 2018/1/17.
 * @author hhk
 */
@Service
@RestController
public class UserController {
    private static final int INSERT_NUM = 2;
    @Autowired
    private User1Service user1Service;

    @RequestMapping("/getUsers")
    public List<UserEntity> getUsers() {
        List<UserEntity> users = user1Service.getUsersByProvince("guangdong");
        return users;
    }


    /**
     * 测试
     *
     * @return
     */
    @RequestMapping(value = "update")
    public Integer updateTransactional() {
        List<UserEntity> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            UserEntity user2 = createUserGuangdong();
            user2 = new Random().nextBoolean() ? createUserGuangdong():createUserBeijing();
            userList.add(user2);
        }

        user1Service.insertUserList(userList);
        userList.get(0).setOrderStatus(BusinessConstant.OrderStatus.APPROVING);
        user1Service.updateById(userList.get(0));
        userList.get(0).setOrderStatus(BusinessConstant.OrderStatus.SAVED);
        user1Service.updateById(userList.get(0));
        return userList.size();
    }

    private UserEntity createUserGuangdong() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        UserEntity user2 = new UserEntity();
        user2.setProvince(ProvinceEnum.GUANGDONG);
        user2.setCity(new Random().nextBoolean() ? ("ZHAOQING") : (new Random().nextBoolean() ? "SHENZHEN" : "GUANGZHOU"));
        user2.setOrderId(new Long(new Random().nextInt(100) + 1));
        user2.setNickName("hhk[" + uuid.substring(0, 5) + "]");
        user2.setPassWord(uuid);
        user2.setUserName("hhk[" + uuid.substring(5, 10) + "]");
        user2.setUserSex(new Random().nextBoolean() ? UserSexEnum.MAN : UserSexEnum.WOMAN);
        return user2;
    }

    /**
     *
     *
     */
    private UserEntity createUserBeijing() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        UserEntity user2 = new UserEntity();
        user2.setProvince(ProvinceEnum.BEIJING);
        user2.setCity("BEIJING");
        user2.setOrderId(new Long(new Random().nextInt(100) + 1));
        user2.setNickName("hhk[" + uuid.substring(0, 5) + "]");
        user2.setPassWord(uuid);
        user2.setUserName("hhk[" + uuid.substring(5, 10) + "]");
        user2.setUserSex(new Random().nextBoolean() ? UserSexEnum.MAN : UserSexEnum.WOMAN);
       return user2;
    }

}