package org.cris6h16.Repositories.Page;

import java.util.List;

public class Page<T> {
    private List<T> content;
    private PageRequest request;

    public Page(List<T> content, PageRequest request) {
        this.content = content;
        this.request = request;
    }

    public List<T> getContent() {
        return content;
    }

    public PageRequest getRequest() {
        return request;
    }
}
