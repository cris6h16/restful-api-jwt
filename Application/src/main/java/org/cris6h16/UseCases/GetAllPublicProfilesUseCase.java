package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.GetAllPublicProfilesPort;
import org.cris6h16.In.Results.ResultPublicProfile;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.Utils.UserValidator;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GetAllPublicProfilesUseCase implements GetAllPublicProfilesPort {
    private final UserValidator userValidator;
    private final TransactionManager transactionManager;
    private final UserRepository userRepository;

    public GetAllPublicProfilesUseCase(UserValidator userValidator, TransactionManager transactionManager, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
    }


    @Override
    public List<ResultPublicProfile> getPublicProfilesPage(GetAllPublicProfilesCommand cmd) {
        cmdNotNull(cmd);

        AtomicReference<List<UserModel>> ref = new AtomicReference<>();
        transactionManager.readCommitted(() -> ref.set(findPage(cmd)));
        return ref.get().stream()
                .map(this::toResultPublicProfile)
                .toList();
    }

    private ResultPublicProfile toResultPublicProfile(UserModel userModel) {
        return null;
    }

    private List<UserModel> findPage(GetAllPublicProfilesCommand cmd) {
        return userRepository.findAllCustom(
                cmd.getPage(),
                cmd.getPageSize(),
                cmd.getSortBy(),
                cmd.getSortDirection()
        );
    }

    private void cmdNotNull(GetAllPublicProfilesCommand cmd) {
        if (cmd == null) {
            throw new UnexpectedException("Command cannot be null");
        }
    }
}
