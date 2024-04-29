package com.yupi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.common.BaseResponse;
import com.yupi.common.ErrorCode;
import com.yupi.common.ResultUtil;
import com.yupi.exception.BusinessException;
import com.yupi.exception.ThrowUtils;
import com.yupi.model.domain.User;
import com.yupi.model.request.*;
import com.yupi.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.constant.UserConstant.ADMIN_ROLE;
import static com.yupi.constant.UserConstant.USER_LOGIN_STATE;

//控制层Controller封装请求
//controller层倾向于对请求参数本身的校验，不涉及业务罗辑本身(越少越好)
//service层是对业务逻辑的校验（有可能被controller之外的类调用）
//@RestController适用于编写restful风格的api,返回值默认为json类型
@RequestMapping("/user")
@RestController//咱们这个类里面所有的请求的接口返回值，响应的数据类型都是application json
public class UserController {

    @Resource//spring提供的注解
    private UserService userService;

    //------------------------------添加返回类型---------------用户注册---
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userName = userRegisterRequest.getUsername();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userName,userAccount, userPassword, checkPassword,planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userName,userAccount, userPassword, checkPassword,planetCode);
        return ResultUtil.success(result);
    }

    //-------------------------------------------------------------用户登录--
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }
    //-------------------------------------------------------------用户注销--

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
       if (request == null){
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
        int result = userService.userLogout(request);
        return ResultUtil.success(result);
    }
    //---------------------------------------------------------------------

    /**
     * 当前登录用户请求
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        // 获取登录态
        User resultUser = userService.getLoginUser(request);
        return ResultUtil.success(resultUser);
    }

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "不好意思，您暂无权限");
        }
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtil.success(user.getId());
    }

    //    ------------------------------------------------------------查询用户---------
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(UserSearchRequest searchRequest) {
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(searchRequest);
        List<User> userList = userService.list(queryWrapper);
        List<User> users = userList.stream().map(userService::getSafetyUser).collect(Collectors.toList());
        return ResultUtil.success(users);
    }

    //    --------------------------------------------------------------删除用户---------

    /**
     * 删除用户
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest deleteRequest, HttpServletRequest request) {
        //校检是否位管理员
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"不好意思，您暂无权限");
        }
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean removeUser = userService.removeById(deleteRequest.getId());
        return ResultUtil.success(removeUser);
    }
    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "不好意思，您暂无权限");
        }
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtil.success(true);
    }

    /**
     * 用户自己更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtil.success(true);
    }

    /**
     * 修改密码
     *
     * @param updatePasswordRequest
     * @param request
     * @return
     */
    @PostMapping("/update/password")
    public BaseResponse<Boolean> updateUserPassword(@RequestBody UserUpdatePasswordRequest updatePasswordRequest,
                                                    HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "不好意思，您暂无权限");
        }
        boolean updateUserPassword = userService.updateUserPassword(updatePasswordRequest, request);
        if (updateUserPassword) {
            return ResultUtil.success(true);
        } else {
            return ResultUtil.error(ErrorCode.INVALID_PASSWORD_ERROR);
        }
    }
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        //管理员校验
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || !user.getUserRole().equals(ADMIN_ROLE)) {
            return false;
        }
        return true;
    }
}
