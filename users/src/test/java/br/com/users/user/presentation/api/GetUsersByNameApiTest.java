package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.ALTERNATIVE_USER_EMAIL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class GetUsersByNameApiTest {

  private static final String URL_USERS = "/users/name/";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  public GetUsersByNameApiTest(
      MockMvc mockMvc,
      EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private User findUser() {
    return (User) entityManager
        .createQuery("SELECT u FROM User u WHERE email = :email")
        .setParameter("email", "thomas.anderson@itcompany.com")
        .getSingleResult();
  }

  @Test
  void shouldReturnUserWhenUserExists() throws Exception {
    var user = findUser();
    var userPage = PageUtil.generatePageOfUser(user);
    var userExpected = UserOutputDto.toPage(userPage);

    var request = get(URL_USERS + user.getName());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var users = JsonUtil.fromJson(contentAsString, UserContent.class);
    assertThat(users.getContent()).usingRecursiveComparison().isEqualTo(userExpected);
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
    var request = get(URL_USERS + ALTERNATIVE_USER_EMAIL);
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(0)));
  }
}
