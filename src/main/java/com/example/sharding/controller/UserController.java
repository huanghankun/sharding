package com.example.sharding.controller;


import com.example.sharding.aop.OptRepeatCheckPointCut;
import com.example.sharding.common.BusinessConstant;
import com.example.sharding.entity.ProvinceEnum;
import com.example.sharding.entity.UserEntity;
import com.example.sharding.entity.UserSexEnum;
import com.example.sharding.service.User1Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Kane on 2018/1/17.
 *
 * @author hhk
 */
@Service
@RestController
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final int THARD_NUM = 10;
    private static final int INSERT_NUM = 10;
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
    public Integer updateTransactional() throws ExecutionException, InterruptedException {
        /**
         * 100线程，每条线程插入1000，用时221175
         * 1000线程，每条线程插入100，用时232116
         */
        Long startTime = System.currentTimeMillis();
        List<Future<Integer>> futureList = new ArrayList<>();
        int sum = 0;
        List<UserEntity> userList = null;
        for (int i = 0; i < THARD_NUM; i++) {
            userList = new ArrayList<>();
            for (int j = 0; j < INSERT_NUM; j++) {
                sum++;
                UserEntity user2 = new Random().nextBoolean() ? createUserGuangdong() : createUserBeijing();
                userList.add(user2);
            }
            Future<Integer> future1 = user1Service.insertUserList(userList);
            futureList.add(future1);
        }
        int all = 0;
        for (Future<Integer> f : futureList) {
            all += f.get();
        }
        Long endTime = System.currentTimeMillis();
        LOGGER.info("输入 = {},保存 = {},用时 = {}", sum, all, (endTime - startTime));

        UserEntity user = userList.get(0);
        UserEntity  newUser = new UserEntity();
        newUser.setId(user.getId());
        newUser.setProvince(user.getProvince());
        newUser.setDataVersion(user.getDataVersion());
        newUser.setOrderStatus(BusinessConstant.OrderStatus.APPROVING);
        user1Service.updateById(newUser);
        user.setOrderStatus(BusinessConstant.OrderStatus.SAVED);
        user1Service.updateById(newUser);
        return sum;
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

    /*
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