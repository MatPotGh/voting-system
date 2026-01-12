package com.example.voting.integration;

import com.example.voting.repository.VoteRepository;
import com.example.voting.repository.VoterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class RepositoryIntegrationSpec extends IntegrationTestBase {

  @Autowired
  protected VoteRepository voteRepository;

  @Autowired
  protected VoterRepository voterRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void cleanDatabase() {
    jdbcTemplate.update("DELETE FROM votes");
    jdbcTemplate.update("DELETE FROM election_options");
    jdbcTemplate.update("DELETE FROM elections");
    jdbcTemplate.update("DELETE FROM candidates");
    jdbcTemplate.update("DELETE FROM voters");
  }

  protected void assertVoterExists(String pesel) {
    var voter = voterRepository.findByPesel(pesel);
    Assertions.assertTrue(voter.isPresent());
  }

  protected void assertVoterBlocked(Long voterId) {
    var voter = voterRepository.findById(voterId).orElseThrow();
    Assertions.assertTrue(voter.isBlocked());
  }

  protected void assertVoterNotBlocked(Long voterId) {
    var voter = voterRepository.findById(voterId).orElseThrow();
    Assertions.assertFalse(voter.isBlocked());
  }

  protected void assertVoterVotedForOption(Long voterId, Long optionId) {
    Assertions.assertTrue(voteRepository.existsByVoterIdAndOptionId(voterId, optionId));
  }
}
