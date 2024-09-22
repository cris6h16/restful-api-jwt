package org.cris6h16.In.Commands;

import org.cris6h16.Repositories.Page.Order;
import org.cris6h16.Repositories.Page.SortBy;

public class GetAllPublicProfilesCommand {
    private final int page;
    private final int pageSize;
    private final SortBy sortBy;
    private final Order sortDirection;

    public GetAllPublicProfilesCommand(int page, int pageSize, SortBy sortBy, Order sortDirection) {
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

    public SortBy getSortBy() {
        return sortBy;
    }

    public Order getSortDirection() {
        return sortDirection;
    }
}
