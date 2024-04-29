package com.yupi.service;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.model.domain.User;
import com.yupi.model.request.UserSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 * 自动生成器的使用，MybatisX
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("dogYupi");
        user.setUserAccount("1234");
        user.setAvatarUrl("1.jpg");
        user.setGender(0);
        user.setUserPassword("xxxx");
        user.setEmail("123");
        user.setPhone("456");

        boolean result = userService.save(user);
        //  没有指定ID，测试是否会输出ID
        System.out.println(user.getId());
        //判断值是否相等，《断言》
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        //测试-密码为空
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        String userName ="123";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode,userName);
        Assertions.assertEquals(-1, result);

        //测试-账户名称小于四位
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode,userName);
        Assertions.assertEquals(-1, result);

        //密码小于八位
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode,userName);
        Assertions.assertEquals(-1, result);

        //账户不能包含特殊字符
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode,userName);
        Assertions.assertEquals(-1, result);

        //密码检验大于8位
        userPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode,userName);
        Assertions.assertEquals(-1, result);

        //判断账户不能重复，数据库已有dogyupi
        userAccount = "dogyupi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode,userName);
        Assertions.assertEquals(-1, result);


    }
    //最后注册用户yupi
    @Test
    void a(){
        String userAccount;
        String userPassword;
        String checkPassword;
        String planetCode;
        String userName;
        userName="aaa";
        userAccount = "yupipi2";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode="1234567891";

        long result = userService.userRegister(userName,userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertTrue(result > 0);
    }

    }