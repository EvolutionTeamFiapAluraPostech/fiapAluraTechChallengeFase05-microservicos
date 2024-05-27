package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.createNewUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import br.com.users.shared.api.JsonUtil;
import br.com.users.shared.api.PageUtil;
import br.com.users.user.domain.entity.User;
import br.com.users.user.presentation.dto.UserContent;
import br.com.users.user.presentation.dto.UserOutputDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class GetUsersByNameOrEmailApiTest {

  private static final String URL_USERS = "/users/user-name-email";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  public GetUsersByNameOrEmailApiTest(
      MockMvc mockMvc,
      EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private User createAndSaveUser() {
    var user = createNewUser();
    return entityManager.merge(user);
  }

  private User findUser() {
    return (User) entityManager
        .createQuery("SELECT u FROM User u WHERE email = :email")
        .setParameter("email", "thomas.anderson@itcompany.com")
        .getSingleResult();
  }

  @Test
  void shouldReturnUserWhenFindUserByNameAndUserAlreadyExists() throws Exception {
    var user = createAndSaveUser();
    var userPage = PageUtil.generatePageOfUser(user);
    var userOutputDtoExpected = UserOutputDto.toPage(userPage);

    var request = get(URL_USERS)
        .param("name", user.getName());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison().isEqualTo(userOutputDtoExpected);
  }

  @Test
  void shouldReturnUserWhenFindUserByEmailAndUserAlreadyExists() throws Exception {
    var user = createAndSaveUser();
    var userPage = PageUtil.generatePageOfUser(user);
    var userOutputDtoExpected = UserOutputDto.toPage(userPage);

    var request = get(URL_USERS)
        .param("email", user.getEmail());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison().isEqualTo(userOutputDtoExpected);
  }

  @Test
  void shouldReturnUserWhenFindUserByNameAndEmailAndUserAlreadyExists() throws Exception {
    var user = createAndSaveUser();
    var userPage = PageUtil.generatePageOfUser(user);
    var userOutputDtoExpected = UserOutputDto.toPage(userPage);

    var request = get(URL_USERS)
        .param("name", user.getName())
        .param("email", user.getEmail());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison().isEqualTo(userOutputDtoExpected);
  }

  @Test
  void shouldReturnAllUsersWhenNameAndEmailAreEmptyUserAlreadyExists() throws Exception {
    var user = findUser();
    var userPage = PageUtil.generatePageOfUser(user);
    var userOutputDtoExpected = UserOutputDto.toPage(userPage);

    var request = get(URL_USERS)
        .param("name", "")
        .param("email", "");
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison().isEqualTo(userOutputDtoExpected);
  }

  @Test
  void shouldReturnAllUsersWhenNameAndEmailParametersWereNotInformedAndUserAlreadyExists()
      throws Exception {
    var user = findUser();
    var userPage = PageUtil.generatePageOfUser(user);
    var userOutputDtoExpected = UserOutputDto.toPage(userPage);

    var request = get(URL_USERS);
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison().isEqualTo(userOutputDtoExpected);
  }

  @Test
  void shouldReturnNothingWhenFindUserByNameAndUserDoesNotExists() throws Exception {
    createAndSaveUser();
    var userPageOutputDtoExpected = Page.empty();

    var request = get(URL_USERS)
        .param("name", "Agent Smith")
        .param("email", "");
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison()
        .isEqualTo(userPageOutputDtoExpected);
  }

  @Test
  void shouldReturnNothingWhenFindUserByEmailAndUserDoesNotExists() throws Exception {
    createAndSaveUser();
    var userPageOutputDtoExpected = Page.empty();

    var request = get(URL_USERS)
        .param("name", "")
        .param("email", "smith@matrix.com");
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(userFound.getContent()).usingRecursiveComparison()
        .isEqualTo(userPageOutputDtoExpected);
  }
}
