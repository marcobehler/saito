package com.marcobehler.saito.core.pagination;

import java.util.Collection;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class Paginator {

    private boolean paginationInProgress = false;

    public void restartIfNecessary(Collection<Object> collection, Integer pageSize) {
        if (!paginationInProgress) {
            int pages = collection.size() / pageSize;
            paginationInProgress = true;
            throw new PaginationException(pages, pageSize);
        }
    }
}
