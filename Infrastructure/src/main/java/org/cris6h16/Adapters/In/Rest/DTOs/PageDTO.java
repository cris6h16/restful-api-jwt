package org.cris6h16.Adapters.In.Rest.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;

import java.util.List;

public class PageDTO<T> {
    /**
     * total elements in the current page ( useful when is the last page )
     */
    @JsonAlias(value = "page-elements")
    private final long pageElements;
    private final List<T> items;
    private List<GetPublicProfileOutput> profiles;
    @JsonAlias(value = "page-request")
    private final GetAllPublicProfilesCommand input;
    @JsonAlias(value = "total-pages")
    private final int totalPages;

    public PageDTO(long pageElements, List<T> items, GetAllPublicProfilesCommand input, int totalPages) {
        this.pageElements = pageElements;
        this.items = items;
        this.input = input;
        this.totalPages = totalPages;
    }

    public PageDTO(GetAllPublicProfilesOutput<T> output) {
        this.pageElements = output.getPageElements();
        this.totalPages = output.getTotalPages();
        this.items = output.getItems();
        this.profiles = output.getProfiles();
        this.input = output.getInput();
    }
}
