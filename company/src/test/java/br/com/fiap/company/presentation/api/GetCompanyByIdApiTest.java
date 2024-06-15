package br.com.fiap.company.presentation.api;

import static br.com.fiap.company.shared.testdata.CompanyTestData.createNewCompany;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class GetCompanyByIdApiTest {

  private static final String URL_COMPANIES = "/companies/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetCompanyByIdApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Company createAndPersistNewCompany() {
    var company = createNewCompany();
    return entityManager.merge(company);
  }

  @Test
  void shouldGetCompanyById() throws Exception {
    var company = createAndPersistNewCompany();

    var request = get(URL_COMPANIES, company.getId());
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo(company.getId().toString())))
        .andExpect(jsonPath("$.name", equalTo(company.getName())))
        .andExpect(jsonPath("$.docNumber", equalTo(company.getDocNumber())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "a", "1a#"})
  void shouldReturnBadRequestWhenCompanyIdIsInvalid(String id) throws Exception {
    var request = get(URL_COMPANIES, id);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void shouldReturnNotFoundWhenCompanyIdDoesNotExist() throws Exception  {
    var request = get(URL_COMPANIES, UUID.randomUUID());
    mockMvc.perform(request)
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
