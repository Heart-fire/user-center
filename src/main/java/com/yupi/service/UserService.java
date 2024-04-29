package com.yupi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.model.request.UserSearchRequest;
import com.yupi.model.request.UserUpdatePasswordRequest;

import javax.servlet.http.HttpServletRequest;

/**
* @author XinHuo
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-03-27 15:17:06
*/
public interface UserService extends IService<User> {


    /**
     * ===========================================用户注册
     *@param userAccount 用户账户
     *@param userPassword 用户密码
     *@param checkPassword 校验密码
     * @parade planetCode 学号
     *@return 新用户id
     */
     long userRegister(String username,String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * ============================================用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
     User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 请求用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 修改密码
     *
     * @param updatePasswordRequest
     * @param request
     */
    boolean updateUserPassword(UserUpdatePasswordRequest updatePasswordRequest, HttpServletRequest request);
    /**
     * 分页条件
     * @param searchRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserSearchRequest searchRequest);
}
