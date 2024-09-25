package org.cris6h16.Repositories.Page;

public class PageRequest {
    private int page;
    private int pageSize;
    private String sortBy;
    private boolean isAscending ;

    public PageRequest(int page, int pageSize, String sortBy, boolean isAscending) {
        this.page = page;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.isAscending = isAscending;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
