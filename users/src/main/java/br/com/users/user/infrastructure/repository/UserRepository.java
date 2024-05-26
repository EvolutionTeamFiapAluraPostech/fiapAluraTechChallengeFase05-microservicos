package br.com.users.user.infrastructure.repository;

import br.com.users.user.infrastructure.repository.query.UserQueryRepository;
import br.com.users.user.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>, UserQueryRepository {

  Optional<User> findByEmail(String email);

  Optional<User> findByDocNumber(String docNumber);

  Page<User> findByNameLikeIgnoreCase(String name, Pageable pageable);
}
