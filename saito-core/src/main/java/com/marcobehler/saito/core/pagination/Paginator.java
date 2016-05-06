package com.marcobehler.saito.core.pagination;

import java.util.Collection;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class Paginator {

    public void restartIfNecessary(Collection<Object> collection, Integer pageSize) {
        int pages = collection.size() / pageSize;
        throw new PaginationException(pages, pageSize);
    }
}
