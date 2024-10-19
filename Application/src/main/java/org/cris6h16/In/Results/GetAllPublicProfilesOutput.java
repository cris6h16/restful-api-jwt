package org.cris6h16.In.Results;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;

import java.util.List;

public class GetAllPublicProfilesOutput {
    private long totalElements;
    private long totalPages;
    private GetAllPublicProfilesCommand command;
    private List<GetPublicProfileOutput> items;

    public GetAllPublicProfilesOutput() {
    }

    public GetAllPublicProfilesOutput(long totalElements, long totalPages, GetAllPublicProfilesCommand command, List<GetPublicProfileOutput> items) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.command = command;
        this.items = items;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public GetAllPublicProfilesCommand getCommand() {
        return command;
    }

    public List<GetPublicProfileOutput> getItems() {
        return items;
    }
}
