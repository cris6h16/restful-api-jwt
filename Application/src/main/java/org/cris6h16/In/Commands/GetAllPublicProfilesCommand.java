package org.cris6h16.In.Commands;


import org.cris6h16.Repositories.Page.MySortOrder;

import java.util.List;

public class GetAllPublicProfilesCommand {
    private final int pageNumber;
    private final int pageSize;
    private final List<MySortOrder> mySortOrders;

    public GetAllPublicProfilesCommand(int pageNumber, int pageSize, List<MySortOrder> mySortOrders) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.mySortOrders = mySortOrders;
    }
}
