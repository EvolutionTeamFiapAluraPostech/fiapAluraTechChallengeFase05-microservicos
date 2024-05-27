package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.*;
import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_NAME;
import static br.com.users.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import br.com.users.shared.api.JsonUtil;
import br.com.users.shared.testData.user.UserTestData;
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

  @Test
  void shouldReturnBadRequestWhenUserDocNumberAlreadyExits() throws Exception {
    var user = createNewUser();
    user.setEmail(ALTERNATIVE_USER_EMAIL);
    var userInputDto = JsonUtil.toJson(user);
    entityManager.persist(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }
}
