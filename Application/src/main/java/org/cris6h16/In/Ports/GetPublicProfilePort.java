package org.cris6h16.In.Ports;

import org.cris6h16.Models.UserModel;

public interface GetPublicProfilePort {
    UserModel handle(Long id); // todo: follow clean architecture: use presenters, etc
}
