package org.cris6h16.In.Ports;

import org.cris6h16.In.Results.LoginOutput;

public interface LoginPort {
    LoginOutput handle(String email, String password);
}
