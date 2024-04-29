package com.yupi.model.request;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
//生成了序列化id
    private static final long serialVersionUID = 4475332955107312306L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
    private String username;

}
