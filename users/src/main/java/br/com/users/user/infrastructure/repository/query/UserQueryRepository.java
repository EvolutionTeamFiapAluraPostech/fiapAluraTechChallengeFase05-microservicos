package br.com.users.user.infrastructure.repository.query;

import br.com.users.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UserQueryRepository {

  @Query(value = """
          SELECT u
          FROM User u
          LEFT JOIN FETCH u.authorities a
          WHERE (:name IS NULL OR UPPER(TRIM(u.name)) LIKE CONCAT('%', UPPER(TRIM(:name)), '%'))
            AND (:email IS NULL OR UPPER(TRIM(u.email)) LIKE CONCAT('%', UPPER(TRIM(:email)), '%'))
      """)
  Page<User> queryUserByNameLikeIgnoreCaseOrEmail(String name, String email, Pageable pageable);
}
