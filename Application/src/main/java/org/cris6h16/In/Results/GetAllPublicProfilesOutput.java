package org.cris6h16.In.Results;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;

import java.util.List;

public class GetAllPublicProfilesOutput {
    private final long totalElements;
    private final List<GetPublicProfileOutput> items;

    public GetAllPublicProfilesOutput(long totalElements, List<GetPublicProfileOutput> items) {
        this.totalElements = totalElements;
        this.items = items;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<GetPublicProfileOutput> getItems() {
        return items;
    }
}
