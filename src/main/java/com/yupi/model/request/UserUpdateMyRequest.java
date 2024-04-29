package com.yupi.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新个人信息请求
 *
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别 男1 女0
     */
    private Integer gender;

    /**
     * 学号
     */
    private String planetCode;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;



    private static final long serialVersionUID = 1L;
}
