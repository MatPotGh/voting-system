package com.example.voting.controller;

import com.example.voting.dto.VoteRequest;
import com.example.voting.dto.CreateVoterRequest;
import com.example.voting.integration.BaseWebMvcIntegrationSpec;
import com.example.voting.internal.dto.CreateElectionOptionRequest;
import com.example.voting.internal.dto.CreateElectionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ElectionControllerIT extends BaseWebMvcIntegrationSpec {

  private static final String ADMIN_ELECTIONS_URL = "/api/admin/elections";
  private static final String ADMIN_VOTERS_URL = "/api/admin/voters";
  private static final String PUBLIC_ELECTIONS_URL = "/api/public/elections";

  @Test
  void shouldCreateElection() throws Exception {
    // given
    var request = new CreateElectionRequest("Wybory na Wójta 2025");

    // when
    var response = doRequest(
        post(ADMIN_ELECTIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
    );

    // then
    response.andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Wybory na Wójta 2025"));
  }

  @Test
  void shouldAddCandidateOptionToElection() throws Exception {
    // given
    var createElection = new CreateElectionRequest("Wybory 2025");
    var electionCreateJson = doRequest(
        post(ADMIN_ELECTIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(createElection))
    ).andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var electionId = objectMapper.readTree(electionCreateJson).get("id").asText();
    var optionReq = new CreateElectionOptionRequest("Jan", "Kowalski", 45);

    // when
    var response = doRequest(
        post(ADMIN_ELECTIONS_URL + "/" + electionId + "/options")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(optionReq))
    );

    // then
    response.andExpect(status().isCreated())
        .andExpect(jsonPath("$.candidateFirstName").value("Jan"))
        .andExpect(jsonPath("$.candidateLastName").value("Kowalski"));
  }

  @Test
  void shouldReturnElectionResultsWithVoteCounts() throws Exception {
    // given
    var createElection = new CreateElectionRequest("Wybory 2030");
    var electionCreateJson = doRequest(
        post(ADMIN_ELECTIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(createElection))
    ).andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var electionId = objectMapper.readTree(electionCreateJson).get("id").asText();

    var optionReqA = new CreateElectionOptionRequest("Jan", "Kowalski", 45);
    var optionReqB = new CreateElectionOptionRequest("Anna", "Nowak", 38);

    var optionAJson = doRequest(
        post(ADMIN_ELECTIONS_URL + "/" + electionId + "/options")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(optionReqA))
    ).andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var optionBJson = doRequest(
        post(ADMIN_ELECTIONS_URL + "/" + electionId + "/options")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(optionReqB))
    ).andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var optionAId = objectMapper.readTree(optionAJson).get("id").asLong();
    var optionBId = objectMapper.readTree(optionBJson).get("id").asLong();

    // when
    castVote(electionId, optionAId);
    castVote(electionId, optionAId);
    castVote(electionId, optionBId);

    // then
    doRequest(
        get(ADMIN_ELECTIONS_URL + "/{id}/results", electionId)
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.options[?(@.candidateFirstName=='Jan' && @.candidateLastName=='Kowalski')].voteCount").value(2))
        .andExpect(jsonPath("$.options[?(@.candidateFirstName=='Anna' && @.candidateLastName=='Nowak')].voteCount").value(1));
  }

  private void castVote(String electionId, Long optionId) throws Exception {
    var pesel = String.valueOf(System.nanoTime()).substring(0, 11);
    var voterJson = doRequest(
        post(ADMIN_VOTERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CreateVoterRequest(pesel)))
    ).andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var voterId = objectMapper.readTree(voterJson).get("id").asLong();

    var voteRequest = new VoteRequest(voterId, optionId);

    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/" + electionId + "/votes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteRequest))
    ).andExpect(status().isCreated());
  }
}

