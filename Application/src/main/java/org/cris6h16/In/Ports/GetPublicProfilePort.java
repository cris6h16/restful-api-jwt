package org.cris6h16.In.Ports;

import org.cris6h16.In.Results.GetPublicProfileOutput;

public interface GetPublicProfilePort {
    GetPublicProfileOutput handle(Long id);
}
