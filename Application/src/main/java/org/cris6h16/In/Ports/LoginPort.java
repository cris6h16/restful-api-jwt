package org.cris6h16.In.Ports;

import org.cris6h16.In.Results.ResultLogin;

public interface LoginPort {
    ResultLogin handle(String email, String password);
}
