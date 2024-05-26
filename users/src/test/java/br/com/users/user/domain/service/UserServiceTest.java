package br.com.users.user.domain.service;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_UUID;
import static br.com.users.shared.testData.user.UserTestData.createNewUser;
import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.infrastructure.repository.UserRepository;
import br.com.users.user.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 1;

  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserService userService;

  @Test
  void shouldSaveUserWhenAllUserAttributesAreCorrect() {
    var user = createNewUser();
    when(userRepository.save(user)).then(returnsFirstArg());

    var userSaved = userService.save(user);

    assertThat(userSaved).isNotNull();
    assertThat(userSaved.getName()).isEqualTo(user.getName());
    assertThat(userSaved.getEmail()).isEqualTo(user.getEmail());
    verify(userRepository).save(user);
  }

  @Test
  void shouldGetAllUsersPaginatedWhenUsersExits() {
    var user = createUser();
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);

    when(userRepository.findAll(pageable)).thenReturn(page);
    var usersFound = userService.getAllUsersPaginated(pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldReturnEmptyPageWhenDoesNotExistAnyUserSaved() {
    var users = new ArrayList<User>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.findAll(pageable)).thenReturn(page);

    var usersFound = userService.getAllUsersPaginated(pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindUserByEmailWhenUserExists() {
    var user = createUser();
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    var userFoundOptional = userService.findByEmail(user.getEmail());
    var userFound = userFoundOptional.orElse(null);

    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(user.getName());
    assertThat(userFound.getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  void shouldFindUserByCpfWhenUserExists() {
    var user = createUser();
    when(userRepository.findByDocNumber(user.getDocNumber())).thenReturn(Optional.of(user));

    var userFoundOptional = userService.findByCpf(user.getDocNumber());
    var userFound = userFoundOptional.orElse(null);

    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(user.getName());
    assertThat(userFound.getDocNumber()).isEqualTo(user.getDocNumber());
  }

  @Test
  void shouldReturnEmptyWhenFindUserByEmailDoesNotExist() {
    var user = createNewUser();
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

    var userFoundOptional = userService.findByEmail(user.getEmail());
    var userFound = userFoundOptional.orElse(null);

    assertThat(userFound).isNull();
  }

  @Test
  void shouldFindUserByNameWhenUserExists() {
    var user = createUser();
    var name = user.getName();
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.findByNameLikeIgnoreCase(name, pageable)).thenReturn(page);

    var usersPageFound = userService.findByNamePageable(name, pageable);

    assertThat(usersPageFound).isNotNull();
    assertThat(usersPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersPageFound.getTotalPages()).isEqualTo(size);
    assertThat(usersPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldReturnEmptyPageWhenFindByNameAndDoesNotExistAnyUserSaved() {
    var name = "Smith";
    var users = new ArrayList<User>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.findByNameLikeIgnoreCase(name, pageable)).thenReturn(page);

    var usersFound = userService.findByNamePageable(name, pageable);

    assertThat(usersFound).isNotNull();
    assertThat(usersFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersFound.getTotalPages()).isEqualTo(size);
    assertThat(usersFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindUserByNameAndEmailWhenUserExists() {
    var user = createUser();
    var name = user.getName();
    var email = user.getEmail();
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable)).thenReturn(
        page);

    var usersPageFound = userService.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable);

    assertThat(usersPageFound).isNotNull();
    assertThat(usersPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersPageFound.getTotalPages()).isEqualTo(size);
    assertThat(usersPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindUserByNameOnlyWhenUserExists() {
    var user = createUser();
    var name = user.getName();
    var email = "";
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable)).thenReturn(
        page);

    var usersPageFound = userService.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable);

    assertThat(usersPageFound).isNotNull();
    assertThat(usersPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersPageFound.getTotalPages()).isEqualTo(size);
    assertThat(usersPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindUserByEmailOnlyWhenUserExists() {
    var user = createUser();
    var name = "";
    var email = user.getEmail();
    var users = List.of(user);
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = users.size();
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable)).thenReturn(
        page);

    var usersPageFound = userService.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable);

    assertThat(usersPageFound).isNotNull();
    assertThat(usersPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersPageFound.getTotalPages()).isEqualTo(size);
    assertThat(usersPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldReturnEmptyPageWhenFindByNameOrEmailAndDoesNotExistAnyUserSaved() {
    var name = "Smith";
    var email = "smith@matrix.com";
    var users = new ArrayList<User>();
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    var size = 0;
    var page = new PageImpl<>(users, pageable, size);
    when(userRepository.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable)).thenReturn(
        page);

    var usersPageFound = userService.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable);

    assertThat(usersPageFound).isNotNull();
    assertThat(usersPageFound.getSize()).isEqualTo(PAGE_SIZE);
    assertThat(usersPageFound.getTotalPages()).isEqualTo(size);
    assertThat(usersPageFound.getTotalElements()).isEqualTo(size);
  }

  @Test
  void shouldFindUserByIdWhenUserExists() {
    var user = createUser();
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    var userFoundOptional = userService.findById(user.getId());
    var userFound = userFoundOptional.orElse(null);

    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(user.getName());
    assertThat(userFound.getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  void shouldReturnEmptyWhenFindUserByIdDoesNotExist() {
    var uuid = UUID.randomUUID();
    when(userRepository.findById(uuid)).thenReturn(Optional.empty());

    var userFound = userService.findById(uuid);

    assertThat(userFound.isPresent()).isEqualTo(Boolean.FALSE);
  }

  @Test
  void shouldFindUserByEmailRequiredWhenUserExists() {
    var user = createUser();
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    var userFound = userService.findByEmailRequired(user.getEmail());

    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(user.getName());
    assertThat(userFound.getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  void shouldFindUserByIdRequiredWhenUserExists() {
    var user = createUser();
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    var userFound = userService.findUserByIdRequired(user.getId());

    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(user.getName());
    assertThat(userFound.getDocNumber()).isEqualTo(user.getDocNumber());
  }

  @Test
  void shouldReturnEmptyWhenFindUserByEmailRequiredDoesNotExist() {
    when(userRepository.findByEmail(DEFAULT_USER_EMAIL)).thenReturn(Optional.empty());

    assertThrows(NoResultException.class,
        () -> userService.findByEmailRequired(DEFAULT_USER_EMAIL));
  }

  @Test
  void shouldReturnEmptyWhenFindUserByIdRequiredDoesNotExist() {
    when(userRepository.findById(DEFAULT_USER_UUID)).thenReturn(Optional.empty());

    assertThrows(NoResultException.class,
        () -> userService.findUserByIdRequired(DEFAULT_USER_UUID));
  }

}
