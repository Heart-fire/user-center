package com.yupi.common;

public enum ErrorCode {

    SUCCESS(0,"OK",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"暂无权限",""),

    INVALID_PASSWORD_ERROR(40102, "无效密码", ""),
    OPERATION_ERROR(50001, "操作失败", "操作失败"),
    SYSTEM_ERROR(50000,"系统内部异常","");


    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述(详情)
     */
    private final String descripton;

    ErrorCode(int code, String message, String descripton) {
        this.code = code;
        this.message = message;
        this.descripton = descripton;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescripton() {
        return descripton;
    }
}

