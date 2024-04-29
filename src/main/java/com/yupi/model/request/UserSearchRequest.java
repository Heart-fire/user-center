package com.yupi.model.request;


import com.yupi.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serializable;
import java.util.Date;

/**
 * 用户更新请求
 */
@EqualsAndHashCode(callSuper = true)//用于自动生成对象的 equals() 和 hashCode() 方法。
@Data
public class UserSearchRequest extends PageRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 性别 男 女
     */
    private Integer gender;


    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态  0-正常 1-注销 2-封号
     */
    private Integer userStatus;


    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户角色-0-普通用户-1-管理员
     */
    private Integer userRole;

    /**
     * 学号
     */
    private String planetCode;

    private static final long serialVersionUID = 1L;

}