package com.example.voting.controller;

import com.example.voting.dto.CreateVoterRequest;
import com.example.voting.integration.BaseWebMvcIntegrationSpec;
import com.example.voting.service.exceptions.PeselAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoterControllerIT extends BaseWebMvcIntegrationSpec {

  private static final String ADMIN_VOTERS_URL = "/api/admin/voters";

  @Test
  void shouldCreateVoter() throws Exception {
    // given
    var pesel = "12345678901";
    var request = new CreateVoterRequest(pesel);

    // when
    var response = doRequest(
        post(ADMIN_VOTERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
    );

    // then
    response.andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.pesel").value(pesel))
        .andExpect(jsonPath("$.blocked").value(false));

    assertVoterExists(pesel);
  }

  @Test
  void shouldBlockAndUnblockVoter() throws Exception {
    // given
    var pesel = "98765432109";
    var createJson = doRequest(
        post(ADMIN_VOTERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CreateVoterRequest(pesel)))
    ).andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var voterId = objectMapper.readTree(createJson).get("id").asLong();

    // when
    doRequest(
        patch(ADMIN_VOTERS_URL + "/{id}/block", voterId)
    ).andExpect(status().isNoContent());

    // then
    assertVoterBlocked(voterId);

    // when
    doRequest(
        patch(ADMIN_VOTERS_URL + "/{id}/unblock", voterId)
    ).andExpect(status().isNoContent());

    // then
    assertVoterNotBlocked(voterId);
  }

  @Test
  void shouldFailOnDuplicatePesel() throws Exception {
    // given
    var pesel = "11111111111";
    var request = new CreateVoterRequest(pesel);

    // when
    doRequest(
        post(ADMIN_VOTERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
    ).andExpect(status().isCreated());

    var duplicateResponse = doRequest(
        post(ADMIN_VOTERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
    );

    // then
    duplicateResponse.andExpect(status().isConflict());

    var exception = duplicateResponse.andReturn().getResolvedException();

    Assertions.assertNotNull(exception, "Exception should be resolved by handler");
    Assertions.assertInstanceOf(PeselAlreadyExistsException.class, exception);
    Assertions.assertEquals("Voter with PESEL='11111111111' already exists", exception.getMessage());

    assertVoterExists(pesel);
  }
}
