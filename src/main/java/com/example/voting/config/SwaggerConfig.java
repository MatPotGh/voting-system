package com.example.voting.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI votingOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Voting System API")
            .description("API documentation for the Voting System")
            .version("v1"));
  }
}
