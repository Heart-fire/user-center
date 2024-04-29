package com.yupi.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 账号密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 10位学号
     */
    private String planetCode;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-正常
     */
    private Integer userStatus;

    private static final long serialVersionUID = 1L;
}
