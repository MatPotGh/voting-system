package com.example.voting.service;

import com.example.voting.internal.dto.ElectionDetailsResponse;
import com.example.voting.internal.dto.ElectionSummaryResponse;

import java.util.List;

public interface PublicElectionService {
  List<ElectionSummaryResponse> getAllElections();
  ElectionDetailsResponse getElectionDetails(Long id);
  void castVote(Long electionId, Long voterId, Long optionId);
}
