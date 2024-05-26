package br.com.users.user.application.usecase;

import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetAllUsersUseCase {

  private final UserService userService;

  public GetAllUsersUseCase(UserService userService) {
    this.userService = userService;
  }

  public Page<User> execute(Pageable pageable){
    return userService.getAllUsersPaginated(pageable);
  }
}
