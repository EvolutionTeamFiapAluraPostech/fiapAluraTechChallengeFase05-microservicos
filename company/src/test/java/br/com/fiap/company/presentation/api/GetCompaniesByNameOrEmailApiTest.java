package br.com.fiap.company.presentation.api;

import static br.com.fiap.company.shared.testdata.CompanyTestData.createNewCompany;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.presentation.api.dto.CompanyContent;
import br.com.fiap.company.presentation.api.dto.CompanyOutputDto;
import br.com.fiap.company.shared.annotation.DatabaseTest;
import br.com.fiap.company.shared.annotation.IntegrationTest;
import br.com.fiap.company.shared.api.JsonUtil;
import br.com.fiap.company.shared.api.PageUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class GetCompaniesByNameOrEmailApiTest {

  private static final String URL_COMPANIES = "/companies/name-email";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  GetCompaniesByNameOrEmailApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private Company createAndPersistNewCompany() {
    var company = createNewCompany();
    return entityManager.merge(company);
  }

  @Test
  void shouldReturnOkWhenFindCompanyByName() throws Exception {
    var company = createAndPersistNewCompany();
    var companyPage = PageUtil.generatePageOfCompany(company);
    var companyOutputDtoExpected = CompanyOutputDto.toPage(companyPage);

    var request = get(URL_COMPANIES)
        .param("name", company.getName())
        .param("email", "");
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var companyFound = JsonUtil.fromJson(contentAsString, CompanyContent.class);
    assertThat(companyFound.getContent().get(0).id()).isEqualTo(
        companyOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenFindCompanyByEmail() throws Exception {
    var company = createAndPersistNewCompany();
    var companyPage = PageUtil.generatePageOfCompany(company);
    var companyOutputDtoExpected = CompanyOutputDto.toPage(companyPage);

    var request = get(URL_COMPANIES)
        .param("name", "")
        .param("email", company.getEmail());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var companyFound = JsonUtil.fromJson(contentAsString, CompanyContent.class);
    assertThat(companyFound.getContent().get(0).id()).isEqualTo(
        companyOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenFindCompanyByNameAndEmail() throws Exception {
    var company = createAndPersistNewCompany();
    var companyPage = PageUtil.generatePageOfCompany(company);
    var companyOutputDtoExpected = CompanyOutputDto.toPage(companyPage);

    var request = get(URL_COMPANIES)
        .param("name", company.getName())
        .param("email", company.getEmail());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var companyFound = JsonUtil.fromJson(contentAsString, CompanyContent.class);
    assertThat(companyFound.getContent().get(0).id()).isEqualTo(
        companyOutputDtoExpected.getContent().get(0).id());
  }

  @Test
  void shouldReturnOkWhenFindAllCompaniesByEmptyNameAndEmptyEmail() throws Exception {
    var request = get(URL_COMPANIES)
        .param("name", "")
        .param("email", "");
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var companyContent = JsonUtil.fromJson(contentAsString, CompanyContent.class);
    assertThat(companyContent.getContent()).hasSize(1);
  }

  @Test
  void shouldReturnOkWhenFindAllCompaniesButNothingWasFound() throws Exception {
    var request = get(URL_COMPANIES)
        .param("name", "Agent Smith")
        .param("email", "agent.smith@matrix.com");
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON));
  }
}
