package org.cris6h16.In.Results;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;

import java.util.List;

public class GetAllPublicProfilesOutput {
    /**
     * total elements in the current page ( useful when is the last page )
     */
    private final long pageElements;
    private final List<GetPublicProfileOutput> items;
    private final GetAllPublicProfilesCommand input;
    private final int totalPages;

    public GetAllPublicProfilesOutput(long pageElements, List<GetPublicProfileOutput> items, GetAllPublicProfilesCommand input, int totalPages) {
        this.pageElements = pageElements;
        this.items = items;
        this.input = input;
        this.totalPages = totalPages;
    }

    public long getPageElements() {
        return pageElements;
    }

    public List<GetPublicProfileOutput> getItems() {
        return items;
    }

    public GetAllPublicProfilesCommand getInput() {
        return input;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
