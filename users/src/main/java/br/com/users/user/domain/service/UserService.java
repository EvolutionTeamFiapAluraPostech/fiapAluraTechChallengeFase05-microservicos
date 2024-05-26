package br.com.users.user.domain.service;

import static br.com.users.user.domain.messages.UserMessages.USER_EMAIL_NOT_FOUND;
import static br.com.users.user.domain.messages.UserMessages.USER_ID_NOT_FOUND;

import br.com.users.user.domain.exception.NoResultException;
import br.com.users.user.infrastructure.repository.UserRepository;
import br.com.users.user.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public Page<User> getAllUsersPaginated(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public User findByEmailRequired(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(
            () -> new NoResultException(new FieldError(this.getClass().getSimpleName(), "email",
                USER_EMAIL_NOT_FOUND.formatted(email))));
  }

  public Optional<User> findByCpf(String cpf) {
    return userRepository.findByDocNumber(cpf);
  }

  public Page<User> findByNamePageable(String name, Pageable pageable) {
    return userRepository.findByNameLikeIgnoreCase(name, pageable);
  }

  public Page<User> queryUserByNameLikeIgnoreCaseOrEmail(String name, String email,
      Pageable pageable) {
    return userRepository.queryUserByNameLikeIgnoreCaseOrEmail(name, email, pageable);
  }

  public Optional<User> findById(UUID uuid) {
    return userRepository.findById(uuid);
  }

  public User findUserByIdRequired(UUID userUuid) {
    return userRepository.findById(userUuid)
        .orElseThrow(
            () -> new NoResultException(new FieldError(this.getClass().getSimpleName(), "cpf",
                USER_ID_NOT_FOUND.formatted(userUuid))));
  }

}
