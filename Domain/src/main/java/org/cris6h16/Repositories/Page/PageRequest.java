package org.cris6h16.Repositories.Page;

public class PageRequest {
    private int page;
    private int pageSize;
    private SortBy sortBy;
    private Order direction ;

    public PageRequest(int page, int pageSize, SortBy sortBy, Order direction) {
        this.page = page;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public Order getDirection() {
        return direction;
    }
}
