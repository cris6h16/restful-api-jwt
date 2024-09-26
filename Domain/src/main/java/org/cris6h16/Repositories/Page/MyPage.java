package org.cris6h16.Repositories.Page;

import java.util.List;
public class MyPage<T>{

    private final int totalPages;
    private final long totalElementsAll;
    private final MyPageable pageRequest;
    private final List<T> items;

    public MyPage(int totalPages, long totalElementsAll, MyPageable pageRequest, List<T> items) {
        this.totalPages = totalPages;
        this.totalElementsAll = totalElementsAll;
        this.pageRequest = pageRequest;
        this.items = items;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElementsAll() {
        return totalElementsAll;
    }

    public MyPageable getPageRequest() {
        return pageRequest;
    }

    public List<T> getItems() {
        return items;
    }
}

