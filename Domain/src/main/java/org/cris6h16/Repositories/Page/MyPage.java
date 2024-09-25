package org.cris6h16.Repositories.Page;

import java.util.List;
public class MyPage<T>{

    private final int totalPages;
    /**
     * total elements in the current page ( useful when is the last page )
     */
    private final long pageElements;
    private final MyPageable pageRequest;
    private final List<T> items;

    public MyPage(int totalPages, long pageElements, MyPageable pageRequest, List<T> items) {
        this.totalPages = totalPages;
        this.pageElements = pageElements;
        this.pageRequest = pageRequest;
        this.items = items;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getPageElements() {
        return pageElements;
    }

    public MyPageable getPageRequest() {
        return pageRequest;
    }

    public List<T> getItems() {
        return items;
    }
}

