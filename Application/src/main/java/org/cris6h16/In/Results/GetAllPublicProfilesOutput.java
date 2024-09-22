package org.cris6h16.In.Results;

import org.cris6h16.Repositories.Page.Page;

public class GetAllPublicProfilesOutput {
    private Page<GetPublicProfileOutput> content;

    public GetAllPublicProfilesOutput(Page<GetPublicProfileOutput> content) {
        this.content = content;
    }
}
