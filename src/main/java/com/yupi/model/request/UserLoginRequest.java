package com.yupi.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 8055192595441380580L;
    private String userAccount;
    private String userPassword;
}
