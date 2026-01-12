package com.example.voting.internal.service;

import com.example.voting.domain.Candidate;
import com.example.voting.domain.Election;
import com.example.voting.domain.ElectionOption;
import com.example.voting.internal.dto.CreateElectionOptionRequest;
import com.example.voting.internal.dto.CreateElectionRequest;
import com.example.voting.internal.dto.ElectionOptionResponse;
import com.example.voting.internal.dto.ElectionResponse;
import com.example.voting.internal.dto.ElectionResultsResponse;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionOptionRepository;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.VoteRepository;
import com.example.voting.service.exceptions.ElectionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

  private final ElectionRepository electionRepository;
  private final ElectionOptionRepository optionRepository;
  private final CandidateRepository candidateRepository;
  private final VoteRepository voteRepository;

  @Override
  @Transactional
  public ElectionResponse createElection(CreateElectionRequest request) {
    var saved = electionRepository.save(
        Election.builder()
            .name(request.name())
            .build()
    );
    return new ElectionResponse(saved.getId(), saved.getName());
  }

  @Override
  @Transactional
  public ElectionOptionResponse addOption(Long electionId, CreateElectionOptionRequest request) {
    var election = electionRepository.findById(electionId)
        .orElseThrow(() -> new ElectionNotFoundException(electionId));

    var savedCandidate = candidateRepository.save(
        Candidate.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .age(request.age())
            .createdAt(OffsetDateTime.now())
            .build()
    );

        var electionOption = ElectionOption.builder()
        .election(election)
        .candidate(savedCandidate)
        .build();

    var saved = optionRepository.save(electionOption);
    return new ElectionOptionResponse(saved.getId(), savedCandidate.getFirstName(), savedCandidate.getLastName());

  }

  @Override
  @Transactional(readOnly = true)
  public ElectionResultsResponse getElectionResults(Long electionId) {
    var election = electionRepository.findByIdWithOptions(electionId)
        .orElseThrow(() -> new ElectionNotFoundException(electionId));

    var optionResults = election.getOptions().stream()
        .map(option -> new ElectionResultsResponse.ElectionOptionResultResponse(
            option.getCandidate().getFirstName(),
            option.getCandidate().getLastName(),
            voteRepository.countByOptionId(option.getId())
        ))
        .toList();

    return new ElectionResultsResponse(
        election.getName(),
        optionResults
    );
  }
}
