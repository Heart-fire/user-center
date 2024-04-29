-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                                                                                       null comment '用户昵称',
    userAccount  varchar(256)                                                                                       null comment '账号',
    avatarUrl    varchar(1024) default 'https://wbe-tilas.oss-cn-hangzhou.aliyuncs.com/%E8%94%A1%E5%BE%90%E5%9D%A4' null comment '用户头像',
    gender       tinyint       default 1                                                                            null comment '性别 1-男 0-女',
    userPassword varchar(512)                                                                                       not null comment '密码',
    email        varchar(512)                                                                                       null comment '邮箱',
    userstatus   int           default 0                                                                            not null comment '状态 0-正常 1-注销 2-封号',
    phone        varchar(128)                                                                                       null comment '电话',
    createTime   datetime      default CURRENT_TIMESTAMP                                                            null comment '创建时间',
    updateTime   datetime      default CURRENT_TIMESTAMP                                                            null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint       default 0                                                                            not null comment '是否删除',
    userRole     int           default 0                                                                            not null comment '用户角色-0-普通用户-1-管理员',
    planetCode   varchar(512)                                                                                       null comment '学号'
)
    comment '用户表';