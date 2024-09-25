package org.cris6h16.Repositories.Page;

import java.util.List;

public class MyPageable {
    private final int pageNumber;
    private final int pageSize;
    private final List<MySortOrder> mySortOrders;

    public MyPageable(int pageNumber, int pageSize, List<MySortOrder> mySortOrders) {
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

    public List<MySortOrder> getSortOrders() {
        return mySortOrders;
    }
}
