package br.com.fiap.company.presentation.api;

import static br.com.fiap.company.shared.testdata.CompanyTestData.ALTERNATIVE_COMPANY_CPF_DOC_NUMBER;
import static br.com.fiap.company.shared.testdata.CompanyTestData.ALTERNATIVE_COMPANY_EMAIL;
import static br.com.fiap.company.shared.testdata.CompanyTestData.ALTERNATIVE_COMPANY_NAME;
import static br.com.fiap.company.shared.testdata.CompanyTestData.DEFAULT_COMPANY_CPF_DOC_NUMBER;
import static br.com.fiap.company.shared.testdata.CompanyTestData.createCompany;
import static br.com.fiap.company.shared.testdata.CompanyTestData.createNewCompany;
import static br.com.fiap.company.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.company.domain.entity.Company;
import br.com.fiap.company.domain.enums.DocNumberType;
import br.com.fiap.company.shared.annotation.DatabaseTest;
import br.com.fiap.company.shared.annotation.IntegrationTest;
import br.com.fiap.company.shared.api.JsonUtil;
import br.com.fiap.company.shared.testdata.CompanyTestData;
import br.com.fiap.company.shared.util.StringUtil;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class PutCompanyApiTest {

  private static final String URL_COMPANIES = "/companies/{id}";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PutCompanyApiTest(MockMvc mockMvc, EntityManager entityManager) {
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

  private Company createAndPersistOtherCompany() {
    var company = createNewCompany();
    company.setDocNumber(ALTERNATIVE_COMPANY_CPF_DOC_NUMBER);
    return entityManager.merge(company);
  }

  @Test
  void shouldPutCompany() throws Exception {
    var company = createAndPersistNewCompany();
    company.setName(ALTERNATIVE_COMPANY_NAME);
    company.setEmail(ALTERNATIVE_COMPANY_EMAIL);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var companyFound = getCompanyFoundById(id);
    assertThat(companyFound).isNotNull();
    assertThat(companyFound.getId()).isNotNull().isEqualTo(company.getId());
    assertThat(companyFound.getName()).isNotNull().isEqualTo(company.getName());
    assertThat(companyFound.getDocNumber()).isNotNull().isEqualTo(company.getDocNumber());
    assertThat(companyFound.getDocNumberType()).isNotNull().isEqualTo(company.getDocNumberType());
    assertThat(companyFound.getEmail()).isNotNull().isEqualTo(company.getEmail());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyNameWasNotFilled(String companyName) throws Exception {
    var company = createAndPersistNewCompany();
    company.setName(companyName);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyNameLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyName = "AB";
    company.setName(companyName);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyNameLengthIsBiggerThan500Characters() throws Exception {
    var company = createCompany();
    var companyName = StringUtil.generateStringLength(501);
    company.setName(companyName);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyEmailWasNotFilled(String companyEmail) throws Exception {
    var company = createCompany();
    company.setEmail(companyEmail);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"email.domain.com", " email.domain.com", "@", "1", "A@b@c@example.com",
      "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", "email @example.com"})
  void shouldReturnBadRequestWhenCompanyEmailWasInvalid(String companyEmail) throws Exception {
    var company = createCompany();
    company.setEmail(companyEmail);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyEmailLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyEmail = "ab";
    company.setEmail(companyEmail);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyEmailLengthIsBiggerThan500Characters() throws Exception {
    var company = createCompany();
    var companyEmail = StringUtil.generateStringLength(501);
    company.setEmail(companyEmail);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyDocNumberWasNotFilled(String companyDocNumber)
      throws Exception {
    var company = createCompany();
    company.setDocNumber(companyDocNumber);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "12", "123", "1234", "12345", "123456", "1234567", "12345678",
      "123456789", "1234567890"})
  void shouldReturnBadRequestWhenCompanyDocNumberLengthIsInvalid(String docNumber)
      throws Exception {
    var company = createCompany();
    company.setDocNumber(docNumber);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyDocNumberLengthIsBiggerThan14Characters()
      throws Exception {
    var company = createCompany();
    var docNumber = StringUtil.generateStringLength(15);
    company.setDocNumber(docNumber);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
  void shouldReturnBadRequestWhenCompanyDocNumberIsAnInvalidCpf(String number) throws Exception {
    var company = createAndPersistNewCompany();
    company.setDocNumberType(DocNumberType.CPF);
    var cpf = StringUtil.generateStringRepeatCharLength(number, 11);
    company.setDocNumber(cpf);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
  void shouldReturnBadRequestWhenCompanyDocNumberIsAnInvalidCnpj(String number) throws Exception {
    var company = createAndPersistNewCompany();
    company.setDocNumberType(DocNumberType.CNPJ);
    var cnpj = StringUtil.generateStringRepeatCharLength(number, 14);
    company.setDocNumber(cnpj);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyDocNumberAlreadyExists() throws Exception {
    createAndPersistNewCompany();
    var company = createAndPersistOtherCompany();
    var companyAux = CompanyTestData.createCompany();
    companyAux.setId(company.getId());
    companyAux.setDocNumber(DEFAULT_COMPANY_CPF_DOC_NUMBER);
    var companyInputDto = JsonUtil.toJson(companyAux);

    var request = put(URL_COMPANIES, companyAux.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyStreetWasNotFilled(String companyStreet)
      throws Exception {
    var company = createCompany();
    company.setStreet(companyStreet);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyStreetLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyStreet = StringUtil.generateStringLength(2);
    company.setStreet(companyStreet);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyStreetLengthIsBiggerThan255Characters() throws Exception {
    var company = createCompany();
    var companyStreet = StringUtil.generateStringLength(256);
    company.setStreet(companyStreet);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyAddressNumberWasNotFilled(String companyAddressNumber)
      throws Exception {
    var company = createCompany();
    company.setNumber(companyAddressNumber);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyAddressNumberLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyAddressNumber = StringUtil.generateStringLength(2);
    company.setNumber(companyAddressNumber);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyAddressNumberLengthIsBiggerThan100Characters()
      throws Exception {
    var company = createCompany();
    var companyAddressNumber = StringUtil.generateStringLength(101);
    company.setNumber(companyAddressNumber);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyNeighborhoodWasNotFilled(String companyNeighborhood)
      throws Exception {
    var company = createCompany();
    company.setNeighborhood(companyNeighborhood);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyNeighborhoodLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyNeighborhood = StringUtil.generateStringLength(2);
    company.setNeighborhood(companyNeighborhood);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyNeighborhoodLengthIsBiggerThan100Characters()
      throws Exception {
    var company = createCompany();
    var companyNeighborhood = StringUtil.generateStringLength(101);
    company.setNeighborhood(companyNeighborhood);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyCityWasNotFilled(String companyCity) throws Exception {
    var company = createCompany();
    company.setCity(companyCity);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyCityLengthIsInvalid() throws Exception {
    var company = createCompany();
    var city = StringUtil.generateStringLength(2);
    company.setCity(city);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyCityLengthIsBiggerThan100Characters() throws Exception {
    var company = createCompany();
    var city = StringUtil.generateStringLength(101);
    company.setCity(city);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyStateWasNotFilled(String companyState) throws Exception {
    var company = createCompany();
    company.setState(companyState);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyStateLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyState = "S";
    company.setState(companyState);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyStateLengthIsBiggerThan2Characters() throws Exception {
    var company = createCompany();
    var companyState = "SAO";
    company.setState(companyState);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyCountryWasNotFilled(String companyCountry)
      throws Exception {
    var company = createCompany();
    company.setCountry(companyCountry);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyCountryLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyCountry = "AA";
    company.setCountry(companyCountry);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyCountryLengthIsBiggerThan100Characters() throws Exception {
    var company = createCompany();
    var companyCountry = StringUtil.generateStringLength(101);
    company.setCountry(companyCountry);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenCompanyPostalCodeWasNotFilled(String companyPostalCode)
      throws Exception {
    var company = createCompany();
    company.setPostalCode(companyPostalCode);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyPostalCodeLengthIsInvalid() throws Exception {
    var company = createCompany();
    var companyPostalCode = "1";
    company.setPostalCode(companyPostalCode);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyPostalCodeLengthIsBiggerThan8Characters()
      throws Exception {
    var company = createCompany();
    var companyPostalCode = StringUtil.generateStringLength(9);
    company.setPostalCode(companyPostalCode);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenCompanyPostalCodeHasAnAlphaCharacter()
      throws Exception {
    var company = createCompany();
    var companyPostalCode = "A123456B";
    company.setPostalCode(companyPostalCode);
    var companyInputDto = JsonUtil.toJson(company);

    var request = put(URL_COMPANIES, company.getId())
        .contentType(APPLICATION_JSON)
        .content(companyInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }
}
