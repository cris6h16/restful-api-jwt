package org.cris6h16.Repositories.Page;

import java.util.List;
public class PageResult<T> {

    private final int pageItems;
    private final int totalPages;
    private final boolean isFirstPage;
    private final boolean isLastPage;
    private final boolean hasNextPage;
    private final boolean hasPreviousPage;
    private final PageRequest pageRequest;
    private final List<T> items;

    public PageResult(int pageItems,
                      int totalPages,
                      boolean isFirstPage,
                      boolean isLastPage,
                      boolean hasNextPage,
                      boolean hasPreviousPage,
                      PageRequest pageRequest,
                      List<T> items) {
        if (pageItems < 0 || totalPages < 0) {
            throw new IllegalArgumentException("Page items and total pages must be non-negative.");
        }
        this.pageItems = pageItems;
        this.totalPages = totalPages;
        this.isFirstPage = isFirstPage;
        this.isLastPage = isLastPage;
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
        this.pageRequest = pageRequest;
        this.items = items == null ? List.of() : items; // Prevent null items list
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0, 0, true, true, false, false, null, List.of());
    }

    // Getters

    public int getPageItems() {
        return pageItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public List<T> getItems() {
        return items;
    }
}

