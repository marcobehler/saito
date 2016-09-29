package com.marcobehler.saito.core.pagination;

import com.google.common.collect.Lists;
import javafx.scene.control.Pagination;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

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


    public Page toPage(int page) {
     return new Page(getPartitions().get(page - 1), page);
    }



    @Getter
    public static class Page {
        private final List<Object> data;

        private final int pageNumber;

        public Page(List<Object> data, int pageNumber) {
            this.data = data;
            this.pageNumber = pageNumber;
        }
    }

}
