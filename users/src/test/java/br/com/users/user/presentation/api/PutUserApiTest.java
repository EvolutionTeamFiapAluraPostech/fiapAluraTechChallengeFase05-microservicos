package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_CPF;
import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_EMAIL;
import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_NAME;
import static br.com.users.shared.testData.user.UserTestData.DEFAULT_USER_PASSWORD;
import static br.com.users.shared.testData.user.UserTestData.createNewUser;
import static br.com.users.shared.util.IsUUID.isUUID;
import static br.com.users.user.domain.enums.DocNumberType.CPF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import br.com.users.shared.api.JsonUtil;
import br.com.users.shared.testData.user.UserTestData;
import br.com.users.user.domain.entity.User;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class PutUserApiTest {

  private static final String URL_USERS = "/users/";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  PutUserApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private User createAndPersistUser() {
    var user = createNewUser();
    return entityManager.merge(user);
  }

  private User createAndPersistUserWithDifferentAttributes() {
    var user = User.builder()
        .name(ALTERNATIVE_USER_NAME)
        .email(ALTERNATIVE_USER_EMAIL)
        .docNumberType(CPF)
        .docNumber(ALTERNATIVE_USER_CPF)
        .password(DEFAULT_USER_PASSWORD)
        .build();
    return entityManager.merge(user);
  }

  private User findUser() {
    return (User) entityManager
        .createQuery("SELECT u FROM User u WHERE email = :email")
        .setParameter("email", "thomas.anderson@itcompany.com")
        .getSingleResult();
  }

  @Test
  void shouldUpdateUser() throws Exception {
    var user = createAndPersistUser();
    user.setName(ALTERNATIVE_USER_NAME);
    user.setEmail(ALTERNATIVE_USER_EMAIL);
    var userInputDto = JsonUtil.toJson(user);

    var request = put(URL_USERS + user.getId())
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isAccepted())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id", isUUID()))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var id = JsonPath.parse(contentAsString).read("$.id").toString();
    var userFound = entityManager.find(User.class, UUID.fromString(id));
    assertThat(userFound).isNotNull();
    assertThat(userFound.getName()).isEqualTo(ALTERNATIVE_USER_NAME);
    assertThat(userFound.getEmail()).isEqualTo(ALTERNATIVE_USER_EMAIL);
    assertThat(userFound.getPassword()).isEqualTo(DEFAULT_USER_PASSWORD);
  }

  @Test
  void shouldReturnNotFoundWhenUserWasNotFoundToUpdate() throws Exception {
    var userUuid = UUID.randomUUID();
    var user = createNewUser();
    user.setId(userUuid);
    var userInputDto = JsonUtil.toJson(user);

    var request = put(URL_USERS + userUuid)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenUserUuidIsInvalid() throws Exception {
    var userUuid = "aaa";
    var user = createNewUser();
    var userInputDto = JsonUtil.toJson(user);

    var request = put(URL_USERS + userUuid)
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }

  @Ignore
  void shouldReturnBadRequestWhenUserEmailAlreadyExistsInOtherUser() throws Exception {
    var user = createAndPersistUser();
    user.setName(ALTERNATIVE_USER_NAME);
    user.setEmail("thomas.anderson@itcompany.com");
    var userInputDto = JsonUtil.toJson(user);

    var request = put(URL_USERS + user.getId())
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }

  @Ignore
  void shouldReturnBadRequestWhenUserCpfAlreadyExistsInOtherUser() throws Exception {
    var secondUser = createAndPersistUserWithDifferentAttributes();
    secondUser.setDocNumber(UserTestData.DEFAULT_USER_CPF);
    var userInputDto = JsonUtil.toJson(secondUser);

    var request = put(URL_USERS + secondUser.getId())
        .contentType(APPLICATION_JSON)
        .content(userInputDto);
    mockMvc.perform(request)
        .andExpect(status().isConflict());
  }
}
