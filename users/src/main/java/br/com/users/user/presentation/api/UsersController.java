package br.com.users.user.presentation.api;

import br.com.users.user.application.usecase.CreateUserUseCase;
import br.com.users.user.application.usecase.DeleteUserUseCase;
import br.com.users.user.application.usecase.GetAllUsersUseCase;
import br.com.users.user.application.usecase.GetUserByCpfUseCase;
import br.com.users.user.application.usecase.GetUserByEmailUseCase;
import br.com.users.user.application.usecase.GetUserByIdUseCase;
import br.com.users.user.application.usecase.GetUsersByNameOrEmailUseCase;
import br.com.users.user.application.usecase.GetUsersByNameUseCase;
import br.com.users.user.application.usecase.UpdateUserUseCase;
import br.com.users.user.presentation.dto.PostUserInputDto;
import br.com.users.user.presentation.dto.PutUserInputDto;
import br.com.users.user.presentation.dto.UserFilter;
import br.com.users.user.presentation.dto.UserOutputDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController implements UsersApi {

  private final CreateUserUseCase createUserUseCase;
  private final GetAllUsersUseCase getAllUsersUseCase;
  private final GetUserByEmailUseCase getUserByEmailUseCase;
  private final GetUsersByNameUseCase getUsersByNameUseCase;
  private final GetUserByIdUseCase getUserByIdUseCase;
  private final GetUsersByNameOrEmailUseCase getUsersByNameOrEmailUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;
  private final GetUserByCpfUseCase getUserByCpfUseCase;

  public UsersController(
      CreateUserUseCase createUserUseCase,
      GetAllUsersUseCase getAllUsersUseCase,
      GetUserByEmailUseCase getUserByEmailUseCase,
      GetUsersByNameUseCase getUsersByNameUseCase,
      GetUserByIdUseCase getUserByIdUseCase,
      GetUsersByNameOrEmailUseCase getUsersByNameOrEmailUseCase,
      UpdateUserUseCase updateUserUseCase,
      DeleteUserUseCase deleteUserUseCase, GetUserByCpfUseCase getUserByCpfUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.getAllUsersUseCase = getAllUsersUseCase;
    this.getUserByEmailUseCase = getUserByEmailUseCase;
    this.getUsersByNameUseCase = getUsersByNameUseCase;
    this.getUserByIdUseCase = getUserByIdUseCase;
    this.getUsersByNameOrEmailUseCase = getUsersByNameOrEmailUseCase;
    this.updateUserUseCase = updateUserUseCase;
    this.deleteUserUseCase = deleteUserUseCase;
    this.getUserByCpfUseCase = getUserByCpfUseCase;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<UserOutputDto> getAllUsersPaginated(
      @PageableDefault(sort = {"name"}) Pageable pageable) {
    var usersPage = getAllUsersUseCase.execute(pageable);
    return UserOutputDto.toPage(usersPage);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserOutputDto postUser(@RequestBody @Valid PostUserInputDto postUserInputDto) {
    var user = PostUserInputDto.toUser(postUserInputDto);
    var userCreated = createUserUseCase.execute(user);
    return UserOutputDto.from(userCreated);
  }

  @GetMapping("/email/{email}")
  @ResponseStatus(HttpStatus.OK)
  public UserOutputDto getUserByEmail(@PathVariable @Email String email) {
    var userFound = getUserByEmailUseCase.execute(email);
    return UserOutputDto.from(userFound);
  }

  @GetMapping("/name/{name}")
  @ResponseStatus(HttpStatus.OK)
  public Page<UserOutputDto> getUsersByName(@PathVariable String name,
      @PageableDefault(sort = {"name"}) Pageable pageable) {
    var usersPage = getUsersByNameUseCase.execute(name, pageable);
    return UserOutputDto.toPage(usersPage);
  }

  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public UserOutputDto getUserById(@PathVariable String userId) {
    var user = getUserByIdUseCase.execute(userId);
    return UserOutputDto.from(user);
  }

  @GetMapping("/user-name-email")
  @ResponseStatus(HttpStatus.OK)
  public Page<UserOutputDto> getUsersByNameOrEmail(UserFilter userFilter,
      @PageableDefault(sort = {"name"}) Pageable pageable) {
    var usersPage = getUsersByNameOrEmailUseCase.execute(userFilter.name(), userFilter.email(),
        pageable);
    return !usersPage.getContent().isEmpty() ? UserOutputDto.toPage(usersPage) : Page.empty();
  }

  @PutMapping("/{userUuid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public UserOutputDto putUser(@PathVariable String userUuid,
      @RequestBody @Valid PutUserInputDto putUserInputDto) {
    var user = PutUserInputDto.toUser(putUserInputDto);
    var userUpdated = updateUserUseCase.execute(userUuid, user);
    return UserOutputDto.from(userUpdated);
  }

  @DeleteMapping("/{userUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable String userUuid) {
    deleteUserUseCase.execute(userUuid);
  }

  @GetMapping("/cpf/{cpf}")
  @ResponseStatus(HttpStatus.OK)
  public UserOutputDto getUserByCpf(@PathVariable String cpf) {
    var user = getUserByCpfUseCase.execute(cpf);
    return UserOutputDto.from(user);
  }
}
