package org.cris6h16.In.Results;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.PageResult;

import java.util.List;

public class GetAllPublicProfilesOutput {
    private List<GetPublicProfileOutput> profiles;
    private final int pageItems;
    private final int totalPages;
    private final boolean isFirstPage;
    private final boolean isLastPage;
    private final boolean hasNextPage;
    private final boolean hasPreviousPage;
    private final GetAllPublicProfilesCommand input;

    public GetAllPublicProfilesOutput(List<GetPublicProfileOutput> profiles, PageResult<UserModel> pageInfo, GetAllPublicProfilesCommand input) {
        this.profiles = profiles;
        this.pageItems = pageInfo.getPageItems();
        this.totalPages = pageInfo.getTotalPages();
        this.isFirstPage = pageInfo.isFirstPage();
        this.isLastPage = pageInfo.isLastPage();
        this.hasNextPage = pageInfo.isHasNextPage();
        this.hasPreviousPage = pageInfo.isHasPreviousPage();
        this.input = input;
    }

    public List<GetPublicProfileOutput> getProfiles() {
        return profiles;
    }

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

    public GetAllPublicProfilesCommand getInput() {
        return input;
    }
}
