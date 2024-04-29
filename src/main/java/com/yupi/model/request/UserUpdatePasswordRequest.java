package com.yupi.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdatePasswordRequest implements Serializable {

    private static final long serialVersionUID = -5996345129538944393L;

    /**
     * 原密码
     */
    private String userPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
