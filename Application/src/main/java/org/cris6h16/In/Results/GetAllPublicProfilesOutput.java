package org.cris6h16.In.Results;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;

import java.util.List;

public class GetAllPublicProfilesOutput {
    private final long totalElements;
    private final long totalPages;
    private final GetAllPublicProfilesCommand command;
    private final List<GetPublicProfileOutput> items;

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
