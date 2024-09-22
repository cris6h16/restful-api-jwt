package org.cris6h16.In.Commands;

public class GetAllPublicProfilesCommand {
    private final int page;
    private final int pageSize;
    private final String sortBy;
    private final String sortDirection;

    public GetAllPublicProfilesCommand(int page, int pageSize, String sortBy, String sortDirection) {
        this.page = page;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
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

    public String getSortDirection() {
        return sortDirection;
    }
}
