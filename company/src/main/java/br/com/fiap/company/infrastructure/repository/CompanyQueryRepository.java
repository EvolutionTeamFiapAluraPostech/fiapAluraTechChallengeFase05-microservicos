package br.com.fiap.company.infrastructure.repository;

import br.com.fiap.company.domain.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CompanyQueryRepository {

  @Query(value = """
          SELECT c
          FROM Company c
          WHERE (:name IS NULL OR UPPER(TRIM(c.name)) LIKE CONCAT('%', UPPER(TRIM(:name)), '%'))
            AND (:email IS NULL OR UPPER(TRIM(c.email)) LIKE CONCAT('%', UPPER(TRIM(:email)), '%'))
      """)
  Page<Company> queryCompaniesByNameLikeIgnoreCaseOrEmail(String name, String email, Pageable pageable);
}
