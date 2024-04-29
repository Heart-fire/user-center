package com.yupi.constant;

public interface UserConstant {
    //setAttribute它是一个map,它可以存储键值对，我们就需要设置一个键值对，给用户的登录状态分配一个键
    String USER_LOGIN_STATE = "userLoginstate";
    // --权限-------------------
    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    //管理员权限
    int ADMIN_ROLE = 1;

    /**
     * ban-封号
     */
    String USER_BAN = "2";

}
