package com.yupi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.common.ErrorCode;
import com.yupi.exception.BusinessException;
import com.yupi.exception.ThrowUtils;
import com.yupi.model.domain.User;
import com.yupi.Mapper.UserMapper;
import com.yupi.model.request.UserSearchRequest;
import com.yupi.model.request.UserUpdatePasswordRequest;
import com.yupi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yupi.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 1.用户在前端输入账户和密码、以及校验码(QdQ)
 * 2.校验用户的账户、密码、校验密码，是否符合要求
 * <p>
 * 1.账户的话不小于4位
 * 2.密码就不小于8位吧
 * 3.账户不能重复
 * 4.账户不包含特殊字符
 * 5.密码和校验密码相同
 * 3.对密码进行加密（密码千万不要直接以明文存储到数据库中）
 * 4.向数据库插入用户数据
 *
 * @author January
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-03-27 15:17:06
 */
@Service
@Slf4j//记录日志
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Override
    public long userRegister(String username,String userAccount, String userPassword, String checkPassword,String planetCode) {
        //校验
        //用apache里的方法StringUtils.isAnyBlank接收多个字符串，同时判断（是否为空，null...）
        if (StringUtils.isAnyBlank(username,userAccount, userPassword, checkPassword,planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空！");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于8!");
        }
        //----------------------------------------------------学号的长度为10
        if (planetCode.length()!=10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"学号固定任意10位数！");
        }
        //账户不能包含特殊字符
        String validPattern = "^[^`~!@#$%^&*()+=\\|{}':;',\\[\\].<>/?￥%…—*|【】\\\\《》；“”‘’，，。、？]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }
        //密码和校验密码要相同，不相同返回-1
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不相同");
        }
        // -------------------------------------------账户不能重复--要查询数据库--
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        // -------------------------------------------学号不能重复--要查询数据库--
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"学号重复");
        }
        //2.加密
        //Assertions.assertEquals(-1, result); 是JUnit测试框架中的一个断言方法，
        //用于验证两个值是否相等。如果这两个值不相等，那么测试就会失败，并抛出一个AssertionError
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //非空校验
        //用apache里的方法StringUtils.isAnyBlank接收多个字符串，同时判断（是否为空，null...）
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请您正确输入账号和密码！");
        }
        if (StringUtils.isAnyBlank(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请您正确输入账号和密码！");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"您的账号长度小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"您的密码小于8位哇");
        }
        //账户不能包含特殊字符
        String validPattern = "^[^`~!@#$%^&*()+=\\|{}':;',\\[\\].<>/?￥%…—*|【】\\\\《》；“”‘’，，。、？]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号禁止含有特殊字符");
        }
        //2.加密
        //Assertions.assertEquals(-1, result); 是JUnit测试框架中的一个断言方法，
        //用于验证两个值是否相等。如果这两个值不相等，那么测试就会失败，并抛出一个AssertionError
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
//        解决方案：确保在查询数据库时，你使用的密码是经过与存储时相同的SALT和MD5加密的。！！！
        queryWrapper.eq("userPassword", encryptPassword);
        //查询数据库要调用Mapper层的方法
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed,userAccount Cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请仔细检查账号密码是否正确！");
        }
        //3.用户脱敏
        User safetyUser = getSafetyUser(user);
        //4,记录用户的登录状态,继续写记录用户的登录状态，怎么记录用户的登录状态呢？
        //我们使用request,用getsession拿到session
        //用setAttribute往session里设置一些值（比如用户信息）
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserPassword(originUser.getUserPassword());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        throw new BusinessException(ErrorCode.SUCCESS,"已退出登录,欢迎下次拜访，祝您生活愉快！");
    }
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        return currentUser;
    }
    /**
     * 分页查询
     * @param searchRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserSearchRequest searchRequest) {
        if (searchRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String username = searchRequest.getUsername();
        String userAccount = searchRequest.getUserAccount();
        Integer gender = searchRequest.getGender();
        String phone = searchRequest.getPhone();
        String email = searchRequest.getEmail();
        Integer userStatus = searchRequest.getUserStatus();
        Integer userRole = searchRequest.getUserRole();
        String planetCode = searchRequest.getPlanetCode();
        Date updateTime = searchRequest.getUpdateTime();
        Date createTime = searchRequest.getCreateTime();

        // username
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        // userAccount
        if (StringUtils.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        // gender
        if (gender != null) {
            queryWrapper.eq("gender", gender);
        }
        // phone
        if (StringUtils.isNotBlank(phone)) {
            queryWrapper.like("phone", phone);
        }
        // email
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.like("email", email);
        }
        // userStatus
        if (userStatus != null) {
            queryWrapper.eq("userStatus", userStatus);
        }
        // userRole
        if (userRole != null) {
            queryWrapper.eq("userRole", userRole);
        }
        // planetCode
        if (StringUtils.isNotBlank(planetCode)) {
            queryWrapper.eq("planetCode", planetCode);
        }
        // updateTime
        if (updateTime != null) {
            queryWrapper.ge("updateTime", updateTime);
        }
        // createTime
        if (createTime != null) {
            queryWrapper.ge("createTime", createTime);
        }
        return queryWrapper;
    }



    /**
     * 修改密码
     *
     * @param updatePasswordRequest
     * @param request
     * @return
     */
    @Override
    public boolean updateUserPassword(UserUpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        if (updatePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = getLoginUser(request);
        Long userId = currentUser.getId();
        if (userId < 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "不存在该用户");
        }
        User user = new User();
        BeanUtils.copyProperties(updatePasswordRequest, user);
        user.setId(currentUser.getId());

        // 使用 MD5加盐值 加密新密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + updatePasswordRequest.getNewPassword()).getBytes());
        user.setUserPassword(encryptedPassword);
        if (encryptedPassword.equals(updatePasswordRequest.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改密码不能相同");
        }
        boolean result = updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}

