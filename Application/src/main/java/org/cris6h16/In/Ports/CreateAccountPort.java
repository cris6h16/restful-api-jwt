package org.cris6h16.In.Ports;

import org.cris6h16.In.Commands.CreateAccountCommand;

public interface CreateAccountPort {
    Long createAccount(CreateAccountCommand command);
}
