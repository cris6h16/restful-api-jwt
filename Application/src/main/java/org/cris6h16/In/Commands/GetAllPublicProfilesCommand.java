package org.cris6h16.In.Commands;


import org.cris6h16.Repositories.Page.MySortOrder;

import java.util.List;
import java.util.Objects;

public class GetAllPublicProfilesCommand {
    private final int pageNumber;
    private final int pageSize;
    private final List<MySortOrder> mySortOrders;

    public GetAllPublicProfilesCommand(int pageNumber, int pageSize, List<MySortOrder> mySortOrders) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.mySortOrders = mySortOrders;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<MySortOrder> getMySortOrders() {
        return mySortOrders;
    }

    // used in tests
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GetAllPublicProfilesCommand that = (GetAllPublicProfilesCommand) obj;

        if (pageNumber != that.pageNumber) return false;
        if (pageSize != that.pageSize) return false;
        return Objects.equals(mySortOrders, that.mySortOrders);
    }
}
