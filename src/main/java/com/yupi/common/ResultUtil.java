package com.yupi.common;

/**
 * 返回工具类
 */
public class ResultUtil {
    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse<>(code,null,message,description);
    }

    /**
     * 失败
     * @param errorCode
     * @param messsage
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode,String messsage,String description){
        return new BaseResponse<>(errorCode.getCode(),null,messsage,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse(errorCode.getCode(),errorCode.getMessage(),description);
    }
}