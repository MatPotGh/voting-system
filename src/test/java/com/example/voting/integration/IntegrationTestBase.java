package com.example.voting.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("integration-test")
@Testcontainers
public abstract class IntegrationTestBase {

  private static final String POSTGRES_IMAGE = "postgres:15-alpine";
  private static final String POSTGRES_DB_USERNAME = "itest_user";
  private static final String POSTGRES_DB_PASSWORD = "itest_pass";
  private static final String POSTGRES_DB_NAME = "itest_db";

  private static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
          .withDatabaseName(POSTGRES_DB_NAME)
          .withUsername(POSTGRES_DB_USERNAME)
          .withPassword(POSTGRES_DB_PASSWORD);

  static {
    postgreSQLContainer.start();
  }


  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }
}
