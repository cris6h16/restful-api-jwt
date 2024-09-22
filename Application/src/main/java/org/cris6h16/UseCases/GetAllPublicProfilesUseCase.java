package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.GetAllPublicProfilesPort;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.Page;
import org.cris6h16.Repositories.Page.PageRequest;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Utils.UserValidator;

import java.util.List;

public class GetAllPublicProfilesUseCase implements GetAllPublicProfilesPort {
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public GetAllPublicProfilesUseCase(UserValidator userValidator, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }


    @Override
    public GetAllPublicProfilesOutput getPublicProfilesPage(GetAllPublicProfilesCommand cmd) {
        cmdNotNull(cmd);
        return toGetAllPublicProfilesOutput(findPage(cmd));
    }

    private GetAllPublicProfilesOutput toGetAllPublicProfilesOutput(Page<UserModel> page) {
        List<GetPublicProfileOutput> ppo = page.getContent().stream()
                .map(u -> new GetPublicProfileOutput.Builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .roles(u.getRoles())
                        .active(u.getActive())
                        .emailVerified(u.getEmailVerified())
                        .lastModified(u.getLastModified())
                        .build())
                .toList();

        return new GetAllPublicProfilesOutput(new Page<>(ppo, page.getRequest()));
    }

    private Page<UserModel> findPage(GetAllPublicProfilesCommand cmd) {
        PageRequest req = new PageRequest(cmd.getPage(), cmd.getPageSize(), cmd.getSortBy(), cmd.getSortDirection());
        return userRepository.findPageCustom(req);
    }

    private void cmdNotNull(GetAllPublicProfilesCommand cmd) {
        if (cmd == null) {
            throw new UnexpectedException("Command cannot be null");
        }
    }
}
