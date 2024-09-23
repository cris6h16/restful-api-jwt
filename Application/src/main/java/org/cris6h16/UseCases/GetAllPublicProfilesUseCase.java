package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.GetAllPublicProfilesPort;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.PageResult;
import org.cris6h16.Repositories.Page.PageRequest;
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

    private GetAllPublicProfilesOutput toOutput(PageResult<UserModel> page, GetAllPublicProfilesCommand cmd) {
        List<GetPublicProfileOutput> profiles = page.getItems().stream()
                .map(this::toPublicProfileOutput)
                .toList();

        return new GetAllPublicProfilesOutput(profiles, page, cmd);
    }

    private GetPublicProfileOutput toPublicProfileOutput(UserModel userModel) {
        return new GetPublicProfileOutput(userModel);
    }

    private PageResult<UserModel> findPage(GetAllPublicProfilesCommand cmd) {
        PageRequest req = new PageRequest(cmd.getPage(), cmd.getPageSize(), cmd.getSortBy(), cmd.getSortDirection());
        return userRepository.findPageCustom(req);
    }

    private void cmdNotNull(GetAllPublicProfilesCommand cmd) {
        if (cmd == null) {
            throw new UnexpectedException("Command cannot be null.");
        }
    }
}
