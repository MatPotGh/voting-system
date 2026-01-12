package com.example.voting.service;

import com.example.voting.domain.ElectionOption;
import com.example.voting.domain.Vote;
import com.example.voting.internal.dto.ElectionDetailsResponse;
import com.example.voting.internal.dto.ElectionOptionResponse;
import com.example.voting.internal.dto.ElectionSummaryResponse;
import com.example.voting.repository.ElectionOptionRepository;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.VoteRepository;
import com.example.voting.repository.VoterRepository;
import com.example.voting.service.exceptions.ElectionNotFoundException;
import com.example.voting.service.exceptions.ElectionOptionNotFoundException;
import com.example.voting.service.exceptions.InvalidOperationException;
import com.example.voting.service.exceptions.VoterAlreadyVotedException;
import com.example.voting.service.exceptions.VoterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicElectionServiceImpl implements PublicElectionService {

  private final ElectionRepository electionRepository;
  private final VoterRepository voterRepository;
  private final ElectionOptionRepository optionRepository;
  private final VoteRepository voteRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ElectionSummaryResponse> getAllElections() {
    return electionRepository.findAll()
        .stream()
        .map(election -> new ElectionSummaryResponse(election.getId(), election.getName()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ElectionDetailsResponse getElectionDetails(Long id) {
    var election = electionRepository.findByIdWithOptions(id)
        .orElseThrow(() -> new ElectionNotFoundException(id));

    var options = election.getOptions().stream()
        .map(this::toOptionResponse)
        .toList();

    return new ElectionDetailsResponse(election.getId(), election.getName(), options);
  }

  @Override
  @Transactional
  public void castVote(Long electionId, Long voterId, Long optionId) {
    var voter = voterRepository.findById(voterId)
        .orElseThrow(() -> new VoterNotFoundException(voterId));
    if (voter.isBlocked()) {
      throw new InvalidOperationException("Voter is blocked");
    }

    var election = electionRepository.findById(electionId)
        .orElseThrow(() -> new ElectionNotFoundException(electionId));

    var option = optionRepository.findById(optionId)
        .orElseThrow(() -> new ElectionOptionNotFoundException(optionId));
    if (!option.getElection().getId().equals(election.getId())) {
      throw new InvalidOperationException("Option does not belong to election");
    }

    if (voteRepository.existsByVoterIdAndElectionId(voter.getId(), election.getId())) {
      throw new VoterAlreadyVotedException(voter.getId(), election.getId());
    }

    var vote = Vote.builder()
        .voter(voter)
        .election(election)
        .option(option)
        .castAt(OffsetDateTime.now())
        .build();
    voteRepository.save(vote);

  }

  private ElectionOptionResponse toOptionResponse(ElectionOption option) {
    return new ElectionOptionResponse(option.getId(), option.getCandidate().getFirstName(), option.getCandidate().getLastName());
  }
}
