package br.com.users.shared.testcontainers.postgres;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class PostgresContainer {

  @Container
  public static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
      "postgres:15.1-alpine")
      .withDatabaseName("digitalparking-db")
      .withUsername("digitalparking-postgres-user")
      .withPassword("digitalparking-postgres-pwd");

  static {
    postgreSQLContainer.start();
  }

  public static class Initializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues values = TestPropertyValues.of(
          "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.password=" + postgreSQLContainer.getPassword(),
          "spring.datasource.username=" + postgreSQLContainer.getUsername()
      );
      values.applyTo(applicationContext);
    }
  }
}
