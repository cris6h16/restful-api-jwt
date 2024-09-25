package org.cris6h16.In.Commands;


public class GetAllPublicProfilesCommand {
    private final int page;
    private final int pageSize;
    private final String sortBy;
    private final boolean isAscending;

    public GetAllPublicProfilesCommand(int page, int pageSize, String sortBy, boolean isAscending) {
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

    public boolean getSortDirection() {
        return isAscending;
    }
}
