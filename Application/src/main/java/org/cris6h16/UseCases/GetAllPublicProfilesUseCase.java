package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.GetAllPublicProfilesPort;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.UserRepository;

import java.util.List;

public class GetAllPublicProfilesUseCase implements GetAllPublicProfilesPort {

    private final UserRepository userRepository;

    public GetAllPublicProfilesUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetAllPublicProfilesOutput handle(GetAllPublicProfilesCommand cmd) {
        cmdNotNull(cmd);
        return toOutput(findPage(cmd), cmd);
    }

    private GetAllPublicProfilesOutput toOutput(Page<UserModel> page, GetAllPublicProfilesCommand cmd) {
        List<GetPublicProfileOutput> profiles = page.getItems().stream()
                .map(this::toPublicProfileOutput)
                .toList();

        return new GetAllPublicProfilesOutput(profiles, page, cmd);
    }

    private GetPublicProfileOutput toPublicProfileOutput(UserModel userModel) {
        return new GetPublicProfileOutput(userModel);
    }

    private Page<UserModel> findPage(GetAllPublicProfilesCommand cmd) {
        MyPageable req = new MyPageable(
                cmd.getPage(),
                cmd.getPageSize(),
                cmd.getSortBy(),
                cmd.getSortDirection()
        );
        return userRepository.findPage(req);
    }

    private void cmdNotNull(GetAllPublicProfilesCommand cmd) {
        if (cmd == null) {
            throw new UnexpectedException("Command cannot be null.");
        }
    }
}
