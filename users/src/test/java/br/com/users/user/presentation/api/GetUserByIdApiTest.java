package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.createNewUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import br.com.users.shared.api.JsonUtil;
import br.com.users.user.domain.entity.User;
import br.com.users.user.presentation.dto.UserOutputDto;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@IntegrationTest
@DatabaseTest
class GetUserByIdApiTest {

  private static final String URL_USERS = "/users/";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  public GetUserByIdApiTest(
      MockMvc mockMvc,
      EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private User createAndPersistNewUser() {
    var user = createNewUser();
    return entityManager.merge(user);
  }

  @Test
  void shoudReturnUserWhenUserExists() throws Exception {
    var user = createAndPersistNewUser();
    var userOutputDtoExpected = UserOutputDto.from(user);

    var request = get(URL_USERS + user.getId());
    var mvcResult = mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var userFound = JsonUtil.fromJson(contentAsString, User.class);
    var userDtoFound = UserOutputDto.from(userFound);
    assertThat(userDtoFound).usingRecursiveComparison().isEqualTo(userOutputDtoExpected);
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
    var request = MockMvcRequestBuilders.get(URL_USERS + UUID.randomUUID());
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenUserUuidIsInvalid() throws Exception {
    var request = MockMvcRequestBuilders.get(URL_USERS + "aaa");
    mockMvc.perform(request)
        .andExpect(status().isBadRequest());
  }
}
