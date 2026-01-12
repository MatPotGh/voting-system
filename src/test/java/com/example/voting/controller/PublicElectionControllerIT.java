package com.example.voting.controller;

import com.example.voting.dto.VoteRequest;
import com.example.voting.dto.CreateVoterRequest;
import com.example.voting.integration.BaseWebMvcIntegrationSpec;
import com.example.voting.internal.dto.CreateElectionOptionRequest;
import com.example.voting.internal.dto.CreateElectionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicElectionControllerIT extends BaseWebMvcIntegrationSpec {

  private static final String ADMIN_ELECTIONS_URL = "/api/admin/elections";
  private static final String ADMIN_VOTERS_URL = "/api/admin/voters";
  private static final String PUBLIC_ELECTIONS_URL = "/api/public/elections";


  @Test
  void shouldListAndGetElectionDetails() throws Exception {
    // given
    var electionId = createElection("Wybory 2027");
    addCandidateOption(electionId, "Jan", "Kowalski", 40);

    // when & then
    doRequest(get(PUBLIC_ELECTIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].name").value("Wybory 2027"));

    doRequest(get(PUBLIC_ELECTIONS_URL + "/{id}", electionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(electionId))
        .andExpect(jsonPath("$.name").value("Wybory 2027"))
        .andExpect(jsonPath("$.options[0].candidateFirstName").value("Jan"))
        .andExpect(jsonPath("$.options[0].candidateLastName").value("Kowalski"));
  }

  @Test
  void shouldCastVoteSuccessfully() throws Exception {
    // given
    var electionId = createElection("Wybory 2028");
    var optionId = addCandidateOption(electionId, "Anna", "Nowak", 38);
    var voterId = createVoter("12345678901");

    var voteReq = new VoteRequest(voterId, optionId);

    // when
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isCreated());

    // then
    assertVoterVotedForOption(voterId, optionId);
  }

  @Test
  void shouldFailWhenVoterNotFound() throws Exception {
    // given
    var electionId = createElection("Wybory 2029");
    var optionId = addCandidateOption(electionId, "Piotr", "Zieliński", 44);

    var voteReq = new VoteRequest(999999L, optionId);

    // when & then
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Voter with id=" + voteReq.voterId() + " not found"));
  }

  @Test
  void shouldFailWhenVoterBlocked() throws Exception {
    // given
    var electionId = createElection("Wybory 2030");
    var optionId = addCandidateOption(electionId, "Marek", "Lewandowski", 50);
    var voterId = createVoter("22222222222");

    doRequest(patch(ADMIN_VOTERS_URL + "/{id}/block", voterId))
        .andExpect(status().isNoContent());

    var voteReq = new VoteRequest(voterId, optionId);

    // when & then
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Voter is blocked"));
  }

  @Test
  void shouldFailWhenElectionNotFound() throws Exception {
    // given
    var electionId = createElection("Wybory 2031");
    var optionId = addCandidateOption(electionId, "Ewa", "Wiśniewska", 41);
    var voterId = createVoter("33333333333");
    var incorrectElectionId = 999999L;

    var voteReq = new VoteRequest(voterId, optionId);

    // when & then
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", incorrectElectionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Election with id=" + incorrectElectionId + " not found"));
  }

  @Test
  void shouldFailWhenOptionNotFound() throws Exception {
    // given
    var electionId = createElection("Wybory 2032");
    addCandidateOption(electionId, "Karol", "Bąk", 39);
    var voterId = createVoter("44444444444");

    var voteReq = new VoteRequest(voterId, 999999L);

    // when & then
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("ElectionOption with id=" + voteReq.optionId() + " not found"));
  }

  @Test
  void shouldFailWhenOptionDoesNotBelongToElection() throws Exception {
    // given
    var electionA = createElection("Wybory 2033 A");
    var optA = addCandidateOption(electionA, "Adam", "Nowicki", 47);
    var electionB = createElection("Wybory 2033 B");
    var voterId = createVoter("55555555555");

    var voteReq = new VoteRequest(voterId, optA);

    // when & then
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionB)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Option does not belong to election"));
  }

  @Test
  void shouldFailOnDuplicateVote() throws Exception {
    // given
    var electionId = createElection("Wybory 2034");
    var optionId = addCandidateOption(electionId, "Zofia", "Kaczmarek", 36);
    var voterId = createVoter("66666666666");

    var voteReq = new VoteRequest(voterId, optionId);

    // when
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    ).andExpect(status().isCreated());

    // then
    assertVoterVotedForOption(voterId, optionId);

    // when & then
    doRequest(
        post(PUBLIC_ELECTIONS_URL + "/{id}/votes", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(voteReq))
    )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Voter with id=" + voterId + " has already voted in election id=" + electionId));
  }

  private Long createElection(String name) throws Exception {
    var json = doRequest(
        post(ADMIN_ELECTIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CreateElectionRequest(name)))
    ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    var node = objectMapper.readTree(json);
    return node.get("id").asLong();
  }

  private Long addCandidateOption(Long electionId, String firstName, String lastName, int age) throws Exception {
    var json = doRequest(
        post(ADMIN_ELECTIONS_URL + "/{id}/options", electionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CreateElectionOptionRequest(firstName, lastName, age)))
    ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    var node = objectMapper.readTree(json);
    return node.get("id").asLong();
  }

  private Long createVoter(String pesel) throws Exception {
    var json = doRequest(
        post(ADMIN_VOTERS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CreateVoterRequest(pesel)))
    ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    var node = objectMapper.readTree(json);
    return node.get("id").asLong();
  }
}
