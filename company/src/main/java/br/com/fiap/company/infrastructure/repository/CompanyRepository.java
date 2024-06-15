package br.com.fiap.company.infrastructure.repository;

import br.com.fiap.company.domain.entity.Company;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, UUID>, CompanyQueryRepository {

  Optional<Company> findByDocNumber(String docNumber);
}
