package br.com.fiap.company.presentation.api;

import static br.com.fiap.company.shared.testdata.CompanyTestData.createNewCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.shared.annotation.DatabaseTest;
import br.com.fiap.company.shared.annotation.IntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class DeleteCompanyApiTest {

  private static final String URL_COMPANIES = "/companies/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  DeleteCompanyApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Company createAndPersistNewCompany() {
    var company = createNewCompany();
    return entityManager.merge(company);
  }

  private Company getCompanyFoundById(String id) {
    return entityManager.find(Company.class, UUID.fromString(id));
  }

  @Test
  void shouldDeleteCompany() throws Exception {
    var company = createAndPersistNewCompany();

    var request = delete(URL_COMPANIES, company.getId());
    mockMvc.perform(request)
        .andExpect(status().isNoContent());

    var companyFound = getCompanyFoundById(company.getId().toString());
    assertThat(companyFound).isNotNull();
    assertThat(companyFound.getId()).isNotNull().isEqualTo(company.getId());
    assertThat(companyFound.getDeleted()).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"1Ab"})
  void shouldReturnBadRequestWhenCompanyIdIsInvalid(String companyId) throws Exception {
    var request = delete(URL_COMPANIES, companyId);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnNotFoundWhenCompanyIdWasNotFound() throws Exception {
    var request = delete(URL_COMPANIES, UUID.randomUUID());
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }
}
