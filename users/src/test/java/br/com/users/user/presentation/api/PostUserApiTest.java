package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_CPF;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_NAME;
import static br.com.users.shared.testData.user.UserTestData.createNewUser;
import static br.com.users.shared.util.IsUUID.isUUID;
import static br.com.users.user.domain.enums.DocNumberType.CNPJ;
import static br.com.users.user.domain.enums.DocNumberType.CPF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import br.com.users.shared.api.JsonUtil;
import br.com.users.shared.util.StringUtil;
import br.com.users.user.domain.entity.User;
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
class PostUserApiTest {

  private static final String URL_USERS = "/users";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PostUserApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  @Test
  void shouldCreateUser() throws Exception {
    var user = createNewUser();
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var userFound = entityManager.find(User.class, UUID.fromString(id));
    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(DEFAULT_USER_NAME);
    assertThat(userFound.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenUserNameWasNotFilled(String name) throws Exception {
    var user = createNewUser();
    user.setName(name);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserNameLengthIsGreaterThan500Characters() throws Exception {
    var userNameIsGreaterThan500Characters = StringUtil.generateStringLength(501);
    var user = createNewUser();
    user.setName(userNameIsGreaterThan500Characters);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenUserEmailWasNotFilled(String email) throws Exception {
    var user = createNewUser();
    user.setEmail(email);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserEmailLengthIsGreaterThan500Characters() throws Exception {
    var userEmailLengthIsGreaterThan500Characters = StringUtil.generateStringLength(
        501);
    var user = createNewUser();
    user.setEmail(userEmailLengthIsGreaterThan500Characters);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"name.domain.com", "@", "name@", "namedomaincom"})
  void shouldReturnBadRequestWhenUserEmailIsInvalid(String email) throws Exception {
    var user = createNewUser();
    user.setEmail(email);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserEmailAlreadyExits() throws Exception {
    var user = createNewUser();
    var userInputDto = JsonUtil.toJson(user);
    entityManager.merge(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnBadRequestWhenUserPasswordWasNotFilled() throws Exception {
    var user = createNewUser();
    user.setPassword(null);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenUserPasswordIsNullOrEmpty(String password) throws Exception {
    var user = createNewUser();
    user.setPassword(password);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcdefghijk", "0ABCDEFGHI", "abcd1234", "Abcd1234"})
  void shouldReturnBadRequestWhenUserPasswordIsInvalid(String password) throws Exception {
    var user = createNewUser();
    user.setPassword(password);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcde"})
  void shouldReturnBadRequestWhenUserPasswordDoesNotHaveNumber(String password) throws Exception {
    var user = createNewUser();
    user.setPassword(password);
    var userInputDto = JsonUtil.toJson(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"XPTO"})
  void shouldReturnBadRequestWhenUserDocNumberTypeIsInvalid(String docNumberType) throws Exception {
    var userInputDto = """
        {
          "name": "Morpheus",
          "email": "morpheus@matrix.com",
          "password": "@Bcd1234",
          "docNumberType": "%s",
          "docNumber": "11955975094"
        }""".formatted(docNumberType);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserDocNumberAlreadyExits() throws Exception {
    var user = createNewUser();
    user.setDocNumber(DEFAULT_USER_CPF);
    var userInputDto = JsonUtil.toJson(user);
    entityManager.persist(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"12345678901", "00000000000", "1111111111", "22222222222", "33333333333",
      "44444444444", "55555555555", "66666666666", "77777777777", "88888888888", "99999999999",
      "abcdefg"})
  void shouldReturnBadRequestWhenUserDocNumberIsAnInvalidCpf(String cpf) throws Exception {
    var user = createNewUser();
    user.setDocNumberType(CPF);
    user.setDocNumber(cpf);
    var userInputDto = JsonUtil.toJson(user);
    entityManager.persist(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"12345678901234", "00000000000000", "1111111111111", "22222222222222",
      "33333333333333", "44444444444444", "55555555555555", "66666666666666", "77777777777777",
      "88888888888888", "99999999999999", "abcdefg"})
  void shouldReturnBadRequestWhenUserDocNumberIsAnInvalidCnpj(String cnpj) throws Exception {
    var user = createNewUser();
    user.setDocNumberType(CNPJ);
    user.setDocNumber(cnpj);
    var userInputDto = JsonUtil.toJson(user);
    entityManager.persist(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }
}
