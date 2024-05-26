package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_INPUT;
import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_NAME;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_NAME;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_PASSWORD;
import static br.com.users.shared.testData.user.UserTestData.USER_INPUT;
import static br.com.users.shared.testData.user.UserTestData.USER_TEMPLATE_INPUT;
import static br.com.users.shared.util.IsUUID.isUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
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
  public PostUserApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private User createNewUser() {
    return UserTestData.createNewUser();
  }

  @Test
  void shouldCreateUser() throws Exception {
    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(ALTERNATIVE_USER_INPUT);
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var userFound = entityManager.find(User.class, UUID.fromString(id));
    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(ALTERNATIVE_USER_NAME);
    assertThat(userFound.getEmail()).isEqualTo(ALTERNATIVE_USER_EMAIL);
  }

  @Test
  void shouldReturnBadRequestWhenUserNameWasNotFilled() throws Exception {
    var user = User.builder()
        .name("")
        .email(DEFAULT_USER_EMAIL)
        .password(DEFAULT_USER_PASSWORD)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserNameLengthIsGreaterThan500Characters() throws Exception {
    var userNameIsGreaterThan500Characters = StringUtil.generateStringLength(        501);
    var user = User.builder()
        .name(userNameIsGreaterThan500Characters)
        .email(DEFAULT_USER_EMAIL)
        .password(DEFAULT_USER_PASSWORD)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserEmailWasNotFilled() throws Exception {
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .password(DEFAULT_USER_PASSWORD)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserEmailLengthIsGreaterThan500Characters() throws Exception {
    var userEmailLengthIsGreaterThan500Characters = StringUtil.generateStringLength(
         501);
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .email(userEmailLengthIsGreaterThan500Characters)
        .password(DEFAULT_USER_PASSWORD)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"name.domain.com", "@", "name@", "namedomaincom"})
  void shouldReturnBadRequestWhenUserEmailIsInvalid(String email) throws Exception {
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .email(email)
        .password(DEFAULT_USER_PASSWORD)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserEmailAlreadyExits() throws Exception {
    var user = createNewUser();
    entityManager.merge(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_INPUT);

    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnBadRequestWhenUserPasswordWasNotFilled() throws Exception {
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .email(DEFAULT_USER_EMAIL)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldReturnBadRequestWhenUserPasswordIsNullOrEmpty(String password) throws Exception {
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .email(DEFAULT_USER_EMAIL)
        .password(password)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcdefghijk", "0ABCDEFGHI", "abcd1234", "Abcd1234"})
  void shouldReturnBadRequestWhenUserPasswordIsInvalid(String password) throws Exception {
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .email(DEFAULT_USER_EMAIL)
        .password(password)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcde"})
  void shouldReturnBadRequestWhenUserPasswordDoesNotHaveNumber(String password) throws Exception {
    var user = User.builder()
        .name(DEFAULT_USER_NAME)
        .email(DEFAULT_USER_EMAIL)
        .password(password)
        .build();

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_TEMPLATE_INPUT
            .formatted(user.getName(), user.getEmail(), user.getDocNumber(), user.getPassword()));

    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenUserCpfAlreadyExits() throws Exception {
    var user = createNewUser();
    user.setEmail(ALTERNATIVE_USER_EMAIL);
    entityManager.persist(user);

    var request = post(URL_USERS)
        .contentType(APPLICATION_JSON)
        .content(USER_INPUT);

    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }

}
