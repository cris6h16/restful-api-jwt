package org.cris6h16.In.Ports;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;

public interface GetAllPublicProfilesPort {
    GetAllPublicProfilesOutput handle(GetAllPublicProfilesCommand cmd);
}
