package com.yupi.common;

import lombok.Data;

@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 5;

    /**
     * 排序字段
     */
    private String sortField;

}
