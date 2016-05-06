package com.marcobehler.saito.core.pagination;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
@Slf4j
public class Paginator {

    private boolean paginationInProgress = false;

    public void restartIfNecessary(Collection<Object> collection, Integer pageSize) {
        log.info("{} {}", collection, pageSize);
        if (!paginationInProgress) {
            int pages = collection.size() / pageSize;
            paginationInProgress = true;
            throw new PaginationException(pages, pageSize);
        }
    }
}
