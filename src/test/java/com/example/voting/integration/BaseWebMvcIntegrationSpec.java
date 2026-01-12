package com.example.voting.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
public abstract class BaseWebMvcIntegrationSpec extends RepositoryIntegrationSpec {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  protected ResultActions doRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print());
  }
}
