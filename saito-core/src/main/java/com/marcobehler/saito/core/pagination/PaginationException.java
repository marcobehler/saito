package com.marcobehler.saito.core.pagination;

import lombok.Getter;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Getter
public class PaginationException extends RuntimeException{

    private final int pages;

    private final int pageSize;


    public PaginationException(int pages, int pageSize) {
        this.pages = pages;
        this.pageSize = pageSize;
    }
}
