package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.GetAllPublicProfilesPort;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.UserRepository;

public class GetAllPublicProfilesUseCase implements GetAllPublicProfilesPort {

    private final UserRepository userRepository;

    public GetAllPublicProfilesUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetAllPublicProfilesOutput handle(GetAllPublicProfilesCommand cmd) {
        cmdNotNull(cmd);
        return toOutput(findPage(cmd));
    }

    private GetAllPublicProfilesOutput toOutput(MyPage<UserModel> page) {
        long totalElements = page.getTotalElementsAll();
        long totalPages = page.getTotalPages();
        var command = new GetAllPublicProfilesCommand(
                page.getPageRequest().getPageNumber(),
                page.getPageRequest().getPageSize(),
                page.getPageRequest().getSortOrders());
        var items = page.getItems().stream()
                .map(GetPublicProfileOutput::new)
                .toList();
        return new GetAllPublicProfilesOutput(totalElements, totalPages, command, items);
    }

    private MyPage<UserModel> findPage(GetAllPublicProfilesCommand cmd) {
        MyPageable req = new MyPageable(
                cmd.getPageNumber(),
                cmd.getPageSize(),
                cmd.getMySortOrders()
        );
        return userRepository.findPage(req);
    }

    private void cmdNotNull(GetAllPublicProfilesCommand cmd) {
        if (cmd == null) {
            throw new UnexpectedException("Command cannot be null.");
        }
    }
}
