package org.cris6h16.In.Ports;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.ResultPublicProfile;

import java.util.List;

public interface GetAllPublicProfilesPort {
    List<ResultPublicProfile> getPublicProfilesPage(GetAllPublicProfilesCommand cmd);
}
