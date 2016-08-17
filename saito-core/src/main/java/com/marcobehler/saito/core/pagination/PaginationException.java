package com.marcobehler.saito.core.pagination;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Getter
public class PaginationException extends RuntimeException{

    private final int pages;

    private final int pageSize;

    private final List<Object> data;

    public PaginationException(int pages, int pageSize, List<Object> data) {
        this.pages = pages;
        this.pageSize = pageSize;
        this.data = data;
    }


    public List<List<Object>> getPartitions() {
        return Lists.partition(data, pageSize);
    }
}
