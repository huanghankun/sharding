package com.example.sharding.entity;

import java.io.Serializable;

/**
 * Created by Kane on 2018/1/17.
 * @author hhk
 */
public class UserEntity implements Serializable {

    private static final Long serialVersionUID = 1L;
    private Long id;
    private ProvinceEnum province;
    private String city;
    private Long orderId;
    private Long userId;
    private String userName;
    private String passWord;
    private UserSexEnum userSex;
    private String nickName;
    private Integer orderStatus;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserEntity{");
        sb.append("id=").append(id);
        sb.append(", province=").append(province);
        sb.append(", city='").append(city).append('\'');
        sb.append(", orderId=").append(orderId);
        sb.append(", userId=").append(userId);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", passWord='").append(passWord).append('\'');
        sb.append(", userSex=").append(userSex);
        sb.append(", nickName='").append(nickName).append('\'');
        sb.append(", orderStatus='").append(orderStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static Long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProvinceEnum getProvince() {
        return province;
    }

    public void setProvince(ProvinceEnum province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public UserSexEnum getUserSex() {
        return userSex;
    }

    public void setUserSex(UserSexEnum userSex) {
        this.userSex = userSex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}