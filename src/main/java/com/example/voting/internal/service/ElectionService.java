package com.example.voting.internal.service;

import com.example.voting.internal.dto.CreateElectionOptionRequest;
import com.example.voting.internal.dto.CreateElectionRequest;
import com.example.voting.internal.dto.ElectionResultsResponse;
import com.example.voting.internal.dto.ElectionOptionResponse;
import com.example.voting.internal.dto.ElectionResponse;

public interface ElectionService {
  ElectionResponse createElection(CreateElectionRequest request);
  ElectionOptionResponse addOption(Long electionId, CreateElectionOptionRequest request);
  ElectionResultsResponse getElectionResults(Long electionId);
}
