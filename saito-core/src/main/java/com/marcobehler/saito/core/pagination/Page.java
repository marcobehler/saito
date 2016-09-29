package com.marcobehler.saito.core.pagination;

import lombok.Getter;

import java.util.List;

/**
 * Created by BEHLEMA on 29.09.2016.
 */
@Getter
public class Page {
    private final List<Object> data;

    private final int pageNumber;

    public Page(List<Object> data, int pageNumber) {
        this.data = data;
        this.pageNumber = pageNumber;
    }
}
