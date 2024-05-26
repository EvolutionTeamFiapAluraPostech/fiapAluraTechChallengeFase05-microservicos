package br.com.users.user.presentation.api;

import static br.com.users.shared.testData.user.UserTestData.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import br.com.users.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@DatabaseTest
class DeleteUserApiTest {

  private static final String URL_USERS = "/users/";
  private final MockMvc mockMvc;
  private final EntityManager entityManager;

  @Autowired
  DeleteUserApiTest(MockMvc mockMvc, EntityManager entityManager) {
    this.mockMvc = mockMvc;
    this.entityManager = entityManager;
  }

  private User createAndPersistUser() {
    var user = createUser();
    return entityManager.merge(user);
  }

  @Test
  void shouldDeleteAnUser() throws Exception {
    var user = createAndPersistUser();

    var request = delete(URL_USERS + user.getId())
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(request)
        .andExpect(status().isNoContent());

    var userFound = entityManager.find(User.class, user.getId());
    assertThat(userFound).isNotNull();
    assertThat(userFound.getDeleted()).isEqualTo(Boolean.TRUE);
  }

  @Test
  void shouldReturnNotFoundWhenUserUuidWasNotFound() throws Exception {
    var request = delete(URL_USERS + UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(request)
        .andExpect(status().isNotFound());
  }
}
