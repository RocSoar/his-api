package com.roc.his.api.common;

import lombok.Data;

import java.util.List;

@Data
public class PageUtils {

    public PageUtils(List list, long totalCount, int pageIndex, int pageSize) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * 总记录数
     */
    private long totalCount;

    /**
     * 每页显示几条记录
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 当前页数
     */
    private int pageIndex;

    /**
     * 分页数据
     */
    private List list;
}