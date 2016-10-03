package com.marcobehler.saito.core.pagination;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Paginator {

    private boolean paginationInProgress = false;

    public void restartIfNecessary(List<Object> collection, Integer pageSize) {
        log.info("{} {}", collection, pageSize);

        if (collection.isEmpty()) {
            return;
        }

        if (paginationInProgress) {
            return;
        }

        if (collection.size() <= pageSize) {
            return;
        }

        int pages = collection.size() / pageSize;
        paginationInProgress = true;
        throw new PaginationException(pages, pageSize, collection);
    }
}
